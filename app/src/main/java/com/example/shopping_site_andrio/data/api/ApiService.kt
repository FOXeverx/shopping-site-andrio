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
}
