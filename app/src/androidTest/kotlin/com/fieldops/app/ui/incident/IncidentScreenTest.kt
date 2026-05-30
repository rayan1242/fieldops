package com.fieldops.app.ui.incident

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.core.app.ActivityScenario
import com.fieldops.app.MainActivity
import com.fieldops.app.data.local.dao.IncidentDao
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class IncidentScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createEmptyComposeRule()

    @Inject lateinit var incidentDao: IncidentDao

    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun setup() {
        hiltRule.inject()
        runBlocking { incidentDao.deleteAll() }
        scenario = ActivityScenario.launch(MainActivity::class.java)
        composeRule.waitForIdle()
    }

    @After
    fun tearDown() {
        scenario.close()
    }

    @Test
    fun incidentScreen_showsCorrectly() {
        composeRule.onNodeWithText("Incident Reports").assertIsDisplayed()
    }

    @Test
    fun addButton_showsForm() {
        composeRule.onNodeWithContentDescription("Toggle form").performClick()
        composeRule.onNodeWithText("File New Incident").assertIsDisplayed()
    }

    @Test
    fun submittingWithoutLocation_showsError() {
        composeRule.onNodeWithContentDescription("Toggle form").performClick()
        composeRule.onNodeWithText("Description *").performTextInput("Building fire on Main St")
        composeRule.onNodeWithText("File Incident").performClick()
        composeRule.onNodeWithText("Location is required").assertIsDisplayed()
    }

    @Test
    fun submittingWithoutDescription_showsError() {
        composeRule.onNodeWithContentDescription("Toggle form").performClick()
        composeRule.onNodeWithText("Location *").performTextInput("Main St")
        composeRule.onNodeWithText("File Incident").performClick()
        composeRule.onNodeWithText("Description is required").assertIsDisplayed()
    }

    @Test
    fun filingValidIncident_closesForm() {
        composeRule.onNodeWithContentDescription("Toggle form").performClick()
        composeRule.onNodeWithText("Location *").performTextInput("Main St")
        composeRule.onNodeWithText("Description *").performTextInput("Building fire")
        composeRule.onNodeWithText("File Incident").performClick()
        composeRule.onNodeWithText("File New Incident").assertDoesNotExist()
    }

    @Test
    fun filedIncident_appearsInList() {
        composeRule.onNodeWithContentDescription("Toggle form").performClick()
        composeRule.onNodeWithText("Location *").performTextInput("Main St")
        composeRule.onNodeWithText("Description *").performTextInput("Building fire")
        composeRule.onNodeWithText("File Incident").performClick()
        composeRule.onNodeWithText("Main St").assertIsDisplayed()
    }

    @Test
    fun emptyState_shownWhenNoIncidents() {
        composeRule.onNodeWithText("No incidents reported").assertIsDisplayed()
    }
}
