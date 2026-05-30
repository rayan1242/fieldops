package com.fieldops.app.data.remote.dto

import com.fieldops.app.domain.model.Asset
import com.fieldops.app.domain.model.AssetStatus
import com.google.gson.annotations.SerializedName

data class AssetDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("status") val status: String,
    @SerializedName("assigned_to") val assignedTo: String,
    @SerializedName("location") val location: String,
    @SerializedName("last_updated") val lastUpdated: Long
)

fun AssetDto.toDomain() = Asset(
    id = id,
    name = name,
    status = AssetStatus.valueOf(status),
    assignedTo = assignedTo,
    location = location,
    lastUpdated = lastUpdated
)

fun Asset.toDto() = AssetDto(
    id = id,
    name = name,
    status = status.name,
    assignedTo = assignedTo,
    location = location,
    lastUpdated = lastUpdated
)
