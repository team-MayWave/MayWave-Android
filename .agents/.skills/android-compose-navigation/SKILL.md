---
name: android-compose-navigation
description: Use when implementing Jetpack Compose Navigation. Enforce Route class/object based navigation instead of raw route strings, and keep NavHost logic separated from screen UI.
---

# Android Compose Navigation Skill

## Purpose

Use this skill when creating or modifying navigation in an Android Jetpack Compose project.

Navigation must use Jetpack Navigation Compose.

Do not use raw route strings directly inside `NavHost`, `composable()`, or `navigate()` calls.

Use a route class/object structure instead.

---

## Main Rule

Do not write routes like this:

```kotlin
navController.navigate("home")

composable("home") {
    HomeScreen()
}
```

Use route definitions like this:

```kotlin
sealed class Route(val path: String) {
    data object Home : Route("home")
    data object Login : Route("login")
    data object SignUp : Route("sign_up")
}
```

Then use them like this:

```kotlin
navController.navigate(Route.Home.path)

composable(Route.Home.path) {
    HomeScreen()
}
```

---

## Recommended File Structure

```text
navigation/
  Route.kt
  AppNavigation.kt
```

---

## Route.kt Rule

`Route.kt` must only define route names.

```kotlin
package com.example.app.navigation

sealed class Route(val path: String) {
    data object Home : Route("home")
    data object Login : Route("login")
    data object SignUp : Route("sign_up")
}
```

---

## AppNavigation.kt Rule

`AppNavigation.kt` must only define `NavHost` and screen connections.

```kotlin
package com.example.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.app.ui.home.HomeScreen
import com.example.app.ui.login.LoginScreen
import com.example.app.ui.signup.SignUpScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Route.Home.path
    ) {
        composable(Route.Home.path) {
            HomeScreen(
                onLoginClick = {
                    navController.navigate(Route.Login.path)
                }
            )
        }

        composable(Route.Login.path) {
            LoginScreen(
                onSignUpClick = {
                    navController.navigate(Route.SignUp.path)
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Route.SignUp.path) {
            SignUpScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
```

---

## Screen Rule

Screen composables should not directly create or own `NavController`.

Bad:

```kotlin
@Composable
fun HomeScreen() {
    val navController = rememberNavController()
}
```

Good:

```kotlin
@Composable
fun HomeScreen(
    onLoginClick: () -> Unit
) {
    HomeContent(
        onLoginClick = onLoginClick
    )
}
```

The screen receives event functions from navigation.

---

## Event Flow Rule

Navigation event flow should be:

```text
Button click
-> onClick()
-> Screen event parameter
-> AppNavigation
-> navController.navigate(Route.XXX.path)
```

Do not make child UI components know route names directly.

---

## Avoid

- Raw route strings inside screen files.
- Calling `rememberNavController()` inside every screen.
- Mixing `NavHost` and UI layout in the same file.
- Passing `NavController` deeply into small UI components.
- Duplicating route strings across files.