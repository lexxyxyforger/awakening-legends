# Legends Of Tournament

A desktop RPG built with **Java 21** and **JavaFX 21.0.2** — a fanmade tribute to *The God of Highschool*.

## Features

- **13 interactive scenes**: Home Lobby, Battle, Character, Inventory, Shop, Quest, Gacha/Summon, Event, Mailbox, Attendance, World Map, Boss Raid, Story
- **10 manager singletons**: Character, Inventory, Quest, Attendance, Raid, Arena, Event, Gacha, Battle, Save
- **12 data models**: Player, GameCharacter, Enemy, Item, Weapon, Armor, Quest, Mail, SummonBanner, RaidBoss, AttendanceReward, StatusEffect
- **Gacha system** with rate-up banners, pity counters (SSR @ 90, SR @ 10), and pull history
- **Turn-based combat** with skills, ultimates, potions, crits, and damage popups
- **Progression systems**: character leveling, awakening, evolution, skill upgrades, equipment
- **28-day attendance rewards**, daily/weekly/achievement quests, event missions
- **Boss raids** with 3-minute timer, contribution tracking, and rewards
- **Story mode** with 5 chapters, dialogue system, and chapter rewards
- **Persistent save** via JSON (Gson), auto-saved on scene transitions
- **Dark anime RPG UI** with glassmorphism, neon glow, particle effects

## Requirements

- JDK 21+
- Apache Maven 3.8+

## Quick Start

```bash
# Clone and run
git clone <repo-url>
cd goh-rpg
mvn javafx:run
```

## Commands

| Action | Command |
|--------|---------|
| Run GUI | `mvn javafx:run` |
| Compile | `mvn compile` |
| Test | `mvn test` |
| Clean build | `mvn clean compile` |

## Project Structure

```
src/main/java/com/feyydev/
├── Main.java               # JavaFX entry point + scene hub
├── components/              # Reusable UI widgets
│   ├── CharacterCardComponent.java
│   ├── CharacterDetailPanel.java
│   ├── GlassPanel.java
│   ├── MissionCard.java
│   ├── NavigationBar.java
│   ├── RewardCard.java
│   └── TopBar.java
├── managers/                # Singleton game managers
│   ├── ArenaManager.java
│   ├── AttendanceManager.java
│   ├── BattleManager.java
│   ├── CharacterManager.java
│   ├── EventManager.java
│   ├── GachaManager.java
│   ├── InventoryManager.java
│   ├── QuestManager.java
│   ├── RaidManager.java
│   └── SaveManager.java
├── models/                  # Data POJOs
│   ├── Armor.java
│   ├── AttendanceReward.java
│   ├── Enemy.java
│   ├── GameCharacter.java
│   ├── Item.java
│   ├── Mail.java
│   ├── Player.java
│   ├── Quest.java
│   ├── RaidBoss.java
│   ├── StatusEffect.java
│   ├── SummonBanner.java
│   └── Weapon.java
├── scenes/                  # All 14 scenes
│   ├── AttendanceScene.java
│   ├── BattleScene.java
│   ├── CharacterScene.java
│   ├── EventScene.java
│   ├── GachaScene.java
│   ├── HomeScene.java
│   ├── InventoryScene.java
│   ├── MailboxScene.java
│   ├── QuestScene.java
│   ├── RaidScene.java
│   ├── ShopScene.java
│   ├── SplashScene.java
│   ├── StoryScene.java
│   └── WorldMapScene.java
├── services/
│   ├── AudioManager.java
│   ├── JsonService.java
│   └── MailService.java
└── utils/
    └── Constants.java       # Game constants + data factory (529 lines)

src/main/resources/com/feyydev/
├── global.css               # Unified dark theme design system
├── home.css                 # Home scene specific styles
├── character.css            # Character scene specific styles
├── style.css                # Legacy white theme (unused)
└── assets/                  # Placeholder asset directories
    ├── backgrounds/
    ├── characters/
    ├── enemies/
    └── ui/
```

## Architecture

- Each scene receives `Player` and a `Consumer<SceneType>` navigator for transitions
- All managers are singletons accessed via `getInstance()` then `setPlayer(player)`
- Save data persists to `saves/save.json` via Gson
- Character art uses emoji icons from `Constants.getCharIcon()` (no PNG assets)
- AudioManager is a stub — all `play*()` methods print to console
- Window is fixed at 1000×700, non-resizable

## Scene Navigation

| SceneType | Scene | Description |
|-----------|-------|-------------|
| `HOME` | HomeScene | Character lobby with quick actions |
| `BATTLE` | BattleScene | Turn-based combat |
| `CHARACTER` | CharacterScene | Character detail + selector |
| `INVENTORY` | InventoryScene | Items, weapons, armor |
| `SHOP` | ShopScene | Gold/Gem/Event/Premium shops |
| `QUEST` | QuestScene | Daily, weekly, achievement quests |
| `GACHA` | GachaScene | Summon banners with rates & pity |
| `EVENT` | EventScene | Limited-time event missions |
| `MAILBOX` | MailboxScene | Claimable mail with rewards |
| `ATTENDANCE` | AttendanceScene | 28-day login rewards |
| `WORLD_MAP` | WorldMapScene | Stage progression (10 stages × 5 chapters) |
| `RAID` | RaidScene | Boss raid with 3-min timer |
| `STORY` | StoryScene | 5-chapter narrative with dialogues |
