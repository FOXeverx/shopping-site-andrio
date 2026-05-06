package com.example.shopping_site_andrio.data.model

data class ProductDto(
    val id: Int,
    val name: String,
    val description: String?,
    val price: Double,
    val stock: Int,
    val category_id: Int?,
    val image_url: String?,
    val is_active: Boolean?,
    val created_at: String?,
    val updated_at: String?
)
