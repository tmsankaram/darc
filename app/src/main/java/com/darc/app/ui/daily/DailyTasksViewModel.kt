package com.darc.app.ui.daily

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darc.app.data.entity.LogEntity
import com.darc.app.data.entity.RoutineEntity
import com.darc.app.data.entity.TaskEntity
import com.darc.app.data.repository.LogRepository
import com.darc.app.data.repository.RoutineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TaskWithStatus(
    val task: TaskEntity,
    val routine: RoutineEntity?,
    val isCompletedToday: Boolean
)

data class DailyTasksUiState(
    val tasks: List<TaskWithStatus> = emptyList(),
    val completedCount: Int = 0,
    val totalCount: Int = 0,
    val isLoading: Boolean = true,
    val showCompletionDialog: Boolean = false,
    val selectedTask: TaskEntity? = null
)

@HiltViewModel
class DailyTasksViewModel @Inject constructor(
    private val routineRepository: RoutineRepository,
    private val logRepository: LogRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DailyTasksUiState())
    val uiState: StateFlow<DailyTasksUiState> = _uiState.asStateFlow()

    init {
        loadDailyTasks()
    }

    private fun loadDailyTasks() {
        viewModelScope.launch {
            combine(
                routineRepository.getAllTasks(),
                routineRepository.getAllRoutines(),
                logRepository.getTodayLogs()
            ) { tasks, routines, todayLogs ->
                // Filter for daily tasks (or all active tasks for now)
                val dailyTasks = tasks.filter { it.type == "Daily" || it.type == "Quest" }
                val completedTaskIds = todayLogs.filter { it.isCompleted }.map { it.taskId }.toSet()

                val tasksWithStatus = dailyTasks.map { task ->
                    TaskWithStatus(
                        task = task,
                        routine = routines.find { it.id == task.routineId },
                        isCompletedToday = completedTaskIds.contains(task.id)
                    )
                }

                DailyTasksUiState(
                    tasks = tasksWithStatus,
                    completedCount = tasksWithStatus.count { it.isCompletedToday },
                    totalCount = tasksWithStatus.size,
                    isLoading = false,
                    showCompletionDialog = _uiState.value.showCompletionDialog,
                    selectedTask = _uiState.value.selectedTask
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun showCompleteDialog(task: TaskEntity) {
        _uiState.value = _uiState.value.copy(showCompletionDialog = true, selectedTask = task)
    }

    fun hideDialog() {
        _uiState.value = _uiState.value.copy(showCompletionDialog = false, selectedTask = null)
    }

    fun completeTask(task: TaskEntity, notes: String = "") {
        viewModelScope.launch {
            logRepository.completeTask(task, notes)
            hideDialog()
        }
    }

    fun skipTask(task: TaskEntity, notes: String = "") {
        viewModelScope.launch {
            logRepository.skipTask(task, notes)
            hideDialog()
        }
    }
}
