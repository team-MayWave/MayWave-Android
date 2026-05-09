package com.example.maywave.data.remote

import com.example.maywave.data.dto.GamePlayResponseDto
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @POST("api/game/play")
    suspend fun playGame(
        @Query("roleId") roleId: Int,
        @Query("scenarioId") scenarioId: Int,
        @Query("choice") choice: Int
    ): GamePlayResponseDto
}
