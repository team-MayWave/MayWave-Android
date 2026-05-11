package com.example.maywave.chat.component.overlay

import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.maywave.R

fun chatInfoOverlayContentFor(stateKey: String?): ChatInfoOverlayContent? {
    return when (stateKey) {
        "citizen_intro_scene" -> citizenResistanceStartInfo()
        "citizen_close_choice" -> citizenStreetDayInfo()
        "citizen_distance_scene" -> citizenMilitaryControlInfo()
        "citizen_avoid_situation_scene" -> citizenGeumnamroMarchInfo()
        "citizen_help_fallen_scene" -> citizenAirborneCrackdownInfo()
        "doctor_intro_choice",
        "doctor_run_to_patient_choice",
        "doctor_focus_patient_scene" -> doctorStreetTreatmentInfo()
        "doctor_request_transfer_scene" -> doctorHospitalLimitInfo()
        "doctor_prepare_hospital_scene" -> doctorHospitalOverloadInfo()
        "reporter_intro_choice" -> reporterFirstRecordInfo()
        "reporter_continue_record_scene" -> reporterGumnamroRecordInfo()
        "reporter_escape_scene" -> reporterUnrecordedSceneInfo()
        else -> null
    }
}

private fun citizenResistanceStartInfo() = ChatInfoOverlayContent(
    title = "광주의 저항이 시작된 날",
    description = "시민들과 학생들은 계엄 확대와\n유혈 진압에 맞서 항거했습니다.",
    imageResId = R.drawable.chat_info_citizen_resistance_start,
    imageContentDescription = "광주 시내로 진입한 군용 차량",
    imageHeight = 240.dp,
    imageWidthFraction = 0.56f,
    imageContentScale = ContentScale.Crop,
    history = ChatInfoHistory(
        bodyText = "1980년 광주의 끔찍한 사건은 1979년 10월 26일 박정희 前 대통령 사망 이후, 유신독재 정권의 수혜자였던 전두환, 노태우를 위시한 신군부집단이 12.12쿠데타로 군권을 장악하고 정권 탈취의 야욕을 드러내며 시작된다.\n\n1980년 5월 17일 비상계엄 전국 확대에 따라 계엄군의 유혈 진압에 맞서 광주시민들과 학생들은 계엄 해제와 민주화를 요구하며 항거했다.",
        sourceText = MAY_18_ARCHIVES_SOURCE
    )
)

private fun citizenStreetDayInfo() = ChatInfoOverlayContent(
    title = "시민들이 거리로 나온 날",
    description = "계엄령 확대와 언론 통제에 분노한 시민들이\n민주화를 요구하며 거리로 나섰습니다.",
    imageResId = R.drawable.chat_info_citizen_street_day,
    imageContentDescription = "계엄군 앞에 모인 시민과 학생들",
    imageHeight = 188.dp,
    history = ChatInfoHistory(
        dateText = "1980년 5월 18일 오전 10시경",
        bodyText = "1980년 5월 17일 밤, 전남대에서 진주한 계엄군은 도서관 등 공부하고 있던 학생들을 무자비하게 구타하고 불법 구금하였다. 다음 날인 5월 18일 아침 학교에 등교하거나 5.17비상계엄확대조치에 항의하기 위해 정문 앞에 모인 학생들을 무자비하게 강제해산시켰다. 이에 학생들이 항의하면서 항쟁의 불씨가 되었다.",
        sourceText = MAY_18_ARCHIVES_SOURCE
    )
)

private fun citizenMilitaryControlInfo() = ChatInfoOverlayContent(
    title = "계엄군이 광주를 통제하기 시작한 날",
    description = "계엄군과 시민들의 충돌이 이어졌습니다.\n이후 광주에서는 최초의 총격 사건이 발생했습니다.",
    imageResId = R.drawable.chat_info_citizen_military_control,
    imageContentDescription = "시민을 진압하는 계엄군",
    imageHeight = 188.dp,
    history = ChatInfoHistory(
        dateText = "1980년 5월 19일 오후 4시경",
        bodyText = "시위 진압차 출동한 11공수여단과 63대 소속 장갑차가 계림동 광주고교 부근(250-91번지)에서 시위대에 포위되어 공격을 당하자 안에 타고 있던 차 아무개 대위가 해치를 열고 M16을 난사, 고교생 1명(김영찬), 초등학생 2명, 중학생 2명이 총상을 입는다.",
        sourceText = "출처: 소년중앙-중앙일보"
    )
)

