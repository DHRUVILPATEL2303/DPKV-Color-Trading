package com.dpkv.color_trading_admin_dpkv.data.repoImpl.fundRepoImpl

import com.dpkv.color_trading_admin_dpkv.common.ResultState
import com.dpkv.color_trading_admin_dpkv.data.models.FundResponseModel
import com.dpkv.color_trading_admin_dpkv.data.models.FundsRequestModel
import com.dpkv.color_trading_admin_dpkv.data.remote.FundsApi
import com.dpkv.color_trading_admin_dpkv.domain.repo.fundRepo.FundRepository
import com.dpkv.color_trading_admin_dpkv.network.safeApiCall
import javax.inject.Inject

class FundRepositoryImpl @Inject constructor(
    val fundsApi: FundsApi
) : FundRepository {
    override suspend fun addFunds(
        userID: Int,
        amount: Int
    ): ResultState<FundResponseModel> {
        return safeApiCall { fundsApi.addFunds(FundsRequestModel(userID, amount)) }
    }

    override suspend fun deductFunds(
        userID: Int,
        amount: Int
    ): ResultState<FundResponseModel> {

        return safeApiCall {
            fundsApi.deductFunds(FundsRequestModel(userID, amount))
        }
    }
}