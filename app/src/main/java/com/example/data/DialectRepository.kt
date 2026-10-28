package com.example.data

import com.example.model.*

object DialectRepository {

    val allPhrases: List<PhraseItem> = NovicePhrases.phrases +
        ElementaryPhrases.phrases +
        IntermediatePhrases.phrases +
        AdvancedPhrases.phrases +
        MasteryPhrases.phrases

    val decks: List<FlashcardDeck> = listOf(
        FlashcardDeck(
            id = "deck_novice_1_kinship",
            title = "Novice Level 1: Kinship & Addressing People",
            description = "Immediate and extended family titles (achayan, chechi, ammachi, ammayi, aliya), personal pronouns (njan, nee, njangal, avar), respectful vs. peer addressing.",
            iconEmoji = "🌱",
            levelIndex = 0,
            levelLabel = "Novice Level 1",
            phrases = allPhrases.filter { it.levelNumber == 0 }
        ),
        FlashcardDeck(
            id = "deck_novice_ng",
            title = "Novice Level 2: Greetings, Introductions & Repair",
            description = "Introduce yourself, greet a visitor, and ask for repetition when you do not understand.",
            iconEmoji = "🌱",
            levelIndex = 1,
            levelLabel = "Novice Level 2",
            phrases = allPhrases.filter { it.levelNumber == 1 }
        ),
        FlashcardDeck(
            id = "deck_novice_nn",
            title = "Novice Level 3: Numbers, Prices & Quantities",
            description = "Count small quantities, understand common prices, and ask how much you need to pay.",
            iconEmoji = "🌱",
            levelIndex = 2,
            levelLabel = "Novice Level 3",
            phrases = allPhrases.filter { it.levelNumber == 2 }
        ),
        FlashcardDeck(
            id = "deck_novice_2_kitchen_food",
            title = "Novice Level 4: Kitchen Essentials & Food Preferences",
            description = "Staple foods (choru, vellam, chaaya, meen, erachi), simple yes/no/quantity limits (venda, mathi, kurachu, theere venda, kooduthal).",
            iconEmoji = "🌱",
            levelIndex = 3,
            levelLabel = "Novice Level 4",
            phrases = allPhrases.filter { it.levelNumber == 3 }
        ),
        FlashcardDeck(
            id = "deck_novice_3_survival_verbs",
            title = "Novice Level 5: The 15 Survival Verbs",
            description = "Core actions in simple imperative & present forms (vaa, po, kazhikku, kudi, iri, para, kelkku, thaa, edu, cheyyu).",
            iconEmoji = "🌱",
            levelIndex = 4,
            levelLabel = "Novice Level 5",
            phrases = allPhrases.filter { it.levelNumber == 4 }
        ),
        FlashcardDeck(
            id = "deck_novice_nd",
            title = "Novice Level 6: People, Things & Simple Descriptions",
            description = "Describe an object and combine familiar nouns with size, colour, and possession.",
            iconEmoji = "🌱",
            levelIndex = 5,
            levelLabel = "Novice Level 6",
            phrases = allPhrases.filter { it.levelNumber == 5 }
        ),
        FlashcardDeck(
            id = "deck_novice_4_reactions_fillers",
            title = "Novice Level 7: Fast Reactions & Conversational Fillers",
            description = "Emotional cues and fillers (ayyo, saramilla, sathyam, aano, sherikkum, pinne, athre ullu, enthaa).",
            iconEmoji = "🌱",
            levelIndex = 6,
            levelLabel = "Novice Level 7",
            phrases = allPhrases.filter { it.levelNumber == 6 }
        ),
        FlashcardDeck(
            id = "deck_elem_1_questions_direction",
            title = "Elementary Level 1: Conversational Questions & Direction",
            description = "Question words and directional anchors (evideya, eppozha, entha, aara, engane, ivide, avide, nere, appurathe).",
            iconEmoji = "🛺",
            levelIndex = 7,
            levelLabel = "Elementary Level 1",
            phrases = allPhrases.filter { it.levelNumber == 7 }
        ),
        FlashcardDeck(
            id = "deck_elementary_ec",
            title = "Elementary Level 2: Calendar, Time & Appointments",
            description = "Arrange a meeting, understand dates, and describe when something happens.",
            iconEmoji = "🛺",
            levelIndex = 8,
            levelLabel = "Elementary Level 2",
            phrases = allPhrases.filter { it.levelNumber == 8 }
        ),
        FlashcardDeck(
            id = "deck_elementary_et",
            title = "Elementary Level 3: Buses, Autos & Travel",
            description = "Buy a ticket, check a destination, and tell a driver where to stop.",
            iconEmoji = "🛺",
            levelIndex = 9,
            levelLabel = "Elementary Level 3",
            phrases = allPhrases.filter { it.levelNumber == 9 }
        ),
        FlashcardDeck(
            id = "deck_elementary_es",
            title = "Elementary Level 4: Groceries, Money & Ordering",
            description = "Ask for ingredients, order a meal, pay, and explain a simple dietary preference.",
            iconEmoji = "🛺",
            levelIndex = 10,
            levelLabel = "Elementary Level 4",
            phrases = allPhrases.filter { it.levelNumber == 10 }
        ),
        FlashcardDeck(
            id = "deck_elem_2_past_tense_routines",
            title = "Elementary Level 5: Spoken Past Tense & Everyday Routines",
            description = "Everyday past-tense markers (kazhicho, poyi, vannu, urangiyo, kandu, cheythu) and time words (innale, innum, nerathe, vaiki).",
            iconEmoji = "🛺",
            levelIndex = 11,
            levelLabel = "Elementary Level 5",
            phrases = allPhrases.filter { it.levelNumber == 11 }
        ),
        FlashcardDeck(
            id = "deck_elem_3_future_commitments",
            title = "Elementary Level 6: Future Intentions & Casual Commitments",
            description = "The -aam and -um spoken future forms (varaam, kazhikkaam, nokkam, cheyyaam, naale, matte-naal).",
            iconEmoji = "🛺",
            levelIndex = 12,
            levelLabel = "Elementary Level 6",
            phrases = allPhrases.filter { it.levelNumber == 12 }
        ),
        FlashcardDeck(
            id = "deck_elem_4_physical_states",
            title = "Elementary Level 7: Describing Physical States & Desires",
            description = "Needs and physical feelings (vishakkunnu, thadasam, choodu, thanippu, urakkam varunnu, sheenam, vayya).",
            iconEmoji = "🛺",
            levelIndex = 13,
            levelLabel = "Elementary Level 7",
            phrases = allPhrases.filter { it.levelNumber == 13 }
        ),
        FlashcardDeck(
            id = "deck_elementary_eh",
            title = "Elementary Level 8: Body, Health & Asking for Help",
            description = "Name common symptoms and ask someone for help or a medical appointment.",
            iconEmoji = "🛺",
            levelIndex = 14,
            levelLabel = "Elementary Level 8",
            phrases = allPhrases.filter { it.levelNumber == 14 }
        ),
        FlashcardDeck(
            id = "deck_elem_5_home_objects",
            title = "Elementary Level 9: Home, Chores & Daily Objects",
            description = "Objects around the room (vaathil, janala, kaasu, phone, chaavi, vandi, bhaagyam).",
            iconEmoji = "🛺",
            levelIndex = 15,
            levelLabel = "Elementary Level 9",
            phrases = allPhrases.filter { it.levelNumber == 15 }
        ),
        FlashcardDeck(
            id = "deck_elementary_ed",
            title = "Elementary Level 10: Clothes, Washing & Getting Ready",
            description = "Choose clothes, explain sizes, and talk through getting ready to go out.",
            iconEmoji = "🛺",
            levelIndex = 16,
            levelLabel = "Elementary Level 10",
            phrases = allPhrases.filter { it.levelNumber == 16 }
        ),
        FlashcardDeck(
            id = "deck_elementary_ew",
            title = "Elementary Level 11: Weather, Outdoors & Places",
            description = "Describe the weather and surroundings, and suggest a simple outing.",
            iconEmoji = "🛺",
            levelIndex = 17,
            levelLabel = "Elementary Level 11",
            phrases = allPhrases.filter { it.levelNumber == 17 }
        ),
        FlashcardDeck(
            id = "deck_int_1_connectors",
            title = "Intermediate Level 1: Conjunctions & Sentence Connectors",
            description = "Glue words that create flowing speech (ennittum, athukondu, pakshe, athinaale, allenkil, ennaalum).",
            iconEmoji = "🏡",
            levelIndex = 18,
            levelLabel = "Intermediate Level 1",
            phrases = allPhrases.filter { it.levelNumber == 18 }
        ),
        FlashcardDeck(
            id = "deck_intermediate_in",
            title = "Intermediate Level 2: Telling a Story & Sequencing Events",
            description = "Retell an experience using sequence, cause, reported speech, and a conclusion.",
            iconEmoji = "🏡",
            levelIndex = 19,
            levelLabel = "Intermediate Level 2",
            phrases = allPhrases.filter { it.levelNumber == 19 }
        ),
        FlashcardDeck(
            id = "deck_int_2_conditionals",
            title = "Intermediate Level 3: Conditional Statements ('If / When')",
            description = "Spoken -aal and -appol forms (vannal, paranjaal, kandappol, poyappol, cheythaal nannayi).",
            iconEmoji = "🏡",
            levelIndex = 20,
            levelLabel = "Intermediate Level 3",
            phrases = allPhrases.filter { it.levelNumber == 20 }
        ),
        FlashcardDeck(
            id = "deck_intermediate_iw",
            title = "Intermediate Level 4: Work, Tasks & Phone Calls",
            description = "Describe work, ask for clarification, and arrange a call or task handover.",
            iconEmoji = "🏡",
            levelIndex = 21,
            levelLabel = "Intermediate Level 4",
            phrases = allPhrases.filter { it.levelNumber == 21 }
        ),
        FlashcardDeck(
            id = "deck_intermediate_il",
            title = "Intermediate Level 5: School, Learning & Technology",
            description = "Explain a learning problem and ask for help with a document or device.",
            iconEmoji = "🏡",
            levelIndex = 22,
            levelLabel = "Intermediate Level 5",
            phrases = allPhrases.filter { it.levelNumber == 22 }
        ),
        FlashcardDeck(
            id = "deck_intermediate_im",
            title = "Intermediate Level 6: Hobbies, Media & Preferences",
            description = "Discuss something you watched, compare preferences, and recommend an activity.",
            iconEmoji = "🏡",
            levelIndex = 23,
            levelLabel = "Intermediate Level 6",
            phrases = allPhrases.filter { it.levelNumber == 23 }
        ),
        FlashcardDeck(
            id = "deck_int_4_opinions_doubts",
            title = "Intermediate Level 7: Opinions, Doubts & Comparisons",
            description = "Expressing perspective (thoonnunnu, vishwasamilla, athekkāl nallathu, enthekilum, chilappol, oru karyam parayatte).",
            iconEmoji = "🏡",
            levelIndex = 24,
            levelLabel = "Intermediate Level 7",
            phrases = allPhrases.filter { it.levelNumber == 24 }
        ),
        FlashcardDeck(
            id = "deck_int_5_errands_shopping",
            title = "Intermediate Level 8: Errands, Shopping & Outside Interactions",
            description = "Interacting at shops, restaurants, and appointments (vilakooduthal, kurakkumo, baakki, chodyam, ready aayo).",
            iconEmoji = "🏡",
            levelIndex = 25,
            levelLabel = "Intermediate Level 8",
            phrases = allPhrases.filter { it.levelNumber == 25 }
        ),
        FlashcardDeck(
            id = "deck_intermediate_ir",
            title = "Intermediate Level 9: Relationships, Invitations & Boundaries",
            description = "Invite someone, decline with a reason, and express a boundary respectfully.",
            iconEmoji = "🏡",
            levelIndex = 26,
            levelLabel = "Intermediate Level 9",
            phrases = allPhrases.filter { it.levelNumber == 26 }
        ),
        FlashcardDeck(
            id = "deck_int_3_banter",
            title = "Intermediate Level 10: Teasing, Sarcasm & Informal Banter",
            description = "Playful reactions, idioms, and attitude (thallu, scene aanu, veruthe, sceneilla, kollam, pinnallathe, shook, jaada).",
            iconEmoji = "🏡",
            levelIndex = 27,
            levelLabel = "Intermediate Level 10",
            phrases = allPhrases.filter { it.levelNumber == 27 }
        ),
        FlashcardDeck(
            id = "deck_adv_1_nuance_tags",
            title = "Advanced Level 1: Nuance Tags & Suffix Flow",
            description = "Micro-modifiers that define tone (-allo for affirmation, -tto for friendly warning/promise, -ketto, -enne, -thanne).",
            iconEmoji = "📜",
            levelIndex = 28,
            levelLabel = "Advanced Level 1",
            phrases = allPhrases.filter { it.levelNumber == 28 }
        ),
        FlashcardDeck(
            id = "deck_adv_2_complex_emotions",
            title = "Advanced Level 2: Complex Emotional States & Dispositions",
            description = "Subtle internal feelings (deshyam, sankadam, maduppu, kothiyaavunnu, ashwasam, vedhana, pedi, bharam).",
            iconEmoji = "📜",
            levelIndex = 29,
            levelLabel = "Advanced Level 2",
            phrases = allPhrases.filter { it.levelNumber == 29 }
        ),
        FlashcardDeck(
            id = "deck_adv_3_compound_verbs",
            title = "Advanced Level 3: Compound Verbs & Natural Contractions",
            description = "Natural contractions (kondubovaam, ittaekku, paranjukodukku, nokkikkollu, aayi-theernnu).",
            iconEmoji = "📜",
            levelIndex = 30,
            levelLabel = "Advanced Level 3",
            phrases = allPhrases.filter { it.levelNumber == 30 }
        ),
        FlashcardDeck(
            id = "deck_advanced_ap",
            title = "Advanced Level 4: Problems, Explanations & Negotiation",
            description = "Explain a problem, compare possible solutions, and agree on a next step.",
            iconEmoji = "📜",
            levelIndex = 31,
            levelLabel = "Advanced Level 4",
            phrases = allPhrases.filter { it.levelNumber == 31 }
        ),
        FlashcardDeck(
            id = "deck_advanced_an",
            title = "Advanced Level 5: News, Society & Evaluating Claims",
            description = "Summarise a news item, distinguish opinion from evidence, and explain uncertainty.",
            iconEmoji = "📜",
            levelIndex = 32,
            levelLabel = "Advanced Level 5",
            phrases = allPhrases.filter { it.levelNumber == 32 }
        ),
        FlashcardDeck(
            id = "deck_adv_4_family_lore",
            title = "Advanced Level 6: Family Lore, Tradition & Festive Vocabulary",
            description = "Wedding, feast, religious, and holiday registers (sadya, kalyanam, perunnal, acharam, anugraham, bandhangal).",
            iconEmoji = "📜",
            levelIndex = 33,
            levelLabel = "Advanced Level 6",
            phrases = allPhrases.filter { it.levelNumber == 33 }
        ),
        FlashcardDeck(
            id = "deck_adv_5_tactful_pushback",
            title = "Advanced Level 7: Expressing Disagreement & Tactful Pushback",
            description = "Polite and informal debate (athalla njan paranjathu, anganeyonnum alla, athil karyamilla, sammathikkilla).",
            iconEmoji = "📜",
            levelIndex = 34,
            levelLabel = "Advanced Level 7",
            phrases = allPhrases.filter { it.levelNumber == 34 }
        ),
        FlashcardDeck(
            id = "deck_mst_1_rapid_slang",
            title = "Mastery Level 1: Rapid Colloquial Slang & Generational Idioms",
            description = "Contemporary youth slang, inside jokes, and culturally loaded slang phrases (adipoli, kidu, vibe, scene kando, uyir, mass).",
            iconEmoji = "👑",
            levelIndex = 35,
            levelLabel = "Mastery Level 1",
            phrases = allPhrases.filter { it.levelNumber == 35 }
        ),
        FlashcardDeck(
            id = "deck_mst_2_classic_proverbs",
            title = "Mastery Level 2: Classic Proverbs & Everyday Metaphors",
            description = "Frequently referenced sayings and metaphors (minnunnathellaam ponnalla, kaaryam kaanaan kalipidikkuka, mulla poovinu manamilla).",
            iconEmoji = "👑",
            levelIndex = 36,
            levelLabel = "Mastery Level 2",
            phrases = allPhrases.filter { it.levelNumber == 36 }
        ),
        FlashcardDeck(
            id = "deck_mst_3_subtext_irony",
            title = "Mastery Level 3: Subtext, Irony & Passive Phrasing",
            description = "Knowing how to say things without stating them directly; reading between the lines in family dynamics.",
            iconEmoji = "👑",
            levelIndex = 37,
            levelLabel = "Mastery Level 3",
            phrases = allPhrases.filter { it.levelNumber == 37 }
        ),
        FlashcardDeck(
            id = "deck_mst_4_deep_abstract",
            title = "Mastery Level 4: Deep Vocabulary & Abstract Conversation",
            description = "Discussing philosophy, personal ethics, politics, and abstract career topics naturally.",
            iconEmoji = "👑",
            levelIndex = 38,
            levelLabel = "Mastery Level 4",
            phrases = allPhrases.filter { it.levelNumber == 38 }
        ),
        FlashcardDeck(
            id = "deck_mst_5_dialect_switching",
            title = "Mastery Level 5: Dialect & Accent Switching",
            description = "Recognizing differences between Central (Kottayam/Kochi/Adoor), Northern (Malabar), and Southern (Trivandrum) vocabulary nuances.",
            iconEmoji = "👑",
            levelIndex = 39,
            levelLabel = "Mastery Level 5",
            phrases = allPhrases.filter { it.levelNumber == 39 }
        ),
    )

