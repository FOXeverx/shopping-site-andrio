package com.example.shopping_site_andrio.domain.model

data class UiState<T>(
    val loading: Boolean = false,
    val data: T? = null,
    val error: String? = null
) {
    companion object {
        fun <T> loading(): UiState<T> = UiState(loading = true)
        fun <T> success(data: T): UiState<T> = UiState(data = data)
        fun <T> error(error: String): UiState<T> = UiState(error = error)
    }
}
