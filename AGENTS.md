# Legends Of Tournament — Agent Guide

**Java 21 + JavaFX 21.0.2 Maven project** — desktop RPG with 13 scenes, 10 managers, 12 models, JSON save via Gson.

## Entrypoint

- Main class: `com.feyydev.Main` (JavaFX `Application`)
- `App.java` has been removed — do not recreate

## Commands

| Action | Command |
|--------|---------|
| Run GUI | `mvn javafx:run` |
| Build | `mvn compile` |
| Test | `mvn test` |
| Clean+build | `mvn clean compile` |

No CI, formatter, linter, or typecheck.

## Architecture

```
Main (JavaFX entry, navigation hub via mainApp.navigateTo(SceneType))
├── scenes/*      (13 scenes, each takes Main mainApp as constructor param)
├── managers/*    (10 singletons, all require getInstance().setPlayer(player))
├── models/*      (12 POJOs)
├── services/*    (3 singletons)
└── utils/Constants (constants + factory methods for all default game data)
```

### SceneType enum (in `Main.java`)
`HOME, BATTLE, CHARACTER, INVENTORY, SHOP, QUEST, GACHA, EVENT, MAILBOX, ATTENDANCE, WORLD_MAP, RAID, STORY`

## Key conventions (easy to miss)

- **Managers are singletons**: `getInstance()` then `setPlayer(player)` before use. All 10 managers are wired with `setPlayer()` in `Main.start()`.
- **`Constants.java` is also a data factory**: `createDefaultCharacters()`, `createStageEnemies()`, `createDefaultWeapons/Armors/Items()`, `createDaily/Weekly/AchievementQuests()`, `createAttendanceRewards()`, `createSummonBanners()`, `createWelcomeMail()`, `createEventMissions()`, `getStoryDialogues()`. Do NOT hardcode these values elsewhere.
- **Assets directories exist but are empty**: `src/main/resources/com/feyydev/assets/{backgrounds,characters,enemies,ui}/` — all image paths are placeholders. CSS-only visuals.
- **AudioManager is stub**: all `play*()` methods just `System.out.println()`. No real audio files.
- **Save path**: `saves/save.json` (relative to project root), managed by `SaveManager` using Gson.
- **All scene constructors** receive `Main mainApp` to call `mainApp.navigateTo(SceneType)` for scene transitions.

## Dependencies

| GroupId | ArtifactId | Version | Scope |
|---------|-----------|---------|-------|
| `org.openjfx` | `javafx-controls` | 21.0.2 | compile |
| `com.google.code.gson` | `gson` | 2.10.1 | compile |
| `junit` | `junit` | 4.13.2 | test |

## Repo quirks

- `.sixth/skills/` — local skill system dir, empty. Ignore.
- `.github/modernize/java-upgrade/` — migration hooks, not CI. Ignore.
- No root `.gitignore` — `target/` and `saves/` are tracked.
- Only test: `AppTest` (JUnit 4, trivial `assertTrue(true)`).
