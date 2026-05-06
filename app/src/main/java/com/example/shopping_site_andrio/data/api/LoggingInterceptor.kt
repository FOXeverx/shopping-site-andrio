package com.example.shopping_site_andrio.data.api

import okhttp3.Interceptor
import okhttp3.Response

class LoggingInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBody = request.body?.toString() ?: "null"
        android.util.Log.d("HTTP", "Request: ${request.method} ${request.url} body=$requestBody")

        val response = chain.proceed(request)
        val responseBody = response.peekBody(Long.MAX_VALUE).string()
        android.util.Log.d("HTTP", "Response: ${response.code} ${response.message} body=$responseBody")

        return response
    }
}
