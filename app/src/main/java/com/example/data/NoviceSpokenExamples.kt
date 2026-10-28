package com.example.data

data class SpokenExample(
    val malayalam: String,
    val manglish: String,
    val english: String
)

object NoviceSpokenExamples {
    private val kinship = listOf(
        SpokenExample("ഞാൻ അമ്മയെ സഹായിക്കാം.", "Njaan Ammaye sahaayikkaam.", "I can help Mom."),
        SpokenExample("നീ ഇവിടെ ഇരിക്ക്.", "Nee ivide irikku.", "You sit here."),
        SpokenExample("നിങ്ങൾ ചായ കുടിക്കൂ.", "Ningal chaaya kudikkoo.", "Please have some tea."),
        SpokenExample("ഞങ്ങൾ വീട്ടിൽ വരാം.", "Njangal veettil varaam.", "We'll come home."),
        SpokenExample("നമ്മൾ ഒരുമിച്ച് പോകാം.", "Nammal orumichu pokaam.", "Let's go together."),
        SpokenExample("അവൻ അപ്പച്ചന്റെ കൂടെയുണ്ട്.", "Avan Appachante koodeyundu.", "He's with Grandpa."),
        SpokenExample("അവൾ ചേച്ചിയെ വിളിച്ചു.", "Aval chechiye vilichu.", "She called her elder sister."),
        SpokenExample("അവർ അകത്തുണ്ട്.", "Avar akathundu.", "They're inside."),
        SpokenExample("അപ്പാ, ഞാൻ സഹായിക്കാം.", "Appa, njaan sahaayikkaam.", "Dad, I can help."),
        SpokenExample("അമ്മ അടുക്കളയിലുണ്ട്.", "Amma adukkalayilundu.", "Mom is in the kitchen."),
        SpokenExample("എന്റെ അച്ചായൻ വീട്ടിലുണ്ട്.", "Ente achayan veettilundu.", "My husband is at home."),
        SpokenExample("ചേച്ചി എന്നെ വിളിച്ചു.", "Chechi enne vilichu.", "My elder sister called me."),
        SpokenExample("അമ്മച്ചിയെ ഞാൻ സഹായിക്കാം.", "Ammachiye njaan sahaayikkaam.", "I can help Grandma."),
        SpokenExample("അപ്പച്ചൻ മുറ്റത്തുണ്ട്.", "Appachan muttathundu.", "Grandpa is in the courtyard."),
        SpokenExample("ചേട്ടൻ കടയിൽ പോയി.", "Chettan kadayil poyi.", "Big brother went to the shop."),
        SpokenExample("അനിയൻ സ്കൂളിൽ പോയി.", "Aniyan schoolil poyi.", "My younger brother went to school."),
        SpokenExample("അനിയത്തി, ഇവിടെ വാ.", "Aniyathi, ivide vaa.", "Little sister, come here."),
        SpokenExample("അമ്മായി ഇന്ന് വരുന്നുണ്ട്.", "Ammayi innu varunnundu.", "Aunt is coming today."),
        SpokenExample("അമ്മാവൻ വൈകിട്ട് വരും.", "Ammavan vaikittu varum.", "Uncle will come in the evening."),
        SpokenExample("എന്റെ അളിയൻ ഇന്ന് വരും.", "Ente aliyan innu varum.", "My brother-in-law will come today."),
        SpokenExample("നാത്തൂൻ അകത്തുണ്ട്.", "Naathoon akathundu.", "My sister-in-law is inside."),
        SpokenExample("മോനേ, ഇവിടെ വാ.", "Mone, ivide vaa.", "Son, come here."),
        SpokenExample("മോളേ, അമ്മയുടെ അടുത്തിരിക്ക്.", "Mole, ammayude aduthirikkoo.", "Daughter, sit beside Mom."),
        SpokenExample("നമ്മുടെ കുടുംബം ഇവിടെ തന്നെയുണ്ട്.", "Nammude kudumbam ivide thanneyundu.", "Our family is right here."),
        SpokenExample("ബന്ധുക്കൾ വൈകിട്ട് വരും.", "Bandhukkal vaikittu varum.", "The relatives will come in the evening.")
    )

