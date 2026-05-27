package com.example.shopping_site_andrio.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopping_site_andrio.data.api.ApiResult
import com.example.shopping_site_andrio.data.model.ProductDto
import com.example.shopping_site_andrio.data.model.RecommendItemDto
import com.example.shopping_site_andrio.data.repository.CartRepository
import com.example.shopping_site_andrio.data.repository.ProductRepository
import com.example.shopping_site_andrio.data.repository.RecommendRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val searchQuery: String = "",
    val minPrice: String = "",
    val maxPrice: String = "",
    val sort: String = "created_at",
    val order: String = "desc",
    val showFilters: Boolean = false,
    val recommendations: List<RecommendItemDto> = emptyList(),
    val recommendationsLoading: Boolean = false,
    val addingToCartProductId: Int? = null,
    val addToCartMessage: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val recommendRepository: RecommendRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadRecommendations()
    }

    fun updateSearch(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun updateMinPrice(price: String) {
        _uiState.value = _uiState.value.copy(minPrice = price)
    }

    fun updateMaxPrice(price: String) {
        _uiState.value = _uiState.value.copy(maxPrice = price)
    }

    fun updateSort(sort: String) {
        _uiState.value = _uiState.value.copy(sort = sort)
    }

    fun updateOrder(order: String) {
        _uiState.value = _uiState.value.copy(order = order)
    }

    fun toggleFilters() {
        _uiState.value = _uiState.value.copy(showFilters = !_uiState.value.showFilters)
    }

    fun getProductFlow() = productRepository.getProducts(
        search = _uiState.value.searchQuery.ifBlank { null },
        minPrice = _uiState.value.minPrice.toDoubleOrNull(),
        maxPrice = _uiState.value.maxPrice.toDoubleOrNull(),
        sort = _uiState.value.sort,
        order = _uiState.value.order
    )

    fun addToCart(productId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(addingToCartProductId = productId)
            when (val result = cartRepository.addToCart(productId, 1)) {
                is ApiResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        addingToCartProductId = null,
                        addToCartMessage = "Added to cart"
                    )
                }
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        addingToCartProductId = null,
                        addToCartMessage = result.message
                    )
                }
            }
        }
    }

    fun clearAddToCartMessage() {
        _uiState.value = _uiState.value.copy(addToCartMessage = null)
    }

    private fun loadRecommendations() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(recommendationsLoading = true)
            when (val result = recommendRepository.getUserRecommendations(10)) {
                is ApiResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        recommendations = result.data,
                        recommendationsLoading = false
                    )
                }
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(recommendationsLoading = false)
                }
            }
        }
    }
}
