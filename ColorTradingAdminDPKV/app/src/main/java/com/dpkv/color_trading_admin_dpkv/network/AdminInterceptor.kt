package com.dpkv.color_trading_admin_dpkv.network

import okhttp3.Interceptor
import okhttp3.Response

class AdminInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("X-Admin-Secret", "ADMINDPKV@123123")
            .build()

        return chain.proceed(request)
    }
}