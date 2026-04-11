package com.dpkv.color_trading.network

import com.dpkv.color_trading.data.websocket.WebSocketManager
import com.dpkv.color_trading.datastore.local.TokenManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

class SessionManager @Inject constructor(
    private val tokenManager: TokenManager,
    private val webSocketManager: WebSocketManager
) {

    private val _logoutEvent = MutableSharedFlow<Unit>()
    val logoutEvent = _logoutEvent.asSharedFlow()

    suspend fun logout() {
        tokenManager.clearTokens()
        webSocketManager.disconnect()
        _logoutEvent.emit(Unit)
    }
}