    val studyPlanStages: List<StudyLevelStage> = listOf(
        StudyLevelStage(
            levelIndex = 0,
            tierId = "novice",
            tierName = "Level 1: Novice (Formulaic & Survival)",
            rankTitle = "Novice Level 1",
            topicTitle = "Kinship & Addressing People",
            description = "Immediate and extended family titles (achayan, chechi, ammachi, ammayi, aliya), personal pronouns (njan, nee, njangal, avar), respectful vs. peer addressing.",
            iconEmoji = "🌱",
            deckId = "deck_novice_1_kinship",
            requiredWordsToUnlock = 0,
            isTierFinalStage = false
        ),
        StudyLevelStage(
            levelIndex = 1,
            tierId = "novice",
            tierName = "Level 1: Novice (Formulaic & Survival)",
            rankTitle = "Novice Level 2",
            topicTitle = "Greetings, Introductions & Repair",
            description = "Introduce yourself, greet a visitor, and ask for repetition when you do not understand.",
            iconEmoji = "🌱",
            deckId = "deck_novice_ng",
            requiredWordsToUnlock = 25,
            isTierFinalStage = false
        ),
        StudyLevelStage(
            levelIndex = 2,
            tierId = "novice",
            tierName = "Level 1: Novice (Formulaic & Survival)",
            rankTitle = "Novice Level 3",
            topicTitle = "Numbers, Prices & Quantities",
            description = "Count small quantities, understand common prices, and ask how much you need to pay.",
            iconEmoji = "🌱",
            deckId = "deck_novice_nn",
            requiredWordsToUnlock = 45,
            isTierFinalStage = false
        ),
        StudyLevelStage(
            levelIndex = 3,
            tierId = "novice",
            tierName = "Level 1: Novice (Formulaic & Survival)",
            rankTitle = "Novice Level 4",
            topicTitle = "Kitchen Essentials & Food Preferences",
            description = "Staple foods (choru, vellam, chaaya, meen, erachi), simple yes/no/quantity limits (venda, mathi, kurachu, theere venda, kooduthal).",
            iconEmoji = "🌱",
            deckId = "deck_novice_2_kitchen_food",
            requiredWordsToUnlock = 69,
            isTierFinalStage = false
        ),
        StudyLevelStage(
            levelIndex = 4,
            tierId = "novice",
            tierName = "Level 1: Novice (Formulaic & Survival)",
            rankTitle = "Novice Level 5",
            topicTitle = "The 15 Survival Verbs",
            description = "Core actions in simple imperative & present forms (vaa, po, kazhikku, kudi, iri, para, kelkku, thaa, edu, cheyyu).",
            iconEmoji = "🌱",
            deckId = "deck_novice_3_survival_verbs",
            requiredWordsToUnlock = 94,
            isTierFinalStage = false
        ),
        StudyLevelStage(
            levelIndex = 5,
            tierId = "novice",
            tierName = "Level 1: Novice (Formulaic & Survival)",
            rankTitle = "Novice Level 6",
            topicTitle = "People, Things & Simple Descriptions",
            description = "Describe an object and combine familiar nouns with size, colour, and possession.",
            iconEmoji = "🌱",
            deckId = "deck_novice_nd",
            requiredWordsToUnlock = 119,
            isTierFinalStage = false
        ),
        StudyLevelStage(
            levelIndex = 6,
            tierId = "novice",
            tierName = "Level 1: Novice (Formulaic & Survival)",
            rankTitle = "Novice Level 7",
            topicTitle = "Fast Reactions & Conversational Fillers",
            description = "Emotional cues and fillers (ayyo, saramilla, sathyam, aano, sherikkum, pinne, athre ullu, enthaa).",
            iconEmoji = "🌱",
            deckId = "deck_novice_4_reactions_fillers",
            requiredWordsToUnlock = 141,
            isTierFinalStage = true
        ),
        StudyLevelStage(
            levelIndex = 7,
            tierId = "elementary",
            tierName = "Level 2: Elementary (Transactional & Daily Routine)",
            rankTitle = "Elementary Level 1",
            topicTitle = "Conversational Questions & Direction",
            description = "Question words and directional anchors (evideya, eppozha, entha, aara, engane, ivide, avide, nere, appurathe).",
            iconEmoji = "🛺",
            deckId = "deck_elem_1_questions_direction",
            requiredWordsToUnlock = 166,
            isTierFinalStage = false
        ),
        StudyLevelStage(
            levelIndex = 8,
            tierId = "elementary",
            tierName = "Level 2: Elementary (Transactional & Daily Routine)",
            rankTitle = "Elementary Level 2",
            topicTitle = "Calendar, Time & Appointments",
            description = "Arrange a meeting, understand dates, and describe when something happens.",
            iconEmoji = "🛺",
            deckId = "deck_elementary_ec",
            requiredWordsToUnlock = 191,
            isTierFinalStage = false
        ),
        StudyLevelStage(
            levelIndex = 9,
            tierId = "elementary",
            tierName = "Level 2: Elementary (Transactional & Daily Routine)",
            rankTitle = "Elementary Level 3",
            topicTitle = "Buses, Autos & Travel",
            description = "Buy a ticket, check a destination, and tell a driver where to stop.",
            iconEmoji = "🛺",
            deckId = "deck_elementary_et",
            requiredWordsToUnlock = 215,
            isTierFinalStage = false
        ),
        StudyLevelStage(
            levelIndex = 10,
            tierId = "elementary",
            tierName = "Level 2: Elementary (Transactional & Daily Routine)",
            rankTitle = "Elementary Level 4",
            topicTitle = "Groceries, Money & Ordering",
            description = "Ask for ingredients, order a meal, pay, and explain a simple dietary preference.",
            iconEmoji = "🛺",
            deckId = "deck_elementary_es",
            requiredWordsToUnlock = 239,
            isTierFinalStage = false
        ),
        StudyLevelStage(
            levelIndex = 11,
            tierId = "elementary",
            tierName = "Level 2: Elementary (Transactional & Daily Routine)",
            rankTitle = "Elementary Level 5",
            topicTitle = "Spoken Past Tense & Everyday Routines",
            description = "Everyday past-tense markers (kazhicho, poyi, vannu, urangiyo, kandu, cheythu) and time words (innale, innum, nerathe, vaiki).",
            iconEmoji = "🛺",
            deckId = "deck_elem_2_past_tense_routines",
            requiredWordsToUnlock = 267,
            isTierFinalStage = false
        ),
        StudyLevelStage(
            levelIndex = 12,
            tierId = "elementary",
            tierName = "Level 2: Elementary (Transactional & Daily Routine)",
            rankTitle = "Elementary Level 6",
            topicTitle = "Future Intentions & Casual Commitments",
            description = "The -aam and -um spoken future forms (varaam, kazhikkaam, nokkam, cheyyaam, naale, matte-naal).",
            iconEmoji = "🛺",
            deckId = "deck_elem_3_future_commitments",
            requiredWordsToUnlock = 297,
            isTierFinalStage = false
        ),
        StudyLevelStage(
            levelIndex = 13,
            tierId = "elementary",
            tierName = "Level 2: Elementary (Transactional & Daily Routine)",
            rankTitle = "Elementary Level 7",
            topicTitle = "Describing Physical States & Desires",
            description = "Needs and physical feelings (vishakkunnu, thadasam, choodu, thanippu, urakkam varunnu, sheenam, vayya).",
            iconEmoji = "🛺",
            deckId = "deck_elem_4_physical_states",
            requiredWordsToUnlock = 322,
            isTierFinalStage = false
        ),
        StudyLevelStage(
            levelIndex = 14,
            tierId = "elementary",
            tierName = "Level 2: Elementary (Transactional & Daily Routine)",
            rankTitle = "Elementary Level 8",
            topicTitle = "Body, Health & Asking for Help",
            description = "Name common symptoms and ask someone for help or a medical appointment.",
            iconEmoji = "🛺",
            deckId = "deck_elementary_eh",
            requiredWordsToUnlock = 347,
            isTierFinalStage = false
        ),
        StudyLevelStage(
            levelIndex = 15,
            tierId = "elementary",
            tierName = "Level 2: Elementary (Transactional & Daily Routine)",
            rankTitle = "Elementary Level 9",
            topicTitle = "Home, Chores & Daily Objects",
            description = "Objects around the room (vaathil, janala, kaasu, phone, chaavi, vandi, bhaagyam).",
            iconEmoji = "🛺",
            deckId = "deck_elem_5_home_objects",
            requiredWordsToUnlock = 371,
            isTierFinalStage = false
        ),
        StudyLevelStage(
            levelIndex = 16,
            tierId = "elementary",
            tierName = "Level 2: Elementary (Transactional & Daily Routine)",
            rankTitle = "Elementary Level 10",
            topicTitle = "Clothes, Washing & Getting Ready",
            description = "Choose clothes, explain sizes, and talk through getting ready to go out.",
            iconEmoji = "🛺",
            deckId = "deck_elementary_ed",
            requiredWordsToUnlock = 396,
            isTierFinalStage = false
        ),
        StudyLevelStage(
            levelIndex = 17,
            tierId = "elementary",
            tierName = "Level 2: Elementary (Transactional & Daily Routine)",
            rankTitle = "Elementary Level 11",
            topicTitle = "Weather, Outdoors & Places",
            description = "Describe the weather and surroundings, and suggest a simple outing.",
            iconEmoji = "🛺",
            deckId = "deck_elementary_ew",
            requiredWordsToUnlock = 414,
            isTierFinalStage = true
        ),
        StudyLevelStage(
            levelIndex = 18,
            tierId = "intermediate",
            tierName = "Level 3: Intermediate (Spontaneous & Conversational)",
            rankTitle = "Intermediate Level 1",
            topicTitle = "Conjunctions & Sentence Connectors",
            description = "Glue words that create flowing speech (ennittum, athukondu, pakshe, athinaale, allenkil, ennaalum).",
            iconEmoji = "🏡",
            deckId = "deck_int_1_connectors",
            requiredWordsToUnlock = 438,
            isTierFinalStage = false
        ),
        StudyLevelStage(
            levelIndex = 19,
            tierId = "intermediate",
            tierName = "Level 3: Intermediate (Spontaneous & Conversational)",
            rankTitle = "Intermediate Level 2",
            topicTitle = "Telling a Story & Sequencing Events",
            description = "Retell an experience using sequence, cause, reported speech, and a conclusion.",
            iconEmoji = "🏡",
            deckId = "deck_intermediate_in",
            requiredWordsToUnlock = 463,
            isTierFinalStage = false
        ),
        StudyLevelStage(
            levelIndex = 20,
            tierId = "intermediate",
            tierName = "Level 3: Intermediate (Spontaneous & Conversational)",
            rankTitle = "Intermediate Level 3",
            topicTitle = "Conditional Statements ('If / When')",
            description = "Spoken -aal and -appol forms (vannal, paranjaal, kandappol, poyappol, cheythaal nannayi).",
            iconEmoji = "🏡",
            deckId = "deck_int_2_conditionals",
            requiredWordsToUnlock = 485,
            isTierFinalStage = false
        ),
        StudyLevelStage(
            levelIndex = 21,
            tierId = "intermediate",
            tierName = "Level 3: Intermediate (Spontaneous & Conversational)",
            rankTitle = "Intermediate Level 4",
            topicTitle = "Work, Tasks & Phone Calls",
            description = "Describe work, ask for clarification, and arrange a call or task handover.",
            iconEmoji = "🏡",
            deckId = "deck_intermediate_iw",
            requiredWordsToUnlock = 510,
            isTierFinalStage = false
        ),
        StudyLevelStage(
            levelIndex = 22,
            tierId = "intermediate",
            tierName = "Level 3: Intermediate (Spontaneous & Conversational)",
            rankTitle = "Intermediate Level 5",
            topicTitle = "School, Learning & Technology",
            description = "Explain a learning problem and ask for help with a document or device.",
            iconEmoji = "🏡",
            deckId = "deck_intermediate_il",
            requiredWordsToUnlock = 532,
            isTierFinalStage = false
        ),
        StudyLevelStage(
            levelIndex = 23,
            tierId = "intermediate",
            tierName = "Level 3: Intermediate (Spontaneous & Conversational)",
            rankTitle = "Intermediate Level 6",
            topicTitle = "Hobbies, Media & Preferences",
            description = "Discuss something you watched, compare preferences, and recommend an activity.",
            iconEmoji = "🏡",
            deckId = "deck_intermediate_im",
            requiredWordsToUnlock = 556,
            isTierFinalStage = false
        ),
        StudyLevelStage(
            levelIndex = 24,
            tierId = "intermediate",
            tierName = "Level 3: Intermediate (Spontaneous & Conversational)",
            rankTitle = "Intermediate Level 7",
            topicTitle = "Opinions, Doubts & Comparisons",
            description = "Expressing perspective (thoonnunnu, vishwasamilla, athekkāl nallathu, enthekilum, chilappol, oru karyam parayatte).",
            iconEmoji = "🏡",
            deckId = "deck_int_4_opinions_doubts",
            requiredWordsToUnlock = 578,
            isTierFinalStage = false
        ),
        StudyLevelStage(
            levelIndex = 25,
            tierId = "intermediate",
            tierName = "Level 3: Intermediate (Spontaneous & Conversational)",
            rankTitle = "Intermediate Level 8",
            topicTitle = "Errands, Shopping & Outside Interactions",
            description = "Interacting at shops, restaurants, and appointments (vilakooduthal, kurakkumo, baakki, chodyam, ready aayo).",
            iconEmoji = "🏡",
            deckId = "deck_int_5_errands_shopping",
            requiredWordsToUnlock = 603,
            isTierFinalStage = false
        ),
        StudyLevelStage(
            levelIndex = 26,
            tierId = "intermediate",
            tierName = "Level 3: Intermediate (Spontaneous & Conversational)",
            rankTitle = "Intermediate Level 9",
            topicTitle = "Relationships, Invitations & Boundaries",
            description = "Invite someone, decline with a reason, and express a boundary respectfully.",
            iconEmoji = "🏡",
            deckId = "deck_intermediate_ir",
            requiredWordsToUnlock = 628,
            isTierFinalStage = false
        ),
        StudyLevelStage(
            levelIndex = 27,
            tierId = "intermediate",
            tierName = "Level 3: Intermediate (Spontaneous & Conversational)",
            rankTitle = "Intermediate Level 10",
            topicTitle = "Teasing, Sarcasm & Informal Banter",
            description = "Playful reactions, idioms, and attitude (thallu, scene aanu, veruthe, sceneilla, kollam, pinnallathe, shook, jaada).",
            iconEmoji = "🏡",
            deckId = "deck_int_3_banter",
            requiredWordsToUnlock = 650,
            isTierFinalStage = true
        ),
        StudyLevelStage(
            levelIndex = 28,
            tierId = "advanced",
            tierName = "Level 4: Advanced (Professional & Nuanced)",
            rankTitle = "Advanced Level 1",
            topicTitle = "Nuance Tags & Suffix Flow",
            description = "Micro-modifiers that define tone (-allo for affirmation, -tto for friendly warning/promise, -ketto, -enne, -thanne).",
            iconEmoji = "📜",
            deckId = "deck_adv_1_nuance_tags",
            requiredWordsToUnlock = 680,
            isTierFinalStage = false
        ),
        StudyLevelStage(
            levelIndex = 29,
            tierId = "advanced",
            tierName = "Level 4: Advanced (Professional & Nuanced)",
            rankTitle = "Advanced Level 2",
            topicTitle = "Complex Emotional States & Dispositions",
            description = "Subtle internal feelings (deshyam, sankadam, maduppu, kothiyaavunnu, ashwasam, vedhana, pedi, bharam).",
            iconEmoji = "📜",
            deckId = "deck_adv_2_complex_emotions",
            requiredWordsToUnlock = 705,
            isTierFinalStage = false
        ),
        StudyLevelStage(
            levelIndex = 30,
            tierId = "advanced",
            tierName = "Level 4: Advanced (Professional & Nuanced)",
            rankTitle = "Advanced Level 3",
            topicTitle = "Compound Verbs & Natural Contractions",
            description = "Natural contractions (kondubovaam, ittaekku, paranjukodukku, nokkikkollu, aayi-theernnu).",
            iconEmoji = "📜",
            deckId = "deck_adv_3_compound_verbs",
            requiredWordsToUnlock = 735,
            isTierFinalStage = false
        ),
        StudyLevelStage(
            levelIndex = 31,
            tierId = "advanced",
            tierName = "Level 4: Advanced (Professional & Nuanced)",
            rankTitle = "Advanced Level 4",
            topicTitle = "Problems, Explanations & Negotiation",
            description = "Explain a problem, compare possible solutions, and agree on a next step.",
            iconEmoji = "📜",
            deckId = "deck_advanced_ap",
            requiredWordsToUnlock = 765,
            isTierFinalStage = false
        ),
        StudyLevelStage(
            levelIndex = 32,
            tierId = "advanced",
            tierName = "Level 4: Advanced (Professional & Nuanced)",
            rankTitle = "Advanced Level 5",
            topicTitle = "News, Society & Evaluating Claims",
            description = "Summarise a news item, distinguish opinion from evidence, and explain uncertainty.",
            iconEmoji = "📜",
            deckId = "deck_advanced_an",
            requiredWordsToUnlock = 787,
            isTierFinalStage = false
        ),
        StudyLevelStage(
            levelIndex = 33,
            tierId = "advanced",
            tierName = "Level 4: Advanced (Professional & Nuanced)",
            rankTitle = "Advanced Level 6",
            topicTitle = "Family Lore, Tradition & Festive Vocabulary",
            description = "Wedding, feast, religious, and holiday registers (sadya, kalyanam, perunnal, acharam, anugraham, bandhangal).",
            iconEmoji = "📜",
            deckId = "deck_adv_4_family_lore",
            requiredWordsToUnlock = 811,
            isTierFinalStage = false
        ),
        StudyLevelStage(
            levelIndex = 34,
            tierId = "advanced",
            tierName = "Level 4: Advanced (Professional & Nuanced)",
            rankTitle = "Advanced Level 7",
            topicTitle = "Expressing Disagreement & Tactful Pushback",
            description = "Polite and informal debate (athalla njan paranjathu, anganeyonnum alla, athil karyamilla, sammathikkilla).",
            iconEmoji = "📜",
            deckId = "deck_adv_5_tactful_pushback",
            requiredWordsToUnlock = 836,
            isTierFinalStage = true
        ),
        StudyLevelStage(
            levelIndex = 35,
            tierId = "mastery",
            tierName = "Level 5: Mastery (Idioms & Cultural Nuance)",
            rankTitle = "Mastery Level 1",
            topicTitle = "Rapid Colloquial Slang & Generational Idioms",
            description = "Contemporary youth slang, inside jokes, and culturally loaded slang phrases (adipoli, kidu, vibe, scene kando, uyir, mass).",
            iconEmoji = "👑",
            deckId = "deck_mst_1_rapid_slang",
            requiredWordsToUnlock = 861,
            isTierFinalStage = false
        ),
        StudyLevelStage(
            levelIndex = 36,
            tierId = "mastery",
            tierName = "Level 5: Mastery (Idioms & Cultural Nuance)",
            rankTitle = "Mastery Level 2",
            topicTitle = "Classic Proverbs & Everyday Metaphors",
            description = "Frequently referenced sayings and metaphors (minnunnathellaam ponnalla, kaaryam kaanaan kalipidikkuka, mulla poovinu manamilla).",
            iconEmoji = "👑",
            deckId = "deck_mst_2_classic_proverbs",
            requiredWordsToUnlock = 891,
            isTierFinalStage = false
        ),
        StudyLevelStage(
            levelIndex = 37,
            tierId = "mastery",
            tierName = "Level 5: Mastery (Idioms & Cultural Nuance)",
            rankTitle = "Mastery Level 3",
            topicTitle = "Subtext, Irony & Passive Phrasing",
            description = "Knowing how to say things without stating them directly; reading between the lines in family dynamics.",
            iconEmoji = "👑",
            deckId = "deck_mst_3_subtext_irony",
            requiredWordsToUnlock = 916,
            isTierFinalStage = false
        ),
        StudyLevelStage(
            levelIndex = 38,
            tierId = "mastery",
            tierName = "Level 5: Mastery (Idioms & Cultural Nuance)",
            rankTitle = "Mastery Level 4",
            topicTitle = "Deep Vocabulary & Abstract Conversation",
            description = "Discussing philosophy, personal ethics, politics, and abstract career topics naturally.",
            iconEmoji = "👑",
            deckId = "deck_mst_4_deep_abstract",
            requiredWordsToUnlock = 941,
            isTierFinalStage = false
        ),
        StudyLevelStage(
            levelIndex = 39,
            tierId = "mastery",
            tierName = "Level 5: Mastery (Idioms & Cultural Nuance)",
            rankTitle = "Mastery Level 5",
            topicTitle = "Dialect & Accent Switching",
            description = "Recognizing differences between Central (Kottayam/Kochi/Adoor), Northern (Malabar), and Southern (Trivandrum) vocabulary nuances.",
            iconEmoji = "👑",
            deckId = "deck_mst_5_dialect_switching",
            requiredWordsToUnlock = 971,
            isTierFinalStage = true
        ),
    )
    fun buildOverallDeck(knownCount: Int): FlashcardDeck {
        val reachedStages = studyPlanStages.filter { stage ->
            knownCount >= stage.requiredWordsToUnlock
        }.ifEmpty { listOf(studyPlanStages.first()) }
        val reachedPhrases = reachedStages.flatMap { stage ->
            decks.find { it.id == stage.deckId }?.phrases.orEmpty()
        }.distinctBy { it.id }

        return FlashcardDeck(
            id = "overall_study_deck",
            title = "All Unlocked Material",
            description = "Consolidated deck spanning all unlocked levels",
            iconEmoji = "📚",
            levelIndex = reachedStages.maxOf { it.levelIndex },
            levelLabel = reachedStages.last().rankTitle,
            phrases = reachedPhrases
        )
    }

