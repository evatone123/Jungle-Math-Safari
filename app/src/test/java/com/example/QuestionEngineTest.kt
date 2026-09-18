package com.example

import com.example.data.model.DifficultyLevel
import com.example.data.model.MathTopic
import com.example.data.repository.QuestionEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QuestionEngineTest {

    @Test
    fun testCountingQuestions() {
        val questions = QuestionEngine.generateQuestionsForLevel(MathTopic.COUNTING, level = 1, count = 5, difficulty = DifficultyLevel.EASY)
        assertEquals(5, questions.size)
        questions.forEach { q ->
            assertEquals(MathTopic.COUNTING, q.topic)
            assertEquals(DifficultyLevel.EASY, q.difficultyLevel)
            assertTrue(q.correctAnswer in 1..8)
            assertTrue(q.options.contains(q.correctAnswer))
            assertEquals(4, q.options.size)
        }
    }

    @Test
    fun testAdditionDifficultyLevels() {
        val easyQuestions = QuestionEngine.generateQuestionsForLevel(MathTopic.ADDITION, level = 1, count = 5, difficulty = DifficultyLevel.EASY)
        easyQuestions.forEach { q ->
            assertEquals(DifficultyLevel.EASY, q.difficultyLevel)
            assertEquals(q.countA + q.countB, q.correctAnswer)
            assertTrue(q.correctAnswer <= 12)
            assertTrue(q.options.contains(q.correctAnswer))
        }

        val hardQuestions = QuestionEngine.generateQuestionsForLevel(MathTopic.ADDITION, level = 5, count = 5, difficulty = DifficultyLevel.HARD)
        hardQuestions.forEach { q ->
            assertEquals(DifficultyLevel.HARD, q.difficultyLevel)
            if (q.formulaText.contains("? =")) {
                assertEquals(q.countB, q.correctAnswer)
            } else {
                assertEquals(q.countA + q.countB, q.correctAnswer)
            }
            assertTrue(q.options.contains(q.correctAnswer))
        }
    }

    @Test
    fun testSubtractionQuestions() {
        val questions = QuestionEngine.generateQuestionsForLevel(MathTopic.SUBTRACTION, level = 3, count = 5, difficulty = DifficultyLevel.MEDIUM)
        assertEquals(5, questions.size)
        questions.forEach { q ->
            assertEquals(MathTopic.SUBTRACTION, q.topic)
            assertEquals(q.countA - q.countB, q.correctAnswer)
            assertTrue(q.correctAnswer >= 0)
            assertTrue(q.options.contains(q.correctAnswer))
        }
    }

    @Test
    fun testMultiplicationQuestions() {
        val easyQuestions = QuestionEngine.generateQuestionsForLevel(MathTopic.MULTIPLICATION, level = 2, count = 5, difficulty = DifficultyLevel.EASY)
        easyQuestions.forEach { q ->
            assertEquals(MathTopic.MULTIPLICATION, q.topic)
            assertEquals(q.countA * q.countB, q.correctAnswer)
            assertTrue(q.countA in 1..5)
            assertTrue(q.countB in 1..5)
            assertTrue(q.options.contains(q.correctAnswer))
        }

        val hardQuestions = QuestionEngine.generateQuestionsForLevel(MathTopic.MULTIPLICATION, level = 4, count = 5, difficulty = DifficultyLevel.HARD)
        hardQuestions.forEach { q ->
            assertEquals(MathTopic.MULTIPLICATION, q.topic)
            assertEquals(q.countA * q.countB, q.correctAnswer)
            assertTrue(q.options.contains(q.correctAnswer))
        }
    }

    @Test
    fun testDailyChallengeQuestions() {
        val easyDaily = QuestionEngine.generateDailyChallengeQuestions(DifficultyLevel.EASY)
        assertEquals(5, easyDaily.size)
        assertEquals(DifficultyLevel.EASY, easyDaily[0].difficultyLevel)

        val hardDaily = QuestionEngine.generateDailyChallengeQuestions(DifficultyLevel.HARD)
        assertEquals(5, hardDaily.size)
        assertEquals(DifficultyLevel.HARD, hardDaily[0].difficultyLevel)
    }
}
