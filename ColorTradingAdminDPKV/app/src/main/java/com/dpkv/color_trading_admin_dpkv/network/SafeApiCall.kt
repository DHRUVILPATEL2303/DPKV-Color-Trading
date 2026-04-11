package com.dpkv.color_trading_admin_dpkv.network

import com.dpkv.color_trading_admin_dpkv.common.ResultState
import com.dpkv.color_trading_admin_dpkv.data.response.ApiResponse
import retrofit2.Response

suspend fun <T> safeApiCall(
    apiCall: suspend () -> Response<ApiResponse<T>>
): ResultState<T> {

    return try {
        val response = apiCall()

        if (response.isSuccessful) {
            val body = response.body()

            if (body?.success == true) {
                ResultState.Success(body.data!!)
            } else {
                ResultState.Error(body?.error ?: body?.message ?: "Unknown error")
            }
        } else {
            ResultState.Error("HTTP ${response.code()} ${response.message()}")
        }

    } catch (e: Exception) {
        ResultState.Error(e.message ?: "Something went wrong")
    }
}