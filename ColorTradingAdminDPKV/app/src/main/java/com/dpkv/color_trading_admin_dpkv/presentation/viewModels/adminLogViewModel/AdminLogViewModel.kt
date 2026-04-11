package com.dpkv.color_trading_admin_dpkv.presentation.viewModels.adminLogViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.dpkv.color_trading_admin_dpkv.domain.useCase.adminLogUseCase.GetPaginatedAdminLogsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AdminLogViewModel @Inject constructor(
    private val getPaginatedAdminLogsUseCase: GetPaginatedAdminLogsUseCase
) : ViewModel() {

    val adminLogs = getPaginatedAdminLogsUseCase()
        .cachedIn(viewModelScope)
}
