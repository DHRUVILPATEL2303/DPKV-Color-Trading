package com.dpkv.color_trading.data.models.refreshtoken

data class RefreshTokenRequestModel(
    val refresh_token : String
)
data class RefreshTokenResponse(
    val access_token : String
)
