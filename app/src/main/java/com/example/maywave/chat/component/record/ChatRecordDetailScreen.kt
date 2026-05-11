package com.example.maywave.chat.component.record

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maywave.ui.theme.ChatDescriptionTextColor
import kotlinx.coroutines.delay

private const val RECORD_DETAIL_BEFORE_FADE_DELAY_MILLIS = 2_000
private const val RECORD_DETAIL_TRANSITION_DURATION_MILLIS = 2_000
private const val RECORD_DETAIL_AUTO_SCROLL_LAYOUT_DELAY_MILLIS = 24L
private const val RECORD_DETAIL_AUTO_SCROLL_STEP_PX = 12f

data class ChatRecordDetailContent(
    @DrawableRes val imageResId: Int,
    val imageContentDescription: String,
    val bodyText: String,
    val dateText: String = "1980년 5월 19일",
    val imageHeight: Dp = 222.dp,
    val imageContentScale: ContentScale = ContentScale.Crop,
    val bottomText: String? = null
)

@Composable
fun ChatRecordDetailScreen(
    content: ChatRecordDetailContent,
    onRecordTypingFinished: () -> Unit,
    modifier: Modifier = Modifier,
    startTyping: Boolean = true,
    isPaused: Boolean = false,
    onRecordTypingAnimationStarted: () -> Unit = {},
    onRecordTypingAnimationFinished: () -> Unit = {}
) {
    val currentOnRecordTypingFinished by rememberUpdatedState(onRecordTypingFinished)
    val currentOnRecordTypingAnimationStarted by rememberUpdatedState(onRecordTypingAnimationStarted)
    val currentOnRecordTypingAnimationFinished by rememberUpdatedState(onRecordTypingAnimationFinished)
    val listState = rememberLazyListState()
    var isRecordTypingFinished by remember(content, startTyping) { mutableStateOf(false) }
    var recordTypingProgressKey by remember(content, startTyping) { mutableIntStateOf(0) }
    var bottomTextVisibleCharacterCount by remember(
        content.bottomText,
        startTyping
    ) {
        mutableIntStateOf(0)
    }
    var hasNotifiedBottomTypingFinished by remember(
        content.bottomText,
        startTyping
    ) {
        mutableStateOf(false)
    }

    LaunchedEffect(content.bottomText, isRecordTypingFinished, startTyping, isPaused) {
        val bottomText = content.bottomText

        if (bottomText == null || !startTyping) {
            bottomTextVisibleCharacterCount = 0
            hasNotifiedBottomTypingFinished = false
            return@LaunchedEffect
        }

        if (isPaused) return@LaunchedEffect

        if (!isRecordTypingFinished) {
            bottomTextVisibleCharacterCount = 0
            hasNotifiedBottomTypingFinished = false
            return@LaunchedEffect
        }

        var isBottomTypingAnimationRunning = bottomTextVisibleCharacterCount < bottomText.length

        if (isBottomTypingAnimationRunning) {
            currentOnRecordTypingAnimationStarted()
        }

        try {
            typeRecordTextFrom(
                text = bottomText,
                startVisibleCharacterCount = bottomTextVisibleCharacterCount
            ) { visibleCharacterCount -> bottomTextVisibleCharacterCount = visibleCharacterCount }

            if (isBottomTypingAnimationRunning) {
                isBottomTypingAnimationRunning = false
                currentOnRecordTypingAnimationFinished()
            }
        } finally {
            if (isBottomTypingAnimationRunning) {
                currentOnRecordTypingAnimationFinished()
            }
        }

        if (hasNotifiedBottomTypingFinished) return@LaunchedEffect

        delay(RECORD_AFTER_TYPING_DELAY_MILLIS)
        hasNotifiedBottomTypingFinished = true
        currentOnRecordTypingFinished()
    }

    LaunchedEffect(recordTypingProgressKey, startTyping, isPaused) {
        if (!startTyping || isPaused || recordTypingProgressKey <= 0) return@LaunchedEffect

        delay(RECORD_DETAIL_AUTO_SCROLL_LAYOUT_DELAY_MILLIS)
        if (listState.canScrollForward) {
            listState.scrollBy(RECORD_DETAIL_AUTO_SCROLL_STEP_PX)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        LazyColumn(
            state = listState,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(28.dp),
            contentPadding = PaddingValues(top = 29.dp, bottom = 126.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Image(
                    painter = painterResource(id = content.imageResId),
                    contentDescription = content.imageContentDescription,
                    contentScale = content.imageContentScale,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(content.imageHeight)
                        .padding(horizontal = 32.dp)
                )
            }

            item {
                ChatRecord(
                    dateText = content.dateText,
                    bodyText = content.bodyText,
                    startTyping = startTyping,
                    isPaused = isPaused,
                    onTypingProgress = {
                        recordTypingProgressKey += 1
                    },
                    onTypingAnimationStarted = currentOnRecordTypingAnimationStarted,
                    onTypingAnimationFinished = currentOnRecordTypingAnimationFinished,
                    onTypingFinished = {
                        if (content.bottomText == null) {
                            currentOnRecordTypingFinished()
                        } else {
                            isRecordTypingFinished = true
                        }
                    }
                )
            }
        }

        content.bottomText?.let { bottomText ->
            Text(
                text = bottomText.take(
                    bottomTextVisibleCharacterCount.coerceIn(0, bottomText.length)
                ),
                color = ChatDescriptionTextColor,
                fontSize = 12.sp,
                fontWeight = FontWeight(400),
                letterSpacing = 0.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 66.dp)
            )
        }
    }
}

