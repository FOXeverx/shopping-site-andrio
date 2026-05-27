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
    val validationErrorMessage: String? = null,
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
            registerState = UiState(),
            validationErrorMessage = null
        )
    }

    fun login() {
        val state = _uiState.value
        if (state.username.isBlank() || state.password.isBlank()) {
            _uiState.value = _uiState.value.copy(
                validationErrorMessage = "Please enter username and password"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(validationErrorMessage = null, loginState = UiState.loading())
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
        if (state.username.isBlank()) {
            _uiState.value = _uiState.value.copy(validationErrorMessage = "Please enter a username")
            return
        }
        if (state.email.isBlank()) {
            _uiState.value = _uiState.value.copy(validationErrorMessage = "Please enter an email")
            return
        }
        if (state.password.isBlank()) {
            _uiState.value = _uiState.value.copy(validationErrorMessage = "Please enter a password")
            return
        }
        if (state.confirmPassword.isBlank()) {
            _uiState.value = _uiState.value.copy(validationErrorMessage = "Please confirm your password")
            return
        }
        if (state.password != state.confirmPassword) {
            _uiState.value = _uiState.value.copy(validationErrorMessage = "Passwords do not match")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(validationErrorMessage = null, registerState = UiState.loading())
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

    fun forgotPassword() {
        val state = _uiState.value
        if (state.username.isBlank()) {
            _uiState.value = _uiState.value.copy(validationErrorMessage = "Please enter your username first")
            return
        }
        viewModelScope.launch {
            when (authRepository.forgotPassword(state.username)) {
                is ApiResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        validationErrorMessage = null,
                        registerState = UiState.error("Password reset email sent")
                    )
                }
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(validationErrorMessage = "Failed to send reset email")
                }
            }
        }
    }

    fun clearValidationError() {
        _uiState.value = _uiState.value.copy(validationErrorMessage = null)
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
