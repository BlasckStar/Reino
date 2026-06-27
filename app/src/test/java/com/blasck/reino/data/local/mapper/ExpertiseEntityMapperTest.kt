package com.blasck.reino.data.local.mapper

import com.blasck.reino.domain.model.Expertise
import org.junit.Assert.assertEquals
import org.junit.Test

class ExpertiseEntityMapperTest {
    @Test
    fun `preserves expertise fields and modifiers`() {
        val expertise =
            Expertise.ExpertiseModel(
                name = "Corrida",
                difficultType = "F",
                difficultLevel = "D",
                difficultExtra = "HT",
                cost = "2",
                nh = "16",
                modifiers = listOf("1", "-2"),
            )

        val restored = expertise.toEntity(characterId = 9, position = 3).toDomain()

        assertEquals(expertise, restored)
    }
}
