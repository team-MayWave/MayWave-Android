package com.example.maywave.text

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun IntroSelectText(modifier: Modifier = Modifier) {
    Text(
        text = "선택하기",
        color = Color.White,
        fontSize = 22.sp,
        fontWeight = FontWeight(600),
        modifier = modifier
    )
}
