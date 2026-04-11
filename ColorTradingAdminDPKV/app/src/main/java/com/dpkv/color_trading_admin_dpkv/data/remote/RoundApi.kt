package com.dpkv.color_trading_admin_dpkv.data.remote

import com.dpkv.color_trading_admin_dpkv.data.models.RoundsResponse
import com.dpkv.color_trading_admin_dpkv.data.response.ApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RoundApi {
    @GET("/api/v1/admin/rounds")
    suspend fun getRounds(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<ApiResponse<RoundsResponse>>
}