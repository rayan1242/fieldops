package com.fieldops.app.data.repository

import com.fieldops.app.data.local.dao.AssetDao
import com.fieldops.app.data.local.entity.toDomain
import com.fieldops.app.data.local.entity.toEntity
import com.fieldops.app.data.remote.api.FieldOpsApiService
import com.fieldops.app.data.remote.dto.toDomain as dtoToDomain
import com.fieldops.app.domain.model.Asset
import com.fieldops.app.domain.model.AssetStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

open class AssetRepository @Inject constructor(
    private val assetDao: AssetDao,
    private val apiService: FieldOpsApiService
) {
    open val assets: Flow<List<Asset>> = assetDao.getAllAssets()
        .map { entities -> entities.map { it.toDomain() } }

    open suspend fun syncAssets() {
        withContext(Dispatchers.IO) {
            try {
                val remoteAssets = apiService.getAssets()
                assetDao.insertAll(remoteAssets.map { it.dtoToDomain().toEntity() })
            } catch (e: IOException) {
                // Network failed — Room serves cached data
            }
        }
    }

    open suspend fun updateAssetStatus(id: String, status: AssetStatus) {
        withContext(Dispatchers.IO) {
            assetDao.updateStatus(
                id = id,
                status = status.name,
                timestamp = System.currentTimeMillis()
            )
            try {
                apiService.updateAssetStatus(id = id, status = mapOf("status" to status.name))
            } catch (e: IOException) {
                // Network failed — updated locally
            }
        }
    }

    open suspend fun getAssetById(id: String): Asset? {
        return withContext(Dispatchers.IO) {
            assetDao.getAssetById(id)?.toDomain()
        }
    }
}
