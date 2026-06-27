package com.blasck.reino.domain.repository

import com.blasck.reino.domain.drive.DriveCharacterEntry
import com.blasck.reino.domain.drive.DriveRemoteFile

interface DriveCatalogRepository {
    suspend fun loadCatalog(): List<DriveCharacterEntry>

    suspend fun downloadFile(file: DriveRemoteFile): ByteArray
}
