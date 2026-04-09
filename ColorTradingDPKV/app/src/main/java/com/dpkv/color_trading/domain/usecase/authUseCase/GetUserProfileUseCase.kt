package com.dpkv.color_trading.domain.usecase.authUseCase

import com.dpkv.color_trading.common.ResultState
import com.dpkv.color_trading.data.models.profile.ProfileResponseModel
import com.dpkv.color_trading.domain.repo.authRepo.AuthRepository
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(): ResultState<ProfileResponseModel> {
        return authRepository.getUserProfile()
    }
}