package com.dpkv.color_trading_admin_dpkv.presentation.viewModels.fundsViewModel

import android.view.View
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dpkv.color_trading_admin_dpkv.common.ResultState
import com.dpkv.color_trading_admin_dpkv.data.models.FundResponseModel
import com.dpkv.color_trading_admin_dpkv.data.models.FundsRequestModel
import com.dpkv.color_trading_admin_dpkv.domain.useCase.fundsUseCase.AddFundsUseCase
import com.dpkv.color_trading_admin_dpkv.domain.useCase.fundsUseCase.DeductFundsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class FundsViewModel @Inject constructor(
    private val addFundsUseCase: AddFundsUseCase,
    private val deductFundsUseCase: DeductFundsUseCase
) : ViewModel() {

    private val _addFundsState = MutableStateFlow(CommonFundsState<FundResponseModel>())
    val addFundsState = _addFundsState.asStateFlow()

    private val _deductFundsState = MutableStateFlow(CommonFundsState<FundResponseModel>())
    val deductFundsState = _deductFundsState.asStateFlow()

    fun addFunds(userId: Int, amount: Int) {
        viewModelScope.launch {
            _addFundsState.value = CommonFundsState(isLoading = true)

            when (val result = addFundsUseCase(userId, amount)) {

                is ResultState.Success -> {
                    _addFundsState.value = CommonFundsState(success = result.data)
                }

                is ResultState.Error -> {
                    _addFundsState.value = CommonFundsState(error = result.message)
                }

                is ResultState.Loading -> {
                    _addFundsState.value = CommonFundsState(isLoading = true)
                }
            }
        }
    }

    fun deductFunds(userId: Int, amount: Int) {
        viewModelScope.launch {
            _deductFundsState.value = CommonFundsState(isLoading = true)

            when (val result = deductFundsUseCase(userId, amount)) {

                is ResultState.Success -> {
                    _deductFundsState.value = CommonFundsState(success = result.data)
                }

                is ResultState.Error -> {
                    _deductFundsState.value = CommonFundsState(error = result.message)
                }

                is ResultState.Loading -> {
                    _deductFundsState.value = CommonFundsState(isLoading = true)
                }
            }
        }
    }
}

data class CommonFundsState<T>(
    val error : String? = null,
    val success : T ?=null,
    val isLoading : Boolean = false
)