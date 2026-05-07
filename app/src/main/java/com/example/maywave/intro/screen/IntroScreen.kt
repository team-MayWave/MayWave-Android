package com.example.maywave.intro.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.annotation.DrawableRes
import com.example.maywave.R
import com.example.maywave.chat.navigation.ChatRoute
import com.example.maywave.intro.component.IntroArrowButton
import com.example.maywave.intro.component.IntroSelectTextButton
import com.example.maywave.intro.text.description.CitizenIntroRoleDescriptionText
import com.example.maywave.intro.text.description.DoctorIntroRoleDescriptionText
import com.example.maywave.intro.text.description.ReporterIntroRoleDescriptionText
import com.example.maywave.intro.text.rolename.CitizenIntroRoleNameText
import com.example.maywave.intro.text.rolename.DoctorIntroRoleNameText
import com.example.maywave.intro.text.rolename.ReporterIntroRoleNameText
import com.example.maywave.ui.theme.MayWaveTheme

@Composable
fun Intro(
    modifier: Modifier = Modifier,
    onStartChat: (ChatRoute) -> Unit = {}
) {
    var selectedRole by rememberSaveable { mutableStateOf(IntroRole.Citizen) }

    IntroRoleScreen(
        roleImageRes = selectedRole.imageRes,
        roleImageDescription = selectedRole.imageDescription,
        roleName = { selectedRole.RoleNameText() },
        roleDescription = { selectedRole.RoleDescriptionText() },
        onPreviousRole = { selectedRole = selectedRole.previous() },
        onNextRole = { selectedRole = selectedRole.next() },
        onClick = { onStartChat(selectedRole.route) },
        modifier = modifier
    )
}

@Composable
fun CitizenIntro(modifier: Modifier = Modifier) {
    IntroRoleScreen(
        roleImageRes = R.drawable.intro_citizen,
        roleImageDescription = "시민 역할",
        roleName = { CitizenIntroRoleNameText() },
        roleDescription = { CitizenIntroRoleDescriptionText() },
        modifier = modifier
    )
}

@Composable
fun DoctorIntro(modifier: Modifier = Modifier) {
    IntroRoleScreen(
        roleImageRes = R.drawable.intro_doctor,
        roleImageDescription = "의사 역할",
        roleName = { DoctorIntroRoleNameText() },
        roleDescription = { DoctorIntroRoleDescriptionText() },
        modifier = modifier
    )
}

@Composable
fun ReporterIntro(modifier: Modifier = Modifier) {
    IntroRoleScreen(
        roleImageRes = R.drawable.intro_reporter,
        roleImageDescription = "기자 역할",
        roleName = { ReporterIntroRoleNameText() },
        roleDescription = { ReporterIntroRoleDescriptionText() },
        modifier = modifier
    )
}

@Composable
private fun IntroRoleScreen(
    @DrawableRes roleImageRes: Int,
    roleImageDescription: String,
    roleName: @Composable () -> Unit,
    roleDescription: @Composable () -> Unit,
    modifier : Modifier = Modifier,
    onPreviousRole: () -> Unit = {},
    onNextRole: () -> Unit = {},
    onClick: () -> Unit ={}
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
    ) {
        val widthScale = maxWidth / ReferenceScreenWidth
        val heightScale = maxHeight / ReferenceScreenHeight
        val scale = minOf(widthScale, heightScale)
        val selectButtonCenterY = backgroundSourceYToScreenY(
            sourceYRatio = SelectButtonBackgroundYRatio,
            screenWidth = maxWidth,
            screenHeight = maxHeight
        )

        Image(
            painter = painterResource(id = R.drawable.intro_role_background),
            contentDescription = "역할 소개 배경",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Image(
            painter = painterResource(id = roleImageRes),
            contentDescription = roleImageDescription,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = RoleImageTopOffset * scale)
                .width(RoleImageWidth * scale)
                .height(RoleImageHeight * scale)
        )

        IntroArrowButton(
            arrowImageRes = R.drawable.intro_previous_role,
            contentDescription = "이전 역할",
            onClick = onPreviousRole,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = ArrowHorizontalOffset * scale, y = ArrowVerticalOffset * scale)
        )

        IntroArrowButton(
            arrowImageRes = R.drawable.intro_next_role,
            contentDescription = "다음 역할",
            onClick = onNextRole,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = -ArrowHorizontalOffset * scale, y = ArrowVerticalOffset * scale)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .offset(y = 165.dp)
        ) {
            roleName()

            Spacer(modifier = Modifier.height(RoleNameDividerSpacing * scale))

            RoleDivider(scale = scale)

            Spacer(modifier = Modifier.height(DividerDescriptionSpacing * scale))

            roleDescription()
        }

        IntroSelectTextButton(
            onClick = onClick,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = selectButtonCenterY - SelectButtonVisualHeight / 1.5f)
        )
    }
}

