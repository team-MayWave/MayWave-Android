package com.example.maywave.chat.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.maywave.R
import com.example.maywave.chat.component.choice.ChatChoiceElement
import com.example.maywave.chat.component.header.ChatInfoButton
import com.example.maywave.chat.component.header.ChatTopTitle
import com.example.maywave.chat.component.header.ElementTitle
import com.example.maywave.chat.component.media.ChatSceneImage
import com.example.maywave.chat.component.message.ChatFirstText
import com.example.maywave.chat.component.message.ChatNarrationText
import com.example.maywave.chat.component.message.MyChatElement
import com.example.maywave.chat.component.message.OtherChatElement
import com.example.maywave.chat.component.navigation.ChatBackButton
import com.example.maywave.chat.component.overlay.ChatFinalFadeOverlay
import com.example.maywave.chat.component.overlay.ChatFinalStep
import com.example.maywave.chat.component.record.ChatRecordDetailContent
import com.example.maywave.chat.component.record.ChatRecordDetailTransition
import com.example.maywave.chat.component.record.ChatRecordTypingAutoScrollProvider
import com.example.maywave.chat.viewmodel.ChatGameRequest
import com.example.maywave.chat.viewmodel.ChatGameUiState
import com.example.maywave.chat.viewmodel.ChatGameViewModel
import com.example.maywave.chat.viewmodel.ChatGameViewModelFactory
import com.example.maywave.ui.theme.MayWaveTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged

private const val CHAT_ELEMENT_REVEAL_DURATION_MILLIS = 2_000
private const val SERVER_RESULT_NEXT_REVEAL_DELAY_MILLIS = 4_000
private const val INITIAL_CHAT_ELEMENT_COUNT = 8
private const val INTRO_SOUND_STOP_ITEM_COUNT = 3
private const val CLOSE_BRANCH_ELEMENT_COUNT = 10
private const val CLOSE_BRANCH_RADIO_START_ITEM_COUNT = 3
private const val CLOSE_BRANCH_RADIO_STOP_ITEM_COUNT = 10
private const val HELP_FALLEN_ELEMENT_COUNT = 8
private const val AVOID_SITUATION_ELEMENT_COUNT = 10
private const val DISTANCE_BRANCH_ELEMENT_COUNT = 10
private const val HELP_FALLEN_SCENE_SOUND_START_ITEM_COUNT = 2
private const val HELP_FALLEN_RECORD_ITEM_INDEX = 6
private const val AVOID_SITUATION_RECORD_ITEM_INDEX = 8
private const val DISTANCE_RECORD_ITEM_INDEX = 7
private const val AUTO_SCROLL_LAYOUT_DELAY_MILLIS = 100
private const val AUTO_SCROLL_ANIMATION_DURATION_MILLIS = 650
private const val CHAT_ITEM_REVEAL_DELAY_MILLIS = 0
private val CHAT_ELEMENT_SPACING = 35.dp
private val BRANCH_CHAT_ELEMENT_SPACING = 35.dp
private val AUTO_SCROLL_BOTTOM_PADDING = 16.dp
private val CitizenCloseRequest = ChatGameRequest(roleId = 2, scenarioId = 1, choice = 1)
private val CitizenDistanceRequest = ChatGameRequest(roleId = 2, scenarioId = 1, choice = 2)
private val CitizenHelpFallenRequest = ChatGameRequest(roleId = 2, scenarioId = 2, choice = 1)
private val CitizenAvoidSituationRequest = ChatGameRequest(roleId = 2, scenarioId = 2, choice = 2)

