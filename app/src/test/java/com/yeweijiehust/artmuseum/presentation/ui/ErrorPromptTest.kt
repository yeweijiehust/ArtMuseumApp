package com.yeweijiehust.artmuseum.presentation.ui

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.yeweijiehust.artmuseum.domain.model.AppLanguage
import com.yeweijiehust.artmuseum.presentation.localization.LocalAppStrings
import com.yeweijiehust.artmuseum.presentation.localization.stringsFor
import com.yeweijiehust.artmuseum.presentation.theme.ArtMuseumTheme
import com.yeweijiehust.artmuseum.presentation.viewmodel.UiError
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ErrorPromptTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun wrongPasswordShowsEnglishCredentialPrompt() {
        composeRule.setContent {
            ArtMuseumTheme {
                val strings = stringsFor(AppLanguage.English)
                CompositionLocalProvider(LocalAppStrings provides strings) {
                    ErrorState(UiError.InvalidCredentials)
                }
            }
        }

        composeRule.onNodeWithText("Email or password is incorrect").assertIsDisplayed()
    }

    @Test
    fun unreachableServiceShowsChineseRecoveryPrompt() {
        composeRule.setContent {
            ArtMuseumTheme {
                val strings = stringsFor(AppLanguage.Chinese)
                CompositionLocalProvider(LocalAppStrings provides strings) {
                    ErrorState(UiError.Unreachable)
                }
            }
        }

        composeRule.onNodeWithText("无法连接到艺术博物馆服务，请检查网络连接或 API 地址").assertIsDisplayed()
    }
}
