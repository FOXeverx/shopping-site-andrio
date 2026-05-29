package com.example.shopping_site_andrio.domain.model

sealed class UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent()
}