    fun buildOverallDeckForLevel(maxUnlockedLevel: Int): FlashcardDeck {
        val reachedStages = studyPlanStages.filter { it.levelIndex <= maxUnlockedLevel }
        val reachedPhrases = reachedStages.flatMap { stage ->
            decks.find { it.id == stage.deckId }?.phrases.orEmpty()
        }.distinctBy { it.id }

        val currentStage = studyPlanStages.find { it.levelIndex == maxUnlockedLevel } ?: studyPlanStages.first()
        return FlashcardDeck(
            id = "overall_study_deck",
            title = "All Unlocked Material",
            description = "Consolidated deck spanning ${currentStage.rankTitle}",
            iconEmoji = "📚",
            levelIndex = currentStage.levelIndex,
            levelLabel = currentStage.rankTitle,
            phrases = reachedPhrases
        )
    }

    fun getCurrentStage(stats: com.example.model.UserStats): StudyLevelStage {
        val level = stats.unlockedLevelIndex.coerceIn(0, studyPlanStages.size - 1)
        return studyPlanStages.find { it.levelIndex == level } ?: studyPlanStages.first()
    }

    fun getNextStage(stats: com.example.model.UserStats): StudyLevelStage? {
        val nextLevel = stats.unlockedLevelIndex + 1
        return studyPlanStages.find { it.levelIndex == nextLevel }
    }

