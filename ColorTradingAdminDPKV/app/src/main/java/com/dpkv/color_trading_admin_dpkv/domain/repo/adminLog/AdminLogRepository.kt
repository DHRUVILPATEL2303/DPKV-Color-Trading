package com.dpkv.color_trading_admin_dpkv.domain.repo.adminLog

import androidx.paging.PagingData
import com.dpkv.color_trading_admin_dpkv.common.ResultState
import com.dpkv.color_trading_admin_dpkv.data.models.AdminLogModel
import com.dpkv.color_trading_admin_dpkv.data.models.AdminLogsResponse
import kotlinx.coroutines.flow.Flow

interface AdminLogRepository {

    suspend fun getAllAdminLogs(
        page: Int,
        limit: Int
    ): ResultState<AdminLogsResponse>

    fun getPaginatedAdminLogs(): Flow<PagingData<AdminLogModel>>
}