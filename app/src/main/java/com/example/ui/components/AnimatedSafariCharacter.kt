package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
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
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MathTopic
import com.example.ui.theme.AdventureOrange
import com.example.ui.theme.BananaYellow
import com.example.ui.theme.EncouragementGreen
import com.example.ui.theme.JunglePrimary
import com.example.ui.theme.SafariGold
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

/**
 * Positive feedback reaction state for the animated animal character
 */
enum class AnimalReactionState {
    IDLE,
    CORRECT_CELEBRATION,
    ENCOURAGING_RETRY
}

/**
 * Data class holding encouraging message and animal identity
 */
data class AnimalCompanionInfo(
    val name: String,
    val animalEmoji: String,
    val title: String,
    val primaryColor: Color,
    val victoryQuotes: List<String>,
    val encouragementQuotes: List<String>
)

object AnimalCompanions {
    val LION = AnimalCompanionInfo(
        name = "Leo the Lion",
        animalEmoji = "🦁",
        title = "King of Numbers",
        primaryColor = Color(0xFFE65100),
        victoryQuotes = listOf(
            "ROAR! Incredible math power! 🦁⚡",
            "Leo is doing a king-sized victory dance! 👑",
            "Mighty calculation! You nailed it! 🌟",
            "Super roar! Addition master in the house! 🎉"
        ),
        encouragementQuotes = listOf(
            "Take courage, explorer! Try again! 💪",
            "Leo believes in you! You've got this! 🐾"
        )
    )

    val MONKEY = AnimalCompanionInfo(
        name = "Kiki the Monkey",
        animalEmoji = "🐒",
        title = "Banana Grove Acrobat",
        primaryColor = Color(0xFFFFA000),
        victoryQuotes = listOf(
            "OOH-OOH-AHH-AHH! You got it right! 🍌🐒",
            "Kiki is doing happy flips through the trees! 🌴",
            "Banana-tastic math skills! Yaaay! ⭐",
            "High paw! That was top-notch counting! 🐾"
        ),
        encouragementQuotes = listOf(
            "Swing back and try one more time! 🐒",
            "No bananas lost! Let's count again! 🍌"
        )
    )

    val ELEPHANT = AnimalCompanionInfo(
        name = "Tembo the Elephant",
        animalEmoji = "🐘",
        title = "Wisdom of the River",
        primaryColor = Color(0xFF1976D2),
        victoryQuotes = listOf(
            "TRUMPET! Tembo never forgets a genius! 🐘🎺",
            "Splashing water in celebration! Splendid! 🌊",
            "Gentle giant high-five! Brilliant answer! 💎",
            "River-smooth subtraction! Excellent! ✨"
        ),
        encouragementQuotes = listOf(
            "Big footsteps take patience! You're so close! 🐘",
            "Take a deep breath at the river and try! 💧"
        )
    )

    val GIRAFFE = AnimalCompanionInfo(
        name = "Twiga the Giraffe",
        animalEmoji = "🦒",
        title = "Canopy Multiplier",
        primaryColor = Color(0xFF2E7D32),
        victoryQuotes = listOf(
            "Reaching the highest branches of math! 🦒🌿",
            "Canopy-level genius! Multiplication star! 🌟",
            "Twiga's long neck is nodding in joy! 🍃",
            "Fantastic! Multiplying your safari wisdom! 🚀"
        ),
        encouragementQuotes = listOf(
            "Keep your head high! You can do it! 🦒",
            "Spot on thinking, let's try once more! 🌿"
        )
    )

    fun forTopic(topic: MathTopic): AnimalCompanionInfo = when (topic) {
        MathTopic.COUNTING -> MONKEY
        MathTopic.ADDITION -> LION
        MathTopic.SUBTRACTION -> ELEPHANT
        MathTopic.MULTIPLICATION -> GIRAFFE
    }
}

/**
 * Animated Animal Character Component
 *
 * Utilizes basic Compose animations:
 * - [rememberInfiniteTransition]: Continuous natural breathing, bobbing & ear wiggle in idle state.
 * - [animateFloatAsState] / [Animatable]: Bouncy jump keyframes, squash & stretch on landings, celebration rotation wiggles.
 * - [Canvas]: Star/confetti sparkle particle bursts that erupt when answering correctly.
 * - [AnimatedVisibility]: Smooth speech bubble delivery of positive encouraging feedback.
 */
