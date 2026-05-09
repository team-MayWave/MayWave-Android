package com.example.maywave.chat.component.message

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.maywave.ui.theme.ChatFirstTextColor
import com.example.maywave.ui.theme.MayWaveTheme

@Composable
fun ChatFirstText(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        color = ChatFirstTextColor,
        fontSize = 15.sp,
        fontWeight = FontWeight(400),
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()
    )
}

@Preview(showBackground = false)
@Composable
private fun ChatFirstTextPreview() {
    MayWaveTheme {
        ChatFirstText(
            text = "당신은 신문 움직이지 못했습니다. 눈앞의 상황은 낯설고, 어디까지 다가가야 할지 알 수 없었습니다."
        )
    }
}
