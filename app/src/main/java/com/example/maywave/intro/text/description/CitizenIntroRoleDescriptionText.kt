package com.example.maywave.intro.text.description

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CitizenIntroRoleDescriptionText(modifier: Modifier = Modifier) {
    IntroRoleDescriptionText(
        text = "그날, 평범한 시민이었습니다.\n그리고, 역사의 한가운데에 있었습니다.",
        modifier = modifier
    )
}
