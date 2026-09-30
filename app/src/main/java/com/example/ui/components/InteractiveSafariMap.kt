package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.LevelProgressEntity
import com.example.data.model.DifficultyLevel
import com.example.data.model.MathTopic
import com.example.ui.theme.AdventureOrange
import com.example.ui.theme.BananaYellow
import com.example.ui.theme.EncouragementGreen
import com.example.ui.theme.JunglePrimary
import com.example.ui.theme.SafariGold
import com.example.ui.theme.StarGold
import kotlin.math.roundToInt

/**
 * Biome trail definition containing math topics, visual themes, lesson titles, and objectives.
 */
data class BiomeTrailConfig(
    val topic: MathTopic,
    val biomeName: String,
    val landmarkEmoji: String,
    val companionEmoji: String,
    val companionName: String,
    val environmentVibe: String,
    val requiredStarsToUnlock: Int,
    val themeColorHex: Long,
    val secondaryColorHex: Long,
    val pathBorderColorHex: Long,
    val pathFillColorHex: Long,
    val decorList: List<DecorItem>,
    val lessonTitles: List<String>,
    val lessonObjectives: List<String>
)

data class DecorItem(
    val emoji: String,
    val xPercent: Float,
    val yOffsetDp: Int
)

/**
 * Pre-configured data for all 4 safari math biomes with 10 rich lessons each.
 */
