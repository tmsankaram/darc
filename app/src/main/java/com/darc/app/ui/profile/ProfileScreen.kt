package com.darc.app.ui.profile

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darc.app.data.entity.PlayerEntity
import com.darc.app.data.entity.StatsEntity

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

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
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "PLAYER PROFILE",
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
                uiState.player?.let { player ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp)
                    ) {
                        // Player Card
                        PlayerCard(
                            player = player,
                            rankColor = Color(uiState.rankColor)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Stats Section
                        Text(
                            text = "STATS",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF888888),
                            letterSpacing = 2.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        uiState.stats?.let { stats ->
                            StatsGrid(stats = stats)
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Progression
                        Text(
                            text = "PROGRESSION",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF888888),
                            letterSpacing = 2.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        ProgressionCard(player = player)
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayerCard(player: PlayerEntity, rankColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2D1B4E),
                        Color(0xFF1A1A2E)
                    )
                )
            )
            .border(
                width = 2.dp,
                color = rankColor,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Rank Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(rankColor.copy(alpha = 0.2f))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "RANK ${player.rank}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = rankColor,
                    letterSpacing = 3.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Name
            Text(
                text = player.name.uppercase(),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 2.sp
            )

            // Title
            Text(
                text = "\"${player.title}\"",
                fontSize = 16.sp,
                color = Color(0xFFBB86FC),
                fontStyle = FontStyle.Italic
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Level and EXP
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${player.level}",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "LEVEL",
                        fontSize = 12.sp,
                        color = Color(0xFF888888),
                        letterSpacing = 1.sp
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${player.currentExp}",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4ECDC4)
                    )
                    Text(
                        text = "EXP",
                        fontSize = 12.sp,
                        color = Color(0xFF888888),
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun StatsGrid(stats: StatsEntity) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatBar(
                label = "STRENGTH",
                value = stats.strength,
                color = Color(0xFFFF6B6B),
                modifier = Modifier.weight(1f)
            )
            StatBar(
                label = "INTELLIGENCE",
                value = stats.intelligence,
                color = Color(0xFF4ECDC4),
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatBar(
                label = "DISCIPLINE",
                value = stats.discipline,
                color = Color(0xFFBB86FC),
                modifier = Modifier.weight(1f)
            )
            StatBar(
                label = "WILLPOWER",
                value = stats.willpower,
                color = Color(0xFFFFE66D),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatBar(
    label: String,
    value: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = value / 100f,
        animationSpec = tween(1000),
        label = "stat_progress"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1A1A1A))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = label,
                    fontSize = 10.sp,
                    color = Color(0xFF888888),
                    letterSpacing = 1.sp
                )
                Text(
                    text = "$value",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = color,
                trackColor = Color(0xFF2D2D2D)
            )
        }
    }
}

@Composable
private fun ProgressionCard(player: PlayerEntity) {
    val progress = player.currentExp.toFloat() / player.expToNextLevel.toFloat()
    val expNeeded = player.expToNextLevel - player.currentExp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1A1A1A))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Level ${player.level}",
                    fontSize = 14.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Level ${player.level + 1}",
                    fontSize = 14.sp,
                    color = Color(0xFF888888)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp)),
                color = Color(0xFFBB86FC),
                trackColor = Color(0xFF2D2D2D)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "$expNeeded EXP to next level",
                fontSize = 12.sp,
                color = Color(0xFF888888)
            )
        }
    }
}
