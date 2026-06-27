package com.blasck.reino.domain.update

import com.blasck.reino.domain.model.Character
import com.blasck.reino.domain.model.CharacterAttributes
import com.blasck.reino.domain.model.CharacterIdentity
import com.blasck.reino.domain.model.Expertise
import com.blasck.reino.domain.model.Inventory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CharacterUpdateTest {
    @Test
    fun `preview detects scalar list additions and removals`() {
        val current =
            character(
                strength = 10,
                skills = listOf(skill("Esgrima", nh = "14")),
                items = listOf(item("Corda")),
            )
        val updated =
            character(
                strength = 11,
                skills = listOf(skill("Esgrima", nh = "15"), skill("Arco", nh = "12")),
                items = emptyList(),
            )

        val preview = current.previewUpdateWith(updated)

        assertTrue(preview.hasChanges)
        assertEquals(1, preview.addedCount)
        assertEquals(1, preview.removedCount)
        assertEquals(2, preview.changedCount)
        assertTrue(preview.changes.any { it.category == CharacterChangeCategory.ATTRIBUTES && it.label == "ST" })
        assertTrue(preview.changes.any { it.type == CharacterChangeType.ADDED && it.label == "Arco" })
        assertTrue(preview.changes.any { it.type == CharacterChangeType.REMOVED && it.label == "Corda" })
    }

    @Test
    fun `preview uses normalized stable keys for list matching`() {
        val current =
            character(
                skills = listOf(skill("Danca Fionica", nh = "14")),
            )
        val updated =
            character(
                skills = listOf(skill("Dan\u00e7a Fi\u00f4nica", nh = "14")),
            )

        val preview = current.previewUpdateWith(updated)

        assertFalse(preview.hasChanges)
    }

    private fun character(
        strength: Int = 10,
        skills: List<Expertise.ExpertiseModel> = emptyList(),
        items: List<Inventory.InventoryModel> = emptyList(),
    ): Character =
        Character(
            identity = CharacterIdentity(name = "Syrio", player = "Luiz"),
            attributes = CharacterAttributes(strength = strength),
            expertise = Expertise(list = skills),
            inventory = Inventory(list = items),
        )

    private fun skill(
        name: String,
        nh: String,
    ): Expertise.ExpertiseModel =
        Expertise.ExpertiseModel(
            name = name,
            nh = nh,
            difficultType = "DX",
            difficultLevel = "Media",
        )

    private fun item(name: String): Inventory.InventoryModel =
        Inventory.InventoryModel(
            name = name,
            quantity = "1",
            weight = "1 kg",
        )
}
