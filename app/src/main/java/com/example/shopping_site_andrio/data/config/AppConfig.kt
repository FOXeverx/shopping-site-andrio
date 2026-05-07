package com.example.shopping_site_andrio.data.config

import com.example.shopping_site_andrio.BuildConfig

object AppConfig {

    const val BASE_URL: String = BuildConfig.BASE_URL

    const val CONNECT_TIMEOUT_SEC: Long = 30L
    const val READ_TIMEOUT_SEC: Long = 30L
    const val WRITE_TIMEOUT_SEC: Long = 30L

    const val DATASTORE_NAME: String = "shopping_prefs"

    const val DEFAULT_PAGE_SIZE: Int = 20
    const val DEFAULT_RECOMMEND_LIMIT: Int = 5
    const val DEFAULT_USER_RECOMMEND_LIMIT: Int = 10
}
