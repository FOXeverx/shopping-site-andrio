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
import com.example.shopping_site_andrio.data.model.LoginLogEntry
import com.example.shopping_site_andrio.data.model.PurchaseSummary
import com.example.shopping_site_andrio.ui.component.formatPrice

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUserDetailScreen(
    userId: Int,
    onBack: () -> Unit,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val state by viewModel.userDetailState.collectAsState()

    LaunchedEffect(userId) { viewModel.loadUserDetail(userId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Detail") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Text("Browse History", style = MaterialTheme.typography.titleSmall) }
            when {
                state.browseLogs.loading -> item { CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 2.dp) }
                state.browseLogs.data.isNullOrEmpty() -> item { Text("No browse records", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                else -> items(state.browseLogs.data ?: emptyList(), key = { it.id }) { log -> BrowseLogCard(log) }
            }

            item { HorizontalDivider(); Spacer(Modifier.height(8.dp)); Text("Login History", style = MaterialTheme.typography.titleSmall) }
            when {
                state.loginLogs.loading -> item { CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 2.dp) }
                state.loginLogs.data.isNullOrEmpty() -> item { Text("No login records", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                else -> items(state.loginLogs.data ?: emptyList(), key = { it.id }) { log -> LoginLogCard(log) }
            }

            item { HorizontalDivider(); Spacer(Modifier.height(8.dp)); Text("Purchase Summary", style = MaterialTheme.typography.titleSmall) }
            when {
                state.purchaseSummary.loading -> item { CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 2.dp) }
                state.purchaseSummary.data.isNullOrEmpty() -> item { Text("No purchases", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                else -> items(state.purchaseSummary.data ?: emptyList(), key = { it.category_id ?: 0 }) { ps -> PurchaseSummaryCard(ps) }
            }
        }
    }
}

@Composable
private fun BrowseLogCard(log: BrowseLogEntry) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Text(log.product_name ?: "Product #${log.product_id}", style = MaterialTheme.typography.titleSmall)
            Text("${log.stay_time ?: 0}s ago", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun LoginLogCard(log: LoginLogEntry) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            val success = log.success == true
            Text(if (success) "Success" else "Failed", color = if (success) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
            Text(log.ip_address ?: "-", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            log.created_at?.let { Text(it.take(16).replace("T", " "), style = MaterialTheme.typography.labelSmall) }
        }
    }
}

@Composable
private fun PurchaseSummaryCard(ps: PurchaseSummary) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(ps.category_name ?: "Category #${ps.category_id}", style = MaterialTheme.typography.titleSmall)
            Text(formatPrice(ps.total_amount ?: 0.0), color = MaterialTheme.colorScheme.primary)
        }
    }
}
