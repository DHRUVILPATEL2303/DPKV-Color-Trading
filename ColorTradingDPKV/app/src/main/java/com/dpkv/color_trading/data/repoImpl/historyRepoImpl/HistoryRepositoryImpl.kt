package com.dpkv.color_trading.data.repoImpl.historyRepoImpl

import com.dpkv.color_trading.common.ResultState
import com.dpkv.color_trading.common.safeApiCall
import com.dpkv.color_trading.data.models.bets.BetResponseModel
import com.dpkv.color_trading.data.models.transactions.TransactionResponseModel
import com.dpkv.color_trading.data.remote.HistoryApi
import com.dpkv.color_trading.domain.repo.historyRepo.HistoryRepo
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor(
    val historyApi: HistoryApi
) : HistoryRepo {
    override suspend fun getTransactions(): ResultState<List<TransactionResponseModel>> {
        return safeApiCall {
            historyApi.getTransactions()
        }
    }

    override suspend fun getAllBets(): ResultState<List<BetResponseModel>> {
        return safeApiCall {
            historyApi.getBets()
        }
    }
}