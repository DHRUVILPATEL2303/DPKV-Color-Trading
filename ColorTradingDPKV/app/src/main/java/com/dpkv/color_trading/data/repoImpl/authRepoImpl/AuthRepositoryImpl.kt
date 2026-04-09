package com.dpkv.color_trading.data.repoImpl.authRepoImpl

import android.util.Log
import com.dpkv.color_trading.common.ResultState
import com.dpkv.color_trading.common.safeApiCall
import com.dpkv.color_trading.data.models.login.LoginRequestModel
import com.dpkv.color_trading.data.models.login.LoginResponse
import com.dpkv.color_trading.data.models.signup.SignUpRequestModel
import com.dpkv.color_trading.data.models.signup.SignUpResponse
import com.dpkv.color_trading.data.remote.AuthApi
import com.dpkv.color_trading.domain.repo.authRepo.AuthRepository
import javax.inject.Inject


class AuthRepositoryImpl @Inject constructor(
    val authApi: AuthApi
) : AuthRepository {
    override suspend fun signUp(
        email: String,
        password: String
    ): ResultState<SignUpResponse> {

        val request = SignUpRequestModel(email, password)
        Log.d("signup",request.toString())

        return safeApiCall {
            authApi.signUp(request)
        }
    }

    override suspend fun login(
        email: String,
        password: String
    ): ResultState<LoginResponse> {

        val request = LoginRequestModel(email, password)

        return safeApiCall {
            authApi.login(request)
        }
    }
}