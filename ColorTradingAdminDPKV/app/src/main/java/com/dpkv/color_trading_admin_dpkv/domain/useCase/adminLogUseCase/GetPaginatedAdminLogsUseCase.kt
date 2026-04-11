package com.dpkv.color_trading_admin_dpkv.domain.useCase.adminLogUseCase

import com.dpkv.color_trading_admin_dpkv.domain.repo.adminLog.AdminLogRepository
import javax.inject.Inject

class GetPaginatedAdminLogsUseCase @Inject constructor(
    private val adminLogRepository: AdminLogRepository
) {
    operator fun invoke() = adminLogRepository.getPaginatedAdminLogs()
}
