package com.dpkv.color_trading.presentation.viewmodels.rootViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dpkv.color_trading.datastore.local.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class RootViewModel @Inject constructor(
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow<Boolean?>(null)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    init {
        viewModelScope.launch {
            tokenManager.accessToken.collect { token ->
                _isLoggedIn.value = !token.isNullOrEmpty()
            }
        }
    }
}