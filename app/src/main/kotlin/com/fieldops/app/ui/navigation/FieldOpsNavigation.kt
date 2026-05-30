package com.fieldops.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fieldops.app.ui.asset.AssetScreen
import com.fieldops.app.ui.incident.IncidentScreen

@Composable
fun FieldOpsNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            FieldOpsBottomNavigation(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "incidents",
            modifier = modifier.padding(paddingValues)
        ) {
            composable("incidents") {
                IncidentScreen(
                    onNavigateToDetail = { id -> navController.navigate("incident/$id") }
                )
            }
            composable("incident/{incidentId}") { backStackEntry ->
                val incidentId = backStackEntry.arguments?.getString("incidentId") ?: ""
                // Detail screen placeholder — navigates back on tap
                IncidentScreen(onNavigateToDetail = { navController.popBackStack() })
            }
            composable("assets") {
                AssetScreen()
            }
        }
    }
}

@Composable
fun FieldOpsBottomNavigation(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == "incidents",
            onClick = { onNavigate("incidents") },
            icon = { Icon(Icons.Default.Warning, contentDescription = "Incidents") },
            label = { Text("Incidents") }
        )
        NavigationBarItem(
            selected = currentRoute == "assets",
            onClick = { onNavigate("assets") },
            icon = { Icon(Icons.Default.List, contentDescription = "Assets") },
            label = { Text("Assets") }
        )
    }
}
