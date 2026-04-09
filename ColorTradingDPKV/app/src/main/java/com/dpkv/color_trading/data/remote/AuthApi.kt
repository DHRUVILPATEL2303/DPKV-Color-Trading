package com.dpkv.color_trading.data.remote

import com.dpkv.color_trading.common.ApiResponse
import com.dpkv.color_trading.data.models.login.LoginRequestModel
import com.dpkv.color_trading.data.models.login.LoginResponse
import com.dpkv.color_trading.data.models.profile.ProfileResponseModel
import com.dpkv.color_trading.data.models.refreshtoken.RefreshTokenRequestModel
import com.dpkv.color_trading.data.models.refreshtoken.RefreshTokenResponse
import com.dpkv.color_trading.data.models.signup.SignUpRequestModel
import com.dpkv.color_trading.data.models.signup.SignUpResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthApi {

    @POST("/api/v1/auth/signUp")
    @Headers("No-Auth: true")
    suspend fun signUp(@Body reqModel: SignUpRequestModel): Response<ApiResponse<SignUpResponse>>

    @POST("/api/v1/auth/login")
    @Headers("No-Auth: true")
    suspend fun login(@Body reqModel: LoginRequestModel): Response<ApiResponse<LoginResponse>>

    @POST("/api/v1/auth/refresh")
    @Headers("No-Auth: true")
    suspend fun refreshToken(
        @Body refreshToken: RefreshTokenRequestModel
    ): Response<ApiResponse<RefreshTokenResponse>>


    @GET("/api/v1/auth/profile")
    suspend fun getUserProfile() : Response<ApiResponse<ProfileResponseModel>>
}