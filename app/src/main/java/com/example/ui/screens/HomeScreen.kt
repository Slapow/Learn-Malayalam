package com.example.ui.screens

import androidx.compose.foundation.BorderStroke

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.DialectRepository
import com.example.model.FlashcardDeck
import com.example.model.PhraseItem
import com.example.model.QuizletStudyMode
import com.example.model.QuizType
import com.example.model.StudyLevelStage
import com.example.model.UserStats
import com.example.ui.components.StreakCalendarDialog
import com.example.ui.components.TopHeaderBar
import com.example.ui.components.LevelProgressDialog
import com.example.ui.theme.*
import com.example.viewmodel.Screen
import com.example.viewmodel.TranslatorState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userStats: UserStats,
    selectedLevelIndex: Int,
    onSelectLevel: (Int) -> Unit,
    onNavigate: (Screen) -> Unit,
    onSpeakPhrase: (PhraseItem) -> Unit,
    onToggleLearned: (String) -> Unit,
    onOpenDeckInMode: (FlashcardDeck, QuizletStudyMode) -> Unit = { _, _ -> },
    onStartDrill: (Int) -> Unit = {},
    onStartProductionPractice: (Int, QuizType) -> Unit = { _, _ -> },
    onStartLearningDeck: (FlashcardDeck, Int) -> Unit = { _, _ -> },
    onLaunchAdaptiveFocus: () -> Unit = {},
    onOpenCloudSync: () -> Unit = {},
    lastSyncedDevice: String = "Android",
    lastActiveScreenLabel: String = "HOME",
    onResumeCloudSession: () -> Unit = {},
    isDevModeEnabled: Boolean = false,
    onEndDevMode: () -> Unit = {},
    translatorState: TranslatorState = TranslatorState(),
    onTranslatorInputChange: (String) -> Unit = {},
    onTranslate: () -> Unit = {},
    onDismissTranslator: () -> Unit = {}
) {
    val useDarkTileColors = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    val currentStage = DialectRepository.getCurrentStage(userStats)
    val nextStage = DialectRepository.getNextStage(userStats)

    var showStreakDialog by remember { mutableStateOf(false) }
    var showLevelProgressDialog by remember { mutableStateOf(false) }
    var showTranslatorDialog by remember { mutableStateOf(false) }

    var levelDropdownExpanded by remember { mutableStateOf(false) }
    val levelDropdownMaxHeight = (LocalConfiguration.current.screenHeightDp * 0.5f).dp
        .coerceAtMost(360.dp)

    val unlockedStages = remember(userStats.unlockedLevelIndex) {
        DialectRepository.studyPlanStages.filter { it.levelIndex <= userStats.unlockedLevelIndex }
    }

    val currentDeck = DialectRepository.decks.find { it.id == currentStage.deckId }

    val selectedDeck = DialectRepository.getPracticeDeck(selectedLevelIndex, userStats.unlockedLevelIndex)
    val roundProgress = DialectRepository.getDrillRoundProgress(
        listOf(currentStage.levelIndex),
        userStats
    )
    val deckAlreadyLearned = userStats.masteredDeckIds.contains(currentStage.deckId) ||
        (currentDeck?.phrases?.isNotEmpty() == true && currentDeck.phrases.all { phrase ->
            userStats.learnedPhraseIds.contains(phrase.id) ||
                userStats.masteryRecords[phrase.id]?.isKnown == true
        })

    Scaffold(
        topBar = {
            val tier = com.example.model.ActflProficiencyTier.fromTierId(currentStage.tierId)
            val tierStages = DialectRepository.studyPlanStages.filter { it.tierId == currentStage.tierId }
            val levelInTier = (tierStages.indexOfFirst { it.levelIndex == currentStage.levelIndex }.takeIf { it >= 0 } ?: 0) + 1

            TopHeaderBar(
                streakDays = userStats.streakDays,
                stageIcon = tier.iconEmoji,
                stageText = "$levelInTier",
                onStreakClick = { showStreakDialog = true },
                onStageClick = { showLevelProgressDialog = true }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigate(Screen.SETTINGS) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
                modifier = Modifier.padding(bottom = 8.dp, end = 8.dp).testTag("fab_settings")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings"
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isDevModeEnabled) {
                item {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("home_dev_banner")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Text("🛠️", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Developer Mode Active",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                    Text(
                                        text = "Testing controls active in all learning & drill modes",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.85f)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = onEndDevMode,
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error,
                                    contentColor = MaterialTheme.colorScheme.onError
                                ),
                                modifier = Modifier.height(34.dp).testTag("home_btn_end_dev")
                            ) {
                                Text("End Dev", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // 1. Current Level Progress Banner
            item {
                val tier = com.example.model.ActflProficiencyTier.fromTierId(currentStage.tierId)
                val sectionSubtitle = if (currentStage.levelIndex == 0) {
                    "Everyday words and questions"
                } else {
                    currentStage.topicTitle
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .border(
                            width = 1.5.dp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(20.dp)
                        ),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(tier.iconEmoji, fontSize = 28.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${currentStage.rankTitle} / ${DialectRepository.studyPlanStages.count { it.tierId == currentStage.tierId }}",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = sectionSubtitle,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Text(
                                    text = "${currentDeck?.phrases?.distinctBy { it.id }?.size ?: 0} words",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        LinearProgressIndicator(
                            progress = { roundProgress.ratio },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp)),
                            color = GoldenSpice,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "${roundProgress.completedRounds} / ${roundProgress.totalRounds} rounds completed",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        val buttonText = if (roundProgress.completedRounds > 0) "Continue Learning" else "Start Learning"

                        Button(
                            onClick = {
                                if (deckAlreadyLearned) {
                                    onStartDrill(currentStage.levelIndex)
                                } else {
                                    currentDeck?.let { onStartLearningDeck(it, currentStage.levelIndex) }
                                }
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("start_continue_learning_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = buttonText,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }
            }

            // 2. Action Tiles Section with Single Level Dropdown Menu
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Activities & Practice",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    // Single Dropdown Menu across from "Activities & Practice"
                    Box {
                        Surface(
                            onClick = { levelDropdownExpanded = true },
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
                            tonalElevation = 1.dp,
                            modifier = Modifier.testTag("activities_level_dropdown_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                val dropdownDisplayText = if (selectedLevelIndex == -1) {
                                    "📚 Overall"
                                } else {
                                    val stage = DialectRepository.studyPlanStages.getOrNull(selectedLevelIndex)
                                    if (stage != null) {
                                        val tier = com.example.model.ActflProficiencyTier.fromTierId(stage.tierId)
                                        "${tier.iconEmoji} ${stage.rankTitle}"
                                    } else "Level ${selectedLevelIndex + 1}"
                                }
                                Text(
                                    text = dropdownDisplayText,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    maxLines = 1
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Select Practice Level",
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = levelDropdownExpanded,
                            onDismissRequest = { levelDropdownExpanded = false },
                            modifier = Modifier
                                .heightIn(max = levelDropdownMaxHeight)
                                .testTag("activities_level_dropdown_menu")
                        ) {
                            // Top Option: Overall (any word up till current level, not locked levels)
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            text = "📚 Overall",
                                            fontWeight = if (selectedLevelIndex == -1) FontWeight.ExtraBold else FontWeight.SemiBold,
                                            color = if (selectedLevelIndex == -1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Any word up till current level (${currentStage.rankTitle})",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                },
                                onClick = {
                                    onSelectLevel(-1)
                                    levelDropdownExpanded = false
                                },
                                modifier = Modifier.testTag("dropdown_option_overall")
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                            // Subsequent Options: Unlocked levels up to current level
                            unlockedStages.forEach { stage ->
                                val isSelected = selectedLevelIndex == stage.levelIndex
                                val tier = com.example.model.ActflProficiencyTier.fromTierId(stage.tierId)
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(tier.iconEmoji, fontSize = 16.sp)
                                            Column {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(
                                                        text = stage.rankTitle,
                                                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Normal,
                                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                                    )
                                                    if (stage.levelIndex == currentStage.levelIndex) {
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Surface(
                                                            color = MaterialTheme.colorScheme.secondaryContainer,
                                                            shape = RoundedCornerShape(4.dp)
                                                        ) {
                                                            Text(
                                                                text = "CURRENT",
                                                                style = MaterialTheme.typography.labelSmall.copy(
                                                                    fontSize = 8.sp,
                                                                    fontWeight = FontWeight.ExtraBold
                                                                ),
                                                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                            )
                                                        }
                                                    }
                                                }
                                                Text(
                                                    text = stage.topicTitle,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    },
                                    onClick = {
                                        onSelectLevel(stage.levelIndex)
                                        levelDropdownExpanded = false
                                    },
                                    modifier = Modifier.testTag("dropdown_option_level_${stage.levelIndex}")
                                )
                            }
                        }
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Row 1: Decks & Drills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        HomeTileCard(
                            title = "Decks",
                            icon = "🗂️",
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                onOpenDeckInMode(selectedDeck, QuizletStudyMode.FLASHCARDS)
                            }
                        )

                        HomeTileCard(
                            title = "Drills",
                            icon = "🎯",
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                onStartDrill(selectedLevelIndex)
                            }
                        )
                    }

                    // Row 2: Speaking & Typing
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        HomeTileCard(
                            title = "Speaking",
                            icon = "🎙️",
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                onStartProductionPractice(selectedLevelIndex, QuizType.SPEAKING_PRACTICE)
                            }
                        )

                        HomeTileCard(
                            title = "Typing",
                            icon = "✍️",
                            containerColor = if (useDarkTileColors) Color(0xFF173F67) else Color(0xFFD7E9FF),
                            contentColor = if (useDarkTileColors) Color(0xFFD7E9FF) else Color(0xFF12365B),
                            modifier = Modifier.weight(1f),
                            onClick = {
                                onStartProductionPractice(selectedLevelIndex, QuizType.MANGLISH_TYPING)
                            }
                        )
                    }

                    // Row 3: Match Game & Dialect Translator
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        HomeTileCard(
                            title = "Match Game",
                            icon = "🧩",
                            containerColor = if (useDarkTileColors) Color(0xFF493366) else Color(0xFFEEDFFF),
                            contentColor = if (useDarkTileColors) Color(0xFFEEDFFF) else Color(0xFF40265F),
                            modifier = Modifier.weight(1f),
                            onClick = {
                                onOpenDeckInMode(selectedDeck, QuizletStudyMode.MATCH)
                            }
                        )

                        HomeTileCard(
                            title = "Translator",
                            icon = "🔤",
                            containerColor = if (useDarkTileColors) Color(0xFF653047) else Color(0xFFFFDAE8),
                            contentColor = if (useDarkTileColors) Color(0xFFFFDAE8) else Color(0xFF60243E),
                            modifier = Modifier.weight(1f),
                            onClick = { showTranslatorDialog = true }
                        )
                    }
                }
            }
        }
    }

    // Modal Dialogs
    if (showStreakDialog) {
        StreakCalendarDialog(
            streakDays = userStats.streakDays,
            onDismiss = { showStreakDialog = false }
        )
    }

    if (showLevelProgressDialog) {
        LevelProgressDialog(
            userStats = userStats,
            currentStage = currentStage,
            nextStage = nextStage,
            onDismiss = { showLevelProgressDialog = false }
        )
    }

    if (showTranslatorDialog) {
        DialectTranslatorDialog(
            state = translatorState,
            onInputChange = onTranslatorInputChange,
            onTranslate = onTranslate,
            onSpeakPhrase = onSpeakPhrase,
            onDismiss = {
                onDismissTranslator()
                showTranslatorDialog = false
            }
        )
    }
}

