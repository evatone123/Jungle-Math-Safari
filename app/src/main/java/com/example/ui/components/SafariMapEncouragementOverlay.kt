package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.CompanionEncouragement
import com.example.data.model.DifficultyLevel
import com.example.data.model.LessonEncouragementState
import com.example.data.model.MathTopic
import com.example.ui.theme.AdventureOrange
import com.example.ui.theme.BananaYellow
import com.example.ui.theme.CoinAmber
import com.example.ui.theme.EncouragementGreen
import com.example.ui.theme.JunglePrimary
import com.example.ui.theme.SafariGold
import com.example.ui.theme.StarGold
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Animated Animal Companion Screen State Overlay for the Safari Map.
 *
 * Displays jubilant animal companions offering personalized encouraging messages,
 * celebratory animations, audio cheers, and direct trail progression actions
 * after a lesson is completed.
 */
@Composable
fun SafariMapEncouragementOverlay(
    state: LessonEncouragementState,
    onDismiss: () -> Unit,
    onSpeakEncouragement: (String) -> Unit,
    onGiveHighFive: () -> Unit = {},
    onStartNextLesson: (MathTopic, Int, DifficultyLevel) -> Unit = { _, _, _ -> },
    onRetryLesson: (MathTopic, Int, DifficultyLevel) -> Unit = { _, _, _ -> },
    onViewStickers: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedCompanionIndex by remember { mutableIntStateOf(state.selectedCompanionIndex) }
    val currentCompanion = state.companions.getOrElse(selectedCompanionIndex) {
        state.companions.firstOrNull() ?: CompanionEncouragement(
            companionId = "lion",
            name = "Leo the Lion",
            emoji = "🦁",
            title = "Addition Buddy",
            role = "Pride Leader",
            colorHex = 0xFFE65100,
            cheerHeadline = "Mighty Pride Roar!",
            message = "You did a fantastic job on this trail milestone!",
            voiceSpeech = "Mighty Pride Roar! Great job!",
            celebrationMove = "JUMP"
        )
    }

    // Animation States for the active companion
    val companionJumpY = remember { Animatable(0f) }
    val companionScaleX = remember { Animatable(1f) }
    val companionScaleY = remember { Animatable(1f) }
    val companionRotation = remember { Animatable(0f) }
    val highFiveBurstAlpha = remember { Animatable(0f) }
    val celebrationParticleProgress = remember { Animatable(0f) }
    var highFiveCount by remember { mutableIntStateOf(0) }
    var isSpeakingAudio by remember { mutableStateOf(false) }

    // Ambient floating/bobbing animation
    val ambientTransition = rememberInfiniteTransition(label = "ambient_companion_idle")
    val idleBobbing by ambientTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "idle_bobbing"
    )
    val idleBreathing by ambientTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1300, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "idle_breathing"
    )

    // Trigger initial celebration when screen state appears or companion changes
    LaunchedEffect(currentCompanion.companionId) {
        // Automatically play particles and jump
        launch {
            celebrationParticleProgress.snapTo(0f)
            celebrationParticleProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(1400, easing = FastOutLinearInEasing)
            )
        }

        // Bouncy Jump Entrance
        companionScaleX.animateTo(1.15f, tween(90))
        companionScaleY.animateTo(0.85f, tween(90))

        launch {
            companionRotation.animateTo(-12f, tween(140))
            companionRotation.animateTo(12f, tween(180))
            companionRotation.animateTo(0f, tween(120))
        }

        companionScaleX.animateTo(0.9f, tween(110))
        companionScaleY.animateTo(1.2f, tween(110))
        companionJumpY.animateTo(-55f, tween(240, easing = FastOutSlowInEasing))
        companionJumpY.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
        companionScaleX.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        companionScaleY.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
    }

    // High Five Action function
    val triggerHighFive = {
        highFiveCount++
        onGiveHighFive()
        coroutineScope.launch {
            // Heart burst animation
            launch {
                highFiveBurstAlpha.snapTo(1f)
                highFiveBurstAlpha.animateTo(0f, tween(800, easing = FastOutSlowInEasing))
            }
            // Particle burst
            launch {
                celebrationParticleProgress.snapTo(0f)
                celebrationParticleProgress.animateTo(1f, tween(1000, easing = FastOutLinearInEasing))
            }
            // Energetic jump
            companionJumpY.animateTo(-65f, tween(180, easing = FastOutSlowInEasing))
            companionRotation.animateTo(if (highFiveCount % 2 == 0) 15f else -15f, tween(140))
            companionJumpY.animateTo(0f, spring(dampingRatio = Spring.DampingRatioHighBouncy))
            companionRotation.animateTo(0f, tween(100))
        }
    }

    val companionColor = Color(currentCompanion.colorHex)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.65f))
            .testTag("safari_map_encouragement_screen_state"),
        contentAlignment = Alignment.Center
    ) {
        // Falling jungle confetti and sparkles background
        ConfettiCelebrationCanvas(
            modifier = Modifier.fillMaxSize(),
            particleProgress = celebrationParticleProgress.value
        )

        // Main Encouragement Card
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 16.dp)
                .clip(RoundedCornerShape(32.dp))
                .border(3.dp, SafariGold, RoundedCornerShape(32.dp))
                .testTag("companion_encouragement_card"),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 18.dp)
            ) {
                // Top Header Banner with celebration gradient
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    companionColor,
                                    companionColor.copy(alpha = 0.88f)
                                )
                            )
                        )
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.Black.copy(alpha = 0.25f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = state.topic.animalEmoji,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${state.biomeName} • Lvl ${state.levelNumber}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            // Close / Dismiss Icon
                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("btn_close_encouragement")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = if (state.isPerfect) "PERFECT TRAIL RUN! 🌟" else "LESSON COMPLETED! 🏆",
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Black,
                            color = BananaYellow,
                            letterSpacing = 0.8.sp,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = state.lessonTitle,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.95f),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Star Burst Display
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            for (i in 1..3) {
                                val isEarned = i <= state.starsEarned
                                Box(
                                    modifier = Modifier
                                        .padding(horizontal = 4.dp)
                                        .size(if (i == 2) 42.dp else 34.dp)
                                        .clip(CircleShape)
                                        .background(if (isEarned) StarGold.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Star $i",
                                        tint = if (isEarned) StarGold else Color.White.copy(alpha = 0.4f),
                                        modifier = Modifier.size(if (i == 2) 32.dp else 24.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Quick stats pill
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color.White.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "${state.score} pts",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = BananaYellow.copy(alpha = 0.3f)
                            ) {
                                Text(
                                    text = "+${state.coinsEarned} Coins 🪙",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BananaYellow,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Companion Squad Carousel Tabs (Tap any animal companion to hear them cheer!)
                Text(
                    text = "SAFARI BUDDY CHEERS 🐾 (Tap a friend!)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = JunglePrimary,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                    letterSpacing = 0.5.sp
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.companions.forEachIndexed { index, companion ->
                        val isSelected = index == selectedCompanionIndex
                        val tabColor = Color(companion.colorHex)
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    selectedCompanionIndex = index
                                }
                                .testTag("companion_tab_${companion.companionId}"),
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) tabColor.copy(alpha = 0.16f) else MaterialTheme.colorScheme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) tabColor else Color.Transparent
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = companion.emoji,
                                    fontSize = 20.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = companion.name.substringBefore(" the"),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (isSelected) tabColor else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = companion.role,
                                        fontSize = 9.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Animated Animal Companion Interactive Stage
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .testTag("animated_companion_stage"),
                        contentAlignment = Alignment.Center
                    ) {
                        // High-five burst ring animation
                        if (highFiveBurstAlpha.value > 0f) {
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .scale(1f + (1f - highFiveBurstAlpha.value) * 0.5f)
                                    .alpha(highFiveBurstAlpha.value)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            listOf(SafariGold.copy(alpha = 0.7f), Color.Transparent)
                                        )
                                    )
                            )
                        }

                        // Floating decorative sparkles around companion
                        Text(
                            text = "✨",
                            fontSize = 18.sp,
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .offset(x = 10.dp, y = (-8).dp)
                                .alpha(idleBreathing)
                        )
                        Text(
                            text = "⭐",
                            fontSize = 16.sp,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = (-8).dp, y = (-4).dp)
                        )
                        Text(
                            text = "🎉",
                            fontSize = 18.sp,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset(x = (-4).dp, y = 8.dp)
                        )

                        // Main Companion Avatar Mascot
                        Box(
                            modifier = Modifier
                                .offset {
                                    IntOffset(
                                        x = 0,
                                        y = (companionJumpY.value + idleBobbing).roundToInt()
                                    )
                                }
                                .scale(
                                    scaleX = companionScaleX.value * idleBreathing,
                                    scaleY = companionScaleY.value * idleBreathing
                                )
                                .rotate(companionRotation.value)
                                .size(96.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            companionColor.copy(alpha = 0.28f),
                                            companionColor.copy(alpha = 0.10f)
                                        )
                                    )
                                )
                                .border(2.5.dp, companionColor, CircleShape)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    triggerHighFive()
                                }
                                .testTag("animated_companion_avatar"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = currentCompanion.emoji,
                                fontSize = 54.sp
                            )
                        }
                    }

                    // Mascot Name & Title
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = currentCompanion.name,
                            fontWeight = FontWeight.Black,
                            fontSize = 17.sp,
                            color = companionColor
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = companionColor.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = currentCompanion.title,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = companionColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Speech Bubble with Personalized Encouragement
                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = companionColor.copy(alpha = 0.08f),
                        border = androidx.compose.foundation.BorderStroke(
                            width = 1.5.dp,
                            color = companionColor.copy(alpha = 0.35f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("companion_speech_bubble")
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "💬",
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = currentCompanion.cheerHeadline,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = companionColor,
                                    textAlign = TextAlign.Center
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "“${currentCompanion.message}”",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Interactive Audio & High Five Buttons inside bubble
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Speak button
                                Button(
                                    onClick = {
                                        isSpeakingAudio = true
                                        onSpeakEncouragement(currentCompanion.voiceSpeech)
                                        coroutineScope.launch {
                                            delay(3500)
                                            isSpeakingAudio = false
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = companionColor),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier.testTag("btn_speak_encouragement")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = "Speak quote",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isSpeakingAudio) "Listening..." else "Hear Me Cheer! 🔊",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                // High Five Button
                                Button(
                                    onClick = { triggerHighFive() },
                                    colors = ButtonDefaults.buttonColors(containerColor = SafariGold),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier.testTag("btn_give_high_five")
                                ) {
                                    Text(
                                        text = "High-Five! 🐾",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    // Newly unlocked sticker preview (if any)
                    state.unlockedSticker?.let { sticker ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onViewStickers?.invoke() },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = BananaYellow.copy(alpha = 0.25f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SafariGold)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = sticker.animalEmoji, fontSize = 28.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "NEW ANIMAL STICKER UNLOCKED! 🐾",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = AdventureOrange
                                    )
                                    Text(
                                        text = "${sticker.name} • ${sticker.title}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Text(
                                    text = "View ➜",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AdventureOrange
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Primary Action Buttons
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val nextLevelNum = state.levelNumber + 1
                        if (nextLevelNum <= 10 && !state.isDailyChallenge) {
                            Button(
                                onClick = {
                                    onDismiss()
                                    onStartNextLesson(state.topic, nextLevelNum, DifficultyLevel.MEDIUM)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = JunglePrimary),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("btn_next_lesson_from_encouragement")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Next Lesson: Lvl $nextLevelNum",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "Next Lesson",
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        // Explore Safari Trail Button
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = SafariGold),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("btn_continue_trail")
                        ) {
                            Text(
                                text = "Explore Safari Trail 🗺️",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        }

                        // Replay lesson button
                        Button(
                            onClick = {
                                onDismiss()
                                onRetryLesson(state.topic, state.levelNumber, DifficultyLevel.MEDIUM)
                            },
                            colors = ButtonDefaults.outlinedButtonColors(),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                                .testTag("btn_replay_lesson")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Replay",
                                    tint = JunglePrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Practice Lesson Again",
                                    fontSize = 13.sp,
                                    color = JunglePrimary,
                                    fontWeight = FontWeight.SemiBold
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
 * Canvas drawing falling celebratory confetti, tropical leaves, and star sparkles.
 */
@Composable
private fun ConfettiCelebrationCanvas(
    modifier: Modifier = Modifier,
    particleProgress: Float
) {
    val confettiColors = listOf(
        SafariGold,
        JunglePrimary,
        AdventureOrange,
        BananaYellow,
        EncouragementGreen,
        Color(0xFFE91E63),
        Color(0xFF00BCD4)
    )

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val totalParticles = 28

        for (i in 0 until totalParticles) {
            val randomFactor = ((i * 37) % 100) / 100f
            val startX = (i.toFloat() / totalParticles) * width + (sin(i.toDouble()).toFloat() * 20f)
            val currentY = ((particleProgress * height * 1.2f) + (randomFactor * height)) % height
            val currentX = startX + (sin((currentY / 60f).toDouble()).toFloat() * 18f)

            val color = confettiColors[i % confettiColors.size]
            val radius = 4f + ((i % 4) * 2f)

            if (i % 3 == 0) {
                // Circle confetti
                drawCircle(
                    color = color.copy(alpha = 0.85f),
                    radius = radius,
                    center = Offset(currentX, currentY)
                )
            } else if (i % 3 == 1) {
                // Diamond sparkle
                val path = Path().apply {
                    moveTo(currentX, currentY - radius * 1.5f)
                    lineTo(currentX + radius, currentY)
                    lineTo(currentX, currentY + radius * 1.5f)
                    lineTo(currentX - radius, currentY)
                    close()
                }
                drawPath(path = path, color = color.copy(alpha = 0.85f))
            } else {
                // Small rectangle / ribbon
                drawCircle(
                    color = color.copy(alpha = 0.65f),
                    radius = radius * 0.7f,
                    center = Offset(currentX, currentY)
                )
            }
        }
    }
}
