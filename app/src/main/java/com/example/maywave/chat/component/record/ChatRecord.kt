package com.example.maywave.chat.component.record

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maywave.ui.theme.ChatDescriptionTextColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged

private const val RECORD_TITLE_TEXT = "[기록]"
private const val RECORD_DIVIDER_TEXT = "──────────────"
internal const val RECORD_TYPING_CHARACTER_DELAY_MILLIS = 72L
private const val RECORD_TYPING_STEP_PAUSE_MILLIS = 120L
internal const val RECORD_AFTER_TYPING_DELAY_MILLIS = 2_000L
private val RecordDividerTextColor = Color(0xFF8A8A8A)

private val LocalChatRecordTypingAutoScrollEnabled = compositionLocalOf { true }

@Composable
fun ChatRecordTypingAutoScrollProvider(
    enabled: Boolean,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalChatRecordTypingAutoScrollEnabled provides enabled,
        content = content
    )
}

@Composable
fun ChatRecord(
    bodyText: String,
    modifier: Modifier = Modifier,
    dateText: String = "1980년 5월 19일",
    startTyping: Boolean = true,
    isPaused: Boolean = false,
    onTypingProgress: () -> Unit = {},
    onTypingAnimationStarted: () -> Unit = {},
    onTypingAnimationFinished: () -> Unit = {},
    onTypingFinished: () -> Unit = {}
) {
    val currentOnTypingFinished by rememberUpdatedState(onTypingFinished)
    val currentOnTypingProgress by rememberUpdatedState(onTypingProgress)
    val currentOnTypingAnimationStarted by rememberUpdatedState(onTypingAnimationStarted)
    val currentOnTypingAnimationFinished by rememberUpdatedState(onTypingAnimationFinished)
    var titleVisibleCharacterCount by remember(bodyText, dateText, startTyping) { mutableIntStateOf(0) }
    var topDividerVisibleCharacterCount by remember(bodyText, dateText, startTyping) { mutableIntStateOf(0) }
    var dateVisibleCharacterCount by remember(bodyText, dateText, startTyping) { mutableIntStateOf(0) }
    var bottomDividerVisibleCharacterCount by remember(bodyText, dateText, startTyping) { mutableIntStateOf(0) }
    var bodyVisibleCharacterCount by remember(bodyText, dateText, startTyping) { mutableIntStateOf(0) }
    var hasNotifiedTypingFinished by remember(bodyText, dateText, startTyping) { mutableStateOf(false) }

    LaunchedEffect(bodyText, dateText, startTyping) {
        titleVisibleCharacterCount = 0
        topDividerVisibleCharacterCount = 0
        dateVisibleCharacterCount = 0
        bottomDividerVisibleCharacterCount = 0
        bodyVisibleCharacterCount = 0
        hasNotifiedTypingFinished = false
    }

    LaunchedEffect(bodyText, dateText, startTyping, isPaused) {
        if (!startTyping || isPaused) return@LaunchedEffect

        var isTypingAnimationRunning = hasRemainingRecordTyping(
            bodyText = bodyText,
            dateText = dateText,
            titleVisibleCharacterCount = titleVisibleCharacterCount,
            topDividerVisibleCharacterCount = topDividerVisibleCharacterCount,
            dateVisibleCharacterCount = dateVisibleCharacterCount,
            bottomDividerVisibleCharacterCount = bottomDividerVisibleCharacterCount,
            bodyVisibleCharacterCount = bodyVisibleCharacterCount
        )

        if (isTypingAnimationRunning) {
            currentOnTypingAnimationStarted()
        }

        try {
            typeRecordTextFrom(
                text = RECORD_TITLE_TEXT,
                startVisibleCharacterCount = titleVisibleCharacterCount
            ) {
                titleVisibleCharacterCount = it
                currentOnTypingProgress()
            }
            delay(RECORD_TYPING_STEP_PAUSE_MILLIS)
            typeRecordTextFrom(
                text = RECORD_DIVIDER_TEXT,
                startVisibleCharacterCount = topDividerVisibleCharacterCount
            ) {
                topDividerVisibleCharacterCount = it
                currentOnTypingProgress()
            }
            delay(RECORD_TYPING_STEP_PAUSE_MILLIS)
            typeRecordTextFrom(
                text = dateText,
                startVisibleCharacterCount = dateVisibleCharacterCount
            ) {
                dateVisibleCharacterCount = it
                currentOnTypingProgress()
            }
            delay(RECORD_TYPING_STEP_PAUSE_MILLIS)
            typeRecordTextFrom(
                text = RECORD_DIVIDER_TEXT,
                startVisibleCharacterCount = bottomDividerVisibleCharacterCount
            ) {
                bottomDividerVisibleCharacterCount = it
                currentOnTypingProgress()
            }
            delay(RECORD_TYPING_STEP_PAUSE_MILLIS)
            typeRecordTextFrom(
                text = bodyText,
                startVisibleCharacterCount = bodyVisibleCharacterCount
            ) {
                bodyVisibleCharacterCount = it
                currentOnTypingProgress()
            }

            if (isTypingAnimationRunning) {
                isTypingAnimationRunning = false
                currentOnTypingAnimationFinished()
            }
        } finally {
            if (isTypingAnimationRunning) {
                currentOnTypingAnimationFinished()
            }
        }

        if (hasNotifiedTypingFinished) return@LaunchedEffect

        delay(RECORD_AFTER_TYPING_DELAY_MILLIS)
        hasNotifiedTypingFinished = true
        currentOnTypingFinished()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp)
    ) {
        TypingRecordText(
            text = RECORD_TITLE_TEXT,
            visibleCharacterCount = titleVisibleCharacterCount,
            modifier = Modifier.fillMaxWidth()
        )

        TypingRecordText(
            text = RECORD_DIVIDER_TEXT,
            visibleCharacterCount = topDividerVisibleCharacterCount,
            modifier = Modifier.fillMaxWidth(),
            color = RecordDividerTextColor
        )

        TypingRecordText(
            text = dateText,
            visibleCharacterCount = dateVisibleCharacterCount,
            modifier = Modifier.fillMaxWidth()
        )

        TypingRecordText(
            text = RECORD_DIVIDER_TEXT,
            visibleCharacterCount = bottomDividerVisibleCharacterCount,
            modifier = Modifier.fillMaxWidth(),
            color = RecordDividerTextColor
        )

        Spacer(modifier = Modifier.height(22.dp))

        TypingRecordText(
            text = bodyText,
            visibleCharacterCount = bodyVisibleCharacterCount,
            modifier = Modifier.fillMaxWidth(),
            fontSize = 12,
            lineHeight = 22
        )
    }
}

