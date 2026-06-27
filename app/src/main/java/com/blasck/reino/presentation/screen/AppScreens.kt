package com.blasck.reino.presentation.screen

import kotlinx.serialization.Serializable


sealed class AppScreens {
    @Serializable object HOME: AppScreens()
    @Serializable object CHARACTER_IMPORT: AppScreens()
    @Serializable object DRIVE_CATALOG: AppScreens()
    @Serializable object CUSTOM_DRIVE_SOURCE: AppScreens()
    @Serializable data class Error(val error: String, val code: String = "9999"): AppScreens()
    @Serializable data class LocalCharacterView(val id: Long, val name: String): AppScreens()
    @Serializable data class CharacterSession(val id: Long, val name: String): AppScreens()
    @Serializable data class CharacterSearch(val id: Long, val name: String): AppScreens()
    @Serializable data class CharacterUpdate(val id: Long, val name: String): AppScreens()
    @Serializable data class CharacterList(val filter: String, val screen: String): AppScreens()

}
