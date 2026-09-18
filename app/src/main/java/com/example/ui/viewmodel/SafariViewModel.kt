package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SafariAudioEngine
import com.example.data.local.entities.ChildProfileEntity
import com.example.data.local.entities.DailyRecordEntity
import com.example.data.local.entities.LevelProgressEntity
import com.example.data.local.entities.UserStatsEntity
import com.example.data.model.AnimalCompanion
import com.example.data.model.AnimalSticker
import com.example.data.model.DifficultyLevel
import com.example.data.model.GameLevelSummary
import com.example.data.model.MathTopic
import com.example.data.model.Question
import com.example.data.model.SafariBadge
import com.example.data.remote.GeminiTutorService
import com.example.data.repository.QuestionEngine
import com.example.data.repository.SafariRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

data class GameSessionState(
    val topic: MathTopic = MathTopic.COUNTING,
    val levelNumber: Int = 1,
    val difficulty: DifficultyLevel = DifficultyLevel.MEDIUM,
    val questions: List<Question> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val score: Int = 0,
    val correctCount: Int = 0,
    val hintsUsedInCurrentQuestion: Int = 0,
    val totalHintsUsed: Int = 0,
    val selectedOption: Int? = null,
    val interactiveCount: Int = 0,
    val isAnswerChecked: Boolean = false,
    val isAnswerCorrect: Boolean? = null,
    val isSessionComplete: Boolean = false,
    val isDailyChallenge: Boolean = false,
    val summary: GameLevelSummary? = null,
    val tutorMessage: String? = null,
    val tutorStep: Int = 0,
    val isTutorLoading: Boolean = false,
    val showTutorSheet: Boolean = false
)

data class SafariUiState(
    val profile: ChildProfileEntity = ChildProfileEntity(),
    val stats: UserStatsEntity = UserStatsEntity(),
    val animals: List<AnimalCompanion> = emptyList(),
    val badges: List<SafariBadge> = emptyList(),
    val stickers: List<AnimalSticker> = emptyList(),
    val newlyUnlockedSticker: AnimalSticker? = null,
    val levels: List<LevelProgressEntity> = emptyList(),
    val todayRecord: DailyRecordEntity? = null,
    val gameSession: GameSessionState = GameSessionState(),
    val selectedDifficulty: DifficultyLevel = DifficultyLevel.MEDIUM,
    val parentGateQuestion: String = "12 + 7 = ?",
    val parentGateAnswer: Int = 19
)

