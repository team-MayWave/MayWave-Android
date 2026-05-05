package com.example.maywave.text.rolename

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun IntroRoleNameText(modifier: Modifier = Modifier) {
    Text(
        text = "시민",
        color = Color.White,
        fontSize = 32.sp,
        lineHeight = 34.sp,
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.W600,
        modifier = modifier
    )
}
