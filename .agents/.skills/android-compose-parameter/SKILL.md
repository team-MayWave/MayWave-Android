---
name: android-compose-parameter
description: Android Jetpack Compose에서 파라미터와 리소스 애노테이션을 실무형으로 작성하기 위한 규칙
---
# Android Compose Parameter Skill

## 목적

Android Jetpack Compose 코드에서 Composable 함수, data class, UI 상태 모델을 작성할 때  
파라미터와 애노테이션을 읽기 쉽고 실무적인 방식으로 작성한다.

특히 `@DrawableRes`, `@StringRes`, `@ColorRes` 같은 Android 리소스 애노테이션은  
복잡한 use-site target 문법보다 일반적으로 읽기 쉬운 형태를 우선 사용한다.

---

## 핵심 규칙

### 1. 리소스 애노테이션은 기본적으로 일반 방식으로 작성한다

권장:

```kotlin
data class IntroRole(
    @DrawableRes val imageRes: Int,
    val imageDescription: String,
    val name: String,
    val description: String
)
```

비권장 :

```kotlin
data class IntroRole(
    @param:DrawableRes val imageRes: Int,
    val imageDescription: String,
    val name: String,
    val description: String
)
```