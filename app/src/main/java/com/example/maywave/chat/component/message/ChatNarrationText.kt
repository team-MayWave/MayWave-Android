package com.example.maywave.chat.component.message

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maywave.ui.theme.ChatDescriptionTextColor

@Composable
fun ChatNarrationText(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        color = ChatDescriptionTextColor,
        fontSize = 12.sp,
        fontWeight = FontWeight(400),
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp)
    )
}
