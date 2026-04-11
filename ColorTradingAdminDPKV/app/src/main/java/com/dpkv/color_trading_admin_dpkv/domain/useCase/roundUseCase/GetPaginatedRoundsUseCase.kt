package com.dpkv.color_trading_admin_dpkv.domain.useCase.roundUseCase

import com.dpkv.color_trading_admin_dpkv.domain.repo.roundRepo.RoundRepository
import javax.inject.Inject

class GetPaginatedRoundsUseCase @Inject constructor(
    private val roundRepository: RoundRepository
) {
    operator fun invoke() = roundRepository.getPaginatedRounds()
}
