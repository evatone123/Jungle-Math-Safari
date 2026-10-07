package com.example.data.repository

import com.example.data.model.DifficultyLevel
import com.example.data.model.MathTopic
import com.example.data.model.Question
import com.example.data.model.QuestionType
import kotlin.random.Random

object QuestionEngine {

    fun generateQuestionsForLevel(
        topic: MathTopic,
        level: Int,
        difficulty: DifficultyLevel = DifficultyLevel.MEDIUM,
        count: Int = 5
    ): List<Question> {
        return (1..count).map { index ->
            when (topic) {
                MathTopic.COUNTING -> generateCountingQuestion(level, index, difficulty)
                MathTopic.ADDITION -> generateAdditionQuestion(level, index, difficulty)
                MathTopic.SUBTRACTION -> generateSubtractionQuestion(level, index, difficulty)
                MathTopic.MULTIPLICATION -> generateMultiplicationQuestion(level, index, difficulty)
            }
        }
    }

    fun generateDailyChallengeQuestions(difficulty: DifficultyLevel = DifficultyLevel.MEDIUM): List<Question> {
        val baseLvl = when (difficulty) {
            DifficultyLevel.EASY -> 1
            DifficultyLevel.MEDIUM -> 3
            DifficultyLevel.HARD -> 5
        }
        return listOf(
            generateCountingQuestion(level = baseLvl, index = 1, difficulty = difficulty),
            generateAdditionQuestion(level = baseLvl, index = 2, difficulty = difficulty),
            generateAdditionQuestion(level = baseLvl + 1, index = 3, difficulty = difficulty),
            generateSubtractionQuestion(level = baseLvl, index = 4, difficulty = difficulty),
            generateMultiplicationQuestion(level = baseLvl, index = 5, difficulty = difficulty)
        )
    }

    // 1. COUNTING ENGINE
    private fun generateCountingQuestion(level: Int, index: Int, difficulty: DifficultyLevel): Question {
        val (minCount, maxCount) = when (difficulty) {
            DifficultyLevel.EASY -> {
                // Easy: 1 to 5 (early levels), max 7
                val max = when {
                    level <= 2 -> 4
                    level <= 5 -> 5
                    else -> 7
                }
                Pair(1, max)
            }
            DifficultyLevel.MEDIUM -> {
                // Medium: 2 to 14
                val max = when {
                    level <= 2 -> 7
                    level <= 5 -> 10
                    else -> 14
                }
                Pair(2, max)
            }
            DifficultyLevel.HARD -> {
                // Hard: 6 to 25 items!
                val max = when {
                    level <= 2 -> 15
                    level <= 5 -> 20
                    else -> 25
                }
                Pair(maxOf(5, level + 3), max)
            }
        }

        val targetCount = Random.nextInt(minCount, maxCount + 1)
        val items = listOf("🍌", "🥭", "🍍", "🥥", "🍉", "🍇", "🥝")
        val emoji = items[(index + level) % items.size]

        val questionType = when (difficulty) {
            DifficultyLevel.EASY -> if (index % 2 == 0) QuestionType.FEED_ANIMAL else QuestionType.TAP_COUNT
            DifficultyLevel.MEDIUM -> if (index % 3 == 0) QuestionType.FEED_ANIMAL else if (index % 2 == 0) QuestionType.TAP_COUNT else QuestionType.MULTIPLE_CHOICE
            DifficultyLevel.HARD -> if (index % 2 == 0) QuestionType.MULTIPLE_CHOICE else QuestionType.TAP_COUNT
        }

        val prompt = when {
            questionType == QuestionType.FEED_ANIMAL -> "Feed Kiki the monkey exactly $targetCount $emoji treats!"
            difficulty == DifficultyLevel.HARD -> "Scan the jungle canopy carefully! How many $emoji do you count in total?"
            else -> "How many $emoji can you count in the jungle?"
        }

        val distractors = generateDistractors(targetCount, min = maxOf(1, targetCount - 5), max = targetCount + 5)
        val options = (distractors + targetCount).shuffled()

        val hints = listOf(
            "Point to each $emoji one by one with your finger!",
            if (difficulty == DifficultyLevel.HARD) "Tip: Try counting by groups of 2 or 5 to scan faster!" else "Count slowly from 1 up to the total.",
            "Kiki says: There are exactly $targetCount $emoji! Choose $targetCount."
        )

        return Question(
            id = "count_${difficulty.id}_${level}_${index}_${System.currentTimeMillis()}",
            topic = MathTopic.COUNTING,
            difficulty = level,
            difficultyLevel = difficulty,
            questionType = questionType,
            promptText = prompt,
            itemEmoji = emoji,
            countA = targetCount,
            countB = 0,
            operatorSymbol = "",
            formulaText = "",
            correctAnswer = targetCount,
            options = options,
            progressiveHints = hints
        )
    }

