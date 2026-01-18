package com.darc.app.navigation

import androidx.lifecycle.ViewModel
import com.darc.app.data.repository.PlayerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor(
    playerRepository: PlayerRepository
) : ViewModel() {

    val hasPlayer: Flow<Boolean> = playerRepository.getPlayer().map { it != null }
}