@Composable
fun AnimatedAnimalCharacter(
    reactionState: AnimalReactionState,
    topic: MathTopic,
    modifier: Modifier = Modifier,
    customAnimalEmoji: String? = null,
    onTapAnimal: (() -> Unit)? = null
) {
    val companion = remember(topic) { AnimalCompanions.forTopic(topic) }
    val displayEmoji = customAnimalEmoji ?: companion.animalEmoji
    val scope = rememberCoroutineScope()

    // Interactive Tap Pulse
    var tapCounter by remember { mutableIntStateOf(0) }

    // Jump & Squash-and-Stretch Animatable states
    val jumpOffset = remember { Animatable(0f) }
    val scaleX = remember { Animatable(1f) }
    val scaleY = remember { Animatable(1f) }
    val rotationAngle = remember { Animatable(0f) }
    val celebrationAuraAlpha = remember { Animatable(0f) }

    // Particle Animation Progress
    val particleProgress = remember { Animatable(0f) }

    // Random quote selector on state change
    var currentQuote by remember { mutableStateOf("") }

    // Continuous idle breathing/bobbing animation
    val infiniteTransition = rememberInfiniteTransition(label = "idle_companion")
    val idleBobbing by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "idle_bob"
    )
    val idleBreathing by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "idle_breath"
    )

    // Trigger basic Compose animation sequence whenever reactionState changes to CORRECT_CELEBRATION
    LaunchedEffect(reactionState, tapCounter) {
        when (reactionState) {
            AnimalReactionState.CORRECT_CELEBRATION -> {
                currentQuote = companion.victoryQuotes.random()

                // Launch particle eruption
                launch {
                    particleProgress.snapTo(0f)
                    particleProgress.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(1200, easing = FastOutLinearInEasing)
                    )
                }

                // Launch aura pulse
                launch {
                    celebrationAuraAlpha.snapTo(0.8f)
                    celebrationAuraAlpha.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(900, easing = FastOutSlowInEasing)
                    )
                }

                // Squash preparation before jump
                scaleX.animateTo(1.18f, tween(100))
                scaleY.animateTo(0.82f, tween(100))

                // Launch into the air (Jump 1)
                launch {
                    rotationAngle.animateTo(-14f, tween(150))
                    rotationAngle.animateTo(14f, tween(200))
                    rotationAngle.animateTo(0f, tween(150))
                }

                scaleX.animateTo(0.88f, tween(120))
                scaleY.animateTo(1.22f, tween(120))

                jumpOffset.animateTo(
                    targetValue = -50f,
                    animationSpec = tween(220, easing = FastOutSlowInEasing)
                )

                // Fall down and squash upon landing
                jumpOffset.animateTo(
                    targetValue = 0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )

                scaleX.animateTo(1.15f, tween(80))
                scaleY.animateTo(0.85f, tween(80))

                // Second mini bounce
                jumpOffset.animateTo(-25f, tween(160, easing = FastOutSlowInEasing))
                jumpOffset.animateTo(0f, tween(160, easing = FastOutSlowInEasing))

                // Settle back to normal
                scaleX.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                scaleY.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
            }

            AnimalReactionState.ENCOURAGING_RETRY -> {
                currentQuote = companion.encouragementQuotes.random()

                // Gentle sympathetic nod
                jumpOffset.animateTo(8f, tween(180))
                jumpOffset.animateTo(-6f, tween(180))
                jumpOffset.animateTo(0f, spring(dampingRatio = Spring.DampingRatioLowBouncy))
                scaleX.animateTo(1f)
                scaleY.animateTo(1f)
            }

            AnimalReactionState.IDLE -> {
                jumpOffset.snapTo(0f)
                scaleX.snapTo(1f)
                scaleY.snapTo(1f)
                rotationAngle.snapTo(0f)
                celebrationAuraAlpha.snapTo(0f)
                particleProgress.snapTo(0f)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("animated_animal_character_container"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Positive Encouraging Speech Bubble (Animated visibility on feedback)
        AnimatedVisibility(
            visible = reactionState != AnimalReactionState.IDLE && currentQuote.isNotBlank(),
            enter = fadeIn(tween(250)) + scaleIn(tween(300)) + slideInVertically(tween(300)) { it / 2 }
        ) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = if (reactionState == AnimalReactionState.CORRECT_CELEBRATION) {
                    EncouragementGreen.copy(alpha = 0.14f)
                } else {
                    AdventureOrange.copy(alpha = 0.14f)
                },
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.5.dp,
                    color = if (reactionState == AnimalReactionState.CORRECT_CELEBRATION) EncouragementGreen else AdventureOrange
                ),
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("animal_encouragement_bubble")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (reactionState == AnimalReactionState.CORRECT_CELEBRATION) "🎉" else "💡",
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = currentQuote,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (reactionState == AnimalReactionState.CORRECT_CELEBRATION) {
                            EncouragementGreen
                        } else {
                            AdventureOrange
                        },
                        lineHeight = 17.sp
                    )
                }
            }
        }

        // Animal Character Area with Particle Bursts & Aura
        Box(
            modifier = Modifier
                .size(130.dp)
                .testTag("animal_character_stage"),
            contentAlignment = Alignment.Center
        ) {
            // 1. Celebration Particle Burst Canvas (Stars, Sparkles, Hearts)
            if (particleProgress.value > 0f && particleProgress.value < 1f) {
                CelebrationParticleCanvas(
                    progress = particleProgress.value,
                    primaryColor = companion.primaryColor,
                    modifier = Modifier.size(130.dp)
                )
            }

            // 2. Glowing Celebration Aura behind the animal
            if (celebrationAuraAlpha.value > 0f) {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .scale(1f + (1f - celebrationAuraAlpha.value) * 0.4f)
                        .alpha(celebrationAuraAlpha.value)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    SafariGold.copy(alpha = 0.6f),
                                    companion.primaryColor.copy(alpha = 0.3f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }

            // 3. Animal Mascot Base & Body
            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = 0,
                            y = (if (reactionState == AnimalReactionState.IDLE) idleBobbing else jumpOffset.value).roundToInt()
                        )
                    }
                    .scale(
                        scaleX = if (reactionState == AnimalReactionState.IDLE) idleBreathing else scaleX.value,
                        scaleY = if (reactionState == AnimalReactionState.IDLE) idleBreathing else scaleY.value
                    )
                    .rotate(rotationAngle.value)
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                companion.primaryColor.copy(alpha = 0.22f),
                                companion.primaryColor.copy(alpha = 0.08f)
                            )
                        )
                    )
                    .border(
                        width = 2.dp,
                        color = if (reactionState == AnimalReactionState.CORRECT_CELEBRATION) {
                            SafariGold
                        } else {
                            companion.primaryColor.copy(alpha = 0.4f)
                        },
                        shape = CircleShape
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        tapCounter++
                        onTapAnimal?.invoke()
                    }
                    .testTag("animated_animal_avatar"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = displayEmoji,
                    fontSize = 48.sp,
                    modifier = Modifier.testTag("animal_emoji_glyph")
                )

                // Cheerful Crown or Star badge when in correct celebration
                if (reactionState == AnimalReactionState.CORRECT_CELEBRATION) {
                    Text(
                        text = "⭐",
                        fontSize = 18.sp,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = (-4).dp, y = 2.dp)
                    )
                }
            }
        }

        // Animal Companion Name & Subtitle
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = companion.name,
                fontWeight = FontWeight.Black,
                fontSize = 13.sp,
                color = companion.primaryColor
            )
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = companion.primaryColor.copy(alpha = 0.12f)
            ) {
                Text(
                    text = if (reactionState == AnimalReactionState.CORRECT_CELEBRATION) "Cheering for you! 🌟" else companion.title,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = companion.primaryColor,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

/**
 * Canvas drawing celebratory bursts of floating stars, confetti dots, and sparkles.
 */
@Composable
private fun CelebrationParticleCanvas(
    progress: Float,
    primaryColor: Color,
    modifier: Modifier = Modifier
) {
    val particleCount = 14
    val colors = listOf(
        SafariGold,
        primaryColor,
        EncouragementGreen,
        AdventureOrange,
        BananaYellow,
        Color(0xFFE91E63)
    )

    Canvas(modifier = modifier) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val maxRadius = size.width * 0.48f

        for (i in 0 until particleCount) {
            val angle = (i.toFloat() / particleCount) * (2 * Math.PI.toFloat()) + (i * 0.2f)
            val distance = (maxRadius * progress) * (0.6f + (i % 3) * 0.2f)
            val px = centerX + cos(angle) * distance
            val py = centerY + sin(angle) * distance - (progress * 18f) // drift upward
            val particleAlpha = (1f - progress).coerceIn(0f, 1f)
            val particleColor = colors[i % colors.size].copy(alpha = particleAlpha)

            val particleSize = (8f * (1f - progress * 0.5f)).coerceAtLeast(2f)

            if (i % 2 == 0) {
                // Circular confetti
                drawCircle(
                    color = particleColor,
                    radius = particleSize,
                    center = Offset(px, py)
                )
            } else {
                // Diamond / Star sparkle
                val path = Path().apply {
                    moveTo(px, py - particleSize * 1.5f)
                    lineTo(px + particleSize, py)
                    lineTo(px, py + particleSize * 1.5f)
                    lineTo(px - particleSize, py)
                    close()
                }
                drawPath(path = path, color = particleColor)
            }
        }
    }
}