private fun backgroundSourceYToScreenY(
    sourceYRatio: Float,
    screenWidth: Dp,
    screenHeight: Dp
): Dp {
    val screenAspectRatio = screenWidth / screenHeight
    val drawnBackgroundHeight = if (screenAspectRatio > BackgroundImageAspectRatio) {
        screenWidth / BackgroundImageAspectRatio
    } else {
        screenHeight
    }
    val croppedTopHeight = (drawnBackgroundHeight - screenHeight) / 2

    return drawnBackgroundHeight * sourceYRatio - croppedTopHeight
}

@Composable
private fun RoleDivider(
    modifier: Modifier = Modifier,
    scale: Float = 1f
) {
    Image(
        painter = painterResource(id = R.drawable.intro_role_divider),
        contentDescription = "구분선",
        contentScale = ContentScale.Fit,
        modifier = modifier
            .size(width = RoleDividerWidth * scale, height = RoleDividerHeight * scale)
    )
}

private val ReferenceScreenWidth = 393.dp
private val ReferenceScreenHeight = 852.dp
private val RoleImageTopOffset = 11.dp
private val RoleImageWidth = 371.dp
private val RoleImageHeight = 558.dp
private val ImageRoleTextSpacing = 35.dp
private val RoleNameDividerSpacing = 10.dp
private val DividerDescriptionSpacing = 21.76.dp
private val RoleDividerWidth = 124.dp
private val RoleDividerHeight = 7.dp
private val ArrowHorizontalOffset = 21.dp
private val ArrowVerticalOffset = (-15).dp
private const val BackgroundImageAspectRatio = 853f / 1844f
private const val SelectButtonBackgroundYRatio = 0.9f
private val SelectButtonVisualHeight = 25.dp


private enum class IntroRole(
    @param:DrawableRes val imageRes: Int,
    val imageDescription: String,
    val route: ChatRoute
) {
    Citizen(
        imageRes = R.drawable.intro_citizen,
        imageDescription = "시민 역할",
        route = ChatRoute.Citizen
    ),
    Doctor(
        imageRes = R.drawable.intro_doctor,
        imageDescription = "의사 역할",
        route = ChatRoute.Doctor
    ),
    Reporter(
        imageRes = R.drawable.intro_reporter,
        imageDescription = "기자 역할",
        route = ChatRoute.Reporter
    );

    fun previous(): IntroRole = when (this) {
        Citizen -> Reporter
        Doctor -> Citizen
        Reporter -> Doctor
    }

    fun next(): IntroRole = when (this) {
        Citizen -> Doctor
        Doctor -> Reporter
        Reporter -> Citizen
    }
}

@Composable
private fun IntroRole.RoleNameText() {
    when (this) {
        IntroRole.Citizen -> CitizenIntroRoleNameText()
        IntroRole.Doctor -> DoctorIntroRoleNameText()
        IntroRole.Reporter -> ReporterIntroRoleNameText()
    }
}

@Composable
private fun IntroRole.RoleDescriptionText() {
    when (this) {
        IntroRole.Citizen -> CitizenIntroRoleDescriptionText()
        IntroRole.Doctor -> DoctorIntroRoleDescriptionText()
        IntroRole.Reporter -> ReporterIntroRoleDescriptionText()
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun IntroPreview() {
    MayWaveTheme {
        Intro()
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852,showSystemUi = true)
@Composable
private fun CitizenIntroPreview() {
    MayWaveTheme {
        CitizenIntro()
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun DoctorIntroPreview() {
    MayWaveTheme {
        DoctorIntro()
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun ReporterIntroPreview() {
    MayWaveTheme {
        ReporterIntro()
    }
}
