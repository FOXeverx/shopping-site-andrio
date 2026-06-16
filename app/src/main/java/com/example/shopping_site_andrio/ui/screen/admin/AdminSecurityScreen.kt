package com.example.shopping_site_andrio.ui.screen.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.shopping_site_andrio.data.model.IpBlockEntry
import com.example.shopping_site_andrio.data.model.SecurityThreat
import com.example.shopping_site_andrio.ui.component.ErrorView
import com.example.shopping_site_andrio.ui.component.SkeletonLoading

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSecurityScreen(
    onBack: () -> Unit,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val state by viewModel.securityState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) { viewModel.loadThreats(); viewModel.loadIpBlocks() }
    LaunchedEffect(state.message) {
        state.message?.let { snackbarHostState.showSnackbar(it); viewModel.clearSecurityMessage() }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Security") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = state.selectedTab) {
                Tab(selected = state.selectedTab == 0, onClick = { viewModel.setSecurityTab(0) }, text = { Text("Threats") })
                Tab(selected = state.selectedTab == 1, onClick = { viewModel.setSecurityTab(1) }, text = { Text("IP Blocks") })
            }
            when (state.selectedTab) {
                0 -> ThreatsTab(state, viewModel)
                1 -> IpBlocksTab(state, viewModel)
            }
        }
    }

    if (state.showBlockDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideBlockDialog() },
            title = { Text("Block IP") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = state.blockIp, onValueChange = viewModel::updateBlockIp, label = { Text("IP Address") }, singleLine = true)
                    OutlinedTextField(value = state.blockReason, onValueChange = viewModel::updateBlockReason, label = { Text("Reason") }, singleLine = true)
                    OutlinedTextField(value = state.blockExpiresMinutes, onValueChange = viewModel::updateBlockExpiresMinutes, label = { Text("Expires (min, empty=permanent)") }, singleLine = true)
                }
            },
            confirmButton = { Button(onClick = { viewModel.blockIp() }) { Text("Block") } },
            dismissButton = { TextButton(onClick = { viewModel.hideBlockDialog() }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun ThreatsTab(state: AdminSecurityUiState, viewModel: AdminViewModel) {
    when {
        state.threats.loading -> SkeletonLoading()
        state.threats.error != null -> ErrorView(message = state.threats.error ?: "Error", onRetry = { viewModel.loadThreats() })
        state.threats.data.isNullOrEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No threats") }
        else -> LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.threats.data ?: emptyList(), key = { it.id }) { threat ->
                ElevatedCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(threat.threat_type ?: "?", style = MaterialTheme.typography.titleSmall)
                            Text(threat.severity ?: "", color = when (threat.severity) { "high" -> MaterialTheme.colorScheme.error; "medium" -> MaterialTheme.colorScheme.tertiary; else -> MaterialTheme.colorScheme.onSurfaceVariant }, style = MaterialTheme.typography.labelSmall)
                        }
                        Text("IP: ${threat.ip_address ?: "-"}", style = MaterialTheme.typography.bodySmall)
                        if (threat.is_resolved != true) {
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = { viewModel.resolveThreat(threat.id) }, modifier = Modifier.fillMaxWidth()) { Text("Resolve") }
                        } else { Text("Resolved", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelSmall) }
                    }
                }
            }
        }
    }
}

@Composable
private fun IpBlocksTab(state: AdminSecurityUiState, viewModel: AdminViewModel) {
    when {
        state.ipBlocks.loading -> SkeletonLoading()
        state.ipBlocks.error != null -> ErrorView(message = state.ipBlocks.error ?: "Error", onRetry = { viewModel.loadIpBlocks() })
        state.ipBlocks.data.isNullOrEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No IP blocks") }
        else -> {
            Box(Modifier.fillMaxSize()) {
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.ipBlocks.data ?: emptyList(), key = { it.id }) { block ->
                        ElevatedCard(Modifier.fillMaxWidth()) {
                            Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text(block.ip_address ?: "-", style = MaterialTheme.typography.titleSmall)
                                    Text(block.reason ?: "", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                TextButton(onClick = { viewModel.unblockIp(block.id) }, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) { Text("Unblock") }
                            }
                        }
                    }
                }
                FloatingActionButton(
                    onClick = { viewModel.showBlockDialog() },
                    modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Block IP")
                }
            }
        }
    }
}
