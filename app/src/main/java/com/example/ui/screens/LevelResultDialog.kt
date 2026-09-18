package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GameLevelSummary
import com.example.ui.theme.AdventureOrange
import com.example.ui.theme.BananaYellow
import com.example.ui.theme.CoinAmber
import com.example.ui.theme.JunglePrimary
import com.example.ui.theme.SafariGold
import com.example.ui.theme.StarGold

@Composable
fun LevelResultDialog(
    summary: GameLevelSummary,
    onNextLevel: () -> Unit,
    onRetryLevel: () -> Unit,
    onReturnMap: () -> Unit,
    onViewStickers: (() -> Unit)? = null
) {
    AlertDialog(
        onDismissRequest = onReturnMap,
        shape = RoundedCornerShape(28.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (summary.isPerfect) "PERFECT ADVENTURE! 🌟" else "SAFARI COMPLETE! 🏆",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = JunglePrimary,
                    textAlign = TextAlign.Center
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = "${summary.topic.title} • Level ${summary.levelNumber}",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(summary.difficulty.colorHex).copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "${summary.difficulty.emoji} ${summary.difficulty.title}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(summary.difficulty.colorHex),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Stars Display
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 12.dp)
                ) {
                    for (i in 1..3) {
                        val isFilled = i <= summary.stars
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(if (i == 2) 54.dp else 44.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isFilled) StarGold.copy(alpha = 0.2f) else Color(0xFFEEEEEE)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Star $i",
                                tint = if (isFilled) StarGold else Color.LightGray,
                                modifier = Modifier.size(if (i == 2) 40.dp else 30.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Stats Summary
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Correct", fontSize = 12.sp, color = Color.Gray)
                            Text(
                                text = "${summary.correctCount}/${summary.totalCount}",
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Score", fontSize = 12.sp, color = Color.Gray)
                            Text(
                                text = "${summary.score} pts",
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = JunglePrimary
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Coins Earned", fontSize = 12.sp, color = Color.Gray)
                            Text(
                                text = "+${summary.coinsEarned} 🪙",
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = CoinAmber
                            )
                        }
                    }
                }

                if (summary.newlyUnlockedStickers.isNotEmpty()) {
                    val sticker = summary.newlyUnlockedStickers.first()
                    Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = BananaYellow.copy(alpha = 0.25f)),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(SafariGold)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = sticker.animalEmoji, fontSize = 32.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "NEW ANIMAL STICKER! 🐾",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFE65100)
                                )
                                Text(
                                    text = "${sticker.name} • ${sticker.title}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (summary.newlyUnlockedStickers.isNotEmpty() && onViewStickers != null) {
                    Button(
                        onClick = onViewStickers,
                        colors = ButtonDefaults.buttonColors(containerColor = SafariGold),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                            .testTag("result_view_stickers_button")
                    ) {
                        Text(
                            text = "View in Sticker Album 🐾",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }
                }

                Button(
                    onClick = onNextLevel,
                    colors = ButtonDefaults.buttonColors(containerColor = JunglePrimary),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("result_next_level_button")
                ) {
                    Text(
                        text = "Next Level ➜",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
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
                    onClick = onRetryLevel,
                    colors = ButtonDefaults.outlinedButtonColors(),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.testTag("result_retry_button")
                ) {
                    Text("Try Again 🔄")
                }
                Button(
                    onClick = onReturnMap,
                    colors = ButtonDefaults.outlinedButtonColors(),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.testTag("result_map_button")
                ) {
                    Text("Safari Map 🗺️")
                }
            }
        }
    )
}
