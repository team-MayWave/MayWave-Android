package com.example.maywave.chat.component.message

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maywave.R
import com.example.maywave.ui.theme.ChatChoiceTextColor
import com.example.maywave.ui.theme.MayWaveTheme

@Composable
fun MyChatElement(
    modifier: Modifier = Modifier,
    chatText: String = ""
) {
    Row(
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Top,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 1.dp, end = 22.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "나",
                color = Color.White,
                fontSize = 10.sp,
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight(400),
                fontFamily = FontFamily.Serif,
                lineHeight = 10.sp,
                letterSpacing = 0.sp,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .padding(end = 20.dp)
                    .height(12.dp)
            )

            Box(
                modifier = Modifier
                    .padding(top = 3.dp)
                    .offset(x = (-12).dp)
                    .widthIn(max = 174.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.other_chat),
                    contentDescription = "내 채팅 말풍선",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .matchParentSize()
                        .graphicsLayer(scaleX = -1f)
                        .clip(RoundedCornerShape(15.dp))
                )

                Text(
                    text = chatText,
                    color = ChatChoiceTextColor,
                    fontSize = 10.sp,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight(600),
                    fontFamily = FontFamily.Serif,
                    lineHeight = 13.sp,
                    letterSpacing = 0.sp,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .padding(start = 12.dp, top = 8.dp, end = 12.dp, bottom = 8.dp)
                        .widthIn(max = 236.dp)
                )
            }
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 300)
@Composable
private fun MyChatElementPreview() {
    MayWaveTheme {
        Column(modifier = Modifier.background(Color.Black)) {
            MyChatElement(chatText = "무슨 일인데?")
        }
    }
}
