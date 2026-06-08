package com.yeweijiehust.artmuseum

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class MainActivityTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun galleryLoadsAndProtectedUploadRoutesToLogin() {
        composeRule.waitUntil(20_000) {
            composeRule.onAllNodes(androidx.compose.ui.test.hasTestTag("nav_gallery")).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithTag("nav_gallery").assertIsDisplayed()
        composeRule.onNodeWithTag("nav_upload").performClick()
        composeRule.onNodeWithTag("auth_screen").assertIsDisplayed()
    }
}
