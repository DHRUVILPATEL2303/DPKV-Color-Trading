package com.dpkv.color_trading.data.models.transactions

data class TransactionResponseModel(
    val id: Int,
    val amount: Long,
    val type: String,
    val created_at: String
)