@Composable
private fun HomeTileCard(
    title: String,
    icon: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(icon, fontSize = 28.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = contentColor
            )
        }
    }
}

@Composable
fun DecksMenuDialog(
    userStats: UserStats,
    currentStage: StudyLevelStage,
    onSelectDeck: (FlashcardDeck) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.82f)
                .clip(RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🗂️ Select Study Deck",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    // Overall Deck Option
                    item {
                        val overallDeck = DialectRepository.buildOverallDeck(userStats.learnedPhraseIds.size)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onSelectDeck(overallDeck) },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("📚", fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Overall Deck (All Unlocked)",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(
                                        text = "${overallDeck.phrases.size} cards available for review",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                    }

                    // Level Decks
                    items(DialectRepository.studyPlanStages) { stage ->
                        val deck = DialectRepository.decks.find { it.id == stage.deckId } ?: return@items
                        val isUnlocked = stage.levelIndex <= userStats.unlockedLevelIndex
                        val isCurrent = stage.levelIndex == currentStage.levelIndex

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable(enabled = isUnlocked) { onSelectDeck(deck) },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCurrent) {
                                    MaterialTheme.colorScheme.secondaryContainer
                                } else if (isUnlocked) {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                                }
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(if (isUnlocked) stage.iconEmoji else "🔒", fontSize = 22.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = stage.rankTitle,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (isUnlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                            )
                                        )
                                        if (isCurrent) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "CURRENT",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 9.sp
                                                ),
                                                color = MaterialTheme.colorScheme.onSecondaryContainer
                                            )
                                        }
                                    }
                                    Text(
                                        text = stage.topicTitle,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isUnlocked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
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

@Composable
fun DrillsMenuDialog(
    userStats: UserStats,
    currentStage: StudyLevelStage,
    onSelectDrill: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.82f)
                .clip(RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🎯 Select Practice Drill",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(DialectRepository.studyPlanStages) { stage ->
                        val isUnlocked = stage.levelIndex <= userStats.unlockedLevelIndex
                        val isCurrent = stage.levelIndex == currentStage.levelIndex
                        val isMastered = userStats.masteredDrillLevelIndices.contains(stage.levelIndex)

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable(enabled = isUnlocked) { onSelectDrill(stage.levelIndex) },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCurrent) {
                                    MaterialTheme.colorScheme.secondaryContainer
                                } else if (isUnlocked) {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                                }
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(if (isUnlocked) stage.iconEmoji else "🔒", fontSize = 22.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "${stage.rankTitle} Drill",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (isUnlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                            )
                                        )
                                        if (isCurrent) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "CURRENT",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 9.sp
                                                ),
                                                color = MaterialTheme.colorScheme.onSecondaryContainer
                                            )
                                        } else if (isMastered) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "✓ MASTERED",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 9.sp
                                                ),
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                    Text(
                                        text = stage.topicTitle,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isUnlocked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
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
