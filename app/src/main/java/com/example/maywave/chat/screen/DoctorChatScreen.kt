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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.maywave.R
import com.example.maywave.chat.component.choice.ChatChoiceElement
import com.example.maywave.chat.component.header.ChatInfoButton
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
import com.example.maywave.chat.component.record.ChatRecordDetailContent
import com.example.maywave.chat.component.record.ChatRecordDetailTransition
import com.example.maywave.chat.component.record.ChatRecordTypingAutoScrollProvider
import com.example.maywave.chat.viewmodel.ChatGameRequest
import com.example.maywave.chat.viewmodel.ChatGameUiState
import com.example.maywave.chat.viewmodel.ChatGameViewModel
import com.example.maywave.chat.viewmodel.ChatGameViewModelFactory
import com.example.maywave.ui.theme.MayWaveTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged

private const val DOCTOR_CHAT_ELEMENT_REVEAL_DURATION_MILLIS = 2_000
private const val DOCTOR_SERVER_RESULT_NEXT_REVEAL_DELAY_MILLIS = 4_000
private const val DOCTOR_INITIAL_ELEMENT_COUNT = 8
private const val DOCTOR_INTRO_SOUND_STOP_ITEM_COUNT = 3
private const val DOCTOR_RUN_TO_PATIENT_SOUND_STOP_ITEM_COUNT = 3
private const val DOCTOR_CROWD_CRYING_SOUND_START_ITEM_COUNT = 4
private const val DOCTOR_CROWD_CRYING_SOUND_FADE_OUT_ITEM_COUNT = 13
private const val DOCTOR_HEARTBEAT_SOUND_START_ITEM_COUNT = 3
private const val DOCTOR_HEARTBEAT_SOUND_FADE_OUT_ITEM_COUNT = 11
private const val DOCTOR_RUN_TO_PATIENT_ELEMENT_COUNT = 17
private const val DOCTOR_PREPARE_HOSPITAL_ELEMENT_COUNT = 17
private const val DOCTOR_FOCUS_PATIENT_ELEMENT_COUNT = 15
private const val DOCTOR_CHECK_PATIENTS_ELEMENT_COUNT = 14
private const val DOCTOR_REQUEST_TRANSFER_ELEMENT_COUNT = 21
private const val DOCTOR_FOCUS_PATIENT_RECORD_ITEM_INDEX = 13
private const val DOCTOR_CHECK_PATIENTS_RECORD_ITEM_INDEX = 12
private const val DOCTOR_PREPARE_HOSPITAL_RECORD_ITEM_INDEX = 16
private const val DOCTOR_REQUEST_TRANSFER_RECORD_ITEM_INDEX = 20
private const val DOCTOR_AUTO_SCROLL_LAYOUT_DELAY_MILLIS = 100
private const val DOCTOR_AUTO_SCROLL_ANIMATION_DURATION_MILLIS = 650
private const val DOCTOR_CHAT_ITEM_REVEAL_DELAY_MILLIS = 0
private val DOCTOR_CHAT_ELEMENT_SPACING = 35.dp
private val DOCTOR_BRANCH_CHAT_ELEMENT_SPACING = 35.dp
private val DOCTOR_AUTO_SCROLL_BOTTOM_PADDING = 16.dp
private val DoctorRunToPatientRequest = ChatGameRequest(roleId = 1, scenarioId = 3, choice = 1)
private val DoctorPrepareHospitalRequest = ChatGameRequest(roleId = 1, scenarioId = 3, choice = 2)
private val DoctorFocusPatientRequest = ChatGameRequest(roleId = 1, scenarioId = 4, choice = 1)
private val DoctorCheckPatientsRequest = ChatGameRequest(roleId = 1, scenarioId = 4, choice = 2)
private val DoctorRequestTransferRequest = ChatGameRequest(roleId = 1, scenarioId = 4, choice = 3)

