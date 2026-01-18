package com.darc.app.ui.daily

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darc.app.data.entity.TaskEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyTasksScreen(
    viewModel: DailyTasksViewModel = hiltViewModel(),
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
                Column {
                    Text(
                        text = "TODAY'S TASKS",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "${uiState.completedCount}/${uiState.totalCount} completed",
                        fontSize = 14.sp,
                        color = Color(0xFF888888)
                    )
                }
            }

            // Progress bar
            LinearProgressIndicator(
                progress = {
                    if (uiState.totalCount > 0) uiState.completedCount.toFloat() / uiState.totalCount.toFloat()
                    else 0f
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .padding(horizontal = 16.dp),
                color = Color(0xFF4ECDC4),
                trackColor = Color(0xFF2D2D2D)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFFBB86FC))
                }
            } else if (uiState.tasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No tasks for today",
                            fontSize = 18.sp,
                            color = Color(0xFF888888)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Add some daily tasks to your routines",
                            fontSize = 14.sp,
                            color = Color(0xFF555555)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(uiState.tasks) { taskWithStatus ->
                        DailyTaskCard(
                            taskWithStatus = taskWithStatus,
                            onClick = {
                                if (!taskWithStatus.isCompletedToday) {
                                    viewModel.showCompleteDialog(taskWithStatus.task)
                                }
                            }
                        )
                    }
                }
            }
        }

        // Completion Dialog
        if (uiState.showCompletionDialog && uiState.selectedTask != null) {
            TaskCompletionDialog(
                task = uiState.selectedTask!!,
                onComplete = { notes -> viewModel.completeTask(uiState.selectedTask!!, notes) },
                onSkip = { notes -> viewModel.skipTask(uiState.selectedTask!!, notes) },
                onDismiss = { viewModel.hideDialog() }
            )
        }
    }
}

@Composable
private fun DailyTaskCard(
    taskWithStatus: TaskWithStatus,
    onClick: () -> Unit
) {
    val isCompleted = taskWithStatus.isCompletedToday
    val difficultyColor = when {
        taskWithStatus.task.difficulty <= 3 -> Color(0xFF4ECDC4)
        taskWithStatus.task.difficulty <= 6 -> Color(0xFFFFE66D)
        taskWithStatus.task.difficulty <= 8 -> Color(0xFFFFA500)
        else -> Color(0xFFFF6B6B)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isCompleted) Color(0xFF1A2A1A) else Color(0xFF1A1A1A))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = taskWithStatus.task.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCompleted) Color(0xFF4ECDC4) else Color.White,
                        textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    taskWithStatus.routine?.let {
                        Text(
                            text = it.name,
                            fontSize = 12.sp,
                            color = Color(0xFF666666)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = "LV ${taskWithStatus.task.difficulty}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = difficultyColor
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "+${taskWithStatus.task.expReward} EXP",
                        fontSize = 12.sp,
                        color = Color(0xFF4ECDC4)
                    )
                }
            }

            if (isCompleted) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF4ECDC4)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskCompletionDialog(
    task: TaskEntity,
    onComplete: (String) -> Unit,
    onSkip: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var notes by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1A1A1A)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "COMPLETE TASK",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = task.title,
                fontSize = 18.sp,
                color = Color(0xFFBB86FC)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Target: ${task.target}",
                fontSize = 14.sp,
                color = Color(0xFF888888)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (optional)", color = Color(0xFF888888)) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFFBB86FC),
                    unfocusedBorderColor = Color(0xFF333333),
                    cursorColor = Color(0xFFBB86FC)
                ),
                shape = RoundedCornerShape(12.dp),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { onSkip(notes) },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFFF6B6B)
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF6B6B)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SKIP", letterSpacing = 2.sp)
                }

                Button(
                    onClick = { onComplete(notes) },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4ECDC4)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("DONE", color = Color.Black, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
