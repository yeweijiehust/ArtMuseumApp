package com.yeweijiehust.artmuseum.domain.model

data class MuseumImage(
    val id: String,
    val ownerId: String,
    val ownerDisplayName: String,
    val url: String,
    val width: Int,
    val height: Int,
    val format: String,
    val bytes: Long,
    val title: String,
    val description: String?,
    val altText: String?,
    val createdAt: String,
    val updatedAt: String
)

data class GalleryPage(
    val items: List<MuseumImage>,
    val nextCursor: String?
)

data class User(
    val id: String,
    val email: String,
    val displayName: String,
    val createdAt: String
)

enum class AppLanguage {
    Device,
    English,
    Chinese
}

data class UploadInput(
    val bytes: ByteArray,
    val fileName: String,
    val mimeType: String,
    val title: String,
    val description: String,
    val altText: String
)

data class ImageUpdate(
    val title: String,
    val description: String,
    val altText: String
)

sealed class AppFailure(message: String) : Exception(message) {
    sealed class Network(message: String) : AppFailure(message)

    data object Offline : Network("OFFLINE")
    data object Timeout : Network("TIMEOUT")
    data object Unreachable : Network("UNREACHABLE")
    data object ServerUnavailable : AppFailure("SERVER_UNAVAILABLE")
    data object InvalidResponse : AppFailure("INVALID_RESPONSE")
    data object RateLimited : AppFailure("RATE_LIMITED")
    data object Unauthorized : AppFailure("UNAUTHORIZED")
    data object Forbidden : AppFailure("FORBIDDEN")
    data object NotFound : AppFailure("NOT_FOUND")
    data class Api(val code: String, val detail: String) : AppFailure(detail)
    data class InvalidEndpoint(val detail: String) : AppFailure(detail)
}