    fun getCurrentStageForKnownCount(knownCount: Int): StudyLevelStage {
        return studyPlanStages.lastOrNull { knownCount >= it.requiredWordsToUnlock }
            ?: studyPlanStages.first()
    }

    fun getNextStageForKnownCount(knownCount: Int): StudyLevelStage? {
        val currentStage = getCurrentStageForKnownCount(knownCount)
        val currentIndex = studyPlanStages.indexOf(currentStage)
        return studyPlanStages.getOrNull(currentIndex + 1)
    }

    // Course reviews sample taught material; they do not certify proficiency.
    fun getGraduationPromptsForTier(tier: ActflProficiencyTier): List<GraduationPrompt> {
        val phraseIds = when (tier) {
            ActflProficiencyTier.NOVICE -> listOf("n0_11", "ng_08", "nn_24", "n1_16", "n2_01", "n3_01")
            ActflProficiencyTier.ELEMENTARY -> listOf("e1_01", "ec_20", "et_18", "es_23", "eh_23", "e2_01")
            ActflProficiencyTier.INTERMEDIATE -> listOf("i1_01", "in_20", "iw_18", "il_22", "im_19", "ir_18")
            ActflProficiencyTier.ADVANCED -> listOf("a1_14", "a2_01", "a3_01", "an_20", "ap_19", "a5_01")
            ActflProficiencyTier.MASTERY -> listOf("m1_01", "m2_01", "m3_01", "m4_01", "m5_01")
        }
        return phraseIds.mapIndexed { index, id ->
            val phrase = allPhrases.single { it.id == id }
            GraduationPrompt(
                id = "grad_${tier.tierId}_$index",
                phrase = phrase,
                scenarioPrompt = "Say in Malayalam: ${phrase.english}",
                targetExpectedManglish = phrase.manglish,
                hint = phrase.phoneticGuide.ifBlank { "Recall the expression from this tier's lessons." }
            )
        }
    }

