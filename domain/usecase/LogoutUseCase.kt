package com.example.shopping_site_andrio.domain.usecase

import com.example.shopping_site_andrio.data.api.ApiResult
import com.example.shopping_site_andrio.data.repository.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): ApiResult<Nothing> {
        return authRepository.logout()
    }
}