val BIOME_TRAIL_CONFIGS = listOf(
    BiomeTrailConfig(
        topic = MathTopic.COUNTING,
        biomeName = "Counting Creek",
        landmarkEmoji = "🌊",
        companionEmoji = "🐒",
        companionName = "Kiki",
        environmentVibe = "Babbling streams, river pebbles & water lilies",
        requiredStarsToUnlock = 0,
        themeColorHex = 0xFF2E7D32,      // Lush Forest Green
        secondaryColorHex = 0xFF00897B,  // River Teal
        pathBorderColorHex = 0xFF81C784,
        pathFillColorHex = 0xFFC8E6C9,
        decorList = listOf(
            DecorItem("🪷", 0.15f, 90),
            DecorItem("🦫", 0.85f, 210),
            DecorItem("🐸", 0.12f, 380),
            DecorItem("🐟", 0.82f, 520),
            DecorItem("🌾", 0.18f, 680),
            DecorItem("🪵", 0.84f, 810)
        ),
        lessonTitles = listOf(
            "River Pebbles",
            "Baby Fruits",
            "Water Lilypads",
            "River Otters",
            "Capybara Crossing",
            "Bamboo Shoots",
            "Waterfall Berries",
            "Dragonfly Hop",
            "Sparkling Pearls",
            "Creek Champion"
        ),
        lessonObjectives = listOf(
            "Count river stones from 1 to 4",
            "Count sweet treats and berries up to 6",
            "Hop and count floating lilypads up to 8",
            "Count friendly otters in the stream up to 10",
            "Count groups of capybaras up to 12",
            "Count fresh green bamboo snacks up to 14",
            "Count treats by the waterfall up to 15",
            "Count buzzing river dragonflies up to 16",
            "Count shiny underwater pearls up to 18",
            "Master grand river counting up to 20!"
        )
    ),
    BiomeTrailConfig(
        topic = MathTopic.ADDITION,
        biomeName = "Addition Woods",
        landmarkEmoji = "🌳",
        companionEmoji = "🦁",
        companionName = "Leo",
        environmentVibe = "Sunlit ancient groves, fruit trees & glowing fireflies",
        requiredStarsToUnlock = 5,
        themeColorHex = 0xFFE65100,      // Amber Orange
        secondaryColorHex = 0xFFF57C00,  // Bright Orange
        pathBorderColorHex = 0xFFFFB74D,
        pathFillColorHex = 0xFFFFE0B2,
        decorList = listOf(
            DecorItem("🍎", 0.82f, 80),
            DecorItem("🐆", 0.14f, 220),
            DecorItem("🍄", 0.86f, 390),
            DecorItem("🦋", 0.12f, 540),
            DecorItem("🌿", 0.85f, 700),
            DecorItem("🍉", 0.16f, 820)
        ),
        lessonTitles = listOf(
            "Fruit Pairs",
            "Apple Harvest",
            "Forest Foraging",
            "Leo's Pounce",
            "Tapir's Feast",
            "Double Digits",
            "Hidden Treasures",
            "Ancient Grove",
            "Sunlit Canopy",
            "Woods Master"
        ),
        lessonObjectives = listOf(
            "Add 1 and 2 juicy jungle fruits together",
            "Combine fresh apples for sums up to 6",
            "Add forest treats for sums up to 8",
            "Pounce on numbers with sums up to 10",
            "Combine orchard fruits for sums up to 12",
            "Add jungle items for sums up to 14",
            "Discover sum totals up to 16",
            "Combine larger bunches up to 18",
            "Addition explorer challenges up to 20",
            "Conquer Leo's ultimate addition trial!"
        )
    ),
    BiomeTrailConfig(
        topic = MathTopic.SUBTRACTION,
        biomeName = "Subtraction Savannah",
        landmarkEmoji = "🌾",
        companionEmoji = "🐘",
        companionName = "Tembo",
        environmentVibe = "Golden grasslands, acacia trees & watering holes",
        requiredStarsToUnlock = 15,
        themeColorHex = 0xFFC2185B,      // Crimson Rose
        secondaryColorHex = 0xFFD81B60,
        pathBorderColorHex = 0xFFF48FB1,
        pathFillColorHex = 0xFFFCE4EC,
        decorList = listOf(
            DecorItem("🥥", 0.15f, 90),
            DecorItem("🦓", 0.84f, 230),
            DecorItem("☀️", 0.12f, 400),
            DecorItem("🐾", 0.86f, 550),
            DecorItem("🦒", 0.14f, 710),
            DecorItem("🌾", 0.82f, 830)
        ),
        lessonTitles = listOf(
            "Coconut Share",
            "Watering Hole",
            "Acacia Leaves",
            "Zebra Herd",
            "Savannah Breeze",
            "Sunset Grazing",
            "Pride Rock Trial",
            "Sandy Paths",
            "Safari Scout",
            "Tembo's Triumph"
        ),
        lessonObjectives = listOf(
            "Take away 1 or 2 sweet coconuts",
            "Find how many animals leave the pool (from 6)",
            "Subtract leaves dropping from branches (from 8)",
            "Count how many zebras remain (from 10)",
            "Take away numbers up to 12",
            "Solve sunset grazing differences up to 14",
            "Pride Rock subtraction challenges up to 16",
            "Find remaining treats up to 18",
            "Master big subtraction differences up to 20",
            "Solve the Great Savannah subtraction quest!"
        )
    ),
    BiomeTrailConfig(
        topic = MathTopic.MULTIPLICATION,
        biomeName = "Multiplication Canopy",
        landmarkEmoji = "🌴",
        companionEmoji = "🦒",
        companionName = "Twiga",
        environmentVibe = "Sky-high suspension bridges, mist & tropical canopies",
        requiredStarsToUnlock = 30,
        themeColorHex = 0xFF6A1B9A,      // Royal Purple
        secondaryColorHex = 0xFF8E24AA,
        pathBorderColorHex = 0xFFCE93D8,
        pathFillColorHex = 0xFFF3E5F5,
        decorList = listOf(
            DecorItem("🍌", 0.85f, 85),
            DecorItem("🦜", 0.15f, 215),
            DecorItem("☁️", 0.84f, 385),
            DecorItem("🌺", 0.12f, 535),
            DecorItem("🐒", 0.86f, 695),
            DecorItem("✨", 0.16f, 815)
        ),
        lessonTitles = listOf(
            "Twiga's Pairs",
            "Trio Parrots",
            "Vine Bunches",
            "High-Five Hands",
            "Treetop Arrays",
            "Cloud Platforms",
            "Canopy Patterns",
            "Skybridge Arrays",
            "Harpy Eagle",
            "Golden Peak Idol"
        ),
        lessonObjectives = listOf(
            "Multiply in groups of 2 (2 x N)",
            "Multiply in groups of 3 (3 x N)",
            "Multiply in groups of 4 (4 x N)",
            "Multiply by 5s like monkey hands (5 x N)",
            "Multiply 2s, 3s, and 5s in treetop arrays",
            "Groups of 3 and 4 across skybridges",
            "Multiply arrays up to 5 x 5",
            "Groups of 6 in the high emergent trees",
            "Master multiplication up to 8 x 5",
            "Ultimate Grand Multiplication Peak Master!"
        )
    )
)

