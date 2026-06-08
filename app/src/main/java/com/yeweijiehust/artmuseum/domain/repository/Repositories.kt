package com.yeweijiehust.artmuseum.domain.repository

import com.yeweijiehust.artmuseum.domain.model.AppLanguage
import com.yeweijiehust.artmuseum.domain.model.GalleryPage
import com.yeweijiehust.artmuseum.domain.model.ImageUpdate
import com.yeweijiehust.artmuseum.domain.model.MuseumImage
import com.yeweijiehust.artmuseum.domain.model.UploadInput
import com.yeweijiehust.artmuseum.domain.model.User
import kotlinx.coroutines.flow.Flow

interface EndpointRepository {
    val endpoint: Flow<String>
    suspend fun verifyAndSave(endpoint: String)
}

interface PreferencesRepository {
    val language: Flow<AppLanguage>
    suspend fun setLanguage(language: AppLanguage)
}

interface AuthRepository {
    val currentUser: Flow<User?>
    suspend fun restoreSession()
    suspend fun login(email: String, password: String)
    suspend fun register(displayName: String, email: String, password: String)
    suspend fun logout()
}

interface GalleryRepository {
    fun observePublicImages(): Flow<List<MuseumImage>>
    fun observeMyImages(): Flow<List<MuseumImage>>
    suspend fun refreshPublic(): GalleryPage
    suspend fun loadMorePublic(cursor: String): GalleryPage
    suspend fun refreshMine()
    suspend fun getImage(id: String): MuseumImage
    suspend fun upload(input: UploadInput): MuseumImage
    suspend fun update(id: String, update: ImageUpdate): MuseumImage
    suspend fun delete(id: String)
    suspend fun clear()
}
