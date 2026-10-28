package com.example

import com.example.data.DialectRepository
import com.example.model.ActflProficiencyTier
import com.example.model.UserStats
import com.example.model.SpokenCurriculum
import org.junit.Assert.*
import org.junit.Test

class LevelProgressionUnitTest {

    @Test
    fun `initial level 0 displays Novice Level 1 in header and progress banner`() {
        val initialStats = UserStats(unlockedLevelIndex = 0)
        val stage = DialectRepository.getCurrentStage(initialStats)

        assertEquals("Novice Level 1", stage.rankTitle)
        assertEquals("🌱", stage.iconEmoji)

        val bannerTitle = "${stage.rankTitle} Progress"
        assertEquals("Novice Level 1 Progress", bannerTitle)
    }

    @Test
    fun `curriculum has 40 aligned levels with achievable tier targets`() {
        assertEquals(40, DialectRepository.studyPlanStages.size)
        assertEquals(40, DialectRepository.decks.size)
        assertEquals(996, DialectRepository.allPhrases.size)
        assertEquals(996, DialectRepository.allPhrases.map { it.id }.toSet().size)
        assertEquals(listOf(7, 11, 10, 7, 5), ActflProficiencyTier.entries.map { tier ->
            SpokenCurriculum.levels.count { it.tier == tier }
        })
        var cumulative = 0
        DialectRepository.studyPlanStages.forEach { stage ->
            val level = SpokenCurriculum.levels[stage.levelIndex]
            val deck = DialectRepository.decks.single { it.id == stage.deckId }
            assertEquals(level.deckId, deck.id)
            assertEquals(level.entryCount, deck.phrases.size)
            assertEquals(cumulative, stage.requiredWordsToUnlock)
            assertTrue(deck.phrases.all { it.levelNumber == stage.levelIndex })
            cumulative += deck.phrases.size
        }
        ActflProficiencyTier.entries.forEach { tier ->
            val stages = DialectRepository.studyPlanStages.filter { it.tierId == tier.tierId }
            assertEquals(listOf(stages.last()), stages.filter { it.isTierFinalStage })
            val available = DialectRepository.allPhrases.count { phrase ->
                SpokenCurriculum.levels[phrase.levelNumber].tier.levelNumber <= tier.levelNumber
            }
            assertEquals(available, tier.minWordsToGraduate)
        }
    }

    @Test
    fun `spoken course reviews use taught phrases from the assessed tier`() {
        ActflProficiencyTier.entries.forEach { tier ->
            val prompts = DialectRepository.getGraduationPromptsForTier(tier)
            assertTrue(prompts.isNotEmpty())
            prompts.forEach { prompt ->
                assertEquals(tier, SpokenCurriculum.levels[prompt.phrase.levelNumber].tier)
                assertEquals(prompt.phrase.manglish, prompt.targetExpectedManglish)
            }
        }
    }

    @Test
    fun `all stages within each major tier share the exact same major tier emoji`() {
        val stages = DialectRepository.studyPlanStages
        stages.forEach { stage ->
            val tier = ActflProficiencyTier.fromTierId(stage.tierId)
            assertEquals(
                "Stage ${stage.rankTitle} should use major tier emoji ${tier.iconEmoji}",
                tier.iconEmoji,
                stage.iconEmoji
            )
        }
    }

