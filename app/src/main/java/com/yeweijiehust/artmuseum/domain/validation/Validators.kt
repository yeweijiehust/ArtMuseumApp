package com.yeweijiehust.artmuseum.domain.validation

object Validators {
    private val supportedImages = setOf("image/jpeg", "image/png", "image/webp")
    const val MAX_IMAGE_BYTES = 10 * 1024 * 1024

    fun email(value: String) = value.length in 3..254 && value.contains("@") && value.substringAfter("@").contains(".")
    fun password(value: String) = value.length in 8..128
    fun displayName(value: String) = value.trim().length in 2..80
    fun title(value: String) = value.trim().length in 1..120
    fun description(value: String) = value.length <= 1000
    fun altText(value: String) = value.length <= 300
    fun image(mimeType: String?, bytes: Long) = mimeType in supportedImages && bytes in 1..MAX_IMAGE_BYTES
}
