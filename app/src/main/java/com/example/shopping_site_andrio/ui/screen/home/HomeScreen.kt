package com.example.shopping_site_andrio.ui.screen.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.shopping_site_andrio.ui.component.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onProductClick: (Int) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val products = viewModel.getProductFlow().collectAsLazyPagingItems()
    var searchActive by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.addToCartMessage) {
        uiState.addToCartMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearAddToCartMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Shopping") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = viewModel::updateSearch,
                onSearch = {
                    searchActive = false
                    products.refresh()
                },
                active = searchActive,
                onActiveChange = { active ->
                    searchActive = active
                    if (!active && uiState.searchQuery.isBlank()) {
                        products.refresh()
                    }
                },
                leadingIcon = { },
                placeholder = { Text("Search products...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {}

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = uiState.sort == "price" && uiState.order == "asc",
                    onClick = {
                        if (uiState.sort == "price" && uiState.order == "asc") {
                            viewModel.updateSort("created_at")
                            viewModel.updateOrder("desc")
                        } else {
                            viewModel.updateSort("price")
                            viewModel.updateOrder("asc")
                        }
                    },
                    label = { Text("Price ↑") }
                )
                FilterChip(
                    selected = uiState.sort == "price" && uiState.order == "desc",
                    onClick = {
                        if (uiState.sort == "price" && uiState.order == "desc") {
                            viewModel.updateSort("created_at")
                            viewModel.updateOrder("desc")
                        } else {
                            viewModel.updateSort("price")
                            viewModel.updateOrder("desc")
                        }
                    },
                    label = { Text("Price ↓") }
                )
                FilterChip(
                    selected = uiState.showFilters,
                    onClick = { viewModel.toggleFilters() },
                    label = { Text("Filter") }
                )
            }

            if (uiState.showFilters) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.minPrice,
                        onValueChange = viewModel::updateMinPrice,
                        label = { Text("Min") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = uiState.maxPrice,
                        onValueChange = viewModel::updateMaxPrice,
                        label = { Text("Max") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (!uiState.recommendationsLoading && uiState.recommendations.isNotEmpty()) {
                Text(
                    text = "Recommended for You",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(120.dp)
                ) {
                    items(uiState.recommendations) { item ->
                        ElevatedCard(
                            onClick = { onProductClick(item.product_id) },
                            modifier = Modifier.width(140.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Text(
                                    text = item.product_name,
                                    style = MaterialTheme.typography.labelMedium,
                                    maxLines = 2
                                )
                                item.reason?.let { reason ->
                                    Text(
                                        text = reason,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            when {
                products.loadState.refresh is LoadState.Loading -> {
                    SkeletonLoading()
                }
                products.loadState.refresh is LoadState.Error -> {
                    ErrorView(
                        message = "Failed to load products",
                        onRetry = { products.refresh() }
                    )
                }
                products.itemCount == 0 -> {
                    EmptyState(message = "No products found")
                }
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(products.itemCount) { index ->
                            products[index]?.let { product ->
                                ProductCard(
                                    product = product,
                                    onClick = { onProductClick(product.id) },
                                    onAddToCart = { viewModel.addToCart(product.id) },
                                    isAddingToCart = uiState.addingToCartProductId == product.id
                                )
                            }
                        }
                        when (products.loadState.append) {
                            is LoadState.Loading -> {
                                item {
                                    Box(
                                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                    }
                                }
                            }
                            is LoadState.Error -> {
                                item {
                                    ErrorView(
                                        message = "Load more failed",
                                        onRetry = { products.retry() }
                                    )
                                }
                            }
                            else -> {}
                        }
                    }
                }
            }
        }
    }
}
