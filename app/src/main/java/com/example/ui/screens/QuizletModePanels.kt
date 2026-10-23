package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.example.model.*
import com.example.ui.components.SpeechPracticeBar
import com.example.ui.components.ThreePartPhraseCard
import com.example.ui.theme.*
import com.example.viewmodel.FlashcardState
import kotlinx.coroutines.delay

@Composable
fun DeckAdaptiveQuestionPanel(
    state: FlashcardState,
    isListening: Boolean,
    speechStatus: String,
    onTypedChange: (String) -> Unit,
    onSelectMcOption: (Int) -> Unit,
    onSelectTrueFalse: (Boolean) -> Unit,
    onCopyInputChange: (String) -> Unit,
    onEvaluate: () -> Unit,
    onOverrideCorrect: () -> Unit,
    onNext: () -> Unit,
    onRestartMode: () -> Unit,
    onSpeakPhrase: (PhraseItem) -> Unit,
    onStartSpeech: ((String) -> Unit) -> Unit,
    onStopSpeech: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val currentQ = state.studyQueue.getOrNull(state.currentQuestionIndex)

    if (state.isRoundComplete || currentQ == null) {
        val accuracy = if (state.totalAnsweredInRound > 0) {
            ((state.totalCorrectInRound.toFloat() / state.totalAnsweredInRound.toFloat()) * 100).toInt()
        } else 100
        val letterGrade = when {
            accuracy >= 95 -> "A+"
            accuracy >= 88 -> "A"
            accuracy >= 80 -> "B"
            accuracy >= 70 -> "C"
            else -> "Keep Practicing"
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(PalmContainerLight),
                contentAlignment = Alignment.Center
            ) {
                Text("🎓", fontSize = 42.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "${state.activeMode.title} Complete!",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Grade: $letterGrade ($accuracy%) • ${state.totalCorrectInRound}/${state.totalAnsweredInRound} first-try hits • ${state.reshuffleEventsCount} cards reshuffled until mastered!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onRestartMode,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("restart_study_mode_button")
            ) {
                Icon(Icons.Filled.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Study This Deck Again", fontWeight = FontWeight.Bold)
            }
        }
        return
    }

    val cardStreak = state.cardStreaks[currentQ.phrase.id] ?: 0
    var isHintRevealed by remember(currentQ.id) { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Top Status Row: Format Tag + Card Mastery Streak (0/2, 1/2, 2/2)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = when (currentQ.format) {
                                StudyPromptFormat.MULTIPLE_CHOICE -> "Choice"
                                StudyPromptFormat.TRUE_FALSE -> "True / False"
                                StudyPromptFormat.WRITTEN_MANGLISH -> "Typing"
                                StudyPromptFormat.SPOKEN_OUT_LOUD -> "Speaking"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (currentQ.isRemediationStep) {
                            Surface(
                                color = GoldenContainerLight,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.padding(end = 6.dp)
                            ) {
                                Text(
                                    text = "🎯 Building Block Refresher",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = GoldenOnContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Surface(
                            color = when (cardStreak) {
                                0 -> TerracottaContainer
                                1 -> GoldenContainerLight
                                else -> PalmContainerLight
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = when (cardStreak) {
                                    0 -> "🔴 Streak 0/${currentQ.requiredMasteryStreak}"
                                    1 -> "🟡 Familiar 1/${currentQ.requiredMasteryStreak}"
                                    else -> "🟢 Mastered 2/2"
                                },
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = when (cardStreak) {
                                    0 -> TerracottaAccent
                                    1 -> GoldenOnContainer
                                    else -> EmeraldDark
                                },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Prompt Card with Audio & Level 0 Phonetic + Word Breakdown
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = currentQ.promptText,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 19.sp,
                                    lineHeight = 25.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )

                            FilledTonalIconButton(
                                onClick = { onSpeakPhrase(currentQ.phrase) },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Listen pronunciation",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        if (currentQ.subPromptText.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            if (!isHintRevealed) {
                                Surface(
                                    onClick = { isHintRevealed = true },
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.testTag("deck_reveal_hint_button")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("💡", fontSize = 12.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Tap to reveal hint & breakdown",
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
                                    Text(
                                        text = currentQ.subPromptText,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Medium,
                                            lineHeight = 17.sp
                                        ),
                                        color = EmeraldDark,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Interactive Input Area by Question Format
            item {
                when (currentQ.format) {
                    StudyPromptFormat.MULTIPLE_CHOICE -> {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            currentQ.options.forEachIndexed { idx, optText ->
                                val isSelected = state.selectedMcOption == idx
                                val isCorrectOpt = idx == currentQ.correctOptionIndex
                                val bgColor = when {
                                    state.isQuestionEvaluated && isCorrectOpt -> PalmContainerLight
                                    state.isQuestionEvaluated && isSelected && !isCorrectOpt -> TerracottaContainer
                                    isSelected -> MaterialTheme.colorScheme.primaryContainer
                                    else -> MaterialTheme.colorScheme.surface
                                }
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable(enabled = !state.isQuestionEvaluated) {
                                            onSelectMcOption(idx)
                                        }
                                        .border(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                                            shape = RoundedCornerShape(14.dp)
                                        )
                                        .testTag("study_mc_option_$idx"),
                                    colors = CardDefaults.cardColors(containerColor = bgColor)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = { if (!state.isQuestionEvaluated) onSelectMcOption(idx) }
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = optText,
                                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }

                    StudyPromptFormat.TRUE_FALSE -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            listOf(true to "✅ True", false to "❌ False").forEach { (boolVal, label) ->
                                val isSelected = state.selectedTrueFalse == boolVal
                                OutlinedButton(
                                    onClick = { if (!state.isQuestionEvaluated) onSelectTrueFalse(boolVal) },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(54.dp)
                                ) {
                                    Text(label, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                            }
                        }
                    }

                    StudyPromptFormat.WRITTEN_MANGLISH -> {
                        OutlinedTextField(
                            value = state.typedResponse,
                            onValueChange = onTypedChange,
                            enabled = !state.isQuestionEvaluated,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("study_written_input"),
                            label = { Text("Type Manglish answer") },
                            placeholder = { Text("e.g., ${currentQ.phrase.manglish.take(3)}...") },
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = {
                                focusManager.clearFocus()
                                onEvaluate()
                            })
                        )
                    }

                    StudyPromptFormat.SPOKEN_OUT_LOUD -> {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Surface(
                                color = GoldenContainerLight.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Target to Speak Out Loud:",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = GoldenOnContainer
                                        )
                                        Text(
                                            text = currentQ.phrase.manglish,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        if (currentQ.phrase.phoneticGuide.isNotBlank()) {
                                            Text(
                                                text = "Phonetic: ${currentQ.phrase.phoneticGuide}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = GoldenOnContainer
                                            )
                                        }
                                    }
                                    IconButton(onClick = { onSpeakPhrase(currentQ.phrase) }) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                            contentDescription = "Hear pronunciation",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }

                            SpeechPracticeBar(
                                isListening = isListening,
                                onMicClick = {
                                    if (isListening) onStopSpeech() else onStartSpeech { _ -> }
                                },
                                statusMessage = speechStatus,
                                accuracyFeedback = if (state.spokenTranscript.isNotBlank()) {
                                    "Heard: \"${state.spokenTranscript}\""
                                } else null
                            )

                            // Optional fallback input if user is in a noisy/quiet environment
                            OutlinedTextField(
                                value = state.typedResponse,
                                onValueChange = onTypedChange,
                                enabled = !state.isQuestionEvaluated,
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Or type your spoken cadence here if mic is unavailable") },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }
            }

            // QUIZLET "COPY ANSWER WHEN WRONG" BOX
            if (state.isQuestionEvaluated && state.mustCopyToContinue) {
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = TerracottaContainer.copy(alpha = 0.55f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(2.dp, TerracottaAccent, RoundedCornerShape(18.dp))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "✍️ Copy Correct Answer to Continue",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                                    color = TerracottaAccent
                                )
                                TextButton(onClick = onOverrideCorrect) {
                                    Text(
                                        text = "Override: I was right",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            Text(
                                text = "This card was added back to the shuffle! Type the exact Manglish below until it matches to unlock Continue:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Surface(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = currentQ.phrase.manglish,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.ExtraBold
                                        ),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    if (currentQ.phrase.phoneticGuide.isNotBlank()) {
                                        Text(
                                            text = "Say it: ${currentQ.phrase.phoneticGuide}  •  \"${currentQ.phrase.english}\"",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = state.copyInputText,
                                onValueChange = onCopyInputChange,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("copy_answer_input"),
                                label = { Text("Retype '${currentQ.phrase.manglish}' here") },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                trailingIcon = {
                                    if (state.isCopyVerified) {
                                        Icon(
                                            imageVector = Icons.Filled.CheckCircle,
                                            contentDescription = "Matched",
                                            tint = EmeraldPrimary
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        // Bottom Evaluation & Continue Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = if (state.isQuestionEvaluated) {
                if (state.isQuestionCorrect) PalmContainerLight else TerracottaContainer
            } else MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 12.dp)
            ) {
                if (state.isQuestionEvaluated) {
                    Text(
                        text = state.gradeHeadline,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = if (state.isQuestionCorrect) EmeraldDark else TerracottaAccent
                    )
                    Text(
                        text = state.gradeDetail,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val canContinue = !state.mustCopyToContinue || state.isCopyVerified
                    Button(
                        onClick = onNext,
                        enabled = canContinue,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (state.isQuestionCorrect) EmeraldPrimary else TerracottaAccent,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("study_continue_button")
                    ) {
                        Text(
                            text = if (!canContinue) {
                                "Type Correct Answer Above to Continue"
                            } else {
                                "Continue (${state.currentQuestionIndex + 1}/${state.studyQueue.size})"
                            },
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    val canSubmit = when (currentQ.format) {
                        StudyPromptFormat.MULTIPLE_CHOICE -> state.selectedMcOption != null
                        StudyPromptFormat.TRUE_FALSE -> state.selectedTrueFalse != null
                        StudyPromptFormat.WRITTEN_MANGLISH -> state.typedResponse.isNotBlank()
                        StudyPromptFormat.SPOKEN_OUT_LOUD -> state.spokenTranscript.isNotBlank() || state.typedResponse.isNotBlank()
                    }
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            onEvaluate()
                        },
                        enabled = canSubmit,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("study_check_button")
                    ) {
                        Text("Check Answer", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

/**
 * Quizlet Match Mode: Timed 12-tile matching game against the clock
 */
@Composable
fun QuizletMatchBoardPanel(
    state: FlashcardState,
    onTapTile: (String) -> Unit,
    onTickTimer: () -> Unit,
    onRestartMatch: () -> Unit
) {
    LaunchedEffect(state.activeMode, state.isMatchFinished) {
        while (state.activeMode == QuizletStudyMode.MATCH && !state.isMatchFinished) {
            delay(100L)
            onTickTimer()
        }
    }

    val secondsStr = "%.1fs".format(state.matchElapsedTenths / 10.0f)
    val bestStr = state.matchBestTenths?.let { "%.1fs".format(it / 10.0f) } ?: "--"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Timer & Scoreboard bar
        Surface(
            color = GoldenContainerLight,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Timer, contentDescription = null, tint = GoldenOnContainer)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Time: $secondsStr",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = GoldenOnContainer
                    )
                }

                Text(
                    text = "🏆 Best: $bestStr",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = EmeraldDark
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (state.isMatchFinished) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("⚡", fontSize = 54.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Board Cleared in $secondsStr!",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Best Time: $bestStr • +20 XP earned!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onRestartMatch,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    Icon(Icons.Filled.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Play Match Again", fontWeight = FontWeight.Bold)
                }
            }
        } else {
            Text(
                text = "Tap an English card and its matching Manglish card to clear them (+1.0s penalty on wrong match):",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(state.matchTiles, key = { it.id }) { tile ->
                    val isSelected = state.selectedMatchTileId == tile.id
                    val isMismatched = state.mismatchedTileIds.contains(tile.id)

                    if (tile.isMatched) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(88.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(PalmContainerLight.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "Matched",
                                tint = EmeraldPrimary.copy(alpha = 0.5f)
                            )
                        }
                    } else {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(88.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { onTapTile(tile.id) }
                                .border(
                                    width = if (isSelected || isMismatched) 2.dp else 1.dp,
                                    color = when {
                                        isMismatched -> TerracottaAccent
                                        isSelected -> MaterialTheme.colorScheme.primary
                                        else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                    },
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .testTag("match_tile_${tile.id}"),
                            colors = CardDefaults.cardColors(
                                containerColor = when {
                                    isMismatched -> TerracottaContainer
                                    isSelected -> MaterialTheme.colorScheme.primaryContainer
                                    tile.isEnglish -> MaterialTheme.colorScheme.surface
                                    else -> GoldenContainerLight.copy(alpha = 0.35f)
                                }
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(10.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = tile.text,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        lineHeight = 18.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center,
                                    maxLines = 2
                                )
                                if (tile.subText.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = tile.subText,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
