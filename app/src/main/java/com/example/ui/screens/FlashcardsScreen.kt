package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
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
import com.example.model.FlashcardDeck
import com.example.model.PhraseItem
import com.example.model.QuizletStudyMode
import com.example.model.UserStats
import com.example.ui.theme.*
import com.example.viewmodel.FlashcardState
import kotlinx.coroutines.launch
import kotlin.math.abs

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
    onStopSpeech: () -> Unit = {}
) {
    BackHandler { onBack() }

    val currentPhrase: PhraseItem? = state.currentDeck.phrases.getOrNull(state.currentCardIndex)
    val totalCards = state.currentDeck.phrases.size
    val nextPhrase: PhraseItem? = if (totalCards > 1) {
        state.currentDeck.phrases.getOrNull((state.currentCardIndex + 1) % totalCards)
    } else null
    val knownCount = userStats.learnedPhraseIds.size

    val rotation by animateFloatAsState(
        targetValue = if (state.isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 360, easing = FastOutSlowInEasing),
        label = "card_flip"
    )

    var levelMenuExpanded by remember { mutableStateOf(false) }
    var settingsMenuExpanded by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    val swipeThresholdPx = with(density) { 115.dp.toPx() }
    val flyOffDistancePx = with(density) { 460.dp.toPx() }

    val cardOffset = remember(currentPhrase?.id, state.currentCardIndex) {
        Animatable(Offset.Zero, Offset.VectorConverter)
    }
    var isAnimatingOut by remember(currentPhrase?.id, state.currentCardIndex) {
        mutableStateOf(false)
    }

    val swipeRatio = (cardOffset.value.x / swipeThresholdPx).coerceIn(-1f, 1f)
    val absSwipeProgress = abs(swipeRatio).coerceIn(0f, 1f)

    val isRecycled = currentPhrase != null && (
        state.stillLearningInSession.contains(currentPhrase.id) ||
            (userStats.masteryRecords[currentPhrase.id]?.mistakeCount ?: 0) > 0
        )
    val isNextRecycled = nextPhrase != null && (
        state.stillLearningInSession.contains(nextPhrase.id) ||
            (userStats.masteryRecords[nextPhrase.id]?.mistakeCount ?: 0) > 0
        )

    // Subtle scale pulse on the tally badges when dragging toward left or right (Quizlet style)
    val leftBadgeScale = 1f + (if (swipeRatio < 0f) abs(swipeRatio) * 0.12f else 0f)
    val rightBadgeScale = 1f + (if (swipeRatio > 0f) swipeRatio * 0.12f else 0f)

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
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left group: Back arrow + Card Count to the right of back arrow
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.testTag("flashcards_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }

                        if (totalCards > 0) {
                            Surface(
                                color = GoldenContainerLight,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .testTag("flashcards_count_bubble")
                            ) {
                                Text(
                                    text = "${state.currentCardIndex + 1}/$totalCards",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = GoldenOnContainer,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }

                    // Right group: Level dropdown menu to the left of Settings + Settings at top right
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
                                modifier = Modifier
                                    .clickable { levelMenuExpanded = true }
                                    .testTag("level_selector_dropdown_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = state.currentDeck.levelLabel,
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Filled.KeyboardArrowDown,
                                        contentDescription = "Select Level",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
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
                                        onRestartDeck()
                                        settingsMenuExpanded = false
                                    },
                                    modifier = Modifier.testTag("flashcards_restart_option")
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Still Learning (Left, Red) and Learned (Right, Green) Tally Indicators
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = if (swipeRatio < -0.15f) {
                        TerracottaContainer
                    } else {
                        TerracottaContainer.copy(alpha = 0.7f)
                    },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(
                        width = if (swipeRatio < -0.15f) 1.5.dp else 1.dp,
                        color = TerracottaAccent.copy(alpha = if (swipeRatio < -0.15f) 0.9f else 0.5f)
                    ),
                    modifier = Modifier
                        .graphicsLayer {
                            scaleX = leftBadgeScale
                            scaleY = leftBadgeScale
                        }
                        .testTag("still_learning_tally_badge")
                ) {
                    Text(
                        text = "Still Learning: ${state.stillLearningInSession.size}",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = TerracottaAccent,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                Surface(
                    color = if (swipeRatio > 0.15f) {
                        PalmContainerLight
                    } else {
                        PalmContainerLight.copy(alpha = 0.75f)
                    },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(
                        width = if (swipeRatio > 0.15f) 1.5.dp else 1.dp,
                        color = EmeraldPrimary.copy(alpha = if (swipeRatio > 0.15f) 0.9f else 0.5f)
                    ),
                    modifier = Modifier
                        .graphicsLayer {
                            scaleX = rightBadgeScale
                            scaleY = rightBadgeScale
                        }
                        .testTag("learned_tally_badge")
                ) {
                    Text(
                        text = "Learned: ${state.learnedInSession.size}",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = EmeraldDark,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            // Quizlet-Style Stacked & Swipeable Flashcard Container
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                // 1. Underneath Next Card in Stack (scales & rises smoothly as top card is dragged)
                if (nextPhrase != null) {
                    val backScale = 0.93f + (0.07f * absSwipeProgress)
                    val backTranslationY = with(density) { (14.dp * (1f - absSwipeProgress)).toPx() }
                    val nextFrontWord = if (state.isEnglishOnFront) nextPhrase.english else nextPhrase.manglish

                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                scaleX = backScale
                                scaleY = backScale
                                translationY = backTranslationY
                                alpha = 0.65f + (0.35f * absSwipeProgress)
                            }
                            .border(
                                width = 1.5.dp,
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(28.dp)
                            ),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        MinimalFlashcardContent(
                            centerWord = nextFrontWord,
                            isRecycled = isNextRecycled,
                            swipeRatio = 0f,
                            onSpeak = {}
                        )
                    }
                }

                // 2. Active Foreground Card with Quizlet Physics & Fly-off Animation
                if (currentPhrase != null) {
                    val baseBorder = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    val borderColor = when {
                        swipeRatio > 0.05f -> lerp(baseBorder, EmeraldPrimary, swipeRatio)
                        swipeRatio < -0.05f -> lerp(baseBorder, TerracottaAccent, abs(swipeRatio))
                        else -> baseBorder
                    }

                    val baseSurface = MaterialTheme.colorScheme.surface
                    val cardContainerColor = when {
                        swipeRatio > 0.05f -> lerp(baseSurface, PalmContainerLight, swipeRatio * 0.35f)
                        swipeRatio < -0.05f -> lerp(baseSurface, TerracottaContainer, abs(swipeRatio) * 0.35f)
                        else -> baseSurface
                    }

                    val frontWord = if (state.isEnglishOnFront) currentPhrase.english else currentPhrase.manglish
                    val backWord = if (state.isEnglishOnFront) currentPhrase.manglish else currentPhrase.english

                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                translationX = cardOffset.value.x
                                translationY = cardOffset.value.y * 0.35f
                                rotationZ = (cardOffset.value.x / 28f).coerceIn(-16f, 16f)
                                rotationY = rotation
                                cameraDistance = 14f * this.density
                            }
                            .pointerInput(currentPhrase.id, state.currentCardIndex) {
                                val velocityTracker = VelocityTracker()
                                detectDragGestures(
                                    onDragStart = {
                                        velocityTracker.resetTracking()
                                    },
                                    onDragEnd = {
                                        if (isAnimatingOut) return@detectDragGestures
                                        val velocityX = velocityTracker.calculateVelocity().x
                                        val currentX = cardOffset.value.x
                                        val currentY = cardOffset.value.y

                                        val swipedRight = currentX > swipeThresholdPx ||
                                            (currentX > swipeThresholdPx * 0.4f && velocityX > 900f)
                                        val swipedLeft = currentX < -swipeThresholdPx ||
                                            (currentX < -swipeThresholdPx * 0.4f && velocityX < -900f)

                                        when {
                                            swipedRight -> {
                                                isAnimatingOut = true
                                                coroutineScope.launch {
                                                    cardOffset.animateTo(
                                                        targetValue = Offset(
                                                            x = flyOffDistancePx,
                                                            y = currentY + 60f
                                                        ),
                                                        animationSpec = tween(
                                                            durationMillis = 210,
                                                            easing = FastOutLinearInEasing
                                                        )
                                                    )
                                                    onMarkKnown()
                                                }
                                            }
                                            swipedLeft -> {
                                                isAnimatingOut = true
                                                coroutineScope.launch {
                                                    cardOffset.animateTo(
                                                        targetValue = Offset(
                                                            x = -flyOffDistancePx,
                                                            y = currentY + 60f
                                                        ),
                                                        animationSpec = tween(
                                                            durationMillis = 210,
                                                            easing = FastOutLinearInEasing
                                                        )
                                                    )
                                                    onMarkLearning()
                                                }
                                            }
                                            else -> {
                                                coroutineScope.launch {
                                                    cardOffset.animateTo(
                                                        targetValue = Offset.Zero,
                                                        animationSpec = spring(
                                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                                            stiffness = Spring.StiffnessMedium
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    },
                                    onDragCancel = {
                                        if (!isAnimatingOut) {
                                            coroutineScope.launch {
                                                cardOffset.animateTo(
                                                    targetValue = Offset.Zero,
                                                    animationSpec = spring(
                                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                                        stiffness = Spring.StiffnessMedium
                                                    )
                                                )
                                            }
                                        }
                                    },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        if (!isAnimatingOut) {
                                            velocityTracker.addPosition(
                                                change.uptimeMillis,
                                                change.position
                                            )
                                            coroutineScope.launch {
                                                cardOffset.snapTo(cardOffset.value + dragAmount)
                                            }
                                        }
                                    }
                                )
                            }
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                if (!isAnimatingOut) {
                                    onFlipCard()
                                }
                            }
                            .border(
                                width = if (absSwipeProgress > 0.1f) 2.5.dp else 1.5.dp,
                                color = borderColor,
                                shape = RoundedCornerShape(28.dp)
                            )
                            .testTag("flashcard_flip_card"),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = cardContainerColor
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        if (rotation <= 90f) {
                            MinimalFlashcardContent(
                                centerWord = frontWord,
                                isRecycled = isRecycled,
                                swipeRatio = swipeRatio,
                                onSpeak = { onSpeakPhrase(currentPhrase) }
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .graphicsLayer { rotationY = 180f }
                            ) {
                                MinimalFlashcardContent(
                                    centerWord = backWord,
                                    isRecycled = isRecycled,
                                    swipeRatio = swipeRatio,
                                    onSpeak = { onSpeakPhrase(currentPhrase) }
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

        // Quizlet-style live drag stamp badge centered at top while actively swiping
        if (abs(swipeRatio) > 0.12f) {
            val isKnow = swipeRatio > 0f
            val stampAlpha = ((abs(swipeRatio) - 0.12f) / 0.65f).coerceIn(0f, 1f)
            val stampScale = 0.85f + (0.15f * stampAlpha)
            Surface(
                color = if (isKnow) EmeraldPrimary else TerracottaAccent,
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
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isKnow) "Know" else "Still learning",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = Color.White
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
