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
import com.example.maywave.chat.viewmodel.ChatGameViewModel
import com.example.maywave.chat.viewmodel.ChatGameViewModelFactory
import com.example.maywave.ui.theme.MayWaveTheme
import kotlin.math.abs
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged

private const val REPORTER_CHAT_ELEMENT_REVEAL_DURATION_MILLIS = 2_000
private const val REPORTER_SERVER_RESULT_NEXT_REVEAL_DELAY_MILLIS = 4_000
private const val REPORTER_INITIAL_ELEMENT_COUNT = 10
private const val REPORTER_CONTINUE_RECORD_ELEMENT_COUNT = 11
private const val REPORTER_ESCAPE_ELEMENT_COUNT = 5
private const val REPORTER_CONTINUE_RECORD_DETAIL_ITEM_INDEX = 10
private const val REPORTER_CONTINUE_RECORD_ITEM_INDEX = 11
private const val REPORTER_ESCAPE_RECORD_ITEM_INDEX = 5
private const val REPORTER_INITIAL_LAZY_ITEM_INDEX = 0
private const val REPORTER_BRANCH_LAZY_ITEM_INDEX = 1
private const val REPORTER_AUTO_SCROLL_LAYOUT_DELAY_MILLIS = 100
private const val REPORTER_AUTO_SCROLL_ANIMATION_DURATION_MILLIS = 650
private const val REPORTER_CHAT_ITEM_REVEAL_DELAY_MILLIS = 0
private val REPORTER_CHAT_ELEMENT_SPACING = 35.dp
private val REPORTER_BRANCH_CHAT_ELEMENT_SPACING = 35.dp
private val REPORTER_AUTO_SCROLL_BOTTOM_PADDING = 16.dp
private val ReporterContinueRecordRequest = ChatGameRequest(roleId = 3, scenarioId = 5, choice = 1)
private val ReporterEscapeRequest = ChatGameRequest(roleId = 3, scenarioId = 5, choice = 2)

