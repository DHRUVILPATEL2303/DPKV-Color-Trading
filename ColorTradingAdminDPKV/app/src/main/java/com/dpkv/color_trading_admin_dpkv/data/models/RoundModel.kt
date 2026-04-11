package com.dpkv.color_trading_admin_dpkv.data.models

import com.google.gson.annotations.SerializedName

data class RoundModel(

    @SerializedName("round_number")
    val roundNumber: Int,
    val result: String, // RED / GREEN

    val status: String, // COMPLETED / OPEN

    @SerializedName("created_at")
    val createdAt: String
)

data class RoundsResponse(
    val items: List<RoundModel>,
    val page: Int,
    val limit: Int,
    val total: Int
)