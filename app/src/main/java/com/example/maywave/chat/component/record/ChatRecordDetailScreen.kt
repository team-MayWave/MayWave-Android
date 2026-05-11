package com.example.maywave.chat.component.record

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
    startTyping: Boolean = true
) {
    val currentOnRecordTypingFinished by rememberUpdatedState(onRecordTypingFinished)
    var isRecordTypingFinished by remember(content, startTyping) { mutableStateOf(false) }
    var bottomTextVisibleCharacterCount by remember(
        content.bottomText,
        startTyping
    ) {
        mutableIntStateOf(0)
    }

    LaunchedEffect(content.bottomText, isRecordTypingFinished, startTyping) {
        val bottomText = content.bottomText

        if (bottomText == null || !startTyping) {
            bottomTextVisibleCharacterCount = 0
            return@LaunchedEffect
        }

        if (!isRecordTypingFinished) {
            bottomTextVisibleCharacterCount = 0
            return@LaunchedEffect
        }

        typeRecordText(bottomText) { visibleCharacterCount ->
            bottomTextVisibleCharacterCount = visibleCharacterCount
        }
        delay(RECORD_AFTER_TYPING_DELAY_MILLIS)
        currentOnRecordTypingFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        LazyColumn(
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
    beforeRecordContent: @Composable () -> Unit
) {
    val currentOnRecordTypingFinished by rememberUpdatedState(onRecordTypingFinished)
    var retainedContent by remember { mutableStateOf<ChatRecordDetailContent?>(null) }
    var transitionPhase by remember { mutableStateOf(RecordDetailTransitionPhase.BeforeRecord) }
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
            return@LaunchedEffect
        }

        retainedContent = content
        transitionPhase = RecordDetailTransitionPhase.BeforeRecord
        delay(RECORD_DETAIL_BEFORE_FADE_DELAY_MILLIS.toLong())
        transitionPhase = RecordDetailTransitionPhase.FadingOutBeforeRecord
        delay(RECORD_DETAIL_TRANSITION_DURATION_MILLIS.toLong())
        transitionPhase = RecordDetailTransitionPhase.FadingInRecord
        delay(RECORD_DETAIL_TRANSITION_DURATION_MILLIS.toLong())
        transitionPhase = RecordDetailTransitionPhase.RecordVisible
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
                    onRecordTypingFinished = currentOnRecordTypingFinished,
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
