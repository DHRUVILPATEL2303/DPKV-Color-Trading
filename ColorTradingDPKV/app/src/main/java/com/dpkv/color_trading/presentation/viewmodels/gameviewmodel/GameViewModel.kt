package com.dpkv.color_trading.presentation.viewmodels.gameviewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dpkv.color_trading.data.websocket.WsEvent
import com.dpkv.color_trading.domain.repo.gameREpo.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val repository: GameRepository
) : ViewModel() {

    val connectionState = repository.connectionState

    private val _uiState = MutableStateFlow(GameState())
    val uiState = _uiState.asStateFlow()

    init {
        observeWs()
        observeConnection()
    }

    private fun observeConnection() {
        viewModelScope.launch {
            repository.connectionState.collect { connected ->
                _uiState.value = _uiState.value.copy(
                    isConnected = connected
                )
            }
        }
    }

    private fun observeWs() {
        viewModelScope.launch {
            repository.events.collect { event ->

                when (event) {

                    is WsEvent.Timer -> {
                        _uiState.value = _uiState.value.copy(
                            secondsLeft = event.data.seconds_left
                        )
                    }

                    is WsEvent.Result -> {
                        _uiState.value = _uiState.value.copy(
                            result = event.data.result
                        )
                    }

                    is WsEvent.RoundStart -> {
                        _uiState.value = _uiState.value.copy(
                            roundId = event.data.round_id,
                            result = "",
                            isBettingOpen = true
                        )
                    }

                    is WsEvent.BettingClosed -> {
                        _uiState.value = _uiState.value.copy(
                            isBettingOpen = false
                        )
                    }

                    else -> {
                        Log.d("GameVM", "Unknown event: $event")
                    }
                }
            }
        }
    }

    fun connect() {
        viewModelScope.launch {
            repository.connect()
        }
    }

    fun placeBet(amount: Int, color: String) {
        repository.placeBet(amount, color)
    }

    override fun onCleared() {
        super.onCleared()
        repository.disconnect()
    }
}
data class GameState(
    val secondsLeft: Int = 0,
    val result: String = "",
    val roundId: Int = 0,
    val isConnected: Boolean = false,
    val isBettingOpen: Boolean = true
)