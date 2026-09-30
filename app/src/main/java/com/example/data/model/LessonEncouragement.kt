package com.example.data.model

data class CompanionEncouragement(
    val companionId: String,
    val name: String,
    val emoji: String,
    val title: String,
    val role: String,
    val colorHex: Long,
    val cheerHeadline: String,
    val message: String,
    val voiceSpeech: String,
    val celebrationMove: String = "JUMP"
)

data class LessonEncouragementState(
    val topic: MathTopic,
    val levelNumber: Int,
    val lessonTitle: String,
    val biomeName: String,
    val starsEarned: Int,
    val score: Int,
    val coinsEarned: Int,
    val isPerfect: Boolean,
    val isDailyChallenge: Boolean = false,
    val primaryCompanionId: String = "lion",
    val companions: List<CompanionEncouragement>,
    val selectedCompanionIndex: Int = 0,
    val unlockedSticker: AnimalSticker? = null,
    val timestamp: Long = System.currentTimeMillis()
)

object LessonEncouragementFactory {

    fun create(
        topic: MathTopic,
        levelNumber: Int,
        starsEarned: Int,
        score: Int,
        coinsEarned: Int,
        isPerfect: Boolean,
        isDailyChallenge: Boolean = false,
        unlockedSticker: AnimalSticker? = null,
        selectedCompanionId: String = "lion"
    ): LessonEncouragementState {
        val biomeName = when (topic) {
            MathTopic.COUNTING -> "Counting Creek"
            MathTopic.ADDITION -> "Addition Woods"
            MathTopic.SUBTRACTION -> "Subtraction Savannah"
            MathTopic.MULTIPLICATION -> "Multiplication Canopy"
        }

        val lessonTitle = if (isDailyChallenge) {
            "Daily Safari Challenge"
        } else {
            when (topic) {
                MathTopic.COUNTING -> listOf(
                    "River Pebbles", "Baby Fruits", "Water Lilypads", "River Otters",
                    "Capybara Crossing", "Bamboo Shoots", "Waterfall Berries", "Dragonfly Hop",
                    "Sparkling Pearls", "Creek Champion"
                ).getOrElse(levelNumber - 1) { "Lesson $levelNumber" }
                MathTopic.ADDITION -> listOf(
                    "Fruit Pairs", "Apple Harvest", "Forest Foraging", "Leo's Pounce",
                    "Tapir's Feast", "Double Digits", "Hidden Treasures", "Ancient Grove",
                    "Sunlit Canopy", "Woods Master"
                ).getOrElse(levelNumber - 1) { "Lesson $levelNumber" }
                MathTopic.SUBTRACTION -> listOf(
                    "Coconut Share", "Watering Hole", "Acacia Leaves", "Zebra Herd",
                    "Savannah Breeze", "Sunset Grazing", "Pride Rock Trial", "Sandy Paths",
                    "Safari Scout", "Tembo's Triumph"
                ).getOrElse(levelNumber - 1) { "Lesson $levelNumber" }
                MathTopic.MULTIPLICATION -> listOf(
                    "Twiga's Pairs", "Trio Parrots", "Vine Bunches", "High-Five Hands",
                    "Treetop Arrays", "Cloud Platforms", "Canopy Patterns", "Skybridge Arrays",
                    "Harpy Eagle", "Golden Peak Idol"
                ).getOrElse(levelNumber - 1) { "Lesson $levelNumber" }
            }
        }

        val companionsList = buildCompanionsList(topic, levelNumber, starsEarned, isPerfect)

        val primaryIndex = companionsList.indexOfFirst { it.companionId == selectedCompanionId }
            .let { if (it >= 0) it else 0 }

        return LessonEncouragementState(
            topic = topic,
            levelNumber = levelNumber,
            lessonTitle = lessonTitle,
            biomeName = biomeName,
            starsEarned = starsEarned,
            score = score,
            coinsEarned = coinsEarned,
            isPerfect = isPerfect,
            isDailyChallenge = isDailyChallenge,
            primaryCompanionId = selectedCompanionId,
            companions = companionsList,
            selectedCompanionIndex = primaryIndex,
            unlockedSticker = unlockedSticker
        )
    }

