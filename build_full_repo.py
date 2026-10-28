# Clean Builder using repo_methods.kt.template
import os
import csv
from collections import Counter
import curriculum_spec
import generate_novice
import generate_elementary
import generate_intermediate
import generate_advanced
import generate_mastery
from curriculum_additions import phrases_for

base_phrases = {
    'novice': generate_novice.get_novice_phrases(),
    'elementary': generate_elementary.get_elementary_phrases(),
    'intermediate': generate_intermediate.get_intermediate_phrases(),
    'advanced': generate_advanced.get_advanced_phrases(),
    'mastery': generate_mastery.get_mastery_phrases()
}
all_seed_phrases = [p for tier, base in base_phrases.items() for p in base + phrases_for(tier)]
if len({p[0] for p in all_seed_phrases}) != len(all_seed_phrases):
    raise ValueError('Duplicate seed IDs would overwrite learner progress')
if any(not all(p[:4]) for p in all_seed_phrases):
    raise ValueError('Each entry must have an ID and all three language fields')

def escape_kt(s):
    if not isinstance(s, str):
        return str(s)
    return s.replace('\\', '\\\\').replace('"', '\\"').replace('$', '\\$')

def format_string_list(items):
    return "listOf(" + ", ".join(f'"{escape_kt(x)}"' for x in items) + ")"

# Level metadata lookup
level_meta = {}
for tier in curriculum_spec.curriculum_spec:
    tierId = tier["tierId"]
    iconEmoji = tier["iconEmoji"]
    for lvl in tier["levels"]:
        level_meta[lvl["levelIndex"]] = {
            "tierId": tierId,
            "iconEmoji": iconEmoji,
            "levelIndex": lvl["levelIndex"],
            "rankTitle": lvl["rankTitle"],
            "topicTitle": lvl["topicTitle"],
            "deckId": lvl["deckId"],
            "description": lvl["description"],
            "isTierFinalStage": lvl["isTierFinalStage"],
            "requiredWords": lvl["requiredWords"],
            "wordsInLevel": lvl["wordsInLevel"]
        }

def prefix_to_level(prefix):
    mapping = {level['prefix']: level['levelIndex']
               for tier in curriculum_spec.curriculum_spec for level in tier['levels']}
    return mapping[prefix]

def generate_tier_kt(class_name, phrases, out_path):
    phrases = phrases + phrases_for(class_name.removesuffix("Phrases").lower())
    actual_counts = Counter(prefix_to_level(p[0].split("_")[0]) for p in phrases)
    expected_counts = {
        index: metadata["wordsInLevel"]
        for index, metadata in level_meta.items()
        if metadata["tierId"] == class_name.removesuffix("Phrases").lower()
    }
    if actual_counts != expected_counts:
        raise ValueError(
            f"{class_name} does not match curriculum_spec.py: "
            f"expected {dict(expected_counts)}, found {dict(actual_counts)}"
        )
    lines = [
        "package com.example.data",
        "",
        "import com.example.model.PhraseItem",
        "",
        f"object {class_name} {{",
        "    val phrases: List<PhraseItem> = listOf("
    ]

    for p in phrases:
        p_id = p[0]
        prefix = p_id.split("_")[0]
        lvl_idx = prefix_to_level(prefix)
        meta = level_meta[lvl_idx]
        manglish = p[1]
        malayalam = p[2]
        english = p[3]
        regionalNuance = p[4]
        textbookAlternative = p[5]
        phoneticGuide = p[6]
        wordBreakdown = p[7]
        variants = p[8]
        category = f"{meta['rankTitle']}: {meta['topicTitle']}"
        levelBadge = meta['rankTitle']
        tags = [meta['tierId'], f"level{lvl_idx}"]

        lines.append("        PhraseItem(")
        lines.append(f'            id = "{escape_kt(p_id)}",')
        lines.append(f'            manglish = "{escape_kt(manglish)}",')
        lines.append(f'            malayalam = "{escape_kt(malayalam)}",')
        lines.append(f'            english = "{escape_kt(english)}",')
        lines.append(f'            regionalNuance = "{escape_kt(regionalNuance)}",')
        lines.append(f'            textbookAlternative = "{escape_kt(textbookAlternative)}",')
        lines.append(f'            category = "{escape_kt(category)}",')
        lines.append(f'            levelNumber = {lvl_idx},')
        lines.append(f'            levelBadge = "{escape_kt(levelBadge)}",')
        lines.append(f'            phoneticGuide = "{escape_kt(phoneticGuide)}",')
        lines.append(f'            wordBreakdown = "{escape_kt(wordBreakdown)}",')
        lines.append(f'            acceptedVariants = {format_string_list(variants)},')
        lines.append(f'            tags = {format_string_list(tags)}')
        lines.append("        ),")

    lines.append("    )")
    lines.append("}")
    lines.append("")

    with open(out_path, "w", encoding="utf-8") as f:
        f.write("\n".join(lines))
    print(f"Generated {out_path} ({len(phrases)} phrases)")

