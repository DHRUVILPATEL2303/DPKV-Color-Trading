package com.dpkv.color_trading.data.repoImpl.gameRepoImpl

import com.dpkv.color_trading.data.websocket.WebSocketManager
import com.dpkv.color_trading.domain.repo.gameREpo.GameRepository
import javax.inject.Inject

class GameRepositoryImpl @Inject constructor(
    private val wsManager: WebSocketManager
) : GameRepository {

    override val events = wsManager.events
    override val connectionState = wsManager.connectionState

    override suspend fun connect() {
        wsManager.connect()
    }

    override fun disconnect() {
        wsManager.disconnect()
    }

    override fun placeBet(amount: Int, color: String) {
        wsManager.placeBet(amount, color)
    }
}