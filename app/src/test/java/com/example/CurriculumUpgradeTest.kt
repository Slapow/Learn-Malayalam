package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.DialectRepository
import com.example.data.DrillItemDatabase
import com.example.data.DrillItemRepository
import com.example.data.UserStatsRepository
import com.example.model.ActflProficiencyTier
import com.example.model.DrillItem
import com.example.model.SpokenCurriculum
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class CurriculumUpgradeTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun `reseeding adds cards and moves existing cards without resetting review history`() = runBlocking {
        val database = Room.inMemoryDatabaseBuilder(context, DrillItemDatabase::class.java).build()
        try {
            val dao = database.drillItemDao()
            dao.insertAll(listOf(DrillItem(
                id = "e1_01", tier = ActflProficiencyTier.ELEMENTARY,
                levelId = "elementary_1_questions_directions", levelNumber = 1,
                malayalam = "old", manglish = "old", english = "old",
                intervalDays = 17, repetitionCount = 4, easeFactor = 2.3f,
                nextReviewTimestamp = 123456789L, lastReviewedTimestamp = 123L
            )))
            val repository = DrillItemRepository(dao)
            repository.seedCurriculumIfNeeded()
            repository.seedCurriculumIfNeeded()
            assertEquals(DialectRepository.allPhrases.size, dao.countItems())
            val level = SpokenCurriculum.levels.first { it.deckId == "deck_elem_1_questions_direction" }
            val card = dao.getItemsForLevel(level.levelId).single { it.id == "e1_01" }
            assertEquals(DialectRepository.allPhrases.single { it.id == card.id }.english, card.english)
            assertEquals(17, card.intervalDays)
            assertEquals(4, card.repetitionCount)
            assertEquals(2.3f, card.easeFactor, 0.001f)
            assertEquals(123456789L, card.nextReviewTimestamp)
            assertEquals(123L, card.lastReviewedTimestamp)
            SpokenCurriculum.levels.forEach {
                assertEquals(it.entryCount, dao.getItemsForLevel(it.levelId).size)
            }
        } finally {
            database.close()
        }
    }

    @Test
    fun `legacy progress follows its original units and migration runs only once`() {
        val prefs = context.getSharedPreferences("parayu_stats", Context.MODE_PRIVATE)
        prefs.edit().clear()
            .putInt("unlocked_level", 4)
            .putStringSet("mastered_drills", setOf("0", "3"))
            .putStringSet("completed_drill_rounds", setOf("3:2"))
            .putStringSet("learned_phrases", setOf("n0_01"))
            .putStringSet("graduated_tiers", setOf("novice"))
            .commit()
        try {
            val first = UserStatsRepository(context).stats.value
            assertEquals(SpokenCurriculum.migrateLegacyIndex(4), first.unlockedLevelIndex)
            assertEquals(setOf(0, SpokenCurriculum.migrateLegacyIndex(3)), first.masteredDrillLevelIndices)
            assertTrue("${SpokenCurriculum.migrateLegacyIndex(3)}:2" in first.completedDrillRounds)
            assertEquals(setOf("n0_01"), first.learnedPhraseIds)
            assertEquals(setOf("novice"), first.graduatedTiers)
            assertFalse(1 in first.masteredDrillLevelIndices) // New introductions unit.
            val second = UserStatsRepository(context).stats.value
            assertEquals(first.unlockedLevelIndex, second.unlockedLevelIndex)
            assertEquals(first.masteredDrillLevelIndices, second.masteredDrillLevelIndices)
        } finally {
            prefs.edit().clear().commit()
        }
    }
}
