package com.example.shopping_site_andrio.ui.screen.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopping_site_andrio.data.api.ApiResult
import com.example.shopping_site_andrio.data.model.OrderDto
import com.example.shopping_site_andrio.data.repository.OrderRepository
import com.example.shopping_site_andrio.domain.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OrderListUiState(
    val orders: UiState<List<OrderDto>> = UiState.loading()
)

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrderListUiState())
    val uiState: StateFlow<OrderListUiState> = _uiState.asStateFlow()

    init {
        loadOrders()
    }

    fun loadOrders() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(orders = UiState.loading())
            when (val result = orderRepository.getOrders()) {
                is ApiResult.Success -> {
                    _uiState.value = _uiState.value.copy(orders = UiState.success(result.data))
                }
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(orders = UiState.error(result.message))
                }
            }
        }
    }

    fun confirmOrder(orderId: Int, token: String) {
        viewModelScope.launch {
            when (val result = orderRepository.confirmOrder(orderId, token)) {
                is ApiResult.Success -> loadOrders()
                is ApiResult.Error -> {}
            }
        }
    }
}
