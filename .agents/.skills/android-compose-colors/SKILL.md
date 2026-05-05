---
name: android-compose-colors
description: Use when defining or applying colors and themes in Jetpack Compose. Enforce MaterialTheme, Color.kt, Theme.kt, dark/light mode color schemes, and avoid repeated hard-coded colors.
---

# Android Compose Colors and Theme Skill

## Purpose

Use this skill when creating or modifying colors, typography, shapes, themes, or dark/light mode behavior in an Android Jetpack Compose project.

Colors must not be hard-coded repeatedly inside each screen.

Define colors in the theme layer and use them through `MaterialTheme` or a custom app color system.

---

## Main Rule

Do not write colors directly in every UI file like this:

```kotlin
Text(
    text = "Hello",
    color = Color(0xFF000000)
)
```

Use theme colors like this:

```kotlin
Text(
    text = "Hello",
    color = MaterialTheme.colorScheme.onBackground
)
```

---

## Recommended File Structure

```text
ui/
  theme/
    Color.kt
    Theme.kt
    Type.kt
    AppColors.kt
```

---

## Color.kt Rule

`Color.kt` should define reusable app colors.

```kotlin
package com.example.app.ui.theme

import androidx.compose.ui.graphics.Color

val PrimaryBlue = Color(0xFF2F80ED)
val ErrorRed = Color(0xFFEB5757)
val DividerGray = Color(0xFFE0E0E0)

val LightBackground = Color(0xFFFFFFFF)
val DarkBackground = Color(0xFF121212)

val LightSurface = Color(0xFFFFFFFF)
val DarkSurface = Color(0xFF1E1E1E)
```

Use meaningful names based on role or design purpose.

Good:

```kotlin
val PrimaryBlue = Color(0xFF2F80ED)
val ErrorRed = Color(0xFFEB5757)
val DividerGray = Color(0xFFE0E0E0)
```

Bad:

```kotlin
val Color1 = Color(0xFF2F80ED)
val MyColor = Color(0xFFEB5757)
```

---

## Theme.kt Rule

Apply the color scheme inside the app theme.

```kotlin
package com.example.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    background = LightBackground,
    onBackground = Color.Black,
    surface = LightSurface,
    onSurface = Color.Black,
    error = ErrorRed
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    background = DarkBackground,
    onBackground = Color.White,
    surface = DarkSurface,
    onSurface = Color.White,
    error = ErrorRed
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
```

---

## MainActivity Rule

Apply the app theme once at the top level.

```kotlin
setContent {
    AppTheme {
        AppNavigation()
    }
}
```

Do not wrap every small composable with the theme.

---

## UI Usage Rule

Screen UI should use `MaterialTheme.colorScheme`.

```kotlin
@Composable
fun HomeScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Text(
            text = "Home",
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
```

---

## Custom App Color Rule

If MaterialTheme is not enough, create a custom app color system.

```kotlin
package com.example.app.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class AppColors(
    val mainButtonBackground: Color,
    val mainButtonText: Color,
    val descriptionText: Color
)

val LightAppColors = AppColors(
    mainButtonBackground = PrimaryBlue,
    mainButtonText = Color.White,
    descriptionText = Color.DarkGray
)

val DarkAppColors = AppColors(
    mainButtonBackground = PrimaryBlue,
    mainButtonText = Color.White,
    descriptionText = Color.LightGray
)

val LocalAppColors = staticCompositionLocalOf {
    LightAppColors
}
```

---

## Avoid

- Hard-coding colors repeatedly inside screens.
- Creating duplicate color values in multiple files.
- Using unclear color names.
- Ignoring dark mode.
- Mixing theme setup and screen UI layout in the same file.