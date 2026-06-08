package com.yeweijiehust.artmuseum.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yeweijiehust.artmuseum.presentation.localization.LocalAppStrings
import com.yeweijiehust.artmuseum.presentation.viewmodel.UiError

@Composable
fun LoadingState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorState(error: UiError, onRetry: (() -> Unit)? = null, modifier: Modifier = Modifier) {
    val strings = LocalAppStrings.current
    val message = when (error) {
        UiError.Offline -> strings.offline
        UiError.Timeout -> strings.timeout
        UiError.Unreachable -> strings.unreachable
        UiError.ServerUnavailable -> strings.serverUnavailable
        UiError.InvalidResponse -> strings.invalidResponse
        UiError.RateLimited -> strings.rateLimited
        UiError.InvalidCredentials -> strings.invalidCredentials
        UiError.EmailExists -> strings.emailExists
        UiError.Unauthorized -> strings.unauthorized
        UiError.Forbidden -> strings.forbidden
        UiError.NotFound -> strings.notFound
        UiError.InvalidRequest -> strings.invalidRequest
        UiError.FileRequired -> strings.fileRequired
        UiError.TitleRequired -> strings.titleRequired
        UiError.InvalidFileType -> strings.invalidFileType
        UiError.FileTooLarge -> strings.fileTooLarge
        UiError.StorageFailure -> strings.storageFailure
        UiError.InvalidEndpoint -> strings.invalidEndpoint
        UiError.Generic -> strings.genericError
    }
    Column(
        modifier = modifier.fillMaxWidth().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(message, color = MaterialTheme.colorScheme.error)
        if (onRetry != null) Button(onClick = onRetry) { Text(strings.retry) }
    }
}

@Composable
fun EmptyState(message: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(message, style = MaterialTheme.typography.titleMedium)
    }
}