private fun hasRemainingRecordTyping(
    bodyText: String,
    dateText: String,
    titleVisibleCharacterCount: Int,
    topDividerVisibleCharacterCount: Int,
    dateVisibleCharacterCount: Int,
    bottomDividerVisibleCharacterCount: Int,
    bodyVisibleCharacterCount: Int
): Boolean {
    return titleVisibleCharacterCount < RECORD_TITLE_TEXT.length ||
        topDividerVisibleCharacterCount < RECORD_DIVIDER_TEXT.length ||
        dateVisibleCharacterCount < dateText.length ||
        bottomDividerVisibleCharacterCount < RECORD_DIVIDER_TEXT.length ||
        bodyVisibleCharacterCount < bodyText.length
}

@Composable
private fun TypingRecordText(
    text: String,
    visibleCharacterCount: Int,
    modifier: Modifier = Modifier,
    fontSize: Int = 11,
    lineHeight: Int = 18,
    color: Color = ChatDescriptionTextColor
) {
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val autoScrollEnabled = LocalChatRecordTypingAutoScrollEnabled.current
    val currentVisibleCharacterCount by rememberUpdatedState(visibleCharacterCount)
    val visibleText = text.take(visibleCharacterCount.coerceIn(0, text.length))

    LaunchedEffect(autoScrollEnabled) {
        snapshotFlow { currentVisibleCharacterCount }
            .distinctUntilChanged()
            .collect { currentVisibleCharacterCount ->
                if (currentVisibleCharacterCount > 0 && autoScrollEnabled) {
                    bringIntoViewRequester.bringIntoView()
                }
            }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter
    ) {
        Text(
            text = text,
            color = color,
            fontSize = fontSize.sp,
            fontWeight = FontWeight(400),
            lineHeight = lineHeight.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.alpha(0f)
        )

        Text(
            text = visibleText,
            color = color,
            fontSize = fontSize.sp,
            fontWeight = FontWeight(400),
            lineHeight = lineHeight.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.bringIntoViewRequester(bringIntoViewRequester)
        )
    }
}

internal suspend fun typeRecordText(
    text: String,
    onVisibleCharacterCountChange: (Int) -> Unit
) {
    typeRecordTextFrom(
        text = text,
        startVisibleCharacterCount = 0,
        onVisibleCharacterCountChange = onVisibleCharacterCountChange
    )
}

internal suspend fun typeRecordTextFrom(
    text: String,
    startVisibleCharacterCount: Int,
    onVisibleCharacterCountChange: (Int) -> Unit
) {
    val startCount = startVisibleCharacterCount.coerceIn(0, text.length)

    if (startCount == text.length) return

    if (startCount == 0) {
        onVisibleCharacterCountChange(0)
    }

    for (visibleCharacterCount in (startCount + 1)..text.length) {
        delay(RECORD_TYPING_CHARACTER_DELAY_MILLIS)
        onVisibleCharacterCountChange(visibleCharacterCount)
    }
}
