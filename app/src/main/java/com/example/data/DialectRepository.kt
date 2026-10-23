package com.example.data

import com.example.model.*

object DialectRepository {

    val allPhrases: List<PhraseItem> = listOf(
        // =========================================================================
        // STAGE 0: GROUND LEVEL 0 — First 12 Core Words & Pronouns (1 Word Each!)
        // =========================================================================
        PhraseItem(
            id = "l0_01",
            manglish = "Njaan",
            malayalam = "ഞാൻ",
            english = "I / Me",
            regionalNuance = "The #1 most used word in Malayalam. Pronounced with a soft 'ny' sound at the start.",
            textbookAlternative = "Njan",
            category = "Novice Level 1: Core Words",
            levelNumber = 0,
            levelBadge = "Novice Level 1",
            phoneticGuide = "NYAAN (rhymes with 'naan')",
            wordBreakdown = "Njaan = I / me",
            acceptedVariants = listOf("njaan", "njan", "nyan", "gnan"),
            tags = listOf("level0", "pronoun", "core")
        ),
        PhraseItem(
            id = "l0_02",
            manglish = "Nee",
            malayalam = "നീ",
            english = "You (casual / friend)",
            regionalNuance = "Used with friends, siblings, or younger people at home.",
            textbookAlternative = "Thaankal (formal)",
            category = "Novice Level 1: Core Words",
            levelNumber = 0,
            levelBadge = "Novice Level 1",
            phoneticGuide = "NEE (just like English 'knee')",
            wordBreakdown = "Nee = You (informal)",
            acceptedVariants = listOf("nee", "ni"),
            tags = listOf("level0", "pronoun", "core")
        ),
        PhraseItem(
            id = "l0_03",
            manglish = "Athe",
            malayalam = "അതെ",
            english = "Yes / That's right",
            regionalNuance = "Your everyday word for 'Yes' when agreeing with someone.",
            textbookAlternative = "Athe",
            category = "Novice Level 1: Core Words",
            levelNumber = 0,
            levelBadge = "Novice Level 1",
            phoneticGuide = "UH-they (soft 'th' as in 'they')",
            wordBreakdown = "Athe = Yes",
            acceptedVariants = listOf("athe", "athey", "ade"),
            tags = listOf("level0", "basics", "core")
        ),
        PhraseItem(
            id = "l0_04",
            manglish = "Illa",
            malayalam = "ഇല്ല",
            english = "No / Don't have / Not there",
            regionalNuance = "Used for 'No' when saying something isn't there or didn't happen.",
            textbookAlternative = "Illa",
            category = "Novice Level 1: Core Words",
            levelNumber = 0,
            levelBadge = "Novice Level 1",
            phoneticGuide = "IL-lah",
            wordBreakdown = "Illa = No / Not present",
            acceptedVariants = listOf("illa", "ila"),
            tags = listOf("level0", "basics", "core")
        ),
        PhraseItem(
            id = "l0_05",
            manglish = "Alla",
            malayalam = "അല്ല",
            english = "No (Not that one)",
            regionalNuance = "While 'Illa' means 'don't have', 'Alla' means 'it is not that' (e.g., 'Not tea, coffee').",
            textbookAlternative = "Alla",
            category = "Novice Level 1: Core Words",
            levelNumber = 0,
            levelBadge = "Novice Level 1",
            phoneticGuide = "UL-lah",
            wordBreakdown = "Alla = No / Not that",
            acceptedVariants = listOf("alla", "ala"),
            tags = listOf("level0", "basics", "core")
        ),
        PhraseItem(
            id = "l0_06",
            manglish = "Undo?",
            malayalam = "ഉണ്ടോ?",
            english = "Is there? / Do you have?",
            regionalNuance = "Add 'undo?' after any noun (like 'Chaya undo?') to ask 'Do you have tea?'.",
            textbookAlternative = "Undodo?",
            category = "Novice Level 1: Core Words",
            levelNumber = 0,
            levelBadge = "Novice Level 1",
            phoneticGuide = "OON-doh?",
            wordBreakdown = "Undo? = Is there? / Have?",
            acceptedVariants = listOf("undo", "undoo", "ondo"),
            tags = listOf("level0", "question", "core")
        ),
        PhraseItem(
            id = "l0_07",
            manglish = "Veno?",
            malayalam = "വേണോ?",
            english = "Do you want?",
            regionalNuance = "Heard 20 times a day in an Adoor kitchen: 'Chaya veno?' (Do you want tea?).",
            textbookAlternative = "Venamo?",
            category = "Novice Level 1: Core Words",
            levelNumber = 0,
            levelBadge = "Novice Level 1",
            phoneticGuide = "VAY-noh?",
            wordBreakdown = "Veno? = Want?",
            acceptedVariants = listOf("veno", "venoo", "venamo"),
            tags = listOf("level0", "question", "core")
        ),
        PhraseItem(
            id = "l0_08",
            manglish = "Venda",
            malayalam = "വേണ്ട",
            english = "Don't want / No thanks",
            regionalNuance = "Polite & direct reply to 'Veno?' when you're full: 'Venda!' (No thanks / don't want).",
            textbookAlternative = " Aavashyamilla",
            category = "Novice Level 1: Core Words",
            levelNumber = 0,
            levelBadge = "Novice Level 1",
            phoneticGuide = "VEN-dah",
            wordBreakdown = "Venda = Don't want",
            acceptedVariants = listOf("venda", "vendaa"),
            tags = listOf("level0", "basics", "core")
        ),
        PhraseItem(
            id = "l0_09",
            manglish = "Entha?",
            malayalam = "എന്താ?",
            english = "What? / What is it?",
            regionalNuance = "Casual spoken form of textbook 'Enthaanu?'. Short, warm, and natural.",
            textbookAlternative = "Enthaanu?",
            category = "Novice Level 1: Core Words",
            levelNumber = 0,
            levelBadge = "Novice Level 1",
            phoneticGuide = "EN-thah?",
            wordBreakdown = "Entha? = What?",
            acceptedVariants = listOf("entha", "enthaa", "enthu"),
            tags = listOf("level0", "question", "core")
        ),
        PhraseItem(
            id = "l0_10",
            manglish = "Evide?",
            malayalam = "എവിടെ?",
            english = "Where?",
            regionalNuance = "Attach after any person or thing ('Appa evide?') to ask 'Where is Dad?'.",
            textbookAlternative = "Evideyaanu?",
            category = "Novice Level 1: Core Words",
            levelNumber = 0,
            levelBadge = "Novice Level 1",
            phoneticGuide = "EH-vi-day?",
            wordBreakdown = "Evide? = Where?",
            acceptedVariants = listOf("evide", "evida", "evidey"),
            tags = listOf("level0", "question", "core")
        ),
        PhraseItem(
            id = "l0_11",
            manglish = "Ippo",
            malayalam = "ഇപ്പൊ",
            english = "Now / Right now",
            regionalNuance = "Spoken Central Travancore drops the final 'l' of textbook 'ippol' -> 'ippo'.",
            textbookAlternative = "Ippol",
            category = "Novice Level 1: Core Words",
            levelNumber = 0,
            levelBadge = "Novice Level 1",
            phoneticGuide = "IP-poh",
            wordBreakdown = "Ippo = Now",
            acceptedVariants = listOf("ippo", "ippol", "ipo"),
            tags = listOf("level0", "time", "core")
        ),
        PhraseItem(
            id = "l0_12",
            manglish = "Ketto",
            malayalam = "കേട്ടോ",
            english = "Okay? / Got it? (Friendly tag)",
            regionalNuance = "Added to the end of sentences in Adoor to sound warm and caring (literally 'Did you hear?').",
            textbookAlternative = "Kettuvo?",
            category = "Novice Level 1: Core Words",
            levelNumber = 0,
            levelBadge = "Novice Level 1",
            phoneticGuide = "KET-toh",
            wordBreakdown = "Ketto = Okay? / Heard?",
            acceptedVariants = listOf("ketto", "keto", "kettoo"),
            tags = listOf("level0", "tag", "core")
        ),

        // =========================================================================
        // STAGE 1: BEGINNER LEVEL 1 — Family & First 2-Word Combos
        // =========================================================================
        PhraseItem(
            id = "l1_01",
            manglish = "Appa",
            malayalam = "അപ്പ",
            english = "Dad / Father",
            regionalNuance = "Natural Central Travancore / Adoor family word for father.",
            textbookAlternative = "Pithavu",
            category = "Beginner Level 1: Family & 2-Word Combos",
            levelNumber = 1,
            levelBadge = "Beginner Level 1",
            phoneticGuide = "UP-pah",
            wordBreakdown = "Appa = Dad / Father",
            acceptedVariants = listOf("appa", "appan"),
            tags = listOf("level1", "family")
        ),
        PhraseItem(
            id = "l1_02",
            manglish = "Amma",
            malayalam = "അമ്മ",
            english = "Mom / Mother",
            regionalNuance = "Universal warm word for mother across Kerala.",
            textbookAlternative = "Maathavu",
            category = "Beginner Level 1: Family & 2-Word Combos",
            levelNumber = 1,
            levelBadge = "Beginner Level 1",
            phoneticGuide = "UM-mah",
            wordBreakdown = "Amma = Mom / Mother",
            acceptedVariants = listOf("amma", "ama"),
            tags = listOf("level1", "family")
        ),
        PhraseItem(
            id = "l1_03",
            manglish = "Ammachi",
            malayalam = "അമ്മാച്ചി",
            english = "Grandma (Central Travancore)",
            regionalNuance = "'Ammachi' is the signature Pathanamthitta/Adoor term for grandmother.",
            textbookAlternative = "Muthassi",
            category = "Beginner Level 1: Family & 2-Word Combos",
            levelNumber = 1,
            levelBadge = "Beginner Level 1",
            phoneticGuide = "UM-mah-chee",
            wordBreakdown = "Ammachi = Grandma",
            acceptedVariants = listOf("ammachi", "amachi", "ammachiye"),
            tags = listOf("level1", "family")
        ),
        PhraseItem(
            id = "l1_04",
            manglish = "Appachan",
            malayalam = "അപ്പച്ചൻ",
            english = "Grandpa (Central Travancore)",
            regionalNuance = "'Appachan' is grandfather or respected elder man in Central Travancore.",
            textbookAlternative = "Muthassan",
            category = "Beginner Level 1: Family & 2-Word Combos",
            levelNumber = 1,
            levelBadge = "Beginner Level 1",
            phoneticGuide = "UP-puh-chun",
            wordBreakdown = "Appachan = Grandpa",
            acceptedVariants = listOf("appachan", "apachan"),
            tags = listOf("level1", "family")
        ),
        PhraseItem(
            id = "l1_05",
            manglish = "Chaya veno?",
            malayalam = "ചായ വേണോ?",
            english = "Do you want tea?",
            regionalNuance = "Your first 2-word sentence! Combines 'Chaya' (tea) + 'veno?' (do you want?).",
            textbookAlternative = "Chaya aavashyamundo?",
            category = "Beginner Level 1: Family & 2-Word Combos",
            levelNumber = 1,
            levelBadge = "Beginner Level 1",
            phoneticGuide = "CHAH-yah VAY-noh?",
            wordBreakdown = "Chaya (tea) + veno? (do you want?)",
            prerequisiteIds = listOf("l0_07"),
            acceptedVariants = listOf("chaya veno", "chaaya veno", "chaya venoo"),
            tags = listOf("level1", "food", "combo")
        ),
        PhraseItem(
            id = "l1_06",
            manglish = "Vellam veno?",
            malayalam = "വെള്ളം വേണോ?",
            english = "Do you want water?",
            regionalNuance = "'Vellam' is water. 'Vellam veno?' is asked to every guest who steps into the house.",
            textbookAlternative = "Jalam venamo?",
            category = "Beginner Level 1: Family & 2-Word Combos",
            levelNumber = 1,
            levelBadge = "Beginner Level 1",
            phoneticGuide = "VEL-lum VAY-noh?",
            wordBreakdown = "Vellam (water) + veno? (do you want?)",
            prerequisiteIds = listOf("l0_07"),
            acceptedVariants = listOf("vellam veno", "velam veno"),
            tags = listOf("level1", "food", "combo")
        ),
        PhraseItem(
            id = "l1_07",
            manglish = "Chaya venda",
            malayalam = "ചായ വേണ്ട",
            english = "No tea, thanks / Don't want tea",
            regionalNuance = "Combines 'Chaya' (tea) + 'venda' (don't want).",
            textbookAlternative = "Chaya venda",
            category = "Beginner Level 1: Family & 2-Word Combos",
            levelNumber = 1,
            levelBadge = "Beginner Level 1",
            phoneticGuide = "CHAH-yah VEN-dah",
            wordBreakdown = "Chaya (tea) + venda (don't want)",
            prerequisiteIds = listOf("l0_08"),
            acceptedVariants = listOf("chaya venda", "chaaya venda"),
            tags = listOf("level1", "food", "combo")
        ),
        PhraseItem(
            id = "l1_08",
            manglish = "Amma evide?",
            malayalam = "അമ്മ എവിടെ?",
            english = "Where is Mom?",
            regionalNuance = "In Malayalam you don't need a separate word for 'is' here—just 'Amma' + 'evide?'!",
            textbookAlternative = "Amma evideyaanu?",
            category = "Beginner Level 1: Family & 2-Word Combos",
            levelNumber = 1,
            levelBadge = "Beginner Level 1",
            phoneticGuide = "UM-mah EH-vi-day?",
            wordBreakdown = "Amma (Mom) + evide? (where?)",
            prerequisiteIds = listOf("l1_02", "l0_10"),
            acceptedVariants = listOf("amma evide", "amma evida"),
            tags = listOf("level1", "family", "combo")
        ),
        PhraseItem(
            id = "l1_09",
            manglish = "Sugamaano?",
            malayalam = "സുഖമാണോ?",
            english = "How are you? / Are you doing well?",
            regionalNuance = "The standard friendly greeting when you meet someone.",
            textbookAlternative = "Kshemamaano?",
            category = "Beginner Level 1: Family & 2-Word Combos",
            levelNumber = 1,
            levelBadge = "Beginner Level 1",
            phoneticGuide = "SOO-guh-mah-noh?",
            wordBreakdown = "Sugam (good/well) + aano? (is it?)",
            acceptedVariants = listOf("sugamaano", "sukhamano", "sugamano", "sughamaano"),
            tags = listOf("level1", "greeting")
        ),
        PhraseItem(
            id = "l1_10",
            manglish = "Njaan varaam",
            malayalam = "ഞാൻ വരാം",
            english = "I will come / See you soon",
            regionalNuance = "Used both for 'I'll come' and as a polite 'See you later!' when leaving.",
            textbookAlternative = "Njan varാം",
            category = "Beginner Level 1: Family & 2-Word Combos",
            levelNumber = 1,
            levelBadge = "Beginner Level 1",
            phoneticGuide = "NYAAN vuh-RAAM",
            wordBreakdown = "Njaan (I) + varaam (will come)",
            prerequisiteIds = listOf("l0_01"),
            acceptedVariants = listOf("njaan varaam", "njan varam", "njan varaam", "njaan varam"),
            tags = listOf("level1", "combo")
        ),

        // =========================================================================
        // STAGE 2: BEGINNER LEVEL 2 — Adoor Action Shortcuts (-oolla)
        // =========================================================================
        PhraseItem(
            id = "l2_01",
            manglish = "Varoolla",
            malayalam = "വരൂല്ല",
            english = "Won't come / Not coming",
            regionalNuance = "In Adoor/Pathanamthitta, textbook 'varunnilla' shrinks to 'varoolla' (or 'varathilla').",
            textbookAlternative = "Varunnilla",
            category = "Beginner Level 2: Action Shortcuts",
            levelNumber = 2,
            levelBadge = "Beginner Level 2",
            phoneticGuide = "vuh-ROO-lah",
            wordBreakdown = "Var- (come) + -oolla (won't / not)",
            acceptedVariants = listOf("varoolla", "varoola", "varathilla"),
            tags = listOf("level2", "contraction")
        ),
        PhraseItem(
            id = "l2_02",
            manglish = "Cheyyoolla",
            malayalam = "ചെയ്യൂല്ല",
            english = "Won't do / Not doing",
            regionalNuance = "Spoken Central Travancore shortcut for 'cheyyunnilla'.",
            textbookAlternative = "Cheyyunnilla",
            category = "Beginner Level 2: Action Shortcuts",
            levelNumber = 2,
            levelBadge = "Beginner Level 2",
            phoneticGuide = "chey-YOO-lah",
            wordBreakdown = "Cheyy- (do) + -oolla (won't / not)",
            acceptedVariants = listOf("cheyyoolla", "cheyyoola", "cheyoola", "cheyoolla"),
            tags = listOf("level2", "contraction")
        ),
        PhraseItem(
            id = "l2_03",
            manglish = "Pokoolla",
            malayalam = "പോകൂല്ല",
            english = "Won't go / Not going",
            regionalNuance = "Spoken contraction for 'pokunnilla' (not going).",
            textbookAlternative = "Pokunnilla",
            category = "Beginner Level 2: Action Shortcuts",
            levelNumber = 2,
            levelBadge = "Beginner Level 2",
            phoneticGuide = "poh-KOO-lah",
            wordBreakdown = "Pok- (go) + -oolla (won't / not)",
            acceptedVariants = listOf("pokoolla", "pokoola", "povoolla", "povoola"),
            tags = listOf("level2", "contraction")
        ),
        PhraseItem(
            id = "l2_04",
            manglish = "Parayoolla",
            malayalam = "പറയൂല്ല",
            english = "Won't say / Not telling",
            regionalNuance = "Spoken contraction for 'parayunnilla' (not saying).",
            textbookAlternative = "Parayunnilla",
            category = "Beginner Level 2: Action Shortcuts",
            levelNumber = 2,
            levelBadge = "Beginner Level 2",
            phoneticGuide = "puh-ruh-YOO-lah",
            wordBreakdown = "Paray- (say/tell) + -oolla (won't)",
            acceptedVariants = listOf("parayoolla", "parayoola"),
            tags = listOf("level2", "contraction")
        ),
        PhraseItem(
            id = "l2_05",
            manglish = "Ariyoolla",
            malayalam = "അറിയൂല്ല",
            english = "Don't know",
            regionalNuance = "Casual spoken way to say 'I don't know' ('Enikku ariyoolla').",
            textbookAlternative = "Ariyilla",
            category = "Beginner Level 2: Action Shortcuts",
            levelNumber = 2,
            levelBadge = "Beginner Level 2",
            phoneticGuide = "uh-ree-YOO-lah",
            wordBreakdown = "Ariy- (know) + -oolla (don't)",
            acceptedVariants = listOf("ariyoolla", "ariyoola", "ariyathilla", "ariyilla"),
            tags = listOf("level2", "contraction")
        ),
        PhraseItem(
            id = "l2_06",
            manglish = "Scene illa",
            malayalam = "സീൻ ഇല്ല",
            english = "No problem / All good",
            regionalNuance = "Everyday Manglish blending English 'scene' + Malayalam 'illa' (no).",
            textbookAlternative = "Prashnamilla",
            category = "Beginner Level 2: Action Shortcuts",
            levelNumber = 2,
            levelBadge = "Beginner Level 2",
            phoneticGuide = "SEEN IL-lah",
            wordBreakdown = "Scene (issue/drama) + illa (no/none)",
            prerequisiteIds = listOf("l0_04"),
            acceptedVariants = listOf("scene illa", "seen illa", "scene ila"),
            tags = listOf("level2", "manglish")
        ),

        // =========================================================================
        // STAGE 3: BEGINNER LEVEL 3 — First Short Home Sentences
        // =========================================================================
        PhraseItem(
            id = "ct_01",
            manglish = "Njaan ippo varoolla ketto",
            malayalam = "ഞാൻ ഇപ്പൊ വരൂല്ല കേട്ടോ",
            english = "I won't be coming right now, okay?",
            regionalNuance = "Combines 4 words you learned in Levels 0 & 2: Njaan + ippo + varoolla + ketto!",
            textbookAlternative = "Njan ippol varunnilla",
            category = "Contractions",
            levelNumber = 3,
            levelBadge = "Beginner Level 3",
            phoneticGuide = "NYAAN IP-poh vuh-ROO-lah KET-toh",
            wordBreakdown = "Njaan (I) + ippo (now) + varoolla (won't come) + ketto (okay?)",
            prerequisiteIds = listOf("l0_01", "l0_11", "l2_01", "l0_12"),
            acceptedVariants = listOf(
                "njaan ippo varoolla ketto",
                "njan ippo varoolla ketto",
                "njan ippo varoola ketto",
                "njaan ippol varoolla ketto"
            ),
            tags = listOf("contraction", "daily", "adoor")
        ),
        PhraseItem(
            id = "ct_02",
            manglish = "Avar athu cheyyoolla",
            malayalam = "അവർ അത് ചെയ്യൂല്ല",
            english = "They won't do that.",
            regionalNuance = "'cheyyoolla' is the natural Adoor contraction for formal 'cheyyunnilla'.",
            textbookAlternative = "Avar athu cheyyunnilla",
            category = "Contractions",
            levelNumber = 3,
            levelBadge = "Beginner Level 3",
            phoneticGuide = "UH-vur UH-thoo chey-YOO-lah",
            wordBreakdown = "Avar (They) + athu (that) + cheyyoolla (won't do)",
            prerequisiteIds = listOf("l2_02"),
            acceptedVariants = listOf("avar athu cheyyoolla", "avar athu cheyyoola", "avar ath cheyyoola"),
            tags = listOf("contraction", "spoken")
        ),
        PhraseItem(
            id = "ct_04",
            manglish = "Njaan athu parayoolla",
            malayalam = "ഞാൻ അത് പറയൂല്ല",
            english = "I won't say that.",
            regionalNuance = "Spoken 'parayoolla' in place of literary 'parayunnilla'.",
            textbookAlternative = "Njan athu parayunnilla",
            category = "Contractions",
            levelNumber = 3,
            levelBadge = "Beginner Level 3",
            phoneticGuide = "NYAAN UH-thoo puh-ruh-YOO-lah",
            wordBreakdown = "Njaan (I) + athu (that) + parayoolla (won't say)",
            prerequisiteIds = listOf("l0_01", "l2_04"),
            acceptedVariants = listOf("njaan athu parayoolla", "njan athu parayoolla", "njan athu parayoola"),
            tags = listOf("contraction", "daily")
        ),
        PhraseItem(
            id = "ct_03",
            manglish = "Appa innale town-il pokoolla ennu paranju",
            malayalam = "അപ്പ ഇന്നലെ ടൗണിൽ പോകൂല്ല എന്ന് പറഞ്ഞു",
            english = "Appa said he won't be going to town.",
            regionalNuance = "'pokoolla' instead of 'pokunnilla'. Notice 'Appa' used for father naturally in Central Travancore households.",
            textbookAlternative = "Pithavu innale nagarathil pokunnilla ennu paranju",
            category = "Contractions",
            levelNumber = 3,
            levelBadge = "Beginner Level 3",
            phoneticGuide = "UP-pah IN-nuh-ley TOWN-il poh-KOO-lah EN-noo puh-RUN-joo",
            wordBreakdown = "Appa (Dad) + innale (yesterday) + town-il (to town) + pokoolla (won't go) + ennu paranju (said that)",
            prerequisiteIds = listOf("l1_01", "l2_03"),
            acceptedVariants = listOf(
                "appa innale town-il pokoolla ennu paranju",
                "appa innale townil pokoolla ennu paranju",
                "appa innale townil pokoola ennu paranju"
            ),
            tags = listOf("contraction", "family", "travel")
        ),
        PhraseItem(
            id = "ct_05",
            manglish = "Ammachi ippo varathilla, kurachu kazhinje varoo",
            malayalam = "അമ്മാച്ചി ഇപ്പൊ വരത്തില്ല, കുറച്ചു കഴിഞ്ഞേ വരൂ",
            english = "Ammachi won't be coming now, she will come after a while.",
            regionalNuance = "'varathilla' is another common Pathanamthitta variant of the negative future contraction alongside 'varoolla'.",
            textbookAlternative = "Muthassi ippol varikayilla",
            category = "Contractions",
            levelNumber = 3,
            levelBadge = "Beginner Level 3",
            phoneticGuide = "UM-mah-chee IP-poh vuh-RUH-thil-lah, koo-RUH-choo kuh-ZHIN-jey vuh-ROO",
            wordBreakdown = "Ammachi (Grandma) + ippo (now) + varathilla (won't come) + kurachu kazhinje (after a little while) + varoo (will come)",
            prerequisiteIds = listOf("l1_03", "l0_11", "l2_01"),
            acceptedVariants = listOf(
                "ammachi ippo varathilla kurachu kazhinje varoo",
                "ammachi ippo varathilla",
                "ammachi ippo varoolla"
            ),
            tags = listOf("contraction", "ammachi")
        ),

        // =========================================================================
        // STAGE 4: NOVICE LEVEL 1 — Family & Home with Ammachi
        // =========================================================================
        PhraseItem(
            id = "ct_08",
            manglish = "Ammachiye, chaya ready aayo?",
            malayalam = "അമ്മാച്ചിയേ, ചായ റെഡിയായോ?",
            english = "Ammachi, is the tea ready?",
            regionalNuance = "'Ammachi' is the loving term for grandmother in Central Travancore. Manglish loanword 'ready aayo' is 100% natural at home.",
            textbookAlternative = "Muthassi, theyila jalam thayyaraayo?",
            category = "Family & Home",
            levelNumber = 4,
            levelBadge = "Novice Level 1",
            phoneticGuide = "UM-mah-chee-yey, CHAH-yah RED-ee AH-yoh?",
            wordBreakdown = "Ammachiye (O Grandma) + chaya (tea) + ready aayo? (did it become ready?)",
            prerequisiteIds = listOf("l1_03", "l1_05"),
            acceptedVariants = listOf(
                "ammachiye chaya ready aayo",
                "ammachi chaya ready aayo",
                "ammachiye chaya ready ayo"
            ),
            tags = listOf("family", "food", "manglish")
        ),
        PhraseItem(
            id = "ct_09",
            manglish = "Appachan palli-yil poyi vannu",
            malayalam = "അപ്പച്ചൻ പള്ളിയിൽ പോയി വന്നു",
            english = "Appachan went to church and came back.",
            regionalNuance = "'Appachan' is grandfather or revered elder. Sunday church visits are a cornerstone of Central Travancore family life.",
            textbookAlternative = "Muthassan aaraadhanalayathil poyi madangi ethi",
            category = "Family & Home",
            levelNumber = 4,
            levelBadge = "Novice Level 1",
            phoneticGuide = "UP-puh-chun PUL-lee-yil POH-yee VUN-noo",
            wordBreakdown = "Appachan (Grandpa) + palli-yil (to church) + poyi (went) + vannu (came)",
            prerequisiteIds = listOf("l1_04"),
            acceptedVariants = listOf(
                "appachan palliyil poyi vannu",
                "appachan palli yil poyi vannu"
            ),
            tags = listOf("family", "church")
        ),
        PhraseItem(
            id = "ct_10",
            manglish = "Achayan ippo call cheythu, bus kitti ennu paranju",
            malayalam = "അച്ചായൻ ഇപ്പൊ കോൾ ചെയ്തു, ബസ് കിട്ടി എന്ന് പറഞ്ഞു",
            english = "Achayan just called, said he got the bus.",
            regionalNuance = "'Achayan' is an affectionate address for an elder brother or male relative. Notice 'call cheythu' blended naturally.",
            textbookAlternative = "Jyeshtan ippol dhooravaani vazhi ariyichu",
            category = "Family & Home",
            levelNumber = 4,
            levelBadge = "Novice Level 1",
            phoneticGuide = "uh-CHAH-yun IP-poh CALL chey-thoo, BUS kit-tee EN-noo puh-RUN-joo",
            wordBreakdown = "Achayan (Elder brother) + ippo (now) + call cheythu (called) + bus kitti (got the bus) + ennu paranju (said that)",
            prerequisiteIds = listOf("l0_11"),
            acceptedVariants = listOf(
                "achayan ippo call cheythu bus kitti ennu paranju",
                "achayan ippo call cheythu"
            ),
            tags = listOf("family", "travel", "manglish")
        ),
        PhraseItem(
            id = "ct_11",
            manglish = "Amma ivide undo, atho kadayil poyo?",
            malayalam = "അമ്മ ഇവിടെ ഉണ്ടോ, അതോ കടയിൽ പോയോ?",
            english = "Is Amma here, or did she go to the shop?",
            regionalNuance = "Short casual question ending in 'poyo?' rather than formal 'poyirikkukayaano?'.",
            textbookAlternative = "Amma ivide undodo, atho kachavadasthalathilekku poyo?",
            category = "Family & Home",
            levelNumber = 4,
            levelBadge = "Novice Level 1",
            phoneticGuide = "UM-mah IH-vi-dey OON-doh, UH-thoh KUH-duh-yil POH-yoh?",
            wordBreakdown = "Amma (Mom) + ivide (here) + undo (is there?) + atho (or) + kadayil (to the shop) + poyo? (did she go?)",
            prerequisiteIds = listOf("l1_02", "l0_06"),
            acceptedVariants = listOf(
                "amma ivide undo atho kadayil poyo",
                "amma ivide undo"
            ),
            tags = listOf("family", "home")
        ),

        // =========================================================================
        // STAGE 5: NOVICE LEVEL 2 — Food & Thattukada
        // =========================================================================
        PhraseItem(
            id = "ct_12",
            manglish = "Bhai, oru double strong chaya-yum randu parottayum!",
            malayalam = "ഭായ്, ഒരു ഡബിൾ സ്ട്രോങ്ങ് ചായയും രണ്ട് പൊറോട്ടയും!",
            english = "Brother, one double strong tea and two parottas!",
            regionalNuance = "Standard Thattukada / Chaya kada order across Adoor.",
            textbookAlternative = "Sahodara, theevratha koodiya chaya labhikkumo?",
            category = "Food & Chaya",
            levelNumber = 5,
            levelBadge = "Novice Level 2",
            phoneticGuide = "BHAI, OH-roo DOUBLE STRONG CHAH-yah-yum RUN-doo puh-ROH-tuh-yum!",
            wordBreakdown = "Bhai (Brother) + oru (one) + double strong chaya-yum (strong tea and) + randu (two) + parottayum (parottas)",
            prerequisiteIds = listOf("l1_05"),
            acceptedVariants = listOf(
                "bhai oru double strong chayayum randu parottayum",
                "oru double strong chayayum randu parottayum",
                "bhai oru double strong chaya randu parotta"
            ),
            tags = listOf("food", "thattukada", "chaya")
        ),
        PhraseItem(
            id = "ct_13",
            manglish = "Pazhampori nallonam choodundo?",
            malayalam = "പഴംപൊരി നല്ലോണം ചൂടുണ്ടോ?",
            english = "Is the banana fritter piping hot?",
            regionalNuance = "'Nallonam' means properly/really well in Central Travancore speech.",
            textbookAlternative = "Pazhampori nannaayi choodaayittaano ullathu?",
            category = "Food & Chaya",
            levelNumber = 5,
            levelBadge = "Novice Level 2",
            phoneticGuide = "puh-ZHUM-poh-ree NUL-loh-num CHOO-doon-doh?",
            wordBreakdown = "Pazhampori (banana fritter) + nallonam (really well) + choodundo? (is it hot?)",
            prerequisiteIds = listOf("l0_06"),
            acceptedVariants = listOf(
                "pazhampori nallonam choodundo",
                "pazhampori choodundo"
            ),
            tags = listOf("food", "snack")
        ),
        PhraseItem(
            id = "ct_14",
            manglish = "Kadi entha ullathu? Appam undo?",
            malayalam = "കടി എന്താ ഉള്ളത്? അപ്പം ഉണ്ടോ?",
            english = "What snacks do you have? Is there Appam?",
            regionalNuance = "'Kadi' refers to tea-time snacks in Malayalam.",
            textbookAlternative = "Laghubhakshanangal enthellaamaanu labhyam?",
            category = "Food & Chaya",
            levelNumber = 5,
            levelBadge = "Novice Level 2",
            phoneticGuide = "KUH-dee EN-thah OOL-luh-thoo? UP-pum OON-doh?",
            wordBreakdown = "Kadi (snacks) + entha (what) + ullathu (is available)? + Appam (rice hopper) + undo? (is there?)",
            prerequisiteIds = listOf("l0_09", "l0_06"),
            acceptedVariants = listOf(
                "kadi entha ullathu appam undo",
                "appam undo"
            ),
            tags = listOf("food", "breakfast")
        ),

        // =========================================================================
        // STAGE 6: INTERMEDIATE LEVEL 1 — Travel & Auto in Pathanamthitta
        // =========================================================================
        PhraseItem(
            id = "ct_15",
            manglish = "Chetta, Adoor KSRTC stand-ilekku varumo?",
            malayalam = "ചേട്ടാ, അടൂർ കെ.എസ്.ആർ.ടി.സി സ്റ്റാൻഡിലേക്ക് വരുമോ?",
            english = "Brother, will you come to the Adoor KSRTC bus stand?",
            regionalNuance = "How to hail an autorickshaw politely in Adoor.",
            textbookAlternative = "Driver mahashaya, vandi varumo?",
            category = "Travel & Auto",
            levelNumber = 6,
            levelBadge = "Intermediate Level 1",
            phoneticGuide = "CHEYT-tah, uh-DOOR K-S-R-T-C STAND-i-ley-koo vuh-ROO-moh?",
            wordBreakdown = "Chetta (Elder brother) + Adoor KSRTC stand-ilekku (to Adoor bus stand) + varumo? (will you come?)",
            acceptedVariants = listOf(
                "chetta adoor ksrtc standilekku varumo",
                "adoor ksrtc standilekku varumo"
            ),
            tags = listOf("travel", "auto", "adoor")
        ),
        PhraseItem(
            id = "ct_16",
            manglish = "Meter idumo, atho fixed rate aano?",
            malayalam = "മീറ്റർ ഇടുമോ, അതോ ഫിക്സഡ് റേറ്റ് ആണോ?",
            english = "Will you put the meter on, or is it a fixed rate?",
            regionalNuance = "Essential phrase when hiring an auto in Central Travancore towns.",
            textbookAlternative = "Doora maapanam upayogikkumo?",
            category = "Travel & Auto",
            levelNumber = 6,
            levelBadge = "Intermediate Level 1",
            phoneticGuide = "MEE-tur IH-doo-moh, UH-thoh FIXED RATE AH-noh?",
            wordBreakdown = "Meter idumo (Will you turn on the meter) + atho (or) + fixed rate aano? (is it a fixed rate?)",
            acceptedVariants = listOf(
                "meter idumo atho fixed rate aano",
                "meter idumo"
            ),
            tags = listOf("travel", "auto")
        ),
        PhraseItem(
            id = "ct_17",
            manglish = "Pathanamthitta fast passenger vannittundo?",
            malayalam = "പത്തനംതിട്ട ഫാസ്റ്റ് പാസഞ്ചർ വന്നിട്ടുണ്ടോ?",
            english = "Has the Pathanamthitta Fast Passenger bus arrived yet?",
            regionalNuance = "'Fast passenger' refers to the KSRTC bus route running through Central Travancore.",
            textbookAlternative = "Vaahanam ethiyittundo?",
            category = "Travel & Auto",
            levelNumber = 6,
            levelBadge = "Intermediate Level 1",
            phoneticGuide = "puh-thuh-num-THIT-tuh FAST PASSENGER vun-nit-TOON-doh?",
            wordBreakdown = "Pathanamthitta fast passenger (the bus) + vannittundo? (has it arrived?)",
            prerequisiteIds = listOf("l0_06"),
            acceptedVariants = listOf(
                "pathanamthitta fast passenger vannittundo",
                "fast passenger vannittundo"
            ),
            tags = listOf("travel", "bus")
        ),

        // =========================================================================
        // STAGE 6 (CONTINUED): INTERMEDIATE LEVEL 3 — Catholic Church, Holy Qurbana & Prayers
        // =========================================================================
        PhraseItem(
            id = "cc_01",
            manglish = "Njaan palliye pokuvaa",
            malayalam = "ഞാൻ പള്ളിയിൽ പോകുവാ",
            english = "I am going to church.",
            regionalNuance = "Everyday household phrase on Sunday mornings in Central Travancore. 'Palliye' is the colloquial spoken form of 'palliyil'.",
            textbookAlternative = "Njan palliyilekku pokunnu",
            category = "Catholic & Church",
            levelNumber = 6,
            levelBadge = "Intermediate Level 3 • Catholic & Church",
            phoneticGuide = "NYAAN PUL-li-ye POH-koo-vah",
            wordBreakdown = "Njaan (I) + palliye (to church) + pokuvaa (going)",
            prerequisiteIds = listOf("l0_01"),
            acceptedVariants = listOf("njaan palliye pokuvaa", "njan palliye pokuva", "njaan palliyil pokuvaa"),
            tags = listOf("church", "catholic", "sunday")
        ),
        PhraseItem(
            id = "cc_02",
            manglish = "Njaayarazhcha Holy Qurbana koodanam",
            malayalam = "ഞായറാഴ്ച കുർബാന കൂടണം",
            english = "We must attend Sunday Holy Mass (Qurbana).",
            regionalNuance = "'Qurbana kooduka' is the authentic Malayalam idiom meaning to participate in Holy Mass.",
            textbookAlternative = "Njaayarazhcha vishuddha Qurbana anubhadhikkanam",
            category = "Catholic & Church",
            levelNumber = 6,
            levelBadge = "Intermediate Level 3 • Catholic & Church",
            phoneticGuide = "NYAH-yur-AHZH-chuh HO-lee koor-BAH-nah KOO-duh-num",
            wordBreakdown = "Njaayarazhcha (Sunday) + Holy Qurbana (Holy Mass) + koodanam (must attend)",
            acceptedVariants = listOf("njaayarazhcha holy qurbana koodanam", "holy qurbana koodanam", "qurbana koodanam"),
            tags = listOf("church", "catholic", "mass")
        ),
        PhraseItem(
            id = "cc_03",
            manglish = "Sandhya prarthana kazhinjo?",
            malayalam = "സന്ധ്യാ പ്രാർത്ഥന കഴിഞ്ഞോ?",
            english = "Is the evening family prayer finished?",
            regionalNuance = "Daily evening rosary and family prayer ('sandhya prarthana') is a cherished staple of Central Travancore Christian homes.",
            textbookAlternative = "Sandhya prarthana theernno?",
            category = "Catholic & Church",
            levelNumber = 6,
            levelBadge = "Intermediate Level 3 • Catholic & Church",
            phoneticGuide = "SUN-dhyah PRAHR-thuh-nuh kuh-ZHIN-yoh?",
            wordBreakdown = "Sandhya (Evening) + prarthana (prayer) + kazhinjo? (is it finished?)",
            prerequisiteIds = listOf("l0_06"),
            acceptedVariants = listOf("sandhya prarthana kazhinjo", "prarthana kazhinjo", "prarthana"),
            tags = listOf("church", "catholic", "prayer")
        ),
        PhraseItem(
            id = "cc_04",
            manglish = "Palli achan veettil vannu",
            malayalam = "പള്ളി അച്ചൻ വീട്ടിൽ വന്നു",
            english = "The parish priest visited our home.",
            regionalNuance = "'Achan' is both the formal title for priest/father (Reverend) and the warm community title used across parish families.",
            textbookAlternative = "Idavaka vikaari bhavanathil ethi",
            category = "Catholic & Church",
            levelNumber = 6,
            levelBadge = "Intermediate Level 3 • Catholic & Church",
            phoneticGuide = "PUL-li UCH-un VEET-til VUN-noo",
            wordBreakdown = "Palli achan (Parish priest) + veettil (at home) + vannu (came)",
            acceptedVariants = listOf("palli achan veettil vannu", "achan veettil vannu", "palli achan"),
            tags = listOf("church", "catholic", "priest")
        ),
        PhraseItem(
            id = "cc_05",
            manglish = "Nanma niranja Mariyame, ninakku sthuthi",
            malayalam = "നന്മ നിറഞ്ഞ മറിയമേ, നിനക്ക് സ്തുതി",
            english = "Hail Mary, full of grace (Praise be to thee).",
            regionalNuance = "The opening line of the Hail Mary recited in Malayalam in Catholic rosaries and church services worldwide.",
            textbookAlternative = "Nanma niranja Mariyame, krupayaal niranja avale",
            category = "Catholic & Church",
            levelNumber = 6,
            levelBadge = "Intermediate Level 3 • Catholic & Church",
            phoneticGuide = "NUN-muh ni-RUN-juh muh-ree-YUM-ey, ni-NUK-koo STHOO-thee",
            wordBreakdown = "Nanma niranja (Full of goodness/grace) + Mariyame (Mary) + ninakku sthuthi (praise to you)",
            acceptedVariants = listOf("nanma niranja mariyame ninakku sthuthi", "nanma niranja mariyame", "mariyame ninakku sthuthi"),
            tags = listOf("prayer", "catholic", "hailmary")
        ),
        PhraseItem(
            id = "cc_06",
            manglish = "Swargasthanaaya ഞങ്ങളുടെ Pithave",
            malayalam = "സ്വർഗ്ഗസ്ഥനായ ഞങ്ങളുടെ പിതാവേ",
            english = "Our Father who art in heaven.",
            regionalNuance = "The venerated opening of the Lord's Prayer (Our Father) learned by heart by every Malayalam-speaking Catholic child.",
            textbookAlternative = "Swargasthanaaya njangalude pithave",
            category = "Catholic & Church",
            levelNumber = 6,
            levelBadge = "Intermediate Level 3 • Catholic & Church",
            phoneticGuide = "SWUR-gus-thuh-nah-yuh NYUNG-ngul-oo-dey pi-THAH-vey",
            wordBreakdown = "Swargasthanaaya (Who art in heaven) + njangalude (our) + Pithave (Father)",
            prerequisiteIds = listOf("l0_01"),
            acceptedVariants = listOf("swargasthanaaya njangalude pithave", "swargasthanaya pithave", "swargasthanaaya pithave"),
            tags = listOf("prayer", "catholic", "ourfather")
        ),
        PhraseItem(
            id = "cc_07",
            manglish = "Dhaivathinu sthuthi!",
            malayalam = "ദൈവത്തിന് സ്തുതി!",
            english = "Praise be to God! / Thanks be to God!",
            regionalNuance = "Spoken reflexively upon hearing good news, surviving hardship, or finishing a task. One of the most universal expressions.",
            textbookAlternative = "Eashwaranu nanni",
            category = "Catholic & Church",
            levelNumber = 6,
            levelBadge = "Intermediate Level 3 • Catholic & Church",
            phoneticGuide = "DHY-vuh-THIN-oo STHOO-thee!",
            wordBreakdown = "Dhaivathinu (To God) + sthuthi (Praise / Glory)",
            acceptedVariants = listOf("dhaivathinu sthuthi", "daivathinu sthuthi", "deivathinu sthuthi"),
            tags = listOf("blessing", "catholic", "daily")
        ),
        PhraseItem(
            id = "cc_08",
            manglish = "Karthavu anugrahikkatte",
            malayalam = "കർത്താവ് അനുഗ്രഹിക്കട്ടെ",
            english = "May the Lord bless you.",
            regionalNuance = "A profound blessing bestowed by Catholic grandparents (Ammachi/Appachan) and parish priests on birthdays, journeys, and farewells.",
            textbookAlternative = "Daivam ninne anugrahikkatte",
            category = "Catholic & Church",
            levelNumber = 6,
            levelBadge = "Intermediate Level 3 • Catholic & Church",
            phoneticGuide = "KUR-thah-voo uh-NOO-gruh-hik-KUT-tey",
            wordBreakdown = "Karthavu (The Lord) + anugrahikkatte (May [He] bless)",
            acceptedVariants = listOf("karthavu anugrahikkatte", "karthavu anugrahikate", "karthav anugrahikkatte"),
            tags = listOf("blessing", "catholic", "elders")
        ),
        PhraseItem(
            id = "cc_09",
            manglish = "Prarthikkum munpu kurishu varakkoo",
            malayalam = "പ്രാർത്ഥിക്കും മുൻപ് കുരിശ് വരയ്ക്കൂ",
            english = "Make the sign of the cross before praying.",
            regionalNuance = "'Kurishu varakkuka' (drawing the cross on forehead and chest) is the sacred Catholic gesture before meals and prayer.",
            textbookAlternative = "Prarthanaykku munpaayi kurishu adayalam varakkuka",
            category = "Catholic & Church",
            levelNumber = 6,
            levelBadge = "Intermediate Level 3 • Catholic & Church",
            phoneticGuide = "PRAHR-thik-koom MOON-poo KOO-ri-shoo vuh-RUK-koo",
            wordBreakdown = "Prarthikkum munpu (Before praying) + kurishu varakkoo (make sign of the cross)",
            acceptedVariants = listOf("prarthikkum munpu kurishu varakkoo", "kurishu varakkoo", "kurishu varakkuka"),
            tags = listOf("catholic", "prayer", "gesture")
        ),
        PhraseItem(
            id = "cc_10",
            manglish = "Palli perunnal koodan pokuvaa",
            malayalam = "പള്ളി പെരുന്നാൾ കൂടാൻ പോകുവാ",
            english = "Going to celebrate the church feast (Perunnal).",
            regionalNuance = "'Perunnal' is the vibrant annual patron saint festival celebrated across Central Travancore parishes with processions and community feasts.",
            textbookAlternative = "Devalaya perunnalil pangedukkan pokunnu",
            category = "Catholic & Church",
            levelNumber = 6,
            levelBadge = "Intermediate Level 3 • Catholic & Church",
            phoneticGuide = "PUL-li pey-ROON-nahl KOO-dahn POH-koo-vah",
            wordBreakdown = "Palli perunnal (Church feast) + koodan (to participate) + pokuvaa (going)",
            acceptedVariants = listOf("palli perunnal koodan pokuvaa", "palli perunnal", "perunnal koodan pokuvaa"),
            tags = listOf("catholic", "festival", "church")
        ),

        // =========================================================================
        // STAGE 7: ADVANCED LEVEL 1 — Storytelling, Past Memories & Deep Feelings
        // =========================================================================
        PhraseItem(
            id = "adv_01",
            manglish = "Njan oru karyam parayatte?",
            malayalam = "ഞാൻ ഒരു കാര്യം പറയട്ടെ?",
            english = "Can I tell you something?",
            regionalNuance = "The classic, gentle conversational opening in Malayalam when confiding an important thought or beginning a personal story.",
            textbookAlternative = "Njan oru vishayam parayatte?",
            category = "Advanced: Storytelling",
            levelNumber = 7,
            levelBadge = "Advanced Level 1 • Storytelling & Feelings",
            phoneticGuide = "NYAHN OH-roo KAH-ryum puh-RUY-yut-tey?",
            wordBreakdown = "Njan (I) + oru karyam (one matter/thing) + parayatte? (may I say?)",
            prerequisiteIds = listOf("l0_01"),
            acceptedVariants = listOf("njan oru karyam parayatte", "njaan oru karyam parayatte"),
            tags = listOf("advanced", "storytelling", "opener")
        ),
        PhraseItem(
            id = "adv_02",
            manglish = "Athoru nalla kalam aayirunnu",
            malayalam = "അതൊരു നല്ല കാലം ആയിരുന്നു",
            english = "That was such a fond, good time.",
            regionalNuance = "Used when reminiscing with family members about childhood, old parish festivals, or memories in Kerala.",
            textbookAlternative = "Athu oru nalla kaalaghattamaayirunnu",
            category = "Advanced: Storytelling",
            levelNumber = 7,
            levelBadge = "Advanced Level 1 • Storytelling & Feelings",
            phoneticGuide = "UH-tho-roo NUL-luh KAH-lum AH-yi-roon-noo",
            wordBreakdown = "Athu (That) + oru nalla kalam (a good time/era) + aayirunnu (was)",
            acceptedVariants = listOf("athoru nalla kalam aayirunnu", "athoru nalla kaalam aayirunnu"),
            tags = listOf("advanced", "nostalgia", "memories")
        ),
        PhraseItem(
            id = "adv_03",
            manglish = "Enikku vallatha sankadam thonni",
            malayalam = "എനിക്ക് വല്ലാത്ത സങ്കടം തോന്നി",
            english = "I felt really heartbroken / deeply emotional.",
            regionalNuance = "Expresses genuine vulnerability and deep sorrow. 'Vallatha' emphasizes the emotional depth beyond ordinary words.",
            textbookAlternative = "Enikku valare dukhamaayi",
            category = "Advanced: Storytelling",
            levelNumber = 7,
            levelBadge = "Advanced Level 1 • Storytelling & Feelings",
            phoneticGuide = "eh-NIK-koo vuhl-LAHTH-thuh SUN-kuh-dum THOHN-nee",
            wordBreakdown = "Enikku (To me) + vallatha (immense/overwhelming) + sankadam (sorrow) + thonni (felt)",
            acceptedVariants = listOf("enikku vallatha sankadam thonni", "vallatha sankadam thonni"),
            tags = listOf("advanced", "feelings", "emotion")
        ),
        PhraseItem(
            id = "adv_04",
            manglish = "Njan athe patti alochikkuvaayirunnu",
            malayalam = "ഞാൻ അതെ പറ്റി ആലോചിക്കുകയായിരുന്നു",
            english = "I was deeply reflecting on that matter.",
            regionalNuance = "Shows sophisticated inner reflection and deliberate consideration in conversation.",
            textbookAlternative = "Njan athinekkurichu chinthikkukayaayirunnu",
            category = "Advanced: Storytelling",
            levelNumber = 7,
            levelBadge = "Advanced Level 1 • Storytelling & Feelings",
            phoneticGuide = "NYAHN UH-they PUT-ti ah-LOH-chik-koo-vah-yi-roon-noo",
            wordBreakdown = "Njan (I) + athe patti (about that) + alochikkuvaayirunnu (was thinking/reflecting)",
            acceptedVariants = listOf("njan athe patti alochikkuvaayirunnu", "athe patti alochikkukayaayirunnu"),
            tags = listOf("advanced", "reflection")
        ),
        PhraseItem(
            id = "adv_05",
            manglish = "Athinu shesham njan veettilekku poyi",
            malayalam = "അതിന് ശേഷം ഞാൻ വീട്ടിലേക്ക് പോയി",
            english = "After that happened, I went straight home.",
            regionalNuance = "Sequential narrative linker ('athinu shesham' = after that) essential for telling past stories clearly.",
            textbookAlternative = "Athinu pinneedu njan veettilekku thirichu poyi",
            category = "Advanced: Storytelling",
            levelNumber = 7,
            levelBadge = "Advanced Level 1 • Storytelling & Feelings",
            phoneticGuide = "UH-thin-oo SHEY-shum NYAHN VEET-ti-ley-koo POH-yee",
            wordBreakdown = "Athinu shesham (After that) + njan (I) + veettilekku (to home) + poyi (went)",
            acceptedVariants = listOf("athinu shesham njan veettilekku poyi", "athinu shesham njan poyi"),
            tags = listOf("advanced", "narrative", "sequencing")
        ),
        PhraseItem(
            id = "adv_06",
            manglish = "Ningal paranjathu njan eppozhum orkkunnu",
            malayalam = "നിങ്ങൾ പറഞ്ഞത് ഞാൻ എപ്പോഴും ഓർക്കുന്നു",
            english = "I always remember what you told me.",
            regionalNuance = "A heartfelt statement acknowledging advice or wisdom shared by an elder or respected friend.",
            textbookAlternative = "Ningal upadeshichathu njan ormikkunnu",
            category = "Advanced: Storytelling",
            levelNumber = 7,
            levelBadge = "Advanced Level 1 • Storytelling & Feelings",
            phoneticGuide = "NING-ngul puh-RUN-juh-thoo NYAHN EP-poh-zhum ORK-koon-noo",
            wordBreakdown = "Ningal paranjathu (What you said) + njan (I) + eppozhum (always) + orkkunnu (remember)",
            acceptedVariants = listOf("ningal paranjathu njan eppozhum orkkunnu", "paranjathu njan orkkunnu"),
            tags = listOf("advanced", "respect", "wisdom")
        ),

        // =========================================================================
        // STAGE 8: ADVANCED LEVEL 2 — Opinions, Nuanced Reasoning & Polite Debate
        // =========================================================================
        PhraseItem(
            id = "adv_07",
            manglish = "Ente abhiprayathil athu shariyalla",
            malayalam = "എന്റെ അഭിപ്രായത്തിൽ അത് ശരിയല്ല",
            english = "In my opinion, that is not right.",
            regionalNuance = "How educated speakers respectfully voice disagreement without being confrontational or rude.",
            textbookAlternative = "Ente kaazhchapaadil athu thettaanu",
            category = "Advanced: Opinions",
            levelNumber = 8,
            levelBadge = "Advanced Level 2 • Opinions & Polite Debate",
            phoneticGuide = "EN-tey uh-bhi-PRAH-yuh-thil UH-thoo shuh-ri-YUL-lah",
            wordBreakdown = "Ente abhiprayathil (In my opinion) + athu (that) + shariyalla (is not right)",
            acceptedVariants = listOf("ente abhiprayathil athu shariyalla", "abhiprayathil athu shariyalla"),
            tags = listOf("advanced", "debate", "opinion")
        ),
        PhraseItem(
            id = "adv_08",
            manglish = "Ningal paranjathu shariyaanu, ennalum...",
            malayalam = "നിങ്ങൾ പറഞ്ഞത് ശരിയാണ്, എന്നാലും...",
            english = "What you said is true, nevertheless...",
            regionalNuance = "The diplomat's tool: concedes the other party's point gracefully before introducing another perspective.",
            textbookAlternative = "Aparante vaadam sathyamaanu, engilum...",
            category = "Advanced: Opinions",
            levelNumber = 8,
            levelBadge = "Advanced Level 2 • Opinions & Polite Debate",
            phoneticGuide = "NING-ngul puh-RUN-juh-thoo shuh-ri-YAH-noo, EN-nah-loom...",
            wordBreakdown = "Ningal paranjathu (What you said) + shariyaanu (is true) + ennalum (nevertheless...)",
            acceptedVariants = listOf("ningal paranjathu shariyaanu ennalum", "ningal paranjathu shariyanu ennalum"),
            tags = listOf("advanced", "nuance", "debate")
        ),
        PhraseItem(
            id = "adv_09",
            manglish = "Namukku vere oru nalla vazhi nokkam",
            malayalam = "നമുക്ക് വേറെ ഒരു നല്ല വഴി നോക്കാം",
            english = "Let us look for a better alternative way.",
            regionalNuance = "Constructive problem-solving phrase that unifies the group towards a shared solution.",
            textbookAlternative = "Nammalkku matroru margam anveshikkaam",
            category = "Advanced: Opinions",
            levelNumber = 8,
            levelBadge = "Advanced Level 2 • Opinions & Polite Debate",
            phoneticGuide = "nuh-MOOK-koo VEY-rey OH-roo NUL-luh VUH-zhi NOHK-kahm",
            wordBreakdown = "Namukku (For us) + vere oru (another) + nalla vazhi (good way/path) + nokkam (let's look)",
            acceptedVariants = listOf("namukku vere oru nalla vazhi nokkam", "namukku vere vazhi nokkam"),
            tags = listOf("advanced", "solutions", "cooperation")
        ),
        PhraseItem(
            id = "adv_10",
            manglish = "Athukondu thanneyaanu njan angane cheythathu",
            malayalam = "അതുകൊണ്ട് തന്നെയാണ് ഞാൻ അങ്ങനെ ചെയ്തത്",
            english = "That is the exact reason why I did that.",
            regionalNuance = "'Athukondu thanneyaanu' pinpoints strict causal logic when defending your actions.",
            textbookAlternative = "Aakaaranathaal thanneyaanu njan pravarthichathu",
            category = "Advanced: Opinions",
            levelNumber = 8,
            levelBadge = "Advanced Level 2 • Opinions & Polite Debate",
            phoneticGuide = "UH-thoo-kon-doo THUN-ney-ah-noo NYAHN UNG-ngun-ey CHEY-thuh-thoo",
            wordBreakdown = "Athukondu thanneyaanu (For that exact reason) + njan (I) + angane (like that) + cheythathu (did)",
            acceptedVariants = listOf("athukondu thanneyaanu njan angane cheythathu", "athukondu thanne njan cheythathu"),
            tags = listOf("advanced", "reasoning", "logic")
        ),
        PhraseItem(
            id = "adv_11",
            manglish = "Avarodu chodikkunnathaanu nallathu",
            malayalam = "അവരോട് ചോദിക്കുന്നതാണ് നല്ലത്",
            english = "It is wiser to consult with them first.",
            regionalNuance = "Recommends deference and consultation with elders or stakeholders in the community.",
            textbookAlternative = "Avarodu aalochikkunnathaanu uchitham",
            category = "Advanced: Opinions",
            levelNumber = 8,
            levelBadge = "Advanced Level 2 • Opinions & Polite Debate",
            phoneticGuide = "uh-vuh-ROH-doo choh-DIK-koon-nuh-thah-noo NUL-luh-thoo",
            wordBreakdown = "Avarodu (With them) + chodikkunnathaanu (asking is) + nallathu (good/wise)",
            acceptedVariants = listOf("avarodu chodikkunnathaanu nallathu", "avarodu chodikkunnatha nallathu"),
            tags = listOf("advanced", "wisdom", "counsel")
        ),
        PhraseItem(
            id = "adv_12",
            manglish = "Ithu nammalude samayathinu upakaarapradhamaanu",
            malayalam = "ഇത് നമ്മുടെ സമയത്തിന് ഉപകാരപ്രദമാണ്",
            english = "This is beneficial for our time and effort.",
            regionalNuance = "High-register reasoning expressing practical utility and collective efficiency.",
            textbookAlternative = "Ithu kaalathinu anuyojyamaanu",
            category = "Advanced: Opinions",
            levelNumber = 8,
            levelBadge = "Advanced Level 2 • Opinions & Polite Debate",
            phoneticGuide = "IH-thoo num-mul-oo-dey SUH-muh-yuh-thin-oo oo-puh-KAH-ruh-pruh-dhuh-mah-noo",
            wordBreakdown = "Ithu (This) + nammalude samayathinu (for our time) + upakaarapradhamaanu (is useful/beneficial)",
            acceptedVariants = listOf("ithu nammalude samayathinu upakaarapradhamaanu", "upakaarapradhamaanu"),
            tags = listOf("advanced", "formal", "reasoning")
        ),

        // =========================================================================
        // STAGE 9: SUPERIOR LEVEL 1 — Near-Native Banter, Regional Slang & Wit
        // =========================================================================
        PhraseItem(
            id = "ct_06",
            manglish = "Enthuvaadey ithu? Ithre neram aayo?",
            malayalam = "എന്തുവാടേ ഇത്? ഇത്രേ നേരമായോ?",
            english = "What on earth is this, man? Is it already this late?",
            regionalNuance = "'Enthuvaadey' is the quintessential Central Travancore exclamation of surprise or disbelief.",
            textbookAlternative = "Enthanu ithu? Ithramathram samayamayo?",
            category = "Superior: Banter",
            levelNumber = 9,
            levelBadge = "Superior Level 1 • Near-Native Banter",
            phoneticGuide = "en-thoo-VAH-dey IH-thoo? ITH-rey NEY-rum AH-yoh?",
            wordBreakdown = "Enthuvaadey (What is this, bro) + ithu (this)? + Ithre neram (this much time) + aayo? (has it become?)",
            prerequisiteIds = listOf("l0_09"),
            acceptedVariants = listOf("enthuvaadey ithu ithre neram aayo", "enthuvaadey ithu", "enthuvadey ithu"),
            tags = listOf("slang", "adoor", "banter")
        ),
        PhraseItem(
            id = "ct_07",
            manglish = "Ennattu entha undaaye?",
            malayalam = "എന്നിട്ട് എന്താ ഉണ്ടായേ?",
            english = "So then what happened?",
            regionalNuance = "'Ennattu' / 'ennittu' acts as a conversational springboard to keep the story rolling smoothly.",
            textbookAlternative = "Athinu shesham enthu sambhavichu?",
            category = "Superior: Banter",
            levelNumber = 9,
            levelBadge = "Superior Level 1 • Near-Native Banter",
            phoneticGuide = "EN-nut-too EN-thah oon-DAH-yey?",
            wordBreakdown = "Ennattu (And then) + entha (what) + undaaye? (happened?)",
            prerequisiteIds = listOf("l0_09"),
            acceptedVariants = listOf("ennattu entha undaaye", "ennittu entha undaye", "ennittu entha undaaye"),
            tags = listOf("filler", "conversation")
        ),
        PhraseItem(
            id = "ct_18",
            manglish = "Enthuvaadey ithre naalaayi kandittu! Evidaayirunnu?",
            malayalam = "എന്തുവാടേ ഇത്രേ നാളായി കണ്ടിട്ട്! എവിടെയായിരുന്നു?",
            english = "What's up, man! Haven't seen you in ages! Where were you?",
            regionalNuance = "Warm reunion greeting among close friends in Adoor.",
            textbookAlternative = "Kure naalukalkku shesham kaanunnu. Enge aayirunnu?",
            category = "Superior: Banter",
            levelNumber = 9,
            levelBadge = "Superior Level 1 • Near-Native Banter",
            phoneticGuide = "en-thoo-VAH-dey ITH-rey NAH-lah-yee KUN-dit-too! eh-vi-DAH-yi-roon-noo?",
            wordBreakdown = "Enthuvaadey (What's up man) + ithre naalaayi (so many days) + kandittu (since seeing)! + Evidaayirunnu? (Where were you?)",
            prerequisiteIds = listOf("l0_10"),
            acceptedVariants = listOf("enthuvaadey ithre naalaayi kandittu evidaayirunnu", "evidaayirunnu"),
            tags = listOf("greeting", "banter")
        ),
        PhraseItem(
            id = "ct_19",
            manglish = "Scene illa, njaan manage cheythoalaam",
            malayalam = "സീൻ ഇല്ല, ഞാൻ മാനേജ് ചെയ്തോളാം",
            english = "No problem at all, I will manage it.",
            regionalNuance = "'Scene illa' is the universally popular youth Manglish phrase meaning 'no stress / no problem'.",
            textbookAlternative = "Prashnamilla, njan kariyangal nokkikkollam",
            category = "Superior: Banter",
            levelNumber = 9,
            levelBadge = "Superior Level 1 • Near-Native Banter",
            phoneticGuide = "SEEN IL-lah, NYAAN MANAGE chey-THOH-laam",
            wordBreakdown = "Scene illa (No problem) + njaan (I) + manage cheythoalaam (will manage it myself)",
            prerequisiteIds = listOf("l2_06", "l0_01"),
            acceptedVariants = listOf("scene illa njaan manage cheythoalaam", "scene illa njan manage cheytholam", "scene illa"),
            tags = listOf("manglish", "youth", "slang")
        ),
        PhraseItem(
            id = "ct_20",
            manglish = "Entha vishesham? Sugamaano?",
            malayalam = "എന്താ വിശേഷം? സുഖമാണോ?",
            english = "What's new? Are you doing well?",
            regionalNuance = "Natural everyday check-in phrase. Punchy, friendly, non-pretentious.",
            textbookAlternative = "Kshemam aano? Enthaanu kshema samacharana?",
            category = "Superior: Banter",
            levelNumber = 9,
            levelBadge = "Superior Level 1 • Near-Native Banter",
            phoneticGuide = "EN-thah vi-SHEY-shum? SOO-guh-mah-noh?",
            wordBreakdown = "Entha (What) + vishesham (news)? + Sugamaano? (Are you well?)",
            prerequisiteIds = listOf("l0_09", "l1_09"),
            acceptedVariants = listOf("entha vishesham sugamaano", "entha vishesham", "sugamaano"),
            tags = listOf("greeting", "daily")
        ),
        PhraseItem(
            id = "sup_01",
            manglish = "Avan oru valiya pulliyaanennu vicharichu!",
            malayalam = "അവൻ ഒരു വലിയ പുള്ളിയാണെന്ന് വിചാരിച്ചു!",
            english = "He thought he was some big hotshot!",
            regionalNuance = "'Pulli' is the affectionate colloquial Malayalam slang for a person or character, often used playfully.",
            textbookAlternative = "Avan oru pradhaanappetta aalaanennu dharichu",
            category = "Superior: Banter",
            levelNumber = 9,
            levelBadge = "Superior Level 1 • Near-Native Banter",
            phoneticGuide = "UH-vun OH-roo VUL-i-yuh PUL-li-yah-nen-noo vi-chuh-RI-choo!",
            wordBreakdown = "Avan (He) + oru valiya pulliyaanennu (that he is a big shot) + vicharichu (thought)",
            acceptedVariants = listOf("avan oru valiya pulliyaanennu vicharichu", "valiya pulli"),
            tags = listOf("slang", "humor", "superior")
        ),
        PhraseItem(
            id = "sup_02",
            manglish = "Kaaryam paranjaal kettoodey ninakku?",
            malayalam = "കാര്യം പറഞ്ഞാൽ കേട്ടൂടെ നിനക്ക്?",
            english = "Can't you listen when someone explains something to you?",
            regionalNuance = "Affectionate scolded banter among family members in Central Travancore. Ending in '-oodey' gives it local warmth.",
            textbookAlternative = "Parayunnathu shradhikkande?",
            category = "Superior: Banter",
            levelNumber = 9,
            levelBadge = "Superior Level 1 • Near-Native Banter",
            phoneticGuide = "KAH-ryum puh-RUN-jahl ket-TOO-dey ni-NUK-koo?",
            wordBreakdown = "Kaaryam paranjaal (When matter is explained) + kettoodey (can't you listen) + ninakku? (to you?)",
            acceptedVariants = listOf("kaaryam paranjaal kettoodey ninakku", "karyam paranjal kettoode"),
            tags = listOf("superior", "banter", "family")
        ),
        PhraseItem(
            id = "sup_03",
            manglish = "Nammalude naattile visheshangal paranjal theeroolla",
            malayalam = "നമ്മുടെ നാട്ടിലെ വിശേഷങ്ങൾ പറഞ്ഞാൽ തീരൂല്ല",
            english = "If we start discussing our hometown news, it will never end!",
            regionalNuance = "Said when expatriates (Pravasis) and locals catch up on home district happenings. Features Adoor '-oolla' contraction.",
            textbookAlternative = "Nammude naattile vartakkal theerukayilla",
            category = "Superior: Banter",
            levelNumber = 9,
            levelBadge = "Superior Level 1 • Near-Native Banter",
            phoneticGuide = "num-mul-oo-dey NAHT-ti-ley vi-SHEY-shung-ngul puh-RUN-jahl theey-ROO-lah",
            wordBreakdown = "Nammalude naattile (In our hometown) + visheshangal (news/stories) + paranjal (if told) + theeroolla (will never end)",
            acceptedVariants = listOf("nammalude naattile visheshangal paranjal theeroolla", "paranjal theeroolla"),
            tags = listOf("superior", "culture", "pravasi")
        ),

        // =========================================================================
        // STAGE 10: SUPERIOR LEVEL 2 — Famous Malayalam Proverbs (Pazhamchollukal) & Idioms
        // =========================================================================
        PhraseItem(
            id = "prv_01",
            manglish = "Minnumathellam ponnalla",
            malayalam = "മിന്നുന്നതെല്ലാം പൊന്നല്ല",
            english = "All that glitters is not gold.",
            regionalNuance = "The most widely referenced Malayalam proverb warning that flashy outer appearances do not equal genuine value.",
            textbookAlternative = "Prakaashikkunnathellam swarnamalla",
            category = "Superior: Proverbs",
            levelNumber = 10,
            levelBadge = "Superior Level 2 • Proverbs & Idioms",
            phoneticGuide = "MIN-noo-nuh-thel-lum PON-nul-lah",
            wordBreakdown = "Minnumathellam (All that sparkles) + ponnalla (is not gold)",
            acceptedVariants = listOf("minnumathellam ponnalla", "minnunnath ellam ponnalla"),
            tags = listOf("proverb", "superior", "wisdom")
        ),
        PhraseItem(
            id = "prv_02",
            manglish = "Velichathu kaanumbozhalle niram ariyoo",
            malayalam = "വെളിച്ചത്ത് കാണുമ്പോഴല്ലേ നിറം അറിയൂ",
            english = "Only when brought into the light is the true character revealed.",
            regionalNuance = "A shrewd Travancore proverb about testing ideas, people, and cloth in direct sunlight before judging them.",
            textbookAlternative = "Velichathil vekkumbozhe sathyavastha ariyoo",
            category = "Superior: Proverbs",
            levelNumber = 10,
            levelBadge = "Superior Level 2 • Proverbs & Idioms",
            phoneticGuide = "veh-LI-chuh-thoo kah-noom-BOH-zhul-ley ni-rum uh-ri-YOO",
            wordBreakdown = "Velichathu kaanumbozhalle (Only when seen in light) + niram ariyoo (is color known)",
            acceptedVariants = listOf("velichathu kaanumbozhalle niram ariyoo", "velichath kaanumbozhalle"),
            tags = listOf("proverb", "superior", "insight")
        ),
        PhraseItem(
            id = "prv_03",
            manglish = "Oruma undenkil ulaka jeyikkam",
            malayalam = "ഒത്തൊരുമ ഉണ്ടെങ്കിൽ ഉലകം ജയിക്കാം",
            english = "With unity, we can conquer any challenge in the world.",
            regionalNuance = "Celebrates the power of family solidarity and parish parishioner unity working toward a shared goal.",
            textbookAlternative = "Aikyamaanu mahabalam",
            category = "Superior: Proverbs",
            levelNumber = 10,
            levelBadge = "Superior Level 2 • Proverbs & Idioms",
            phoneticGuide = "OH-roo-muh OON-deng-kil oo-luh-kuh jey-IK-kum",
            wordBreakdown = "Oruma (Unity/togetherness) + undenkil (if there is) + ulaka (the world) + jeyikkam (can conquer)",
            acceptedVariants = listOf("oruma undenkil ulaka jeyikkam", "otthoruma undenkil ulakam jeyikkam"),
            tags = listOf("proverb", "superior", "unity")
        ),
        PhraseItem(
            id = "prv_04",
            manglish = "Kaakkakkum thankunju ponkunju",
            malayalam = "കാക്കയ്ക്കും തൻകുഞ്ഞ് പൊൻകുഞ്ഞ്",
            english = "Even to a crow, its own child is pure gold.",
            regionalNuance = "Every parent views their own child with unconditional pride, no matter what others think.",
            textbookAlternative = "Ellaa maathaapithakkalkkum thammude makkal shreshtaraan",
            category = "Superior: Proverbs",
            levelNumber = 10,
            levelBadge = "Superior Level 2 • Proverbs & Idioms",
            phoneticGuide = "KAHK-kahk-koom thun-KOON-joo pon-KOON-joo",
            wordBreakdown = "Kaakkakkum (Even to a crow) + thankunju (its own young) + ponkunju (is a golden child)",
            acceptedVariants = listOf("kaakkakkum thankunju ponkunju", "kaakkakkum than kunju ponkunju"),
            tags = listOf("proverb", "superior", "family")
        ),
        PhraseItem(
            id = "prv_05",
            manglish = "Athiru kaathaal aashanka illa",
            malayalam = "അതിര് കാത്താൽ ആശങ്ക ഇല്ല",
            english = "When proper boundaries are maintained, there is never worry.",
            regionalNuance = "A classic land, relationship, and etiquette proverb of Central Travancore farming and family life.",
            textbookAlternative = "Athirollidathu prashnamilla",
            category = "Superior: Proverbs",
            levelNumber = 10,
            levelBadge = "Superior Level 2 • Proverbs & Idioms",
            phoneticGuide = "UH-thi-roo KAH-thahl ah-SHUN-kuh IL-lah",
            wordBreakdown = "Athiru (Boundary) + kaathaal (if protected/kept) + aashanka (worry) + illa (none)",
            acceptedVariants = listOf("athiru kaathaal aashanka illa", "athir kaathal aashanka illa"),
            tags = listOf("proverb", "superior", "boundaries")
        ),

        // =========================================================================
        // STAGE 11: DISTINGUISHED — Cultural Orator & Community Eloquence
        // =========================================================================
        PhraseItem(
            id = "dst_01",
            manglish = "Athithi daivam aayi karuthunna naadaanu nammudethu",
            malayalam = "അതിഥി ദൈവമായി കരുതുന്ന നാടാണ് നമ്മുടേത്",
            english = "Ours is a land that welcomes every guest with divine reverence.",
            regionalNuance = "Reflects the quintessential Kerala standard of hospitality ('Athithi Devo Bhava') spoken in community welcomes.",
            textbookAlternative = "Nammude naadu virunhukaare eashwaranaayi snehikkunnu",
            category = "Distinguished: Master",
            levelNumber = 11,
            levelBadge = "Distinguished • Cultural Master",
            phoneticGuide = "uh-THI-thi DHY-vum AH-yee kuh-ROO-thoon-nuh NAH-dah-noo num-moo-DEY-thoo",
            wordBreakdown = "Athithi (Guest) + daivam aayi (as God) + karuthunna (holding/considering) + naadaanu nammudethu (is our land)",
            acceptedVariants = listOf("athithi daivam aayi karuthunna naadaanu nammudethu", "athithi daivam"),
            tags = listOf("distinguished", "hospitality", "speech")
        ),
        PhraseItem(
            id = "dst_02",
            manglish = "Kshemam anveshikkunnathu nammude paramparayaanu",
            malayalam = "ക്ഷേമം അന്വേഷിക്കുന്നത് നമ്മുടെ പാരമ്പര്യമാണ്",
            english = "Inquiring into the well-being of others is our time-honored heritage.",
            regionalNuance = "Elevated cultural speech delivered at community receptions, wedding toasts, and parish assemblies.",
            textbookAlternative = "Kshema samacharam anveshikkal nammude aachaaramaanu",
            category = "Distinguished: Master",
            levelNumber = 11,
            levelBadge = "Distinguished • Cultural Master",
            phoneticGuide = "KSHEY-mum un-vey-SHIK-koon-nuh-thoo num-moo-dey PAH-rum-puh-ri-yuh-mah-noo",
            wordBreakdown = "Kshemam (Welfare/peace) + anveshikkunnathu (inquiring into) + nammude paramparayaanu (is our heritage)",
            acceptedVariants = listOf("kshemam anveshikkunnathu nammude paramparayaanu", "paramparayaanu"),
            tags = listOf("distinguished", "heritage", "speech")
        ),
        PhraseItem(
            id = "dst_03",
            manglish = "Sahanabhavathodu koodi ellaam parihaarikkanam",
            malayalam = "സഹനഭാവത്തോടു കൂടി എല്ലാം പരിഹരിക്കണം",
            english = "With patient compassion, all challenges must be resolved.",
            regionalNuance = "The pastoral counsel of Christian elders and community arbitrators reconciling disagreements with grace.",
            textbookAlternative = "Kshamayode kaaryangal theerppukalpikkanam",
            category = "Distinguished: Master",
            levelNumber = 11,
            levelBadge = "Distinguished • Cultural Master",
            phoneticGuide = "suh-huh-nuh-BHAH-vuh-thoh-doo KOO-di EL-lahm puh-ri-HAH-rik-kuh-num",
            wordBreakdown = "Sahanabhavathodu koodi (With patient forbearance) + ellaam (everything) + parihaarikkanam (must be resolved)",
            acceptedVariants = listOf("sahanabhavathodu koodi ellaam parihaarikkanam", "parihaarikkanam"),
            tags = listOf("distinguished", "peace", "counsel")
        ),
        PhraseItem(
            id = "dst_04",
            manglish = "Eeviduthe ellavarum ente swantham aalukalaanu",
            malayalam = "ഇവിടുത്തെ എല്ലാവരും എന്റെ സ്വന്തം ആളുകളാണ്",
            english = "Everyone gathered here is truly like my own family.",
            regionalNuance = "The ultimate expression of communal warmth spoken by a parish leader or host embracing their audience as one.",
            textbookAlternative = "Eeviduthe aalukalente kudumbam thanneyaanu",
            category = "Distinguished: Master",
            levelNumber = 11,
            levelBadge = "Distinguished • Cultural Master",
            phoneticGuide = "ee-vi-DOOTH-ey EL-lah-vuh-room EN-tey SWUN-thum AH-loo-kuh-lah-noo",
            wordBreakdown = "Eeviduthe ellavarum (Everyone here) + ente swantham aalukalaanu (are my very own people)",
            acceptedVariants = listOf("eeviduthe ellavarum ente swantham aalukalaanu", "swantham aalukalaanu"),
            tags = listOf("distinguished", "family", "community")
        )
    )

