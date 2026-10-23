package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DialectRepository
import com.example.model.FlashcardDeck
import com.example.model.QuizletStudyMode
import com.example.model.UserStats
import com.example.ui.components.ThreePartPhraseCard
import com.example.ui.components.TopHeaderBar
import com.example.ui.theme.*
import com.example.viewmodel.Screen

@Composable
fun HomeScreen(
    userStats: UserStats,
    onNavigate: (Screen) -> Unit,
    onSpeakPhrase: (com.example.model.PhraseItem) -> Unit,
    onToggleLearned: (String) -> Unit,
    onOpenDeckInMode: (FlashcardDeck, QuizletStudyMode) -> Unit = { _, _ -> },
    onLaunchAdaptiveFocus: () -> Unit = {},
    onOpenCloudSync: () -> Unit = {},
    lastSyncedDevice: String = "Android",
    lastActiveScreenLabel: String = "HOME",
    onResumeCloudSession: () -> Unit = {}
) {
    val knownCount = userStats.learnedPhraseIds.size
    val currentStage = DialectRepository.getCurrentStageForKnownCount(knownCount)
    val nextStage = DialectRepository.getNextStageForKnownCount(knownCount)
    val phraseOfTheDay = DialectRepository.allPhrases.firstOrNull {
        it.levelNumber == currentStage.levelIndex && !userStats.learnedPhraseIds.contains(it.id)
    } ?: DialectRepository.allPhrases.first()

    val strugglingCount = userStats.masteryRecords.values.count { it.isStruggling }
    val level0Deck = DialectRepository.decks.first()

    Scaffold(
        topBar = {
            TopHeaderBar(
                xp = userStats.xp,
                streakDays = userStats.streakDays,
                levelLabel = currentStage.rankTitle,
                isCloudSynced = true,
                onOpenCloudSync = onOpenCloudSync
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
            // Current Level & Study Plan Progression Hero Banner
            item {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_hero_banner")
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        EmeraldDark
                                    )
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = GoldenSpice,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "${currentStage.iconEmoji} ${currentStage.rankTitle.uppercase()}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 11.sp,
                                            letterSpacing = 0.5.sp
                                        ),
                                        color = GoldenOnContainer,
                                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.5.dp)
                                    )
                                }

                                TextButton(
                                    onClick = { onNavigate(Screen.DIALECT_GUIDE) },
                                    colors = ButtonDefaults.textButtonColors(contentColor = Color.White),
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                                ) {
                                    Text(
                                        text = "Plan →",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = currentStage.topicTitle,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    lineHeight = 23.sp
                                ),
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Clean Progress Bar toward next level
                            if (nextStage != null) {
                                val targetWords = nextStage.requiredWordsToUnlock.coerceAtLeast(1)
                                val progress = (knownCount.toFloat() / targetWords.toFloat()).coerceIn(0f, 1f)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Next: ${nextStage.rankTitle}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                        color = Color.White.copy(alpha = 0.85f)
                                    )
                                    Text(
                                        text = "$knownCount/$targetWords known",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = GoldenSpice
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = GoldenSpice,
                                    trackColor = Color.White.copy(alpha = 0.22f)
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                            }

                            Button(
                                onClick = {
                                    onOpenDeckInMode(level0Deck, QuizletStudyMode.MIXED_MASTERY)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GoldenSpice,
                                    contentColor = GoldenOnContainer
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("start_quick_drill_button")
                            ) {
                                Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Continue Practice", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }

            // Dynamic Adaptive Struggle Banner (if user has missed words)
            if (strugglingCount > 0) {
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = TerracottaContainer.copy(alpha = 0.6f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TerracottaAccent.copy(alpha = 0.35f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLaunchAdaptiveFocus() }
                            .testTag("home_adaptive_focus_card")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("🎯", fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "$strugglingCount tricky words need review",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = TerracottaAccent
                                    )
                                    Text(
                                        text = "Tap to practice weak words + root basics",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            FilledTonalButton(
                                onClick = onLaunchAdaptiveFocus,
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = TerracottaAccent,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text("Review", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Starter Word / Phrase of the Day (with English Phonetics + Word Breakdown)
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⭐ Word of the Day",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = phraseOfTheDay.levelBadge,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    ThreePartPhraseCard(
                        phrase = phraseOfTheDay,
                        onSpeakClick = { onSpeakPhrase(phraseOfTheDay) },
                        isLearned = userStats.learnedPhraseIds.contains(phraseOfTheDay.id),
                        onToggleLearned = { onToggleLearned(phraseOfTheDay.id) }
                    )
                }
            }

            // Study Modes Heading
            item {
                Text(
                    text = "Study Modes",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // 2x2 Grid of Study Modes (Uncluttered, compact, visual)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CompactModeCard(
                            title = "Flashcards",
                            tag = "7 Modes",
                            icon = "🗂️",
                            badgeColor = GoldenContainerLight,
                            badgeTextColor = GoldenOnContainer,
                            onClick = { onNavigate(Screen.FLASHCARDS) },
                            testTag = "mode_flashcards_card",
                            modifier = Modifier.weight(1f)
                        )

                        CompactModeCard(
                            title = "Mixed Drill",
                            tag = "Write & Speak",
                            icon = "🎯",
                            badgeColor = TerracottaContainer,
                            badgeTextColor = TerracottaAccent,
                            onClick = { onNavigate(Screen.QUIZ) },
                            testTag = "mode_drill_card",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CompactModeCard(
                            title = "AI Tutor",
                            tag = "English Q&A",
                            icon = "💬",
                            badgeColor = MaterialTheme.colorScheme.secondaryContainer,
                            badgeTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            onClick = { onNavigate(Screen.AI_TUTOR) },
                            testTag = "mode_ai_tutor_card",
                            modifier = Modifier.weight(1f)
                        )

                        CompactModeCard(
                            title = "Study Plan",
                            tag = "${userStats.learnedPhraseIds.size} Known",
                            icon = "🗺️",
                            badgeColor = PalmContainerLight,
                            badgeTextColor = EmeraldDark,
                            onClick = { onNavigate(Screen.DIALECT_GUIDE) },
                            testTag = "mode_guide_card",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Quick Stats Row
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        StatItem(label = "Known Words", value = "${userStats.learnedPhraseIds.size}")
                        StatItem(label = "Total XP", value = "${userStats.xp}")
                        StatItem(label = "Daily Streak", value = "${userStats.streakDays}d")
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun CompactModeCard(
    title: String,
    tag: String,
    icon: String,
    badgeColor: Color,
    badgeTextColor: Color,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag(testTag),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(badgeColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = icon, fontSize = 20.sp)
                }

                Surface(
                    color = badgeColor.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = tag,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = badgeTextColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun LearningModeCard(
    title: String,
    subtitle: String,
    tag: String,
    icon: String,
    badgeColor: Color,
    badgeTextColor: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .testTag(testTag),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(badgeColor),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, fontSize = 26.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    color = badgeColor.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = tag,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = badgeTextColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = "Open",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
