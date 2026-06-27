package com.blasck.reino.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.blasck.reino.data.local.dao.CharacterBackupDao
import com.blasck.reino.data.local.dao.CharacterDao
import com.blasck.reino.data.local.entity.CharacterBackupEntity
import com.blasck.reino.data.local.entity.CharacterEntity
import com.blasck.reino.data.local.dao.ExpertiseDao
import com.blasck.reino.data.local.entity.ExpertiseEntity

@Database(
    entities = [CharacterEntity::class, ExpertiseEntity::class, CharacterBackupEntity::class],
    version = 5,
    exportSchema = true,
)
abstract class ReinoDatabase : RoomDatabase() {
    abstract fun characterDao(): CharacterDao

    abstract fun expertiseDao(): ExpertiseDao

    abstract fun characterBackupDao(): CharacterBackupDao
}
