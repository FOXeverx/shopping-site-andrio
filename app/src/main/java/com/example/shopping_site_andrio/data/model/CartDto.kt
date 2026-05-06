package com.example.shopping_site_andrio.data.model

data class CartItemDto(
    val id: Int,
    val product_id: Int,
    val product_name: String?,
    val product_price: Double?,
    val image_url: String?,
    val quantity: Int
)

data class AddCartRequest(
    val product_id: Int,
    val quantity: Int
)

data class UpdateCartRequest(
    val quantity: Int
)
