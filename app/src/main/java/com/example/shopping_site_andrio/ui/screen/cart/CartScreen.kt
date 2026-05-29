package com.example.shopping_site_andrio.ui.screen.cart

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.shopping_site_andrio.data.config.AppConfig
import com.example.shopping_site_andrio.data.model.CartItemDto
import com.example.shopping_site_andrio.ui.component.EmptyState
import com.example.shopping_site_andrio.ui.component.ErrorView
import com.example.shopping_site_andrio.ui.component.formatPrice
import com.example.shopping_site_andrio.ui.component.SkeletonLoading

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CartScreen(
    viewModel: CartViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showOrderDialog by remember { mutableStateOf(false) }
    var address by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var orderSubmitting by remember { mutableStateOf(false) }
    var deleteConfirmId by remember { mutableIntStateOf(-1) }

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    LaunchedEffect(uiState.orderResult.data) {
        if (uiState.orderResult.data != null) {
            orderSubmitting = false
            showOrderDialog = false
            snackbarHostState.showSnackbar("Order created successfully!")
            viewModel.clearMessage()
        }
    }

    LaunchedEffect(uiState.orderResult.error) {
        if (uiState.orderResult.error != null) {
            orderSubmitting = false
            snackbarHostState.showSnackbar(uiState.orderResult.error ?: "Order failed")
            viewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Cart") },
                actions = {
                    IconButton(onClick = { viewModel.loadCart() }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                    }
                }
            )
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
                        items(uiState.cartItems.data ?: emptyList(), key = { it.id }) { item ->
                            CartItemRow(
                                item = item,
                                onQuantityChange = { qty ->
                                    viewModel.updateQuantity(item.id, qty)
                                },
                                onRemove = { deleteConfirmId = item.id },
                                modifier = Modifier.animateItemPlacement()
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

    if (deleteConfirmId > 0) {
        AlertDialog(
            onDismissRequest = { deleteConfirmId = -1 },
            title = { Text("Remove Item") },
            text = { Text("Are you sure you want to remove this item?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.removeItem(deleteConfirmId)
                    deleteConfirmId = -1
                }) {
                    Text("Remove", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteConfirmId = -1 }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showOrderDialog) {
        AlertDialog(
            onDismissRequest = { if (!orderSubmitting) showOrderDialog = false },
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
                Button(
                    onClick = {
                        orderSubmitting = true
                        viewModel.createOrder(address, note)
                    },
                    enabled = !orderSubmitting
                ) {
                    if (orderSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showOrderDialog = false },
                    enabled = !orderSubmitting
                ) {
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
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    ElevatedCard(modifier = modifier.fillMaxWidth()) {
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
                IconButton(onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onRemove()
                }) {
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