@Composable
fun DoctorChatScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onInfoClick: () -> Unit = {},
    showInfoUnreadDot: Boolean = true,
    onInfoRead: () -> Unit = {},
    onInfoUnreadStateShown: (String?) -> Unit = {},
    isChatPaused: Boolean = false,
    onIntroSceneFinished: () -> Unit = {},
    onChoiceClickSound: () -> Unit = {},
    onPlayRunToPatientSound: () -> Unit = {},
    onStopRunToPatientSound: () -> Unit = {},
    onPlayCrowdCryingSound: () -> Unit = {},
    onFadeOutCrowdCryingSound: () -> Unit = {},
    onStopCrowdCryingSound: () -> Unit = {},
    onPlayHeartbeatSound: () -> Unit = {},
    onFadeOutHeartbeatSound: () -> Unit = {},
    onStopHeartbeatSound: () -> Unit = {},
    onPlayRecordTypingSound: () -> Unit = {},
    onStopRecordTypingSound: () -> Unit = {},
    onFadeOutChatBackgroundMusic: () -> Unit = {}
) {
    val listState = rememberLazyListState()
    val chatGameViewModel: ChatGameViewModel = viewModel(
        factory = remember { ChatGameViewModelFactory() }
    )
    val chatGameUiState by chatGameViewModel.uiState.collectAsState()
    var selectedOpeningBranch by rememberSaveable { mutableStateOf<DoctorOpeningBranch?>(null) }
    var selectedTriageBranch by rememberSaveable { mutableStateOf<DoctorTriageBranch?>(null) }
    var openingRevealKey by rememberSaveable { mutableIntStateOf(0) }
    var triageRevealKey by rememberSaveable { mutableIntStateOf(0) }
    var isPrepareHospitalRecordTypingFinished by rememberSaveable { mutableStateOf(false) }
    var isFocusPatientRecordTypingFinished by rememberSaveable { mutableStateOf(false) }
    var isCheckPatientsRecordTypingFinished by rememberSaveable { mutableStateOf(false) }
    var isRequestTransferRecordTypingFinished by rememberSaveable { mutableStateOf(false) }
    var hasNotifiedIntroSceneFinished by rememberSaveable { mutableStateOf(false) }
    var isRunToPatientSoundPlaying by remember { mutableStateOf(false) }
    var isCrowdCryingSoundPlaying by remember { mutableStateOf(false) }
    var hasStartedCrowdCryingFadeOut by rememberSaveable { mutableStateOf(false) }
    var isHeartbeatSoundPlaying by remember { mutableStateOf(false) }
    var hasStartedHeartbeatFadeOut by rememberSaveable { mutableStateOf(false) }
    val shouldFollowNewContent = rememberDoctorShouldFollowNewContent(listState = listState)
    val isRevealPaused = isChatPaused || !shouldFollowNewContent
    val currentOnIntroSceneFinished by rememberUpdatedState(onIntroSceneFinished)
    val currentOnPlayRunToPatientSound by rememberUpdatedState(onPlayRunToPatientSound)
    val currentOnStopRunToPatientSound by rememberUpdatedState(onStopRunToPatientSound)
    val currentOnPlayCrowdCryingSound by rememberUpdatedState(onPlayCrowdCryingSound)
    val currentOnFadeOutCrowdCryingSound by rememberUpdatedState(onFadeOutCrowdCryingSound)
    val currentOnStopCrowdCryingSound by rememberUpdatedState(onStopCrowdCryingSound)
    val currentOnPlayHeartbeatSound by rememberUpdatedState(onPlayHeartbeatSound)
    val currentOnFadeOutHeartbeatSound by rememberUpdatedState(onFadeOutHeartbeatSound)
    val currentOnStopHeartbeatSound by rememberUpdatedState(onStopHeartbeatSound)
    val currentOnPlayRecordTypingSound by rememberUpdatedState(onPlayRecordTypingSound)
    val currentOnStopRecordTypingSound by rememberUpdatedState(onStopRecordTypingSound)
    val currentOnFadeOutChatBackgroundMusic by rememberUpdatedState(onFadeOutChatBackgroundMusic)
    val runToPatientItemCount = if (chatGameUiState.hasErrorFor(DoctorRunToPatientRequest)) {
        1
    } else {
        DOCTOR_RUN_TO_PATIENT_ELEMENT_COUNT
    }
    val prepareHospitalItemCount = if (chatGameUiState.hasErrorFor(DoctorPrepareHospitalRequest)) {
        1
    } else {
        DOCTOR_PREPARE_HOSPITAL_ELEMENT_COUNT
    }
    val focusPatientItemCount = if (chatGameUiState.hasErrorFor(DoctorFocusPatientRequest)) {
        1
    } else {
        DOCTOR_FOCUS_PATIENT_ELEMENT_COUNT
    }
    val checkPatientsItemCount = if (chatGameUiState.hasErrorFor(DoctorCheckPatientsRequest)) {
        1
    } else {
        DOCTOR_CHECK_PATIENTS_ELEMENT_COUNT
    }
    val requestTransferItemCount = if (chatGameUiState.hasErrorFor(DoctorRequestTransferRequest)) {
        1
    } else {
        DOCTOR_REQUEST_TRANSFER_ELEMENT_COUNT
    }
    val revealedInitialItemCount = rememberDoctorSequentialRevealCount(
        itemCount = DOCTOR_INITIAL_ELEMENT_COUNT,
        initiallyVisibleItemCount = 1,
        isPaused = isRevealPaused
    )
    val revealedRunToPatientItemCount = rememberDoctorSequentialRevealCount(
        itemCount = runToPatientItemCount,
        revealKey = openingRevealKey,
        enabled = selectedOpeningBranch == DoctorOpeningBranch.RunToPatient &&
            chatGameUiState.isResultReadyFor(DoctorRunToPatientRequest),
        firstItemNextRevealDelayMillis = DOCTOR_SERVER_RESULT_NEXT_REVEAL_DELAY_MILLIS,
        isPaused = isRevealPaused
    )
    val revealedPrepareHospitalItemCount = rememberDoctorSequentialRevealCount(
        itemCount = prepareHospitalItemCount,
        revealKey = openingRevealKey,
        enabled = selectedOpeningBranch == DoctorOpeningBranch.PrepareHospital &&
            chatGameUiState.isResultReadyFor(DoctorPrepareHospitalRequest),
        blockedAfterItemIndex = DOCTOR_PREPARE_HOSPITAL_RECORD_ITEM_INDEX,
        canRevealAfterBlockedItem = isPrepareHospitalRecordTypingFinished,
        firstItemNextRevealDelayMillis = DOCTOR_SERVER_RESULT_NEXT_REVEAL_DELAY_MILLIS,
        isPaused = isRevealPaused
    )
    val revealedFocusPatientItemCount = rememberDoctorSequentialRevealCount(
        itemCount = focusPatientItemCount,
        revealKey = triageRevealKey,
        enabled = selectedTriageBranch == DoctorTriageBranch.FocusPatient &&
            chatGameUiState.isResultReadyFor(DoctorFocusPatientRequest),
        blockedAfterItemIndex = DOCTOR_FOCUS_PATIENT_RECORD_ITEM_INDEX,
        canRevealAfterBlockedItem = isFocusPatientRecordTypingFinished,
        firstItemNextRevealDelayMillis = DOCTOR_SERVER_RESULT_NEXT_REVEAL_DELAY_MILLIS,
        isPaused = isRevealPaused
    )
    val revealedCheckPatientsItemCount = rememberDoctorSequentialRevealCount(
        itemCount = checkPatientsItemCount,
        revealKey = triageRevealKey,
        enabled = selectedTriageBranch == DoctorTriageBranch.CheckPatients &&
            chatGameUiState.isResultReadyFor(DoctorCheckPatientsRequest),
        blockedAfterItemIndex = DOCTOR_CHECK_PATIENTS_RECORD_ITEM_INDEX,
        canRevealAfterBlockedItem = isCheckPatientsRecordTypingFinished,
        firstItemNextRevealDelayMillis = DOCTOR_SERVER_RESULT_NEXT_REVEAL_DELAY_MILLIS,
        isPaused = isRevealPaused
    )
    val revealedRequestTransferItemCount = rememberDoctorSequentialRevealCount(
        itemCount = requestTransferItemCount,
        revealKey = triageRevealKey,
        enabled = selectedTriageBranch == DoctorTriageBranch.RequestTransfer &&
            chatGameUiState.isResultReadyFor(DoctorRequestTransferRequest),
        blockedAfterItemIndex = DOCTOR_REQUEST_TRANSFER_RECORD_ITEM_INDEX,
        canRevealAfterBlockedItem = isRequestTransferRecordTypingFinished,
        firstItemNextRevealDelayMillis = DOCTOR_SERVER_RESULT_NEXT_REVEAL_DELAY_MILLIS,
        isPaused = isRevealPaused
    )
    val finalSequenceKey = when {
        selectedTriageBranch == DoctorTriageBranch.FocusPatient &&
            revealedFocusPatientItemCount >= DOCTOR_FOCUS_PATIENT_ELEMENT_COUNT &&
            isFocusPatientRecordTypingFinished -> "focus_patient"
        selectedTriageBranch == DoctorTriageBranch.CheckPatients &&
            revealedCheckPatientsItemCount >= DOCTOR_CHECK_PATIENTS_ELEMENT_COUNT &&
            isCheckPatientsRecordTypingFinished -> "check_patients"
        selectedTriageBranch == DoctorTriageBranch.RequestTransfer &&
            revealedRequestTransferItemCount >= DOCTOR_REQUEST_TRANSFER_ELEMENT_COUNT &&
            isRequestTransferRecordTypingFinished -> "request_transfer"
        selectedOpeningBranch == DoctorOpeningBranch.PrepareHospital &&
            revealedPrepareHospitalItemCount >= DOCTOR_PREPARE_HOSPITAL_ELEMENT_COUNT &&
            isPrepareHospitalRecordTypingFinished -> "prepare_hospital"
        else -> null
    }
    val infoUnreadStateKey = doctorInfoUnreadStateKey(
        selectedOpeningBranch = selectedOpeningBranch,
        selectedTriageBranch = selectedTriageBranch,
        finalSequenceKey = finalSequenceKey
    )
    val activeRecordDetailContent = doctorActiveRecordDetailContent(
        selectedTriageBranch = selectedTriageBranch,
        revealedFocusPatientItemCount = revealedFocusPatientItemCount,
        revealedCheckPatientsItemCount = revealedCheckPatientsItemCount
    )

    LaunchedEffect(infoUnreadStateKey) {
        onInfoUnreadStateShown(infoUnreadStateKey)
    }

    LaunchedEffect(revealedInitialItemCount, isRevealPaused, hasNotifiedIntroSceneFinished) {
        if (
            !hasNotifiedIntroSceneFinished &&
            !isRevealPaused &&
            revealedInitialItemCount >= DOCTOR_INTRO_SOUND_STOP_ITEM_COUNT
        ) {
            hasNotifiedIntroSceneFinished = true
            currentOnIntroSceneFinished()
        }
    }

    LaunchedEffect(selectedOpeningBranch, revealedRunToPatientItemCount, isRunToPatientSoundPlaying) {
        val shouldPlayRunToPatientSound = selectedOpeningBranch == DoctorOpeningBranch.RunToPatient &&
            revealedRunToPatientItemCount < DOCTOR_RUN_TO_PATIENT_SOUND_STOP_ITEM_COUNT

        if (shouldPlayRunToPatientSound && !isRunToPatientSoundPlaying) {
            currentOnPlayRunToPatientSound()
            isRunToPatientSoundPlaying = true
        } else if (!shouldPlayRunToPatientSound && isRunToPatientSoundPlaying) {
            currentOnStopRunToPatientSound()
            isRunToPatientSoundPlaying = false
        }
    }

    LaunchedEffect(
        selectedOpeningBranch,
        revealedRunToPatientItemCount,
        isCrowdCryingSoundPlaying,
        hasStartedCrowdCryingFadeOut
    ) {
        val shouldPlayCrowdCryingSound = selectedOpeningBranch == DoctorOpeningBranch.RunToPatient &&
            revealedRunToPatientItemCount in DOCTOR_CROWD_CRYING_SOUND_START_ITEM_COUNT until
            DOCTOR_CROWD_CRYING_SOUND_FADE_OUT_ITEM_COUNT

        when {
            shouldPlayCrowdCryingSound &&
                !isCrowdCryingSoundPlaying &&
                !hasStartedCrowdCryingFadeOut -> {
                currentOnPlayCrowdCryingSound()
                isCrowdCryingSoundPlaying = true
            }

            selectedOpeningBranch == DoctorOpeningBranch.RunToPatient &&
                revealedRunToPatientItemCount >= DOCTOR_CROWD_CRYING_SOUND_FADE_OUT_ITEM_COUNT &&
                isCrowdCryingSoundPlaying &&
                !hasStartedCrowdCryingFadeOut -> {
                currentOnFadeOutCrowdCryingSound()
                isCrowdCryingSoundPlaying = false
                hasStartedCrowdCryingFadeOut = true
            }

            selectedOpeningBranch != DoctorOpeningBranch.RunToPatient && isCrowdCryingSoundPlaying -> {
                currentOnStopCrowdCryingSound()
                isCrowdCryingSoundPlaying = false
                hasStartedCrowdCryingFadeOut = false
            }
        }
    }

    LaunchedEffect(
        selectedTriageBranch,
        revealedFocusPatientItemCount,
        isHeartbeatSoundPlaying,
        hasStartedHeartbeatFadeOut
    ) {
        val shouldPlayHeartbeatSound = selectedTriageBranch == DoctorTriageBranch.FocusPatient &&
            revealedFocusPatientItemCount in DOCTOR_HEARTBEAT_SOUND_START_ITEM_COUNT until
            DOCTOR_HEARTBEAT_SOUND_FADE_OUT_ITEM_COUNT

        when {
            shouldPlayHeartbeatSound &&
                !isHeartbeatSoundPlaying &&
                !hasStartedHeartbeatFadeOut -> {
                currentOnPlayHeartbeatSound()
                isHeartbeatSoundPlaying = true
            }

            selectedTriageBranch == DoctorTriageBranch.FocusPatient &&
                revealedFocusPatientItemCount >= DOCTOR_HEARTBEAT_SOUND_FADE_OUT_ITEM_COUNT &&
                isHeartbeatSoundPlaying &&
                !hasStartedHeartbeatFadeOut -> {
                currentOnFadeOutHeartbeatSound()
                isHeartbeatSoundPlaying = false
                hasStartedHeartbeatFadeOut = true
            }

            selectedTriageBranch != DoctorTriageBranch.FocusPatient && isHeartbeatSoundPlaying -> {
                currentOnStopHeartbeatSound()
                isHeartbeatSoundPlaying = false
                hasStartedHeartbeatFadeOut = false
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            currentOnStopRunToPatientSound()
            currentOnStopCrowdCryingSound()
            currentOnStopHeartbeatSound()
            currentOnStopRecordTypingSound()
        }
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
        ChatRecordDetailTransition(
            content = activeRecordDetailContent,
            onRecordTypingFinished = {
                currentOnFadeOutChatBackgroundMusic()
                when (selectedTriageBranch) {
                    DoctorTriageBranch.FocusPatient -> {
                        isFocusPatientRecordTypingFinished = true
                    }

                    DoctorTriageBranch.CheckPatients -> {
                        isCheckPatientsRecordTypingFinished = true
                    }

                    DoctorTriageBranch.RequestTransfer,
                    null -> Unit
                }
            },
            modifier = Modifier.fillMaxSize(),
            isPaused = isRevealPaused,
            onRecordTypingAnimationStarted = currentOnPlayRecordTypingSound,
            onRecordTypingAnimationFinished = currentOnStopRecordTypingSound
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                DoctorChatHeader(
                    onBackClick = onBackClick,
                    onInfoClick = onInfoClick,
                    showInfoUnreadDot = showInfoUnreadDot && infoUnreadStateKey != null,
                    onInfoRead = onInfoRead
                )

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
                                    openingRevealKey += 1
                                    chatGameViewModel.submitChoice(DoctorRunToPatientRequest)
                                },
                                onSecondChoiceClick = {
                                    selectedOpeningBranch = DoctorOpeningBranch.PrepareHospital
                                    openingRevealKey += 1
                                    chatGameViewModel.submitChoice(DoctorPrepareHospitalRequest)
                                },
                                selectedChoiceIndex = when (selectedOpeningBranch) {
                                    DoctorOpeningBranch.RunToPatient -> 0
                                    DoctorOpeningBranch.PrepareHospital -> 1
                                    null -> null
                                },
                                onChoiceClickSound = onChoiceClickSound
                            )
                        }
                    }

                    when (selectedOpeningBranch) {
                        DoctorOpeningBranch.RunToPatient -> {
                            item {
                                ChatRecordTypingAutoScrollProvider(
                                    enabled = shouldFollowNewContent
                                ) {
                                    RunToPatientContent(
                                        selectedTriageBranch = selectedTriageBranch,
                                        chatGameUiState = chatGameUiState,
                                        revealedItemCount = revealedRunToPatientItemCount,
                                        revealedFocusPatientItemCount = revealedFocusPatientItemCount,
                                        revealedCheckPatientsItemCount = revealedCheckPatientsItemCount,
                                        revealedRequestTransferItemCount = revealedRequestTransferItemCount,
                                        onRequestTransferRecordTypingFinished = {
                                            isRequestTransferRecordTypingFinished = true
                                        },
                                        onFocusPatientClick = {
                                            selectedTriageBranch = DoctorTriageBranch.FocusPatient
                                            triageRevealKey += 1
                                            chatGameViewModel.submitChoice(DoctorFocusPatientRequest)
                                        },
                                        onCheckPatientsClick = {
                                            selectedTriageBranch = DoctorTriageBranch.CheckPatients
                                            triageRevealKey += 1
                                            chatGameViewModel.submitChoice(DoctorCheckPatientsRequest)
                                        },
                                        onRequestTransferClick = {
                                            selectedTriageBranch = DoctorTriageBranch.RequestTransfer
                                            triageRevealKey += 1
                                            chatGameViewModel.submitChoice(DoctorRequestTransferRequest)
                                        },
                                        onChoiceClickSound = onChoiceClickSound
                                    )
                                }
                            }
                        }

                        DoctorOpeningBranch.PrepareHospital -> {
                            item {
                                ChatRecordTypingAutoScrollProvider(
                                    enabled = shouldFollowNewContent
                                ) {
                                    PrepareHospitalContent(
                                        resultText = chatGameUiState.resultTextFor(DoctorPrepareHospitalRequest),
                                        revealedItemCount = revealedPrepareHospitalItemCount,
                                        onRecordTypingFinished = {
                                            isPrepareHospitalRecordTypingFinished = true
                                        }
                                    )
                                }
                            }
                        }

                        null -> Unit
                    }
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
                        text = "그날의 시간은 끝났지만,\n그날의 이야기는\n아직 끝나지 않았습니다.\n우리는,\n그날을 기억합니다.",
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
    onBackClick: () -> Unit,
    onInfoClick: () -> Unit,
    showInfoUnreadDot: Boolean,
    onInfoRead: () -> Unit
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

        ChatInfoButton(
            onClick = onInfoClick,
            showUnreadDot = showInfoUnreadDot,
            onRead = onInfoRead,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 30.dp, top = 18.dp)
        )
    }
}

