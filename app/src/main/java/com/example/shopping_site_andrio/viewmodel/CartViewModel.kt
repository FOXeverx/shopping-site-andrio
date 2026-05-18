package com.example.shopping_site_andrio.ui.screen.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopping_site_andrio.data.api.ApiResult
import com.example.shopping_site_andrio.data.model.CartItemDto
import com.example.shopping_site_andrio.data.model.OrderDto
import com.example.shopping_site_andrio.data.repository.CartRepository
import com.example.shopping_site_andrio.data.repository.OrderRepository
import com.example.shopping_site_andrio.data.repository.ProductRepository
import com.example.shopping_site_andrio.domain.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CartUiState(
    val cartItems: UiState<List<CartItemDto>> = UiState.loading(),
    val orderResult: UiState<OrderDto> = UiState(),
    val message: String? = null
)

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        loadCart()
    }

    fun loadCart() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(cartItems = UiState.loading())
            when (val result = cartRepository.getCart()) {
                is ApiResult.Success -> {
                    val enrichedItems = result.data.map { item ->
                        async {
                            val productResult = productRepository.getProductDetail(item.product_id)
                            if (productResult is ApiResult.Success) {
                                val p = productResult.data
                                item.copy(
                                    product_name = p.name,
                                    product_price = p.price,
                                    image_url = p.image_url
                                )
                            } else {
                                item
                            }
                        }
                    }.awaitAll()
                    _uiState.value = _uiState.value.copy(cartItems = UiState.success(enrichedItems))
                }
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(cartItems = UiState.error(result.message))
                }
            }
        }
    }

    fun updateQuantity(cartItemId: Int, quantity: Int) {
        if (quantity < 1) return
        viewModelScope.launch {
            when (val result = cartRepository.updateCartItem(cartItemId, quantity)) {
                is ApiResult.Success -> loadCart()
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(message = result.message)
                }
            }
        }
    }

    fun removeItem(cartItemId: Int) {
        viewModelScope.launch {
            when (val result = cartRepository.removeCartItem(cartItemId)) {
                is ApiResult.Success -> loadCart()
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(message = result.message)
                }
            }
        }
    }

    fun createOrder(address: String?, note: String?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(orderResult = UiState.loading())
            when (val result = orderRepository.createOrder(address, note)) {
                is ApiResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        orderResult = UiState.success(result.data)
                    )
                    loadCart()
                }
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        orderResult = UiState.error(result.message)
                    )
                }
            }
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null, orderResult = UiState())
    }

    val totalPrice: Double
        get() {
            val items = _uiState.value.cartItems.data ?: return 0.0
            return items.sumOf { (it.product_price ?: 0.0) * it.quantity }
        }
}
