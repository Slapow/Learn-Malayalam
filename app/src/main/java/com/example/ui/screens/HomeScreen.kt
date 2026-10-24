package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DialectRepository
import com.example.model.FlashcardDeck
import com.example.model.QuizletStudyMode
import com.example.model.StudyLevelStage
import com.example.model.UserStats
import com.example.ui.components.StreakCalendarDialog
import com.example.ui.components.TopHeaderBar
import com.example.ui.components.XpLevelProgressDialog
import com.example.ui.theme.*
import com.example.viewmodel.Screen

enum class HomeTabFilter {
    ALL, FLASHCARDS, DRILLS
}

@Composable
fun HomeScreen(
    userStats: UserStats,
    onNavigate: (Screen) -> Unit,
    onSpeakPhrase: (com.example.model.PhraseItem) -> Unit,
    onToggleLearned: (String) -> Unit,
    onOpenDeckInMode: (FlashcardDeck, QuizletStudyMode) -> Unit = { _, _ -> },
    onStartDrill: (Int) -> Unit = {},
    onLaunchAdaptiveFocus: () -> Unit = {},
    onOpenCloudSync: () -> Unit = {},
    lastSyncedDevice: String = "Android",
    lastActiveScreenLabel: String = "HOME",
    onResumeCloudSession: () -> Unit = {}
) {
    val currentStage = DialectRepository.getCurrentStage(userStats)
    val nextStage = DialectRepository.getNextStage(userStats)

    var showStreakDialog by remember { mutableStateOf(false) }
    var showXpDialog by remember { mutableStateOf(false) }
    var activeFilter by remember { mutableStateOf(HomeTabFilter.ALL) }

    Scaffold(
        topBar = {
            TopHeaderBar(
                xp = userStats.xp,
                streakDays = userStats.streakDays,
                onStreakClick = { showStreakDialog = true },
                onXpClick = { showXpDialog = true }
            )
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
            // Simplified Filter Row (All, Flashcards, Drills)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = activeFilter == HomeTabFilter.ALL,
                        onClick = { activeFilter = HomeTabFilter.ALL },
                        label = { Text("All Activities", fontWeight = FontWeight.SemiBold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("filter_all")
                    )

                    FilterChip(
                        selected = activeFilter == HomeTabFilter.FLASHCARDS,
                        onClick = { activeFilter = HomeTabFilter.FLASHCARDS },
                        label = { Text("🗂️ Flashcards", fontWeight = FontWeight.SemiBold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("filter_flashcards")
                    )

                    FilterChip(
                        selected = activeFilter == HomeTabFilter.DRILLS,
                        onClick = { activeFilter = HomeTabFilter.DRILLS },
                        label = { Text("🎯 Drills", fontWeight = FontWeight.SemiBold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("filter_drills")
                    )
                }
            }

            // =========================================================================
            // FLASHCARDS SECTION
            // =========================================================================
            if (activeFilter == HomeTabFilter.ALL || activeFilter == HomeTabFilter.FLASHCARDS) {
                item {
                    Text(
                        text = "🗂️ Flashcards",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                items(DialectRepository.decks, key = { "deck_${it.id}" }) { deck ->
                    val totalCards = deck.phrases.size
                    val masteredCards = deck.phrases.count { p ->
                        userStats.masteryRecords[p.id]?.isKnown == true || userStats.learnedPhraseIds.contains(p.id)
                    }
                    val progressRatio = if (totalCards > 0) masteredCards.toFloat() / totalCards.toFloat() else 0f
                    val isDeckDone = totalCards > 0 && masteredCards >= totalCards

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .border(
                                width = 1.dp,
                                color = if (isDeckDone) EmeraldDark.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                                shape = RoundedCornerShape(18.dp)
                            )
                            .testTag("home_deck_card_${deck.id}"),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(deck.iconEmoji, fontSize = 24.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = deck.title,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = deck.levelLabel,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                if (isDeckDone) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = PalmContainerLight
                                    ) {
                                        Text(
                                            text = "Mastered ✓",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                                            color = EmeraldDark,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Progress Bar + Count
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "$masteredCards / $totalCards cards mastered",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${(progressRatio * 100).toInt()}%",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isDeckDone) EmeraldDark else MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            LinearProgressIndicator(
                                progress = { progressRatio },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = if (isDeckDone) EmeraldDark else MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Continue Button
                            Button(
                                onClick = {
                                    onOpenDeckInMode(deck, QuizletStudyMode.FLASHCARDS)
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isDeckDone) PalmContainerLight else MaterialTheme.colorScheme.primary,
                                    contentColor = if (isDeckDone) EmeraldDark else Color.White
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("continue_deck_${deck.id}")
                            ) {
                                Text(
                                    text = if (isDeckDone) "Review Flashcards" else "Continue Flashcards",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // =========================================================================
            // DRILLS SECTION
            // =========================================================================
            if (activeFilter == HomeTabFilter.ALL || activeFilter == HomeTabFilter.DRILLS) {
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "🎯 Drills",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                items(DialectRepository.studyPlanStages, key = { "drill_${it.levelIndex}" }) { stage ->
                    val isDrillMastered = userStats.masteredDrillLevelIndices.contains(stage.levelIndex)
                    val drillProgressRatio = if (isDrillMastered) 1f else 0f

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .border(
                                width = 1.dp,
                                color = if (isDrillMastered) EmeraldDark.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                                shape = RoundedCornerShape(18.dp)
                            )
                            .testTag("home_drill_card_${stage.levelIndex}"),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(stage.iconEmoji, fontSize = 24.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "${stage.rankTitle} Drill (3 Rounds)",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "MCQ • Tiles • Speaking • Writing",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                if (isDrillMastered) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = PalmContainerLight
                                    ) {
                                        Text(
                                            text = "Mastered ✓",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                                            color = EmeraldDark,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Drill Progress Bar + Count
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isDrillMastered) "All 3 Rounds Mastered (12/12)" else "Progress: 0 / 3 Rounds",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = if (isDrillMastered) "100%" else "0%",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isDrillMastered) EmeraldDark else TerracottaAccent
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            LinearProgressIndicator(
                                progress = { drillProgressRatio },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = if (isDrillMastered) EmeraldDark else GoldenSpice,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Continue Drill Button
                            Button(
                                onClick = {
                                    onStartDrill(stage.levelIndex)
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isDrillMastered) PalmContainerLight else GoldenSpice,
                                    contentColor = if (isDrillMastered) EmeraldDark else GoldenOnContainer
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("continue_drill_${stage.levelIndex}")
                            ) {
                                Text(
                                    text = if (isDrillMastered) "Practice Drill Again" else "Continue Drill",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Streak Calendar Modal Dialog
    if (showStreakDialog) {
        StreakCalendarDialog(
            streakDays = userStats.streakDays,
            onDismiss = { showStreakDialog = false }
        )
    }

    // XP & Current Level Progress Dialog
    if (showXpDialog) {
        XpLevelProgressDialog(
            userStats = userStats,
            currentStage = currentStage,
            nextStage = nextStage,
            onDismiss = { showXpDialog = false }
        )
    }
}
