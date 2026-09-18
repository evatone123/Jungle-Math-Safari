package com.example

import com.example.data.model.StickerRarity
import com.example.data.repository.SafariRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testAnimalStickersCollectionData() {
        val stickers = SafariRepository.ALL_STICKERS
        assertEquals(12, stickers.size)

        // Verify required problems are strictly ascending
        for (i in 0 until stickers.size - 1) {
            assertTrue(
                "Sticker ${stickers[i].id} threshold (${stickers[i].requiredProblems}) should be < ${stickers[i + 1].id} (${stickers[i + 1].requiredProblems})",
                stickers[i].requiredProblems < stickers[i + 1].requiredProblems
            )
        }

        // Verify all stickers have animal emoji, name, title, and fun fact
        stickers.forEach { sticker ->
            assertTrue(sticker.id.isNotBlank())
            assertTrue(sticker.name.isNotBlank())
            assertTrue(sticker.animalEmoji.isNotBlank())
            assertTrue(sticker.title.isNotBlank())
            assertTrue(sticker.funFact.isNotBlank())
            assertFalse(sticker.isUnlocked)
        }

        // Verify rarity distribution
        val bronze = stickers.filter { it.rarity == StickerRarity.BRONZE }
        val silver = stickers.filter { it.rarity == StickerRarity.SILVER }
        val gold = stickers.filter { it.rarity == StickerRarity.GOLD }
        val diamond = stickers.filter { it.rarity == StickerRarity.DIAMOND }
        val mythic = stickers.filter { it.rarity == StickerRarity.MYTHIC }

        assertTrue(bronze.isNotEmpty())
        assertTrue(silver.isNotEmpty())
        assertTrue(gold.isNotEmpty())
        assertTrue(diamond.isNotEmpty())
        assertTrue(mythic.isNotEmpty())
    }
}

