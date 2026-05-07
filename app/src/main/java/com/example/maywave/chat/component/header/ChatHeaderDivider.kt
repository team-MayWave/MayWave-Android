package com.example.maywave.chat.component.header

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.maywave.ui.theme.MayWaveTheme

@Composable
fun ChatHeaderDivider(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .width(350.dp)
            .height(1.dp)
            .background(Color.White.copy(alpha = 0.2f))
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun ChatHeaderDividerPreview() {
    MayWaveTheme {
        ChatHeaderDivider()
    }
}
