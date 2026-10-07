package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.ChildProfileEntity
import com.example.data.local.entities.UserStatsEntity
import com.example.data.model.AnimalSticker
import com.example.data.model.MathTopic
import com.example.ui.components.CategoryComparativeBarChart
import com.example.ui.components.CategoryDonutDistributionChart
import com.example.ui.components.CategoryMasteryRingsChart
import com.example.ui.components.CategoryProgressItem
import com.example.ui.components.SafariTopBar
import com.example.ui.theme.AdventureOrange
import com.example.ui.theme.BananaYellow
import com.example.ui.theme.EncouragementGreen
import com.example.ui.theme.JunglePrimary
import com.example.ui.theme.RiverBlue
import com.example.ui.theme.SafariGold

enum class ProgressChartViewMode(val label: String, val emoji: String) {
    BAR_CHART("Bar Comparison", "📊"),
    DONUT_DISTRIBUTION("Donut Share", "🍩"),
    MASTERY_RINGS("Mastery Rings", "🎯")
}

@Composable
fun ProgressTrackingScreen(
    profile: ChildProfileEntity,
    stats: UserStatsEntity,
    stickers: List<AnimalSticker> = emptyList(),
    onBackClick: () -> Unit,
    onPracticeTopic: (MathTopic) -> Unit,
    onMusicToggle: () -> Unit,
    isMusicOn: Boolean = true,
    modifier: Modifier = Modifier
) {
    var selectedChartMode by remember { mutableStateOf(ProgressChartViewMode.BAR_CHART) }

    // Prepare Category Data Items
    val categoryItems = remember(stats) {
        listOf(
            CategoryProgressItem(
                topic = MathTopic.COUNTING,
                title = "Counting",
                emoji = "🍌",
                animalEmoji = "🐒",
                animalName = "Kiki",
                solvedCount = stats.countingCorrect,
                totalCount = stats.countingTotal,
                color = Color(0xFFFFA000) // Banana Amber
            ),
            CategoryProgressItem(
                topic = MathTopic.ADDITION,
                title = "Addition",
                emoji = "🍎",
                animalEmoji = "🦁",
                animalName = "Leo",
                solvedCount = stats.additionCorrect,
                totalCount = stats.additionTotal,
                color = Color(0xFFE65100) // Lion Orange
            ),
            CategoryProgressItem(
                topic = MathTopic.SUBTRACTION,
                title = "Subtraction",
                emoji = "🥥",
                animalEmoji = "🐘",
                animalName = "Tembo",
                solvedCount = stats.subtractionCorrect,
                totalCount = stats.subtractionTotal,
                color = Color(0xFF1976D2) // River Blue
            ),
            CategoryProgressItem(
                topic = MathTopic.MULTIPLICATION,
                title = "Multiplication",
                emoji = "🌿",
                animalEmoji = "🦒",
                animalName = "Twiga",
                solvedCount = stats.multiplicationCorrect,
                totalCount = stats.multiplicationTotal,
                color = Color(0xFF2E7D32) // Canopy Green
            )
        )
    }

    val totalSolved = stats.totalCorrect
    val totalAttempted = stats.totalAnswered
    val overallAccuracy = if (totalAttempted > 0) (totalSolved.toFloat() / totalAttempted * 100).toInt() else 0

    // Next sticker milestone target
    val nextSticker = stickers.firstOrNull { !it.isUnlocked }
    val problemsUntilNextSticker = nextSticker?.let { (it.requiredProblems - totalSolved).coerceAtLeast(0) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val isWideScreen = maxWidth >= 740.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 1150.dp)
            ) {
                SafariTopBar(
                    title = "Math Progress & Skills 📊",
                    subtitle = "Visual breakdown across counting, addition & more",
                    onBackClick = onBackClick,
                    onMusicToggle = onMusicToggle,
                    isMusicOn = isMusicOn
                )
            }

            if (isWideScreen) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 1150.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    // Left Column: Child Header, KPIs, Chart
                    Column(
                        modifier = Modifier.weight(1.1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Child Explorer Header Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(containerColor = JunglePrimary),
                            elevation = CardDefaults.cardElevation(3.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.25f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = profile.avatarEmoji, fontSize = 32.sp)
                                    }
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column {
                                        Text(
                                            text = "${profile.nickname}'s Journey",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "Age ${profile.ageRange} Explorer • Overall Accuracy: $overallAccuracy%",
                                            fontSize = 12.sp,
                                            color = Color.White.copy(alpha = 0.9f)
                                        )
                                    }
                                }
                            }
                        }

                        // High-Level KPI Summary Tiles
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            KpiMetricCard(
                                emoji = "✅",
                                label = "Solved",
                                value = "$totalSolved",
                                color = EncouragementGreen,
                                modifier = Modifier.weight(1f)
                            )
                            KpiMetricCard(
                                emoji = "🎯",
                                label = "Accuracy",
                                value = "$overallAccuracy%",
                                color = AdventureOrange,
                                modifier = Modifier.weight(1f)
                            )
                            KpiMetricCard(
                                emoji = "⭐",
                                label = "Stars",
                                value = "${stats.totalStars}",
                                color = SafariGold,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Chart View Mode Selector Chips
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Select Chart Visualization",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ProgressChartViewMode.entries.forEach { mode ->
                                    val isSelected = (selectedChartMode == mode)
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { selectedChartMode = mode },
                                        label = {
                                            Text(
                                                text = "${mode.emoji} ${mode.label}",
                                                fontSize = 12.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = JunglePrimary,
                                            selectedLabelColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.testTag("chart_mode_${mode.name.lowercase()}")
                                    )
                                }
                            }
                        }

                        // Visual Chart Display
                        when (selectedChartMode) {
                            ProgressChartViewMode.BAR_CHART -> {
                                CategoryComparativeBarChart(
                                    items = categoryItems,
                                    onItemSelected = { }
                                )
                            }
                            ProgressChartViewMode.DONUT_DISTRIBUTION -> {
                                CategoryDonutDistributionChart(
                                    items = categoryItems,
                                    onItemSelected = { }
                                )
                            }
                            ProgressChartViewMode.MASTERY_RINGS -> {
                                CategoryMasteryRingsChart(
                                    items = categoryItems,
                                    masteryGoal = 30
                                )
                            }
                        }
                    }

                    // Right Column: Insights & Category Details
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Pedagogical Insights & Next Goal Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(JunglePrimary.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "Learning Insight",
                                        tint = JunglePrimary
                                    )
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Learning Insights & Next Milestone",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    val topCategory = categoryItems.maxByOrNull { it.solvedCount }
                                    val insightText = if (topCategory != null && topCategory.solvedCount > 0) {
                                        "Strongest area: ${topCategory.emoji} ${topCategory.title} with ${topCategory.solvedCount} problems solved! "
                                    } else {
                                        "Start an adventure in Banana Grove to launch your math progress! "
                                    }
                                    val milestoneText = if (nextSticker != null && problemsUntilNextSticker != null) {
                                        if (problemsUntilNextSticker == 0) {
                                            "Great work! You've qualified for the ${nextSticker.name} sticker! 🐾"
                                        } else {
                                            "Solve $problemsUntilNextSticker more problems to unlock the ${nextSticker.name} sticker ${nextSticker.animalEmoji}!"
                                        }
                                    } else {
                                        "Incredible! You have unlocked all animal stickers in the safari album! 💎"
                                    }
                                    Text(
                                        text = "$insightText$milestoneText",
                                        fontSize = 12.sp,
                                        color = Color.Gray,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }

                        Text(
                            text = "Detailed Category Breakdown",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        categoryItems.forEach { item ->
                            CategoryDetailCard(
                                item = item,
                                onPracticeClick = { onPracticeTopic(item.topic) }
                            )
                        }
                    }
                }
            } else {
                // Mobile layout
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Child Explorer Header Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = JunglePrimary),
                        elevation = CardDefaults.cardElevation(3.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.25f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = profile.avatarEmoji, fontSize = 32.sp)
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text(
                                        text = "${profile.nickname}'s Journey",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Age ${profile.ageRange} Explorer • Overall Accuracy: $overallAccuracy%",
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                }
                            }
                        }
                    }

                    // High-Level KPI Summary Tiles
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        KpiMetricCard(
                            emoji = "✅",
                            label = "Solved",
                            value = "$totalSolved",
                            color = EncouragementGreen,
                            modifier = Modifier.weight(1f)
                        )
                        KpiMetricCard(
                            emoji = "🎯",
                            label = "Accuracy",
                            value = "$overallAccuracy%",
                            color = AdventureOrange,
                            modifier = Modifier.weight(1f)
                        )
                        KpiMetricCard(
                            emoji = "⭐",
                            label = "Stars",
                            value = "${stats.totalStars}",
                            color = SafariGold,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Chart View Mode Selector Chips
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Select Chart Visualization",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ProgressChartViewMode.entries.forEach { mode ->
                                val isSelected = (selectedChartMode == mode)
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedChartMode = mode },
                                    label = {
                                        Text(
                                            text = "${mode.emoji} ${mode.label}",
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = JunglePrimary,
                                        selectedLabelColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.testTag("chart_mode_${mode.name.lowercase()}")
                                )
                            }
                        }
                    }

                    // Visual Chart Display
                    when (selectedChartMode) {
                        ProgressChartViewMode.BAR_CHART -> {
                            CategoryComparativeBarChart(
                                items = categoryItems,
                                onItemSelected = { }
                            )
                        }
                        ProgressChartViewMode.DONUT_DISTRIBUTION -> {
                            CategoryDonutDistributionChart(
                                items = categoryItems,
                                onItemSelected = { }
                            )
                        }
                        ProgressChartViewMode.MASTERY_RINGS -> {
                            CategoryMasteryRingsChart(
                                items = categoryItems,
                                masteryGoal = 30
                            )
                        }
                    }

                    // Pedagogical Insights & Next Goal Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(JunglePrimary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Learning Insight",
                                    tint = JunglePrimary
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Learning Insights & Next Milestone",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                val topCategory = categoryItems.maxByOrNull { it.solvedCount }
                                val insightText = if (topCategory != null && topCategory.solvedCount > 0) {
                                    "Strongest area: ${topCategory.emoji} ${topCategory.title} with ${topCategory.solvedCount} problems solved! "
                                } else {
                                    "Start an adventure in Banana Grove to launch your math progress! "
                                }
                                val milestoneText = if (nextSticker != null && problemsUntilNextSticker != null) {
                                    if (problemsUntilNextSticker == 0) {
                                        "Great work! You've qualified for the ${nextSticker.name} sticker! 🐾"
                                    } else {
                                        "Solve $problemsUntilNextSticker more problems to unlock the ${nextSticker.name} sticker ${nextSticker.animalEmoji}!"
                                    }
                                } else {
                                    "Incredible! You have unlocked all animal stickers in the safari album! 💎"
                                }
                                Text(
                                    text = "$insightText$milestoneText",
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }

                    // Detailed Category Breakdown Cards
                    Text(
                        text = "Detailed Category Breakdown",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    categoryItems.forEach { item ->
                        CategoryDetailCard(
                            item = item,
                            onPracticeClick = { onPracticeTopic(item.topic) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun KpiMetricCard(
    emoji: String,
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = emoji, fontSize = 22.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = color
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = Color.Gray,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun CategoryDetailCard(
    item: CategoryProgressItem,
    onPracticeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(item.color.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = item.animalEmoji, fontSize = 24.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${item.emoji} ${item.title}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "• ${item.animalName}",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                        Text(
                            text = item.masteryTier,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = item.color
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = item.color.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "${item.solvedCount} Solved",
                        color = item.color,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${item.solvedCount} correct / ${item.totalCount} total attempted",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = "${item.accuracyPct}% Accuracy",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = item.color
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { if (item.totalCount > 0) (item.solvedCount.toFloat() / item.totalCount).coerceIn(0f, 1f) else 0f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = item.color,
                trackColor = item.color.copy(alpha = 0.15f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Practice Button
            Button(
                onClick = onPracticeClick,
                colors = ButtonDefaults.buttonColors(containerColor = item.color),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("practice_button_${item.topic.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Practice ${item.title}",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Practice ${item.title} Now",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}