@Composable
private fun RunToPatientContent(
    selectedTriageBranch: DoctorTriageBranch?,
    chatGameUiState: ChatGameUiState,
    revealedItemCount: Int,
    revealedFocusPatientItemCount: Int,
    revealedCheckPatientsItemCount: Int,
    revealedRequestTransferItemCount: Int,
    onRequestTransferRecordTypingFinished: () -> Unit,
    onFocusPatientClick: () -> Unit,
    onCheckPatientsClick: () -> Unit,
    onRequestTransferClick: () -> Unit,
    onChoiceClickSound: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(DOCTOR_BRANCH_CHAT_ELEMENT_SPACING),
        modifier = modifier.fillMaxWidth()
    ) {
        AnimatedDoctorChatItem(
            visible = revealedItemCount >= 1,
            animateInitiallyVisible = true
        ) {
            ChatNarrationText(text = chatGameUiState.resultTextFor(DoctorRunToPatientRequest))
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
                },
                onChoiceClickSound = onChoiceClickSound
            )
        }

        when (selectedTriageBranch) {
            DoctorTriageBranch.FocusPatient -> {
                FocusPatientContent(
                    resultText = chatGameUiState.resultTextFor(DoctorFocusPatientRequest),
                    revealedItemCount = revealedFocusPatientItemCount
                )
            }

            DoctorTriageBranch.CheckPatients -> {
                CheckPatientsContent(
                    resultText = chatGameUiState.resultTextFor(DoctorCheckPatientsRequest),
                    revealedItemCount = revealedCheckPatientsItemCount
                )
            }

            DoctorTriageBranch.RequestTransfer -> {
                RequestTransferContent(
                    resultText = chatGameUiState.resultTextFor(DoctorRequestTransferRequest),
                    revealedItemCount = revealedRequestTransferItemCount,
                    onRecordTypingFinished = onRequestTransferRecordTypingFinished
                )
            }

            null -> Unit
        }
    }
}

