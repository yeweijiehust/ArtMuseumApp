package com.yeweijiehust.artmuseum.presentation.ui

import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.yeweijiehust.artmuseum.domain.model.UploadInput
import com.yeweijiehust.artmuseum.domain.validation.Validators
import com.yeweijiehust.artmuseum.presentation.localization.LocalAppStrings
import com.yeweijiehust.artmuseum.presentation.viewmodel.EditViewModel
import com.yeweijiehust.artmuseum.presentation.viewmodel.MineViewModel
import com.yeweijiehust.artmuseum.presentation.viewmodel.UploadViewModel

@Composable
fun UploadScreen(onSuccess: () -> Unit, viewModel: UploadViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val strings = LocalAppStrings.current
    val context = LocalContext.current
    var uri by remember { mutableStateOf<android.net.Uri?>(null) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var altText by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri = it }

    LaunchedEffect(state.success) {
        if (state.success) onSuccess()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth().widthIn(max = 640.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(strings.uploadImage, style = MaterialTheme.typography.headlineMedium)
                uri?.let {
                    AsyncImage(
                        model = it,
                        contentDescription = altText.ifBlank { title },
                        modifier = Modifier.fillMaxWidth().height(300.dp),
                        contentScale = ContentScale.Fit
                    )
                }
                OutlinedButton(
                    onClick = { picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(strings.chooseImage)
                }
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it.take(120) },
                    label = { Text(strings.title) },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it.take(1000) },
                    label = { Text(strings.description) },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = altText,
                    onValueChange = { altText = it.take(300) },
                    label = { Text(strings.altText) },
                    modifier = Modifier.fillMaxWidth()
                )
                localError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                state.error?.let { ErrorState(it) }
                Button(
                    onClick = {
                        val selected = uri
                        val resolver = context.contentResolver
                        val mime = selected?.let(resolver::getType)
                        val metadata = selected?.let { value ->
                            resolver.query(value, arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE), null, null, null)
                                ?.use { cursor ->
                                    if (cursor.moveToFirst()) {
                                        cursor.getString(0) to cursor.getLong(1)
                                    } else null
                                }
                        }
                        localError = when {
                            selected == null -> strings.required
                            !Validators.title(title) -> strings.required
                            mime !in setOf("image/jpeg", "image/png", "image/webp") -> strings.invalidFileType
                            metadata != null && metadata.second > Validators.MAX_IMAGE_BYTES -> strings.fileTooLarge
                            else -> null
                        }
                        if (localError == null && selected != null && mime != null) {
                            val bytes = resolver.openInputStream(selected)?.use { it.readBytes() }
                            if (bytes == null || !Validators.image(mime, bytes.size.toLong())) {
                                localError = strings.fileTooLarge
                            } else {
                                viewModel.upload(
                                    UploadInput(bytes, metadata?.first ?: "image", mime, title.trim(), description, altText)
                                )
                            }
                        }
                    },
                    enabled = !state.submitting,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(strings.uploadImage)
                }
            }
        }
    }
}

@Composable
fun MineScreen(onImage: (String) -> Unit, viewModel: MineViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val strings = LocalAppStrings.current
    when {
        state.images.isEmpty() && state.refreshing -> LoadingState()
        state.images.isEmpty() && state.error != null -> ErrorState(state.error!!, viewModel::refresh)
        state.images.isEmpty() -> EmptyState(strings.emptyMine)
        else -> LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.images, key = { it.id }) { image ->
                MuseumImageCard(image, onImage)
            }
        }
    }
}

@Composable
fun EditScreen(onDone: () -> Unit, viewModel: EditViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val strings = LocalAppStrings.current
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var altText by remember { mutableStateOf("") }
    var confirmDelete by remember { mutableStateOf(false) }

    LaunchedEffect(state.image?.id) {
        state.image?.let {
            title = it.title
            description = it.description.orEmpty()
            altText = it.altText.orEmpty()
        }
    }
    LaunchedEffect(state.deleted) {
        if (state.deleted) onDone()
    }
    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text(strings.confirmDelete) },
            confirmButton = {
                TextButton(onClick = { confirmDelete = false; viewModel.delete() }) { Text(strings.delete) }
            },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text(strings.cancel) } }
        )
    }
    when {
        state.loading -> LoadingState()
        state.error != null && state.image == null -> ErrorState(state.error!!)
        state.image != null -> LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().widthIn(max = 640.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    AsyncImage(
                        model = state.image!!.url,
                        contentDescription = state.image!!.altText ?: state.image!!.title,
                        modifier = Modifier.fillMaxWidth().height(280.dp),
                        contentScale = ContentScale.Fit
                    )
                    OutlinedTextField(title, { title = it.take(120) }, label = { Text(strings.title) }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(description, { description = it.take(1000) }, label = { Text(strings.description) }, minLines = 3, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(altText, { altText = it.take(300) }, label = { Text(strings.altText) }, modifier = Modifier.fillMaxWidth())
                    state.error?.let { ErrorState(it) }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = {
                                if (Validators.title(title) && Validators.description(description) && Validators.altText(altText)) {
                                    viewModel.save(title.trim(), description, altText)
                                }
                            },
                            enabled = !state.saving,
                            modifier = Modifier.weight(1f)
                        ) { Text(strings.save) }
                        OutlinedButton(
                            onClick = { confirmDelete = true },
                            enabled = !state.saving,
                            modifier = Modifier.weight(1f)
                        ) { Text(strings.delete) }
                    }
                }
            }
        }
    }
}
