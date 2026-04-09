package com.dpkv.color_trading.data.websocket

import com.google.gson.Gson
import com.google.gson.JsonParser

class WsParser {

    private val gson = Gson()

    fun parse(message: String): WsEvent {
        return try {

            val cleanMessage = if (message.startsWith("\"")) {
                gson.fromJson(message, String::class.java)
            } else {
                message
            }

            val jsonObject = JsonParser().parse(cleanMessage).asJsonObject
            val type = jsonObject.get("type")?.asString

            // No 'type' field — treat as a server error/response message
            if (type == null) {
                val errorMsg = jsonObject.get("message")?.asString
                return if (errorMsg != null) WsEvent.BetError(errorMsg) else WsEvent.Unknown(cleanMessage)
            }

            when (type) {

                "TIMER" -> {
                    val data = gson.fromJson(cleanMessage, TimerMessage::class.java)
                    WsEvent.Timer(data)
                }

                "RESULT" -> {
                    val data = gson.fromJson(cleanMessage, ResultMessage::class.java)
                    WsEvent.Result(data)
                }

                "ROUND_START" -> {
                    val data = gson.fromJson(cleanMessage, RoundStartMessage::class.java)
                    WsEvent.RoundStart(data)
                }

                "BETTING_CLOSED" -> {
                    WsEvent.BettingClosed("BETTING_CLOSED")
                }

                else -> WsEvent.Unknown(cleanMessage)
            }

        } catch (e: Exception) {
            WsEvent.Unknown(message)
        }
    }
}