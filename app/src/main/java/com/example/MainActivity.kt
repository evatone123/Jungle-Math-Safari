package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.model.MathTopic
import com.example.ui.components.ParentGateDialog
import com.example.ui.components.TutorBottomSheet
import com.example.ui.screens.AnimalsScreen
import com.example.ui.screens.BadgesScreen
import com.example.ui.screens.DailyChallengeScreen
import com.example.ui.screens.GameScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LevelResultDialog
import com.example.ui.screens.LevelSelectScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ParentDashboardScreen
import com.example.ui.screens.ProgressTrackingScreen
import com.example.ui.screens.SafariMapScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.StickersScreen
import com.example.ui.screens.StickerCelebrationDialog
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.SafariViewModel
import com.example.ui.viewmodel.SafariViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val factory = remember { SafariViewModelFactory(applicationContext) }
                val viewModel: SafariViewModel = viewModel(factory = factory)
                SafariApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun SafariApp(viewModel: SafariViewModel) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()
    var showParentGate by remember { mutableStateOf(false) }
    var chosenTopicForLevelSelect by remember { mutableStateOf(MathTopic.COUNTING) }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "splash",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("splash") {
                SplashScreen(
                    onStartAdventure = {
                        if (uiState.profile.nickname == "Explorer" && uiState.stats.totalAnswered == 0) {
                            navController.navigate("onboarding") {
                                popUpTo("splash") { inclusive = true }
                            }
                        } else {
                            navController.navigate("home") {
                                popUpTo("splash") { inclusive = true }
                            }
                        }
                    }
                )
            }

            composable("onboarding") {
                OnboardingScreen(
                    currentName = uiState.profile.nickname,
                    currentAge = uiState.profile.ageRange,
                    currentAvatar = uiState.profile.avatarEmoji,
                    onComplete = { name, age, avatar ->
                        viewModel.updateProfile(name, age, avatar)
                        navController.navigate("home") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    }
                )
            }

            composable("home") {
                HomeScreen(
                    avatarEmoji = uiState.profile.avatarEmoji,
                    nickname = uiState.profile.nickname,
                    stars = uiState.stats.totalStars,
                    coins = uiState.stats.totalCoins,
                    isSoundOn = uiState.profile.soundEnabled,
                    onSoundToggle = {
                        viewModel.updateSettings(
                            soundEnabled = !uiState.profile.soundEnabled,
                            voiceEnabled = uiState.profile.voiceEnabled,
                            musicEnabled = uiState.profile.musicEnabled,
                            cloudSync = uiState.profile.isCloudSyncEnabled
                        )
                    },
                    isMusicOn = uiState.profile.musicEnabled,
                    onMusicToggle = { viewModel.toggleMusic() },
                    onPlaySafari = { navController.navigate("map") },
                    onDailyChallenge = { navController.navigate("daily") },
                    onAnimalsClick = { navController.navigate("animals") },
                    onBadgesClick = { navController.navigate("badges") },
                    onStickersClick = { navController.navigate("stickers") },
                    stickersCount = uiState.stickers.count { it.isUnlocked },
                    onProgressClick = { navController.navigate("progress") },
                    totalProblemsSolved = uiState.stats.totalCorrect,
                    onParentZoneClick = {
                        viewModel.generateNewParentGate()
                        showParentGate = true
                    },
                    onEditProfile = { navController.navigate("onboarding") }
                )
            }

            composable("map") {
                SafariMapScreen(
                    levels = uiState.levels,
                    onSelectTopic = { topic ->
                        chosenTopicForLevelSelect = topic
                        navController.navigate("level_select/${topic.id}")
                    },
                    onBackClick = { navController.popBackStack() },
                    isMusicOn = uiState.profile.musicEnabled,
                    onMusicToggle = { viewModel.toggleMusic() }
                )
            }

            composable(
                route = "level_select/{topicId}",
                arguments = listOf(navArgument("topicId") { type = NavType.StringType })
            ) { backStackEntry ->
                val topicId = backStackEntry.arguments?.getString("topicId") ?: MathTopic.COUNTING.id
                val topic = MathTopic.fromId(topicId)
                chosenTopicForLevelSelect = topic

                LevelSelectScreen(
                    topic = topic,
                    levels = uiState.levels,
                    initialDifficulty = uiState.selectedDifficulty,
                    onSelectLevel = { levelNum, difficulty ->
                        viewModel.startLevel(topic, levelNum, difficulty)
                        navController.navigate("game")
                    },
                    onBackClick = { navController.popBackStack() },
                    isMusicOn = uiState.profile.musicEnabled,
                    onMusicToggle = { viewModel.toggleMusic() }
                )
            }

            composable("game") {
                GameScreen(
                    session = uiState.gameSession,
                    onSelectOption = { viewModel.selectOption(it) },
                    onIncrementInteractive = { viewModel.incrementInteractive() },
                    onDecrementInteractive = { viewModel.decrementInteractive() },
                    onSubmitInteractive = { viewModel.submitInteractive() },
                    onTutorHelp = { viewModel.requestTutorHint() },
                    onNextQuestion = { viewModel.nextQuestion() },
                    onQuit = { navController.popBackStack() },
                    isMusicOn = uiState.profile.musicEnabled,
                    onMusicToggle = { viewModel.toggleMusic() }
                )
            }

            composable("daily") {
                DailyChallengeScreen(
                    todayRecord = uiState.todayRecord,
                    initialDifficulty = uiState.selectedDifficulty,
                    onStartChallenge = { difficulty ->
                        viewModel.startDailyChallenge(difficulty)
                        navController.navigate("game")
                    },
                    onBackClick = { navController.popBackStack() },
                    isMusicOn = uiState.profile.musicEnabled,
                    onMusicToggle = { viewModel.toggleMusic() }
                )
            }

            composable("animals") {
                AnimalsScreen(
                    animals = uiState.animals,
                    selectedCompanionId = uiState.profile.selectedCompanionId,
                    onSelectAnimal = { viewModel.selectAnimalCompanion(it) },
                    onBackClick = { navController.popBackStack() },
                    isMusicOn = uiState.profile.musicEnabled,
                    onMusicToggle = { viewModel.toggleMusic() }
                )
            }

            composable("badges") {
                BadgesScreen(
                    badges = uiState.badges,
                    onBackClick = { navController.popBackStack() },
                    onNavigateToStickers = { navController.navigate("stickers") },
                    isMusicOn = uiState.profile.musicEnabled,
                    onMusicToggle = { viewModel.toggleMusic() }
                )
            }

            composable("stickers") {
                StickersScreen(
                    stickers = uiState.stickers,
                    totalProblemsSolved = uiState.stats.totalCorrect,
                    onBackClick = { navController.popBackStack() },
                    onSpeakStickerFact = { viewModel.speakStickerFact(it) },
                    isMusicOn = uiState.profile.musicEnabled,
                    onMusicToggle = { viewModel.toggleMusic() }
                )
            }

            composable("parent_zone") {
                ParentDashboardScreen(
                    profile = uiState.profile,
                    stats = uiState.stats,
                    onUpdateProfile = { name, age, avatar ->
                        viewModel.updateProfile(name, age, avatar)
                    },
                    onUpdateSettings = { sound, voice, music, sync ->
                        viewModel.updateSettings(sound, voice, music, sync)
                    },
                    onResetProgress = {
                        viewModel.resetProgress()
                    },
                    onBackClick = { navController.popBackStack() },
                    currentAudioTheme = uiState.currentAudioTheme,
                    musicVolume = uiState.backgroundMusicVolume,
                    onSelectAudioTheme = { viewModel.selectJungleAudioTheme(it) },
                    onSetMusicVolume = { viewModel.setBackgroundMusicVolume(it) },
                    onNavigateToProgress = { navController.navigate("progress") }
                )
            }

            composable("progress") {
                ProgressTrackingScreen(
                    profile = uiState.profile,
                    stats = uiState.stats,
                    stickers = uiState.stickers,
                    onBackClick = { navController.popBackStack() },
                    onPracticeTopic = { topic ->
                        chosenTopicForLevelSelect = topic
                        navController.navigate("level_select/${topic.id}")
                    },
                    onMusicToggle = { viewModel.toggleMusic() },
                    isMusicOn = uiState.profile.musicEnabled
                )
            }
        }

        // Parent Gate Dialog
        if (showParentGate) {
            ParentGateDialog(
                question = uiState.parentGateQuestion,
                onVerify = { viewModel.verifyParentGate(it) },
                onSuccess = {
                    showParentGate = false
                    navController.navigate("parent_zone")
                },
                onDismiss = { showParentGate = false }
            )
        }

        // Tutor Bottom Sheet
        if (uiState.gameSession.showTutorSheet) {
            TutorBottomSheet(
                tutorMessage = uiState.gameSession.tutorMessage,
                isLoading = uiState.gameSession.isTutorLoading,
                step = uiState.gameSession.tutorStep,
                onDismiss = { viewModel.dismissTutor() }
            )
        }

        // Level Complete Dialog
        if (uiState.gameSession.isSessionComplete && uiState.gameSession.summary != null) {
            val summary = uiState.gameSession.summary!!
            LevelResultDialog(
                summary = summary,
                onNextLevel = {
                    val nextLevel = summary.levelNumber + 1
                    if (nextLevel <= 10 && !uiState.gameSession.isDailyChallenge) {
                        viewModel.startLevel(summary.topic, nextLevel, summary.difficulty)
                    } else {
                        navController.popBackStack("map", inclusive = false)
                    }
                },
                onRetryLevel = {
                    if (uiState.gameSession.isDailyChallenge) {
                        viewModel.startDailyChallenge(summary.difficulty)
                    } else {
                        viewModel.startLevel(summary.topic, summary.levelNumber, summary.difficulty)
                    }
                },
                onReturnMap = {
                    navController.popBackStack("map", inclusive = false)
                },
                onViewStickers = {
                    navController.navigate("stickers")
                }
            )
        }

        // Sticker Unlock Celebration Dialog
        uiState.newlyUnlockedSticker?.let { sticker ->
            StickerCelebrationDialog(
                sticker = sticker,
                onViewAlbum = {
                    viewModel.dismissStickerCelebration()
                    navController.navigate("stickers")
                },
                onDismiss = {
                    viewModel.dismissStickerCelebration()
                },
                onSpeakFact = {
                    viewModel.speakStickerFact(sticker)
                }
            )
        }
    }
}
