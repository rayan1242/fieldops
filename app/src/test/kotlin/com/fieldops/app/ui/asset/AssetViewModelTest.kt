package com.fieldops.app.ui.asset

import com.fieldops.app.domain.model.*
import com.fieldops.app.fake.FakeAssetRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AssetViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeAssetRepository
    private lateinit var viewModel: AssetViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeAssetRepository()
        viewModel = AssetViewModel(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun makeAsset(id: String, name: String, location: String) = Asset(
        id = id,
        name = name,
        status = AssetStatus.ACTIVE,
        assignedTo = "Unit1",
        location = location
    )

    @Test
    fun `search filters assets by name`() = runTest {
        fakeRepository.addAsset(makeAsset("1", "Oxygen Tank", "Grid A1"))
        fakeRepository.addAsset(makeAsset("2", "Fire Hose", "Grid A2"))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onSearchQueryChange("Oxygen")
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.filteredAssets.value.size)
        assertEquals("Oxygen Tank", viewModel.filteredAssets.value.first().name)
    }

    @Test
    fun `search filters assets by location`() = runTest {
        fakeRepository.addAsset(makeAsset("1", "Oxygen Tank", "Grid A1"))
        fakeRepository.addAsset(makeAsset("2", "Fire Hose", "Grid B2"))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onSearchQueryChange("Grid A")
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.filteredAssets.value.size)
        assertEquals("Grid A1", viewModel.filteredAssets.value.first().location)
    }

    @Test
    fun `empty search shows all assets`() = runTest {
        fakeRepository.addAsset(makeAsset("1", "Oxygen Tank", "Grid A1"))
        fakeRepository.addAsset(makeAsset("2", "Fire Hose", "Grid B2"))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onSearchQueryChange("")
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(2, viewModel.filteredAssets.value.size)
    }

    @Test
    fun `updating asset status calls repository`() = runTest {
        viewModel.updateAssetStatus("asset_1", AssetStatus.IN_USE)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(fakeRepository.updatedAssets.containsKey("asset_1"))
        assertEquals(AssetStatus.IN_USE, fakeRepository.updatedAssets["asset_1"])
    }

    @Test
    fun `sync failure shows error state`() = runTest {
        fakeRepository.shouldThrowError = true
        viewModel.syncAssets()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value is AssetState.Error)
    }
}
