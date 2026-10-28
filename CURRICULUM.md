# Spoken Malayalam curriculum

The course contains **996 teaching entries across 40 levels**, up from 630 entries
across 24 levels. It adds 366 entries in 16 practical units. See
[the level plan](CURRICULUM_LEVELS.md) for topics and counts, and
[the vocabulary inventory](curriculum_inventory.tsv) for every entry by level.

## Why this sequence

Early units introduce addressing people, introductions, asking for repetition,
numbers, food, actions, and simple descriptions. Elementary units develop everyday
transactions, time, travel, shopping, routines, health, and home life. Intermediate
units develop connected stories, work and learning, preferences, relationships,
and informal conversation. Later units add nuance, explanation, negotiation,
news discussion, idioms, and cultural interpretation.

Units contain 18–30 entries according to the scope of the task. These are editorial
choices: research does **not** establish a universal number of words per level or
justify a claim that a beginner's working memory holds 25 new words. A unit can be
studied over multiple sessions. It is not a daily new-word quota.

The design draws on these sources:

- [Nation, *The Four Strands* (2007)](https://www.wgtn.ac.nz/lals/resources/paul-nations-resources/paul-nations-publications/publications/documents/2007-Four-strands.pdf):
  balance learning about language with meaningful input, meaningful output, and
  practice using familiar language fluently. The existing recall drills and spaced
  reviews support part of that programme. Learners should also listen to speakers,
  retell experiences, and hold conversations; adding cards alone does not supply
  all four strands.
- [Kim & Webb, *The Effects of Spaced Practice on Second Language Learning* (2022)](https://doi.org/10.1111/lang.12479):
  supports distributing practice over time. Keep reviewing earlier material after
  moving to another unit. Completing three drill rounds does not establish durable
  long-term mastery by itself.
- [Council of Europe, CEFR global scale](https://www.coe.int/en/web/common-European-framework-reference-languages/table-1-cefr-3.3-common-reference-levels-global-scale):
  proficiency concerns what someone can understand and communicate. Course tier
  names and spoken course reviews are not CEFR or ACTFL assessments.
- [Nation, *How large a vocabulary is needed for reading and listening?* (2006)](https://doi.org/10.26686/wgtn.12552221.v1):
  estimates thousands of word families for broad coverage of **English** texts.
  Those estimates are not Malayalam fluency cutoffs and cannot be equated with
  counts of flashcards.

Short goal-based units, revisiting familiar material, and recall practice are useful
features to take from app-based teaching. This plan does not copy Duolingo's unit
sizes or claim that finishing an app proves conversational fluency.

## What the counts mean

An entry may be a word, an inflected form, or a multiword phrase. Related and repeated
expressions can appear in different contexts. **996 entries does not mean 996 unique
word families.** Tier vocabulary targets now describe the content actually available
in the app, instead of advertising unsupported targets of up to 8,000 words.

This is an expanded practical foundation, not a complete route to fluent or native
speech. Further vocabulary, extensive listening, and spontaneous interaction are
still needed. The additions are authored teaching material, not a corpus-ranked or
native-speaker-validated Malayalam syllabus; dialect and phrasing review would
improve the content before a public release.

## Maintaining and upgrading the data

- `curriculum_spec.py` defines the ordered units and permanent level/deck IDs.
- Existing `generate_*.py` files retain the original phrase IDs and content;
  `curriculum_additions.py` supplies the new units.
- Run `python -B build_full_repo.py` from the project root to regenerate Kotlin
  seed data, drill metadata, the level plan, and the inventory. Counts are checked
  against the specification; duplicate phrase IDs are rejected.
- Database seeding inserts missing cards and refreshes content and level metadata
  in a transaction. It preserves review intervals, ease, repetition counts, and
  timestamps. This is a content update, so the Room schema version stays at 1.
- The versioned local progress migration maps the original 24 indices to the same
  units in the new sequence. Phrase/deck IDs, learned records, and previously
  earned graduations remain intact. Added units are not marked completed; returning
  learners can revisit them among their unlocked levels.

Lightweight validation: `python -B -m unittest test_curriculum.py`.
Android regression tests cover alignment, Room reseeding, and progress migration;
they require an explicitly requested Gradle test run under this workspace's rules.