@Composable
fun CitizenChatScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onInfoClick: () -> Unit = {},
    showInfoUnreadDot: Boolean = true,
    onInfoRead: () -> Unit = {},
    onInfoUnreadStateShown: (String?) -> Unit = {},
    isChatPaused: Boolean = false,
    onIntroSceneFinished: () -> Unit = {},
    onChoiceClickSound: () -> Unit = {},
    onPlayBranchRadio: () -> Unit = {},
    onStopBranchRadio: () -> Unit = {},
    onPlayHelpFallenSceneSound: () -> Unit = {},
    onStopHelpFallenSceneSound: () -> Unit = {},
    onPlayDistanceRecordSound: () -> Unit = {},
    onStopDistanceRecordSound: () -> Unit = {},
    onPlayAirborneCrackdownSceneSound: () -> Unit = {},
    onFadeOutAirborneCrackdownSceneSound: () -> Unit = {},
    onStopAirborneCrackdownSceneSound: () -> Unit = {},
    onPlayRecordTypingSound: () -> Unit = {},
    onStopRecordTypingSound: () -> Unit = {},
    onFadeOutChatBackgroundMusic: () -> Unit = {}
) {
    val listState = rememberLazyListState()
    val chatGameViewModel: ChatGameViewModel = viewModel(
        factory = remember { ChatGameViewModelFactory() }
    )
    val chatGameUiState by chatGameViewModel.uiState.collectAsState()
    var selectedBranch by rememberSaveable { mutableStateOf<CitizenChatBranch?>(null) }
    var selectedCloseBranch by rememberSaveable { mutableStateOf<CitizenCloseBranch?>(null) }
    var branchRevealKey by rememberSaveable { mutableIntStateOf(0) }
    var closeBranchRevealKey by rememberSaveable { mutableIntStateOf(0) }
    var isHelpFallenRecordTransitionFinished by rememberSaveable { mutableStateOf(false) }
    var isAvoidSituationRecordTypingFinished by rememberSaveable { mutableStateOf(false) }
    var isDistanceRecordTypingFinished by rememberSaveable { mutableStateOf(false) }
    var hasNotifiedIntroSceneFinished by rememberSaveable { mutableStateOf(false) }
    var isBranchRadioPlaying by rememberSaveable { mutableStateOf(false) }
    var isHelpFallenSceneSoundPlaying by remember { mutableStateOf(false) }
    var isDistanceBranchSceneSoundPlaying by remember { mutableStateOf(false) }
    val shouldFollowNewContent = rememberShouldFollowNewContent(listState = listState)
    val isRevealPaused = isChatPaused || !shouldFollowNewContent
    val currentOnIntroSceneFinished by rememberUpdatedState(onIntroSceneFinished)
    val currentOnPlayBranchRadio by rememberUpdatedState(onPlayBranchRadio)
    val currentOnStopBranchRadio by rememberUpdatedState(onStopBranchRadio)
    val currentOnPlayHelpFallenSceneSound by rememberUpdatedState(onPlayHelpFallenSceneSound)
    val currentOnStopHelpFallenSceneSound by rememberUpdatedState(onStopHelpFallenSceneSound)
    val currentOnPlayDistanceRecordSound by rememberUpdatedState(onPlayDistanceRecordSound)
    val currentOnStopDistanceRecordSound by rememberUpdatedState(onStopDistanceRecordSound)
    val currentOnPlayAirborneCrackdownSceneSound by rememberUpdatedState(onPlayAirborneCrackdownSceneSound)
    val currentOnFadeOutAirborneCrackdownSceneSound by rememberUpdatedState(onFadeOutAirborneCrackdownSceneSound)
    val currentOnStopAirborneCrackdownSceneSound by rememberUpdatedState(onStopAirborneCrackdownSceneSound)
    val currentOnPlayRecordTypingSound by rememberUpdatedState(onPlayRecordTypingSound)
    val currentOnStopRecordTypingSound by rememberUpdatedState(onStopRecordTypingSound)
    val currentOnFadeOutChatBackgroundMusic by rememberUpdatedState(onFadeOutChatBackgroundMusic)
    val closeItemCount = if (chatGameUiState.hasErrorFor(CitizenCloseRequest)) {
        1
    } else {
        CLOSE_BRANCH_ELEMENT_COUNT
    }
    val distanceItemCount = if (chatGameUiState.hasErrorFor(CitizenDistanceRequest)) {
        1
    } else {
        DISTANCE_BRANCH_ELEMENT_COUNT
    }
    val helpFallenItemCount = if (chatGameUiState.hasErrorFor(CitizenHelpFallenRequest)) {
        1
    } else {
        HELP_FALLEN_ELEMENT_COUNT
    }
    val avoidSituationItemCount = if (chatGameUiState.hasErrorFor(CitizenAvoidSituationRequest)) {
        1
    } else {
        AVOID_SITUATION_ELEMENT_COUNT
    }
    val revealedInitialItemCount = rememberSequentialRevealCount(
        itemCount = INITIAL_CHAT_ELEMENT_COUNT,
        initiallyVisibleItemCount = 1,
        isPaused = isRevealPaused
    )
    val revealedCloseItemCount = rememberSequentialRevealCount(
        itemCount = closeItemCount,
        revealKey = branchRevealKey,
        enabled = selectedBranch == CitizenChatBranch.Close &&
            chatGameUiState.isResultReadyFor(CitizenCloseRequest),
        firstItemNextRevealDelayMillis = SERVER_RESULT_NEXT_REVEAL_DELAY_MILLIS,
        isPaused = isRevealPaused
    )
    val revealedDistanceItemCount = rememberSequentialRevealCount(
        itemCount = distanceItemCount,
        revealKey = branchRevealKey,
        enabled = selectedBranch == CitizenChatBranch.Distance &&
            chatGameUiState.isResultReadyFor(CitizenDistanceRequest),
        blockedAfterItemIndex = DISTANCE_RECORD_ITEM_INDEX,
        canRevealAfterBlockedItem = isDistanceRecordTypingFinished,
        firstItemNextRevealDelayMillis = SERVER_RESULT_NEXT_REVEAL_DELAY_MILLIS,
        isPaused = isRevealPaused
    )
    val revealedHelpFallenItemCount = rememberSequentialRevealCount(
        itemCount = helpFallenItemCount,
        revealKey = closeBranchRevealKey,
        enabled = selectedCloseBranch == CitizenCloseBranch.HelpFallen &&
            chatGameUiState.isResultReadyFor(CitizenHelpFallenRequest),
        blockedAfterItemIndex = HELP_FALLEN_RECORD_ITEM_INDEX,
        canRevealAfterBlockedItem = isHelpFallenRecordTransitionFinished,
        firstItemNextRevealDelayMillis = SERVER_RESULT_NEXT_REVEAL_DELAY_MILLIS,
        isPaused = isRevealPaused
    )
    val revealedAvoidSituationItemCount = rememberSequentialRevealCount(
        itemCount = avoidSituationItemCount,
        revealKey = closeBranchRevealKey,
        enabled = selectedCloseBranch == CitizenCloseBranch.AvoidSituation &&
            chatGameUiState.isResultReadyFor(CitizenAvoidSituationRequest),
        blockedAfterItemIndex = AVOID_SITUATION_RECORD_ITEM_INDEX,
        canRevealAfterBlockedItem = isAvoidSituationRecordTypingFinished,
        firstItemNextRevealDelayMillis = SERVER_RESULT_NEXT_REVEAL_DELAY_MILLIS,
        isPaused = isRevealPaused
    )
    val finalSequenceKey = when {
        selectedCloseBranch == CitizenCloseBranch.HelpFallen &&
            revealedHelpFallenItemCount >= HELP_FALLEN_ELEMENT_COUNT &&
            isHelpFallenRecordTransitionFinished -> "help_fallen"
        selectedCloseBranch == CitizenCloseBranch.AvoidSituation &&
            revealedAvoidSituationItemCount >= AVOID_SITUATION_ELEMENT_COUNT &&
            isAvoidSituationRecordTypingFinished -> "avoid_situation"
        selectedBranch == CitizenChatBranch.Distance &&
            revealedDistanceItemCount >= DISTANCE_BRANCH_ELEMENT_COUNT &&
            isDistanceRecordTypingFinished -> "distance"
        else -> null
    }
    val infoUnreadStateKey = citizenInfoUnreadStateKey(
        selectedBranch = selectedBranch,
        selectedCloseBranch = selectedCloseBranch,
        revealedInitialItemCount = revealedInitialItemCount,
        revealedCloseItemCount = revealedCloseItemCount,
        revealedDistanceItemCount = revealedDistanceItemCount,
        revealedHelpFallenItemCount = revealedHelpFallenItemCount,
        revealedAvoidSituationItemCount = revealedAvoidSituationItemCount,
        finalSequenceKey = finalSequenceKey
    )
    val activeRecordDetailContent = citizenActiveRecordDetailContent(
        selectedBranch = selectedBranch,
        selectedCloseBranch = selectedCloseBranch,
        revealedHelpFallenItemCount = revealedHelpFallenItemCount,
        revealedAvoidSituationItemCount = revealedAvoidSituationItemCount,
        revealedDistanceItemCount = revealedDistanceItemCount
    )

    LaunchedEffect(infoUnreadStateKey) {
        onInfoUnreadStateShown(infoUnreadStateKey)
    }

    LaunchedEffect(revealedInitialItemCount, isRevealPaused, hasNotifiedIntroSceneFinished) {
        if (
            !hasNotifiedIntroSceneFinished &&
            !isRevealPaused &&
            revealedInitialItemCount >= INTRO_SOUND_STOP_ITEM_COUNT
        ) {
            hasNotifiedIntroSceneFinished = true
            currentOnIntroSceneFinished()
        }
    }

    LaunchedEffect(selectedBranch, revealedCloseItemCount, isRevealPaused, isBranchRadioPlaying) {
        val shouldPlayBranchRadio = selectedBranch == CitizenChatBranch.Close &&
            !isRevealPaused &&
            revealedCloseItemCount in CLOSE_BRANCH_RADIO_START_ITEM_COUNT until CLOSE_BRANCH_RADIO_STOP_ITEM_COUNT

        if (shouldPlayBranchRadio && !isBranchRadioPlaying) {
            currentOnPlayBranchRadio()
            isBranchRadioPlaying = true
        } else if (!shouldPlayBranchRadio && isBranchRadioPlaying) {
            currentOnStopBranchRadio()
            isBranchRadioPlaying = false
        }
    }

    LaunchedEffect(
        selectedCloseBranch,
        revealedHelpFallenItemCount,
        isRevealPaused,
        isHelpFallenSceneSoundPlaying
    ) {
        val shouldPlayHelpFallenSceneSound = selectedCloseBranch == CitizenCloseBranch.HelpFallen &&
            !isRevealPaused &&
            revealedHelpFallenItemCount in HELP_FALLEN_SCENE_SOUND_START_ITEM_COUNT until HELP_FALLEN_RECORD_ITEM_INDEX

        if (shouldPlayHelpFallenSceneSound && !isHelpFallenSceneSoundPlaying) {
            currentOnPlayHelpFallenSceneSound()
            isHelpFallenSceneSoundPlaying = true
        } else if (!shouldPlayHelpFallenSceneSound && isHelpFallenSceneSoundPlaying) {
            currentOnStopHelpFallenSceneSound()
            isHelpFallenSceneSoundPlaying = false
        }
    }

    LaunchedEffect(
        selectedBranch,
        revealedDistanceItemCount,
        isRevealPaused,
        isDistanceBranchSceneSoundPlaying
    ) {
        val shouldPlayDistanceBranchSceneSound = selectedBranch == CitizenChatBranch.Distance &&
            !isRevealPaused &&
            revealedDistanceItemCount < DISTANCE_RECORD_ITEM_INDEX

        when {
            shouldPlayDistanceBranchSceneSound && !isDistanceBranchSceneSoundPlaying -> {
                currentOnPlayAirborneCrackdownSceneSound()
                isDistanceBranchSceneSoundPlaying = true
            }

            !shouldPlayDistanceBranchSceneSound && isDistanceBranchSceneSoundPlaying &&
                selectedBranch == CitizenChatBranch.Distance &&
                revealedDistanceItemCount >= DISTANCE_RECORD_ITEM_INDEX -> {
                currentOnFadeOutAirborneCrackdownSceneSound()
                isDistanceBranchSceneSoundPlaying = false
            }

            !shouldPlayDistanceBranchSceneSound && isDistanceBranchSceneSoundPlaying -> {
                currentOnStopAirborneCrackdownSceneSound()
                isDistanceBranchSceneSoundPlaying = false
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            currentOnStopBranchRadio()
            currentOnStopHelpFallenSceneSound()
            currentOnStopDistanceRecordSound()
            currentOnStopAirborneCrackdownSceneSound()
            currentOnStopRecordTypingSound()
        }
    }

    AutoScrollOnReveal(
        listState = listState,
        revealKey = revealedInitialItemCount,
        enabled = revealedInitialItemCount > 1,
        targetItemIndex = revealedInitialItemCount - 1,
        shouldFollowNewContent = shouldFollowNewContent
    )

    AutoScrollOnReveal(
        listState = listState,
        revealKey = revealedCloseItemCount,
        enabled = selectedBranch == CitizenChatBranch.Close && revealedCloseItemCount > 0,
        targetItemIndex = INITIAL_CHAT_ELEMENT_COUNT,
        shouldFollowNewContent = shouldFollowNewContent
    )

    AutoScrollOnReveal(
        listState = listState,
        revealKey = revealedDistanceItemCount,
        enabled = selectedBranch == CitizenChatBranch.Distance &&
            revealedDistanceItemCount in 1 until DISTANCE_BRANCH_ELEMENT_COUNT,
        targetItemIndex = INITIAL_CHAT_ELEMENT_COUNT,
        shouldFollowNewContent = shouldFollowNewContent
    )

    AutoScrollOnReveal(
        listState = listState,
        revealKey = revealedHelpFallenItemCount,
        enabled = selectedCloseBranch == CitizenCloseBranch.HelpFallen &&
            revealedHelpFallenItemCount in 1 until HELP_FALLEN_ELEMENT_COUNT,
        targetItemIndex = INITIAL_CHAT_ELEMENT_COUNT,
        shouldFollowNewContent = shouldFollowNewContent
    )

    AutoScrollOnReveal(
        listState = listState,
        revealKey = revealedAvoidSituationItemCount,
        enabled = selectedCloseBranch == CitizenCloseBranch.AvoidSituation &&
            revealedAvoidSituationItemCount in 1 until AVOID_SITUATION_ELEMENT_COUNT,
        targetItemIndex = INITIAL_CHAT_ELEMENT_COUNT,
        shouldFollowNewContent = shouldFollowNewContent
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        ChatRecordDetailTransition(
            content = activeRecordDetailContent,
            onRecordTypingFinished = {
                currentOnFadeOutChatBackgroundMusic()
                when {
                    selectedCloseBranch == CitizenCloseBranch.HelpFallen -> {
                        isHelpFallenRecordTransitionFinished = true
                    }

                    selectedCloseBranch == CitizenCloseBranch.AvoidSituation -> {
                        isAvoidSituationRecordTypingFinished = true
                    }

                    selectedBranch == CitizenChatBranch.Distance -> {
                        currentOnStopDistanceRecordSound()
                        isDistanceRecordTypingFinished = true
                    }
                }
            },
            modifier = Modifier.fillMaxSize(),
            isPaused = isRevealPaused,
            onRecordScreenShown = {
                if (selectedBranch == CitizenChatBranch.Distance) {
                    currentOnPlayDistanceRecordSound()
                }
            },
            onRecordTypingAnimationStarted = currentOnPlayRecordTypingSound,
            onRecordTypingAnimationFinished = currentOnStopRecordTypingSound
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                ChatHeader(
                    onBackClick = onBackClick,
                    onInfoClick = onInfoClick,
                    showInfoUnreadDot = showInfoUnreadDot && infoUnreadStateKey != null,
                    onInfoRead = onInfoRead
                )

                LazyColumn(
                    state = listState,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(CHAT_ELEMENT_SPACING),
                    contentPadding = PaddingValues(top = 49.dp, bottom = 48.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    item {
                        AnimatedChatItem(visible = revealedInitialItemCount >= 1) {
                            ElementTitle()
                        }
                    }

                    item {
                        AnimatedChatItem(visible = revealedInitialItemCount >= 2) {
                            ChatSceneImage(
                                imageResId = R.drawable.chat_first_img,
                                contentDescription = "금남로에 모인 시민들",
                                height = 214.dp
                            )
                        }
                    }

                    item {
                        AnimatedChatItem(visible = revealedInitialItemCount >= 3) {
                            ChatFirstText(text = "시내 분위기가 심상치 않습니다")
                        }
                    }

                    item {
                        AnimatedChatItem(visible = revealedInitialItemCount >= 4) {
                            ChatFirstText(text = "사람들이 모여들기 시작합니다")
                        }
                    }

                    item {
                        AnimatedChatItem(visible = revealedInitialItemCount >= 5) {
                            OtherChatElement(
                                nameText = "친구",
                                chatText = "전남대 쪽에서 학생들이 막혔다더라. 계엄군이 들어왔대."
                            )
                        }
                    }

                    item {
                        AnimatedChatItem(visible = revealedInitialItemCount >= 6) {
                            MyChatElement(chatText = "무슨 일인데?")
                        }
                    }

                    item {
                        AnimatedChatItem(visible = revealedInitialItemCount >= 7) {
                            ChatNarrationText(text = "잠시 후, 군인들이 시내로 이동합니다.")
                        }
                    }

                    item {
                        AnimatedChatItem(visible = revealedInitialItemCount >= 8) {
                            ChatChoiceElement(
                                firstChoiceText = "가까이 가서 본다",
                                secondChoiceText = "멀리서 지켜 본다",
                                onFirstChoiceClick = {
                                    selectedBranch = CitizenChatBranch.Close
                                    branchRevealKey += 1
                                    chatGameViewModel.submitChoice(CitizenCloseRequest)
                                },
                                onSecondChoiceClick = {
                                    selectedBranch = CitizenChatBranch.Distance
                                    branchRevealKey += 1
                                    chatGameViewModel.submitChoice(CitizenDistanceRequest)
                                },
                                selectedChoiceIndex = when (selectedBranch) {
                                    CitizenChatBranch.Close -> 0
                                    CitizenChatBranch.Distance -> 1
                                    null -> null
                                },
                                onChoiceClickSound = onChoiceClickSound
                            )
                        }
                    }

                    when (selectedBranch) {
                        CitizenChatBranch.Close -> {
                            item {
                                ChatRecordTypingAutoScrollProvider(
                                    enabled = shouldFollowNewContent
                                ) {
                                    CloseBranchContent(
                                        selectedCloseBranch = selectedCloseBranch,
                                        chatGameUiState = chatGameUiState,
                                        revealedItemCount = revealedCloseItemCount,
                                        revealedHelpFallenItemCount = revealedHelpFallenItemCount,
                                        revealedAvoidSituationItemCount = revealedAvoidSituationItemCount,
                                        onHelpFallenClick = {
                                            selectedCloseBranch = CitizenCloseBranch.HelpFallen
                                            closeBranchRevealKey += 1
                                            chatGameViewModel.submitChoice(CitizenHelpFallenRequest)
                                        },
                                        onAvoidSituationClick = {
                                            selectedCloseBranch = CitizenCloseBranch.AvoidSituation
                                            closeBranchRevealKey += 1
                                            chatGameViewModel.submitChoice(CitizenAvoidSituationRequest)
                                        },
                                        onChoiceClickSound = onChoiceClickSound
                                    )
                                }
                            }
                        }

                        CitizenChatBranch.Distance -> {
                            item {
                                ChatRecordTypingAutoScrollProvider(
                                    enabled = shouldFollowNewContent
                                ) {
                                    DistanceBranchContent(
                                        resultText = chatGameUiState.resultTextFor(CitizenDistanceRequest),
                                        revealedItemCount = revealedDistanceItemCount
                                    )
                                }
                            }
                        }

                        null -> Unit
                    }
                }
            }
        }

        if (finalSequenceKey != null) {
            ChatFinalFadeOverlay(
                sequenceKey = finalSequenceKey,
                steps = listOf(
                    ChatFinalStep(
                        text = "그날의 시간은 끝났지만,\n그날의 이야기는\n아직 끝나지 않았습니다.\n우리는,\n그날을 기억합니다.",
                        showDate = true
                    )
                ),
                onBackClick = onBackClick
            )
        }
    }
}

@Composable
private fun ChatHeader(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onInfoClick: () -> Unit,
    showInfoUnreadDot: Boolean,
    onInfoRead: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
    ) {

        ChatBackButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 28.dp, top = 18.dp)
        )

        ChatTopTitle(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
        )

        ChatInfoButton(
            onClick = onInfoClick,
            showUnreadDot = showInfoUnreadDot,
            onRead = onInfoRead,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 30.dp, top = 18.dp)
        )
    }
}

