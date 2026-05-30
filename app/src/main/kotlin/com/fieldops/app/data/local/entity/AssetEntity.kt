package com.fieldops.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fieldops.app.domain.model.Asset
import com.fieldops.app.domain.model.AssetStatus

@Entity(tableName = "assets")
data class AssetEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "status") val status: String,
    @ColumnInfo(name = "assigned_to") val assignedTo: String,
    @ColumnInfo(name = "location") val location: String,
    @ColumnInfo(name = "last_updated") val lastUpdated: Long
)

fun AssetEntity.toDomain() = Asset(
    id = id,
    name = name,
    status = AssetStatus.valueOf(status),
    assignedTo = assignedTo,
    location = location,
    lastUpdated = lastUpdated
)

fun Asset.toEntity() = AssetEntity(
    id = id,
    name = name,
    status = status.name,
    assignedTo = assignedTo,
    location = location,
    lastUpdated = lastUpdated
)
