# ClassSync

Android classroom management app (Java, Firebase Auth + Firestore).

## Build & run

```bash
./gradlew assembleDebug          # build APK
./gradlew test                   # unit tests (JUnit 4)
./gradlew connectedAndroidTest   # instrumented tests (Espresso) — needs emulator/device
./gradlew lint                   # Android Lint
```

- **Gradle 9.3.1**, AGP 9.1.1, **Java 21** toolchain (config in `app/build.gradle.kts:39-42`).
- `gradle.properties` sets `org.gradle.java.home` to Java 17 — override if using Java 21.
- Version catalog at `gradle/libs.versions.toml`.

## Architecture

- Single `:app` module, Java-only (no Kotlin).
- 19 fragments organized as `ui/auth/`, `ui/teacher/`, `ui/student/`, `ui/shared/`.
- Navigation via Jetpack Navigation Component (`nav_graph.xml`) with `NavHostFragment`.
- Bottom navigation is **role-aware**: teacher gets `TeacherHomeFragment`, student gets `StudentHomeFragment`. Logic in `MainActivity.java:33-65`.
- State: `ViewModel` + `LiveData`. Session cached in `SharedPreferences` via `UserSession.java`.
- Data models: `AppUser`, `Course`, `Assignment`, `TaskItem`, `Group`, `AppNotification`.

## Firebase / Firestore

- `AuthRepository.java` handles register/login/session restore. Role is one of `"teacher"` or `"student"` (constants in `AuthRepository.java:23-24`).
- Firestore document paths are built by `FirestorePaths.java`:
  `users/{uid}`, `courses/{id}`, then subcollections: `assignments`, `groups`, `tasks`.
- `firestore.rules` at root — only allows user-level read/write to their own `users/{uid}` doc.
- `google-services.json` required in `app/` for Firebase init.
- `dataconnect/` is a separate Firebase Data Connect (PostgreSQL) experiment — not used by the Android app.

## Notable

- **`package` vs `applicationId` differ**: namespace is `com.example.classsync`, applicationId is `com.chestlyace.classync`.
- `classsync frontend/` is a **static Tailwind CSS design prototype** (Figma export), not a runnable frontend.
- No CI/CD workflows, no README, no lint config beyond Android Lint defaults.
- ProGuard disabled for release (`isMinifyEnabled = false`).
