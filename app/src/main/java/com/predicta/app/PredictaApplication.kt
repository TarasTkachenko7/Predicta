package com.predicta.app

import android.app.Application
import com.predicta.app.di.appModule
import com.predicta.app.di.databaseModule
import com.predicta.app.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class PredictaApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@PredictaApplication)
            modules(
                appModule,
                networkModule,
                databaseModule,
            )
        }
    }
}
