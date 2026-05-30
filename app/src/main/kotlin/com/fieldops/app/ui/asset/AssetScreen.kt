package com.fieldops.app.ui.asset

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fieldops.app.domain.model.Asset
import com.fieldops.app.domain.model.AssetState
import com.fieldops.app.domain.model.AssetStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetScreen(viewModel: AssetViewModel = hiltViewModel()) {
    val filteredAssets by viewModel.filteredAssets.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Asset Tracking") },
            actions = {
                IconButton(onClick = { viewModel.syncAssets() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Sync")
                }
            }
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = viewModel::onSearchQueryChange,
            label = { Text("Search by name, location or ID") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )

        if (uiState is AssetState.Loading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        if (uiState is AssetState.Error) {
            Text(
                text = (uiState as AssetState.Error).message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(8.dp)
            )
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(items = filteredAssets, key = { it.id }) { asset ->
                AssetCard(
                    asset = asset,
                    onStatusChange = { newStatus ->
                        viewModel.updateAssetStatus(asset.id, newStatus)
                    }
                )
            }

            if (filteredAssets.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (searchQuery.isEmpty()) "No assets assigned"
                                   else "No assets found for '$searchQuery'",
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AssetCard(
    asset: Asset,
    onStatusChange: (AssetStatus) -> Unit
) {
    var showStatusMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = asset.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = asset.location,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Box {
                    Surface(
                        color = when (asset.status) {
                            AssetStatus.ACTIVE -> Color.Green
                            AssetStatus.IN_USE -> Color(0xFFFF6600)
                            AssetStatus.NEEDS_RESTOCK -> Color(0xFFFFAA00)
                            AssetStatus.FAULTY -> Color.Red
                        },
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.clickable { showStatusMenu = true }
                    ) {
                        Text(
                            text = asset.status.name,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    DropdownMenu(
                        expanded = showStatusMenu,
                        onDismissRequest = { showStatusMenu = false }
                    ) {
                        AssetStatus.values().forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status.name) },
                                onClick = {
                                    onStatusChange(status)
                                    showStatusMenu = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
