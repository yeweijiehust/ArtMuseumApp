package com.yeweijiehust.artmuseum.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.yeweijiehust.artmuseum.domain.model.MuseumImage
import com.yeweijiehust.artmuseum.presentation.localization.LocalAppStrings
import com.yeweijiehust.artmuseum.presentation.viewmodel.DetailViewModel
import com.yeweijiehust.artmuseum.presentation.viewmodel.GalleryViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    onImage: (String) -> Unit,
    viewModel: GalleryViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val strings = LocalAppStrings.current
    val gridState = rememberLazyGridState()

    LaunchedEffect(gridState, state.nextCursor) {
        snapshotFlow {
            val last = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            last >= state.images.lastIndex - 3 && state.nextCursor != null
        }.distinctUntilChanged().filter { it }.collect { viewModel.loadMore() }
    }

    PullToRefreshBox(
        isRefreshing = state.refreshing,
        onRefresh = viewModel::refresh,
        modifier = Modifier.fillMaxSize()
    ) {
        when {
            state.images.isEmpty() && state.refreshing -> LoadingState()
            state.images.isEmpty() && state.error != null -> ErrorState(state.error!!, viewModel::refresh)
            state.images.isEmpty() -> EmptyState(strings.emptyGallery)
            else -> LazyVerticalGrid(
                columns = GridCells.Adaptive(164.dp),
                state = gridState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.images, key = { it.id }) { image ->
                    MuseumImageCard(image, onImage)
                }
                if (state.error != null) {
                    item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                        ErrorState(state.error!!, viewModel::loadMore)
                    }
                }
                if (state.loadingMore) {
                    item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                        LoadingState()
                    }
                }
            }
        }
    }
}

@Composable
fun MuseumImageCard(image: MuseumImage, onImage: (String) -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth().clickable { onImage(image.id) },
        shape = RoundedCornerShape(6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        AsyncImage(
            model = image.url,
            contentDescription = image.altText ?: image.title,
            modifier = Modifier.fillMaxWidth().aspectRatio(
                if (image.width > 0 && image.height > 0) image.width.toFloat() / image.height else 1f
            ).heightIn(min = 120.dp, max = 280.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentScale = ContentScale.Crop
        )
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(image.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(image.ownerDisplayName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
        }
    }
}

@Composable
fun DetailScreen(viewModel: DetailViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val strings = LocalAppStrings.current
    when {
        state.loading -> LoadingState()
        state.error != null -> ErrorState(state.error!!, viewModel::load)
        state.image != null -> {
            val image = state.image!!
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AsyncImage(
                    model = image.url,
                    contentDescription = image.altText ?: image.title,
                    modifier = Modifier.fillMaxWidth().weight(1f).widthIn(max = 900.dp),
                    contentScale = ContentScale.Fit
                )
                Column(
                    modifier = Modifier.fillMaxWidth().widthIn(max = 900.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(image.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("${strings.by} ${image.ownerDisplayName}", color = MaterialTheme.colorScheme.secondary)
                    Text(image.description ?: strings.noDescription)
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("${strings.dimensions}: ${image.width} × ${image.height}", style = MaterialTheme.typography.bodySmall)
                        Text("${strings.format}: ${image.format.uppercase()}", style = MaterialTheme.typography.bodySmall)
                        Text("${strings.fileSize}: ${image.bytes / 1024} KiB", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
