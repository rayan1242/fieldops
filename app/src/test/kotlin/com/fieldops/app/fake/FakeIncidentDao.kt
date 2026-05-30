package com.fieldops.app.fake

import com.fieldops.app.data.local.dao.IncidentDao
import com.fieldops.app.data.local.entity.IncidentEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeIncidentDao : IncidentDao {
    private val incidents = MutableStateFlow<List<IncidentEntity>>(emptyList())

    override fun getAllIncidents(): Flow<List<IncidentEntity>> = incidents

    override suspend fun getUnsyncedIncidents(): List<IncidentEntity> =
        incidents.value.filter { !it.isSynced }

    override suspend fun insertIncident(incident: IncidentEntity) {
        incidents.value = incidents.value + incident
    }

    override suspend fun markAsSynced(id: String) {
        incidents.value = incidents.value.map { entity ->
            if (entity.id == id) entity.copy(isSynced = true) else entity
        }
    }

    override suspend fun deleteIncident(incident: IncidentEntity) {
        incidents.value = incidents.value - incident
    }

    override suspend fun getIncidentById(id: String): IncidentEntity? =
        incidents.value.find { it.id == id }
}
