package com.darc.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darc.app.data.entity.PlayerEntity
import com.darc.app.data.entity.StreakEntity
import com.darc.app.data.repository.LogRepository
import com.darc.app.data.repository.PlayerRepository
import com.darc.app.data.repository.StreakRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val player: PlayerEntity? = null,
    val streak: StreakEntity? = null,
    val todayCompletedCount: Int = 0,
    val todayTotalCount: Int = 0,
    val isLoading: Boolean = true,
    val streakBroken: Boolean = false,
    val expPenalty: Int = 0
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val streakRepository: StreakRepository,
    private val logRepository: LogRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // Keep original flow for compatibility
    val player: Flow<PlayerEntity?> = playerRepository.getPlayer()

    init {
        loadHomeData()
        checkStreak()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            combine(
                playerRepository.getPlayer(),
                streakRepository.getStreak(),
                logRepository.getTodayLogs()
            ) { player, streak, todayLogs ->
                val completedToday = todayLogs.count { it.isCompleted }
                HomeUiState(
                    player = player,
                    streak = streak,
                    todayCompletedCount = completedToday,
                    todayTotalCount = todayLogs.size,
                    isLoading = false,
                    streakBroken = _uiState.value.streakBroken,
                    expPenalty = _uiState.value.expPenalty
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    private fun checkStreak() {
        viewModelScope.launch {
            val result = streakRepository.checkAndUpdateStreak()
            if (result.streakBroken) {
                _uiState.value = _uiState.value.copy(
                    streakBroken = true,
                    expPenalty = result.expPenalty
                )
            }
        }
    }

    fun dismissStreakBrokenAlert() {
        _uiState.value = _uiState.value.copy(streakBroken = false, expPenalty = 0)
    }

    fun useRevivalToken() {
        viewModelScope.launch {
            val success = streakRepository.useRevivalToken()
            if (success) {
                _uiState.value = _uiState.value.copy(streakBroken = false, expPenalty = 0)
            }
        }
    }
}
