package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.ChildProfileEntity
import com.example.data.local.entities.DailyRecordEntity
import com.example.data.local.entities.LevelProgressEntity
import com.example.data.local.entities.UnlockedAnimalEntity
import com.example.data.local.entities.UnlockedBadgeEntity
import com.example.data.local.entities.UnlockedStickerEntity
import com.example.data.local.entities.UserStatsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChildProfileDao {
    @Query("SELECT * FROM child_profile WHERE id = 1 LIMIT 1")
    fun getProfile(): Flow<ChildProfileEntity?>

    @Query("SELECT * FROM child_profile WHERE id = 1 LIMIT 1")
    suspend fun getProfileSync(): ChildProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: ChildProfileEntity)
}

@Dao
interface LevelProgressDao {
    @Query("SELECT * FROM level_progress WHERE areaTopic = :topic ORDER BY levelNumber ASC")
    fun getLevelsForTopic(topic: String): Flow<List<LevelProgressEntity>>

    @Query("SELECT * FROM level_progress WHERE areaTopic = :topic AND levelNumber = :level LIMIT 1")
    suspend fun getLevel(topic: String, level: Int): LevelProgressEntity?

    @Query("SELECT * FROM level_progress")
    fun getAllLevels(): Flow<List<LevelProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateLevel(level: LevelProgressEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLevels(levels: List<LevelProgressEntity>)
}

@Dao
interface StatsDao {
    @Query("SELECT * FROM user_stats WHERE id = 1 LIMIT 1")
    fun getStats(): Flow<UserStatsEntity?>

    @Query("SELECT * FROM user_stats WHERE id = 1 LIMIT 1")
    suspend fun getStatsSync(): UserStatsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStats(stats: UserStatsEntity)
}

@Dao
interface BadgeDao {
    @Query("SELECT * FROM unlocked_badges")
    fun getUnlockedBadges(): Flow<List<UnlockedBadgeEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun unlockBadge(badge: UnlockedBadgeEntity)
}

@Dao
interface AnimalDao {
    @Query("SELECT * FROM unlocked_animals")
    fun getUnlockedAnimals(): Flow<List<UnlockedAnimalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun unlockOrUpdateAnimal(animal: UnlockedAnimalEntity)

    @Query("UPDATE unlocked_animals SET isSelected = 0")
    suspend fun clearSelectedAnimals()

    @Query("UPDATE unlocked_animals SET isSelected = 1 WHERE animalId = :animalId")
    suspend fun selectAnimal(animalId: String)
}

@Dao
interface DailyRecordDao {
    @Query("SELECT * FROM daily_records WHERE dateKey = :dateKey LIMIT 1")
    fun getDailyRecord(dateKey: String): Flow<DailyRecordEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateDailyRecord(record: DailyRecordEntity)
}

@Dao
interface StickerDao {
    @Query("SELECT * FROM unlocked_stickers")
    fun getUnlockedStickers(): Flow<List<UnlockedStickerEntity>>

    @Query("SELECT * FROM unlocked_stickers")
    suspend fun getUnlockedStickersSync(): List<UnlockedStickerEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun unlockSticker(sticker: UnlockedStickerEntity)

    @Query("DELETE FROM unlocked_stickers")
    suspend fun clearAllStickers()
}