    private val kitchen = listOf(
        SpokenExample("ചോറ് കുറച്ച് മതി.", "Choru kurachu mathi.", "A little rice is enough."),
        SpokenExample("വെള്ളം ഒന്ന് തരാമോ?", "Vellam onnu tharaamo?", "Could you give me some water?"),
        SpokenExample("ചായ വേണോ?", "Chaaya veno?", "Would you like tea?"),
        SpokenExample("കാപ്പി വേണ്ട, ചായ മതി.", "Kaappi venda, chaaya mathi.", "No coffee; tea is enough."),
        SpokenExample("ഇന്ന് മീൻ കറി ഉണ്ടോ?", "Innu meen curry undo?", "Is there fish curry today?"),
        SpokenExample("ഇറച്ചി കുറച്ച് വിളമ്പൂ.", "Erachi kurachu vilamboo.", "Serve me a little meat."),
        SpokenExample("അപ്പവും മുട്ടക്കറിയും വേണം.", "Appavum muttakkariyum venam.", "I want appam and egg curry."),
        SpokenExample("പുട്ടും കടലയും ഉണ്ടാക്കാമോ?", "Puttum kadalayum undaakkaamo?", "Can you make puttu and kadala?"),
        SpokenExample("കടലക്കറി കുറച്ച് തരൂ.", "Kadalakkari kurachu tharoo.", "Give me a little chickpea curry."),
        SpokenExample("പറോട്ട ചൂടോടെ തരൂ.", "Parotta choodode tharoo.", "Serve the parotta hot."),
        SpokenExample("ഒരു പഴം കൂടി തരാമോ?", "Oru pazham koodi tharaamo?", "Could you give me one more banana?"),
        SpokenExample("പാൽ ചായയിൽ ഒഴിക്കൂ.", "Paal chaayayil ozhikkoo.", "Pour milk into the tea."),
        SpokenExample("മോരൊന്ന് ഒഴിക്കൂ.", "Moronnu ozhikkoo.", "Pour me some buttermilk."),
        SpokenExample("സാമ്പാർ ചോറിന്റെ കൂടെ മതി.", "Sambar chorinte koode mathi.", "Sambar with rice is enough."),
        SpokenExample("അച്ചാർ കുറച്ച് മതി.", "Achar kurachu mathi.", "A little pickle is enough."),
        SpokenExample("എനിക്ക് ചായ വേണം.", "Enikku chaaya venam.", "I want tea."),
        SpokenExample("ഇന്ന് ഇറച്ചി വേണ്ട.", "Innu erachi venda.", "I don't want meat today."),
        SpokenExample("മതി, ഇത്രയും പോരേ.", "Mathi, ithrayum pore.", "Enough, this much is fine."),
        SpokenExample("കുറച്ച് ഉപ്പ് ഇടൂ.", "Kurachu uppu idoo.", "Add a little salt."),
        SpokenExample("കുറച്ച് കറി കൂടി തരൂ.", "Kurachu kari koodi tharoo.", "Give me some more curry."),
        SpokenExample("എനിക്ക് ഇത് തീരെ വേണ്ട.", "Enikku ithu theere venda.", "I really don't want this at all."),
        SpokenExample("ഞാൻ ചോറ് കഴിച്ചു.", "Njaan choru kazhichu.", "I ate rice."),
        SpokenExample("എനിക്ക് വിശക്കുന്നു, ചോറ് തരൂ.", "Enikku vishakkunnu, choru tharoo.", "I'm hungry; give me some rice."),
        SpokenExample("ഈ പായസം നല്ല മധുരമാണ്.", "Ee payasam nalla madhuramaanu.", "This payasam is very sweet."),
        SpokenExample("ഈ കറിക്ക് നല്ല എരിവുണ്ട്.", "Ee karrikku nalla erivundu.", "This curry is nicely spicy.")
    )

    fun forPhraseId(id: String): SpokenExample? = when {
        id.startsWith("n0_") -> kinship.getOrNull(id.substringAfterLast('_').toIntOrNull()?.minus(1) ?: -1)
        id.startsWith("n1_") -> kitchen.getOrNull(id.substringAfterLast('_').toIntOrNull()?.minus(1) ?: -1)
        else -> null
    }
}
