# Intermediate Data Generator (Tier 3: Levels 9 to 13, 130 words)

def get_intermediate_phrases():
    # Level 9: Intermediate 1 - Conjunctions & Sentence Connectors (25 words)
    int_1 = [
        ('i1_01', 'Ennittum', 'എന്നിട്ടും', 'Even then', "Connecting contrasting actions: 'I told him, yet he did it'.", 'Ennittum', 'EN-NIT-TUM', 'Ennittum = Even then', ['ennittum']),
        ('i1_02', 'Athukondu', 'അതുകൊണ്ട്', 'Therefore', 'Standard connector explaining a result or reason.', 'Athukondu', 'A-THU-KON-DU', 'Athukondu = Therefore', ['athukondu', 'adhukondu']),
        ('i1_03', 'Pakshe', 'പക്ഷേ', 'But', 'The quintessential contrastive conjunction.', 'Pakshe', 'PAK-SHAY', 'Pakshe = But', ['pakshe']),
        ('i1_04', 'Athinaale', 'അതിനാൽ', 'Hence', 'Slightly more formal equivalent of athukondu.', 'Athinaal', 'A-THI-NAA-LAY', 'Athinaale = Hence', ['athinaale', 'athinaal']),
        ('i1_05', 'Allenkil', 'അല്ലെങ്കിൽ', 'Otherwise', 'Presenting an alternative or warning.', 'Allenkil', 'AL-LENG-KIL', 'Allenkil = Otherwise', ['allenkil', 'allankil']),
        ('i1_06', 'Ennaalum', 'എന്നാലും', 'Even so', 'Expressing affectionate surprise or mild objection.', 'Ennaalum', 'EN-NAA-LUM', 'Ennaalum = Even so', ['ennaalum', 'ennalum']),
        ('i1_07', 'Athu maathramalla', 'അത് മാത്രമല്ല', 'Not only that', 'Adding another point to a story or argument.', 'Athu mathramalla', 'A-THU MAATH-RA-MAL-LA', 'Athu maathramalla = Not only that', ['athu maathramalla']),
        ('i1_08', 'Pinneyum', 'പിന്നെയും', 'Again', 'Describing something happening once more.', 'Meendum', 'PIN-NAY-YUM', 'Pinneyum = Again', ['pinneyum']),
        ('i1_09', 'Athupole', 'അതുപോലെ', 'Similarly', 'Drawing a comparison between two habits or recipes.', 'Athupole', 'A-THU-POH-LAY', 'Athupole = Similarly', ['athupole']),
        ('i1_10', 'Kaaranam', 'കാരണം', 'Because', 'Giving the reason why someone could not come.', 'Hetuvinaal', 'KAA-RA-NAM', 'Kaaranam = Because', ['kaaranam']),
        ('i1_11', 'Ennu vechaal', 'എന്ന് വെച്ചാൽ', 'That means…', 'Very conversational phrase clarifying meaning.', 'Athayathu', 'EN-NU VE-CHAAL', 'Ennu vechaal = That means…', ['ennu vechaal', 'ennuvechal']),
        ('i1_12', 'Athode', 'അതോടെ', 'With that', 'Narrating the next sequence of events.', 'Athode koodi', 'A-THOH-DAY', 'Athode = With that', ['athode']),
        ('i1_13', 'Athukondaanu', 'അതുകൊണ്ടാണ്', 'That is precisely why', 'Reinforcing the exact rationale for a choice.', 'Athukondaanu', 'A-THU-KON-DAA-NU', 'Athukondaanu = That is precisely why', ['athukondaanu']),
        ('i1_14', 'Engilum', 'എങ്കിലും', 'Even if', 'At least have a cup of tea before leaving.', 'Enkilum', 'ENG-I-LUM', 'Engilum = Even if', ['engilum', 'enkilum']),
        ('i1_15', 'Athrem', 'അത്രേം', 'That much', '', '', '', 'Athrem = That much', ['athrem']),
        ('i1_16', 'Ithrem', 'ഇത്രേം', 'This much', '', '', '', 'Ithrem = This much', ['ithrem']),
        ('i1_17', 'Avasanam', 'അവസാനം', 'Finally', 'Concluding a long journey or family tale.', 'Oduvil', 'A-VA-SAA-NAM', 'Avasanam = Finally', ['avasanam']),
        ('i1_18', 'Athukondu', 'അതുകൊണ്ട്', 'So', '', '', '', 'Athukondu = So', ['athukondu']),
        ('i1_19', 'Enthenkilum', 'എന്തെങ്കിലും', 'Something', 'Asking if anyone needs anything from the shop.', 'Ethegilum', 'EN-THENG-I-LUM', 'Enthenkilum = Something', ['enthenkilum']),
        ('i1_20', 'Eppozhenkilum', 'എപ്പോഴെങ്കിലും', 'Sometime', 'Inviting a relative to visit anytime.', 'Eppozhegilum', 'EP-PO-ZHENG-I-LUM', 'Eppozhenkilum = Sometime', ['eppozhenkilum']),
        ('i1_21', 'Evideyengilum', 'എവിടെയെങ്കിലും', 'Somewhere', 'Suggesting going somewhere to relax.', 'Evideyenkilum', 'E-VI-DAY-YENG-I-LUM', 'Evideyengilum = Somewhere', ['evideyengilum']),
        ('i1_22', 'Aarenkilum', 'ആരെങ്കിലും', 'Someone', 'Asking if anyone called or knocked.', 'Aarenkilum', 'AA-RENG-I-LUM', 'Aarenkilum = Someone', ['aarenkilum']),
        ('i1_23', 'Pakshe oru karyam', 'പക്ഷേ ഒരു കാര്യം', 'But one thing', 'Pausing conversation to make an important point.', 'Pakshe oru karyam', 'PAK-SHAY O-RU KAA-RYAM', 'Pakshe oru karyam = But one thing', ['pakshe oru karyam']),
        ('i1_24', 'Athukondu njan paranju', 'അതുകൊണ്ട് ഞാൻ പറഞ്ഞു', "So that's why I said", 'Explaining your past advice to family.', 'Athukondu njan paranju', 'A-THU-KON-DU NYAAN PA-RAN-JU', "Athukondu njan paranju = So that's why I said", ['athukondu njan paranju']),
        ('i1_25', 'Ennittum avan vannilla', 'എന്നിട്ടും അവൻ വന്നില്ല', "Even after that, he didn't come", 'Expressing mild exasperation at a delayed cousin.', 'Ennittum avan vannilla', 'EN-NIT-TUM A-VAN VAN-NIL-LA', "Ennittum avan vannilla = Even after that, he didn't come", ['ennittum avan vannilla'])
    ]

    # Level 10: Intermediate 2 - Conditional Statements ("If / When") (25 words)
    int_2 = [
        ('i2_01', 'Vannaal', 'വന്നാൽ', 'If someone comes', "Core conditional: 'If he comes, tell me'.", 'Varikayaanenkil', 'VAN-NAAL', 'Vannaal = If someone comes', ['vannaal', 'vannal']),
        ('i2_02', 'Paranjaal', 'പറഞ്ഞാൽ', 'If you say', "'If you tell Mom, she will make tea'.", 'Parayukayaanenkil', 'PA-RAN-JAAL', 'Paranjaal = If you say', ['paranjaal', 'paranjal']),
        ('i2_03', 'Kandappol', 'കണ്ടപ്പോൾ', 'When I saw', "Relating a past sight: 'When I saw him, he smiled'.", 'Kandappol', 'KAN-DAP-POL', 'Kandappol = When I saw', ['kandappol']),
        ('i2_04', 'Poyappol', 'പോയപ്പോൾ', 'When I went', "'When we went to church, rain started'.", 'Poyappol', 'POH-YAP-POL', 'Poyappol = When I went', ['poyappol']),
        ('i2_05', 'Cheythaal nannaayirikkum', 'ചെയ്താൽ നന്നായിരിക്കും', 'It would be good to do it', '', '', '', 'Cheythaal nannaayirikkum = It would be good to do it', ['cheythaal nannaayirikkum']),
        ('i2_06', 'Varumpol', 'വരുമ്പോൾ', 'When coming', "'Pick up some milk on your way home'.", 'Varumbol', 'VA-RUM-POL', 'Varumpol = When coming', ['varumpol', 'varumbol']),
        ('i2_07', 'Kazhichappol', 'കഴിച്ചപ്പോൾ', 'When I ate it', 'Complimenting the cooking upon trying it.', 'Kazhichappol', 'KA-ZHI-CHAP-POL', 'Kazhichappol = When I ate it', ['kazhichappol']),
        ('i2_08', 'Kelkkumbol', 'കേൾക്കുമ്പോൾ', 'When hearing', 'Emotional reaction to hearing family news.', 'Kaelkkumbol', 'KAYL-KUM-POL', 'Kelkkumbol = When hearing', ['kelkkumbol']),
        ('i2_09', 'Nokkiyaal', 'നോക്കിയാൽ', 'If you look', "'If you look inside the cupboard, it's there'.", 'Nokkukayaanenkil', 'NOK-KI-YAAL', 'Nokkiyaal = If you look', ['nokkiyaal', 'nokkiyal']),
        ('i2_10', 'Undenkil', 'ഉണ്ടെങ്കിൽ', 'If there is', "'If there is sugar, add one spoon'.", 'Undenkil', 'UN-DENG-KIL', 'Undenkil = If there is', ['undenkil']),
        ('i2_11', 'Illenkil', 'ഇല്ലെങ്കിൽ', 'If not', "'If not, we can buy it tomorrow'.", 'Illenkil', 'IL-LENG-KIL', 'Illenkil = If not', ['illenkil']),
        ('i2_12', 'Pattumenkil', 'പറ്റുമെങ്കിൽ', 'If possible', '', '', '', 'Pattumenkil = If possible', ['pattumenkil']),
        ('i2_13', 'Kettappol', 'കേട്ടപ്പോൾ', 'When I heard', 'Recounting your surprise upon hearing news.', 'Kettappol', 'KAYT-TAP-POL', 'Kettappol = When I heard', ['kettappol']),
        ('i2_14', 'Poyaal', 'പോയാൽ', 'If you go', "'If you go now, there won't be traffic'.", 'Pokukayaanenkil', 'POH-YAAL', 'Poyaal = If you go', ['poyaal', 'poyal']),
        ('i2_15', 'Cheythaal', 'ചെയ്താൽ', 'If you do', "'If you help out, we finish fast'.", 'Cheyyukayaanenkil', 'CHEY-THAAL', 'Cheythaal = If you do', ['cheythaal', 'cheythal']),
        ('i2_16', 'Kandaal', 'കണ്ടാൽ', 'If you see', "'If you see Achayan, tell him to call'.", 'Kaanukayaanenkil', 'KAN-DAAL', 'Kandaal = If you see', ['kandaal', 'kandal']),
        ('i2_17', 'Kudichaal', 'കുടിച്ചാൽ', 'If you drink', "'If you drink ginger tea, cold will go'.", 'Kudikkukayaanenkil', 'KU-DI-CHAAL', 'Kudichaal = If you drink', ['kudichaal']),
        ('i2_18', 'Choichaal', 'ചോദിച്ചാൽ', 'If you ask', "'If Dad asks, tell him we are at church'.", 'Chodhikkukayaanenkil', 'CHOI-CHAAL', 'Choichaal = If you ask', ['choichaal', 'chodichaal']),
        ('i2_19', 'Choichappol', 'ചോദിച്ചപ്പോൾ', 'When I asked', 'Relating what happened when you inquired.', 'Chodhichappol', 'CHOI-CHAP-POL', 'Choichappol = When I asked', ['choichappol']),
        ('i2_20', 'Vannaal mathi', 'വന്നാൽ മതി', "It's enough if you just come", 'Warmly insisting someone attend without gifts.', 'Vannaal mathi', 'VAN-NAAL MA-THI', "Vannaal mathi = It's enough if you just come", ['vannaal mathi']),
        ('i2_21', 'Nee paranjaal', 'നീ പറഞ്ഞാൽ', 'If you say so', "Trusting a loved one's judgment.", 'Nee paranjaal', 'NEE PA-RAN-JAAL', 'Nee paranjaal = If you say so', ['nee paranjaal']),
        ('i2_22', 'Ippo poyaal', 'ഇപ്പോ പോയാൽ', 'If we leave right now', 'Planning departure time around traffic.', 'Ippol poyaal', 'IP-POH POH-YAAL', 'Ippo poyaal = If we leave right now', ['ippo poyaal']),
        ('i2_23', 'Varumpol konduvaa', 'വരുമ്പോൾ കൊണ്ടുവരൂ', 'Bring it when you come', 'Asking someone to bring treats or groceries.', 'Varumbol konduvaruka', 'VA-RUM-POL KON-DU-VAA', 'Varumpol konduvaa = Bring it when you come', ['varumpol konduvaa']),
        ('i2_24', 'Samayam undenkil', 'സമയം ഉണ്ടെങ്കിൽ', 'If you have time', "Courteous way to request someone's company.", 'Samayamundenkil', 'SA-MA-YAM UN-DENG-KIL', 'Samayam undenkil = If you have time', ['samayam undenkil']),
        ('i2_25', 'Vayyaathaayaal', 'വയ്യാതായാൽ', 'If you feel unwell', '', '', '', 'Vayyaathaayaal = If you feel unwell', ['vayyaathaayaal'])
    ]

    # Level 11: Intermediate 3 - Teasing, Sarcasm & Informal Banter (30 words)
    int_3 = [
        ('i3_01', 'Thallu', 'തള്ള്', 'Bragging', 'Calling out someone telling boastful stories.', 'Athishayokthi', 'THAL-LU', 'Thallu = Bragging', ['thallu']),
        ('i3_02', 'Scene aanu', 'സീൻ ആണ്', "It's a problem", 'Modern youth slang for tense or dramatic situation.', 'Karyamaanu', 'SCENE AA-NU', "Scene aanu = It's a problem", ['scene aanu']),
        ('i3_03', 'Sceneilla', 'സീനില്ല', 'No issue', 'Telling a friend not to worry in the slightest.', 'Kuzhappamilla', 'SCENE-IL-LA', 'Sceneilla = No issue', ['sceneilla', 'scene illa']),
        ('i3_04', 'Pinnallathe!', 'പിന്നല്ലാതെ!', 'Of course!', 'Hearty, spirited affirmation among cousins.', 'Pinne allaathe', 'PIN-NAL-LA-THAY', 'Pinnallathe! = Of course!', ['pinnallathe', 'pinnallathe!']),
        ('i3_05', 'Jaada', 'ജാഡ', 'Pretension', 'Teasing someone acting overly formal or haughty.', 'Ahamkaaram', 'JAA-DA', 'Jaada = Pretension', ['jaada']),
        ('i3_06', 'Comedy aanu', 'കോമഡി ആണ്', "That's hilarious", 'Laughing off an absurd misunderstanding.', 'Haasyamaanu', 'CO-ME-DY AA-NU', "Comedy aanu = That's hilarious", ['comedy aanu']),
        ('i3_07', 'Scene maker', 'സീൻ മേക്കർ', 'Troublemaker', 'Teasing that one cousin who always stirs drama.', 'Kuzhappakkaaran', 'SCENE MA-KER', 'Scene maker = Troublemaker', ['scene maker']),
        ('i3_08', 'Pottatharam', 'പൊട്ടത്തരം', 'Silly nonsense', 'Laughing at a silly blunder made at home.', 'Muddatharam', 'POT-TA-THA-RAM', 'Pottatharam = Silly nonsense', ['pottatharam']),
        ('i3_09', 'Aano ketto!', 'ആണോ കേട്ടോ!', 'Oh, really!', 'Teasing reaction to someone acting smart.', 'Appadiyo', 'AA-NOH KAYT-TOH', 'Aano ketto! = Oh, really!', ['aano ketto']),
        ('i3_10', 'Enthonnedey?', 'എന്തോന്നടേയ്?', 'What on earth, man?!', 'Authentic Pathanamthitta/Adoor regional friendly banter.', 'Enthaanithu', 'EN-THON-NAY-DAY', 'Enthonnedey? = What on earth, man?!', ['enthonnedey', 'enthonnadey']),
        ('i3_11', 'Alambakkaruthu', 'അലമ്പാക്കരുത്', "Don't make trouble", 'Warning someone not to spoil the fun plan.', 'Nashippikkaruthu', 'A-LAM-BAK-KA-RU-THU', "Alambakkaruthu = Don't make trouble", ['alambakkaruthu', 'alambakaruthu']),
        ('i3_12', 'Dialogue adikkalle', 'ഡയലോഗ് അടിക്കല്ലേ', 'Stop making speeches', 'Teasing someone being overly dramatic.', 'Veedambam parayaruthu', 'DI-A-LOGUE A-DIK-KAL-LAY', 'Dialogue adikkalle = Stop making speeches', ['dialogue adikkalle']),
        ('i3_13', 'Oola', 'ഓള', 'Lame', 'Informal slang describing a poor plan or bad joke.', 'Thaazhnnatharam', 'OO-LA', 'Oola = Lame', ['oola']),
        ('i3_14', 'Kidu', 'കിടു', 'Awesome', 'Praised widely by youth for excellent food or movie.', 'Adipoli', 'KI-DU', 'Kidu = Awesome', ['kidu']),
        ('i3_15', 'Chank', 'ചങ്ക്', 'Close friend', '', '', '', 'Chank = Close friend', ['chank']),
        ('i3_16', 'Mass', 'മാസ്സ്', 'Impressive', 'Describing a cool entry or decisive action.', 'Prasashtham', 'MASS', 'Mass = Impressive', ['mass']),
        ('i3_17', 'Chali', 'ചളി', 'Corny joke', 'Groaning at a pun that fell flat.', 'Chali', 'CHA-LI', 'Chali = Corny joke', ['chali']),
        ('i3_18', 'Pani paali', 'പണി പാളി', 'The plan backfired badly!', 'When a prank or shortcut goes terribly wrong.', 'Aabaththil chaadi', 'PA-NI PAA-LI', 'Pani paali = The plan backfired badly!', ['pani paali', 'panipaali']),
        ('i3_19', 'Show kaanikkalle', 'ഷോ കാണിക്കല്ലേ', 'Stop showing off!', 'Telling someone to tone down their swagger.', 'Ahamkaaram kaanikaruthu', 'SHOW KAA-NIK-KAL-LAY', 'Show kaanikkalle = Stop showing off!', ['show kaanikkalle']),
        ('i3_20', 'Thallu kooduthal', 'തള്ള് കൂടുതൽ', 'Too much bragging happening here!', 'Playfully rolling your eyes at a big story.', 'Athishayokthi kooduthal', 'THAL-LU KOO-DU-THAL', 'Thallu kooduthal = Too much bragging happening here!', ['thallu kooduthal']),
        ('i3_21', 'Nee puliyaanu', 'നീ പുലിയാണ്', "You're brilliant!", 'Complimenting someone on a clever feat.', 'Nee samarthanaanu', 'NEE PU-LI-YAA-NU', "Nee puliyaanu = You're brilliant!", ['nee puliyaanu', 'nee puli']),
        ('i3_22', 'Veruthe alla', 'വെറുതെയല്ല', 'No wonder!', 'Realizing why someone behaved a certain way.', 'Athinalaayirikkum', 'VE-RU-THAY AL-LA', 'Veruthe alla = No wonder!', ['veruthe alla', 'verutheyalla']),
        ('i3_23', 'Enthuvaadey ithu?', 'എന്തുവാടേയ് ഇത്?', "What's this, man?", 'Central Travancore exclamation of amused bewilderment.', 'Enthaanithu', 'EN-THU-VAA-DAY I-THU', "Enthuvaadey ithu? = What's this, man?", ['enthuvaadey ithu', 'enthuvaadey']),
        ('i3_24', 'Vayaru niranju', 'വയറു നിറഞ്ഞു', "I'm full", 'Satisfied sigh after a hearty home meal.', 'Thriptiyaayi', 'VA-YA-RU NI-RAN-JU', "Vayaru niranju = I'm full", ['vayaru niranju']),
        ('i3_25', 'Njan sammathichu', 'ഞാൻ സമ്മതിച്ചു', 'I agree', 'Playfully conceding an argument to your girlfriend.', 'Njan anuvadichu', 'NYAAN SAM-MA-THI-CHU', 'Njan sammathichu = I agree', ['njan sammathichu']),
        ('i3_26', 'Atheda', 'അതെടാ', 'Yes, man', '', '', '', 'Atheda = Yes, man', ['atheda']),
        ('i3_27', 'Vibe aanu', 'വൈബ് ആണ്', 'Great vibes', 'Enjoying the evening music or cool rain.', 'Aanandaprathamaanu', 'VIBE AA-NU', 'Vibe aanu = Great vibes', ['vibe aanu']),
        ('i3_28', 'Scene kando?', 'സീൻ കണ്ടോ?', 'Did you see that drama?', 'Whispering to a cousin as something funny unfolds.', 'Kando', 'SCENE KAN-DOH', 'Scene kando? = Did you see that drama?', ['scene kando']),
        ('i3_29', 'Patti poyi', 'പറ്റിപ്പോയി', 'Made a mistake', "Apologetic admission: 'I made a blunder'.", 'Thettu patti', 'PAT-TI POH-YI', 'Patti poyi = Made a mistake', ['patti poyi']),
        ('i3_30', 'Kollallo', 'കൊള്ളാമല്ലോ', "Hey, that's surprisingly neat!", 'Admiring a thoughtful gesture or tasty dish.', 'Nallathaanallo', 'KOL-LAL-LOH', "Kollallo = Hey, that's surprisingly neat!", ['kollallo'])
    ]

    # Level 12: Intermediate 4 - Opinions, Doubts & Comparisons (25 words)
    int_4 = [
        ('i4_01', 'Thonnunnu', 'തോന്നുന്നു', 'It seems', 'Gentle way to offer an opinion without being rigid.', 'Enikku thonnunnu', 'THON-NUN-NU', 'Thonnunnu = It seems', ['thonnunnu']),
        ('i4_02', 'Vishwasamilla', 'വിശ്വാസമില്ല', "I don't believe it", 'Expressing skepticism about a rumor.', 'Vishwasikkunnilla', 'VISH-WAA-SA-MIL-LA', "Vishwasamilla = I don't believe it", ['vishwasamilla']),
        ('i4_03', 'Athekkaal nallathu', 'അതിനേക്കാൾ നല്ലത്', 'Better than that', "Comparing two options: 'This one is better than that'.", 'Athinilum nallathu', 'A-THIN-AYK-KAAL NAL-LA-THU', 'Athekkaal nallathu = Better than that', ['athekkaal nallathu', 'athinekkal nallathu']),
        ('i4_04', 'Chilappol', 'ചിലപ്പോൾ', 'Sometimes', "Stating probability: 'Perhaps it will rain'.", 'Chilappol', 'CHI-LAP-POL', 'Chilappol = Sometimes', ['chilappol']),
        ('i4_05', 'Oru karyam parayatte?', 'ഒരു കാര്യം പറയട്ടെ?', 'May I say one thing?', 'Courteous opener before sharing important feedback.', 'Njan oru karyam parayatte', 'O-RU KAA-RYAM PA-RA-YAT-TAY', 'Oru karyam parayatte? = May I say one thing?', ['oru karyam parayatte']),
        ('i4_06', 'Enikku thonniyathu', 'എനിക്ക് തോന്നിയത്', 'What I thought', 'Explaining your thought process.', 'Ente abhiprayam', 'E-NIK-KU THON-NI-YA-THU', 'Enikku thonniyathu = What I thought', ['enikku thonniyathu']),
        ('i4_07', 'Kooduthal nallathu', 'കൂടുതൽ നല്ലത്', 'Much better', 'Recommending an alternative path or dish.', 'Kooduthal nallathaano', 'KOO-DU-THAL NAL-LA-THU', 'Kooduthal nallathu = Much better', ['kooduthal nallathu']),
        ('i4_08', 'Kuravalla', 'കുറവല്ല', 'Quite a lot', "Acknowledging someone's considerable effort.", 'Kuravalla', 'KU-RA-VAL-LA', 'Kuravalla = Quite a lot', ['kuravalla']),
        ('i4_09', 'Nallapole', 'നല്ലപോലെ', 'Properly', "'Do the job properly' or 'She sings nicely'.", 'Nallavannam', 'NAL-LA-POH-LAY', 'Nallapole = Properly', ['nallapole']),
        ('i4_10', 'Sathyam paranjaal', 'സത്യം പറഞ്ഞാൽ', 'To be honest', 'Heartfelt conversational opener.', 'Sathyamaayi paranjaal', 'SATH-YAM PA-RAN-JAAL', 'Sathyam paranjaal = To be honest', ['sathyam paranjaal', 'sathyam paranjal']),
        ('i4_11', 'Urappilla', 'ഉറപ്പില്ല', 'Not sure', "When you don't want to give a false guarantee.", 'Nischayamilla', 'U-RAP-PIL-LA', 'Urappilla = Not sure', ['urappilla']),
        ('i4_12', 'Theerchayaayum', 'തീർച്ചയായും', 'Definitely', 'Giving firm, reassuring affirmation.', 'Theerchayaayum', 'THEER-CHA-YAA-YUM', 'Theerchayaayum = Definitely', ['theerchayaayum', 'theerchayayum']),
        ('i4_13', 'Ente abhiprayathil', 'എന്റെ അഭിപ്രായത്തിൽ', 'In my opinion', 'Polite way to frame personal advice among elders.', 'Ente abhiprayam', 'EN-TAY A-BHI-PRAA-YA-THIL', 'Ente abhiprayathil = In my opinion', ['ente abhiprayathil']),
        ('i4_14', 'Athu shari aanu', 'അത് ശരിയാണ്', 'That is correct', "Validating someone's thoughtful observation.", 'Athu shariyaanu', 'A-THU SHE-RI AA-NU', 'Athu shari aanu = That is correct', ['athu shari aanu', 'athu shariyaanu']),
        ('i4_15', 'Anganeyalla', 'അങ്ങനെയല്ല', "It's not quite like that", 'Gentle correction when someone misunderstands.', 'Anganeyalla', 'ANG-A-NAY-YAL-LA', "Anganeyalla = It's not quite like that", ['anganeyalla']),
        ('i4_16', 'Ithreyum nallathu', 'ഇത്രയും നല്ലത്', 'This good', 'Expressing heartfelt appreciation of hospitality.', 'Ithra nallathu', 'ITH-RAY-YUM NAL-LA-THU', 'Ithreyum nallathu = This good', ['ithreyum nallathu']),
        ('i4_17', 'Avasyamundu', 'ആവശ്യമുണ്ട്', 'There is a need', 'Stating that something is genuinely needed.', 'Aavashyamundu', 'AA-VA-SHYA-MUN-DU', 'Avasyamundu = There is a need', ['avasyamundu', 'aavashyamundu']),
        ('i4_18', 'Karyamilla', 'കാര്യമില്ല', 'No point in it', 'Advising not to stress over an unchangeable issue.', 'Prayojanamilla', 'KAA-RYA-MIL-LA', 'Karyamilla = No point in it', ['karyamilla']),
        ('i4_19', 'Ente thonnal', 'എന്റെ തോന്നൽ', 'My impression', 'Clarifying that you are guessing, not asserting fact.', 'Ente thonnal', 'EN-TAY THON-NAL', 'Ente thonnal = My impression', ['ente thonnal']),
        ('i4_20', 'Orkkunnu', 'ഓർക്കുന്നു', 'I remember', 'Recalling a fond past visit to Kerala.', 'Ormikkunnu', 'ORK-KUN-NU', 'Orkkunnu = I remember', ['orkkunnu']),
        ('i4_21', 'Marannupoyi', 'മറന്നുപോയി', 'I forgot', 'Honest admission when an errand slipped your mind.', 'Marannu', 'MA-RAN-NU POH-YI', 'Marannupoyi = I forgot', ['marannupoyi', 'marannu poyi']),
        ('i4_22', 'Thiricharinju', 'തിരിച്ചറിഞ്ഞു', 'Realized', 'Gaining understanding of a cultural nuance.', 'Manassilaakki', 'THI-RI-CHA-RIN-JU', 'Thiricharinju = Realized', ['thiricharinju']),
        ('i4_23', 'Bhedhamaanu', 'ഭേദമാണ്', 'Better', "Health update: 'Fever has improved now'.", 'Kooduthal nallathaayi', 'BHAY-DHA-MAA-NU', 'Bhedhamaanu = Better', ['bhedhamaanu', 'bhedham']),
        ('i4_24', 'Valare nallathu', 'വളരെ നല്ലത്', 'Very good indeed', 'Praising good character or an act of kindness.', 'Ettavum nallathu', 'VA-LA-RAY NAL-LA-THU', 'Valare nallathu = Very good indeed', ['valare nallathu']),
        ('i4_25', 'Njan orkkam', 'ഞാൻ ഓർക്കാം', 'I will keep it in mind', "Promising to remember elder's wise advice.", 'Njan ormikkam', 'NYAAN ORK-KAAM', 'Njan orkkam = I will keep it in mind', ['njan orkkam'])
    ]

    # Level 13: Intermediate 5 - Errands, Shopping & Outside Interactions (25 words)
    int_5 = [
        ('i5_01', 'Vilakooduthal', 'വിലക്കൂടുതൽ', 'Too expensive', 'Noting high prices at a market stall.', 'Vilayadhikam', 'VI-LA-KOO-DU-THAL', 'Vilakooduthal = Too expensive', ['vilakooduthal']),
        ('i5_02', 'Kurakkumo?', 'കുറയ്ക്കുമോ?', 'Will you reduce the price?', 'Polite bargaining at street markets in Kerala.', 'Vila kurakkumo', 'KU-RAK-KU-MOH', 'Kurakkumo? = Will you reduce the price?', ['kurakkumo', 'kurakkumo?']),
        ('i5_03', 'Baakki', 'ബാക്കി', 'Change', 'Asking for balance change after paying cash.', 'Shishtam', 'BAAK-KI', 'Baakki = Change', ['baakki', 'bakki']),
        ('i5_04', 'Chodyam', 'ചോദ്യം', 'Question', 'Having a question about timings or route.', 'Prashnam', 'CHOH-DHYAM', 'Chodyam = Question', ['chodyam']),
        ('i5_05', 'Ready aayo?', 'റെഡിയായോ?', 'Is it ready?', 'Asking if food order or tailor work is ready.', 'Thayyaraayo', 'REA-DY AA-YOH', 'Ready aayo? = Is it ready?', ['ready aayo', 'ready aayo?']),
        ('i5_06', 'Ethra aayi?', 'എത്രയായി?', 'How much did it come to?', 'Standard question to driver or cashier.', 'Ethra roopa aayi', 'ETH-RA AA-YI', 'Ethra aayi? = How much did it come to?', ['ethra aayi', 'ethra aayi?']),
        ('i5_07', 'Bill thaamo?', 'ബിൽ തരുമോ?', 'Could you please give the bill?', 'Asking politely at restaurant table.', 'Bill tharumo', 'BILL THAA-MOH', 'Bill thaamo? = Could you please give the bill?', ['bill thaamo', 'bill tharumo']),
        ('i5_08', 'Cover undo?', 'കവർ ഉണ്ടോ?', 'Do you have a carry bag?', 'Asking store clerk for a shopping bag.', 'Sanchi undo', 'CO-VER UN-DOH', 'Cover undo? = Do you have a carry bag?', ['cover undo', 'cover undo?']),
        ('i5_09', 'Meter idumo?', 'മീറ്റർ ഇടുമോ?', 'Will you run the meter?', 'Asking auto rickshaw driver to use standard meter.', 'Meter iduka', 'MEE-TER I-DU-MOH', 'Meter idumo? = Will you run the meter?', ['meter idumo', 'meter idumo?']),
        ('i5_10', 'Sthalam ethiyo?', 'സ്ഥലം എത്തിയോ?', 'Have we reached the place?', 'Checking with bus conductor or driver.', 'Idam ethiyo', 'STHA-LAM ETH-I-YOH', 'Sthalam ethiyo? = Have we reached the place?', ['sthalam ethiyo', 'sthalam ethiyo?']),
        ('i5_11', 'Oru plate koode', 'ഒരു പ്ലേറ്റ് കൂടെ', 'One more plate please', 'Ordering second serving of beef roast or parotta.', 'Oru plate koodi', 'O-RU PLATE KOO-DAY', 'Oru plate koode = One more plate please', ['oru plate koode']),
        ('i5_12', 'Pack cheyyaamo?', 'പാക്ക് ചെയ്യാമോ?', 'Could you pack this?', 'Asking for takeaway parcel at restaurant.', 'Parcel cheyyamo', 'PACK CHEY-YAA-MOH', 'Pack cheyyaamo? = Could you pack this?', ['pack cheyyaamo', 'parcel cheyyamo']),
        ('i5_13', 'GPay undo?', 'ജിപേ ഉണ്ടോ?', 'Do you accept Google Pay?', 'The #1 modern payment question everywhere in Kerala.', 'UPI undo', 'G-PAY UN-DOH', 'GPay undo? = Do you accept Google Pay?', ['gpay undo', 'gpay undo?']),
        ('i5_14', 'Kaasu illa', 'കാശ് ഇല്ല', 'I have no money', 'Explaining you need to withdraw or pay digitally.', 'Panamilla', 'KAA-SHU IL-LA', 'Kaasu illa = I have no money', ['kaasu illa', 'kaash illa']),
        ('i5_15', 'Ividayano irangendathu?', 'ഇവിടെയാണോ ഇറങ്ങേണ്ടത്?', 'Should I get down here?', 'Checking bus stop with fellow passenger.', 'Ivideyaano', 'I-VI-DAY-AA-NOH I-RANG-EN-DA-THU', 'Ividayano irangendathu? = Should I get down here?', ['ividayano irangendathu']),
        ('i5_16', 'Bus stand evide?', 'ബസ് സ്റ്റാൻഡ് എവിടെ?', 'Where is the bus stand?', 'Asking directions on the street.', 'Bus stand evideyaanu', 'BUS STAND E-VI-DAY', 'Bus stand evide? = Where is the bus stand?', ['bus stand evide', 'bus stand evide?']),
        ('i5_17', 'Adoor KSRTC', 'അടൂർ കെ.എസ്.ആർ.ടി.സി', 'Adoor KSRTC Depot', 'Key regional transit hub connecting Pathanamthitta.', 'Adoor depot', 'A-DOOR K-S-R-T-C', 'Adoor KSRTC = Adoor KSRTC Depot', ['adoor ksrtc', 'ksrtc']),
        ('i5_18', 'Auto vilikkoo', 'ഓട്ടോ വിളിക്കൂ', 'Call an auto rickshaw', 'Hailing transport for family outing.', 'Auto vilikkuka', 'AU-TO VI-LIK-KOO', 'Auto vilikkoo = Call an auto rickshaw', ['auto vilikkoo', 'auto vilikku']),
        ('i5_19', 'Ivide nirthumo?', 'ഇവിടെ നിർത്തുമോ?', 'Could you stop right here?', 'Telling auto or bus driver your stop arrived.', 'Ivide nirthuka', 'I-VI-DAY NIR-THU-MOH', 'Ivide nirthumo? = Could you stop right here?', ['ivide nirthumo', 'ivide nirthumo?']),
        ('i5_20', 'Baakki kaasu thaamo?', 'ബാക്കി കാശ് തരുമോ?', 'Could you give me my change?', 'Polite reminder at shop counter.', 'Baakki tharumo', 'BAAK-KI KAA-SHU THAA-MOH', 'Baakki kaasu thaamo? = Could you give me my change?', ['baakki kaasu thaamo']),
        ('i5_21', 'Fresh aano?', 'ഫ്രഷ് ആണോ?', 'Is this fresh?', 'Asking fishmonger or fruit vendor.', 'Puthiya thaano', 'FRESH AA-NOH', 'Fresh aano? = Is this fresh?', ['fresh aano', 'fresh aano?']),
        ('i5_22', 'Vila ethraya?', 'വില എത്രയാ?', 'What is the price?', 'Inquiring about item cost.', 'Vila ethrayaanu', 'VI-LA ETH-RA-YA', 'Vila ethraya? = What is the price?', ['vila ethraya', 'vila ethraya?']),
        ('i5_23', 'Choodu chaya thaamo?', 'ചൂടു ചായ തരുമോ?', 'Could I have hot tea?', 'Ordering at tea shop counter.', 'Choodu chaya tharoo', 'CHOO-DU CHAA-YA THAA-MOH', 'Choodu chaya thaamo? = Could I have hot tea?', ['choodu chaya thaamo']),
        ('i5_24', 'Kurachu kazhinju varaam', 'കുറച്ചു കഴിഞ്ഞ് വരാം', 'I will come back in a little while', "Telling store owner you'll return shortly.", 'Pinneedu varaam', 'KU-RA-CHU KA-ZHI-NJU VA-RAAM', 'Kurachu kazhinju varaam = I will come back in a little while', ['kurachu kazhinju varaam']),
        ('i5_25', 'Nannayi poyi', 'നന്നായിപ്പോയി', 'It went very well', 'Expressing satisfaction after concluding errands.', 'Nallathaayi theernnu', 'NAN-NAA-YI POH-YI', 'Nannayi poyi = It went very well', ['nannayi poyi'])
    ]

    return int_1 + int_2 + int_3 + int_4 + int_5

print("Intermediate phrases ready: 130 phrases (5 levels).")
