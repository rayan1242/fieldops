package com.fieldops.app.data.repository

import com.fieldops.app.data.local.dao.IncidentDao
import com.fieldops.app.data.local.entity.toDomain
import com.fieldops.app.data.local.entity.toEntity
import com.fieldops.app.data.remote.api.FieldOpsApiService
import com.fieldops.app.data.remote.dto.toDto
import com.fieldops.app.domain.model.Incident
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

open class IncidentRepository @Inject constructor(
    private val incidentDao: IncidentDao,
    private val apiService: FieldOpsApiService
) {
    open val incidents: Flow<List<Incident>> = incidentDao.getAllIncidents()
        .map { entities -> entities.map { it.toDomain() } }

    open suspend fun fileIncident(incident: Incident) {
        withContext(Dispatchers.IO) {
            incidentDao.insertIncident(incident.toEntity().copy(isSynced = false))
            try {
                apiService.createIncident(incident.toDto())
                incidentDao.markAsSynced(incident.id)
            } catch (e: IOException) {
                // Network failed — Room has it, WorkManager will sync later
            }
        }
    }

    open suspend fun getIncidentById(id: String): Incident? {
        return withContext(Dispatchers.IO) {
            incidentDao.getIncidentById(id)?.toDomain()
        }
    }

    open suspend fun deleteIncident(incident: Incident) {
        withContext(Dispatchers.IO) {
            incidentDao.deleteIncident(incident.toEntity())
            try {
                apiService.deleteIncident(incident.id)
            } catch (e: IOException) {
                // Network failed — deleted locally
            }
        }
    }

    open suspend fun syncOfflineIncidents() {
        withContext(Dispatchers.IO) {
            val unsynced = incidentDao.getUnsyncedIncidents()
            unsynced.forEach { entity ->
                try {
                    apiService.createIncident(entity.toDomain().toDto())
                    incidentDao.markAsSynced(entity.id)
                } catch (e: IOException) {
                    // Still no signal — retry next time
                }
            }
        }
    }
}