    fun buildAdaptiveStruggleDeck(masteryRecords: Map<String, PhraseMasteryRecord>): FlashcardDeck {
        val strugglingIds = masteryRecords.values
            .filter { it.isStruggling }
            .sortedByDescending { it.mistakeCount }
            .map { it.phraseId }

        val targetPhrases = mutableListOf<PhraseItem>()
        for (id in strugglingIds) {
            val phrase = allPhrases.find { it.id == id } ?: continue
            if (phrase !in targetPhrases) targetPhrases.add(phrase)
            for (prereqId in phrase.prerequisiteIds) {
                val prereq = allPhrases.find { it.id == prereqId }
                val prereqRecord = masteryRecords[prereqId]
                if (prereq != null && prereq !in targetPhrases && (prereqRecord == null || !prereqRecord.isKnown)) {
                    targetPhrases.add(prereq)
                }
            }
            if (targetPhrases.size >= 12) break
        }

        if (targetPhrases.isEmpty()) {
            val unlearnedLevel0 = allPhrases.filter { phrase ->
                val rec = masteryRecords[phrase.id]
                rec == null || !rec.isKnown
            }.take(8)
            if (unlearnedLevel0.isNotEmpty()) {
                targetPhrases.addAll(unlearnedLevel0)
            } else {
                targetPhrases.addAll(allPhrases.take(8))
            }
        }

        return FlashcardDeck(
            id = "adaptive_struggle_deck",
            title = "Targeted Focus & Recovery",
            description = "Phrases that need reinforcement before moving forward",
            iconEmoji = "🎯",
            levelIndex = 0,
            levelLabel = "Targeted Focus",
            phrases = targetPhrases.ifEmpty { allPhrases.take(6) }
        )
    }

