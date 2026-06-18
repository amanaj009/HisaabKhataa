# Project History

## v0.1.0

- Created fresh Android project `HisaabKhataa` in `E:\AndroidStudioProjects`.
- Set up Kotlin + Compose single-module architecture.
- Added startup flow: splash, language, onboarding, login, OTP, business setup.
- Added main app flow: dashboard, parties, ledger detail, add entry, reminders, reports, settings, privacy policy.
- Added sample data layer for business profile, customers, ledger entries, reminders, and reports.
- Added basic comments in key navigation/data files to make future refactors easier.

## v0.2.0

- Added centralized app copy model for English and Hindi-driven main screens.
- Upgraded `AddEntryScreen` from placeholder guidance to a real form with entry preview.
- Added mutable app state so new ledger entries update party balance and ledger history immediately.
- Introduced backend-ready contracts: `LedgerRepository`, `FakeLedgerRepository`, and `LedgerApiService`.
- Improved dashboard and party detail screens to feel more practical for merchant usage.
- Normalized visible money labels to `Rs.` to avoid text encoding issues in the current environment.

## v0.3.0

- Added party creation and party editing flow with reusable draft state.
- Made reminders and report cards dynamic from the current party ledger state instead of static sample-only lists.
- Added backend-oriented ledger payload model and payload preview in the add-entry screen.
- Extended app state to manage party drafts, derived reminders, derived reports, and payload generation.
- Verified the Android module still compiles after the new navigation and state wiring.

## v0.4.0

- Replaced sample-only runtime storage with local SQLite-backed persistence for parties and ledger entries.
- Added production repository wiring so the app now boots from a persistence-backed repository instead of a fake in-memory one.
- Added `HttpLedgerApiService` scaffold and API config placeholders for future PHP endpoint sync.
- Added stored due-date value support and due-date based reminder labels such as overdue, today, and tomorrow.
- Improved Hindi copy phrasing in key onboarding, auth, and action flows.

## v0.5.0

- Extended local SQLite storage to persist merchant profile, language/reminder settings, and a future sync queue.
- Added local-first retry metadata and deferred sync strategy so add-entry and party changes are queued for future backend activation.
- Added report date-range filtering using stored entry timestamps and local CSV export generation.
- Upgraded settings screen into a practical profile/settings control surface with sync queue visibility.
- Kept the current build fully local-only with no Google login or active cloud dependency.

## Recommended next steps

- Move `AppState` into screen ViewModels.
- Add real Hindi copy across all screens.
- Add delete/reset and backup-restore flows for local data.
- Replace deferred sync queue with active PHP API sync when backend is ready.
