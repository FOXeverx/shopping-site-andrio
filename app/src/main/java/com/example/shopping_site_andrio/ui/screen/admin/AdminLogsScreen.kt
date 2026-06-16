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
import com.example.shopping_site_andrio.data.model.BrowseLogEntry
import com.example.shopping_site_andrio.data.model.OperationLogEntry
import com.example.shopping_site_andrio.ui.component.SkeletonLoading

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminLogsScreen(
    onBack: () -> Unit,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val state by viewModel.logsState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) { viewModel.loadLogs() }
    LaunchedEffect(state.message) {
        state.message?.let { snackbarHostState.showSnackbar(it); viewModel.clearLogsMessage() }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Audit Logs") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = state.selectedTab) {
                Tab(selected = state.selectedTab == 0, onClick = { viewModel.setLogsTab(0) }, text = { Text("Operation") })
                Tab(selected = state.selectedTab == 1, onClick = { viewModel.setLogsTab(1); viewModel.loadBrowseLogs() }, text = { Text("Browse") })
            }
            when (state.selectedTab) {
                0 -> LogListSection(state.operationLogs) { it.operationItem() }
                1 -> {
                    Row(Modifier.padding(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(state.filterProductId, viewModel::updateLogFilterProductId, label = { Text("Product ID") }, modifier = Modifier.width(100.dp), singleLine = true)
                        OutlinedTextField(state.filterUserId, viewModel::updateLogFilterUserId, label = { Text("User ID") }, modifier = Modifier.width(100.dp), singleLine = true)
                        FilledTonalButton(onClick = { viewModel.loadBrowseLogs() }) { Text("Filter") }
                        FilledTonalButton(onClick = { viewModel.triggerRecommend() }) { Text("Trigger Recommend") }
                    }
                    LogListSection(state.browseLogs) { it.browseItem() }
                }
            }
        }
    }
}

@Composable
private fun OperationLogEntry.operationItem() {
    ElevatedCard(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
        Column(Modifier.padding(12.dp)) {
            Text("$action → $target_type #$target_id", style = MaterialTheme.typography.titleSmall)
            Text("by $username", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun BrowseLogEntry.browseItem() {
    ElevatedCard(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
        Column(Modifier.padding(12.dp)) {
            Text(product_name ?: "Product #$product_id", style = MaterialTheme.typography.titleSmall)
            Text("User #$user_id ($username) • ${stay_time}s", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun <T> LogListSection(uiState: com.example.shopping_site_andrio.domain.model.UiState<List<T>>, content: @Composable (T) -> Unit) {
    when {
        uiState.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        uiState.error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(uiState.error ?: "Error", color = MaterialTheme.colorScheme.error) }
        uiState.data.isNullOrEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No records") }
        else -> LazyColumn { items(uiState.data ?: emptyList()) { content(it) } }
    }
}
