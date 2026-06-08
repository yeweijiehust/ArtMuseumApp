package com.yeweijiehust.artmuseum.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yeweijiehust.artmuseum.domain.model.AppFailure
import com.yeweijiehust.artmuseum.domain.model.AppLanguage
import com.yeweijiehust.artmuseum.domain.model.ImageUpdate
import com.yeweijiehust.artmuseum.domain.model.MuseumImage
import com.yeweijiehust.artmuseum.domain.model.UploadInput
import com.yeweijiehust.artmuseum.domain.model.User
import com.yeweijiehust.artmuseum.domain.repository.AuthRepository
import com.yeweijiehust.artmuseum.domain.repository.EndpointRepository
import com.yeweijiehust.artmuseum.domain.repository.GalleryRepository
import com.yeweijiehust.artmuseum.domain.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class UiError {
    Offline,
    Timeout,
    Unreachable,
    ServerUnavailable,
    InvalidResponse,
    RateLimited,
    InvalidCredentials,
    EmailExists,
    Unauthorized,
    Forbidden,
    NotFound,
    InvalidRequest,
    FileRequired,
    TitleRequired,
    InvalidFileType,
    FileTooLarge,
    StorageFailure,
    InvalidEndpoint,
    Generic
}

fun Throwable.toUiError() = when (this) {
    AppFailure.Offline -> UiError.Offline
    AppFailure.Timeout -> UiError.Timeout
    AppFailure.Unreachable -> UiError.Unreachable
    AppFailure.ServerUnavailable -> UiError.ServerUnavailable
    AppFailure.InvalidResponse -> UiError.InvalidResponse
    AppFailure.RateLimited -> UiError.RateLimited
    AppFailure.Unauthorized -> UiError.Unauthorized
    AppFailure.Forbidden -> UiError.Forbidden
    AppFailure.NotFound -> UiError.NotFound
    is AppFailure.InvalidEndpoint -> UiError.InvalidEndpoint
    is AppFailure.Api -> when (code) {
        "INVALID_CREDENTIALS" -> UiError.InvalidCredentials
        "EMAIL_EXISTS" -> UiError.EmailExists
        "VALIDATION_ERROR", "BAD_REQUEST" -> UiError.InvalidRequest
        "FILE_REQUIRED" -> UiError.FileRequired
        "TITLE_REQUIRED" -> UiError.TitleRequired
        "INVALID_FILE_TYPE" -> UiError.InvalidFileType
        "FILE_TOO_LARGE" -> UiError.FileTooLarge
        "STORAGE_FAILURE" -> UiError.StorageFailure
        else -> UiError.Generic
    }
    else -> UiError.Generic
}

data class AppUiState(
    val user: User? = null,
    val endpoint: String = "",
    val language: AppLanguage = AppLanguage.Device,
    val restoring: Boolean = true,
    val savingEndpoint: Boolean = false,
    val endpointSaved: Boolean = false,
    val error: UiError? = null
)

@HiltViewModel
class AppViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val endpointRepository: EndpointRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {
    private val operationState = MutableStateFlow(AppUiState())
    val state: StateFlow<AppUiState> = combine(
        authRepository.currentUser,
        endpointRepository.endpoint,
        preferencesRepository.language,
        operationState
    ) { user, endpoint, language, operation ->
        operation.copy(user = user, endpoint = endpoint, language = language)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppUiState())

    init {
        viewModelScope.launch {
            runCatching { authRepository.restoreSession() }
            operationState.value = operationState.value.copy(restoring = false)
        }
    }

    fun saveEndpoint(endpoint: String) {
        viewModelScope.launch {
            operationState.value = operationState.value.copy(savingEndpoint = true, endpointSaved = false, error = null)
            runCatching { endpointRepository.verifyAndSave(endpoint) }
                .onSuccess {
                    operationState.value = operationState.value.copy(savingEndpoint = false, endpointSaved = true)
                    authRepository.restoreSession()
                }
                .onFailure {
                    operationState.value = operationState.value.copy(savingEndpoint = false, error = it.toUiError())
                }
        }
    }

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch { preferencesRepository.setLanguage(language) }
    }

    fun logout() {
        viewModelScope.launch { authRepository.logout() }
    }
}

data class GalleryUiState(
    val images: List<MuseumImage> = emptyList(),
    val refreshing: Boolean = true,
    val loadingMore: Boolean = false,
    val nextCursor: String? = null,
    val error: UiError? = null
)

