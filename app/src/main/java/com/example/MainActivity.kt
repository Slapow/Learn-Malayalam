package com.example

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.audio.AudioHelper
import com.example.data.CloudProgressRepository
import com.example.data.DrillItemDatabase
import com.example.data.DrillItemRepository
import com.example.data.UserStatsRepository
import com.example.ui.components.LaptopSyncSheet
import com.example.ui.components.LevelUpCelebrationDialog
import com.example.ui.components.SpokenGraduationDialog
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.Screen
import com.example.web.WebCompanionServer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

fun FirebaseAuth.authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
    val listener = FirebaseAuth.AuthStateListener { auth ->
        trySend(auth.currentUser)
    }
    addAuthStateListener(listener)
    awaitClose { removeAuthStateListener(listener) }
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val auth = remember { FirebaseAuth.getInstance() }
                val currentUser by auth.authStateFlow().collectAsStateWithLifecycle(initialValue = auth.currentUser)
                val scope = rememberCoroutineScope()
                val context = LocalContext.current

                val activeUser = currentUser
                if (activeUser == null) {
                    AuthScreen(
                        onAuthSuccess = { /* AuthStateListener automatically transitions to MainFeatureScreen */ }
                    )
                } else {
                    MainFeatureScreen(
                        user = activeUser,
                        onSignOut = {
                            scope.launch {
                                auth.signOut()
                                try {
                                    CredentialManager.create(context).clearCredentialState(ClearCredentialStateRequest())
                                } catch (_: Exception) {}
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MainFeatureScreen(
    user: FirebaseUser,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current
    val currentUserId = user.uid
    val userDisplayName = user.displayName ?: "Malayalam Learner"
    val userEmail = user.email ?: "Google Cloud Account"

    val viewModel: MainViewModel = viewModel(
        key = currentUserId,
        factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY]!!
                val dbId = app.getString(R.string.firestore_database_id)
                val firestore = FirebaseFirestore.getInstance(dbId)
                MainViewModel(
                    cloudRepo = CloudProgressRepository(firestore),
                    statsRepo = UserStatsRepository(app),
                    drillRepository = DrillItemRepository(DrillItemDatabase.getInstance(app).drillItemDao()),
                    audioHelper = AudioHelper(app),
                    currentUserId = currentUserId,
                    userDisplayName = userDisplayName
                )
            }
        }
    )

    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val userStats by viewModel.userStats.collectAsStateWithLifecycle()
    // Keep the activity selection when HomeScreen leaves the composition during study.
    var selectedHomeLevelIndex by rememberSaveable(currentUserId) { mutableStateOf<Int?>(null) }
    val homeLevelIndex = selectedHomeLevelIndex
        ?.coerceIn(-1, userStats.unlockedLevelIndex) ?: userStats.unlockedLevelIndex
    LaunchedEffect(userStats.unlockedLevelIndex) {
        if (selectedHomeLevelIndex != null && selectedHomeLevelIndex != homeLevelIndex) {
            selectedHomeLevelIndex = homeLevelIndex
        }
    }
    val quizState by viewModel.quizState.collectAsStateWithLifecycle()
    val flashcardState by viewModel.flashcardState.collectAsStateWithLifecycle()
    val aiTutorState by viewModel.aiTutorState.collectAsStateWithLifecycle()
    val translatorState by viewModel.translatorState.collectAsStateWithLifecycle()
    val speechStatus by viewModel.speechStatus.collectAsStateWithLifecycle()
    val isListening by viewModel.isListening.collectAsStateWithLifecycle()
    val speechLevel by viewModel.speechLevel.collectAsStateWithLifecycle()
    val cloudSyncStatus by viewModel.cloudSyncStatus.collectAsStateWithLifecycle()
    val lastSyncedDevice by viewModel.lastSyncedDevice.collectAsStateWithLifecycle()
    val lastActiveCloudScreen by viewModel.lastActiveCloudScreen.collectAsStateWithLifecycle()
    val tierGraduationState by viewModel.tierGraduationState.collectAsStateWithLifecycle()
    val levelUpEvent by viewModel.levelUpEvent.collectAsStateWithLifecycle()
    val isDevModeEnabled by viewModel.isDevModeEnabled.collectAsStateWithLifecycle()

    val webCompanionServer = remember(viewModel) {
        WebCompanionServer(
            context = context.applicationContext,
            getSessionSnapshot = { viewModel.buildSessionSnapshotJson() },
            onRemoteSessionUpdated = { json -> viewModel.applyRemoteLanSession(json) }
        )
    }

    var isLanServerRunning by remember { mutableStateOf(false) }
    var lanUrl by remember { mutableStateOf(webCompanionServer.getLocalUrl()) }

    DisposableEffect(webCompanionServer) {
        val url = webCompanionServer.startServer()
        lanUrl = url
        isLanServerRunning = webCompanionServer.isServerRunning()
        onDispose {
            webCompanionServer.stopServer()
        }
    }

    // Audio permission launcher
    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(
                context,
                "Microphone permission is required for speaking practice.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun checkAndStartSpeech(onTranscription: (String) -> Unit) {
        val hasPerm = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPerm) {
            viewModel.startSpeechPractice(onTranscription)
        } else {
            audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                Screen.HOME -> {
                    HomeScreen(
                        userStats = userStats,
                        selectedLevelIndex = homeLevelIndex,
                        onSelectLevel = { selectedHomeLevelIndex = it },
                        translatorState = translatorState,
                        onTranslatorInputChange = viewModel::updateTranslatorInput,
                        onTranslate = viewModel::translateInput,
                        onDismissTranslator = viewModel::dismissTranslator,
                        onNavigate = { screen -> viewModel.navigateTo(screen) },
                        onSpeakPhrase = { phrase -> viewModel.speakPhrase(phrase) },
                        onToggleLearned = { id -> viewModel.toggleLearned(id) },
                        onOpenDeckInMode = { deck, mode -> viewModel.openDeckWithMode(deck, mode) },
                        onStartLearningDeck = { deck, levelIndex ->
                            viewModel.openLevelDeckBeforeDrills(deck, levelIndex)
                        },
                        onStartDrill = { levelIndex ->
                            viewModel.startNewQuizSession(levelIndex)
                            viewModel.navigateTo(Screen.QUIZ)
                        },
                        onStartProductionPractice = { levelIndex, quizType ->
                            viewModel.startNewQuizSession(levelIndex, questionType = quizType)
                            viewModel.navigateTo(Screen.QUIZ)
                        },
                        onLaunchAdaptiveFocus = { viewModel.launchDynamicAdaptiveReview() },
                        onOpenCloudSync = { viewModel.navigateTo(Screen.SETTINGS) },
                        lastSyncedDevice = lastSyncedDevice,
                        lastActiveScreenLabel = lastActiveCloudScreen,
                        onResumeCloudSession = { viewModel.resumeLastCloudSession() },
                        isDevModeEnabled = isDevModeEnabled,
                        onEndDevMode = { viewModel.devEndMode() }
                    )
                }

                Screen.QUIZ -> {
                    LessonQuizScreen(
                        state = quizState,
                        isListening = isListening,
                        speechLevel = speechLevel,
                        speechStatus = speechStatus,
                        onBack = { viewModel.navigateTo(Screen.HOME) },
                        onTypedInputChange = { text -> viewModel.updateQuizTypedInput(text) },
                        onCopyInputChange = { text -> viewModel.updateQuizCopyInput(text) },
                        onToggleTile = { idx -> viewModel.toggleWordTile(idx) },
                        onSelectOption = { idx -> viewModel.selectQuizOption(idx) },
                        onEvaluate = { viewModel.evaluateCurrentQuestion() },
                        onOverrideCorrect = { viewModel.overrideQuizQuestionAsCorrect() },
                        onSkip = { viewModel.skipCurrentQuizQuestion() },
                        onNext = { viewModel.nextQuizQuestion() },
                        onRestart = {
                            viewModel.startNewQuizSession(
                                quizState.levelIndex,
                                questionType = quizState.practiceQuestionType
                            )
                        },
                        onContinueNextRound = { viewModel.continueToNextRound() },
                        onSpeakPhrase = { phrase -> viewModel.speakPhrase(phrase) },
                        onStartSpeech = { callback -> checkAndStartSpeech(callback) },
                        onStopSpeech = { viewModel.stopSpeechPractice() },
                        isDevModeEnabled = isDevModeEnabled,
                        onEndDevMode = { viewModel.devEndMode() },
                        onDevGetCorrect = { viewModel.devGetQuizQuestionCorrect() },
                        onDevSkipToLast = { viewModel.devSkipToLastQuizQuestion() }
                    )
                }

                Screen.FLASHCARDS -> {
                    FlashcardsScreen(
                        state = flashcardState,
                        userStats = userStats,
                        isListening = isListening,
                        speechLevel = speechLevel,
                        speechStatus = speechStatus,
                        onBack = { viewModel.navigateTo(Screen.HOME) },
                        onSelectDeck = { deck -> viewModel.selectDeck(deck) },
                        onSelectMode = { mode -> viewModel.selectQuizletMode(mode) },
                        onLaunchAdaptiveFocus = { viewModel.launchDynamicAdaptiveReview() },
                        onFlipCard = { viewModel.flipCard() },
                        onSessionInteraction = viewModel::markStudySessionStarted,
                        onMarkKnown = { viewModel.markCardKnown() },
                        onMarkLearning = { viewModel.markCardLearning() },
                        onSpeakPhrase = { phrase -> viewModel.speakPhrase(phrase) },
                        onSpeakEnglish = { englishText -> viewModel.speakEnglish(englishText) },
                        onUndo = { viewModel.undoLastFlashcardSwipe() },
                        onAdvanceWithoutTally = { viewModel.advanceCardWithoutTally() },
                        onToggleFrontLanguage = { viewModel.toggleFlashcardFrontLanguage() },
                        onShuffleDeck = { viewModel.shuffleCurrentFlashcardDeck() },
                        onRestartDeck = { viewModel.restartCurrentFlashcardDeck() },
                        onTypedResponseChange = { text -> viewModel.updateStudyTypedResponse(text) },
                        onSelectMcOption = { idx -> viewModel.selectStudyMcOption(idx) },
                        onSelectTrueFalse = { ans -> viewModel.selectStudyTrueFalse(ans) },
                        onCopyInputChange = { text -> viewModel.updateStudyCopyInput(text) },
                        onEvaluateQuestion = { viewModel.evaluateDeckStudyQuestion() },
                        onOverrideCorrect = { viewModel.overrideStudyQuestionAsCorrect() },
                        onNextQuestion = { viewModel.nextDeckStudyQuestion() },
                        onRestartMode = { viewModel.selectQuizletMode(flashcardState.activeMode) },
                        onTapMatchTile = { tileId -> viewModel.onMatchTileTapped(tileId) },
                        onTickMatchTimer = { viewModel.tickMatchTimer() },
                        onStartSpeech = { callback -> checkAndStartSpeech(callback) },
                        onStopSpeech = { viewModel.stopSpeechPractice() },
                        onContinueToDrills = { levelIndex ->
                            viewModel.startNewQuizSession(levelIndex)
                            viewModel.navigateTo(Screen.QUIZ)
                        },
                        isDevModeEnabled = isDevModeEnabled,
                        onEndDevMode = { viewModel.devEndMode() },
                        onDevGetCorrect = { viewModel.devGetFlashcardsScreenCorrect() },
                        onDevSkipToLast = { viewModel.devSkipToLastFlashcardsScreenQuestion() }
                    )
                }

                Screen.AI_TUTOR -> {
                    AiTutorScreen(
                        state = aiTutorState,
                        isListening = isListening,
                        speechLevel = speechLevel,
                        speechStatus = speechStatus,
                        onBack = { viewModel.navigateTo(Screen.HOME) },
                        onSelectScenario = { scenario -> viewModel.selectRoleplayScenario(scenario) },
                        onInputChange = { text -> viewModel.updateAiTutorInput(text) },
                        onSendMessage = { text -> viewModel.sendTutorMessage(text) },
                        onSpeakText = { ml, mg -> viewModel.speakText(ml, mg) },
                        onStartSpeech = { callback -> checkAndStartSpeech(callback) },
                        onStopSpeech = { viewModel.stopSpeechPractice() }
                    )
                }

                Screen.DIALECT_GUIDE -> {
                    DialectGuideScreen(
                        userStats = userStats,
                        onBack = { viewModel.navigateTo(Screen.HOME) },
                        onSpeakText = { ml, mg -> viewModel.speakText(ml, mg) },
                        onToggleLearned = { id -> viewModel.toggleLearned(id) },
                        onOpenDeckInMode = { deck, mode -> viewModel.openDeckWithMode(deck, mode) },
                        onLaunchAdaptiveFocus = { viewModel.launchDynamicAdaptiveReview() },
                        onStartGraduationExam = { tier -> viewModel.startTierGraduationExam(tier) }
                    )
                }

                Screen.SETTINGS -> {
                    SettingsScreen(
                        user = user,
                        userStats = userStats,
                        onBack = { viewModel.navigateTo(Screen.HOME) },
                        onSignOut = onSignOut,
                        cloudSyncStatus = cloudSyncStatus,
                        lastSyncedDevice = lastSyncedDevice,
                        hostingUrl = "https://parayu-learn-malayalam.web.app",
                        localLanUrl = lanUrl,
                        onResetAllProgress = { viewModel.resetAllProgressToZero() },
                        onSetDevLevel = { idx -> viewModel.setDevLevel(idx) },
                        onSetDevStreak = { days -> viewModel.setDevStreak(days) },
                        onQueueDevLevelUp = { isDrill -> viewModel.queueDevLevelUp(isDrill) },
                        isDevModeEnabled = isDevModeEnabled,
                        onToggleDevMode = { viewModel.setDevModeEnabled(it) }
                    )
                }
            }

            // ACTFL Tier Spoken Graduation Exam Dialog
            tierGraduationState?.let { examState ->
                SpokenGraduationDialog(
                    examState = examState,
                    onToggleListen = {
                        val hasPerm = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.RECORD_AUDIO
                        ) == PackageManager.PERMISSION_GRANTED
                        if (hasPerm) {
                            viewModel.toggleGraduationListening()
                        } else {
                            audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    },
                    onSpeakPhraseText = { ml, mg -> viewModel.speakText(ml, mg) },
                    onNextPrompt = { viewModel.nextGraduationPrompt() },
                    onSelfVerifySpoken = { viewModel.evaluateGraduationPrompt(overridePassed = true) },
                    onDismiss = { viewModel.dismissGraduationExam() },
                    isDevModeEnabled = isDevModeEnabled,
                    onEndDevMode = { viewModel.devEndMode() },
                    onDevGetCorrect = { viewModel.evaluateGraduationPrompt(overridePassed = true) },
                    onDevSkipToLast = { viewModel.devSkipToLastGraduationPrompt() }
                )
            }

            // Level Up Celebration Dialog
            levelUpEvent?.let { event ->
                LevelUpCelebrationDialog(
                    event = event,
                    onDismiss = { viewModel.dismissLevelUpEvent() }
                )
            }
        }
    }
}