private fun citizenGeumnamroMarchInfo() = ChatInfoOverlayContent(
    title = "학생들이 금남로로 향하기 시작한 순간",
    description = "시민들은 “금남로로 가자”를 외치며 이동했습니다.\n시위는 광주 시내로 퍼져갔습니다.",
    imageResId = R.drawable.chat_info_citizen_geumnamro_march,
    imageContentDescription = "금남로를 가득 메운 시민과 학생들",
    imageHeight = 180.dp,
    history = ChatInfoHistory(
        dateText = "1980년 5월 18일 오전 10시 20분",
        bodyText = "전남대학교 정문 앞에 모인 학생들은 “금남로로 가자”는 구호를 외치며 금남로 방향으로 이동하기 시작했습니다. 이후 학생들의 행렬은 광주 시내로 이어졌고, 시민들도 하나둘 거리로 나오기 시작했습니다.",
        sourceText = MAY_18_ARCHIVES_SOURCE
    )
)

private fun citizenAirborneCrackdownInfo() = ChatInfoOverlayContent(
    title = "공수부대의 진압이 시작된 날",
    description = "공수부대가 유동 3거리에 투입되기 시작했습니다.\n거리 곳곳에서는 강경 진압과 폭력이 이어졌습니다.",
    imageResId = R.drawable.chat_info_citizen_airborne_crackdown,
    imageContentDescription = "부상자를 끌고 가는 공수부대원들",
    imageHeight = 240.dp,
    history = ChatInfoHistory(
        dateText = "1980년 5월 18일 오후 3시 40분",
        bodyText = "유동 3거리 일대에 공수부대가 투입되면서 시민들과 학생들에 대한 강경 진압이 본격적으로 시작되었습니다. 계엄군은 시위대를 무차별적으로 폭행하고 강제 해산시켰으며, 광주 시내 곳곳에서는 충돌과 폭력 상황이 이어졌습니다.",
        sourceText = MAY_18_ARCHIVES_SOURCE
    )
)

private fun doctorStreetTreatmentInfo() = ChatInfoOverlayContent(
    title = "거리에서 치료가 시작된 순간",
    description = "부상자들이 계속 늘어나고 있었습니다.\n의료진은 거리에서 치료를 이어갔습니다.",
    imageResId = R.drawable.chat_info_doctor_street_treatment,
    imageContentDescription = "거리에서 부상자를 치료하는 시민과 의료진",
    imageHeight = 198.dp,
    history = ChatInfoHistory(
        bodyText = "계엄군의 강경 진압으로 거리 곳곳에서 부상자가 발생하기 시작했습니다.\n병원으로 옮길 수 없는 상황에서는 의료진과 시민들이 길거리에서 직접 응급 처치를 이어갔고, 의대생과 간호사들도 부상자 구조에 참여했습니다.\n\n당시 광주기독병원과 전남대병원 의료진들은 부족한 의료 물품 속에서도 시민들을 치료하고 있었습니다.",
        sourceText = MAY_18_ARCHIVES_SOURCE
    )
)

private fun doctorHospitalLimitInfo() = ChatInfoOverlayContent(
    title = "병원이 부상자들로 가득 찬 날",
    description = "병원에는 부상자들이 계속 실려왔습니다.\n의료진은 부족한 인력 속에서 치료를 이어갔습니다.",
    imageResId = R.drawable.chat_info_doctor_hospital_color,
    imageContentDescription = "부상자가 실려온 병원 응급실",
    imageHeight = 198.dp,
    history = ChatInfoHistory(
        bodyText = "계엄군의 강경 진압으로 광주 시내 곳곳에서 부상자가 발생했고, 전남대병원과 광주기독병원에는 시민들이 계속해서 실려오기 시작했습니다.\n\n의료진과 간호사, 의대생들은 부족한 약품과 인력 속에서도 밤낮 없이 치료를 이어갔으며, 병원 복도와 응급실까지 부상자들로 가득 차기 시작했습니다.\n\n당시 일부 시민들은 직접 헌혈과 환자 이송에 참여하며 구조 활동을 도왔습니다.",
        sourceText = MAY_18_ARCHIVES_SOURCE
    )
)

