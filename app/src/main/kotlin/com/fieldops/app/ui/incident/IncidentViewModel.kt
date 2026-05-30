package com.fieldops.app.ui.incident

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fieldops.app.data.repository.IncidentRepository
import com.fieldops.app.domain.model.Incident
import com.fieldops.app.domain.model.IncidentState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IncidentViewModel @Inject constructor(
    private val repository: IncidentRepository
) : ViewModel() {

    val incidents: StateFlow<List<Incident>> = repository.incidents
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _uiState = MutableStateFlow<IncidentState>(IncidentState.Idle)
    val uiState: StateFlow<IncidentState> = _uiState

    fun fileIncident(incident: Incident) {
        if (incident.location.isBlank()) {
            _uiState.value = IncidentState.Error("Location is required")
            return
        }
        if (incident.description.isBlank()) {
            _uiState.value = IncidentState.Error("Description is required")
            return
        }
        viewModelScope.launch {
            _uiState.value = IncidentState.Loading
            try {
                repository.fileIncident(incident)
                _uiState.value = IncidentState.Success
            } catch (e: Exception) {
                _uiState.value = IncidentState.Error(e.message ?: "Failed to file incident")
            }
        }
    }

    fun deleteIncident(incident: Incident) {
        viewModelScope.launch {
            try {
                repository.deleteIncident(incident)
            } catch (e: Exception) {
                _uiState.value = IncidentState.Error(e.message ?: "Failed to delete incident")
            }
        }
    }

    fun resetState() {
        _uiState.value = IncidentState.Idle
    }
}