@Composable
private fun rememberSequentialRevealCount(
    itemCount: Int,
    revealKey: Any? = Unit,
    initiallyVisibleItemCount: Int = 0,
    enabled: Boolean = true,
    blockedAfterItemIndex: Int? = null,
    canRevealAfterBlockedItem: Boolean = true,
    firstItemNextRevealDelayMillis: Int = CHAT_ELEMENT_REVEAL_DURATION_MILLIS,
    isPaused: Boolean = false
): Int {
    val initialItemCount = initiallyVisibleItemCount.coerceIn(0, itemCount)
    val blockedItemIndex = blockedAfterItemIndex?.coerceIn(0, itemCount)
    var revealedItemCount by rememberSaveable(revealKey) {
        mutableIntStateOf(initiallyVisibleItemCount.coerceIn(0, itemCount))
    }

    LaunchedEffect(
        revealKey,
        itemCount,
        initialItemCount,
        enabled,
        blockedItemIndex,
        canRevealAfterBlockedItem,
        firstItemNextRevealDelayMillis,
        isPaused
    ) {
        if (!enabled) {
            revealedItemCount = 0
            return@LaunchedEffect
        }

        if (isPaused) return@LaunchedEffect

        revealedItemCount = revealedItemCount.coerceAtLeast(initialItemCount)

        while (revealedItemCount < itemCount) {
            if (
                blockedItemIndex != null &&
                revealedItemCount >= blockedItemIndex &&
                !canRevealAfterBlockedItem
            ) {
                return@LaunchedEffect
            }

            val revealDelayMillis = if (revealedItemCount == 1) {
                firstItemNextRevealDelayMillis
            } else {
                CHAT_ELEMENT_REVEAL_DURATION_MILLIS
            }
            delay(revealDelayMillis.toLong())
            revealedItemCount += 1
        }
    }

    return if (enabled) revealedItemCount.coerceIn(0, itemCount) else 0
}

