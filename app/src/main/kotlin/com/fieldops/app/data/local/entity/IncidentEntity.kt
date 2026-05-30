package com.fieldops.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fieldops.app.domain.model.Incident
import com.fieldops.app.domain.model.IncidentType
import com.fieldops.app.domain.model.Severity

@Entity(tableName = "incidents")
data class IncidentEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "location") val location: String,
    @ColumnInfo(name = "severity") val severity: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "reported_by") val reportedBy: String,
    @ColumnInfo(name = "timestamp") val timestamp: Long,
    @ColumnInfo(name = "is_synced") val isSynced: Boolean = false
)

fun IncidentEntity.toDomain() = Incident(
    id = id,
    type = IncidentType.valueOf(type),
    location = location,
    severity = Severity.valueOf(severity),
    description = description,
    reportedBy = reportedBy,
    timestamp = timestamp,
    isSynced = isSynced
)

fun Incident.toEntity() = IncidentEntity(
    id = id,
    type = type.name,
    location = location,
    severity = severity.name,
    description = description,
    reportedBy = reportedBy,
    timestamp = timestamp,
    isSynced = isSynced
)
