package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.SafariHeader
import com.example.ui.theme.AdventureOrange
import com.example.ui.theme.BananaYellow
import com.example.ui.theme.EncouragementGreen
import com.example.ui.theme.JunglePrimary
import com.example.ui.theme.SafariGold
import com.example.ui.theme.StarGold

@Composable
fun HomeScreen(
    avatarEmoji: String,
    nickname: String,
    stars: Int,
    coins: Int,
    isSoundOn: Boolean,
    onSoundToggle: () -> Unit,
    isMusicOn: Boolean = true,
    onMusicToggle: () -> Unit = {},
    onPlaySafari: () -> Unit,
    onDailyChallenge: () -> Unit,
    onAnimalsClick: () -> Unit,
    onBadgesClick: () -> Unit,
    onStickersClick: () -> Unit = {},
    stickersCount: Int = 0,
    onEncyclopediaClick: () -> Unit = {},
    unlockedAnimalsCount: Int = 0,
    onProgressClick: () -> Unit = {},
    totalProblemsSolved: Int = 0,
    onParentZoneClick: () -> Unit,
    onEditProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val isWideScreen = maxWidth >= 680.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Header - centered & capped width for tablet ergonomics
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 1100.dp),
                contentAlignment = Alignment.Center
            ) {
                SafariHeader(
                    avatarEmoji = avatarEmoji,
                    nickname = nickname,
                    stars = stars,
                    coins = coins,
                    isSoundOn = isSoundOn,
                    onSoundToggle = onSoundToggle,
                    isMusicOn = isMusicOn,
                    onMusicToggle = onMusicToggle,
                    onAvatarClick = onEditProfile
                )
            }

            if (isWideScreen) {
                // Tablet / Large Screen / Landscape 2-Column Dashboard Layout
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 1100.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    // Left Column: Hero banner & Main Play Expedition CTAs
                    Column(
                        modifier = Modifier.weight(1.15f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        HeroJungleBanner(bannerHeight = 210.dp)
                        PlaySafariCard(onPlaySafari = onPlaySafari)
                        DailyChallengeCard(onDailyChallenge = onDailyChallenge)
                    }

                    // Right Column: Exploration, Collection & Parent Settings
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AnimalsAndRewardsRow(
                            onAnimalsClick = onAnimalsClick,
                            onBadgesClick = onBadgesClick
                        )
                        StickerAlbumCard(
                            stickersCount = stickersCount,
                            onStickersClick = onStickersClick
                        )
                        EncyclopediaCard(
                            unlockedAnimalsCount = unlockedAnimalsCount,
                            onEncyclopediaClick = onEncyclopediaClick
                        )
                        ProgressCard(
                            totalProblemsSolved = totalProblemsSolved,
                            onProgressClick = onProgressClick
                        )
                        ParentZoneCard(onParentZoneClick = onParentZoneClick)
                    }
                }
            } else {
                // Phone Portrait: Single Centered Streamlined Column
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 560.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    HeroJungleBanner(bannerHeight = 160.dp)
                    Spacer(modifier = Modifier.height(4.dp))
                    PlaySafariCard(onPlaySafari = onPlaySafari)
                    DailyChallengeCard(onDailyChallenge = onDailyChallenge)
                    AnimalsAndRewardsRow(
                        onAnimalsClick = onAnimalsClick,
                        onBadgesClick = onBadgesClick
                    )
                    StickerAlbumCard(
                        stickersCount = stickersCount,
                        onStickersClick = onStickersClick
                    )
                    EncyclopediaCard(
                        unlockedAnimalsCount = unlockedAnimalsCount,
                        onEncyclopediaClick = onEncyclopediaClick
                    )
                    ProgressCard(
                        totalProblemsSolved = totalProblemsSolved,
                        onProgressClick = onProgressClick
                    )
                    ParentZoneCard(onParentZoneClick = onParentZoneClick)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun HeroJungleBanner(
    bannerHeight: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(bannerHeight)
        ) {
            Image(
                painter = painterResource(id = R.drawable.jungle_banner),
                contentDescription = "Jungle Landscape",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Dark subtle overlay for readable text
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.65f))
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Ready for Adventure?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Text(
                    text = "Explore 4 wild math worlds and collect stars!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = BananaYellow
                )
            }
        }
    }
}

@Composable
private fun PlaySafariCard(
    onPlaySafari: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(24.dp))
            .clickable { onPlaySafari() }
            .testTag("play_safari_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = JunglePrimary),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(BananaYellow),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🗺️", fontSize = 32.sp)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "PLAY SAFARI",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "Counting • Addition • Subtraction • Multiplication",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun DailyChallengeCard(
    onDailyChallenge: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(22.dp))
            .clickable { onDailyChallenge() }
            .testTag("daily_challenge_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = SafariGold),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "🌟", fontSize = 36.sp)
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = "DAILY SAFARI",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "5 Quick Questions • Earn 300 🪙 Bonus!",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.95f)
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White.copy(alpha = 0.3f)
            ) {
                Text(
                    text = "PLAY",
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun AnimalsAndRewardsRow(
    onAnimalsClick: () -> Unit,
    onBadgesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Animals Card
        Card(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(20.dp))
                .clickable { onAnimalsClick() }
                .testTag("my_animals_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "🐾", fontSize = 36.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "My Animals",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "5 Companions",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        // Rewards / Badges Card
        Card(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(20.dp))
                .clickable { onBadgesClick() }
                .testTag("my_rewards_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "🏅", fontSize = 36.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "My Rewards",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Badges & Stars",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun StickerAlbumCard(
    stickersCount: Int,
    onStickersClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable { onStickersClick() }
            .testTag("animal_stickers_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(BananaYellow.copy(alpha = 0.35f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🐾", fontSize = 26.sp)
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Animal Sticker Album",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = JunglePrimary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "$stickersCount/12 ✨",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = JunglePrimary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "Collect digital stickers by solving math problems!",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
            Text(
                text = "➜",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = JunglePrimary
            )
        }
    }
}

@Composable
private fun EncyclopediaCard(
    unlockedAnimalsCount: Int,
    onEncyclopediaClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable { onEncyclopediaClick() }
            .testTag("jungle_encyclopedia_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(SafariGold.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "📖", fontSize = 26.sp)
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Jungle Encyclopedia",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AdventureOrange.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "$unlockedAnimalsCount/16 🦁",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AdventureOrange,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "Real animal facts unlocked across math zones!",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
            Text(
                text = "➜",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AdventureOrange
            )
        }
    }
}

@Composable
private fun ProgressCard(
    totalProblemsSolved: Int,
    onProgressClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable { onProgressClick() }
            .testTag("math_progress_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(JunglePrimary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "📊", fontSize = 26.sp)
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Math Progress & Charts",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EncouragementGreen.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "$totalProblemsSolved Solved",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EncouragementGreen,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "Visual graphs: Counting, Addition, Multiplication & more",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
            Text(
                text = "➜",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = JunglePrimary
            )
        }
    }
}

@Composable
private fun ParentZoneCard(
    onParentZoneClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable { onParentZoneClick() }
            .testTag("parent_zone_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Parent Gate",
                    tint = AdventureOrange,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Parent Zone & Learning Stats",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "⚙️",
                fontSize = 16.sp
            )
        }
    }
}
