"""Additional communicative units. Each row is Manglish | Malayalam | English.

IDs are permanent; never derive them from a level's position in the course.
These are teaching entries, not a count of unique lemmas or word families.
"""

UNITS = []


def unit(tier, prefix, title, goal, rows):
    entries = [tuple(part.strip() for part in row.split('|'))
               for row in rows.strip().splitlines() if row.strip()]
    if any(len(entry) != 3 or not all(entry) for entry in entries):
        raise ValueError(f"Invalid entry in {prefix}")
    UNITS.append(dict(tier=tier, prefix=prefix, title=title, goal=goal, entries=entries))


unit('novice', 'ng', 'Greetings, Introductions & Repair',
     'Introduce yourself, greet a visitor, and ask for repetition when you do not understand.', '''
Namaskaaram | നമസ്കാരം | Hello
Sukhamaano? | സുഖമാണോ? | How are you?
Sukhamaanu | സുഖമാണ് | I am well
Ente peru | എന്റെ പേര് | My name
Ningalude peru enthaanu? | നിങ്ങളുടെ പേര് എന്താണ്? | What is your name?
Njaan Malayalam padikkunnu | ഞാൻ മലയാളം പഠിക്കുന്നു | I am learning Malayalam
Enikku manassilaayilla | എനിക്ക് മനസ്സിലായില്ല | I did not understand
Onnu koodi parayaamo? | ഒന്നു കൂടി പറയാമോ? | Could you say that once more?
Pathukke parayaamo? | പതുക്കെ പറയാമോ? | Could you speak slowly?
Ithinte artham enthaanu? | ഇതിന്റെ അർത്ഥം എന്താണ്? | What does this mean?
Malayalathil engane parayum? | മലയാളത്തിൽ എങ്ങനെ പറയും? | How do you say it in Malayalam?
Enikku ariyilla | എനിക്ക് അറിയില്ല | I do not know
Enikku kurachu Malayalam ariyaam | എനിക്ക് കുറച്ചു മലയാളം അറിയാം | I know a little Malayalam
Parichayappettathil santhosham | പരിചയപ്പെട്ടതിൽ സന്തോഷം | Nice to meet you
Nanni | നന്ദി | Thank you
Kshamikkanam | ക്ഷമിക്കണം | Please excuse me
Sahaayikkaamo? | സഹായിക്കാമോ? | Could you help?
Pinne kaanaam | പിന്നെ കാണാം | See you later
Shubharaathri | ശുഭരാത്രി | Good night
Akathu varaamo? | അകത്തു വരാമോ? | May I come inside?
''')

unit('novice', 'nn', 'Numbers, Prices & Quantities',
     'Count small quantities, understand common prices, and ask how much you need to pay.', '''
Poojyam | പൂജ്യം | Zero
Onnu | ഒന്ന് | One
Randu | രണ്ട് | Two
Moonnu | മൂന്ന് | Three
Naalu | നാല് | Four
Anchu | അഞ്ച് | Five
Aaru | ആറ് | Six
Ezhu | ഏഴ് | Seven
Ettu | എട്ട് | Eight
Onpathu | ഒൻപത് | Nine
Pathu | പത്ത് | Ten
Pathinonnu | പതിനൊന്ന് | Eleven
Panthrandu | പന്ത്രണ്ട് | Twelve
Irupathu | ഇരുപത് | Twenty
Muppathu | മുപ്പത് | Thirty
Nalpathu | നാൽപത് | Forty
Anpathu | അൻപത് | Fifty
Nooru | നൂറ് | One hundred
Aayiram | ആയിരം | One thousand
Pakuthi | പകുതി | Half
Oru kilo | ഒരു കിലോ | One kilogram
Randu ennam | രണ്ട് എണ്ണം | Two items
Ethra roopa? | എത്ര രൂപ? | How many rupees?
Aake ethra? | ആകെ എത്ര? | How much altogether?
''')

