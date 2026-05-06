package com.example.shopping_site_andrio.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopping_site_andrio.data.api.ApiResult
import com.example.shopping_site_andrio.data.model.ProductDto
import com.example.shopping_site_andrio.data.model.RecommendItemDto
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
    val recommendationsLoading: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val recommendRepository: RecommendRepository
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
