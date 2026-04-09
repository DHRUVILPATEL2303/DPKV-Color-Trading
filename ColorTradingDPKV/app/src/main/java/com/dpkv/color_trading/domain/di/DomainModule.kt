package com.dpkv.color_trading.domain.di

import androidx.annotation.IntDef
import com.dpkv.color_trading.data.repoImpl.authRepoImpl.AuthRepositoryImpl
import com.dpkv.color_trading.data.repoImpl.gameRepoImpl.GameRepositoryImpl
import com.dpkv.color_trading.domain.repo.authRepo.AuthRepository
import com.dpkv.color_trading.domain.repo.gameREpo.GameRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DomainModule {

    @Singleton
    @Binds
    abstract fun bindAuthRepo(impl : AuthRepositoryImpl ) : AuthRepository

    @Singleton
    @Binds
    abstract fun bindGameRepo(impl: GameRepositoryImpl) : GameRepository
}