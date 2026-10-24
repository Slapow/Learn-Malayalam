package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DialectRepository
import com.example.model.FlashcardDeck
import com.example.model.PhraseItem
import com.example.model.QuizletStudyMode
import com.example.model.UserStats
import com.example.ui.theme.*
import com.example.viewmodel.FlashcardState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardsScreen(
    state: FlashcardState,
    userStats: UserStats = UserStats(),
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
    val knownCount = userStats.learnedPhraseIds.size

    val rotation by animateFloatAsState(
        targetValue = if (state.isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "card_flip"
    )

    var levelMenuExpanded by remember { mutableStateOf(false) }

    var dragOffsetX by remember(currentPhrase?.id, state.currentCardIndex) {
        mutableFloatStateOf(0f)
    }
    val animatedOffsetX by animateFloatAsState(
        targetValue = dragOffsetX,
        animationSpec = tween(durationMillis = 150),
        label = "card_swipe_offset"
    )

    val isRecycled = currentPhrase != null && (
        state.stillLearningInSession.contains(currentPhrase.id) ||
            (userStats.masteryRecords[currentPhrase.id]?.mistakeCount ?: 0) > 0
        )

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("flashcards_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                title = {
                    Text(
                        text = "Flashcards",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                        maxLines = 1
                    )
                },
                actions = {
                    Box(modifier = Modifier.padding(end = 8.dp)) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
                            modifier = Modifier
                                .clickable { levelMenuExpanded = true }
                                .testTag("level_selector_dropdown_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = state.currentDeck.levelLabel,
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Filled.KeyboardArrowDown,
                                    contentDescription = "Select Level",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = levelMenuExpanded,
                            onDismissRequest = { levelMenuExpanded = false }
                        ) {
                            val isOverallSelected = state.currentDeck.id == "deck_overall"
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "Overall",
                                        fontWeight = if (isOverallSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                        color = if (isOverallSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                },
                                trailingIcon = if (isOverallSelected) {
                                    {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = "Selected",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                } else null,
                                onClick = {
                                    onSelectDeck(DialectRepository.buildOverallDeck(knownCount))
                                    levelMenuExpanded = false
                                },
                                modifier = Modifier.testTag("deck_chip_deck_overall")
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                            DialectRepository.studyPlanStages.forEach { stage ->
                                val stageDeck = DialectRepository.decks.find { it.id == stage.deckId }
                                    ?: DialectRepository.decks.first()
                                val isReached = stage.levelIndex == 0 ||
                                    knownCount >= stage.requiredWordsToUnlock ||
                                    userStats.masteredDeckIds.contains(stage.deckId)
                                val isSelected = state.currentDeck.id == stageDeck.id

                                DropdownMenuItem(
                                    enabled = isReached,
                                    text = {
                                        Text(
                                            text = stage.rankTitle,
                                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                            color = when {
                                                !isReached -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                                isSelected -> MaterialTheme.colorScheme.primary
                                                else -> MaterialTheme.colorScheme.onSurface
                                            }
                                        )
                                    },
                                    trailingIcon = when {
                                        !isReached -> {
                                            {
                                                Icon(
                                                    imageVector = Icons.Filled.Lock,
                                                    contentDescription = "Locked",
                                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                        isSelected -> {
                                            {
                                                Icon(
                                                    imageVector = Icons.Filled.Check,
                                                    contentDescription = "Selected",
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                        else -> null
                                    },
                                    onClick = {
                                        if (isReached) {
                                            onSelectDeck(stageDeck)
                                            levelMenuExpanded = false
                                        }
                                    },
                                    modifier = Modifier.testTag("deck_chip_${stageDeck.id}")
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (currentPhrase != null) {
                val borderColor = when {
                    dragOffsetX > 60f -> EmeraldPrimary
                    dragOffsetX < -60f -> TerracottaAccent
                    else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                }

                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            translationX = animatedOffsetX
                            rotationZ = (animatedOffsetX / 35f).coerceIn(-12f, 12f)
                            rotationY = rotation
                            cameraDistance = 12f * density
                        }
                        .pointerInput(currentPhrase.id, state.currentCardIndex) {
                            detectHorizontalDragGestures(
                                onDragEnd = {
                                    when {
                                        dragOffsetX > 150f -> {
                                            dragOffsetX = 0f
                                            onMarkKnown()
                                        }
                                        dragOffsetX < -150f -> {
                                            dragOffsetX = 0f
                                            onMarkLearning()
                                        }
                                        else -> {
                                            dragOffsetX = 0f
                                        }
                                    }
                                },
                                onDragCancel = {
                                    dragOffsetX = 0f
                                },
                                onHorizontalDrag = { change, dragAmount ->
                                    change.consume()
                                    dragOffsetX += dragAmount
                                }
                            )
                        }
                        .clickable { onFlipCard() }
                        .border(
                            width = 2.dp,
                            color = borderColor,
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
                        MinimalFlashcardContent(
                            centerWord = currentPhrase.english,
                            isRecycled = isRecycled,
                            onSpeak = { onSpeakPhrase(currentPhrase) }
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer { rotationY = 180f }
                        ) {
                            MinimalFlashcardContent(
                                centerWord = currentPhrase.manglish,
                                isRecycled = isRecycled,
                                onSpeak = { onSpeakPhrase(currentPhrase) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MinimalFlashcardContent(
    centerWord: String,
    isRecycled: Boolean,
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
                lineHeight = 36.sp
            ),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 16.dp)
        )
    }
}
