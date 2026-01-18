package com.darc.app.ui.recovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darc.app.data.entity.StreakEntity
import com.darc.app.data.repository.StreakRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RecoveryUiState(
    val streak: StreakEntity? = null,
    val canRecover: Boolean = false,
    val isLoading: Boolean = true,
    val recoverySuccess: Boolean = false,
    val showConfirmDialog: Boolean = false
)

@HiltViewModel
class RecoveryViewModel @Inject constructor(
    private val streakRepository: StreakRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecoveryUiState())
    val uiState: StateFlow<RecoveryUiState> = _uiState.asStateFlow()

    init {
        loadRecoveryData()
    }

    private fun loadRecoveryData() {
        viewModelScope.launch {
            streakRepository.getStreak().collect { streak ->
                _uiState.value = RecoveryUiState(
                    streak = streak,
                    canRecover = (streak?.revivalTokens ?: 0) > 0,
                    isLoading = false
                )
            }
        }
    }

    fun showConfirmDialog() {
        _uiState.value = _uiState.value.copy(showConfirmDialog = true)
    }

    fun hideDialog() {
        _uiState.value = _uiState.value.copy(showConfirmDialog = false)
    }

    fun useRevivalToken() {
        viewModelScope.launch {
            val success = streakRepository.useRevivalToken()
            _uiState.value = _uiState.value.copy(
                recoverySuccess = success,
                showConfirmDialog = false
            )
        }
    }

    fun dismissSuccess() {
        _uiState.value = _uiState.value.copy(recoverySuccess = false)
    }
}