@Composable
private fun FocusPatientContent(
    resultText: String,
    revealedItemCount: Int,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(DOCTOR_BRANCH_CHAT_ELEMENT_SPACING),
        modifier = modifier.fillMaxWidth()
    ) {
        AnimatedDoctorChatItem(
            visible = revealedItemCount >= 1,
            animateInitiallyVisible = true
        ) {
            ChatNarrationText(text = resultText)
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

    }
}

@Composable
private fun CheckPatientsContent(
    resultText: String,
    revealedItemCount: Int,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(DOCTOR_BRANCH_CHAT_ELEMENT_SPACING),
        modifier = modifier.fillMaxWidth()
    ) {
        AnimatedDoctorChatItem(
            visible = revealedItemCount >= 1,
            animateInitiallyVisible = true
        ) {
            ChatNarrationText(text = resultText)
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

    }
}

@Composable
private fun RequestTransferContent(
    resultText: String,
    revealedItemCount: Int,
    onRecordTypingFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(DOCTOR_BRANCH_CHAT_ELEMENT_SPACING),
        modifier = modifier.fillMaxWidth()
    ) {
        AnimatedDoctorChatItem(
            visible = revealedItemCount >= 1,
            animateInitiallyVisible = true
        ) {
            ChatNarrationText(text = resultText)
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

        HospitalOverloadItems(
            revealedItemCount = revealedItemCount,
            firstItemIndex = 6,
            onRecordTypingFinished = onRecordTypingFinished
        )
    }
}

