package com.example.maywave.chat.component.header

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maywave.ui.theme.MayWaveTheme

@Composable
fun ChatTopTitle(
    modifier: Modifier = Modifier,
    titleText: String = "시민",
    descriptionText: String = "1980년 5월 18일, 광주"
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = titleText,
            color = Color.White,
            fontSize = 25.sp,
            fontWeight = FontWeight(600),
            lineHeight = 28.sp,
            letterSpacing = 0.sp,
            textAlign = TextAlign.Center,

        )

        Spacer(modifier = Modifier.height(9.dp))

        Text(
            text = descriptionText,
            color = Color(0xFF5D5D5D),
            fontSize = 15.sp,
            fontWeight = FontWeight(400),
            lineHeight = 15.sp,
            letterSpacing = 0.sp,
            textAlign = TextAlign.Center,

        )

        Spacer(modifier = Modifier.height(11.dp))

        ChatHeaderDivider()

    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 393)
@Composable
private fun ChatTopTitlePreview() {
    MayWaveTheme {
        ChatTopTitle()
    }
}
