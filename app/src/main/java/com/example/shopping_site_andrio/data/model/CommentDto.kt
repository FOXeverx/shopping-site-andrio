package com.example.shopping_site_andrio.data.model

data class CommentDto(
    val id: Int,
    val product_id: Int,
    val user_id: Int,
    val username: String,
    val content: String,
    val created_at: String?
)

data class AddCommentRequest(
    val content: String
)
