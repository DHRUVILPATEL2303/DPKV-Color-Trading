package com.dpkv.color_trading_admin_dpkv.domain.repo.fundRepo

import com.dpkv.color_trading_admin_dpkv.common.ResultState
import com.dpkv.color_trading_admin_dpkv.data.models.FundResponseModel

interface FundRepository {

    suspend fun addFunds(userID : Int,amount : Int) : ResultState<FundResponseModel>

    suspend fun deductFunds(userID: Int,amount: Int) : ResultState<FundResponseModel>
}