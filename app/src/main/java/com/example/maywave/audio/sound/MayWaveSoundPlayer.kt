package com.example.maywave.audio.sound

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.annotation.RawRes
import com.example.maywave.R

class MayWaveSoundPlayer(
    context: Context
) {
    private companion object {
        const val CITIZEN_BRANCH_RADIO_START_POSITION_MILLIS = 41_000
        const val CITIZEN_BRANCH_RADIO_STOP_POSITION_MILLIS = 52_000
        const val CITIZEN_BRANCH_RADIO_CHECK_INTERVAL_MILLIS = 24L
        const val CITIZEN_DISTANCE_RECORD_START_POSITION_MILLIS = 24_000
        const val AIRBORNE_CRACKDOWN_SCENE_VOLUME = 1f
        const val AIRBORNE_CRACKDOWN_SCENE_FADE_OUT_DURATION_MILLIS = 2_000L
        const val AIRBORNE_CRACKDOWN_SCENE_FADE_OUT_STEP_MILLIS = 100L
        const val DOCTOR_CROWD_CRYING_VOLUME = 0.22f
        const val DOCTOR_CROWD_CRYING_FADE_OUT_DURATION_MILLIS = 1_800L
        const val DOCTOR_CROWD_CRYING_FADE_OUT_STEP_MILLIS = 100L
        const val DOCTOR_HEARTBEAT_VOLUME = 0.28f
        const val DOCTOR_HEARTBEAT_FADE_OUT_DURATION_MILLIS = 2_000L
        const val DOCTOR_HEARTBEAT_FADE_OUT_STEP_MILLIS = 100L
        const val RECORD_TYPING_LOOP_START_POSITION_MILLIS = 1_000
        const val RECORD_TYPING_LOOP_END_POSITION_MILLIS = 3_000
        const val RECORD_TYPING_LOOP_CHECK_INTERVAL_MILLIS = 24L
    }

    private val appContext = context.applicationContext
    private val mainHandler = Handler(Looper.getMainLooper())
    private var startSoundPlayer: MediaPlayer? = null
    private var roleVoicePlayer: MediaPlayer? = null
    private var choiceClickPlayer: MediaPlayer? = null
    private var doctorRunToPatientPlayer: MediaPlayer? = null
    private var doctorCrowdCryingPlayer: MediaPlayer? = null
    private var doctorHeartbeatPlayer: MediaPlayer? = null
    private var citizenBranchRadioPlayer: MediaPlayer? = null
    private var citizenHelpFallenScenePlayer: MediaPlayer? = null
    private var citizenDistanceRecordPlayer: MediaPlayer? = null
    private var airborneCrackdownScenePlayer: MediaPlayer? = null
    private var recordTypingPlayer: MediaPlayer? = null
    private var doctorCrowdCryingFadeOutRunnable: Runnable? = null
    private var doctorHeartbeatFadeOutRunnable: Runnable? = null
    private var airborneCrackdownSceneFadeOutRunnable: Runnable? = null
    private var citizenBranchRadioStopRunnable: Runnable? = null
    private var recordTypingLoopRunnable: Runnable? = null

    fun playStartSound() {
        startSoundPlayer = startSoundPlayer.releaseSafely()
        startSoundPlayer = createAndStartPlayer(R.raw.start_sound) { completedPlayer ->
            if (startSoundPlayer === completedPlayer) {
                startSoundPlayer = null
            }
        }
    }

    fun stopStartSound() {
        startSoundPlayer = startSoundPlayer.releaseSafely()
    }

    fun playRoleVoice(roleVoiceSound: RoleVoiceSound) {
        roleVoicePlayer = roleVoicePlayer.releaseSafely()
        roleVoicePlayer = createAndStartPlayer(roleVoiceSound.rawResId) { completedPlayer ->
            if (roleVoicePlayer === completedPlayer) {
                roleVoicePlayer = null
            }
        }
    }

    fun stopRoleVoice() {
        roleVoicePlayer = roleVoicePlayer.releaseSafely()
    }

    fun playChoiceClick() {
        choiceClickPlayer = choiceClickPlayer.releaseSafely()
        choiceClickPlayer = createAndStartPlayer(R.raw.choice_click) { completedPlayer ->
            if (choiceClickPlayer === completedPlayer) {
                choiceClickPlayer = null
            }
        }
    }

    fun playDoctorRunToPatientSound() {
        doctorRunToPatientPlayer = doctorRunToPatientPlayer.releaseSafely()
        doctorRunToPatientPlayer = createAndStartPlayer(R.raw.doctor_run_to_patient_sound) { completedPlayer ->
            if (doctorRunToPatientPlayer === completedPlayer) {
                doctorRunToPatientPlayer = null
            }
        }
    }

    fun stopDoctorRunToPatientSound() {
        doctorRunToPatientPlayer = doctorRunToPatientPlayer.releaseSafely()
    }

    fun playDoctorCrowdCryingSound() {
        stopDoctorCrowdCryingFadeOut()
        doctorCrowdCryingPlayer = doctorCrowdCryingPlayer.releaseSafely()
        doctorCrowdCryingPlayer = createAndStartPlayer(
            rawResId = R.raw.doctor_crowd_crying_sound,
            isLooping = true
        ) { completedPlayer ->
            if (doctorCrowdCryingPlayer === completedPlayer) {
                doctorCrowdCryingPlayer = null
            }
        }?.apply {
            setVolume(DOCTOR_CROWD_CRYING_VOLUME, DOCTOR_CROWD_CRYING_VOLUME)
        }
    }

    fun fadeOutDoctorCrowdCryingSound() {
        val player = doctorCrowdCryingPlayer ?: return
        stopDoctorCrowdCryingFadeOut()
        val startedAt = SystemClock.uptimeMillis()
        val fadeOutRunnable = object : Runnable {
            override fun run() {
                if (doctorCrowdCryingPlayer !== player) return

                val elapsedMillis = SystemClock.uptimeMillis() - startedAt
                val remainingRatio = 1f - (elapsedMillis.toFloat() / DOCTOR_CROWD_CRYING_FADE_OUT_DURATION_MILLIS)
                val volume = DOCTOR_CROWD_CRYING_VOLUME * remainingRatio.coerceIn(0f, 1f)
                player.setVolume(volume, volume)

                if (elapsedMillis >= DOCTOR_CROWD_CRYING_FADE_OUT_DURATION_MILLIS) {
                    stopDoctorCrowdCryingSound()
                } else {
                    mainHandler.postDelayed(this, DOCTOR_CROWD_CRYING_FADE_OUT_STEP_MILLIS)
                }
            }
        }
        doctorCrowdCryingFadeOutRunnable = fadeOutRunnable
        mainHandler.post(fadeOutRunnable)
    }

    fun stopDoctorCrowdCryingSound() {
        stopDoctorCrowdCryingFadeOut()
        doctorCrowdCryingPlayer = doctorCrowdCryingPlayer.releaseSafely()
    }

    fun playDoctorHeartbeatSound() {
        stopDoctorHeartbeatFadeOut()
        doctorHeartbeatPlayer = doctorHeartbeatPlayer.releaseSafely()
        doctorHeartbeatPlayer = createAndStartPlayer(
            rawResId = R.raw.doctor_heartbeat_sound,
            isLooping = true
        ) { completedPlayer ->
            if (doctorHeartbeatPlayer === completedPlayer) {
                doctorHeartbeatPlayer = null
            }
        }?.apply {
            setVolume(DOCTOR_HEARTBEAT_VOLUME, DOCTOR_HEARTBEAT_VOLUME)
        }
    }

    fun fadeOutDoctorHeartbeatSound() {
        val player = doctorHeartbeatPlayer ?: return
        stopDoctorHeartbeatFadeOut()
        val startedAt = SystemClock.uptimeMillis()
        val fadeOutRunnable = object : Runnable {
            override fun run() {
                if (doctorHeartbeatPlayer !== player) return

                val elapsedMillis = SystemClock.uptimeMillis() - startedAt
                val remainingRatio = 1f - (elapsedMillis.toFloat() / DOCTOR_HEARTBEAT_FADE_OUT_DURATION_MILLIS)
                val volume = DOCTOR_HEARTBEAT_VOLUME * remainingRatio.coerceIn(0f, 1f)
                player.setVolume(volume, volume)

                if (elapsedMillis >= DOCTOR_HEARTBEAT_FADE_OUT_DURATION_MILLIS) {
                    stopDoctorHeartbeatSound()
                } else {
                    mainHandler.postDelayed(this, DOCTOR_HEARTBEAT_FADE_OUT_STEP_MILLIS)
                }
            }
        }
        doctorHeartbeatFadeOutRunnable = fadeOutRunnable
        mainHandler.post(fadeOutRunnable)
    }

    fun stopDoctorHeartbeatSound() {
        stopDoctorHeartbeatFadeOut()
        doctorHeartbeatPlayer = doctorHeartbeatPlayer.releaseSafely()
    }

    fun playCitizenBranchRadio() {
        stopCitizenBranchRadio()
        citizenBranchRadioPlayer = createAndStartPlayer(
            rawResId = R.raw.citizen_branch_radio,
            startPositionMillis = CITIZEN_BRANCH_RADIO_START_POSITION_MILLIS
        ) { completedPlayer ->
            if (citizenBranchRadioPlayer === completedPlayer) {
                citizenBranchRadioPlayer = null
            }
        }
        startCitizenBranchRadioStopCheck()
    }

    fun stopCitizenBranchRadio() {
        stopCitizenBranchRadioStopCheck()
        citizenBranchRadioPlayer = citizenBranchRadioPlayer.releaseSafely()
    }

    fun playCitizenHelpFallenSceneSound() {
        citizenHelpFallenScenePlayer = citizenHelpFallenScenePlayer.releaseSafely()
        citizenHelpFallenScenePlayer = createAndStartPlayer(R.raw.citizen_help_fallen_scene_sound) { completedPlayer ->
            if (citizenHelpFallenScenePlayer === completedPlayer) {
                citizenHelpFallenScenePlayer = null
            }
        }
    }

    fun stopCitizenHelpFallenSceneSound() {
        citizenHelpFallenScenePlayer = citizenHelpFallenScenePlayer.releaseSafely()
    }

    fun playCitizenDistanceRecord() {
        citizenDistanceRecordPlayer = citizenDistanceRecordPlayer.releaseSafely()
        citizenDistanceRecordPlayer = createAndStartPlayer(
            rawResId = R.raw.citizen_distance_record_sound,
            startPositionMillis = CITIZEN_DISTANCE_RECORD_START_POSITION_MILLIS
        ) { completedPlayer ->
            if (citizenDistanceRecordPlayer === completedPlayer) {
                citizenDistanceRecordPlayer = null
            }
        }
    }

    fun stopCitizenDistanceRecord() {
        citizenDistanceRecordPlayer = citizenDistanceRecordPlayer.releaseSafely()
    }

    fun playAirborneCrackdownSceneSound() {
        stopAirborneCrackdownSceneFadeOut()
        airborneCrackdownScenePlayer = airborneCrackdownScenePlayer.releaseSafely()
        airborneCrackdownScenePlayer = createAndStartPlayer(R.raw.citizen_help_fallen_scene_sound) { completedPlayer ->
            if (airborneCrackdownScenePlayer === completedPlayer) {
                airborneCrackdownScenePlayer = null
            }
        }?.apply {
            setVolume(AIRBORNE_CRACKDOWN_SCENE_VOLUME, AIRBORNE_CRACKDOWN_SCENE_VOLUME)
        }
    }

    fun fadeOutAirborneCrackdownSceneSound() {
        val player = airborneCrackdownScenePlayer ?: return
        stopAirborneCrackdownSceneFadeOut()
        val startedAt = SystemClock.uptimeMillis()
        val fadeOutRunnable = object : Runnable {
            override fun run() {
                if (airborneCrackdownScenePlayer !== player) return

                val elapsedMillis = SystemClock.uptimeMillis() - startedAt
                val remainingRatio = 1f - (elapsedMillis.toFloat() / AIRBORNE_CRACKDOWN_SCENE_FADE_OUT_DURATION_MILLIS)
                val volume = AIRBORNE_CRACKDOWN_SCENE_VOLUME * remainingRatio.coerceIn(0f, 1f)
                player.setVolume(volume, volume)

                if (elapsedMillis >= AIRBORNE_CRACKDOWN_SCENE_FADE_OUT_DURATION_MILLIS) {
                    stopAirborneCrackdownSceneSound()
                } else {
                    mainHandler.postDelayed(this, AIRBORNE_CRACKDOWN_SCENE_FADE_OUT_STEP_MILLIS)
                }
            }
        }
        airborneCrackdownSceneFadeOutRunnable = fadeOutRunnable
        mainHandler.post(fadeOutRunnable)
    }

    fun stopAirborneCrackdownSceneSound() {
        stopAirborneCrackdownSceneFadeOut()
        airborneCrackdownScenePlayer = airborneCrackdownScenePlayer.releaseSafely()
    }

    fun playRecordTypingSound() {
        stopRecordTypingSound()
        recordTypingPlayer = createAndStartPlayer(
            rawResId = R.raw.typewriter_sound_effect,
            startPositionMillis = RECORD_TYPING_LOOP_START_POSITION_MILLIS
        ) { completedPlayer ->
            if (recordTypingPlayer === completedPlayer) {
                recordTypingPlayer = null
            }
        }
        startRecordTypingLoopCheck()
    }

    fun stopRecordTypingSound() {
        stopRecordTypingLoopCheck()
        recordTypingPlayer = recordTypingPlayer.releaseSafely()
    }

    fun release() {
        startSoundPlayer = startSoundPlayer.releaseSafely()
        roleVoicePlayer = roleVoicePlayer.releaseSafely()
        choiceClickPlayer = choiceClickPlayer.releaseSafely()
        doctorRunToPatientPlayer = doctorRunToPatientPlayer.releaseSafely()
        stopDoctorCrowdCryingFadeOut()
        doctorCrowdCryingPlayer = doctorCrowdCryingPlayer.releaseSafely()
        stopDoctorHeartbeatFadeOut()
        doctorHeartbeatPlayer = doctorHeartbeatPlayer.releaseSafely()
        stopCitizenBranchRadioStopCheck()
        citizenBranchRadioPlayer = citizenBranchRadioPlayer.releaseSafely()
        citizenHelpFallenScenePlayer = citizenHelpFallenScenePlayer.releaseSafely()
        citizenDistanceRecordPlayer = citizenDistanceRecordPlayer.releaseSafely()
        stopAirborneCrackdownSceneFadeOut()
        airborneCrackdownScenePlayer = airborneCrackdownScenePlayer.releaseSafely()
        stopRecordTypingLoopCheck()
        recordTypingPlayer = recordTypingPlayer.releaseSafely()
    }

    private fun startCitizenBranchRadioStopCheck() {
        val stopRunnable = object : Runnable {
            override fun run() {
                val player = citizenBranchRadioPlayer ?: return

                if (player.currentPosition >= CITIZEN_BRANCH_RADIO_STOP_POSITION_MILLIS) {
                    stopCitizenBranchRadio()
                    return
                }
                mainHandler.postDelayed(this, CITIZEN_BRANCH_RADIO_CHECK_INTERVAL_MILLIS)
            }
        }
        citizenBranchRadioStopRunnable = stopRunnable
        mainHandler.postDelayed(stopRunnable, CITIZEN_BRANCH_RADIO_CHECK_INTERVAL_MILLIS)
    }

    private fun stopDoctorCrowdCryingFadeOut() {
        doctorCrowdCryingFadeOutRunnable?.let(mainHandler::removeCallbacks)
        doctorCrowdCryingFadeOutRunnable = null
    }

    private fun stopDoctorHeartbeatFadeOut() {
        doctorHeartbeatFadeOutRunnable?.let(mainHandler::removeCallbacks)
        doctorHeartbeatFadeOutRunnable = null
    }

    private fun stopAirborneCrackdownSceneFadeOut() {
        airborneCrackdownSceneFadeOutRunnable?.let(mainHandler::removeCallbacks)
        airborneCrackdownSceneFadeOutRunnable = null
    }

    private fun stopCitizenBranchRadioStopCheck() {
        citizenBranchRadioStopRunnable?.let(mainHandler::removeCallbacks)
        citizenBranchRadioStopRunnable = null
    }

    private fun startRecordTypingLoopCheck() {
        val loopRunnable = object : Runnable {
            override fun run() {
                val player = recordTypingPlayer ?: return

                if (player.currentPosition >= RECORD_TYPING_LOOP_END_POSITION_MILLIS) {
                    player.seekTo(RECORD_TYPING_LOOP_START_POSITION_MILLIS)
                }
                mainHandler.postDelayed(this, RECORD_TYPING_LOOP_CHECK_INTERVAL_MILLIS)
            }
        }
        recordTypingLoopRunnable = loopRunnable
        mainHandler.postDelayed(loopRunnable, RECORD_TYPING_LOOP_CHECK_INTERVAL_MILLIS)
    }

    private fun stopRecordTypingLoopCheck() {
        recordTypingLoopRunnable?.let(mainHandler::removeCallbacks)
        recordTypingLoopRunnable = null
    }

    private fun createAndStartPlayer(
        @RawRes rawResId: Int,
        startPositionMillis: Int = 0,
        isLooping: Boolean = false,
        onCompletion: (MediaPlayer) -> Unit
    ): MediaPlayer? {
        return runCatching {
            MediaPlayer.create(appContext, rawResId)?.apply {
                this.isLooping = isLooping
                setOnCompletionListener { completedPlayer ->
                    completedPlayer.setOnCompletionListener(null)
                    completedPlayer.release()
                    onCompletion(completedPlayer)
                }
                if (startPositionMillis > 0) {
                    seekTo(startPositionMillis)
                }
                start()
            }
        }.getOrNull()
    }

    private fun MediaPlayer?.releaseSafely(): MediaPlayer? {
        this?.setOnCompletionListener(null)
        this?.release()
        return null
    }
}

enum class RoleVoiceSound(
    @RawRes val rawResId: Int
) {
    Citizen(R.raw.citizen_voice),
    Doctor(R.raw.doctor_voice),
    Reporter(R.raw.reporter_voice)
}
