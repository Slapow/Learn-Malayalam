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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.AnswerPrefixHint
import com.example.ui.components.AnswerPrefixHintPanel
import com.example.viewmodel.FlashcardState

@Composable
fun DeckAdaptiveQuestionPanel(
    state: FlashcardState,
    isListening: Boolean,
    speechLevel: Float = 0f,
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
                    .background(MaterialTheme.colorScheme.primaryContainer),
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
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
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
    val usesDrillProductionFormat =
        currentQ.format == StudyPromptFormat.WRITTEN_MANGLISH ||
            currentQ.format == StudyPromptFormat.SPOKEN_OUT_LOUD
    var isHintExpanded by remember(currentQ.id, state.isQuestionEvaluated) { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Top Status Row: Card Mastery Streak (0/2, 1/2, 2/2)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (currentQ.isRemediationStep) {
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.padding(end = 6.dp)
                            ) {
                                Text(
                                    text = "🎯 Building Block Refresher",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Surface(
                            color = when (cardStreak) {
                                0 -> MaterialTheme.colorScheme.errorContainer
                                1 -> MaterialTheme.colorScheme.secondaryContainer
                                else -> MaterialTheme.colorScheme.primaryContainer
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
                                    0 -> MaterialTheme.colorScheme.error
                                    1 -> MaterialTheme.colorScheme.onSecondaryContainer
                                    else -> MaterialTheme.colorScheme.onPrimaryContainer
                                },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Prompt with a small prefix hint for typing and spoken questions
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

                            if (usesDrillProductionFormat) {
                                AnswerPrefixHint(
                                    answer = currentQ.phrase.manglish,
                                    isEvaluated = state.isQuestionEvaluated,
                                    isExpanded = isHintExpanded,
                                    onToggle = { isHintExpanded = !isHintExpanded },
                                    modifier = Modifier.padding(start = 8.dp).testTag("deck_reveal_hint_button")
                                )
                            }
                            if (!usesDrillProductionFormat) {
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
                        }
                        if (usesDrillProductionFormat) {
                            AnswerPrefixHintPanel(
                                answer = currentQ.phrase.manglish,
                                isExpanded = isHintExpanded,
                                isEvaluated = state.isQuestionEvaluated,
                                modifier = Modifier.testTag("deck_hint_panel")
                            )
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
                                val isWrongSelection = state.isQuestionEvaluated && isSelected && !isCorrectOpt
                                val emeraldChoiceColor = MaterialTheme.colorScheme.primary

                                val borderColor = when {
                                    state.isQuestionEvaluated && isCorrectOpt -> emeraldChoiceColor
                                    isWrongSelection -> MaterialTheme.colorScheme.error
                                    isSelected -> MaterialTheme.colorScheme.primary
                                    else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                }

                                val textColor = when {
                                    state.isQuestionEvaluated && isCorrectOpt -> emeraldChoiceColor
                                    isWrongSelection -> MaterialTheme.colorScheme.error
                                    else -> MaterialTheme.colorScheme.onSurface
                                }

                                val radioColor = when {
                                    state.isQuestionEvaluated && isCorrectOpt -> emeraldChoiceColor
                                    isWrongSelection -> MaterialTheme.colorScheme.error
                                    else -> MaterialTheme.colorScheme.primary
                                }

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable(enabled = !state.isQuestionEvaluated) {
                                            onSelectMcOption(idx)
                                        }
                                        .border(
                                            width = if (isSelected || (state.isQuestionEvaluated && isCorrectOpt)) 2.dp else 1.dp,
                                            color = borderColor,
                                            shape = RoundedCornerShape(14.dp)
                                        )
                                        .testTag("study_mc_option_$idx"),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            RadioButton(
                                                selected = isSelected,
                                                onClick = { if (!state.isQuestionEvaluated) onSelectMcOption(idx) },
                                                colors = RadioButtonDefaults.colors(
                                                    selectedColor = radioColor,
                                                    unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = optText,
                                                style = MaterialTheme.typography.bodyLarge.copy(
                                                    fontWeight = if (isSelected || (state.isQuestionEvaluated && isCorrectOpt)) FontWeight.Bold else FontWeight.Normal
                                                ),
                                                color = textColor
                                            )
                                        }

                                        if (state.isQuestionEvaluated) {
                                            if (isCorrectOpt) {
                                                Icon(
                                                    imageVector = Icons.Filled.Check,
                                                    contentDescription = "Correct",
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            } else if (isWrongSelection) {
                                                Icon(
                                                    imageVector = Icons.Filled.Close,
                                                    contentDescription = "Wrong",
                                                    tint = MaterialTheme.colorScheme.error,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
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
                                val isCorrectChoice = boolVal == currentQ.isTruePair
                                val isWrongSelection = state.isQuestionEvaluated && isSelected && !isCorrectChoice

                                val btnBorderColor = when {
                                    state.isQuestionEvaluated && isCorrectChoice -> MaterialTheme.colorScheme.primary
                                    isWrongSelection -> MaterialTheme.colorScheme.error
                                    isSelected -> MaterialTheme.colorScheme.primary
                                    else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                }

                                val btnTextColor = when {
                                    state.isQuestionEvaluated && isCorrectChoice -> MaterialTheme.colorScheme.primary
                                    isWrongSelection -> MaterialTheme.colorScheme.error
                                    else -> MaterialTheme.colorScheme.onSurface
                                }

                                OutlinedButton(
                                    onClick = { if (!state.isQuestionEvaluated) onSelectTrueFalse(boolVal) },
                                    shape = RoundedCornerShape(14.dp),
                                    border = androidx.compose.foundation.BorderStroke(
                                        width = if (isSelected || (state.isQuestionEvaluated && isCorrectChoice)) 2.dp else 1.dp,
                                        color = btnBorderColor
                                    ),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = MaterialTheme.colorScheme.surface,
                                        contentColor = btnTextColor
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(54.dp)
                                ) {
                                    Text(label, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = btnTextColor)
                                }
                            }
                        }
                    }

                    StudyPromptFormat.WRITTEN_MANGLISH -> {
                        ManglishTypingCard(
                            typedInput = state.typedResponse,
                            onTypedInputChange = onTypedChange,
                            onSubmit = {
                                focusManager.clearFocus()
                                onEvaluate()
                            },
                            onSkip = {},
                            isEvaluated = state.isQuestionEvaluated,
                            isCorrect = state.isQuestionCorrect,
                            isSkipped = false,
                            correctPhrase = currentQ.phrase
                        )
                    }

                    StudyPromptFormat.SPOKEN_OUT_LOUD -> {
                        SpeakingPracticeCard(
                            phrase = currentQ.phrase,
                            isListening = isListening,
                            speechLevel = speechLevel,
                            speechStatus = speechStatus,
                            transcription = state.spokenTranscript,
                            typedInput = state.typedResponse,
                            onTypedInputChange = onTypedChange,
                            isEvaluated = state.isQuestionEvaluated,
                            isCorrect = state.isQuestionCorrect,
                            onStartSpeech = onStartSpeech,
                            onStopSpeech = onStopSpeech,
                            onSubmitTyped = {
                                focusManager.clearFocus()
                                onEvaluate()
                            }
                        )
                    }
                }
            }

            // QUIZLET "COPY ANSWER WHEN WRONG" BOX
            if (state.isQuestionEvaluated && state.mustCopyToContinue) {
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.55f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(2.dp, MaterialTheme.colorScheme.error, RoundedCornerShape(18.dp))
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
                                    color = MaterialTheme.colorScheme.error
                                )
                                if (!usesDrillProductionFormat) {
                                    TextButton(onClick = onOverrideCorrect) {
                                        Text(
                                            text = "Override: I was right",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
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
                                            tint = MaterialTheme.colorScheme.primary
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.isQuestionEvaluated) {
                    if (!usesDrillProductionFormat) {
                        Text(
                            text = state.gradeHeadline,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = if (state.isQuestionCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = state.gradeDetail,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (!usesDrillProductionFormat || !state.isQuestionCorrect) {
                        if (usesDrillProductionFormat) {
                            OutlinedButton(
                                onClick = onOverrideCorrect,
                                shape = RoundedCornerShape(16.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.5.dp,
                                    MaterialTheme.colorScheme.primary
                                ),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("study_override_button")
                            ) {
                                Text("Override: I was right", fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val canContinue = !state.mustCopyToContinue || state.isCopyVerified
                        Button(
                            onClick = onNext,
                            enabled = canContinue,
                            shape = RoundedCornerShape(16.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (state.isQuestionCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                contentColor = if (state.isQuestionCorrect) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onError
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
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
                    }
                } else if (currentQ.format != StudyPromptFormat.WRITTEN_MANGLISH &&
                    currentQ.format != StudyPromptFormat.SPOKEN_OUT_LOUD) {
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            onEvaluate()
                        },
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("study_check_answer_button")
                    ) {
                        Text("Check Answer", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun MatchGameModePanel(
    state: FlashcardState,
    onTapTile: (String) -> Unit,
    onRestartMatch: () -> Unit
) {
    val secondsStr = String.format("%.1fs", state.matchElapsedTenths / 10f)
    val bestStr = state.matchBestTenths?.let { String.format("%.1fs", it / 10f) } ?: "--"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Timer & Scoreboard bar
        Surface(
            color = MaterialTheme.colorScheme.secondaryContainer,
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
                    Icon(Icons.Filled.Timer, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Time: $secondsStr",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                Text(
                    text = "🏆 Best: $bestStr",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
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
                    text = "Best Time: $bestStr",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onRestartMatch,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
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
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "Matched",
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
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
                                        isMismatched -> MaterialTheme.colorScheme.error
                                        isSelected -> MaterialTheme.colorScheme.primary
                                        else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                    },
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .testTag("match_tile_${tile.id}"),
                            colors = CardDefaults.cardColors(
                                containerColor = when {
                                    isMismatched -> MaterialTheme.colorScheme.errorContainer
                                    isSelected -> MaterialTheme.colorScheme.primaryContainer
                                    tile.isEnglish -> MaterialTheme.colorScheme.surface
                                    else -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f)
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
                            }
                        }
                    }
                }
            }
        }
    }
}