    val decks: List<FlashcardDeck> = listOf(
        FlashcardDeck(
            id = "deck_level0_words",
            title = "Novice 1: First 12 Words (Start Here!)",
            description = "Know only English? Start here with single-word building blocks: I, You, Yes, No, Want, What, Where.",
            iconEmoji = "🌱",
            levelIndex = 0,
            levelLabel = "Novice Level 1",
            phrases = allPhrases.filter { it.levelNumber == 0 }
        ),
        FlashcardDeck(
            id = "deck_level0_people_combos",
            title = "Novice 2: Family & 2-Word Combos",
            description = "Appa, Amma, Ammachi, and your very first 2-word questions ('Chaya veno?', 'Amma evide?').",
            iconEmoji = "👋",
            levelIndex = 1,
            levelLabel = "Novice Level 2",
            phrases = allPhrases.filter { it.levelNumber == 1 }
        ),
        FlashcardDeck(
            id = "deck_level1_shortcuts",
            title = "Novice 3: Adoor Action Shortcuts",
            description = "Learn single-word Central Travancore '-oolla' shortcuts: Varoolla, Cheyyoolla, Pokoolla.",
            iconEmoji = "⚡",
            levelIndex = 2,
            levelLabel = "Novice Level 3",
            phrases = allPhrases.filter { it.levelNumber == 2 }
        ),
        FlashcardDeck(
            id = "deck_contractions",
            title = "Novice 3: Short Home Sentences",
            description = "Put Novice 1 + Novice 2 words together into natural 3-to-4 word Adoor sentences.",
            iconEmoji = "🧩",
            levelIndex = 3,
            levelLabel = "Novice Level 3",
            phrases = allPhrases.filter { it.levelNumber == 3 }
        ),
        FlashcardDeck(
            id = "deck_family",
            title = "Intermediate 1: Kitchen & Family Talk",
            description = "Everyday Central Travancore household phrases with Ammachi, Appachan, and Achayan.",
            iconEmoji = "🏡",
            levelIndex = 4,
            levelLabel = "Intermediate Level 1",
            phrases = allPhrases.filter { it.levelNumber == 4 }
        ),
        FlashcardDeck(
            id = "deck_thattukada",
            title = "Intermediate 2: Thattukada & Travel",
            description = "Order double strong chaya, hot parotta, and hail autos in Adoor effortlessly.",
            iconEmoji = "☕",
            levelIndex = 5,
            levelLabel = "Intermediate Level 2",
            phrases = allPhrases.filter { it.levelNumber == 5 }
        ),
        FlashcardDeck(
            id = "deck_catholic_church",
            title = "Intermediate 3: Catholic Church & Prayers",
            description = "Sunday Holy Mass (Qurbana), evening family rosary, Hail Mary, parish priest greetings & feasts.",
            iconEmoji = "⛪",
            levelIndex = 6,
            levelLabel = "Intermediate Level 3 • Catholic",
            phrases = allPhrases.filter { it.levelNumber == 6 }
        ),
        FlashcardDeck(
            id = "deck_advanced_storytelling",
            title = "Advanced 1: Storytelling & Feelings",
            description = "Express personal sorrow, reminiscence of fond times, and recount narrative sequences.",
            iconEmoji = "📜",
            levelIndex = 7,
            levelLabel = "Advanced Level 1",
            phrases = allPhrases.filter { it.levelNumber == 7 }
        ),
        FlashcardDeck(
            id = "deck_advanced_opinions",
            title = "Advanced 2: Opinions & Polite Debate",
            description = "Diplomatically voice disagreement, provide nuanced reasoning, and propose alternatives.",
            iconEmoji = "⚖️",
            levelIndex = 8,
            levelLabel = "Advanced Level 2",
            phrases = allPhrases.filter { it.levelNumber == 8 }
        ),
        FlashcardDeck(
            id = "deck_slang",
            title = "Superior 1: Enthuvaadey & Rapid Banter",
            description = "Rapid Central Travancore cadence, humorous exclamations ('pulli', 'aadey'), and witty retorts.",
            iconEmoji = "🔥",
            levelIndex = 9,
            levelLabel = "Superior Level 1 • Near-Native Banter",
            phrases = allPhrases.filter { it.levelNumber == 9 }
        ),
        FlashcardDeck(
            id = "deck_superior_proverbs",
            title = "Superior 2: Malayalam Proverbs & Idioms",
            description = "Famous Pazhamchollukal ('Minnumathellam ponnalla') that native speakers drop into conversations.",
            iconEmoji = "🦉",
            levelIndex = 10,
            levelLabel = "Superior Level 2 • Proverbs",
            phrases = allPhrases.filter { it.levelNumber == 10 }
        ),
        FlashcardDeck(
            id = "deck_distinguished_master",
            title = "Distinguished: Cultural Orator & Master",
            description = "Exceptional mastery: cultural hospitality, parish speeches, and gracious community counsel.",
            iconEmoji = "👑",
            levelIndex = 11,
            levelLabel = "Distinguished • Master Speaker",
            phrases = allPhrases.filter { it.levelNumber == 11 }
        )
    )

