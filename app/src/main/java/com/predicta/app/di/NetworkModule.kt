package com.predicta.app.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.predicta.app.core.network.ApiCallExecutor
import com.predicta.app.core.network.PredictaApiConfig
import com.predicta.app.core.network.PredictaBaseUrlInterceptor
import com.predicta.app.core.network.PredictaBaseUrlProvider
import com.predicta.app.core.network.PredictaHeadersInterceptor
import com.predicta.app.data.remote.PredictaApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

val networkModule = module {

    single {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = true
            prettyPrint = false
            coerceInputValues = true
        }
    }

    single { PredictaBaseUrlProvider(androidContext()) }
    single { PredictaBaseUrlInterceptor(baseUrlProvider = get()) }
    single { PredictaHeadersInterceptor(sessionManager = get()) }
    single { ApiCallExecutor(json = get()) }

    single {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        OkHttpClient.Builder()
            .addInterceptor(get<PredictaBaseUrlInterceptor>())
            .addInterceptor(get<PredictaHeadersInterceptor>())
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    single<Retrofit> {
        val json: Json = get()
        val contentType = "application/json".toMediaType()

        Retrofit.Builder()
            .baseUrl(PredictaApiConfig.DEFAULT_BASE_URL)
            .client(get())
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    single<PredictaApi> {
        get<Retrofit>().create(PredictaApi::class.java)
    }
}
