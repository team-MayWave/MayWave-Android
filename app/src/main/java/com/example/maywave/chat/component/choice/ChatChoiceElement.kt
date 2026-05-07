package com.example.maywave.chat.component.choice

import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.maywave.R
import com.example.maywave.ui.theme.ChatChoiceTextBackgroundColor
import com.example.maywave.ui.theme.ChatChoiceTextBackgroundOutlineColor
import com.example.maywave.ui.theme.ChatChoiceTextColor
import com.example.maywave.ui.theme.ChatChoiceTitleColor
import com.example.maywave.ui.theme.MayWaveTheme

@Composable
fun ChatChoiceElement(
    firstChoiceText: String,
    secondChoiceText: String,
    onFirstChoiceClick: () -> Unit,
    onSecondChoiceClick: () -> Unit,
    thirdChoiceText: String? = null,
    onThirdChoiceClick: (() -> Unit)? = null,
    selectedChoiceIndex: Int? = null,
    modifier: Modifier = Modifier
) {
    val isChoiceLocked = selectedChoiceIndex != null

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
    ) {
        ChatChoiceTitle()

        Spacer(modifier = Modifier.height(23.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 68.dp)
        ) {
            ChoiceButton(
                text = firstChoiceText,
                onClick = onFirstChoiceClick,
                isSelected = selectedChoiceIndex == FIRST_CHOICE_INDEX,
                isDimmed = selectedChoiceIndex != null && selectedChoiceIndex != FIRST_CHOICE_INDEX,
                enabled = !isChoiceLocked
            )

            Spacer(modifier = Modifier.height(9.dp))

            ChoiceButton(
                text = secondChoiceText,
                onClick = onSecondChoiceClick,
                isSelected = selectedChoiceIndex == SECOND_CHOICE_INDEX,
                isDimmed = selectedChoiceIndex != null && selectedChoiceIndex != SECOND_CHOICE_INDEX,
                enabled = !isChoiceLocked
            )

            if (thirdChoiceText != null && onThirdChoiceClick != null) {
                Spacer(modifier = Modifier.height(9.dp))

                ChoiceButton(
                    text = thirdChoiceText,
                    onClick = onThirdChoiceClick,
                    isSelected = selectedChoiceIndex == THIRD_CHOICE_INDEX,
                    isDimmed = selectedChoiceIndex != null && selectedChoiceIndex != THIRD_CHOICE_INDEX,
                    enabled = !isChoiceLocked
                )
            }
        }
    }
}

@Composable
private fun ChatChoiceTitle(modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.width(27.dp))

        Image(
            painter = painterResource(id = R.drawable.location_text_left),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .width(100.dp)
                .height(1.dp)
        )

        Text(
            text = "당신의 선택은?",
            color = ChatChoiceTitleColor,
            fontSize = 10.sp,
            fontWeight = FontWeight(400),
            fontFamily = FontFamily.Serif,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(148.dp)
        )

        Image(
            painter = painterResource(id = R.drawable.location_text_right),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .width(100.dp)
                .height(1.dp)
        )
    }
}

@Composable
private fun ChoiceButton(
    text: String,
    onClick: () -> Unit,
    isSelected: Boolean,
    isDimmed: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val buttonBackgroundColor = if (isDimmed) {
        ChatChoiceTextBackgroundColor.copy(alpha = DIMMED_CHOICE_ALPHA)
    } else {
        ChatChoiceTextBackgroundColor
    }
    val buttonOutlineColor = when {
        isDimmed -> ChatChoiceTextBackgroundOutlineColor.copy(alpha = DIMMED_CHOICE_ALPHA)
        isSelected -> ChatChoiceTextBackgroundOutlineColor
        else -> ChatChoiceTextBackgroundOutlineColor
    }
    val buttonTextColor = if (isDimmed) {
        ChatChoiceTextColor.copy(alpha = DIMMED_CHOICE_ALPHA)
    } else {
        ChatChoiceTextColor
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .height(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(color = buttonBackgroundColor)
            .border(
                border = BorderStroke(1.dp, color = buttonOutlineColor),
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(
                enabled = enabled,
                onClick = onClick
            )
    ) {
        Text(
            text = text,
            color = buttonTextColor,
            fontSize = 11.sp,
            lineHeight = 13.sp,
            fontWeight = if (isDimmed) FontWeight(600) else FontWeight.Normal,
            fontFamily = FontFamily.Serif,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 10.dp)
        )
    }
}

private const val FIRST_CHOICE_INDEX = 0
private const val SECOND_CHOICE_INDEX = 1
private const val THIRD_CHOICE_INDEX = 2
private const val DIMMED_CHOICE_ALPHA = 0.3f

@Preview(showBackground = true, widthDp = 393)
@Composable
private fun ChatChoiceElementPreview() {
    MayWaveTheme {
        ChatChoiceElement(
            firstChoiceText = "가까이 가서 본다",
            secondChoiceText = "멀리서 본다",
            onFirstChoiceClick = {},
            onSecondChoiceClick = {}
        )
    }
}