@Composable
private fun rememberShouldFollowNewContent(
    listState: LazyListState
): Boolean {
    var shouldFollowNewContent by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(listState) {
        snapshotFlow {
            AutoScrollSnapshot(
                canScrollForward = listState.canScrollForward,
                isScrollInProgress = listState.isScrollInProgress,
                lastScrolledBackward = listState.lastScrolledBackward
            )
        }
            .distinctUntilChanged()
            .collect { snapshot ->
                when {
                    !snapshot.canScrollForward -> shouldFollowNewContent = true
                    snapshot.isScrollInProgress && snapshot.lastScrolledBackward -> shouldFollowNewContent = false
                }
            }
    }

    return shouldFollowNewContent
}

@Composable
private fun AutoScrollOnReveal(
    listState: LazyListState,
    revealKey: Any?,
    enabled: Boolean = true,
    targetItemIndex: Int? = null,
    shouldFollowNewContent: Boolean
) {
    val currentShouldFollowNewContent by rememberUpdatedState(shouldFollowNewContent)
    val bottomPaddingPx = with(LocalDensity.current) {
        AUTO_SCROLL_BOTTOM_PADDING.toPx()
    }

    LaunchedEffect(revealKey, enabled, targetItemIndex) {
        if (!enabled) return@LaunchedEffect
        delay(AUTO_SCROLL_LAYOUT_DELAY_MILLIS.toLong())
        if (!currentShouldFollowNewContent) return@LaunchedEffect

        val layoutInfo = listState.layoutInfo
        val lastItemIndex = layoutInfo.totalItemsCount - 1

        if (lastItemIndex >= 0) {
            val targetIndex = (targetItemIndex ?: lastItemIndex).coerceIn(0, lastItemIndex)
            val targetItem = layoutInfo.visibleItemsInfo.firstOrNull { it.index == targetIndex }

            if (targetItem != null) {
                val overflow = targetItem.offset + targetItem.size + bottomPaddingPx -
                    layoutInfo.viewportEndOffset

                if (overflow > 0f) {
                    listState.animateScrollBy(
                        value = overflow,
                        animationSpec = tween(durationMillis = AUTO_SCROLL_ANIMATION_DURATION_MILLIS)
                    )
                }
            } else if (targetIndex > (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1)) {
                listState.animateScrollToItem(targetIndex)
            }
        }
    }
}