    data class LevelMasteryStatus(
        val levelIndex: Int,
        val rankTitle: String,
        val totalCards: Int,
        val masteredCards: Int,
        val isDeckMastered: Boolean,
        val isDrillMastered: Boolean,
        val isLevelMastered: Boolean
    )

    data class DrillRoundProgress(
        val completedRounds: Int,
        val totalRounds: Int
    ) {
        val ratio: Float
            get() = if (totalRounds <= 0) 0f else (completedRounds.toFloat() / totalRounds.toFloat()).coerceIn(0f, 1f)
    }

    fun getDrillRoundProgress(levelIndices: Collection<Int>, stats: com.example.model.UserStats): DrillRoundProgress {
        val rounds = levelIndices.distinct().flatMap { levelIndex ->
            listOf(1, 2, 3).map { round -> "${levelIndex}:${round}" }
        }
        val completedRounds = rounds.count { roundKey ->
            roundKey in stats.completedDrillRounds || roundKey.replace(":", "_") in stats.completedDrillRounds
        }
        return DrillRoundProgress(
            completedRounds = completedRounds,
            totalRounds = rounds.size
        )
    }

    fun getNextDrillRound(levelIndex: Int, stats: com.example.model.UserStats): Int =
        listOf(1, 2, 3).firstOrNull { round ->
            "${levelIndex}:${round}" !in stats.completedDrillRounds &&
            "${levelIndex}_${round}" !in stats.completedDrillRounds
        } ?: 1

