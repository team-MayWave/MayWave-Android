package com.example.maywave

import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.maywave.screen.Intro
import com.example.maywave.ui.theme.MayWaveTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val splashStartedAt = SystemClock.uptimeMillis()
        installSplashScreen().setKeepOnScreenCondition {
            SystemClock.uptimeMillis() - splashStartedAt < SPLASH_DURATION_MILLIS
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MayWaveTheme {
                Intro()
            }
        }
    }

    private companion object {
        const val SPLASH_DURATION_MILLIS = 1_500L
    }
}
