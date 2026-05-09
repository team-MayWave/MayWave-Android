package com.example.maywave.chat.component.header

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.maywave.ui.theme.ChatChoiceTitleColor
import com.example.maywave.ui.theme.MayWaveTheme

@Composable
fun ElementTitle(
    modifier: Modifier = Modifier,
    dateText: String = "5월 18일",
    locationText: String = "광주, 금남로"
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.location_text_left),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .width(40.dp)
                    .height(1.dp)
            )

            Spacer(modifier = Modifier.width(13.dp))

            Text(
                text = dateText,
                color = ChatChoiceTitleColor,
                fontSize = 15.sp,
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight(400),
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.width(13.dp))

            Image(
                painter = painterResource(id = R.drawable.location_text_right),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .width(40.dp)
                    .height(1.dp)
            )
        }

        Spacer(modifier = Modifier.height(9.dp))

        Text(
            text = locationText,
            color = ChatChoiceTitleColor,
            fontSize = 12.sp,
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight(400),
            lineHeight = 9.sp,
            letterSpacing = 0.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 393)
@Composable
private fun ElementTitlePreview() {
    MayWaveTheme {
        ElementTitle(
            modifier = Modifier.background(Color.Black)
        )
    }
}
