package com.darc.app.ui.routines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darc.app.data.entity.RoutineEntity
import com.darc.app.data.entity.TaskEntity
import com.darc.app.data.repository.RoutineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RoutineWithTaskCount(
    val routine: RoutineEntity,
    val taskCount: Int
)

data class RoutinesUiState(
    val routines: List<RoutineWithTaskCount> = emptyList(),
    val isLoading: Boolean = true,
    val showCreateSheet: Boolean = false,
    val editingRoutine: RoutineEntity? = null
)

@HiltViewModel
class RoutinesViewModel @Inject constructor(
    private val routineRepository: RoutineRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RoutinesUiState())
    val uiState: StateFlow<RoutinesUiState> = _uiState.asStateFlow()

    init {
        loadRoutines()
    }

    private fun loadRoutines() {
        viewModelScope.launch {
            combine(
                routineRepository.getAllRoutines(),
                routineRepository.getAllTasks()
            ) { routines, tasks ->
                routines.map { routine ->
                    RoutineWithTaskCount(
                        routine = routine,
                        taskCount = tasks.count { it.routineId == routine.id }
                    )
                }
            }.collect { routinesWithCounts ->
                _uiState.value = _uiState.value.copy(
                    routines = routinesWithCounts,
                    isLoading = false
                )
            }
        }
    }

    fun showCreateSheet() {
        _uiState.value = _uiState.value.copy(showCreateSheet = true, editingRoutine = null)
    }

    fun showEditSheet(routine: RoutineEntity) {
        _uiState.value = _uiState.value.copy(showCreateSheet = true, editingRoutine = routine)
    }

    fun hideSheet() {
        _uiState.value = _uiState.value.copy(showCreateSheet = false, editingRoutine = null)
    }

    fun createRoutine(name: String, description: String) {
        viewModelScope.launch {
            routineRepository.createRoutine(name, description)
            hideSheet()
        }
    }

    fun updateRoutine(routine: RoutineEntity, name: String, description: String) {
        viewModelScope.launch {
            routineRepository.updateRoutine(routine.copy(name = name, description = description))
            hideSheet()
        }
    }

    fun deleteRoutine(id: Long) {
        viewModelScope.launch {
            routineRepository.deleteRoutine(id)
        }
    }
}
