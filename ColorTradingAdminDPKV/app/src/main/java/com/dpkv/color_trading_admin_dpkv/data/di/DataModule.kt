package com.dpkv.color_trading_admin_dpkv.data.di

import androidx.annotation.Size
import androidx.core.view.WindowInsetsCompat
import com.dpkv.color_trading_admin_dpkv.common.BASE_URL
import com.dpkv.color_trading_admin_dpkv.data.remote.AdminLogApi
import com.dpkv.color_trading_admin_dpkv.data.remote.FundsApi
import com.dpkv.color_trading_admin_dpkv.data.remote.RoundApi
import com.dpkv.color_trading_admin_dpkv.network.AdminInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ) : Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AdminInterceptor())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideFundApi(
        retrofit: Retrofit
    ) : FundsApi {
       return retrofit.create<FundsApi>(FundsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAdminLogApi(
        retrofit: Retrofit
    ): AdminLogApi {
       return  retrofit.create<AdminLogApi>(AdminLogApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRoundApi(
        retrofit: Retrofit
    ): RoundApi {
        return retrofit.create<RoundApi>(RoundApi::class.java)
    }
}