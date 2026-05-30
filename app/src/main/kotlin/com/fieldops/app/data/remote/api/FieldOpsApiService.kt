package com.fieldops.app.data.remote.api

import com.fieldops.app.data.remote.dto.AssetDto
import com.fieldops.app.data.remote.dto.IncidentDto
import retrofit2.http.*

interface FieldOpsApiService {

    @GET("api/incidents")
    suspend fun getIncidents(): List<IncidentDto>

    @POST("api/incidents")
    suspend fun createIncident(@Body incident: IncidentDto): IncidentDto

    @PUT("api/incidents/{id}")
    suspend fun updateIncident(
        @Path("id") id: String,
        @Body incident: IncidentDto
    ): IncidentDto

    @DELETE("api/incidents/{id}")
    suspend fun deleteIncident(@Path("id") id: String)

    @GET("api/assets")
    suspend fun getAssets(): List<AssetDto>

    @PUT("api/assets/{id}/status")
    suspend fun updateAssetStatus(
        @Path("id") id: String,
        @Body status: Map<String, String>
    ): AssetDto
}
