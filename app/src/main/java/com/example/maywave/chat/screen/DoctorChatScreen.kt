package com.example.maywave.chat.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.maywave.R
import com.example.maywave.chat.component.choice.ChatChoiceElement
import com.example.maywave.chat.component.header.ChatTopTitle
import com.example.maywave.chat.component.header.ElementTitle
import com.example.maywave.chat.component.media.ChatSceneImage
import com.example.maywave.chat.component.message.ChatFirstText
import com.example.maywave.chat.component.message.ChatNarrationText
import com.example.maywave.chat.component.message.MyChatElement
import com.example.maywave.chat.component.message.OtherChatElement
import com.example.maywave.chat.component.navigation.ChatBackButton
import com.example.maywave.chat.component.overlay.ChatFinalFadeOverlay
import com.example.maywave.chat.component.overlay.ChatFinalStep
import com.example.maywave.chat.component.record.ChatRecord
import com.example.maywave.ui.theme.MayWaveTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged

private const val DOCTOR_CHAT_ELEMENT_REVEAL_DURATION_MILLIS = 2_000
private const val DOCTOR_INITIAL_ELEMENT_COUNT = 8
private const val DOCTOR_RUN_TO_PATIENT_ELEMENT_COUNT = 17
private const val DOCTOR_PREPARE_HOSPITAL_ELEMENT_COUNT = 17
private const val DOCTOR_FOCUS_PATIENT_ELEMENT_COUNT = 15
private const val DOCTOR_CHECK_PATIENTS_ELEMENT_COUNT = 14
private const val DOCTOR_REQUEST_TRANSFER_ELEMENT_COUNT = 21
private val DOCTOR_CHAT_ELEMENT_SPACING = 35.dp
private val DOCTOR_BRANCH_CHAT_ELEMENT_SPACING = 35.dp
private val DOCTOR_AUTO_SCROLL_BOTTOM_PADDING = 16.dp

