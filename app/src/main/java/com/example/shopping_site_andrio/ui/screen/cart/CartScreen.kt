package com.example.shopping_site_andrio.ui.screen.cart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.shopping_site_andrio.data.config.AppConfig
import com.example.shopping_site_andrio.data.model.CartItemDto
import com.example.shopping_site_andrio.ui.component.EmptyState
import com.example.shopping_site_andrio.ui.component.ErrorView
import com.example.shopping_site_andrio.ui.component.formatPrice
import com.example.shopping_site_andrio.ui.component.SkeletonLoading

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: CartViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showOrderDialog by remember { mutableStateOf(false) }
    var address by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(title = { Text("Cart") })
        }
    ) { padding ->
        when {
            uiState.cartItems.loading -> {
                SkeletonLoading(modifier = Modifier.padding(padding))
            }
            uiState.cartItems.error != null -> {
                ErrorView(
                    message = uiState.cartItems.error ?: "Error",
                    onRetry = { viewModel.loadCart() },
                    modifier = Modifier.padding(padding)
                )
            }
            uiState.cartItems.data.isNullOrEmpty() -> {
                EmptyState(
                    message = "Your cart is empty",
                    modifier = Modifier.padding(padding)
                )
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.cartItems.data ?: emptyList()) { item ->
                            CartItemRow(
                                item = item,
                                onQuantityChange = { qty ->
                                    viewModel.updateQuantity(item.id, qty)
                                },
                                onRemove = { viewModel.removeItem(item.id) }
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total: ${formatPrice(viewModel.totalPrice)}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Button(onClick = { showOrderDialog = true }) {
                            Text("Checkout")
                        }
                    }
                }
            }
        }
    }

    if (showOrderDialog) {
        AlertDialog(
            onDismissRequest = { showOrderDialog = false },
            title = { Text("Create Order") },
            text = {
                Column {
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Shipping Address") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text("Note (optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.createOrder(address, note)
                    showOrderDialog = false
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showOrderDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun CartItemRow(
    item: CartItemDto,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = AppConfig.resolveImageUrl(item.product?.image_url) ?: "https://via.placeholder.com/80",
                contentDescription = item.product?.name ?: "Product",
                modifier = Modifier.size(72.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.product?.name ?: "Product",
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatPrice(item.product?.price ?: 0.0),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(onClick = { onQuantityChange(item.quantity - 1) }) {
                    Icon(Icons.Filled.Remove, contentDescription = "Decrease quantity")
                }
                Text(
                    text = item.quantity.toString(),
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(onClick = { onQuantityChange(item.quantity + 1) }) {
                    Icon(Icons.Filled.Add, contentDescription = "Increase quantity")
                }
                IconButton(onClick = onRemove) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "Remove item",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
