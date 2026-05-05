---
name: android-compose-ui-rules
description: Use when creating Jetpack Compose UI screens. Enforce small responsibility-based composables, Screen -> Content -> Section -> Component structure, and avoid one huge composable per screen.
---

# Android Compose UI Rules Skill

## Purpose

Use this skill when creating or modifying Jetpack Compose UI.

Each screen must be divided into small composable components.

Do not create one huge composable containing the entire UI.

---

## Main Rule

Separate UI by responsibility.

Bad:

```kotlin
@Composable
fun HomeScreen() {
    Column {
        Text(...)
        Image(...)
        Button(...)
        Text(...)
        Row(...)
    }
}
```

Good:

```kotlin
@Composable
fun HomeScreen() {
    HomeContent()
}

@Composable
private fun HomeContent() {
    Column {
        HomeTitle()
        HomeDescription()
        HomeImage()
        HomeStartButton()
    }
}
```

---

## Recommended Screen Structure

Use this pattern:

```text
Screen
-> Content
-> Section
-> Small UI Components
```

Example:

```text
LoginScreen
-> LoginContent
-> LoginTitle
-> LoginTextFields
-> LoginButton
-> LoginErrorMessage
```

---

## Screen Rule

The screen composable connects state and events.

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

The screen should not contain all layout details directly.

---

## Content Rule

Content composable contains the main layout structure.

```kotlin
@Composable
private fun LoginContent(
    loginUiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LoginTitle()

        Spacer(modifier = Modifier.height(24.dp))

        LoginTextFields(
            email = loginUiState.email,
            password = loginUiState.password,
            onEmailChange = onEmailChange,
            onPasswordChange = onPasswordChange
        )

        Spacer(modifier = Modifier.height(24.dp))

        LoginButton(
            isLoading = loginUiState.isLoading,
            onLoginClick = onLoginClick
        )

        LoginErrorMessage(
            errorMessage = loginUiState.errorMessage
        )
    }
}
```

---

## Small Component Rule

Each meaningful UI element should be separated into its own composable.

```kotlin
@Composable
private fun LoginTitle() {
    Text(
        text = "Login",
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onBackground
    )
}
```

```kotlin
@Composable
private fun LoginTextFields(
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit
) {
    Column {
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = {
                Text(text = "Email")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = {
                Text(text = "Password")
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
```

```kotlin
@Composable
private fun LoginButton(
    isLoading: Boolean,
    onLoginClick: () -> Unit
) {
    Button(
        onClick = onLoginClick,
        enabled = !isLoading,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = if (isLoading) "Loading..." else "Login"
        )
    }
}
```

```kotlin
@Composable
private fun LoginErrorMessage(
    errorMessage: String?
) {
    if (errorMessage != null) {
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error
        )
    }
}
```

---

## Naming Rule

Composable names must describe their UI responsibility.

Good:

```kotlin
LoginTitle()
LoginButton()
BookCoverImage()
BookDescriptionText()
StartButton()
```

Bad:

```kotlin
Text1()
Button1()
MyComposable()
BoxView()
```

---

## Modifier Rule

Pass `Modifier` from parent only when the parent needs to control size, padding, or position.

```kotlin
@Composable
private fun LoginButton(
    modifier: Modifier = Modifier,
    onLoginClick: () -> Unit
) {
    Button(
        onClick = onLoginClick,
        modifier = modifier
    ) {
        Text(text = "Login")
    }
}
```

Parent usage:

```kotlin
LoginButton(
    modifier = Modifier.fillMaxWidth(),
    onLoginClick = onLoginClick
)
```

---

## Preview Rule

Create preview for screen-level UI when possible.

```kotlin
@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    AppTheme {
        LoginScreen(
            loginUiState = LoginUiState(),
            onEmailChange = {},
            onPasswordChange = {},
            onLoginClick = {}
        )
    }
}
```

---

## Avoid

- One giant composable with all UI.
- Mixing navigation logic inside small UI components.
- Mixing network logic inside UI.
- Using unclear composable names.
- Repeating the same UI code multiple times.
- Hard-coding colors instead of using theme colors.