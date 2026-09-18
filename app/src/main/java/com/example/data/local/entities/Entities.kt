package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "child_profile")
data class ChildProfileEntity(
    @PrimaryKey val id: Int = 1,
    val nickname: String = "Explorer",
    val ageRange: String = "6-8",
    val selectedCompanionId: String = "lion",
    val avatarEmoji: String = "🦁",
    val soundEnabled: Boolean = true,
    val voiceEnabled: Boolean = true,
    val musicEnabled: Boolean = true,
    val isCloudSyncEnabled: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "level_progress",
    primaryKeys = ["areaTopic", "levelNumber"]
)
data class LevelProgressEntity(
    val areaTopic: String,
    val levelNumber: Int,
    val starsEarned: Int = 0,
    val highScore: Int = 0,
    val isUnlocked: Boolean = false,
    val timesPlayed: Int = 0
)

@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey val id: Int = 1,
    val totalCoins: Int = 50,
    val totalStars: Int = 0,
    val totalAnswered: Int = 0,
    val totalCorrect: Int = 0,
    val countingCorrect: Int = 0,
    val countingTotal: Int = 0,
    val additionCorrect: Int = 0,
    val additionTotal: Int = 0,
    val subtractionCorrect: Int = 0,
    val subtractionTotal: Int = 0,
    val multiplicationCorrect: Int = 0,
    val multiplicationTotal: Int = 0
)

@Entity(tableName = "unlocked_badges")
data class UnlockedBadgeEntity(
    @PrimaryKey val badgeId: String,
    val unlockedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "unlocked_animals")
data class UnlockedAnimalEntity(
    @PrimaryKey val animalId: String,
    val isSelected: Boolean = false,
    val unlockedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "daily_records")
data class DailyRecordEntity(
    @PrimaryKey val dateKey: String, // e.g. "2026-09-16"
    val completed: Boolean = false,
    val score: Int = 0,
    val stars: Int = 0
)

@Entity(tableName = "unlocked_stickers")
data class UnlockedStickerEntity(
    @PrimaryKey val stickerId: String,
    val unlockedAt: Long = System.currentTimeMillis()
)