@Composable
fun DoctorChatScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
    val listState = rememberLazyListState()
    var selectedOpeningBranch by rememberSaveable { mutableStateOf<DoctorOpeningBranch?>(null) }
    var selectedTriageBranch by rememberSaveable { mutableStateOf<DoctorTriageBranch?>(null) }
    var openingRevealKey by rememberSaveable { mutableIntStateOf(0) }
    var triageRevealKey by rememberSaveable { mutableIntStateOf(0) }
    val shouldFollowNewContent = rememberDoctorShouldFollowNewContent(listState = listState)
    val revealedInitialItemCount = rememberDoctorSequentialRevealCount(
        itemCount = DOCTOR_INITIAL_ELEMENT_COUNT,
        initiallyVisibleItemCount = 1
    )
    val revealedRunToPatientItemCount = rememberDoctorSequentialRevealCount(
        itemCount = DOCTOR_RUN_TO_PATIENT_ELEMENT_COUNT,
        revealKey = openingRevealKey,
        enabled = selectedOpeningBranch == DoctorOpeningBranch.RunToPatient
    )
    val revealedPrepareHospitalItemCount = rememberDoctorSequentialRevealCount(
        itemCount = DOCTOR_PREPARE_HOSPITAL_ELEMENT_COUNT,
        revealKey = openingRevealKey,
        enabled = selectedOpeningBranch == DoctorOpeningBranch.PrepareHospital
    )
    val revealedFocusPatientItemCount = rememberDoctorSequentialRevealCount(
        itemCount = DOCTOR_FOCUS_PATIENT_ELEMENT_COUNT,
        revealKey = triageRevealKey,
        enabled = selectedTriageBranch == DoctorTriageBranch.FocusPatient
    )
    val revealedCheckPatientsItemCount = rememberDoctorSequentialRevealCount(
        itemCount = DOCTOR_CHECK_PATIENTS_ELEMENT_COUNT,
        revealKey = triageRevealKey,
        enabled = selectedTriageBranch == DoctorTriageBranch.CheckPatients
    )
    val revealedRequestTransferItemCount = rememberDoctorSequentialRevealCount(
        itemCount = DOCTOR_REQUEST_TRANSFER_ELEMENT_COUNT,
        revealKey = triageRevealKey,
        enabled = selectedTriageBranch == DoctorTriageBranch.RequestTransfer
    )
    val finalSequenceKey = when {
        selectedTriageBranch == DoctorTriageBranch.FocusPatient &&
            revealedFocusPatientItemCount >= DOCTOR_FOCUS_PATIENT_ELEMENT_COUNT -> "focus_patient"
        selectedTriageBranch == DoctorTriageBranch.CheckPatients &&
            revealedCheckPatientsItemCount >= DOCTOR_CHECK_PATIENTS_ELEMENT_COUNT -> "check_patients"
        selectedTriageBranch == DoctorTriageBranch.RequestTransfer &&
            revealedRequestTransferItemCount >= DOCTOR_REQUEST_TRANSFER_ELEMENT_COUNT -> "request_transfer"
        selectedOpeningBranch == DoctorOpeningBranch.PrepareHospital &&
            revealedPrepareHospitalItemCount >= DOCTOR_PREPARE_HOSPITAL_ELEMENT_COUNT -> "prepare_hospital"
        else -> null
    }
    AutoScrollOnDoctorReveal(
        listState = listState,
        revealKey = revealedInitialItemCount,
        enabled = revealedInitialItemCount > 1,
        targetItemIndex = revealedInitialItemCount - 1,
        shouldFollowNewContent = shouldFollowNewContent
    )

    AutoScrollOnDoctorReveal(
        listState = listState,
        revealKey = revealedRunToPatientItemCount,
        enabled = selectedOpeningBranch == DoctorOpeningBranch.RunToPatient && revealedRunToPatientItemCount > 0,
        targetItemIndex = DOCTOR_INITIAL_ELEMENT_COUNT,
        shouldFollowNewContent = shouldFollowNewContent
    )

    AutoScrollOnDoctorReveal(
        listState = listState,
        revealKey = revealedPrepareHospitalItemCount,
        enabled = selectedOpeningBranch == DoctorOpeningBranch.PrepareHospital &&
            revealedPrepareHospitalItemCount in 1 until DOCTOR_PREPARE_HOSPITAL_ELEMENT_COUNT,
        targetItemIndex = DOCTOR_INITIAL_ELEMENT_COUNT,
        shouldFollowNewContent = shouldFollowNewContent
    )

    AutoScrollOnDoctorReveal(
        listState = listState,
        revealKey = revealedFocusPatientItemCount,
        enabled = selectedTriageBranch == DoctorTriageBranch.FocusPatient &&
            revealedFocusPatientItemCount in 1 until DOCTOR_FOCUS_PATIENT_ELEMENT_COUNT,
        targetItemIndex = DOCTOR_INITIAL_ELEMENT_COUNT,
        shouldFollowNewContent = shouldFollowNewContent
    )

    AutoScrollOnDoctorReveal(
        listState = listState,
        revealKey = revealedCheckPatientsItemCount,
        enabled = selectedTriageBranch == DoctorTriageBranch.CheckPatients &&
            revealedCheckPatientsItemCount in 1 until DOCTOR_CHECK_PATIENTS_ELEMENT_COUNT,
        targetItemIndex = DOCTOR_INITIAL_ELEMENT_COUNT,
        shouldFollowNewContent = shouldFollowNewContent
    )

    AutoScrollOnDoctorReveal(
        listState = listState,
        revealKey = revealedRequestTransferItemCount,
        enabled = selectedTriageBranch == DoctorTriageBranch.RequestTransfer &&
            revealedRequestTransferItemCount in 1 until DOCTOR_REQUEST_TRANSFER_ELEMENT_COUNT,
        targetItemIndex = DOCTOR_INITIAL_ELEMENT_COUNT,
        shouldFollowNewContent = shouldFollowNewContent
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            DoctorChatHeader(onBackClick = onBackClick)

            LazyColumn(
                state = listState,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(DOCTOR_CHAT_ELEMENT_SPACING),
                contentPadding = PaddingValues(top = 49.dp, bottom = 48.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                item {
                    AnimatedDoctorChatItem(visible = revealedInitialItemCount >= 1) {
                        ElementTitle()
                    }
                }

                item {
                    AnimatedDoctorChatItem(visible = revealedInitialItemCount >= 2) {
                        ChatSceneImage(
                            imageResId = R.drawable.chat_first_img,
                            contentDescription = "금남로에 모인 시민들",
                            height = 214.dp
                        )
                    }
                }

                item {
                    AnimatedDoctorChatItem(visible = revealedInitialItemCount >= 3) {
                        ChatFirstText(text = "시내 분위기가 심상치 않습니다")
                    }
                }

                item {
                    AnimatedDoctorChatItem(visible = revealedInitialItemCount >= 4) {
                        ChatFirstText(text = "사람들이 모여들기 시작합니다")
                    }
                }

                item {
                    AnimatedDoctorChatItem(visible = revealedInitialItemCount >= 5) {
                        OtherChatElement(
                            nameText = "동료",
                            chatText = "전남대 쪽에서 다친 사람들이 나온대. 응급환자 들어올 수도 있어."
                        )
                    }
                }

                item {
                    AnimatedDoctorChatItem(visible = revealedInitialItemCount >= 6) {
                        ChatNarrationText(text = "잠시 후, 부상자가 발생했다는 소식이 전해집니다.")
                    }
                }

                item {
                    AnimatedDoctorChatItem(visible = revealedInitialItemCount >= 7) {
                        MyChatElement(chatText = "어디야? 상태 어때?")
                    }
                }

                item {
                    AnimatedDoctorChatItem(visible = revealedInitialItemCount >= 8) {
                        ChatChoiceElement(
                            firstChoiceText = "환자에게 바로 달려간다",
                            secondChoiceText = "병원으로 돌아가 대비한다",
                            onFirstChoiceClick = {
                                selectedOpeningBranch = DoctorOpeningBranch.RunToPatient
                                selectedTriageBranch = null
                                openingRevealKey += 1
                                triageRevealKey = 0
                            },
                            onSecondChoiceClick = {
                                selectedOpeningBranch = DoctorOpeningBranch.PrepareHospital
                                selectedTriageBranch = null
                                openingRevealKey += 1
                                triageRevealKey = 0
                            },
                            selectedChoiceIndex = when (selectedOpeningBranch) {
                                DoctorOpeningBranch.RunToPatient -> 0
                                DoctorOpeningBranch.PrepareHospital -> 1
                                null -> null
                            }
                        )
                    }
                }

                when (selectedOpeningBranch) {
                    DoctorOpeningBranch.RunToPatient -> {
                        item {
                            RunToPatientContent(
                                selectedTriageBranch = selectedTriageBranch,
                                revealedItemCount = revealedRunToPatientItemCount,
                                revealedFocusPatientItemCount = revealedFocusPatientItemCount,
                                revealedCheckPatientsItemCount = revealedCheckPatientsItemCount,
                                revealedRequestTransferItemCount = revealedRequestTransferItemCount,
                                onFocusPatientClick = {
                                    selectedTriageBranch = DoctorTriageBranch.FocusPatient
                                    triageRevealKey += 1
                                },
                                onCheckPatientsClick = {
                                    selectedTriageBranch = DoctorTriageBranch.CheckPatients
                                    triageRevealKey += 1
                                },
                                onRequestTransferClick = {
                                    selectedTriageBranch = DoctorTriageBranch.RequestTransfer
                                    triageRevealKey += 1
                                }
                            )
                        }
                    }

                    DoctorOpeningBranch.PrepareHospital -> {
                        item {
                            PrepareHospitalContent(revealedItemCount = revealedPrepareHospitalItemCount)
                        }
                    }

                    null -> Unit
                }
            }
        }

        if (finalSequenceKey != null) {
            ChatFinalFadeOverlay(
                sequenceKey = finalSequenceKey,
                steps = listOf(
                    ChatFinalStep(
                        text = "그날, 누군가는 한 사람에게 집중했고, 누군가는 더 많은 사람을 살피려 했으며, 누군가는 이송을 선택했습니다. 하지만 어떤 선택이든, 모두를 동시에 살릴 수 없는 상황 속에서 내려진 결정이었습니다. 그날의 판단은 지금까지 기억되고 있습니다."
                    ),
                    ChatFinalStep(
                        text = "당신은 그날의 의료진이었습니다."
                    ),
                    ChatFinalStep(
                        text = "그날의 시간은 끝났지만, 그날의 이야기는 아직 끝나지 않았습니다. 우리는, 그날을 기억합니다.",
                        showDate = true
                    )
                ),
                onBackClick = onBackClick
            )
        }
    }
}