/**
 * X-positions (% of width) for the 10 lesson stepping stones creating a natural winding trail.
 */
val LESSON_X_PERCENTAGES = listOf(
    0.50f, // Lesson 1: Entrance / Center
    0.28f, // Lesson 2: Swings Left
    0.20f, // Lesson 3: Deep Left
    0.44f, // Lesson 4: Center-Left
    0.74f, // Lesson 5: Swings Right
    0.80f, // Lesson 6: Deep Right
    0.54f, // Lesson 7: Center-Right
    0.26f, // Lesson 8: Swings Left
    0.34f, // Lesson 9: Pre-Boss Center-Left
    0.50f  // Lesson 10: Biome Peak / Center
)

/**
 * Selected lesson payload for popup details
 */
data class SelectedLessonData(
    val config: BiomeTrailConfig,
    val levelNumber: Int,
    val title: String,
    val objective: String,
    val starsEarned: Int,
    val highScore: Int,
    val isUnlocked: Boolean
)

/**
 * Interactive visual Safari Map component displaying a winding jungle trail,
 * tracking user's progress through math biomes as they complete individual lessons.
 */
@Composable
fun InteractiveSafariMap(
    levels: List<LevelProgressEntity>,
    onSelectTopic: (MathTopic) -> Unit,
    onStartLesson: (MathTopic, Int, DifficultyLevel) -> Unit,
    onNavigateToEncyclopedia: (MathTopic?) -> Unit,
    companionAvatar: String = "🦁",
    onTriggerEncouragement: ((MathTopic, Int) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val totalStars = remember(levels) { levels.sumOf { it.starsEarned } }
    val totalCompletedLessons = remember(levels) { levels.count { it.starsEarned > 0 } }

    var selectedBiomeFilter by remember { mutableStateOf<MathTopic?>(null) }
    var selectedLessonForModal by remember { mutableStateOf<SelectedLessonData?>(null) }
    var selectedDifficulty by remember { mutableStateOf(DifficultyLevel.MEDIUM) }

    // Pulsing animation for the current active lesson node
    val infiniteTransition = rememberInfiniteTransition(label = "safari_map_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.14f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val verticalScrollState = rememberScrollState()

    // Determine the biomes to render based on the filter
    val biomesToDisplay = remember(selectedBiomeFilter) {
        if (selectedBiomeFilter == null) {
            BIOME_TRAIL_CONFIGS
        } else {
            BIOME_TRAIL_CONFIGS.filter { it.topic == selectedBiomeFilter }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("interactive_safari_map_container")
    ) {
        // Sticky Header: Safari Progress HUD & Biome Filter
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 3.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Top HUD: Total Lessons & Stars
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "SAFARI EXPEDITION TRAIL 🧭",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AdventureOrange,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "$totalCompletedLessons / 40 Lessons Mastered",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = BananaYellow.copy(alpha = 0.25f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SafariGold)
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
                                    text = "$totalStars ⭐",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    color = JunglePrimary
                                )
                            }
                        }

                        // Encyclopedia shortcut button
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = JunglePrimary.copy(alpha = 0.12f),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onNavigateToEncyclopedia(selectedBiomeFilter) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "📖", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Wildlife",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = JunglePrimary
                                )
                            }
                        }

                        // Companion Cheers shortcut button
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SafariGold.copy(alpha = 0.2f),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    val completed = levels.filter { it.starsEarned > 0 }.maxByOrNull { it.levelNumber }
                                    val topic = if (completed != null) MathTopic.fromId(completed.areaTopic) else (selectedBiomeFilter ?: MathTopic.COUNTING)
                                    val lvl = completed?.levelNumber ?: 1
                                    onTriggerEncouragement?.invoke(topic, lvl)
                                }
                                .testTag("btn_map_companion_cheers")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "🐾", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Cheers",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = JunglePrimary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Biome Jump Navigation Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = selectedBiomeFilter == null,
                        onClick = { selectedBiomeFilter = null },
                        label = { Text("🗺️ All Trail (40)") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = JunglePrimary,
                            selectedLabelColor = Color.White
                        )
                    )

                    BIOME_TRAIL_CONFIGS.forEach { config ->
                        val topicLevels = levels.filter { it.areaTopic == config.topic.id }
                        val completedInBiome = topicLevels.count { it.starsEarned > 0 }
                        val isBiomeUnlocked = totalStars >= config.requiredStarsToUnlock

                        FilterChip(
                            selected = selectedBiomeFilter == config.topic,
                            onClick = { selectedBiomeFilter = config.topic },
                            label = {
                                Text(
                                    text = "${config.landmarkEmoji} ${config.biomeName} ($completedInBiome/10)${if (!isBiomeUnlocked) " 🔒" else ""}"
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(config.themeColorHex),
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // Scrollable Visual Trail Area
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(verticalScrollState)
                .padding(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            biomesToDisplay.forEachIndexed { biomeIndex, config ->
                BiomeTrailSection(
                    config = config,
                    levels = levels,
                    totalGlobalStars = totalStars,
                    pulseScale = pulseScale,
                    companionAvatar = companionAvatar,
                    onLessonClick = { lessonData ->
                        selectedLessonForModal = lessonData
                    },
                    onOpenEncyclopedia = { onNavigateToEncyclopedia(config.topic) },
                    onOpenTopicLevelSelect = { onSelectTopic(config.topic) }
                )

                // Gateway milestone landmark connecting biomes
                if (biomeIndex < biomesToDisplay.size - 1) {
                    val nextConfig = biomesToDisplay[biomeIndex + 1]
                    val isNextUnlocked = totalStars >= nextConfig.requiredStarsToUnlock
                    BiomeTransitionGateway(
                        fromName = config.biomeName,
                        toName = nextConfig.biomeName,
                        nextBiomeEmoji = nextConfig.landmarkEmoji,
                        isUnlocked = isNextUnlocked,
                        requiredStars = nextConfig.requiredStarsToUnlock
                    )
                }
            }

            // Grand Summit Peak Shrine at the end of the full expedition
            if (selectedBiomeFilter == null || selectedBiomeFilter == MathTopic.MULTIPLICATION) {
                GrandSummitPeakShrine(
                    totalStars = totalStars,
                    totalCompleted = totalCompletedLessons
                )
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }

    // Interactive Lesson Launch Modal
    selectedLessonForModal?.let { lessonData ->
        LessonLaunchDialog(
            lessonData = lessonData,
            selectedDifficulty = selectedDifficulty,
            onDifficultyChange = { selectedDifficulty = it },
            onStart = {
                selectedLessonForModal = null
                onStartLesson(lessonData.config.topic, lessonData.levelNumber, selectedDifficulty)
            },
            onViewEncouragement = if (lessonData.starsEarned > 0) {
                {
                    selectedLessonForModal = null
                    onTriggerEncouragement?.invoke(lessonData.config.topic, lessonData.levelNumber)
                }
            } else null,
            onDismiss = { selectedLessonForModal = null }
        )
    }
}

/**
 * Renders an entire 10-lesson Biome section with its background, winding canvas trail,
 * environmental decor, and lesson stepping stones.
 */
@Composable
private fun BiomeTrailSection(
    config: BiomeTrailConfig,
    levels: List<LevelProgressEntity>,
    totalGlobalStars: Int,
    pulseScale: Float,
    companionAvatar: String,
    onLessonClick: (SelectedLessonData) -> Unit,
    onOpenEncyclopedia: () -> Unit,
    onOpenTopicLevelSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val topicLevels = levels.filter { it.areaTopic == config.topic.id }
    val starsInBiome = topicLevels.sumOf { it.starsEarned }
    val completedCount = topicLevels.count { it.starsEarned > 0 }
    val isBiomeUnlocked = totalGlobalStars >= config.requiredStarsToUnlock
    val levelsMap = topicLevels.associateBy { it.levelNumber }

    // Find the current active lesson (first unlocked lesson with 0 stars, or next level)
    val currentActiveLevelNumber = remember(levelsMap, isBiomeUnlocked) {
        if (!isBiomeUnlocked) -1
        else {
            val uncompleted = (1..10).firstOrNull { lvl ->
                val record = levelsMap[lvl]
                (record?.isUnlocked == true || lvl == 1) && (record?.starsEarned ?: 0) == 0
            }
            uncompleted ?: 10 // If all completed, highlight level 10
        }
    }

    val themeColor = Color(config.themeColorHex)
    val secondaryColor = Color(config.secondaryColorHex)
    val pathBorderColor = Color(config.pathBorderColorHex)
    val pathFillColor = Color(config.pathFillColorHex)

    // Vertical spacing between lessons in dp
    val stepHeightDp = 92
    val totalTrailHeightDp = (10 * stepHeightDp) + 60

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {
        // Biome Header Banner Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .clickable { onOpenTopicLevelSelect() }
                .testTag("biome_banner_${config.topic.id}"),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = themeColor),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = config.landmarkEmoji, fontSize = 28.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = config.biomeName.uppercase(),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BananaYellow,
                                    letterSpacing = 1.sp
                                )
                                if (!isBiomeUnlocked) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color.Black.copy(alpha = 0.3f)
                                    ) {
                                        Text(
                                            text = "🔒 Req ${config.requiredStarsToUnlock}⭐",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "${config.topic.animalName} • ${config.topic.description}",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.95f),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Wildlife Encyclopedia link
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onOpenEncyclopedia() }
                    ) {
                        Text(
                            text = "🦁 Animals",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Progress Bar in Biome
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "$completedCount of 10 Lessons Mastered",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "$starsInBiome / 30 ⭐",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = BananaYellow
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = { (completedCount / 10f).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = SafariGold,
                    trackColor = Color.White.copy(alpha = 0.3f),
                    strokeCap = StrokeCap.Round
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Visual Canvas Trail Container
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(totalTrailHeightDp.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            pathFillColor.copy(alpha = 0.45f),
                            Color.White.copy(alpha = 0.85f),
                            pathFillColor.copy(alpha = 0.35f)
                        )
                    )
                )
                .border(2.dp, pathBorderColor.copy(alpha = 0.6f), RoundedCornerShape(26.dp))
        ) {
            val widthPx = constraints.maxWidth.toFloat()
            val density = LocalDensity.current.density

            // Compute coordinates for all 10 lesson points
            val lessonPoints = remember(widthPx, density) {
                (0 until 10).map { index ->
                    val xPercent = LESSON_X_PERCENTAGES[index]
                    val yDp = 30 + (index * stepHeightDp)
                    Offset(
                        x = widthPx * xPercent,
                        y = yDp * density
                    )
                }
            }

            // Draw winding road path on Canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                if (lessonPoints.isNotEmpty()) {
                    val roadPath = Path()
                    roadPath.moveTo(lessonPoints[0].x, lessonPoints[0].y)

                    for (i in 0 until lessonPoints.size - 1) {
                        val current = lessonPoints[i]
                        val next = lessonPoints[i + 1]
                        val midY = (current.y + next.y) / 2f

                        // Smooth cubic curve between adjacent stepping stones
                        roadPath.cubicTo(
                            current.x, midY,
                            next.x, midY,
                            next.x, next.y
                        )
                    }

                    // 1. Wide outer trail border
                    drawPath(
                        path = roadPath,
                        color = pathBorderColor,
                        style = Stroke(
                            width = 24.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )

                    // 2. Inner trail surface
                    drawPath(
                        path = roadPath,
                        color = Color.White,
                        style = Stroke(
                            width = 16.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )

                    // 3. Dashed golden stepping-stones center line
                    drawPath(
                        path = roadPath,
                        color = if (isBiomeUnlocked) secondaryColor else Color.LightGray,
                        style = Stroke(
                            width = 6.dp.toPx(),
                            cap = StrokeCap.Round,
                            pathEffect = PathEffect.dashPathEffect(
                                floatArrayOf(16.dp.toPx(), 14.dp.toPx()), 0f
                            )
                        )
                    )
                }
            }

            // Render environmental decor along the path
            config.decorList.forEach { decor ->
                val decorX = (maxWidth.value * decor.xPercent).roundToInt() - 14
                Box(
                    modifier = Modifier
                        .offset { IntOffset(decorX.dp.roundToPx(), decor.yOffsetDp.dp.roundToPx()) }
                ) {
                    Text(text = decor.emoji, fontSize = 24.sp)
                }
            }

            // Render 10 Lesson Stepping Stone Nodes
            (1..10).forEachIndexed { index, levelNumber ->
                val levelRecord = levelsMap[levelNumber]
                val stars = levelRecord?.starsEarned ?: 0
                val isUnlocked = isBiomeUnlocked && (levelRecord?.isUnlocked == true || levelNumber == 1)
                val isCompleted = stars > 0
                val isCurrentActive = isUnlocked && levelNumber == currentActiveLevelNumber
                val highScore = levelRecord?.highScore ?: 0

                val title = config.lessonTitles.getOrElse(index) { "Lesson $levelNumber" }
                val objective = config.lessonObjectives.getOrElse(index) { "Solve math questions" }

                // Position calculation: node is 62dp wide, so offset by 31dp to center
                val nodeX = (maxWidth.value * LESSON_X_PERCENTAGES[index]).roundToInt() - 31
                val nodeY = 30 + (index * stepHeightDp) - 31

                Box(
                    modifier = Modifier
                        .offset { IntOffset(nodeX.dp.roundToPx(), nodeY.dp.roundToPx()) }
                        .size(62.dp)
                ) {
                    // Pulsing Companion Avatar perched on active lesson
                    if (isCurrentActive) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .offset(y = (-24).dp)
                                .scale(pulseScale)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = BananaYellow,
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, SafariGold),
                                shadowElevation = 4.dp
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = companionAvatar, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "PLAY",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 10.sp,
                                        color = Color(0xFF3E2723)
                                    )
                                }
                            }
                        }
                    }

                    // Stepping Stone Node Circle
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(52.dp)
                            .scale(if (isCurrentActive) pulseScale else 1f)
                            .shadow(if (isUnlocked) 6.dp else 1.dp, CircleShape)
                            .clip(CircleShape)
                            .background(
                                when {
                                    !isUnlocked -> Color(0xFFE0E0E0)
                                    isCompleted -> themeColor
                                    else -> secondaryColor
                                }
                            )
                            .border(
                                width = if (isCurrentActive) 3.5.dp else 2.dp,
                                color = when {
                                    !isUnlocked -> Color.Gray
                                    isCurrentActive -> SafariGold
                                    isCompleted -> BananaYellow
                                    else -> Color.White
                                },
                                shape = CircleShape
                            )
                            .clickable {
                                onLessonClick(
                                    SelectedLessonData(
                                        config = config,
                                        levelNumber = levelNumber,
                                        title = title,
                                        objective = objective,
                                        starsEarned = stars,
                                        highScore = highScore,
                                        isUnlocked = isUnlocked
                                    )
                                )
                            }
                            .testTag("lesson_node_${config.topic.id}_$levelNumber"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!isUnlocked) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked Lesson",
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "$levelNumber",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    // Earned Stars under the Stepping Stone
                    if (isUnlocked && isCompleted) {
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .offset(y = 12.dp)
                                .background(Color.Black.copy(alpha = 0.65f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 4.dp, vertical = 1.dp),
                            horizontalArrangement = Arrangement.spacedBy(1.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            for (s in 1..3) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (s <= stars) StarGold else Color.Gray,
                                    modifier = Modifier.size(10.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Visual Gateway milestone landmark connecting biomes along the trail
 */
@Composable
private fun BiomeTransitionGateway(
    fromName: String,
    toName: String,
    nextBiomeEmoji: String,
    isUnlocked: Boolean,
    requiredStars: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .testTag("biome_gateway_${toName.lowercase().replace(" ", "_")}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) JunglePrimary else Color(0xFFEEEEEE)
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (isUnlocked) SafariGold.copy(alpha = 0.3f) else Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isUnlocked) nextBiomeEmoji else "🔒",
                        fontSize = 22.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (isUnlocked) "Bridge to $toName" else "Gateway to $toName",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        color = if (isUnlocked) Color.White else Color.DarkGray
                    )
                    Text(
                        text = if (isUnlocked) "Unlocked! Ready to explore" else "Requires $requiredStars ⭐ total to cross",
                        fontSize = 11.sp,
                        color = if (isUnlocked) BananaYellow else Color.Gray
                    )
                }
            }

            if (isUnlocked) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Cleared",
                    tint = BananaYellow
                )
            } else {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = AdventureOrange.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "$requiredStars ⭐",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = AdventureOrange,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

/**
 * Grand Summit Peak Shrine celebrating all 40 lessons mastered
 */
@Composable
private fun GrandSummitPeakShrine(
    totalStars: Int,
    totalCompleted: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .testTag("grand_summit_shrine"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SafariGold),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "🏆", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "GRAND SAFARI SUMMIT",
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                color = Color(0xFF3E2723),
                letterSpacing = 1.sp
            )
            Text(
                text = "The Legendary Golden Math Idol of the Jungle!",
                fontSize = 12.sp,
                color = Color(0xFF4E342E),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White.copy(alpha = 0.85f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$totalCompleted / 40 Lessons • $totalStars / 120 Stars ⭐",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFF3E2723)
                    )
                }
            }
        }
    }
}

