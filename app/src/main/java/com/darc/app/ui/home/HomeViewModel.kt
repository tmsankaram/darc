package com.darc.app.ui.home

import androidx.lifecycle.ViewModel
import com.darc.app.data.entity.PlayerEntity
import com.darc.app.data.repository.PlayerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val playerRepository: PlayerRepository
) : ViewModel() {

    val player: Flow<PlayerEntity?> = playerRepository.getPlayer()
}
