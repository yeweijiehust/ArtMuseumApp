package com.yeweijiehust.artmuseum.presentation.viewmodel

import com.google.common.truth.Truth.assertThat
import com.yeweijiehust.artmuseum.domain.model.AppFailure
import com.yeweijiehust.artmuseum.domain.model.GalleryPage
import com.yeweijiehust.artmuseum.domain.model.ImageUpdate
import com.yeweijiehust.artmuseum.domain.model.MuseumImage
import com.yeweijiehust.artmuseum.domain.model.UploadInput
import com.yeweijiehust.artmuseum.domain.model.User
import com.yeweijiehust.artmuseum.domain.repository.AuthRepository
import com.yeweijiehust.artmuseum.domain.repository.GalleryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

@OptIn(ExperimentalCoroutinesApi::class)
class ViewModelsTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun galleryKeepsCachedImagesWhenRefreshIsOffline() = runTest {
        val image = sampleImage()
        val repository = FakeGalleryRepository(MutableStateFlow(listOf(image)), AppFailure.Offline)
        val viewModel = GalleryViewModel(repository)
        backgroundScope.launch(mainDispatcherRule.dispatcher) { viewModel.state.collect {} }

        advanceUntilIdle()

        assertThat(viewModel.state.value.images).containsExactly(image)
        assertThat(viewModel.state.value.error).isEqualTo(UiError.Offline)
        assertThat(viewModel.state.value.refreshing).isFalse()
    }

    @Test
    fun galleryGuardsLoadMoreWithoutCursor() = runTest {
        val repository = FakeGalleryRepository(MutableStateFlow(emptyList()), null)
        val viewModel = GalleryViewModel(repository)
        backgroundScope.launch(mainDispatcherRule.dispatcher) { viewModel.state.collect {} }
        advanceUntilIdle()

        viewModel.loadMore()
        advanceUntilIdle()

        assertThat(repository.loadMoreCalls).isEqualTo(0)
    }

    @Test
    fun authPublishesSuccessAfterLogin() = runTest {
        val repository = FakeAuthRepository()
        val viewModel = AuthViewModel(repository)

        viewModel.login("user@example.com", "password123")
        advanceUntilIdle()

        assertThat(viewModel.state.value.success).isTrue()
        assertThat(repository.currentUser.value?.email).isEqualTo("user@example.com")
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val dispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestRule {
    override fun apply(base: Statement, description: Description) = object : Statement() {
        override fun evaluate() {
            Dispatchers.setMain(dispatcher)
            try {
                base.evaluate()
            } finally {
                Dispatchers.resetMain()
            }
        }
    }
}

private class FakeGalleryRepository(
    private val public: MutableStateFlow<List<MuseumImage>>,
    private val refreshFailure: Throwable?
) : GalleryRepository {
    var loadMoreCalls = 0
    private val mine = MutableStateFlow<List<MuseumImage>>(emptyList())

    override fun observePublicImages(): Flow<List<MuseumImage>> = public
    override fun observeMyImages(): Flow<List<MuseumImage>> = mine
    override suspend fun refreshPublic(): GalleryPage {
        refreshFailure?.let { throw it }
        return GalleryPage(public.value, null)
    }

    override suspend fun loadMorePublic(cursor: String): GalleryPage {
        loadMoreCalls++
        return GalleryPage(emptyList(), null)
    }

    override suspend fun refreshMine() = Unit
    override suspend fun getImage(id: String) = public.value.first()
    override suspend fun upload(input: UploadInput) = public.value.first()
    override suspend fun update(id: String, update: ImageUpdate) = public.value.first()
    override suspend fun delete(id: String) = Unit
    override suspend fun clear() = Unit
}

private class FakeAuthRepository : AuthRepository {
    override val currentUser = MutableStateFlow<User?>(null)
    override suspend fun restoreSession() = Unit
    override suspend fun login(email: String, password: String) {
        currentUser.value = User("1", email, "User", "now")
    }

    override suspend fun register(displayName: String, email: String, password: String) {
        currentUser.value = User("1", email, displayName, "now")
    }

    override suspend fun logout() {
        currentUser.value = null
    }
}

private fun sampleImage() = MuseumImage(
    id = "1",
    ownerId = "owner",
    ownerDisplayName = "Owner",
    url = "https://example.com/image.jpg",
    width = 100,
    height = 100,
    format = "jpg",
    bytes = 100,
    title = "Work",
    description = null,
    altText = null,
    createdAt = "now",
    updatedAt = "now"
)
