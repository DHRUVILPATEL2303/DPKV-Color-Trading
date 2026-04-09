package com.dpkv.color_trading.domain.repo.gameREpo

import com.dpkv.color_trading.data.websocket.WsEvent
import kotlinx.coroutines.flow.Flow

interface GameRepository {
    val events: Flow<WsEvent>
    val connectionState: Flow<Boolean>

    suspend fun connect()
    fun disconnect()

    fun placeBet(amount: Int, color: String)
}