package com.blasck.reino.data.repository

import com.blasck.reino.domain.drive.DrivePublicListingParser
import com.blasck.reino.domain.drive.DriveRemoteFile
import com.blasck.reino.domain.drive.ReinoDriveConfig
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

interface DriveFolderListingDataSource {
    suspend fun listFolder(folderId: String): List<DriveRemoteFile>
}

class PublicDriveFolderListingDataSource(
    private val client: OkHttpClient,
    private val parser: DrivePublicListingParser = DrivePublicListingParser(),
) : DriveFolderListingDataSource {
    override suspend fun listFolder(folderId: String): List<DriveRemoteFile> {
        val request = Request.Builder().url(ReinoDriveConfig.folderUrl(folderId)).build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Drive retornou HTTP ${response.code}")
            return parser.parse(response.body.string())
        }
    }
}

class FallbackDriveFolderListingDataSource(
    private val primary: DriveFolderListingDataSource,
    private val fallback: DriveFolderListingDataSource? = null,
) : DriveFolderListingDataSource {
    override suspend fun listFolder(folderId: String): List<DriveRemoteFile> {
        val primaryResult = runCatching { primary.listFolder(folderId) }
        val primaryFiles = primaryResult.getOrNull()
        if (!primaryFiles.isNullOrEmpty() || fallback == null) return primaryResult.getOrThrow()
        return fallback.listFolder(folderId)
    }
}
