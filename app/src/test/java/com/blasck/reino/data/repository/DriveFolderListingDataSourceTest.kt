package com.blasck.reino.data.repository

import com.blasck.reino.domain.drive.DriveRemoteFile
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class DriveFolderListingDataSourceTest {
    @Test
    fun `fallback is not used when primary returns files`() =
        runBlocking {
            val primary = FakeListingDataSource(result = listOf(file("primary")))
            val fallback = FakeListingDataSource(result = listOf(file("fallback")))
            val dataSource = FallbackDriveFolderListingDataSource(primary, fallback)

            val files = dataSource.listFolder("folder")

            assertEquals("primary".padEnd(26, '0'), files.single().fileId)
            assertEquals(1, primary.calls)
            assertEquals(0, fallback.calls)
        }

    @Test
    fun `fallback is used when primary returns empty list`() =
        runBlocking {
            val primary = FakeListingDataSource(result = emptyList())
            val fallback = FakeListingDataSource(result = listOf(file("fallback")))
            val dataSource = FallbackDriveFolderListingDataSource(primary, fallback)

            val files = dataSource.listFolder("folder")

            assertEquals("fallback".padEnd(26, '0'), files.single().fileId)
            assertEquals(1, primary.calls)
            assertEquals(1, fallback.calls)
        }

    private class FakeListingDataSource(
        private val result: List<DriveRemoteFile>,
    ) : DriveFolderListingDataSource {
        var calls = 0

        override suspend fun listFolder(folderId: String): List<DriveRemoteFile> {
            calls += 1
            return result
        }
    }

    private fun file(id: String): DriveRemoteFile =
        DriveRemoteFile(
            fileId = id.padEnd(26, '0'),
            name = "$id V4.xlsx",
            mimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        )
}
