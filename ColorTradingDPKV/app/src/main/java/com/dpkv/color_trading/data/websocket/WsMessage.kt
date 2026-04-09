package com.dpkv.color_trading.data.websocket

data class WsBaseMessage(
    val type: String?
)
data class TimerMessage(
    val type: String,
    val seconds_left: Int,
    val round_id: Int
)
data class ResultMessage(
    val type: String,
    val result: String,
    val round_id: Int
)

data class RoundStartMessage(
    val type: String,
    val round_id: Int
)

data class ErrorMessage(
    val message: String
)