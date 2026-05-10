package com.example.maywave.chat.component.header

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
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
        Image(
            painter = painterResource(id = R.drawable.chat_info_button),
            contentDescription = "채팅 정보",
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(24.dp)
        )

        if (showUnreadDot) {
            Image(
                painter = painterResource(id = R.drawable.chat_info_unread_dot),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(8.dp)
            )
        }
    }
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
