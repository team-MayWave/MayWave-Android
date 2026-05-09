package com.example.maywave.chat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maywave.data.repository.GamePlayRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatGameViewModel(
    private val gamePlayRepository: GamePlayRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatGameUiState())
    val uiState: StateFlow<ChatGameUiState> = _uiState.asStateFlow()

    fun submitChoice(request: ChatGameRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.startLoading(request)

            val result = gamePlayRepository.playGame(
                roleId = request.roleId,
                scenarioId = request.scenarioId,
                choice = request.choice
            )

            _uiState.value = result.fold(
                onSuccess = { message ->
                    _uiState.value.saveResponse(
                        request = request,
                        message = message
                    )
                },
                onFailure = {
                    _uiState.value.saveError(request)
                }
            )
        }
    }
}
