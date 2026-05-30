package com.fieldops.app.ui.asset

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.fieldops.app.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class AssetScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
        composeRule.onNodeWithText("Assets").performClick()
    }

    @Test
    fun assetScreen_showsCorrectly() {
        composeRule.onNodeWithText("Asset Tracking").assertIsDisplayed()
    }

    @Test
    fun searchBar_isDisplayed() {
        composeRule.onNodeWithText("Search by name, location or ID").assertIsDisplayed()
    }

    @Test
    fun syncButton_isDisplayed() {
        composeRule.onNodeWithContentDescription("Sync").assertIsDisplayed()
    }

    @Test
    fun emptyState_shownWhenNoAssets() {
        composeRule.onNodeWithText("No assets assigned").assertIsDisplayed()
    }
}
