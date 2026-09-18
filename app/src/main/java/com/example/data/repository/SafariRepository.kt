package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entities.ChildProfileEntity
import com.example.data.local.entities.DailyRecordEntity
import com.example.data.local.entities.LevelProgressEntity
import com.example.data.local.entities.UnlockedAnimalEntity
import com.example.data.local.entities.UnlockedBadgeEntity
import com.example.data.local.entities.UnlockedStickerEntity
import com.example.data.local.entities.UserStatsEntity
import com.example.data.model.AnimalCompanion
import com.example.data.model.AnimalSticker
import com.example.data.model.DifficultyLevel
import com.example.data.model.GameLevelSummary
import com.example.data.model.MathTopic
import com.example.data.model.SafariBadge
import com.example.data.model.StickerRarity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SafariRepository(private val database: AppDatabase) {

    private val profileDao = database.childProfileDao()
    private val levelProgressDao = database.levelProgressDao()
    private val statsDao = database.statsDao()
    private val badgeDao = database.badgeDao()
    private val animalDao = database.animalDao()
    private val dailyRecordDao = database.dailyRecordDao()
    private val stickerDao = database.stickerDao()

    val childProfile: Flow<ChildProfileEntity> = profileDao.getProfile().map {
        it ?: ChildProfileEntity()
    }

    val userStats: Flow<UserStatsEntity> = statsDao.getStats().map {
        it ?: UserStatsEntity()
    }

    fun getLevelsForTopic(topic: MathTopic): Flow<List<LevelProgressEntity>> {
        return levelProgressDao.getLevelsForTopic(topic.id)
    }

    val allLevels: Flow<List<LevelProgressEntity>> = levelProgressDao.getAllLevels()

    val availableAnimals: Flow<List<AnimalCompanion>> = animalDao.getUnlockedAnimals().map { unlockedList ->
        val unlockedMap = unlockedList.associateBy { it.animalId }
        ALL_ANIMALS.map { animal ->
            val unlockedRecord = unlockedMap[animal.id]
            animal.copy(
                isUnlocked = unlockedRecord != null,
                isSelected = unlockedRecord?.isSelected == true
            )
        }
    }

    val allBadges: Flow<List<SafariBadge>> = badgeDao.getUnlockedBadges().map { unlockedList ->
        val unlockedSet = unlockedList.map { it.badgeId }.toSet()
        ALL_BADGES.map { badge ->
            badge.copy(isUnlocked = unlockedSet.contains(badge.id))
        }
    }

    val allStickers: Flow<List<AnimalSticker>> = combine(
        stickerDao.getUnlockedStickers(),
        statsDao.getStats()
    ) { unlockedList, statsEntity ->
        val unlockedSet = unlockedList.map { it.stickerId }.toSet()
        val totalCorrect = statsEntity?.totalCorrect ?: 0
        ALL_STICKERS.map { sticker ->
            val isUnlocked = unlockedSet.contains(sticker.id) || totalCorrect >= sticker.requiredProblems
            sticker.copy(
                isUnlocked = isUnlocked,
                currentProgress = totalCorrect.coerceAtMost(sticker.requiredProblems)
            )
        }
    }

    fun getTodayDailyRecord(): Flow<DailyRecordEntity?> {
        val todayKey = getTodayKey()
        return dailyRecordDao.getDailyRecord(todayKey)
    }

    suspend fun updateProfile(nickname: String, ageRange: String, avatar: String) {
        val current = profileDao.getProfileSync() ?: ChildProfileEntity()
        profileDao.insertOrUpdateProfile(
            current.copy(
                nickname = nickname.ifBlank { "Explorer" },
                ageRange = ageRange,
                avatarEmoji = avatar
            )
        )
    }

    suspend fun updateSettings(
        soundEnabled: Boolean,
        voiceEnabled: Boolean,
        musicEnabled: Boolean = true,
        cloudSync: Boolean = false
    ) {
        val current = profileDao.getProfileSync() ?: ChildProfileEntity()
        profileDao.insertOrUpdateProfile(
            current.copy(
                soundEnabled = soundEnabled,
                voiceEnabled = voiceEnabled,
                musicEnabled = musicEnabled,
                isCloudSyncEnabled = cloudSync
            )
        )
    }

    suspend fun selectAnimalCompanion(animalId: String) {
        animalDao.clearSelectedAnimals()
        animalDao.selectAnimal(animalId)
        val companion = ALL_ANIMALS.firstOrNull { it.id == animalId }
        if (companion != null) {
            val currentProfile = profileDao.getProfileSync() ?: ChildProfileEntity()
            profileDao.insertOrUpdateProfile(
                currentProfile.copy(
                    selectedCompanionId = animalId,
                    avatarEmoji = companion.emoji
                )
            )
        }
    }

    suspend fun recordLevelCompletion(
        topic: MathTopic,
        levelNumber: Int,
        score: Int,
        stars: Int,
        correctCount: Int,
        totalCount: Int,
        difficulty: DifficultyLevel = DifficultyLevel.MEDIUM
    ): GameLevelSummary {
        val currentLevel = levelProgressDao.getLevel(topic.id, levelNumber)
            ?: LevelProgressEntity(areaTopic = topic.id, levelNumber = levelNumber, isUnlocked = true)

        val newHighScore = maxOf(currentLevel.highScore, score)
        val newStars = maxOf(currentLevel.starsEarned, stars)

        levelProgressDao.insertOrUpdateLevel(
            currentLevel.copy(
                highScore = newHighScore,
                starsEarned = newStars,
                timesPlayed = currentLevel.timesPlayed + 1
            )
        )

        // Unlock next level if stars >= 1
        if (stars >= 1 && levelNumber < 10) {
            val nextLevel = levelProgressDao.getLevel(topic.id, levelNumber + 1)
                ?: LevelProgressEntity(areaTopic = topic.id, levelNumber = levelNumber + 1)
            levelProgressDao.insertOrUpdateLevel(nextLevel.copy(isUnlocked = true))
        }

        // Coins earned: base coins scaled by difficulty multiplier!
        val isPerfect = (correctCount == totalCount && totalCount > 0)
        val baseCoins = (correctCount * 20) + 50 + (if (isPerfect) 50 else 0)
        val coinsEarned = (baseCoins * difficulty.scoreMultiplier).toInt()

        // Update User Stats
        val currentStats = statsDao.getStatsSync() ?: UserStatsEntity()
        val updatedStats = currentStats.copy(
            totalCoins = currentStats.totalCoins + coinsEarned,
            totalStars = currentStats.totalStars + (if (stars > currentLevel.starsEarned) stars - currentLevel.starsEarned else 0),
            totalAnswered = currentStats.totalAnswered + totalCount,
            totalCorrect = currentStats.totalCorrect + correctCount,
            countingCorrect = currentStats.countingCorrect + if (topic == MathTopic.COUNTING) correctCount else 0,
            countingTotal = currentStats.countingTotal + if (topic == MathTopic.COUNTING) totalCount else 0,
            additionCorrect = currentStats.additionCorrect + if (topic == MathTopic.ADDITION) correctCount else 0,
            additionTotal = currentStats.additionTotal + if (topic == MathTopic.ADDITION) totalCount else 0,
            subtractionCorrect = currentStats.subtractionCorrect + if (topic == MathTopic.SUBTRACTION) correctCount else 0,
            subtractionTotal = currentStats.subtractionTotal + if (topic == MathTopic.SUBTRACTION) totalCount else 0,
            multiplicationCorrect = currentStats.multiplicationCorrect + if (topic == MathTopic.MULTIPLICATION) correctCount else 0,
            multiplicationTotal = currentStats.multiplicationTotal + if (topic == MathTopic.MULTIPLICATION) totalCount else 0
        )
        statsDao.insertOrUpdateStats(updatedStats)

        // Check & unlock badges
        checkAndUnlockBadges(topic, isPerfect, updatedStats)

        // Check animal unlocks based on progress
        checkAnimalUnlocks(updatedStats)

        // Check and award digital animal stickers for problem milestones
        val newStickers = checkAndUnlockStickers(updatedStats)

        return GameLevelSummary(
            topic = topic,
            levelNumber = levelNumber,
            difficulty = difficulty,
            score = score,
            stars = stars,
            coinsEarned = coinsEarned,
            correctCount = correctCount,
            totalCount = totalCount,
            isPerfect = isPerfect,
            newlyUnlockedStickers = newStickers
        )
    }

    data class DailyCompletionResult(
        val bonusCoins: Int,
        val newlyUnlockedStickers: List<AnimalSticker>
    )

    suspend fun recordDailyChallengeCompletion(
        score: Int,
        stars: Int,
        correctCount: Int,
        difficulty: DifficultyLevel = DifficultyLevel.MEDIUM
    ): DailyCompletionResult {
        val todayKey = getTodayKey()
        val bonusCoins = (300 * difficulty.scoreMultiplier).toInt()
        dailyRecordDao.insertOrUpdateDailyRecord(
            DailyRecordEntity(
                dateKey = todayKey,
                completed = true,
                score = score,
                stars = stars
            )
        )

        val currentStats = statsDao.getStatsSync() ?: UserStatsEntity()
        val updatedStats = currentStats.copy(
            totalCoins = currentStats.totalCoins + bonusCoins,
            totalStars = currentStats.totalStars + stars,
            totalAnswered = currentStats.totalAnswered + 5,
            totalCorrect = currentStats.totalCorrect + correctCount
        )
        statsDao.insertOrUpdateStats(updatedStats)

        badgeDao.unlockBadge(UnlockedBadgeEntity(badgeId = "daily_adventurer"))
        val newStickers = checkAndUnlockStickers(updatedStats)
        return DailyCompletionResult(bonusCoins, newStickers)
    }

    suspend fun resetAllProgress() {
        database.clearAllTables()
        // Re-seed defaults
        profileDao.insertOrUpdateProfile(ChildProfileEntity())
        statsDao.insertOrUpdateStats(UserStatsEntity())
        animalDao.unlockOrUpdateAnimal(UnlockedAnimalEntity("lion", true))
        animalDao.unlockOrUpdateAnimal(UnlockedAnimalEntity("monkey", false))
        badgeDao.unlockBadge(UnlockedBadgeEntity("first_safari"))

        val topics = listOf("counting", "addition", "subtraction", "multiplication")
        val initialLevels = mutableListOf<LevelProgressEntity>()
        topics.forEach { topic ->
            for (lvl in 1..10) {
                initialLevels.add(
                    LevelProgressEntity(
                        areaTopic = topic,
                        levelNumber = lvl,
                        isUnlocked = (lvl == 1)
                    )
                )
            }
        }
        levelProgressDao.insertLevels(initialLevels)
    }

    private suspend fun checkAndUnlockBadges(topic: MathTopic, isPerfect: Boolean, stats: UserStatsEntity) {
        badgeDao.unlockBadge(UnlockedBadgeEntity("first_safari"))

        when (topic) {
            MathTopic.COUNTING -> badgeDao.unlockBadge(UnlockedBadgeEntity("counting_champ"))
            MathTopic.ADDITION -> badgeDao.unlockBadge(UnlockedBadgeEntity("addition_star"))
            MathTopic.SUBTRACTION -> badgeDao.unlockBadge(UnlockedBadgeEntity("subtraction_ace"))
            MathTopic.MULTIPLICATION -> badgeDao.unlockBadge(UnlockedBadgeEntity("multiplication_master"))
        }

        if (isPerfect) {
            badgeDao.unlockBadge(UnlockedBadgeEntity("perfect_explorer"))
        }

        if (stats.totalCoins >= 200) {
            badgeDao.unlockBadge(UnlockedBadgeEntity("coin_collector"))
        }
    }

    private suspend fun checkAnimalUnlocks(stats: UserStatsEntity) {
        // Unlock Tembo the Elephant after 5 correct subtraction or 10 total correct
        if (stats.subtractionCorrect >= 3 || stats.totalCorrect >= 8) {
            animalDao.unlockOrUpdateAnimal(UnlockedAnimalEntity("elephant", false))
        }
        // Unlock Twiga the Giraffe after 5 correct multiplication or 15 total correct
        if (stats.multiplicationCorrect >= 3 || stats.totalCorrect >= 15) {
            animalDao.unlockOrUpdateAnimal(UnlockedAnimalEntity("giraffe", false))
        }
        // Unlock Zane the Zebra after 25 total correct answers
        if (stats.totalCorrect >= 25) {
            animalDao.unlockOrUpdateAnimal(UnlockedAnimalEntity("zebra", false))
        }
    }

    private suspend fun checkAndUnlockStickers(stats: UserStatsEntity): List<AnimalSticker> {
        val alreadyUnlocked = stickerDao.getUnlockedStickersSync().map { it.stickerId }.toSet()
        val newlyUnlocked = mutableListOf<AnimalSticker>()

        ALL_STICKERS.forEach { sticker ->
            if (stats.totalCorrect >= sticker.requiredProblems && !alreadyUnlocked.contains(sticker.id)) {
                stickerDao.unlockSticker(UnlockedStickerEntity(sticker.id))
                newlyUnlocked.add(sticker.copy(isUnlocked = true, currentProgress = stats.totalCorrect))
            }
        }
        return newlyUnlocked
    }

    private fun getTodayKey(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    companion object {
        val ALL_STICKERS = listOf(
            AnimalSticker(
                id = "sticker_monkey",
                name = "Cheeky Chimp",
                animalEmoji = "🐒",
                requiredProblems = 3,
                title = "Banana Scout",
                funFact = "Monkeys express affection by grooming and sharing sweet jungle fruits!",
                rarity = StickerRarity.BRONZE
            ),
            AnimalSticker(
                id = "sticker_parrot",
                name = "Rainbow Parrot",
                animalEmoji = "🦜",
                requiredProblems = 6,
                title = "Treetop Whistler",
                funFact = "Parrots are super smart birds that can count small groups of berries!",
                rarity = StickerRarity.BRONZE
            ),
            AnimalSticker(
                id = "sticker_hippo",
                name = "Happy Hippo",
                animalEmoji = "🦛",
                requiredProblems = 10,
                title = "River Splasher",
                funFact = "Hippos love cool water and can hold their breath for up to 5 minutes!",
                rarity = StickerRarity.BRONZE
            ),
            AnimalSticker(
                id = "sticker_koala",
                name = "Sleepy Koala",
                animalEmoji = "🐨",
                requiredProblems = 15,
                title = "Eucalyptus Hugger",
                funFact = "Koalas have unique fingerprints that look almost identical to human fingerprints!",
                rarity = StickerRarity.SILVER
            ),
            AnimalSticker(
                id = "sticker_kangaroo",
                name = "Bouncing Joey",
                animalEmoji = "🦘",
                requiredProblems = 20,
                title = "Savanna Hopper",
                funFact = "Kangaroos use their strong, muscular tails like a third leg for balance!",
                rarity = StickerRarity.SILVER
            ),
            AnimalSticker(
                id = "sticker_lion",
                name = "Roaring Leo",
                animalEmoji = "🦁",
                requiredProblems = 25,
                title = "Savanna Monarch",
                funFact = "A lion's mighty roar can be heard from 5 miles away across the plains!",
                rarity = StickerRarity.SILVER
            ),
            AnimalSticker(
                id = "sticker_elephant",
                name = "Wise Tembo",
                animalEmoji = "🐘",
                requiredProblems = 35,
                title = "Gentle Giant",
                funFact = "Elephants have over 40,000 muscles in their incredible, flexible trunks!",
                rarity = StickerRarity.GOLD
            ),
            AnimalSticker(
                id = "sticker_cheetah",
                name = "Speedy Sprint",
                animalEmoji = "🐆",
                requiredProblems = 50,
                title = "Lightning Sprinter",
                funFact = "Cheetahs can accelerate from 0 to 60 mph faster than many race cars!",
                rarity = StickerRarity.GOLD
            ),
            AnimalSticker(
                id = "sticker_giraffe",
                name = "Skyward Twiga",
                animalEmoji = "🦒",
                requiredProblems = 75,
                title = "Stargazing Scout",
                funFact = "Giraffes have 18-inch-long prehensile blue tongues to gather tall tree leaves!",
                rarity = StickerRarity.GOLD
            ),
            AnimalSticker(
                id = "sticker_zebra",
                name = "Dazzle Zane",
                animalEmoji = "🦓",
                requiredProblems = 100,
                title = "Pattern Prodigy",
                funFact = "Every single zebra has a unique pattern of stripes, like human fingerprints!",
                rarity = StickerRarity.DIAMOND
            ),
            AnimalSticker(
                id = "sticker_panda",
                name = "Zen Panda",
                animalEmoji = "🐼",
                requiredProblems = 150,
                title = "Bamboo Master",
                funFact = "Giant pandas spend up to 14 hours every day happily munching bamboo!",
                rarity = StickerRarity.DIAMOND
            ),
            AnimalSticker(
                id = "sticker_golden_eagle",
                name = "Sovereign Eagle",
                animalEmoji = "🦅",
                requiredProblems = 200,
                title = "Sky Sovereign",
                funFact = "Eagles have crystal sharp vision and can spot a tiny movement from miles up!",
                rarity = StickerRarity.MYTHIC
            )
        )

        val ALL_ANIMALS = listOf(
            AnimalCompanion(
                id = "lion",
                name = "Leo",
                title = "Addition Buddy",
                emoji = "🦁",
                skill = "Addition",
                quote = "Roar! Let's solve this together!",
                unlockLevel = 1
            ),
            AnimalCompanion(
                id = "monkey",
                name = "Kiki",
                title = "Counting Buddy",
                emoji = "🐒",
                skill = "Counting",
                quote = "Let's count bananas together!",
                unlockLevel = 1
            ),
            AnimalCompanion(
                id = "elephant",
                name = "Tembo",
                title = "Subtraction Buddy",
                emoji = "🐘",
                skill = "Subtraction",
                quote = "Take your time and think deeply.",
                unlockLevel = 2
            ),
            AnimalCompanion(
                id = "giraffe",
                name = "Twiga",
                title = "Multiplication Buddy",
                emoji = "🦒",
                skill = "Multiplication",
                quote = "Let's reach high for the stars!",
                unlockLevel = 3
            ),
            AnimalCompanion(
                id = "zebra",
                name = "Zane",
                title = "Safari Scout",
                emoji = "🦓",
                skill = "Mixed Math",
                quote = "Every jungle trail has a pattern!",
                unlockLevel = 4
            )
        )

        val ALL_BADGES = listOf(
            SafariBadge(
                id = "first_safari",
                title = "First Safari",
                description = "Began the magical math journey!",
                emoji = "🦁",
                requirement = "Start your adventure"
            ),
            SafariBadge(
                id = "counting_champ",
                title = "Counting Champ",
                description = "Mastered counting in Banana Grove!",
                emoji = "🍌",
                requirement = "Complete a Counting level"
            ),
            SafariBadge(
                id = "addition_star",
                title = "Addition Star",
                description = "Added numbers like a mighty lion!",
                emoji = "🍎",
                requirement = "Complete an Addition level"
            ),
            SafariBadge(
                id = "subtraction_ace",
                title = "Subtraction Ace",
                description = "Shared coconuts with Tembo the Elephant!",
                emoji = "🥥",
                requirement = "Complete a Subtraction level"
            ),
            SafariBadge(
                id = "multiplication_master",
                title = "Multiplication Master",
                description = "Reached the treetops with Twiga!",
                emoji = "🌿",
                requirement = "Complete a Multiplication level"
            ),
            SafariBadge(
                id = "perfect_explorer",
                title = "Perfect Explorer",
                description = "Earned 3 gold stars on a level!",
                emoji = "⭐",
                requirement = "Get 100% on any level"
            ),
            SafariBadge(
                id = "coin_collector",
                title = "Coin Collector",
                description = "Gathered 200+ shiny Safari Coins!",
                emoji = "🪙",
                requirement = "Reach 200 total coins"
            ),
            SafariBadge(
                id = "daily_adventurer",
                title = "Daily Adventurer",
                description = "Finished a 5-question Daily Safari!",
                emoji = "🌟",
                requirement = "Complete a Daily Challenge"
            )
        )
    }
}
