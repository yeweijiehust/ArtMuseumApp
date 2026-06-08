package com.yeweijiehust.artmuseum.data.remote

import com.google.common.truth.Truth.assertThat
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assume.assumeTrue
import org.junit.Test
import java.io.File

class LiveContractDeserializationTest {
    private val json = Json { ignoreUnknownKeys = true }
    private val fixtureDir = System.getenv("ARTMUSEUM_CONTRACT_FIXTURE_DIR")?.let(::File)

    @Test
    fun liveFixturesDeserializeWithProductionSerializers() {
        assumeTrue(fixtureDir?.isDirectory == true)
        val health = json.decodeFromString<HealthDto>(fixture("health.json"))
        val gallery = json.decodeFromString<ImageListDto>(fixture("gallery.json"))
        val image = json.decodeFromString<ImageDto>(fixture("image.json"))
        val error = json.decodeFromString<ApiErrorBodyDto>(fixture("unauthorized.json"))

        assertThat(health).isEqualTo(HealthDto(true, "artmuseum-api"))
        assertThat(gallery.items).isNotEmpty()
        assertThat(gallery.nextCursor).isNotEmpty()
        assertThat(gallery.items.first().title).isNotEmpty()
        assertThat(image.id).isEqualTo(gallery.items.first().id)
        assertThat(error.error.code).isEqualTo("UNAUTHORIZED")
    }

    @Test
    fun liveOpenApiContainsRequiredContract() {
        assumeTrue(fixtureDir?.isDirectory == true)
        val root = json.parseToJsonElement(fixture("openapi.json")).jsonObject
        val paths = root.getValue("paths").jsonObject
        val schemes = root.getValue("components").jsonObject.getValue("securitySchemes").jsonObject

        assertThat(paths.keys).containsAtLeast(
            "/api/health",
            "/api/auth/register",
            "/api/auth/login",
            "/api/auth/logout",
            "/api/auth/me",
            "/api/images",
            "/api/images/mine",
            "/api/images/{id}"
        )
        assertThat(
            schemes.getValue("cookieAuth").jsonObject.getValue("name").jsonPrimitive.content
        ).isEqualTo("am_session")
        assertThat(paths.getValue("/api/images").jsonObject).containsKey("post")
    }

    private fun fixture(name: String) = File(fixtureDir, name).readText()
}
