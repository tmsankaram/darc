package com.darc.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darc.app.data.entity.PlayerEntity

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToRoutines: () -> Unit = {}
) {
    val player by viewModel.player.collectAsState(initial = null)

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            player?.let { p ->
                // Player Header
                PlayerHeader(player = p)

                Spacer(modifier = Modifier.height(24.dp))

                // Quick Actions
                Text(
                    text = "QUICK ACTIONS",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF888888),
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionCard(
                        icon = Icons.Default.List,
                        label = "Routines",
                        color = Color(0xFFBB86FC),
                        onClick = onNavigateToRoutines,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Quick Stats
                QuickStats(player = p)

                Spacer(modifier = Modifier.height(24.dp))

                // Today's Tasks section placeholder
                Text(
                    text = "TODAY'S TASKS",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF888888),
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Placeholder for tasks
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1A1A1A)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Tasks will appear here",
                        color = Color(0xFF555555)
                    )
                }
            } ?: run {
                // Loading state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFFBB86FC))
                }
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1A1A1A))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
    }
}

@Composable
private fun PlayerHeader(player: PlayerEntity) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "RANK ${player.rank}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFBB86FC),
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = player.name.uppercase(),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "\"${player.title}\"",
                    fontSize = 14.sp,
                    color = Color(0xFF888888),
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }

            // Level badge
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF2D1B4E)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${player.level}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "LVL",
                        fontSize = 10.sp,
                        color = Color(0xFF888888),
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // EXP Bar
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "EXP",
                    fontSize = 12.sp,
                    color = Color(0xFF888888)
                )
                Text(
                    text = "${player.currentExp} / ${player.expToNextLevel}",
                    fontSize = 12.sp,
                    color = Color(0xFF888888)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { player.currentExp.toFloat() / player.expToNextLevel.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = Color(0xFFBB86FC),
                trackColor = Color(0xFF2D2D2D)
            )
        }
    }
}

@Composable
private fun QuickStats(player: PlayerEntity) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatCard(label = "STREAK", value = "0", color = Color(0xFFFF6B6B))
        StatCard(label = "TASKS", value = "0/0", color = Color(0xFF4ECDC4))
        StatCard(label = "TOKENS", value = "0", color = Color(0xFFFFE66D))
    }
}

@Composable
private fun StatCard(label: String, value: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1A1A1A))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = label,
                fontSize = 10.sp,
                color = Color(0xFF666666),
                letterSpacing = 1.sp
            )
        }
    }
}
