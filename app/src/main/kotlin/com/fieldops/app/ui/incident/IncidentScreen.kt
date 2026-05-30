package com.fieldops.app.ui.incident

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fieldops.app.domain.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidentScreen(
    viewModel: IncidentViewModel = hiltViewModel(),
    onNavigateToDetail: (String) -> Unit
) {
    val incidents by viewModel.incidents.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    var type by rememberSaveable { mutableStateOf(IncidentType.OTHER) }
    var location by rememberSaveable { mutableStateOf("") }
    var severity by rememberSaveable { mutableStateOf(Severity.LOW) }
    var description by rememberSaveable { mutableStateOf("") }
    var showForm by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is IncidentState.Success) {
            showForm = false
            location = ""
            description = ""
            viewModel.resetState()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Incident Reports") },
            actions = {
                IconButton(onClick = { showForm = !showForm }) {
                    Icon(
                        imageVector = if (showForm) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = "Toggle form"
                    )
                }
            }
        )

        if (showForm) {
            IncidentForm(
                type = type,
                location = location,
                severity = severity,
                description = description,
                uiState = uiState,
                onTypeChange = { type = it },
                onLocationChange = { location = it },
                onSeverityChange = { severity = it },
                onDescriptionChange = { description = it },
                onSubmit = {
                    viewModel.fileIncident(
                        Incident(
                            type = type,
                            location = location,
                            severity = severity,
                            description = description,
                            reportedBy = "CurrentOfficer"
                        )
                    )
                }
            )
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(items = incidents, key = { it.id }) { incident ->
                IncidentCard(
                    incident = incident,
                    onDelete = { viewModel.deleteIncident(incident) },
                    onClick = { onNavigateToDetail(incident.id) }
                )
            }

            if (incidents.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "No incidents reported", color = Color.Gray)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidentForm(
    type: IncidentType,
    location: String,
    severity: Severity,
    description: String,
    uiState: IncidentState,
    onTypeChange: (IncidentType) -> Unit,
    onLocationChange: (String) -> Unit,
    onSeverityChange: (Severity) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "File New Incident",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text("Type", style = MaterialTheme.typography.labelMedium)
            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                IncidentType.values().forEach { incidentType ->
                    FilterChip(
                        selected = type == incidentType,
                        onClick = { onTypeChange(incidentType) },
                        label = { Text(incidentType.name) },
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = location,
                onValueChange = onLocationChange,
                label = { Text("Location *") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState is IncidentState.Error &&
                    (uiState as IncidentState.Error).message.contains("Location")
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text("Severity", style = MaterialTheme.typography.labelMedium)
            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                Severity.values().forEach { sev ->
                    FilterChip(
                        selected = severity == sev,
                        onClick = { onSeverityChange(sev) },
                        label = { Text(sev.name) },
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChange,
                label = { Text("Description *") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth(),
                isError = uiState is IncidentState.Error &&
                    (uiState as IncidentState.Error).message.contains("Description")
            )

            if (uiState is IncidentState.Error) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = (uiState as IncidentState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState !is IncidentState.Loading
            ) {
                if (uiState is IncidentState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                } else {
                    Text("File Incident")
                }
            }
        }
    }
}

@Composable
fun IncidentCard(
    incident: Incident,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = incident.type.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = incident.severity.name,
                        color = when (incident.severity) {
                            Severity.CRITICAL -> Color.Red
                            Severity.HIGH -> Color(0xFFFF6600)
                            Severity.MEDIUM -> Color(0xFFFFAA00)
                            Severity.LOW -> Color.Green
                        },
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = Color.Gray
                )
                Text(
                    text = incident.location,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Text(
                text = incident.description,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (!incident.isSynced) {
                Text(
                    text = "Pending sync",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
