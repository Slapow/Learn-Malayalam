package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.QuizQuestion
import com.example.model.QuizType
import com.example.ui.components.SpeechPracticeBar
import com.example.ui.components.ThreePartPhraseCard
import com.example.ui.theme.*
import com.example.viewmodel.QuizState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonQuizScreen(
    state: QuizState,
    isListening: Boolean,
    speechStatus: String,
    onBack: () -> Unit,
    onTypedInputChange: (String) -> Unit,
    onCopyInputChange: (String) -> Unit = {},
    onToggleTile: (Int) -> Unit,
    onSelectOption: (Int) -> Unit,
    onEvaluate: () -> Unit,
    onSkip: () -> Unit = {},
    onNext: () -> Unit,
    onRestart: () -> Unit,
    onSpeakPhrase: (com.example.model.PhraseItem) -> Unit,
    onStartSpeech: ((String) -> Unit) -> Unit,
    onStopSpeech: () -> Unit
) {
    BackHandler { onBack() }
    val focusManager = LocalFocusManager.current
    var isHintRevealed by remember(state.currentIndex) { mutableStateOf(false) }

    val currentQ: QuizQuestion? = state.questions.getOrNull(state.currentIndex)
    val progress = if (state.questions.isNotEmpty()) {
        (state.currentIndex.toFloat() / state.questions.size.toFloat()).coerceIn(0f, 1f)
    } else 0f

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
                        onClick = onBack,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .testTag("quiz_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }

                    // 2. Center: Question Type bubble (left of center) & Questions Count bubble (right of center)
                    if (currentQ != null && !state.isFinished) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Surface(
                                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.65f),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.padding(end = 4.dp)
                                ) {
                                    Text(
                                        text = when (currentQ.type) {
                                            QuizType.MANGLISH_TYPING -> "Typing"
                                            QuizType.SPEAKING_PRACTICE -> "Speaking"
                                            QuizType.WORD_TILES -> "Tiles"
                                            QuizType.DIALECT_CHOICE -> "Choice"
                                        },
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Surface(
                                    color = GoldenContainerLight,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.padding(start = 4.dp)
                                ) {
                                    Text(
                                        text = "Q ${state.currentIndex + 1}/${state.questions.size}",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = GoldenOnContainer,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                    }

                    // 3. Far Right: Reshuffled count
                    if (!state.isFinished) {
                        Text(
                            text = "🔄 ${state.reshuffledCount}",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (state.reshuffledCount > 0) {
                                TerracottaAccent
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            },
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 12.dp)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        if (state.isFinished) {
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
                        .background(GoldenContainerLight),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🏆", fontSize = 48.sp)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Beginner Drill Complete!",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Every word you practiced has been updated in your Known Words Database & Cloud Firestore! (+20 XP)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

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
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                )

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
                        )
                    },
                    label = "DrillSlideTransition",
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) { targetIndex ->
                    val q = state.questions.getOrNull(targetIndex) ?: currentQ
                    var hintRevealedThisItem by remember(targetIndex) { mutableStateOf(false) }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Text(
                                text = q.prompt,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    lineHeight = 27.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            if ((q.hint.isNotBlank() || q.phrase.wordBreakdown.isNotBlank()) && q.type != QuizType.MANGLISH_TYPING) {
                                Spacer(modifier = Modifier.height(10.dp))
                                if (!hintRevealedThisItem) {
                                    Surface(
                                        onClick = { hintRevealedThisItem = true },
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        modifier = Modifier.testTag("reveal_hint_button")
                                    ) {
                                        Text(
                                            text = "💡 Hint",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                } else {
                                    Surface(
                                        color = PalmContainerLight.copy(alpha = 0.55f),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            if (q.hint.isNotBlank()) {
                                                Text(
                                                    text = "💡 Hint: ${q.hint}",
                                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                                    color = EmeraldDark
                                                )
                                            }
                                            if (q.phrase.wordBreakdown.isNotBlank()) {
                                                if (q.hint.isNotBlank()) Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = "🧩 Breakdown: ${q.phrase.wordBreakdown}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = EmeraldDark
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            when (q.type) {
                                QuizType.MANGLISH_TYPING -> {
                                    ManglishTypingCard(
                                        typedInput = state.typedInput,
                                        onTypedInputChange = onTypedInputChange,
                                        onSpeakPrompt = { onSpeakPhrase(q.phrase) },
                                        onSubmit = {
                                            focusManager.clearFocus()
                                            onEvaluate()
                                        },
                                        onSkip = onSkip,
                                        isEvaluated = state.isEvaluated,
                                        isCorrect = state.isCorrect,
                                        isSkipped = state.isSkipped,
                                        correctPhrase = q.phrase,
                                        hintText = q.hint,
                                        wordBreakdown = q.phrase.wordBreakdown,
                                        isHintRevealed = hintRevealedThisItem,
                                        onToggleHint = { hintRevealedThisItem = !hintRevealedThisItem }
                                    )
                                }

                                QuizType.SPEAKING_PRACTICE -> {
                                    SpeakingPracticeCard(
                                        phrase = q.phrase,
                                        isListening = isListening,
                                        speechStatus = speechStatus,
                                        transcription = state.speechTranscription,
                                        onListenAudio = { onSpeakPhrase(q.phrase) },
                                        onStartSpeech = onStartSpeech,
                                        onStopSpeech = onStopSpeech
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
                                        selectedIndex = state.selectedOptionIndex,
                                        onSelectOption = onSelectOption,
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

                // Bottom Evaluation Drawer:
                // For MANGLISH_TYPING: no "Check Answer" button (user presses Enter on keyboard); only shown when wrong/skipped to click Continue.
                // For other question types: shown when not evaluated ("Check Answer") or when wrong/skipped ("Continue").
                val showBottomDrawer = if (currentQ.type == QuizType.MANGLISH_TYPING) {
                    state.isEvaluated && !state.isCorrect
                } else {
                    !state.isEvaluated || !state.isCorrect
                }
                if (showBottomDrawer) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 8.dp,
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 14.dp)
                        ) {
                            if (state.isEvaluated) {
                                val canContinue = !state.mustCopyAnswer || state.isCopyAnswerUnlocked
                                Button(
                                    onClick = onNext,
                                    enabled = canContinue,
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .testTag("quiz_continue_button")
                                ) {
                                    Text(
                                        text = if (!canContinue) {
                                            "Type Correct Answer Above to Continue"
                                        } else if (state.currentIndex + 1 >= state.questions.size && state.reshuffledCount == 0) {
                                            "Complete (+20 XP)"
                                        } else {
                                            "Continue"
                                        },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
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
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
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
    }
}

@Composable
fun ManglishTypingCard(
    typedInput: String,
    onTypedInputChange: (String) -> Unit,
    onSpeakPrompt: () -> Unit,
    onSubmit: () -> Unit,
    onSkip: () -> Unit,
    isEvaluated: Boolean,
    isCorrect: Boolean,
    isSkipped: Boolean,
    correctPhrase: com.example.model.PhraseItem,
    hintText: String = "",
    wordBreakdown: String = "",
    isHintRevealed: Boolean = false,
    onToggleHint: () -> Unit = {}
) {
    val headerLabel = when {
        isEvaluated && isCorrect -> "Correct!"
        isEvaluated && isSkipped -> "Skipped"
        isEvaluated && !isCorrect -> "Wrong!"
        else -> "Type in Manglish:"
    }

    val headerColor = when {
        isEvaluated && isCorrect -> EmeraldDark
        isEvaluated && isSkipped -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        isEvaluated && !isCorrect -> TerracottaAccent
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val borderColor = when {
        isEvaluated && isCorrect -> EmeraldDark
        isEvaluated && isSkipped -> Color(0xFFB0BEC5)
        isEvaluated && !isCorrect -> TerracottaAccent
        else -> MaterialTheme.colorScheme.outline
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = headerLabel,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = if (isEvaluated) FontWeight.ExtraBold else FontWeight.Medium
                    ),
                    color = headerColor
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (hintText.isNotBlank() || wordBreakdown.isNotBlank()) {
                        Surface(
                            onClick = onToggleHint,
                            shape = RoundedCornerShape(8.dp),
                            color = if (isHintRevealed) {
                                PalmContainerLight.copy(alpha = 0.7f)
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                            },
                            modifier = Modifier.testTag("reveal_hint_button")
                        ) {
                            Text(
                                text = "💡 Hint",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = if (isHintRevealed) EmeraldDark else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = onSpeakPrompt,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Listen prompt",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            AnimatedVisibility(visible = isHintRevealed && (hintText.isNotBlank() || wordBreakdown.isNotBlank())) {
                Surface(
                    color = PalmContainerLight.copy(alpha = 0.55f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        if (hintText.isNotBlank()) {
                            Text(
                                text = "💡 Hint: $hintText",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                color = EmeraldDark
                            )
                        }
                        if (wordBreakdown.isNotBlank()) {
                            if (hintText.isNotBlank()) Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "🧩 Breakdown: $wordBreakdown",
                                style = MaterialTheme.typography.bodySmall,
                                color = EmeraldDark
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

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
                placeholder = {
                    Text(if (isSkipped) "Skipped" else "Type your answer here...")
                },
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontStyle = if (isSkipped) FontStyle.Italic else FontStyle.Normal,
                    color = when {
                        isSkipped -> Color(0xFF757575)
                        isEvaluated && isCorrect -> EmeraldDark
                        isEvaluated && !isCorrect -> TerracottaAccent
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                ),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = borderColor,
                    unfocusedBorderColor = borderColor,
                    disabledBorderColor = borderColor,
                    focusedContainerColor = when {
                        isEvaluated && isCorrect -> PalmContainerLight.copy(alpha = 0.25f)
                        isEvaluated && isSkipped -> Color(0xFFF5F5F5)
                        isEvaluated && !isCorrect -> TerracottaContainer.copy(alpha = 0.25f)
                        else -> MaterialTheme.colorScheme.surface
                    },
                    unfocusedContainerColor = when {
                        isEvaluated && isCorrect -> PalmContainerLight.copy(alpha = 0.25f)
                        isEvaluated && isSkipped -> Color(0xFFF5F5F5)
                        isEvaluated && !isCorrect -> TerracottaContainer.copy(alpha = 0.25f)
                        else -> MaterialTheme.colorScheme.surface
                    },
                    disabledContainerColor = when {
                        isEvaluated && isCorrect -> PalmContainerLight.copy(alpha = 0.25f)
                        isEvaluated && isSkipped -> Color(0xFFF5F5F5)
                        isEvaluated && !isCorrect -> TerracottaContainer.copy(alpha = 0.25f)
                        else -> MaterialTheme.colorScheme.surface
                    },
                    disabledTextColor = when {
                        isSkipped -> Color(0xFF757575)
                        isEvaluated && isCorrect -> EmeraldDark
                        isEvaluated && !isCorrect -> TerracottaAccent
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                ),
                leadingIcon = if (isEvaluated && !isSkipped) {
                    {
                        AnimatedVisibility(
                            visible = isEvaluated && !isSkipped,
                            enter = fadeIn() + expandHorizontally(animationSpec = tween(300))
                        ) {
                            if (isCorrect) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = "Correct",
                                    tint = EmeraldDark,
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Wrong",
                                    tint = TerracottaAccent,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                } else null,
                trailingIcon = if (!isEvaluated && !isSkipped && typedInput.isEmpty()) {
                    {
                        Surface(
                            onClick = onSkip,
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.75f),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .testTag("quiz_dont_know_button")
                        ) {
                            Text(
                                text = "Don't know",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                } else null,
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
                        color = PalmContainerLight.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, EmeraldDark.copy(alpha = 0.4f)),
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
                                    color = EmeraldDark
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = correctPhrase.manglish,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    color = EmeraldDark
                                )
                                if (correctPhrase.malayalam.isNotBlank()) {
                                    Text(
                                        text = correctPhrase.malayalam,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = PalmOnContainerLight
                                    )
                                }
                            }
                            IconButton(onClick = onSpeakPrompt) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Listen correct pronunciation",
                                    tint = EmeraldDark
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
    speechStatus: String,
    transcription: String,
    onListenAudio: () -> Unit,
    onStartSpeech: ((String) -> Unit) -> Unit,
    onStopSpeech: () -> Unit
) {
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
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = phrase.manglish,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (phrase.phoneticGuide.isNotBlank()) {
                            Text(
                                text = "🔊 Say it: ${phrase.phoneticGuide}",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                color = TerracottaAccent
                            )
                        }
                        Text(
                            text = phrase.malayalam,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = onListenAudio) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Listen target pronunciation",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            SpeechPracticeBar(
                isListening = isListening,
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
        isEvaluated && isCorrect -> EmeraldDark
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
                            isEvaluated && isCorrect -> EmeraldDark
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
                                    isWrongSpot -> TerracottaContainer.copy(alpha = 0.35f)
                                    isRightSpot -> PalmContainerLight.copy(alpha = 0.55f)
                                    else -> MaterialTheme.colorScheme.primary
                                },
                                border = when {
                                    isWrongSpot -> BorderStroke(2.dp, TerracottaAccent)
                                    isRightSpot -> BorderStroke(1.5.dp, EmeraldDark)
                                    else -> null
                                },
                                modifier = Modifier.clickable(enabled = !isEvaluated) { onToggleTile(idx) }
                            ) {
                                Text(
                                    text = word,
                                    color = when {
                                        isWrongSpot -> TerracottaAccent
                                        isRightSpot -> EmeraldDark
                                        else -> Color.White
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
                        color = PalmContainerLight.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, EmeraldDark.copy(alpha = 0.4f)),
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
                                    color = EmeraldDark
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = question.correctTileOrder.joinToString(" "),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    color = EmeraldDark
                                )
                                if (question.phrase.malayalam.isNotBlank()) {
                                    Text(
                                        text = question.phrase.malayalam,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = PalmOnContainerLight
                                    )
                                }
                            }
                            IconButton(onClick = onSpeakPrompt) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Listen correct pronunciation",
                                    tint = EmeraldDark
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
    selectedIndex: Int?,
    onSelectOption: (Int) -> Unit,
    isEvaluated: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        options.forEachIndexed { index, optionText ->
            val isSelected = selectedIndex == index
            val cardColor = when {
                isEvaluated && index == 0 -> PalmContainerLight
                isEvaluated && isSelected && index != 0 -> TerracottaContainer
                isSelected -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.surface
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable(enabled = !isEvaluated) { onSelectOption(index) }
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .testTag("quiz_option_$index"),
                colors = CardDefaults.cardColors(containerColor = cardColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { if (!isEvaluated) onSelectOption(index) }
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = optionText,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 16.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
