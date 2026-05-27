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

data class OrderDetailUiState(
    val order: UiState<OrderDto> = UiState.loading()
)

@HiltViewModel
class OrderDetailViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrderDetailUiState())
    val uiState: StateFlow<OrderDetailUiState> = _uiState.asStateFlow()

    fun loadOrderDetail(orderId: Int) {
        viewModelScope.launch {
            when (val result = orderRepository.getOrderDetail(orderId)) {
                is ApiResult.Success -> {
                    _uiState.value = _uiState.value.copy(order = UiState.success(result.data))
                }
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(order = UiState.error(result.message))
                }
            }
        }
    }
}