@Composable
fun ReporterChatScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onInfoClick: () -> Unit = {},
    showInfoUnreadDot: Boolean = true,
    onInfoRead: () -> Unit = {},
    onInfoUnreadStateShown: (String) -> Unit = {}
) {
    val listState = rememberLazyListState()
    val chatGameViewModel: ChatGameViewModel = viewModel(
        factory = remember { ChatGameViewModelFactory() }
    )
    val chatGameUiState by chatGameViewModel.uiState.collectAsState()
    var selectedBranch by rememberSaveable { mutableStateOf<ReporterChatBranch?>(null) }
    var branchRevealKey by rememberSaveable { mutableIntStateOf(0) }
    var isContinueRecordTypingFinished by rememberSaveable { mutableStateOf(false) }
    var isEscapeRecordTypingFinished by rememberSaveable { mutableStateOf(false) }
    val shouldFollowNewContent = rememberReporterShouldFollowNewContent(listState = listState)
    val continueRecordItemCount = if (chatGameUiState.hasErrorFor(ReporterContinueRecordRequest)) {
        1
    } else {
        REPORTER_CONTINUE_RECORD_ELEMENT_COUNT
    }
    val escapeItemCount = if (chatGameUiState.hasErrorFor(ReporterEscapeRequest)) {
        1
    } else {
        REPORTER_ESCAPE_ELEMENT_COUNT
    }
    val revealedInitialItemCount = rememberReporterSequentialRevealCount(
        itemCount = REPORTER_INITIAL_ELEMENT_COUNT,
        initiallyVisibleItemCount = 1
    )
    val revealedContinueRecordItemCount = rememberReporterSequentialRevealCount(
        itemCount = continueRecordItemCount,
        revealKey = branchRevealKey,
        enabled = selectedBranch == ReporterChatBranch.ContinueRecord &&
            chatGameUiState.isResultReadyFor(ReporterContinueRecordRequest),
        blockedAfterItemIndex = REPORTER_CONTINUE_RECORD_ITEM_INDEX,
        canRevealAfterBlockedItem = isContinueRecordTypingFinished,
        firstItemNextRevealDelayMillis = REPORTER_SERVER_RESULT_NEXT_REVEAL_DELAY_MILLIS
    )
    val revealedEscapeItemCount = rememberReporterSequentialRevealCount(
        itemCount = escapeItemCount,
        revealKey = branchRevealKey,
        enabled = selectedBranch == ReporterChatBranch.Escape &&
            chatGameUiState.isResultReadyFor(ReporterEscapeRequest),
        blockedAfterItemIndex = REPORTER_ESCAPE_RECORD_ITEM_INDEX,
        canRevealAfterBlockedItem = isEscapeRecordTypingFinished,
        firstItemNextRevealDelayMillis = REPORTER_SERVER_RESULT_NEXT_REVEAL_DELAY_MILLIS
    )
    val finalSequenceKey = when {
        selectedBranch == ReporterChatBranch.ContinueRecord &&
            revealedContinueRecordItemCount >= REPORTER_CONTINUE_RECORD_ELEMENT_COUNT &&
            isContinueRecordTypingFinished -> "reporter_continue_record"
        selectedBranch == ReporterChatBranch.Escape &&
            revealedEscapeItemCount >= REPORTER_ESCAPE_ELEMENT_COUNT &&
            isEscapeRecordTypingFinished -> "reporter_escape"
        else -> null
    }
    val infoUnreadStateKey = reporterInfoUnreadStateKey(
        selectedBranch = selectedBranch,
        revealedInitialItemCount = revealedInitialItemCount,
        revealedContinueRecordItemCount = revealedContinueRecordItemCount,
        revealedEscapeItemCount = revealedEscapeItemCount,
        finalSequenceKey = finalSequenceKey
    )
    val activeRecordDetailContent = reporterActiveRecordDetailContent(
        selectedBranch = selectedBranch,
        revealedContinueRecordItemCount = revealedContinueRecordItemCount,
        revealedEscapeItemCount = revealedEscapeItemCount
    )

    LaunchedEffect(infoUnreadStateKey) {
        infoUnreadStateKey?.let(onInfoUnreadStateShown)
    }

    AutoScrollOnReporterReveal(
        listState = listState,
        revealKey = revealedInitialItemCount,
        enabled = revealedInitialItemCount > 1,
        targetItemIndex = REPORTER_INITIAL_LAZY_ITEM_INDEX,
        shouldFollowNewContent = shouldFollowNewContent
    )

    AutoScrollOnReporterReveal(
        listState = listState,
        revealKey = revealedContinueRecordItemCount,
        enabled = selectedBranch == ReporterChatBranch.ContinueRecord &&
            revealedContinueRecordItemCount in 1 until REPORTER_CONTINUE_RECORD_ELEMENT_COUNT,
        targetItemIndex = REPORTER_BRANCH_LAZY_ITEM_INDEX,
        shouldFollowNewContent = shouldFollowNewContent
    )

    AutoScrollOnReporterReveal(
        listState = listState,
        revealKey = revealedEscapeItemCount,
        enabled = selectedBranch == ReporterChatBranch.Escape &&
            revealedEscapeItemCount in 1 until REPORTER_ESCAPE_ELEMENT_COUNT,
        targetItemIndex = REPORTER_BRANCH_LAZY_ITEM_INDEX,
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
                when (selectedBranch) {
                    ReporterChatBranch.ContinueRecord -> {
                        isContinueRecordTypingFinished = true
                    }

                    ReporterChatBranch.Escape -> {
                        isEscapeRecordTypingFinished = true
                    }

                    null -> Unit
                }
            },
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                ReporterChatHeader(
                    onBackClick = onBackClick,
                    onInfoClick = onInfoClick,
                    showInfoUnreadDot = showInfoUnreadDot && infoUnreadStateKey != null,
                    onInfoRead = onInfoRead
                )

                LazyColumn(
                    state = listState,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(REPORTER_CHAT_ELEMENT_SPACING),
                    contentPadding = PaddingValues(top = 49.dp, bottom = 48.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    item {
                        InitialReporterContent(
                            revealedItemCount = revealedInitialItemCount,
                            selectedBranch = selectedBranch,
                            onContinueRecordClick = {
                                selectedBranch = ReporterChatBranch.ContinueRecord
                                branchRevealKey += 1
                                chatGameViewModel.submitChoice(ReporterContinueRecordRequest)
                            },
                            onEscapeClick = {
                                selectedBranch = ReporterChatBranch.Escape
                                branchRevealKey += 1
                                chatGameViewModel.submitChoice(ReporterEscapeRequest)
                            }
                        )
                    }

                    when (selectedBranch) {
                        ReporterChatBranch.ContinueRecord -> {
                            item {
                                ChatRecordTypingAutoScrollProvider(
                                    enabled = shouldFollowNewContent
                                ) {
                                    ContinueRecordContent(
                                        resultText = chatGameUiState.resultTextFor(ReporterContinueRecordRequest),
                                        revealedItemCount = revealedContinueRecordItemCount
                                    )
                                }
                            }
                        }

                        ReporterChatBranch.Escape -> {
                            item {
                                ChatRecordTypingAutoScrollProvider(
                                    enabled = shouldFollowNewContent
                                ) {
                                    EscapeContent(
                                        resultText = chatGameUiState.resultTextFor(ReporterEscapeRequest),
                                        revealedItemCount = revealedEscapeItemCount
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
                        text = "그날,\n어떤 장면은 기록으로 남았고,\n어떤 장면은 남지 못했습니다.\n하지만 기록된 것과\n기록되지 못한 것 모두,\n그날의 일부였습니다.\n그날의 진실은\n그렇게 이어지고 있습니다."
                    ),
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
private fun ReporterChatHeader(
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
            titleText = "기자",
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
private fun InitialReporterContent(
    revealedItemCount: Int,
    selectedBranch: ReporterChatBranch?,
    onContinueRecordClick: () -> Unit,
    onEscapeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(REPORTER_CHAT_ELEMENT_SPACING),
        modifier = modifier.fillMaxWidth()
    ) {
        AnimatedReporterChatItem(visible = revealedItemCount >= 1) {
            ElementTitle()
        }

        AnimatedReporterChatItem(visible = revealedItemCount >= 2) {
            ChatSceneImage(
                imageResId = R.drawable.chat_reporter_1,
                contentDescription = "군인들과 시민들이 뒤엉킨 금남로"
            )
        }

        AnimatedReporterChatItem(visible = revealedItemCount >= 3) {
            ChatNarrationText(text = "군인들과 시민들이\n뒤엉켜 있습니다.")
        }

        AnimatedReporterChatItem(visible = revealedItemCount >= 4) {
            ChatNarrationText(text = "부상자들이 계속 발생하고 있습니다.")
        }

        AnimatedReporterChatItem(visible = revealedItemCount >= 5) {
            MyChatElement(chatText = "이건...")
        }

        AnimatedReporterChatItem(visible = revealedItemCount >= 6) {
            MyChatElement(chatText = "남겨야 돼..")
        }

        AnimatedReporterChatItem(visible = revealedItemCount >= 7) {
            ChatNarrationText(
                text = "당신이 카메라를 들고 있는 순간,\n한 군인이 당신을 바라봅니다."
            )
        }

        AnimatedReporterChatItem(visible = revealedItemCount >= 8) {
            ChatNarrationText(text = "시선이 마주칩니다.")
        }

        AnimatedReporterChatItem(visible = revealedItemCount >= 9) {
            MyChatElement(chatText = "....")
        }

        AnimatedReporterChatItem(visible = revealedItemCount >= 10) {
            ChatChoiceElement(
                firstChoiceText = "계속 촬영한다",
                secondChoiceText = "도망친다",
                onFirstChoiceClick = onContinueRecordClick,
                onSecondChoiceClick = onEscapeClick,
                selectedChoiceIndex = when (selectedBranch) {
                    ReporterChatBranch.ContinueRecord -> 0
                    ReporterChatBranch.Escape -> 1
                    null -> null
                }
            )
        }
    }
}

@Composable
private fun ContinueRecordContent(
    resultText: String,
    revealedItemCount: Int,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(REPORTER_BRANCH_CHAT_ELEMENT_SPACING),
        modifier = modifier.fillMaxWidth()
    ) {
        if (revealedItemCount <= REPORTER_CONTINUE_RECORD_DETAIL_ITEM_INDEX) {
            ContinueRecordBeforeItems(
                resultText = resultText,
                revealedItemCount = revealedItemCount
            )
        }
    }
}

@Composable
private fun ContinueRecordBeforeItems(
    resultText: String,
    revealedItemCount: Int,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(REPORTER_BRANCH_CHAT_ELEMENT_SPACING),
        modifier = modifier.fillMaxWidth()
    ) {
        AnimatedReporterChatItem(
            visible = revealedItemCount >= 1,
            animateInitiallyVisible = true
        ) {
            ChatNarrationText(text = resultText)
        }

        AnimatedReporterChatItem(visible = revealedItemCount >= 2) {
            ChatNarrationText(text = "당신은 카메라를 내리지 않습니다.")
        }

        AnimatedReporterChatItem(visible = revealedItemCount >= 3) {
            OtherChatElement(
                nameText = "군인",
                chatText = "야! 찍지 마!"
            )
        }

        AnimatedReporterChatItem(visible = revealedItemCount >= 4) {
            ChatNarrationText(text = "위험한 순간까지 기록합니다.")
        }

        AnimatedReporterChatItem(visible = revealedItemCount >= 5) {
            ChatNarrationText(text = "군인이 당신 쪽으로 다가옵니다.")
        }

        AnimatedReporterChatItem(visible = revealedItemCount >= 6) {
            OtherChatElement(
                nameText = "군인",
                chatText = "카메라 내려!"
            )
        }

        AnimatedReporterChatItem(visible = revealedItemCount >= 7) {
            MyChatElement(chatText = "....")
        }

        AnimatedReporterChatItem(visible = revealedItemCount >= 8) {
            ChatNarrationText(text = "당신은 끝까지 카메라를 놓지 않습니다.")
        }

        AnimatedReporterChatItem(visible = revealedItemCount >= 9) {
            ChatNarrationText(text = "그 순간까지 기록합니다.")
        }
    }
}

@Composable
private fun EscapeContent(
    resultText: String,
    revealedItemCount: Int,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(REPORTER_BRANCH_CHAT_ELEMENT_SPACING),
        modifier = modifier.fillMaxWidth()
    ) {
        AnimatedReporterChatItem(
            visible = revealedItemCount >= 1,
            animateInitiallyVisible = true
        ) {
            ChatNarrationText(text = resultText)
        }

        AnimatedReporterChatItem(visible = revealedItemCount >= 2) {
            ChatNarrationText(
                text = "당신은 뒤를 돌아보지 않습니다.\n카메라는 내려간 채입니다."
            )
        }

        AnimatedReporterChatItem(visible = revealedItemCount >= 3) {
            MyChatElement(chatText = "...")
        }

        AnimatedReporterChatItem(visible = revealedItemCount >= 4) {
            ChatSceneImage(
                imageResId = R.drawable.chat_reportor_2,
                contentDescription = "군인을 피해 달아나는 기자"
            )
        }

    }
}

@Composable
private fun rememberReporterSequentialRevealCount(
    itemCount: Int,
    revealKey: Any? = Unit,
    initiallyVisibleItemCount: Int = 0,
    enabled: Boolean = true,
    blockedAfterItemIndex: Int? = null,
    canRevealAfterBlockedItem: Boolean = true,
    firstItemNextRevealDelayMillis: Int = REPORTER_CHAT_ELEMENT_REVEAL_DURATION_MILLIS
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
        firstItemNextRevealDelayMillis
    ) {
        if (!enabled) {
            revealedItemCount = 0
            return@LaunchedEffect
        }

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
                REPORTER_CHAT_ELEMENT_REVEAL_DURATION_MILLIS
            }
            delay(revealDelayMillis.toLong())
            revealedItemCount += 1
        }
    }

    return if (enabled) revealedItemCount.coerceIn(0, itemCount) else 0
}

@Composable
private fun rememberReporterShouldFollowNewContent(
    listState: LazyListState
): Boolean {
    var shouldFollowNewContent by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(listState) {
        snapshotFlow {
            ReporterAutoScrollSnapshot(
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
private fun AutoScrollOnReporterReveal(
    listState: LazyListState,
    revealKey: Any?,
    enabled: Boolean = true,
    targetItemIndex: Int? = null,
    shouldFollowNewContent: Boolean
) {
    val currentShouldFollowNewContent by rememberUpdatedState(shouldFollowNewContent)
    val bottomPaddingPx = with(LocalDensity.current) {
        REPORTER_AUTO_SCROLL_BOTTOM_PADDING.toPx()
    }

    LaunchedEffect(revealKey, enabled, targetItemIndex) {
        if (!enabled) return@LaunchedEffect
        delay(REPORTER_AUTO_SCROLL_LAYOUT_DELAY_MILLIS.toLong())
        if (!currentShouldFollowNewContent) return@LaunchedEffect

        val layoutInfo = listState.layoutInfo
        val lastItemIndex = layoutInfo.totalItemsCount - 1

        if (lastItemIndex >= 0) {
            val targetIndex = (targetItemIndex ?: lastItemIndex).coerceIn(0, lastItemIndex)
            val targetItem = layoutInfo.visibleItemsInfo.firstOrNull { it.index == targetIndex }

            if (targetItem != null) {
                val targetItemBottom = targetItem.offset + targetItem.size
                val desiredItemBottom = layoutInfo.viewportEndOffset - bottomPaddingPx
                val scrollDelta = targetItemBottom - desiredItemBottom

                if (abs(scrollDelta) > 1f) {
                    listState.animateScrollBy(
                        value = scrollDelta,
                        animationSpec = tween(
                            durationMillis = REPORTER_AUTO_SCROLL_ANIMATION_DURATION_MILLIS
                        )
                    )
                }
            } else if (targetIndex > (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1)) {
                listState.animateScrollToItem(targetIndex)
            }
        }
    }
}

@Composable
private fun AnimatedReporterChatItem(
    visible: Boolean,
    modifier: Modifier = Modifier,
    animateInitiallyVisible: Boolean = false,
    revealDelayMillis: Int = REPORTER_CHAT_ITEM_REVEAL_DELAY_MILLIS,
    content: @Composable () -> Unit
) {
    var hasStartedReveal by remember {
        mutableStateOf(visible && !animateInitiallyVisible)
    }
    val alpha by animateFloatAsState(
        targetValue = if (hasStartedReveal) 1f else 0f,
        animationSpec = tween(durationMillis = REPORTER_CHAT_ELEMENT_REVEAL_DURATION_MILLIS),
        label = "reporter chat item alpha"
    )
    LaunchedEffect(visible) {
        if (visible) {
            delay(revealDelayMillis.toLong())
            hasStartedReveal = true
        }
    }

    if (visible || hasStartedReveal) {
        Box(
            modifier = modifier.alpha(alpha)
        ) {
            content()
        }
    }
}

private enum class ReporterChatBranch {
    ContinueRecord,
    Escape
}

private fun reporterActiveRecordDetailContent(
    selectedBranch: ReporterChatBranch?,
    revealedContinueRecordItemCount: Int,
    revealedEscapeItemCount: Int
): ChatRecordDetailContent? {
    return when {
        selectedBranch == ReporterChatBranch.ContinueRecord &&
            revealedContinueRecordItemCount >= REPORTER_CONTINUE_RECORD_DETAIL_ITEM_INDEX -> {
            ChatRecordDetailContent(
                imageResId = R.drawable.reporter_gumnamro_record,
                imageContentDescription = "광주 현장을 촬영하는 기자",
                dateText = "1980년 5월 20일",
                imageHeight = 222.dp,
                bodyText = "독일 기자 힌츠페터는 광주에 잠입해 시민들과 계엄군의 충돌 현장을\n카메라에 기록했습니다.\n그는 금남로와 전남도청 일대에서 촬영한 영상과 사진들을 해외로 전달했고,\n이는 이후 광주의 상황이 세계에 알려지는 중요한 계기가 되었습니다."
            )
        }

        selectedBranch == ReporterChatBranch.Escape &&
            revealedEscapeItemCount >= REPORTER_ESCAPE_RECORD_ITEM_INDEX -> {
            ChatRecordDetailContent(
                imageResId = R.drawable.chat_reportor_2,
                imageContentDescription = "군인을 피해 달아나는 기자",
                imageHeight = 214.dp,
                bodyText = "광주에서는\n많은 사건들이 발생했지만,\n모든 순간이 기록되지는 않았습니다."
            )
        }

        else -> null
    }
}

private fun reporterInfoUnreadStateKey(
    selectedBranch: ReporterChatBranch?,
    revealedInitialItemCount: Int,
    revealedContinueRecordItemCount: Int,
    revealedEscapeItemCount: Int,
    finalSequenceKey: String?
): String? {
    if (finalSequenceKey != null) return null

    return when {
        selectedBranch == ReporterChatBranch.ContinueRecord &&
            revealedContinueRecordItemCount >= 10 -> "reporter_continue_record_scene"
        selectedBranch == ReporterChatBranch.Escape &&
            revealedEscapeItemCount >= 4 -> "reporter_escape_scene"
        selectedBranch == null &&
            revealedInitialItemCount >= REPORTER_INITIAL_ELEMENT_COUNT -> "reporter_intro_choice"
        else -> null
    }
}

private data class ReporterAutoScrollSnapshot(
    val canScrollForward: Boolean,
    val isScrollInProgress: Boolean,
    val lastScrolledBackward: Boolean
)

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun ReporterChatScreenPreview() {
    MayWaveTheme {
        ReporterChatScreen()
    }
}
