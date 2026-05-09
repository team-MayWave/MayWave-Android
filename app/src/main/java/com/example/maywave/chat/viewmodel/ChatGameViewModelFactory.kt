package com.example.maywave.chat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.maywave.data.remote.ApiServiceFactory
import com.example.maywave.data.repository.GamePlayRepository

class ChatGameViewModelFactory(
    private val gamePlayRepository: GamePlayRepository = GamePlayRepository(
        apiService = ApiServiceFactory.apiService
    )
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatGameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatGameViewModel(gamePlayRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
