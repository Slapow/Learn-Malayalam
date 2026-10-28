# Elementary Data Generator (Tier 2: Levels 4 to 8, 130 words)

def get_elementary_phrases():
    # Level 4: Elementary 1 - Conversational Questions & Direction (25 words)
    elem_1 = [
        ('e1_01', 'Evideya?', 'എവിടെയാ?', 'Where is it?', 'The primary spoken question asking location in Malayalam.', 'Evideyaanu', 'E-VI-DAY-YA', 'Evideya? = Where is it?', ['evideya', 'evideya?']),
        ('e1_02', 'Eppozha?', 'എപ്പോഴാ?', 'When is it?', 'Asking about timing of an event, meal, or departure.', 'Eppozhaanu', 'EP-PO-ZHA', 'Eppozha? = When is it?', ['eppozha', 'eppozha?']),
        ('e1_03', 'Entha?', 'എന്താ?', 'What is it?', 'Direct question asking about a matter or object.', 'Enthaanu', 'EN-THA', 'Entha? = What is it?', ['entha', 'entha?']),
        ('e1_04', 'Aara?', 'ആരാ?', 'Who is it?', 'Asking who knocked or who is calling.', 'Aaraanu', 'AA-RA', 'Aara? = Who is it?', ['aara', 'aara?']),
        ('e1_05', 'Engane?', 'എങ്ങനെ?', 'How?', 'Asking how to do something or how a journey went.', 'Enganeyaanu', 'ENG-A-NAY', 'Engane? = How?', ['engane', 'engane?']),
        ('e1_06', 'Enthinaa?', 'എന്തിനാ?', 'Why?', 'Casual question asking the purpose of something.', 'Enthinaanu', 'EN-THI-NAA', 'Enthinaa? = Why?', ['enthinaa', 'enthina']),
        ('e1_07', 'Ethra?', 'എത്ര?', 'How much?', 'Asking price, quantity, or time count.', 'Ethrayaanu', 'ETH-RA', 'Ethra? = How much?', ['ethra', 'ethra?']),
        ('e1_08', 'Eethaa?', 'ഏതാ?', 'Which one?', 'Asking to choose among items or dishes.', 'Eethaanu', 'AY-THAA', 'Eethaa? = Which one?', ['eethaa', 'eetha']),
        ("e1_09", "Ivide", "ഇവിടെ", "Here", "Right here in this room or spot.", "Ivide", "I-VI-DAY", "Ivide = Here", ["ivide"]),
        ("e1_10", "Avide", "അവിടെ", "There", "Over there in that spot or town.", "Avide", "A-VI-DAY", "Avide = There", ["avide"]),
        ('e1_11', 'Nere', 'നേരെ', 'Straight ahead', 'Direction to keep walking or driving straight ahead.', 'Nere', 'NAY-RAY', 'Nere = Straight ahead', ['nere']),
        ('e1_12', 'Appurathu', 'അപ്പുറത്ത്', 'On the other side', '', '', '', 'Appurathu = On the other side', ['appurathu']),
        ('e1_13', 'Ippurathu', 'ഇപ്പുറത്ത്', 'On this side', '', '', '', 'Ippurathu = On this side', ['ippurathu']),
        ("e1_14", "Ullil", "ഉള്ളിൽ", "Inside", "Inside the house, car, or room.", "Akathu", "UL-LIL", "Ullil = Inside", ["ullil"]),
        ("e1_15", "Veliyil", "വെളിയിൽ", "Outside", "Outside in the courtyard or yard.", "Purathu", "VE-LI-YIL", "Veliyil = Outside", ["veliyil"]),
        ('e1_16', 'Thazhe', 'താഴെ', 'Below', 'Downstairs or placed on the ground floor.', 'Thazhe', 'THAA-ZHAY', 'Thazhe = Below', ['thazhe']),
        ('e1_17', 'Mukalil', 'മുകളിൽ', 'Above', 'Upstairs on the balcony or upper shelf.', 'Mukalil', 'MU-KA-LIL', 'Mukalil = Above', ['mukalil']),
        ('e1_18', 'Aduthu', 'അടുത്ത്', 'Near', 'Within close walking distance in the neighborhood.', 'Samipathu', 'A-DU-THU', 'Aduthu = Near', ['aduthu']),
        ('e1_19', 'Doore', 'ദൂരെ', 'Far away', 'Far down the road or in another district.', 'Doore', 'DOO-RAY', 'Doore = Far away', ['doore']),
        ('e1_20', 'Valathu', 'വലത്ത്', 'Right side', 'Turning right at the junction.', 'Valathuvasham', 'VA-LA-THU', 'Valathu = Right side', ['valathu']),
        ('e1_21', 'Idathu', 'ഇടത്ത്', 'Left side', 'Turning left at the church gate.', 'Idathuvasham', 'I-DA-THU', 'Idathu = Left side', ['idathu']),
        ("e1_22", "Munnil", "മുന്നിൽ", "In front", "In front of the house or building.", "Munnil", "MUN-NIL", "Munnil = In front", ["munnil"]),
        ('e1_23', 'Purakil', 'പുറകിൽ', 'Behind', '', '', '', 'Purakil = Behind', ['purakil']),
        ('e1_24', 'Koodi', 'കൂടി', 'Together', 'Coming along together with us.', 'Oppam', 'KOO-DI', 'Koodi = Together', ['koodi']),
        ('e1_25', 'Maathram', 'മാത്രം', 'Only', 'Only a small portion or just this one.', 'Maathram', 'MAATH-RAM', 'Maathram = Only', ['maathram', 'mathram'])
    ]

    # Level 5: Elementary 2 - Spoken Past Tense & Everyday Routines (30 words)
    elem_2 = [
        ('e2_01', 'Kazhicho?', 'കഴിച്ചോ?', 'Did you eat?', 'The universal Kerala greeting showing love and care.', 'Bhakshanam kazhicho', 'KA-ZHI-CHOH', 'Kazhicho? = Did you eat?', ['kazhicho', 'kazhicho?']),
        ('e2_02', 'Poyi', 'പോയി', 'Went', 'He went to town or I went out.', 'Poyi', 'POH-YI', 'Poyi = Went', ['poyi']),
        ('e2_03', 'Vannu', 'വന്നു', 'Came', 'The guests arrived or someone came home.', 'Vannu', 'VAN-NU', 'Vannu = Came', ['vannu']),
        ('e2_04', 'Urangiyo?', 'ഉറങ്ങിയോ?', 'Did you sleep?', 'Morning greeting from family elders.', 'Urangiyo', 'U-RANG-I-YOH', 'Urangiyo? = Did you sleep?', ['urangiyo', 'urangiyo?']),
        ('e2_05', 'Kandu', 'കണ്ടു', 'Saw', 'I saw a friend or saw the movie.', 'Kandu', 'KAN-DU', 'Kandu = Saw', ['kandu']),
        ('e2_06', 'Cheythu', 'ചെയ്തു', 'Did', 'Finished doing the homework, chore, or task.', 'Cheythu', 'CHEY-THU', 'Cheythu = Did', ['cheythu']),
        ('e2_07', 'Paranju', 'പറഞ്ഞു', 'Said', 'Dad told me to let you know.', 'Paranju', 'PA-RAN-JU', 'Paranju = Said', ['paranju']),
        ('e2_08', 'Kettu', 'കേട്ടു', 'Heard', 'I heard the news or church bell.', 'Kettu', 'KAYT-TU', 'Kettu = Heard', ['kettu']),
        ('e2_09', 'Vaangi', 'വാങ്ങി', 'Bought', 'Bought tea leaves from the market.', 'Vaangi', 'VAANG-I', 'Vaangi = Bought', ['vaangi']),
        ('e2_10', 'Koduthu', 'കൊടുത്തു', 'Gave', 'Handed money or clothes to someone.', 'Koduthu', 'KO-DU-THU', 'Koduthu = Gave', ['koduthu']),
        ('e2_11', 'Thudangi', 'തുടങ്ങി', 'Started', 'The movie started or rain began.', 'Aarambhichu', 'THU-DANG-I', 'Thudangi = Started', ['thudangi']),
        ('e2_12', 'Theernnu', 'തീർന്നു', 'Finished', 'The food is finished or petrol ran out.', 'Theernnupoyi', 'THEER-NNU', 'Theernnu = Finished', ['theernnu']),
        ("e2_13", "Innale", "ഇന്നലെ", "Yesterday", "Reference to yesterday's events.", "Innale", "IN-NA-LAY", "Innale = Yesterday", ["innale"]),
        ("e2_14", "Innu", "ഇന്ന്", "Today", "Reference to today.", "Innu", "IN-NU", "Innu = Today", ["innu"]),
        ('e2_15', 'Nerathe', 'നേരത്തെ', 'Earlier', 'Woke up early this morning.', 'Nerathe', 'NAY-RA-THAY', 'Nerathe = Earlier', ['nerathe']),
        ('e2_16', 'Vaiki', 'വൈകി', 'Was late', 'Traffic was delayed or running late.', 'Thaamasichu', 'VAI-KI', 'Vaiki = Was late', ['vaiki']),
        ("e2_17", "Ravile", "രാവിലെ", "In the morning", "Morning routine time anchor.", "Prabhathathil", "RAA-VI-LAY", "Ravile = In the morning", ["ravile"]),
        ('e2_18', 'Uchakku', 'ഉച്ചയ്ക്ക്', 'At noon', 'Noon meal and midday heat.', 'Uchakkalathu', 'U-CHAK-KU', 'Uchakku = At noon', ['uchakku']),
        ('e2_19', 'Vaikittu', 'വൈകിട്ട്', 'In the evening', 'Tea time and stroll time around 5 PM.', 'Sandhyakku', 'VAI-KIT-TU', 'Vaikittu = In the evening', ['vaikittu']),
        ('e2_20', 'Rathri', 'രാത്രി', 'Night', 'Dinner and sleep time.', 'Raathriyil', 'RAA-THRI', 'Rathri = Night', ['rathri']),
        ('e2_21', 'Ippo', 'ഇപ്പോ', 'Now', "Colloquial Central Travancore form of textbook 'Ippol'.", 'Ippol', 'IP-POH', 'Ippo = Now', ['ippo']),
        ('e2_22', 'Pinne', 'പിന്നെ', 'Later', 'Doing something afterwards.', 'Pinneedu', 'PIN-NAY', 'Pinne = Later', ['pinne']),
        ('e2_23', 'Kazhinja aazhcha', 'കഴിഞ്ഞ ആഴ്ച', 'Last week', 'Looking back at the previous week.', 'Kazhinja aazhcha', 'KA-ZHI-NJA AAZH-CHA', 'Kazhinja aazhcha = Last week', ['kazhinja aazhcha']),
        ('e2_24', 'Kulichu', 'കുളിച്ചു', 'Took a bath', 'Very important daily routine update in Kerala.', 'Snanam cheythu', 'KU-LI-CHU', 'Kulichu = Took a bath', ['kulichu']),
        ('e2_25', 'Vannirunnu', 'വന്നിരുന്നു', 'Had come', 'They visited earlier in the afternoon.', 'Vannirunnu', 'VAN-NI-RUN-NU', 'Vannirunnu = Had come', ['vannirunnu']),
        ('e2_26', 'Poyirunnu', 'പോയിരുന്നു', 'Had gone earlier', 'Had gone to town earlier in the morning.', 'Poyirunnu', 'POH-YI-RUN-NU', 'Poyirunnu = Had gone earlier', ['poyirunnu']),
        ('e2_27', 'Njan paranju', 'ഞാൻ പറഞ്ഞു', 'I said', 'Recounting what you said in a conversation.', 'Njan paranju', 'NYAAN PA-RAN-JU', 'Njan paranju = I said', ['njan paranju', 'njaan paranju']),
        ('e2_28', 'Avar vannu', 'അവർ വന്നു', 'They arrived', "Announcing guests' arrival.", 'Avar vannu', 'A-VAR VAN-NU', 'Avar vannu = They arrived', ['avar vannu']),
        ('e2_29', 'Palli-yil poyi', 'പള്ളിയിൽ പോയി', 'Went to church', 'Heart of Sunday morning routine in Central Travancore.', 'Devalayathil poyi', 'PAL-LI-YIL POH-YI', 'Palli-yil poyi = Went to church', ['palli-yil poyi', 'palliyil poyi']),
        ('e2_30', 'Chaya kudichu', 'ചായ കുടിച്ചു', 'Drank tea', 'Reply when asked if you had your evening tea.', 'Chaya kudichu', 'CHAA-YA KU-DI-CHU', 'Chaya kudichu = Drank tea', ['chaya kudichu', 'chaaya kudichu'])
    ]

    # Level 6: Elementary 3 - Future Intentions & Casual Commitments (25 words)
    elem_3 = [
        ('e3_01', 'Varaam', 'വരാം', 'I will come', 'Warm, casual commitment to visit or come over.', 'Njan varaam', 'VA-RAAM', 'Varaam = I will come', ['varaam', 'varam']),
        ('e3_02', 'Povaam', 'പോകാം', "Let's go", 'Casual agreement to set out or travel.', 'Pokaam', 'POH-VAAM', "Povaam = Let's go", ['povaam', 'pokam']),
        ('e3_03', 'Kazhikkaam', 'കഴിക്കാം', "Let's eat", 'Agreeing to sit down for a meal.', 'Kazhikkaam', 'KA-ZHIK-KAAM', "Kazhikkaam = Let's eat", ['kazhikkaam']),
        ('e3_04', 'Nokkam', 'നോക്കാം', "Let's see", "Polite, non-committal way to say 'Let me look into it'.", 'Njan nokkaam', 'NOK-KAAM', "Nokkam = Let's see", ['nokkam', 'nokkaam']),
        ('e3_05', 'Cheyyaam', 'ചെയ്യാം', "I'll do it", 'Agreeing cheerfully to handle a chore or errand.', 'Cheyyaam', 'CHEY-YAAM', "Cheyyaam = I'll do it", ['cheyyaam', 'cheyyam']),
        ('e3_06', 'Tharaam', 'തരാം', "I'll give it to you", 'Promising to hand over an item or money.', 'Tharaam', 'THA-RAAM', "Tharaam = I'll give it to you", ['tharaam', 'tharam']),
        ('e3_07', 'Parayaam', 'പറയാം', 'I will tell you', 'Promising to share information later.', 'Parayaam', 'PA-RA-YAAM', 'Parayaam = I will tell you', ['parayaam', 'parayam']),
        ('e3_08', 'Kelkkaam', 'കേൾക്കാം', "I'll listen", "Agreeing to hear someone's story or song.", 'Kaelkkaam', 'KAYL-KAAM', "Kelkkaam = I'll listen", ['kelkkaam']),
        ("e3_09", "Naale", "നാളെ", "Tomorrow", "Key anchor for all future plans.", "Naale", "NAA-LAY", "Naale = Tomorrow", ["naale"]),
        ('e3_10', 'Mattannaal', 'മറ്റന്നാൾ', 'Day after tomorrow', '', '', '', 'Mattannaal = Day after tomorrow', ['mattannaal']),
        ('e3_11', 'Ini', 'ഇനി', 'From now on', 'Transitioning to what happens next.', 'Ini', 'I-NI', 'Ini = From now on', ['ini']),
        ('e3_12', 'Varum', 'വരും', 'Will come', 'He/She/They will arrive later.', 'Varum', 'VA-RUM', 'Varum = Will come', ['varum']),
        ('e3_13', 'Pokum', 'പോകും', 'Will go', 'The bus will go or Dad will go.', 'Pokum', 'POH-KUM', 'Pokum = Will go', ['pokum']),
        ("e3_14", "Cheyyum", "ചെയ്യും", "Will do", "Ammachi will make lunch or he will do it.", "Cheyyum", "CHEY-YUM", "Cheyyum = Will do", ["cheyyum"]),
        ("e3_15", "Kazhikkum", "കഴിക്കും", "Will eat", "They will eat after everyone arrives.", "Kazhikkum", "KA-ZHIK-KUM", "Kazhikkum = Will eat", ["kazhikkum"]),
        ('e3_16', 'Undaakkum', 'ഉണ്ടാക്കും', 'Will make', 'Making tea, snacks, or dinner.', 'Nirrmikkum', 'UN-DAAK-KUM', 'Undaakkum = Will make', ['undaakkum']),
        ('e3_17', 'Varoolla', 'വരൂല്ല', "Won't come", "Classic spoken contraction for textbook 'varunnilla'.", 'Varunnilla', 'VA-ROOL-LA', "Varoolla = Won't come", ['varoolla', 'varula']),
        ('e3_18', 'Pokoolla', 'പോകൂല്ല', "Won't go", "Regional spoken contraction for 'won't go'.", 'Pokunnilla', 'POH-KOOL-LA', "Pokoolla = Won't go", ['pokoolla']),
        ('e3_19', 'Cheyyoolla', 'ചെയ്യൂല്ല', "Won't do", '', '', '', "Cheyyoolla = Won't do", ['cheyyoolla']),
        ('e3_20', 'Pattilla', 'പറ്റില്ല', 'Cannot', 'Expressing inability or firm impossibility.', 'Saadhyamalla', 'PAT-TIL-LA', 'Pattilla = Cannot', ['pattilla']),
        ('e3_21', 'Naale kaanaam', 'നാളെ കാണാം', 'See you tomorrow!', 'Universal cheerful goodbye.', 'Naale kaanaam', 'NAA-LAY KAA-NAAM', 'Naale kaanaam = See you tomorrow!', ['naale kaanaam', 'naale kaanam']),
        ('e3_22', 'Ippo tharaam', 'ഇപ്പോ തരാം', 'I will give it right now', 'Reassuring immediate compliance.', 'Ippol tharaam', 'IP-POH THA-RAAM', 'Ippo tharaam = I will give it right now', ['ippo tharaam']),
        ('e3_23', 'Njan cheyyaam', 'ഞാൻ ചെയ്യാം', "I'll do it myself", 'Stepping up to help with family chores.', 'Njan cheyyാം', 'NYAAN CHEY-YAAM', "Njan cheyyaam = I'll do it myself", ['njan cheyyaam', 'njaan cheyyam']),
        ('e3_24', 'Povaan samayam aayi', 'പോകാൻ സമയം ആയി', "It's time to go", "Polite notice that it's time to depart.", 'Purapedan samayamayi', 'POH-VAAN SA-MA-YAM AA-YI', "Povaan samayam aayi = It's time to go", ['povaan samayam aayi']),
        ('e3_25', 'Pinne kaanaam', 'പിന്നെ കാണാം', 'See you later!', 'Casual friendly goodbye when heading out.', 'Pinneedu kaanaam', 'PIN-NAY KAA-NAAM', 'Pinne kaanaam = See you later!', ['pinne kaanaam'])
    ]

    # Level 7: Elementary 4 - Describing Physical States & Desires (25 words)
    elem_4 = [
        ('e4_01', 'Choodu', 'ചൂട്', 'Heat', 'Describing hot tea, spicy food, or humid weather.', 'Ushnam', 'CHOO-DU', 'Choodu = Heat', ['choodu', 'chood']),
        ('e4_02', 'Thanuppu', 'തണുപ്പ്', 'Cold', '', '', '', 'Thanuppu = Cold', ['thanuppu']),
        ('e4_03', 'Urakkam varunnu', 'ഉറക്കം വരുന്നു', 'Feeling sleepy', 'Admitting you are nodding off after dinner.', 'Nidra thonnunnu', 'U-RAK-KAM VA-RUN-NU', 'Urakkam varunnu = Feeling sleepy', ['urakkam varunnu']),
        ('e4_04', 'Sheenam', 'ക്ഷീണം', 'Tiredness', 'Feeling fatigued after a long journey or day.', 'Thalarcha', 'KSHEE-NAM', 'Sheenam = Tiredness', ['sheenam', 'ksheenam']),
        ('e4_05', 'Vayya', 'വയ്യ', 'Feeling unwell', 'Crucial home phrase to state you are sick or physically unable.', 'Vayyathaaayi', 'VAY-YA', 'Vayya = Feeling unwell', ['vayya']),
        ("e4_06", "Dhaaham", "ദാഹം", "Thirst", "Desire for cool drinking water.", "Kudikkan thonnunnu", "DHAA-HAM", "Dhaaham = Thirst", ["dhaaham", "daaham"]),
        ('e4_07', 'Vedhana', 'വേദന', 'Pain', 'Describing physical bodily ache or hurt.', 'Vedhana', 'VAY-DHA-NA', 'Vedhana = Pain', ['vedhana']),
        ('e4_08', 'Thalavedhana', 'തലവേദന', 'Headache', 'Very common excuse or physical complaint.', 'Thala vedhana', 'THA-LA-VAY-DHA-NA', 'Thalavedhana = Headache', ['thalavedhana', 'thala vedhana']),
        ('e4_09', 'Vayaruvedana', 'വയറുവേദന', 'Stomach ache', '', '', '', 'Vayaruvedana = Stomach ache', ['vayaruvedana']),
        ('e4_10', 'Maduppu', 'മടുപ്പ്', 'Boredom', 'Expressing weariness of waiting or repetition.', 'Veruppu', 'MA-DUP-PU', 'Maduppu = Boredom', ['maduppu']),
        ('e4_11', 'Sukham', 'സുഖം', 'Well-being', "The heart of 'Sukhamano?' (How are you?).", 'Kshemam', 'SU-KHAM', 'Sukham = Well-being', ['sukham']),
        ('e4_12', 'Sukhamano?', 'സുഖമാണോ?', 'How are you?', 'Standard cordial greeting across Kerala.', 'Kshemamaano', 'SU-KHA-MAA-NOH', 'Sukhamano? = How are you?', ['sukhamano', 'sukhamano?']),
        ('e4_13', 'Enikku sukham aanu', 'എനിക്ക് സുഖമാണ്', 'I am doing well', 'Standard polite reply to greeting.', 'Njan sukhamayi irikkunnu', 'E-NIK-KU SU-KHAM AA-NU', 'Enikku sukham aanu = I am doing well', ['enikku sukham aanu']),
        ('e4_14', 'Pani', 'പനി', 'Fever', '', '', '', 'Pani = Fever', ['pani']),
        ("e4_15", "Chuma", "ചുമ", "Cough", "Dry or wet seasonal throat cough.", "Kasa", "CHU-MA", "Chuma = Cough", ["chuma"]),
        ('e4_16', 'Jaladosham', 'ജലദോഷം', 'Common cold', 'Nasal cold or sneezing.', 'Jaladosham', 'JA-LA-DOH-SHAM', 'Jaladosham = Common cold', ['jaladosham']),
        ("e4_17", "Marunnu", "മരുന്ന്", "Medicine", "Pill or herbal syrup from the pharmacy.", "Oushadham", "MA-RUN-NU", "Marunnu = Medicine", ["marunnu"]),
        ('e4_18', 'Kaalvedhana', 'കാൽവേദന', 'Leg pain', 'Ache from walking or standing long in church.', 'Kaal vedhana', 'KAAL-VAY-DHA-NA', 'Kaalvedhana = Leg pain', ['kaalvedhana']),
        ('e4_19', 'Choodu vellam', 'ചൂടുവെള്ളം', 'Hot water', 'Standard safe drinking water in every Kerala home (often with jeerakam).', 'Choodu vellam', 'CHOO-DU VEL-LAM', 'Choodu vellam = Hot water', ['choodu vellam']),
        ('e4_20', 'Vellam thaa', 'വെള്ളം താ', 'Give me some water', 'Simple daily domestic request.', 'Vellam tharoo', 'VEL-LAM THAA', 'Vellam thaa = Give me some water', ['vellam thaa']),
        ('e4_21', 'Enikku vayya', 'എനിക്ക് വയ്യ', "I'm not feeling well", 'Telling family you need to rest.', 'Enikku ashwasamilla', 'E-NIK-KU VAY-YA', "Enikku vayya = I'm not feeling well", ['enikku vayya']),
        ('e4_22', 'Ksheenam undu', 'ക്ഷീണം ഉണ്ട്', 'I feel tired', 'Explaining why you are resting quietly.', 'Ksheenamundu', 'KSHEE-NAM UN-DU', 'Ksheenam undu = I feel tired', ['ksheenam undu']),
        ('e4_23', 'Shanthatha', 'ശാന്തത', 'Peace', 'Peaceful atmosphere at home or in prayer.', 'Samaadhaanam', 'SHAAN-THA-THA', 'Shanthatha = Peace', ['shanthatha']),
        ('e4_24', 'Santhosham', 'സന്തോഷം', 'Happiness', 'Expressing delight at seeing relatives.', 'Aanandam', 'SAN-THOH-SHAM', 'Santhosham = Happiness', ['santhosham']),
        ('e4_25', 'Santhosham aayi', 'സന്തോഷമായി', 'Felt happy', 'Polite appreciation for hospitality or gifts.', 'Santhoshamaayi', 'SAN-THOH-SHAM AA-YI', 'Santhosham aayi = Felt happy', ['santhosham aayi'])
    ]

    # Level 8: Elementary 5 - Home, Chores & Daily Objects (25 words)
    elem_5 = [
        ("e5_01", "Vaathil", "വാതിൽ", "Door", "Main wooden front door of the house.", "Kavaatam", "VAA-THIL", "Vaathil = Door", ["vaathil", "vathil"]),
        ("e5_02", "Janala", "ജനൽ", "Window", "Open window letting in the garden breeze.", "Janal", "JA-NA-LA", "Janala = Window", ["janala", "janal"]),
        ('e5_03', 'Kaasu', 'കാശ്', 'Money', 'Loose change, auto fare, or rupee notes.', 'Panam', 'KAA-SHU', 'Kaasu = Money', ['kaasu', 'kaash']),
        ('e5_04', 'Phone', 'ഫോൺ', 'Phone', 'Smartphone for calls and WhatsApp messages.', 'Phone', 'PHONE', 'Phone = Phone', ['phone']),
        ('e5_05', 'Thaakkol', 'താക്കോൽ', 'Key', '', '', '', 'Thaakkol = Key', ['thaakkol']),
        ('e5_06', 'Vandi', 'വണ്ടി', 'Vehicle', 'Any vehicle or family car.', 'Vaahanam', 'VAN-DI', 'Vandi = Vehicle', ['vandi']),
        ('e5_07', 'Bhaagyam', 'ഭാഗ്യം', 'Luck', "Expressing relief: 'What good luck!'.", 'Bhaagyam', 'BHAA-GYAM', 'Bhaagyam = Luck', ['bhaagyam', 'bhagyam']),
        ('e5_08', 'Light', 'ലൈറ്റ്', 'Light', 'Room light or tube light.', 'Velicham', 'LIGHT', 'Light = Light', ['light']),
        ('e5_09', 'Fan', 'ഫാൻ', 'Fan', 'Ceiling fan keeping the hall breezy.', 'Fan', 'FAN', 'Fan = Fan', ['fan']),
        ('e5_10', 'Pusthakam', 'പുസ്തകം', 'Book', 'Reading book, schoolbook, or Scripture.', 'Grantham', 'PUS-THA-KAM', 'Pusthakam = Book', ['pusthakam']),
        ('e5_11', 'Mesha', 'മേശ', 'Table', '', '', '', 'Mesha = Table', ['mesha']),
        ("e5_12", "Kasera", "കസേര", "Chair", "Wooden or plastic patio chair.", "Kasera", "KA-SAY-RA", "Kasera = Chair", ["kasera"]),
        ('e5_13', 'Thuni', 'തുണി', 'Cloth', 'Fresh clothes or laundry hanging to dry.', 'Vasthram', 'THU-NI', 'Thuni = Cloth', ['thuni']),
        ('e5_14', 'Paathram', 'പാത്രം', 'Vessel', 'Cooking pot or dinner plate to be washed.', 'Pathram', 'PAA-THRAM', 'Paathram = Vessel', ['paathram', 'pathram']),
        ("e5_15", "Adukkala", "അടുക്കള", "Kitchen", "Where Ammachi brews tea and makes appam.", "Paachakapura", "A-DUK-KA-LA", "Adukkala = Kitchen", ["adukkala"]),
        ('e5_16', 'Muttam', 'മുറ്റം', 'Courtyard', 'Swept front yard with potted plants and trees.', 'Muttam', 'MUT-TAM', 'Muttam = Courtyard', ['muttam']),
        ('e5_17', 'Purathu', 'പുറത്ത്', 'Outside', 'Stepping out onto the veranda.', 'Purathu', 'PU-RA-THU', 'Purathu = Outside', ['purathu']),
        ('e5_18', 'Vaathil adaykku', 'വാതിൽ അടയ്ക്ക്', 'Shut the door', '', '', '', 'Vaathil adaykku = Shut the door', ['vaathil adaykku']),
        ('e5_19', 'Light idu', 'ലൈറ്റ് ഇട്', 'Turn on the light', 'Switching on the room light as dusk falls.', 'Light iduka', 'LIGHT I-DU', 'Light idu = Turn on the light', ['light idu']),
        ('e5_20', 'Fan idu', 'ഫാൻ ഇട്', 'Turn on the fan', 'Asking someone to switch on the fan when warm.', 'Fan iduka', 'FAN I-DU', 'Fan idu = Turn on the fan', ['fan idu']),
        ('e5_21', 'Thaakkol evide?', 'താക്കോൽ എവിടെ?', 'Where is the key?', '', '', '', 'Thaakkol evide? = Where is the key?', ['thaakkol evide?']),
        ('e5_22', 'Phone pravarthikkunnilla', 'ഫോൺ പ്രവർത്തിക്കുന്നില്ല', "The phone isn't working", '', '', '', "Phone pravarthikkunnilla = The phone isn't working", ['phone pravarthikkunnilla']),
        ('e5_23', 'Vandi edukku', 'വണ്ടി എടുക്ക്', 'Take out the vehicle', 'Getting the car or scooter ready to head out.', 'Vandi edukkoo', 'VAN-DI E-DUK-KU', 'Vandi edukku = Take out the vehicle', ['vandi edukku']),
        ('e5_24', 'Paathram kazhuku', 'പാത്രം കഴുകു', 'Wash the dishes', 'Helping clean up after evening meal.', 'Paathram kazhukuka', 'PAA-THRAM KA-ZHU-KU', 'Paathram kazhuku = Wash the dishes', ['paathram kazhuku']),
        ('e5_25', 'Nalla bhaagyam', 'നല്ല ഭാഗ്യം', 'Great luck!', 'Celebrating an avoided traffic jam or good news.', 'Vallya bhagyam', 'NAL-LA BHAA-GYAM', 'Nalla bhaagyam = Great luck!', ['nalla bhaagyam'])
    ]

    return elem_1 + elem_2 + elem_3 + elem_4 + elem_5

print("Elementary phrases ready: 130 phrases (5 levels).")
