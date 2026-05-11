package com.example.maywave.chat.component.header

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.maywave.R
import com.example.maywave.ui.theme.MayWaveTheme

@Composable
fun ChatInfoButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showUnreadDot: Boolean = false,
    onRead: () -> Unit = {}
) {
    val density = LocalDensity.current
    val transition = rememberInfiniteTransition(label = "ChatInfoButtonTransition")
    val offsetY by transition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = UNREAD_DOT_SHAKE_INTERVAL_MILLIS
                0f at 0 using LinearEasing
                -UNREAD_DOT_SHAKE_OFFSET_DP at 90 using LinearEasing
                UNREAD_DOT_SHAKE_OFFSET_DP at 180 using LinearEasing
                -UNREAD_DOT_SHAKE_OFFSET_DP at 270 using LinearEasing
                0f at UNREAD_DOT_SHAKE_DURATION_MILLIS using LinearEasing
                0f at UNREAD_DOT_SHAKE_INTERVAL_MILLIS using LinearEasing
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "ChatInfoButtonOffsetY"
    )

    Box(
        modifier = modifier
            .size(24.dp)
            .clickable(
                role = Role.Button,
                onClick = {
                    onRead()
                    onClick()
                }
            )
    ) {
        val animatedModifier = if (showUnreadDot) {
            Modifier.offset {
                IntOffset(
                    x = 0,
                    y = with(density) { offsetY.dp.roundToPx() }
                )
            }
        } else {
            Modifier
        }

        Box(modifier = animatedModifier.size(24.dp)) {
            Image(
                painter = painterResource(id = R.drawable.chat_info_button),
                contentDescription = "채팅 정보",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(24.dp)
            )

            if (showUnreadDot) {
                ChatInfoUnreadDot(modifier = Modifier.align(Alignment.BottomEnd))
            }
        }
    }
}

@Composable
private fun ChatInfoUnreadDot(
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(id = R.drawable.chat_info_unread_dot),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = modifier.size(8.dp)
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun ChatInfoButtonPreview() {
    MayWaveTheme {
        ChatInfoButton(
            onClick = {},
            showUnreadDot = true
        )
    }
}

private const val UNREAD_DOT_SHAKE_DURATION_MILLIS = 360
private const val UNREAD_DOT_SHAKE_INTERVAL_MILLIS = 2_000
private const val UNREAD_DOT_SHAKE_OFFSET_DP = 2f
