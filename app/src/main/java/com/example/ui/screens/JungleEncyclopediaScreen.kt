package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.LevelProgressEntity
import com.example.data.model.JungleAnimalEntry
import com.example.data.model.JungleEncyclopediaData
import com.example.data.model.MathTopic
import com.example.ui.components.SafariTopBar
import com.example.ui.theme.AdventureOrange
import com.example.ui.theme.BananaYellow
import com.example.ui.theme.EncouragementGreen
import com.example.ui.theme.JunglePrimary
import com.example.ui.theme.SafariGold
import com.example.ui.theme.StarGold

/**
 * Jungle Encyclopedia Screen
 *
 * Populates with fun educational facts about real jungle animals as the child
 * progresses through different mathematical zones (Counting Creek, Addition Woods, etc.).
 */
@Composable
fun JungleEncyclopediaScreen(
    levels: List<LevelProgressEntity>,
    initialZoneFilter: MathTopic? = null,
    onSpeakAnimalFact: (JungleAnimalEntry) -> Unit,
    onNavigateToZone: (MathTopic) -> Unit,
    onBackClick: () -> Unit,
    isMusicOn: Boolean = true,
    onMusicToggle: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Populate animals with computed unlock statuses based on levels/stars
    val animals = remember(levels) {
        val totalStars = levels.sumOf { it.starsEarned }
        JungleEncyclopediaData.getPopulatedEncyclopedia(levels, totalStars)
    }

    var selectedZoneFilter by remember { mutableStateOf<MathTopic?>(initialZoneFilter) }
    var selectedAnimalForDetail by remember { mutableStateOf<JungleAnimalEntry?>(null) }
    var onlyShowUnlocked by remember { mutableStateOf(false) }

    val filteredAnimals = remember(animals, selectedZoneFilter, onlyShowUnlocked) {
        animals.filter { animal ->
            (selectedZoneFilter == null || animal.zone == selectedZoneFilter) &&
                    (!onlyShowUnlocked || animal.isUnlocked)
        }
    }

    val totalUnlocked = animals.count { it.isUnlocked }
    val totalAnimals = animals.size

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SafariTopBar(
            title = "Jungle Encyclopedia 📖",
            subtitle = "Real animal facts & wildlife discoveries",
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
                columns = GridCells.Adaptive(minSize = 320.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Milestone Discovery Progress Bar Banner
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("encyclopedia_progress_card"),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = JunglePrimary),
                        elevation = CardDefaults.cardElevation(3.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "WILDLIFE DISCOVERY",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BananaYellow,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = "$totalUnlocked of $totalAnimals Animals Found!",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color.Black.copy(alpha = 0.25f)
                                ) {
                                    Text(
                                        text = "${((totalUnlocked.toFloat() / totalAnimals) * 100).toInt()}% Explored",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = BananaYellow,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            LinearProgressIndicator(
                                progress = { (totalUnlocked.toFloat() / totalAnimals).coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(5.dp)),
                                color = SafariGold,
                                trackColor = Color.White.copy(alpha = 0.25f),
                                strokeCap = StrokeCap.Round
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Solve math problems and earn stars in each zone to uncover new jungle wildlife!",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }

                // Zone Category Filter Chips
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedZoneFilter == null,
                            onClick = { selectedZoneFilter = null },
                            label = { Text("All Zones ($totalAnimals)") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = JunglePrimary,
                                selectedLabelColor = Color.White
                            )
                        )

                        MathTopic.entries.forEach { topic ->
                            val zoneName = when (topic) {
                                MathTopic.COUNTING -> "Counting Creek"
                                MathTopic.ADDITION -> "Addition Woods"
                                MathTopic.SUBTRACTION -> "Subtraction Savannah"
                                MathTopic.MULTIPLICATION -> "Multiplication Canopy"
                            }
                            val countInZone = animals.count { it.zone == topic && it.isUnlocked }

                            FilterChip(
                                selected = selectedZoneFilter == topic,
                                onClick = { selectedZoneFilter = topic },
                                label = { Text("${topic.animalEmoji} $zoneName ($countInZone/4)") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(topic.colorHex),
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }

                // Encyclopedia Animal Cards List
                items(filteredAnimals) { animal ->
                    EncyclopediaAnimalCard(
                        animal = animal,
                        onCardClick = {
                            if (animal.isUnlocked) {
                                selectedAnimalForDetail = animal
                            }
                        },
                        onSpeakFact = { onSpeakAnimalFact(animal) },
                        onNavigateToZone = { onNavigateToZone(animal.zone) }
                    )
                }

                item(span = { GridItemSpan(maxLineSpan) }) {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    // High-Detail Animal Trivia Dialog
    selectedAnimalForDetail?.let { animal ->
        AnimalFactsheetDialog(
            animal = animal,
            onDismiss = { selectedAnimalForDetail = null },
            onSpeak = { onSpeakAnimalFact(animal) }
        )
    }
}

/**
 * Individual animal facts card showing locked / unlocked states, fun facts, and read-aloud buttons.
 */
@Composable
private fun EncyclopediaAnimalCard(
    animal: JungleAnimalEntry,
    onCardClick: () -> Unit,
    onSpeakFact: () -> Unit,
    onNavigateToZone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val zoneColor = Color(animal.zone.colorHex)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(enabled = animal.isUnlocked) { onCardClick() }
            .testTag("encyclopedia_card_${animal.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (animal.isUnlocked) MaterialTheme.colorScheme.surface else Color(0xFFF5F5F5)
        ),
        elevation = CardDefaults.cardElevation(if (animal.isUnlocked) 3.dp else 1.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (animal.isUnlocked) 1.5.dp else 1.dp,
            color = if (animal.isUnlocked) zoneColor.copy(alpha = 0.4f) else Color(0xFFE0E0E0)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top Row: Animal Avatar & Names & Zone Tag
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
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(
                                if (animal.isUnlocked) zoneColor.copy(alpha = 0.18f) else Color(0xFFE0E0E0)
                            )
                            .border(
                                width = 1.5.dp,
                                color = if (animal.isUnlocked) zoneColor else Color.Gray,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (animal.isUnlocked) animal.emoji else "🔒",
                            fontSize = if (animal.isUnlocked) 30.sp else 24.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = if (animal.isUnlocked) animal.commonName else "Secret Jungle Creature",
                            fontWeight = FontWeight.Black,
                            fontSize = 17.sp,
                            color = if (animal.isUnlocked) MaterialTheme.colorScheme.onSurface else Color.Gray
                        )
                        Text(
                            text = if (animal.isUnlocked) animal.scientificName else "Locked Mystery Animal",
                            fontStyle = FontStyle.Italic,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = zoneColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = animal.zoneName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = zoneColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (animal.isUnlocked) {
                // Key Fun Fact Box
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "💡", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Did You Know?",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = AdventureOrange
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = animal.funFacts.firstOrNull() ?: "",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Safari Math Connection snippet
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "📐", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = animal.mathConnection,
                        fontSize = 11.sp,
                        color = JunglePrimary,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action Row: Read Aloud button and Details hint
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = onSpeakFact,
                        colors = ButtonDefaults.buttonColors(containerColor = zoneColor),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("listen_fact_btn_${animal.id}")
                    ) {
                        Text(text = "🔊 Listen Aloud", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Text(
                        text = "Tap for full factsheet ➜",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = zoneColor
                    )
                }
            } else {
                // Locked Milestone State
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "🗺️", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Milestone Unlock Requirement:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color.DarkGray
                                )
                                Text(
                                    text = animal.milestoneRequirement,
                                    fontSize = 12.sp,
                                    color = AdventureOrange,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Button(
                            onClick = onNavigateToZone,
                            colors = ButtonDefaults.buttonColors(containerColor = zoneColor),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(text = "Play Zone", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Detailed factsheet dialog showing habitat, diet, speed, multiple fun facts, and speech.
 */
@Composable
private fun AnimalFactsheetDialog(
    animal: JungleAnimalEntry,
    onDismiss: () -> Unit,
    onSpeak: () -> Unit
) {
    val zoneColor = Color(animal.zone.colorHex)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = null,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("animal_factsheet_dialog")
            ) {
                // Header Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(zoneColor, zoneColor.copy(alpha = 0.85f))
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
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = animal.emoji, fontSize = 36.sp)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = animal.commonName,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = animal.scientificName,
                                fontStyle = FontStyle.Italic,
                                fontSize = 12.sp,
                                color = BananaYellow
                            )
                            Text(
                                text = "Zone: ${animal.zoneName}",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Wildlife Profile Quick Specs
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(14.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FactRow(icon = "🌿", label = "Habitat", value = animal.habitat)
                    FactRow(icon = "🍉", label = "Diet", value = animal.diet)
                    FactRow(icon = "📏", label = "Size & Weight", value = animal.sizeAndWeight)
                    FactRow(icon = "⚡", label = "Top Ability", value = animal.topSpeedOrStat)
                    FactRow(icon = "🔊", label = "Vocal Sound", value = animal.vocalSoundDescription)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // List of Fun Facts
                Text(
                    text = "Fascinating Jungle Facts:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))

                animal.funFacts.forEach { fact ->
                    Row(
                        modifier = Modifier.padding(vertical = 3.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(text = "•", fontWeight = FontWeight.Black, color = zoneColor, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = fact,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Safari Math Connection
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = JunglePrimary.copy(alpha = 0.12f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "📐", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = animal.mathConnection,
                            fontSize = 11.sp,
                            color = JunglePrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onSpeak,
                colors = ButtonDefaults.buttonColors(containerColor = zoneColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "🔊 Speak Trivia", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Close")
            }
        }
    )
}

@Composable
private fun FactRow(
    icon: String,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(text = icon, fontSize = 13.sp)
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "$label: ",
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 14.sp
        )
    }
}
