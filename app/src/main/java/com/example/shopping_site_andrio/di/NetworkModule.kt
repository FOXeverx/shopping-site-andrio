package com.example.shopping_site_andrio.di

import com.example.shopping_site_andrio.data.api.ApiService
import com.example.shopping_site_andrio.data.api.AuthInterceptor
import com.example.shopping_site_andrio.data.api.LoggingInterceptor
import com.example.shopping_site_andrio.data.config.AppConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(LoggingInterceptor())
            .connectTimeout(AppConfig.CONNECT_TIMEOUT_SEC, TimeUnit.SECONDS)
            .readTimeout(AppConfig.READ_TIMEOUT_SEC, TimeUnit.SECONDS)
            .writeTimeout(AppConfig.WRITE_TIMEOUT_SEC, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(AppConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}
