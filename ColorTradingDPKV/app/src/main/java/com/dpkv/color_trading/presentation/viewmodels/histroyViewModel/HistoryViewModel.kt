package com.dpkv.color_trading.presentation.viewmodels.histroyViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dpkv.color_trading.common.ResultState
import com.dpkv.color_trading.data.models.bets.BetResponseModel
import com.dpkv.color_trading.data.models.transactions.TransactionResponseModel
import com.dpkv.color_trading.domain.usecase.historyUseCase.GetAllBetsUseCase
import com.dpkv.color_trading.domain.usecase.historyUseCase.GetAllTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getAllBetsUseCase: GetAllBetsUseCase,
    private val getAllTransactionUseCase: GetAllTransactionUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HistoryState())
    val state = _state.asStateFlow()

    fun loadHistory() {
        viewModelScope.launch {

            _state.value = _state.value.copy(isLoading = true)

            val betsDeferred = async { getAllBetsUseCase() }
            val txnDeferred = async { getAllTransactionUseCase() }

            val betsResult = betsDeferred.await()
            val txnResult = txnDeferred.await()

            var betsList: List<BetResponseModel> = emptyList()
            var txnList: List<TransactionResponseModel> = emptyList()
            var errorMsg: String? = null

            when (betsResult) {
                is ResultState.Success -> betsList = betsResult.data
                is ResultState.Error -> errorMsg = betsResult.error
                else -> {}
            }

            when (txnResult) {
                is ResultState.Success -> txnList = txnResult.data
                is ResultState.Error -> errorMsg = txnResult.error
                else -> {}
            }

            _state.value = HistoryState(
                bets = betsList,
                transactions = txnList,
                error = errorMsg,
                isLoading = false
            )
        }
    }
}

data class HistoryState(
    val isLoading: Boolean = false,
    val bets: List<BetResponseModel> = emptyList(),
    val transactions: List<TransactionResponseModel> = emptyList(),
    val error: String? = null
)