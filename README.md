# FinTrack

An offline-first personal finance tracker for Android, built to demonstrate senior-level Android architecture: offline-first data design, conflict resolution, background sync, multi-module Clean Architecture, and full-pyramid testing.

## Problem

Most expense trackers assume constant connectivity and either block on network calls or silently lose data when two devices edit the same record while offline. FinTrack is built around the opposite assumption: **the network is unreliable, and the app should never know or care.** Every read and write goes through a local Room database first; syncing to the cloud is a background concern that can fail, retry, and reconcile without the user ever seeing a spinner or losing an edit.

## Architecture

Clean Architecture across a multi-module Gradle project:

```
:app                    — application shell, DI graph, nav host
:core:ui                — design system, shared composables, theme
:core:data               — Room, DataStore, network client, sync engine
:core:domain             — use cases, repository interfaces, models (pure Kotlin, no Android deps)
:core:testing             — fakes and test rules shared across modules
:feature:transactions     — log/edit/list transactions
:feature:accounts         — manage accounts
:feature:budgets          — category budgets
:feature:reports          — monthly summaries
:feature:auth              — biometric lock, onboarding
```

**Dependency rule:** `:core:domain` depends on nothing. `:core:data` implements domain's repository interfaces. `:feature:*` modules depend only on `:core:domain` and `:core:ui` — never on each other, and never directly on `:core:data`. Only `:app` knows every feature exists.

### Offline-first data flow

Every write (add, edit, delete) goes to Room immediately and is marked `PENDING` in a sync outbox. The UI observes Room via `Flow` — it never waits on the network. A `CoroutineWorker`, triggered both periodically (every 15 minutes) and immediately after a write, drains the outbox to Firestore and pulls remote changes down, merging them into Room.

### Conflict resolution

Field-level last-write-wins, compared via an `updatedAt` timestamp per record, with one deliberate exception: **deletes always win**, regardless of timestamp. This was a real bug I found and fixed during testing — an early version compared `updatedAt` uniformly, which meant a newer remote edit could resurrect a transaction that had been deleted offline. I also found and fixed a related ordering bug: syncing pushed local changes *before* pulling remote ones, which meant a local delete could silently overwrite a legitimate concurrent remote edit before the conflict-resolution logic ever got a chance to run. The fix was reordering the sync cycle to pull-then-push and making the merge function delete-aware. All three scenarios (remote-newer, local-newer, delete-vs-edit) are covered by automated tests in `TransactionConflictResolverTest`.

Deletes are soft (`isDeleted` flag), never hard-removed, specifically so this comparison has something to reconcile against.

### Money handling

Amounts are stored as `Long` cents, never `Double`/`Float` — floating point is unsuitable for currency (0.1 + 0.2 ≠ 0.3). Formatting to display currency happens only at the UI layer.

## Tech stack

| Layer | Choice |
|---|---|
| UI | Jetpack Compose, Material3 |
| DI | Hilt |
| Local persistence | Room (Flow-based reactive queries) |
| Remote | Cloud Firestore |
| Background work | WorkManager (`CoroutineWorker` + Hilt integration) |
| Async | Kotlin Coroutines + Flow |
| Testing | JUnit5 (unit), Turbine (Flow testing), MockK, Compose UI Test (JUnit4, per Compose's testing infrastructure) |
| CI | GitHub Actions (lint + unit tests on every push) |

## Testing

Full testing pyramid, all currently passing:
- **Unit** — pure conflict-resolution logic, isolated from I/O (`TransactionConflictResolverTest`)
- **Repository** — `TransactionRepositoryImpl` against an in-memory fake DAO (`FakeTransactionDao`), verifying real add/observe/soft-delete behavior rather than mocked method calls
- **ViewModel** — `StateFlow` combination and emission order tested with Turbine and a custom `MainDispatcherRule`
- **Compose UI** — screen rendering, tap interactions, and conditional UI (debug controls) via `createAndroidComposeRule`

Run unit tests: `./gradlew test`
Run instrumented Compose tests (requires a connected device/emulator): `./gradlew connectedDebugAndroidTest`

## CI

GitHub Actions runs lint and the full JVM unit test suite (unit + repository + ViewModel layers) on every push and PR. Instrumented Compose UI tests are not yet part of the CI pipeline, since they require an emulator runner — a documented next step, not an oversight.

## Setup

1. Clone the repo
2. Create a Firebase project, enable **Cloud Firestore** (not Realtime Database — they're separate products), using the `(default)` database ID
3. Download your own `google-services.json` from Firebase Console and place it at `app/google-services.json` (intentionally excluded from this repo via `.gitignore`, since it's project-specific config)
4. Set Firestore rules to allow read/write for development (see `firestore.rules` — **test-mode rules only, not production-ready**)
5. Build and run

## Known simplifications

Documented deliberately, not hidden:
- Firestore security rules are open (`allow read, write: if true`) for development — a production version would need real auth-scoped rules
- Room migrations use `fallbackToDestructiveMigration` during active schema iteration rather than hand-written `Migration` objects — appropriate for a project with no real user data to preserve yet
- No authentication layer yet — single implicit user per Firestore project

## Roadmap

- [ ] Budgets feature (category limits, progress tracking)
- [ ] Reports feature (monthly summaries, charts)
- [ ] Biometric app lock
- [ ] Migrate sync engine to a generic multi-entity contract (currently transaction-specific; accounts/budgets will need their own sync engines following the same pattern)
- [ ] Instrumented tests in CI