    @Test
    fun `top right level indicator shows tier emoji and level number within tier`() {
        // Stage 0: Novice Level 1 -> 🌱 1
        val stage0 = DialectRepository.studyPlanStages[0]
        val tier0 = ActflProficiencyTier.fromTierId(stage0.tierId)
        val tierStages0 = DialectRepository.studyPlanStages.filter { it.tierId == stage0.tierId }
        val levelInTier0 = tierStages0.indexOfFirst { it.levelIndex == stage0.levelIndex } + 1

        assertEquals("🌱", tier0.iconEmoji)
        assertEquals(1, levelInTier0)
        val indicator0 = "${tier0.iconEmoji} $levelInTier0"
        assertEquals("🌱 1", indicator0)

        // Stage 1: Novice Level 2 -> 🌱 2
        val stage1 = DialectRepository.studyPlanStages[1]
        val tier1 = ActflProficiencyTier.fromTierId(stage1.tierId)
        val tierStages1 = DialectRepository.studyPlanStages.filter { it.tierId == stage1.tierId }
        val levelInTier1 = tierStages1.indexOfFirst { it.levelIndex == stage1.levelIndex } + 1

        assertEquals("🌱", tier1.iconEmoji)
        assertEquals(2, levelInTier1)
        val indicator1 = "${tier1.iconEmoji} $levelInTier1"
        assertEquals("🌱 2", indicator1)

        // Stage 2: Novice Level 3 -> 🌱 3
        val stage2 = DialectRepository.studyPlanStages[2]
        val tier2 = ActflProficiencyTier.fromTierId(stage2.tierId)
        val tierStages2 = DialectRepository.studyPlanStages.filter { it.tierId == stage2.tierId }
        val levelInTier2 = tierStages2.indexOfFirst { it.levelIndex == stage2.levelIndex } + 1

        assertEquals("🌱", tier2.iconEmoji)
        assertEquals(3, levelInTier2)
        val indicator2 = "${tier2.iconEmoji} $levelInTier2"
        assertEquals("🌱 3", indicator2)

        // Stage 3: Novice Level 4 -> 🌱 4
        val stage3 = DialectRepository.studyPlanStages[3]
        val tier3 = ActflProficiencyTier.fromTierId(stage3.tierId)
        val tierStages3 = DialectRepository.studyPlanStages.filter { it.tierId == stage3.tierId }
        val levelInTier3 = tierStages3.indexOfFirst { it.levelIndex == stage3.levelIndex } + 1

        assertEquals("🌱", tier3.iconEmoji)
        assertEquals(4, levelInTier3)
        val indicator3 = "${tier3.iconEmoji} $levelInTier3"
        assertEquals("🌱 4", indicator3)

        // Each tier starts its displayed level numbering at one.
        val stage4 = DialectRepository.studyPlanStages.first { it.tierId == "elementary" }
        val tier4 = ActflProficiencyTier.fromTierId(stage4.tierId)
        val tierStages4 = DialectRepository.studyPlanStages.filter { it.tierId == stage4.tierId }
        val levelInTier4 = tierStages4.indexOfFirst { it.levelIndex == stage4.levelIndex } + 1

        assertEquals("🛺", tier4.iconEmoji)
        assertEquals(1, levelInTier4)
        val indicator4 = "${tier4.iconEmoji} $levelInTier4"
        assertEquals("🛺 1", indicator4)
    }

    @Test
    fun `completing Novice Level 1 drill masters the level and unlocks Novice Level 2`() {
        val statsWithDrillMastered = UserStats(
            unlockedLevelIndex = 0,
            masteredDrillLevelIndices = setOf(0)
        )

        val status = DialectRepository.getLevelMasteryStatus(0, statsWithDrillMastered)
        assertTrue("Level should be mastered when drill is completed", status.isLevelMastered)

        // Advance to next level
        val updatedStats = statsWithDrillMastered.copy(unlockedLevelIndex = 1)
        val newStage = DialectRepository.getCurrentStage(updatedStats)

        assertEquals("Novice Level 2", newStage.rankTitle)
        val bannerTitle = "${newStage.rankTitle} Progress"
        assertEquals("Novice Level 2 Progress", bannerTitle)
    }

    @Test
    fun `completing Novice Level 1 deck masters the level`() {
        val deck0 = DialectRepository.decks.first()
        val masteredPhrases = deck0.phrases.map { it.id }.toSet()

        val statsWithDeckMastered = UserStats(
            unlockedLevelIndex = 0,
            learnedPhraseIds = masteredPhrases,
            masteredDeckIds = setOf(deck0.id)
        )

        val status = DialectRepository.getLevelMasteryStatus(0, statsWithDeckMastered)
        assertTrue("Level should be mastered when deck is fully learned", status.isLevelMastered)
    }

    @Test
    fun `progression through novice levels 1, 2, 3, and 4 updates titles sequentially`() {
        val stages = listOf(0, 1, 2, 3).map { level ->
            val stats = UserStats(unlockedLevelIndex = level)
            DialectRepository.getCurrentStage(stats)
        }

        assertEquals("Novice Level 1", stages[0].rankTitle)
        assertEquals("Novice Level 2", stages[1].rankTitle)
        assertEquals("Novice Level 3", stages[2].rankTitle)
        assertEquals("Novice Level 4", stages[3].rankTitle)

        assertEquals("Novice Level 1 Progress", "${stages[0].rankTitle} Progress")
        assertEquals("Novice Level 2 Progress", "${stages[1].rankTitle} Progress")
        assertEquals("Novice Level 3 Progress", "${stages[2].rankTitle} Progress")
        assertEquals("Novice Level 4 Progress", "${stages[3].rankTitle} Progress")
    }
}
