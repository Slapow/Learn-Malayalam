"""Source and SQLite checks that run without Gradle or Android build artifacts."""
import csv
import importlib
import json
from pathlib import Path
import re
import sqlite3
import unittest

from curriculum_additions import phrases_for
from curriculum_spec import curriculum_spec

ROOT = Path(__file__).parent


class CurriculumTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.levels = [level for tier in curriculum_spec for level in tier['levels']]
        cls.phrases = []
        for tier in curriculum_spec:
            name = tier['tierId']
            module = importlib.import_module('generate_' + name)
            cls.phrases += getattr(module, 'get_' + name + '_phrases')() + phrases_for(name)

    def test_every_card_belongs_to_a_nonempty_unit_with_matching_counts(self):
        self.assertEqual(996, len(self.phrases))
        self.assertEqual(996, len({p[0] for p in self.phrases}))
        self.assertEqual(list(range(40)), [l['levelIndex'] for l in self.levels])
        self.assertEqual(40, len({l['levelId'] for l in self.levels}))
        self.assertEqual(40, len({l['deckId'] for l in self.levels}))
        prefixes = {l['prefix'] for l in self.levels}
        for phrase in self.phrases:
            self.assertIn(phrase[0].split('_')[0], prefixes)
            self.assertTrue(all(phrase[:4]))
            self.assertTrue(any('\u0d00' <= c <= '\u0d7f' for c in phrase[2]))
        total = 0
        for level in self.levels:
            cards = [p for p in self.phrases if p[0].split('_')[0] == level['prefix']]
            self.assertEqual(level['wordsInLevel'], len(cards))
            self.assertEqual(total, level['requiredWords'])
            total += len(cards)

    def test_tier_sizes_and_final_gates(self):
        self.assertEqual([7, 11, 10, 7, 5], [len(t['levels']) for t in curriculum_spec])
        for tier in curriculum_spec:
            self.assertEqual([tier['levels'][-1]], [l for l in tier['levels'] if l['isTierFinalStage']])
        self.assertGreater(len({l['wordsInLevel'] for l in self.levels}), 2)

    def test_legacy_mapping_keeps_all_original_units(self):
        legacy = {l['legacyIndex']: l for l in self.levels if 'legacyIndex' in l}
        self.assertEqual(set(range(24)), set(legacy))
        source = (ROOT / 'app/src/main/java/com/example/model/SpokenCurriculum.kt').read_text(encoding='utf-8')
        indices = list(map(int, re.search(r'legacyIndices = listOf\((.*?)\)', source)[1].split(',')))
        self.assertEqual([legacy[i]['levelIndex'] for i in range(24)], indices)
        self.assertEqual('deck_elem_1_questions_direction', self.levels[indices[4]]['deckId'])
        self.assertEqual('deck_mst_5_dialect_switching', self.levels[indices[23]]['deckId'])

    def test_generated_inventory_matches_source_content(self):
        with (ROOT / 'curriculum_inventory.tsv').open(encoding='utf-8', newline='') as f:
            inventory = list(csv.DictReader(f, delimiter='\t'))
        self.assertEqual(len(self.phrases), len(inventory))
        self.assertEqual({p[:4] for p in self.phrases},
                         {(r['id'], r['manglish'], r['malayalam'], r['english']) for r in inventory})
        for tier in curriculum_spec:
            source = (ROOT / f"app/src/main/java/com/example/data/{tier['tierId'].title()}Phrases.kt").read_text(encoding='utf-8')
            expected = {p[0] for p in self.phrases if p[0].split('_')[0] in {l['prefix'] for l in tier['levels']}}
            self.assertEqual(expected, set(re.findall(r'\bid = "([^"]+)"', source)))

    def test_production_seed_update_preserves_review_columns(self):
        source = (ROOT / 'app/src/main/java/com/example/data/DrillItemDao.kt').read_text(encoding='utf-8')
        query = re.search(r'UPDATE drill_items SET.*?WHERE id = :itemId', source, re.S)[0]
        params = dict(tier='ELEMENTARY', levelId='elementary_1_questions_directions',
                      levelNumber=1, malayalam='new', manglish='new', english='new',
                      spokenExampleMalayalam='', spokenExampleManglish='', spokenExampleEnglish='', itemId='e1_01')
        with sqlite3.connect(':memory:') as db:
            db.execute('CREATE TABLE drill_items (id TEXT PRIMARY KEY, tier TEXT, levelId TEXT, levelNumber INTEGER, '
                       'malayalam TEXT, manglish TEXT, english TEXT, spokenExampleMalayalam TEXT, '
                       'spokenExampleManglish TEXT, spokenExampleEnglish TEXT, intervalDays INTEGER, '
                       'repetitionCount INTEGER, easeFactor REAL, nextReviewTimestamp INTEGER, lastReviewedTimestamp INTEGER)')
            db.execute("INSERT INTO drill_items VALUES ('e1_01', 'NOVICE', 'old', 4, '', '', '', '', '', '', 17, 4, 2.3, 999, 123)")
            db.execute(query, params)
            db.execute(query, params)
            self.assertEqual(('ELEMENTARY', params['levelId'], 1, 'new', 17, 4, 2.3, 999, 123),
                             db.execute('SELECT tier, levelId, levelNumber, english, intervalDays, repetitionCount, easeFactor, '
                                        'nextReviewTimestamp, lastReviewedTimestamp FROM drill_items').fetchone())

    def test_generated_translations_match_vocabulary_sources(self):
        expected = {p[0]: p[1:4] for p in self.phrases}
        actual = {}
        for tier in curriculum_spec:
            source = (ROOT / f"app/src/main/java/com/example/data/{tier['tierId'].title()}Phrases.kt").read_text(encoding='utf-8')
            for block in source.split('PhraseItem(')[1:]:
                fields = [json.loads(re.search(r'\b' + key + r' = ("(?:\\.|[^"\\])*")', block)[1])
                          for key in ('id', 'manglish', 'malayalam', 'english')]
                actual[fields[0]] = tuple(fields[1:])
        self.assertEqual(expected, actual)

    def test_family_and_food_cards_teach_the_selected_sense(self):
        phrases = {p[0]: p for p in self.phrases}
        for ident, english in {
            'n0_11': 'Husband', 'n0_13': 'Grandmother', 'n0_14': 'Grandfather',
            'n0_15': 'Older brother', 'n0_20': 'Brother-in-law',
            'n1_01': 'Cooked rice', 'n1_09': 'Chickpeas',
            'n1_13': 'Buttermilk', 'n1_24': 'Sweetness',
        }.items():
            with self.subTest(ident=ident):
                self.assertEqual(english, phrases[ident][3])
        self.assertNotEqual(phrases['n0_04'][3], phrases['n0_05'][3])

    def test_corrected_script_and_manglish_pairs(self):
        phrases = {p[0]: p for p in self.phrases}
        for ident, forms in {
            'e3_19': ('Cheyyoolla', 'ചെയ്യൂല്ല'),
            'e4_09': ('Vayaruvedana', 'വയറുവേദന'),
            'e4_14': ('Pani', 'പനി'),
            'e5_11': ('Mesha', 'മേശ'),
            'a5_05': ('Njaan yojikkunnilla', 'ഞാൻ യോജിക്കുന്നില്ല'),
            'm4_15': ('Vichaaradhaara', 'വിചാരധാര'),
        }.items():
            with self.subTest(ident=ident):
                self.assertEqual(forms, phrases[ident][1:3])

    def test_course_review_material_was_taught_in_the_assessed_tier(self):
        source = (ROOT / 'repo_methods.kt.template').read_text(encoding='utf-8')
        reviews = re.findall(r'ActflProficiencyTier\.(\w+) -> listOf\(([^\n]+)\)', source)
        self.assertEqual(5, len(reviews))
        for tier, values in reviews:
            prefixes = {l['prefix'] for t in curriculum_spec if t['tierId'] == tier.lower() for l in t['levels']}
            taught = {p[0] for p in self.phrases if p[0].split('_')[0] in prefixes}
            for phrase_id in re.findall(r'"([^"]+)"', values):
                self.assertIn(phrase_id, taught)


if __name__ == '__main__':
    unittest.main()
