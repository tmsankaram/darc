package com.darc.app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darc.app.data.entity.PlayerEntity
import com.darc.app.data.entity.StatsEntity
import com.darc.app.data.repository.PlayerRepository
import com.darc.app.domain.GamificationEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val player: PlayerEntity? = null,
    val stats: StatsEntity? = null,
    val rankColor: Long = 0xFFBB86FC,
    val isLoading: Boolean = true
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val gamificationEngine: GamificationEngine
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            playerRepository.getPlayer().collect { player ->
                player?.let {
                    val stats = gamificationEngine.recalculateStats()
                    val rankColor = gamificationEngine.getRankColor(it.rank)

                    // Update title if needed
                    val newTitle = gamificationEngine.calculateTitle(it.level, stats)
                    if (newTitle != it.title) {
                        playerRepository.updatePlayer(it.copy(title = newTitle))
                    }

                    _uiState.value = ProfileUiState(
                        player = it.copy(title = newTitle),
                        stats = stats,
                        rankColor = rankColor,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun refreshStats() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val stats = gamificationEngine.recalculateStats()
            _uiState.value = _uiState.value.copy(stats = stats, isLoading = false)
        }
    }
}
