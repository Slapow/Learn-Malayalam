package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
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
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Practice Drills",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                },
                actions = {
                    Surface(
                        color = GoldenContainerLight,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Text(
                            text = "Q ${state.currentIndex + 1}/${state.questions.size}",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = GoldenOnContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            )
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

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = when (currentQ.type) {
                                        QuizType.MANGLISH_TYPING -> "Typing"
                                        QuizType.SPEAKING_PRACTICE -> "Speaking"
                                        QuizType.WORD_TILES -> "Tiles"
                                        QuizType.DIALECT_CHOICE -> "Choice"
                                    },
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }

                            if (state.reshuffledCount > 0) {
                                Text(
                                    text = "🔄 Reshuffled: ${state.reshuffledCount}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = TerracottaAccent
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = currentQ.prompt,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                lineHeight = 27.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (currentQ.hint.isNotBlank() || currentQ.phrase.wordBreakdown.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            if (!isHintRevealed) {
                                Surface(
                                    onClick = { isHintRevealed = true },
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.testTag("reveal_hint_button")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("💡", fontSize = 12.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Tap to reveal hint",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            } else {
                                Surface(
                                    color = PalmContainerLight.copy(alpha = 0.55f),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        if (currentQ.hint.isNotBlank()) {
                                            Text(
                                                text = "💡 Hint: ${currentQ.hint}",
                                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                                color = EmeraldDark
                                            )
                                        }
                                        if (currentQ.phrase.wordBreakdown.isNotBlank()) {
                                            if (currentQ.hint.isNotBlank()) Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "🧩 Breakdown: ${currentQ.phrase.wordBreakdown}",
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
                        when (currentQ.type) {
                            QuizType.MANGLISH_TYPING -> {
                                ManglishTypingCard(
                                    typedInput = state.typedInput,
                                    onTypedInputChange = onTypedInputChange,
                                    onSpeakPrompt = { onSpeakPhrase(currentQ.phrase) },
                                    onSubmit = {
                                        focusManager.clearFocus()
                                        onEvaluate()
                                    },
                                    isEvaluated = state.isEvaluated
                                )
                            }

                            QuizType.SPEAKING_PRACTICE -> {
                                SpeakingPracticeCard(
                                    phrase = currentQ.phrase,
                                    isListening = isListening,
                                    speechStatus = speechStatus,
                                    transcription = state.speechTranscription,
                                    onListenAudio = { onSpeakPhrase(currentQ.phrase) },
                                    onStartSpeech = onStartSpeech,
                                    onStopSpeech = onStopSpeech
                                )
                            }

                            QuizType.WORD_TILES -> {
                                WordTilesBuilderCard(
                                    question = currentQ,
                                    selectedIndices = state.selectedTileIndices,
                                    onToggleTile = onToggleTile
                                )
                            }

                            QuizType.DIALECT_CHOICE -> {
                                DialectChoiceCard(
                                    options = currentQ.options,
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
                                        text = "We added this card back to the shuffle! Type \"${currentQ.phrase.manglish}\" below to continue:",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "${currentQ.phrase.manglish}  (${currentQ.phrase.phoneticGuide})",
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
                                        label = { Text("Type '${currentQ.phrase.manglish}'") },
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

                // Bottom Evaluation Drawer
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = if (state.isEvaluated) {
                        if (state.isCorrect) PalmContainerLight else TerracottaContainer
                    } else MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 14.dp)
                    ) {
                        if (state.isEvaluated) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 10.dp)
                            ) {
                                Icon(
                                    imageVector = if (state.isCorrect) Icons.Filled.CheckCircle else Icons.Filled.Error,
                                    contentDescription = null,
                                    tint = if (state.isCorrect) EmeraldPrimary else TerracottaAccent,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = state.feedbackMessage,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (state.isCorrect) PalmOnContainerLight else TerracottaAccent
                                )
                            }

                            val canContinue = !state.mustCopyAnswer || state.isCopyAnswerUnlocked
                            Button(
                                onClick = onNext,
                                enabled = canContinue,
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (state.isCorrect) EmeraldPrimary else TerracottaAccent,
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
                                    } else if (state.currentIndex + 1 >= state.questions.size) {
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

@Composable
fun ManglishTypingCard(
    typedInput: String,
    onTypedInputChange: (String) -> Unit,
    onSpeakPrompt: () -> Unit,
    onSubmit: () -> Unit,
    isEvaluated: Boolean
) {
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
                    text = "Type in Manglish:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                IconButton(onClick = onSpeakPrompt) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Listen prompt",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = typedInput,
                onValueChange = onTypedInputChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("manglish_text_input"),
                placeholder = { Text("Type your answer here...") },
                singleLine = false,
                maxLines = 3,
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onSubmit() }),
                enabled = !isEvaluated
            )
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
    onToggleTile: (Int) -> Unit
) {
    val shuffledOrder = remember(question.id) {
        question.correctTileOrder.indices.shuffled()
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Assembled Answer:",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
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
                        selectedIndices.forEach { idx ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.clickable { onToggleTile(idx) }
                            ) {
                                Text(
                                    text = question.correctTileOrder[idx],
                                    color = Color.White,
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
                            .clickable(enabled = !isUsed) { onToggleTile(tileIdx) }
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
