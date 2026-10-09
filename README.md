# 🎒 Tomorrow's Bag

A native Android app for tracking which notebooks a kid needs to bring to
school the next day — no sign-up, no account, everything stays on the
device.

## Status

- [x] Kid mode — greets the kid by name, shows their class, a subject grid
      to tap for tomorrow, a note field, and an "All Packed!" confirmation
- [x] Parent mode — kid's name/class/location, a usual weekly plan per
      weekday (suggests subjects automatically), manage subjects
      (add/rename/remove + emoji), school holidays (take priority over
      guessing), and history of past days
- [x] Everything stored locally (Room database + SharedPreferences) — fully
      standalone, not connected to any account or the web version

## Installing the latest build

Every push to `main` rebuilds the app and publishes a debug APK to a
rolling [**Latest debug build**](../../releases/tag/latest-debug) release —
download the `.apk` there and install it directly (allow installs from
this source when prompted).

One-time note: the debug keystore (`app/debug.keystore`) is committed to
the repo on purpose, so every CI build is signed with the same key. Without
it, each fresh CI runner would generate its own random debug key, making
every build "update-incompatible" with the last — installing a new APK
without first uninstalling the old one would fail with "App not installed."

## Building locally

```
./gradlew assembleDebug
```

The debug APK lands at `app/build/outputs/apk/debug/app-debug.apk`.

## Tech

- Kotlin + Jetpack Compose (Material 3), MVVM (one shared ViewModel, no
  Hilt/Dagger — a hand-written factory wires it up in `TomorrowsBagApp`)
- Room for local storage, `SharedPreferences` for the small profile fields
- `compileSdk`/`targetSdk` 35, `minSdk` 26