    val studyPlanStages: List<StudyLevelStage> = listOf(
        StudyLevelStage(
            levelIndex = 0,
            tierId = "novice",
            tierName = "Novice Tier",
            rankTitle = "Novice Level 1",
            topicTitle = "First 12 Core Words & Pronouns",
            description = "Start here with single-word building blocks: I, You, Yes, No, Want?, Where?, Now.",
            iconEmoji = "🌱",
            deckId = "deck_level0_words",
            requiredWordsToUnlock = 0,
            isTierFinalStage = false
        ),
        StudyLevelStage(
            levelIndex = 1,
            tierId = "novice",
            tierName = "Novice Tier",
            rankTitle = "Novice Level 2",
            topicTitle = "Family Names & 2-Word Combos",
            description = "Appa, Amma, Ammachi & Appachan, and simple 2-word questions like 'Chaya veno?' and 'Amma evide?'.",
            iconEmoji = "👋",
            deckId = "deck_level0_people_combos",
            requiredWordsToUnlock = 5,
            isTierFinalStage = false
        ),
        StudyLevelStage(
            levelIndex = 2,
            tierId = "novice",
            tierName = "Novice Tier",
            rankTitle = "Novice Level 3",
            topicTitle = "Home Sentences & Action Shortcuts",
            description = "Learn spoken contractions (Varoolla, Cheyyoolla) and short home sentences. Pass the Spoken Graduation Exam to reach Intermediate!",
            iconEmoji = "⚡",
            deckId = "deck_level1_shortcuts",
            requiredWordsToUnlock = 10,
            isTierFinalStage = true
        ),
        StudyLevelStage(
            levelIndex = 3,
            tierId = "intermediate",
            tierName = "Intermediate Tier",
            rankTitle = "Intermediate Level 1",
            topicTitle = "Kitchen & Family Conversations",
            description = "Chat naturally at home in Adoor about morning tea, church, and family errands.",
            iconEmoji = "🏡",
            deckId = "deck_family",
            requiredWordsToUnlock = 15,
            isTierFinalStage = false
        ),
        StudyLevelStage(
            levelIndex = 4,
            tierId = "intermediate",
            tierName = "Intermediate Tier",
            rankTitle = "Intermediate Level 2",
            topicTitle = "Thattukada & Travel Navigation",
            description = "Order double strong chaya, hot pazhampori, and navigate Adoor KSRTC bus stand and auto meters.",
            iconEmoji = "🛺",
            deckId = "deck_thattukada",
            requiredWordsToUnlock = 20,
            isTierFinalStage = false
        ),
        StudyLevelStage(
            levelIndex = 5,
            tierId = "intermediate",
            tierName = "Intermediate Tier",
            rankTitle = "Intermediate Level 3 (Catholic & Church)",
            topicTitle = "Catholic Church, Mass & Daily Prayers",
            description = "Holy Mass (Qurbana), evening family rosary, Hail Mary, parish priest greetings, and festive Perunnal. Pass Spoken Graduation to reach Advanced!",
            iconEmoji = "⛪",
            deckId = "deck_catholic_church",
            requiredWordsToUnlock = 25,
            isTierFinalStage = true
        ),
        StudyLevelStage(
            levelIndex = 6,
            tierId = "advanced",
            tierName = "Advanced Tier",
            rankTitle = "Advanced Level 1",
            topicTitle = "Storytelling, Feelings & Memories",
            description = "Express complex personal sorrow, reminisce of fond memories, and recount sequential past stories.",
            iconEmoji = "📜",
            deckId = "deck_advanced_storytelling",
            requiredWordsToUnlock = 31,
            isTierFinalStage = false
        ),
        StudyLevelStage(
            levelIndex = 7,
            tierId = "advanced",
            tierName = "Advanced Tier",
            rankTitle = "Advanced Level 2",
            topicTitle = "Opinions, Nuanced Reasoning & Debate",
            description = "Diplomatically voice disagreement, provide nuanced reasoning, and propose alternative solutions. Pass Spoken Graduation to reach Superior!",
            iconEmoji = "⚖️",
            deckId = "deck_advanced_opinions",
            requiredWordsToUnlock = 37,
            isTierFinalStage = true
        ),
        StudyLevelStage(
            levelIndex = 8,
            tierId = "superior",
            tierName = "Superior Tier",
            rankTitle = "Superior Level 1",
            topicTitle = "Fast Family Banter & Colloquial Slang",
            description = "Rapid Central Travancore cadence, humorous exclamations ('Enthuvaadey', 'pulli'), and witty retorts.",
            iconEmoji = "🔥",
            deckId = "deck_slang",
            requiredWordsToUnlock = 43,
            isTierFinalStage = false
        ),
        StudyLevelStage(
            levelIndex = 9,
            tierId = "superior",
            tierName = "Superior Tier",
            rankTitle = "Superior Level 2",
            topicTitle = "Malayalam Proverbs & Cultural Idioms",
            description = "Master famous Pazhamchollukal ('Minnumathellam ponnalla') that native speakers drop into conversations. Pass Spoken Graduation to reach Distinguished!",
            iconEmoji = "🦉",
            deckId = "deck_superior_proverbs",
            requiredWordsToUnlock = 49,
            isTierFinalStage = true
        ),
        StudyLevelStage(
            levelIndex = 10,
            tierId = "distinguished",
            tierName = "Distinguished Tier",
            rankTitle = "Distinguished Level 1",
            topicTitle = "Cultural Orator & Community Eloquence",
            description = "Exceptional mastery: cultural hospitality, parish speeches, and gracious community counsel. Earn Master Speaker Certification!",
            iconEmoji = "👑",
            deckId = "deck_distinguished_master",
            requiredWordsToUnlock = 55,
            isTierFinalStage = true
        )
    )

