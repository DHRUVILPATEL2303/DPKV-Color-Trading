package com.dpkv.color_trading_admin_dpkv.domain.di

import com.dpkv.color_trading_admin_dpkv.data.repoImpl.adminLogsRepoImpl.AdminLogRepositoryImpl
import com.dpkv.color_trading_admin_dpkv.data.repoImpl.fundRepoImpl.FundRepositoryImpl
import com.dpkv.color_trading_admin_dpkv.data.repoImpl.roundRepoImpl.RoundRepositoryImpl
import com.dpkv.color_trading_admin_dpkv.domain.repo.adminLog.AdminLogRepository
import com.dpkv.color_trading_admin_dpkv.domain.repo.fundRepo.FundRepository
import com.dpkv.color_trading_admin_dpkv.domain.repo.roundRepo.RoundRepository
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

    @Binds
    abstract fun bindRoundRepository(
        roundRepositoryImpl: RoundRepositoryImpl
    ) : RoundRepository

    @Binds
    abstract fun bindAdminLogsRepository(
        adminLogRepositoryImpl: AdminLogRepositoryImpl
    ): AdminLogRepository

}