@Composable
private fun PrepareHospitalContent(
    resultText: String,
    revealedItemCount: Int,
    onRecordTypingFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(DOCTOR_BRANCH_CHAT_ELEMENT_SPACING),
        modifier = modifier.fillMaxWidth()
    ) {
        AnimatedDoctorChatItem(
            visible = revealedItemCount >= 1,
            animateInitiallyVisible = true
        ) {
            ChatNarrationText(text = resultText)
        }

        HospitalOverloadItems(
            revealedItemCount = revealedItemCount,
            firstItemIndex = 2,
            onRecordTypingFinished = onRecordTypingFinished
        )
    }
}

@Composable
private fun HospitalOverloadItems(
    revealedItemCount: Int,
    firstItemIndex: Int,
    onRecordTypingFinished: () -> Unit
) {
    val isCompletionTextVisible = revealedItemCount >= firstItemIndex + 14
    val currentOnRecordTypingFinished by rememberUpdatedState(onRecordTypingFinished)

    LaunchedEffect(isCompletionTextVisible) {
        if (isCompletionTextVisible) {
            currentOnRecordTypingFinished()
        }
    }

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

    AnimatedDoctorChatItem(visible = isCompletionTextVisible) {
        ChatNarrationText(
            text = "당시 병원에는 많은 부상자들이 몰려들었고, 의료 인력과 장비는 그 수요를 감당하기 어려웠습니다."
        )
    }
}

