package com.example.shopping_site_andrio.ui.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopping_site_andrio.data.api.ApiResult
import com.example.shopping_site_andrio.data.datastore.TokenManager
import com.example.shopping_site_andrio.data.model.UserDto
import com.example.shopping_site_andrio.data.repository.AuthRepository
import com.example.shopping_site_andrio.domain.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val user: UiState<UserDto> = UiState.loading(),
    val email: String = "",
    val oldPassword: String = "",
    val newPassword: String = "",
    val verificationCode: String = "",
    val message: String? = null,
    val showChangePassword: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUser()
    }

    fun loadUser() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(user = UiState.loading())
            when (val result = authRepository.getCurrentUser()) {
                is ApiResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        user = UiState.success(result.data),
                        email = result.data.email ?: ""
                    )
                }
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(user = UiState.error(result.message))
                }
            }
        }
    }

    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun saveProfile() {
        viewModelScope.launch {
            when (val result = authRepository.updateUser(_uiState.value.email)) {
                is ApiResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        user = UiState.success(result.data),
                        message = "Profile updated"
                    )
                }
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(message = result.message)
                }
            }
        }
    }

    fun toggleChangePassword() {
        _uiState.value = _uiState.value.copy(showChangePassword = !_uiState.value.showChangePassword)
    }

    fun updateOldPassword(p: String) { _uiState.value = _uiState.value.copy(oldPassword = p) }
    fun updateNewPassword(p: String) { _uiState.value = _uiState.value.copy(newPassword = p) }
    fun updateVerificationCode(c: String) { _uiState.value = _uiState.value.copy(verificationCode = c) }

    fun sendVerificationCode() {
        viewModelScope.launch {
            when (authRepository.sendChangePasswordCode()) {
                is ApiResult.Success -> {
                    _uiState.value = _uiState.value.copy(message = "Code sent to your email")
                }
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(message = "Failed to send code")
                }
            }
        }
    }

    fun changePassword() {
        val state = _uiState.value
        viewModelScope.launch {
            when (val result = authRepository.changePassword(
                state.oldPassword, state.newPassword, state.verificationCode
            )) {
                is ApiResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        message = "Password changed",
                        showChangePassword = false,
                        oldPassword = "",
                        newPassword = "",
                        verificationCode = ""
                    )
                }
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(message = result.message)
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
}