/**
 * Modal dialog displaying detailed lesson objectives, difficulty options, and a direct Play button.
 */
@Composable
private fun LessonLaunchDialog(
    lessonData: SelectedLessonData,
    selectedDifficulty: DifficultyLevel,
    onDifficultyChange: (DifficultyLevel) -> Unit,
    onStart: () -> Unit,
    onViewEncouragement: (() -> Unit)? = null,
    onDismiss: () -> Unit
) {
    val themeColor = Color(lessonData.config.themeColorHex)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = null,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("lesson_launch_dialog")
            ) {
                // Header Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(themeColor, Color(lessonData.config.secondaryColorHex))
                            )
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (lessonData.isUnlocked) "${lessonData.levelNumber}" else "🔒",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = themeColor
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "${lessonData.config.landmarkEmoji} ${lessonData.config.biomeName.uppercase()}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BananaYellow,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Lesson ${lessonData.levelNumber}: ${lessonData.title}",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (lessonData.isUnlocked) {
                    // Math Concept Objective
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🎯", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Safari Mission Objective:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = lessonData.objective,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Stars & Best Score record
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Current Record:",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                for (s in 1..3) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = if (s <= lessonData.starsEarned) StarGold else Color(0xFFE0E0E0),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        if (lessonData.highScore > 0) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = AdventureOrange.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "High Score: ${lessonData.highScore}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = AdventureOrange,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Difficulty Selector
                    Text(
                        text = "Select Expedition Challenge:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DifficultyLevel.entries.forEach { diff ->
                            val isSelected = selectedDifficulty == diff
                            FilterChip(
                                selected = isSelected,
                                onClick = { onDifficultyChange(diff) },
                                label = {
                                    Text(
                                        text = "${diff.emoji} ${diff.title}",
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = themeColor,
                                    selectedLabelColor = Color.White
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                } else {
                    // Locked Lesson Message
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFFFF3E0),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "🔒", fontSize = 32.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Path Locked!",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFFE65100)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Earn at least 1 star on Lesson ${lessonData.levelNumber - 1} to unlock this safari stepping stone!",
                                fontSize = 12.sp,
                                color = Color.DarkGray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Column(horizontalAlignment = Alignment.End) {
                if (onViewEncouragement != null) {
                    Button(
                        onClick = onViewEncouragement,
                        colors = ButtonDefaults.buttonColors(containerColor = SafariGold),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                            .testTag("btn_modal_companion_cheers")
                    ) {
                        Text(
                            text = "🐾 Companion Cheers!",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
                if (lessonData.isUnlocked) {
                    Button(
                        onClick = onStart,
                        colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_start_lesson_modal")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Start",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "START LESSON! 🚀",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Close", fontWeight = FontWeight.SemiBold)
            }
        }
    )
}