@Composable
private fun AnimatedChatItem(
    visible: Boolean,
    modifier: Modifier = Modifier,
    animateInitiallyVisible: Boolean = false,
    revealDelayMillis: Int = CHAT_ITEM_REVEAL_DELAY_MILLIS,
    content: @Composable () -> Unit
) {
    var hasStartedReveal by remember {
        mutableStateOf(visible && !animateInitiallyVisible)
    }
    val alpha by animateFloatAsState(
        targetValue = if (hasStartedReveal) 1f else 0f,
        animationSpec = tween(durationMillis = CHAT_ELEMENT_REVEAL_DURATION_MILLIS),
        label = "chat item alpha"
    )
    LaunchedEffect(visible) {
        if (visible) {
            delay(revealDelayMillis.toLong())
            hasStartedReveal = true
        }
    }

    if (visible || hasStartedReveal) {
        Box(
            modifier = modifier
                .alpha(alpha)
        ) {
            content()
        }
    }
}

@Composable
private fun CloseBranchContent(
    selectedCloseBranch: CitizenCloseBranch?,
    chatGameUiState: ChatGameUiState,
    revealedItemCount: Int,
    revealedHelpFallenItemCount: Int,
    revealedAvoidSituationItemCount: Int,
    onHelpFallenClick: () -> Unit,
    onAvoidSituationClick: () -> Unit,
    onChoiceClickSound: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(BRANCH_CHAT_ELEMENT_SPACING),
        modifier = modifier.fillMaxWidth()
    ) {
        AnimatedChatItem(
            visible = revealedItemCount >= 1,
            animateInitiallyVisible = true
        ) {
            ChatNarrationText(text = chatGameUiState.resultTextFor(CitizenCloseRequest))
        }

        AnimatedChatItem(visible = revealedItemCount >= 2) {
            MyChatElement(chatText = ".....")
        }

        AnimatedChatItem(visible = revealedItemCount >= 3) {
            ChatSceneImage(
                imageResId = R.drawable.citizen_chat_branch_one_first,
                contentDescription = "군인 앞에 모인 시민들"
            )
        }

        AnimatedChatItem(visible = revealedItemCount >= 4) {
            ChatNarrationText(text = "순간, 군인들이 움직이기 시작합니다.")
        }

        AnimatedChatItem(visible = revealedItemCount >= 5) {
            OtherChatElement(
                nameText = "주변 시민",
                chatText = "뒤로 가! 위험해!"
            )
        }

        AnimatedChatItem(visible = revealedItemCount >= 6) {
            ChatNarrationText(text = "사람들이 뒤로 밀려납니다.")
        }

        AnimatedChatItem(visible = revealedItemCount >= 7) {
            OtherChatElement(
                nameText = "주변 시민",
                chatText = "일으켜! 괜찮아?!"
            )
        }

        AnimatedChatItem(visible = revealedItemCount >= 8) {
            MyChatElement(chatText = ".....")
        }

        AnimatedChatItem(visible = revealedItemCount >= 9) {
            ChatNarrationText(text = "넘어지는 사람이 보입니다.")
        }

        AnimatedChatItem(visible = revealedItemCount >= 10) {
            ChatChoiceElement(
                firstChoiceText = "쓰러진 사람에게 다가간다",
                secondChoiceText = "뒤로 물러나 상황을 피한다",
                onFirstChoiceClick = onHelpFallenClick,
                onSecondChoiceClick = onAvoidSituationClick,
                selectedChoiceIndex = when (selectedCloseBranch) {
                    CitizenCloseBranch.HelpFallen -> 0
                    CitizenCloseBranch.AvoidSituation -> 1
                    null -> null
                },
                onChoiceClickSound = onChoiceClickSound
            )
        }

        when (selectedCloseBranch) {
            CitizenCloseBranch.HelpFallen -> {
                HelpFallenContent(
                    resultText = chatGameUiState.resultTextFor(CitizenHelpFallenRequest),
                    revealedItemCount = revealedHelpFallenItemCount
                )
            }

            CitizenCloseBranch.AvoidSituation -> {
                AvoidSituationContent(
                    resultText = chatGameUiState.resultTextFor(CitizenAvoidSituationRequest),
                    revealedItemCount = revealedAvoidSituationItemCount
                )
            }

            null -> Unit
        }
    }
}

