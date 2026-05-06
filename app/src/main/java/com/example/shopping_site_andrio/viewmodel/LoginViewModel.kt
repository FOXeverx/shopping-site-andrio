package com.example.shopping_site_andrio.ui.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopping_site_andrio.data.api.ApiResult
import com.example.shopping_site_andrio.data.datastore.TokenManager
import com.example.shopping_site_andrio.data.repository.AuthRepository
import com.example.shopping_site_andrio.domain.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val isLoggedIn: Boolean = false,
    val loginState: UiState<Boolean> = UiState(),
    val registerState: UiState<Boolean> = UiState(),
    val username: String = "",
    val password: String = "",
    val email: String = "",
    val confirmPassword: String = "",
    val isRegistering: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            tokenManager.isLoggedIn.collect { loggedIn ->
                _uiState.value = _uiState.value.copy(isLoggedIn = loggedIn)
            }
        }
    }

    fun updateUsername(value: String) {
        _uiState.value = _uiState.value.copy(username = value)
    }

    fun updatePassword(value: String) {
        _uiState.value = _uiState.value.copy(password = value)
    }

    fun updateEmail(value: String) {
        _uiState.value = _uiState.value.copy(email = value)
    }

    fun updateConfirmPassword(value: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = value)
    }

    fun toggleRegisterMode() {
        _uiState.value = _uiState.value.copy(
            isRegistering = !_uiState.value.isRegistering,
            loginState = UiState(),
            registerState = UiState()
        )
    }

    fun login() {
        val state = _uiState.value
        if (state.username.isBlank() || state.password.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loginState = UiState.loading())
            when (val result = authRepository.login(state.username, state.password)) {
                is ApiResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        loginState = UiState.success(true),
                        isLoggedIn = true
                    )
                }
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        loginState = UiState.error(result.message)
                    )
                }
            }
        }
    }

    fun register() {
        val state = _uiState.value
        if (state.username.isBlank() || state.email.isBlank() ||
            state.password.isBlank() || state.confirmPassword.isBlank()
        ) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(registerState = UiState.loading())
            when (val result = authRepository.register(
                state.username, state.email, state.password, state.confirmPassword
            )) {
                is ApiResult.Success -> {
                    _uiState.value = _uiState.value.copy(registerState = UiState.success(true))
                }
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(registerState = UiState.error(result.message))
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.value = LoginUiState()
        }
    }

    fun clearLoginError() {
        _uiState.value = _uiState.value.copy(loginState = UiState())
    }

    fun clearRegisterError() {
        _uiState.value = _uiState.value.copy(registerState = UiState())
    }
}
