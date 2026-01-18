package com.darc.app.ui.routines

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darc.app.data.entity.RoutineEntity
import com.darc.app.data.entity.TaskEntity
import com.darc.app.data.repository.RoutineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RoutineDetailUiState(
    val routine: RoutineEntity? = null,
    val tasks: List<TaskEntity> = emptyList(),
    val isLoading: Boolean = true,
    val showTaskSheet: Boolean = false,
    val editingTask: TaskEntity? = null
)

@HiltViewModel
class RoutineDetailViewModel @Inject constructor(
    private val routineRepository: RoutineRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val routineId: Long = savedStateHandle.get<Long>("routineId") ?: 0L

    private val _uiState = MutableStateFlow(RoutineDetailUiState())
    val uiState: StateFlow<RoutineDetailUiState> = _uiState.asStateFlow()

    init {
        loadRoutineDetails()
    }

    private fun loadRoutineDetails() {
        viewModelScope.launch {
            combine(
                routineRepository.getRoutineById(routineId),
                routineRepository.getTasksForRoutine(routineId)
            ) { routine, tasks ->
                RoutineDetailUiState(
                    routine = routine,
                    tasks = tasks,
                    isLoading = false,
                    showTaskSheet = _uiState.value.showTaskSheet,
                    editingTask = _uiState.value.editingTask
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun showAddTaskSheet() {
        _uiState.value = _uiState.value.copy(showTaskSheet = true, editingTask = null)
    }

    fun showEditTaskSheet(task: TaskEntity) {
        _uiState.value = _uiState.value.copy(showTaskSheet = true, editingTask = task)
    }

    fun hideTaskSheet() {
        _uiState.value = _uiState.value.copy(showTaskSheet = false, editingTask = null)
    }

    fun addTask(
        title: String,
        type: String,
        difficulty: Int,
        target: String
    ) {
        viewModelScope.launch {
            val expReward = calculateExpReward(difficulty)
            routineRepository.addTask(
                routineId = routineId,
                title = title,
                type = type,
                difficulty = difficulty,
                target = target,
                expReward = expReward
            )
            hideTaskSheet()
        }
    }

    fun updateTask(
        task: TaskEntity,
        title: String,
        type: String,
        difficulty: Int,
        target: String
    ) {
        viewModelScope.launch {
            val expReward = calculateExpReward(difficulty)
            routineRepository.updateTask(
                task.copy(
                    title = title,
                    type = type,
                    difficulty = difficulty,
                    target = target,
                    expReward = expReward
                )
            )
            hideTaskSheet()
        }
    }

    fun deleteTask(id: Long) {
        viewModelScope.launch {
            routineRepository.deleteTask(id)
        }
    }

    private fun calculateExpReward(difficulty: Int): Int {
        // Base EXP formula: difficulty * 10, with bonuses for higher difficulty
        return when {
            difficulty <= 3 -> difficulty * 10
            difficulty <= 6 -> difficulty * 12
            difficulty <= 8 -> difficulty * 15
            else -> difficulty * 20
        }
    }
}
