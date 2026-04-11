package com.dpkv.color_trading_admin_dpkv.domain.repo.roundRepo

import androidx.paging.PagingData
import com.dpkv.color_trading_admin_dpkv.common.ResultState
import com.dpkv.color_trading_admin_dpkv.data.models.RoundModel
import kotlinx.coroutines.flow.Flow

interface RoundRepository {

    suspend fun getAllRounds(
        limit: Int
    ): ResultState<List<RoundModel>>

    fun getPaginatedRounds(): Flow<PagingData<RoundModel>>
}