package com.blasck.reino.data.repository

import com.blasck.reino.domain.drive.DriveCatalogBuilder
import com.blasck.reino.domain.drive.DriveCharacterEntry
import com.blasck.reino.domain.drive.DriveRemoteFile
import com.blasck.reino.domain.drive.ReinoDriveConfig
import com.blasck.reino.domain.drive.isCharacterImagesFolder
import com.blasck.reino.domain.repository.DriveCatalogRepository
import com.blasck.reino.domain.repository.DriveSourceRepository
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class GoogleDriveCatalogRepository(
    private val client: OkHttpClient,
    private val listingDataSource: DriveFolderListingDataSource = PublicDriveFolderListingDataSource(client),
    private val sourceRepository: DriveSourceRepository,
    private val builder: DriveCatalogBuilder = DriveCatalogBuilder(),
) : DriveCatalogRepository {
    override suspend fun loadCatalog(): List<DriveCharacterEntry> {
        val source = sourceRepository.getSource()
        val rootFiles = listingDataSource.listFolder(source.rootFolderId)
        val imageFolderId =
            rootFiles.firstOrNull { it.isCharacterImagesFolder() }?.fileId
                ?: ReinoDriveConfig.CHARACTER_IMAGES_FOLDER_ID.takeIf { source.isDefault }
        val imageFiles =
            imageFolderId
                ?.let { listingDataSource.listFolder(it) }
                .orEmpty()
        return builder.build(
            sheets = rootFiles,
            images = imageFiles,
        )
    }

    override suspend fun downloadFile(file: DriveRemoteFile): ByteArray =
        bytes(file.downloadUrl)

    private fun bytes(url: String): ByteArray {
        val request = Request.Builder().url(url).build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Drive retornou HTTP ${response.code}")
            return response.body.bytes()
        }
    }
}
