package com.blasck.reino.data.repository

import android.util.Log
import com.blasck.reino.BuildConfig
import com.blasck.reino.domain.drive.DriveFolderLinkParser
import com.blasck.reino.domain.drive.DriveSourceConfig
import com.blasck.reino.domain.drive.DriveSourceOption
import com.blasck.reino.domain.drive.ReinoDriveConfig
import com.blasck.reino.domain.repository.DefaultDriveSourceProvider
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class FirebaseRemoteConfigDriveSourceProvider(
    private val remoteConfig: FirebaseRemoteConfig,
) : DefaultDriveSourceProvider {
    private val configurationLock = Mutex()
    private val refreshLock = Mutex()
    private val json =
        Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
    private val defaultSourceFlow = MutableStateFlow(DriveSourceConfig())
    private val sourceOptionsFlow = MutableStateFlow(defaultSourceOptions())
    private var configured = false
    private var refreshed = false

    override fun observeDefaultSource(): Flow<DriveSourceConfig> =
        defaultSourceFlow.asStateFlow()

    override fun observeSourceOptions(): Flow<List<DriveSourceOption>> =
        sourceOptionsFlow.asStateFlow()

    override fun getCachedDefaultSource(): DriveSourceConfig =
        defaultSourceFlow.value

    override suspend fun refresh() {
        if (refreshed) return
        refreshLock.withLock {
            if (refreshed) return
            ensureConfigured()
            runCatching { remoteConfig.fetchAndActivate().await() }
                .onSuccess { activated ->
                    defaultSourceFlow.value = remoteConfig.toDriveSourceConfig()
                    sourceOptionsFlow.value = remoteConfig.toDriveSourceOptions()
                    Log.d(
                        TAG,
                        "Remote Config fetch completed. activated=$activated " +
                            "status=${remoteConfig.info.lastFetchStatus} " +
                            "interval=${remoteConfig.info.configSettings.minimumFetchIntervalInSeconds}",
                    )
                    remoteConfig.logDriveFlags()
                }
                .onFailure { error ->
                    Log.w(TAG, "Remote Config fetch failed. Using bundled Drive source fallback.", error)
                    remoteConfig.logDriveFlags()
                }
            refreshed = true
        }
    }

    private suspend fun ensureConfigured() {
        if (configured) return
        configurationLock.withLock {
            if (configured) return
            remoteConfig.setConfigSettingsAsync(
                FirebaseRemoteConfigSettings
                    .Builder()
                    .setMinimumFetchIntervalInSeconds(fetchIntervalSeconds())
                    .build(),
            ).await()
            remoteConfig.setDefaultsAsync(
                mapOf(
                    KEY_USE_REMOTE_DEFAULT_DRIVE_LINK to false,
                    KEY_DEFAULT_DRIVE_FOLDER_LINK to ReinoDriveConfig.folderUrl(ReinoDriveConfig.ROOT_FOLDER_ID),
                    KEY_DRIVE_SOURCE_OPTIONS_JSON to defaultSourceOptionsJson(),
                ),
            ).await()
            defaultSourceFlow.value = remoteConfig.toDriveSourceConfig()
            sourceOptionsFlow.value = remoteConfig.toDriveSourceOptions()
            configured = true
        }
    }

    private fun FirebaseRemoteConfig.toDriveSourceConfig(): DriveSourceConfig {
        if (!getBoolean(KEY_USE_REMOTE_DEFAULT_DRIVE_LINK)) return DriveSourceConfig()

        val link = getString(KEY_DEFAULT_DRIVE_FOLDER_LINK).trim()
        val folderId = DriveFolderLinkParser.extractFolderId(link) ?: return DriveSourceConfig()
        return DriveSourceConfig(
            rootFolderId = folderId,
            customLink = link,
        )
    }

    private fun FirebaseRemoteConfig.toDriveSourceOptions(): List<DriveSourceOption> {
        val rawJson = getString(KEY_DRIVE_SOURCE_OPTIONS_JSON).trim()
        val remoteOptions =
            runCatching {
                parseSourceOptions(rawJson)
            }.onFailure { error ->
                Log.w(TAG, "Remote Config source options JSON is invalid. raw=$rawJson", error)
            }.getOrDefault(emptyList())

        return (remoteOptions + remoteDefaultSourceOption())
            .ifEmpty { defaultSourceOptions() }
            .filter { option -> option.name.isNotBlank() && option.link.isNotBlank() }
            .distinctBy { it.link.trim() }
    }

    private fun parseSourceOptions(rawJson: String): List<DriveSourceOption> {
        if (rawJson.isBlank()) return emptyList()
        val root = json.parseToJsonElement(rawJson)
        val array =
            when (root) {
                is JsonArray -> root
                is JsonObject ->
                    root["sources"]?.jsonArrayOrNull()
                        ?: root["options"]?.jsonArrayOrNull()
                        ?: root["links"]?.jsonArrayOrNull()
                        ?: root["driveSources"]?.jsonArrayOrNull()
                        ?: root["drive_source_options"]?.jsonArrayOrNull()
                        ?: JsonArray(emptyList())

                else -> JsonArray(emptyList())
            }

        return array.mapNotNull { it.toDriveSourceOption() }
    }

    private fun JsonElement.toDriveSourceOption(): DriveSourceOption? {
        val item = jsonObjectOrNull() ?: return null
        val name =
            item.firstString("name", "title", "label", "nome")
                ?: item.firstString("link", "url", "folderUrl", "folder_url", "folderId", "folder_id")
                ?: return null
        val link =
            item.firstString("link", "url", "folderUrl", "folder_url", "folderId", "folder_id")
                ?: return null
        val description =
            item.firstString("description", "subtitle", "descricao", "descrição")
                .orEmpty()
        return DriveSourceOption(
            name = name,
            link = link,
            description = description,
        )
    }

    private fun JsonElement.jsonArrayOrNull(): JsonArray? =
        this as? JsonArray

    private fun JsonElement.jsonObjectOrNull(): JsonObject? =
        this as? JsonObject

    private fun JsonObject.firstString(vararg keys: String): String? =
        keys.firstNotNullOfOrNull { key ->
            get(key)
                ?.runCatching { jsonPrimitive.content.trim() }
                ?.getOrNull()
                ?.takeIf(String::isNotBlank)
        }

    private fun FirebaseRemoteConfig.logDriveFlags() {
        val useRemote = getBoolean(KEY_USE_REMOTE_DEFAULT_DRIVE_LINK)
        val link = getString(KEY_DEFAULT_DRIVE_FOLDER_LINK).trim()
        val folderId = DriveFolderLinkParser.extractFolderId(link).orEmpty()
        val optionsJson = getString(KEY_DRIVE_SOURCE_OPTIONS_JSON).trim()
        val options = toDriveSourceOptions()
        Log.d(
            TAG,
            "Remote Config flags: " +
                "$KEY_USE_REMOTE_DEFAULT_DRIVE_LINK=$useRemote, " +
                "$KEY_DEFAULT_DRIVE_FOLDER_LINK='$link', " +
                "extractedFolderId='$folderId', " +
                "$KEY_DRIVE_SOURCE_OPTIONS_JSON='$optionsJson', " +
                "parsedOptions=${options.map { it.name }}",
        )
    }

    private fun defaultSourceOptions(): List<DriveSourceOption> =
        listOf(
            DriveSourceOption(
                name = "Drive padrao do Reino",
                link = ReinoDriveConfig.folderUrl(ReinoDriveConfig.ROOT_FOLDER_ID),
                description = "Fonte oficial configurada no aplicativo.",
            ),
        )

    private fun FirebaseRemoteConfig.remoteDefaultSourceOption(): List<DriveSourceOption> {
        if (!getBoolean(KEY_USE_REMOTE_DEFAULT_DRIVE_LINK)) return emptyList()
        val link = getString(KEY_DEFAULT_DRIVE_FOLDER_LINK).trim()
        val folderId = DriveFolderLinkParser.extractFolderId(link) ?: return emptyList()
        return listOf(
            DriveSourceOption(
                name = "Fonte padrao remota",
                link = link,
                description = "Fonte definida pela flag $KEY_DEFAULT_DRIVE_FOLDER_LINK.",
            ),
        )
    }

    private fun defaultSourceOptionsJson(): String =
        """
        [
          {
            "name": "Drive padrao do Reino",
            "link": "${ReinoDriveConfig.folderUrl(ReinoDriveConfig.ROOT_FOLDER_ID)}",
            "description": "Fonte oficial configurada no aplicativo."
          }
        ]
        """.trimIndent()

    private fun fetchIntervalSeconds(): Long =
        if (BuildConfig.DEBUG) DEBUG_FETCH_INTERVAL_SECONDS else RELEASE_FETCH_INTERVAL_SECONDS

    companion object {
        const val KEY_USE_REMOTE_DEFAULT_DRIVE_LINK = "use_remote_default_drive_link"
        const val KEY_DEFAULT_DRIVE_FOLDER_LINK = "default_drive_folder_link"
        const val KEY_DRIVE_SOURCE_OPTIONS_JSON = "drive_source_options_json"

        private const val TAG = "ReinoRemoteConfig"
        private const val DEBUG_FETCH_INTERVAL_SECONDS = 0L
        private const val RELEASE_FETCH_INTERVAL_SECONDS = 3_600L
    }
}
