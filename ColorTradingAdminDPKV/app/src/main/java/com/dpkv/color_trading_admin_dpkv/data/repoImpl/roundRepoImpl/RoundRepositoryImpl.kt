package com.dpkv.color_trading_admin_dpkv.data.repoImpl.roundRepoImpl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.dpkv.color_trading_admin_dpkv.common.ResultState
import com.dpkv.color_trading_admin_dpkv.data.models.RoundModel
import com.dpkv.color_trading_admin_dpkv.data.remote.RoundApi
import com.dpkv.color_trading_admin_dpkv.domain.repo.roundRepo.RoundRepository
import com.dpkv.color_trading_admin_dpkv.network.BasePagingSource
import com.dpkv.color_trading_admin_dpkv.network.safeApiCall
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RoundRepositoryImpl @Inject constructor(
    private val roundApi: RoundApi
) : RoundRepository {
    override suspend fun getAllRounds(limit: Int): ResultState<List<RoundModel>> {
        return try {
            val response = roundApi.getRounds(1, limit)
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true && body.data != null) {
                    ResultState.Success(body.data.items)
                } else {
                    ResultState.Error(body?.error ?: body?.message ?: "Unknown error")
                }
            } else {
                ResultState.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            ResultState.Error(e.message ?: "Something went wrong")
        }
    }

    override fun getPaginatedRounds(): Flow<PagingData<RoundModel>> {
        return Pager(
            config = PagingConfig(pageSize = 20, prefetchDistance = 2),
            pagingSourceFactory = {
                BasePagingSource(
                    apiCall = { page, limit -> roundApi.getRounds(page, limit) },
                    mapResponse = { response -> response.items }
                )
            }
        ).flow
    }
}