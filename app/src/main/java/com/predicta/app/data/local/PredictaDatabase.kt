package com.predicta.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [AppCacheEntity::class], version = 1, exportSchema = false)
abstract class PredictaDatabase : RoomDatabase()
