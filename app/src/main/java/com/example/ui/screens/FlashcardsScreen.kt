package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
import kotlinx.coroutines.delay
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

    var showLevelDialog by remember { mutableStateOf(false) }
    var settingsMenuExpanded by remember { mutableStateOf(false) }

    // Autoplay mode state
    var isAutoplayOn by remember { mutableStateOf(false) }
    var autoplayBannerText by remember { mutableStateOf<String?>(null) }

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

    LaunchedEffect(currentPhrase?.id, state.currentCardIndex) {
        cardOffset.snapTo(Offset.Zero)
        isAnimatingOut = false
    }

    // Autoplay banner auto-hide effect
    LaunchedEffect(autoplayBannerText) {
        if (autoplayBannerText != null) {
            delay(1800L)
            autoplayBannerText = null
        }
    }

    // Autoplay automated loop: speaks each word, flips card, speaks second word, then swipes it away
    LaunchedEffect(isAutoplayOn, currentPhrase?.id, state.currentCardIndex) {
        if (!isAutoplayOn || currentPhrase == null) return@LaunchedEffect

        if (state.isEnglishOnFront) {
            // Front face shows English
            onSpeakEnglish(currentPhrase.english)
            delay(2200L)
            if (!isAutoplayOn) return@LaunchedEffect

            // Automatically flip card
            onFlipCard()

            // Back face shows Malayalam
            onSpeakPhrase(currentPhrase)
            delay(2400L)
            if (!isAutoplayOn) return@LaunchedEffect
        } else {
            // Front face shows Malayalam
            onSpeakPhrase(currentPhrase)
            delay(2200L)
            if (!isAutoplayOn) return@LaunchedEffect

            // Automatically flip card
            onFlipCard()

            // Back face shows English
            onSpeakEnglish(currentPhrase.english)
            delay(2400L)
            if (!isAutoplayOn) return@LaunchedEffect
        }

        // Automatically swipe card away
        isAnimatingOut = true
        cardOffset.animateTo(
            targetValue = Offset(flyOffDistancePx, 20f),
            animationSpec = tween(durationMillis = 220, easing = FastOutLinearInEasing)
        )

        // Advance to next card without changing tally
        onAdvanceWithoutTally()
    }

    val swipeRatio = (cardOffset.value.x / swipeThresholdPx).coerceIn(-1f, 1f)
    val absSwipeProgress = abs(swipeRatio).coerceIn(0f, 1f)

    // Only show recycled indicator if card was sent to still learning AND shown for the 2+ time in this session
    val isRecycled = currentPhrase != null &&
        state.cardsSentToStillLearning.contains(currentPhrase.id) &&
        (state.cardPresentationCount[currentPhrase.id] ?: 1) >= 2

    val isNextRecycled = nextPhrase != null &&
        state.cardsSentToStillLearning.contains(nextPhrase.id) &&
        (state.cardPresentationCount[nextPhrase.id] ?: 1) >= 2

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
                        .padding(horizontal = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: Back Arrow
                    IconButton(
                        onClick = {
                            isAutoplayOn = false
                            onBack()
                        },
                        modifier = Modifier.testTag("flashcards_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }

                    // Middle: Question Count centered horizontally
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        if (totalCards > 0) {
                            Surface(
                                color = GoldenContainerLight,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("flashcards_count_bubble")
                            ) {
                                Text(
                                    text = "${state.currentCardIndex + 1} / $totalCards",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = GoldenOnContainer,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    // Right: Settings Menu containing Level Selection & Card Options
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
        }
    ) { innerPadding ->
        val studyProgress = if (totalCards > 0) {
            ((state.currentCardIndex + 1).toFloat() / totalCards.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Top Progress Bar (directly under top taskbar, matching drills tab)
            LinearProgressIndicator(
                progress = { studyProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .testTag("flashcards_progress_bar"),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            )

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
                    .padding(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                // 1. Underneath Next Card in Stack (scales & rises smoothly as top card is dragged)
                if (nextPhrase != null) {
                    val backScale = 0.94f + (0.06f * absSwipeProgress)
                    val backTranslationY = with(density) { (12.dp * (1f - absSwipeProgress)).toPx() }
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

                // 2. Active Foreground Card with Quizlet Physics, Slight Lean, and Uninverted Gesture Space
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

                    // Critical: pointerInput is attached to this Box in untransformed screen space
                    // BEFORE applying graphicsLayer 3D flip. This ensures drag coordinates and
                    // left/right directions are NEVER inverted when the card is flipped 180 degrees.
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(currentPhrase.id, state.currentCardIndex) {
                                val velocityTracker = VelocityTracker()
                                detectDragGestures(
                                    onDragStart = {
                                        if (isAutoplayOn) isAutoplayOn = false
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
                                                            y = currentY + 30f
                                                        ),
                                                        animationSpec = tween(
                                                            durationMillis = 200,
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
                                                            y = currentY + 30f
                                                        ),
                                                        animationSpec = tween(
                                                            durationMillis = 200,
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
                                    if (isAutoplayOn) isAutoplayOn = false
                                    onFlipCard()
                                }
                            }
                            .graphicsLayer {
                                translationX = cardOffset.value.x
                                translationY = cardOffset.value.y * 0.25f
                                // Slight Quizlet lean: subtle tilt up to 5 degrees max
                                rotationZ = (cardOffset.value.x / 95f).coerceIn(-5f, 5f)
                                rotationY = rotation
                                cameraDistance = 14f * this.density
                            }
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxSize()
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

            // Under the card: Undo Arrow on left, "Tap to reveal..." or "Autoplay on/off" in middle, Play/Pause on right
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Undo Arrow
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

                // Middle: Tap to reveal "Malayalam" or "English", or temporarily "Autoplay on" / "Autoplay off"
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

                // Right: Autoplay Play / Pause button
                IconButton(
                    onClick = {
                        isAutoplayOn = !isAutoplayOn
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

/**
 * Level Selection Dialog displayed from the Flashcards Settings menu.
 * Defaults selection to and clearly highlights the user's current level.
 */
@Composable
fun LevelSelectionDialog(
    currentDeckId: String,
    userStats: UserStats,
    onSelectDeck: (FlashcardDeck) -> Unit,
    onDismiss: () -> Unit
) {
    val knownCount = userStats.learnedPhraseIds.size
    val currentStage = DialectRepository.getCurrentStage(userStats)
    val currentLevelDeck = DialectRepository.decks.find { it.id == currentStage.deckId } ?: DialectRepository.decks.first()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.82f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f), RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Select Study Level",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Current Level: ${currentStage.rankTitle}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Shortcut to Jump to Current Level if not already selected
                if (currentDeckId != currentLevelDeck.id) {
                    Surface(
                        onClick = {
                            onSelectDeck(currentLevelDeck)
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("select_current_level_shortcut")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("⭐", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Jump to Current Level",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "${currentStage.rankTitle} (${currentStage.topicTitle})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Overall Deck Option
                    item {
                        val isOverallSelected = currentDeckId == "deck_overall"
                        Surface(
                            onClick = {
                                onSelectDeck(DialectRepository.buildOverallDeck(knownCount))
                            },
                            shape = RoundedCornerShape(14.dp),
                            color = if (isOverallSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            border = if (isOverallSelected) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("level_dialog_deck_overall")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Text("📚", fontSize = 22.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Overall (All Unlocked)",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Cards across all unlocked levels",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                if (isOverallSelected) {
                                    Icon(Icons.Filled.Check, contentDescription = "Selected", tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }

                    // All 12 Study Plan Stages
                    items(DialectRepository.studyPlanStages) { stage ->
                        val stageDeck = DialectRepository.decks.find { it.id == stage.deckId } ?: DialectRepository.decks.first()
                        val isReached = stage.levelIndex == 0 ||
                            knownCount >= stage.requiredWordsToUnlock ||
                            userStats.masteredDeckIds.contains(stage.deckId) ||
                            stage.levelIndex <= userStats.unlockedLevelIndex
                        val isSelected = currentDeckId == stageDeck.id
                        val isCurrentLevel = stage.levelIndex == currentStage.levelIndex

                        Surface(
                            onClick = {
                                if (isReached) {
                                    onSelectDeck(stageDeck)
                                }
                            },
                            shape = RoundedCornerShape(14.dp),
                            color = when {
                                isSelected -> MaterialTheme.colorScheme.primaryContainer
                                isReached -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                            },
                            border = if (isSelected) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("level_dialog_${stage.deckId}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Text(stage.iconEmoji, fontSize = 22.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = stage.rankTitle,
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    fontWeight = if (isSelected || isCurrentLevel) FontWeight.ExtraBold else FontWeight.Bold
                                                ),
                                                color = when {
                                                    !isReached -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                                    isSelected -> MaterialTheme.colorScheme.primary
                                                    else -> MaterialTheme.colorScheme.onSurface
                                                }
                                            )
                                            if (isCurrentLevel) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = GoldenSpice.copy(alpha = 0.3f)
                                                ) {
                                                    Text(
                                                        text = "Current",
                                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                                        color = GoldenOnContainer,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }
                                        Text(
                                            text = stage.topicTitle,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (isReached) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                        )
                                    }
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = "Selected",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                } else if (!isReached) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Filled.Lock,
                                            contentDescription = "Locked",
                                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "${stage.requiredWordsToUnlock}w",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Close", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

