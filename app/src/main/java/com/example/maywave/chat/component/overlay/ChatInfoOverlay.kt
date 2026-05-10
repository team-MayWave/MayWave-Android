package com.example.maywave.chat.component.overlay

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.maywave.R
import com.example.maywave.ui.theme.ChatInfoOverlayBackgroundColor
import com.example.maywave.ui.theme.ChatInfoOverlayDescriptionTextColor
import com.example.maywave.ui.theme.ChatInfoOverlayHistoryBackgroundColor
import com.example.maywave.ui.theme.ChatInfoOverlayHistoryTextColor
import com.example.maywave.ui.theme.ChatInfoOverlayHistoryTitleColor
import com.example.maywave.ui.theme.MayWaveTheme
import com.example.maywave.ui.theme.NanumMyeongjo
import com.example.maywave.ui.theme.Pretendard

data class ChatInfoOverlayContent(
    val title: String,
    val description: String,
    val imageResId: Int,
    val imageContentDescription: String,
    val history: ChatInfoHistory,
    val imageHeight: Dp = 214.dp,
    val imageWidthFraction: Float = 1f,
    val imageContentScale: ContentScale = ContentScale.Crop
)

data class ChatInfoHistory(
    val dateText: String? = null,
    val bodyText: String,
    val sourceText: String
)

@Composable
fun ChatInfoOverlay(
    content: ChatInfoOverlayContent,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrimInteractionSource = remember { MutableInteractionSource() }

    BoxWithConstraints(
        contentAlignment = Alignment.TopCenter,
        modifier = modifier
            .fillMaxSize()
            .clickable(
                interactionSource = scrimInteractionSource,
                indication = null,
                onClick = {}
            )
            .background(Color.Black.copy(alpha = CHAT_INFO_SCRIM_ALPHA))
    ) {
        val cardTopOffset = CHAT_INFO_HEADER_DIVIDER_BOTTOM + CHAT_INFO_HEADER_TO_CARD_SPACING
        val cardHeight = maxHeight - cardTopOffset - CHAT_INFO_BOTTOM_SPACING

        ChatInfoOverlayCard(
            content = content,
            onCloseClick = onCloseClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = cardTopOffset)
                .height(cardHeight)
        )
    }
}

@Composable
private fun ChatInfoOverlayCard(
    content: ChatInfoOverlayContent,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = ChatInfoOverlayBackgroundColor,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.03f)),
        modifier = modifier
    ) {
        Box {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(start = 20.dp, top = 31.dp, end = 20.dp, bottom = 29.dp)
            ) {
                ChatInfoTitle(text = content.title)

                Spacer(modifier = Modifier.height(17.dp))

                ChatInfoDescription(text = content.description)

                Spacer(modifier = Modifier.height(28.dp))

                ChatInfoMainImage(
                    imageResId = content.imageResId,
                    contentDescription = content.imageContentDescription,
                    height = content.imageHeight,
                    widthFraction = content.imageWidthFraction,
                    contentScale = content.imageContentScale
                )

                Spacer(modifier = Modifier.height(36.dp))

                ChatInfoHistoryBox(history = content.history)
            }

            ChatInfoCloseButton(
                onClick = onCloseClick,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 2.dp, top = 2.dp)
                    .zIndex(1f)
            )
        }
    }
}

