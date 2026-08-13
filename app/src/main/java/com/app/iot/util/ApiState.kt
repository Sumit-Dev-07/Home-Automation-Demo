package com.app.iot.util

import com.google.gson.Gson
import okio.IOException
import retrofit2.Response
import java.net.SocketTimeoutException

sealed class ApiState<out T> {
    data object Loading : ApiState<Nothing>()
    data class Success<out T>(val data: T) : ApiState<T>()
    data class Error(val message: String, val code: Int? = null) : ApiState<Nothing>()
}

suspend fun <T> safeApiCall(
    apiResponse: suspend () -> Response<T>
): ApiState<T> = try {
    val response = apiResponse()

    if (response.isSuccessful) {
        val body = response.body()

        body?.let {
            ApiState.Success(it)
        } ?: apiError("Empty response body")

    } else {
        val errorCode = response.code()
        val serverMessage = parseErrorBody(response.errorBody()?.string())

        val errorMessage = serverMessage ?: when (errorCode) {
            401 -> "Invalid credentials"
            404 -> "User Not Found"
            500 -> "Internal server error. Please try again later."
            502 -> "Bad gateway. Please try again later."
            503 -> "Service unavailable. Please try again later."
            504 -> "Gateway timeout. Please try again later."
            else -> "Error: $errorCode ${response.message()}"
        }

        ApiState.Error(errorMessage, errorCode)
    }

} catch (ex: Exception) {
    apiError(ex.message ?: ex.toString())
} catch (io: IOException) {
    apiError(io.message ?: "No Internet")
} catch (sc: SocketTimeoutException) {
    apiError(sc.message ?: "Timeout")
}

private data class ErrorResponse(val message: String? = null)

private fun parseErrorBody(errorBodyString: String?): String? {
    if (errorBodyString.isNullOrBlank()) return null
    return try {
        Gson().fromJson(errorBodyString, ErrorResponse::class.java)?.message
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun <T> apiError(errorMessage: String): ApiState<T> = ApiState.Error(errorMessage)
