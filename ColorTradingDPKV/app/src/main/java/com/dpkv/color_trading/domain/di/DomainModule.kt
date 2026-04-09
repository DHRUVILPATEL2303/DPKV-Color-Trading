package com.dpkv.color_trading.domain.di

import androidx.annotation.IntDef
import com.dpkv.color_trading.data.repoImpl.authRepoImpl.AuthRepositoryImpl
import com.dpkv.color_trading.data.repoImpl.gameRepoImpl.GameRepositoryImpl
import com.dpkv.color_trading.data.repoImpl.historyRepoImpl.HistoryRepositoryImpl
import com.dpkv.color_trading.data.repoImpl.roundRepoImpl.RoundRepositoryImpl
import com.dpkv.color_trading.domain.repo.authRepo.AuthRepository
import com.dpkv.color_trading.domain.repo.gameREpo.GameRepository
import com.dpkv.color_trading.domain.repo.historyRepo.HistoryRepo
import com.dpkv.color_trading.domain.repo.roundRepo.RoundRepository
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

    @Singleton
    @Binds
    abstract fun bindRoundRepo(impl: RoundRepositoryImpl) : RoundRepository

    @Singleton
    @Binds
    abstract fun bindHistoryRepo(impl: HistoryRepositoryImpl) : HistoryRepo
}