@Composable
fun ChatRecordDetailTransition(
    content: ChatRecordDetailContent?,
    onRecordTypingFinished: () -> Unit,
    modifier: Modifier = Modifier,
    isPaused: Boolean = false,
    onRecordScreenShown: (ChatRecordDetailContent) -> Unit = {},
    onRecordTypingAnimationStarted: () -> Unit = {},
    onRecordTypingAnimationFinished: () -> Unit = {},
    beforeRecordContent: @Composable () -> Unit
) {
    val currentOnRecordTypingFinished by rememberUpdatedState(onRecordTypingFinished)
    val currentOnRecordScreenShown by rememberUpdatedState(onRecordScreenShown)
    val currentOnRecordTypingAnimationStarted by rememberUpdatedState(onRecordTypingAnimationStarted)
    val currentOnRecordTypingAnimationFinished by rememberUpdatedState(onRecordTypingAnimationFinished)
    var retainedContent by remember { mutableStateOf<ChatRecordDetailContent?>(null) }
    var transitionPhase by remember { mutableStateOf(RecordDetailTransitionPhase.BeforeRecord) }
    var hasNotifiedRecordScreenShown by remember { mutableStateOf(false) }
    val beforeRecordAlpha by animateFloatAsState(
        targetValue = if (transitionPhase == RecordDetailTransitionPhase.BeforeRecord) 1f else 0f,
        animationSpec = tween(durationMillis = RECORD_DETAIL_TRANSITION_DURATION_MILLIS),
        label = "record before screen alpha"
    )
    val recordAlpha by animateFloatAsState(
        targetValue = when (transitionPhase) {
            RecordDetailTransitionPhase.FadingInRecord,
            RecordDetailTransitionPhase.RecordVisible -> 1f

            RecordDetailTransitionPhase.BeforeRecord,
            RecordDetailTransitionPhase.FadingOutBeforeRecord -> 0f
        },
        animationSpec = tween(durationMillis = RECORD_DETAIL_TRANSITION_DURATION_MILLIS),
        label = "record detail screen alpha"
    )

    LaunchedEffect(content) {
        if (content == null) {
            retainedContent = null
            transitionPhase = RecordDetailTransitionPhase.BeforeRecord
            hasNotifiedRecordScreenShown = false
            return@LaunchedEffect
        }

        retainedContent = content
        transitionPhase = RecordDetailTransitionPhase.BeforeRecord
        hasNotifiedRecordScreenShown = false
    }

    LaunchedEffect(retainedContent, transitionPhase, isPaused) {
        if (retainedContent == null || isPaused) return@LaunchedEffect

        when (transitionPhase) {
            RecordDetailTransitionPhase.BeforeRecord -> {
                delay(RECORD_DETAIL_BEFORE_FADE_DELAY_MILLIS.toLong())
                transitionPhase = RecordDetailTransitionPhase.FadingOutBeforeRecord
            }

            RecordDetailTransitionPhase.FadingOutBeforeRecord -> {
                delay(RECORD_DETAIL_TRANSITION_DURATION_MILLIS.toLong())
                transitionPhase = RecordDetailTransitionPhase.FadingInRecord
            }

            RecordDetailTransitionPhase.FadingInRecord -> {
                delay(RECORD_DETAIL_TRANSITION_DURATION_MILLIS.toLong())
                transitionPhase = RecordDetailTransitionPhase.RecordVisible
            }

            RecordDetailTransitionPhase.RecordVisible -> Unit
        }
    }

    LaunchedEffect(retainedContent, transitionPhase, hasNotifiedRecordScreenShown) {
        val recordContent = retainedContent ?: return@LaunchedEffect

        if (
            transitionPhase == RecordDetailTransitionPhase.RecordVisible &&
            !hasNotifiedRecordScreenShown
        ) {
            hasNotifiedRecordScreenShown = true
            currentOnRecordScreenShown(recordContent)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (
            transitionPhase == RecordDetailTransitionPhase.BeforeRecord ||
            transitionPhase == RecordDetailTransitionPhase.FadingOutBeforeRecord
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(beforeRecordAlpha)
            ) {
                beforeRecordContent()
            }
        }

        retainedContent?.let { recordContent ->
            if (
                transitionPhase == RecordDetailTransitionPhase.FadingInRecord ||
                transitionPhase == RecordDetailTransitionPhase.RecordVisible
            ) {
                ChatRecordDetailScreen(
                    content = recordContent,
                    startTyping = transitionPhase == RecordDetailTransitionPhase.RecordVisible,
                    isPaused = isPaused,
                    onRecordTypingFinished = currentOnRecordTypingFinished,
                    onRecordTypingAnimationStarted = currentOnRecordTypingAnimationStarted,
                    onRecordTypingAnimationFinished = currentOnRecordTypingAnimationFinished,
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(recordAlpha)
                )
            }
        }
    }
}

private enum class RecordDetailTransitionPhase {
    BeforeRecord,
    FadingOutBeforeRecord,
    FadingInRecord,
    RecordVisible
}
