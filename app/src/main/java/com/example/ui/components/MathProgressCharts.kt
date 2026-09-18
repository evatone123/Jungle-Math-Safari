package com.example.ui.components

import android.graphics.Paint
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MathTopic
import com.example.ui.theme.JunglePrimary
import kotlin.math.cos
import kotlin.math.sin

/**
 * Clean data model representing category learning progress for charting
 */
data class CategoryProgressItem(
    val topic: MathTopic,
    val title: String,
    val emoji: String,
    val animalEmoji: String,
    val animalName: String,
    val solvedCount: Int,
    val totalCount: Int,
    val color: Color
) {
    val accuracyPct: Int
        get() = if (totalCount > 0) (solvedCount.toFloat() / totalCount * 100).toInt() else 0

    val masteryTier: String
        get() = when {
            solvedCount >= 50 -> "👑 Math Master"
            solvedCount >= 25 -> "⭐ Trailblazer"
            solvedCount >= 10 -> "🐾 Explorer"
            solvedCount > 0 -> "🌱 Sprout"
            else -> "🔒 Unexplored"
        }
}

/**
 * Interactive Comparative Bar Chart rendered using Jetpack Compose Canvas graphics.
 * Displays problems solved (colored gradient bar) alongside total attempted problems (subtle track),
 * with dynamic gridlines, Y-axis value steps, category labels, and tap inspection.
 */
