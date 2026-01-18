package com.darc.app.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darc.app.data.repository.PlayerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingState(
    val currentStep: Int = 0,
    val name: String = "",
    val focus: String = "",
    val isComplete: Boolean = false,
    val isLoading: Boolean = false
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val playerRepository: PlayerRepository
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingState())
    val state: StateFlow<OnboardingState> = _state.asStateFlow()

    fun updateName(name: String) {
        _state.value = _state.value.copy(name = name)
    }

    fun updateFocus(focus: String) {
        _state.value = _state.value.copy(focus = focus)
    }

    fun nextStep() {
        _state.value = _state.value.copy(currentStep = _state.value.currentStep + 1)
    }

    fun previousStep() {
        if (_state.value.currentStep > 0) {
            _state.value = _state.value.copy(currentStep = _state.value.currentStep - 1)
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            playerRepository.createPlayer(
                name = _state.value.name,
                focus = _state.value.focus
            )
            _state.value = _state.value.copy(isLoading = false, isComplete = true)
        }
    }
}
