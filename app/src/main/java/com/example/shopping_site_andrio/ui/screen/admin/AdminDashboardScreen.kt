package com.example.shopping_site_andrio.ui.screen.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.shopping_site_andrio.ui.component.formatPrice

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onBack: () -> Unit,
    onNavigateToUsers: () -> Unit,
    onNavigateToLogs: () -> Unit,
    onNavigateToAnomalies: () -> Unit,
    onNavigateToSecurity: () -> Unit,
    onNavigateToProducts: () -> Unit,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val state by viewModel.dashboardState.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadDashboard() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Text("Overview", style = MaterialTheme.typography.titleLarge) }

            item { StatCard("Users", state.userStats) { it.total_users?.toString() ?: "-" } }
            item { StatCard("Anomalies", state.anomalyStats) { "${it.unresolved ?: 0} unresolved / ${it.total ?: 0} total" } }
            item { StatCard("Threats", state.threatStats) { "${it.unresolved ?: 0} unresolved, ${it.today ?: 0} today" } }
            item { StatCard("Sales Forecast", state.salesPredict) { "${it.trend ?: "-"} — avg ${formatPrice(it.current_avg ?: 0.0)}" } }

            item { Spacer(Modifier.height(4.dp)); Text("Management", style = MaterialTheme.typography.titleSmall) }
            item { NavCard("User Management", "Create, edit, delete users") { onNavigateToUsers() } }
            item { NavCard("Audit Logs", "Operation & browse logs") { onNavigateToLogs() } }
            item { NavCard("Anomalies", "Review & resolve anomalies") { onNavigateToAnomalies() } }
            item { NavCard("Security", "Threats & IP blocking") { onNavigateToSecurity() } }
            item { NavCard("Product Management", "Create, edit, delete products") { onNavigateToProducts() } }
        }
    }
}

@Composable
private fun <T> StatCard(title: String, uiState: com.example.shopping_site_andrio.domain.model.UiState<T>, value: (T) -> String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            when {
                uiState.loading -> CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                uiState.data != null -> Text(value(uiState.data), style = MaterialTheme.typography.titleMedium)
                uiState.error != null -> Text(uiState.error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun NavCard(title: String, subtitle: String, onClick: () -> Unit) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
