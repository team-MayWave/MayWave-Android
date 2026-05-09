package com.example.maywave.chat.component.message

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
fun OtherChatElement(
    modifier: Modifier = Modifier,
    nameText: String = "",
    chatText: String = "",
    showProfileImage: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(start = 22.dp, top = 1.dp),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    contentHorizontalAlignment: Alignment.Horizontal = Alignment.Start,
    nameTextAlign: TextAlign = TextAlign.Start
) {
    Row(
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = Alignment.Top,
        modifier = modifier
            .fillMaxWidth()
            .padding(contentPadding)
    ) {
        if (showProfileImage) {
            Image(
                painter = painterResource(id = R.drawable.other_chat_profile),
                contentDescription = "상대 채팅 프로필",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .offset(y = 6.dp)
                    .width(37.dp)
                    .height(36.dp)
            )

            Spacer(modifier = Modifier.width(1.dp))
        }

        Column(
            horizontalAlignment = contentHorizontalAlignment
        ) {
            Text(
                text = nameText,
                color = Color.White,
                fontSize = 10.sp,
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight(400),
                lineHeight = 10.sp,
                letterSpacing = 0.sp,
                textAlign = nameTextAlign,
                modifier = Modifier
                    .padding(start = 20.dp, end = 14.dp)
            )

            Box(
                modifier = Modifier
                    .padding(top = 3.dp)
                    .offset(x = 12.dp)
                    .widthIn(max = 174.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.other_chat),
                    contentDescription = "상대 채팅 말풍선",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .matchParentSize()
                        .clip(RoundedCornerShape(15.dp))
                )

                Text(
                    text = chatText,
                    color = ChatChoiceTextColor,
                    fontSize = 10.sp,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight(600),
                    lineHeight = 13.sp,
                    letterSpacing = 0.sp,
                    modifier = Modifier
                        .padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 16.dp)
                        .widthIn(max = 236.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 300)
@Composable
private fun OtherChatElementPreview() {
    MayWaveTheme {
        Column(modifier = Modifier.background(Color.Black)) {

            OtherChatElement(
                nameText = "주변 시민",
                chatText = "전남대 쪽에서 학생들이 막혔다더라. 계엄군이 들어왔대."
            )
        }
    }
}