unit('novice', 'nd', 'People, Things & Simple Descriptions',
     'Describe an object and combine familiar nouns with size, colour, and possession.', '''
Ithu | ഇത് | This thing
Athu | അത് | That thing
Ente | എന്റെ | My
Ninte | നിന്റെ | Your (casual)
Nammude | നമ്മുടെ | Our (including you)
Valuthu | വലുത് | Big thing
Cheruthu | ചെറുത് | Small thing
Puthiyathu | പുതിയത് | New thing
Pazhayathu | പഴയത് | Old thing
Nallathu | നല്ലത് | Good thing
Cheeththa | ചീത്ത | Bad
Neelam | നീലം | Blue
Pacha | പച്ച | Green
Chuvappu | ചുവപ്പ് | Red
Manja | മഞ്ഞ | Yellow
Karuppu | കറുപ്പ് | Black
Vella | വെള്ള | White
Ithu enthaanu? | ഇത് എന്താണ്? | What is this?
Ithu entethaanu | ഇത് എന്റേതാണ് | This is mine
Athu enikku venam | അത് എനിക്ക് വേണം | I want that
Ithu nallathaanu | ഇത് നല്ലതാണ് | This is good
Ithu puthiyathaano? | ഇത് പുതിയതാണോ? | Is this new?
''')

unit('elementary', 'et', 'Buses, Autos & Travel',
     'Buy a ticket, check a destination, and tell a driver where to stop.', '''
Bus stop | ബസ് സ്റ്റോപ്പ് | Bus stop
Railway station | റെയിൽവേ സ്റ്റേഷൻ | Railway station
Vimaanathaavalam | വിമാനത്താവളം | Airport
Ticket | ടിക്കറ്റ് | Ticket
Yaathra | യാത്ര | Journey
Yaathrakkaar | യാത്രക്കാർ | Passengers
Driver | ഡ്രൈവർ | Driver
Seetu | സീറ്റ് | Seat
Kooli | കൂലി | Fare
Dooram | ദൂരം | Distance
Paatha | പാത | Route
Paalam | പാലം | Bridge
Valathottu | വലത്തോട്ട് | To the right
Idathottu | ഇടത്തോട്ട് | To the left
Thiriyanam | തിരിയണം | Need to turn
Ivide nirthaamo? | ഇവിടെ നിർത്താമോ? | Could you stop here?
Oru ticket tharoo | ഒരു ടിക്കറ്റ് തരൂ | Please give me one ticket
Ee bus Kottayathinu pokumo? | ഈ ബസ് കോട്ടയത്തിനു പോകുമോ? | Does this bus go to Kottayam?
Ethra samayam edukkum? | എത്ര സമയം എടുക്കും? | How long will it take?
Adutha stop ethaanu? | അടുത്ത സ്റ്റോപ്പ് ഏതാണ്? | Which is the next stop?
Enikku iranganam | എനിക്ക് ഇറങ്ങണം | I need to get off
Auto kittumo? | ഓട്ടോ കിട്ടുമോ? | Can I get an auto?
Vaiki ethum | വൈകി എത്തും | Will arrive late
Vazhi thetti | വഴി തെറ്റി | Lost the way
''')

unit('elementary', 'es', 'Groceries, Money & Ordering',
     'Ask for ingredients, order a meal, pay, and explain a simple dietary preference.', '''
Pachakkari | പച്ചക്കറി | Vegetables
Ulli | ഉള്ളി | Onion
Thakkaali | തക്കാളി | Tomato
Urulakkizhangu | ഉരുളക്കിഴങ്ങ് | Potato
Mulaku | മുളക് | Chilli
Uppu | ഉപ്പ് | Salt
Panchasaara | പഞ്ചസാര | Sugar
Enna | എണ്ണ | Oil
Thenga | തേങ്ങ | Coconut
Ari | അരി | Uncooked rice
Mutta | മുട്ട | Egg
Thairu | തൈര് | Curd
Pachamulaku | പച്ചമുളക് | Green chilli
Inchi | ഇഞ്ചി | Ginger
Veluthulli | വെളുത്തുള്ളി | Garlic
Manjal | മഞ്ഞൾ | Turmeric
Kada | കട | Shop
Chantha | ചന്ത | Market
Sanchi | സഞ്ചി | Bag
Raseethu | രസീത് | Receipt
Panam | പണം | Money
Chillara | ചില്ലറ | Small change
Bill tharaamo? | ബിൽ തരാമോ? | Could you give me the bill?
Kaaram kurachu mathi | കാരം കുറച്ചു മതി | Just a little spice is enough
Irachi venda | ഇറച്ചി വേണ്ട | No meat please
Ithu puthiyathaano? | ഇത് പുതിയതാണോ? | Is this new?
Pothinju tharaamo? | പൊതിഞ്ഞു തരാമോ? | Could you wrap it up?
Vere enthenkilum undo? | വേറെ എന്തെങ്കിലും ഉണ്ടോ? | Is there anything else?
''')

