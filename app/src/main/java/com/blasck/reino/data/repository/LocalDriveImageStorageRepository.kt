package com.blasck.reino.data.repository

import android.content.Context
import com.blasck.reino.domain.drive.DriveRemoteFile
import com.blasck.reino.domain.repository.DriveImageStorageRepository
import java.io.File

class LocalDriveImageStorageRepository(
    context: Context,
) : DriveImageStorageRepository {
    private val imageDir = File(context.filesDir, "drive_images")

    override suspend fun saveImage(
        image: DriveRemoteFile,
        bytes: ByteArray,
    ): String {
        if (!imageDir.exists()) imageDir.mkdirs()
        val file = File(imageDir, image.localImageFileName())
        file.writeBytes(bytes)
        return file.absolutePath
    }
}

internal fun DriveRemoteFile.localImageFileName(): String {
    val extension = name.substringAfterLast('.', "img").lowercase()
    val baseName =
        name.substringBeforeLast('.')
            .lowercase()
            .replace(Regex("""[^a-z0-9]+"""), "-")
            .trim('-')
            .ifBlank { fileId }
    return "$baseName.$extension"
}