@Composable
private fun DoctorChatHeader(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
    ) {
        ChatBackButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 28.dp, top = 18.dp)
        )

        ChatTopTitle(
            titleText = "의사",
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
        )
    }
}

@Composable
private fun RunToPatientContent(
    selectedTriageBranch: DoctorTriageBranch?,
    revealedItemCount: Int,
    revealedFocusPatientItemCount: Int,
    revealedCheckPatientsItemCount: Int,
    revealedRequestTransferItemCount: Int,
    onFocusPatientClick: () -> Unit,
    onCheckPatientsClick: () -> Unit,
    onRequestTransferClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(DOCTOR_BRANCH_CHAT_ELEMENT_SPACING),
        modifier = modifier.fillMaxWidth()
    ) {
        AnimatedDoctorChatItem(visible = revealedItemCount >= 1) {
            ChatNarrationText(text = "당신은 망설일 틈 없이 움직였습니다.")
        }

        AnimatedDoctorChatItem(visible = revealedItemCount >= 2) {
            ChatNarrationText(
                text = "그날, 많은 의료진이 위험 속에서도 부상자들에게 달려갔습니다."
            )
        }

        AnimatedDoctorChatItem(visible = revealedItemCount >= 3) {
            MyChatElement(chatText = "환자 어디 있어?")
        }

        AnimatedDoctorChatItem(visible = revealedItemCount >= 4) {
            OtherChatElement(
                nameText = "주변 시민",
                chatText = "여기요! 여기 좀 봐주세요!"
            )
        }

        AnimatedDoctorChatItem(visible = revealedItemCount >= 5) {
            ChatNarrationText(text = "쓰러진 사람이 보입니다.")
        }

        AnimatedDoctorChatItem(visible = revealedItemCount >= 6) {
            ChatSceneImage(
                imageResId = R.drawable.chat_doctor_first,
                contentDescription = "금남로에서 환자를 살피는 의사",
                height = 214.dp
            )
        }

        AnimatedDoctorChatItem(visible = revealedItemCount >= 7) {
            ChatNarrationText(text = "손이 멈칫합니다.")
        }

        AnimatedDoctorChatItem(visible = revealedItemCount >= 8) {
            MyChatElement(chatText = "잠깐만...")
        }

        AnimatedDoctorChatItem(visible = revealedItemCount >= 9) {
            MyChatElement(chatText = "이 상처...")
        }

        AnimatedDoctorChatItem(visible = revealedItemCount >= 10) {
            MyChatElement(chatText = "...총상이야")
        }

        AnimatedDoctorChatItem(visible = revealedItemCount >= 11) {
            MyChatElement(chatText = "이건... 사고가 아니야.")
        }

        AnimatedDoctorChatItem(visible = revealedItemCount >= 12) {
            ChatNarrationText(text = "순간, 상황이 다르게 보이기 시작합니다.")
        }

        AnimatedDoctorChatItem(visible = revealedItemCount >= 13) {
            OtherChatElement(
                nameText = "주변 시민",
                chatText = "살릴 수 있죠...?"
            )
        }

        AnimatedDoctorChatItem(visible = revealedItemCount >= 14) {
            ChatNarrationText(
                text = "환자를 살피던 순간, 다른 곳에서도 도움을 요청하는 소리가 들립니다."
            )
        }

        AnimatedDoctorChatItem(visible = revealedItemCount >= 15) {
            MyChatElement(chatText = "...")
        }

        AnimatedDoctorChatItem(visible = revealedItemCount >= 16) {
            MyChatElement(chatText = "환자가... 한 명이 아니야.")
        }

        AnimatedDoctorChatItem(visible = revealedItemCount >= 17) {
            ChatChoiceElement(
                firstChoiceText = "이 환자를 먼저 살린다",
                secondChoiceText = "다른 부상자들을 확인한다",
                thirdChoiceText = "병원으로 이송을 요청한다",
                onFirstChoiceClick = onFocusPatientClick,
                onSecondChoiceClick = onCheckPatientsClick,
                onThirdChoiceClick = onRequestTransferClick,
                selectedChoiceIndex = when (selectedTriageBranch) {
                    DoctorTriageBranch.FocusPatient -> 0
                    DoctorTriageBranch.CheckPatients -> 1
                    DoctorTriageBranch.RequestTransfer -> 2
                    null -> null
                }
            )
        }

        when (selectedTriageBranch) {
            DoctorTriageBranch.FocusPatient -> {
                FocusPatientContent(revealedItemCount = revealedFocusPatientItemCount)
            }

            DoctorTriageBranch.CheckPatients -> {
                CheckPatientsContent(revealedItemCount = revealedCheckPatientsItemCount)
            }

            DoctorTriageBranch.RequestTransfer -> {
                RequestTransferContent(revealedItemCount = revealedRequestTransferItemCount)
            }

            null -> Unit
        }
    }
}