unit('elementary', 'eh', 'Body, Health & Asking for Help',
     'Name common symptoms and ask someone for help or a medical appointment.', '''
Thala | തല | Head
Kai | കൈ | Hand
Kaal | കാൽ | Leg
Kannu | കണ്ണ് | Eye
Chevi | ചെവി | Ear
Mookku | മൂക്ക് | Nose
Vaaya | വായ | Mouth
Pallu | പല്ല് | Tooth
Vayaru | വയറ് | Stomach
Thonda | തൊണ്ട | Throat
Pani | പനി | Fever
Chuma | ചുമ | Cough
Jaladosham | ജലദോഷം | Common cold
Thalavedana | തലവേദന | Headache
Vayar vedanikkunnu | വയർ വേദനിക്കുന്നു | My stomach hurts
Marunnu | മരുന്ന് | Medicine
Aashupathri | ആശുപത്രി | Hospital
Doctor | ഡോക്ടർ | Doctor
Murivu | മുറിവ് | Wound
Raktham | രക്തം | Blood
Enikku sukhamilla | എനിക്ക് സുഖമില്ല | I am unwell
Doctor-e kaananam | ഡോക്ടറെ കാണണം | I need to see a doctor
Adiyanthira sahaayam venam | അടിയന്തര സഹായം വേണം | I need urgent help
Enikku allergy undu | എനിക്ക് അലർജി ഉണ്ട് | I have an allergy
''')

unit('elementary', 'ec', 'Calendar, Time & Appointments',
     'Arrange a meeting, understand dates, and describe when something happens.', '''
Thingalaazhcha | തിങ്കളാഴ്ച | Monday
Chovvaazhcha | ചൊവ്വാഴ്ച | Tuesday
Budhanaazhcha | ബുധനാഴ്ച | Wednesday
Vyaazhaazhcha | വ്യാഴാഴ്ച | Thursday
Velliyaazhcha | വെള്ളിയാഴ്ച | Friday
Shaniyaazhcha | ശനിയാഴ്ച | Saturday
Njaayaraazhcha | ഞായറാഴ്ച | Sunday
Aazhcha | ആഴ്ച | Week
Maasam | മാസം | Month
Varsham | വർഷം | Year
Theeyathi | തീയതി | Date
Raavile | രാവിലെ | In the morning
Uchaykku | ഉച്ചയ്ക്ക് | At noon
Vaikunneram | വൈകുന്നേരം | Evening
Raathri | രാത്രി | Night
Manikkoor | മണിക്കൂർ | Hour
Minute | മിനിറ്റ് | Minute
Ara manikkoor | അര മണിക്കൂർ | Half an hour
Ethra maniyaayi? | എത്ര മണിയായി? | What time is it?
Moonnu manikku kaanaam | മൂന്ന് മണിക്ക് കാണാം | Let us meet at three
Adutha aazhcha | അടുത്ത ആഴ്ച | Next week
Kazhinja maasam | കഴിഞ്ഞ മാസം | Last month
Samayam maattaamo? | സമയം മാറ്റാമോ? | Could we change the time?
Enikku samayamilla | എനിക്ക് സമയമില്ല | I do not have time
''')

unit('elementary', 'ew', 'Weather, Outdoors & Places',
     'Describe the weather and surroundings, and suggest a simple outing.', '''
Mazha | മഴ | Rain
Veyil | വെയിൽ | Sunshine
Kaattu | കാറ്റ് | Wind
Megham | മേഘം | Cloud
Aakaasham | ആകാശം | Sky
Thanuppu | തണുപ്പ് | Cold weather
Mazhakkaalam | മഴക്കാലം | Rainy season
Venalkkaalam | വേനൽക്കാലം | Summer
Kuda | കുട | Umbrella
Maram | മരം | Tree
Poovu | പൂവ് | Flower
Ila | ഇല | Leaf
Puzha | പുഴ | River
Kadal | കടൽ | Sea
Mala | മല | Mountain
Vayal | വയൽ | Paddy field
Thottam | തോട്ടം | Garden
Mrugam | മൃഗം | Animal
Pakshi | പക്ഷി | Bird
Naaya | നായ | Dog
Poocha | പൂച്ച | Cat
Mazha peyyunnu | മഴ പെയ്യുന്നു | It is raining
Kuda edutho? | കുട എടുത്തോ? | Did you take an umbrella?
Purathu pokaam | പുറത്തു പോകാം | Let us go outside
''')

