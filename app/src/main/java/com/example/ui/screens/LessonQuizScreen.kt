package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.example.model.QuizQuestion
import com.example.model.QuizType
import com.example.ui.components.DevModeTestingBar
import com.example.ui.components.AnswerPrefixHint
import com.example.ui.components.AnswerPrefixHintPanel
import com.example.ui.components.SpeechPracticeBar
import com.example.ui.components.ThreePartPhraseCard
import com.example.ui.theme.*
import com.example.viewmodel.QuizState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonQuizScreen(
    state: QuizState,
    isListening: Boolean,
    speechLevel: Float = 0f,
    speechStatus: String,
    onBack: () -> Unit,
    onTypedInputChange: (String) -> Unit,
    onCopyInputChange: (String) -> Unit = {},
    onToggleTile: (Int) -> Unit,
    onSelectOption: (Int) -> Unit,
    onEvaluate: () -> Unit,
    onOverrideCorrect: () -> Unit = {},
    onSkip: () -> Unit = {},
    onNext: () -> Unit,
    onRestart: () -> Unit,
    onContinueNextRound: () -> Unit = {},
    onSpeakPhrase: (com.example.model.PhraseItem) -> Unit,
    onStartSpeech: ((String) -> Unit) -> Unit,
    onStopSpeech: () -> Unit,
    isDevModeEnabled: Boolean = false,
    onEndDevMode: () -> Unit = {},
    onDevGetCorrect: () -> Unit = {},
    onDevSkipToLast: () -> Unit = {}
) {
    var showExitDialog by remember { mutableStateOf(false) }

    val handleBackPress = {
        if (!state.isFinished && (state.currentIndex > 0 || state.roundCorrectCount > 0)) {
            showExitDialog = true
        } else {
            onBack()
        }
    }

    BackHandler { handleBackPress() }
    val focusManager = LocalFocusManager.current
    var roundCountdown by remember(state.completedRoundNumber) { mutableIntStateOf(3) }

    val currentQ: QuizQuestion? = state.questions.getOrNull(state.currentIndex)
    val roundProgress = if (state.fixedRoundTargetCount > 0) {
        (state.roundCorrectCount.toFloat() / state.fixedRoundTargetCount.toFloat()).coerceIn(0f, 1f)
    } else 0f

    LaunchedEffect(state.showRoundCheckpoint, state.completedRoundNumber) {
        if (state.showRoundCheckpoint) {
            for (count in 3 downTo 1) {
                roundCountdown = count
                delay(1_000)
            }
            onContinueNextRound()
        }
    }

    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // 1. Far Left: Back arrow
                    IconButton(
                        onClick = { handleBackPress() },
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .testTag("quiz_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }

                    // 2. Center: Fixed Target Count bubble
                    if (currentQ != null && !state.isFinished) {
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .align(Alignment.Center)
                                .testTag("drill_tally_counter")
                        ) {
                            Text(
                                text = "${(state.roundCorrectCount).coerceAtMost(state.fixedRoundTargetCount)} / ${state.fixedRoundTargetCount}",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (state.showRoundCheckpoint) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("🎉", fontSize = 56.sp)
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Round ${state.completedRoundNumber} Complete!",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(28.dp))
                Text(
                    text = "Starting round ${state.completedRoundNumber + 1} in",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                AnimatedContent(
                    targetState = roundCountdown,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(350)) togetherWith
                            fadeOut(animationSpec = tween(350))
                    },
                    label = "RoundCountdown",
                    modifier = Modifier.testTag("round_countdown")
                ) { count ->
                    Text(
                        text = count.toString(),
                        style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        } else if (state.isFinished) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🏆", fontSize = 48.sp)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "${state.levelTitle} Complete!",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = onRestart,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("quiz_practice_again_button")
                ) {
                    Icon(Icons.Filled.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Practice Again", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onBack,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                ) {
                    Text("Return to Home", fontWeight = FontWeight.SemiBold)
                }
            }
        } else if (currentQ != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                LinearProgressIndicator(
                    progress = { roundProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                )

                if (isDevModeEnabled) {
                    DevModeTestingBar(
                        onEndDevMode = onEndDevMode,
                        onCurrentQuestionCorrect = onDevGetCorrect,
                        onSkipToLastQuestion = onDevSkipToLast,
                        modeLabel = "Drill Round ${state.currentRoundNumber}/3"
                    )
                }

                AnimatedContent(
                    targetState = state.currentIndex,
                    transitionSpec = {
                        (slideInHorizontally(
                            initialOffsetX = { fullWidth -> fullWidth },
                            animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
                        ) + fadeIn(animationSpec = tween(durationMillis = 250))).togetherWith(
                            slideOutHorizontally(
                                targetOffsetX = { fullWidth -> -fullWidth },
                                animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
                            ) + fadeOut(animationSpec = tween(durationMillis = 200))
                        ).using(null) // Keep size changes from adding a spring to the horizontal slide.
                    },
                    contentAlignment = Alignment.TopStart,
                    label = "DrillSlideTransition",
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) { targetIndex ->
                    val q = state.questions.getOrNull(targetIndex) ?: currentQ
                    var isHintExpanded by remember(q.id, state.isEvaluated) { mutableStateOf(false) }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = q.prompt,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp,
                                        lineHeight = 27.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                                if (q.type == QuizType.MANGLISH_TYPING || q.type == QuizType.SPEAKING_PRACTICE) {
                                    AnswerPrefixHint(
                                        answer = q.phrase.manglish,
                                        isEvaluated = state.isEvaluated,
                                        isExpanded = isHintExpanded,
                                        onToggle = { isHintExpanded = !isHintExpanded },
                                        modifier = Modifier.padding(start = 8.dp).testTag("reveal_hint_button")
                                    )
                                }
                                if (q.recycleCount > 0) {
                                    Row(
                                        modifier = Modifier
                                            .padding(start = 8.dp)
                                            .testTag("quiz_question_recycled_indicator"),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Autorenew,
                                            contentDescription = "Recycled question ${q.recycleCount} times",
                                            tint = TerracottaAccent,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        if (q.recycleCount > 1) {
                                            Text(
                                                text = "Recycle x ${q.recycleCount}",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = TerracottaAccent
                                            )
                                        }
                                    }
                                }
                            }
                            if (q.type == QuizType.MANGLISH_TYPING || q.type == QuizType.SPEAKING_PRACTICE) {
                                AnswerPrefixHintPanel(
                                    answer = q.phrase.manglish,
                                    isExpanded = isHintExpanded,
                                    isEvaluated = state.isEvaluated,
                                    modifier = Modifier.testTag("quiz_hint_panel")
                                )
                            }
                        }

                        item {
                            when (q.type) {
                                QuizType.MANGLISH_TYPING -> {
                                    ManglishTypingCard(
                                        typedInput = state.typedInput,
                                        onTypedInputChange = onTypedInputChange,
                                        onSubmit = {
                                            focusManager.clearFocus()
                                            onEvaluate()
                                        },
                                        onSkip = onSkip,
                                        isEvaluated = state.isEvaluated,
                                        isCorrect = state.isCorrect,
                                        isSkipped = state.isSkipped,
                                        correctPhrase = q.phrase
                                    )
                                }

                                QuizType.SPEAKING_PRACTICE -> {
                                    SpeakingPracticeCard(
                                        phrase = q.phrase,
                                        isListening = isListening,
                                        speechLevel = speechLevel,
                                        speechStatus = speechStatus,
                                        transcription = state.speechTranscription,
                                        typedInput = state.typedInput,
                                        onTypedInputChange = onTypedInputChange,
                                        isEvaluated = state.isEvaluated,
                                        isCorrect = state.isCorrect,
                                        onStartSpeech = onStartSpeech,
                                        onStopSpeech = onStopSpeech,
                                        onSubmitTyped = {
                                            focusManager.clearFocus()
                                            onEvaluate()
                                        }
                                    )
                                }

                                QuizType.WORD_TILES -> {
                                    WordTilesBuilderCard(
                                        question = q,
                                        selectedIndices = state.selectedTileIndices,
                                        onToggleTile = onToggleTile,
                                        isEvaluated = state.isEvaluated,
                                        isCorrect = state.isCorrect,
                                        onSpeakPrompt = { onSpeakPhrase(q.phrase) }
                                    )
                                }

                                QuizType.DIALECT_CHOICE -> {
                                    DialectChoiceCard(
                                        options = q.options,
                                        correctIndex = q.correctOptionIndex,
                                        selectedIndex = state.selectedOptionIndex,
                                        onSelectOption = { idx ->
                                            if (!state.isEvaluated) {
                                                onSelectOption(idx)
                                                onEvaluate()
                                            }
                                        },
                                        isEvaluated = state.isEvaluated
                                    )
                                }
                            }
                        }

                        // Mandatory Copy Answer When Wrong
                        if (state.isEvaluated && state.mustCopyAnswer) {
                            item {
                                Card(
                                    shape = RoundedCornerShape(18.dp),
                                    colors = CardDefaults.cardColors(containerColor = TerracottaContainer.copy(alpha = 0.55f)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(2.dp, TerracottaAccent, RoundedCornerShape(18.dp))
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            text = "✍️ Type Correct Answer to Continue",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                                            color = TerracottaAccent
                                        )
                                        Text(
                                            text = "We added this card back to the shuffle! Type \"${q.phrase.manglish}\" below to continue:",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = q.phrase.manglish,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.ExtraBold
                                            ),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        OutlinedTextField(
                                            value = state.copyAnswerInput,
                                            onValueChange = onCopyInputChange,
                                            label = { Text("Type '${q.phrase.manglish}'") },
                                            singleLine = true,
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("quiz_copy_answer_input")
                                        )
                                    }
                                }
                            }
                        }

                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }

                // Voice, typed answers, and choices submit directly. Keep Continue
                // and correction controls available after a wrong/skipped answer.
                val showBottomButton = if (currentQ.type == QuizType.MANGLISH_TYPING ||
                    currentQ.type == QuizType.DIALECT_CHOICE || currentQ.type == QuizType.SPEAKING_PRACTICE) {
                    state.isEvaluated && !state.isCorrect
                } else {
                    !state.isEvaluated || !state.isCorrect
                }
                if (showBottomButton) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (state.isEvaluated) {
                            val canContinue = !state.mustCopyAnswer || state.isCopyAnswerUnlocked
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (!state.isCorrect && (currentQ.type == QuizType.MANGLISH_TYPING || currentQ.type == QuizType.SPEAKING_PRACTICE)) {
                                    OutlinedButton(
                                        onClick = onOverrideCorrect,
                                        shape = RoundedCornerShape(16.dp),
                                        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = MaterialTheme.colorScheme.primary
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                            .testTag("quiz_override_button")
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Override",
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Override: I was right",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp
                                            )
                                        }
                                    }
                                }

                                Button(
                                    onClick = onNext,
                                    enabled = canContinue,
                                    shape = RoundedCornerShape(16.dp),
                                    elevation = ButtonDefaults.buttonElevation(
                                        defaultElevation = 4.dp,
                                        pressedElevation = 8.dp,
                                        disabledElevation = 0.dp
                                    ),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(54.dp)
                                        .testTag("quiz_continue_button")
                                ) {
                                    Text(
                                        text = if (!canContinue) {
                                            "Type Correct Answer Above to Continue"
                                        } else if (state.currentIndex + 1 >= state.questions.size && state.reshuffledCount == 0) {
                                            "Complete"
                                        } else {
                                            "Continue"
                                        },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        } else {
                            val canCheck = when (currentQ.type) {
                                QuizType.MANGLISH_TYPING -> state.typedInput.isNotBlank()
                                QuizType.SPEAKING_PRACTICE -> true
                                QuizType.WORD_TILES -> state.selectedTileIndices.isNotEmpty()
                                QuizType.DIALECT_CHOICE -> state.selectedOptionIndex != null
                            }

                            Button(
                                onClick = onEvaluate,
                                enabled = canCheck,
                                shape = RoundedCornerShape(16.dp),
                                elevation = ButtonDefaults.buttonElevation(
                                    defaultElevation = 4.dp,
                                    pressedElevation = 8.dp,
                                    disabledElevation = 0.dp
                                ),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                                    .testTag("quiz_check_answer_button")
                            ) {
                                Text("Check Answer", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = {
                Text(
                    text = when (state.practiceQuestionType) {
                        QuizType.MANGLISH_TYPING -> "Exit Typing?"
                        QuizType.SPEAKING_PRACTICE -> "Exit Speaking?"
                        else -> "Leave Drills?"
                    },
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Text(
                    text = when (state.practiceQuestionType) {
                        QuizType.MANGLISH_TYPING, QuizType.SPEAKING_PRACTICE ->
                            "Are you sure? You will lose progress for the current round."
                        else -> "Are you sure? You will lose progress for the current round (Round ${state.currentRoundNumber} of 3)."
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

}

@Composable
fun ManglishTypingCard(
    typedInput: String,
    onTypedInputChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onSkip: () -> Unit,
    isEvaluated: Boolean,
    isCorrect: Boolean,
    isSkipped: Boolean,
    correctPhrase: com.example.model.PhraseItem
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(isEvaluated, isSkipped) {
        if (!isEvaluated && !isSkipped) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                value = if (isSkipped) "Skipped" else typedInput,
                onValueChange = { newText ->
                    if (newText.contains('\n')) {
                        val cleaned = newText.replace("\n", "")
                        onTypedInputChange(cleaned)
                        if (!isEvaluated && cleaned.isNotBlank()) {
                            onSubmit()
                        }
                    } else {
                        onTypedInputChange(newText)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onPreviewKeyEvent { keyEvent ->
                        if (keyEvent.type == KeyEventType.KeyDown &&
                            (keyEvent.key == Key.Enter || keyEvent.key == Key.NumPadEnter)
                        ) {
                            if (!isEvaluated && typedInput.isNotBlank()) {
                                onSubmit()
                            }
                            true
                        } else {
                            false
                        }
                    }
                    .testTag("manglish_text_input"),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (isEvaluated && !isCorrect) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = if (isEvaluated && !isCorrect) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                    disabledBorderColor = if (isEvaluated && !isCorrect) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (!isEvaluated && typedInput.isNotBlank()) {
                            onSubmit()
                        }
                    }
                ),
                enabled = !isEvaluated
            )

            // When wrong or skipped: show correct answer below in green
            AnimatedVisibility(
                visible = isEvaluated && !isCorrect,
                enter = fadeIn(animationSpec = tween(300)) + expandVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(14.dp))
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Correct answer:",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = correctPhrase.manglish,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                if (correctPhrase.malayalam.isNotBlank()) {
                                    Text(
                                        text = correctPhrase.malayalam,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                    )
                                }
                            }

                        }
                    }
                }
            }
    }
}

@Composable
fun SpeakingPracticeCard(
    phrase: com.example.model.PhraseItem,
    isListening: Boolean,
    speechLevel: Float = 0f,
    speechStatus: String,
    transcription: String,
    typedInput: String,
    onTypedInputChange: (String) -> Unit,
    isEvaluated: Boolean,
    isCorrect: Boolean,
    onStartSpeech: ((String) -> Unit) -> Unit,
    onStopSpeech: () -> Unit,
    onSubmitTyped: () -> Unit = {}
) {
    var isTypingInstead by remember(phrase.id) { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SpeechPracticeBar(
                isListening = isListening,
                speechLevel = speechLevel,
                onMicClick = {
                    if (isListening) {
                        onStopSpeech()
                    } else {
                        onStartSpeech { _ -> }
                    }
                },
                statusMessage = speechStatus,
                accuracyFeedback = if (transcription.isNotBlank()) "Recognized: \"$transcription\"" else null
            )

            TextButton(
                onClick = { isTypingInstead = !isTypingInstead },
                enabled = !isEvaluated,
                modifier = Modifier.testTag("speaking_type_instead_button")
            ) {
                Text(if (isTypingInstead) "Use microphone instead" else "Type instead")
            }

            if (isTypingInstead) {
                OutlinedTextField(
                    value = typedInput,
                    onValueChange = onTypedInputChange,
                    enabled = !isEvaluated,
                    label = { Text("Type your Manglish answer") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        if (!isEvaluated && typedInput.isNotBlank()) onSubmitTyped()
                    }),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .onPreviewKeyEvent { event ->
                            if (event.type == KeyEventType.KeyUp && event.key == Key.Enter &&
                                !isEvaluated && typedInput.isNotBlank()) {
                                onSubmitTyped()
                                true
                            } else false
                        }
                        .testTag("speaking_typed_fallback_input")
                )
            }

            if (isEvaluated && !isCorrect) {
                Text(
                    text = "Correct answer: ${phrase.manglish}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                if (phrase.malayalam.isNotBlank()) {
                    Text(
                        text = phrase.malayalam,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun WordTilesBuilderCard(
    question: QuizQuestion,
    selectedIndices: List<Int>,
    onToggleTile: (Int) -> Unit,
    isEvaluated: Boolean = false,
    isCorrect: Boolean = false,
    onSpeakPrompt: () -> Unit = {}
) {
    val shuffledOrder = remember(question.id) {
        question.correctTileOrder.indices.shuffled()
    }

    val headerLabel = when {
        isEvaluated && isCorrect -> "Correct!"
        isEvaluated && !isCorrect -> "Wrong!"
        else -> "Assembled Answer:"
    }

    val headerColor = when {
        isEvaluated && isCorrect -> MaterialTheme.colorScheme.primary
        isEvaluated && !isCorrect -> TerracottaAccent
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = headerLabel,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = if (isEvaluated) FontWeight.ExtraBold else FontWeight.Medium
                ),
                color = headerColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 60.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .border(
                        width = if (isEvaluated) 2.dp else 1.dp,
                        color = when {
                            isEvaluated && isCorrect -> MaterialTheme.colorScheme.primary
                            isEvaluated && !isCorrect -> TerracottaAccent.copy(alpha = 0.6f)
                            else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        },
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(10.dp)
            ) {
                if (selectedIndices.isEmpty()) {
                    Text(
                        text = "Tap tiles below in the right order...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        selectedIndices.forEachIndexed { posIndex, idx ->
                            val word = question.correctTileOrder[idx]
                            val expectedAtPos = question.correctTileOrder.getOrNull(posIndex)
                            val isWrongSpot = isEvaluated && (word != expectedAtPos)
                            val isRightSpot = isEvaluated && (word == expectedAtPos)

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = when {
                                    isWrongSpot -> MaterialTheme.colorScheme.surface
                                    isRightSpot -> MaterialTheme.colorScheme.surface
                                    else -> MaterialTheme.colorScheme.primary
                                },
                                border = when {
                                    isWrongSpot -> BorderStroke(2.dp, TerracottaAccent)
                                    isRightSpot -> BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                                    else -> null
                                },
                                modifier = Modifier.clickable(enabled = !isEvaluated) { onToggleTile(idx) }
                            ) {
                                Text(
                                    text = word,
                                    color = when {
                                        isWrongSpot -> TerracottaAccent
                                        isRightSpot -> MaterialTheme.colorScheme.primary
                                        else -> MaterialTheme.colorScheme.onPrimary
                                    },
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                shuffledOrder.forEach { tileIdx ->
                    val isUsed = selectedIndices.contains(tileIdx)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isUsed) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f) else MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier
                            .clickable(enabled = !isUsed && !isEvaluated) { onToggleTile(tileIdx) }
                            .testTag("tile_${question.correctTileOrder[tileIdx]}")
                    ) {
                        Text(
                            text = question.correctTileOrder[tileIdx],
                            color = if (isUsed) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f) else MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            // Show correct answer below in green when wrong
            AnimatedVisibility(
                visible = isEvaluated && !isCorrect,
                enter = fadeIn(animationSpec = tween(300)) + expandVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(14.dp))
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Correct answer:",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = question.correctTileOrder.joinToString(" "),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                if (question.phrase.malayalam.isNotBlank()) {
                                    Text(
                                        text = question.phrase.malayalam,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                    )
                                }
                            }
                            IconButton(onClick = onSpeakPrompt) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Listen correct pronunciation",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DialectChoiceCard(
    options: List<String>,
    correctIndex: Int,
    selectedIndex: Int?,
    onSelectOption: (Int) -> Unit,
    isEvaluated: Boolean
) {
    val emeraldChoiceColor = if (isSystemInDarkTheme()) EmeraldPrimaryDark else EmeraldDark

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        options.forEachIndexed { index, optionText ->
            val isSelected = selectedIndex == index
            val isCorrectChoice = index == correctIndex
            val isWrongChoice = isEvaluated && isSelected && !isCorrectChoice

            val borderColor = when {
                isEvaluated && isCorrectChoice -> emeraldChoiceColor
                isWrongChoice -> TerracottaAccent
                isSelected -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            }

            val textColor = when {
                isEvaluated && isCorrectChoice -> emeraldChoiceColor
                isWrongChoice -> TerracottaAccent
                else -> MaterialTheme.colorScheme.onSurface
            }

            val radioColor = when {
                isEvaluated && isCorrectChoice -> emeraldChoiceColor
                isWrongChoice -> TerracottaAccent
                else -> MaterialTheme.colorScheme.primary
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable(enabled = !isEvaluated) { onSelectOption(index) }
                    .border(
                        width = if (isSelected || (isEvaluated && isCorrectChoice)) 2.dp else 1.dp,
                        color = borderColor,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .testTag("quiz_option_$index"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { if (!isEvaluated) onSelectOption(index) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = radioColor
                            )
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = optionText,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = if (isSelected || (isEvaluated && isCorrectChoice)) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 16.sp
                            ),
                            color = textColor
                        )
                    }

                    if (isEvaluated) {
                        if (isCorrectChoice) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "Correct",
                                tint = emeraldChoiceColor,
                                modifier = Modifier.size(22.dp)
                            )
                        } else if (isWrongChoice) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Wrong",
                                tint = TerracottaAccent,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
