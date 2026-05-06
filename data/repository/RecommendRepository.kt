package com.example.shopping_site_andrio.data.repository

import com.example.shopping_site_andrio.data.api.ApiResult
import com.example.shopping_site_andrio.data.api.ApiService
import com.example.shopping_site_andrio.data.api.safeApiCall
import com.example.shopping_site_andrio.data.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecommendRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getProductRecommendations(productId: Int, limit: Int = 5): ApiResult<List<RecommendItemDto>> {
        return safeApiCall { apiService.getProductRecommendations(productId, limit) }
    }

    suspend fun getBoughtAlsoRecommendations(productId: Int, limit: Int = 5): ApiResult<List<RecommendItemDto>> {
        return safeApiCall { apiService.getBoughtAlsoRecommendations(productId, limit) }
    }

    suspend fun getUserRecommendations(limit: Int = 10): ApiResult<List<RecommendItemDto>> {
        return safeApiCall { apiService.getUserRecommendations(limit) }
    }
}
