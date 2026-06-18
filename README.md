# Hisaab Khataa

Hisaab Khataa is a Kotlin + Jetpack Compose Android prototype for a Forvill-ledger style product inspired by the merchant workflow of apps like Khatabook, but with original branding, layout choices, and UI direction.

Current prototype coverage:

1. Splash
2. Language selection
3. Onboarding
4. Mobile login
5. OTP verification
6. Business setup
7. Dashboard
8. Party list
9. Party detail ledger
10. Add entry guide screen
11. Reminders
12. Reports
13. Settings
14. Privacy policy

Current product-strength upgrades:

1. Central English/Hindi copy layer for the main flow
2. Real add-entry form with type, amount, note, and due label
3. In-memory ledger update flow so saved entries immediately reflect in party balances
4. Backend-ready repository and API service scaffolding for future PHP integration
5. More practical dashboard and ledger summaries for merchant-style usage
6. Party create/edit flow inside the app
7. Dynamic reminders and reports derived from live in-memory ledger state
8. API payload preview inside add-entry so backend wiring is easier to implement next
9. Local SQLite persistence for parties and ledger entries across app restarts
10. Due-date based reminder labeling using stored `YYYY-MM-DD` values
11. Persistent business profile and app settings stored locally on-device
12. Local future-sync queue with retry metadata for add-entry and party changes
13. Report date-range filters with local CSV export flow

Build command:

```powershell
.\gradlew.bat :app:compileDebugKotlin
```
