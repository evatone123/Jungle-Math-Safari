package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.AdventureOrange
import com.example.ui.theme.BananaYellow
import com.example.ui.theme.JunglePrimary
import com.example.ui.theme.SafariGold

@Composable
fun SplashScreen(
    onStartAdventure: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scaleAnim = remember { Animatable(0.85f) }

    LaunchedEffect(Unit) {
        scaleAnim.animateTo(
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF1B5E20),
                        Color(0xFF2E7D32),
                        Color(0xFF388E3C)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Main Brand block
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Banner / Icon
                Surface(
                    shape = RoundedCornerShape(32.dp),
                    color = Color.White.copy(alpha = 0.15f),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .padding(12.dp)
                            .scale(scaleAnim.value),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_safari_lion),
                            contentDescription = "Safari Lion Mascot",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(24.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Text(
                    text = "JUNGLE MATH\nSAFARI",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Black,
                    color = BananaYellow,
                    textAlign = TextAlign.Center,
                    lineHeight = 40.sp,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Learn. Explore. Roar! 🦁",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }

            // Animal Companions Preview
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White.copy(alpha = 0.95f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Meet your safari guides!",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = JunglePrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        AnimalBadgeItem(emoji = "🐒", name = "Kiki", skill = "Counting")
                        AnimalBadgeItem(emoji = "🦁", name = "Leo", skill = "Addition")
                        AnimalBadgeItem(emoji = "🐘", name = "Tembo", skill = "Subtraction")
                        AnimalBadgeItem(emoji = "🦒", name = "Twiga", skill = "Multiplication")
                    }
                }
            }

            // Enter Button
            Button(
                onClick = onStartAdventure,
                colors = ButtonDefaults.buttonColors(containerColor = AdventureOrange),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .testTag("start_adventure_button")
            ) {
                Text(
                    text = "START SAFARI! 🌴",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun AnimalBadgeItem(emoji: String, name: String, skill: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = emoji, fontSize = 32.sp)
        Text(
            text = name,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1B2E1E)
        )
        Text(
            text = skill,
            fontSize = 10.sp,
            color = Color.Gray
        )
    }
}