@Composable
private fun FocusPatientContent(
    revealedItemCount: Int,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(DOCTOR_BRANCH_CHAT_ELEMENT_SPACING),
        modifier = modifier.fillMaxWidth()
    ) {
        AnimatedDoctorChatItem(visible = revealedItemCount >= 1) {
            MyChatElement(chatText = "지금 이 사람부터 볼게요.")
        }

        AnimatedDoctorChatItem(visible = revealedItemCount >= 2) {
            ChatNarrationText(text = "당신은 눈앞의 환자에게 집중합니다.")
        }

        AnimatedDoctorChatItem(visible = revealedItemCount >= 3) {
            MyChatElement(chatText = "의식 있어요?")
        }

        AnimatedDoctorChatItem(visible = revealedItemCount >= 4) {
            OtherChatElement(
                nameText = "주변 시민",
                chatText = "뭐라도 도와드릴까요?!"
            )
        }

        AnimatedDoctorChatItem(visible = revealedItemCount >= 5) {
            MyChatElement(chatText = "여기 눌러주세요! 계속!")
        }

        AnimatedDoctorChatItem(visible = revealedItemCount >= 6) {
            ChatNarrationText(text = "주변에서 계속 소리가 들립니다.")
        }

        AnimatedDoctorChatItem(visible = revealedItemCount >= 7) {
            OtherChatElement(
                nameText = "주변 시민",
                chatText = "여기도 다쳤어요!"
            )
        }

        AnimatedDoctorChatItem(visible = revealedItemCount >= 8) {
            OtherChatElement(
                nameText = "주변 시민",
                chatText = "이쪽도 좀 봐주세요!"
            )
        }

        AnimatedDoctorChatItem(visible = revealedItemCount >= 9) {
            ChatNarrationText(
                text = "다른 환자들이 보이지만, 지금은 이 사람을 놓을 수 없습니다."
            )
        }

        AnimatedDoctorChatItem(visible = revealedItemCount >= 10) {
            ChatNarrationText(text = "출혈이 쉽게 멈추지 않습니다.")
        }

        AnimatedDoctorChatItem(visible = revealedItemCount >= 11) {
            MyChatElement(chatText = "...이송해야 해.")
        }

        AnimatedDoctorChatItem(visible = revealedItemCount >= 12) {
            ChatNarrationText(
                text = "당신은 한 사람에게 집중했습니다. 그날, 많은 의료진이 눈앞의 생명을 살리기 위해 다른 선택을 뒤로 미뤄야 했습니다."
            )
        }

        AnimatedDoctorChatItem(visible = revealedItemCount >= 13) {
            ChatSceneImage(
                imageResId = R.drawable.chat_doctor_second,
                contentDescription = "현장에서 환자에게 응급 처치를 하는 의사",
                height = 214.dp
            )
        }

        AnimatedDoctorChatItem(visible = revealedItemCount >= 14) {
            ChatRecord(
                bodyText = "당시 현장에서는 의료 인력과 장비가 부족한 상황에서 많은 부상자들이 즉각적인 치료를 받지 못했습니다."
            )
        }
    }
}