    fun getTierLearnedWordProgress(tierId: String, stats: com.example.model.UserStats): Pair<Int, Int> {
        val tierStages = studyPlanStages.filter { it.tierId == tierId }
        val totalPhrases = tierStages.flatMap { stage ->
            decks.find { it.id == stage.deckId }?.phrases.orEmpty()
        }.distinctBy { it.id }
        val learnedPhraseIds = tierStages
            .flatMap { stage -> decks.find { it.id == stage.deckId }?.phrases.orEmpty() }
            .map { it.id }
            .toSet()
            .intersect(stats.learnedPhraseIds)
        return Pair(learnedPhraseIds.size, totalPhrases.size)
    }

    fun getLevelMasteryStatus(levelIndex: Int, stats: com.example.model.UserStats): LevelMasteryStatus {
        val stage = studyPlanStages.getOrNull(levelIndex) ?: studyPlanStages.first()
        val deck = decks.find { it.id == stage.deckId } ?: decks.first()
        val totalCards = deck.phrases.size
        val masteredCards = deck.phrases.count { p ->
            stats.learnedPhraseIds.contains(p.id) || (stats.masteryRecords[p.id]?.isKnown == true)
        }
        val isDeckMastered = totalCards > 0 && masteredCards >= totalCards
        val isDrillMastered = stats.masteredDrillLevelIndices.contains(levelIndex)
        val isLevelMastered = isDeckMastered || isDrillMastered

        return LevelMasteryStatus(
            levelIndex = stage.levelIndex,
            rankTitle = stage.rankTitle,
            totalCards = totalCards,
            masteredCards = masteredCards,
            isDeckMastered = isDeckMastered,
            isDrillMastered = isDrillMastered,
            isLevelMastered = isLevelMastered
        )
    }

