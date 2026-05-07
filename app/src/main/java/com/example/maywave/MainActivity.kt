package com.example.maywave

import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.maywave.chat.navigation.ChatRoute
import com.example.maywave.chat.screen.CitizenChatScreen
import com.example.maywave.chat.screen.DoctorChatScreen
import com.example.maywave.chat.screen.ReporterChatScreen
import com.example.maywave.intro.screen.Intro
import com.example.maywave.ui.theme.MayWaveTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val splashStartedAt = SystemClock.uptimeMillis()
        installSplashScreen().setKeepOnScreenCondition {
            SystemClock.uptimeMillis() - splashStartedAt < SPLASH_DURATION_MILLIS
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MayWaveTheme {
                var currentRoute by rememberSaveable(stateSaver = ChatRouteSaver) {
                    mutableStateOf(ChatRoute.Intro)
                }
                var pendingChatRoute by remember { mutableStateOf<ChatRoute?>(null) }
                var transitionPhase by remember { mutableStateOf(RouteTransitionPhase.Idle) }
                val routeFadeDurationMillis = when (transitionPhase) {
                    RouteTransitionPhase.FadingOutChat,
                    RouteTransitionPhase.FadingInIntro -> BACK_TO_INTRO_FADE_DURATION_MILLIS

                    RouteTransitionPhase.Idle,
                    RouteTransitionPhase.FadingOutIntro,
                    RouteTransitionPhase.FadingInChat -> INTRO_TO_CHAT_FADE_DURATION_MILLIS
                }
                val screenAlpha by animateFloatAsState(
                    targetValue = when (transitionPhase) {
                        RouteTransitionPhase.FadingOutIntro,
                        RouteTransitionPhase.FadingOutChat -> 0f

                        RouteTransitionPhase.Idle,
                        RouteTransitionPhase.FadingInChat,
                        RouteTransitionPhase.FadingInIntro -> 1f
                    },
                    animationSpec = tween(durationMillis = routeFadeDurationMillis.toInt()),
                    label = "RouteTransitionAlpha"
                )

                LaunchedEffect(transitionPhase, pendingChatRoute) {
                    when (transitionPhase) {
                        RouteTransitionPhase.FadingOutIntro -> {
                            val chatRoute = pendingChatRoute ?: return@LaunchedEffect
                            delay(INTRO_TO_CHAT_FADE_DURATION_MILLIS)
                            currentRoute = chatRoute
                            transitionPhase = RouteTransitionPhase.FadingInChat
                        }

                        RouteTransitionPhase.FadingInChat -> {
                            delay(INTRO_TO_CHAT_FADE_DURATION_MILLIS)
                            pendingChatRoute = null
                            transitionPhase = RouteTransitionPhase.Idle
                        }

                        RouteTransitionPhase.FadingOutChat -> {
                            delay(BACK_TO_INTRO_FADE_DURATION_MILLIS)
                            currentRoute = ChatRoute.Intro
                            transitionPhase = RouteTransitionPhase.FadingInIntro
                        }

                        RouteTransitionPhase.FadingInIntro -> {
                            delay(BACK_TO_INTRO_FADE_DURATION_MILLIS)
                            transitionPhase = RouteTransitionPhase.Idle
                        }

                        RouteTransitionPhase.Idle -> Unit
                    }
                }

                val navigateBackToIntro = {
                    if (transitionPhase == RouteTransitionPhase.Idle && currentRoute != ChatRoute.Intro) {
                        pendingChatRoute = null
                        transitionPhase = RouteTransitionPhase.FadingOutChat
                    }
                }
                val startChatWithFade = { route: ChatRoute ->
                    if (transitionPhase == RouteTransitionPhase.Idle) {
                        pendingChatRoute = route
                        transitionPhase = RouteTransitionPhase.FadingOutIntro
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(screenAlpha)
                    ) {
                        when (currentRoute) {
                            ChatRoute.Citizen -> {
                                CitizenChatScreen(
                                    onBackClick = navigateBackToIntro,
                                    modifier = Modifier.systemBarsPadding()
                                )
                            }

                            ChatRoute.Doctor -> {
                                DoctorChatScreen(
                                    onBackClick = navigateBackToIntro,
                                    modifier = Modifier.systemBarsPadding()
                                )
                            }

                            ChatRoute.Reporter -> {
                                ReporterChatScreen(
                                    onBackClick = navigateBackToIntro,
                                    modifier = Modifier.systemBarsPadding()
                                )
                            }

                            ChatRoute.Intro -> {
                                Intro(onStartChat = startChatWithFade)
                            }
                        }
                    }
                }
            }
        }
    }

    private companion object {
        const val SPLASH_DURATION_MILLIS = 1_500L
        const val INTRO_TO_CHAT_FADE_DURATION_MILLIS = 1_500L
        const val BACK_TO_INTRO_FADE_DURATION_MILLIS = 2_000L

        val ChatRouteSaver = Saver<ChatRoute, String>(
            save = { it.route },
            restore = { ChatRoute.fromRoute(it) }
        )
    }
}

private enum class RouteTransitionPhase {
    Idle,
    FadingOutIntro,
    FadingInChat,
    FadingOutChat,
    FadingInIntro
}