    fun getCurrentStageForKnownCount(knownCount: Int): StudyLevelStage {
        return studyPlanStages.lastOrNull { knownCount >= it.requiredWordsToUnlock } ?: studyPlanStages.first()
    }

    fun getNextStageForKnownCount(knownCount: Int): StudyLevelStage? {
        val current = getCurrentStageForKnownCount(knownCount)
        return studyPlanStages.getOrNull(current.levelIndex + 1)
    }

    /**
     * Generates a Spoken Graduation Oral Defense exam tailored strictly to the
     * words and phrases the learner has encountered up to that specific ACTFL tier.
     */
    fun getGraduationPromptsForTier(tier: ActflProficiencyTier): List<GraduationPrompt> {
        return when (tier) {
            ActflProficiencyTier.NOVICE -> listOf(
                GraduationPrompt(
                    id = "grad_nov_1",
                    phrase = allPhrases.first { it.id == "l3_01" }, // Njaan ippo varoolla ketto
                    scenarioPrompt = "You are at home in Adoor. Tell your family: 'I won't come right now, okay?'",
                    targetExpectedManglish = "Njaan ippo varoolla ketto",
                    hint = "Njaan (I) + ippo (now) + varoolla (won't come) + ketto (okay?)"
                ),
                GraduationPrompt(
                    id = "grad_nov_2",
                    phrase = allPhrases.first { it.id == "l1_05" }, // Chaya veno?
                    scenarioPrompt = "A guest walks into your home. Offer them tea: 'Do you want tea?'",
                    targetExpectedManglish = "Chaya veno?",
                    hint = "Chaya (tea) + veno? (want?)"
                ),
                GraduationPrompt(
                    id = "grad_nov_3",
                    phrase = allPhrases.first { it.id == "l1_08" }, // Amma evide?
                    scenarioPrompt = "You step into the house and ask your sibling: 'Where is Mom?'",
                    targetExpectedManglish = "Amma evide?",
                    hint = "Amma (Mom) + evide? (where?)"
                ),
                GraduationPrompt(
                    id = "grad_nov_4",
                    phrase = allPhrases.first { it.id == "l2_01" }, // Varoolla
                    scenarioPrompt = "In Central Travancore dialect, say the single-word contraction for 'Won't come':",
                    targetExpectedManglish = "Varoolla",
                    hint = "Adoor spoken shortcut for textbook 'varunnilla'"
                )
            )
            ActflProficiencyTier.INTERMEDIATE -> listOf(
                GraduationPrompt(
                    id = "grad_int_1",
                    phrase = allPhrases.first { it.id == "cc_07" }, // Dhaivathinu sthuthi
                    scenarioPrompt = "You just heard wonderful news at home or finished your evening prayer. Say 'Praise be to God / Thanks be to God':",
                    targetExpectedManglish = "Dhaivathinu sthuthi",
                    hint = "Dhaivathinu (to God) + sthuthi (praise/glory)"
                ),
                GraduationPrompt(
                    id = "grad_int_2",
                    phrase = allPhrases.first { it.id == "cc_01" }, // Njaan palliye pokuvaa
                    scenarioPrompt = "Tell your family that you are leaving for church:",
                    targetExpectedManglish = "Njaan palliye pokuvaa",
                    hint = "Njaan (I) + palliye (to church) + pokuvaa (going)"
                ),
                GraduationPrompt(
                    id = "grad_int_3",
                    phrase = allPhrases.first { it.id == "l2_09" }, // Oru strong chaya pinne randu parotta
                    scenarioPrompt = "At the thattukada counter, order: 'One strong tea and two parottas':",
                    targetExpectedManglish = "Oru strong chaya pinne randu parotta",
                    hint = "Oru (one) + strong chaya + pinne (and) + randu parotta"
                ),
                GraduationPrompt(
                    id = "grad_int_4",
                    phrase = allPhrases.first { it.id == "cc_05" }, // Nanma niranja Mariyame, ninakku sthuthi
                    scenarioPrompt = "Recite the opening prayer line of the Hail Mary in Malayalam:",
                    targetExpectedManglish = "Nanma niranja Mariyame, ninakku sthuthi",
                    hint = "Nanma niranja Mariyame (Hail Mary) + ninakku sthuthi (praise to thee)"
                )
            )
            ActflProficiencyTier.ADVANCED -> listOf(
                GraduationPrompt(
                    id = "grad_adv_1",
                    phrase = allPhrases.first { it.id == "adv_07" }, // Ente abhiprayathil athu shariyalla
                    scenarioPrompt = "State your personal opinion diplomatically: 'In my opinion, that is not right':",
                    targetExpectedManglish = "Ente abhiprayathil athu shariyalla",
                    hint = "Ente abhiprayathil (in my opinion) + athu (that) + shariyalla (is not right)"
                ),
                GraduationPrompt(
                    id = "grad_adv_2",
                    phrase = allPhrases.first { it.id == "adv_01" }, // Njan oru karyam parayatte?
                    scenarioPrompt = "Politely prepare your listener for a story: 'Can I tell you something?':",
                    targetExpectedManglish = "Njan oru karyam parayatte?",
                    hint = "Njan (I) + oru karyam (one matter) + parayatte? (may I say?)"
                ),
                GraduationPrompt(
                    id = "grad_adv_3",
                    phrase = allPhrases.first { it.id == "adv_08" }, // Ningal paranjathu shariyaanu, ennalum...
                    scenarioPrompt = "Show advanced nuanced debate skill: 'What you said is true, nevertheless...':",
                    targetExpectedManglish = "Ningal paranjathu shariyaanu, ennalum",
                    hint = "Ningal paranjathu (what you said) + shariyaanu (is true) + ennalum (nevertheless)"
                ),
                GraduationPrompt(
                    id = "grad_adv_4",
                    phrase = allPhrases.first { it.id == "adv_02" }, // Athoru nalla kalam aayirunnu
                    scenarioPrompt = "Reminisce with elder relatives: 'That was such a fond, good time':",
                    targetExpectedManglish = "Athoru nalla kalam aayirunnu",
                    hint = "Athu (that) + oru nalla kalam (a good time) + aayirunnu (was)"
                )
            )
            ActflProficiencyTier.SUPERIOR -> listOf(
                GraduationPrompt(
                    id = "grad_sup_1",
                    phrase = allPhrases.first { it.id == "ct_06" }, // Enthuvaadey ithu?
                    scenarioPrompt = "Express authentic Central Travancore colloquial astonishment: 'What on earth is this, man?':",
                    targetExpectedManglish = "Enthuvaadey ithu?",
                    hint = "Adoor regional exclamation blending 'enthu' and friendly tag 'aadey'"
                ),
                GraduationPrompt(
                    id = "grad_sup_2",
                    phrase = allPhrases.first { it.id == "prv_01" }, // Minnumathellam ponnalla
                    scenarioPrompt = "Deliver the famous Malayalam proverb meaning 'All that glitters is not gold':",
                    targetExpectedManglish = "Minnumathellam ponnalla",
                    hint = "Minnumathellam (all that shines) + ponnalla (is not gold)"
                ),
                GraduationPrompt(
                    id = "grad_sup_3",
                    phrase = allPhrases.first { it.id == "prv_02" }, // Velichathu kaanumbozhalle niram ariyoo
                    scenarioPrompt = "Quote the cultural proverb: 'Only when brought into the light is the true character revealed':",
                    targetExpectedManglish = "Velichathu kaanumbozhalle niram ariyoo",
                    hint = "Velichathu kaanumbozhalle (only when seen in light) + niram ariyoo (is color known)"
                ),
                GraduationPrompt(
                    id = "grad_sup_4",
                    phrase = allPhrases.first { it.id == "sup_03" }, // Nammalude naattile visheshangal paranjal theeroolla
                    scenarioPrompt = "Say with warm native Travancore humor: 'If we start talking about our hometown news, it will never end!':",
                    targetExpectedManglish = "Nammalude naattile visheshangal paranjal theeroolla",
                    hint = "Naattile visheshangal (hometown news) + theeroolla (will not finish)"
                )
            )
            ActflProficiencyTier.DISTINGUISHED -> listOf(
                GraduationPrompt(
                    id = "grad_dst_1",
                    phrase = allPhrases.first { it.id == "dst_01" }, // Athithi daivam aayi karuthunna naadaanu nammudethu
                    scenarioPrompt = "Deliver Kerala's foundational cultural hospitality statement: 'Ours is a land that welcomes every guest with divine reverence':",
                    targetExpectedManglish = "Athithi daivam aayi karuthunna naadaanu nammudethu",
                    hint = "Athithi daivam (guest as divine) + naadaanu nammudethu (ours is the land)"
                ),
                GraduationPrompt(
                    id = "grad_dst_2",
                    phrase = allPhrases.first { it.id == "dst_02" }, // Kshemam anveshikkunnathu nammude paramparayaanu
                    scenarioPrompt = "Honor community heritage in speech: 'Inquiring into the well-being of others is our time-honored heritage':",
                    targetExpectedManglish = "Kshemam anveshikkunnathu nammude paramparayaanu",
                    hint = "Kshemam anveshikkunnathu (inquiring into welfare) + paramparayaanu (is heritage)"
                ),
                GraduationPrompt(
                    id = "grad_dst_3",
                    phrase = allPhrases.first { it.id == "dst_04" }, // Eeviduthe ellavarum ente swantham aalukalaanu
                    scenarioPrompt = "Speak with deep warmth at a parish gathering: 'Everyone gathered here is truly like my own family':",
                    targetExpectedManglish = "Eeviduthe ellavarum ente swantham aalukalaanu",
                    hint = "Ellavarum (everyone) + ente swantham aalukalaanu (are my own people)"
                )
            )
        }
    }