private fun doctorActiveRecordDetailContent(
    selectedTriageBranch: DoctorTriageBranch?,
    revealedFocusPatientItemCount: Int,
    revealedCheckPatientsItemCount: Int
): ChatRecordDetailContent? {
    return when {
        selectedTriageBranch == DoctorTriageBranch.FocusPatient &&
            revealedFocusPatientItemCount >= DOCTOR_FOCUS_PATIENT_RECORD_ITEM_INDEX -> {
            ChatRecordDetailContent(
                imageResId = R.drawable.doctor_hospital_record,
                imageContentDescription = "병원에서 환자를 돌보는 의료진",
                imageHeight = 214.dp,
                bodyText = "계엄군의 강경 진압으로 광주 시내 곳곳에서 부상자가 발생했습니다.\n당시 전남대병원과 광주기독병원 의료진들은 부족한 의료 물품 속에서도\n시민들을 치료해야 했으며,\n의대생과 간호사들 또한 구조 활동에 참여했습니다.\n병원으로 이송되지 못한 부상자들은 거리에서 응급 처치를 받기도 했습니다."
            )
        }

        selectedTriageBranch == DoctorTriageBranch.CheckPatients &&
            revealedCheckPatientsItemCount >= DOCTOR_CHECK_PATIENTS_RECORD_ITEM_INDEX -> {
            ChatRecordDetailContent(
                imageResId = R.drawable.doctor_crowd_record,
                imageContentDescription = "광주 시내에 모인 시민들과 검은 연기",
                imageHeight = 439.dp,
                imageContentScale = ContentScale.Fit,
                bodyText = "광주에서는\n많은 부상자들이 발생했고,\n의료 현장은\n그 상황을 감당하기 어려웠습니다."
            )
        }

        else -> null
    }
}

