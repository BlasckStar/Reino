package com.blasck.reino.domain.repository

import com.blasck.reino.domain.drive.DriveSourceConfig
import kotlinx.coroutines.flow.Flow

interface DriveSourceRepository {
    fun observeSource(): Flow<DriveSourceConfig>

    suspend fun getSource(): DriveSourceConfig

    suspend fun saveCustomSource(config: DriveSourceConfig)

    suspend fun clearCustomSource()
}