# Generate the 5 tier phrase files
data_dir = os.path.join("app", "src", "main", "java", "com", "example", "data")
generate_tier_kt("NovicePhrases", generate_novice.get_novice_phrases(), os.path.join(data_dir, "NovicePhrases.kt"))
generate_tier_kt("ElementaryPhrases", generate_elementary.get_elementary_phrases(), os.path.join(data_dir, "ElementaryPhrases.kt"))
generate_tier_kt("IntermediatePhrases", generate_intermediate.get_intermediate_phrases(), os.path.join(data_dir, "IntermediatePhrases.kt"))
generate_tier_kt("AdvancedPhrases", generate_advanced.get_advanced_phrases(), os.path.join(data_dir, "AdvancedPhrases.kt"))
generate_tier_kt("MasteryPhrases", generate_mastery.get_mastery_phrases(), os.path.join(data_dir, "MasteryPhrases.kt"))

# Now generate DialectRepository.kt
repo_lines = [
    "package com.example.data",
    "",
    "import com.example.model.*",
    "",
    "object DialectRepository {",
    "",
    "    val allPhrases: List<PhraseItem> = NovicePhrases.phrases +",
    "        ElementaryPhrases.phrases +",
    "        IntermediatePhrases.phrases +",
    "        AdvancedPhrases.phrases +",
    "        MasteryPhrases.phrases",
    "",
    "    val decks: List<FlashcardDeck> = listOf("
]

for idx in sorted(level_meta):
    meta = level_meta[idx]
    deck_id = meta["deckId"]
    title = f"{meta['rankTitle']}: {meta['topicTitle']}"
    desc = meta["description"]
    emoji = meta["iconEmoji"]
    lvl_label = meta["rankTitle"]
    repo_lines.append("        FlashcardDeck(")
    repo_lines.append(f'            id = "{escape_kt(deck_id)}",')
    repo_lines.append(f'            title = "{escape_kt(title)}",')
    repo_lines.append(f'            description = "{escape_kt(desc)}",')
    repo_lines.append(f'            iconEmoji = "{escape_kt(emoji)}",')
    repo_lines.append(f'            levelIndex = {idx},')
    repo_lines.append(f'            levelLabel = "{escape_kt(lvl_label)}",')
    repo_lines.append(f'            phrases = allPhrases.filter {{ it.levelNumber == {idx} }}')
    repo_lines.append("        ),")

repo_lines.append("    )")
repo_lines.append("")
repo_lines.append("    val studyPlanStages: List<StudyLevelStage> = listOf(")

for idx in sorted(level_meta):
    meta = level_meta[idx]
    tier = next(t for t in curriculum_spec.curriculum_spec if t["tierId"] == meta["tierId"])
    tier_title = tier["tierName"]
    repo_lines.append("        StudyLevelStage(")
    repo_lines.append(f'            levelIndex = {idx},')
    repo_lines.append(f'            tierId = "{escape_kt(meta["tierId"])}",')
    repo_lines.append(f'            tierName = "{escape_kt(tier_title)}",')
    repo_lines.append(f'            rankTitle = "{escape_kt(meta["rankTitle"])}",')
    repo_lines.append(f'            topicTitle = "{escape_kt(meta["topicTitle"])}",')
    repo_lines.append(f'            description = "{escape_kt(meta["description"])}",')
    repo_lines.append(f'            iconEmoji = "{escape_kt(meta["iconEmoji"])}",')
    repo_lines.append(f'            deckId = "{escape_kt(meta["deckId"])}",')
    repo_lines.append(f'            requiredWordsToUnlock = {meta["requiredWords"]},')
    is_final = "true" if meta["isTierFinalStage"] else "false"
    repo_lines.append(f'            isTierFinalStage = {is_final}')
    repo_lines.append("        ),")

repo_lines.append("    )")
repo_lines.append("")

with open("repo_methods.kt.template", "r", encoding="utf-8") as f:
    repo_methods = f.read()

