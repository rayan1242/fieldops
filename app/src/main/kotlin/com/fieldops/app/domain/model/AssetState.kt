package com.fieldops.app.domain.model

sealed class AssetState {
    object Idle : AssetState()
    object Loading : AssetState()
    object Success : AssetState()
    data class Error(val message: String) : AssetState()
}
