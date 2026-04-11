package com.dpkv.color_trading_admin_dpkv.data.models

import com.google.gson.annotations.SerializedName


data class AdminLogModel(
    val id: Int,

    @SerializedName("admin_id")
    val adminId: Int,

    @SerializedName("user_id")
    val userId: Int,

    val amount: Int,

    val action: String,

    @SerializedName("created_at")
    val createdAt: String
)


data class AdminLogsResponse(
    val items: List<AdminLogModel>,
    val page: Int,
    val limit: Int,
    val total: Int
)