package com.example.shopping_site_andrio.data.model

data class CartItemDto(
    val id: Int,
    val product: ProductDto?,
    val quantity: Int,
    val subtotal: Double? = null
)

data class AddCartRequest(
    val product_id: Int,
    val quantity: Int
)

data class UpdateCartRequest(
    val quantity: Int
)
