package com.example.shopping_site_andrio.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.shopping_site_andrio.data.api.ApiService
import com.example.shopping_site_andrio.data.api.safeApiCall
import com.example.shopping_site_andrio.data.model.ProductDto

class ProductPagingSource(
    private val apiService: ApiService,
    private val search: String? = null,
    private val categoryId: Int? = null,
    private val minPrice: Double? = null,
    private val maxPrice: Double? = null,
    private val sort: String? = null,
    private val order: String? = null
) : PagingSource<Int, ProductDto>() {

    override fun getRefreshKey(state: PagingState<Int, ProductDto>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ProductDto> {
        return try {
            val page = params.key ?: 1
            val response = apiService.getProducts(
                page = page,
                pageSize = params.loadSize,
                search = search,
                categoryId = categoryId,
                minPrice = minPrice,
                maxPrice = maxPrice,
                sort = sort,
                order = order
            )
            val result = safeApiCall { response }
            when (result) {
                is com.example.shopping_site_andrio.data.api.ApiResult.Success -> {
                    val products = result.data
                    val prevKey = if (page > 1) page - 1 else null
                    val nextKey = if (products.isNotEmpty()) page + 1 else null
                    LoadResult.Page(
                        data = products,
                        prevKey = prevKey,
                        nextKey = nextKey
                    )
                }
                is com.example.shopping_site_andrio.data.api.ApiResult.Error -> {
                    LoadResult.Error(Exception(result.message))
                }
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
