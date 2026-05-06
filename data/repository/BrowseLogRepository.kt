package com.example.shopping_site_andrio.data.repository

import com.example.shopping_site_andrio.data.api.ApiResult
import com.example.shopping_site_andrio.data.api.ApiService
import com.example.shopping_site_andrio.data.api.safeApiCall
import com.example.shopping_site_andrio.data.model.BrowseLogRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BrowseLogRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun logBrowse(productId: Int, stayTime: Int = 0): ApiResult<Nothing> {
        return safeApiCall { apiService.logBrowse(BrowseLogRequest(productId, stayTime)) }
    }
}
