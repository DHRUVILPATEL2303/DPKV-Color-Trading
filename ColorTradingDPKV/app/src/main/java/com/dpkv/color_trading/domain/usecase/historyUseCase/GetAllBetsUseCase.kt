package com.dpkv.color_trading.domain.usecase.historyUseCase

import com.dpkv.color_trading.common.ResultState
import com.dpkv.color_trading.data.models.bets.BetResponseModel
import com.dpkv.color_trading.domain.repo.historyRepo.HistoryRepo
import javax.inject.Inject

class GetAllBetsUseCase @Inject constructor(
    val historyRepo: HistoryRepo
) {
    suspend operator fun invoke(): ResultState<List<BetResponseModel>> {
        return historyRepo.getAllBets()
    }
}