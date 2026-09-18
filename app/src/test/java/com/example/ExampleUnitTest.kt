package com.example

import com.example.audio.BackgroundAudioManager
import com.example.audio.JungleAudioTheme
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
    fun testJungleAudioThemesAndPresets() {
        val themes = JungleAudioTheme.entries
        assertEquals(3, themes.size)

        // Verify all themes have unique valid properties
        themes.forEach { theme ->
            assertTrue(theme.id.isNotBlank())
            assertTrue(theme.title.isNotBlank())
            assertTrue(theme.emoji.isNotBlank())
            assertTrue(theme.description.isNotBlank())
            assertEquals(theme, JungleAudioTheme.fromId(theme.id))
        }

        // Test fallback
        assertEquals(JungleAudioTheme.SERENE_RIVER, JungleAudioTheme.fromId("unknown_preset"))
    }

    @Test
    fun testBackgroundAudioManagerVolumeAndThemeControl() {
        val manager = BackgroundAudioManager()
        assertEquals(JungleAudioTheme.SERENE_RIVER, manager.currentTheme)
        assertEquals(0.45f, manager.masterVolume, 0.001f)
        assertFalse(manager.isPlaying)

        manager.setVolume(0.8f)
        assertEquals(0.8f, manager.masterVolume, 0.001f)

        // Verify clamping
        manager.setVolume(1.5f)
        assertEquals(1.0f, manager.masterVolume, 0.001f)

        manager.setVolume(0.01f)
        assertEquals(0.1f, manager.masterVolume, 0.001f)

        manager.setTheme(JungleAudioTheme.CANOPY_BIRDS)
        assertEquals(JungleAudioTheme.CANOPY_BIRDS, manager.currentTheme)
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

    @Test
    fun testCategoryProgressItemCalculations() {
        val countingItem = com.example.ui.components.CategoryProgressItem(
            topic = com.example.data.model.MathTopic.COUNTING,
            title = "Counting",
            emoji = "🍌",
            animalEmoji = "🐒",
            animalName = "Kiki",
            solvedCount = 18,
            totalCount = 20,
            color = androidx.compose.ui.graphics.Color.Yellow
        )

        assertEquals(90, countingItem.accuracyPct)
        assertEquals("🐾 Explorer", countingItem.masteryTier)

        val masterItem = countingItem.copy(solvedCount = 55, totalCount = 60)
        assertEquals("👑 Math Master", masterItem.masteryTier)

        val unplayedItem = countingItem.copy(solvedCount = 0, totalCount = 0)
        assertEquals(0, unplayedItem.accuracyPct)
        assertEquals("🔒 Unexplored", unplayedItem.masteryTier)
    }

    @Test
    fun testAnimalCompanionsForMathTopics() {
        val lion = com.example.ui.components.AnimalCompanions.forTopic(com.example.data.model.MathTopic.ADDITION)
        assertEquals("Leo the Lion", lion.name)
        assertEquals("🦁", lion.animalEmoji)
        assertTrue(lion.victoryQuotes.isNotEmpty())
        assertTrue(lion.encouragementQuotes.isNotEmpty())

        val monkey = com.example.ui.components.AnimalCompanions.forTopic(com.example.data.model.MathTopic.COUNTING)
        assertEquals("Kiki the Monkey", monkey.name)
        assertEquals("🐒", monkey.animalEmoji)
        assertTrue(monkey.victoryQuotes.isNotEmpty())

        val elephant = com.example.ui.components.AnimalCompanions.forTopic(com.example.data.model.MathTopic.SUBTRACTION)
        assertEquals("Tembo the Elephant", elephant.name)
        assertEquals("🐘", elephant.animalEmoji)

        val giraffe = com.example.ui.components.AnimalCompanions.forTopic(com.example.data.model.MathTopic.MULTIPLICATION)
        assertEquals("Twiga the Giraffe", giraffe.name)
        assertEquals("🦒", giraffe.animalEmoji)
    }
}

