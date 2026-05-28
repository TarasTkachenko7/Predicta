package com.predicta.app.core.network

import android.content.Context
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PredictaBaseUrlProvider(context: Context) {

    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    private val _baseUrl = MutableStateFlow(readBaseUrl())
    val baseUrl: StateFlow<String> = _baseUrl.asStateFlow()

    fun getBaseUrl(): String {
        return _baseUrl.value
    }

    fun setBaseUrl(baseUrl: String) {
        val normalizedBaseUrl = baseUrl.normalizedBaseUrl()
        preferences.edit()
            .putString(KEY_BASE_URL, normalizedBaseUrl)
            .apply()
        _baseUrl.update { normalizedBaseUrl }
    }

    fun resetBaseUrl() {
        preferences.edit()
            .remove(KEY_BASE_URL)
            .apply()
        _baseUrl.update { PredictaApiConfig.DEFAULT_BASE_URL }
    }

    private fun readBaseUrl(): String {
        return preferences.getString(KEY_BASE_URL, null)
            ?.takeIf { it.isNotBlank() }
            ?.normalizedBaseUrl()
            ?: PredictaApiConfig.DEFAULT_BASE_URL
    }

    private fun String.normalizedBaseUrl(): String {
        val trimmed = trim()
            .removeSuffix("/swagger")
            .removeSuffix("/swagger/")

        val httpUrl = trimmed.toHttpUrlOrNull()
        if (httpUrl != null) {
            return httpUrl.newBuilder()
                .encodedPath("/")
                .encodedQuery(null)
                .fragment(null)
                .build()
                .toString()
        }

        return if (trimmed.endsWith("/")) trimmed else "$trimmed/"
    }

    private companion object {
        const val PREFERENCES_NAME = "predicta_network"
        const val KEY_BASE_URL = "base_url"
    }
}