full_repo_content = "\n".join(repo_lines) + repo_methods
with open(os.path.join(data_dir, "DialectRepository.kt"), "w", encoding="utf-8") as f:
    f.write(full_repo_content)

print(f"Generated {os.path.join(data_dir, 'DialectRepository.kt')} successfully!")

# Generate the drill level mapping from the same metadata as the decks.
model_dir = os.path.join('app', 'src', 'main', 'java', 'com', 'example', 'model')
curriculum_lines = [
    'package com.example.model', '',
    '// Generated by build_full_repo.py from curriculum_spec.py.',
    'data class SpokenCurriculumLevel(',
    '    val tier: ActflProficiencyTier,',
    '    val levelNumber: Int,',
    '    val levelId: String,',
    '    val title: String,',
    '    val deckId: String,',
    '    val entryCount: Int',
    ')', '', 'object SpokenCurriculum {',
    '    const val VERSION = 2',
    '    val levels = listOf('
]
legacy_indices = {}
for tier in curriculum_spec.curriculum_spec:
    for number, level in enumerate(tier['levels'], 1):
        args = ', '.join([
            f"ActflProficiencyTier.{tier['tierId'].upper()}", str(number),
            f'"{escape_kt(level["levelId"])}"', f'"{escape_kt(level["topicTitle"])}"',
            f'"{escape_kt(level["deckId"])}"', str(level['wordsInLevel'])
        ])
        curriculum_lines.append(f'        SpokenCurriculumLevel({args}),')
        if 'legacyIndex' in level:
            legacy_indices[level['legacyIndex']] = level['levelIndex']
curriculum_lines += [
    '    )', '',
    '    private val legacyIndices = listOf(' + ', '.join(str(legacy_indices[i]) for i in range(24)) + ')',
    '    fun migrateLegacyIndex(index: Int): Int = legacyIndices[index.coerceIn(0, legacyIndices.lastIndex)]',
    '    fun levelForIndex(index: Int): SpokenCurriculumLevel = levels[index.coerceIn(0, levels.lastIndex)]',
    '    fun levelIndex(levelId: String): Int = levels.indexOfFirst { it.levelId == levelId }',
    '    fun entriesInTier(tier: ActflProficiencyTier): Int = levels.filter { it.tier == tier }.sumOf { it.entryCount }',
    '    fun cumulativeEntries(tier: ActflProficiencyTier): Int = levels.filter { it.tier.levelNumber <= tier.levelNumber }.sumOf { it.entryCount }',
    '}', ''
]
with open(os.path.join(model_dir, 'SpokenCurriculum.kt'), 'w', encoding='utf-8') as f:
    f.write('\n'.join(curriculum_lines))

# Reviewable inventory: exact material taught at each level, including stable IDs.
with open('curriculum_inventory.tsv', 'w', encoding='utf-8', newline='') as f:
    writer = csv.writer(f, delimiter='\t')
    writer.writerow(['level_index', 'tier', 'level', 'topic', 'id', 'manglish', 'malayalam', 'english'])
    for phrase in sorted(all_seed_phrases, key=lambda p: (prefix_to_level(p[0].split('_')[0]), p[0])):
        meta = level_meta[prefix_to_level(phrase[0].split('_')[0])]
        writer.writerow([meta['levelIndex'], meta['tierId'], meta['rankTitle'], meta['topicTitle'], *phrase[:4]])

plan = ['<!-- Generated by build_full_repo.py. -->',
        '# Spoken Malayalam level plan', '',
        'Counts are teaching entries (words, inflections, and phrases), not unique word families or proficiency scores.', '',
        '| Tier | Levels | Entries | Cumulative entries |',
        '| --- | ---: | ---: | ---: |']
cumulative = 0
for tier in curriculum_spec.curriculum_spec:
    entries = sum(l['wordsInLevel'] for l in tier['levels'])
    cumulative += entries
    plan.append(f"| {tier['tierId'].title()} | {len(tier['levels'])} | {entries} | {cumulative} |")
plan += ['', 'The [complete vocabulary inventory](curriculum_inventory.tsv) lists the words and phrases in every level.', '',
         '| Level | Topic | Entries | Learning goal |', '| --- | --- | ---: | --- |']
for tier in curriculum_spec.curriculum_spec:
    for level in tier['levels']:
        plan.append(f"| {level['rankTitle']} | {level['topicTitle']} | {level['wordsInLevel']} | {level['description']} |")
with open('CURRICULUM_LEVELS.md', 'w', encoding='utf-8') as f:
    f.write('\n'.join(plan) + '\n')
