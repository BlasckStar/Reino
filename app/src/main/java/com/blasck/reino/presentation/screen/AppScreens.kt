package com.blasck.reino.presentation.screen

import kotlinx.serialization.Serializable


sealed class AppScreens {
    @Serializable object HOME: AppScreens()
    @Serializable object WIKI: AppScreens()
    @Serializable data class Error(val error: String, val code: String = "9999"): AppScreens()
    @Serializable data class CharacterView(val id: String, val name: String): AppScreens()
    @Serializable data class CharacterList(val filter: String, val screen: String): AppScreens()

}