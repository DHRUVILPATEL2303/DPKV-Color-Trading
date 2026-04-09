package com.dpkv.color_trading.domain.usecase.authUseCase

import com.dpkv.color_trading.common.ResultState
import com.dpkv.color_trading.data.models.login.LoginResponse
import com.dpkv.color_trading.domain.repo.authRepo.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String
    ): ResultState<LoginResponse> {
        return authRepository.login(email, password)
    }
}