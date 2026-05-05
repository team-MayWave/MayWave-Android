---
name: android-compose-retrofit
description: Use when setting up Retrofit in Android. Enforce separated Retrofit instance creation and ApiService creation, instead of creating service objects directly inside the Retrofit client.
---

# Android Compose Retrofit Skill

## Purpose

Use this skill when creating or modifying Retrofit-related code in an Android project.

Retrofit setup must be separated from API service creation.

Do not create the API service directly inside the Retrofit object.

---

## Main Rule

Do not use this pattern:

```kotlin
object RetrofitClient {
    val apiService: ApiService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)
}
```

Use this separated pattern instead:

```text
RetrofitClient
-> creates Retrofit instance only

ApiServiceFactory
-> creates ApiService from Retrofit
```

---

## Recommended File Structure

```text
data/
  remote/
    RetrofitClient.kt
    ApiService.kt
    ApiServiceFactory.kt
```

---

## RetrofitClient.kt Rule

`RetrofitClient.kt` must only create and expose the Retrofit instance.

```kotlin
package com.example.app.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "https://example.com/"

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}
```

Do not create `ApiService` inside this file.

---

## ApiService.kt Rule

`ApiService.kt` must only define server API functions.

```kotlin
package com.example.app.data.remote

import com.example.app.data.dto.LoginRequestDto
import com.example.app.data.dto.LoginResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("login")
    suspend fun login(
        @Body loginRequestDto: LoginRequestDto
    ): LoginResponseDto
}
```

This file should not contain `Retrofit.Builder`.

---

## ApiServiceFactory.kt Rule

`ApiServiceFactory.kt` must create API service objects from Retrofit.

```kotlin
package com.example.app.data.remote

object ApiServiceFactory {

    val apiService: ApiService =
        RetrofitClient.retrofit.create(ApiService::class.java)
}
```

---

## Repository Usage Rule

Repository should receive `ApiService` through constructor injection.

Good:

```kotlin
package com.example.app.data.repository

import com.example.app.data.remote.ApiService

class LoginRepository(
    private val apiService: ApiService
) {
    suspend fun login(
        email: String,
        password: String
    ) {
        // apiService.login(...)
    }
}
```

Bad:

```kotlin
class LoginRepository {
    private val apiService =
        RetrofitClient.retrofit.create(ApiService::class.java)
}
```

Repository should not create Retrofit directly.

---

## Responsibility Rule

Each file must have one clear responsibility.

```text
RetrofitClient.kt
-> Retrofit instance creation only

ApiService.kt
-> API endpoint function definitions only

ApiServiceFactory.kt
-> ApiService object creation only

Repository.kt
-> Data request logic only
```

---

## Avoid

- Creating `ApiService` directly inside `RetrofitClient`.
- Creating Retrofit inside Repository.
- Creating Retrofit inside ViewModel.
- Mixing DTO, API service, Repository, and Retrofit setup in one file.
- Using raw network code inside UI composables.