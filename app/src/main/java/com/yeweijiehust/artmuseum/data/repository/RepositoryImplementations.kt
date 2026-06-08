package com.yeweijiehust.artmuseum.data.repository

import androidx.room.withTransaction
import com.yeweijiehust.artmuseum.BuildConfig
import com.yeweijiehust.artmuseum.data.local.ArtMuseumDatabase
import com.yeweijiehust.artmuseum.data.local.ImageDao
import com.yeweijiehust.artmuseum.data.local.toDomain
import com.yeweijiehust.artmuseum.data.local.toEntity
import com.yeweijiehust.artmuseum.data.preferences.AppPreferences
import com.yeweijiehust.artmuseum.data.remote.ArtMuseumApi
import com.yeweijiehust.artmuseum.data.remote.ImageUpdateDto
import com.yeweijiehust.artmuseum.data.remote.LoginRequestDto
import com.yeweijiehust.artmuseum.data.remote.RegisterRequestDto
import com.yeweijiehust.artmuseum.data.remote.SessionCookieJar
import com.yeweijiehust.artmuseum.data.remote.SessionStore
import com.yeweijiehust.artmuseum.data.remote.apiCall
import com.yeweijiehust.artmuseum.data.remote.apiCallUnit
import com.yeweijiehust.artmuseum.data.remote.toDomain
import com.yeweijiehust.artmuseum.domain.model.AppFailure
import com.yeweijiehust.artmuseum.domain.model.AppLanguage
import com.yeweijiehust.artmuseum.domain.model.GalleryPage
import com.yeweijiehust.artmuseum.domain.model.ImageUpdate
import com.yeweijiehust.artmuseum.domain.model.MuseumImage
import com.yeweijiehust.artmuseum.domain.model.UploadInput
import com.yeweijiehust.artmuseum.domain.model.User
import com.yeweijiehust.artmuseum.domain.repository.AuthRepository
import com.yeweijiehust.artmuseum.domain.repository.EndpointRepository
import com.yeweijiehust.artmuseum.domain.repository.GalleryRepository
import com.yeweijiehust.artmuseum.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URI
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EndpointRepositoryImpl @Inject constructor(
    private val preferences: AppPreferences,
    private val api: ArtMuseumApi,
    private val json: Json,
    private val cookieJar: SessionCookieJar,
    private val database: ArtMuseumDatabase,
    private val sessionStore: SessionStore
) : EndpointRepository {
    override val endpoint: Flow<String> = preferences.endpoint

    override suspend fun verifyAndSave(endpoint: String) {
        val normalized = normalizeEndpoint(endpoint)
        val health = apiCall(json) { api.health("$normalized/api/health") }
        if (!health.ok || health.service != "artmuseum-api") {
            throw AppFailure.InvalidEndpoint("Unexpected service")
        }
        if (normalized != preferences.endpoint.first()) {
            cookieJar.clear()
            sessionStore.user.value = null
            database.clearAllTables()
            preferences.setEndpoint(normalized)
        }
    }

    private fun normalizeEndpoint(value: String): String {
        val trimmed = value.trim().trimEnd('/')
        val uri = runCatching { URI(trimmed) }.getOrNull()
            ?: throw AppFailure.InvalidEndpoint("Invalid URL")
        val local = uri.host in setOf("localhost", "127.0.0.1", "10.0.2.2")
        if (uri.scheme != "https" && !(BuildConfig.DEBUG && uri.scheme == "http" && local)) {
            throw AppFailure.InvalidEndpoint("HTTPS is required")
        }
        if (uri.host.isNullOrBlank()) throw AppFailure.InvalidEndpoint("Host is required")
        return trimmed
    }
}

@Singleton
class PreferencesRepositoryImpl @Inject constructor(
    private val preferences: AppPreferences
) : PreferencesRepository {
    override val language: Flow<AppLanguage> = preferences.language
    override suspend fun setLanguage(language: AppLanguage) = preferences.setLanguage(language)
}

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val preferences: AppPreferences,
    private val api: ArtMuseumApi,
    private val json: Json,
    private val cookieJar: SessionCookieJar,
    private val sessionStore: SessionStore
) : AuthRepository {
    override val currentUser: Flow<User?> = sessionStore.user

    override suspend fun restoreSession() {
        sessionStore.user.value = try {
            apiCall(json) { api.me(url("/api/auth/me")) }.user.toDomain()
        } catch (_: AppFailure.Unauthorized) {
            cookieJar.clear()
            null
        }
    }

    override suspend fun login(email: String, password: String) {
        sessionStore.user.value = apiCall(json) {
            api.login(url("/api/auth/login"), LoginRequestDto(email.trim(), password))
        }.user.toDomain()
    }

    override suspend fun register(displayName: String, email: String, password: String) {
        sessionStore.user.value = apiCall(json) {
            api.register(url("/api/auth/register"), RegisterRequestDto(displayName.trim(), email.trim(), password))
        }.user.toDomain()
    }

    override suspend fun logout() {
        runCatching { apiCallUnit(json) { api.logout(url("/api/auth/logout")) } }
        cookieJar.clear()
        sessionStore.user.value = null
    }

    private suspend fun url(path: String) = "${preferences.endpoint.first()}$path"
}

