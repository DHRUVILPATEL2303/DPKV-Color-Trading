package com.dpkv.color_trading.common

import retrofit2.Response


suspend fun <T> safeApiCall(
    apiCall: suspend () -> Response<ApiResponse<T>>
): ResultState<T> {
    return try {
        val response = apiCall()

        if (response.isSuccessful) {
            val body = response.body()

            if (body != null) {
                if (body.success) {
                    ResultState.Success(body.data!!)
                } else {
                    ResultState.Error(body.error ?: body.message ?: "Unknown error")
                }
            } else {
                ResultState.Error("Empty response body")
            }
        } else {
            ResultState.Error("HTTP ${response.code()}")
        }

    } catch (e: Exception) {
        ResultState.Error(e.message ?: "Network error")
    }
}