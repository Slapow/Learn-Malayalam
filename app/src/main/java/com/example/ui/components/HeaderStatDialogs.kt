package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.DialectRepository
import com.example.model.StudyLevelStage
import com.example.model.UserStats
import com.example.ui.theme.*
import java.util.Calendar

/**
 * Calendar Dialog showing practiced days for the user's streak
 */
@Composable
fun StreakCalendarDialog(
    streakDays: Int,
    onDismiss: () -> Unit
) {
    val cal = Calendar.getInstance()
    val currentDayOfMonth = cal.get(Calendar.DAY_OF_MONTH)
    val maxDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val monthName = when (cal.get(Calendar.MONTH)) {
        Calendar.JANUARY -> "January"
        Calendar.FEBRUARY -> "February"
        Calendar.MARCH -> "March"
        Calendar.APRIL -> "April"
        Calendar.MAY -> "May"
        Calendar.JUNE -> "June"
        Calendar.JULY -> "July"
        Calendar.AUGUST -> "August"
        Calendar.SEPTEMBER -> "September"
        Calendar.OCTOBER -> "October"
        Calendar.NOVEMBER -> "November"
        Calendar.DECEMBER -> "December"
        else -> "This Month"
    }
    val year = cal.get(Calendar.YEAR)

    // Calculate which days in the current month count as practiced for this streak
    val streakStartDay = (currentDayOfMonth - streakDays + 1).coerceAtLeast(1)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.5.dp, TerracottaAccent.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
                .testTag("streak_calendar_dialog"),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with flame icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = TerracottaContainer.copy(alpha = 0.85f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🔥", fontSize = 20.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "$streakDays Day Streak",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = TerracottaAccent
                            )
                            Text(
                                text = "$monthName $year",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Motivational banner
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = TerracottaContainer.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (streakDays > 0) {
                            "You've practiced $streakDays day${if (streakDays > 1) "s" else ""} in a row! Come back daily to keep your flame glowing."
                        } else {
                            "Start your streak today by completing a flashcard deck or 3-round drill!"
                        },
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 16.sp),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Days of week row
                val dayHeaders = listOf("M", "T", "W", "T", "F", "S", "S")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    dayHeaders.forEach { header ->
                        Text(
                            text = header,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.width(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Calendar days grid
                val daysList = (1..maxDaysInMonth).toList()
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(daysList) { day ->
                        val isPracticed = streakDays > 0 && day in streakStartDay..currentDayOfMonth
                        val isToday = day == currentDayOfMonth
                        val isFuture = day > currentDayOfMonth

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .then(
                                    when {
                                        isPracticed -> Modifier.background(TerracottaAccent)
                                        isToday -> Modifier
                                            .background(MaterialTheme.colorScheme.primaryContainer)
                                            .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                        else -> Modifier.background(Color.Transparent)
                                    }
                                )
                        ) {
                            if (isPracticed) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "$day",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 11.sp
                                        ),
                                        color = Color.White
                                    )
                                }
                            } else {
                                Text(
                                    text = "$day",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 11.sp
                                    ),
                                    color = when {
                                        isToday -> MaterialTheme.colorScheme.primary
                                        isFuture -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
                                        else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TerracottaAccent),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Text("Got It", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Level & XP Progress Dialog showing current level progress bar,
 * current XP, and goal XP to reach the next level.
 */
@Composable
fun XpLevelProgressDialog(
    userStats: UserStats,
    currentStage: StudyLevelStage,
    nextStage: StudyLevelStage?,
    onDismiss: () -> Unit
) {
    val levelIndex = currentStage.levelIndex
    val tier = com.example.model.ActflProficiencyTier.fromTierId(currentStage.tierId)

    // Stage progression aligned with 5-tier framework targets (1000, 3500, 8000, 16000, 30000)
    val stageGoals = listOf(
        100,    // Stage 0: Novice 1
        500,    // Stage 1: Novice 2
        1000,   // Stage 2: Novice 3 (Tier 1 Novice Graduation Gate: 1,000 XP)
        1800,   // Stage 3: Elementary 1
        2600,   // Stage 4: Elementary 2
        3500,   // Stage 5: Elementary 3 (Tier 2 Elementary Graduation Gate: 3,500 XP)
        5500,   // Stage 6: Intermediate 1
        8000,   // Stage 7: Intermediate 2 (Tier 3 Intermediate Graduation Gate: 8,000 XP)
        12000,  // Stage 8: Advanced 1
        16000,  // Stage 9: Advanced 2 (Tier 4 Advanced Graduation Gate: 16,000 XP)
        23000,  // Stage 10: Mastery 1
        30000   // Stage 11: Mastery 2 (Tier 5 Native Master Speaker: 30,000+ XP)
    )
    val currentGoalXp = stageGoals.getOrElse(levelIndex) { tier.xpTargetToGraduate }
    val prevGoalXp = if (levelIndex > 0) stageGoals.getOrElse(levelIndex - 1) { 0 } else 0

    val currentXp = userStats.xp
    val span = (currentGoalXp - prevGoalXp).coerceAtLeast(1)
    val currentInSpan = (currentXp - prevGoalXp).coerceAtLeast(0)
    val xpProgress = (currentInSpan.toFloat() / span.toFloat()).coerceIn(0f, 1f)
    val xpRemaining = (currentGoalXp - currentXp).coerceAtLeast(0)

    val masteryStatus = DialectRepository.getLevelMasteryStatus(levelIndex, userStats)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.5.dp, GoldenSpice.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                .testTag("xp_progress_dialog"),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = GoldenContainerLight,
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(tier.iconEmoji, fontSize = 22.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "${currentStage.rankTitle}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = tier.title,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // XP Progress Card
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Current XP",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "$currentXp XP",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = GoldenOnContainer
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { xpProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp)),
                            color = GoldenSpice,
                            trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Goal: $currentGoalXp XP",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = if (xpRemaining > 0) "$xpRemaining XP to next level" else "Goal achieved! Complete drill & cards",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Speaking Milestone & Vocabulary Footprint
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PalmContainerLight.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "🎯 ${tier.shortTitle} Milestone (${tier.targetVocabulary})",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = EmeraldDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = tier.canDoStatements.firstOrNull() ?: tier.focusDescription,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Level Up Checklist
                Text(
                    text = "Level Up Checklist:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Card requirement
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (masteryStatus.isDeckMastered) Icons.Default.Check else Icons.Default.Close,
                        contentDescription = null,
                        tint = if (masteryStatus.isDeckMastered) EmeraldDark else TerracottaAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Master all flashcards (${masteryStatus.masteredCards}/${masteryStatus.totalCards} mastered)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Drill requirement
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (masteryStatus.isDrillMastered) Icons.Default.Check else Icons.Default.Close,
                        contentDescription = null,
                        tint = if (masteryStatus.isDrillMastered) EmeraldDark else TerracottaAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (masteryStatus.isDrillMastered) "Master 3-round drill (Completed ✓)" else "Master 3-round progressive drill",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (nextStage != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Next: ${nextStage.iconEmoji} ${nextStage.rankTitle} (${nextStage.topicTitle})",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = EmeraldDark
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Text("Close", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
