package com.fieldops.app.ui.incident

import com.fieldops.app.domain.model.*
import com.fieldops.app.fake.FakeIncidentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class IncidentViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeIncidentRepository
    private lateinit var viewModel: IncidentViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeIncidentRepository()
        viewModel = IncidentViewModel(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun validIncident(location: String = "Main St", description: String = "Building fire") =
        Incident(
            type = IncidentType.FIRE,
            location = location,
            severity = Severity.HIGH,
            description = description,
            reportedBy = "Officer001"
        )

    @Test
    fun `filing incident without location shows error`() = runTest {
        viewModel.fileIncident(validIncident(location = ""))

        assertTrue(viewModel.uiState.value is IncidentState.Error)
        assertEquals("Location is required", (viewModel.uiState.value as IncidentState.Error).message)
    }

    @Test
    fun `filing incident without description shows error`() = runTest {
        viewModel.fileIncident(validIncident(description = ""))

        assertTrue(viewModel.uiState.value is IncidentState.Error)
        assertEquals("Description is required", (viewModel.uiState.value as IncidentState.Error).message)
    }

    @Test
    fun `filing valid incident shows success`() = runTest {
        viewModel.fileIncident(validIncident())
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value is IncidentState.Success)
    }

    @Test
    fun `filing valid incident calls repository`() = runTest {
        viewModel.fileIncident(validIncident())
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(fakeRepository.filedIncidents.isNotEmpty())
        assertEquals("Main St", fakeRepository.filedIncidents.first().location)
    }

    @Test
    fun `network failure shows error state`() = runTest {
        fakeRepository.shouldThrowError = true
        viewModel.fileIncident(validIncident())
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value is IncidentState.Error)
    }

    @Test
    fun `resetState clears error state`() = runTest {
        fakeRepository.shouldThrowError = true
        viewModel.fileIncident(validIncident())
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.uiState.value is IncidentState.Error)

        viewModel.resetState()

        assertTrue(viewModel.uiState.value is IncidentState.Idle)
    }

    @Test
    fun `deleting incident calls repository`() = runTest {
        viewModel.deleteIncident(validIncident())
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(fakeRepository.deletedIncidents.isNotEmpty())
    }
}
