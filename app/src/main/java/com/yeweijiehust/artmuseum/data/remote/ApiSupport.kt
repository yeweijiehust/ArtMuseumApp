package com.yeweijiehust.artmuseum.data.remote

import com.yeweijiehust.artmuseum.domain.model.AppFailure
import kotlinx.serialization.json.Json
import retrofit2.Response
import java.io.IOException

suspend fun <T : Any> apiCall(json: Json, block: suspend () -> Response<T>): T {
    val response = try {
        block()
    } catch (_: IOException) {
        throw AppFailure.Offline
    }
    response.body()?.let { body ->
        if (response.isSuccessful) return body
    }
    val error = response.errorBody()?.string()?.let {
        runCatching { json.decodeFromString<ApiErrorBodyDto>(it).error }.getOrNull()
    }
    throw when (response.code()) {
        401 -> AppFailure.Unauthorized
        403 -> AppFailure.Forbidden
        404 -> AppFailure.NotFound
        else -> AppFailure.Api(error?.code ?: "HTTP_${response.code()}", error?.message ?: response.message())
    }
}

suspend fun apiCallUnit(json: Json, block: suspend () -> Response<Unit>) {
    val response = try {
        block()
    } catch (_: IOException) {
        throw AppFailure.Offline
    }
    if (response.isSuccessful) return
    val error = response.errorBody()?.string()?.let {
        runCatching { json.decodeFromString<ApiErrorBodyDto>(it).error }.getOrNull()
    }
    throw when (response.code()) {
        401 -> AppFailure.Unauthorized
        403 -> AppFailure.Forbidden
        404 -> AppFailure.NotFound
        else -> AppFailure.Api(error?.code ?: "HTTP_${response.code()}", error?.message ?: response.message())
    }
}
