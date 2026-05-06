package com.example.shopping_site_andrio.domain.usecase

import com.example.shopping_site_andrio.data.api.ApiResult
import com.example.shopping_site_andrio.data.model.LoginResponse
import com.example.shopping_site_andrio.data.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(username: String, password: String): ApiResult<LoginResponse> {
        return authRepository.login(username, password)
    }
}
