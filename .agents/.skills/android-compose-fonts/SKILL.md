---
name: android-compose-fonts
description: Use this skill when adding, modifying, or applying fonts through a custom app MaterialTheme in an Android Jetpack Compose project.
---

# Android Compose Fonts Skill

## Purpose

This skill defines how fonts must be added, organized, connected to Typography, applied to a custom app Theme, and used from `MainActivity` in this Android Jetpack Compose project.

Use this skill when:

- adding a new font file
- creating a `FontFamily`
- modifying `Typography`
- creating or updating the app's custom Theme function
- applying the custom Theme in `MainActivity`
- applying fonts to `Text` composables
- deciding where font-related files should be placed

<!-- 한글:
이 스킬은 Android Compose 프로젝트에서 폰트 파일을 어디에 넣고,
FontFamily, Typography, 전용 앱 Theme, MainActivity 적용까지 어떻게 연결할지 정하는 규칙이다.
-->

---

## Core Rule

Do not apply fonts directly in many Composable screens.

Use this flow:

```txt
res/font/*.ttf or *.otf
        ↓
ui/theme/Font.kt
        ↓
ui/theme/Type.kt
        ↓
ui/theme/Theme.kt
        ↓
MainActivity.kt
        ↓
MaterialTheme.typography in Composables
```

The project must have a custom app Theme function.

Example:

```txt
MayWaveTheme
BookOnTheme
GomsTheme
ProjectNameTheme
```

The custom app Theme function must wrap Compose Material 3 `MaterialTheme`.

```txt
Custom App Theme = Project-specific wrapper
MaterialTheme     = Compose Material 3 theme system
```

<!-- 한글:
MaterialTheme을 버리는 것이 아니다.
MayWaveTheme 같은 앱 전용 Theme 함수를 만들고,
그 안에서 MaterialTheme에 색상, 폰트, 모양을 넣는 방식이다.
-->

---

## Recommended Font and Theme Structure

Use this structure:

```txt
app/
└── src/
    └── main/
        ├── res/
        │   └── font/
        │       ├── nanum_myeongjo_regular.ttf
        │       └── nanum_myeongjo_bold.ttf
        └── java/
            └── com/example/project/
                ├── MainActivity.kt
                └── ui/
                    └── theme/
                        ├── Color.kt
                        ├── Font.kt
                        ├── Type.kt
                        ├── Shape.kt
                        └── Theme.kt
```

Responsibilities:

```txt
res/font/       → stores .ttf or .otf font files
Font.kt         → defines FontFamily values
Type.kt         → defines Typography values
Shape.kt        → defines Shapes values if needed
Color.kt        → defines ColorScheme values
Theme.kt        → defines the custom app Theme function
MainActivity.kt → applies the custom app Theme to the whole app
```

<!-- 한글:
폰트 파일은 res/font에 넣고,
FontFamily는 Font.kt,
Typography는 Type.kt,
앱 전용 Theme 함수는 Theme.kt,
앱 전체 적용은 MainActivity.kt에서 한다.
-->

---

## Font File Rule

Put font files only in:

```txt
app/src/main/res/font/
```

Use lowercase snake_case file names.

Good:

```txt
nanum_myeongjo_regular.ttf
nanum_myeongjo_bold.ttf
pretendard_regular.otf
pretendard_semibold.otf
```

Bad:

```txt
NanumMyeongjo-Regular.ttf
NanumMyeongjo Bold.ttf
font1.ttf
myFontFile.ttf
```

Do not put font files in:

```txt
assets/
drawable/
mipmap/
java/
kotlin/
```

unless the developer explicitly requests a different structure.

<!-- 한글:
Android 리소스 파일 이름은 소문자와 언더스코어를 쓰는 것이 안전하다.
폰트 파일은 기본적으로 res/font 폴더에 둔다.
-->

---

## FontFamily Rule

Define `FontFamily` values in:

```txt
ui/theme/Font.kt
```

Do not define `FontFamily` directly inside Composable functions.

Good:

```kotlin
package com.example.project.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.project.R

val NanumMyeongjo = FontFamily(
    Font(
        resId = R.font.nanum_myeongjo_regular,
        weight = FontWeight.Normal
    ),
    Font(
        resId = R.font.nanum_myeongjo_bold,
        weight = FontWeight.Bold
    )
)
```

Bad:

```kotlin
@Composable
fun ChatScreen() {
    val font = FontFamily(Font(R.font.nanum_myeongjo_regular))

    Text(
        text = "Hello",
        fontFamily = font
    )
}
```

Reason:

```txt
Composable should focus on UI.
Font definitions should be centralized in the theme layer.
```

