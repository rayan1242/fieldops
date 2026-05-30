package com.fieldops.app.data.remote.dto

import com.fieldops.app.domain.model.Incident
import com.fieldops.app.domain.model.IncidentType
import com.fieldops.app.domain.model.Severity
import com.google.gson.annotations.SerializedName

data class IncidentDto(
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String,
    @SerializedName("location") val location: String,
    @SerializedName("severity") val severity: String,
    @SerializedName("description") val description: String,
    @SerializedName("reported_by") val reportedBy: String,
    @SerializedName("timestamp") val timestamp: Long
)

fun IncidentDto.toDomain() = Incident(
    id = id,
    type = IncidentType.valueOf(type),
    location = location,
    severity = Severity.valueOf(severity),
    description = description,
    reportedBy = reportedBy,
    timestamp = timestamp,
    isSynced = true
)

fun Incident.toDto() = IncidentDto(
    id = id,
    type = type.name,
    location = location,
    severity = severity.name,
    description = description,
    reportedBy = reportedBy,
    timestamp = timestamp
)
