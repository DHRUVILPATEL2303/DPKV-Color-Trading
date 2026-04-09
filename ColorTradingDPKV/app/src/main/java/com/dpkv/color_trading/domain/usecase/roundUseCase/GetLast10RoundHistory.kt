package com.dpkv.color_trading.domain.usecase.roundUseCase

import com.dpkv.color_trading.common.ResultState
import com.dpkv.color_trading.data.models.roundshistory.RoundHistoryResponseModel
import com.dpkv.color_trading.domain.repo.roundRepo.RoundRepository
import javax.inject.Inject

class GetLast10RoundHistory @Inject constructor(
    private val roundRepository: RoundRepository
) {
    suspend operator fun invoke(): ResultState<List<RoundHistoryResponseModel>> {
        return roundRepository.getLast10RoundsHistory()
    }
}