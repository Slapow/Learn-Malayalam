# Learn Malayalam

An Android application for learning spoken Malayalam with Manglish typing and speech practice, focused on colloquial Central Travancore (Adoor & Pathanamthitta) dialect.

## Features
* **Spoken & Written Practice**: Interactive drills with native speech and audio practice.
* **Dialect Guidance**: Specialized focus on regional Central Travancore Malayalam.
* **AI Tutor Integration**: Dynamic Gemini API integration for translations and tutoring.
* **Progress Tracking**: Cloud-synced progress and streak statistics using Firebase.

## Run Locally

**Prerequisites:** [Android Studio](https://developer.android.com/studio)

1. Open Android Studio.
2. Select **Open** and choose the root directory of this project.
3. Allow Android Studio to sync Gradle and download required dependencies.
4. Create a `.env` file in the project root directory and define your `GEMINI_API_KEY`:
   ```env
   GEMINI_API_KEY=YOUR_GEMINI_API_KEY_HERE
   ```
   *(Refer to `.env.example` for details)*
5. Connect an Android device or start an emulator.
6. Click **Run** in Android Studio to build and launch the app.