@Composable
private fun rememberDoctorSequentialRevealCount(
    itemCount: Int,
    revealKey: Any? = Unit,
    initiallyVisibleItemCount: Int = 0,
    enabled: Boolean = true,
    blockedAfterItemIndex: Int? = null,
    canRevealAfterBlockedItem: Boolean = true,
    firstItemNextRevealDelayMillis: Int = DOCTOR_CHAT_ELEMENT_REVEAL_DURATION_MILLIS,
    isPaused: Boolean = false
): Int {
    val initialItemCount = initiallyVisibleItemCount.coerceIn(0, itemCount)
    val blockedItemIndex = blockedAfterItemIndex?.coerceIn(0, itemCount)
    var revealedItemCount by rememberSaveable(revealKey) {
        mutableIntStateOf(initiallyVisibleItemCount.coerceIn(0, itemCount))
    }

    LaunchedEffect(
        revealKey,
        itemCount,
        initialItemCount,
        enabled,
        blockedItemIndex,
        canRevealAfterBlockedItem,
        firstItemNextRevealDelayMillis,
        isPaused
    ) {
        if (!enabled) {
            revealedItemCount = 0
            return@LaunchedEffect
        }

        if (isPaused) return@LaunchedEffect

        revealedItemCount = revealedItemCount.coerceAtLeast(initialItemCount)

        while (revealedItemCount < itemCount) {
            if (
                blockedItemIndex != null &&
                revealedItemCount >= blockedItemIndex &&
                !canRevealAfterBlockedItem
            ) {
                return@LaunchedEffect
            }

            val revealDelayMillis = if (revealedItemCount == 1) {
                firstItemNextRevealDelayMillis
            } else {
                DOCTOR_CHAT_ELEMENT_REVEAL_DURATION_MILLIS
            }
            delay(revealDelayMillis.toLong())
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
        delay(DOCTOR_AUTO_SCROLL_LAYOUT_DELAY_MILLIS.toLong())
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
                    listState.animateScrollBy(
                        value = overflow,
                        animationSpec = tween(
                            durationMillis = DOCTOR_AUTO_SCROLL_ANIMATION_DURATION_MILLIS
                        )
                    )
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
    animateInitiallyVisible: Boolean = false,
    revealDelayMillis: Int = DOCTOR_CHAT_ITEM_REVEAL_DELAY_MILLIS,
    content: @Composable () -> Unit
) {
    var hasStartedReveal by remember {
        mutableStateOf(visible && !animateInitiallyVisible)
    }
    val alpha by animateFloatAsState(
        targetValue = if (hasStartedReveal) 1f else 0f,
        animationSpec = tween(durationMillis = DOCTOR_CHAT_ELEMENT_REVEAL_DURATION_MILLIS),
        label = "doctor chat item alpha"
    )
    LaunchedEffect(visible) {
        if (visible) {
            delay(revealDelayMillis.toLong())
            hasStartedReveal = true
        }
    }

    if (visible || hasStartedReveal) {
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

private fun doctorInfoUnreadStateKey(
    selectedOpeningBranch: DoctorOpeningBranch?,
    selectedTriageBranch: DoctorTriageBranch?,
    finalSequenceKey: String?
): String? {
    if (finalSequenceKey != null) return null

    return when {
        selectedTriageBranch == DoctorTriageBranch.FocusPatient -> "doctor_focus_patient_scene"
        selectedTriageBranch == DoctorTriageBranch.RequestTransfer -> "doctor_request_transfer_scene"
        selectedOpeningBranch == DoctorOpeningBranch.PrepareHospital -> "doctor_prepare_hospital_scene"
        else -> null
    }
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