    // 2. ADDITION ENGINE
    private fun generateAdditionQuestion(level: Int, index: Int, difficulty: DifficultyLevel): Question {
        val (a, b, isMissingAddend) = when (difficulty) {
            DifficultyLevel.EASY -> {
                // Easy: Single digit, sums up to 8 (e.g. 1-4 + 1-4)
                val maxPart = if (level <= 2) 3 else 4
                val valA = Random.nextInt(1, maxPart + 1)
                val valB = Random.nextInt(1, maxPart + 1)
                Triple(valA, valB, false)
            }
            DifficultyLevel.MEDIUM -> {
                // Medium: Standard additions, sums up to 18 (e.g. 3-10 + 2-8)
                val (maxA, maxB) = when {
                    level <= 2 -> Pair(6, 5)
                    level <= 5 -> Pair(10, 8)
                    else -> Pair(12, 10)
                }
                val valA = Random.nextInt(2, maxA + 1)
                val valB = Random.nextInt(2, maxB + 1)
                Triple(valA, valB, false)
            }
            DifficultyLevel.HARD -> {
                // Hard: Double-digit & multi-step additions (sums up to 45)!
                // Every 3rd question is a missing addend challenge: A + ? = Sum
                val isMissing = (index % 3 == 0)
                val valA = Random.nextInt(10, 18 + level * 2)
                val valB = Random.nextInt(6, 15 + level)
                Triple(valA, valB, isMissing)
            }
        }

        val sum = a + b
        val answer = if (isMissingAddend) b else sum

        val fruits = listOf("🍎", "🍓", "🍊", "🍇", "🍒", "🍑")
        val emoji = fruits[(index + level) % fruits.size]

        val distractors = generateDistractors(answer, min = maxOf(1, answer - 6), max = answer + 6)
        val options = (distractors + answer).shuffled()

        val prompt = if (isMissingAddend) {
            "Leo has $a $emoji and wants $sum altogether. How many more $emoji does Leo need?"
        } else {
            "Leo gathered $a $emoji and discovered $b more. How many altogether?"
        }

        val formula = if (isMissingAddend) "$a + ? = $sum" else "$a + $b = ?"

        val hints = listOf(
            if (isMissingAddend) "Start at $a and count up until you reach $sum!" else "Leo roars: Start at $a and count $b more forward!",
            if (isMissingAddend) "$sum minus $a will give you the missing number: ${sum - a}." else "Add the ones and tens: $a + $b = $sum.",
            "Leo roars proudly: The correct answer is $answer!"
        )

        return Question(
            id = "add_${difficulty.id}_${level}_${index}_${System.currentTimeMillis()}",
            topic = MathTopic.ADDITION,
            difficulty = level,
            difficultyLevel = difficulty,
            questionType = QuestionType.VISUAL_GROUP,
            promptText = prompt,
            itemEmoji = emoji,
            countA = a,
            countB = b,
            operatorSymbol = "+",
            formulaText = formula,
            correctAnswer = answer,
            options = options,
            progressiveHints = hints
        )
    }

    // 3. SUBTRACTION ENGINE
    private fun generateSubtractionQuestion(level: Int, index: Int, difficulty: DifficultyLevel): Question {
        val (a, b, isMissingSubtrahend) = when (difficulty) {
            DifficultyLevel.EASY -> {
                // Easy: Minuend 2 to 7, Subtrahend 1 to 3
                val valA = Random.nextInt(3, if (level <= 2) 6 else 8)
                val valB = Random.nextInt(1, minOf(valA, 3) + 1)
                Triple(valA, valB, false)
            }
            DifficultyLevel.MEDIUM -> {
                // Medium: Minuend up to 16, Subtrahend up to 8
                val maxTot = when {
                    level <= 2 -> 9
                    level <= 5 -> 14
                    else -> 18
                }
                val valA = Random.nextInt(4, maxTot + 1)
                val valB = Random.nextInt(1, minOf(valA - 1, 8) + 1)
                Triple(valA, valB, false)
            }
            DifficultyLevel.HARD -> {
                // Hard: Minuend 16 to 38, Subtrahend 5 to 18 (multi-digit subtraction & missing parts)
                val isMissing = (index % 3 == 0)
                val valA = Random.nextInt(16, 26 + level * 2)
                val valB = Random.nextInt(5, minOf(valA - 3, 16 + level))
                Triple(valA, valB, isMissing)
            }
        }

        val difference = a - b
        val answer = if (isMissingSubtrahend) b else difference

        val items = listOf("🥥", "🥜", "🌰", "🍃", "🪵", "🍄")
        val emoji = items[(index + level) % items.size]

        val distractors = generateDistractors(answer, min = maxOf(0, answer - 5), max = answer + 6)
        val options = (distractors + answer).shuffled()

        val prompt = if (isMissingSubtrahend) {
            "Tembo started with $a $emoji and now has $difference left. How many $emoji were shared away?"
        } else {
            "Tembo has $a $emoji. If $b are shared with friends, how many are left?"
        }

        val formula = if (isMissingSubtrahend) "$a − ? = $difference" else "$a − $b = ?"

        val hints = listOf(
            if (isMissingSubtrahend) "Count back from $a down to $difference to find how many were removed!" else "Start at $a and take away $b: count backwards.",
            if (isMissingSubtrahend) "$a − $difference = ${a - difference} shared away!" else "Subtracting: $a minus $b leaves $difference.",
            "Tembo trumpets: The answer is $answer!"
        )

        return Question(
            id = "sub_${difficulty.id}_${level}_${index}_${System.currentTimeMillis()}",
            topic = MathTopic.SUBTRACTION,
            difficulty = level,
            difficultyLevel = difficulty,
            questionType = QuestionType.VISUAL_GROUP,
            promptText = prompt,
            itemEmoji = emoji,
            countA = a,
            countB = b,
            operatorSymbol = "−",
            formulaText = formula,
            correctAnswer = answer,
            options = options,
            progressiveHints = hints
        )
    }

