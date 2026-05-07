package com.example.shopping_site_andrio.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.shopping_site_andrio.data.api.ApiResult
import com.example.shopping_site_andrio.data.api.ApiService
import com.example.shopping_site_andrio.data.api.safeApiCall
import com.example.shopping_site_andrio.data.config.AppConfig
import com.example.shopping_site_andrio.data.model.ProductDto
import com.example.shopping_site_andrio.data.paging.ProductPagingSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val apiService: ApiService
) {
    fun getProducts(
        search: String? = null,
        categoryId: Int? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        sort: String? = null,
        order: String? = null
    ): Flow<PagingData<ProductDto>> {
        return Pager(
            config = PagingConfig(pageSize = AppConfig.DEFAULT_PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = {
                ProductPagingSource(
                    apiService = apiService,
                    search = search,
                    categoryId = categoryId,
                    minPrice = minPrice,
                    maxPrice = maxPrice,
                    sort = sort,
                    order = order
                )
            }
        ).flow
    }

    suspend fun getProductDetail(id: Int): ApiResult<ProductDto> {
        return safeApiCall { apiService.getProductDetail(id) }
    }
}
