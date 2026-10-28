package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DialectRepository
import kotlinx.coroutines.Job
import com.example.model.FlashcardDeck
import com.example.model.PhraseItem
import com.example.model.QuizletStudyMode
import com.example.model.UserStats
import com.example.ui.components.DevModeTestingBar
import com.example.ui.theme.*
import com.example.viewmodel.FlashcardState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardsScreen(
    state: FlashcardState,
    userStats: UserStats = UserStats(),
    isListening: Boolean = false,
    speechLevel: Float = 0f,
    speechStatus: String = "Ready to listen",
    onBack: () -> Unit,
    onSelectDeck: (FlashcardDeck) -> Unit,
    onSelectMode: (QuizletStudyMode) -> Unit = {},
    onLaunchAdaptiveFocus: () -> Unit = {},
    onFlipCard: () -> Unit,
    onMarkKnown: () -> Unit,
    onMarkLearning: () -> Unit,
    onUndo: () -> Unit = {},
    onAdvanceWithoutTally: () -> Unit = {},
    onSpeakPhrase: (PhraseItem) -> Unit,
    onSpeakEnglish: (String) -> Unit = {},
    onToggleFrontLanguage: () -> Unit = {},
    onShuffleDeck: () -> Unit = {},
    onRestartDeck: () -> Unit = {},
    onTypedResponseChange: (String) -> Unit = {},
    onSelectMcOption: (Int) -> Unit = {},
    onSelectTrueFalse: (Boolean) -> Unit = {},
    onCopyInputChange: (String) -> Unit = {},
    onEvaluateQuestion: () -> Unit = {},
    onOverrideCorrect: () -> Unit = {},
    onNextQuestion: () -> Unit = {},
    onRestartMode: () -> Unit = {},
    onTapMatchTile: (String) -> Unit = {},
    onTickMatchTimer: () -> Unit = {},
    onStartSpeech: ((String) -> Unit) -> Unit = {},
    onStopSpeech: () -> Unit = {},
    onContinueToDrills: (Int) -> Unit = {},
    isDevModeEnabled: Boolean = false,
    onEndDevMode: () -> Unit = {},
    onDevGetCorrect: () -> Unit = {},
    onDevSkipToLast: () -> Unit = {},
    onSessionInteraction: () -> Unit = {}
) {
    val totalCards = state.currentDeck.phrases.size
    val currentPhrase: PhraseItem? = state.currentDeck.phrases.getOrNull(state.currentCardIndex)

    val nextIdx = if (totalCards > 1) {
        (1 until totalCards).firstNotNullOfOrNull { i ->
            val idx = (state.currentCardIndex + i) % totalCards
            val id = state.currentDeck.phrases.getOrNull(idx)?.id
            if (id != null && !state.learnedInSession.contains(id)) idx else null
        }
    } else null

    val nextPhrase: PhraseItem? = nextIdx?.let { state.currentDeck.phrases.getOrNull(it) }
    val presentationCount = currentPhrase?.let { state.cardPresentationCount[it.id] } ?: 0

    var showLevelDialog by remember { mutableStateOf(false) }
    var settingsMenuExpanded by remember { mutableStateOf(false) }
    var isAutoplayOn by remember { mutableStateOf(false) }
    var autoplayBannerText by remember { mutableStateOf<String?>(null) }
    var showExitDialog by remember(state.currentDeck.id, state.activeMode) { mutableStateOf(false) }

    val handleBackPress = {
        val flashcardsCompleted = totalCards > 0 && state.learnedInSession.size >= totalCards
        val needsConfirmation = state.hasStartedSession && when (state.activeMode) {
            QuizletStudyMode.FLASHCARDS -> !flashcardsCompleted
            QuizletStudyMode.MATCH -> !state.isMatchFinished
            else -> false
        }
        isAutoplayOn = false
        if (needsConfirmation) showExitDialog = true else onBack()
    }
    BackHandler { handleBackPress() }

    val presentationKey = listOf(
        state.currentDeck.id, currentPhrase?.id, state.currentCardIndex,
        state.activeMode, presentationCount, state.swipeHistory.size,
        state.learnedInSession.size, state.stillLearningInSession.size
    )

    // Autoplay automated loop
    LaunchedEffect(isAutoplayOn, presentationKey) {
        if (!isAutoplayOn || currentPhrase == null) return@LaunchedEffect

        if (state.isEnglishOnFront) {
            onSpeakEnglish(currentPhrase.english)
            delay(2200L)
            if (!isAutoplayOn) return@LaunchedEffect
            onFlipCard()
            onSpeakPhrase(currentPhrase)
            delay(2400L)
            if (!isAutoplayOn) return@LaunchedEffect
        } else {
            onSpeakPhrase(currentPhrase)
            delay(2200L)
            if (!isAutoplayOn) return@LaunchedEffect
            onFlipCard()
            onSpeakEnglish(currentPhrase.english)
            delay(2400L)
            if (!isAutoplayOn) return@LaunchedEffect
        }

        onAdvanceWithoutTally()
    }

    LaunchedEffect(autoplayBannerText) {
        if (autoplayBannerText != null) {
            delay(1800L)
            autoplayBannerText = null
        }
    }

    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { handleBackPress() },
                        modifier = Modifier.testTag("flashcards_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }

                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("flashcards_count_bubble")
                        ) {
                            val bubbleText = if (state.activeMode == QuizletStudyMode.FLASHCARDS) {
                                "${state.learnedInSession.size} / $totalCards"
                            } else {
                                "${state.activeMode.iconEmoji} ${state.activeMode.title}"
                            }
                            Text(
                                text = bubbleText,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Box {
                        IconButton(
                            onClick = { settingsMenuExpanded = true },
                            modifier = Modifier.testTag("flashcards_settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = "Flashcard Settings"
                            )
                        }

                        DropdownMenu(
                            expanded = settingsMenuExpanded,
                            onDismissRequest = { settingsMenuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.Layers,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                text = {
                                    Column {
                                        Text(
                                            text = "Select Level",
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = state.currentDeck.levelLabel,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                },
                                onClick = {
                                    settingsMenuExpanded = false
                                    showLevelDialog = true
                                },
                                modifier = Modifier.testTag("flashcards_select_level_option")
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                            DropdownMenuItem(
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.SwapHoriz,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                text = {
                                    Text(
                                        text = if (state.isEnglishOnFront) {
                                            "Front: English (Switch to Manglish)"
                                        } else {
                                            "Front: Manglish (Switch to English)"
                                        },
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                onClick = {
                                    onToggleFrontLanguage()
                                    settingsMenuExpanded = false
                                },
                                modifier = Modifier.testTag("flashcards_toggle_front_language")
                            )

                            DropdownMenuItem(
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.Shuffle,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                text = {
                                    Text(
                                        text = "Shuffle Cards",
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                onClick = {
                                    isAutoplayOn = false
                                    onShuffleDeck()
                                    settingsMenuExpanded = false
                                },
                                modifier = Modifier.testTag("flashcards_shuffle_option")
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                            DropdownMenuItem(
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.Refresh,
                                        contentDescription = null,
                                        tint = TerracottaAccent
                                    )
                                },
                                text = {
                                    Text(
                                        text = "Restart Flashcards",
                                        fontWeight = FontWeight.Bold,
                                        color = TerracottaAccent
                                    )
                                },
                                onClick = {
                                    isAutoplayOn = false
                                    onRestartDeck()
                                    settingsMenuExpanded = false
                                },
                                modifier = Modifier.testTag("flashcards_restart_option")
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            if (state.activeMode == QuizletStudyMode.FLASHCARDS && totalCards > 0 && state.learnedInSession.size < totalCards) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (isAutoplayOn) isAutoplayOn = false
                            onUndo()
                        },
                        modifier = Modifier.testTag("flashcards_undo_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Undo,
                            contentDescription = "Undo previous swipe",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    val currentFaceIsEnglish = if (state.isEnglishOnFront) !state.isFlipped else state.isFlipped
                    val targetRevealLanguage = if (currentFaceIsEnglish) "Malayalam" else "English"
                    val isAutoplayNotice = autoplayBannerText != null
                    val bottomDisplayText = autoplayBannerText ?: "Tap to reveal $targetRevealLanguage"

                    Surface(
                        onClick = {
                            if (isAutoplayOn) {
                                isAutoplayOn = false
                                autoplayBannerText = "Autoplay off"
                            } else {
                                onFlipCard()
                            }
                        },
                        color = Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("flashcards_tap_to_reveal_button")
                    ) {
                        Text(
                            text = bottomDisplayText,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (isAutoplayNotice) FontWeight.ExtraBold else FontWeight.SemiBold
                            ),
                            color = if (isAutoplayNotice) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            isAutoplayOn = !isAutoplayOn
                            if (isAutoplayOn) onSessionInteraction()
                            autoplayBannerText = if (isAutoplayOn) "Autoplay on" else "Autoplay off"
                        },
                        modifier = Modifier.testTag("flashcards_autoplay_button")
                    ) {
                        Icon(
                            imageVector = if (isAutoplayOn) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = if (isAutoplayOn) "Pause autoplay" else "Start autoplay",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (isDevModeEnabled) {
                    DevModeTestingBar(
                        onEndDevMode = onEndDevMode,
                        onCurrentQuestionCorrect = onDevGetCorrect,
                        onSkipToLastQuestion = onDevSkipToLast,
                        modeLabel = state.activeMode.title
                    )
                }

                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    when (state.activeMode) {
                        QuizletStudyMode.FLASHCARDS -> {
                            val isCompleted = totalCards > 0 && state.learnedInSession.size >= totalCards
                            val animatedProgress by animateFloatAsState(
                                targetValue = if (isCompleted) 1f else if (totalCards > 0) state.learnedInSession.size.toFloat() / totalCards.toFloat() else 0f,
                                animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
                                label = "flashcards_progress_bar_anim"
                            )

                            Column(modifier = Modifier.fillMaxSize()) {
                                LinearProgressIndicator(
                                    progress = { animatedProgress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .testTag("flashcards_progress_bar"),
                                    color = MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                )

                                // Tally Badges
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        color = TerracottaContainer.copy(alpha = 0.85f),
                                        shape = RoundedCornerShape(12.dp),
                                        border = BorderStroke(1.dp, TerracottaAccent.copy(alpha = 0.6f)),
                                        modifier = Modifier.testTag("still_learning_tally_badge")
                                    ) {
                                        Text(
                                            text = "Still Learning: ${state.stillLearningInSession.size}",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = TerracottaAccent,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                        )
                                    }

                                    Surface(
                                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f),
                                        shape = RoundedCornerShape(12.dp),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)),
                                        modifier = Modifier.testTag("learned_tally_badge")
                                    ) {
                                        Text(
                                            text = "Learned: ${state.learnedInSession.size}",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                        )
                                    }
                                }

                                if (totalCards == 0) {
                                    // Empty Deck State
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = MaterialTheme.colorScheme.secondaryContainer,
                                            modifier = Modifier.size(80.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Layers,
                                                contentDescription = "Empty Deck",
                                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                                modifier = Modifier.padding(16.dp).size(48.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(24.dp))
                                        Text(
                                            text = "No Cards Available",
                                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "This deck currently has no cards to review.",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(32.dp))
                                        Button(
                                            onClick = onBack,
                                            shape = RoundedCornerShape(16.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primary,
                                                contentColor = MaterialTheme.colorScheme.onPrimary
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth(0.7f)
                                                .height(50.dp)
                                                .testTag("flashcards_empty_deck_return_home_button")
                                        ) {
                                            Text(
                                                text = "Return Home",
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                            )
                                        }
                                    }
                                } else if (isCompleted) {
                                    // Completion Celebration
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = MaterialTheme.colorScheme.primaryContainer,
                                            modifier = Modifier.size(80.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Completed",
                                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                                modifier = Modifier.padding(16.dp).size(48.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(24.dp))
                                        Text(
                                            text = "Completed!",
                                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "You have mastered all $totalCards cards.",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(32.dp))
                                        val continueLevel = state.continueToDrillsLevelIndex
                                        Button(
                                            onClick = {
                                                if (continueLevel != null) onContinueToDrills(continueLevel) else onBack()
                                            },
                                            shape = RoundedCornerShape(16.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primary,
                                                contentColor = MaterialTheme.colorScheme.onPrimary
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth(0.7f)
                                                .height(50.dp)
                                                .testTag(
                                                    if (continueLevel != null) {
                                                        "flashcards_completion_continue_to_drills_button"
                                                    } else {
                                                        "flashcards_completion_return_home_button"
                                                    }
                                                )
                                        ) {
                                            Text(
                                                text = if (continueLevel != null) "Continue to Drills" else "Return Home",
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                            )
                                        }
                                        if (continueLevel != null) {
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Text(
                                                text = "Return Home",
                                                modifier = Modifier
                                                    .clickable(onClick = onBack)
                                                    .testTag("flashcards_completion_return_home_text"),
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                } else {
                                    // Active Flashcard Stack
                                    val isRecycled = currentPhrase != null &&
                                        state.cardsSentToStillLearning.contains(currentPhrase.id) &&
                                        (state.cardPresentationCount[currentPhrase.id] ?: 1) >= 2

                                    val isNextRecycled = nextPhrase != null &&
                                        state.cardsSentToStillLearning.contains(nextPhrase.id) &&
                                        (state.cardPresentationCount[nextPhrase.id] ?: 1) >= 2

                                    SwipeableFlashcardStack(
                                        currentPhrase = currentPhrase,
                                        nextPhrase = nextPhrase,
                                        state = state,
                                        presentationCount = presentationCount,
                                        isRecycled = isRecycled,
                                        isNextRecycled = isNextRecycled,
                                        onFlipCard = {
                                            if (isAutoplayOn) isAutoplayOn = false
                                            onFlipCard()
                                        },
                                        onMarkKnown = onMarkKnown,
                                        onMarkLearning = onMarkLearning,
                                        onSpeakPhrase = onSpeakPhrase,
                                        onSessionInteraction = onSessionInteraction
                                    )
                                }
                            }
                        }

                        QuizletStudyMode.MATCH -> {
                            val tickMatchTimer by rememberUpdatedState(onTickMatchTimer)
                            LaunchedEffect(state.isMatchFinished) {
                                if (!state.isMatchFinished) {
                                    while (true) {
                                        delay(100L)
                                        tickMatchTimer()
                                    }
                                }
                            }

                            MatchGameModePanel(
                                state = state,
                                onTapTile = onTapMatchTile,
                                onRestartMatch = onRestartMode
                            )
                        }

                        else -> {
                            DeckAdaptiveQuestionPanel(
                                state = state,
                                isListening = isListening,
                                speechLevel = speechLevel,
                                speechStatus = speechStatus,
                                onTypedChange = onTypedResponseChange,
                                onSelectMcOption = onSelectMcOption,
                                onSelectTrueFalse = onSelectTrueFalse,
                                onCopyInputChange = onCopyInputChange,
                                onEvaluate = onEvaluateQuestion,
                                onOverrideCorrect = onOverrideCorrect,
                                onNext = onNextQuestion,
                                onRestartMode = onRestartMode,
                                onSpeakPhrase = onSpeakPhrase,
                                onStartSpeech = onStartSpeech,
                                onStopSpeech = onStopSpeech
                            )
                        }
                    }
                }
            }
        }
    }

    if (showExitDialog) {
        val isMatch = state.activeMode == QuizletStudyMode.MATCH
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = {
                Text(
                    text = if (isMatch) "Leave Match Game?" else "Leave Flashcards?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Text(
                    text = if (isMatch) {
                        "Are you sure? You will lose progress for the current match."
                    } else {
                        "Leave this flashcard session? Your saved learning progress will be kept."
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showExitDialog = false
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TerracottaAccent)
                ) {
                    Text("Leave", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("Stay", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (showLevelDialog) {
        LevelSelectionDialog(
            currentDeckId = state.currentDeck.id,
            userStats = userStats,
            onSelectDeck = { deck ->
                isAutoplayOn = false
                onSelectDeck(deck)
                showLevelDialog = false
            },
            onDismiss = { showLevelDialog = false }
        )
    }
}

@Composable
private fun SwipeableFlashcardStack(
    currentPhrase: PhraseItem?,
    nextPhrase: PhraseItem?,
    state: FlashcardState,
    presentationCount: Int,
    isRecycled: Boolean,
    isNextRecycled: Boolean,
    onFlipCard: () -> Unit,
    onMarkKnown: () -> Unit,
    onMarkLearning: () -> Unit,
    onSpeakPhrase: (PhraseItem) -> Unit,
    onSessionInteraction: () -> Unit
) {
    if (currentPhrase == null) return

    var offsetX by remember(currentPhrase.id) { mutableFloatStateOf(0f) }
    var offsetY by remember(currentPhrase.id) { mutableFloatStateOf(0f) }
    var isSwipingOut by remember(currentPhrase.id) { mutableStateOf(false) }
    var settleJob by remember(currentPhrase.id) { mutableStateOf<Job?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

    val cardRotationY by key(currentPhrase.id, presentationCount) {
        animateFloatAsState(
            targetValue = if (state.isFlipped) 180f else 0f,
            animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing),
            label = "card_flip_y"
        )
    }

    val swipeThreshold = with(density) { 110.dp.toPx() }
    val flyOffDistance = with(density) { 680.dp.toPx() }
    val absOffset = abs(offsetX)
    val swipeRatio = (offsetX / swipeThreshold).coerceIn(-1f, 1f)
    val absProgress = (absOffset / swipeThreshold).coerceIn(0f, 1f)

    fun settleCard(direction: Int) {
        settleJob?.cancel()
        settleJob = coroutineScope.launch {
            if (direction == 0) {
                val animX = launch {
                    animate(
                        initialValue = offsetX,
                        targetValue = 0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    ) { value, _ -> offsetX = value }
                }
                val animY = launch {
                    animate(
                        initialValue = offsetY,
                        targetValue = 0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    ) { value, _ -> offsetY = value }
                }
                animX.join()
                animY.join()
                offsetX = 0f
                offsetY = 0f
            } else {
                isSwipingOut = true
                try {
                    val dist = kotlin.math.hypot(offsetX.toDouble(), offsetY.toDouble()).toFloat().coerceAtLeast(1f)
                    val dirX = if (abs(offsetX) > 1f) offsetX / dist else direction.toFloat()
                    val dirY = offsetY / dist
                    val targetX = dirX * flyOffDistance
                    val targetY = dirY * flyOffDistance

                    val animX = launch {
                        animate(
                            initialValue = offsetX,
                            targetValue = targetX,
                            animationSpec = tween(180, easing = FastOutSlowInEasing)
                        ) { value, _ -> offsetX = value }
                    }
                    val animY = launch {
                        animate(
                            initialValue = offsetY,
                            targetValue = targetY,
                            animationSpec = tween(180, easing = FastOutSlowInEasing)
                        ) { value, _ -> offsetY = value }
                    }
                    animX.join()
                    animY.join()
                    if (direction > 0) onMarkKnown() else onMarkLearning()
                } catch (e: Exception) {
                    // Prevent crash
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("flashcard_swipe_area")
            .pointerInput(currentPhrase.id, presentationCount) {
                var totalDragX = 0f
                var totalDragY = 0f
                var isDragging = false
                val tracker = VelocityTracker()

                detectDragGestures(
                    onDragStart = { offset ->
                        settleJob?.cancel()
                        settleJob = null
                        isSwipingOut = false
                        offsetX = 0f
                        offsetY = 0f
                        tracker.resetTracking()
                        totalDragX = 0f
                        totalDragY = 0f
                        isDragging = false
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        totalDragX += dragAmount.x
                        totalDragY += dragAmount.y
                        if (abs(totalDragX) > 6.dp.toPx() || abs(totalDragY) > 6.dp.toPx()) {
                            isDragging = true
                        }
                        tracker.addPosition(change.uptimeMillis, change.position)
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    },
                    onDragEnd = {
                        if (isDragging) {
                            val velocityX = tracker.calculateVelocity().x
                            val threshold = with(density) { 110.dp.toPx() }
                            val velThreshold = with(density) { 450.dp.toPx() }
                            val direction = when {
                                totalDragX > threshold || offsetX > threshold || velocityX > velThreshold -> 1
                                totalDragX < -threshold || offsetX < -threshold || velocityX < -velThreshold -> -1
                                else -> 0
                            }
                            settleCard(direction)
                        }
                    },
                    onDragCancel = {
                        settleCard(0)
                    }
                )
            }
            .padding(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        // 1. Underneath Next Card (if available)
        if (nextPhrase != null) {
            val backScale = 0.94f + (0.06f * absProgress)
            val backTranslationY = with(density) { (12.dp * (1f - absProgress)).toPx() }
            val nextWord = if (state.isEnglishOnFront) nextPhrase.english else nextPhrase.manglish

            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = backScale
                        scaleY = backScale
                        translationY = backTranslationY
                        alpha = 0.65f + (0.35f * absProgress)
                    }
                    .border(
                        width = 1.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(28.dp)
                    ),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                MinimalFlashcardContent(
                    centerWord = nextWord,
                    isRecycled = isNextRecycled,
                    swipeRatio = 0f,
                    onSpeak = {}
                )
            }
        }

        // 2. Active Top Card
        val baseBorder = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        val borderColor = when {
            swipeRatio > 0.05f -> lerp(baseBorder, EmeraldPrimary, swipeRatio)
            swipeRatio < -0.05f -> lerp(baseBorder, TerracottaAccent, abs(swipeRatio))
            else -> baseBorder
        }

        val baseSurface = MaterialTheme.colorScheme.surface
        val cardColor = when {
            swipeRatio > 0.05f -> lerp(baseSurface, MaterialTheme.colorScheme.primaryContainer, swipeRatio * 0.35f)
            swipeRatio < -0.05f -> lerp(baseSurface, MaterialTheme.colorScheme.errorContainer, abs(swipeRatio) * 0.35f)
            else -> baseSurface
        }

        val frontWord = if (state.isEnglishOnFront) currentPhrase.english else currentPhrase.manglish
        val backWord = if (state.isEnglishOnFront) currentPhrase.manglish else currentPhrase.english
        val currentCardRotationY = if (cardRotationY <= 90f) cardRotationY else cardRotationY - 180f

        Card(
            modifier = Modifier
                .fillMaxSize()
                .testTag("flashcard_flip_card")
                .graphicsLayer {
                    translationX = offsetX
                    translationY = offsetY
                    rotationZ = (offsetX / 22f).coerceIn(-12f, 12f)
                    rotationY = currentCardRotationY
                    cameraDistance = 14f * density.density
                }
                .border(
                    width = if (absProgress > 0.1f) 2.5.dp else 1.5.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(28.dp)
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    if (!isSwipingOut) {
                        onFlipCard()
                    }
                },
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = cardColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            val displayedWord = if (cardRotationY <= 90f) frontWord else backWord
            MinimalFlashcardContent(
                centerWord = displayedWord,
                isRecycled = isRecycled,
                swipeRatio = swipeRatio,
                onSpeak = {
                    onSessionInteraction()
                    onSpeakPhrase(currentPhrase)
                }
            )
        }
    }
}

@Composable
private fun MinimalFlashcardContent(
    centerWord: String,
    isRecycled: Boolean,
    swipeRatio: Float,
    onSpeak: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        IconButton(
            onClick = onSpeak,
            modifier = Modifier
                .align(Alignment.TopStart)
                .testTag("flashcard_audio_button")
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                contentDescription = "Listen audio",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        if (abs(swipeRatio) > 0.12f) {
            val isKnow = swipeRatio > 0f
            val stampAlpha = ((abs(swipeRatio) - 0.12f) / 0.65f).coerceIn(0f, 1f)
            val stampScale = 0.85f + (0.15f * stampAlpha)
            Surface(
                color = if (isKnow) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 6.dp)
                    .graphicsLayer {
                        alpha = stampAlpha
                        scaleX = stampScale
                        scaleY = stampScale
                    }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isKnow) Icons.Filled.Check else Icons.Filled.Close,
                        contentDescription = null,
                        tint = if (isKnow) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isKnow) "Know" else "Still learning",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = if (isKnow) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }

        if (isRecycled) {
            Icon(
                imageVector = Icons.Filled.Autorenew,
                contentDescription = "Recycled card",
                tint = TerracottaAccent,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .size(24.dp)
                    .testTag("flashcard_recycled_indicator")
            )
        }

        Text(
            text = centerWord,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 28.sp,
                textAlign = TextAlign.Center
            ),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun LevelSelectionDialog(
    currentDeckId: String,
    userStats: UserStats,
    onSelectDeck: (FlashcardDeck) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Select Flashcard Level",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DialectRepository.decks.forEach { deck ->
                    val isSelected = deck.id == currentDeckId
                    val isUnlocked = deck.levelIndex <= userStats.unlockedLevelIndex
                    Surface(
                        onClick = {
                            if (isUnlocked) onSelectDeck(deck)
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(deck.iconEmoji, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = deck.levelLabel,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${deck.phrases.size} phrases",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = "Current Level",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            } else if (!isUnlocked) {
                                Icon(
                                    imageVector = Icons.Filled.Lock,
                                    contentDescription = "Locked Level",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", fontWeight = FontWeight.Bold)
            }
        }
    )
}
