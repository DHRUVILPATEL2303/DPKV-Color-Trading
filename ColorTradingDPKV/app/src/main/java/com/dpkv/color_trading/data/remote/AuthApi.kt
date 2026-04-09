package com.dpkv.color_trading.data.remote

import com.dpkv.color_trading.common.ApiResponse
import com.dpkv.color_trading.data.models.login.LoginRequestModel
import com.dpkv.color_trading.data.models.login.LoginResponse
import com.dpkv.color_trading.data.models.signup.SignUpRequestModel
import com.dpkv.color_trading.data.models.signup.SignUpResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("/api/v1/auth/signUp")
    suspend fun signUp(@Body reqModel: SignUpRequestModel): Response<ApiResponse<SignUpResponse>>

    @POST("/api/v1/auth/login")
    suspend fun login(@Body reqModel: LoginRequestModel): Response<ApiResponse<LoginResponse>>
}