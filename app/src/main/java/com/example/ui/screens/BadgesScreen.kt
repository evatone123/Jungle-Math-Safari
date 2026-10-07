package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.DifficultyProgressEntity
import com.example.data.model.SafariBadge
import com.example.ui.components.SafariTopBar
import com.example.ui.theme.AdventureOrange
import com.example.ui.theme.BananaYellow
import com.example.ui.theme.EncouragementGreen
import com.example.ui.theme.JunglePrimary
import com.example.ui.theme.SafariGold
import com.example.ui.theme.StarGold

enum class BadgeFilter(val label: String, val icon: String) {
    ALL("All Badges", "🏅"),
    EASY("Easy Tier", "🌱"),
    MEDIUM("Medium Tier", "🐾"),
    HARD("Hard Tier", "⚡"),
    SPECIAL("Special & Topics", "🌟")
}

@Composable
fun BadgesScreen(
    badges: List<SafariBadge>,
    onBackClick: () -> Unit,
    difficultyProgress: List<DifficultyProgressEntity> = emptyList(),
    onNavigateToStickers: (() -> Unit)? = null,
    isMusicOn: Boolean = true,
    onMusicToggle: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val unlockedCount = badges.count { it.isUnlocked }
    var selectedFilter by remember { mutableStateOf(BadgeFilter.ALL) }

    val filteredBadges = remember(badges, selectedFilter) {
        when (selectedFilter) {
            BadgeFilter.ALL -> badges
            BadgeFilter.EASY -> badges.filter { it.difficultyTier == "easy" }
            BadgeFilter.MEDIUM -> badges.filter { it.difficultyTier == "medium" }
            BadgeFilter.HARD -> badges.filter { it.difficultyTier == "hard" }
            BadgeFilter.SPECIAL -> badges.filter { it.difficultyTier == null || it.difficultyTier == "all" }
        }
    }

    // Compute progress stats for the 3 difficulty levels
    val easyProgress = difficultyProgress.find { it.difficultyId == "easy" }
    val mediumProgress = difficultyProgress.find { it.difficultyId == "medium" }
    val hardProgress = difficultyProgress.find { it.difficultyId == "hard" }

    val easyBadgesUnlocked = badges.count { it.difficultyTier == "easy" && it.isUnlocked }
    val mediumBadgesUnlocked = badges.count { it.difficultyTier == "medium" && it.isUnlocked }
    val hardBadgesUnlocked = badges.count { it.difficultyTier == "hard" && it.isUnlocked }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SafariTopBar(
            title = "Digital Animal Badges 🏅",
            subtitle = "$unlockedCount of ${badges.size} Badges Earned across Difficulties",
            onBackClick = onBackClick,
            onMusicToggle = onMusicToggle,
            isMusicOn = isMusicOn
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 1000.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 160.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Top Difficulty Tiers Overview Cards
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Difficulty Progression Tiers",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = JunglePrimary,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            DifficultyTierSummaryCard(
                                title = "Easy Tier",
                                icon = "🌱",
                                levelsCompleted = easyProgress?.levelsCompleted ?: 0,
                                badgesEarned = easyBadgesUnlocked,
                                totalBadges = 4,
                                tierColor = EncouragementGreen,
                                isSelected = selectedFilter == BadgeFilter.EASY,
                                onClick = {
                                    selectedFilter = if (selectedFilter == BadgeFilter.EASY) BadgeFilter.ALL else BadgeFilter.EASY
                                }
                            )
                            DifficultyTierSummaryCard(
                                title = "Medium Tier",
                                icon = "🐾",
                                levelsCompleted = mediumProgress?.levelsCompleted ?: 0,
                                badgesEarned = mediumBadgesUnlocked,
                                totalBadges = 4,
                                tierColor = SafariGold,
                                isSelected = selectedFilter == BadgeFilter.MEDIUM,
                                onClick = {
                                    selectedFilter = if (selectedFilter == BadgeFilter.MEDIUM) BadgeFilter.ALL else BadgeFilter.MEDIUM
                                }
                            )
                            DifficultyTierSummaryCard(
                                title = "Hard Tier",
                                icon = "⚡",
                                levelsCompleted = hardProgress?.levelsCompleted ?: 0,
                                badgesEarned = hardBadgesUnlocked,
                                totalBadges = 4,
                                tierColor = AdventureOrange,
                                isSelected = selectedFilter == BadgeFilter.HARD,
                                onClick = {
                                    selectedFilter = if (selectedFilter == BadgeFilter.HARD) BadgeFilter.ALL else BadgeFilter.HARD
                                }
                            )
                        }
                    }
                }

                // Filter Chips Row
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BadgeFilter.entries.forEach { filter ->
                            val count = when (filter) {
                                BadgeFilter.ALL -> badges.size
                                BadgeFilter.EASY -> badges.count { it.difficultyTier == "easy" }
                                BadgeFilter.MEDIUM -> badges.count { it.difficultyTier == "medium" }
                                BadgeFilter.HARD -> badges.count { it.difficultyTier == "hard" }
                                BadgeFilter.SPECIAL -> badges.count { it.difficultyTier == null || it.difficultyTier == "all" }
                            }
                            FilterChip(
                                selected = selectedFilter == filter,
                                onClick = { selectedFilter = filter },
                                label = { Text("${filter.icon} ${filter.label} ($count)") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = JunglePrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }

                // Optional Link to Sticker Album
                if (onNavigateToStickers != null) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = BananaYellow.copy(alpha = 0.25f)),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(SafariGold)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(text = "🐾", fontSize = 28.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Animal Sticker Album",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Unlock shiny stickers by solving problem milestones!",
                                            fontSize = 12.sp,
                                            color = Color.DarkGray
                                        )
                                    }
                                }
                                Button(
                                    onClick = onNavigateToStickers,
                                    colors = ButtonDefaults.buttonColors(containerColor = JunglePrimary),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.testTag("badges_view_stickers_button")
                                ) {
                                    Text("View Album 🐾", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Grid of Badges
                items(filteredBadges, key = { it.id }) { badge ->
                    BadgeCard(badge = badge)
                }
            }
        }
    }
}

