package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AnimalSticker
import com.example.data.model.StickerRarity
import com.example.ui.components.SafariTopBar
import com.example.ui.theme.BananaYellow
import com.example.ui.theme.JunglePrimary
import com.example.ui.theme.SafariGold

@Composable
fun StickersScreen(
    stickers: List<AnimalSticker>,
    totalProblemsSolved: Int,
    onBackClick: () -> Unit,
    onSpeakStickerFact: (AnimalSticker) -> Unit = {},
    isMusicOn: Boolean = true,
    onMusicToggle: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val unlockedCount = stickers.count { it.isUnlocked }
    var selectedFilter by remember { mutableStateOf("ALL") }
    var inspectedSticker by remember { mutableStateOf<AnimalSticker?>(null) }

    val filteredStickers = remember(stickers, selectedFilter) {
        when (selectedFilter) {
            "UNLOCKED" -> stickers.filter { it.isUnlocked }
            "LOCKED" -> stickers.filter { !it.isUnlocked }
            "BRONZE" -> stickers.filter { it.rarity == StickerRarity.BRONZE }
            "SILVER" -> stickers.filter { it.rarity == StickerRarity.SILVER }
            "GOLD" -> stickers.filter { it.rarity == StickerRarity.GOLD }
            "DIAMOND" -> stickers.filter { it.rarity == StickerRarity.DIAMOND || it.rarity == StickerRarity.MYTHIC }
            else -> stickers
        }
    }

    val nextStickerToUnlock = stickers.firstOrNull { !it.isUnlocked }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        SafariTopBar(
            title = "Animal Stickers 🐾",
            subtitle = "$unlockedCount of ${stickers.size} Stickers Collected",
            onBackClick = onBackClick,
            onMusicToggle = onMusicToggle,
            isMusicOn = isMusicOn
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Album Progress Header Banner
            item(span = { GridItemSpan(2) }) {
                StickerAlbumBanner(
                    unlockedCount = unlockedCount,
                    totalCount = stickers.size,
                    totalProblemsSolved = totalProblemsSolved,
                    nextSticker = nextStickerToUnlock
                )
            }

            // Filter Chips
            item(span = { GridItemSpan(2) }) {
                StickerFilterRow(
                    selectedFilter = selectedFilter,
                    onSelectFilter = { selectedFilter = it }
                )
            }

            // Sticker Cards
            items(filteredStickers, key = { it.id }) { sticker ->
                StickerCard(
                    sticker = sticker,
                    onClick = {
                        inspectedSticker = sticker
                        if (sticker.isUnlocked) {
                            onSpeakStickerFact(sticker)
                        }
                    }
                )
            }
        }
    }

    // Interactive Sticker Inspection Dialog
    inspectedSticker?.let { sticker ->
        StickerDetailDialog(
            sticker = sticker,
            onDismiss = { inspectedSticker = null },
            onSpeakFact = { onSpeakStickerFact(sticker) }
        )
    }
}

@Composable
fun StickerAlbumBanner(
    unlockedCount: Int,
    totalCount: Int,
    totalProblemsSolved: Int,
    nextSticker: AnimalSticker?,
    modifier: Modifier = Modifier
) {
    val progressFraction = if (totalCount > 0) unlockedCount.toFloat() / totalCount else 0f

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 4.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Collector's Album",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "$totalProblemsSolved math challenges solved correctly!",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = JunglePrimary.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$unlockedCount / $totalCount",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = JunglePrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { progressFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(CircleShape),
                color = JunglePrimary,
                trackColor = Color(0xFFE0E0E0)
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (nextSticker != null) {
                val remaining = (nextSticker.requiredProblems - totalProblemsSolved).coerceAtLeast(1)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BananaYellow.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(text = nextSticker.animalEmoji, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Solve $remaining more problem${if (remaining > 1) "s" else ""} to unlock ${nextSticker.name}!",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF5D4037)
                    )
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SafariGold.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(text = "👑", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Outstanding! You collected all digital animal stickers!",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB78103)
                    )
                }
            }
        }
    }
}

