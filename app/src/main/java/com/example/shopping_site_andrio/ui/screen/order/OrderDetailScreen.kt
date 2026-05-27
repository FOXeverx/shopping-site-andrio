package com.example.shopping_site_andrio.ui.screen.order

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
import com.example.shopping_site_andrio.ui.component.ErrorView
import com.example.shopping_site_andrio.ui.component.SkeletonLoading
import com.example.shopping_site_andrio.ui.component.formatPrice

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    orderId: Int,
    onBack: () -> Unit,
    viewModel: OrderDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(orderId) {
        viewModel.loadOrderDetail(orderId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order #$orderId") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        when {
            uiState.order.loading -> {
                SkeletonLoading(modifier = Modifier.padding(padding))
            }
            uiState.order.error != null -> {
                ErrorView(
                    message = uiState.order.error ?: "Error",
                    onRetry = { viewModel.loadOrderDetail(orderId) },
                    modifier = Modifier.padding(padding)
                )
            }
            uiState.order.data != null -> {
                val order = uiState.order.data!!
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "Status: ${order.status}",
                            style = MaterialTheme.typography.titleMedium,
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

                    item {
                        Text(
                            text = "Total: ${formatPrice(order.total_amount)}",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    order.shipping_address?.let { address ->
                        item {
                            Column {
                                Text("Shipping Address", style = MaterialTheme.typography.labelMedium)
                                Text(address, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }

                    order.note?.let { note ->
                        item {
                            Column {
                                Text("Note", style = MaterialTheme.typography.labelMedium)
                                Text(note, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }

                    item {
                        HorizontalDivider()
                        Text("Items", style = MaterialTheme.typography.titleSmall)
                    }

                    val items = order.items ?: emptyList()
                    if (items.isEmpty()) {
                        item {
                            Text(
                                "No items",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        items(items, key = { it.id }) { item ->
                            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.product_name ?: "Product",
                                            style = MaterialTheme.typography.titleSmall
                                        )
                                        Text(
                                            text = formatPrice(item.price),
                                            color = MaterialTheme.colorScheme.primary,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                    Text(
                                        text = "x${item.quantity}",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
