package com.example.shopping_site_andrio.data.model

data class ApiResponse<T>(
    val success: Boolean,
    val data: T?,
    val message: String?,
    val error: ApiError?,
    val pagination: Pagination?
)

data class ApiError(
    val code: String?,
    val message: String?
)

data class Pagination(
    val page: Int,
    val page_size: Int,
    val total: Int,
    val total_pages: Int?
)