@Composable
private fun CheckPatientsContent(
    revealedItemCount: Int,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(DOCTOR_BRANCH_CHAT_ELEMENT_SPACING),
        modifier = modifier.fillMaxWidth()
    ) {
        AnimatedDoctorChatItem(visible = revealedItemCount >= 1) {
            ChatNarrationText(
                text = "당신은 환자에게서 시선을 떼고 주변을 살핍니다."
            )
        }

        AnimatedDoctorChatItem(visible = revealedItemCount >= 2) {
            ChatNarrationText(text = "여러 명이 쓰러져 있습니다.")
        }

        AnimatedDoctorChatItem(visible = revealedItemCount >= 3) {
            ChatNarrationText(text = "곳곳에서 도움을 요청하는 소리가 들립니다.")
        }

        AnimatedDoctorChatItem(visible = revealedItemCount >= 4) {
            OtherChatElement(
                nameText = "주변 시민",
                chatText = "여기도요! 여기 좀 봐주세요!"
            )
        }

        AnimatedDoctorChatItem(visible = revealedItemCount >= 5) {
            OtherChatElement(
                nameText = "주변 시민",
                chatText = "피가 너무 많이 나요!"
            )
        }

        AnimatedDoctorChatItem(visible = revealedItemCount >= 6) {
            OtherChatElement(
                nameText = "주변 시민",
                chatText = "살려주세요..."
            )
        }

        AnimatedDoctorChatItem(visible = revealedItemCount >= 7) {
            MyChatElement(chatText = "한 명씩 볼 수 있는 상황이 아니야.")
        }

        AnimatedDoctorChatItem(visible = revealedItemCount >= 8) {
            ChatNarrationText(text = "환자 수가 계속 늘어나고 있습니다.")
        }

        AnimatedDoctorChatItem(visible = revealedItemCount >= 9) {
            ChatNarrationText(text = "당신 혼자 감당할 수 있는 수준이 아닙니다.")
        }

        AnimatedDoctorChatItem(visible = revealedItemCount >= 10) {
            MyChatElement(chatText = "모두를 살릴 수는 없어...")
        }

        AnimatedDoctorChatItem(visible = revealedItemCount >= 11) {
            MyChatElement(chatText = "...")
        }

        AnimatedDoctorChatItem(visible = revealedItemCount >= 12) {
            ChatSceneImage(
                imageResId = R.drawable.chat_doctor_fifth,
                contentDescription = "계엄군 차량과 시민들",
                height = 214.dp
            )
        }

        AnimatedDoctorChatItem(visible = revealedItemCount >= 13) {
            ChatRecord(
                bodyText = "광주에서는 많은 부상자들이 발생했고, 의료 현장은 그 상황을 감당하기 어려웠습니다."
            )
        }
    }
}

