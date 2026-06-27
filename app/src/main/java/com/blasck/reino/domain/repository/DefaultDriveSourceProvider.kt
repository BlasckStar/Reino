package com.blasck.reino.domain.repository

import com.blasck.reino.domain.drive.DriveSourceConfig
import com.blasck.reino.domain.drive.DriveSourceOption
import kotlinx.coroutines.flow.Flow

interface DefaultDriveSourceProvider {
    fun observeDefaultSource(): Flow<DriveSourceConfig>

    fun observeSourceOptions(): Flow<List<DriveSourceOption>>

    fun getCachedDefaultSource(): DriveSourceConfig

    suspend fun refresh()
}
