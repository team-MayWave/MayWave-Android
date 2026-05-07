package com.example.maywave.intro.text.description

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
fun IntroRoleDescriptionText(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        color = Color.White,
        fontSize = 15.sp,
        textAlign = TextAlign.Center,
        fontFamily = FontFamily.Serif,
        modifier = modifier
    )
}
