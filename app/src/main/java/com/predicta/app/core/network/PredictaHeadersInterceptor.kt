package com.predicta.app.core.network

import com.predicta.app.feature_auth.data.session.UserSessionManager
import okhttp3.Interceptor
import okhttp3.Response

class PredictaHeadersInterceptor(
    private val sessionManager: UserSessionManager,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath
        val builder = request.newBuilder()
            .header("ngrok-skip-browser-warning", "true")
            .header("Content-Type", "application/json")

        if (path.requiresAuthorization()) {
            sessionManager.getToken()
                .takeIf { it.isNotBlank() }
                ?.let { token -> builder.header("Authorization", "Bearer $token") }
        }

        return chain.proceed(builder.build())
    }

    private fun String.requiresAuthorization(): Boolean {
        val normalizedPath = trimEnd('/')
        return contains("/api/") &&
            normalizedPath != "/api/auth/register" &&
            normalizedPath != "/api/auth/login"
    }
}
