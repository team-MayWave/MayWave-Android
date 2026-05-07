package com.example.maywave.chat.component.media

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ChatSceneImage(
    @DrawableRes imageResId: Int,
    contentDescription: String,
    modifier: Modifier = Modifier,
    height: Dp = 214.dp,
    contentScale: ContentScale = ContentScale.Crop
) {
    Image(
        painter = painterResource(id = imageResId),
        contentDescription = contentDescription,
        contentScale = contentScale,
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .padding(horizontal = 32.dp)
    )
}