private fun doctorHospitalOverloadInfo() = ChatInfoOverlayContent(
    title = "병원이 혼란으로 가득 찬 날",
    description = "병원에는 부상자들이 계속 실려왔습니다.\n의료진은 부족한 인력 속에서 치료를 이어갔습니다.",
    imageResId = R.drawable.chat_info_doctor_hospital_overload,
    imageContentDescription = "부상자가 가득한 병실",
    imageHeight = 198.dp,
    history = ChatInfoHistory(
        bodyText = "계엄군의 강경 진압으로 광주 시내 곳곳에서 부상자가 발생했고, 전남대병원과 광주기독병원에는 시민들이 계속해서 실려오기 시작했습니다.\n\n의료진과 간호사, 의대생들은 부족한 약품과 인력 속에서도 밤낮 없이 치료를 이어갔으며, 병원 복도와 응급실까지 부상자들로 가득 차기 시작했습니다.\n\n당시 일부 시민들은 직접 헌혈과 환자 이송에 참여하며 구조 활동을 도왔습니다.",
        sourceText = MAY_18_ARCHIVES_SOURCE
    )
)

private fun reporterFirstRecordInfo() = ChatInfoOverlayContent(
    title = "공수부대의 진압이 시작된 날",
    description = "공수부대가 유동 3거리에 투입되기 시작했습니다.\n거리 곳곳에서는 강경 진압과 폭력이 이어졌습니다.",
    imageResId = R.drawable.chat_info_citizen_airborne_crackdown,
    imageContentDescription = "부상자를 끌고 가는 공수부대원들",
    imageHeight = 180.dp,
    descriptionToImageSpacing = 30.dp,
    imageToHistorySpacing = 35.dp,
    history = ChatInfoHistory(
        dateText = "1980년 5월 18일 오후 3시 40분",
        bodyText = "유동 3거리 일대에 공수부대가 투입되면서 시민들과 학생들에 대한 강경 진압이 본격적으로 시작되었습니다. 계엄군은 시위대를 무차별적으로 폭행하고 강제 해산시켰으며, 광주 시내 곳곳에서는 충돌과 폭력 상황이 이어졌습니다.",
        sourceText = MAY_18_ARCHIVES_SOURCE
    )
)

private fun reporterGumnamroRecordInfo() = ChatInfoOverlayContent(
    title = "금남로의 기록이 남은 순간",
    description = "광주의 현장을 담은 기록은\n세계에 진실을 전하는 통로가 되었습니다.",
    imageResId = R.drawable.reporter_gumnamro_record,
    imageContentDescription = "광주 현장을 촬영하는 기자",
    imageHeight = 180.dp,
    history = ChatInfoHistory(
        dateText = "1980년 5월 20일",
        bodyText = "독일 기자 힌츠페터는 광주에 잠입해 시민들과 계엄군의 충돌 현장을 카메라에 기록했습니다.\n그가 촬영한 영상과 사진은 해외로 전달되었고, 언론 통제 속에서도 광주의 상황이 세계에 알려지는 중요한 계기가 되었습니다.",
        sourceText = MAY_18_ARCHIVES_SOURCE
    )
)

private fun reporterUnrecordedSceneInfo() = ChatInfoOverlayContent(
    title = "기록되지 못한 장면들",
    description = "모든 순간이 기록으로 남지는 못했지만\n그 또한 광주의 일부였습니다.",
    imageResId = R.drawable.chat_reportor_2,
    imageContentDescription = "군인을 피해 달아나는 기자",
    imageHeight = 188.dp,
    history = ChatInfoHistory(
        bodyText = "광주에서는 많은 사건들이 발생했지만, 모든 순간이 사진과 영상으로 남지는 못했습니다.\n기록되지 못한 장면들은 생존자와 목격자의 증언, 이후의 조사와 자료 수집을 통해 기억되고 있습니다.\n\n남겨진 기록과 남지 못한 기억은 함께 5·18민주화운동의 진실을 구성합니다.",
        sourceText = MAY_18_ARCHIVES_SOURCE
    )
)

private const val MAY_18_ARCHIVES_SOURCE = "출처: 5·18민주화운동기록관"
