package com.fieldops.app.domain.model

import java.util.UUID

data class Incident(
    val id: String = UUID.randomUUID().toString(),
    val type: IncidentType,
    val location: String,
    val severity: Severity,
    val description: String,
    val reportedBy: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)

enum class IncidentType {
    FIRE, CRIME, ACCIDENT, MEDICAL, OTHER
}

enum class Severity {
    LOW, MEDIUM, HIGH, CRITICAL
}
