package com.fieldops.app.fake

import com.fieldops.app.data.remote.api.FieldOpsApiService
import com.fieldops.app.data.remote.dto.AssetDto
import com.fieldops.app.data.remote.dto.IncidentDto
import java.io.IOException

class FakeFieldOpsApiService : FieldOpsApiService {
    var shouldThrowError = false

    override suspend fun getIncidents(): List<IncidentDto> {
        if (shouldThrowError) throw IOException("Network failed")
        return emptyList()
    }

    override suspend fun createIncident(incident: IncidentDto): IncidentDto {
        if (shouldThrowError) throw IOException("Network failed")
        return incident
    }

    override suspend fun updateIncident(id: String, incident: IncidentDto): IncidentDto {
        if (shouldThrowError) throw IOException("Network failed")
        return incident
    }

    override suspend fun deleteIncident(id: String) {
        if (shouldThrowError) throw IOException("Network failed")
    }

    override suspend fun getAssets(): List<AssetDto> {
        if (shouldThrowError) throw IOException("Network failed")
        return emptyList()
    }

    override suspend fun updateAssetStatus(id: String, status: Map<String, String>): AssetDto {
        if (shouldThrowError) throw IOException("Network failed")
        return AssetDto(
            id = id,
            name = "",
            status = status["status"] ?: "",
            assignedTo = "",
            location = "",
            lastUpdated = 0
        )
    }
}
