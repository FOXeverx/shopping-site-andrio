package com.example.shopping_site_andrio.ui.screen.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.shopping_site_andrio.data.model.CommentDto
import com.example.shopping_site_andrio.data.model.RecommendItemDto
import com.example.shopping_site_andrio.ui.component.ErrorView
import com.example.shopping_site_andrio.ui.component.formatPrice
import com.example.shopping_site_andrio.ui.component.SkeletonLoading

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: Int,
    onBack: () -> Unit,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(productId) {
        viewModel.loadProduct(productId)
    }

    DisposableEffect(productId) {
        onDispose {
            viewModel.stopBrowsing(productId)
        }
    }

    LaunchedEffect(uiState.addToCartMessage) {
        uiState.addToCartMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Product Details") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back") }
                }
            )
        }
    ) { padding ->
        when {
            uiState.product.loading -> {
                SkeletonLoading(modifier = Modifier.padding(padding))
            }
            uiState.product.error != null -> {
                ErrorView(
                    message = uiState.product.error ?: "Error",
                    onRetry = { viewModel.loadProduct(productId) },
                    modifier = Modifier.padding(padding)
                )
            }
            uiState.product.data != null -> {
                val product = uiState.product.data!!
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        AsyncImage(
                            model = product.image_url ?: "https://via.placeholder.com/400",
                            contentDescription = product.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                        )
                    }

                    item {
                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formatPrice(product.price),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Stock: ${product.stock}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    item {
                        product.description?.let { desc ->
                            Text(
                                text = desc,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(onClick = {
                                viewModel.updateQuantity(uiState.quantity - 1)
                            }) { Text("-") }
                            Text(
                                text = uiState.quantity.toString(),
                                style = MaterialTheme.typography.titleMedium
                            )
                            OutlinedButton(onClick = {
                                viewModel.updateQuantity(uiState.quantity + 1)
                            }) { Text("+") }
                            Spacer(modifier = Modifier.weight(1f))
                            Button(
                                onClick = { viewModel.addToCart(productId) },
                                enabled = !uiState.addingToCart && product.stock > 0
                            ) {
                                if (uiState.addingToCart) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                } else {
                                    Text("Add to Cart")
                                }
                            }
                        }
                    }

                    if (uiState.relatedRecommendations.isNotEmpty()) {
                        item {
                            Text(
                                text = "Related Products",
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                        item {
                            RecommendationRow(
                                items = uiState.relatedRecommendations,
                                modifier = Modifier.height(100.dp)
                            )
                        }
                    }

                    if (uiState.boughtAlsoRecommendations.isNotEmpty()) {
                        item {
                            Text(
                                text = "Customers Also Bought",
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                        item {
                            RecommendationRow(
                                items = uiState.boughtAlsoRecommendations,
                                modifier = Modifier.height(100.dp)
                            )
                        }
                    }

                    item {
                        HorizontalDivider()
                        Text(
                            text = "Comments",
                            style = MaterialTheme.typography.titleSmall
                        )
                    }

                    if (uiState.commentsLoading) {
                        item {
                            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                        }
                    } else if (uiState.comments.isEmpty()) {
                        item {
                            Text(
                                text = "No comments yet",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        items(uiState.comments) { comment ->
                            CommentItem(comment = comment)
                        }
                    }

                    item {
                        HorizontalDivider()
                        OutlinedTextField(
                            value = uiState.newCommentText,
                            onValueChange = viewModel::updateCommentText,
                            label = { Text("Add a comment") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.addComment(productId) },
                            enabled = !uiState.addingComment && uiState.newCommentText.isNotBlank()
                        ) {
                            if (uiState.addingComment) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Text("Submit")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecommendationRow(
    items: List<RecommendItemDto>,
    modifier: Modifier = Modifier
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        items(items) { item ->
            ElevatedCard(
                modifier = Modifier.width(160.dp)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
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
}

@Composable
fun CommentItem(comment: CommentDto) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = comment.username,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            comment.created_at?.let {
                Text(
                    text = it.take(10),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Text(
            text = comment.content,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
