package com.example.shopping_site_andrio.ui.screen.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.shopping_site_andrio.data.model.AnomalyEntry
import com.example.shopping_site_andrio.ui.component.ErrorView
import com.example.shopping_site_andrio.ui.component.SkeletonLoading

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAnomaliesScreen(
    onBack: () -> Unit,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val state by viewModel.anomaliesState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) { viewModel.loadAnomalies() }
    LaunchedEffect(state.message) {
        state.message?.let { snackbarHostState.showSnackbar(it); viewModel.clearAnomaliesMessage() }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Anomalies") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } }
            )
        }
    ) { padding ->
        when {
            state.anomalies.loading -> SkeletonLoading(modifier = Modifier.padding(padding))
            state.anomalies.error != null -> ErrorView(message = state.anomalies.error ?: "Error", onRetry = { viewModel.loadAnomalies() }, modifier = Modifier.padding(padding))
            state.anomalies.data.isNullOrEmpty() -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { Text("No anomalies") }
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.anomalies.data ?: emptyList(), key = { it.id }) { anomaly ->
                    ElevatedCard(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(anomaly.anomaly_type ?: "?", style = MaterialTheme.typography.titleSmall)
                                Text(anomaly.severity ?: "", color = when (anomaly.severity) {
                                    "high" -> MaterialTheme.colorScheme.error; "medium" -> MaterialTheme.colorScheme.tertiary; else -> MaterialTheme.colorScheme.onSurfaceVariant
                                }, style = MaterialTheme.typography.labelSmall)
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(anomaly.description ?: "", style = MaterialTheme.typography.bodySmall)
                            if (anomaly.is_resolved != true) {
                                Spacer(Modifier.height(8.dp))
                                Button(onClick = { viewModel.resolveAnomaly(anomaly.id) }, modifier = Modifier.fillMaxWidth()) { Text("Resolve") }
                            } else {
                                Text("Resolved", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }
}
