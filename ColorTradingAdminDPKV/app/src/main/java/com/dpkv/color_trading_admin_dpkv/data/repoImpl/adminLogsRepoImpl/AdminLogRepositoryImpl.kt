package com.dpkv.color_trading_admin_dpkv.data.repoImpl.adminLogsRepoImpl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.dpkv.color_trading_admin_dpkv.common.ResultState
import com.dpkv.color_trading_admin_dpkv.data.models.AdminLogModel
import com.dpkv.color_trading_admin_dpkv.data.models.AdminLogsResponse
import com.dpkv.color_trading_admin_dpkv.data.remote.AdminLogApi
import com.dpkv.color_trading_admin_dpkv.domain.repo.adminLog.AdminLogRepository
import com.dpkv.color_trading_admin_dpkv.network.BasePagingSource
import com.dpkv.color_trading_admin_dpkv.network.safeApiCall
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AdminLogRepositoryImpl @Inject constructor(
    private val adminLogApi: AdminLogApi
) : AdminLogRepository {
    override suspend fun getAllAdminLogs(
        page: Int,
        limit: Int
    ): ResultState<AdminLogsResponse> {
        return safeApiCall {
            adminLogApi.getAdminLogs(page, limit)

        }
    }

    override fun getPaginatedAdminLogs(): Flow<PagingData<AdminLogModel>> {
        return Pager(
            config = PagingConfig(pageSize = 20, prefetchDistance = 2),
            pagingSourceFactory = {
                BasePagingSource(
                    apiCall = { page, limit -> adminLogApi.getAdminLogs(page, limit) },
                    mapResponse = { response -> response.items }
                )
            }
        ).flow
    }
}