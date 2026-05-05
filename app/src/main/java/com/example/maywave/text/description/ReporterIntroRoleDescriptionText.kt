package com.example.maywave.text.description

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
fun ReporterIntroRoleDescriptionText(modifier: Modifier = Modifier) {
    Text(
        text = "그날, 진실은 쉽게 보이지 않았습니다.\n당신은 그것을 기록하려 합니다.",
        color = Color.White,
        fontSize = 15.sp,
        textAlign = TextAlign.Center,
        fontFamily = FontFamily.Serif,
        modifier = modifier
    )
}
