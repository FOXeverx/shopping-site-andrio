package com.example.shopping_site_andrio.data.repository

import com.example.shopping_site_andrio.data.api.ApiResult
import com.example.shopping_site_andrio.data.api.ApiService
import com.example.shopping_site_andrio.data.api.safeApiCall
import com.example.shopping_site_andrio.data.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getOrders(page: Int = 1, pageSize: Int = 20): ApiResult<List<OrderDto>> {
        return safeApiCall { apiService.getOrders(page, pageSize) }
    }

    suspend fun getOrderDetail(orderId: Int): ApiResult<OrderDto> {
        return safeApiCall { apiService.getOrderDetail(orderId) }
    }

    suspend fun createOrder(shippingAddress: String?, note: String?): ApiResult<OrderDto> {
        return safeApiCall { apiService.createOrder(CreateOrderRequest(shippingAddress, note)) }
    }

    suspend fun confirmOrder(orderId: Int, token: String): ApiResult<OrderDto> {
        return safeApiCall { apiService.confirmOrder(orderId, ConfirmOrderRequest(token)) }
    }
}
