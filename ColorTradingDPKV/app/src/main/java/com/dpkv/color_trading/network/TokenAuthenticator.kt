package com.dpkv.color_trading.network

import com.dpkv.color_trading.data.models.refreshtoken.RefreshTokenRequestModel
import com.dpkv.color_trading.data.remote.AuthApi
import com.dpkv.color_trading.datastore.local.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val tokenManager: TokenManager,
    private val authApi: AuthApi,
    private val sessionManager: SessionManager
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {

        if (responseCount(response) >= 2) return null

        val refreshToken = runBlocking {
            tokenManager.getRefreshToken()
        } ?: return null

        val refreshResponse = try {
            runBlocking {
                authApi.refreshToken(
                    RefreshTokenRequestModel(refresh_token = refreshToken)
                )
            }
        } catch (e: Exception) {
            return null
        }

        if (!refreshResponse.isSuccessful) return null

        val body = refreshResponse.body() ?: return null

        if (!body.success) {
            runBlocking { tokenManager.clearTokens() }
            runBlocking {
                sessionManager.logout()
            }
            return null
        }



        val newAccessToken = body.data?.access_token ?: return null

        runBlocking {
            tokenManager.saveAccessToken(newAccessToken)
        }

        return response.request.newBuilder()
            .header("Authorization", "Bearer $newAccessToken")
            .build()
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var res = response.priorResponse
        while (res != null) {
            count++
            res = res.priorResponse
        }
        return count
    }
}