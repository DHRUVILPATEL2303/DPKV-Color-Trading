package com.dpkv.color_trading.data.remote

import com.dpkv.color_trading.common.ApiResponse
import com.dpkv.color_trading.data.models.roundshistory.RoundHistoryResponseModel
import retrofit2.Response
import retrofit2.http.GET

interface RoundApi {

    @GET("/api/v1/round/last10")
    suspend fun getHistory(): Response<ApiResponse<List<RoundHistoryResponseModel>>>
}