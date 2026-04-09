package com.dpkv.color_trading.data.websocket

import android.util.Log
import com.dpkv.color_trading.datastore.local.TokenManager
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebSocketManager @Inject constructor(
    private val tokenManager: TokenManager,
    private val okHttpClient: OkHttpClient
) {

    private var webSocket: WebSocket? = null

    private val gson = Gson()
    private val parser = WsParser()

    private val _events = MutableSharedFlow<WsEvent>()
    val events = _events.asSharedFlow()

    private val _connectionState = MutableStateFlow(false)
    val connectionState = _connectionState.asStateFlow()

    suspend fun connect() {

        if (webSocket != null) return

        val token = tokenManager.getAccessToken() ?: return

        val request = Request.Builder()
            .url("ws://10.0.2.2:8085/ws?token=$token")
            .build()

        webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {

            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("WS", "Connected")
                _connectionState.tryEmit(true)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                val event = parser.parse(text)
                _events.tryEmit(event)
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("WS", "Closing: $reason")
                _connectionState.tryEmit(false)
                this@WebSocketManager.webSocket = null
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.d("WS", "Error: ${t.message}")
                _connectionState.tryEmit(false)
                this@WebSocketManager.webSocket = null
                reconnect()
            }
        })
    }

    private fun reconnect() {
        Thread {
            Thread.sleep(2000)
            runBlocking { connect() }
        }.start()
    }

    fun placeBet(amount: Int, color: String) {
        val msg = PlaceBetRequest(amount = amount, color = color)
        val json = gson.toJson(msg)
        webSocket?.send(json)
    }

    fun disconnect() {
        webSocket?.close(1000, "Closed by user")
        webSocket = null
        _connectionState.tryEmit(false)
    }
}