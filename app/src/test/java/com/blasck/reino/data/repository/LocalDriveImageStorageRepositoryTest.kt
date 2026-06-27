package com.blasck.reino.data.repository

import com.blasck.reino.domain.drive.DriveRemoteFile
import org.junit.Assert.assertEquals
import org.junit.Test

class LocalDriveImageStorageRepositoryTest {
    @Test
    fun `uses image name for local cache file`() {
        val image =
            DriveRemoteFile(
                fileId = "1OpbEwGCGurN_BfAg0xMRvUEmR74Don7P",
                name = "Syrio 01.png",
                mimeType = "image/png",
            )

        assertEquals("syrio-01.png", image.localImageFileName())
    }
}
