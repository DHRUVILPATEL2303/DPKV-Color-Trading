package com.dpkv.color_trading_admin_dpkv.domain.di

import com.dpkv.color_trading_admin_dpkv.data.repoImpl.fundRepoImpl.FundRepositoryImpl
import com.dpkv.color_trading_admin_dpkv.domain.repo.fundRepo.FundRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DomainModule {

    @Binds
    abstract fun bindFundRepository(
        fundRepositoryImpl: FundRepositoryImpl
    ) : FundRepository

}