<!-- 한글:
Composable 안에서 FontFamily를 직접 만들면 화면마다 코드가 반복된다.
폰트 정의는 theme 계층에서 한 번만 관리한다.
-->

---

## Typography Rule

Apply fonts through Material 3 `Typography` when possible.

Define Typography in:

```txt
ui/theme/Type.kt
```

Good:

```kotlin
package com.example.project.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val MayWaveTypography = Typography(
    titleLarge = TextStyle(
        fontFamily = NanumMyeongjo,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = NanumMyeongjo,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = NanumMyeongjo,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    )
)
```

If the project is not MayWave, use the actual project name.

Examples:

```txt
MayWaveTypography
BookOnTypography
GomsTypography
AppTypography
```

<!-- 한글:
앱 전체에서 같은 폰트 규칙을 쓰려면 Typography에 연결하는 방식이 가장 좋다.
화면마다 fontFamily를 직접 쓰지 말고, MaterialTheme.typography를 사용하게 만든다.
-->

---

## Custom App Theme Rule

The project must define a custom app Theme function in:

```txt
ui/theme/Theme.kt
```

The custom app Theme must call `MaterialTheme` internally.

Good:

```kotlin
package com.example.project.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun MayWaveTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        MayWaveDarkColorScheme
    } else {
        MayWaveLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MayWaveTypography,
        shapes = MayWaveShapes,
        content = content
    )
}
```

If the project does not use custom shapes yet, this is also acceptable:

```kotlin
package com.example.project.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun MayWaveTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        MayWaveDarkColorScheme
    } else {
        MayWaveLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MayWaveTypography,
        content = content
    )
}
```

Bad:

```kotlin
setContent {
    MaterialTheme {
        App()
    }
}
```

Reason:

```txt
MainActivity should apply the project-specific Theme.
The project-specific Theme should control colors, typography, and shapes.
```

<!-- 한글:
MainActivity에서 MaterialTheme을 바로 쓰지 말고,
MayWaveTheme 같은 앱 전용 Theme을 만들어서 감싼다.
그 전용 Theme 안에서 MaterialTheme을 호출한다.
-->

---

## ColorScheme Rule

Define app color schemes in:

```txt
ui/theme/Color.kt
```

Good:

```kotlin
package com.example.project.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val MayWavePrimary = Color(0xFF4F7CFF)
val MayWaveBackground = Color(0xFFFFFFFF)
val MayWaveSurface = Color(0xFFF5F5F5)
val MayWaveText = Color(0xFF111111)

val MayWaveLightColorScheme = lightColorScheme(
    primary = MayWavePrimary,
    background = MayWaveBackground,
    surface = MayWaveSurface,
    onBackground = MayWaveText,
    onSurface = MayWaveText
)

val MayWaveDarkColorScheme = darkColorScheme(
    primary = MayWavePrimary,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onBackground = Color.White,
    onSurface = Color.White
)
```

Do not hardcode common app colors repeatedly inside screens.

Bad:

```kotlin
Text(
    text = "Hello",
    color = Color(0xFF111111)
)
```

Good:

```kotlin
Text(
    text = "Hello",
    color = MaterialTheme.colorScheme.onBackground
)
```

<!-- 한글:
공통 색상은 Color.kt와 Theme.kt에서 관리한다.
화면에서는 MaterialTheme.colorScheme을 통해 사용한다.
-->

---

## Shape Rule

If the app needs common rounded corners, define them in:

```txt
ui/theme/Shape.kt
```

Good:

```kotlin
package com.example.project.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val MayWaveShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(15.dp),
    large = RoundedCornerShape(24.dp)
)
```

Then connect it in `Theme.kt`:

```kotlin
MaterialTheme(
    colorScheme = colorScheme,
    typography = MayWaveTypography,
    shapes = MayWaveShapes,
    content = content
)
```

In Composables, prefer:

```kotlin
Box(
    modifier = Modifier.clip(MaterialTheme.shapes.medium)
)
```

instead of:

```kotlin
Box(
    modifier = Modifier.clip(RoundedCornerShape(15.dp))
)
```

Direct shape values are acceptable for one-off UI only.

<!-- 한글:
반복해서 쓰는 둥근 정도는 Shape.kt에 넣고,
화면에서는 MaterialTheme.shapes로 쓰는 것이 좋다.
-->

---

## MainActivity Theme Application Rule

Apply the custom app Theme in `MainActivity.kt`.

Good:

```kotlin
package com.example.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.project.ui.theme.MayWaveTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MayWaveTheme {
                App()
            }
        }
    }
}
```

If the project does not have an `App()` composable yet, applying a screen directly is acceptable:

```kotlin
setContent {
    MayWaveTheme {
        IntroScreen()
    }
}
```