    /**
     * Dynamically constructs an Adaptive Focus Deck based on words the user struggled with
     * PLUS their foundational Level 0/1 prerequisite words if they missed a multi-word phrase.
     */
    fun buildAdaptiveStruggleDeck(masteryRecords: Map<String, PhraseMasteryRecord>): FlashcardDeck {
        val strugglingIds = masteryRecords.values
            .filter { it.mistakeCount > 0 && it.consecutiveCorrect < 2 }
            .sortedByDescending { it.mistakeCount }
            .map { it.phraseId }

        val targetPhrases = mutableListOf<PhraseItem>()
        for (id in strugglingIds) {
            val phrase = allPhrases.find { it.id == id } ?: continue
            // If user struggled with a multi-word sentence, pull in its unmastered Level 0/1 building blocks first!
            for (prereqId in phrase.prerequisiteIds) {
                val prereq = allPhrases.find { it.id == prereqId }
                val prereqRecord = masteryRecords[prereqId]
                if (prereq != null && (prereqRecord == null || !prereqRecord.isKnown) && targetPhrases.none { it.id == prereq.id }) {
                    targetPhrases.add(prereq)
                }
            }
            if (targetPhrases.none { it.id == phrase.id }) {
                targetPhrases.add(phrase)
            }
        }

        // If user hasn't made mistakes yet, fill with the next unlearned Ground Level 0 words
        if (targetPhrases.isEmpty()) {
            val unlearnedLevel0 = allPhrases.filter { phrase ->
                val rec = masteryRecords[phrase.id]
                rec == null || !rec.isKnown
            }.take(8)
            targetPhrases.addAll(unlearnedLevel0.ifEmpty { allPhrases.take(6) })
        }

        return FlashcardDeck(
            id = "deck_adaptive_focus",
            title = "Adaptive Focus (Dynamic Review)",
            description = "Automatically tailored to words you missed + their Level 0 building blocks.",
            iconEmoji = "🎯",
            levelIndex = 0,
            levelLabel = "Dynamic Adaptive",
            phrases = targetPhrases
        )
    }

