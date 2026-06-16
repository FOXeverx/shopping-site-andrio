package com.example.shopping_site_andrio.data.api

import com.example.shopping_site_andrio.data.config.AppConfig
import com.example.shopping_site_andrio.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<LoginResponse>>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<UserDto>>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<ApiResponse<Nothing>>

    @POST("auth/logout")
    suspend fun logout(): Response<ApiResponse<Nothing>>

    @GET("auth/me")
    suspend fun getCurrentUser(): Response<ApiResponse<UserDto>>

    @PUT("user/me")
    suspend fun updateUser(@Body request: UpdateUserRequest): Response<ApiResponse<UserDto>>

    @POST("auth/send-change-password-code")
    suspend fun sendChangePasswordCode(): Response<ApiResponse<Nothing>>

    @POST("auth/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<ApiResponse<Nothing>>

    @DELETE("user/me")
    suspend fun deleteAccount(@Body body: Map<String, String>): Response<ApiResponse<Nothing>>

    @GET("product")
    suspend fun getProducts(
        @Query("page") page: Int,
        @Query("page_size") pageSize: Int,
        @Query("search") search: String? = null,
        @Query("category_id") categoryId: Int? = null,
        @Query("min_price") minPrice: Double? = null,
        @Query("max_price") maxPrice: Double? = null,
        @Query("sort") sort: String? = null,
        @Query("order") order: String? = null
    ): Response<ApiResponse<List<ProductDto>>>

    @GET("product/{id}")
    suspend fun getProductDetail(@Path("id") id: Int): Response<ApiResponse<ProductDto>>

    @GET("product/{id}/comments")
    suspend fun getProductComments(
        @Path("id") productId: Int,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = AppConfig.DEFAULT_PAGE_SIZE
    ): Response<ApiResponse<List<CommentDto>>>

    @POST("product/{id}/comments")
    suspend fun addComment(
        @Path("id") productId: Int,
        @Body request: AddCommentRequest
    ): Response<ApiResponse<CommentDto>>

    @GET("cart")
    suspend fun getCart(): Response<ApiResponse<List<CartItemDto>>>

    @POST("cart")
    suspend fun addToCart(@Body request: AddCartRequest): Response<ApiResponse<CartItemDto>>

    @PUT("cart/{id}")
    suspend fun updateCartItem(
        @Path("id") cartItemId: Int,
        @Body request: UpdateCartRequest
    ): Response<ApiResponse<CartItemDto>>

    @DELETE("cart/{id}")
    suspend fun removeCartItem(@Path("id") cartItemId: Int): Response<ApiResponse<Nothing>>

    @GET("order")
    suspend fun getOrders(
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = AppConfig.DEFAULT_PAGE_SIZE
    ): Response<ApiResponse<List<OrderDto>>>

    @GET("order/{id}")
    suspend fun getOrderDetail(@Path("id") orderId: Int): Response<ApiResponse<OrderDto>>

    @POST("order")
    suspend fun createOrder(@Body request: CreateOrderRequest): Response<ApiResponse<OrderDto>>

    @POST("order/{id}/confirm")
    suspend fun confirmOrder(
        @Path("id") orderId: Int,
        @Body request: ConfirmOrderRequest
    ): Response<ApiResponse<OrderDto>>

    @GET("recommend/product/{id}")
    suspend fun getProductRecommendations(
        @Path("id") productId: Int,
        @Query("limit") limit: Int = AppConfig.DEFAULT_RECOMMEND_LIMIT
    ): Response<ApiResponse<List<RecommendItemDto>>>

    @GET("recommend/bought-also/{id}")
    suspend fun getBoughtAlsoRecommendations(
        @Path("id") productId: Int,
        @Query("limit") limit: Int = AppConfig.DEFAULT_RECOMMEND_LIMIT
    ): Response<ApiResponse<List<RecommendItemDto>>>

    @GET("recommend/user/me")
    suspend fun getUserRecommendations(
        @Query("limit") limit: Int = AppConfig.DEFAULT_USER_RECOMMEND_LIMIT
    ): Response<ApiResponse<List<RecommendItemDto>>>

    @POST("browse")
    suspend fun logBrowse(@Body request: BrowseLogRequest): Response<ApiResponse<Nothing>>

    @Multipart
    @POST("upload/image")
    suspend fun uploadImage(@Part file: okhttp3.MultipartBody.Part): Response<ApiResponse<ImageUploadResult>>

    @DELETE("product/comments/{comment_id}")
    suspend fun deleteComment(@Path("comment_id") commentId: Int): Response<ApiResponse<Nothing>>

    @GET("admin/users")
    suspend fun getAdminUsers(
        @Query("role") role: String? = null,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = AppConfig.DEFAULT_PAGE_SIZE
    ): Response<ApiResponse<List<AdminUserListItem>>>

    @GET("admin/users/simple")
    suspend fun getAdminUsersSimple(
        @Query("search") search: String? = null,
        @Query("include_inactive") includeInactive: Boolean? = null,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = AppConfig.DEFAULT_PAGE_SIZE
    ): Response<ApiResponse<List<AdminUserListItem>>>

    @POST("admin/user")
    suspend fun createAdminUser(@Body request: CreateAdminUserRequest): Response<ApiResponse<UserDto>>

    @PUT("admin/user/{user_id}")
    suspend fun updateAdminUser(
        @Path("user_id") userId: Int,
        @Body request: UpdateAdminUserRequest
    ): Response<ApiResponse<UserDto>>

    @DELETE("admin/user/{user_id}")
    suspend fun deleteAdminUser(@Path("user_id") userId: Int): Response<ApiResponse<Nothing>>

    @GET("admin/user/{user_id}/browse")
    suspend fun getAdminUserBrowse(
        @Path("user_id") userId: Int,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = AppConfig.DEFAULT_PAGE_SIZE
    ): Response<ApiResponse<List<BrowseLogEntry>>>

    @GET("admin/user/{user_id}/logins")
    suspend fun getAdminUserLogins(
        @Path("user_id") userId: Int,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = AppConfig.DEFAULT_PAGE_SIZE
    ): Response<ApiResponse<List<LoginLogEntry>>>

    @GET("admin/user/{user_id}/purchases/summary")
    suspend fun getAdminUserPurchasesSummary(
        @Path("user_id") userId: Int
    ): Response<ApiResponse<List<PurchaseSummary>>>

    @GET("admin/user/{user_id}/purchases/{category_id}")
    suspend fun getAdminUserPurchasesDetail(
        @Path("user_id") userId: Int,
        @Path("category_id") categoryId: Int,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = AppConfig.DEFAULT_PAGE_SIZE
    ): Response<ApiResponse<List<PurchaseDetail>>>

    @GET("admin/logs")
    suspend fun getOperationLogs(
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = AppConfig.DEFAULT_PAGE_SIZE
    ): Response<ApiResponse<List<OperationLogEntry>>>

    @GET("admin/logs/browse")
    suspend fun getAdminBrowseLogs(
        @Query("product_id") productId: Int? = null,
        @Query("user_id") userId: Int? = null,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = AppConfig.DEFAULT_PAGE_SIZE
    ): Response<ApiResponse<List<BrowseLogEntry>>>

    @GET("admin/anomalies")
    suspend fun getAnomalies(
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = AppConfig.DEFAULT_PAGE_SIZE
    ): Response<ApiResponse<List<AnomalyEntry>>>

    @POST("admin/anomaly/{id}/resolve")
    suspend fun resolveAnomaly(@Path("id") id: Int): Response<ApiResponse<Nothing>>

    @POST("admin/recommend/trigger")
    suspend fun triggerRecommendation(): Response<ApiResponse<RecommendTriggerResult>>

    @GET("admin/user-stats")
    suspend fun getAdminUserStats(): Response<ApiResponse<UserStats>>

    @GET("admin/anomaly-stats")
    suspend fun getAdminAnomalyStats(): Response<ApiResponse<AnomalyStats>>

    @GET("admin/sales-predict")
    suspend fun getSalesPredict(
        @Query("days") days: Int = 7
    ): Response<ApiResponse<SalesPredict>>

    @GET("admin/security/threats")
    suspend fun getSecurityThreats(
        @Query("threat_type") threatType: String? = null,
        @Query("severity") severity: String? = null,
        @Query("is_resolved") isResolved: Boolean? = null,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = AppConfig.DEFAULT_PAGE_SIZE
    ): Response<ApiResponse<List<SecurityThreat>>>

    @POST("admin/security/threats/{threat_id}/resolve")
    suspend fun resolveSecurityThreat(@Path("threat_id") threatId: Int): Response<ApiResponse<Nothing>>

    @GET("admin/security/threats/stats")
    suspend fun getSecurityThreatStats(): Response<ApiResponse<ThreatStats>>

    @GET("admin/security/ip-blocks")
    suspend fun getIpBlocks(
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = AppConfig.DEFAULT_PAGE_SIZE
    ): Response<ApiResponse<List<IpBlockEntry>>>

    @POST("admin/security/ip-blocks")
    suspend fun blockIp(@Body request: BlockIpRequest): Response<ApiResponse<Nothing>>

    @DELETE("admin/security/ip-blocks/{block_id}")
    suspend fun unblockIp(@Path("block_id") blockId: Int): Response<ApiResponse<Nothing>>
}
