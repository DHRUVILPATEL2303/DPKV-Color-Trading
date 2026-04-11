package com.dpkv.color_trading_admin_dpkv.presentation.viewModels.roundsViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dpkv.color_trading_admin_dpkv.data.models.RoundModel
import com.dpkv.color_trading_admin_dpkv.domain.useCase.roundUseCase.GetLastRoundsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import androidx.paging.cachedIn
import com.dpkv.color_trading_admin_dpkv.domain.useCase.roundUseCase.GetPaginatedRoundsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RoundsViewModel @Inject constructor(
    private val getPaginatedRoundsUseCase: GetPaginatedRoundsUseCase
) : ViewModel() {

    val rounds = getPaginatedRoundsUseCase()
        .cachedIn(viewModelScope)
}

data class CommonRoundState<T>(
    val isLoading : Boolean = false,
    val error : String? =null,
    val data : T?= null

)