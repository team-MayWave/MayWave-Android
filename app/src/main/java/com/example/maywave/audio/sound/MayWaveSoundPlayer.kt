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
        const val DEFAULT_SOUND_VOLUME = 1f
        const val BASS_IMPACT_VOLUME = 5f
        const val INTRO_BACKGROUND_MUSIC_VOLUME = 0.7f
        const val CHAT_BACKGROUND_MUSIC_VOLUME = 0.6f
        const val SOUND_FADE_OUT_DURATION_MILLIS = 1_800L
        const val START_SOUND_FADE_OUT_DURATION_MILLIS = 2_800L
        const val SOUND_FADE_OUT_STEP_MILLIS = 100L
        const val CITIZEN_BRANCH_RADIO_START_POSITION_MILLIS = 41_000
        const val CITIZEN_BRANCH_RADIO_STOP_POSITION_MILLIS = 52_000
        const val CITIZEN_BRANCH_RADIO_CHECK_INTERVAL_MILLIS = 24L
        const val CITIZEN_DISTANCE_RECORD_START_POSITION_MILLIS = 24_000
        const val CITIZEN_HELP_FALLEN_SCENE_VOLUME = 0.8f
        const val AIRBORNE_CRACKDOWN_SCENE_VOLUME = 0.8f
        const val DOCTOR_CROWD_CRYING_VOLUME = 0.22f
        const val DOCTOR_HEARTBEAT_VOLUME = 0.28f
        const val RECORD_TYPING_LOOP_START_POSITION_MILLIS = 1_000
        const val RECORD_TYPING_LOOP_END_POSITION_MILLIS = 3_000
        const val RECORD_TYPING_LOOP_CHECK_INTERVAL_MILLIS = 24L
    }

    private val appContext = context.applicationContext
    private val mainHandler = Handler(Looper.getMainLooper())
    private var bassImpactPlayer: MediaPlayer? = null
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
    private var introBackgroundMusicPlayer: MediaPlayer? = null
    private var chatBackgroundMusicPlayer: MediaPlayer? = null
    private var doctorCrowdCryingFadeOutRunnable: Runnable? = null
    private var doctorHeartbeatFadeOutRunnable: Runnable? = null
    private var airborneCrackdownSceneFadeOutRunnable: Runnable? = null
    private var citizenBranchRadioStopRunnable: Runnable? = null
    private var recordTypingLoopRunnable: Runnable? = null

    fun playBassImpactSound() {
        bassImpactPlayer = bassImpactPlayer.releaseSafely()
        bassImpactPlayer = createAndStartPlayer(
            rawResId = R.raw.bass_impact_sound_effect,
            volume = BASS_IMPACT_VOLUME
        ) { completedPlayer ->
            if (bassImpactPlayer === completedPlayer) {
                bassImpactPlayer = null
            }
        }
    }

    fun stopBassImpactSound() {
        bassImpactPlayer = bassImpactPlayer.fadeOutAndRelease(BASS_IMPACT_VOLUME)
    }

    fun playStartSound() {
        startSoundPlayer = startSoundPlayer.releaseSafely()
        startSoundPlayer = createAndStartPlayer(R.raw.start_sound) { completedPlayer ->
            if (startSoundPlayer === completedPlayer) {
                startSoundPlayer = null
            }
        }
    }

    fun stopStartSound() {
        startSoundPlayer = startSoundPlayer.fadeOutAndRelease(
            durationMillis = START_SOUND_FADE_OUT_DURATION_MILLIS
        )
    }

    fun playRoleVoice(roleVoiceSound: RoleVoiceSound) {
        roleVoicePlayer = roleVoicePlayer.fadeOutAndRelease()
        roleVoicePlayer = createAndStartPlayer(roleVoiceSound.rawResId) { completedPlayer ->
            if (roleVoicePlayer === completedPlayer) {
                roleVoicePlayer = null
            }
        }
    }

    fun stopRoleVoice() {
        roleVoicePlayer = roleVoicePlayer.fadeOutAndRelease()
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
        doctorRunToPatientPlayer = doctorRunToPatientPlayer.fadeOutAndRelease()
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
                val remainingRatio = 1f - (elapsedMillis.toFloat() / SOUND_FADE_OUT_DURATION_MILLIS)
                val volume = DOCTOR_CROWD_CRYING_VOLUME * remainingRatio.coerceIn(0f, 1f)
                player.setVolume(volume, volume)

                if (elapsedMillis >= SOUND_FADE_OUT_DURATION_MILLIS) {
                    stopDoctorCrowdCryingFadeOut()
                    if (doctorCrowdCryingPlayer === player) {
                        doctorCrowdCryingPlayer = player.releaseSafely()
                    }
                } else {
                    mainHandler.postDelayed(this, SOUND_FADE_OUT_STEP_MILLIS)
                }
            }
        }
        doctorCrowdCryingFadeOutRunnable = fadeOutRunnable
        mainHandler.post(fadeOutRunnable)
    }

    fun stopDoctorCrowdCryingSound() {
        stopDoctorCrowdCryingFadeOut()
        doctorCrowdCryingPlayer = doctorCrowdCryingPlayer.fadeOutAndRelease(DOCTOR_CROWD_CRYING_VOLUME)
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
                val remainingRatio = 1f - (elapsedMillis.toFloat() / SOUND_FADE_OUT_DURATION_MILLIS)
                val volume = DOCTOR_HEARTBEAT_VOLUME * remainingRatio.coerceIn(0f, 1f)
                player.setVolume(volume, volume)

                if (elapsedMillis >= SOUND_FADE_OUT_DURATION_MILLIS) {
                    stopDoctorHeartbeatFadeOut()
                    if (doctorHeartbeatPlayer === player) {
                        doctorHeartbeatPlayer = player.releaseSafely()
                    }
                } else {
                    mainHandler.postDelayed(this, SOUND_FADE_OUT_STEP_MILLIS)
                }
            }
        }
        doctorHeartbeatFadeOutRunnable = fadeOutRunnable
        mainHandler.post(fadeOutRunnable)
    }

    fun stopDoctorHeartbeatSound() {
        stopDoctorHeartbeatFadeOut()
        doctorHeartbeatPlayer = doctorHeartbeatPlayer.fadeOutAndRelease(DOCTOR_HEARTBEAT_VOLUME)
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
        citizenBranchRadioPlayer = citizenBranchRadioPlayer.fadeOutAndRelease()
    }

    fun playCitizenHelpFallenSceneSound() {
        citizenHelpFallenScenePlayer = citizenHelpFallenScenePlayer.releaseSafely()
        citizenHelpFallenScenePlayer = createAndStartPlayer(R.raw.citizen_help_fallen_scene_sound) { completedPlayer ->
            if (citizenHelpFallenScenePlayer === completedPlayer) {
                citizenHelpFallenScenePlayer = null
            }
        }?.apply {
            setVolume(CITIZEN_HELP_FALLEN_SCENE_VOLUME, CITIZEN_HELP_FALLEN_SCENE_VOLUME)
        }
    }

    fun stopCitizenHelpFallenSceneSound() {
        citizenHelpFallenScenePlayer = citizenHelpFallenScenePlayer.fadeOutAndRelease(
            CITIZEN_HELP_FALLEN_SCENE_VOLUME
        )
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
        citizenDistanceRecordPlayer = citizenDistanceRecordPlayer.fadeOutAndRelease()
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
                val remainingRatio = 1f - (elapsedMillis.toFloat() / SOUND_FADE_OUT_DURATION_MILLIS)
                val volume = AIRBORNE_CRACKDOWN_SCENE_VOLUME * remainingRatio.coerceIn(0f, 1f)
                player.setVolume(volume, volume)

                if (elapsedMillis >= SOUND_FADE_OUT_DURATION_MILLIS) {
                    stopAirborneCrackdownSceneFadeOut()
                    if (airborneCrackdownScenePlayer === player) {
                        airborneCrackdownScenePlayer = player.releaseSafely()
                    }
                } else {
                    mainHandler.postDelayed(this, SOUND_FADE_OUT_STEP_MILLIS)
                }
            }
        }
        airborneCrackdownSceneFadeOutRunnable = fadeOutRunnable
        mainHandler.post(fadeOutRunnable)
    }

    fun stopAirborneCrackdownSceneSound() {
        stopAirborneCrackdownSceneFadeOut()
        airborneCrackdownScenePlayer = airborneCrackdownScenePlayer.fadeOutAndRelease(
            AIRBORNE_CRACKDOWN_SCENE_VOLUME
        )
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
        recordTypingPlayer = recordTypingPlayer.fadeOutAndRelease()
    }

    fun playIntroBackgroundMusic() {
        chatBackgroundMusicPlayer = chatBackgroundMusicPlayer.fadeOutAndRelease(CHAT_BACKGROUND_MUSIC_VOLUME)
        if (introBackgroundMusicPlayer != null) return

        introBackgroundMusicPlayer = createAndStartPlayer(
            rawResId = R.raw.bgm,
            isLooping = true,
            volume = INTRO_BACKGROUND_MUSIC_VOLUME
        ) { completedPlayer ->
            if (introBackgroundMusicPlayer === completedPlayer) {
                introBackgroundMusicPlayer = null
            }
        }
    }

    fun fadeOutIntroBackgroundMusic() {
        introBackgroundMusicPlayer = introBackgroundMusicPlayer.fadeOutAndRelease(INTRO_BACKGROUND_MUSIC_VOLUME)
    }

    fun playChatBackgroundMusic() {
        introBackgroundMusicPlayer = introBackgroundMusicPlayer.fadeOutAndRelease(INTRO_BACKGROUND_MUSIC_VOLUME)
        if (chatBackgroundMusicPlayer != null) return

        chatBackgroundMusicPlayer = createAndStartPlayer(
            rawResId = R.raw.bgm2,
            isLooping = true,
            volume = CHAT_BACKGROUND_MUSIC_VOLUME
        ) { completedPlayer ->
            if (chatBackgroundMusicPlayer === completedPlayer) {
                chatBackgroundMusicPlayer = null
            }
        }
    }

    fun fadeOutChatBackgroundMusic() {
        chatBackgroundMusicPlayer = chatBackgroundMusicPlayer.fadeOutAndRelease(CHAT_BACKGROUND_MUSIC_VOLUME)
    }

    fun release() {
        bassImpactPlayer = bassImpactPlayer.releaseSafely()
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
        introBackgroundMusicPlayer = introBackgroundMusicPlayer.releaseSafely()
        chatBackgroundMusicPlayer = chatBackgroundMusicPlayer.releaseSafely()
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
        volume: Float = DEFAULT_SOUND_VOLUME,
        onCompletion: (MediaPlayer) -> Unit
    ): MediaPlayer? {
        return runCatching {
            MediaPlayer.create(appContext, rawResId)?.apply {
                this.isLooping = isLooping
                setVolume(volume, volume)
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

    private fun MediaPlayer?.fadeOutAndRelease(
        startVolume: Float = DEFAULT_SOUND_VOLUME,
        durationMillis: Long = SOUND_FADE_OUT_DURATION_MILLIS
    ): MediaPlayer? {
        val player = this ?: return null
        player.setOnCompletionListener(null)
        val startedAt = SystemClock.uptimeMillis()
        val fadeOutRunnable = object : Runnable {
            override fun run() {
                val elapsedMillis = SystemClock.uptimeMillis() - startedAt
                val remainingRatio = 1f - (elapsedMillis.toFloat() / durationMillis)
                val volume = startVolume * remainingRatio.coerceIn(0f, 1f)
                runCatching { player.setVolume(volume, volume) }

                if (elapsedMillis >= durationMillis) {
                    player.releaseSafely()
                } else {
                    mainHandler.postDelayed(this, SOUND_FADE_OUT_STEP_MILLIS)
                }
            }
        }
        mainHandler.post(fadeOutRunnable)
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
