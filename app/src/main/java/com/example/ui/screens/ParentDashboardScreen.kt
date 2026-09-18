package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.audio.JungleAudioTheme
import com.example.data.local.entities.ChildProfileEntity
import com.example.data.local.entities.UserStatsEntity
import com.example.data.model.MathTopic
import com.example.ui.components.CategoryComparativeBarChart
import com.example.ui.components.CategoryProgressItem
import com.example.ui.components.SafariTopBar
import com.example.ui.theme.AdventureOrange
import com.example.ui.theme.BananaYellow
import com.example.ui.theme.EncouragementGreen
import com.example.ui.theme.JunglePrimary
import com.example.ui.theme.SafariGold

@Composable
fun ParentDashboardScreen(
    profile: ChildProfileEntity,
    stats: UserStatsEntity,
    onUpdateProfile: (String, String, String) -> Unit,
    onUpdateSettings: (Boolean, Boolean, Boolean, Boolean) -> Unit,
    onResetProgress: () -> Unit,
    onBackClick: () -> Unit,
    currentAudioTheme: JungleAudioTheme = JungleAudioTheme.SERENE_RIVER,
    musicVolume: Float = 0.45f,
    onSelectAudioTheme: (JungleAudioTheme) -> Unit = {},
    onSetMusicVolume: (Float) -> Unit = {},
    onNavigateToProgress: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var nicknameInput by remember { mutableStateOf(profile.nickname) }
    var ageGroup by remember { mutableStateOf(profile.ageRange) }
    var soundOn by remember { mutableStateOf(profile.soundEnabled) }
    var voiceOn by remember { mutableStateOf(profile.voiceEnabled) }
    var musicOn by remember { mutableStateOf(profile.musicEnabled) }
    var cloudSync by remember { mutableStateOf(profile.isCloudSyncEnabled) }
    var showResetDialog by remember { mutableStateOf(false) }

    val totalAttempted = stats.totalAnswered
    val overallAccuracy = if (totalAttempted > 0) (stats.totalCorrect.toFloat() / totalAttempted * 100).toInt() else 0

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        SafariTopBar(
            title = "Parent & Educator Zone 👨‍👩‍👧",
            subtitle = "Learning analytics and settings",
            onBackClick = onBackClick,
            onMusicToggle = {
                val newMusic = !musicOn
                musicOn = newMusic
                onUpdateSettings(soundOn, voiceOn, newMusic, cloudSync)
            },
            isMusicOn = musicOn
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Overview Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Learning Progress",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Overall accuracy: $overallAccuracy%",
                                fontSize = 13.sp,
                                color = EncouragementGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = JunglePrimary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "$totalAttempted Questions",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = JunglePrimary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val chartItems = remember(stats) {
                        listOf(
                            CategoryProgressItem(
                                topic = MathTopic.COUNTING,
                                title = "Counting",
                                emoji = "🍌",
                                animalEmoji = "🐒",
                                animalName = "Kiki",
                                solvedCount = stats.countingCorrect,
                                totalCount = stats.countingTotal,
                                color = Color(0xFFFFA000)
                            ),
                            CategoryProgressItem(
                                topic = MathTopic.ADDITION,
                                title = "Addition",
                                emoji = "🍎",
                                animalEmoji = "🦁",
                                animalName = "Leo",
                                solvedCount = stats.additionCorrect,
                                totalCount = stats.additionTotal,
                                color = Color(0xFFE65100)
                            ),
                            CategoryProgressItem(
                                topic = MathTopic.SUBTRACTION,
                                title = "Subtraction",
                                emoji = "🥥",
                                animalEmoji = "🐘",
                                animalName = "Tembo",
                                solvedCount = stats.subtractionCorrect,
                                totalCount = stats.subtractionTotal,
                                color = Color(0xFF1976D2)
                            ),
                            CategoryProgressItem(
                                topic = MathTopic.MULTIPLICATION,
                                title = "Multiplication",
                                emoji = "🌿",
                                animalEmoji = "🦒",
                                animalName = "Twiga",
                                solvedCount = stats.multiplicationCorrect,
                                totalCount = stats.multiplicationTotal,
                                color = Color(0xFF2E7D32)
                            )
                        )
                    }

                    // Visual Compose Graphics Chart
                    CategoryComparativeBarChart(items = chartItems)

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onNavigateToProgress,
                        colors = ButtonDefaults.buttonColors(containerColor = JunglePrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("open_progress_analytics_button")
                    ) {
                        Text(
                            text = "Open Full Visual Analytics & Donut Charts 📊 ➜",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Subject Accuracy Breakdown Bars
                    SkillAccuracyBar(label = "🍌 Counting (Grove)", correct = stats.countingCorrect, total = stats.countingTotal, color = Color(0xFFF9A825))
                    Spacer(modifier = Modifier.height(8.dp))
                    SkillAccuracyBar(label = "🦁 Addition (Valley)", correct = stats.additionCorrect, total = stats.additionTotal, color = Color(0xFFE65100))
                    Spacer(modifier = Modifier.height(8.dp))
                    SkillAccuracyBar(label = "🐘 Subtraction (Plains)", correct = stats.subtractionCorrect, total = stats.subtractionTotal, color = Color(0xFF1565C0))
                    Spacer(modifier = Modifier.height(8.dp))
                    SkillAccuracyBar(label = "🦒 Multiplication (Hills)", correct = stats.multiplicationCorrect, total = stats.multiplicationTotal, color = Color(0xFF2E7D32))
                }
            }

            // Pedagogical Recommendation Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SafariGold.copy(alpha = 0.15f))
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
                            .background(SafariGold.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = "Tutor Recommendation", tint = Color(0xFFE65100))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Learning Recommendation",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFFE65100)
                        )
                        val rec = when {
                            stats.countingTotal < 5 -> "Start by exploring Banana Grove to build early counting confidence."
                            stats.subtractionTotal == 0 -> "Try Elephant Plains next! Tembo introduces sharing and taking away."
                            stats.additionTotal == 0 -> "Ready for Lion Valley! Practice simple sums with Leo."
                            else -> "Great math variety! Encourage daily 5-question quests to maintain mastery."
                        }
                        Text(
                            text = rec,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Child Profile Edit
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Child Profile",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = nicknameInput,
                        onValueChange = {
                            nicknameInput = it
                            onUpdateProfile(it, ageGroup, profile.avatarEmoji)
                        },
                        label = { Text("Child's Explorer Name") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("parent_child_name_input")
                    )
                }
            }

            // Preferences / Switches
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Audio & Settings",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    SettingToggleRow(
                        title = "Sound Effects",
                        subtitle = "Playful game chimes and success tones",
                        checked = soundOn,
                        onCheckedChange = {
                            soundOn = it
                            onUpdateSettings(it, voiceOn, musicOn, cloudSync)
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    SettingToggleRow(
                        title = "Ambient Jungle Music",
                        subtitle = "Calming jungle nature sounds & light background music",
                        checked = musicOn,
                        onCheckedChange = {
                            musicOn = it
                            onUpdateSettings(soundOn, voiceOn, it, cloudSync)
                        }
                    )

                    if (musicOn) {
                        Spacer(modifier = Modifier.height(8.dp))
                        // Volume Slider
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Music & Nature Volume",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "${(musicVolume * 100).toInt()}%",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = JunglePrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Slider(
                                value = musicVolume,
                                onValueChange = { onSetMusicVolume(it) },
                                valueRange = 0.1f..1.0f,
                                steps = 9,
                                colors = SliderDefaults.colors(
                                    thumbColor = JunglePrimary,
                                    activeTrackColor = JunglePrimary
                                ),
                                modifier = Modifier.testTag("music_volume_slider")
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        // Soundscape Selector
                        Text(
                            text = "Jungle Soundscape Style",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            JungleAudioTheme.entries.forEach { theme ->
                                val isSelected = currentAudioTheme == theme
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (isSelected) JunglePrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    border = if (isSelected) BorderStroke(1.5.dp, JunglePrimary) else null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable { onSelectAudioTheme(theme) }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = theme.emoji, fontSize = 24.sp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = theme.title,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = if (isSelected) JunglePrimary else MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = theme.description,
                                                fontSize = 11.sp,
                                                color = Color.Gray,
                                                lineHeight = 14.sp
                                            )
                                        }
                                        if (isSelected) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(text = "✓", color = JunglePrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    SettingToggleRow(
                        title = "Voice Narration",
                        subtitle = "Reads math questions aloud for young readers",
                        checked = voiceOn,
                        onCheckedChange = {
                            voiceOn = it
                            onUpdateSettings(soundOn, it, musicOn, cloudSync)
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    SettingToggleRow(
                        title = "Cloud Sync & Backup",
                        subtitle = "Keep badges and progress saved safely",
                        checked = cloudSync,
                        onCheckedChange = {
                            cloudSync = it
                            onUpdateSettings(soundOn, voiceOn, musicOn, it)
                        }
                    )
                }
            }

            // Child Safety & Privacy notice
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Safety",
                        tint = JunglePrimary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Child Safe & Privacy First",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = JunglePrimary
                        )
                        Text(
                            text = "No advertising, no in-app purchases, no external web links. AI tutoring is strictly age-appropriate and does not store personal data.",
                            fontSize = 11.sp,
                            color = Color.DarkGray,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Reset Data Button
            Button(
                onClick = { showResetDialog = true },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reset_progress_button")
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reset All Safari Progress")
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset All Progress?") },
            text = { Text("Are you sure? This will reset all earned coins, level stars, and badges.") },
            confirmButton = {
                Button(
                    onClick = {
                        onResetProgress()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Reset")
                }
            },
            dismissButton = {
                Button(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SkillAccuracyBar(label: String, correct: Int, total: Int, color: Color) {
    val pct = if (total > 0) (correct.toFloat() / total * 100).toInt() else 0
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(text = "$correct/$total ($pct%)", fontSize = 12.sp, color = Color.Gray)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { if (total > 0) correct.toFloat() / total else 0f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = Color.LightGray.copy(alpha = 0.3f)
        )
    }
}

@Composable
private fun SettingToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = subtitle, fontSize = 11.sp, color = Color.Gray)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = JunglePrimary)
        )
    }
}
