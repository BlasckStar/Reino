package com.blasck.reino.data.repository

import android.content.Context
import com.blasck.reino.domain.drive.DriveSourceConfig
import com.blasck.reino.domain.repository.DriveSourceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SharedPreferencesDriveSourceRepository(
    context: Context,
) : DriveSourceRepository {
    private val preferences =
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    private val sourceFlow = MutableStateFlow(readSource())

    override fun observeSource(): Flow<DriveSourceConfig> = sourceFlow.asStateFlow()

    override suspend fun getSource(): DriveSourceConfig = readSource()

    override suspend fun saveCustomSource(config: DriveSourceConfig) {
        preferences
            .edit()
            .putString(KEY_ROOT_FOLDER_ID, config.rootFolderId)
            .putString(KEY_CUSTOM_LINK, config.customLink)
            .apply()
        sourceFlow.value = readSource()
    }

    override suspend fun clearCustomSource() {
        preferences
            .edit()
            .remove(KEY_ROOT_FOLDER_ID)
            .remove(KEY_CUSTOM_LINK)
            .apply()
        sourceFlow.value = readSource()
    }

    private fun readSource(): DriveSourceConfig {
        val folderId = preferences.getString(KEY_ROOT_FOLDER_ID, null)
        val link = preferences.getString(KEY_CUSTOM_LINK, null).orEmpty()
        return if (folderId.isNullOrBlank()) {
            DriveSourceConfig()
        } else {
            DriveSourceConfig(
                rootFolderId = folderId,
                customLink = link,
            )
        }
    }

    private companion object {
        const val PREFERENCES_NAME = "reino_drive_source"
        const val KEY_ROOT_FOLDER_ID = "root_folder_id"
        const val KEY_CUSTOM_LINK = "custom_link"
    }
}
