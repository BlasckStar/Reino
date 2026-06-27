package com.blasck.reino.domain.repository

import com.blasck.reino.domain.drive.DriveRemoteFile

interface DriveImageStorageRepository {
    suspend fun saveImage(
        image: DriveRemoteFile,
        bytes: ByteArray,
    ): String
}
