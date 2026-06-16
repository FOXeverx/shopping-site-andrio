package com.example.shopping_site_andrio.ui.screen.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.example.shopping_site_andrio.data.config.AppConfig
import com.example.shopping_site_andrio.ui.component.formatPrice
import com.example.shopping_site_andrio.ui.component.SkeletonLoading

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductsScreen(
    onBack: () -> Unit,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val state by viewModel.productsState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val products = state.products?.collectAsLazyPagingItems()

    LaunchedEffect(Unit) { viewModel.loadProducts() }
    LaunchedEffect(state.message) {
        state.message?.let { snackbarHostState.showSnackbar(it); viewModel.clearProductsMessage() }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Product Management") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } },
                actions = {
                    IconButton(onClick = { viewModel.showCreateProductDialog() }) { Icon(Icons.Filled.Add, contentDescription = "Add product") }
                    IconButton(onClick = { viewModel.loadProducts() }) { Icon(Icons.Filled.Refresh, contentDescription = "Refresh") }
                }
            )
        }
    ) { padding ->
        if (products == null) {
            SkeletonLoading(modifier = Modifier.padding(padding))
        } else when {
            products.loadState.refresh is LoadState.Loading -> SkeletonLoading(modifier = Modifier.padding(padding))
            products.loadState.refresh is LoadState.Error -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { Text((products.loadState.refresh as LoadState.Error).error.message ?: "Error", color = MaterialTheme.colorScheme.error) }
            products.itemCount == 0 -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { Text("No products") }
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(products.itemCount) { index ->
                    products[index]?.let { product ->
                        ElevatedCard(Modifier.fillMaxWidth()) {
                            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                AsyncImage(
                                    model = AppConfig.resolveImageUrl(product.image_url) ?: "https://via.placeholder.com/60",
                                    contentDescription = product.name,
                                    modifier = Modifier.size(60.dp)
                                )
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(product.name, style = MaterialTheme.typography.titleSmall, maxLines = 1)
                                    Text(formatPrice(product.price), color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodyMedium)
                                    Text("Stock: ${product.stock}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                TextButton(onClick = { viewModel.showEditProductDialog(product) }) { Text("Edit") }
                                TextButton(onClick = { viewModel.deleteProduct(product.id) }, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) { Text("Del") }
                            }
                        }
                    }
                }
            }
        }
    }

    if (state.showCreateDialog) {
        ProductFormDialog(
            title = "Create Product",
            name = state.newName, onNameChange = viewModel::updateNewProductName,
            description = state.newDescription, onDescChange = viewModel::updateNewProductDesc,
            price = state.newPrice, onPriceChange = viewModel::updateNewProductPrice,
            stock = state.newStock, onStockChange = viewModel::updateNewProductStock,
            categoryId = state.newCategoryId, onCategoryChange = viewModel::updateNewProductCategory,
            imageUrl = state.newImageUrl, onImageChange = viewModel::updateNewProductImage,
            onConfirm = { viewModel.createProduct() },
            onDismiss = { viewModel.hideCreateProductDialog() }
        )
    }

    if (state.showEditDialog) {
        ProductFormDialog(
            title = "Edit Product",
            name = state.editName, onNameChange = viewModel::updateEditProductName,
            description = state.editDescription, onDescChange = viewModel::updateEditProductDesc,
            price = state.editPrice, onPriceChange = viewModel::updateEditProductPrice,
            stock = state.editStock, onStockChange = viewModel::updateEditProductStock,
            categoryId = state.editCategoryId, onCategoryChange = viewModel::updateEditProductCategory,
            imageUrl = state.editImageUrl, onImageChange = viewModel::updateEditProductImage,
            onConfirm = { viewModel.updateProduct() },
            onDismiss = { viewModel.hideEditProductDialog() }
        )
    }
}

@Composable
private fun ProductFormDialog(
    title: String,
    name: String, onNameChange: (String) -> Unit,
    description: String, onDescChange: (String) -> Unit,
    price: String, onPriceChange: (String) -> Unit,
    stock: String, onStockChange: (String) -> Unit,
    categoryId: String, onCategoryChange: (String) -> Unit,
    imageUrl: String, onImageChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = onNameChange, label = { Text("Name") }, singleLine = true)
                OutlinedTextField(value = description, onValueChange = onDescChange, label = { Text("Description") }, minLines = 2)
                OutlinedTextField(value = price, onValueChange = onPriceChange, label = { Text("Price") }, singleLine = true)
                OutlinedTextField(value = stock, onValueChange = onStockChange, label = { Text("Stock") }, singleLine = true)
                OutlinedTextField(value = categoryId, onValueChange = onCategoryChange, label = { Text("Category ID (optional)") }, singleLine = true)
                OutlinedTextField(value = imageUrl, onValueChange = onImageChange, label = { Text("Image URL (optional)") }, singleLine = true)
            }
        },
        confirmButton = { Button(onClick = onConfirm) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
