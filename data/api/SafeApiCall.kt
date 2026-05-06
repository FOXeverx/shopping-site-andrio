package com.example.shopping_site_andrio.data.api

import com.example.shopping_site_andrio.data.model.ApiError
import com.example.shopping_site_andrio.data.model.ApiResponse
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val code: String?, val message: String) : ApiResult<Nothing>()
}

suspend fun <T> safeApiCall(apiCall: suspend () -> Response<ApiResponse<T>>): ApiResult<T> {
    return try {
        val response = apiCall()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null && body.success && body.data != null) {
                ApiResult.Success(body.data)
            } else {
                ApiResult.Error(
                    code = body?.error?.code,
                    message = body?.message ?: body?.error?.message ?: "Unknown error"
                )
            }
        } else {
            when (response.code()) {
                401 -> ApiResult.Error(code = "AUTH_001", message = "Unauthorized")
                429 -> ApiResult.Error(code = "SYS_001", message = "请求过于频繁")
                403 -> ApiResult.Error(code = "PERM_001", message = "Access denied")
                404 -> ApiResult.Error(code = "NOT_FOUND", message = "Resource not found")
                else -> ApiResult.Error(
                    code = response.code().toString(),
                    message = response.message()
                )
            }
        }
    } catch (e: IOException) {
        ApiResult.Error(code = "NETWORK", message = "Network error: ${e.message}")
    } catch (e: HttpException) {
        ApiResult.Error(code = "HTTP", message = "HTTP error: ${e.message}")
    } catch (e: Exception) {
        ApiResult.Error(code = "UNKNOWN", message = "Error: ${e.message}")
    }
}