@Composable
fun CategoryComparativeBarChart(
    items: List<CategoryProgressItem>,
    modifier: Modifier = Modifier,
    onItemSelected: ((CategoryProgressItem) -> Unit)? = null
) {
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var animationTrigger by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        animationTrigger = true
    }

    val animatedProgress by animateFloatAsState(
        targetValue = if (animationTrigger) 1f else 0f,
        animationSpec = tween(durationMillis = 850, easing = FastOutSlowInEasing),
        label = "barAnimation"
    )

    // Calculate dynamic Y-axis maximum
    val maxSolvedOrAttempted = items.maxOfOrNull { maxOf(it.solvedCount, it.totalCount) } ?: 0
    val yAxisMax = when {
        maxSolvedOrAttempted <= 10 -> 10
        maxSolvedOrAttempted <= 25 -> 25
        maxSolvedOrAttempted <= 50 -> 50
        maxSolvedOrAttempted <= 100 -> 100
        else -> ((maxSolvedOrAttempted + 24) / 25) * 25
    }

    val steps = 4
    val gridStepValue = yAxisMax / steps

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Problems Solved by Category 📊",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Vibrant bar = Solved • Light track = Total Attempted",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }

                // Legend indicator
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(JunglePrimary)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Solved", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = JunglePrimary)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Canvas Chart Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
            ) {
                val gridColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                val textColor = MaterialTheme.colorScheme.onSurfaceVariant.toArgb()

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                        .testTag("category_bar_chart_canvas")
                        .pointerInput(items) {
                            detectTapGestures { offset ->
                                val leftPadding = 80f
                                val rightPadding = 40f
                                val chartWidth = size.width - leftPadding - rightPadding
                                val barSlotWidth = chartWidth / items.size

                                if (offset.x >= leftPadding && offset.x <= size.width - rightPadding) {
                                    val clickedIndex = ((offset.x - leftPadding) / barSlotWidth).toInt()
                                    if (clickedIndex in items.indices) {
                                        selectedIndex = if (selectedIndex == clickedIndex) null else clickedIndex
                                        selectedIndex?.let { onItemSelected?.invoke(items[it]) }
                                    }
                                }
                            }
                        }
                ) {
                    val width = size.width
                    val height = size.height
                    val leftPadding = 80f
                    val rightPadding = 30f
                    val topPadding = 24f
                    val bottomPadding = 42f

                    val chartHeight = height - topPadding - bottomPadding
                    val chartWidth = width - leftPadding - rightPadding

                    // 1. Draw horizontal dotted/dashed grid lines and Y-axis scale labels
                    val paint = Paint().apply {
                        this.color = textColor
                        this.textSize = 28f
                        this.textAlign = Paint.Align.RIGHT
                        this.isAntiAlias = true
                    }

                    for (i in 0..steps) {
                        val fraction = i.toFloat() / steps
                        val y = topPadding + chartHeight * (1f - fraction)
                        val labelVal = (gridStepValue * i).toString()

                        // Grid line
                        drawLine(
                            color = gridColor,
                            start = Offset(leftPadding, y),
                            end = Offset(width - rightPadding, y),
                            strokeWidth = 1.5f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                        )

                        // Y-axis label
                        drawContext.canvas.nativeCanvas.drawText(
                            labelVal,
                            leftPadding - 14f,
                            y + 10f,
                            paint
                        )
                    }

                    // 2. Draw Bars for each category
                    val barSlotWidth = chartWidth / items.size
                    val barWidth = barSlotWidth * 0.44f

                    items.forEachIndexed { index, item ->
                        val centerX = leftPadding + (index + 0.5f) * barSlotWidth
                        val barLeft = centerX - (barWidth / 2f)

                        // Attempted total height
                        val totalFraction = (item.totalCount.toFloat() / yAxisMax).coerceIn(0f, 1f) * animatedProgress
                        val totalBarHeight = chartHeight * totalFraction
                        val totalBarTop = topPadding + chartHeight - totalBarHeight

                        // Solved height
                        val solvedFraction = (item.solvedCount.toFloat() / yAxisMax).coerceIn(0f, 1f) * animatedProgress
                        val solvedBarHeight = chartHeight * solvedFraction
                        val solvedBarTop = topPadding + chartHeight - solvedBarHeight

                        val isSelected = (selectedIndex == index)

                        // Highlight background if selected
                        if (isSelected) {
                            drawRoundRect(
                                color = item.color.copy(alpha = 0.12f),
                                topLeft = Offset(centerX - barSlotWidth * 0.48f, topPadding),
                                size = Size(barSlotWidth * 0.96f, chartHeight + 36f),
                                cornerRadius = CornerRadius(14f, 14f)
                            )
                        }

                        // Background Track for Total Attempted
                        if (item.totalCount > 0) {
                            drawRoundRect(
                                color = item.color.copy(alpha = 0.22f),
                                topLeft = Offset(barLeft, totalBarTop),
                                size = Size(barWidth, totalBarHeight),
                                cornerRadius = CornerRadius(12f, 12f)
                            )
                        } else {
                            // Baseline placeholder when 0 attempted
                            drawRoundRect(
                                color = Color.LightGray.copy(alpha = 0.35f),
                                topLeft = Offset(barLeft, topPadding + chartHeight - 8f),
                                size = Size(barWidth, 8f),
                                cornerRadius = CornerRadius(4f, 4f)
                            )
                        }

                        // Solved Bar with Gradient
                        if (item.solvedCount > 0) {
                            val gradient = Brush.verticalGradient(
                                colors = listOf(
                                    item.color,
                                    item.color.copy(alpha = 0.82f)
                                ),
                                startY = solvedBarTop,
                                endY = topPadding + chartHeight
                            )

                            drawRoundRect(
                                brush = gradient,
                                topLeft = Offset(barLeft, solvedBarTop),
                                size = Size(barWidth, solvedBarHeight),
                                cornerRadius = CornerRadius(12f, 12f)
                            )
                        }

                        // Value badge above the bar
                        val countText = "${item.solvedCount}"
                        val valuePaint = Paint().apply {
                            this.color = item.color.toArgb()
                            this.textSize = 30f
                            this.textAlign = Paint.Align.CENTER
                            this.isFakeBoldText = true
                            this.isAntiAlias = true
                        }
                        val valueY = if (item.solvedCount > 0) (solvedBarTop - 8f).coerceAtLeast(topPadding) else (topPadding + chartHeight - 12f)
                        drawContext.canvas.nativeCanvas.drawText(
                            countText,
                            centerX,
                            valueY,
                            valuePaint
                        )

                        // X-axis category emoji and label
                        val categoryPaint = Paint().apply {
                            this.color = textColor
                            this.textSize = 26f
                            this.textAlign = Paint.Align.CENTER
                            this.isAntiAlias = true
                        }
                        drawContext.canvas.nativeCanvas.drawText(
                            item.emoji,
                            centerX,
                            height - 18f,
                            categoryPaint
                        )
                    }
                }
            }

            // Interactive Details Spotlight if an item is tapped
            selectedIndex?.let { index ->
                val item = items[index]
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = item.color.copy(alpha = 0.12f),
                    border = BorderStroke(1.5.dp, item.color),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = item.animalEmoji, fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "${item.title} (${item.animalName})",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = item.color
                                )
                                Text(
                                    text = "${item.solvedCount} solved out of ${item.totalCount} attempted",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = item.color
                        ) {
                            Text(
                                text = "${item.accuracyPct}% Accuracy",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Donut / Radial Distribution Chart rendered with Compose Canvas graphics.
 * Displays the distribution proportion of solved math problems across all topics.
 */
@Composable
fun CategoryDonutDistributionChart(
    items: List<CategoryProgressItem>,
    modifier: Modifier = Modifier,
    onItemSelected: ((CategoryProgressItem) -> Unit)? = null
) {
    val totalSolvedAll = items.sumOf { it.solvedCount }
    var selectedTopicId by remember { mutableStateOf<String?>(null) }
    var animTrigger by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        animTrigger = true
    }

    val animProgress by animateFloatAsState(
        targetValue = if (animTrigger) 1f else 0f,
        animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
        label = "donutAnimation"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Skill Distribution 🍩",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Proportion of solved problems across all areas",
                fontSize = 11.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (totalSolvedAll == 0) {
                // Empty state graphic
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🌱", fontSize = 42.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Solve math problems to grow your donut chart!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    // Compose Canvas Radial Donut
                    Box(
                        modifier = Modifier
                            .size(170.dp)
                            .testTag("category_donut_canvas_box"),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(160.dp)) {
                            val strokeWidth = 32f
                            val arcRadius = (size.minDimension - strokeWidth) / 2f
                            val center = Offset(size.width / 2f, size.height / 2f)

                            var currentStartAngle = -90f
                            val gapAngle = 3f

                            items.forEach { item ->
                                if (item.solvedCount > 0) {
                                    val sweepFraction = item.solvedCount.toFloat() / totalSolvedAll
                                    val fullSweep = sweepFraction * 360f * animProgress
                                    val sweepAngle = (fullSweep - gapAngle).coerceAtLeast(1f)
                                    val isSelected = selectedTopicId == item.topic.id

                                    drawArc(
                                        color = if (isSelected) item.color else item.color.copy(alpha = 0.9f),
                                        startAngle = currentStartAngle,
                                        sweepAngle = sweepAngle,
                                        useCenter = false,
                                        topLeft = Offset(center.x - arcRadius, center.y - arcRadius),
                                        size = Size(arcRadius * 2f, arcRadius * 2f),
                                        style = Stroke(
                                            width = if (isSelected) strokeWidth + 8f else strokeWidth,
                                            cap = StrokeCap.Round
                                        )
                                    )

                                    currentStartAngle += fullSweep
                                }
                            }
                        }

                        // Center mascot / total indicator
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val activeItem = items.firstOrNull { it.topic.id == selectedTopicId }
                            if (activeItem != null) {
                                Text(text = activeItem.animalEmoji, fontSize = 24.sp)
                                Text(
                                    text = "${activeItem.solvedCount}",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = activeItem.color
                                )
                                Text(
                                    text = "${activeItem.title}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray
                                )
                            } else {
                                Text(text = "🏆", fontSize = 22.sp)
                                Text(
                                    text = "$totalSolvedAll",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 20.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Total Solved",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.Gray
                                )
                            }
                        }
                    }

                    // Legend & distribution column
                    Column(
                        modifier = Modifier.padding(start = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items.forEach { item ->
                            val pct = if (totalSolvedAll > 0) (item.solvedCount.toFloat() / totalSolvedAll * 100).toInt() else 0
                            val isSelected = selectedTopicId == item.topic.id

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) item.color.copy(alpha = 0.16f) else Color.Transparent,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        selectedTopicId = if (isSelected) null else item.topic.id
                                        onItemSelected?.invoke(item)
                                    }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .clip(CircleShape)
                                            .background(item.color)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${item.emoji} ${item.title}: ",
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium
                                    )
                                    Text(
                                        text = "${item.solvedCount} ($pct%)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = item.color
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Concentric Mastery Rings Chart rendered using Compose Canvas graphics.
 * Displays progress rings towards a 50-problem mastery goal across all categories.
 */
@Composable
fun CategoryMasteryRingsChart(
    items: List<CategoryProgressItem>,
    masteryGoal: Int = 30,
    modifier: Modifier = Modifier
) {
    var animTrigger by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        animTrigger = true
    }

    val animProgress by animateFloatAsState(
        targetValue = if (animTrigger) 1f else 0f,
        animationSpec = tween(durationMillis = 950, easing = FastOutSlowInEasing),
        label = "ringsAnimation"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Mastery Rings 🎯",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Concentric goals toward $masteryGoal problems mastered",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = JunglePrimary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "Goal: $masteryGoal ★",
                        color = JunglePrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // Multi-Ring Canvas
                Box(
                    modifier = Modifier.size(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(160.dp)) {
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val ringStroke = 12f
                        val gap = 6f

                        items.forEachIndexed { index, item ->
                            // From outermost to innermost ring
                            val radius = (size.minDimension / 2f) - (index * (ringStroke + gap)) - (ringStroke / 2f)
                            if (radius > 10f) {
                                // Background track
                                drawCircle(
                                    color = item.color.copy(alpha = 0.18f),
                                    radius = radius,
                                    center = center,
                                    style = Stroke(width = ringStroke)
                                )

                                // Progress Arc
                                val fraction = (item.solvedCount.toFloat() / masteryGoal).coerceIn(0f, 1f) * animProgress
                                val sweep = fraction * 360f

                                if (sweep > 0f) {
                                    drawArc(
                                        color = item.color,
                                        startAngle = -90f,
                                        sweepAngle = sweep,
                                        useCenter = false,
                                        topLeft = Offset(center.x - radius, center.y - radius),
                                        size = Size(radius * 2f, radius * 2f),
                                        style = Stroke(width = ringStroke, cap = StrokeCap.Round)
                                    )
                                }
                            }
                        }
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "👑", fontSize = 24.sp)
                        Text(
                            text = "Safari",
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            color = JunglePrimary
                        )
                    }
                }

                // Legend Column
                Column(
                    modifier = Modifier.padding(start = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items.forEach { item ->
                        val pct = ((item.solvedCount.toFloat() / masteryGoal) * 100).toInt().coerceAtMost(100)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(item.color)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${item.emoji} ${item.title}: ",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${item.solvedCount}/$masteryGoal ($pct%)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = item.color
                            )
                        }
                    }
                }
            }
        }
    }
}
