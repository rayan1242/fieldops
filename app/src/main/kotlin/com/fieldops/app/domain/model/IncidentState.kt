package com.fieldops.app.domain.model

sealed class IncidentState {
    object Idle : IncidentState()
    object Loading : IncidentState()
    object Success : IncidentState()
    data class Error(val message: String) : IncidentState()
}
