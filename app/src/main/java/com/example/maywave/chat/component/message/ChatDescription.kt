package com.example.maywave.chat.component.message

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.maywave.ui.theme.ChatDescriptionTextColor
import com.example.maywave.ui.theme.MayWaveTheme

@Composable
fun ChatDescription(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        color = ChatDescriptionTextColor,
        fontSize = 11.sp,
        fontWeight = FontWeight(400),
        textAlign = TextAlign.Center,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun ChatDescriptionPreview() {
    MayWaveTheme {
        ChatDescription(
            text = "당신은 신문 움직이지 못했습니다. 눈앞의 상황은 낯설고, 어디까지 다가가야 할지 알 수 없었습니다."
        )
    }
}
