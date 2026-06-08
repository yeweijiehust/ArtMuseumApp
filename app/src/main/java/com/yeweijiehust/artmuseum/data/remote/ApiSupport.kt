package com.yeweijiehust.artmuseum.data.remote

import com.yeweijiehust.artmuseum.domain.model.AppFailure
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import retrofit2.Response
import java.io.IOException
import java.net.ConnectException
import java.net.NoRouteToHostException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

suspend fun <T : Any> apiCall(json: Json, block: suspend () -> Response<T>): T {
    val response = try {
        block()
    } catch (failure: IOException) {
        throw mapNetworkFailure(failure)
    } catch (_: SerializationException) {
        throw AppFailure.InvalidResponse
    }
    if (response.isSuccessful) return response.body() ?: throw AppFailure.InvalidResponse
    throw mapApiFailure(response.code(), parseError(json, response), response.message())
}

suspend fun apiCallUnit(json: Json, block: suspend () -> Response<Unit>) {
    val response = try {
        block()
    } catch (failure: IOException) {
        throw mapNetworkFailure(failure)
    } catch (_: SerializationException) {
        throw AppFailure.InvalidResponse
    }
    if (response.isSuccessful) return
    throw mapApiFailure(response.code(), parseError(json, response), response.message())
}

private fun parseError(json: Json, response: Response<*>): ApiErrorDto? =
    response.errorBody()?.string()?.let {
        runCatching { json.decodeFromString<ApiErrorBodyDto>(it).error }.getOrNull()
    }

internal fun mapNetworkFailure(failure: IOException): AppFailure = when (failure) {
    is SocketTimeoutException -> AppFailure.Timeout
    is UnknownHostException, is ConnectException, is NoRouteToHostException -> AppFailure.Unreachable
    else -> AppFailure.Offline
}

internal fun mapApiFailure(status: Int, error: ApiErrorDto?, message: String): AppFailure {
    val code = error?.code
    val detail = error?.message ?: message
    return when (code) {
        "UNAUTHORIZED" -> AppFailure.Unauthorized
        "FORBIDDEN" -> AppFailure.Forbidden
        "NOT_FOUND" -> AppFailure.NotFound
        "INVALID_CREDENTIALS",
        "EMAIL_EXISTS",
        "VALIDATION_ERROR",
        "BAD_REQUEST",
        "FILE_REQUIRED",
        "TITLE_REQUIRED",
        "INVALID_FILE_TYPE",
        "FILE_TOO_LARGE",
        "STORAGE_FAILURE" -> AppFailure.Api(code, detail)
        else -> when {
            status == 401 -> AppFailure.Unauthorized
            status == 403 -> AppFailure.Forbidden
            status == 404 -> AppFailure.NotFound
            status == 408 || status == 504 -> AppFailure.Timeout
            status == 429 -> AppFailure.RateLimited
            status in 500..599 -> AppFailure.ServerUnavailable
            else -> AppFailure.Api(code ?: "HTTP_$status", detail)
        }
    }
}
