package com.dpkv.color_trading.presentation.screens.homescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dpkv.color_trading.presentation.viewmodels.gameviewmodel.GameViewModel
import com.dpkv.color_trading.presentation.viewmodels.roundViewModel.RoundViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import kotlinx.coroutines.coroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreenUI(
    viewModel: GameViewModel = hiltViewModel(),
    roundViewModel: RoundViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState
) {
    val state = viewModel.uiState.collectAsState().value
    val roundState = roundViewModel.state.collectAsState().value
    // val snackbarHostState = remember { SnackbarHostState() } // Removed local state
    val coroutineScope = rememberCoroutineScope()

    var selectedAmount by remember { mutableStateOf("10") }

    var resultToShow by remember { mutableStateOf("") }
    var showResultDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.roundResultEvent.collect { resultMsg ->
            if (resultMsg.isNotEmpty()) {
                resultToShow = resultMsg
                showResultDialog = true
                roundViewModel.onNewResult(
                    roundId = viewModel.uiState.value.roundId,
                    result = resultMsg
                )
            }
        }
    }

    LaunchedEffect(showResultDialog) {
        if (showResultDialog) {
            delay(2000)
            showResultDialog = false
        }
    }

    LaunchedEffect(Unit) {
        viewModel.betError.collect { errorMsg ->
            coroutineScope {
                val job = launch {
                    snackbarHostState.showSnackbar(
                        message = errorMsg,
                        duration = SnackbarDuration.Indefinite
                    )
                }
                delay(1000)
                job.cancel()
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.connect()
    }

    if (showResultDialog && resultToShow.isNotEmpty()) {
        val isRed = resultToShow.equals("RED", ignoreCase = true)
        val resultColor = if (isRed) Color(0xFFE53935) else Color(0xFF43A047)

        AlertDialog(
            onDismissRequest = { showResultDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Text(
                    text = "Round Result",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = resultToShow.uppercase(),
                        style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                        color = resultColor
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showResultDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("OK")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Color Trading") },
                actions = {
                    val statusText = if (state.isConnected) "Online" else "Offline"
                    val statusColor = if (state.isConnected) Color(0xFF4CAF50) else Color(0xFFE53935)
                    Badge(containerColor = statusColor, modifier = Modifier.padding(end = 16.dp)) {
                        Text(statusText, color = Color.White, modifier = Modifier.padding(4.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (state.roundId == 0) {
                        androidx.compose.material3.CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(16.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Connecting...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text(text = "Round: #${state.roundId}", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${state.secondsLeft}s",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (state.secondsLeft <= 5) Color(0xFFE53935) else MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (state.isBettingOpen) "Betting is Open" else "Betting is Closed",
                            color = if (state.isBettingOpen) Color(0xFF4CAF50) else Color(0xFFE53935),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Select Investment Amount",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = selectedAmount,
                onValueChange = { newValue ->
                    if (newValue.all { it.isDigit() }) selectedAmount = newValue
                },
                label = { Text("Amount (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val amounts = listOf("10", "50", "100", "500", "1000")
                amounts.forEach { amount ->
                    FilterChip(
                        selected = selectedAmount == amount,
                        onClick = { selectedAmount = amount },
                        label = { Text("₹$amount") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {
                        val amountInt = selectedAmount.toIntOrNull() ?: 0
                        if (amountInt > 0) {
                            viewModel.placeBet(amountInt, "RED")
                        } else {
                            snackbarHostState.currentSnackbarData?.dismiss()
                            coroutineScope.launch { snackbarHostState.showSnackbar("Enter a valid amount") }
                        }
                    },
                    enabled = state.isBettingOpen,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE53935),
                        disabledContainerColor = Color(0xFFE53935).copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("RED", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                Button(
                    onClick = {
                        val amountInt = selectedAmount.toIntOrNull() ?: 0
                        if (amountInt > 0) {
                            viewModel.placeBet(amountInt, "GREEN")
                        } else {
                            snackbarHostState.currentSnackbarData?.dismiss()
                            coroutineScope.launch { snackbarHostState.showSnackbar("Enter a valid amount") }
                        }
                    },
                    enabled = state.isBettingOpen,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF43A047),
                        disabledContainerColor = Color(0xFF43A047).copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("GREEN", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Recent Bets Results",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(16.dp))

            val history = roundState.history
            if (history.isNotEmpty()) {
                val topRow = history.take(5)
                val bottomRow = history.drop(5).take(5)

                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        topRow.forEach { item ->
                            RoundResultItem(item.round, item.result.toString())
                        }
                    }
                    if (bottomRow.isNotEmpty()) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            bottomRow.forEach { item ->
                                RoundResultItem(item.round, item.result.toString())
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RoundResultItem(roundId: Int, result: String) {
    val isRed = result.equals("RED", ignoreCase = true)
    val color = if (isRed) Color(0xFFE53935) else Color(0xFF43A047)
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = result.take(1).uppercase(),
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = roundId.toString().takeLast(3),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}