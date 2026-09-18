package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.AnimalDao
import com.example.data.local.dao.BadgeDao
import com.example.data.local.dao.ChildProfileDao
import com.example.data.local.dao.DailyRecordDao
import com.example.data.local.dao.LevelProgressDao
import com.example.data.local.dao.StatsDao
import com.example.data.local.dao.StickerDao
import com.example.data.local.entities.ChildProfileEntity
import com.example.data.local.entities.DailyRecordEntity
import com.example.data.local.entities.LevelProgressEntity
import com.example.data.local.entities.UnlockedAnimalEntity
import com.example.data.local.entities.UnlockedBadgeEntity
import com.example.data.local.entities.UnlockedStickerEntity
import com.example.data.local.entities.UserStatsEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ChildProfileEntity::class,
        LevelProgressEntity::class,
        UserStatsEntity::class,
        UnlockedBadgeEntity::class,
        UnlockedAnimalEntity::class,
        DailyRecordEntity::class,
        UnlockedStickerEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun childProfileDao(): ChildProfileDao
    abstract fun levelProgressDao(): LevelProgressDao
    abstract fun statsDao(): StatsDao
    abstract fun badgeDao(): BadgeDao
    abstract fun animalDao(): AnimalDao
    abstract fun dailyRecordDao(): DailyRecordDao
    abstract fun stickerDao(): StickerDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "jungle_math_safari.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    prepopulateDatabase(database)
                }
            }
        }

        private suspend fun prepopulateDatabase(database: AppDatabase) {
            // Default child profile
            database.childProfileDao().insertOrUpdateProfile(
                ChildProfileEntity(
                    id = 1,
                    nickname = "Leo the Explorer",
                    ageRange = "6-8",
                    selectedCompanionId = "lion",
                    avatarEmoji = "🦁"
                )
            )

            // Default stats
            database.statsDao().insertOrUpdateStats(
                UserStatsEntity(
                    id = 1,
                    totalCoins = 100,
                    totalStars = 0,
                    totalAnswered = 0,
                    totalCorrect = 0
                )
            )

            // Starter animal companions
            database.animalDao().unlockOrUpdateAnimal(
                UnlockedAnimalEntity(animalId = "lion", isSelected = true)
            )
            database.animalDao().unlockOrUpdateAnimal(
                UnlockedAnimalEntity(animalId = "monkey", isSelected = false)
            )

            // Unlock First Safari badge
            database.badgeDao().unlockBadge(
                UnlockedBadgeEntity(badgeId = "first_safari")
            )

            // Pre-seed 10 levels for each of the 4 areas (Level 1 unlocked by default)
            val topics = listOf("counting", "addition", "subtraction", "multiplication")
            val initialLevels = mutableListOf<LevelProgressEntity>()
            topics.forEach { topic ->
                for (lvl in 1..10) {
                    initialLevels.add(
                        LevelProgressEntity(
                            areaTopic = topic,
                            levelNumber = lvl,
                            starsEarned = 0,
                            highScore = 0,
                            isUnlocked = (lvl == 1),
                            timesPlayed = 0
                        )
                    )
                }
            }
            database.levelProgressDao().insertLevels(initialLevels)
        }
    }
}
