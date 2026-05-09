package com.example.maywave.data.remote

object ApiServiceFactory {

    val apiService: ApiService =
        RetrofitClient.retrofit.create(ApiService::class.java)
}
