package com.dpkv.color_trading.data.repoImpl.roundRepoImpl

import com.dpkv.color_trading.common.ResultState
import com.dpkv.color_trading.common.safeApiCall
import com.dpkv.color_trading.data.models.roundshistory.RoundHistoryResponseModel
import com.dpkv.color_trading.data.remote.RoundApi
import com.dpkv.color_trading.domain.repo.roundRepo.RoundRepository
import javax.inject.Inject

class RoundRepositoryImpl @Inject constructor(
    val roundApi: RoundApi
) : RoundRepository {
    override suspend fun getLast10RoundsHistory(): ResultState<List<RoundHistoryResponseModel>> {
        return safeApiCall {
            roundApi.getHistory()
        }


    }


}