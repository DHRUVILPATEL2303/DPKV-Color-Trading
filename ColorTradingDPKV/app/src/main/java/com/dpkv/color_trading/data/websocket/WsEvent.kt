package com.dpkv.color_trading.data.websocket

sealed class WsEvent {
    data class Timer(val data: TimerMessage) : WsEvent()
    data class Result(val data: ResultMessage) : WsEvent()
    data class RoundStart(val data: RoundStartMessage) : WsEvent()
    data class Unknown(val raw: String) : WsEvent()
    data class BettingClosed(val type: String) : WsEvent()
}

