package com.example.shopping_site_andrio.data.model

data class OrderDto(
    val id: Int,
    val user_id: Int,
    val status: String,
    val total_amount: Double,
    val shipping_address: String?,
    val note: String?,
    val confirm_token: String?,
    val confirmed_at: String?,
    val expires_at: String?,
    val created_at: String?,
    val updated_at: String?,
    val items: List<OrderItemDto>?
)

data class OrderItemDto(
    val id: Int,
    val order_id: Int,
    val product_id: Int,
    val product_name: String?,
    val quantity: Int,
    val price: Double
)

data class CreateOrderRequest(
    val shipping_address: String? = null,
    val note: String? = null
)

data class ConfirmOrderRequest(
    val token: String
)
