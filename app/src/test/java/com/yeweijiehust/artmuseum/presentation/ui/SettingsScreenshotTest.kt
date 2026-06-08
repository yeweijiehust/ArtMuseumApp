package com.yeweijiehust.artmuseum.presentation.ui

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.yeweijiehust.artmuseum.domain.model.AppLanguage
import com.yeweijiehust.artmuseum.presentation.localization.LocalAppStrings
import com.yeweijiehust.artmuseum.presentation.localization.stringsFor
import com.yeweijiehust.artmuseum.presentation.theme.ArtMuseumTheme
import com.yeweijiehust.artmuseum.presentation.viewmodel.AppUiState
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class SettingsScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun settingsEnglish() {
        composeRule.setContent {
            ArtMuseumTheme {
                val strings = stringsFor(AppLanguage.English)
                CompositionLocalProvider(LocalAppStrings provides strings) {
                    SettingsScreen(
                        state = AppUiState(
                            endpoint = "https://artmuseum-w9mm.onrender.com",
                            language = AppLanguage.English,
                            restoring = false
                        ),
                        onEndpoint = {},
                        onLanguage = {},
                        onLogout = {}
                    )
                }
            }
        }
        composeRule.onRoot().captureRoboImage()
    }
}
