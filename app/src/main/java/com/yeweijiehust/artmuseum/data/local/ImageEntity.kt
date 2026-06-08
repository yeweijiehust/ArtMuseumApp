package com.yeweijiehust.artmuseum.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.yeweijiehust.artmuseum.domain.model.MuseumImage

@Entity(tableName = "images")
data class ImageEntity(
    @PrimaryKey val id: String,
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
    val updatedAt: String,
    val publicPosition: Int?,
    val minePosition: Int?
)

fun ImageEntity.toDomain() = MuseumImage(
    id, ownerId, ownerDisplayName, url, width, height, format, bytes, title,
    description, altText, createdAt, updatedAt
)

fun MuseumImage.toEntity(publicPosition: Int? = null, minePosition: Int? = null) = ImageEntity(
    id, ownerId, ownerDisplayName, url, width, height, format, bytes, title,
    description, altText, createdAt, updatedAt, publicPosition, minePosition
)
