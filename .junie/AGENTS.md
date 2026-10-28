# JetBrains Junie Project Guidelines

Follow the workspace-wide instructions in the root `AGENTS.md`.

- Do not scan, index, or parse `.gradle/`, `build/`, `intermediates/`, `.idea/`, or compiler caches.
- Do not autonomously run Gradle builds or related tasks in the background; do so only when explicitly requested.
- Keep investigation and edits concise and single-pass, favoring targeted reads and direct source modifications over recursive exploratory searches.
- Use Kotlin, Coroutines/Flow, and Jetpack Compose with modern Android architecture.
- Return focused diffs or snippets, not complete large files.
