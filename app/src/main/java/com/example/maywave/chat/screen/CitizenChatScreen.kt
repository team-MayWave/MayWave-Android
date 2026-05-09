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
import com.example.maywave.chat.component.record.ChatRecord
import com.example.maywave.chat.viewmodel.ChatGameRequest
import com.example.maywave.chat.viewmodel.ChatGameUiState
import com.example.maywave.chat.viewmodel.ChatGameViewModel
import com.example.maywave.chat.viewmodel.ChatGameViewModelFactory
import com.example.maywave.ui.theme.MayWaveTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged

private const val CHAT_ELEMENT_REVEAL_DURATION_MILLIS = 2_000
private const val INITIAL_CHAT_ELEMENT_COUNT = 8
private const val CLOSE_BRANCH_ELEMENT_COUNT = 10
private const val HELP_FALLEN_ELEMENT_COUNT = 8
private const val AVOID_SITUATION_ELEMENT_COUNT = 10
private const val DISTANCE_BRANCH_ELEMENT_COUNT = 10
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
    onBackClick: () -> Unit = {}
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
    val shouldFollowNewContent = rememberShouldFollowNewContent(listState = listState)
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
        initiallyVisibleItemCount = 1
    )
    val revealedCloseItemCount = rememberSequentialRevealCount(
        itemCount = closeItemCount,
        revealKey = branchRevealKey,
        enabled = selectedBranch == CitizenChatBranch.Close &&
            chatGameUiState.isResultReadyFor(CitizenCloseRequest)
    )
    val revealedDistanceItemCount = rememberSequentialRevealCount(
        itemCount = distanceItemCount,
        revealKey = branchRevealKey,
        enabled = selectedBranch == CitizenChatBranch.Distance &&
            chatGameUiState.isResultReadyFor(CitizenDistanceRequest)
    )
    val revealedHelpFallenItemCount = rememberSequentialRevealCount(
        itemCount = helpFallenItemCount,
        revealKey = closeBranchRevealKey,
        enabled = selectedCloseBranch == CitizenCloseBranch.HelpFallen &&
            chatGameUiState.isResultReadyFor(CitizenHelpFallenRequest)
    )
    val revealedAvoidSituationItemCount = rememberSequentialRevealCount(
        itemCount = avoidSituationItemCount,
        revealKey = closeBranchRevealKey,
        enabled = selectedCloseBranch == CitizenCloseBranch.AvoidSituation &&
            chatGameUiState.isResultReadyFor(CitizenAvoidSituationRequest)
    )
    val finalSequenceKey = when {
        selectedCloseBranch == CitizenCloseBranch.HelpFallen &&
            revealedHelpFallenItemCount >= HELP_FALLEN_ELEMENT_COUNT -> "help_fallen"
        selectedCloseBranch == CitizenCloseBranch.AvoidSituation &&
            revealedAvoidSituationItemCount >= AVOID_SITUATION_ELEMENT_COUNT -> "avoid_situation"
        selectedBranch == CitizenChatBranch.Distance &&
            revealedDistanceItemCount >= DISTANCE_BRANCH_ELEMENT_COUNT -> "distance"
        else -> null
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            ChatHeader(onBackClick = onBackClick)

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
                                selectedCloseBranch = null
                                branchRevealKey += 1
                                closeBranchRevealKey = 0
                                chatGameViewModel.submitChoice(CitizenCloseRequest)
                            },
                            onSecondChoiceClick = {
                                selectedBranch = CitizenChatBranch.Distance
                                selectedCloseBranch = null
                                branchRevealKey += 1
                                closeBranchRevealKey = 0
                                chatGameViewModel.submitChoice(CitizenDistanceRequest)
                            },
                            selectedChoiceIndex = when (selectedBranch) {
                                CitizenChatBranch.Close -> 0
                                CitizenChatBranch.Distance -> 1
                                null -> null
                            }
                        )
                    }
                }

                when (selectedBranch) {
                    CitizenChatBranch.Close -> {
                        item {
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
                                }
                            )
                        }
                    }

                    CitizenChatBranch.Distance -> {
                        item {
                            DistanceBranchContent(
                                resultText = chatGameUiState.resultTextFor(CitizenDistanceRequest),
                                revealedItemCount = revealedDistanceItemCount
                            )
                        }
                    }

                    null -> Unit
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
    onBackClick: () -> Unit
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
    }
}