    // 4. MULTIPLICATION ENGINE
    private fun generateMultiplicationQuestion(level: Int, index: Int, difficulty: DifficultyLevel): Question {
        val (groups, itemsPerGroup) = when (difficulty) {
            DifficultyLevel.EASY -> {
                // Easy: Doubling and 2-3 groups of 1 to 4 (e.g. 2x2, 2x3, 3x2)
                val g = if (level <= 2) 2 else Random.nextInt(2, 4)
                val it = Random.nextInt(1, 5)
                Pair(g, it)
            }
            DifficultyLevel.MEDIUM -> {
                // Medium: Times tables 2, 3, 4, 5 (up to 5x6)
                val maxG = when {
                    level <= 2 -> 3
                    level <= 5 -> 4
                    else -> 5
                }
                val g = Random.nextInt(2, maxG + 1)
                val it = Random.nextInt(2, 6)
                Pair(g, it)
            }
            DifficultyLevel.HARD -> {
                // Hard: Full times tables including 6, 7, 8, 9 (e.g. 6x6, 7x8, 8x9)
                val g = Random.nextInt(3, minOf(9, 4 + level))
                val it = Random.nextInt(3, minOf(9, 4 + level))
                Pair(g, it)
            }
        }

        val answer = groups * itemsPerGroup

        val treats = listOf("🌿", "🌺", "🥑", "🌻", "🌴", "🪻")
        val emoji = treats[(index + level) % treats.size]

        val distractors = generateDistractors(answer, min = maxOf(1, answer - 8), max = answer + 9)
        val options = (distractors + answer).shuffled()

        val prompt = if (difficulty == DifficultyLevel.HARD) {
            "Twiga spotted $groups tall acacia trees, each canopy bearing $itemsPerGroup lush $emoji clusters. What is the grand total?"
        } else {
            "There are $groups branches. Each branch has $itemsPerGroup $emoji bunches. How many in total?"
        }

        val hints = listOf(
            "Twiga spots $groups branches with $itemsPerGroup $emoji on each!",
            "Think of repeated addition: ${(1..groups).map { itemsPerGroup }.joinToString(" + ")} = $answer.",
            "Twiga reaches high: $groups × $itemsPerGroup = $answer! Tap $answer."
        )

        return Question(
            id = "mult_${difficulty.id}_${level}_${index}_${System.currentTimeMillis()}",
            topic = MathTopic.MULTIPLICATION,
            difficulty = level,
            difficultyLevel = difficulty,
            questionType = QuestionType.VISUAL_GROUP,
            promptText = prompt,
            itemEmoji = emoji,
            countA = groups,
            countB = itemsPerGroup,
            operatorSymbol = "×",
            formulaText = "$groups × $itemsPerGroup = ?",
            correctAnswer = answer,
            options = options,
            progressiveHints = hints
        )
    }

    private fun generateDistractors(correct: Int, min: Int, max: Int): List<Int> {
        val set = mutableSetOf<Int>()
        // Close distractors
        val candidates = listOf(correct - 1, correct + 1, correct - 2, correct + 2, correct + 10, correct - 10, correct + 3, correct - 3)
            .filter { it in min..max && it != correct && it >= 0 }
            .shuffled()

        for (c in candidates) {
            set.add(c)
            if (set.size == 3) break
        }

        // Fill up to 3 distractors if needed
        var attempts = 0
        val safeMin = maxOf(0, min)
        val safeMax = maxOf(safeMin + 4, max)
        while (set.size < 3 && attempts < 25) {
            val r = Random.nextInt(safeMin, safeMax + 1)
            if (r != correct) set.add(r)
            attempts++
        }
        while (set.size < 3) {
            set.add(correct + set.size + 1)
        }
        return set.toList()
    }
}

