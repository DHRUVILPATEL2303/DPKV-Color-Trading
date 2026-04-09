package com.dpkv.color_trading.network

import com.dpkv.color_trading.datastore.local.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()

        if (request.header("No-Auth") == "true") {
            return chain.proceed(request)
        }

        val token = runBlocking {
            tokenManager.getAccessToken()
        }

        val newRequest = request.newBuilder()

        token?.let {
            newRequest.addHeader("Authorization", "Bearer $it")
        }

        return chain.proceed(newRequest.build())
    }
}