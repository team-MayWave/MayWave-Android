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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.maywave.audio.sound.MayWaveSoundPlayer
import com.example.maywave.chat.component.overlay.ChatInfoOverlay
import com.example.maywave.chat.component.overlay.chatInfoOverlayContentFor
import com.example.maywave.chat.navigation.ChatRoute
import com.example.maywave.chat.screen.CitizenChatScreen
import com.example.maywave.chat.screen.DoctorChatScreen
import com.example.maywave.chat.screen.ReporterChatScreen
import com.example.maywave.intro.screen.Intro
import com.example.maywave.intro.screen.SoundGuideScreen
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
                val soundPlayer = remember { MayWaveSoundPlayer(applicationContext) }
                var isLaunchScreenFinished by remember {
                    mutableStateOf(
                        SystemClock.uptimeMillis() - splashStartedAt >= SPLASH_DURATION_MILLIS
                    )
                }
                var currentRoute by rememberSaveable(stateSaver = ChatRouteSaver) {
                    mutableStateOf(ChatRoute.SoundGuide)
                }
                var hasPlayedBassImpactSound by rememberSaveable { mutableStateOf(false) }
                var showInfoUnreadDot by rememberSaveable { mutableStateOf(false) }
                var currentInfoStateKey by rememberSaveable { mutableStateOf<String?>(null) }
                var activeInfoStateKey by rememberSaveable { mutableStateOf<String?>(null) }
                var pendingChatRoute by remember { mutableStateOf<ChatRoute?>(null) }
                var transitionPhase by remember { mutableStateOf(RouteTransitionPhase.Idle) }
                val activeInfoContent = chatInfoOverlayContentFor(activeInfoStateKey)
                val isChatPaused = activeInfoContent != null
                val routeFadeDurationMillis = when (transitionPhase) {
                    RouteTransitionPhase.FadingOutSoundGuide,
                    RouteTransitionPhase.FadingOutChat,
                    RouteTransitionPhase.FadingInIntro -> BACK_TO_INTRO_FADE_DURATION_MILLIS

                    RouteTransitionPhase.Idle,
                    RouteTransitionPhase.FadingOutIntro,
                    RouteTransitionPhase.FadingInChat -> INTRO_TO_CHAT_FADE_DURATION_MILLIS
                }
                val screenAlpha by animateFloatAsState(
                    targetValue = when (transitionPhase) {
                        RouteTransitionPhase.FadingOutSoundGuide,
                        RouteTransitionPhase.FadingOutIntro,
                        RouteTransitionPhase.FadingOutChat -> 0f

                        RouteTransitionPhase.Idle,
                        RouteTransitionPhase.FadingInChat,
                        RouteTransitionPhase.FadingInIntro -> 1f
                    },
                    animationSpec = tween(durationMillis = routeFadeDurationMillis.toInt()),
                    label = "RouteTransitionAlpha"
                )

                DisposableEffect(soundPlayer) {
                    onDispose {
                        soundPlayer.release()
                    }
                }

                LaunchedEffect(Unit) {
                    val remainingSplashMillis = SPLASH_DURATION_MILLIS -
                        (SystemClock.uptimeMillis() - splashStartedAt)
                    if (remainingSplashMillis > 0L) {
                        delay(remainingSplashMillis)
                    }
                    isLaunchScreenFinished = true
                }

                LaunchedEffect(isLaunchScreenFinished, currentRoute, hasPlayedBassImpactSound) {
                    if (
                        isLaunchScreenFinished &&
                        currentRoute == ChatRoute.SoundGuide &&
                        !hasPlayedBassImpactSound
                    ) {
                        hasPlayedBassImpactSound = true
                        soundPlayer.playBassImpactSound()
                    }
                }

                LaunchedEffect(currentRoute) {
                    when {
                        currentRoute == ChatRoute.Intro -> soundPlayer.playIntroBackgroundMusic()
                        currentRoute.isChatRoute() -> soundPlayer.playChatBackgroundMusic()
                        else -> {
                            soundPlayer.fadeOutIntroBackgroundMusic()
                            soundPlayer.fadeOutChatBackgroundMusic()
                        }
                    }
                }

                LaunchedEffect(transitionPhase, pendingChatRoute) {
                    when (transitionPhase) {
                        RouteTransitionPhase.FadingOutSoundGuide -> {
                            delay(BACK_TO_INTRO_FADE_DURATION_MILLIS)
                            currentRoute = ChatRoute.Intro
                            transitionPhase = RouteTransitionPhase.FadingInIntro
                        }

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
                        soundPlayer.stopStartSound()
                        soundPlayer.stopDoctorRunToPatientSound()
                        soundPlayer.stopDoctorCrowdCryingSound()
                        soundPlayer.stopDoctorHeartbeatSound()
                        soundPlayer.stopCitizenBranchRadio()
                        soundPlayer.stopCitizenHelpFallenSceneSound()
                        soundPlayer.stopCitizenDistanceRecord()
                        soundPlayer.stopAirborneCrackdownSceneSound()
                        soundPlayer.stopRecordTypingSound()
                        soundPlayer.fadeOutChatBackgroundMusic()
                        pendingChatRoute = null
                        transitionPhase = RouteTransitionPhase.FadingOutChat
                    }
                }
                val startChatWithFade = { route: ChatRoute ->
                    if (transitionPhase == RouteTransitionPhase.Idle) {
                        soundPlayer.playChoiceClick()
                        soundPlayer.stopRoleVoice()
                        soundPlayer.playStartSound()
                        soundPlayer.fadeOutIntroBackgroundMusic()
                        pendingChatRoute = route
                        transitionPhase = RouteTransitionPhase.FadingOutIntro
                    }
                }
                val startIntroWithFade = {
                    if (transitionPhase == RouteTransitionPhase.Idle && currentRoute == ChatRoute.SoundGuide) {
                        transitionPhase = RouteTransitionPhase.FadingOutSoundGuide
                    }
                }
                val showInfoUnreadDotForState = { stateKey: String? ->
                    if (currentInfoStateKey != stateKey) {
                        currentInfoStateKey = stateKey
                        showInfoUnreadDot = stateKey != null
                    }
                }
                val showChatInfoOverlay = {
                    val stateKey = currentInfoStateKey
                    if (chatInfoOverlayContentFor(stateKey) != null) {
                        activeInfoStateKey = stateKey
                        showInfoUnreadDot = false
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
                            .blur(if (activeInfoContent != null) CHAT_INFO_BACKGROUND_BLUR else 0.dp)
                    ) {
                        when (currentRoute) {
                            ChatRoute.SoundGuide -> {
                                SoundGuideScreen(
                                    onTouchStart = startIntroWithFade,
                                    modifier = Modifier.systemBarsPadding()
                                )
                            }

                            ChatRoute.Citizen -> {
                                CitizenChatScreen(
                                    onBackClick = navigateBackToIntro,
                                    onInfoClick = showChatInfoOverlay,
                                    showInfoUnreadDot = showInfoUnreadDot,
                                    onInfoRead = {},
                                    onInfoUnreadStateShown = showInfoUnreadDotForState,
                                    isChatPaused = isChatPaused,
                                    onIntroSceneFinished = soundPlayer::stopStartSound,
                                    onChoiceClickSound = soundPlayer::playChoiceClick,
                                    onPlayBranchRadio = soundPlayer::playCitizenBranchRadio,
                                    onStopBranchRadio = soundPlayer::stopCitizenBranchRadio,
                                    onPlayHelpFallenSceneSound = soundPlayer::playCitizenHelpFallenSceneSound,
                                    onStopHelpFallenSceneSound = soundPlayer::stopCitizenHelpFallenSceneSound,
                                    onPlayDistanceRecordSound = soundPlayer::playCitizenDistanceRecord,
                                    onStopDistanceRecordSound = soundPlayer::stopCitizenDistanceRecord,
                                    onPlayAirborneCrackdownSceneSound = soundPlayer::playAirborneCrackdownSceneSound,
                                    onFadeOutAirborneCrackdownSceneSound = soundPlayer::fadeOutAirborneCrackdownSceneSound,
                                    onStopAirborneCrackdownSceneSound = soundPlayer::stopAirborneCrackdownSceneSound,
                                    onPlayRecordTypingSound = soundPlayer::playRecordTypingSound,
                                    onStopRecordTypingSound = soundPlayer::stopRecordTypingSound,
                                    onFadeOutChatBackgroundMusic = soundPlayer::fadeOutChatBackgroundMusic,
                                    modifier = Modifier.systemBarsPadding()
                                )
                            }

                            ChatRoute.Doctor -> {
                                DoctorChatScreen(
                                    onBackClick = navigateBackToIntro,
                                    onInfoClick = showChatInfoOverlay,
                                    showInfoUnreadDot = showInfoUnreadDot,
                                    onInfoRead = {},
                                    onInfoUnreadStateShown = showInfoUnreadDotForState,
                                    isChatPaused = isChatPaused,
                                    onIntroSceneFinished = soundPlayer::stopStartSound,
                                    onChoiceClickSound = soundPlayer::playChoiceClick,
                                    onPlayRunToPatientSound = soundPlayer::playDoctorRunToPatientSound,
                                    onStopRunToPatientSound = soundPlayer::stopDoctorRunToPatientSound,
                                    onPlayCrowdCryingSound = soundPlayer::playDoctorCrowdCryingSound,
                                    onFadeOutCrowdCryingSound = soundPlayer::fadeOutDoctorCrowdCryingSound,
                                    onStopCrowdCryingSound = soundPlayer::stopDoctorCrowdCryingSound,
                                    onPlayHeartbeatSound = soundPlayer::playDoctorHeartbeatSound,
                                    onFadeOutHeartbeatSound = soundPlayer::fadeOutDoctorHeartbeatSound,
                                    onStopHeartbeatSound = soundPlayer::stopDoctorHeartbeatSound,
                                    onPlayRecordTypingSound = soundPlayer::playRecordTypingSound,
                                    onStopRecordTypingSound = soundPlayer::stopRecordTypingSound,
                                    onFadeOutChatBackgroundMusic = soundPlayer::fadeOutChatBackgroundMusic,
                                    modifier = Modifier.systemBarsPadding()
                                )
                            }

                            ChatRoute.Reporter -> {
                                ReporterChatScreen(
                                    onBackClick = navigateBackToIntro,
                                    onInfoClick = showChatInfoOverlay,
                                    showInfoUnreadDot = showInfoUnreadDot,
                                    onInfoRead = {},
                                    onInfoUnreadStateShown = showInfoUnreadDotForState,
                                    isChatPaused = isChatPaused,
                                    onIntroSceneFinished = soundPlayer::stopStartSound,
                                    onChoiceClickSound = soundPlayer::playChoiceClick,
                                    onPlayAirborneCrackdownSceneSound = soundPlayer::playAirborneCrackdownSceneSound,
                                    onFadeOutAirborneCrackdownSceneSound = soundPlayer::fadeOutAirborneCrackdownSceneSound,
                                    onStopAirborneCrackdownSceneSound = soundPlayer::stopAirborneCrackdownSceneSound,
                                    onPlayRecordTypingSound = soundPlayer::playRecordTypingSound,
                                    onStopRecordTypingSound = soundPlayer::stopRecordTypingSound,
                                    onFadeOutChatBackgroundMusic = soundPlayer::fadeOutChatBackgroundMusic,
                                    modifier = Modifier.systemBarsPadding()
                                )
                            }

                            ChatRoute.Intro -> {
                                Intro(
                                    onStartChat = startChatWithFade,
                                    isRoleVoiceEnabled = isLaunchScreenFinished,
                                    onPlayRoleVoice = soundPlayer::playRoleVoice,
                                    onStopRoleVoice = soundPlayer::stopRoleVoice
                                )
                            }
                        }
                    }

                    activeInfoContent?.let { content ->
                        ChatInfoOverlay(
                            content = content,
                            onCloseClick = { activeInfoStateKey = null },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }

    private companion object {
        const val SPLASH_DURATION_MILLIS = 1_500L
        const val INTRO_TO_CHAT_FADE_DURATION_MILLIS = 1_500L
        const val BACK_TO_INTRO_FADE_DURATION_MILLIS = 2_000L
        val CHAT_INFO_BACKGROUND_BLUR = 2.dp

        val ChatRouteSaver = Saver<ChatRoute, String>(
            save = { it.route },
            restore = { ChatRoute.fromRoute(it) }
        )
    }
}

private enum class RouteTransitionPhase {
    Idle,
    FadingOutSoundGuide,
    FadingOutIntro,
    FadingInChat,
    FadingOutChat,
    FadingInIntro
}

private fun ChatRoute.isChatRoute(): Boolean {
    return this == ChatRoute.Citizen || this == ChatRoute.Doctor || this == ChatRoute.Reporter
}