@Composable
private fun HelpFallenContent(
    resultText: String,
    revealedItemCount: Int,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(BRANCH_CHAT_ELEMENT_SPACING),
        modifier = modifier.fillMaxWidth()
    ) {
        if (revealedItemCount <= HELP_FALLEN_RECORD_ITEM_INDEX) {
            HelpFallenBeforeRecordItems(
                resultText = resultText,
                revealedItemCount = revealedItemCount
            )
        }
    }
}

@Composable
private fun HelpFallenBeforeRecordItems(
    resultText: String,
    revealedItemCount: Int,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(BRANCH_CHAT_ELEMENT_SPACING),
        modifier = modifier.fillMaxWidth()
    ) {
        AnimatedChatItem(
            visible = revealedItemCount >= 1,
            animateInitiallyVisible = true
        ) {
            ChatNarrationText(text = resultText)
        }

        AnimatedChatItem(visible = revealedItemCount >= 2) {
            ChatSceneImage(
                imageResId = R.drawable.citizen_chat_branch_one_second,
                contentDescription = "군인과 차량이 이동하는 거리",
                height = 198.dp
            )
        }

        AnimatedChatItem(visible = revealedItemCount >= 3) {
            ChatNarrationText(text = "혼란 속에서 사람들의 움직임이 계속됩니다.")
        }

        AnimatedChatItem(visible = revealedItemCount >= 4) {
            OtherChatElement(
                nameText = "주변 시민",
                chatText = "여기 좀 더 비켜줘!"
            )
        }

        AnimatedChatItem(visible = revealedItemCount >= 5) {
            ChatNarrationText(
                text = "당신은 그 자리에 서 있습니다. 아직 상황을 완전히 이해하지 못한 채, 그저 바라보고 있습니다. 그날의 일은, 단순한 충돌로 끝나지 않았습니다."
            )
        }
    }
}

