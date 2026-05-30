package com.fieldops.app.ui.asset

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.core.app.ActivityScenario
import com.fieldops.app.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class AssetScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createEmptyComposeRule()

    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun setup() {
        hiltRule.inject()
        scenario = ActivityScenario.launch(MainActivity::class.java)
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Assets").performClick()
        composeRule.waitForIdle()
    }

    @After
    fun tearDown() {
        scenario.close()
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
