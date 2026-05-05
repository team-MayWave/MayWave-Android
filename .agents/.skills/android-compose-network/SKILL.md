---
name: android-compose-network
description: Use when implementing server communication in Android Compose with MVVM. Enforce UI -> ViewModel -> Repository -> ApiService -> Retrofit flow, StateFlow UI state, and separation of network logic from UI.
---

# Android Compose Network MVVM Skill

## Purpose

Use this skill when implementing server communication in an Android Jetpack Compose project.

Network logic must follow MVVM and layered architecture.

UI must not call Retrofit directly.

---

## Main Rule

Use this request flow:

```text
UI Screen
-> ViewModel
-> Repository
-> ApiService
-> Retrofit
-> Server
```

Use this response flow:

```text
Server
-> Retrofit
-> ApiService
-> Repository
-> ViewModel
-> UI State
-> UI Screen recomposition
```

---

## Recommended Package Structure

```text
data/
  dto/
    LoginRequestDto.kt
    LoginResponseDto.kt

  remote/
    RetrofitClient.kt
    ApiService.kt
    ApiServiceFactory.kt

  repository/
    LoginRepository.kt

ui/
  login/
    LoginScreen.kt
    LoginViewModel.kt
    LoginUiState.kt
    LoginViewModelFactory.kt
```

---

## UI Rule

Composable UI should only display state and send events.

Bad:

```kotlin
@Composable
fun LoginScreen() {
    val response = apiService.login(...)
}
```

Good:

```kotlin
@Composable
fun LoginScreen(
    loginUiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit
) {
    LoginContent(
        loginUiState = loginUiState,
        onEmailChange = onEmailChange,
        onPasswordChange = onPasswordChange,
        onLoginClick = onLoginClick
    )
}
```

UI must not know Retrofit, Repository, DTO, or ApiService.

---

## UiState Rule

Create a UiState data class for each screen.

```kotlin
package com.example.app.ui.login

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoginSuccess: Boolean = false
)
```

UI state must be the single source of truth for the screen.

---

## ViewModel Rule

ViewModel owns UI state and calls Repository.

```kotlin
package com.example.app.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.data.repository.LoginRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginRepository: LoginRepository
) : ViewModel() {

    private val _loginUiState = MutableStateFlow(LoginUiState())
    val loginUiState: StateFlow<LoginUiState> = _loginUiState.asStateFlow()

    fun onEmailChange(email: String) {
        _loginUiState.value = _loginUiState.value.copy(
            email = email
        )
    }

    fun onPasswordChange(password: String) {
        _loginUiState.value = _loginUiState.value.copy(
            password = password
        )
    }

    fun login() {
        viewModelScope.launch {
            _loginUiState.value = _loginUiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            val currentState = _loginUiState.value

            val result = loginRepository.login(
                email = currentState.email,
                password = currentState.password
            )

            result
                .onSuccess {
                    _loginUiState.value = _loginUiState.value.copy(
                        isLoading = false,
                        isLoginSuccess = true
                    )
                }
                .onFailure { throwable ->
                    _loginUiState.value = _loginUiState.value.copy(
                        isLoading = false,
                        errorMessage = throwable.message
                    )
                }
        }
    }
}
```

---

## Repository Rule

Repository contains data request logic.

Repository converts UI-friendly values into DTOs if needed.

```kotlin
package com.example.app.data.repository

import com.example.app.data.dto.LoginRequestDto
import com.example.app.data.dto.LoginResponseDto
import com.example.app.data.remote.ApiService

class LoginRepository(
    private val apiService: ApiService
) {

    suspend fun login(
        email: String,
        password: String
    ): Result<LoginResponseDto> {
        return try {
            val requestDto = LoginRequestDto(
                email = email,
                password = password
            )

            val responseDto = apiService.login(requestDto)

            Result.success(responseDto)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
}
```

---

## ApiService Rule

ApiService only defines server endpoints.

```kotlin
interface ApiService {

    @POST("login")
    suspend fun login(
        @Body loginRequestDto: LoginRequestDto
    ): LoginResponseDto
}
```

ApiService should not contain UI state logic.

---

## DTO Rule

DTO files represent request/response JSON data.

```kotlin
data class LoginRequestDto(
    val email: String,
    val password: String
)

data class LoginResponseDto(
    val accessToken: String,
    val refreshToken: String
)
```

DTO should not contain UI state such as loading, error message, or screen text.

---

## ViewModelFactory Rule

If ViewModel needs Repository in its constructor, create a ViewModelFactory.

```kotlin
package com.example.app.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app.data.repository.LoginRepository

class LoginViewModelFactory(
    private val loginRepository: LoginRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(loginRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
```

---

## Avoid

- Calling ApiService directly from Composable.
- Calling Retrofit directly from ViewModel.
- Creating Retrofit inside Repository.
- Mixing UI state and DTO.
- Putting network request code inside screen UI.
- Letting UI own business logic.