package com.predicta.app.core.network

import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response

class PredictaBaseUrlInterceptor(
    private val baseUrlProvider: PredictaBaseUrlProvider,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val baseUrl = baseUrlProvider.getBaseUrl().toHttpUrlOrNull()
            ?: PredictaApiConfig.DEFAULT_BASE_URL.toHttpUrl()

        val url = request.url.newBuilder()
            .scheme(baseUrl.scheme)
            .host(baseUrl.host)
            .port(baseUrl.port)
            .build()

        return chain.proceed(
            request.newBuilder()
                .url(url)
                .build(),
        )
    }
}
