package com.example.maywave.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.unit.dp
import androidx.annotation.DrawableRes
import com.example.maywave.R
import com.example.maywave.button.IntroArrowButton
import com.example.maywave.text.description.DoctorIntroRoleDescriptionText
import com.example.maywave.text.rolename.DoctorIntroRoleNameText
import com.example.maywave.text.description.CitizenRoleDescriptionText
import com.example.maywave.text.rolename.IntroRoleNameText
import com.example.maywave.text.IntroSelectText
import com.example.maywave.text.description.ReporterIntroRoleDescriptionText
import com.example.maywave.text.rolename.ReporterIntroRoleNameText
import com.example.maywave.ui.theme.MayWaveTheme

@Composable
fun Intro(modifier: Modifier = Modifier) {
    var selectedRole by rememberSaveable { mutableStateOf(IntroRole.Citizen) }

    IntroRoleScreen(
        roleImageRes = selectedRole.imageRes,
        roleImageDescription = selectedRole.imageDescription,
        roleName = { selectedRole.RoleNameText() },
        roleDescription = { selectedRole.RoleDescriptionText() },
        onPreviousRole = { selectedRole = selectedRole.previous() },
        onNextRole = { selectedRole = selectedRole.next() },
        modifier = modifier
    )
}

@Composable
fun CitizenIntro(modifier: Modifier = Modifier) {
    IntroRoleScreen(
        roleImageRes = R.drawable.intro_citizen,
        roleImageDescription = "시민 역할",
        roleName = { IntroRoleNameText() },
        roleDescription = { CitizenRoleDescriptionText() },
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
) {
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
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
                .height(580.dp)
                .width(386.dp)
        )

        IntroArrowButton(
            arrowImageRes = R.drawable.intro_previous_role,
            contentDescription = "이전 역할",
            onClick = onPreviousRole,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = 21.dp, y = (-15).dp)
        )

        IntroArrowButton(
            arrowImageRes = R.drawable.intro_next_role,
            contentDescription = "다음 역할",
            onClick = onNextRole,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = (-21).dp, y = (-15).dp)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .offset(y = (-71).dp)
        ) {
            roleName()

            Spacer(modifier = Modifier.height(10.dp))

            RoleDivider()

            Spacer(modifier = Modifier.height(21.76.dp))

            roleDescription()

            Spacer(modifier = Modifier.height(66.dp))

            IntroSelectText()
        }
    }
}

@Composable
private fun RoleDivider(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.intro_role_divider),
        contentDescription = "구분선",
        contentScale = ContentScale.Fit,
        modifier = modifier
            .width(124.dp)
            .height(7.dp)
    )
}

private enum class IntroRole(
    @param:DrawableRes val imageRes: Int,
    val imageDescription: String
) {
    Citizen(
        imageRes = R.drawable.intro_citizen,
        imageDescription = "시민 역할"
    ),
    Doctor(
        imageRes = R.drawable.intro_doctor,
        imageDescription = "의사 역할"
    ),
    Reporter(
        imageRes = R.drawable.intro_reporter,
        imageDescription = "기자 역할"
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
        IntroRole.Citizen -> IntroRoleNameText()
        IntroRole.Doctor -> DoctorIntroRoleNameText()
        IntroRole.Reporter -> ReporterIntroRoleNameText()
    }
}

@Composable
private fun IntroRole.RoleDescriptionText() {
    when (this) {
        IntroRole.Citizen -> CitizenRoleDescriptionText()
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

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun CitizenPreview() {
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
