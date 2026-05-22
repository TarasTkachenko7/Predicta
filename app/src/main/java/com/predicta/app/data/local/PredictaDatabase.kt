package com.predicta.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Central Room database for the Predicta app.
 *
 * Entities and DAOs will be registered here as features are implemented.
 * The database is configured with no entities initially; add entities to the
 * @Database annotation and increment the version when adding new tables.
 */
@Database(entities = [AppCacheEntity::class], version = 1, exportSchema = false)
abstract class PredictaDatabase : RoomDatabase()