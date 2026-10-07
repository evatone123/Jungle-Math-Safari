package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.data.model.MathTopic
import com.example.data.model.Question
import com.example.data.model.QuestionType
import com.example.ui.components.AnimalReactionState
import com.example.ui.components.AnimatedAnimalCharacter
import com.example.ui.viewmodel.GameSessionState
import com.example.ui.theme.AdventureOrange
import com.example.ui.theme.BananaYellow
import com.example.ui.theme.EncouragementGreen
import com.example.ui.theme.GentleRetryOrange
import com.example.ui.theme.JunglePrimary
import com.example.ui.theme.SafariGold

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GameScreen(
    session: GameSessionState,
    onSelectOption: (Int) -> Unit,
    onIncrementInteractive: () -> Unit,
    onDecrementInteractive: () -> Unit,
    onSubmitInteractive: () -> Unit,
    onTutorHelp: () -> Unit,
    onNextQuestion: () -> Unit,
    onQuit: () -> Unit,
    isMusicOn: Boolean = true,
    onMusicToggle: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val currentQ = session.questions.getOrNull(session.currentQuestionIndex)
    val topic = session.topic
    val topicColor = Color(topic.colorHex)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val isWideScreen = maxWidth >= 700.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Game Header: Quit button, Question X of 5, Score counter & Music toggle
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 1100.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onQuit,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .testTag("game_quit_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Quit Game",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (session.isDailyChallenge) "Daily Challenge" else "${topic.title} • Lvl ${session.levelNumber}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = topicColor
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(session.difficulty.colorHex).copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "${session.difficulty.emoji} ${session.difficulty.title}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(session.difficulty.colorHex),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Question ${session.currentQuestionIndex + 1} of ${session.questions.size}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onMusicToggle,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isMusicOn) BananaYellow.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface)
                                .testTag("game_toggle_music_button")
                        ) {
                            Text(
                                text = if (isMusicOn) "🎵" else "🔇",
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = BananaYellow.copy(alpha = 0.3f)
                        ) {
                            Text(
                                text = "🪙 ${session.score}",
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                color = Color(0xFF6D4C41),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 1100.dp),
                contentAlignment = Alignment.Center
            ) {
                LinearProgressIndicator(
                    progress = {
                        if (session.questions.isNotEmpty()) {
                            (session.currentQuestionIndex + 1).toFloat() / session.questions.size
                        } else 0f
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = topicColor,
                    trackColor = Color.LightGray.copy(alpha = 0.4f)
                )
            }

            if (currentQ != null) {
                Spacer(modifier = Modifier.height(10.dp))

                // Reaction state for the animated companion character
                val reactionState = when {
                    session.isAnswerChecked && session.isAnswerCorrect == true -> AnimalReactionState.CORRECT_CELEBRATION
                    session.isAnswerChecked && session.isAnswerCorrect == false -> AnimalReactionState.ENCOURAGING_RETRY
                    else -> AnimalReactionState.IDLE
                }

                if (isWideScreen) {
                    // Wide / Tablet / Landscape: Side-by-side Question & Answer Panes
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 1150.dp)
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        // Left Pane: Companion Character & Question Prompt & Visuals
                        Column(
                            modifier = Modifier.weight(1.05f),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            AnimatedAnimalCharacter(
                                reactionState = reactionState,
                                topic = topic,
                                modifier = Modifier.fillMaxWidth()
                            )

                            QuestionPromptCard(
                                question = currentQ,
                                topic = topic,
                                topicColor = topicColor
                            )
                        }

                        // Right Pane: Input Controls, Tutor Hint, Feedback & Next Button
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (currentQ.questionType == QuestionType.FEED_ANIMAL) {
                                FeedAnimalInteractiveMode(
                                    targetCount = currentQ.correctAnswer,
                                    currentCount = session.interactiveCount,
                                    itemEmoji = currentQ.itemEmoji,
                                    animalEmoji = topic.animalEmoji,
                                    isAnswerChecked = session.isAnswerChecked,
                                    onIncrement = onIncrementInteractive,
                                    onDecrement = onDecrementInteractive,
                                    onSubmit = onSubmitInteractive
                                )
                            } else {
                                MultipleChoiceOptions(
                                    options = currentQ.options,
                                    correctAnswer = currentQ.correctAnswer,
                                    selectedOption = session.selectedOption,
                                    isAnswerChecked = session.isAnswerChecked,
                                    onSelect = onSelectOption
                                )
                            }

                            TutorHelpButton(
                                isEnabled = !session.isAnswerChecked,
                                onClick = onTutorHelp
                            )

                            AnswerFeedbackCard(
                                session = session,
                                topic = topic,
                                currentQ = currentQ,
                                onNextQuestion = onNextQuestion
                            )
                        }
                    }
                } else {
                    // Phone Portrait: Single Column Streamlined
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 560.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AnimatedAnimalCharacter(
                            reactionState = reactionState,
                            topic = topic,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        QuestionPromptCard(
                            question = currentQ,
                            topic = topic,
                            topicColor = topicColor
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (currentQ.questionType == QuestionType.FEED_ANIMAL) {
                            FeedAnimalInteractiveMode(
                                targetCount = currentQ.correctAnswer,
                                currentCount = session.interactiveCount,
                                itemEmoji = currentQ.itemEmoji,
                                animalEmoji = topic.animalEmoji,
                                isAnswerChecked = session.isAnswerChecked,
                                onIncrement = onIncrementInteractive,
                                onDecrement = onDecrementInteractive,
                                onSubmit = onSubmitInteractive
                            )
                        } else {
                            MultipleChoiceOptions(
                                options = currentQ.options,
                                correctAnswer = currentQ.correctAnswer,
                                selectedOption = session.selectedOption,
                                isAnswerChecked = session.isAnswerChecked,
                                onSelect = onSelectOption
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        TutorHelpButton(
                            isEnabled = !session.isAnswerChecked,
                            onClick = onTutorHelp
                        )

                        AnswerFeedbackCard(
                            session = session,
                            topic = topic,
                            currentQ = currentQ,
                            onNextQuestion = onNextQuestion
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun QuestionPromptCard(
    question: Question,
    topic: MathTopic,
    topicColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Companion Speech Bubble
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = topic.animalEmoji, fontSize = 36.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = question.promptText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 22.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Visual Representation Area
            VisualProblemArea(question = question)

            // Math Formula Display (e.g. "3 + 2 = ?")
            if (question.formulaText.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = topicColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = question.formulaText,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = topicColor,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TutorHelpButton(
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = SafariGold.copy(alpha = 0.15f),
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .clickable(enabled = isEnabled) { onClick() }
                .testTag("tutor_help_button")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "🦉", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Ask Safari Tutor for a Hint",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFFE65100)
                )
            }
        }
    }
}

@Composable
private fun AnswerFeedbackCard(
    session: GameSessionState,
    topic: MathTopic,
    currentQ: Question,
    onNextQuestion: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = session.isAnswerChecked,
        enter = fadeIn() + slideInVertically(),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            val isCorrect = session.isAnswerCorrect == true
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("answer_feedback_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCorrect) EncouragementGreen.copy(alpha = 0.15f) else GentleRetryOrange.copy(alpha = 0.15f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isCorrect) topic.animalEmoji else "💡",
                        fontSize = 32.sp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isCorrect) "Fantastic! +100 Points 🎉" else "Almost! Correct was ${currentQ.correctAnswer}",
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = if (isCorrect) EncouragementGreen else GentleRetryOrange
                        )
                        Text(
                            text = if (isCorrect) "${topic.animalEmoji} says: Super math explorer!" else "Keep practicing, explorer!",
                            fontSize = 13.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onNextQuestion,
                colors = ButtonDefaults.buttonColors(containerColor = JunglePrimary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("next_question_button")
            ) {
                Text(
                    text = if (session.currentQuestionIndex + 1 < session.questions.size) "Next Question ➜" else "Finish Adventure! 🏆",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun VisualProblemArea(question: Question) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFFF9FBF7),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.LightGray.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
            .padding(12.dp)
    ) {
        when (question.topic) {
            MathTopic.COUNTING -> {
                // Show grid of counting items
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalArrangement = Arrangement.Center
                ) {
                    for (i in 1..question.countA) {
                        Text(
                            text = question.itemEmoji,
                            fontSize = 32.sp,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            }
            MathTopic.ADDITION -> {
                // Show Group A + Group B
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Group A
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        FlowRow(maxItemsInEachRow = 4) {
                            for (i in 1..question.countA) {
                                Text(text = question.itemEmoji, fontSize = 26.sp, modifier = Modifier.padding(2.dp))
                            }
                        }
                        Text(text = "${question.countA}", fontWeight = FontWeight.Bold, color = JunglePrimary)
                    }

                    Text(
                        text = " + ",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = AdventureOrange,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    // Group B
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        FlowRow(maxItemsInEachRow = 4) {
                            for (i in 1..question.countB) {
                                Text(text = question.itemEmoji, fontSize = 26.sp, modifier = Modifier.padding(2.dp))
                            }
                        }
                        Text(text = "${question.countB}", fontWeight = FontWeight.Bold, color = JunglePrimary)
                    }
                }
            }
            MathTopic.SUBTRACTION -> {
                // Show Total A with B crossed out / dimmed
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        val remaining = question.countA - question.countB
                        for (i in 1..remaining) {
                            Text(text = question.itemEmoji, fontSize = 30.sp, modifier = Modifier.padding(4.dp))
                        }
                        for (i in 1..question.countB) {
                            // Dimmed / cross-marked
                            Text(text = "❌", fontSize = 24.sp, modifier = Modifier.padding(4.dp))
                        }
                    }
                    Text(
                        text = "${question.countA} coconuts with ${question.countB} shared away",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            MathTopic.MULTIPLICATION -> {
                // Show groups/branches
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (g in 1..question.countA) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White,
                                border = CardDefaults.outlinedCardBorder(),
                                modifier = Modifier.padding(2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    FlowRow(maxItemsInEachRow = 2) {
                                        for (it in 1..question.countB) {
                                            Text(text = question.itemEmoji, fontSize = 20.sp)
                                        }
                                    }
                                    Text(text = "x${question.countB}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                    Text(
                        text = "${question.countA} groups of ${question.countB}",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MultipleChoiceOptions(
    options: List<Int>,
    correctAnswer: Int,
    selectedOption: Int?,
    isAnswerChecked: Boolean,
    onSelect: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        val rows = options.chunked(2)
        rows.forEach { pair ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                pair.forEach { option ->
                    val isSelected = (selectedOption == option)
                    val isThisCorrect = (option == correctAnswer)

                    val bgColor = when {
                        !isAnswerChecked && isSelected -> SafariGold.copy(alpha = 0.25f)
                        isAnswerChecked && isThisCorrect -> EncouragementGreen.copy(alpha = 0.2f)
                        isAnswerChecked && isSelected && !isThisCorrect -> GentleRetryOrange.copy(alpha = 0.2f)
                        else -> MaterialTheme.colorScheme.surface
                    }

                    val borderColor = when {
                        !isAnswerChecked && isSelected -> SafariGold
                        isAnswerChecked && isThisCorrect -> EncouragementGreen
                        isAnswerChecked && isSelected && !isThisCorrect -> GentleRetryOrange
                        else -> Color.LightGray.copy(alpha = 0.5f)
                    }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(68.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .clickable(enabled = !isAnswerChecked) { onSelect(option) }
                            .border(2.dp, borderColor, RoundedCornerShape(18.dp))
                            .testTag("option_button_$option"),
                        color = bgColor,
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = "$option",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FeedAnimalInteractiveMode(
    targetCount: Int,
    currentCount: Int,
    itemEmoji: String,
    animalEmoji: String,
    isAnswerChecked: Boolean,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onSubmit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Tap to fill the treat basket! 🧺",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Basket display
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = BananaYellow.copy(alpha = 0.2f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .border(2.dp, SafariGold, RoundedCornerShape(18.dp))
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (currentCount == 0) {
                        Text(
                            text = "Basket is empty! Tap + below",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    } else {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🧺", fontSize = 32.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = itemEmoji.repeat(currentCount.coerceAtMost(8)),
                                fontSize = 26.sp
                            )
                            if (currentCount > 8) {
                                Text(text = " +${currentCount - 8}", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Counter controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onDecrement,
                    enabled = !isAnswerChecked && currentCount > 0,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray.copy(alpha = 0.3f))
                        .testTag("interactive_minus_button")
                ) {
                    Icon(imageVector = Icons.Default.Remove, contentDescription = "Remove one")
                }

                Text(
                    text = "$currentCount",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = JunglePrimary
                )

                IconButton(
                    onClick = onIncrement,
                    enabled = !isAnswerChecked,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .testTag("interactive_plus_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add one", tint = JunglePrimary)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (!isAnswerChecked) {
                Button(
                    onClick = onSubmit,
                    colors = ButtonDefaults.buttonColors(containerColor = JunglePrimary),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("interactive_submit_button")
                ) {
                    Text(text = "Feed $currentCount Treats! 🍌", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