@Composable
private fun DifficultyTierSummaryCard(
    title: String,
    icon: String,
    levelsCompleted: Int,
    badgesEarned: Int,
    totalBadges: Int,
    tierColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) tierColor.copy(alpha = 0.22f) else MaterialTheme.colorScheme.surface
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(if (isSelected) tierColor else tierColor.copy(alpha = 0.4f))
        ),
        modifier = Modifier.width(170.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = icon, fontSize = 22.sp)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = tierColor.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "$badgesEarned/$totalBadges 🏅",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = tierColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Black,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "$levelsCompleted levels cleared",
                fontSize = 11.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { (badgesEarned.toFloat() / totalBadges).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = tierColor,
                trackColor = Color.LightGray.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
fun BadgeCard(badge: SafariBadge) {
    val tierLabel = when (badge.difficultyTier) {
        "easy" -> "🌱 Easy Animal"
        "medium" -> "🐾 Medium Animal"
        "hard" -> "⚡ Hard Animal"
        "all" -> "👑 Grandmaster"
        else -> "⭐ Safari Star"
    }

    val tierColor = when (badge.difficultyTier) {
        "easy" -> EncouragementGreen
        "medium" -> SafariGold
        "hard" -> AdventureOrange
        "all" -> Color(0xFF9C27B0)
        else -> JunglePrimary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("badge_card_${badge.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (badge.isUnlocked) MaterialTheme.colorScheme.surface else Color(0xFFF2F2F2)
        ),
        elevation = CardDefaults.cardElevation(if (badge.isUnlocked) 3.dp else 0.dp),
        border = if (badge.isUnlocked) {
            CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SafariGold))
        } else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Difficulty tier tag
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = tierColor.copy(alpha = if (badge.isUnlocked) 0.18f else 0.08f)
            ) {
                Text(
                    text = tierLabel,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (badge.isUnlocked) tierColor else Color.Gray,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Animal Badge Icon Stage
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(
                        if (badge.isUnlocked) {
                            BananaYellow.copy(alpha = 0.35f)
                        } else {
                            Color.LightGray.copy(alpha = 0.3f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (badge.isUnlocked) {
                    Text(text = badge.emoji, fontSize = 36.sp)
                } else {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked Badge",
                        tint = Color.Gray,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = badge.title,
                fontWeight = FontWeight.Black,
                fontSize = 14.sp,
                color = if (badge.isUnlocked) MaterialTheme.colorScheme.onSurface else Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (badge.isUnlocked) badge.description else badge.requirement,
                fontSize = 11.sp,
                color = if (badge.isUnlocked) Color.DarkGray else Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 15.sp,
                minLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Progress bar if locked with target goal
            if (!badge.isUnlocked && badge.targetGoal > 1) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LinearProgressIndicator(
                        progress = { (badge.currentProgress.toFloat() / badge.targetGoal).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = tierColor,
                        trackColor = Color.LightGray.copy(alpha = 0.4f)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${badge.currentProgress}/${badge.targetGoal}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (badge.isUnlocked) JunglePrimary.copy(alpha = 0.15f) else Color.LightGray.copy(alpha = 0.4f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (badge.isUnlocked) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Earned",
                            tint = StarGold,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                    }
                    Text(
                        text = if (badge.isUnlocked) "UNLOCKED ✨" else "LOCKED 🔒",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (badge.isUnlocked) JunglePrimary else Color.DarkGray
                    )
                }
            }
        }
    }
}

@Composable
fun AnimalBadgeCelebrationDialog(
    badge: SafariBadge,
    onViewBadges: () -> Unit,
    onDismiss: () -> Unit
) {
    val tierLabel = when (badge.difficultyTier) {
        "easy" -> "🌱 Easy Tier Animal Badge"
        "medium" -> "🐾 Medium Tier Animal Badge"
        "hard" -> "⚡ Hard Tier Animal Badge"
        "all" -> "👑 Grandmaster Animal Badge"
        else -> "⭐ Safari Achievement Badge"
    }

    val tierColor = when (badge.difficultyTier) {
        "easy" -> EncouragementGreen
        "medium" -> SafariGold
        "hard" -> AdventureOrange
        "all" -> Color(0xFF9C27B0)
        else -> JunglePrimary
    }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🏅 NEW ANIMAL BADGE! 🏅",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = JunglePrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(tierColor.copy(alpha = 0.22f))
                        .border(
                            width = 3.dp,
                            color = tierColor,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = badge.emoji,
                        fontSize = 52.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = badge.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = tierColor.copy(alpha = 0.18f),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = tierLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = tierColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = badge.description,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onViewBadges,
                colors = ButtonDefaults.buttonColors(containerColor = JunglePrimary),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_celebrate_view_badges")
            ) {
                Text("View in Badge Hall 🏅", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Keep Exploring 🐾", fontWeight = FontWeight.SemiBold, color = Color.Gray)
            }
        }
    )
}