Bad:

```kotlin
setContent {
    MaterialTheme {
        IntroScreen()
    }
}
```

Bad:

```kotlin
setContent {
    IntroScreen()
}
```

Reason:

```txt
The custom app Theme must wrap the app content.
Without the custom Theme, MaterialTheme.typography will not use the project's font rules.
```

<!-- 한글:
MainActivity에서 MayWaveTheme으로 앱 전체를 감싸야 한다.
그래야 화면 안에서 MaterialTheme.typography를 쓸 때 MayWaveTypography가 적용된다.
-->

---

## Text Usage Rule

In Composables, prefer using `MaterialTheme.typography`.

Good:

```kotlin
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ChatMessageText(
    message: String
) {
    Text(
        text = message,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onBackground
    )
}
```

Acceptable only for special cases:

```kotlin
Text(
    text = "Logo",
    fontFamily = NanumMyeongjo,
    fontWeight = FontWeight.Bold
)
```

Avoid:

```kotlin
Text(
    text = "안녕하세요",
    fontFamily = FontFamily(Font(R.font.nanum_myeongjo_regular))
)
```

Use direct `fontFamily` only when:

- the text is a logo
- the text is a special brand element
- the design intentionally uses a different font from the app typography
- the developer explicitly requests it

<!-- 한글:
일반 텍스트는 MaterialTheme.typography를 사용한다.
특별한 로고나 브랜드 텍스트만 fontFamily를 직접 지정할 수 있다.
-->

---

## MaterialTheme Usage Rule

Even when using a custom app Theme, Composables should still use Compose Material 3 `MaterialTheme`.

Good:

```kotlin
Text(
    text = "안녕하세요",
    style = MaterialTheme.typography.bodyLarge,
    color = MaterialTheme.colorScheme.onBackground
)
```

This works because `MayWaveTheme` internally provides values to `MaterialTheme`.

```txt
MayWaveTheme
    ↓
MaterialTheme(
    colorScheme = MayWaveLightColorScheme,
    typography = MayWaveTypography,
    shapes = MayWaveShapes
)
    ↓
Composable uses MaterialTheme.typography / colorScheme / shapes
```

Do not import or use a fake custom theme object inside every Composable.

Bad:

```kotlin
Text(
    text = "안녕하세요",
    style = MayWaveTypography.bodyLarge
)
```

Good:

```kotlin
Text(
    text = "안녕하세요",
    style = MaterialTheme.typography.bodyLarge
)
```

Reason:

```txt
Screens should depend on MaterialTheme.
The app Theme controls what MaterialTheme provides.
```

<!-- 한글:
화면에서는 MayWaveTypography를 직접 쓰는 것보다 MaterialTheme.typography를 쓰는 게 좋다.
MayWaveTheme이 MaterialTheme에 값을 넣어주기 때문이다.
-->

---

## Preview Rule

Preview composables should also be wrapped with the custom app Theme.

Good:

```kotlin
@Preview(showBackground = true)
@Composable
private fun ChatMessageTextPreview() {
    MayWaveTheme {
        ChatMessageText(
            message = "안녕하세요"
        )
    }
}
```

Bad:

```kotlin
@Preview(showBackground = true)
@Composable
private fun ChatMessageTextPreview() {
    ChatMessageText(
        message = "안녕하세요"
    )
}
```

Reason:

```txt
Preview without the custom app Theme may show different fonts, colors, and shapes from the real app.
```

<!-- 한글:
Preview도 MayWaveTheme으로 감싸야 실제 앱과 비슷하게 보인다.
-->

---

## Font Weight Rule

Map font files to the correct `FontWeight`.

Good:

```kotlin
val NanumMyeongjo = FontFamily(
    Font(R.font.nanum_myeongjo_regular, FontWeight.Normal),
    Font(R.font.nanum_myeongjo_bold, FontWeight.Bold)
)
```

Do not use `FontWeight.Bold` if the project only has a regular font file.

Bad:

```kotlin
val NanumMyeongjo = FontFamily(
    Font(R.font.nanum_myeongjo_regular, FontWeight.Bold)
)
```

If the required weight file does not exist, either:

```txt
1. use the available closest weight
2. ask the developer to add the missing font file
```

<!-- 한글:
Bold를 쓰고 싶으면 실제 bold 폰트 파일이 있는 것이 가장 좋다.
regular 파일만 있는데 Bold로 등록하면 디자인과 다르게 보일 수 있다.
-->

---

## Naming Rule

Use clear names for font families, typography, shapes, and themes.

Good:

```kotlin
val NanumMyeongjo = FontFamily(...)
val Pretendard = FontFamily(...)

val MayWaveTypography = Typography(...)
val MayWaveShapes = Shapes(...)

@Composable
fun MayWaveTheme(...) { ... }
```

Acceptable generic names:

```kotlin
val AppTypography = Typography(...)
val AppShapes = Shapes(...)

@Composable
fun AppTheme(...) { ... }
```

Bad:

```kotlin
val font1 = FontFamily(...)
val myFont = FontFamily(...)
val typography1 = Typography(...)
val MyType = Typography(...)

@Composable
fun Theme1(...) { ... }
```

Use the actual project name when possible.

Example:

```txt
Project name: MayWave
Theme name: MayWaveTheme
Typography name: MayWaveTypography
Shapes name: MayWaveShapes
```

---

## Do Not Create Unnecessary Font Folders

Do not create folders like:

```txt
font/
fonts/
font_element/
font_component/
font_ui/
```

inside the Kotlin package.

Font files belong in:

```txt
res/font/
```

Font definitions belong in:

```txt
ui/theme/
```

<!-- 한글:
폰트 파일은 Android 리소스라서 Kotlin 패키지 안에 폴더를 만들 필요가 없다.
-->

---

## Do Not Skip the Theme Layer

Do not connect font files directly to screens.

Bad flow:

```txt
res/font/*.ttf
        ↓
Text(fontFamily = ...)
```

Good flow:

```txt
res/font/*.ttf
        ↓
Font.kt
        ↓
Type.kt
        ↓
Theme.kt
        ↓
MainActivity.kt
        ↓
Text(style = MaterialTheme.typography.bodyLarge)
```

Reason:

```txt
The theme layer keeps font usage consistent across the app.
```

<!-- 한글:
폰트를 화면에서 바로 쓰면 나중에 수정하기 어렵다.
Theme 계층을 거쳐서 앱 전체에 적용하는 방식이 좋다.
-->

---

## Agent Work Order

When adding or modifying fonts, follow this order:

```txt
1. Check AGENTS.md
2. Check this SKILL.md
3. Check whether res/font already exists
4. Add font files to res/font if needed
5. Define FontFamily in ui/theme/Font.kt
6. Define or update Typography in ui/theme/Type.kt
7. Define or update ColorScheme in ui/theme/Color.kt if needed
8. Define or update Shapes in ui/theme/Shape.kt if needed
9. Create or update the custom app Theme in ui/theme/Theme.kt
10. Apply the custom app Theme in MainActivity.kt
11. Use MaterialTheme.typography in Composables
12. Wrap Preview with the custom app Theme
13. Write a 3-line code change summary
```

Do not skip `Theme.kt`.

Do not skip `MainActivity.kt`.

Do not apply font files directly in many Composables.

---

## Final Output Rule

After completing a font or theme-related code task, write a short summary:

```txt
Created code: ...
Deleted code: ...
Changed code: ...
```

Example:

```txt
Created code: Added `Font.kt` with `NanumMyeongjo` FontFamily and `MayWaveTheme` in `Theme.kt`.
Deleted code: None.
Changed code: Updated `MainActivity.kt` to wrap app content with `MayWaveTheme`.
```

If nothing was created or deleted, write `None`.

Example:

```txt
Created code: None.
Deleted code: None.
Changed code: Updated `Type.kt` so `MaterialTheme.typography.bodyLarge` uses NanumMyeongjo.
```

<!-- 한글:
폰트나 Theme 작업이 끝나면 만든 코드, 삭제한 코드, 변경한 코드를 3줄 정도로 정리한다.
-->

---

## Vocabulary

```txt
font file: 실제 폰트 파일, 예: .ttf, .otf
res/font: Android 폰트 리소스 폴더
FontFamily: Compose에서 여러 굵기의 폰트를 하나로 묶는 객체
FontWeight: 폰트 굵기, 예: Normal, Medium, Bold
Typography: 앱 전체 텍스트 스타일 묶음
TextStyle: 글자 크기, 굵기, 폰트, 줄 간격 등을 담는 스타일
ColorScheme: MaterialTheme에서 사용하는 앱 색상 묶음
Shapes: MaterialTheme에서 사용하는 공통 모양 묶음
MaterialTheme: Compose Material 3의 기본 테마 시스템
Custom App Theme: MayWaveTheme처럼 프로젝트 전용으로 만든 Theme 함수
MaterialTheme.typography: 현재 Theme에서 제공하는 공통 텍스트 스타일
MaterialTheme.colorScheme: 현재 Theme에서 제공하는 공통 색상
MaterialTheme.shapes: 현재 Theme에서 제공하는 공통 모양
MainActivity: 앱 시작 시 setContent로 Compose UI를 붙이는 Activity
```