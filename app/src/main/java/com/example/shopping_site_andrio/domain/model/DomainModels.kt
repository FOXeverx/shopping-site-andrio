package com.example.shopping_site_andrio.domain.model

data class Product(
    val id: Int,
    val name: String,
    val description: String?,
    val price: Double,
    val stock: Int,
    val categoryId: Int?,
    val imageUrl: String?,
    val isActive: Boolean?
)

data class User(
    val id: Int,
    val username: String,
    val email: String?,
    val role: String?
)

data class CartItem(
    val id: Int,
    val productId: Int,
    val productName: String?,
    val productPrice: Double?,
    val imageUrl: String?,
    val quantity: Int
)

data class Comment(
    val id: Int,
    val productId: Int,
    val userId: Int,
    val username: String,
    val content: String,
    val createdAt: String?
)

data class RecommendItem(
    val productId: Int,
    val productName: String,
    val score: Double?,
    val imageUrl: String?,
    val reason: String?
)

data class Order(
    val id: Int,
    val userId: Int,
    val status: String,
    val totalAmount: Double,
    val shippingAddress: String?,
    val note: String?,
    val confirmToken: String?,
    val confirmedAt: String?,
    val expiresAt: String?,
    val createdAt: String?,
    val items: List<OrderItem>?
)

data class OrderItem(
    val id: Int,
    val orderId: Int,
    val productId: Int,
    val productName: String?,
    val quantity: Int,
    val price: Double
)
