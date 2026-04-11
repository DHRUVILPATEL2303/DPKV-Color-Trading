package com.dpkv.color_trading_admin_dpkv.data.remote

import com.dpkv.color_trading_admin_dpkv.data.models.FundResponseModel
import com.dpkv.color_trading_admin_dpkv.data.models.FundsRequestModel
import com.dpkv.color_trading_admin_dpkv.data.response.ApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST


interface FundsApi {

    @POST("/api/v1/admin/add-funds")
    suspend fun addFunds(
        @Body request: FundsRequestModel
    ): Response<ApiResponse<FundResponseModel>>

    @POST("/api/v1/admin/deduct-funds")
    suspend fun deductFunds(
        @Body request: FundsRequestModel
    ): Response<ApiResponse<FundResponseModel>>
}