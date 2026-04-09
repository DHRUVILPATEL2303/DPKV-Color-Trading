package com.dpkv.color_trading.domain.usecase.historyUseCase

import com.dpkv.color_trading.domain.repo.historyRepo.HistoryRepo
import javax.inject.Inject

class GetAllTransactionUseCase @Inject constructor(
    private val historyRepo: HistoryRepo
) {
    suspend operator fun invoke() = historyRepo.getTransactions()
}