unit('elementary', 'ed', 'Clothes, Washing & Getting Ready',
     'Choose clothes, explain sizes, and talk through getting ready to go out.', '''
Uduppu | ഉടുപ്പ് | Clothing
Shirt | ഷർട്ട് | Shirt
Mundu | മുണ്ട് | Mundu
Saari | സാരി | Sari
Cheruppu | ചെരുപ്പ് | Footwear
Thorthu | തോർത്ത് | Bath towel
Soppu | സോപ്പ് | Soap
Cheeppu | ചീപ്പ് | Comb
Kannaadi | കണ്ണാടി | Mirror
Mudi | മുടി | Hair
Alakku | അലക്ക് | Wash clothes
Unakku | ഉണക്കു | Dry something
Kulikkaan ponam | കുളിക്കാൻ പോണം | Need to go bathe
Uduppu maattanam | ഉടുപ്പ് മാറ്റണം | Need to change clothes
Ithu cheruthaanu | ഇത് ചെറുതാണ് | This is small
Vere valuppam undo? | വേറെ വലുപ്പം ഉണ്ടോ? | Is another size available?
Ithu cherunnundu | ഇത് ചേരുന്നുണ്ട് | This fits
Njaan thayyaaraayi | ഞാൻ തയ്യാറായി | I am ready
''')

unit('intermediate', 'iw', 'Work, Tasks & Phone Calls',
     'Describe work, ask for clarification, and arrange a call or task handover.', '''
Joli | ജോലി | Work
Shambalam | ശമ്പളം | Salary
Avadhi | അവധി | Leave
Yogam | യോഗം | Meeting
Sahapravarthakan | സഹപ്രവർത്തകൻ | Male colleague
Sahapravarthaka | സഹപ്രവർത്തക | Female colleague
Utharavaaditham | ഉത്തരവാദിത്തം | Responsibility
Parisheelanam | പരിശീലനം | Training
Parichayam | പരിചയം | Familiarity
Nirdesham | നിർദ്ദേശം | Instruction
Pravrithi | പ്രവൃത്തി | Task
Samayaparidhi | സമയപരിധി | Time limit
Vilichirunno? | വിളിച്ചിരുന്നോ? | Had you called?
Pinne vilikkaam | പിന്നെ വിളിക്കാം | I will call later
Shabdam kelkkunnilla | ശബ്ദം കേൾക്കുന്നില്ല | I cannot hear the sound
Message ayakkaamo? | മെസേജ് അയക്കാമോ? | Could you send a message?
Vishadeekarikkaamo? | വിശദീകരിക്കാമോ? | Could you explain?
Ithu eppozhekkum venam? | ഇത് എപ്പോഴേക്കും വേണം? | By when is this needed?
Njaan poorthiyaakki | ഞാൻ പൂർത്തിയാക്കി | I have completed it
Iniyum samayam venam | ഇനിയും സമയം വേണം | More time is needed
Namakku orumichu cheyyaam | നമുക്ക് ഒരുമിച്ചു ചെയ്യാം | We can do it together
Ee bhaagam njaan cheyyaam | ഈ ഭാഗം ഞാൻ ചെയ്യാം | I will do this part
''')

unit('intermediate', 'il', 'School, Learning & Technology',
     'Explain a learning problem and ask for help with a document or device.', '''
Vidyaalayam | വിദ്യാലയം | School
Class | ക്ലാസ് | Class
Paadam | പാഠം | Lesson
Pareeksha | പരീക്ഷ | Examination
Chodyapaper | ചോദ്യപേപ്പർ | Question paper
Utharam | ഉത്തരം | Answer
Vidyarthi | വിദ്യാർത്ഥി | Student
Adhyaapakan | അധ്യാപകൻ | Male teacher
Adhyaapika | അധ്യാപിക | Female teacher
Pusthakam | പുസ്തകം | Book
Vaayana | വായന | Reading
Ezhuthu | എഴുത്ത് | Writing
Pena | പേന | Pen
Kadalaasu | കടലാസ് | Paper
Computer | കമ്പ്യൂട്ടർ | Computer
Internet | ഇന്റർനെറ്റ് | Internet
Password | പാസ്‌വേഡ് | Password
File | ഫയൽ | File
Thurakkaan pattunnilla | തുറക്കാൻ പറ്റുന്നില്ല | Cannot open it
Save cheytho? | സേവ് ചെയ്തോ? | Did you save it?
Ithu engane pravarthikkum? | ഇത് എങ്ങനെ പ്രവർത്തിക്കും? | How does this work?
Oru udaaharanam tharaamo? | ഒരു ഉദാഹരണം തരാമോ? | Could you give an example?
Thettu thiruthanam | തെറ്റ് തിരുത്തണം | Need to correct the mistake
Veendum shramikkaam | വീണ്ടും ശ്രമിക്കാം | Let us try again
''')