@Composable
private fun RequestTransferContent(
    revealedItemCount: Int,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(DOCTOR_BRANCH_CHAT_ELEMENT_SPACING),
        modifier = modifier.fillMaxWidth()
    ) {
        AnimatedDoctorChatItem(visible = revealedItemCount >= 1) {
            MyChatElement(chatText = "이송해야 해. 들것 있어요?")
        }

        AnimatedDoctorChatItem(visible = revealedItemCount >= 2) {
            OtherChatElement(
                nameText = "주변 시민",
                chatText = "차로 옮겨야 해요!"
            )
        }

        AnimatedDoctorChatItem(visible = revealedItemCount >= 3) {
            ChatNarrationText(text = "당신은 환자를 옮기기 시작합니다.")
        }

        AnimatedDoctorChatItem(visible = revealedItemCount >= 4) {
            ChatNarrationText(text = "몇몇 시민들이 함께 환자를 들어 올립니다.")
        }

        AnimatedDoctorChatItem(visible = revealedItemCount >= 5) {
            ChatNarrationText(text = "급히 차량으로 향합니다.")
        }

        HospitalOverloadItems(revealedItemCount = revealedItemCount, firstItemIndex = 6)
    }
}

@Composable
private fun PrepareHospitalContent(
    revealedItemCount: Int,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(DOCTOR_BRANCH_CHAT_ELEMENT_SPACING),
        modifier = modifier.fillMaxWidth()
    ) {
        AnimatedDoctorChatItem(visible = revealedItemCount >= 1) {
            ChatNarrationText(text = "당신은 병원으로 돌아가 응급 환자를 맞을 준비를 합니다.")
        }

        HospitalOverloadItems(revealedItemCount = revealedItemCount, firstItemIndex = 2)
    }
}

