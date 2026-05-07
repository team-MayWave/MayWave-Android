package com.example.maywave.intro.text.description

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun DoctorIntroRoleDescriptionText(modifier: Modifier = Modifier) {
    IntroRoleDescriptionText(
        text = "그날, 환자들이 몰려왔습니다.\n그리고, 멈출 수 없었습니다.",
        modifier = modifier
    )
}
