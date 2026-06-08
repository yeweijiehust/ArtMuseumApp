package com.yeweijiehust.artmuseum.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF315C4C),
    onPrimary = Color.White,
    secondary = Color(0xFF89512F),
    tertiary = Color(0xFF3D6384),
    background = Color(0xFFF9F8F5),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFE9ECE7),
    onSurface = Color(0xFF20221F),
    error = Color(0xFFB3261E)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFA8D1BE),
    onPrimary = Color(0xFF073728),
    secondary = Color(0xFFFFB68A),
    tertiary = Color(0xFFA4C9ED),
    background = Color(0xFF111412),
    surface = Color(0xFF191C19),
    surfaceVariant = Color(0xFF303631),
    onSurface = Color(0xFFE2E3DF),
    error = Color(0xFFFFB4AB)
)

@Composable
fun ArtMuseumTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        content = content
    )
}