@Composable
private fun HospitalOverloadItems(
    revealedItemCount: Int,
    firstItemIndex: Int
) {
    AnimatedDoctorChatItem(visible = revealedItemCount >= firstItemIndex) {
        ChatSceneImage(
            imageResId = R.drawable.chat_doctor_third,
            contentDescription = "응급의료센터로 옮겨지는 환자",
            height = 214.dp
        )
    }

    AnimatedDoctorChatItem(visible = revealedItemCount >= firstItemIndex + 1) {
        ChatNarrationText(
            text = "병원에 도착했을 때, 이미 분위기가 심상치 않습니다."
        )
    }

    AnimatedDoctorChatItem(visible = revealedItemCount >= firstItemIndex + 2) {
        OtherChatElement(
            nameText = "동료",
            chatText = "환자 들어오기 시작했어!"
        )
    }

    AnimatedDoctorChatItem(visible = revealedItemCount >= firstItemIndex + 3) {
        OtherChatElement(
            nameText = "동료",
            chatText = "곧 더 올 거야!"
        )
    }

    AnimatedDoctorChatItem(visible = revealedItemCount >= firstItemIndex + 4) {
        ChatNarrationText(text = "환자들이 하나둘 들어오기 시작합니다.")
    }

    AnimatedDoctorChatItem(visible = revealedItemCount >= firstItemIndex + 5) {
        ChatNarrationText(
            text = "잠시 후, 환자가 급격히 늘어나기 시작합니다."
        )
    }

    AnimatedDoctorChatItem(visible = revealedItemCount >= firstItemIndex + 6) {
        OtherChatElement(
            nameText = "주변",
            chatText = "여기 자리 없어요!"
        )
    }

    AnimatedDoctorChatItem(visible = revealedItemCount >= firstItemIndex + 7) {
        OtherChatElement(
            nameText = "주변",
            chatText = "장비 부족해요!"
        )
    }

    AnimatedDoctorChatItem(visible = revealedItemCount >= firstItemIndex + 8) {
        OtherChatElement(
            nameText = "주변",
            chatText = "다 볼 수가 없어요!"
        )
    }

    AnimatedDoctorChatItem(visible = revealedItemCount >= firstItemIndex + 9) {
        MyChatElement(chatText = "...")
    }

    AnimatedDoctorChatItem(visible = revealedItemCount >= firstItemIndex + 10) {
        MyChatElement(chatText = "감당이 안 돼...")
    }

    AnimatedDoctorChatItem(visible = revealedItemCount >= firstItemIndex + 11) {
        ChatNarrationText(text = "환자 수가 계속 늘어나고 있습니다.")
    }

    AnimatedDoctorChatItem(visible = revealedItemCount >= firstItemIndex + 12) {
        ChatNarrationText(text = "모든 환자를 치료하는 것은 불가능합니다.")
    }

    AnimatedDoctorChatItem(visible = revealedItemCount >= firstItemIndex + 13) {
        ChatSceneImage(
            imageResId = R.drawable.chat_doctor_fourth,
            contentDescription = "부상자가 몰린 병원 응급실",
            height = 214.dp
        )
    }

    AnimatedDoctorChatItem(visible = revealedItemCount >= firstItemIndex + 14) {
        ChatRecord(
            bodyText = "당시 병원에는 많은 부상자들이 몰려들었고, 의료 인력과 장비는 그 수요를 감당하기 어려웠습니다."
        )
    }
}

