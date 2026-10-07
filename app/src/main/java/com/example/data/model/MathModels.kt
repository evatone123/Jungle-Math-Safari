package com.example.data.model

enum class MathTopic(
    val id: String,
    val title: String,
    val subtitle: String,
    val animalName: String,
    val animalTitle: String,
    val animalEmoji: String,
    val itemEmoji: String,
    val description: String,
    val colorHex: Long
) {
    COUNTING(
        id = "counting",
        title = "Banana Grove",
        subtitle = "Area 1",
        animalName = "Kiki",
        animalTitle = "Counting Buddy",
        animalEmoji = "🐒",
        itemEmoji = "🍌",
        description = "Count the delicious jungle treats!",
        colorHex = 0xFFFFB300
    ),
    ADDITION(
        id = "addition",
        title = "Lion Valley",
        subtitle = "Area 2",
        animalName = "Leo",
        animalTitle = "Addition Buddy",
        animalEmoji = "🦁",
        itemEmoji = "🍎",
        description = "Combine fruits and numbers with a mighty roar!",
        colorHex = 0xFFE65100
    ),
    SUBTRACTION(
        id = "subtraction",
        title = "Elephant Plains",
        subtitle = "Area 3",
        animalName = "Tembo",
        animalTitle = "Subtraction Buddy",
        animalEmoji = "🐘",
        itemEmoji = "🥥",
        description = "Share coconuts and find out how many are left!",
        colorHex = 0xFF1976D2
    ),
    MULTIPLICATION(
        id = "multiplication",
        title = "Giraffe Hills",
        subtitle = "Area 4",
        animalName = "Twiga",
        animalTitle = "Multiplication Buddy",
        animalEmoji = "🦒",
        itemEmoji = "🌿",
        description = "Reach high into the trees and multiply bunches!",
        colorHex = 0xFF388E3C
    );

    companion object {
        fun fromId(id: String): MathTopic {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: COUNTING
        }
    }
}

enum class QuestionType {
    MULTIPLE_CHOICE,
    TAP_COUNT,
    FEED_ANIMAL,
    VISUAL_GROUP
}

data class Question(
    val id: String,
    val topic: MathTopic,
    val difficulty: Int,
    val difficultyLevel: DifficultyLevel = DifficultyLevel.MEDIUM,
    val questionType: QuestionType,
    val promptText: String,
    val itemEmoji: String,
    val countA: Int,
    val countB: Int = 0,
    val operatorSymbol: String = "",
    val formulaText: String = "",
    val correctAnswer: Int,
    val options: List<Int>,
    val progressiveHints: List<String>
)

enum class DifficultyLevel(
    val id: String,
    val title: String,
    val emoji: String,
    val subtitle: String,
    val description: String,
    val scoreMultiplier: Float,
    val colorHex: Long
) {
    EASY(
        id = "easy",
        title = "Easy",
        emoji = "🌿",
        subtitle = "Beginner Explorer",
        description = "Numbers 1-10 • Simple visual counting & single-digit sums",
        scoreMultiplier = 1.0f,
        colorHex = 0xFF43A047 // Leaf Green
    ),
    MEDIUM(
        id = "medium",
        title = "Medium",
        emoji = "🐾",
        subtitle = "Safari Adventurer",
        description = "Numbers up to 20 • Standard operations & bridging ten",
        scoreMultiplier = 1.25f,
        colorHex = 0xFFFB8C00 // Safari Orange
    ),
    HARD(
        id = "hard",
        title = "Hard",
        emoji = "⚡",
        subtitle = "Jungle Master",
        description = "Numbers up to 50 • Multi-digit operations & tricky arrays",
        scoreMultiplier = 1.5f,
        colorHex = 0xFFD81B60 // Master Magenta
    );

    companion object {
        fun fromId(id: String): DifficultyLevel {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: MEDIUM
        }
    }
}

data class AnimalCompanion(
    val id: String,
    val name: String,
    val title: String,
    val emoji: String,
    val skill: String,
    val quote: String,
    val unlockLevel: Int,
    val isUnlocked: Boolean = false,
    val isSelected: Boolean = false
)

data class SafariBadge(
    val id: String,
    val title: String,
    val description: String,
    val emoji: String,
    val isUnlocked: Boolean = false,
    val requirement: String,
    val difficultyTier: String? = null, // "easy", "medium", "hard", "all", or null
    val targetGoal: Int = 1,
    val currentProgress: Int = 0
)

enum class StickerRarity(val label: String, val colorHex: Long, val badgeEmoji: String) {
    BRONZE("Bronze", 0xFFCD7F32, "🥉"),
    SILVER("Silver", 0xFFC0C0C0, "🥈"),
    GOLD("Gold", 0xFFFFD700, "🥇"),
    DIAMOND("Diamond Safari", 0xFF00E5FF, "💎"),
    MYTHIC("Mythic Legend", 0xFFFF4081, "👑")
}

data class AnimalSticker(
    val id: String,
    val name: String,
    val animalEmoji: String,
    val requiredProblems: Int,
    val title: String,
    val funFact: String,
    val rarity: StickerRarity = StickerRarity.BRONZE,
    val isUnlocked: Boolean = false,
    val currentProgress: Int = 0
)

data class GameLevelSummary(
    val topic: MathTopic,
    val levelNumber: Int,
    val difficulty: DifficultyLevel = DifficultyLevel.MEDIUM,
    val score: Int,
    val stars: Int,
    val coinsEarned: Int,
    val correctCount: Int,
    val totalCount: Int,
    val isPerfect: Boolean,
    val newlyUnlockedStickers: List<AnimalSticker> = emptyList(),
    val newlyUnlockedBadges: List<SafariBadge> = emptyList()
)
