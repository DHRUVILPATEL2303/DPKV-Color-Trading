package com.dpkv.color_trading.data.di

import android.content.Context
import com.dpkv.color_trading.common.BASE_URL
import com.dpkv.color_trading.data.remote.AuthApi
import com.dpkv.color_trading.data.remote.HistoryApi
import com.dpkv.color_trading.data.remote.RoundApi
import com.dpkv.color_trading.data.websocket.WebSocketManager
import com.dpkv.color_trading.datastore.local.TokenManager
import com.dpkv.color_trading.network.AuthInterceptor
import com.dpkv.color_trading.network.SessionManager
import com.dpkv.color_trading.network.TokenAuthenticator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideTokenManager(
        @ApplicationContext context: Context
    ): TokenManager = TokenManager(context)

    @Provides
    @Singleton
    fun provideSessionManager(
        tokenManager: TokenManager,
        webSocketManager: WebSocketManager
    ): SessionManager = SessionManager(tokenManager, webSocketManager)

    @Provides
    @Singleton
    fun provideAuthInterceptor(
        tokenManager: TokenManager
    ): AuthInterceptor = AuthInterceptor(tokenManager)

    @Provides
    @Singleton
    @Named("refreshApi")
    fun provideRefreshApi(): AuthApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideTokenAuthenticator(
        tokenManager: TokenManager,
        @Named("refreshApi") authApi: AuthApi,
        sessionManager: dagger.Lazy<SessionManager>
    ): TokenAuthenticator {
        return TokenAuthenticator(tokenManager, authApi, sessionManager)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(
        retrofit: Retrofit
    ): AuthApi = retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideWebsocketManager(
        tokenManager: TokenManager,
        okHttpClient: OkHttpClient
    ): WebSocketManager {
        return WebSocketManager(
            tokenManager,
            okHttpClient
        )
    }

    @Provides
    @Singleton
    fun provideRoundApi(
        retrofit: Retrofit
    ): RoundApi = retrofit.create(RoundApi::class.java)

    @Provides
    @Singleton
    fun provideHistoryApi(
        retrofit: Retrofit
    ): HistoryApi {
        return retrofit.create(HistoryApi::class.java)
    }
}