@Composable
fun StickerFilterRow(
    selectedFilter: String,
    onSelectFilter: (String) -> Unit
) {
    val filters = listOf(
        "ALL" to "All Stickers",
        "UNLOCKED" to "Unlocked ✨",
        "LOCKED" to "Locked 🔒",
        "BRONZE" to "Bronze 🥉",
        "SILVER" to "Silver 🥈",
        "GOLD" to "Gold 🥇",
        "DIAMOND" to "Diamond & Mythic 💎"
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        items(filters) { (key, label) ->
            FilterChip(
                selected = selectedFilter == key,
                onClick = { onSelectFilter(key) },
                label = { Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = JunglePrimary,
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

@Composable
fun StickerCard(
    sticker: AnimalSticker,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rarityColor = Color(sticker.rarity.colorHex)

    val infiniteTransition = rememberInfiniteTransition(label = "stickerPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (sticker.isUnlocked) 1.04f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .clickable { onClick() }
            .testTag("sticker_card_${sticker.id}"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (sticker.isUnlocked) MaterialTheme.colorScheme.surface else Color(0xFFF3F3F3)
        ),
        elevation = CardDefaults.cardElevation(if (sticker.isUnlocked) 3.dp else 1.dp),
        border = if (sticker.isUnlocked) {
            CardDefaults.outlinedCardBorder().copy(
                brush = Brush.linearGradient(
                    listOf(rarityColor, rarityColor.copy(alpha = 0.5f))
                ),
                width = 2.dp
            )
        } else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Rarity Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (sticker.isUnlocked) rarityColor.copy(alpha = 0.15f) else Color.LightGray.copy(alpha = 0.4f)
                ) {
                    Text(
                        text = "${sticker.rarity.badgeEmoji} ${sticker.rarity.label}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (sticker.isUnlocked) rarityColor else Color.Gray,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                if (sticker.isUnlocked) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Unlocked",
                        tint = JunglePrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sticker Avatar Circle
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .scale(if (sticker.isUnlocked) pulseScale else 1f)
                    .clip(CircleShape)
                    .background(
                        if (sticker.isUnlocked) {
                            rarityColor.copy(alpha = 0.18f)
                        } else {
                            Color.LightGray.copy(alpha = 0.3f)
                        }
                    )
                    .border(
                        width = if (sticker.isUnlocked) 2.dp else 1.dp,
                        color = if (sticker.isUnlocked) rarityColor.copy(alpha = 0.6f) else Color.LightGray,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (sticker.isUnlocked) {
                    Text(text = sticker.animalEmoji, fontSize = 40.sp)
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked Sticker",
                            tint = Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sticker Name
            Text(
                text = if (sticker.isUnlocked) sticker.name else "???",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = if (sticker.isUnlocked) MaterialTheme.colorScheme.onSurface else Color.Gray,
                textAlign = TextAlign.Center
            )

            Text(
                text = if (sticker.isUnlocked) sticker.title else "Mystery Explorer",
                fontSize = 12.sp,
                color = if (sticker.isUnlocked) JunglePrimary else Color.LightGray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (sticker.isUnlocked) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = JunglePrimary.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "${sticker.requiredProblems} Solved ✓",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = JunglePrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            } else {
                // Progress bar towards unlock
                val progress = if (sticker.requiredProblems > 0) {
                    sticker.currentProgress.toFloat() / sticker.requiredProblems
                } else 0f

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(6.dp)
                            .clip(CircleShape),
                        color = Color.Gray,
                        trackColor = Color(0xFFE0E0E0)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${sticker.currentProgress}/${sticker.requiredProblems} Problems",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun StickerDetailDialog(
    sticker: AnimalSticker,
    onDismiss: () -> Unit,
    onSpeakFact: () -> Unit
) {
    val rarityColor = Color(sticker.rarity.colorHex)

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(26.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(
                            if (sticker.isUnlocked) rarityColor.copy(alpha = 0.2f) else Color.LightGray.copy(alpha = 0.3f)
                        )
                        .border(
                            width = 3.dp,
                            color = if (sticker.isUnlocked) rarityColor else Color.Gray,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (sticker.isUnlocked) sticker.animalEmoji else "🔒",
                        fontSize = 50.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (sticker.isUnlocked) sticker.name else "Locked Mystery Sticker",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = rarityColor.copy(alpha = 0.15f),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = "${sticker.rarity.badgeEmoji} ${sticker.rarity.label} • ${sticker.title}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = rarityColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (sticker.isUnlocked) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🌿", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Safari Fun Fact",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = sticker.funFact,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 18.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Awarded for answering ${sticker.requiredProblems} math problems correctly! ✨",
                        fontSize = 12.sp,
                        color = JunglePrimary,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F3F3))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "How to Unlock:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.DarkGray
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Answer ${sticker.requiredProblems} math questions correctly across any jungle adventure!",
                                fontSize = 13.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            val remaining = (sticker.requiredProblems - sticker.currentProgress).coerceAtLeast(1)
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = JunglePrimary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "$remaining more correct answer${if (remaining > 1) "s" else ""} needed",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = JunglePrimary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (sticker.isUnlocked) {
                Button(
                    onClick = onSpeakFact,
                    colors = ButtonDefaults.buttonColors(containerColor = JunglePrimary),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Hear Fun Fact 🔊", fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = JunglePrimary),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Keep Exploring 🐾", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            if (sticker.isUnlocked) {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.outlinedButtonColors(),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Close")
                }
            }
        }
    )
}

@Composable
fun StickerCelebrationDialog(
    sticker: AnimalSticker,
    onViewAlbum: () -> Unit,
    onDismiss: () -> Unit,
    onSpeakFact: () -> Unit = {}
) {
    val rarityColor = Color(sticker.rarity.colorHex)

    val infiniteTransition = rememberInfiniteTransition(label = "celebrationZoom")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "zoom"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🎉 NEW STICKER! 🎉",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = JunglePrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .scale(scale)
                        .clip(CircleShape)
                        .background(rarityColor.copy(alpha = 0.22f))
                        .border(
                            width = 3.dp,
                            color = rarityColor,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = sticker.animalEmoji,
                        fontSize = 56.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = sticker.name,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = rarityColor.copy(alpha = 0.15f),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = "${sticker.rarity.badgeEmoji} ${sticker.rarity.label} • ${sticker.title}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = rarityColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🌿", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Safari Fun Fact",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = sticker.funFact,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Unlocked by solving ${sticker.requiredProblems} math problems!",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = JunglePrimary,
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = onViewAlbum,
                    colors = ButtonDefaults.buttonColors(containerColor = JunglePrimary),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("celebration_view_album_button")
                ) {
                    Text(
                        text = "View in Sticker Album 🐾",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        },
        dismissButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onSpeakFact,
                    colors = ButtonDefaults.outlinedButtonColors(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Hear Fact 🔊")
                }
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.outlinedButtonColors(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Awesome! ✨")
                }
            }
        }
    )
}