@Composable
private fun rememberSequentialRevealCount(
    itemCount: Int,
    revealKey: Any? = Unit,
    initiallyVisibleItemCount: Int = 0,
    enabled: Boolean = true
): Int {
    val initialItemCount = initiallyVisibleItemCount.coerceIn(0, itemCount)
    var revealedItemCount by rememberSaveable(revealKey) {
        mutableIntStateOf(initiallyVisibleItemCount.coerceIn(0, itemCount))
    }

    LaunchedEffect(revealKey, itemCount, initialItemCount, enabled) {
        if (!enabled) {
            revealedItemCount = 0
            return@LaunchedEffect
        }

        revealedItemCount = revealedItemCount.coerceAtLeast(initialItemCount)

        while (revealedItemCount < itemCount) {
            delay(CHAT_ELEMENT_REVEAL_DURATION_MILLIS.toLong())
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
        delay(100)
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
                    listState.animateScrollBy(overflow)
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
    content: @Composable () -> Unit
) {
    var hasStartedReveal by remember { mutableStateOf(visible) }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = CHAT_ELEMENT_REVEAL_DURATION_MILLIS),
        label = "chat item alpha"
    )
    LaunchedEffect(visible) {
        if (visible) {
            hasStartedReveal = true
        }
    }

    if (hasStartedReveal) {
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
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(BRANCH_CHAT_ELEMENT_SPACING),
        modifier = modifier.fillMaxWidth()
    ) {
        AnimatedChatItem(visible = revealedItemCount >= 1) {
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
                }
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
        AnimatedChatItem(visible = revealedItemCount >= 1) {
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

        AnimatedChatItem(visible = revealedItemCount >= 6) {
            ChatSceneImage(
                imageResId = R.drawable.citizen_chat_branch_one_third,
                contentDescription = "쓰러진 시민을 부축하는 사람들",
                height = 365.dp,
                contentScale = ContentScale.Fit
            )
        }

        AnimatedChatItem(visible = revealedItemCount >= 7) {
            ChatRecord(
                bodyText = "더 많은 시민들이 거리로 나오기 시작했습니다. 전남의 사건은 광주 전역으로 퍼져나갔습니다."
            )
        }

        AnimatedChatItem(visible = revealedItemCount >= 8) {
            ChatNarrationText(text = "당신은 그 시작을 목격했습니다.")
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
        AnimatedChatItem(visible = revealedItemCount >= 1) {
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

        AnimatedChatItem(visible = revealedItemCount >= 8) {
            ChatSceneImage(
                imageResId = R.drawable.citizen_chat_branch_two_third,
                contentDescription = "거리를 바라보는 시민들",
                height = 202.dp
            )
        }

        AnimatedChatItem(visible = revealedItemCount >= 9) {
            ChatRecord(
                bodyText = "더 많은 시민들이 거리로 나왔습니다. 당신이 멀리서 바라보던 그 순간에도, 많은 사람들은 같은 자리에서 상황을 지켜보고 있었습니다. 전남의 일은 광주 전역으로 퍼져나갔습니다."
            )
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
        AnimatedChatItem(visible = revealedItemCount >= 1) {
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

        AnimatedChatItem(visible = revealedItemCount >= 7) {
            ChatSceneImage(
                imageResId = R.drawable.citizen_chat_branch_two_first,
                contentDescription = "멀리서 지켜보는 시위 현장",
                height = 455.dp,
                contentScale = ContentScale.Fit
            )
        }

        AnimatedChatItem(visible = revealedItemCount >= 8) {
            ChatRecord(
                bodyText = "더 많은 시민들이 거리로 나왔습니다. 당신이 직접 목격한 그 장면들은 사람들 사이에서 빠르게 퍼져나갔습니다. 전남의 일은 광주 전역으로 퍼져나갔습니다."
            )
        }

        AnimatedChatItem(visible = revealedItemCount >= 9) {
            ChatNarrationText(text = "당신은 그날의 시작을, 그 자리에서 지켜보고 있었습니다.")
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