@Composable
private fun ChatInfoCloseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(48.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick
            )
    ) {
        Text(
            text = "×",
            color = Color.White,
            fontSize = 21.sp,
            fontWeight = FontWeight(400),
            lineHeight = 21.sp,
            letterSpacing = 0.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ChatInfoTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        color = Color.White,
        fontFamily = NanumMyeongjo,
        fontSize = 18.sp,
        fontWeight = FontWeight(400),
        lineHeight = 18.sp,
        letterSpacing = 0.sp,
        textAlign = TextAlign.Center,
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
private fun ChatInfoDescription(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        color = ChatInfoOverlayDescriptionTextColor,
        fontFamily = NanumMyeongjo,
        fontSize = 15.sp,
        fontWeight = FontWeight(400),
        lineHeight = 20.sp,
        letterSpacing = 0.sp,
        textAlign = TextAlign.Center,
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
private fun ChatInfoMainImage(
    @DrawableRes imageResId: Int,
    contentDescription: String,
    height: Dp,
    widthFraction: Float,
    contentScale: ContentScale,
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(id = imageResId),
        contentDescription = contentDescription,
        contentScale = contentScale,
        modifier = modifier
            .fillMaxWidth(fraction = widthFraction)
            .height(height)
            .clip(RoundedCornerShape(8.dp))
    )
}

@Composable
private fun ChatInfoHistoryBox(
    history: ChatInfoHistory,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(8.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(ChatInfoOverlayHistoryBackgroundColor)
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.18f),
                shape = shape
            )
            .padding(start = 16.dp, top = 15.dp, end = 14.dp, bottom = 8.dp)
    ) {
        ChatInfoHistoryTitle()

        Spacer(modifier = Modifier.height(13.dp))

        history.dateText?.let { dateText ->
            ChatInfoHistoryDate(text = dateText)

            Spacer(modifier = Modifier.height(12.dp))
        }

        ChatInfoHistoryBody(text = history.bodyText)

        Spacer(modifier = Modifier.height(5.dp))

        ChatInfoSource(text = history.sourceText)
    }
}

@Composable
private fun ChatInfoHistoryTitle(
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Image(
            painter = painterResource(id = R.drawable.chat_info_history_icon),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(18.dp)
        )

        Spacer(modifier = Modifier.size(3.dp))

        Text(
            text = "역사적 배경",
            color = ChatInfoOverlayHistoryTitleColor,
            fontFamily = Pretendard,
            fontSize = 15.sp,
            fontWeight = FontWeight(500),
            lineHeight = 15.sp,
            letterSpacing = 0.sp
        )
    }
}

@Composable
private fun ChatInfoHistoryDate(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        color = Color.White,
        fontFamily = Pretendard,
        fontSize = 13.sp,
        fontWeight = FontWeight(400),
        lineHeight = 13.sp,
        letterSpacing = 0.sp,
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
private fun ChatInfoHistoryBody(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        color = ChatInfoOverlayHistoryTextColor,
        fontFamily = Pretendard,
        fontSize = 13.sp,
        fontWeight = FontWeight(400),
        lineHeight = 14.sp,
        letterSpacing = 0.sp,
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
private fun ChatInfoSource(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        color = ChatInfoOverlayDescriptionTextColor,
        fontFamily = Pretendard,
        fontSize = 8.sp,
        fontWeight = FontWeight(400),
        lineHeight = 13.sp,
        letterSpacing = 0.sp,
        textAlign = TextAlign.End,
        textDecoration = TextDecoration.Underline,
        modifier = modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 393, heightDp = 852)
@Composable
private fun ChatInfoOverlayPreview() {
    MayWaveTheme {
        ChatInfoOverlay(
            content = ChatInfoOverlayContent(
                title = "시민들이 거리로 나온 날",
                description = "계엄령 확대와 언론 통제에 분노한 시민들이\n민주화를 요구하며 거리로 나섰습니다.",
                imageResId = R.drawable.citizen_chat_branch_one_first,
                imageContentDescription = "거리로 나온 시민들",
                history = ChatInfoHistory(
                    dateText = "1980년 5월 18일 오전 10시경",
                    bodyText = "1980년 5월 17일 밤, 전남대에서 진주한 계엄군은 도서관 등 공부하고 있던 학생들을 무자비하게 구타하고 불법 구금하였다. 다음 날인 5월 18일 아침 학교에 등교하거나 5.17비상계엄확대조치에 항의하기 위해 정문 앞에 모인 학생들을 무자비하게 강제해산시켰다. 이에 학생들이 항의하면서 항쟁의 불씨가 되었다.",
                    sourceText = "출처: 5·18민주화운동기록관"
                )
            ),
            onCloseClick = {}
        )
    }
}

private const val CHAT_INFO_SCRIM_ALPHA = 0.72f
private val CHAT_INFO_HEADER_DIVIDER_BOTTOM = 88.dp
private val CHAT_INFO_HEADER_TO_CARD_SPACING = 80.dp
private val CHAT_INFO_BOTTOM_SPACING = 150.dp
