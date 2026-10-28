# Data generator for 24-level Malayalam curriculum
import json

def get_novice_phrases():
    # Level 0: Novice 1 - Kinship & Addressing People (25 words)
    novice_1 = [
        ('n0_01', 'Njaan', 'ഞാൻ', 'I', 'The most fundamental pronoun in Malayalam.', 'Njan', 'NYAAN', 'Njaan = I', ['njaan', 'njan', 'nyan']),
        ('n0_02', 'Nee', 'നീ', 'You (casual)', 'Used with siblings, peers, or close loved ones at home.', 'Nee', 'NEE', 'Nee = You (casual)', ['nee', 'ni']),
        ('n0_03', 'Ningal', 'നിങ്ങൾ', 'You (respectful)', 'Respectful form used for elders, parents, in-laws, or guests.', 'Ningal', 'NIN-GAL', 'Ningal = You (respectful)', ['ningal', 'ningall']),
        ('n0_04', 'Njangal', 'ഞങ്ങൾ', 'We (excluding you)', "We (excluding the listener) — e.g. 'we are coming'.", 'Njangal', 'NYANG-AL', 'Njangal = We (excluding you)', ['njangal', 'njangall']),
        ('n0_05', 'Nammal', 'നമ്മൾ', 'We (including you)', 'We (including the listener) — very friendly and unifying.', 'Nammal', 'NAM-MAL', 'Nammal = We (including you)', ['nammal', 'nammall']),
        ('n0_06', 'Avan', 'അവൻ', 'He', 'Referring to a brother, younger male, or male peer.', 'Avan', 'A-VAN', 'Avan = He', ['avan']),
        ('n0_07', 'Aval', 'അവൾ', 'She', 'Referring to a sister, younger female, or female peer.', 'Aval', 'A-VAL', 'Aval = She', ['aval']),
        ('n0_08', 'Avar', 'അവർ', 'They', 'Used for elders respectfully or groups of people.', 'Avar', 'A-VAR', 'Avar = They', ['avar']),
        ('n0_09', 'Appa', 'അപ്പാ', 'Dad', '', '', '', 'Appa = Dad', ['appa']),
        ('n0_10', 'Amma', 'അമ്മ', 'Mom', 'Universal warm address for Mother across Kerala.', 'Amma', 'AM-MA', 'Amma = Mom', ['amma', 'ammaa']),
        ('n0_11', 'Achayan', 'അച്ചായൻ', 'Husband', 'Affectionate address for a husband in this family setting. Also used for a brother-in-law or an older male relative; usage varies by family.', 'Bharthaavu', 'A-CHA-YAN', 'Achayan = Husband', ['achayan', 'achaya']),
        ('n0_12', 'Chechi', 'ചേച്ചി', 'Older sister', 'Warm, universal address for elder sister or older female cousin.', 'Chechi', 'CHAY-CHI', 'Chechi = Older sister', ['chechi', 'chechii']),
        ('n0_13', 'Ammachi', 'അമ്മച്ചി', 'Grandmother', '', '', '', 'Ammachi = Grandmother', ['ammachi']),
        ('n0_14', 'Appachan', 'അപ്പച്ചൻ', 'Grandfather', 'Grandfather / respected patriarch of the family.', 'Muthashan', 'AP-PAA-CHAN', 'Appachan = Grandfather', ['appachan']),
        ('n0_15', 'Chettan', 'ചേട്ടൻ', 'Older brother', 'Older brother; Chetta is the form used when addressing him.', 'Chettan', 'CHET-TAN', 'Chettan = Older brother', ['chettan', 'chetta']),
        ('n0_16', 'Aniyan', 'അനിയൻ', 'Younger Brother', 'Younger brother or male cousin.', 'Anujan', 'A-NI-YAN', 'Aniyan = Younger Brother', ['aniyan']),
        ('n0_17', 'Aniyathi', 'അനിയത്തി', 'Younger Sister', 'Younger sister or female cousin.', 'Anujathi', 'A-NI-YA-THI', 'Aniyathi = Younger Sister', ['aniyathi']),
        ('n0_18', 'Ammayi', 'അമ്മായി', 'Aunt', 'Aunt or mother-in-law in family circle.', 'Ammayi', 'AM-MAA-YI', 'Ammayi = Aunt', ['ammayi']),
        ('n0_19', 'Ammavan', 'അമ്മാവൻ', 'Maternal Uncle', 'Maternal uncle or respected elder uncle.', 'Ammavan', 'AM-MAA-VAN', 'Ammavan = Maternal Uncle', ['ammavan']),
        ('n0_20', 'Aliyā', 'അളിയാ', 'Brother-in-law', 'Most famous Kerala colloquial greeting for brother-in-law or closest friend.', 'Aliyan', 'A-LI-YAA', 'Aliyā = Brother-in-law', ['aliya', 'aliyaa', 'aliyan']),
        ("n0_21", "Naathoon", "നാത്തൂൻ", "Sister-in-law", "Sister-in-law (husband's sister or brother's wife).", "Naathoon", "NAA-THOON", "Naathoon = Sister-in-law", ["naathoon", "nathoon"]),
        ('n0_22', 'Mon', 'മോൻ', 'Son', 'Affectionate term used by parents and elders for son or boy.', 'Makan', 'MOAN', 'Mon = Son', ['mon', 'mone']),
        ('n0_23', 'Mole', 'മോളേ', 'Daughter', '', '', '', 'Mole = Daughter', ['mole']),
        ("n0_24", "Kudumbam", "കുടുംബം", "Family", "Core concept of the joint household and extended lineage.", "Kudumbam", "KU-DUM-BAM", "Kudumbam = Family", ["kudumbam"]),
        ('n0_25', 'Bandhukkal', 'ബന്ധുക്കൾ', 'Relatives', 'Extended family network and visiting kin.', 'Bandhukkal', 'BAN-DHUK-KAL', 'Bandhukkal = Relatives', ['bandhukkal', 'bandhukal'])
    ]

    # Level 1: Novice 2 - Kitchen Essentials & Food Preferences (25 words)
    novice_2 = [
        ('n1_01', 'Choru', 'ചോറ്', 'Cooked rice', 'The centerpiece of every Malayali meal.', 'Choru', 'CHO-RU', 'Choru = Cooked rice', ['choru', 'chor']),
        ("n1_02", "Vellam", "വെള്ളം", "Water", "Water for drinking or washing.", "Vellam", "VEL-LAM", "Vellam = Water", ["vellam"]),
        ("n1_03", "Chaaya", "ചായ", "Tea", "The lifeblood of Kerala hospitality; always offered to guests.", "Chaya", "CHAA-YA", "Chaaya = Tea", ["chaaya", "chaya"]),
        ("n1_04", "Kaappi", "കാപ്പി", "Coffee", "Filter or boiled morning coffee.", "Kaappi", "KAAP-PI", "Kaappi = Coffee", ["kaappi", "kappi", "coffee"]),
        ("n1_05", "Meen", "മീൻ", "Fish", "Staple protein in Syrian Christian and coastal households.", "Matsyam", "MEEN", "Meen = Fish", ["meen"]),
        ('n1_06', 'Erachi', 'ഇറച്ചി', 'Meat', 'Meat curry or roast, hallmark of Central Travancore feasts.', 'Mamsam', 'E-RA-CHI', 'Erachi = Meat', ['erachi', 'irachi']),
        ('n1_07', 'Appam', 'അപ്പം', 'Rice pancake', 'Lacy fermented rice pancake served with stew.', 'Appam', 'AP-PAM', 'Appam = Rice pancake', ['appam']),
        ('n1_08', 'Puttu', 'പുട്ട്', 'Steamed Rice Cake', 'Iconic cylinder-steamed breakfast staple.', 'Puttu', 'PUT-TU', 'Puttu = Steamed Rice Cake', ['puttu']),
        ('n1_09', 'Kadala', 'കടല', 'Chickpeas', 'Chickpeas. Kadala curry is the cooked dish.', 'Kadala', 'KA-DA-LA', 'Kadala = Chickpeas', ['kadala']),
        ('n1_10', 'Parotta', 'പൊറോട്ട', 'Flaky Flatbread', 'Crispy layered Malabar/Kerala street parotta.', 'Parotta', 'PA-ROT-TA', 'Parotta = Flaky Flatbread', ['parotta', 'porotta']),
        ('n1_11', 'Pazham', 'പഴം', 'Banana', 'Small sweet yellow banana eaten after every feast.', 'Pazham', 'PA-ZHAM', 'Pazham = Banana', ['pazham']),
        ("n1_12", "Paal", "പാൽ", "Milk", "Fresh cow's milk for tea or porridge.", "Paal", "PAAL", "Paal = Milk", ["paal", "pal"]),
        ('n1_13', 'Moru', 'മോര്', 'Buttermilk', 'Buttermilk; it may be served plain or seasoned.', 'Moru', 'MO-RU', 'Moru = Buttermilk', ['moru', 'mor']),
        ('n1_14', 'Sambar', 'സാമ്പാർ', 'Lentil & Vegetable Stew', 'Essential feast curry made with toor dal and drumsticks.', 'Sambar', 'SAAM-BAAR', 'Sambar = Lentil & Vegetable Stew', ['sambar']),
        ("n1_15", "Achar", "അച്ചാർ", "Pickle", "Spicy mango, lime, or fish pickle on the side.", "Achar", "A-CHAAR", "Achar = Pickle", ["achar"]),
        ('n1_16', 'Venam', 'വേണം', 'Want', 'Positive expression of desire or requirement.', 'Aavashyamaanu', 'VAY-NAM', 'Venam = Want', ['venam', 'vaenam']),
        ('n1_17', 'Venda', 'വേണ്ട', "Don't want", 'Polite yet firm way to decline food refills.', 'Aavashyamilla', 'VAYN-DA', "Venda = Don't want", ['venda', 'vaenda']),
        ('n1_18', 'Mathi', 'മതി', 'Enough', 'Crucial word to stop Ammachi from piling more food on your plate!', 'Paryapthamaanu', 'MA-THI', 'Mathi = Enough', ['mathi']),
        ('n1_19', 'Kurachu', 'കുറച്ച്', 'A little bit', 'Ask for a modest portion at mealtime.', 'Alpam', 'KU-RA-CHU', 'Kurachu = A little bit', ['kurachu', 'kurach']),
        ('n1_20', 'Kooduthal', 'കൂടുതൽ', 'More', 'Ask for more curry, rice, or sugar.', 'Athikam', 'KOO-DU-THAL', 'Kooduthal = More', ['kooduthal']),
        ('n1_21', 'Theere venda', 'തീരെ വേണ്ട', "Don't want it at all", 'Firmly refuse when you cannot eat another bite.', 'Thadhavilla', 'THEE-RAY VAYN-DA', "Theere venda = Don't want it at all", ['theere venda', 'theerevenda']),
        ('n1_22', 'Kazhichu', 'കഴിച്ചു', 'Ate', "The standard reply when asked 'Food kazhicho?'.", 'Bhakshanam kazhichu', 'KA-ZHI-CHU', 'Kazhichu = Ate', ['kazhichu']),
        ('n1_23', 'Vishakkunnu', 'വിശക്കുന്നു', 'Feeling hungry', 'Tell family that you are hungry.', 'Vishappu thonnunnu', 'VI-SHAK-KUN-NU', 'Vishakkunnu = Feeling hungry', ['vishakkunnu', 'vishakunu']),
        ('n1_24', 'Madhuram', 'മധുരം', 'Sweetness', 'Sweetness or a sweet taste. The usual word for sugar is panchasaara.', 'Madhuram', 'MA-DHU-RAM', 'Madhuram = Sweetness', ['madhuram']),
        ('n1_25', 'Erivu', 'എരിവ്', 'Spiciness', 'Describing hot chili spices in curry.', 'Erivu', 'E-RI-VU', 'Erivu = Spiciness', ['erivu', 'eriv'])
    ]

    # Level 2: Novice 3 - The 15 Survival Verbs (25 words)
    novice_3 = [
        ('n2_01', 'Vaa', 'വാ', 'Come', 'Call someone to come over to the room or table.', 'Varu', 'VAA', 'Vaa = Come', ['vaa', 'va']),
        ('n2_02', 'Po', 'പോ', 'Go', 'Tell someone to go ahead or leave.', 'Poku', 'POH', 'Po = Go', ['po', 'poh']),
        ('n2_03', 'Kazhikku', 'കഴിക്ക്', 'Eat', 'Warmly invite someone to start their meal.', 'Kazhikkuka', 'KA-ZHIK-KU', 'Kazhikku = Eat', ['kazhikku', 'kazhiku']),
        ('n2_04', 'Kudi', 'കുടി', 'Drink', 'Telling someone to drink their tea or water.', 'Kudikkuka', 'KU-DI', 'Kudi = Drink', ['kudi']),
        ('n2_05', 'Iri', 'ഇരി', 'Sit', 'Warmly welcome someone into the living room.', 'Irikkuka', 'I-RI', 'Iri = Sit', ['iri']),
        ('n2_06', 'Para', 'പറ', 'Say', "Prompting someone: 'Tell me what happened!'.", 'Parayuka', 'PA-RA', 'Para = Say', ['para']),
        ('n2_07', 'Kelkku', 'കേൾക്ക്', 'Listen', 'Asking someone to listen to what is being said.', 'Kaelkkuka', 'KAYL-KU', 'Kelkku = Listen', ['kelkku', 'kaelkku']),
        ('n2_08', 'Thaa', 'താ', 'Give me', 'Hand me that cup, plate, or phone.', 'Tharuka', 'THAA', 'Thaa = Give me', ['thaa', 'tha']),
        ('n2_09', 'Edu', 'എടു', 'Take', 'Take a snack, pick up your bag, or take a seat.', 'Edukka', 'E-DU', 'Edu = Take', ['edu']),
        ('n2_10', 'Cheyyu', 'ചെയ്യൂ', 'Do', '', '', '', 'Cheyyu = Do', ['cheyyu']),
        ('n2_11', 'Nokku', 'നോക്ക്', 'Look', 'Telling someone to look at something or check on it.', 'Nokkuka', 'NOK-KU', 'Nokku = Look', ['nokku']),
        ("n2_12", "Nadakku", "നടക്ക്", "Walk", "Going for a stroll down the village lane.", "Nadakkuka", "NA-DAK-KU", "Nadakku = Walk", ["nadakku"]),
        ("n2_13", "Urangu", "ഉറങ്ങ്", "Sleep", "Telling someone to go get some good rest.", "Uranguka", "U-RANG-U", "Urangu = Sleep", ["urangu"]),
        ('n2_14', 'Vaangikku', 'വാങ്ങിക്ക്', 'Buy', 'Buy something from the shop or take a gift.', 'Vaanguka', 'VAANG-IK-KU', 'Vaangikku = Buy', ['vaangikku', 'vangikku']),
        ('n2_15', 'Nilkku', 'നിൽക്ക്', 'Stand', 'Asking someone to wait a second or stand still.', 'Nilkkuka', 'NIL-KU', 'Nilkku = Stand', ['nilkku', 'nilku']),
        ('n2_16', 'Vaa povaam', 'വാ പോകാം', "Come, let's go", 'Super common phrase when heading out the door.', 'Varu pokaam', 'VAA POH-VAAM', "Vaa povaam = Come, let's go", ['vaa povaam', 'va povaam', 'vaa povaam']),
        ('n2_17', 'Ivide iri', 'ഇവിടെ ഇരി', 'Sit here', 'Offering someone a chair or couch spot beside you.', 'Ivide irikku', 'I-VI-DAY I-RI', 'Ivide iri = Sit here', ['ivide iri']),
        ('n2_18', 'Chaya kudi', 'ചായ കുടി', 'Drink tea', 'Encouraging a guest before the tea gets cold.', 'Chaya kudikku', 'CHAA-YA KU-DI', 'Chaya kudi = Drink tea', ['chaya kudi', 'chaaya kudi']),
        ('n2_19', 'Ippo para', 'ഇപ്പോ പറ', 'Tell me now', 'Eagerly asking for immediate news or story.', 'Ippol parayu', 'IP-PO PA-RA', 'Ippo para = Tell me now', ['ippo para']),
        ('n2_20', 'Athu thaa', 'അത് താ', 'Give me that', 'Pointing to an object and asking for it.', 'Athu tharoo', 'A-THU THAA', 'Athu thaa = Give me that', ['athu thaa']),
        ('n2_21', 'Ithu edu', 'ഇത് എടു', 'Take this', 'Handing someone a snack, plate, or item.', 'Ithu edukkoo', 'I-THU E-DU', 'Ithu edu = Take this', ['ithu edu']),
        ('n2_22', 'Onnu nokku', 'ഒന്ന് നോക്ക്', 'Take a quick look', 'Asking someone to inspect or review something.', 'Onnu nokkuka', 'ON-NU NOK-KU', 'Onnu nokku = Take a quick look', ['onnu nokku']),
        ('n2_23', 'Vegam cheyyu', 'വേഗം ചെയ്യ്', 'Do it quickly', 'Telling someone to hurry up an errand or chore.', 'Vegam cheyyuka', 'VAY-GAM CHEY-YU', 'Vegam cheyyu = Do it quickly', ['vegam cheyyu']),
        ('n2_24', 'Evide nilkkunnu', 'എവിടെ നിൽക്കുന്നു', 'Where are you standing?', 'Asking someone where to find them.', 'Evide nilkkunnu', 'E-VI-DAY NIL-KUN-NU', 'Evide nilkkunnu = Where are you standing?', ['evide nilkkunnu']),
        ('n2_25', 'Nannayi urangu', 'നന്നായി ഉറങ്ങ്', 'Sleep well', 'Warm affectionate sendoff before bed.', 'Nallavannam uranguka', 'NAN-NAA-YI U-RANG-U', 'Nannayi urangu = Sleep well', ['nannayi urangu'])
    ]

    # Level 3: Novice 4 - Fast Reactions & Conversational Fillers (25 words)
    novice_4 = [
        ('n3_01', 'Ayyo', 'അയ്യോ', 'Oh no!', 'The most iconic Kerala emotional reaction to anything surprising, sad, or shocking.', 'Ayyo', 'AY-YOH', 'Ayyo = Oh no!', ['ayyo', 'aiyo']),
        ('n3_02', 'Saramilla', 'സാരമില്ല', 'No problem', 'Reassuring an elder or friend when an accident or spill happens.', 'Kuzhappamilla', 'SAA-RA-MIL-LA', 'Saramilla = No problem', ['saramilla', 'saram illa']),
        ('n3_03', 'Sathyam', 'സത്യം', 'Really!', "Agreeing enthusiastically with someone's observation.", 'Sathyam thanne', 'SATH-YAM', 'Sathyam = Really!', ['sathyam']),
        ('n3_04', 'Aano?', 'ആണോ?', 'Is that so?', 'Conversational filler keeping the speaker talking.', 'Aano', 'AA-NOH', 'Aano? = Is that so?', ['aano', 'aano?']),
        ('n3_05', 'Sherikkum?', 'ശരിക്കും?', 'Really?', 'Expressing genuine surprise or astonishment.', 'Sathyamaano', 'SHE-RIK-KUM', 'Sherikkum? = Really?', ['sherikkum', 'sherikkum?']),
        ('n3_06', 'Pinne!', 'പിന്നെ!', 'Of course!', "Warm, enthusiastic agreement ('Well obviously!').", 'Pinne allaathe', 'PIN-NAY', 'Pinne! = Of course!', ['pinne', 'pinney']),
        ('n3_07', 'Athre ullu', 'അത്രേ ഉള്ളൂ', "That's all", 'Minimizing a difficulty or concluding an answer.', 'Athramathrame ullu', 'ATH-RAY UL-LOO', "Athre ullu = That's all", ['athre ullu', 'athreullu']),
        ('n3_08', 'Enthaa?', 'എന്താ?', 'What?', 'Quick spoken response when someone calls your name.', 'Enthaanu', 'EN-THAA', 'Enthaa? = What?', ['enthaa', 'entha']),
        ('n3_09', 'Athe', 'അതെ', 'Yes', 'Simple affirmative response to a factual question.', 'Athe', 'A-THAY', 'Athe = Yes', ['athe', 'adhae']),
        ('n3_10', 'Alla', 'അല്ല', 'No', 'Negative response denying an identity or fact.', 'Alla', 'AL-LA', 'Alla = No', ['alla']),
        ('n3_11', 'Kashtam', 'കഷ്ടം', 'What a pity!', 'Expressing empathy when hearing bad news.', 'Kashtam thanne', 'KASH-TAM', 'Kashtam = What a pity!', ['kashtam']),
        ('n3_12', 'Nannayi', 'നന്നായി', 'Well done!', 'Positive reinforcement for good news.', 'Nallathayi', 'NAN-NAA-YI', 'Nannayi = Well done!', ['nannayi']),
        ('n3_13', 'Enthoru', 'എന്തൊരു', 'What a…!', 'Exclaiming at something huge, delicious, or crowded.', 'Enthoru', 'EN-THO-RU', 'Enthoru = What a…!', ['enthoru']),
        ('n3_14', 'Aaha', 'ആഹാ', 'Aha!', 'Delighted exclamation upon tasting great food or seeing someone.', 'Aaha', 'AA-HAA', 'Aaha = Aha!', ['aaha', 'aha']),
        ('n3_15', 'Oh ho', 'ഓ ഹോ', 'Oh, I see!', 'Mild amused realization in family chatter.', 'Oh ho', 'OH HOH', 'Oh ho = Oh, I see!', ['oh ho', 'oho']),
        ('n3_16', 'Ente ponno!', 'എന്റെ പൊന്നോ!', 'My goodness!', 'Classic Central Travancore dramatic exclamation of shock or joy.', 'Ente daivame', 'EN-TAY PON-NOH', 'Ente ponno! = My goodness!', ['ente ponno', 'enteponno']),
        ('n3_17', 'Kollam', 'കൊള്ളാം', 'Nice', 'Giving approval to clothes, food, or a plan.', 'Nallathaayirikkunnu', 'KOL-LAAM', 'Kollam = Nice', ['kollam']),
        ('n3_18', 'Potte', 'പോട്ടെ', 'Let it be', 'Dropping a minor grievance or casual goodbye.', 'Poyikollatte', 'POT-TAY', 'Potte = Let it be', ['potte']),
        ('n3_19', 'Sheri', 'ശരി', 'Okay', 'The universal confirmation in Malayalam conversation.', 'Shariyannu', 'SHE-RI', 'Sheri = Okay', ['sheri']),
        ('n3_20', 'Ketto?', 'കേട്ടോ?', 'Did you hear?', 'The ubiquitous friendly tag at the end of sentences.', 'Ketto', 'KAYT-TOH', 'Ketto? = Did you hear?', ['ketto', 'ketto?']),
        ('n3_21', 'Scene illa', 'സീൻ ഇല്ല', 'No problem', "Modern youth slang for 'Don't worry about it at all'.", 'Kuzhappamilla', 'SCENE IL-LA', 'Scene illa = No problem', ['scene illa', 'sceneilla']),
        ('n3_22', 'Veruthe', 'വെറുതെ', 'Just like that', "Explaining that you are just sitting or didn't mean anything serious.", 'Vyarthamaayi', 'VE-RU-THAY', 'Veruthe = Just like that', ['veruthe']),
        ('n3_23', 'Mindalle', 'മിണ്ടല്ലേ', "Don't speak", 'Playfully shushing someone at home.', 'Mindaruthu', 'MIN-DAL-LAY', "Mindalle = Don't speak", ['mindalle']),
        ('n3_24', 'Shock aayi', 'ഷോക്ക് ആയി', 'Got shocked', '', '', '', 'Shock aayi = Got shocked', ['shock aayi']),
        ('n3_25', 'Nokkatte', 'നോക്കട്ടെ', 'Let me see', 'Buying time to inspect or think about a question.', 'Njan nokkikollatte', 'NOK-KAT-TAY', 'Nokkatte = Let me see', ['nokkatte'])
    ]

    return novice_1 + novice_2 + novice_3 + novice_4

print("Novice phrases ready: 100 phrases (4 levels x 25 phrases).")
