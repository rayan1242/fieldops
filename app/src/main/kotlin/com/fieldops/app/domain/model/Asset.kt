package com.fieldops.app.domain.model

import java.util.UUID

data class Asset(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val status: AssetStatus,
    val assignedTo: String,
    val location: String,
    val lastUpdated: Long = System.currentTimeMillis()
)

enum class AssetStatus {
    ACTIVE, IN_USE, NEEDS_RESTOCK, FAULTY
}
