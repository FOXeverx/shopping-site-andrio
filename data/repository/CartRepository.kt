package com.example.shopping_site_andrio.data.repository

import com.example.shopping_site_andrio.data.api.ApiResult
import com.example.shopping_site_andrio.data.api.ApiService
import com.example.shopping_site_andrio.data.api.safeApiCall
import com.example.shopping_site_andrio.data.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getCart(): ApiResult<List<CartItemDto>> {
        return safeApiCall { apiService.getCart() }
    }

    suspend fun addToCart(productId: Int, quantity: Int): ApiResult<CartItemDto> {
        return safeApiCall { apiService.addToCart(AddCartRequest(productId, quantity)) }
    }

    suspend fun updateCartItem(cartItemId: Int, quantity: Int): ApiResult<CartItemDto> {
        return safeApiCall { apiService.updateCartItem(cartItemId, UpdateCartRequest(quantity)) }
    }

    suspend fun removeCartItem(cartItemId: Int): ApiResult<Nothing> {
        return safeApiCall { apiService.removeCartItem(cartItemId) }
    }
}