@HiltViewModel
class GalleryViewModel @Inject constructor(private val repository: GalleryRepository) : ViewModel() {
    private val operationState = MutableStateFlow(GalleryUiState())
    val state: StateFlow<GalleryUiState> = combine(repository.observePublicImages(), operationState) { images, operation ->
        operation.copy(images = images)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), GalleryUiState())

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            operationState.value = operationState.value.copy(refreshing = true, error = null)
            runCatching { repository.refreshPublic() }
                .onSuccess { operationState.value = operationState.value.copy(refreshing = false, nextCursor = it.nextCursor) }
                .onFailure { operationState.value = operationState.value.copy(refreshing = false, error = it.toUiError()) }
        }
    }

    fun loadMore() {
        val current = operationState.value
        val cursor = current.nextCursor ?: return
        if (current.loadingMore) return
        viewModelScope.launch {
            operationState.value = current.copy(loadingMore = true, error = null)
            runCatching { repository.loadMorePublic(cursor) }
                .onSuccess { operationState.value = operationState.value.copy(loadingMore = false, nextCursor = it.nextCursor) }
                .onFailure { operationState.value = operationState.value.copy(loadingMore = false, error = it.toUiError()) }
        }
    }
}

data class DetailUiState(
    val image: MuseumImage? = null,
    val loading: Boolean = true,
    val error: UiError? = null
)

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: GalleryRepository
) : ViewModel() {
    private val id: String = checkNotNull(savedStateHandle["id"])
    val state = MutableStateFlow(DetailUiState())

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            state.value = DetailUiState()
            runCatching { repository.getImage(id) }
                .onSuccess { state.value = DetailUiState(image = it, loading = false) }
                .onFailure { state.value = DetailUiState(loading = false, error = it.toUiError()) }
        }
    }
}

data class AuthUiState(val submitting: Boolean = false, val success: Boolean = false, val error: UiError? = null)

@HiltViewModel
class AuthViewModel @Inject constructor(private val repository: AuthRepository) : ViewModel() {
    val state = MutableStateFlow(AuthUiState())

    fun login(email: String, password: String) = submit { repository.login(email, password) }

    fun register(displayName: String, email: String, password: String) =
        submit { repository.register(displayName, email, password) }

    private fun submit(action: suspend () -> Unit) {
        if (state.value.submitting) return
        viewModelScope.launch {
            state.value = AuthUiState(submitting = true)
            runCatching { action() }
                .onSuccess { state.value = AuthUiState(success = true) }
                .onFailure { state.value = AuthUiState(error = it.toUiError()) }
        }
    }
}

data class UploadUiState(val submitting: Boolean = false, val success: Boolean = false, val error: UiError? = null)

@HiltViewModel
class UploadViewModel @Inject constructor(private val repository: GalleryRepository) : ViewModel() {
    val state = MutableStateFlow(UploadUiState())

    fun upload(input: UploadInput) {
        if (state.value.submitting) return
        viewModelScope.launch {
            state.value = UploadUiState(submitting = true)
            runCatching { repository.upload(input) }
                .onSuccess { state.value = UploadUiState(success = true) }
                .onFailure { state.value = UploadUiState(error = it.toUiError()) }
        }
    }
}

data class MineUiState(
    val images: List<MuseumImage> = emptyList(),
    val refreshing: Boolean = true,
    val error: UiError? = null
)

@HiltViewModel
class MineViewModel @Inject constructor(private val repository: GalleryRepository) : ViewModel() {
    private val operationState = MutableStateFlow(MineUiState())
    val state: StateFlow<MineUiState> = combine(repository.observeMyImages(), operationState) { images, operation ->
        operation.copy(images = images)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MineUiState())

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            operationState.value = operationState.value.copy(refreshing = true, error = null)
            runCatching { repository.refreshMine() }
                .onSuccess { operationState.value = operationState.value.copy(refreshing = false) }
                .onFailure { operationState.value = operationState.value.copy(refreshing = false, error = it.toUiError()) }
        }
    }
}

data class EditUiState(
    val image: MuseumImage? = null,
    val loading: Boolean = true,
    val saving: Boolean = false,
    val deleted: Boolean = false,
    val saved: Boolean = false,
    val error: UiError? = null
)

@HiltViewModel
class EditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: GalleryRepository
) : ViewModel() {
    private val id: String = checkNotNull(savedStateHandle["id"])
    val state = MutableStateFlow(EditUiState())

    init {
        viewModelScope.launch {
            runCatching { repository.getImage(id) }
                .onSuccess { state.value = EditUiState(image = it, loading = false) }
                .onFailure { state.value = EditUiState(loading = false, error = it.toUiError()) }
        }
    }

    fun save(title: String, description: String, altText: String) {
        viewModelScope.launch {
            state.value = state.value.copy(saving = true, error = null)
            runCatching { repository.update(id, ImageUpdate(title, description, altText)) }
                .onSuccess { state.value = state.value.copy(image = it, saving = false, saved = true) }
                .onFailure { state.value = state.value.copy(saving = false, error = it.toUiError()) }
        }
    }

    fun delete() {
        viewModelScope.launch {
            state.value = state.value.copy(saving = true, error = null)
            runCatching { repository.delete(id) }
                .onSuccess { state.value = state.value.copy(saving = false, deleted = true) }
                .onFailure { state.value = state.value.copy(saving = false, error = it.toUiError()) }
        }
    }
}
