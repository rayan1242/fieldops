package com.fieldops.app.ui.asset

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fieldops.app.data.repository.AssetRepository
import com.fieldops.app.domain.model.Asset
import com.fieldops.app.domain.model.AssetState
import com.fieldops.app.domain.model.AssetStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssetViewModel @Inject constructor(
    private val repository: AssetRepository
) : ViewModel() {

    val assets: StateFlow<List<Asset>> = repository.assets
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val filteredAssets: StateFlow<List<Asset>> = combine(
        repository.assets,
        _searchQuery
    ) { assets, query ->
        if (query.isEmpty()) {
            assets
        } else {
            assets.filter { asset ->
                asset.name.contains(query, ignoreCase = true) ||
                    asset.location.contains(query, ignoreCase = true) ||
                    asset.id.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    private val _uiState = MutableStateFlow<AssetState>(AssetState.Idle)
    val uiState: StateFlow<AssetState> = _uiState

    init {
        syncAssets()
    }

    fun syncAssets() {
        viewModelScope.launch {
            _uiState.value = AssetState.Loading
            try {
                repository.syncAssets()
                _uiState.value = AssetState.Success
            } catch (e: Exception) {
                _uiState.value = AssetState.Error(e.message ?: "Failed to sync assets")
            }
        }
    }

    fun updateAssetStatus(id: String, status: AssetStatus) {
        viewModelScope.launch {
            try {
                repository.updateAssetStatus(id, status)
            } catch (e: Exception) {
                _uiState.value = AssetState.Error(e.message ?: "Failed to update status")
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun resetState() {
        _uiState.value = AssetState.Idle
    }
}
