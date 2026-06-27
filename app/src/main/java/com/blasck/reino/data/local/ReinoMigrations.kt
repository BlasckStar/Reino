package com.blasck.reino.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 =
    object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `expertises` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `characterId` INTEGER NOT NULL,
                    `position` INTEGER NOT NULL,
                    `name` TEXT NOT NULL,
                    `description` TEXT NOT NULL,
                    `difficultType` TEXT NOT NULL,
                    `difficultLevel` TEXT NOT NULL,
                    `difficultExtra` TEXT NOT NULL,
                    `cost` TEXT NOT NULL,
                    `level` TEXT NOT NULL,
                    `modifiers` TEXT NOT NULL,
                    FOREIGN KEY(`characterId`) REFERENCES `characters`(`id`)
                        ON UPDATE NO ACTION ON DELETE CASCADE
                )
                """.trimIndent(),
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_expertises_characterId` " +
                    "ON `expertises` (`characterId`)",
            )
        }
    }

val MIGRATION_2_3 =
    object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "ALTER TABLE `characters` ADD COLUMN `characterJson` TEXT NOT NULL DEFAULT ''",
            )
        }
    }

val MIGRATION_3_4 =
    object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `character_backups` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `characterId` INTEGER NOT NULL,
                    `createdAtEpochMillis` INTEGER NOT NULL,
                    `sourceFileName` TEXT NOT NULL,
                    `sheetFormat` TEXT NOT NULL,
                    `importedAtEpochMillis` INTEGER NOT NULL,
                    `updatedAtEpochMillis` INTEGER NOT NULL,
                    `currentHitPoints` INTEGER NOT NULL,
                    `currentFatiguePoints` INTEGER NOT NULL,
                    `currentManaPoints` INTEGER NOT NULL,
                    `notes` TEXT NOT NULL,
                    `characterJson` TEXT NOT NULL,
                    FOREIGN KEY(`characterId`) REFERENCES `characters`(`id`)
                        ON UPDATE NO ACTION ON DELETE CASCADE
                )
                """.trimIndent(),
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_character_backups_characterId` " +
                    "ON `character_backups` (`characterId`)",
            )
        }
    }

val MIGRATION_4_5 =
    object : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "ALTER TABLE `characters` ADD COLUMN `remoteSheetFileId` TEXT NOT NULL DEFAULT ''",
            )
            db.execSQL(
                "ALTER TABLE `characters` ADD COLUMN `remoteImageFileId` TEXT NOT NULL DEFAULT ''",
            )
            db.execSQL(
                "ALTER TABLE `character_backups` ADD COLUMN `remoteSheetFileId` TEXT NOT NULL DEFAULT ''",
            )
            db.execSQL(
                "ALTER TABLE `character_backups` ADD COLUMN `remoteImageFileId` TEXT NOT NULL DEFAULT ''",
            )
        }
    }
