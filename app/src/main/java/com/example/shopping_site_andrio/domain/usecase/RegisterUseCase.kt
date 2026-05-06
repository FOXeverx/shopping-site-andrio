package com.example.shopping_site_andrio.domain.usecase

import com.example.shopping_site_andrio.data.api.ApiResult
import com.example.shopping_site_andrio.data.model.UserDto
import com.example.shopping_site_andrio.data.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        username: String,
        email: String,
        password: String,
        confirmPassword: String
    ): ApiResult<UserDto> {
        return authRepository.register(username, email, password, confirmPassword)
    }
}