    // Practice drill questions
    val quizQuestions: List<QuizQuestion> = listOf(
        QuizQuestion(
            id = "q_01",
            type = QuizType.DIALECT_CHOICE,
            phrase = allPhrases[0], // Njaan
            prompt = "I / Me",
            hint = "Pronounced NYAAN (rhymes with naan)",
            options = listOf("Njaan", "Nee", "Illa", "Evide")
        ),
        QuizQuestion(
            id = "q_02",
            type = QuizType.MANGLISH_TYPING,
            phrase = allPhrases[2], // Athe
            prompt = "Yes",
            hint = "Starts with 'A...' (pronounced UH-they)",
            acceptedManglishVariants = listOf("athe", "athey", "ade")
        ),
        QuizQuestion(
            id = "q_03",
            type = QuizType.SPEAKING_PRACTICE,
            phrase = allPhrases[16], // Chaya veno?
            prompt = "Do you want tea?",
            hint = "Chaya (tea) + veno? (do you want?)",
            acceptedManglishVariants = listOf("chaya veno", "chaaya veno", "veno")
        ),
        QuizQuestion(
            id = "q_04",
            type = QuizType.WORD_TILES,
            phrase = allPhrases[19], // Amma evide?
            prompt = "Where is Mom?",
            correctTileOrder = listOf("Amma", "evide?")
        ),
        QuizQuestion(
            id = "q_05",
            type = QuizType.MANGLISH_TYPING,
            phrase = allPhrases[22], // Varoolla
            prompt = "Won't come",
            hint = "Ends with '-oolla' (vuh-ROO-lah)",
            acceptedManglishVariants = listOf("varoolla", "varoola", "varathilla")
        ),
        QuizQuestion(
            id = "q_06",
            type = QuizType.WORD_TILES,
            phrase = allPhrases[28], // Njaan ippo varoolla ketto
            prompt = "I won't come right now, okay?",
            correctTileOrder = listOf("Njaan", "ippo", "varoolla", "ketto")
        )
    )

