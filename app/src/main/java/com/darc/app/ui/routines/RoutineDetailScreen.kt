package com.darc.app.ui.routines

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darc.app.data.entity.TaskEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineDetailScreen(
    viewModel: RoutineDetailViewModel = hiltViewModel(),
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
                        text = uiState.routine?.name?.uppercase() ?: "ROUTINE",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 2.sp
                    )
                    if (uiState.routine?.description?.isNotEmpty() == true) {
                        Text(
                            text = uiState.routine!!.description,
                            fontSize = 14.sp,
                            color = Color(0xFF888888)
                        )
                    }
                }
            }

            // Tasks Section Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TASKS",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF888888),
                    letterSpacing = 2.sp
                )
                Text(
                    text = "${uiState.tasks.size} task${if (uiState.tasks.size != 1) "s" else ""}",
                    fontSize = 12.sp,
                    color = Color(0xFF555555)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

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
                            text = "No tasks yet",
                            fontSize = 18.sp,
                            color = Color(0xFF888888)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Add tasks to this routine",
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
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(uiState.tasks) { task ->
                        TaskCard(
                            task = task,
                            onEdit = { viewModel.showEditTaskSheet(task) },
                            onDelete = { viewModel.deleteTask(task.id) }
                        )
                    }
                }
            }
        }

        // FAB
        FloatingActionButton(
            onClick = { viewModel.showAddTaskSheet() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = Color(0xFFBB86FC)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Task", tint = Color.White)
        }

        // Task Sheet
        if (uiState.showTaskSheet) {
            TaskSheet(
                editingTask = uiState.editingTask,
                onDismiss = { viewModel.hideTaskSheet() },
                onSave = { title, type, difficulty, target ->
                    if (uiState.editingTask != null) {
                        viewModel.updateTask(uiState.editingTask!!, title, type, difficulty, target)
                    } else {
                        viewModel.addTask(title, type, difficulty, target)
                    }
                }
            )
        }
    }
}

@Composable
private fun TaskCard(
    task: TaskEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val difficultyColor = when {
        task.difficulty <= 3 -> Color(0xFF4ECDC4)
        task.difficulty <= 6 -> Color(0xFFFFE66D)
        task.difficulty <= 8 -> Color(0xFFFFA500)
        else -> Color(0xFFFF6B6B)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1A1A1A))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = task.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF2D2D2D))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = task.type,
                            fontSize = 10.sp,
                            color = Color(0xFFBB86FC)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Difficulty
                    Text(
                        text = "LV ${task.difficulty}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = difficultyColor
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = task.target,
                        fontSize = 12.sp,
                        color = Color(0xFF888888)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "+${task.expReward} EXP",
                        fontSize = 12.sp,
                        color = Color(0xFF4ECDC4)
                    )
                }
            }

            Row {
                IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color(0xFF888888),
                        modifier = Modifier.size(18.dp)
                    )
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color(0xFFFF6B6B),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskSheet(
    editingTask: TaskEntity?,
    onDismiss: () -> Unit,
    onSave: (String, String, Int, String) -> Unit
) {
    var title by remember { mutableStateOf(editingTask?.title ?: "") }
    var type by remember { mutableStateOf(editingTask?.type ?: "Daily") }
    var difficulty by remember { mutableStateOf(editingTask?.difficulty?.toFloat() ?: 5f) }
    var target by remember { mutableStateOf(editingTask?.target ?: "") }

    val types = listOf("Daily", "Weekly", "Quest")

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
                text = if (editingTask != null) "EDIT TASK" else "NEW TASK",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Title
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Task Title", color = Color(0xFF888888)) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFFBB86FC),
                    unfocusedBorderColor = Color(0xFF333333),
                    cursorColor = Color(0xFFBB86FC)
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Type selector
            Text(
                text = "TYPE",
                fontSize = 12.sp,
                color = Color(0xFF888888),
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                types.forEach { t ->
                    FilterChip(
                        selected = type == t,
                        onClick = { type = t },
                        label = { Text(t) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFBB86FC),
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFF2D2D2D),
                            labelColor = Color(0xFF888888)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Difficulty slider
            Text(
                text = "DIFFICULTY: ${difficulty.toInt()}",
                fontSize = 12.sp,
                color = Color(0xFF888888),
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Slider(
                value = difficulty,
                onValueChange = { difficulty = it },
                valueRange = 1f..10f,
                steps = 8,
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFFBB86FC),
                    activeTrackColor = Color(0xFFBB86FC),
                    inactiveTrackColor = Color(0xFF333333)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Target
            OutlinedTextField(
                value = target,
                onValueChange = { target = it },
                label = { Text("Target (e.g., 30 mins, 5 sets)", color = Color(0xFF888888)) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFFBB86FC),
                    unfocusedBorderColor = Color(0xFF333333),
                    cursorColor = Color(0xFFBB86FC)
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { onSave(title, type, difficulty.toInt(), target) },
                enabled = title.isNotBlank() && target.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFBB86FC),
                    disabledContainerColor = Color(0xFF333333)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (editingTask != null) "SAVE CHANGES" else "ADD TASK",
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
