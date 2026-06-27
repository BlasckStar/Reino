package com.blasck.reino.domain.model

import com.blasck.reino.fixtures.SyrioExpectedData
import org.junit.Assert.assertEquals
import org.junit.Test

class CharacterTest {
    @Test
    fun `Syrio reference data matches the current character sheet`() {
        val syrio = SyrioExpectedData.character

        assertEquals("Syrio Augusto", syrio.identity.name)
        assertEquals(17, syrio.attributes.strength)
        assertEquals(21, syrio.attributes.dexterity)
        assertEquals(13, syrio.attributes.intelligence)
        assertEquals(16, syrio.attributes.health)
        assertEquals(19, syrio.attributes.hitPoints)
        assertEquals(17, syrio.attributes.fatiguePoints)
        assertEquals(17, syrio.attributes.manaPoints)
    }

    @Test
    fun `sheet format has a stable identifier`() {
        assertEquals("REINO_V1", CharacterSheetFormat.REINO_V1.id)
    }
}
