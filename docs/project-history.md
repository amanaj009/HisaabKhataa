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

## Recommended next steps

- Replace `SampleData` with repository + API layer.
- Move `AppState` into screen ViewModels.
- Add real Hindi copy across all screens.
- Implement add-entry forms instead of guide placeholder.
- Add persistence, auth backend, notification scheduling, and export flows.
