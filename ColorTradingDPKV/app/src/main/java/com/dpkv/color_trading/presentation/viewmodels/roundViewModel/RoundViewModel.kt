package com.dpkv.color_trading.presentation.viewmodels.roundViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dpkv.color_trading.common.ResultState
import com.dpkv.color_trading.data.models.roundshistory.RoundHistoryResponseModel
import com.dpkv.color_trading.domain.usecase.roundUseCase.GetLast10RoundHistory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoundViewModel @Inject constructor(
    private val getLast10RoundHistory: GetLast10RoundHistory
) : ViewModel() {

    private val _state = MutableStateFlow(RoundHistoryState())
    val state = _state.asStateFlow()

    init {
        fetchHistory()
    }

    private fun fetchHistory() {
        viewModelScope.launch {
            _state.value = RoundHistoryState(isLoading = true)

            when (val result = getLast10RoundHistory()) {

                is ResultState.Success -> {
                    _state.value = RoundHistoryState(
                        history = result.data
                    )
                }

                is ResultState.Error -> {
                    _state.value = RoundHistoryState(
                        error = result.error
                    )
                }

                else -> {}
            }
        }
    }

    fun onNewResult(roundId: Int, result: String) {

        val current = _state.value.history.toMutableList()

        current.add(
            0,
            RoundHistoryResponseModel(
                round = roundId,
                result = result
            )
        )

        val updated = current.take(10)

        _state.value = _state.value.copy(
            history = updated
        )
    }
}

data class RoundHistoryState(
    val history: List<RoundHistoryResponseModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)