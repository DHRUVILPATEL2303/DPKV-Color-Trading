package com.dpkv.color_trading.domain.repo.historyRepo

import com.dpkv.color_trading.common.ResultState
import com.dpkv.color_trading.data.models.bets.BetResponseModel
import com.dpkv.color_trading.data.models.transactions.TransactionResponseModel

interface HistoryRepo {

    suspend fun getTransactions(): ResultState<List<TransactionResponseModel>>

    suspend fun getAllBets(): ResultState<List<BetResponseModel>>

}