@Composable
private fun AvoidSituationContent(
    resultText: String,
    revealedItemCount: Int,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(BRANCH_CHAT_ELEMENT_SPACING),
        modifier = modifier.fillMaxWidth()
    ) {
        AnimatedChatItem(
            visible = revealedItemCount >= 1,
            animateInitiallyVisible = true
        ) {
            ChatNarrationText(text = resultText)
        }

        AnimatedChatItem(visible = revealedItemCount >= 2) {
            MyChatElement(chatText = "...여기서 더 가면 위험할 것 같아.")
        }

        AnimatedChatItem(visible = revealedItemCount >= 3) {
            ChatNarrationText(
                text = "당신은 한 발짝 뒤로 물러납니다. 사람들 사이에 거센 압력이 차 보이기 시작합니다."
            )
        }

        AnimatedChatItem(visible = revealedItemCount >= 4) {
            OtherChatElement(
                nameText = "주변 시민",
                chatText = "일으켜! 괜찮아?!"
            )
        }

        AnimatedChatItem(visible = revealedItemCount >= 5) {
            ChatNarrationText(
                text = "누군가를 부르는 소리가 들립니다. 하지만, 정확히 보이지는 않습니다."
            )
        }

        AnimatedChatItem(visible = revealedItemCount >= 6) {
            ChatSceneImage(
                imageResId = R.drawable.citizen_chat_branch_two_second,
                contentDescription = "거리로 나온 많은 시민들",
                height = 202.dp
            )
        }

        AnimatedChatItem(visible = revealedItemCount >= 7) {
            ChatNarrationText(text = "당신은 그 자리에 서서 상황을 바라보고 있습니다.")
        }
    }
}

@Composable
private fun DistanceBranchContent(
    resultText: String,
    revealedItemCount: Int,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(BRANCH_CHAT_ELEMENT_SPACING),
        modifier = modifier.fillMaxWidth()
    ) {
        AnimatedChatItem(
            visible = revealedItemCount >= 1,
            animateInitiallyVisible = true
        ) {
            ChatNarrationText(text = resultText)
        }

        AnimatedChatItem(visible = revealedItemCount >= 2) {
            MyChatElement(chatText = "뭔가 이상한데... 지금은 좀 떨어져서 보자.")
        }

        AnimatedChatItem(visible = revealedItemCount >= 3) {
            ChatNarrationText(
                text = "사람들 사이에 섞이지 않고, 뒤에서 상황을 바라봅니다. 앞쪽에서 갑자기 사람들이 크게 움직이기 시작합니다."
            )
        }

        AnimatedChatItem(visible = revealedItemCount >= 4) {
            OtherChatElement(
                nameText = "주변 시민",
                chatText = "뒤로 가! 위험해!"
            )
        }

        AnimatedChatItem(visible = revealedItemCount >= 5) {
            ChatNarrationText(text = "무슨 일이 일어나고 있는지 정확히 보이지 않습니다.")
        }

        AnimatedChatItem(visible = revealedItemCount >= 6) {
            ChatNarrationText(text = "당신은 그 자리에 서서, 그저 상황을 바라보고 있습니다.")
        }
    }
}