    private fun buildCompanionsList(
        topic: MathTopic,
        levelNumber: Int,
        starsEarned: Int,
        isPerfect: Boolean
    ): List<CompanionEncouragement> {
        val starsWord = when (starsEarned) {
            3 -> "3 Gold Stars"
            2 -> "2 Bright Stars"
            else -> "a Shiny Star"
        }

        val leo = CompanionEncouragement(
            companionId = "lion",
            name = "Leo the Lion",
            emoji = "🦁",
            title = "Addition Buddy",
            role = "Pride Leader",
            colorHex = 0xFFE65100,
            cheerHeadline = if (isPerfect) "ROAR! Flawless Math Victory!" else "Mighty Pride Roar!",
            message = if (isPerfect) {
                "Incredible courage, explorer! You answered every question without missing a single beat! That's true royal power!"
            } else {
                "You pushed forward like a brave lion scout! Earning $starsWord on Lesson $levelNumber is something to roar proudly about!"
            },
            voiceSpeech = "Roar! Incredible courage! You completed Lesson $levelNumber with $starsWord!",
            celebrationMove = "JUMP"
        )

        val kiki = CompanionEncouragement(
            companionId = "monkey",
            name = "Kiki the Monkey",
            emoji = "🐒",
            title = "Counting Buddy",
            role = "Canopy Acrobat",
            colorHex = 0xFFFFA000,
            cheerHeadline = "Ooh-Ooh! High Paw!",
            message = if (isPerfect) {
                "Banana-tastic! You swung right through the trail and solved every puzzle in record time! Catch some jungle snacks!"
            } else {
                "Yahoo! Look at your math trail growing! Keep swinging branch by branch, you're doing amazing!"
            },
            voiceSpeech = "Ooh ooh ah ah! High paw! That was banana-tastic math thinking!",
            celebrationMove = "DANCE"
        )

        val tembo = CompanionEncouragement(
            companionId = "elephant",
            name = "Tembo the Elephant",
            emoji = "🐘",
            title = "Subtraction Buddy",
            role = "River Guardian",
            colorHex = 0xFF1976D2,
            cheerHeadline = "Trumpet Blast! Pure Genius!",
            message = if (isPerfect) {
                "TRUMPET! An elephant never forgets a true safari champion. Your concentration was as steady as a great oak!"
            } else {
                "Splendid effort by the watering hole! Step by giant step, you are mastering the jungle mathematics trail!"
            },
            voiceSpeech = "Trumpet blast! Splendid job! An elephant never forgets a true safari champion!",
            celebrationMove = "HIGH_FIVE"
        )

        val twiga = CompanionEncouragement(
            companionId = "giraffe",
            name = "Twiga the Giraffe",
            emoji = "🦒",
            title = "Multiplication Buddy",
            role = "High Watcher",
            colorHex = 0xFF2E7D32,
            cheerHeadline = "Reaching the Highest Heights!",
            message = if (isPerfect) {
                "From way up here in the canopy, your bright stars are lighting up the whole Safari Map! Absolutely brilliant!"
            } else {
                "Stand tall and proud! You conquered another milestone on the trail. Keep looking forward to the next horizon!"
            },
            voiceSpeech = "Stand tall and proud! Your bright stars are lighting up the whole safari map!",
            celebrationMove = "WIGGLE"
        )

        val zane = CompanionEncouragement(
            companionId = "zebra",
            name = "Zane the Zebra",
            emoji = "🦓",
            title = "Safari Scout",
            role = "Trail Navigator",
            colorHex = 0xFF5D4037,
            cheerHeadline = "Stripes of Pure Brilliance!",
            message = if (isPerfect) {
                "Fast as lightning and sharp as a scout! You blazed a pristine path through this biome!"
            } else {
                "Galloping ahead with great confidence! Each completed lesson marks a new milestone on our expedition!"
            },
            voiceSpeech = "Stripes of pure brilliance! You blazed a pristine path through the safari trail!",
            celebrationMove = "JUMP"
        )

        // Prioritize the topic companion first
        return when (topic) {
            MathTopic.COUNTING -> listOf(kiki, leo, tembo, twiga, zane)
            MathTopic.ADDITION -> listOf(leo, kiki, tembo, twiga, zane)
            MathTopic.SUBTRACTION -> listOf(tembo, leo, kiki, twiga, zane)
            MathTopic.MULTIPLICATION -> listOf(twiga, leo, tembo, kiki, zane)
        }
    }
}