@Composable
private fun rememberDoctorSequentialRevealCount(
    itemCount: Int,
    revealKey: Any? = Unit,
    initiallyVisibleItemCount: Int = 0,
    enabled: Boolean = true
): Int {
    val initialItemCount = initiallyVisibleItemCount.coerceIn(0, itemCount)
    var revealedItemCount by rememberSaveable(revealKey) {
        mutableIntStateOf(initiallyVisibleItemCount.coerceIn(0, itemCount))
    }

    LaunchedEffect(revealKey, itemCount, initialItemCount, enabled) {
        if (!enabled) {
            revealedItemCount = 0
            return@LaunchedEffect
        }

        revealedItemCount = revealedItemCount.coerceAtLeast(initialItemCount)

        while (revealedItemCount < itemCount) {
            delay(DOCTOR_CHAT_ELEMENT_REVEAL_DURATION_MILLIS.toLong())
            revealedItemCount += 1
        }
    }

    return if (enabled) revealedItemCount.coerceIn(0, itemCount) else 0
}

@Composable
private fun rememberDoctorShouldFollowNewContent(
    listState: LazyListState
): Boolean {
    var shouldFollowNewContent by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(listState) {
        snapshotFlow {
            DoctorAutoScrollSnapshot(
                canScrollForward = listState.canScrollForward,
                isScrollInProgress = listState.isScrollInProgress,
                lastScrolledBackward = listState.lastScrolledBackward
            )
        }
            .distinctUntilChanged()
            .collect { snapshot ->
                when {
                    !snapshot.canScrollForward -> shouldFollowNewContent = true
                    snapshot.isScrollInProgress && snapshot.lastScrolledBackward -> shouldFollowNewContent = false
                }
            }
    }

    return shouldFollowNewContent
}

@Composable
private fun AutoScrollOnDoctorReveal(
    listState: LazyListState,
    revealKey: Any?,
    enabled: Boolean = true,
    targetItemIndex: Int? = null,
    shouldFollowNewContent: Boolean
) {
    val currentShouldFollowNewContent by rememberUpdatedState(shouldFollowNewContent)
    val bottomPaddingPx = with(LocalDensity.current) {
        DOCTOR_AUTO_SCROLL_BOTTOM_PADDING.toPx()
    }

    LaunchedEffect(revealKey, enabled, targetItemIndex) {
        if (!enabled) return@LaunchedEffect
        delay(100)
        if (!currentShouldFollowNewContent) return@LaunchedEffect

        val layoutInfo = listState.layoutInfo
        val lastItemIndex = layoutInfo.totalItemsCount - 1

        if (lastItemIndex >= 0) {
            val targetIndex = (targetItemIndex ?: lastItemIndex).coerceIn(0, lastItemIndex)
            val targetItem = layoutInfo.visibleItemsInfo.firstOrNull { it.index == targetIndex }

            if (targetItem != null) {
                val overflow = targetItem.offset + targetItem.size + bottomPaddingPx -
                    layoutInfo.viewportEndOffset

                if (overflow > 0f) {
                    listState.animateScrollBy(overflow)
                }
            } else if (targetIndex > (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1)) {
                listState.animateScrollToItem(targetIndex)
            }
        }
    }
}

@Composable
private fun AnimatedDoctorChatItem(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var hasStartedReveal by remember { mutableStateOf(visible) }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = DOCTOR_CHAT_ELEMENT_REVEAL_DURATION_MILLIS),
        label = "doctor chat item alpha"
    )
    LaunchedEffect(visible) {
        if (visible) {
            hasStartedReveal = true
        }
    }

    if (hasStartedReveal) {
        Box(
            modifier = modifier.alpha(alpha)
        ) {
            content()
        }
    }
}

private enum class DoctorOpeningBranch {
    RunToPatient,
    PrepareHospital
}

private enum class DoctorTriageBranch {
    FocusPatient,
    CheckPatients,
    RequestTransfer
}

private data class DoctorAutoScrollSnapshot(
    val canScrollForward: Boolean,
    val isScrollInProgress: Boolean,
    val lastScrolledBackward: Boolean
)

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun DoctorChatScreenPreview() {
    MayWaveTheme {
        DoctorChatScreen()
    }
}
