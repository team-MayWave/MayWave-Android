package com.example.maywave.chat.component.record

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maywave.ui.theme.ChatDescriptionTextColor
import kotlinx.coroutines.delay

private const val RECORD_TITLE_TEXT = "[기록]"
private const val RECORD_DIVIDER_TEXT = "──────────────"
private const val RECORD_TYPING_CHARACTER_DELAY_MILLIS = 72L
private const val RECORD_TYPING_STEP_PAUSE_MILLIS = 120L

@Composable
fun ChatRecord(
    bodyText: String,
    modifier: Modifier = Modifier,
    dateText: String = "1980년 5월 19일",
    onTypingFinished: () -> Unit = {}
) {
    val currentOnTypingFinished by rememberUpdatedState(onTypingFinished)
    var titleVisibleCharacterCount by remember(bodyText, dateText) { mutableIntStateOf(0) }
    var topDividerVisibleCharacterCount by remember(bodyText, dateText) { mutableIntStateOf(0) }
    var dateVisibleCharacterCount by remember(bodyText, dateText) { mutableIntStateOf(0) }
    var bottomDividerVisibleCharacterCount by remember(bodyText, dateText) { mutableIntStateOf(0) }
    var bodyVisibleCharacterCount by remember(bodyText, dateText) { mutableIntStateOf(0) }

    LaunchedEffect(bodyText, dateText) {
        titleVisibleCharacterCount = 0
        topDividerVisibleCharacterCount = 0
        dateVisibleCharacterCount = 0
        bottomDividerVisibleCharacterCount = 0
        bodyVisibleCharacterCount = 0

        typeRecordText(RECORD_TITLE_TEXT) { titleVisibleCharacterCount = it }
        delay(RECORD_TYPING_STEP_PAUSE_MILLIS)
        typeRecordText(RECORD_DIVIDER_TEXT) { topDividerVisibleCharacterCount = it }
        delay(RECORD_TYPING_STEP_PAUSE_MILLIS)
        typeRecordText(dateText) { dateVisibleCharacterCount = it }
        delay(RECORD_TYPING_STEP_PAUSE_MILLIS)
        typeRecordText(RECORD_DIVIDER_TEXT) { bottomDividerVisibleCharacterCount = it }
        delay(RECORD_TYPING_STEP_PAUSE_MILLIS)
        typeRecordText(bodyText) { bodyVisibleCharacterCount = it }
        currentOnTypingFinished()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 42.dp)
    ) {
        TypingRecordText(
            text = RECORD_TITLE_TEXT,
            visibleCharacterCount = titleVisibleCharacterCount
        )

        TypingRecordText(
            text = RECORD_DIVIDER_TEXT,
            visibleCharacterCount = topDividerVisibleCharacterCount
        )

        TypingRecordText(
            text = dateText,
            visibleCharacterCount = dateVisibleCharacterCount
        )

        TypingRecordText(
            text = RECORD_DIVIDER_TEXT,
            visibleCharacterCount = bottomDividerVisibleCharacterCount
        )

        Spacer(modifier = Modifier.height(22.dp))

        TypingRecordText(
            text = bodyText,
            visibleCharacterCount = bodyVisibleCharacterCount,
            fontSize = 12,
            lineHeight = 22
        )
    }
}

@Composable
private fun TypingRecordText(
    text: String,
    visibleCharacterCount: Int,
    modifier: Modifier = Modifier,
    fontSize: Int = 11,
    lineHeight: Int = 18
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        Text(
            text = text,
            color = ChatDescriptionTextColor,
            fontSize = fontSize.sp,
            fontWeight = FontWeight(400),
            lineHeight = lineHeight.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.alpha(0f)
        )

        Text(
            text = text.take(visibleCharacterCount.coerceIn(0, text.length)),
            color = ChatDescriptionTextColor,
            fontSize = fontSize.sp,
            fontWeight = FontWeight(400),
            lineHeight = lineHeight.sp,
            textAlign = TextAlign.Center
        )
    }
}

private suspend fun typeRecordText(
    text: String,
    onVisibleCharacterCountChange: (Int) -> Unit
) {
    onVisibleCharacterCountChange(0)

    for (visibleCharacterCount in 1..text.length) {
        delay(RECORD_TYPING_CHARACTER_DELAY_MILLIS)
        onVisibleCharacterCountChange(visibleCharacterCount)
    }
}
