package com.dpkv.color_trading.data.websocket

import com.google.gson.Gson

data class PlaceBetRequest(
    val type: String = "PLACE_BET",
    val amount: Int,
    val color: String
)

