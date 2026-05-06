package com.example.shopping_site_andrio.data.repository

import com.example.shopping_site_andrio.data.api.ApiResult
import com.example.shopping_site_andrio.data.api.ApiService
import com.example.shopping_site_andrio.data.api.safeApiCall
import com.example.shopping_site_andrio.data.datastore.TokenManager
import com.example.shopping_site_andrio.data.model.*
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) {
    suspend fun login(username: String, password: String): ApiResult<LoginResponse> {
        val result = safeApiCall { apiService.login(LoginRequest(username, password)) }
        if (result is ApiResult.Success) {
            val data = result.data
            tokenManager.saveAuthData(
                token = data.token,
                userId = data.user.id,
                username = data.user.username,
                role = data.user.role ?: "customer"
            )
        }
        return result
    }

    suspend fun register(username: String, email: String, password: String, confirmPassword: String): ApiResult<UserDto> {
        return safeApiCall { apiService.register(RegisterRequest(username, email, password, confirmPassword)) }
    }

    suspend fun logout(): ApiResult<Nothing> {
        val result = safeApiCall { apiService.logout() }
        tokenManager.clearAuthData()
        return result
    }

    suspend fun getCurrentUser(): ApiResult<UserDto> {
        return safeApiCall { apiService.getCurrentUser() }
    }

    suspend fun updateUser(email: String): ApiResult<UserDto> {
        return safeApiCall { apiService.updateUser(UpdateUserRequest(email)) }
    }

    suspend fun changePassword(oldPassword: String, newPassword: String, code: String): ApiResult<Nothing> {
        return safeApiCall { apiService.changePassword(ChangePasswordRequest(oldPassword, newPassword, code)) }
    }

    suspend fun sendChangePasswordCode(): ApiResult<Nothing> {
        return safeApiCall { apiService.sendChangePasswordCode() }
    }

    suspend fun forgotPassword(username: String): ApiResult<Nothing> {
        return safeApiCall { apiService.forgotPassword(ForgotPasswordRequest(username)) }
    }

    suspend fun isUserLoggedIn(): Boolean {
        return tokenManager.isLoggedIn.firstOrNull() ?: false
    }
}
