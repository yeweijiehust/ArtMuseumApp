package com.yeweijiehust.artmuseum.domain.validation

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ValidatorsTest {
    @Test
    fun validatesDocumentedTextBoundaries() {
        assertThat(Validators.displayName("a")).isFalse()
        assertThat(Validators.displayName("ab")).isTrue()
        assertThat(Validators.password("1234567")).isFalse()
        assertThat(Validators.password("12345678")).isTrue()
        assertThat(Validators.title(" ")).isFalse()
        assertThat(Validators.title("x".repeat(120))).isTrue()
        assertThat(Validators.title("x".repeat(121))).isFalse()
        assertThat(Validators.description("x".repeat(1001))).isFalse()
        assertThat(Validators.altText("x".repeat(301))).isFalse()
    }

    @Test
    fun validatesUploadTypeAndSizeBoundaries() {
        assertThat(Validators.image("image/jpeg", Validators.MAX_IMAGE_BYTES.toLong())).isTrue()
        assertThat(Validators.image("image/webp", 1)).isTrue()
        assertThat(Validators.image("image/gif", 1)).isFalse()
        assertThat(Validators.image("image/png", Validators.MAX_IMAGE_BYTES + 1L)).isFalse()
        assertThat(Validators.image("image/png", 0)).isFalse()
    }
}
