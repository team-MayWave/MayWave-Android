package com.example.maywave.chat.component.overlay

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maywave.chat.component.navigation.ChatBackButton
import kotlinx.coroutines.delay

private const val FINAL_FADE_DURATION_MILLIS = 2_000
private const val FINAL_HOLD_DURATION_MILLIS = 2_000

internal data class ChatFinalStep(
    val text: String,
    val fontSize: TextUnit = 14.sp,
    val lineHeight: TextUnit = 31.sp,
    val showDate: Boolean = false
)

@Composable
internal fun ChatFinalFadeOverlay(
    sequenceKey: Any,
    steps: List<ChatFinalStep>,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isBackgroundVisible by remember(sequenceKey) { mutableStateOf(false) }
    var visibleStepIndex by remember(sequenceKey) { mutableIntStateOf(NO_VISIBLE_STEP_INDEX) }
    val backgroundAlpha by animateFloatAsState(
        targetValue = if (isBackgroundVisible) 1f else 0f,
        animationSpec = tween(durationMillis = FINAL_FADE_DURATION_MILLIS),
        label = "final background alpha"
    )

    LaunchedEffect(sequenceKey, steps.size) {
        isBackgroundVisible = true
        delay(FINAL_FADE_DURATION_MILLIS.toLong())

        steps.indices.forEach { stepIndex ->
            visibleStepIndex = stepIndex

            if (stepIndex != steps.lastIndex) {
                delay((FINAL_FADE_DURATION_MILLIS + FINAL_HOLD_DURATION_MILLIS).toLong())
                visibleStepIndex = NO_VISIBLE_STEP_INDEX
                delay(FINAL_FADE_DURATION_MILLIS.toLong())
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = backgroundAlpha))
    ) {
        ChatBackButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 28.dp, top = 42.dp)
                .alpha(backgroundAlpha)
        )

        steps.forEachIndexed { stepIndex, step ->
            val stepAlpha by animateFloatAsState(
                targetValue = if (visibleStepIndex == stepIndex) 1f else 0f,
                animationSpec = tween(durationMillis = FINAL_FADE_DURATION_MILLIS),
                label = "final step alpha"
            )

            ChatFinalStepContent(
                step = step,
                modifier = Modifier.alpha(stepAlpha)
            )
        }
    }
}

@Composable
private fun ChatFinalStepContent(
    step: ChatFinalStep,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 44.dp)
    ) {
        Text(
            text = step.text,
            color = Color.White.copy(alpha = 0.9f),
            fontSize = step.fontSize,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight(400),
            lineHeight = step.lineHeight,
            letterSpacing = 0.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center)
        )

        if (step.showDate) {
            Text(
                text = "1980.05.18",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 14.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight(400),
                letterSpacing = 0.sp,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 18.dp)
            )
        }
    }
}

private const val NO_VISIBLE_STEP_INDEX = -1
