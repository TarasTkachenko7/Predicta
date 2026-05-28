package com.predicta.app.core.network

import com.predicta.app.core.error.AppError
import com.predicta.app.core.error.AppResult
import com.predicta.app.data.remote.dto.ErrorResponseDto
import java.io.IOException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import retrofit2.Response

class ApiCallExecutor(
    private val json: Json,
) {

    suspend fun <T> execute(request: suspend () -> Response<T>): AppResult<T> {
        return try {
            val response = request()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    AppResult.Success(body)
                } else {
                    AppResult.Failure(AppError.Unknown("Empty response body"))
                }
            } else {
                AppResult.Failure(response.toAppError())
            }
        } catch (exception: IOException) {
            AppResult.Failure(AppError.Network)
        } catch (exception: SerializationException) {
            AppResult.Failure(AppError.Unknown(exception.message))
        } catch (exception: HttpException) {
            AppResult.Failure(exception.response()?.toAppError() ?: AppError.Unknown(exception.message()))
        }
    }

    private fun Response<*>.toAppError(): AppError {
        val fallbackMessage = message().takeIf { it.isNotBlank() } ?: "HTTP ${code()}"
        val remoteMessage = errorBody()
            ?.string()
            ?.takeIf { it.isNotBlank() }
            ?.let { rawBody ->
                runCatching { json.decodeFromString<ErrorResponseDto>(rawBody).error }
                    .getOrNull()
            }

        return AppError.Remote(
            message = remoteMessage ?: fallbackMessage,
            code = code(),
        )
    }
}
