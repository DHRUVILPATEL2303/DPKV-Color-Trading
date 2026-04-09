package com.dpkv.color_trading.domain.usecase.authUseCase

import android.R
import com.dpkv.color_trading.common.ResultState
import com.dpkv.color_trading.data.models.signup.SignUpResponse
import com.dpkv.color_trading.domain.repo.authRepo.AuthRepository
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    val authRepository: AuthRepository
) {

    suspend operator fun invoke(email : String,password : String) : ResultState<SignUpResponse>{
        return authRepository.signUp(email, password)

    }
}