unit('intermediate', 'in', 'Telling a Story & Sequencing Events',
     'Retell an experience using sequence, cause, reported speech, and a conclusion.', '''
Aadyam | ആദ്യം | First
Athinu shesham | അതിനു ശേഷം | After that
Avasaanam | അവസാനം | Finally
Athinidayil | അതിനിടയിൽ | Meanwhile
Pettennu | പെട്ടെന്ന് | Suddenly
Orikkal | ഒരിക്കൽ | Once
Pandu | പണ്ട് | Long ago
Annu | അന്ന് | On that day
Anneram | അന്നേരം | At that time
Sambhavam | സംഭവം | Incident
Anubhavam | അനുഭവം | Experience
Orma | ഓർമ്മ | Memory
Katha | കഥ | Story
Kaaranam | കാരണം | Reason
Phalam | ഫലം | Result
Marannu poyi | മറന്നു പോയി | Forgot
Orthu nokku | ഓർത്തു നോക്ക് | Try to remember
Avan paranju | അവൻ പറഞ്ഞു | He said
Aval chodichu | അവൾ ചോദിച്ചു | She asked
Njaan veettil ethiyappol | ഞാൻ വീട്ടിൽ എത്തിയപ്പോൾ | When I reached home
Mazha peythathukondu | മഴ പെയ്തതുകൊണ്ട് | Because it rained
Angane sambhavichu | അങ്ങനെ സംഭവിച്ചു | That is how it happened
''')

unit('intermediate', 'im', 'Hobbies, Media & Preferences',
     'Discuss something you watched, compare preferences, and recommend an activity.', '''
Ishtam | ഇഷ്ടം | Liking
Thaalparyam | താൽപര്യം | Interest
Vinodam | വിനോദം | Recreation
Cinema | സിനിമ | Film
Paattu | പാട്ട് | Song
Sangeetham | സംഗീതം | Music
Nritham | നൃത്തം | Dance
Kali | കളി | Game
Kalikkaar | കളിക്കാർ | Players
Jayam | ജയം | Victory
Tholvi | തോൽവി | Defeat
Chithram | ചിത്രം | Picture
Photo | ഫോട്ടോ | Photograph
Vaartha | വാർത്ത | News
Paripaadi | പരിപാടി | Event
Abhinayam | അഭിനയം | Acting
Samvidhaayakan | സംവിധായകൻ | Film director
Enikku vaayikkaan ishtamaanu | എനിക്ക് വായിക്കാൻ ഇഷ്ടമാണ് | I like reading
Ee cinema kandittundo? | ഈ സിനിമ കണ്ടിട്ടുണ്ടോ? | Have you seen this film?
Katha engane undu? | കഥ എങ്ങനെ ഉണ്ട്? | What is the story like?
Enikku athra ishtappettilla | എനിക്ക് അത്ര ഇഷ്ടപ്പെട്ടില്ല | I did not like it very much
Nee enthu parayunnu? | നീ എന്തു പറയുന്നു? | What do you think?
''')

unit('intermediate', 'ir', 'Relationships, Invitations & Boundaries',
     'Invite someone, decline with a reason, and express a boundary respectfully.', '''
Suhruthu | സുഹൃത്ത് | Friend
Sauhrudam | സൗഹൃദം | Friendship
Ayalkkaaran | അയൽക്കാരൻ | Male neighbour
Ayalkkaari | അയൽക്കാരി | Female neighbour
Athithi | അതിഥി | Guest
Kshanam | ക്ഷണം | Invitation
Sammaanam | സമ്മാനം | Gift
Janmadinam | ജന്മദിനം | Birthday
Aaghosham | ആഘോഷം | Celebration
Vaakku | വാക്ക് | Word
Vishvaasam | വിശ്വാസം | Trust
Bahumaanam | ബഹുമാനം | Respect
Sammatham | സമ്മതം | Consent
Svaathanthryam | സ്വാതന്ത്ര്യം | Freedom
Svakaryatha | സ്വകാര്യത | Privacy
Naale veettil varaamo? | നാളെ വീട്ടിൽ വരാമോ? | Could you come home tomorrow?
Varan pattilla | വരാൻ പറ്റില്ല | Cannot come
Kshanichathinu nanni | ക്ഷണിച്ചതിനു നന്ദി | Thank you for inviting me
Enikku aalochikkanam | എനിക്ക് ആലോചിക്കണം | I need to think about it
Ithu parayaan enikku ishtamalla | ഇത് പറയാൻ എനിക്ക് ഇഷ്ടമല്ല | I do not want to talk about this
Enne ottaykku vidu | എന്നെ ഒറ്റയ്ക്കു വിടൂ | Please leave me alone
Ninte sahaayathinu nanni | നിന്റെ സഹായത്തിനു നന്ദി | Thank you for your help
''')

