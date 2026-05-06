package com.example.shopping_site_andrio.data.api

import com.example.shopping_site_andrio.data.datastore.TokenManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { tokenManager.token.firstOrNull() }
        val request = chain.request().newBuilder().apply {
            if (!token.isNullOrEmpty()) {
                addHeader("Authorization", "Bearer $token")
            }
            addHeader("Content-Type", "application/json")
        }.build()
        val response = chain.proceed(request)
        if (response.code == 401) {
            runBlocking { tokenManager.clearAuthData() }
        }
        return response
    }
}
