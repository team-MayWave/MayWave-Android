package com.example.maywave.data.remote

import com.example.maywave.data.dto.GamePlayRequestDto
import com.example.maywave.data.dto.GamePlayResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("api/game/play")
    suspend fun playGame(
        @Body gamePlayRequestDto: GamePlayRequestDto
    ): GamePlayResponseDto
}
