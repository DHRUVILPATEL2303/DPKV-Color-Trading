package com.dpkv.color_trading_admin_dpkv.data.response


data class ApiResponse<T>(
    val success: Boolean,
    val message: String?,
    val data: T?,
    val error: String?
)