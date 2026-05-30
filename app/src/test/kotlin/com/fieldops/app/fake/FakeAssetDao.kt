package com.fieldops.app.fake

import com.fieldops.app.data.local.dao.AssetDao
import com.fieldops.app.data.local.entity.AssetEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeAssetDao : AssetDao {
    private val assets = MutableStateFlow<List<AssetEntity>>(emptyList())

    override fun getAllAssets(): Flow<List<AssetEntity>> = assets

    override suspend fun getAssetById(id: String): AssetEntity? =
        assets.value.find { it.id == id }

    override suspend fun insertAll(assetList: List<AssetEntity>) {
        assets.value = assetList
    }

    override suspend fun insertAsset(asset: AssetEntity) {
        assets.value = assets.value + asset
    }

    override suspend fun updateStatus(id: String, status: String, timestamp: Long) {
        assets.value = assets.value.map { entity ->
            if (entity.id == id) entity.copy(status = status, lastUpdated = timestamp) else entity
        }
    }

    override suspend fun deleteAsset(asset: AssetEntity) {
        assets.value = assets.value - asset
    }
}
