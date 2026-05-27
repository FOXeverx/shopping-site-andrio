package com.example.shopping_site_andrio.ui.screen.order

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.shopping_site_andrio.data.model.OrderDto
import com.example.shopping_site_andrio.ui.component.EmptyState
import com.example.shopping_site_andrio.ui.component.ErrorView
import com.example.shopping_site_andrio.ui.component.formatPrice
import com.example.shopping_site_andrio.ui.component.SkeletonLoading

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderListScreen(
    onOrderClick: (Int) -> Unit,
    viewModel: OrderViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Orders") },
                actions = {
                    IconButton(onClick = { viewModel.loadOrders() }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        when {
            uiState.orders.loading -> {
                SkeletonLoading(modifier = Modifier.padding(padding))
            }
            uiState.orders.error != null -> {
                ErrorView(
                    message = uiState.orders.error ?: "Error",
                    onRetry = { viewModel.loadOrders() },
                    modifier = Modifier.padding(padding)
                )
            }
            uiState.orders.data.isNullOrEmpty() -> {
                EmptyState(
                    message = "No orders yet",
                    modifier = Modifier.padding(padding)
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.orders.data ?: emptyList(), key = { it.id }) { order ->
                        OrderCard(order = order, onClick = { onOrderClick(order.id) })
                    }
                }
            }
        }
    }
}

@Composable
fun OrderCard(order: OrderDto, onClick: () -> Unit) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Order #${order.id}",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = order.status,
                    style = MaterialTheme.typography.labelMedium,
                    color = when (order.status) {
                        "CREATED" -> MaterialTheme.colorScheme.tertiary
                        "CONFIRMED" -> MaterialTheme.colorScheme.primary
                        "SHIPPED" -> MaterialTheme.colorScheme.secondary
                        "COMPLETED" -> MaterialTheme.colorScheme.primary
                        "CANCELLED" -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Total: ${formatPrice(order.total_amount)}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            order.created_at?.let {
                Text(
                    text = "Date: ${it.take(10)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