private enum class CitizenChatBranch {
    Close,
    Distance
}

private enum class CitizenCloseBranch {
    HelpFallen,
    AvoidSituation
}

private fun citizenActiveRecordDetailContent(
    selectedBranch: CitizenChatBranch?,
    selectedCloseBranch: CitizenCloseBranch?,
    revealedHelpFallenItemCount: Int,
    revealedAvoidSituationItemCount: Int,
    revealedDistanceItemCount: Int
): ChatRecordDetailContent? {
    return when {
        selectedCloseBranch == CitizenCloseBranch.HelpFallen &&
            revealedHelpFallenItemCount >= HELP_FALLEN_RECORD_ITEM_INDEX -> {
            ChatRecordDetailContent(
                imageResId = R.drawable.citizen_close_record,
                imageContentDescription = "군인에게 폭행당하는 시민",
                imageHeight = 226.dp,
                imageContentScale = ContentScale.Fit,
                bodyText = "사진 속 인물은 훗날 시민군 상황실장을\n맡게 되는 박남선의 동생, 박남규입니다.\n당시 금남로 일대에서는 공수부대의 강경 진압이 이어지고 있었으며,\n박남규는 가톨릭센터 인근에서 공수부대원에게 폭행당했습니다.\n이러한 진압 장면들은 시민들에게 빠르게 알려졌고,\n분노한 시민들이 거리로 모여들기 시작했습니다.\n이후 시위는 학생 중심에서\n시민 전체로 확산되며 광주 전역으로 퍼져나갔습니다.",
                bottomText = "당신은 그 시작을 목격합니다."
            )
        }

        selectedCloseBranch == CitizenCloseBranch.AvoidSituation &&
            revealedAvoidSituationItemCount >= AVOID_SITUATION_RECORD_ITEM_INDEX -> {
            ChatRecordDetailContent(
                imageResId = R.drawable.citizen_intro_record,
                imageContentDescription = "금남로의 계엄군",
                dateText = "1980년 5월 21일",
                imageHeight = 222.dp,
                imageContentScale = ContentScale.Fit,
                bodyText = "계엄군의 진압이 계속되자 더 많은 시민들이 금남로로 모여들기 시작했습니다.\n당시 시민군으로 알려진 김군과 같은 평범한 시민들도\n거리에서 시위대와 부상자들을 돕고 있었습니다.\n학생들의 시위는 시민 전체의 저항으로 확산되고 있었습니다.\n당신은 그날의 광주를 바라보고 있었습니다."
            )
        }

        selectedBranch == CitizenChatBranch.Distance &&
            revealedDistanceItemCount >= DISTANCE_RECORD_ITEM_INDEX -> {
            ChatRecordDetailContent(
                imageResId = R.drawable.citizen_distance_record,
                imageContentDescription = "멀리서 지켜보는 시위 현장",
                dateText = "1980년 5월 18일",
                imageHeight = 415.dp,
                imageContentScale = ContentScale.Fit,
                bodyText = "계엄군의 강경 진압이 이어지면서 광주 시내에는\n더 많은 시민들이 모여들기 시작했습니다.\n당시 시민군과 대변인을 맡게 되는 윤상원 역시 시민들과\n함께 광주의 상황을 알리며 민주화를 요구하고 있었습니다.\n시민들의 증언과 현장의 소식은 빠르게 퍼져나갔고,\n학생 중심이던 시위는 시민 전체의 저항으로 확산되었습니다.\n당신은 그날의 광주를 지켜본 시민 중 한 사람이었습니다.",
                bottomText = "당신은 그 시작을 목격합니다."
            )
        }

        else -> null
    }
}

private fun citizenInfoUnreadStateKey(
    selectedBranch: CitizenChatBranch?,
    selectedCloseBranch: CitizenCloseBranch?,
    revealedInitialItemCount: Int,
    revealedCloseItemCount: Int,
    revealedDistanceItemCount: Int,
    revealedHelpFallenItemCount: Int,
    revealedAvoidSituationItemCount: Int,
    finalSequenceKey: String?
): String? {
    if (finalSequenceKey != null) return null

    return when {
        selectedCloseBranch == CitizenCloseBranch.HelpFallen &&
            revealedHelpFallenItemCount >= 5 -> "citizen_help_fallen_scene"
        selectedCloseBranch == CitizenCloseBranch.AvoidSituation &&
            revealedAvoidSituationItemCount >= 7 -> "citizen_avoid_situation_scene"
        selectedBranch == CitizenChatBranch.Close &&
            selectedCloseBranch == null &&
            revealedCloseItemCount >= CLOSE_BRANCH_ELEMENT_COUNT -> "citizen_close_choice"
        selectedBranch == CitizenChatBranch.Distance &&
            revealedDistanceItemCount >= DISTANCE_BRANCH_ELEMENT_COUNT -> "citizen_distance_scene"
        selectedBranch == null &&
            revealedInitialItemCount >= 4 -> "citizen_intro_scene"
        else -> null
    }
}

private data class AutoScrollSnapshot(
    val canScrollForward: Boolean,
    val isScrollInProgress: Boolean,
    val lastScrolledBackward: Boolean
)

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun CitizenChatScreenPreview() {
    MayWaveTheme {
        CitizenChatScreen()
    }
}
