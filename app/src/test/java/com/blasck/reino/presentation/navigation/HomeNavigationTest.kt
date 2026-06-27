package com.blasck.reino.presentation.navigation

import com.blasck.reino.presentation.enums.CharacterListFilters
import com.blasck.reino.presentation.enums.HomeScreens
import com.blasck.reino.presentation.screen.AppScreens
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeNavigationTest {
    @Test
    fun `home character list opens local character list`() {
        assertEquals(
            AppScreens.CharacterList(
                filter = CharacterListFilters.DEDICATED.value,
                screen = HomeScreens.DEDICATED.title,
            ),
            HomeScreens.DEDICATED.toAppScreen(),
        )
    }

    @Test
    fun `home import opens drive catalog`() {
        assertEquals(AppScreens.DRIVE_CATALOG, HomeScreens.IMPORT_CHARACTER.toAppScreen())
    }

    @Test
    fun `home custom source opens drive source setup`() {
        assertEquals(AppScreens.CUSTOM_DRIVE_SOURCE, HomeScreens.CUSTOM_DRIVE_SOURCE.toAppScreen())
    }

    @Test
    fun `legacy poll and master menus no longer open mock character lists`() {
        assertEquals(
            AppScreens.Error("Pool ainda sera migrado para o fluxo local/Drive."),
            HomeScreens.POLL.toAppScreen(),
        )
        assertEquals(
            AppScreens.Error("Mestre ainda sera migrado para o fluxo local/Drive."),
            HomeScreens.MASTER.toAppScreen(),
        )
    }
}
