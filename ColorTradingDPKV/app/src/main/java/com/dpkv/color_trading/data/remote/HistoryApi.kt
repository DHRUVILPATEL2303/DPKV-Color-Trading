package com.dpkv.color_trading.data.remote

import com.dpkv.color_trading.common.ApiResponse
import com.dpkv.color_trading.data.models.bets.BetResponseModel
import com.dpkv.color_trading.data.models.transactions.TransactionResponseModel
import retrofit2.Response
import retrofit2.http.GET

interface HistoryApi {


        @GET("/api/v1/transactions/all")
        suspend fun getTransactions(): Response<ApiResponse<List<TransactionResponseModel>>>

        @GET("/api/v1/bets/history")
        suspend fun getBets(): Response<ApiResponse<List<BetResponseModel>>>
}