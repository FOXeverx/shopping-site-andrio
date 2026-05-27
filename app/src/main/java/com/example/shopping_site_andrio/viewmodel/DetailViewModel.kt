package com.example.shopping_site_andrio.ui.screen.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopping_site_andrio.data.api.ApiResult
import com.example.shopping_site_andrio.data.config.AppConfig
import com.example.shopping_site_andrio.data.model.*
import com.example.shopping_site_andrio.data.repository.*
import com.example.shopping_site_andrio.domain.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DetailUiState(
    val product: UiState<ProductDto> = UiState.loading(),
    val comments: List<CommentDto> = emptyList(),
    val commentsLoading: Boolean = false,
    val commentsPage: Int = 1,
    val commentsHasMore: Boolean = true,
    val relatedRecommendations: List<RecommendItemDto> = emptyList(),
    val boughtAlsoRecommendations: List<RecommendItemDto> = emptyList(),
    val newCommentText: String = "",
    val addingComment: Boolean = false,
    val quantity: Int = 1,
    val addingToCart: Boolean = false,
    val snackbarMessage: String? = null
)

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val commentRepository: CommentRepository,
    private val recommendRepository: RecommendRepository,
    private val cartRepository: CartRepository,
    private val browseLogRepository: BrowseLogRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private var browseJob: Job? = null
    private var stayStartTime: Long = 0

    fun loadProduct(productId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(product = UiState.loading())
            when (val result = productRepository.getProductDetail(productId)) {
                is ApiResult.Success -> {
                    _uiState.value = _uiState.value.copy(product = UiState.success(result.data))
                }
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(product = UiState.error(result.message))
                }
            }
        }
        loadComments(productId, refresh = true)
        loadRecommendations(productId)
        startBrowsing(productId)
    }

    private fun loadComments(productId: Int, refresh: Boolean = false) {
        viewModelScope.launch {
            val page = if (refresh) 1 else _uiState.value.commentsPage + 1
            _uiState.value = _uiState.value.copy(commentsLoading = true)
            when (val result = commentRepository.getComments(productId, page = page)) {
                is ApiResult.Success -> {
                    val newComments = result.data
                    val merged = if (refresh) {
                        newComments
                    } else {
                        val existingIds = _uiState.value.comments.map { it.id }.toSet()
                        _uiState.value.comments + newComments.filter { it.id !in existingIds }
                    }
                    _uiState.value = _uiState.value.copy(
                        comments = merged,
                        commentsPage = page,
                        commentsHasMore = newComments.size >= AppConfig.DEFAULT_PAGE_SIZE,
                        commentsLoading = false
                    )
                }
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(commentsLoading = false)
                }
            }
        }
    }

    fun loadMoreComments() {
        val state = _uiState.value
        if (state.commentsLoading || !state.commentsHasMore) return
        val productId = state.product.data?.id ?: return
        loadComments(productId)
    }

    private fun loadRecommendations(productId: Int) {
        viewModelScope.launch {
            when (val result = recommendRepository.getProductRecommendations(productId)) {
                is ApiResult.Success -> {
                    _uiState.value = _uiState.value.copy(relatedRecommendations = result.data)
                }
                else -> {}
            }
        }
        viewModelScope.launch {
            when (val result = recommendRepository.getBoughtAlsoRecommendations(productId)) {
                is ApiResult.Success -> {
                    _uiState.value = _uiState.value.copy(boughtAlsoRecommendations = result.data)
                }
                else -> {}
            }
        }
    }

    private fun startBrowsing(productId: Int) {
        stayStartTime = System.currentTimeMillis()
        browseJob?.cancel()
        browseJob = viewModelScope.launch {
            while (true) {
                delay(1000)
            }
        }
        viewModelScope.launch {
            browseLogRepository.logBrowse(productId, 0)
        }
    }

    fun stopBrowsing(productId: Int) {
        browseJob?.cancel()
        val stayTime = ((System.currentTimeMillis() - stayStartTime) / 1000).toInt()
        viewModelScope.launch {
            browseLogRepository.logBrowse(productId, stayTime)
        }
    }

    fun updateCommentText(text: String) {
        _uiState.value = _uiState.value.copy(newCommentText = text)
    }

    fun addComment(productId: Int) {
        val text = _uiState.value.newCommentText
        if (text.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(addingComment = true)
            when (val result = commentRepository.addComment(productId, text)) {
                is ApiResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        newCommentText = "",
                        addingComment = false
                    )
                    loadComments(productId, refresh = true)
                }
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        addingComment = false,
                        snackbarMessage = result.message
                    )
                }
            }
        }
    }

    fun updateQuantity(qty: Int) {
        if (qty >= 1) {
            _uiState.value = _uiState.value.copy(quantity = qty)
        }
    }

    fun addToCart(productId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(addingToCart = true)
            when (val result = cartRepository.addToCart(productId, _uiState.value.quantity)) {
                is ApiResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        addingToCart = false,
                        snackbarMessage = "Added to cart"
                    )
                }
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        addingToCart = false,
                        snackbarMessage = result.message
                    )
                }
            }
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(snackbarMessage = null)
    }
}
