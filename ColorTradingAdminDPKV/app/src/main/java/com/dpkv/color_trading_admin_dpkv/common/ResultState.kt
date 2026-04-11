package com.dpkv.color_trading_admin_dpkv.common

sealed class ResultState<out T>{
    data class Success<out T>(val data: T) : ResultState<T>()
    data class Error(val message: String) : ResultState<Nothing>()
    object Loading : ResultState<Nothing>()
}