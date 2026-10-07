package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entities.ChildProfileEntity
import com.example.data.local.entities.DailyRecordEntity
import com.example.data.local.entities.DifficultyProgressEntity
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
    private val difficultyProgressDao = database.difficultyProgressDao()

    val childProfile: Flow<ChildProfileEntity> = profileDao.getProfile().map {
        it ?: ChildProfileEntity()
    }

    val userStats: Flow<UserStatsEntity> = statsDao.getStats().map {
        it ?: UserStatsEntity()
    }

    val difficultyProgress: Flow<List<DifficultyProgressEntity>> = difficultyProgressDao.getAllProgress()

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

    val allBadges: Flow<List<SafariBadge>> = combine(
        badgeDao.getUnlockedBadges(),
        statsDao.getStats()
    ) { unlockedList, statsEntity ->
        val unlockedSet = unlockedList.map { it.badgeId }.toSet()
        val stats = statsEntity ?: UserStatsEntity()
        ALL_BADGES.map { badge ->
            val progress = calculateBadgeProgress(badge, stats)
            badge.copy(
                isUnlocked = unlockedSet.contains(badge.id),
                currentProgress = progress
            )
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

        val isEasy = difficulty == DifficultyLevel.EASY
        val isMedium = difficulty == DifficultyLevel.MEDIUM
        val isHard = difficulty == DifficultyLevel.HARD

        // Update User Stats with detailed difficulty counters
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
            multiplicationTotal = currentStats.multiplicationTotal + if (topic == MathTopic.MULTIPLICATION) totalCount else 0,
            easyLevelsCompleted = currentStats.easyLevelsCompleted + if (isEasy) 1 else 0,
            easyCorrect = currentStats.easyCorrect + if (isEasy) correctCount else 0,
            easyTotal = currentStats.easyTotal + if (isEasy) totalCount else 0,
            mediumLevelsCompleted = currentStats.mediumLevelsCompleted + if (isMedium) 1 else 0,
            mediumCorrect = currentStats.mediumCorrect + if (isMedium) correctCount else 0,
            mediumTotal = currentStats.mediumTotal + if (isMedium) totalCount else 0,
            hardLevelsCompleted = currentStats.hardLevelsCompleted + if (isHard) 1 else 0,
            hardCorrect = currentStats.hardCorrect + if (isHard) correctCount else 0,
            hardTotal = currentStats.hardTotal + if (isHard) totalCount else 0
        )
        statsDao.insertOrUpdateStats(updatedStats)

        // Update DifficultyProgressEntity in Room database
        val existingDiff = difficultyProgressDao.getProgress(difficulty.id) ?: DifficultyProgressEntity(difficultyId = difficulty.id)
        difficultyProgressDao.insertOrUpdate(
            existingDiff.copy(
                levelsCompleted = existingDiff.levelsCompleted + 1,
                problemsAttempted = existingDiff.problemsAttempted + totalCount,
                problemsCorrect = existingDiff.problemsCorrect + correctCount,
                perfectRuns = existingDiff.perfectRuns + if (isPerfect) 1 else 0,
                highestScore = maxOf(existingDiff.highestScore, score),
                starsEarned = existingDiff.starsEarned + stars,
                lastPlayedTimestamp = System.currentTimeMillis()
            )
        )

        // Check & unlock badges (including difficulty-specific digital animal badges)
        val newBadges = checkAndUnlockBadges(topic, difficulty, isPerfect, updatedStats)

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
            newlyUnlockedStickers = newStickers,
            newlyUnlockedBadges = newBadges
        )
    }

    data class DailyCompletionResult(
        val bonusCoins: Int,
        val newlyUnlockedStickers: List<AnimalSticker>,
        val newlyUnlockedBadges: List<SafariBadge> = emptyList()
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

        val isEasy = difficulty == DifficultyLevel.EASY
        val isMedium = difficulty == DifficultyLevel.MEDIUM
        val isHard = difficulty == DifficultyLevel.HARD

        val currentStats = statsDao.getStatsSync() ?: UserStatsEntity()
        val updatedStats = currentStats.copy(
            totalCoins = currentStats.totalCoins + bonusCoins,
            totalStars = currentStats.totalStars + stars,
            totalAnswered = currentStats.totalAnswered + 5,
            totalCorrect = currentStats.totalCorrect + correctCount,
            easyLevelsCompleted = currentStats.easyLevelsCompleted + if (isEasy) 1 else 0,
            easyCorrect = currentStats.easyCorrect + if (isEasy) correctCount else 0,
            easyTotal = currentStats.easyTotal + if (isEasy) 5 else 0,
            mediumLevelsCompleted = currentStats.mediumLevelsCompleted + if (isMedium) 1 else 0,
            mediumCorrect = currentStats.mediumCorrect + if (isMedium) correctCount else 0,
            mediumTotal = currentStats.mediumTotal + if (isMedium) 5 else 0,
            hardLevelsCompleted = currentStats.hardLevelsCompleted + if (isHard) 1 else 0,
            hardCorrect = currentStats.hardCorrect + if (isHard) correctCount else 0,
            hardTotal = currentStats.hardTotal + if (isHard) 5 else 0
        )
        statsDao.insertOrUpdateStats(updatedStats)

        val existingDiff = difficultyProgressDao.getProgress(difficulty.id) ?: DifficultyProgressEntity(difficultyId = difficulty.id)
        difficultyProgressDao.insertOrUpdate(
            existingDiff.copy(
                levelsCompleted = existingDiff.levelsCompleted + 1,
                problemsAttempted = existingDiff.problemsAttempted + 5,
                problemsCorrect = existingDiff.problemsCorrect + correctCount,
                starsEarned = existingDiff.starsEarned + stars,
                lastPlayedTimestamp = System.currentTimeMillis()
            )
        )

        badgeDao.unlockBadge(UnlockedBadgeEntity(badgeId = "daily_adventurer"))
        val newBadges = checkAndUnlockBadges(MathTopic.COUNTING, difficulty, false, updatedStats)
        val newStickers = checkAndUnlockStickers(updatedStats)
        return DailyCompletionResult(bonusCoins, newStickers, newBadges)
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

    private suspend fun checkAndUnlockBadges(
        topic: MathTopic,
        difficulty: DifficultyLevel,
        isPerfect: Boolean,
        stats: UserStatsEntity
    ): List<SafariBadge> {
        val alreadyUnlocked = badgeDao.getUnlockedBadgesSync().map { it.badgeId }.toSet()
        val candidateBadgeIds = mutableListOf<String>()

        // Starter & Topic Badges
        candidateBadgeIds.add("first_safari")
        when (topic) {
            MathTopic.COUNTING -> candidateBadgeIds.add("counting_champ")
            MathTopic.ADDITION -> candidateBadgeIds.add("addition_star")
            MathTopic.SUBTRACTION -> candidateBadgeIds.add("subtraction_ace")
            MathTopic.MULTIPLICATION -> candidateBadgeIds.add("multiplication_master")
        }

        if (isPerfect) {
            candidateBadgeIds.add("perfect_explorer")
        }

        if (stats.totalCoins >= 200) {
            candidateBadgeIds.add("coin_collector")
        }

        // --- Easy Tier Digital Animal Badges ---
        if (stats.easyLevelsCompleted >= 1) candidateBadgeIds.add("badge_easy_tortoise")
        if (stats.easyCorrect >= 5) candidateBadgeIds.add("badge_easy_sloth")
        if (stats.easyLevelsCompleted >= 3) candidateBadgeIds.add("badge_easy_koala")
        if (stats.easyLevelsCompleted >= 5) candidateBadgeIds.add("badge_easy_panda")

        // --- Medium Tier Digital Animal Badges ---
        if (stats.mediumLevelsCompleted >= 1) candidateBadgeIds.add("badge_medium_fox")
        if (stats.mediumCorrect >= 10) candidateBadgeIds.add("badge_medium_monkey")
        if (stats.mediumLevelsCompleted >= 3) candidateBadgeIds.add("badge_medium_cheetah")
        if (stats.mediumLevelsCompleted >= 5) candidateBadgeIds.add("badge_medium_rhino")

        // --- Hard Tier Digital Animal Badges ---
        if (stats.hardLevelsCompleted >= 1) candidateBadgeIds.add("badge_hard_eagle")
        if (stats.hardCorrect >= 10) candidateBadgeIds.add("badge_hard_gorilla")
        if (stats.hardLevelsCompleted >= 3) candidateBadgeIds.add("badge_hard_tiger")
        if (stats.hardLevelsCompleted >= 5) candidateBadgeIds.add("badge_hard_lion")

        // --- Multi-Difficulty Grandmaster Badge ---
        if (stats.easyLevelsCompleted >= 1 && stats.mediumLevelsCompleted >= 1 && stats.hardLevelsCompleted >= 1) {
            candidateBadgeIds.add("badge_all_difficulties")
        }

        val newlyUnlocked = mutableListOf<SafariBadge>()
        for (id in candidateBadgeIds) {
            if (!alreadyUnlocked.contains(id)) {
                badgeDao.unlockBadge(UnlockedBadgeEntity(id))
                val badgeDef = ALL_BADGES.find { it.id == id }
                if (badgeDef != null) {
                    newlyUnlocked.add(badgeDef.copy(isUnlocked = true))
                }
            }
        }
        return newlyUnlocked
    }

    private fun calculateBadgeProgress(badge: SafariBadge, stats: UserStatsEntity): Int {
        return when (badge.id) {
            "badge_easy_tortoise" -> stats.easyLevelsCompleted.coerceAtMost(1)
            "badge_easy_sloth" -> stats.easyCorrect.coerceAtMost(5)
            "badge_easy_koala" -> stats.easyLevelsCompleted.coerceAtMost(3)
            "badge_easy_panda" -> stats.easyLevelsCompleted.coerceAtMost(5)

            "badge_medium_fox" -> stats.mediumLevelsCompleted.coerceAtMost(1)
            "badge_medium_monkey" -> stats.mediumCorrect.coerceAtMost(10)
            "badge_medium_cheetah" -> stats.mediumLevelsCompleted.coerceAtMost(3)
            "badge_medium_rhino" -> stats.mediumLevelsCompleted.coerceAtMost(5)

            "badge_hard_eagle" -> stats.hardLevelsCompleted.coerceAtMost(1)
            "badge_hard_gorilla" -> stats.hardCorrect.coerceAtMost(10)
            "badge_hard_tiger" -> stats.hardLevelsCompleted.coerceAtMost(3)
            "badge_hard_lion" -> stats.hardLevelsCompleted.coerceAtMost(5)

            "badge_all_difficulties" -> {
                var tiers = 0
                if (stats.easyLevelsCompleted > 0) tiers++
                if (stats.mediumLevelsCompleted > 0) tiers++
                if (stats.hardLevelsCompleted > 0) tiers++
                tiers
            }
            "coin_collector" -> stats.totalCoins.coerceAtMost(200)
            "perfect_explorer" -> if (stats.totalStars >= 3) 1 else 0
            else -> if (badge.isUnlocked) 1 else 0
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
            // --- Easy Tier Digital Animal Badges ---
            SafariBadge(
                id = "badge_easy_tortoise",
                title = "Tortoise Scout",
                description = "Started steady and solved your first Easy math level!",
                emoji = "🐢",
                requirement = "Complete 1 Easy level",
                difficultyTier = "easy",
                targetGoal = 1
            ),
            SafariBadge(
                id = "badge_easy_sloth",
                title = "Gentle Sloth",
                description = "Solved 5 Easy math problems with great patience and care!",
                emoji = "🦥",
                requirement = "Solve 5 Easy problems",
                difficultyTier = "easy",
                targetGoal = 5
            ),
            SafariBadge(
                id = "badge_easy_koala",
                title = "Koala Climber",
                description = "Climbed high into eucalyptus trees by finishing 3 Easy math levels!",
                emoji = "🐨",
                requirement = "Complete 3 Easy levels",
                difficultyTier = "easy",
                targetGoal = 3
            ),
            SafariBadge(
                id = "badge_easy_panda",
                title = "Panda Scholar",
                description = "Mastered foundational numbers with 5 Easy math victories!",
                emoji = "🐼",
                requirement = "Complete 5 Easy levels",
                difficultyTier = "easy",
                targetGoal = 5
            ),

            // --- Medium Tier Digital Animal Badges ---
            SafariBadge(
                id = "badge_medium_fox",
                title = "Clever Fox",
                description = "Outfoxed tricky math puzzles on your first Medium expedition!",
                emoji = "🦊",
                requirement = "Complete 1 Medium level",
                difficultyTier = "medium",
                targetGoal = 1
            ),
            SafariBadge(
                id = "badge_medium_monkey",
                title = "Acrobat Chimp",
                description = "Swung gracefully through 10 Medium difficulty math challenges!",
                emoji = "🐒",
                requirement = "Solve 10 Medium problems",
                difficultyTier = "medium",
                targetGoal = 10
            ),
            SafariBadge(
                id = "badge_medium_cheetah",
                title = "Cheetah Sprinter",
                description = "Showed lightning calculation speed across 3 Medium levels!",
                emoji = "🐆",
                requirement = "Complete 3 Medium levels",
                difficultyTier = "medium",
                targetGoal = 3
            ),
            SafariBadge(
                id = "badge_medium_rhino",
                title = "Savanna Guardian",
                description = "Stood strong and unshakeable by conquering 5 Medium math levels!",
                emoji = "🦏",
                requirement = "Complete 5 Medium levels",
                difficultyTier = "medium",
                targetGoal = 5
            ),

            // --- Hard Tier Digital Animal Badges ---
            SafariBadge(
                id = "badge_hard_eagle",
                title = "Skyward Eagle",
                description = "Soared high above the canopy with your first Hard math triumph!",
                emoji = "🦅",
                requirement = "Complete 1 Hard level",
                difficultyTier = "hard",
                targetGoal = 1
            ),
            SafariBadge(
                id = "badge_hard_gorilla",
                title = "Silverback Titan",
                description = "Harnessed immense focus to solve 10 tough Hard math problems!",
                emoji = "🦍",
                requirement = "Solve 10 Hard problems",
                difficultyTier = "hard",
                targetGoal = 10
            ),
            SafariBadge(
                id = "badge_hard_tiger",
                title = "Fierce Tiger",
                description = "Pounced on multi-digit calculations across 3 Hard levels!",
                emoji = "🐅",
                requirement = "Complete 3 Hard levels",
                difficultyTier = "hard",
                targetGoal = 3
            ),
            SafariBadge(
                id = "badge_hard_lion",
                title = "Crown Lion King",
                description = "Crowned supreme mathematical monarch after conquering 5 Hard levels!",
                emoji = "🦁",
                requirement = "Complete 5 Hard levels",
                difficultyTier = "hard",
                targetGoal = 5
            ),

            // --- Multi-Difficulty Grandmaster Badge ---
            SafariBadge(
                id = "badge_all_difficulties",
                title = "Safari Grandmaster",
                description = "Proved true mastery across Easy, Medium, and Hard tiers in the wild!",
                emoji = "👑",
                requirement = "Complete levels on Easy, Medium & Hard",
                difficultyTier = "all",
                targetGoal = 3
            ),

            // --- Topic & Milestone Badges ---
            SafariBadge(
                id = "first_safari",
                title = "First Safari",
                description = "Began the magical math journey!",
                emoji = "🦁",
                requirement = "Start your adventure",
                targetGoal = 1
            ),
            SafariBadge(
                id = "counting_champ",
                title = "Counting Champ",
                description = "Mastered counting in Banana Grove!",
                emoji = "🍌",
                requirement = "Complete a Counting level",
                targetGoal = 1
            ),
            SafariBadge(
                id = "addition_star",
                title = "Addition Star",
                description = "Added numbers like a mighty lion!",
                emoji = "🍎",
                requirement = "Complete an Addition level",
                targetGoal = 1
            ),
            SafariBadge(
                id = "subtraction_ace",
                title = "Subtraction Ace",
                description = "Shared coconuts with Tembo the Elephant!",
                emoji = "🥥",
                requirement = "Complete a Subtraction level",
                targetGoal = 1
            ),
            SafariBadge(
                id = "multiplication_master",
                title = "Multiplication Master",
                description = "Reached the treetops with Twiga!",
                emoji = "🌿",
                requirement = "Complete a Multiplication level",
                targetGoal = 1
            ),
            SafariBadge(
                id = "perfect_explorer",
                title = "Perfect Explorer",
                description = "Earned 3 gold stars on a level!",
                emoji = "⭐",
                requirement = "Get 100% on any level",
                targetGoal = 1
            ),
            SafariBadge(
                id = "coin_collector",
                title = "Coin Collector",
                description = "Gathered 200+ shiny Safari Coins!",
                emoji = "🪙",
                requirement = "Reach 200 total coins",
                targetGoal = 200
            ),
            SafariBadge(
                id = "daily_adventurer",
                title = "Daily Adventurer",
                description = "Finished a 5-question Daily Safari!",
                emoji = "🌟",
                requirement = "Complete a Daily Challenge",
                targetGoal = 1
            )
        )
    }
}
