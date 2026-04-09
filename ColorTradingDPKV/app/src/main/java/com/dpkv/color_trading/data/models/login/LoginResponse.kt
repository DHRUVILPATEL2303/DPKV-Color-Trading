package com.dpkv.color_trading.data.models.login

data class LoginResponse(
    val accessToken : String,
    val refreshToken : String,
    val balance : Int,
    val email : String,
    val user_id : Int

)
/*
*  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6InBhMTRAZ21haWwuY29tIiwiZXhwIjoxNzc1ODEyMjY4LCJ1c2VyX2lkIjo0fQ.tIrphOaWY6nCsxXm55ZFOoso5_GLGG0pN4XMaORko08",
        "balance": 0,
        "email": "pa14@gmail.com",
        "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE3NzYzMzA2NjgsInVzZXJfaWQiOjR9.ZlDLOWmDfnYC5OaTKMPKyD7Mgs7UkmSIXu22RbdiTX0",
        "user_id": 4*/
