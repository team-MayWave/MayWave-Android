package com.example.maywave.chat.component.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.maywave.R

@Composable
fun ChatBackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(id = R.drawable.chat_back_button),
        contentDescription = "이전 화면",
        contentScale = ContentScale.Fit,
        modifier = modifier
            .size(19.dp)
            .clickable(onClick = onClick)
    )
}
