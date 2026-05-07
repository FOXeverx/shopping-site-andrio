package com.example.shopping_site_andrio.data.config

import com.example.shopping_site_andrio.BuildConfig

object AppConfig {

    const val BASE_URL: String = BuildConfig.BASE_URL
    private val ORIGIN: String = BASE_URL.removeSuffix("api/")

    fun resolveImageUrl(path: String?): String? {
        if (path.isNullOrBlank()) return null
        if (path.startsWith("http://") || path.startsWith("https://")) return path
        return "${ORIGIN}${path.removePrefix("/")}"
    }

    const val CONNECT_TIMEOUT_SEC: Long = 30L
    const val READ_TIMEOUT_SEC: Long = 30L
    const val WRITE_TIMEOUT_SEC: Long = 30L

    const val DATASTORE_NAME: String = "shopping_prefs"

    const val DEFAULT_PAGE_SIZE: Int = 20
    const val DEFAULT_RECOMMEND_LIMIT: Int = 5
    const val DEFAULT_USER_RECOMMEND_LIMIT: Int = 10
}
