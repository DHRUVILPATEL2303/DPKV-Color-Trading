package com.dpkv.color_trading.domain.repo.authRepo

import com.dpkv.color_trading.common.ResultState
import com.dpkv.color_trading.data.models.login.LoginResponse
import com.dpkv.color_trading.data.models.signup.SignUpResponse

interface AuthRepository {

    suspend fun signUp(email : String , password:  String) : ResultState<SignUpResponse>

    suspend fun login(email : String , password:  String) : ResultState<LoginResponse>
}