@Singleton
class GalleryRepositoryImpl @Inject constructor(
    private val preferences: AppPreferences,
    private val api: ArtMuseumApi,
    private val json: Json,
    private val dao: ImageDao,
    private val database: ArtMuseumDatabase,
    private val cookieJar: SessionCookieJar,
    private val sessionStore: SessionStore
) : GalleryRepository {
    override fun observePublicImages(): Flow<List<MuseumImage>> =
        dao.observePublic().map { items -> items.map { it.toDomain() } }

    override fun observeMyImages(): Flow<List<MuseumImage>> =
        dao.observeMine().map { items -> items.map { it.toDomain() } }

    override suspend fun refreshPublic(): GalleryPage {
        val page = apiCall(json) { api.images(url("/api/images?limit=20")) }.toDomain()
        database.withTransaction {
            dao.clearPublicPositions()
            page.items.forEachIndexed { index, image ->
                val mine = dao.getForMerge(image.id)?.minePosition
                dao.upsert(image.toEntity(publicPosition = index, minePosition = mine))
            }
        }
        return page
    }

    override suspend fun loadMorePublic(cursor: String): GalleryPage {
        val encoded = java.net.URLEncoder.encode(cursor, Charsets.UTF_8.name())
        val page = apiCall(json) { api.images(url("/api/images?limit=20&cursor=$encoded")) }.toDomain()
        val start = dao.observePublic().first().size
        database.withTransaction {
            page.items.forEachIndexed { index, image ->
                val mine = dao.getForMerge(image.id)?.minePosition
                dao.upsert(image.toEntity(publicPosition = start + index, minePosition = mine))
            }
        }
        return page
    }

    override suspend fun refreshMine() {
        val items = authenticated { apiCall(json) { api.myImages(url("/api/images/mine")) } }.items.map { it.toDomain() }
        database.withTransaction {
            dao.clearMinePositions()
            items.forEachIndexed { index, image ->
                val public = dao.getForMerge(image.id)?.publicPosition
                dao.upsert(image.toEntity(publicPosition = public, minePosition = index))
            }
        }
    }

    override suspend fun getImage(id: String): MuseumImage {
        return try {
            val image = apiCall(json) { api.image(url("/api/images/$id")) }.toDomain()
            val cached = dao.getForMerge(id)
            dao.upsert(image.toEntity(cached?.publicPosition, cached?.minePosition))
            image
        } catch (failure: AppFailure.Offline) {
            dao.get(id)?.toDomain() ?: throw failure
        }
    }

    override suspend fun upload(input: UploadInput): MuseumImage {
        val fileBody = input.bytes.toRequestBody(input.mimeType.toMediaType())
        val file = MultipartBody.Part.createFormData("file", input.fileName, fileBody)
        val textType = "text/plain".toMediaType()
        val image = authenticated { apiCall(json) {
            api.upload(
                url("/api/images"),
                file,
                input.title.toRequestBody(textType),
                input.description.toRequestBody(textType),
                input.altText.toRequestBody(textType)
            )
        } }.toDomain()
        refreshPublic()
        refreshMine()
        return image
    }

    override suspend fun update(id: String, update: ImageUpdate): MuseumImage {
        val image = authenticated { apiCall(json) {
            api.update(url("/api/images/$id"), ImageUpdateDto(update.title, update.description, update.altText))
        } }.toDomain()
        val cached = dao.getForMerge(id)
        dao.upsert(image.toEntity(cached?.publicPosition, cached?.minePosition))
        return image
    }

    override suspend fun delete(id: String) {
        authenticated { apiCallUnit(json) { api.delete(url("/api/images/$id")) } }
        dao.delete(id)
    }

    override suspend fun clear() = dao.clear()

    private suspend fun url(path: String) = "${preferences.endpoint.first()}$path"

    private suspend fun <T> authenticated(block: suspend () -> T): T {
        return try {
            block()
        } catch (failure: AppFailure.Unauthorized) {
            cookieJar.clear()
            sessionStore.user.value = null
            throw failure
        }
    }
}
