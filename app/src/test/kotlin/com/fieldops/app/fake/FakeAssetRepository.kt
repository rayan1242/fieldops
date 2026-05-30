package com.fieldops.app.fake

import com.fieldops.app.data.repository.AssetRepository
import com.fieldops.app.domain.model.Asset
import com.fieldops.app.domain.model.AssetStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.IOException

class FakeAssetRepository : AssetRepository(
    assetDao = FakeAssetDao(),
    apiService = FakeFieldOpsApiService()
) {
    var shouldThrowError = false
    val updatedAssets = mutableMapOf<String, AssetStatus>()

    private val _assets = MutableStateFlow<List<Asset>>(emptyList())
    override val assets: Flow<List<Asset>> = _assets

    fun addAsset(asset: Asset) {
        _assets.value = _assets.value + asset
    }

    override suspend fun syncAssets() {
        if (shouldThrowError) throw IOException("Network failed")
    }

    override suspend fun updateAssetStatus(id: String, status: AssetStatus) {
        if (shouldThrowError) throw IOException("Network failed")
        updatedAssets[id] = status
        _assets.value = _assets.value.map { asset ->
            if (asset.id == id) asset.copy(status = status) else asset
        }
    }
}
