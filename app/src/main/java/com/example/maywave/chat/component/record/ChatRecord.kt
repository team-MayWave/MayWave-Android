package com.example.maywave.chat.component.record

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maywave.ui.theme.ChatDescriptionTextColor

@Composable
fun ChatRecord(
    bodyText: String,
    modifier: Modifier = Modifier,
    dateText: String = "1980년 5월 19일"
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 42.dp)
    ) {
        Text(
            text = "[기록]",
            color = ChatDescriptionTextColor,
            fontSize = 11.sp,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight(400),
            lineHeight = 18.sp,
            textAlign = TextAlign.Center
        )

        Text(
            text = "──────────────",
            color = ChatDescriptionTextColor,
            fontSize = 11.sp,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight(400),
            lineHeight = 18.sp,
            textAlign = TextAlign.Center
        )

        Text(
            text = dateText,
            color = ChatDescriptionTextColor,
            fontSize = 11.sp,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight(400),
            lineHeight = 18.sp,
            textAlign = TextAlign.Center
        )

        Text(
            text = "──────────────",
            color = ChatDescriptionTextColor,
            fontSize = 11.sp,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight(400),
            lineHeight = 18.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = bodyText,
            color = ChatDescriptionTextColor,
            fontSize = 12.sp,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight(400),
            lineHeight = 22.sp,
            textAlign = TextAlign.Center
        )
    }
}