    // Every activity resolves its content through the same selected deck.
    fun getPracticeDeck(levelIndex: Int, maxUnlockedLevel: Int = 0): FlashcardDeck =
        if (levelIndex == -1) buildOverallDeckForLevel(maxUnlockedLevel)
        else decks[levelIndex.coerceIn(0, decks.lastIndex)]

    fun generateQuestionForPhraseAndTouch(
        phrase: PhraseItem,
        touch: Int = 1,
        levelIndex: Int = 0,
        indexInRound: Int = 0,
        touchLevel: Int = touch,
        questionType: QuizType? = null,
        sourceDeck: FlashcardDeck = getPracticeDeck(levelIndex)
    ): QuizQuestion {
        val distractors = sourceDeck.phrases.asSequence()
            .map { it.manglish }
            .filter { it.isNotBlank() && it != phrase.manglish }
            .distinct().toList().shuffled().take(3)
        val mcOptions = (listOf(phrase.manglish) + distractors).shuffled()
        val correctIdx = mcOptions.indexOf(phrase.manglish).coerceAtLeast(0)

        val tileOrder = if (phrase.manglish.contains(" ")) {
            phrase.manglish.split(" ").filter { it.isNotBlank() }
        } else {
            listOf(phrase.manglish)
        }

        val uid = System.currentTimeMillis()

        val question = when (touchLevel) {
            1 -> {
                val t1Type = if (indexInRound % 2 == 0) QuizType.DIALECT_CHOICE else QuizType.WORD_TILES
                QuizQuestion(
                    id = "q_t1_${phrase.id}_${indexInRound}_${uid}",
                    type = t1Type,
                    phrase = phrase,
                    prompt = phrase.english,
                    hint = phrase.phoneticGuide.ifBlank { phrase.regionalNuance },
                    options = mcOptions,
                    correctOptionIndex = correctIdx,
                    correctTileOrder = tileOrder,
                    acceptedManglishVariants = phrase.acceptedVariants + listOf(phrase.manglish),
                    roundNumber = 1,
                    roundLabel = "Touch 1: Recognition",
                    touchLevel = 1
                )
            }
            2 -> {
                QuizQuestion(
                    id = "q_t2_${phrase.id}_${indexInRound}_${uid}",
                    type = QuizType.WORD_TILES,
                    phrase = phrase,
                    prompt = phrase.english,
                    hint = "Breakdown: ${phrase.wordBreakdown.ifBlank { phrase.manglish }}",
                    options = mcOptions,
                    correctOptionIndex = correctIdx,
                    correctTileOrder = tileOrder,
                    acceptedManglishVariants = phrase.acceptedVariants + listOf(phrase.manglish),
                    roundNumber = 2,
                    roundLabel = "Touch 2: Cued Recall",
                    touchLevel = 2
                )
            }
            else -> {
                val t3Type = if (indexInRound % 2 == 0) QuizType.MANGLISH_TYPING else QuizType.SPEAKING_PRACTICE
                QuizQuestion(
                    id = "q_t3_${phrase.id}_${indexInRound}_${uid}",
                    type = t3Type,
                    phrase = phrase,
                    prompt = phrase.english,
                    hint = "Pronunciation: ${phrase.phoneticGuide.ifBlank { phrase.manglish }}",
                    options = emptyList(),
                    correctOptionIndex = 0,
                    correctTileOrder = tileOrder,
                    acceptedManglishVariants = phrase.acceptedVariants + listOf(phrase.manglish),
                    roundNumber = 3,
                    roundLabel = "Touch 3: Free Recall",
                    touchLevel = 3
                )
            }
        }
        return questionType?.let { question.copy(type = it) } ?: question
    }

    fun generateDrillQuestionsForLevel(levelIndex: Int): List<QuizQuestion> =
        (1..3).flatMap { round -> generateDrillQuestionsForLevelAndRound(levelIndex, round) }

    fun generateDrillQuestionsForLevelAndRound(
        levelIndex: Int,
        round: Int,
        maxUnlockedLevel: Int = 0,
        questionType: QuizType? = null,
        orderPhrases: (List<PhraseItem>) -> List<PhraseItem> = { it.shuffled() }
    ): List<QuizQuestion> {
        val deck = getPracticeDeck(levelIndex, maxUnlockedLevel)
        return orderPhrases(deck.phrases).mapIndexed { index, phrase ->
            generateQuestionForPhraseAndTouch(
                phrase = phrase,
                touch = round,
                levelIndex = phrase.levelNumber,
                indexInRound = index,
                questionType = questionType,
                sourceDeck = deck
            )
        }
    }

    val quizQuestions: List<QuizQuestion> by lazy {
        generateDrillQuestionsForLevel(0)
    }

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
