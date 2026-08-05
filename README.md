# FinTrack

An offline-first personal finance tracker for Android, built to demonstrate senior-level Android architecture: offline-first data design, conflict resolution, background sync, multi-module Clean Architecture, and full-pyramid testing.

## Problem

Most expense trackers assume constant connectivity and either block on network calls or silently lose data when two devices edit the same record while offline. FinTrack is built around the opposite assumption: **the network is unreliable, and the app should never know or care.** Every read and write goes through a local Room database first; syncing to the cloud is a background concern that can fail, retry, and reconcile without the user ever seeing a spinner or losing an edit.

## Architecture

Clean Architecture across a multi-module Gradle project:

```
:app                    — application shell, DI graph, bottom-nav host
:core:ui                — design system, shared composables, theme
:core:data               — Room, DataStore, network client, sync engine
:core:domain             — use cases, repository interfaces, models (pure Kotlin, no Android deps)
:core:testing             — fakes and test rules shared across modules
:feature:transactions     — log/edit/list transactions ✅
:feature:accounts         — manage accounts ✅
:feature:budgets          — category budgets (planned)
:feature:reports          — monthly summaries (planned)
:feature:auth              — biometric lock, onboarding (planned)
```

Navigation uses Jetpack Navigation Compose (Nav2) with a bottom nav bar in `:app`. Navigation 3 reached stable in late 2025 and was evaluated for this project — deliberately deferred rather than migrated, noted in Roadmap below.

**Dependency rule:** `:core:domain` depends on nothing. `:core:data` implements domain's repository interfaces. `:feature:*` modules depend only on `:core:domain` and `:core:ui` — never on each other, and never directly on `:core:data`. Only `:app` knows every feature exists.

### Offline-first data flow

Every write (add, edit, delete) goes to Room immediately and is marked `PENDING` in a sync outbox. The UI observes Room via `Flow` — it never waits on the network. A `CoroutineWorker`, triggered both periodically (every 15 minutes) and immediately after a write, drains the outbox to Firestore and pulls remote changes down, merging them into Room.

### Conflict resolution

Field-level last-write-wins, compared via an `updatedAt` timestamp per record, with one deliberate exception: **deletes always win**, regardless of timestamp. This was a real bug I found and fixed during testing — an early version compared `updatedAt` uniformly, which meant a newer remote edit could resurrect a transaction that had been deleted offline. I also found and fixed a related ordering bug: syncing pushed local changes *before* pulling remote ones, which meant a local delete could silently overwrite a legitimate concurrent remote edit before the conflict-resolution logic ever got a chance to run. The fix was reordering the sync cycle to pull-then-push and making the merge function delete-aware. All three scenarios (remote-newer, local-newer, delete-vs-edit) are covered by automated tests in `TransactionConflictResolverTest`.

Deletes are soft (`isDeleted` flag), never hard-removed, specifically so this comparison has something to reconcile against.

### Scaling sync across multiple entities

Rather than a separate `WorkManager` job per entity (Transactions, Accounts, and later Budgets), all entities implement a shared `SyncEngine` interface and are bound into a `Set<SyncEngine>` via Hilt multibinding (`@Binds @IntoSet`). A single `SyncWorker` injects the full set and runs every entity's sync in one scheduled job. This keeps one periodic schedule and one immediate-trigger path regardless of how many entities exist — adding a new syncable entity means writing one new `SyncEngine` implementation and one binding line, with no changes to the worker itself.

Each entity currently has its own conflict resolver (`TransactionConflictResolver`, `AccountConflictResolver`) rather than a single generic one — a deliberate choice at this scale (see Roadmap).

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

Full testing pyramid, applied consistently across both completed features (Transactions, Accounts), all currently passing:
- **Unit** — pure conflict-resolution logic per entity, isolated from I/O (`TransactionConflictResolverTest`, `AccountConflictResolverTest`)
- **Repository** — repository implementations against in-memory fake DAOs (`FakeTransactionDao`, `FakeAccountDao`), verifying real add/observe/soft-delete behavior rather than mocked method calls
- **ViewModel** — `StateFlow` combination and emission order tested with Turbine and a custom `MainDispatcherRule` (a JUnit5 extension, since Compose UI tests below still run on JUnit4 — Compose's testing infrastructure predates JUnit5 support)
- **Compose UI** — screen rendering, dialog flows, and tap interactions via `createAndroidComposeRule`, including negative-path cases (e.g. cancelling a dialog doesn't persist data)

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

- [x] Transactions feature, full stack (sync, conflict resolution, tests)
- [x] Accounts feature, full stack
- [x] Generic `Set<SyncEngine>` multibinding architecture (scales to N entities without new WorkManager jobs)
- [ ] Budgets feature (category limits, progress tracking)
- [ ] Reports feature (monthly summaries, charts)
- [ ] Biometric app lock
- [ ] Generalize per-entity conflict resolvers (`TransactionConflictResolver`, `AccountConflictResolver`) behind a bounded generic type once a 4th+ syncable entity exists — deferred deliberately; not enough duplication yet to justify the added indirection
- [ ] Migrate from Navigation 2 to Navigation 3 — evaluated once Nav3 reached stable, deferred to avoid disrupting feature velocity; revisit once core features are complete
- [ ] Instrumented tests in CI