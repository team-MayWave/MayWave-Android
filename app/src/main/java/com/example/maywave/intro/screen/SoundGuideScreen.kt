package com.example.maywave.intro.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maywave.R
import com.example.maywave.ui.theme.MayWaveTheme
import com.example.maywave.ui.theme.Pretendard

@Composable
fun SoundGuideScreen(
    onTouchStart: () -> Unit,
    modifier: Modifier = Modifier
) {
    SoundGuideContent(
        onTouchStart = onTouchStart,
        modifier = modifier
    )
}

@Composable
private fun SoundGuideContent(
    onTouchStart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onTouchStart
            )
            .statusBarsPadding()
    ) {
        SoundGuideCenterSection(
            modifier = Modifier.align(Alignment.Center)
        )

        SoundGuideBottomText(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
        )
    }
}

@Composable
private fun SoundGuideCenterSection(
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        SoundGuideHeadsetImage()

        Spacer(modifier = Modifier.height(50.dp))

        SoundGuideTitle()

        Spacer(modifier = Modifier.height(20.dp))

        SoundGuideDescription()
    }
}

@Composable
private fun SoundGuideHeadsetImage(
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(id = R.drawable.sound_headset),
        contentDescription = "헤드셋",
        modifier = modifier.size(120.dp)
    )
}

@Composable
private fun SoundGuideTitle(
    modifier: Modifier = Modifier
) {
    Text(
        text = "소리를 켜주세요",
        color = Color.White,
        fontFamily = Pretendard,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 30.sp,
        textAlign = TextAlign.Center,
        modifier = modifier
    )
}

@Composable
private fun SoundGuideDescription(
    modifier: Modifier = Modifier
) {
    Text(
        text = "더 몰입감 있는 경험을 위해\n소리재생을 권장합니다.",
        color = Color.White.copy(alpha = 0.68f),
        fontFamily = Pretendard,
        fontWeight = FontWeight.Normal,
        fontSize = 17.sp,
        lineHeight = 24.sp,
        textAlign = TextAlign.Center,
        modifier = modifier
    )
}

@Composable
private fun SoundGuideBottomText(
    modifier: Modifier = Modifier
) {
    Text(
        text = "화면을 터치하면 시작됩니다",
        color = Color.White.copy(alpha = 0.55f),
        fontFamily = Pretendard,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 20.sp,
        textAlign = TextAlign.Center,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun SoundGuideScreenPreview() {
    MayWaveTheme {
        SoundGuideScreen(
            onTouchStart = {}
        )
    }
}
