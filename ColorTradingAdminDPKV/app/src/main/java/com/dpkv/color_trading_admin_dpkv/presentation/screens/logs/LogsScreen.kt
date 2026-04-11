package com.dpkv.color_trading_admin_dpkv.presentation.screens.logs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.dpkv.color_trading_admin_dpkv.presentation.screens.homeScreen.components.LogItemCard
import com.dpkv.color_trading_admin_dpkv.presentation.screens.homeScreen.components.PagingStateView
import com.dpkv.color_trading_admin_dpkv.presentation.viewModels.adminLogViewModel.AdminLogViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogsScreen(viewModel: AdminLogViewModel) {
    val logs = viewModel.adminLogs.collectAsLazyPagingItems()
    val isRefreshing = logs.loadState.refresh is androidx.paging.LoadState.Loading

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { logs.refresh() },
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                count = logs.itemCount,
                contentType = logs.itemContentType { "logs" }
            ) { index ->
                logs[index]?.let { log ->
                    LogItemCard(log)
                }
            }

            item {
                PagingStateView(logs.loadState.append, onRetry = { logs.retry() })
            }
        }
    }
}
