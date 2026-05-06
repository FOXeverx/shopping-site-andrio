package com.example.shopping_site_andrio.data.model

data class RecommendItemDto(
    val product_id: Int,
    val product_name: String,
    val score: Double?,
    val image_url: String?,
    val reason: String?
)
