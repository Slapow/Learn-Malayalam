package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DialectRepository
import com.example.model.FlashcardDeck
import com.example.model.PhraseItem
import com.example.model.QuizletStudyMode
import com.example.ui.theme.*
import com.example.viewmodel.FlashcardState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardsScreen(
    state: FlashcardState,
    isListening: Boolean = false,
    speechStatus: String = "Ready to listen",
    onBack: () -> Unit,
    onSelectDeck: (FlashcardDeck) -> Unit,
    onSelectMode: (QuizletStudyMode) -> Unit = {},
    onLaunchAdaptiveFocus: () -> Unit = {},
    onFlipCard: () -> Unit,
    onMarkKnown: () -> Unit,
    onMarkLearning: () -> Unit,
    onSpeakPhrase: (PhraseItem) -> Unit,
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
    onStopSpeech: () -> Unit = {}
) {
    BackHandler { onBack() }

    val currentPhrase: PhraseItem? = state.currentDeck.phrases.getOrNull(state.currentCardIndex)
    val totalCards = state.currentDeck.phrases.size

    val unfamiliarCount = state.cardStreaks.values.count { it == 0 }
    val familiarCount = state.cardStreaks.values.count { it == 1 }
    val masteredCount = state.cardStreaks.values.count { it >= 2 }

    val rotation by animateFloatAsState(
        targetValue = if (state.isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "card_flip"
    )

    var deckMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "${state.currentDeck.iconEmoji} ${state.currentDeck.title}",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                            maxLines = 1
                        )
                        Text(
                            text = "${state.activeMode.iconEmoji} ${state.activeMode.title}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    FilledTonalButton(
                        onClick = onLaunchAdaptiveFocus,
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("adaptive_focus_deck_button")
                    ) {
                        Text("🎯 Weak Words", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Compact Deck Switcher & Progress Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        modifier = Modifier.clickable { deckMenuExpanded = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Deck: ${state.currentDeck.title}",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Filled.ArrowDropDown,
                                contentDescription = "Switch Deck",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = deckMenuExpanded,
                        onDismissRequest = { deckMenuExpanded = false }
                    ) {
                        DialectRepository.decks.forEach { deck ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "${deck.iconEmoji} ${deck.title} (${deck.levelLabel})",
                                        fontWeight = if (deck.id == state.currentDeck.id) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                onClick = {
                                    onSelectDeck(deck)
                                    deckMenuExpanded = false
                                },
                                modifier = Modifier.testTag("deck_chip_${deck.id}")
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = "Mastered: $masteredCount/$totalCards",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (masteredCount == totalCards) EmeraldDark else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                    )
                }
            }

            // Quizlet 7 Study Modes Selector Bar
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(QuizletStudyMode.entries) { mode ->
                    val isSelected = mode == state.activeMode
                    ElevatedFilterChip(
                        selected = isSelected,
                        onClick = { onSelectMode(mode) },
                        label = {
                            Text(
                                text = "${mode.iconEmoji} ${mode.shortLabel}",
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.elevatedFilterChipColors(
                            selectedContainerColor = GoldenSpice,
                            selectedLabelColor = GoldenOnContainer
                        ),
                        modifier = Modifier.testTag("study_mode_chip_${mode.name}")
                    )
                }
            }

            // 4. Mode Body
            when (state.activeMode) {
                QuizletStudyMode.FLASHCARDS -> {
                    // Classic 3D Flipping Flashcards
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (currentPhrase != null) {
                            Card(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .graphicsLayer {
                                        rotationY = rotation
                                        cameraDistance = 12f * density
                                    }
                                    .clickable { onFlipCard() }
                                    .border(
                                        width = 1.5.dp,
                                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(28.dp)
                                    )
                                    .testTag("flashcard_flip_card"),
                                shape = RoundedCornerShape(28.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                if (rotation <= 90f) {
                                    FlashcardFront(
                                        phrase = currentPhrase,
                                        cardIndex = state.currentCardIndex,
                                        totalCards = totalCards,
                                        onSpeak = { onSpeakPhrase(currentPhrase) }
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .graphicsLayer { rotationY = 180f }
                                    ) {
                                        FlashcardBack(
                                            phrase = currentPhrase,
                                            onSpeak = { onSpeakPhrase(currentPhrase) }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Bottom Action Bar: Study Again vs Flip vs Know It
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = onMarkLearning,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("card_still_learning_button")
                        ) {
                            Icon(Icons.Filled.Close, contentDescription = null, tint = TerracottaAccent)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Still Learning", color = TerracottaAccent, fontWeight = FontWeight.Bold)
                        }

                        FilledTonalIconButton(
                            onClick = onFlipCard,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .size(50.dp)
                                .testTag("card_flip_icon_button"),
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = GoldenContainerLight,
                                contentColor = GoldenOnContainer
                            )
                        ) {
                            Icon(Icons.Filled.Sync, contentDescription = "Flip Card")
                        }

                        Button(
                            onClick = onMarkKnown,
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("card_know_it_button")
                        ) {
                            Icon(Icons.Filled.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Know It! (+5)", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                QuizletStudyMode.MATCH -> {
                    QuizletMatchBoardPanel(
                        state = state,
                        onTapTile = onTapMatchTile,
                        onTickTimer = onTickMatchTimer,
                        onRestartMatch = onRestartMode
                    )
                }

                else -> {
                    // MIXED_MASTERY, LEARN, WRITE, SPEAK, TEST
                    DeckAdaptiveQuestionPanel(
                        state = state,
                        isListening = isListening,
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

@Composable
fun FlashcardFront(
    phrase: PhraseItem,
    cardIndex: Int,
    totalCards: Int,
    onSpeak: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = GoldenContainerLight,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "${phrase.levelBadge.uppercase()} • CARD ${cardIndex + 1}/$totalCards",
                    style = MaterialTheme.typography.labelSmall,
                    color = GoldenOnContainer,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            IconButton(onClick = onSpeak) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "Listen spoken audio",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 12.dp)
        ) {
            Text(
                text = "ENGLISH MEANING",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.2.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = phrase.english,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp,
                    lineHeight = 32.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            var showFrontHint by remember(phrase.id) { mutableStateOf(false) }
            if (phrase.wordBreakdown.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                if (!showFrontHint) {
                    Surface(
                        onClick = { showFrontHint = true },
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "💡 Tap for hint",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                } else {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "Hint: ${phrase.wordBreakdown.substringBefore("=").trim()}...",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Filled.TouchApp,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tap card to reveal Manglish, Phonetics & Audio",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun FlashcardBack(
    phrase: PhraseItem,
    onSpeak: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = PalmContainerLight,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "SPOKEN MANGLISH & MALAYALAM",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = EmeraldDark
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            FilledTonalIconButton(
                onClick = onSpeak,
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "Speak Malayalam",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            // 1. Manglish (Roman Script)
            Text(
                text = phrase.manglish,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 25.sp,
                    lineHeight = 32.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )

            // Phonetics
            if (phrase.phoneticGuide.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "🔊 Say it: ${phrase.phoneticGuide}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = TerracottaAccent
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 2. Malayalam Script
            Text(
                text = phrase.malayalam,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 19.sp,
                    lineHeight = 26.sp
                ),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
            )

            // Word-by-word Breakdown
            if (phrase.wordBreakdown.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = PalmContainerLight.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "🧩 ${phrase.wordBreakdown}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = EmeraldDark,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            // 3. Cultural & Regional Context
            if (phrase.regionalNuance.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = GoldenContainerLight.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "💡 ${phrase.regionalNuance}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.5.sp,
                            lineHeight = 17.sp
                        ),
                        color = GoldenOnContainer,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }

        Text(
            text = "Tap card to flip back",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}
