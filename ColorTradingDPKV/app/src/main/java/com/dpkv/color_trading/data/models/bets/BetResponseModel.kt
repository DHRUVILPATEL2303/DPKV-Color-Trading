package com.dpkv.color_trading.data.models.bets

data class BetResponseModel(
    val round_number: Int,
    val amount: Long,
    val color: String,
    val result: String,
    val created_at: String
)
