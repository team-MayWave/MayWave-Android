package com.example.maywave.chat.navigation

sealed class ChatRoute(val route: String) {
    data object Intro : ChatRoute("intro")
    data object Citizen : ChatRoute("citizen")
    data object Doctor : ChatRoute("doctor")
    data object Reporter : ChatRoute("reporter")

    companion object {
        fun fromRoute(route: String): ChatRoute = when (route) {
            Citizen.route -> Citizen
            Doctor.route -> Doctor
            Reporter.route -> Reporter
            else -> Intro
        }
    }
}