class SafariViewModel(
    private val repository: SafariRepository,
    val audioEngine: SafariAudioEngine
) : ViewModel() {

    private val _gameSession = MutableStateFlow(GameSessionState())
    private val _selectedDifficulty = MutableStateFlow(DifficultyLevel.MEDIUM)
    private val _parentGateQuestion = MutableStateFlow("12 + 7 = ?")
    private val _parentGateAnswer = MutableStateFlow(19)
    private val _newlyUnlockedSticker = MutableStateFlow<AnimalSticker?>(null)

    val uiState: StateFlow<SafariUiState> = combine(
        repository.childProfile,
        repository.userStats,
        repository.availableAnimals,
        repository.allBadges,
        repository.allStickers,
        repository.allLevels,
        repository.getTodayDailyRecord(),
        _gameSession,
        _selectedDifficulty,
        _newlyUnlockedSticker
    ) { args: Array<Any?> ->
        val profile = args[0] as ChildProfileEntity
        val stats = args[1] as UserStatsEntity
        @Suppress("UNCHECKED_CAST")
        val animals = args[2] as List<AnimalCompanion>
        @Suppress("UNCHECKED_CAST")
        val badges = args[3] as List<SafariBadge>
        @Suppress("UNCHECKED_CAST")
        val stickers = args[4] as List<AnimalSticker>
        @Suppress("UNCHECKED_CAST")
        val levels = args[5] as List<LevelProgressEntity>
        val todayRecord = args[6] as? DailyRecordEntity
        val session = args[7] as GameSessionState
        val difficulty = args[8] as DifficultyLevel
        val newlyUnlocked = args[9] as? AnimalSticker

        SafariUiState(
            profile = profile,
            stats = stats,
            animals = animals,
            badges = badges,
            stickers = stickers,
            newlyUnlockedSticker = newlyUnlocked,
            levels = levels,
            todayRecord = todayRecord,
            gameSession = session,
            selectedDifficulty = difficulty,
            parentGateQuestion = _parentGateQuestion.value,
            parentGateAnswer = _parentGateAnswer.value
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SafariUiState()
    )

    init {
        // Sync audio settings with stored profile
        viewModelScope.launch {
            repository.childProfile.collect { profile ->
                audioEngine.isSoundEnabled = profile.soundEnabled
                audioEngine.isVoiceEnabled = profile.voiceEnabled
                audioEngine.setMusicEnabled(profile.musicEnabled)
            }
        }
        generateNewParentGate()
    }

    fun setDifficulty(difficulty: DifficultyLevel) {
        _selectedDifficulty.value = difficulty
    }

    fun startLevel(topic: MathTopic, levelNumber: Int, difficulty: DifficultyLevel = _selectedDifficulty.value) {
        _selectedDifficulty.value = difficulty
        val questions = QuestionEngine.generateQuestionsForLevel(topic, levelNumber, difficulty, count = 5)
        _gameSession.value = GameSessionState(
            topic = topic,
            levelNumber = levelNumber,
            difficulty = difficulty,
            questions = questions,
            currentQuestionIndex = 0,
            score = 0,
            correctCount = 0,
            isDailyChallenge = false
        )
        speakCurrentQuestion()
    }

    fun startDailyChallenge(difficulty: DifficultyLevel = _selectedDifficulty.value) {
        _selectedDifficulty.value = difficulty
        val questions = QuestionEngine.generateDailyChallengeQuestions(difficulty)
        _gameSession.value = GameSessionState(
            topic = MathTopic.COUNTING,
            levelNumber = 1,
            difficulty = difficulty,
            questions = questions,
            currentQuestionIndex = 0,
            score = 0,
            correctCount = 0,
            isDailyChallenge = true
        )
        speakCurrentQuestion()
    }

    fun selectOption(option: Int) {
        val session = _gameSession.value
        if (session.isAnswerChecked || session.questions.isEmpty()) return

        audioEngine.playTap()
        _gameSession.update { it.copy(selectedOption = option) }
        checkAnswer(option)
    }

    fun incrementInteractive() {
        val session = _gameSession.value
        if (session.isAnswerChecked) return
        audioEngine.playTap()
        _gameSession.update { it.copy(interactiveCount = it.interactiveCount + 1) }
    }

    fun decrementInteractive() {
        val session = _gameSession.value
        if (session.isAnswerChecked || session.interactiveCount <= 0) return
        audioEngine.playTap()
        _gameSession.update { it.copy(interactiveCount = it.interactiveCount - 1) }
    }

    fun submitInteractive() {
        val session = _gameSession.value
        if (session.isAnswerChecked || session.questions.isEmpty()) return
        checkAnswer(session.interactiveCount)
    }

    private fun checkAnswer(givenAnswer: Int) {
        val session = _gameSession.value
        val currentQ = session.questions.getOrNull(session.currentQuestionIndex) ?: return
        val isCorrect = (givenAnswer == currentQ.correctAnswer)

        val basePoints = if (isCorrect) {
            if (session.hintsUsedInCurrentQuestion == 0) 100 else 70
        } else {
            0
        }
        val points = (basePoints * session.difficulty.scoreMultiplier).toInt()

        if (isCorrect) {
            audioEngine.playCorrect()
            val cheerPhrases = listOf("Great thinking!", "Fantastic!", "You got it! 🌟", "Roar! Super job!", "Math explorer star!")
            audioEngine.speak(cheerPhrases.random())
        } else {
            audioEngine.playIncorrect()
            val encouragePhrases = listOf("Almost! Let's count together.", "Good try! You can do it!", "Keep exploring!")
            audioEngine.speak(encouragePhrases.random())
        }

        _gameSession.update { current ->
            current.copy(
                isAnswerChecked = true,
                isAnswerCorrect = isCorrect,
                score = current.score + points,
                correctCount = current.correctCount + if (isCorrect) 1 else 0
            )
        }
    }

    fun nextQuestion() {
        val session = _gameSession.value
        val nextIdx = session.currentQuestionIndex + 1

        if (nextIdx < session.questions.size) {
            _gameSession.update { current ->
                current.copy(
                    currentQuestionIndex = nextIdx,
                    selectedOption = null,
                    interactiveCount = 0,
                    isAnswerChecked = false,
                    isAnswerCorrect = null,
                    hintsUsedInCurrentQuestion = 0,
                    showTutorSheet = false,
                    tutorMessage = null,
                    tutorStep = 0
                )
            }
            speakCurrentQuestion()
        } else {
            // Level / Challenge complete!
            finalizeSession()
        }
    }

    private fun finalizeSession() {
        val session = _gameSession.value
        val total = session.questions.size
        val correct = session.correctCount
        val percentage = if (total > 0) (correct.toFloat() / total * 100).toInt() else 0

        val stars = when {
            percentage >= 90 -> 3
            percentage >= 70 -> 2
            percentage >= 50 -> 1
            else -> 1 // Encouraging minimum star
        }

        audioEngine.playCelebration()
        audioEngine.speak("Adventure complete! You earned $stars stars!")

        viewModelScope.launch {
            if (session.isDailyChallenge) {
                val dailyResult = repository.recordDailyChallengeCompletion(session.score, stars, correct, session.difficulty)
                val summary = GameLevelSummary(
                    topic = MathTopic.COUNTING,
                    levelNumber = 1,
                    difficulty = session.difficulty,
                    score = session.score,
                    stars = stars,
                    coinsEarned = dailyResult.bonusCoins,
                    correctCount = correct,
                    totalCount = total,
                    isPerfect = (correct == total),
                    newlyUnlockedStickers = dailyResult.newlyUnlockedStickers
                )
                if (dailyResult.newlyUnlockedStickers.isNotEmpty()) {
                    _newlyUnlockedSticker.value = dailyResult.newlyUnlockedStickers.first()
                    audioEngine.playCelebration()
                    audioEngine.speak("Hooray! You unlocked the ${dailyResult.newlyUnlockedStickers.first().name} sticker!")
                }
                _gameSession.update { it.copy(isSessionComplete = true, summary = summary) }
            } else {
                val summary = repository.recordLevelCompletion(
                    topic = session.topic,
                    levelNumber = session.levelNumber,
                    score = session.score,
                    stars = stars,
                    correctCount = correct,
                    totalCount = total,
                    difficulty = session.difficulty
                )
                if (summary.newlyUnlockedStickers.isNotEmpty()) {
                    _newlyUnlockedSticker.value = summary.newlyUnlockedStickers.first()
                    audioEngine.playCelebration()
                    audioEngine.speak("Hooray! You unlocked the ${summary.newlyUnlockedStickers.first().name} sticker!")
                }
                _gameSession.update { it.copy(isSessionComplete = true, summary = summary) }
            }
        }
    }

    fun requestTutorHint() {
        val session = _gameSession.value
        val currentQ = session.questions.getOrNull(session.currentQuestionIndex) ?: return
        val nextStep = (session.tutorStep + 1).coerceAtMost(3)

        audioEngine.playTap()
        _gameSession.update {
            it.copy(
                showTutorSheet = true,
                isTutorLoading = true,
                tutorStep = nextStep,
                hintsUsedInCurrentQuestion = it.hintsUsedInCurrentQuestion + 1,
                totalHintsUsed = it.totalHintsUsed + 1
            )
        }

        viewModelScope.launch {
            val childName = uiState.value.profile.nickname
            val hint = GeminiTutorService.getTutorGuidance(currentQ, nextStep, childName)
            _gameSession.update { it.copy(tutorMessage = hint, isTutorLoading = false) }
            audioEngine.speak(hint)
        }
    }

    fun dismissTutor() {
        _gameSession.update { it.copy(showTutorSheet = false) }
        audioEngine.stopSpeaking()
    }

    private fun speakCurrentQuestion() {
        val currentQ = _gameSession.value.questions.getOrNull(_gameSession.value.currentQuestionIndex)
        if (currentQ != null) {
            audioEngine.speak(currentQ.promptText)
        }
    }

    fun generateNewParentGate() {
        val a = Random.nextInt(11, 20)
        val b = Random.nextInt(4, 15)
        _parentGateQuestion.value = "$a + $b = ?"
        _parentGateAnswer.value = a + b
    }

    fun verifyParentGate(input: Int): Boolean {
        return input == _parentGateAnswer.value
    }

    fun updateProfile(nickname: String, ageRange: String, avatar: String) {
        viewModelScope.launch {
            repository.updateProfile(nickname, ageRange, avatar)
        }
    }

    fun updateSettings(soundEnabled: Boolean, voiceEnabled: Boolean, musicEnabled: Boolean, cloudSync: Boolean) {
        viewModelScope.launch {
            repository.updateSettings(soundEnabled, voiceEnabled, musicEnabled, cloudSync)
        }
    }

    fun toggleMusic() {
        val currentProfile = uiState.value.profile
        val newMusicState = !currentProfile.musicEnabled
        viewModelScope.launch {
            repository.updateSettings(
                soundEnabled = currentProfile.soundEnabled,
                voiceEnabled = currentProfile.voiceEnabled,
                musicEnabled = newMusicState,
                cloudSync = currentProfile.isCloudSyncEnabled
            )
        }
    }

    fun selectAnimalCompanion(animalId: String) {
        viewModelScope.launch {
            repository.selectAnimalCompanion(animalId)
        }
    }

    fun resetProgress() {
        viewModelScope.launch {
            repository.resetAllProgress()
        }
    }

    fun dismissStickerCelebration() {
        _newlyUnlockedSticker.value = null
    }

    fun speakStickerFact(sticker: AnimalSticker) {
        audioEngine.playTap()
        audioEngine.speak("${sticker.name}! Fun fact: ${sticker.funFact}")
    }

    override fun onCleared() {
        super.onCleared()
        audioEngine.release()
    }
}
