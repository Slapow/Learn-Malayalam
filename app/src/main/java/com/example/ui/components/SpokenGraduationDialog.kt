package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.ActflProficiencyTier
import com.example.model.TierGraduationExamState
import com.example.ui.theme.*

@Composable
fun SpokenGraduationDialog(
    examState: TierGraduationExamState,
    onToggleListen: () -> Unit,
    onSpeakPhraseText: (String, String) -> Unit,
    onNextPrompt: () -> Unit,
    onSelfVerifySpoken: () -> Unit,
    onDismiss: () -> Unit,
    isDevModeEnabled: Boolean = false,
    onEndDevMode: () -> Unit = {},
    onDevGetCorrect: () -> Unit = {},
    onDevSkipToLast: () -> Unit = {}
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 20.dp)
                .clip(RoundedCornerShape(28.dp))
                .border(2.dp, GoldenSpice, RoundedCornerShape(28.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Header with Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(examState.tier.iconEmoji, fontSize = 20.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "ACTFL ORAL GRADUATION EXAM",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = examState.tier.graduationTitle,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_graduation_dialog")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close Exam")
                    }
                }

                if (isDevModeEnabled && !examState.isExamFinished) {
                    DevModeTestingBar(
                        onEndDevMode = onEndDevMode,
                        onCurrentQuestionCorrect = onDevGetCorrect,
                        onSkipToLastQuestion = onDevSkipToLast,
                        modeLabel = "Oral Exam (${examState.currentPromptIndex + 1}/${examState.prompts.size})"
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (examState.isExamFinished) {
                    // =========================================================================
                    // GRADUATION RESULT SCREEN (DIPLOMA / BADGE)
                    // =========================================================================
                    if (examState.didPassGraduation) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp))
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("🎓", fontSize = 54.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "OFFICIAL ORAL DEFENSE PASSED!",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 0.5.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "You successfully graduated from ${examState.tier.title}!\nAll words and next tier stages are now permanently unlocked.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Surface(
                                    color = GoldenSpice,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "🏆 Score: ${examState.scoreCount}/${examState.prompts.size} Correct",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = TerracottaContainer.copy(alpha = 0.5f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, TerracottaAccent, RoundedCornerShape(20.dp))
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("🎙️", fontSize = 48.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Almost There! Score: ${examState.scoreCount}/${examState.prompts.size}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                    color = TerracottaAccent
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "To graduate to the next tier, aim for at least ${examState.prompts.size - 1} correct spoken responses. Practice the deck in Speak or Mixed mode and retake anytime!",
                                    style = MaterialTheme.typography.bodySmall,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("dismiss_graduation_summary")
                    ) {
                        Text(
                            text = if (examState.didPassGraduation) "🚀 Continue with Next Tier" else "Close & Keep Practicing",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                } else {
                    // =========================================================================
                    // ACTIVE ORAL EXAM QUESTION
                    // =========================================================================
                    val prompt = examState.prompts.getOrNull(examState.currentPromptIndex)
                    if (prompt != null) {
                        val progress = (examState.currentPromptIndex.toFloat() / examState.prompts.size.toFloat())
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Question ${examState.currentPromptIndex + 1} of ${examState.prompts.size}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Score: ${examState.scoreCount}/${examState.currentPromptIndex}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Scenario Prompt Card
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "SITUATION PROMPT",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 0.8.sp
                                    ),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = prompt.scenarioPrompt,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                        lineHeight = 23.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (prompt.hint.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "💡 Hint: ${prompt.hint}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Audio sample button
                        OutlinedButton(
                            onClick = { onSpeakPhraseText(prompt.phrase.malayalam, prompt.phrase.manglish) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("listen_target_phrase_button")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Listen", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Hear Target Phrase Audio", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Microphone Big Button
                        val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
                        val scale by infiniteTransition.animateFloat(
                            initialValue = 1f,
                            targetValue = if (examState.isListening) 1.14f else 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(600, easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "mic_scale"
                        )

                        FilledIconButton(
                            onClick = onToggleListen,
                            shape = CircleShape,
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = if (examState.isListening) TerracottaAccent else MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier
                                .size(76.dp)
                                .scale(scale)
                                .testTag("oral_exam_mic_button")
                        ) {
                            Icon(
                                if (examState.isListening) Icons.Default.MicOff else Icons.Default.Mic,
                                contentDescription = if (examState.isListening) "Stop listening" else "Tap to speak",
                                tint = if (examState.isListening) Color.White else MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (examState.isListening) "🎙️ Listening... Speak into your mic!" else "Tap the mic and speak out loud",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (examState.isListening) TerracottaAccent else MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Spoken heard transcript
                        if (examState.spokenTranscript.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Heard: \"${examState.spokenTranscript}\"",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                    modifier = Modifier.padding(10.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        // Evaluated Feedback
                        AnimatedVisibility(visible = examState.isEvaluated) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (examState.isPromptPassed) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (examState.isPromptPassed) "✅" else "⚠️",
                                            fontSize = 20.sp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = examState.feedbackMessage,
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                            color = if (examState.isPromptPassed) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.error
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (!examState.isPromptPassed) {
                                        OutlinedButton(
                                            onClick = onSelfVerifySpoken,
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = "I Spoke It Clearly",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                            )
                                        }
                                    }

                                    Button(
                                        onClick = onNextPrompt,
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.weight(1f).testTag("next_oral_prompt_button")
                                    ) {
                                        Text(
                                            text = if (examState.currentPromptIndex + 1 >= examState.prompts.size) "Finish Exam →" else "Next Question →",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
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
}
