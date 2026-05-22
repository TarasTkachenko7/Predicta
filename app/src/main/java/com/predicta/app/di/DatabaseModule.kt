package com.predicta.app.di

import com.predicta.app.data.local.PredictaDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import androidx.room.Room

val databaseModule = module {
    single<PredictaDatabase> {
        Room.databaseBuilder(
            androidContext(),
            PredictaDatabase::class.java,
            "predicta_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}