    val roleplayScenarios: List<RoleplayScenario> = listOf(
        RoleplayScenario(
            id = "scenario_qa_tutor",
            title = "Malayalam Word & Phrase Q&A",
            partnerName = "Malayalam Tutor",
            partnerRole = "Simple English-to-Malayalam Helper",
            avatarEmoji = "🎓",
            location = "Central Travancore (Adoor / Pathanamthitta)",
            initialMessage = ChatMessage(
                id = "init_1",
                isUser = false,
                manglishText = "Hello / Sugamaano? (soo-guh-MAH-noh) / Namaskaram (nuh-mus-KAA-rum)",
                malayalamText = "ഹലോ / സുഖമാണോ? / നമസ്കാരം",
                englishText = "Ask me how to say any word or phrase! For example: The word for hello is \"Namaskaram\" (formal), or in casual speech people just say \"Hello\" or ask \"Sugamaano?\" (How are you?).",
                correctionFeedback = "In everyday Kerala conversation, English words like \"Hello\", \"Thanks\", and \"Sorry\" are used naturally at home, while Malayalam equivalents like \"Namaskaram\" or \"Nanni\" are mostly formal."
            ),
            systemPromptContext = "Simple English-first Malayalam Q&A tutor. Always answer: 'The word for X is \"Y\"' and add a note on whether speakers use the English word or if it is formal vs informal."
        )
    )

