package com.example.maywave.text.description

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
fun DoctorIntroRoleDescriptionText(modifier: Modifier = Modifier) {
    Text(
        text = "그날, 환자들이 몰려왔습니다.\n그리고, 멈출 수 없었습니다.",
        color = Color.White,
        fontSize = 15.sp,
        textAlign = TextAlign.Center,
        fontFamily = FontFamily.Serif,
        modifier = modifier
    )
}
