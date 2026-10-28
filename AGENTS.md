# Workspace Agent Guidelines

## Performance and tool restrictions
- Do not scan, index, or parse `.gradle/`, `build/`, `intermediates/`, `.idea/`, or compiler caches. Treat these as generated artifacts.
- Do not run Gradle builds or related tasks in the background (including `./gradlew build` and `assembleDebug`) autonomously. Run them only when explicitly requested.
- Prefer concise, single-pass investigation and edits. Avoid recursive, multi-step exploratory searches; use targeted file reads and make direct source changes.
- Keep changes focused. Show concise diffs or focused snippets instead of re-emitting entire large files.

## Android architecture
- Use modern Android architecture with Kotlin, Coroutines/Flow, and Jetpack Compose.
- Follow existing project patterns and keep UI, domain, and data responsibilities appropriately separated.