    data class DialectComparison(
        val category: String,
        val textbookFormal: String,
        val adoorSpoken: String,
        val manglishSpoken: String,
        val meaning: String,
        val explanation: String
    )

    val dialectComparisons: List<DialectComparison> = listOf(
        DialectComparison(
            category = "Ground Level 0 Shortcut",
            textbookFormal = "Ippol (ഇപ്പോൾ) / Enthaanu (എന്താണ്)",
            adoorSpoken = "Ippo (ഇപ്പൊ) / Entha (എന്താ)",
            manglishSpoken = "Ippo / Entha",
            meaning = "Now / What?",
            explanation = "Spoken Malayalam drops stiff word endings so you can speak twice as fast with half the syllables."
        ),
        DialectComparison(
            category = "Verbal Contraction",
            textbookFormal = "Varunnilla (വരുന്നില്ല)",
            adoorSpoken = "Varoolla / Varathilla (വരൂല്ല / വരത്തില്ല)",
            manglishSpoken = "Varoolla",
            meaning = "Not coming / won't come",
            explanation = "In Pathanamthitta/Adoor, the negative progressive suffix '-unnilla' smoothly contracts into '-oolla' or regional '-athilla'."
        ),
        DialectComparison(
            category = "Verbal Contraction",
            textbookFormal = "Cheyyunnilla (ചെയ്യുന്നില്ല)",
            adoorSpoken = "Cheyyoolla (ചെയ്യൂല്ല)",
            manglishSpoken = "Cheyyoolla",
            meaning = "Not doing / won't do",
            explanation = "Natural spoken contraction used in home dialogue instead of standard Malayalam."
        ),
        DialectComparison(
            category = "Verbal Contraction",
            textbookFormal = "Pokunnilla (പോകുന്നില്ല)",
            adoorSpoken = "Pokoolla (പോകൂല്ല)",
            manglishSpoken = "Pokoolla",
            meaning = "Not going",
            explanation = "Eliminates stiff textbook pronunciation for seamless back-and-forth speech."
        ),
        DialectComparison(
            category = "Regional Question / Slang",
            textbookFormal = "Enthanu ithu? (എന്താണ് ഇത്?)",
            adoorSpoken = "Enthuvaadey ithu? (എന്തുവാടേ ഇത്?)",
            manglishSpoken = "Enthuvaadey ithu?",
            meaning = "What is this, man?",
            explanation = "'Enthuvaadey' is the iconic Central Travancore conversational signature, blending 'enthu' (what) and friendly regional tag 'aadey'."
        ),
        DialectComparison(
            category = "Kinship Terms",
            textbookFormal = "Muthassi / Pithamaahi (മുത്തശ്ശി)",
            adoorSpoken = "Ammachi (അമ്മാച്ചി)",
            manglishSpoken = "Ammachi",
            meaning = "Grandmother / Elder woman",
            explanation = "'Ammachi' is universally used across Central Travancore families, reflecting warm domestic kinship."
        ),
        DialectComparison(
            category = "Kinship Terms",
            textbookFormal = "Pithavu / Achan (പിതാവ് / അച്ഛൻ)",
            adoorSpoken = "Appa / Appachan (അപ്പ / അപ്പച്ചൻ)",
            manglishSpoken = "Appa",
            meaning = "Father / Grandfather",
            explanation = "'Appa' for father and 'Appachan' for grandfather are the traditional Central Travancore family terms."
        ),
        DialectComparison(
            category = "Conversational Tag",
            textbookFormal = "Ariyamo? (അറിയാമോ?)",
            adoorSpoken = "Ketto? / Alle? (കേട്ടോ? / അല്ലേ?)",
            manglishSpoken = "Ketto?",
            meaning = "Right? / You hear me?",
            explanation = "Frequent warm tags attached to sentences to check engagement and establish friendliness."
        )
    )
}
