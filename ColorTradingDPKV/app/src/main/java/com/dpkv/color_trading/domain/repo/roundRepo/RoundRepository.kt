package com.dpkv.color_trading.domain.repo.roundRepo

import com.dpkv.color_trading.common.ResultState
import com.dpkv.color_trading.data.models.roundshistory.RoundHistoryResponseModel

interface RoundRepository {

    suspend fun getLast10RoundsHistory() : ResultState<List<RoundHistoryResponseModel>>
}