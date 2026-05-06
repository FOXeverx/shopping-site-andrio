package com.example.shopping_site_andrio.data.model

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val token_type: String,
    val expires_in: Int,
    val user: UserDto
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val confirm_password: String
)

data class UserDto(
    val id: Int,
    val username: String,
    val email: String?,
    val role: String?,
    val is_active: Boolean?,
    val last_login_at: String?,
    val created_at: String?
)

data class ForgotPasswordRequest(
    val username: String
)

data class ChangePasswordRequest(
    val old_password: String,
    val new_password: String,
    val verification_code: String
)

data class UpdateUserRequest(
    val email: String? = null
)
