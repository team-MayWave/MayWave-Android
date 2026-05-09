package com.example.maywave.intro.text.rolename

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun IntroRoleNameText(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        color = Color.White,
        fontSize = 32.sp,
        lineHeight = 34.sp,
        fontWeight = FontWeight(600),
        modifier = modifier
    )
}
