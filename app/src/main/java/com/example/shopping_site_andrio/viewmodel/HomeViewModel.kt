package com.example.shopping_site_andrio.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.shopping_site_andrio.data.api.ApiResult
import com.example.shopping_site_andrio.data.model.ProductDto
import com.example.shopping_site_andrio.data.model.RecommendItemDto
import com.example.shopping_site_andrio.data.repository.CartRepository
import com.example.shopping_site_andrio.data.repository.ProductRepository
import com.example.shopping_site_andrio.data.repository.RecommendRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
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
    val snackbarEvent: SnackbarEvent? = null
)

data class SnackbarEvent(val id: Long, val message: String)

data class FilterParams(
    val search: String?,
    val minPrice: Double?,
    val maxPrice: Double?,
    val sort: String,
    val order: String
)

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val recommendRepository: RecommendRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _filterParams = MutableStateFlow(
        FilterParams(null, null, null, "created_at", "desc")
    )

    private var snackbarCounter = 0L

    val productFlow: Flow<PagingData<ProductDto>> = _filterParams.flatMapLatest { params ->
        productRepository.getProducts(
            search = params.search,
            minPrice = params.minPrice,
            maxPrice = params.maxPrice,
            sort = params.sort,
            order = params.order
        )
    }.cachedIn(viewModelScope)

    init {
        loadRecommendations()
    }

    private fun applyFilters() {
        val s = _uiState.value
        _filterParams.value = FilterParams(
            search = s.searchQuery.ifBlank { null },
            minPrice = s.minPrice.toDoubleOrNull(),
            maxPrice = s.maxPrice.toDoubleOrNull(),
            sort = s.sort,
            order = s.order
        )
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
        applyFilters()
    }

    fun updateOrder(order: String) {
        _uiState.value = _uiState.value.copy(order = order)
        applyFilters()
    }

    fun toggleFilters() {
        _uiState.value = _uiState.value.copy(showFilters = !_uiState.value.showFilters)
    }

    fun applySearch() {
        applyFilters()
    }

    fun addToCart(productId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(addingToCartProductId = productId)
            val message: String
            when (val result = cartRepository.addToCart(productId, 1)) {
                is ApiResult.Success -> {
                    message = "Added to cart"
                }
                is ApiResult.Error -> {
                    message = result.message
                }
            }
            snackbarCounter++
            _uiState.value = _uiState.value.copy(
                addingToCartProductId = null,
                snackbarEvent = SnackbarEvent(snackbarCounter, message)
            )
        }
    }

    fun clearSnackbarMessage() {
        _uiState.value = _uiState.value.copy(snackbarEvent = null)
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