unit('advanced', 'an', 'News, Society & Evaluating Claims',
     'Summarise a news item, distinguish opinion from evidence, and explain uncertainty.', '''
Samuham | സമൂഹം | Society
Sarkkaar | സർക്കാർ | Government
Theranjeduppu | തിരഞ്ഞെടുപ്പ് | Election
Niyamam | നിയമം | Law
Avakaasham | അവകാശം | Right
Saambathikam | സാമ്പത്തികം | Economic affairs
Vila vardhana | വിലവർധന | Price increase
Paristhithi | പരിസ്ഥിതി | Environment
Malineekaranam | മലിനീകരണം | Pollution
Vikasanam | വികസനം | Development
Vidyaabhyaasam | വിദ്യാഭ്യാസം | Education
Aarogyam | ആരോഗ്യം | Health
Suraksha | സുരക്ഷ | Safety
Thelivu | തെളിവ് | Evidence
Vivaram | വിവരം | Information
Uravidam | ഉറവിടം | Source
Abhipraayam | അഭിപ്രായം | Opinion
Yaathaarthyam | യാഥാർത്ഥ്യം | Reality
Ithu urappaano? | ഇത് ഉറപ്പാണോ? | Is this certain?
Thelivu undo? | തെളിവ് ഉണ്ടോ? | Is there evidence?
Vaarthayil kettathaanu | വാർത്തയിൽ കേട്ടതാണ് | I heard it on the news
Enikku urappilla | എനിക്ക് ഉറപ്പില്ല | I am not sure
Randu vashavum nokkanam | രണ്ടു വശവും നോക്കണം | We should consider both sides
Kooduthal vivaram venam | കൂടുതൽ വിവരം വേണം | More information is needed
''')

unit('advanced', 'ap', 'Problems, Explanations & Negotiation',
     'Explain a problem, compare possible solutions, and agree on a next step.', '''
Prashnam | പ്രശ്നം | Problem
Parihaaram | പരിഹാരം | Solution
Saadhyatha | സാധ്യത | Possibility
Nettam | നേട്ടം | Benefit
Nashtam | നഷ്ടം | Loss
Apakadam | അപകടം | Danger
Thadassam | തടസ്സം | Obstacle
Munganana | മുൻഗണന | Priority
Lakshyam | ലക്ഷ്യം | Goal
Padhathi | പദ്ധതി | Plan
Chilavu | ചെലവ് | Expense
Laabham | ലാഭം | Profit
Nibandhana | നിബന്ധന | Condition
Maattam | മാറ്റം | Change
Purogathi | പുരോഗതി | Progress
Karaar | കരാർ | Contract
Namukku vere vazhi nokkaam | നമുക്ക് വേറെ വഴി നോക്കാം | Let us look for another approach
Ithinte gunam enthaanu? | ഇതിന്റെ ഗുണം എന്താണ്? | What is the benefit of this?
Chilavu kuraykkaan pattumo? | ചെലവ് കുറയ്ക്കാൻ പറ്റുമോ? | Can we reduce the cost?
Aadyam ithu cheyyaam | ആദ്യം ഇത് ചെയ്യാം | Let us do this first
Ningalude abhipraayam kelkkaam | നിങ്ങളുടെ അഭിപ്രായം കേൾക്കാം | Let us hear your opinion
Namukku oru theerumaanathil ethaam | നമുക്ക് ഒരു തീരുമാനത്തിൽ എത്താം | Let us reach a decision
''')


def phrases_for(tier):
    return [
        (f"{u['prefix']}_{i:02d}", manglish, malayalam, english,
         u['goal'], '', '', '', [manglish.lower()])
        for u in UNITS if u['tier'] == tier
        for i, (manglish, malayalam, english) in enumerate(u['entries'], 1)
    ]
