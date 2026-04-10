package com.dpkv.color_trading.presentation.viewmodels.authviewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dpkv.color_trading.common.ResultState
import com.dpkv.color_trading.data.models.login.LoginResponse
import com.dpkv.color_trading.data.models.profile.ProfileResponseModel
import com.dpkv.color_trading.data.models.signup.SignUpRequestModel
import com.dpkv.color_trading.data.models.signup.SignUpResponse
import com.dpkv.color_trading.datastore.local.TokenManager
import com.dpkv.color_trading.domain.usecase.authUseCase.GetUserProfileUseCase
import com.dpkv.color_trading.domain.usecase.authUseCase.LoginUseCase
import com.dpkv.color_trading.domain.usecase.authUseCase.SignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.sign

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase,
    private val loginUseCase: LoginUseCase,
    private val tokenManager: TokenManager,
    private val getProfileUseCase: GetUserProfileUseCase
) : ViewModel() {


    private val _signUpState = MutableStateFlow(CommonAuthState<SignUpResponse>())
    val signUpState = _signUpState.asStateFlow()

    private val _loginState = MutableStateFlow(CommonAuthState<LoginResponse>())
    val loginState = _loginState.asStateFlow()


    private val _loginEvent = MutableSharedFlow<Unit>()
    val loginEvent = _loginEvent.asSharedFlow()

    private val _profileState = MutableStateFlow(CommonAuthState<ProfileResponseModel>())
    val profileState = _profileState.asStateFlow()


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

            when (result) {
                is ResultState.Success -> {
                    val data = result.data

                    tokenManager.saveTokens(
                        accessToken = data.accessToken,
                        refreshToken = data.refreshToken
                    )

                    _loginEvent.emit(Unit)

                    _loginState.value = CommonAuthState()

                    fetchProfile()
                }

                is ResultState.Error -> {
                    _loginState.value = CommonAuthState(error = result.error)
                }

                is ResultState.Loading -> {
                    _loginState.value = CommonAuthState(isLoading = true)
                }
            }
        }
    }

    fun fetchProfile() {
        viewModelScope.launch {
            _profileState.value = CommonAuthState(isLoading = true)

            when (val result = getProfileUseCase()) {

                is ResultState.Success -> {
                    _profileState.value = CommonAuthState(data = result.data)
                }

                is ResultState.Error -> {
                    _profileState.value = CommonAuthState(error = result.error)
                }

                is ResultState.Loading -> {
                    _profileState.value = CommonAuthState(isLoading = true)
                }
            }
        }
    }

    fun clearData() {
        _profileState.value = CommonAuthState()
        _loginState.value = CommonAuthState()
        _signUpState.value = CommonAuthState()
    }

    private fun <T> handleResult(result: ResultState<T>): CommonAuthState<T> {
        return when (result) {
            is ResultState.Success -> {
                Log.d("AuthVM", "Success: ${result.data}")
                CommonAuthState(data = result.data)
            }
            is ResultState.Error -> {
                Log.d("AuthVM", "Error: ${result.error}")
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
    val data: T? = null,
    val error: String? = null
)