package com.example.shopping_site_andrio.data.repository

import com.example.shopping_site_andrio.data.api.ApiResult
import com.example.shopping_site_andrio.data.api.ApiService
import com.example.shopping_site_andrio.data.api.safeApiCall
import com.example.shopping_site_andrio.data.config.AppConfig
import com.example.shopping_site_andrio.data.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getComments(productId: Int, page: Int = 1, pageSize: Int = AppConfig.DEFAULT_PAGE_SIZE): ApiResult<List<CommentDto>> {
        return safeApiCall { apiService.getProductComments(productId, page, pageSize) }
    }

    suspend fun addComment(productId: Int, content: String): ApiResult<CommentDto> {
        return safeApiCall { apiService.addComment(productId, AddCommentRequest(content)) }
    }
}
