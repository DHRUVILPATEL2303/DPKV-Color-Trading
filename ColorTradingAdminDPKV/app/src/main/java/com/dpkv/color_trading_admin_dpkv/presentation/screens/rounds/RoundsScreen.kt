package com.dpkv.color_trading_admin_dpkv.presentation.screens.rounds

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
import com.dpkv.color_trading_admin_dpkv.presentation.screens.homeScreen.components.PagingStateView
import com.dpkv.color_trading_admin_dpkv.presentation.screens.homeScreen.components.RoundItemCard
import com.dpkv.color_trading_admin_dpkv.presentation.viewModels.roundsViewModel.RoundsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoundsScreen(viewModel: RoundsViewModel) {
    val rounds = viewModel.rounds.collectAsLazyPagingItems()
    val isRefreshing = rounds.loadState.refresh is androidx.paging.LoadState.Loading

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { rounds.refresh() },
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                count = rounds.itemCount,
                contentType = rounds.itemContentType { "rounds" }
            ) { index ->
                rounds[index]?.let { round ->
                    RoundItemCard(round)
                }
            }

            item {
                PagingStateView(rounds.loadState.append, onRetry = { rounds.retry() })
            }
        }
    }
}
