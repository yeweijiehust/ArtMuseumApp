package com.yeweijiehust.artmuseum.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yeweijiehust.artmuseum.domain.model.AppLanguage
import com.yeweijiehust.artmuseum.presentation.localization.LocalAppStrings
import com.yeweijiehust.artmuseum.presentation.viewmodel.AppUiState

@Composable
fun SettingsScreen(
    state: AppUiState,
    onEndpoint: (String) -> Unit,
    onLanguage: (AppLanguage) -> Unit,
    onLogout: () -> Unit
) {
    val strings = LocalAppStrings.current
    var endpoint by remember { mutableStateOf(state.endpoint) }
    LaunchedEffect(state.endpoint) { endpoint = state.endpoint }
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().widthIn(max = 680.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            OutlinedTextField(
                value = endpoint,
                onValueChange = { endpoint = it },
                label = { Text(strings.endpoint) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = { onEndpoint(endpoint) },
                enabled = !state.savingEndpoint,
                modifier = Modifier.fillMaxWidth()
            ) { Text(strings.testAndSave) }
            if (state.endpointSaved) Text(strings.connectionSaved, color = MaterialTheme.colorScheme.primary)
            state.error?.let { ErrorState(it) }
            Text(strings.language, style = MaterialTheme.typography.titleMedium)
            SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                val choices = listOf(
                    AppLanguage.Device to strings.deviceLanguage,
                    AppLanguage.English to strings.english,
                    AppLanguage.Chinese to strings.chinese
                )
                choices.forEachIndexed { index, choice ->
                    SegmentedButton(
                        selected = state.language == choice.first,
                        onClick = { onLanguage(choice.first) },
                        shape = SegmentedButtonDefaults.itemShape(index, choices.size)
                    ) { Text(choice.second) }
                }
            }
            Text(strings.account, style = MaterialTheme.typography.titleMedium)
            if (state.user == null) {
                Text(strings.signedOut)
            } else {
                Text(state.user.displayName, style = MaterialTheme.typography.titleMedium)
                Text(state.user.email)
                OutlinedButton(onClick = onLogout, modifier = Modifier.fillMaxWidth()) { Text(strings.logout) }
            }
        }
    }
}
