package com.yeweijiehust.artmuseum.data.remote

import com.yeweijiehust.artmuseum.domain.model.GalleryPage
import com.yeweijiehust.artmuseum.domain.model.MuseumImage
import com.yeweijiehust.artmuseum.domain.model.User
import kotlinx.serialization.Serializable

@Serializable
data class HealthDto(val ok: Boolean, val service: String)

@Serializable
data class UserDto(
    val id: String,
    val email: String,
    val displayName: String,
    val createdAt: String
)

@Serializable
data class AuthResponseDto(val user: UserDto)

@Serializable
data class LoginRequestDto(val email: String, val password: String)

@Serializable
data class RegisterRequestDto(val displayName: String, val email: String, val password: String)

@Serializable
data class ImageDto(
    val id: String,
    val ownerId: String,
    val ownerDisplayName: String,
    val url: String,
    val width: Double,
    val height: Double,
    val format: String,
    val bytes: Double,
    val title: String,
    val description: String?,
    val altText: String?,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class ImageListDto(val items: List<ImageDto>, val nextCursor: String?)

@Serializable
data class ImageUpdateDto(val title: String, val description: String, val altText: String)

@Serializable
data class ApiErrorBodyDto(val error: ApiErrorDto)

@Serializable
data class ApiErrorDto(val code: String, val message: String)

fun UserDto.toDomain() = User(id, email, displayName, createdAt)

fun ImageDto.toDomain() = MuseumImage(
    id = id,
    ownerId = ownerId,
    ownerDisplayName = ownerDisplayName,
    url = url,
    width = width.toInt(),
    height = height.toInt(),
    format = format,
    bytes = bytes.toLong(),
    title = title,
    description = description,
    altText = altText,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun ImageListDto.toDomain() = GalleryPage(items.map(ImageDto::toDomain), nextCursor)
