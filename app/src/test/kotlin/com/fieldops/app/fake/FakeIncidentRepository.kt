package com.fieldops.app.fake

import com.fieldops.app.data.repository.IncidentRepository
import com.fieldops.app.domain.model.Incident
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.IOException

class FakeIncidentRepository : IncidentRepository(
    incidentDao = FakeIncidentDao(),
    apiService = FakeFieldOpsApiService()
) {
    var shouldThrowError = false
    val filedIncidents = mutableListOf<Incident>()
    val deletedIncidents = mutableListOf<Incident>()

    private val _incidents = MutableStateFlow<List<Incident>>(emptyList())
    override val incidents: Flow<List<Incident>> = _incidents

    override suspend fun fileIncident(incident: Incident) {
        if (shouldThrowError) throw IOException("Network failed")
        filedIncidents.add(incident)
        _incidents.value = _incidents.value + incident
    }

    override suspend fun deleteIncident(incident: Incident) {
        if (shouldThrowError) throw IOException("Network failed")
        deletedIncidents.add(incident)
        _incidents.value = _incidents.value - incident
    }

    override suspend fun syncOfflineIncidents() {
        if (shouldThrowError) throw IOException("Network failed")
    }
}
