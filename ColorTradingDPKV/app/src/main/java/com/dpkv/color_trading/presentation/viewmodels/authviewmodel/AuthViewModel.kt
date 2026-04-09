package com.dpkv.color_trading.presentation.viewmodels.authviewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dpkv.color_trading.common.ResultState
import com.dpkv.color_trading.data.models.login.LoginResponse
import com.dpkv.color_trading.data.models.signup.SignUpRequestModel
import com.dpkv.color_trading.data.models.signup.SignUpResponse
import com.dpkv.color_trading.domain.usecase.authUseCase.LoginUseCase
import com.dpkv.color_trading.domain.usecase.authUseCase.SignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.sign

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase,
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _signUpState = MutableStateFlow(CommonAuthState<SignUpResponse>())
    val signUpState = _signUpState.asStateFlow()

    private val _loginState = MutableStateFlow(CommonAuthState<LoginResponse>())
    val loginState = _loginState.asStateFlow()


    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _signUpState.value = CommonAuthState(isLoading = true)

            val result = signUpUseCase(email, password)

            _signUpState.value = handleResult(result)
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = CommonAuthState(isLoading = true)

            val result = loginUseCase(email, password)

            _loginState.value = handleResult(result)
        }
    }

    private fun <T> handleResult(result: ResultState<T>): CommonAuthState<T> {
        return when (result) {
            is ResultState.Success -> {
                Log.d("handleResultSuccess",result.data.toString())

                CommonAuthState(success = result.data)
            }
            is ResultState.Error -> {
                Log.d("handleResulterror",result.error)
                CommonAuthState(error = result.error)
            }
            is ResultState.Loading -> {
                CommonAuthState(isLoading = true)
            }
        }
    }



}

data class CommonAuthState<T>(
    val isLoading: Boolean = false,
    val success: T? = null,
    val error: String ?= null
)