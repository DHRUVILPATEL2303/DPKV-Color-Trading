package com.dpkv.color_trading.data.models.profile

data class ProfileResponseModel(
    val user_id: Int,
    val email: String,
    val balance: Int?=0
)