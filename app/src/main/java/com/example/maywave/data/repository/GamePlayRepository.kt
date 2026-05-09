package com.example.maywave.data.repository

import com.example.maywave.data.remote.ApiService

class GamePlayRepository(
    private val apiService: ApiService
) {

    suspend fun playGame(
        roleId: Int,
        scenarioId: Int,
        choice: Int
    ): Result<String> {
        return try {
            val responseDto = apiService.playGame(
                roleId = roleId,
                scenarioId = scenarioId,
                choice = choice
            )

            Result.success(responseDto.message)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
}
