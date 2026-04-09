package com.dpkv.color_trading.data.websocket

import com.google.gson.Gson

class WsParser {

    private val gson = Gson()

    fun parse(message: String): WsEvent {
        return try {
            val base = gson.fromJson(message, WsBaseMessage::class.java)

            when (base.type) {
                "TIMER" -> {
                    val data = gson.fromJson(message, TimerMessage::class.java)
                    WsEvent.Timer(data)
                }

                "RESULT" -> {
                    val data = gson.fromJson(message, ResultMessage::class.java)
                    WsEvent.Result(data)
                }

                "ROUND_START" -> {
                    val data = gson.fromJson(message, RoundStartMessage::class.java)
                    WsEvent.RoundStart(data)
                }
                "BETTING_CLOSED" -> WsEvent.BettingClosed("BETTING_CLOSED")
                else -> WsEvent.Unknown(message)
            }

        } catch (e: Exception) {
            WsEvent.Unknown(message)
        }
    }
}