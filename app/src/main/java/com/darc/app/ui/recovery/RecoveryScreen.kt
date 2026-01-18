package com.darc.app.ui.recovery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecoveryScreen(
    viewModel: RecoveryViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onRecoverySuccess: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    // Navigate back on success
    LaunchedEffect(uiState.recoverySuccess) {
        if (uiState.recoverySuccess) {
            onRecoverySuccess()
            viewModel.dismissSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D0D0D),
                        Color(0xFF1A1A2E)
                    )
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "RECOVERY",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 2.sp
                )
            }

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFFBB86FC))
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(32.dp))

                    // Recovery Icon
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color(0xFF2D1B4E)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Recovery",
                            tint = Color(0xFFFFE66D),
                            modifier = Modifier.size(64.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Title
                    Text(
                        text = "REVIVAL TOKENS",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Token count
                    Text(
                        text = "${uiState.streak?.revivalTokens ?: 0}",
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFE66D)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "tokens available",
                        fontSize = 14.sp,
                        color = Color(0xFF888888)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Info Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1A1A1A))
                            .padding(16.dp)
                    ) {
                        Column {
                            Text(
                                text = "HOW REVIVAL TOKENS WORK",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF888888),
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            TokenInfoRow("Earn 1 token every 7-day streak")
                            TokenInfoRow("Use to restore your streak after missing a day")
                            TokenInfoRow("Prevents EXP penalty when used")
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Streak info
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1A1A1A))
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${uiState.streak?.currentStreak ?: 0}",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFF6B6B)
                                )
                                Text(
                                    text = "CURRENT",
                                    fontSize = 10.sp,
                                    color = Color(0xFF888888),
                                    letterSpacing = 1.sp
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${uiState.streak?.bestStreak ?: 0}",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4ECDC4)
                                )
                                Text(
                                    text = "BEST",
                                    fontSize = 10.sp,
                                    color = Color(0xFF888888),
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Use token button
                    Button(
                        onClick = { viewModel.showConfirmDialog() },
                        enabled = uiState.canRecover,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFE66D),
                            disabledContainerColor = Color(0xFF333333)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (uiState.canRecover) "USE REVIVAL TOKEN" else "NO TOKENS AVAILABLE",
                            color = if (uiState.canRecover) Color.Black else Color(0xFF888888),
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        // Confirmation Dialog
        if (uiState.showConfirmDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.hideDialog() },
                containerColor = Color(0xFF1A1A1A),
                title = {
                    Text(
                        text = "USE REVIVAL TOKEN?",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = "This will restore your streak and prevent EXP penalty. You have ${uiState.streak?.revivalTokens ?: 0} token(s) remaining.",
                        color = Color(0xFF888888)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.useRevivalToken() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFE66D))
                    ) {
                        Text("CONFIRM", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.hideDialog() }) {
                        Text("CANCEL", color = Color(0xFF888888))
                    }
                }
            )
        }
    }
}

@Composable
private fun TokenInfoRow(text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color(0xFFFFE66D))
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            color = Color(0xFFCCCCCC)
        )
    }
}
