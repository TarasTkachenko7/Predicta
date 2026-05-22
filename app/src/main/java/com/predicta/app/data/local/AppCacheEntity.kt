package com.predicta.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_cache")
data class AppCacheEntity(
    @PrimaryKey val id: Int = 1,
    val lastUpdated: Long
)
