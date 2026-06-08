package com.yeweijiehust.artmuseum.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yeweijiehust.artmuseum.presentation.localization.LocalAppStrings
import com.yeweijiehust.artmuseum.presentation.viewmodel.AuthViewModel
import com.yeweijiehust.artmuseum.domain.validation.Validators

@Composable
fun AuthScreen(
    register: Boolean,
    onSuccess: () -> Unit,
    onAlternate: () -> Unit,
    viewModel: AuthViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val strings = LocalAppStrings.current
    var displayName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var attempted by remember { mutableStateOf(false) }
    val emailValid = Validators.email(email)
    val passwordValid = Validators.password(password)
    val nameValid = !register || Validators.displayName(displayName)

    LaunchedEffect(state.success) {
        if (state.success) onSuccess()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).testTag("auth_screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().widthIn(max = 480.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(if (register) strings.register else strings.login, style = MaterialTheme.typography.headlineMedium)
            if (register) {
                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it.take(80) },
                    label = { Text(strings.displayName) },
                    isError = attempted && !nameValid,
                    supportingText = if (attempted && !nameValid) ({ Text(strings.required) }) else null,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            OutlinedTextField(
                value = email,
                onValueChange = { email = it.take(254) },
                label = { Text(strings.email) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = attempted && !emailValid,
                supportingText = if (attempted && !emailValid) ({ Text(strings.invalidEmail) }) else null,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it.take(128) },
                label = { Text(strings.password) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = attempted && !passwordValid,
                supportingText = if (attempted && !passwordValid) ({ Text(strings.passwordLength) }) else null,
                modifier = Modifier.fillMaxWidth()
            )
            state.error?.let { ErrorState(it) }
            Button(
                onClick = {
                    attempted = true
                    if (emailValid && passwordValid && nameValid) {
                        if (register) viewModel.register(displayName, email, password) else viewModel.login(email, password)
                    }
                },
                enabled = !state.submitting,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (register) strings.register else strings.login)
            }
            OutlinedButton(onClick = onAlternate, modifier = Modifier.fillMaxWidth()) {
                Text(if (register) strings.haveAccount else strings.createAccount)
            }
        }
    }
}
