package com.example.shopping_site_andrio.data.repository

import com.example.shopping_site_andrio.data.api.ApiResult
import com.example.shopping_site_andrio.data.api.ApiService
import com.example.shopping_site_andrio.data.api.safeApiCall
import com.example.shopping_site_andrio.data.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getAdminUsers(role: String? = null, page: Int = 1, pageSize: Int = 20): ApiResult<List<AdminUserListItem>> {
        return safeApiCall { apiService.getAdminUsers(role, page, pageSize) }
    }

    suspend fun createUser(request: CreateAdminUserRequest): ApiResult<UserDto> {
        return safeApiCall { apiService.createAdminUser(request) }
    }

    suspend fun updateUser(userId: Int, request: UpdateAdminUserRequest): ApiResult<UserDto> {
        return safeApiCall { apiService.updateAdminUser(userId, request) }
    }

    suspend fun deleteUser(userId: Int): ApiResult<Nothing> {
        return safeApiCall { apiService.deleteAdminUser(userId) }
    }

    suspend fun getUserBrowse(userId: Int, page: Int = 1, pageSize: Int = 20): ApiResult<List<BrowseLogEntry>> {
        return safeApiCall { apiService.getAdminUserBrowse(userId, page, pageSize) }
    }

    suspend fun getUserLogins(userId: Int, page: Int = 1, pageSize: Int = 20): ApiResult<List<LoginLogEntry>> {
        return safeApiCall { apiService.getAdminUserLogins(userId, page, pageSize) }
    }

    suspend fun getUserPurchasesSummary(userId: Int): ApiResult<List<PurchaseSummary>> {
        return safeApiCall { apiService.getAdminUserPurchasesSummary(userId) }
    }

    suspend fun getUserPurchasesDetail(userId: Int, categoryId: Int, page: Int = 1, pageSize: Int = 20): ApiResult<List<PurchaseDetail>> {
        return safeApiCall { apiService.getAdminUserPurchasesDetail(userId, categoryId, page, pageSize) }
    }

    suspend fun getOperationLogs(page: Int = 1, pageSize: Int = 20): ApiResult<List<OperationLogEntry>> {
        return safeApiCall { apiService.getOperationLogs(page, pageSize) }
    }

    suspend fun getBrowseLogs(productId: Int? = null, userId: Int? = null, page: Int = 1, pageSize: Int = 20): ApiResult<List<BrowseLogEntry>> {
        return safeApiCall { apiService.getAdminBrowseLogs(productId, userId, page, pageSize) }
    }

    suspend fun getAnomalies(page: Int = 1, pageSize: Int = 20): ApiResult<List<AnomalyEntry>> {
        return safeApiCall { apiService.getAnomalies(page, pageSize) }
    }

    suspend fun resolveAnomaly(id: Int): ApiResult<Nothing> {
        return safeApiCall { apiService.resolveAnomaly(id) }
    }

    suspend fun triggerRecommendation(): ApiResult<RecommendTriggerResult> {
        return safeApiCall { apiService.triggerRecommendation() }
    }

    suspend fun getUserStats(): ApiResult<UserStats> {
        return safeApiCall { apiService.getAdminUserStats() }
    }

    suspend fun getAnomalyStats(): ApiResult<AnomalyStats> {
        return safeApiCall { apiService.getAdminAnomalyStats() }
    }

    suspend fun getSalesPredict(days: Int = 7): ApiResult<SalesPredict> {
        return safeApiCall { apiService.getSalesPredict(days) }
    }

    suspend fun getSecurityThreats(
        threatType: String? = null, severity: String? = null,
        isResolved: Boolean? = null, page: Int = 1, pageSize: Int = 20
    ): ApiResult<List<SecurityThreat>> {
        return safeApiCall { apiService.getSecurityThreats(threatType, severity, isResolved, page, pageSize) }
    }

    suspend fun resolveSecurityThreat(threatId: Int): ApiResult<Nothing> {
        return safeApiCall { apiService.resolveSecurityThreat(threatId) }
    }

    suspend fun getSecurityThreatStats(): ApiResult<ThreatStats> {
        return safeApiCall { apiService.getSecurityThreatStats() }
    }

    suspend fun getIpBlocks(page: Int = 1, pageSize: Int = 20): ApiResult<List<IpBlockEntry>> {
        return safeApiCall { apiService.getIpBlocks(page, pageSize) }
    }

    suspend fun blockIp(request: BlockIpRequest): ApiResult<Nothing> {
        return safeApiCall { apiService.blockIp(request) }
    }

    suspend fun unblockIp(blockId: Int): ApiResult<Nothing> {
        return safeApiCall { apiService.unblockIp(blockId) }
    }

    suspend fun deleteComment(commentId: Int): ApiResult<Nothing> {
        return safeApiCall { apiService.deleteComment(commentId) }
    }

    suspend fun createProduct(request: CreateProductRequest): ApiResult<ProductDto> {
        return safeApiCall { apiService.createProduct(request) }
    }

    suspend fun updateProduct(productId: Int, request: UpdateProductRequest): ApiResult<ProductDto> {
        return safeApiCall { apiService.updateProduct(productId, request) }
    }

    suspend fun deleteProduct(productId: Int): ApiResult<Nothing> {
        return safeApiCall { apiService.deleteProduct(productId) }
    }
}
