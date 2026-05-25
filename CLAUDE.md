# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

ClassSync is an Android classroom management app built with Java, Firebase Authentication, Firestore, and Firebase Cloud Messaging. It supports two user roles (teacher/student) with role-specific UI flows and features for course management, assignments, group collaboration, and notifications.

## Build & Development Commands

```bash
# Build
./gradlew assembleDebug          # Build debug APK
./gradlew assembleRelease        # Build release APK (ProGuard disabled)

# Testing
./gradlew test                   # Run unit tests (JUnit 4)
./gradlew connectedAndroidTest   # Run instrumented tests (needs emulator/device)

# Code Quality
./gradlew lint                   # Run Android Lint
./gradlew clean                  # Clean build artifacts

# Firebase
firebase emulators:start --only functions   # Test Cloud Functions locally
firebase deploy --only functions            # Deploy Cloud Functions (currently unused)
firebase deploy --only firestore:rules      # Deploy Firestore security rules
firebase deploy --only storage:rules        # Deploy Storage security rules
```

**Requirements:**
- Gradle 9.3.1, Android Gradle Plugin 9.1.1
- Java 21 toolchain (configured in `app/build.gradle.kts:39-42`)
- `google-services.json` must exist in `app/` for Firebase initialization
- Dependencies managed via version catalog at `gradle/libs.versions.toml`

## Architecture

### Module Structure
- Single `:app` module, Java-only (no Kotlin)
- 41 Java source files organized into packages:
  - `data/` — models, Firebase repositories, UserSession
  - `ui/auth/` — SplashFragment, LoginFragment, RegisterFragment
  - `ui/teacher/` — 9 fragments for teacher features
  - `ui/student/` — 12 fragments for student features
  - `ui/shared/` — NotificationsFragment, ProfileFragment (role-agnostic)
  - `notification/` — NotificationHelper for local notification channels
  - `worker/` — DeadlineReminderWorker (WorkManager, currently disabled)

### Navigation
- Jetpack Navigation Component with `nav_graph.xml` in `app/src/main/res/navigation/`
- Bottom navigation is **role-aware**: teachers navigate to `TeacherHomeFragment` (R.id.nav_home), students to `StudentHomeFragment` (R.id.studentHomeFragment)
- Role-aware navigation logic lives in `MainActivity.java:50-83` (OnItemSelectedListener)
- `MainActivity.java:91-131` syncs bottom nav highlight state with current destination
- `MainActivity.java:140-177` implements `setSelectedItemSilently()` to prevent navigation loops

### State Management
- `ViewModel` + `LiveData` pattern for UI state
- `UserSession.java` caches user profile in `SharedPreferences` (uid, email, role, fullName, avatarUrl)
- Role constants: `AuthRepository.ROLE_TEACHER` and `AuthRepository.ROLE_STUDENT`

### Data Models
All in `data/model/`:
- `AppUser` — uid, email, fullName, role, avatarUrl, fcmToken
- `Course` — courseId, name, section, joinCode, teacherId, studentIds, isArchived
- `Assignment` — assignmentId, title, description, dueDate, maxGroupSize, requiresGroups
- `Group` — groupId, name, leaderId, memberIds, isFull, taskCount, completedTaskCount
- `TaskItem` — taskId, title, isDone, assignedTo, assignedName
- `AppNotification` — notificationId, recipientId, type, title, body, courseId, assignmentId, isRead, createdAt

## Firebase Integration

### Authentication
- `AuthRepository.java` handles register, login, logout, session restoration
- User role ("teacher" or "student") is stored in Firestore `users/{uid}` document
- FCM token stored in user document for push notifications

### Firestore Structure
Paths built via `FirestorePaths.java`:
```
users/{uid}
courses/{courseId}
  └─ assignments/{assignmentId}
      └─ groups/{groupId}
          └─ tasks/{taskId}
notifications/{notificationId}  (top-level collection)
```

### Security Rules
`firestore.rules` at root:
- Users can only read/write their own `users/{uid}` document
- Notifications: users can read/update their own; only Cloud Functions can create
- Courses: teachers create/update/delete their courses; any authenticated user can read (for join-code lookup)
- Assignments: course teacher can write; teacher + enrolled students can read
- Groups: students can create only when joining (must be first member + leader); members can update (leader can rename); tasks scoped to group members

`storage.rules` at root:
- Avatar uploads to `avatars/{uid}` with 2MB limit and image/* content type

### Cloud Functions (Disabled)
`functions/index.js` contains 3 Cloud Functions (Node 20) that were replaced by client-side logic:
1. `onAssignmentCreated` — send FCM notifications to enrolled students (now pulled on app open in `StudentHomeFragment.java`)
2. `onTaskCompleted` — notify group members when a task is marked done (now handled via Firestore `addSnapshotListener` in `GroupWorkspaceFragment.java`)
3. `sendDeadlineReminders` — scheduled function to send reminders 24h before due date (now replaced by `WorkManager` periodic worker in `DeadlineReminderWorker.java`, but currently commented out)

**Note:** Cloud Functions are deployed but not invoked. The app uses client-side logic instead to avoid Firebase Blaze plan costs.

### Notifications
- Local notifications via `NotificationHelper.java` with channels: "deadlines", "assignments", "task_updates"
- WorkManager periodic worker for deadline reminders (currently disabled, code preserved in comments in `MainActivity.java:39-42, 179-200` and `DeadlineReminderWorker.java`)
- FCM notifications handled by `MyFirebaseMessagingService.java`

## Important Notes

### Package vs Application ID Mismatch
- Package namespace: `com.example.classsync`
- Application ID: `com.chestlyace.classync`
- This is intentional and works correctly — import statements use package namespace

### Cloud Functions State
- Functions exist in `functions/` directory but are **not actively used**
- Replaced by client-side pull on app open + Firestore snapshot listeners
- Do not modify or deploy without understanding the billing implications

### Frontend Prototype
- `classsync frontend/` is a **static Tailwind CSS design prototype** (Figma export)
- Not a runnable React/Vue app — just HTML/CSS mockups
- Do not attempt to run or integrate it

### Disabled Features
WorkManager and local notification permissions are commented out throughout the codebase:
- `MainActivity.java` lines 13-23, 39-42, 179-200
- `AndroidManifest.xml` lines 6-8
- Keep these commented unless explicitly re-enabling local notifications

### Navigation Gotchas
- Home destination differs by role: `R.id.nav_home` (teacher) vs `R.id.studentHomeFragment` (student)
- When adding new fragments to the Courses tab, update `MainActivity.java:118-125` to sync bottom nav highlight
- Use `NavOptions.Builder().setPopUpTo(homeDestId, false).setLaunchSingleTop(true)` to prevent fragment stacking

## Testing
- No custom test configuration beyond Android defaults
- Unit tests (JUnit 4) in `app/src/test/`
- Instrumented tests (Espresso) in `app/src/androidTest/`
- No CI/CD workflows currently configured
