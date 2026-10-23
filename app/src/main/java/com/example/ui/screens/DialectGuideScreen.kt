package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DialectRepository
import com.example.model.*
import com.example.ui.components.ThreePartPhraseCard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialectGuideScreen(
    userStats: UserStats = UserStats(),
    onBack: () -> Unit,
    onSpeakText: (String, String) -> Unit,
    onSpeakPhrase: (PhraseItem) -> Unit = { onSpeakText(it.malayalam, it.manglish) },
    onToggleLearned: (String) -> Unit = {},
    onOpenDeckInMode: (FlashcardDeck, QuizletStudyMode) -> Unit = { _, _ -> },
    onLaunchAdaptiveFocus: () -> Unit = {},
    onStartGraduationExam: (ActflProficiencyTier) -> Unit = {}
) {
    BackHandler { onBack() }

    // 0 = Study Plan & Level Tracker, 1 = Known Words & Phrases DB, 2 = Dialect Cheat Sheet
    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    // 0 = All, 1 = Known/Mastered, 2 = Still Learning, 3 = Struggling
    var wordFilter by remember { mutableIntStateOf(0) }

    val knownCount = userStats.learnedPhraseIds.size
    val currentStage = DialectRepository.getCurrentStageForKnownCount(knownCount)
    val nextStage = DialectRepository.getNextStageForKnownCount(knownCount)

    val strugglingPhrases = remember(userStats.masteryRecords) {
        DialectRepository.allPhrases.filter { p ->
            val rec = userStats.masteryRecords[p.id]
            rec != null && rec.isStruggling
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Study Plan & Word Database",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Current Rank: ${currentStage.rankTitle} • $knownCount/${DialectRepository.allPhrases.size} Known",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
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
            // Sub-Navigation Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    0 to "🗺️ Study Plan",
                    1 to "📚 Known Words DB",
                    2 to "📖 Dialect Guide"
                ).forEach { (idx, label) ->
                    val isSelected = selectedTab == idx
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedTab = idx },
                        label = {
                            Text(
                                text = label,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("plan_tab_$idx")
                    )
                }
            }

            when (selectedTab) {
                0 -> {
                    // TAB 0: GROUND LEVEL 0 -> INTERMEDIATE 2 STUDY PLAN & DYNAMIC ADAPTIVE TRACKER
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Current Level & Future Unlock Tracker Banner
                        item {
                            Card(
                                shape = RoundedCornerShape(22.dp),
                                colors = CardDefaults.cardColors(containerColor = PalmContainerLight),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(48.dp)
                                                    .clip(CircleShape)
                                                    .background(Color.White),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(currentStage.iconEmoji, fontSize = 26.sp)
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column {
                                                Text(
                                                    text = "YOUR CURRENT LEVEL",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = EmeraldDark
                                                )
                                                Text(
                                                    text = currentStage.rankTitle,
                                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                                                    color = EmeraldDark
                                                )
                                            }
                                        }

                                        Surface(
                                            color = GoldenSpice,
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text(
                                                text = "$knownCount / ${DialectRepository.allPhrases.size} Words",
                                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                                color = GoldenOnContainer,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    if (nextStage != null) {
                                        val needed = (nextStage.requiredWordsToUnlock - knownCount).coerceAtLeast(1)
                                        val prog = (knownCount.toFloat() / nextStage.requiredWordsToUnlock.toFloat()).coerceIn(0f, 1f)
                                        Text(
                                            text = "🔓 Next Unlock: ${nextStage.rankTitle} (${nextStage.topicTitle}) — Master $needed more word(s) to unlock!",
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                            color = EmeraldDark
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        LinearProgressIndicator(
                                            progress = { prog },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(8.dp)
                                                .clip(RoundedCornerShape(4.dp)),
                                            color = EmeraldPrimary,
                                            trackColor = Color.White.copy(alpha = 0.6f)
                                        )
                                    } else {
                                        Text(
                                            text = "🏆 All 8 Study Plan Stages Unlocked! Keep practicing to maintain 100% mastery.",
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                            color = EmeraldDark
                                        )
                                    }
                                }
                            }
                        }

                        // Dynamic Adaptive Struggle Adjustment Card
                        if (strugglingPhrases.isNotEmpty()) {
                            item {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = TerracottaContainer.copy(alpha = 0.6f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, TerracottaAccent.copy(alpha = 0.35f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = "🎯 ${strugglingPhrases.size} Word(s) Need Focus",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = TerracottaAccent
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Recent weak words: ${strugglingPhrases.take(3).joinToString(", ") { it.manglish }}.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Button(
                                            onClick = onLaunchAdaptiveFocus,
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = TerracottaAccent),
                                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = "Practice Weak Words (${strugglingPhrases.size})",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Complete ACTFL Progression Plan
                        item {
                            Column {
                                Text(
                                    text = "ACTFL Language Proficiency Scale (Spoken Malayalam)",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Ground Zero → Novice (1..3) → Intermediate (1..3 • Catholic & Church) → Advanced (1..2) → Superior (1..2) → Distinguished. Pass oral graduation exams to unlock each tier!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        items(DialectRepository.studyPlanStages) { stage ->
                            val stageDeck = DialectRepository.decks.find { it.id == stage.deckId } ?: DialectRepository.decks[0]
                            val stageKnownCount = stageDeck.phrases.count { userStats.learnedPhraseIds.contains(it.id) }
                            val stageTotal = stageDeck.phrases.size
                            val isUnlocked = knownCount >= stage.requiredWordsToUnlock
                            val isCurrent = stage.levelIndex == currentStage.levelIndex
                            val tier = ActflProficiencyTier.fromTierId(stage.tierId)
                            val isTierGraduated = userStats.graduatedTiers.contains(tier.tierId)

                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isCurrent) {
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                                    } else {
                                        MaterialTheme.colorScheme.surface
                                    }
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(
                                        width = if (isCurrent) 2.dp else 1.dp,
                                        color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f),
                                        shape = RoundedCornerShape(20.dp)
                                    )
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                            Text(stage.iconEmoji, fontSize = 26.sp)
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Surface(
                                                    color = MaterialTheme.colorScheme.primaryContainer,
                                                    shape = RoundedCornerShape(6.dp)
                                                ) {
                                                    Text(
                                                        text = stage.tierName.uppercase(),
                                                        style = MaterialTheme.typography.labelSmall.copy(
                                                            fontWeight = FontWeight.ExtraBold,
                                                            fontSize = 10.sp
                                                        ),
                                                        color = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = stage.rankTitle,
                                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                                Text(
                                                    text = stage.topicTitle,
                                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }

                                        Surface(
                                            color = when {
                                                stageKnownCount == stageTotal && stageTotal > 0 -> PalmContainerLight
                                                isUnlocked -> GoldenContainerLight
                                                else -> MaterialTheme.colorScheme.surfaceVariant
                                            },
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text(
                                                text = when {
                                                    stageKnownCount == stageTotal && stageTotal > 0 -> "🟢 Mastered ($stageKnownCount/$stageTotal)"
                                                    isUnlocked -> "🔓 Unlocked ($stageKnownCount/$stageTotal)"
                                                    else -> "🔒 Unlocks at ${stage.requiredWordsToUnlock} words"
                                                },
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = when {
                                                    stageKnownCount == stageTotal && stageTotal > 0 -> EmeraldDark
                                                    isUnlocked -> GoldenOnContainer
                                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                                },
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = stage.description,
                                        style = MaterialTheme.typography.bodySmall.copy(lineHeight = 17.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Preview sample words in this stage
                                    Text(
                                        text = "Words in this level: " + stageDeck.phrases.take(4).joinToString(" • ") { "${it.manglish} (${it.english})" },
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Quick Mode Launchers (only for unlocked levels)
                                    if (isUnlocked) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            OutlinedButton(
                                                onClick = { onOpenDeckInMode(stageDeck, QuizletStudyMode.FLASHCARDS) },
                                                shape = RoundedCornerShape(10.dp),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Cards", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold))
                                            }

                                            Button(
                                                onClick = { onOpenDeckInMode(stageDeck, QuizletStudyMode.MIXED_MASTERY) },
                                                shape = RoundedCornerShape(10.dp),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 5.dp),
                                                modifier = Modifier.weight(1.2f)
                                            ) {
                                                Text("Practice", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                                            }
                                        }
                                    }

                                    // Tier Spoken Graduation Checkpoint
                                    if (stage.isTierFinalStage) {
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Surface(
                                            shape = RoundedCornerShape(14.dp),
                                            color = if (isTierGraduated) PalmContainerLight else GoldenContainerLight,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .border(
                                                    1.dp,
                                                    if (isTierGraduated) EmeraldPrimary else GoldenSpice,
                                                    RoundedCornerShape(14.dp)
                                                )
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(12.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = if (isTierGraduated) "✅ Graduated ${tier.title}" else "🎓 Oral Graduation Checkpoint",
                                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                                                        color = if (isTierGraduated) EmeraldDark else GoldenOnContainer
                                                    )
                                                    Text(
                                                        text = if (isTierGraduated) {
                                                            "Oral defense passed (+50 XP). All next tier stages unlocked!"
                                                        } else {
                                                            "Speak Malayalam out loud to graduate from ${tier.title} to the next ACTFL tier!"
                                                        },
                                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 16.sp),
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Button(
                                                    onClick = { onStartGraduationExam(tier) },
                                                    shape = RoundedCornerShape(10.dp),
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = if (isTierGraduated) EmeraldPrimary else MaterialTheme.colorScheme.primary
                                                    ),
                                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                                ) {
                                                    Text(
                                                        text = if (isTierGraduated) "Retake 🎙️" else "Take Oral Exam 🎙️",
                                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
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

                1 -> {
                    // TAB 1: KNOWN WORDS & PHRASES DATABASE
                    val allPhrases = DialectRepository.allPhrases
                    val masteredList = allPhrases.filter { userStats.learnedPhraseIds.contains(it.id) }
                    val learningList = allPhrases.filter {
                        val rec = userStats.masteryRecords[it.id]
                        rec != null && rec.isStillLearning && !userStats.learnedPhraseIds.contains(it.id)
                    }
                    val strugglingList = allPhrases.filter {
                        val rec = userStats.masteryRecords[it.id]
                        rec != null && rec.isStruggling
                    }

                    val filteredPhrases = allPhrases.filter { p ->
                        val matchesFilter = when (wordFilter) {
                            1 -> userStats.learnedPhraseIds.contains(p.id)
                            2 -> userStats.masteryRecords[p.id]?.isStillLearning == true
                            3 -> userStats.masteryRecords[p.id]?.isStruggling == true
                            else -> true
                        }
                        val q = searchQuery.trim().lowercase()
                        val matchesQuery = q.isBlank() ||
                            p.manglish.lowercase().contains(q) ||
                            p.english.lowercase().contains(q) ||
                            p.malayalam.contains(q) ||
                            p.levelBadge.lowercase().contains(q)
                        matchesFilter && matchesQuery
                    }

                    Column(modifier = Modifier.fillMaxSize()) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search English, Manglish, or Level...") },
                            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                                .testTag("word_bank_search_input")
                        )

                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val chips = listOf(
                                0 to "All (${allPhrases.size})",
                                1 to "🟢 Known (${masteredList.size})",
                                2 to "🟡 Learning (${learningList.size})",
                                3 to "🔴 Struggling (${strugglingList.size})"
                            )
                            items(chips) { (idx, title) ->
                                FilterChip(
                                    selected = wordFilter == idx,
                                    onClick = { wordFilter = idx },
                                    label = { Text(title, fontWeight = FontWeight.Bold) }
                                )
                            }
                        }

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (filteredPhrases.isEmpty()) {
                                item {
                                    Surface(
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(16.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "No words in this filter yet! Study a deck in Mixed Quiz, Learn, or Flashcards mode to build your Known Words Database.",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(20.dp)
                                        )
                                    }
                                }
                            } else {
                                items(filteredPhrases, key = { it.id }) { phrase ->
                                    val rec = userStats.masteryRecords[phrase.id]
                                    val isKnown = userStats.learnedPhraseIds.contains(phrase.id)

                                    Column {
                                        // Mastery Record Header Pill
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 4.dp, vertical = 2.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = when {
                                                    isKnown -> "🟢 KNOWN / MASTERED (Streak: ${rec?.consecutiveCorrect ?: 2}/2)"
                                                    rec?.isStruggling == true -> "🔴 NEEDS PRACTICE (Missed ${rec.mistakeCount}x)"
                                                    rec?.isStillLearning == true -> "🟡 STILL LEARNING (Streak: 1/2)"
                                                    else -> "⚪ NOT STUDIED YET"
                                                },
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                                                color = when {
                                                    isKnown -> EmeraldDark
                                                    rec?.isStruggling == true -> TerracottaAccent
                                                    rec?.isStillLearning == true -> GoldenOnContainer
                                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                                }
                                            )
                                            Text(
                                                text = "Tap ✓ to toggle Known",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        ThreePartPhraseCard(
                                            phrase = phrase,
                                            onSpeakClick = { onSpeakPhrase(phrase) },
                                            isLearned = isKnown,
                                            onToggleLearned = { onToggleLearned(phrase.id) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                else -> {
                    // TAB 2: CENTRAL TRAVANCORE DIALECT CHEAT SHEET
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(DialectRepository.dialectComparisons) { item ->
                            Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${item.category.uppercase()} • \"${item.meaning}\"",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        IconButton(onClick = { onSpeakText(item.adoorSpoken, item.manglishSpoken) }) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                                contentDescription = "Listen",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }

                                    Text(
                                        text = "✅ Adoor Spoken: ${item.adoorSpoken}",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                        color = EmeraldDark
                                    )
                                    Text(
                                        text = "❌ Formal Textbook: ${item.textbookFormal}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TerracottaAccent
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = item.explanation,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
