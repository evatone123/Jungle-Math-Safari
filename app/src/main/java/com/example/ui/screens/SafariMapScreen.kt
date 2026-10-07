package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.LevelProgressEntity
import com.example.data.model.DifficultyLevel
import com.example.data.model.LessonEncouragementState
import com.example.data.model.MathTopic
import com.example.ui.components.InteractiveSafariMap
import com.example.ui.components.SafariMapEncouragementOverlay
import com.example.ui.components.SafariTopBar
import com.example.ui.theme.BananaYellow
import com.example.ui.theme.JunglePrimary
import com.example.ui.theme.SafariGold
import com.example.ui.theme.StarGold
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

data class FutureWorld(
    val title: String,
    val skill: String,
    val animalEmoji: String
)

val FUTURE_WORLDS = listOf(
    FutureWorld("Crocodile River", "Division", "🐊"),
    FutureWorld("Parrot Forest", "Fractions", "🦜"),
    FutureWorld("Turtle Lake", "Telling Time", "🐢"),
    FutureWorld("Safari Market", "Coins & Money", "🦓")
)

enum class SafariMapViewMode {
    VISUAL_TRAIL_MAP,
    EXPEDITION_CARDS
}

@Composable
fun SafariMapScreen(
    levels: List<LevelProgressEntity>,
    onSelectTopic: (MathTopic) -> Unit,
    onStartLesson: (MathTopic, Int, DifficultyLevel) -> Unit = { _, _, _ -> },
    onBackClick: () -> Unit,
    onNavigateToEncyclopedia: (MathTopic?) -> Unit = {},
    companionAvatar: String = "🦁",
    isMusicOn: Boolean = true,
    onMusicToggle: () -> Unit = {},
    activeEncouragement: LessonEncouragementState? = null,
    onDismissEncouragement: () -> Unit = {},
    onSpeakEncouragement: (String) -> Unit = {},
    onGiveHighFive: () -> Unit = {},
    onTriggerEncouragementPreview: (MathTopic, Int) -> Unit = { _, _ -> },
    onViewStickers: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var viewMode by remember { mutableStateOf(SafariMapViewMode.VISUAL_TRAIL_MAP) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SafariTopBar(
                title = "Safari Map 🗺️",
                subtitle = "Explore math zones & conquer milestones",
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
                // View Mode Switcher: Visual Trail Map vs Cards vs BC
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                FilterChip(
                    selected = viewMode == SafariMapViewMode.VISUAL_TRAIL_MAP,
                    onClick = { viewMode = SafariMapViewMode.VISUAL_TRAIL_MAP },
                    label = { Text("🗺️ Visual Trail Map") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = JunglePrimary,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("tab_visual_trail_map")
                )
                FilterChip(
                    selected = viewMode == SafariMapViewMode.EXPEDITION_CARDS,
                    onClick = { viewMode = SafariMapViewMode.EXPEDITION_CARDS },
                    label = { Text("📜 Cards") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = JunglePrimary,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("tab_expedition_cards")
                )
                FilterChip(
                    selected = activeEncouragement != null,
                    onClick = {
                        val completed = levels.filter { it.starsEarned > 0 }.maxByOrNull { it.levelNumber }
                        val topic = if (completed != null) MathTopic.fromId(completed.areaTopic) else MathTopic.COUNTING
                        val lvl = completed?.levelNumber ?: 1
                        onTriggerEncouragementPreview(topic, lvl)
                    },
                    label = { Text("BC") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SafariGold,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("tab_companion_cheers")
                )
            }

            if (viewMode == SafariMapViewMode.VISUAL_TRAIL_MAP) {
                // Interactive Visual Safari Map with winding jungle trail, milestones, and zone waypoints
                InteractiveSafariMap(
                    levels = levels,
                    onSelectTopic = onSelectTopic,
                    onStartLesson = onStartLesson,
                    onNavigateToEncyclopedia = onNavigateToEncyclopedia,
                    companionAvatar = companionAvatar,
                    onTriggerEncouragement = onTriggerEncouragementPreview,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Quick link to Jungle Encyclopedia
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .clickable { onNavigateToEncyclopedia(null) }
                            .testTag("card_open_encyclopedia"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = JunglePrimary)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "📖", fontSize = 28.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Jungle Encyclopedia",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "16 animals to discover across math zones!",
                                        fontSize = 12.sp,
                                        color = BananaYellow
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Open",
                                tint = Color.White
                            )
                        }
                    }
                }

                // 4 Playable Worlds
                items(MathTopic.entries) { topic ->
                    val topicLevels = levels.filter { it.areaTopic == topic.id }
                    val totalStarsInTopic = topicLevels.sumOf { it.starsEarned }
                    val completedLevels = topicLevels.count { it.starsEarned > 0 }

                    WorldAdventureCard(
                        topic = topic,
                        starsCount = totalStarsInTopic,
                        completedLevels = completedLevels,
                        totalLevels = 10,
                        onClick = { onSelectTopic(topic) }
                    )
                }

            // Section Header: Future Expeditions
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Upcoming",
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Upcoming Expeditions (Coming Soon)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                }
            }

            // Locked Future Worlds
            items(FUTURE_WORLDS) { future ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEEEEEE)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = future.animalEmoji, fontSize = 24.sp)
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = future.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color.DarkGray
                                )
                                Text(
                                    text = "Topic: ${future.skill}",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFEEEEEE)
                        ) {
                            Text(
                                text = "🔒 Locked",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
    }
    }

    // Screen State: Animated Animal Companions Offering Encouragement After Completing a Lesson
    activeEncouragement?.let { encouragementState ->
        SafariMapEncouragementOverlay(
            state = encouragementState,
            onDismiss = onDismissEncouragement,
            onSpeakEncouragement = onSpeakEncouragement,
            onGiveHighFive = onGiveHighFive,
            onStartNextLesson = onStartLesson,
            onRetryLesson = onStartLesson,
            onViewStickers = onViewStickers
        )
    }
}
}

@Composable
fun WorldAdventureCard(
    topic: MathTopic,
    starsCount: Int,
    completedLevels: Int,
    totalLevels: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val topicColor = Color(topic.colorHex)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable { onClick() }
            .testTag("world_card_${topic.id}"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Top Banner of the card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(topicColor, topicColor.copy(alpha = 0.85f))
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = topic.animalEmoji, fontSize = 28.sp)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = topic.subtitle.uppercase(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BananaYellow,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = topic.title,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }

                    // Total Stars badge
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color.Black.copy(alpha = 0.25f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Stars",
                                tint = StarGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$starsCount/30",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Details and progress
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "${topic.animalName} • ${topic.animalTitle}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "$completedLevels of $totalLevels levels mastered",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "EXPLORE",
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        color = topicColor
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Go",
                        tint = topicColor
                    )
                }
            }
        }
    }
}
