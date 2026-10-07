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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.example.data.local.entities.LevelProgressEntity
import com.example.data.model.DifficultyLevel
import com.example.data.model.MathTopic
import com.example.ui.components.SafariTopBar
import com.example.ui.theme.BananaYellow
import com.example.ui.theme.JunglePrimary
import com.example.ui.theme.StarGold

@Composable
fun LevelSelectScreen(
    topic: MathTopic,
    levels: List<LevelProgressEntity>,
    initialDifficulty: DifficultyLevel = DifficultyLevel.MEDIUM,
    onSelectLevel: (Int, DifficultyLevel) -> Unit,
    onBackClick: () -> Unit,
    isMusicOn: Boolean = true,
    onMusicToggle: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedDifficulty by remember { mutableStateOf(initialDifficulty) }
    val topicColor = Color(topic.colorHex)
    val levelsMap = levels.filter { it.areaTopic == topic.id }.associateBy { it.levelNumber }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SafariTopBar(
            title = topic.title,
            subtitle = "${topic.animalTitle} • ${topic.animalName}",
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
            // Mascot Guidance Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = topicColor.copy(alpha = 0.12f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = topic.animalEmoji, fontSize = 28.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "${topic.animalName} says:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = topicColor
                        )
                        Text(
                            text = "\"${topic.description}\"",
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Difficulty Selector Component (Easy, Medium, Hard)
            DifficultySelectorCard(
                selectedDifficulty = selectedDifficulty,
                onSelectDifficulty = { selectedDifficulty = it },
                topicColor = topicColor
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Responsive Adaptive Grid of 10 levels
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 145.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items((1..10).toList()) { levelNum ->
                    val levelRecord = levelsMap[levelNum]
                    val isUnlocked = levelRecord?.isUnlocked ?: (levelNum == 1)
                    val stars = levelRecord?.starsEarned ?: 0

                    LevelGridItem(
                        levelNumber = levelNum,
                        isUnlocked = isUnlocked,
                        stars = stars,
                        topicColor = topicColor,
                        difficulty = selectedDifficulty,
                        onClick = {
                            if (isUnlocked) {
                                onSelectLevel(levelNum, selectedDifficulty)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DifficultySelectorCard(
    selectedDifficulty: DifficultyLevel,
    onSelectDifficulty: (DifficultyLevel) -> Unit,
    topicColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🎯", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "CHALLENGE LEVEL",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Black,
                        color = Color.Gray,
                        letterSpacing = 1.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(selectedDifficulty.colorHex).copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "${(selectedDifficulty.scoreMultiplier * 100).toInt()}% Coins",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(selectedDifficulty.colorHex),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3 Difficulty buttons (Easy, Medium, Hard)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DifficultyLevel.entries.forEach { diff ->
                    val isSelected = (diff == selectedDifficulty)
                    val diffColor = Color(diff.colorHex)
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onSelectDifficulty(diff) }
                            .testTag("difficulty_button_${diff.id}"),
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) diffColor else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(text = diff.emoji, fontSize = 15.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = diff.title,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Dynamic Description Box explaining what changes
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(selectedDifficulty.colorHex).copy(alpha = 0.08f)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${selectedDifficulty.emoji} ${selectedDifficulty.subtitle}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(selectedDifficulty.colorHex)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = selectedDifficulty.description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun LevelGridItem(
    levelNumber: Int,
    isUnlocked: Boolean,
    stars: Int,
    topicColor: Color,
    difficulty: DifficultyLevel = DifficultyLevel.MEDIUM,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .clickable(enabled = isUnlocked) { onClick() }
            .testTag("level_button_$levelNumber"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) MaterialTheme.colorScheme.surface else Color(0xFFEEEEEE)
        ),
        elevation = CardDefaults.cardElevation(if (isUnlocked) 3.dp else 0.dp),
        border = if (isUnlocked && stars > 0) {
            CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(topicColor))
        } else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isUnlocked) {
                // Circle with level number
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(topicColor.copy(alpha = 0.15f))
                        .border(2.dp, topicColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$levelNumber",
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp,
                        color = topicColor
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Level $levelNumber",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${difficulty.emoji} ${difficulty.title}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(difficulty.colorHex)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Stars Display
                Row(horizontalArrangement = Arrangement.Center) {
                    for (i in 1..3) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Star $i",
                            tint = if (i <= stars) StarGold else Color.LightGray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            } else {
                // Locked state
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Level $levelNumber",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Locked 🔒",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

