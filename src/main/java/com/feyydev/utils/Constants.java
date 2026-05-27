package com.feyydev.utils;

import com.feyydev.models.*;
import java.util.Arrays;
import java.util.List;

public class Constants {
    public static final String GAME_TITLE = "Legends Of Tournament";
    public static final int WINDOW_WIDTH = 1000;
    public static final int WINDOW_HEIGHT = 700;
    public static final int MAX_PLAYER_LEVEL = 100;
    public static final int GACHA_COST = 300;
    public static final double SSR_RATE = 0.05;
    public static final double SR_RATE = 0.25;
    public static final double R_RATE = 0.70;
    public static final int ENERGY_PER_STAGE = 5;
    public static final int MAX_ENERGY = 100;
    public static final String SAVE_DIR = "saves";
    public static final String SAVE_FILE = "save.json";
    public static final int PITY_SSR = 90;
    public static final int PITY_SR = 10;
    public static final int MAX_TEAM_SIZE = 6;
    public static final int ATTENDANCE_DAYS = 28;
    public static final int RAID_TIME_LIMIT = 180;

    public static String getRarityColor(String rarity) {
        if (rarity == null) return "#60A5FA";
        return switch (rarity) {
            case "SSR" -> "#FFD700";
            case "SR" -> "#C084FC";
            case "Legendary", "Mythic" -> "#FF6B35";
            case "Epic" -> "#a855f7";
            case "Rare" -> "#3b82f6";
            default -> "#60A5FA";
        };
    }

    public static String getRarityBadge(String rarity) {
        if (rarity == null) return "R";
        return switch (rarity) {
            case "SSR" -> "SSR";
            case "SR" -> "SR";
            default -> "R";
        };
    }

    public static List<GameCharacter> createDefaultCharacters() {
        return Arrays.asList(
            // ===== SSR =====
            new GameCharacter("ssr_001", "Jin Mori", "SSR", "Martial Artist"),
            new GameCharacter("ssr_002", "Han Daewi", "SSR", "Martial Artist"),
            new GameCharacter("ssr_003", "Yu Mira", "SSR", "Sword User"),
            new GameCharacter("ssr_004", "Ilpyo Park", "SSR", "Mage"),
            new GameCharacter("ssr_005", "Park Mujin", "SSR", "Mage"),
            new GameCharacter("ssr_006", "Jegal Taek", "SSR", "Sword User"),
            new GameCharacter("ssr_007", "The King", "SSR", "Martial Artist"),
            new GameCharacter("ssr_008", "Maitreya", "SSR", "Support"),
            new GameCharacter("ssr_009", "Satan", "SSR", "Tank"),
            new GameCharacter("ssr_010", "Ogre", "SSR", "Tank"),
            new GameCharacter("ssr_011", "Dragon Knight", "SSR", "Sword User"),
            new GameCharacter("ssr_012", "Shadow Assassin", "SSR", "Assassin"),
            new GameCharacter("ssr_013", "Thunder God", "SSR", "Martial Artist"),
            new GameCharacter("ssr_014", "Ice Queen", "SSR", "Mage"),
            new GameCharacter("ssr_015", "Flame Emperor", "SSR", "Support"),
            // ===== SR =====
            new GameCharacter("sr_001", "Baek Seungchul", "SR", "Martial Artist"),
            new GameCharacter("sr_002", "Seo Hanryang", "SR", "Sword User"),
            new GameCharacter("sr_003", "Q", "SR", "Mage"),
            new GameCharacter("sr_004", "O", "SR", "Assassin"),
            new GameCharacter("sr_005", "Judge P", "SR", "Support"),
            new GameCharacter("sr_006", "Commissioner R", "SR", "Tank"),
            new GameCharacter("sr_007", "Wind Blade", "SR", "Sword User"),
            new GameCharacter("sr_008", "Shadow Step", "SR", "Assassin"),
            new GameCharacter("sr_009", "Crystal Mage", "SR", "Mage"),
            new GameCharacter("sr_010", "Iron Wall", "SR", "Tank"),
            new GameCharacter("sr_011", "Light Bringer", "SR", "Support"),
            new GameCharacter("sr_012", "Storm Fist", "SR", "Martial Artist"),
            new GameCharacter("sr_013", "Viper Strike", "SR", "Assassin"),
            new GameCharacter("sr_014", "Moon Blade", "SR", "Sword User"),
            new GameCharacter("sr_015", "Sage", "SR", "Mage"),
            new GameCharacter("sr_016", "Berserker", "SR", "Martial Artist"),
            new GameCharacter("sr_017", "Paladin", "SR", "Tank"),
            new GameCharacter("sr_018", "Shaman", "SR", "Support"),
            new GameCharacter("sr_019", "Night Walker", "SR", "Assassin"),
            new GameCharacter("sr_020", "Duelist", "SR", "Sword User"),
            // ===== R =====
            new GameCharacter("r_001", "Tournament Fighter", "R", "Martial Artist"),
            new GameCharacter("r_002", "Academy Student", "R", "Martial Artist"),
            new GameCharacter("r_003", "Street Brawler", "R", "Martial Artist"),
            new GameCharacter("r_004", "Sword Trainee", "R", "Sword User"),
            new GameCharacter("r_005", "Apprentice Mage", "R", "Mage"),
            new GameCharacter("r_006", "Scout", "R", "Assassin"),
            new GameCharacter("r_007", "Shield Bearer", "R", "Tank"),
            new GameCharacter("r_008", "Healer Initiate", "R", "Support"),
            new GameCharacter("r_009", "Fencer", "R", "Sword User"),
            new GameCharacter("r_010", "Brawler", "R", "Martial Artist"),
            new GameCharacter("r_011", "Cleric", "R", "Support"),
            new GameCharacter("r_012", "Rogue", "R", "Assassin"),
            new GameCharacter("r_013", "Guardian", "R", "Tank"),
            new GameCharacter("r_014", "Sorcerer", "R", "Mage"),
            new GameCharacter("r_015", "Mercenary", "R", "Sword User")
        );
    }

    public static List<Enemy> createStageEnemies(int chapter, int stage) {
        int level = (chapter - 1) * 10 + stage;
        boolean isBoss = stage == 10;
        boolean isElite = stage == 5;
        String[] normalTypes = {"Slime", "Goblin", "Orc", "Wolf", "Bandit", "Demon", "Skeleton", "Dark Elf", "Minotaur", "Golem"};
        String[] bossTypes = {"Titan Guardian", "Shadow Lord", "Demon King", "Celestial Dragon", "Supreme Overlord"};
        String type;

        if (isBoss) {
            int bossIdx = Math.min(chapter - 1, bossTypes.length - 1);
            type = bossTypes[bossIdx];
        } else if (isElite) {
            type = "Elite " + normalTypes[(stage - 1) % normalTypes.length];
        } else {
            type = normalTypes[(stage - 1) % normalTypes.length];
        }

        Enemy enemy = new Enemy("enemy_" + chapter + "_" + stage, type, type, level, isBoss);
        if (isElite) {
            enemy.setElite(true);
        }

        return Arrays.asList(enemy);
    }

    public static List<Weapon> createDefaultWeapons() {
        return Arrays.asList(
            new Weapon("w_common_001", "Practice Sword", "Sword", "Common", 10),
            new Weapon("w_common_002", "Wooden Staff", "Staff", "Common", 8),
            new Weapon("w_common_003", "Iron Dagger", "Dagger", "Common", 12),
            new Weapon("w_rare_001", "Steel Blade", "Sword", "Rare", 35),
            new Weapon("w_rare_002", "Enchanted Rod", "Staff", "Rare", 30),
            new Weapon("w_rare_003", "Shadow Knife", "Dagger", "Rare", 38),
            new Weapon("w_epic_001", "Dragon Slayer", "Sword", "Epic", 85),
            new Weapon("w_epic_002", "Arcane Scepter", "Staff", "Epic", 80),
            new Weapon("w_epic_003", "Moonlight Dagger", "Dagger", "Epic", 88),
            new Weapon("w_legendary_001", "Excalibur", "Sword", "Legendary", 220),
            new Weapon("w_legendary_002", "Staff of Ages", "Staff", "Legendary", 200),
            new Weapon("w_legendary_003", "Soul Reaper", "Dagger", "Legendary", 240),
            new Weapon("w_mythic_001", "Godslayer", "Sword", "Mythic", 500),
            new Weapon("w_mythic_002", "Celestial Scepter", "Staff", "Mythic", 480),
            new Weapon("w_mythic_003", "Void Blade", "Dagger", "Mythic", 520)
        );
    }

    public static List<Armor> createDefaultArmors() {
        return Arrays.asList(
            new Armor("a_common_001", "Cloth Armor", "Light", "Common", 8),
            new Armor("a_common_002", "Leather Vest", "Light", "Common", 10),
            new Armor("a_rare_001", "Chain Mail", "Medium", "Rare", 28),
            new Armor("a_rare_002", "Scale Armor", "Medium", "Rare", 30),
            new Armor("a_epic_001", "Knight Armor", "Heavy", "Epic", 65),
            new Armor("a_epic_002", "Dragon Hide", "Light", "Epic", 60),
            new Armor("a_legendary_001", "Divine Plate", "Heavy", "Legendary", 160),
            new Armor("a_legendary_002", "Phoenix Robe", "Light", "Legendary", 140),
            new Armor("a_mythic_001", "Guardian Aegis", "Heavy", "Mythic", 400),
            new Armor("a_mythic_002", "Ethereal Shroud", "Light", "Mythic", 380)
        );
    }

    public static List<Item> createDefaultPotions() {
        return Arrays.asList(
            new Item("potion_small", "Small Potion", "Consumable", "Restores 500 HP", "Common", 50, 500),
            new Item("potion_medium", "Medium Potion", "Consumable", "Restores 1500 HP", "Rare", 150, 1500),
            new Item("potion_large", "Large Potion", "Consumable", "Restores 5000 HP", "Epic", 400, 5000),
            new Item("potion_full", "Full Recovery", "Consumable", "Fully restores HP", "Legendary", 1000, 99999),
            new Item("energy_drink", "Energy Drink", "Consumable", "Restores 20 Energy", "Common", 100, 0),
            new Item("summon_ticket", "Summon Ticket", "Material", "Used for 1 summon", "Rare", 0, 0),
            new Item("summon_ticket_10", "10x Summon Ticket", "Material", "Used for 10 summons", "Epic", 0, 0),
            new Item("arena_token", "Arena Token", "Material", "Used for arena battles", "Common", 0, 0),
            new Item("raid_token", "Raid Token", "Material", "Used for raid battles", "Common", 0, 0)
        );
    }

    public static List<Quest> createDailyQuests() {
        return Arrays.asList(
            new Quest("quest_daily_kill", "Enemy Hunter", "Defeat 10 enemies in story mode", "Daily", "KILL", 10, 500, 50, 200),
            new Quest("quest_daily_stage", "Stage Clear", "Clear 5 stages", "Daily", "STAGE", 5, 300, 30, 150),
            new Quest("quest_daily_login", "Daily Login", "Login to the game", "Daily", "LOGIN", 1, 100, 10, 50),
            new Quest("quest_daily_gold", "Gold Collector", "Collect 1000 gold", "Daily", "GOLD", 1000, 200, 20, 100)
        );
    }

    public static List<Quest> createWeeklyQuests() {
        return Arrays.asList(
            new Quest("quest_weekly_kill", "Weekly Hunter", "Defeat 100 enemies", "Weekly", "KILL", 100, 5000, 500, 2000),
            new Quest("quest_weekly_stage", "Weekly Challenger", "Clear 30 stages", "Weekly", "STAGE", 30, 3000, 300, 1500),
            new Quest("quest_weekly_gacha", "Summoner", "Summon 10 characters", "Weekly", "SUMMON", 10, 1000, 200, 500)
        );
    }

    public static List<Quest> createAchievementQuests() {
        return Arrays.asList(
            new Quest("achieve_first_clear", "First Steps", "Clear your first stage", "Achievement", "STAGE", 1, 1000, 100, 500),
            new Quest("achieve_50_kills", "Warrior", "Defeat 50 enemies", "Achievement", "KILL", 50, 5000, 300, 1000),
            new Quest("achieve_100_kills", "Slayer", "Defeat 100 enemies", "Achievement", "KILL", 100, 10000, 500, 2000),
            new Quest("achieve_10_summon", "Collector", "Summon 10 characters", "Achievement", "SUMMON", 10, 2000, 200, 800),
            new Quest("achieve_chapter1", "Chapter 1 Clear", "Complete all stages in Chapter 1", "Achievement", "CHAPTER", 1, 5000, 300, 1500),
            new Quest("achieve_ssr_5", "SSR Collector", "Obtain 5 SSR characters", "Achievement", "SSR_COUNT", 5, 10000, 1000, 3000),
            new Quest("achieve_level_50", "Halfway There", "Reach player level 50", "Achievement", "PLAYER_LEVEL", 50, 20000, 1000, 5000)
        );
    }

    public static List<AttendanceReward> createAttendanceRewards() {
        return Arrays.asList(
            new AttendanceReward(1, "GOLD", 1000, "Gold", "Common", false),
            new AttendanceReward(2, "GEMS", 50, "Gems", "Common", false),
            new AttendanceReward(3, "ENERGY", 20, "Energy", "Common", false),
            new AttendanceReward(4, "ITEM", 3, "Small Potion", "Common", false),
            new AttendanceReward(5, "GOLD", 2000, "Gold", "Common", false),
            new AttendanceReward(6, "GEMS", 50, "Gems", "Common", false),
            new AttendanceReward(7, "CHARACTER", 0, "Random SR Character", "SR", true),
            new AttendanceReward(8, "GOLD", 3000, "Gold", "Common", false),
            new AttendanceReward(9, "GEMS", 80, "Gems", "Common", false),
            new AttendanceReward(10, "ITEM", 5, "Medium Potion", "Rare", false),
            new AttendanceReward(11, "TICKET", 1, "Summon Ticket", "Rare", false),
            new AttendanceReward(12, "GOLD", 5000, "Gold", "Common", false),
            new AttendanceReward(13, "GEMS", 100, "Gems", "Common", false),
            new AttendanceReward(14, "CHARACTER", 0, "Random SSR Character", "SSR", true),
            new AttendanceReward(15, "GOLD", 5000, "Gold", "Common", false),
            new AttendanceReward(16, "GEMS", 100, "Gems", "Common", false),
            new AttendanceReward(17, "ENERGY", 30, "Energy", "Common", false),
            new AttendanceReward(18, "TICKET", 2, "Summon Tickets", "Rare", false),
            new AttendanceReward(19, "GOLD", 8000, "Gold", "Common", false),
            new AttendanceReward(20, "GEMS", 150, "Gems", "Common", false),
            new AttendanceReward(21, "CHARACTER", 0, "Random SR Character", "SR", true),
            new AttendanceReward(22, "GOLD", 10000, "Gold", "Common", false),
            new AttendanceReward(23, "GEMS", 150, "Gems", "Common", false),
            new AttendanceReward(24, "ITEM", 10, "Large Potion", "Epic", false),
            new AttendanceReward(25, "TICKET", 5, "Summon Tickets", "Rare", false),
            new AttendanceReward(26, "GOLD", 15000, "Gold", "Common", false),
            new AttendanceReward(27, "GEMS", 200, "Gems", "Common", false),
            new AttendanceReward(28, "CHARACTER", 0, "Random SSR Character", "SSR", true)
        );
    }

    public static List<SummonBanner> createSummonBanners() {
        return Arrays.asList(
            new SummonBanner("banner_normal", "Normal Summon", "Standard banner with all characters", "Normal",
                Arrays.asList(), 0.0, 300, 1),
            new SummonBanner("banner_premium", "Premium Summon", "Increased SSR rates!", "Premium",
                Arrays.asList("ssr_001", "ssr_003", "ssr_007"), 0.03, 500, 1),
            new SummonBanner("banner_event", "Event Summon", "Featured characters rate up!", "Event",
                Arrays.asList("ssr_013", "ssr_014", "ssr_015"), 0.05, 400, 1)
        );
    }

    public static List<Mail> createWelcomeMail() {
        return Arrays.asList(
            new Mail("welcome_mail", "System", "\uD83C\uDF89", "Welcome to Legends Of Tournament!",
                "Thank you for joining! Here are your starter rewards. Enjoy the game!",
                5000, 300, 1000, 5, 0, null),
            new Mail("daily_gift", "System", "\uD83C\uDF81", "Daily Gift",
                "Your daily login gift is ready! Claim it now!",
                1000, 50, 200, 1, 0, null)
        );
    }

    public static String getCharIcon(String name) {
        if (name == null) return "\uD83D\uDC64";
        return switch (name) {
            case "Jin Mori", "Han Daewi", "Thunder God", "Storm Fist", "Berserker" -> "\uD83D\uDCAA";
            case "Yu Mira", "Moon Blade", "Duelist", "Fencer" -> "\u2694";
            case "Ilpyo Park", "Park Mujin", "Ice Queen", "Crystal Mage", "Sage", "Sorcerer" -> "\uD83E\uDDD9";
            case "The King", "Maitreya", "Light Bringer", "Healer Initiate", "Jegal Taek" -> "\uD83D\uDC66";
            case "Satan", "Ogre", "Iron Wall", "Shield Bearer", "Guardian" -> "\uD83D\uDC77";
            case "Shadow Assassin", "Viper Strike", "Night Walker", "Rogue" -> "\uD83D\uDDE1";
            case "Flame Emperor", "Shaman" -> "\uD83D\uDD25";
            case "Dragon Knight" -> "\uD83D\uDC32";
            case "Baek Seungchul" -> "\uD83E\uDD3B";
            case "Seo Hanryang" -> "\uD83E\uDDD8";
            case "Q" -> "\u2753";
            case "O" -> "\u2B55";
            case "Wind Blade" -> "\uD83C\uDF2C";
            case "Shadow Step" -> "\uD83D\uDC80";
            case "Judge P" -> "\u2696";
            case "Commissioner R" -> "\uD83D\uDC6E";
            case "Paladin", "Sword Trainee" -> "\uD83D\uDEE1";
            case "Street Brawler", "Brawler", "Mercenary" -> "\uD83E\uDD4A";
            case "Apprentice Mage" -> "\uD83E\uDDE0";
            case "Scout" -> "\uD83C\uDFF7";
            case "Cleric" -> "\u2728";
            case "Tournament Fighter" -> "\uD83C\uDFC6";
            case "Academy Student" -> "\uD83C\uDF93";
            default -> "\uD83D\uDC64";
        };
    }

    public static String getItemIcon(String type) {
        if (type == null) return "\uD83D\uDCE6";
        return switch (type) {
            case "Sword" -> "\u2694";
            case "Staff" -> "\uD83E\uDE84";
            case "Dagger" -> "\uD83D\uDDE1";
            case "Light" -> "\uD83D\uDC54";
            case "Medium" -> "\uD83D\uDC55";
            case "Heavy" -> "\uD83D\uDEE1";
            case "Consumable" -> "\uD83E\uDDEA";
            case "Material" -> "\uD83D\uDC8E";
            default -> "\uD83D\uDCE6";
        };
    }

    public static String formatNumber(long n) {
        if (n >= 1_000_000_000) return n / 1_000_000_000 + "B";
        if (n >= 1_000_000) return n / 1_000_000 + "M";
        if (n >= 1_000) return n / 1_000 + "K";
        return String.valueOf(n);
    }

    public static String getRarityHex(String rarity) {
        if (rarity == null) return "#60A5FA";
        return switch (rarity) {
            case "SSR", "Mythic" -> "#FFD700";
            case "SR", "Legendary" -> "#C084FC";
            case "Epic" -> "#a855f7";
            case "Rare" -> "#3b82f6";
            default -> "#60A5FA";
        };
    }

    public static String getCategoryColor(String category) {
        if (category == null) return "#888";
        return switch (category) {
            case "Martial Artist" -> "#ef4444";
            case "Sword User" -> "#3b82f6";
            case "Mage" -> "#a855f7";
            case "Assassin" -> "#f59e0b";
            case "Tank" -> "#10b981";
            case "Support" -> "#06b6d4";
            default -> "#888";
        };
    }

    // ── Story Dialogue System ──

    public record StoryDialogue(String speaker, String icon, String text) {}

    public static List<StoryDialogue> getStoryDialogues(int chapter) {
        return switch (chapter) {
            case 1 -> Arrays.asList(
                new StoryDialogue("Narrator", "\uD83D\uDCD6", "\"Legends Of Tournament\" \u2014 a world where fighters from every realm gather to prove their strength."),
                new StoryDialogue("Jin Mori", "\uD83D\uDCAA", "Heh... so this is the tournament arena. I've been waiting for this! Time to show everyone what I'm made of!"),
                new StoryDialogue("Han Daewi", "\uD83D\uDCAA", "Don't get too cocky, Mori. There are fighters here that could wipe the floor with us."),
                new StoryDialogue("Yu Mira", "\u2694\uFE0F", "He's right. I've seen some of the veterans in the waiting room. Their sword technique is no joke."),
                new StoryDialogue("Jin Mori", "\uD83D\uDCAA", "That's what makes it fun! The stronger they are, the more satisfying it is to beat them!"),
                new StoryDialogue("Narrator", "\uD83D\uDCD6", "The preliminary rounds begin. Fighters clash in explosive bouts across multiple rings."),
                new StoryDialogue("Jin Mori", "\uD83D\uDCAA", "Alright, first match is mine. Sit back and watch, guys!"),
                new StoryDialogue("Narrator", "\uD83D\uDCD6", "With overwhelming speed and power, Jin Mori defeats his opponent in seconds. The crowd erupts."),
                new StoryDialogue("Ilpyo Park", "\uD83E\uDDD9", "Impressive. But the real challenge starts now. The tournament has only just begun."),
                new StoryDialogue("Narrator", "\uD83D\uDCD6", "Chapter 1 Complete. The path to the championship is set... but darker forces lurk in the shadows.")
            );
            case 2 -> Arrays.asList(
                new StoryDialogue("Narrator", "\uD83D\uDCD6", "As the tournament progresses, strange occurrences ripple through the arena."),
                new StoryDialogue("Park Mujin", "\uD83E\uDDD9", "Did you feel that? The energy in the stadium just shifted."),
                new StoryDialogue("Jegal Taek", "\u2694\uFE0F", "Something is wrong. I've been watching the crowd \u2014 there are figures in the shadows."),
                new StoryDialogue("Yu Mira", "\u2694\uFE0F", "I've heard rumors of an organization using this tournament to recruit fighters for some kind of army."),
                new StoryDialogue("Han Daewi", "\uD83D\uDCAA", "An army? For what?"),
                new StoryDialogue("Park Mujin", "\uD83E\uDDD9", "For war. There's a conflict brewing beyond the tournament that none of us know about."),
                new StoryDialogue("Jin Mori", "\uD83D\uDCAA", "Then we fight. That's what we do, right? We'll deal with whatever comes."),
                new StoryDialogue("Narrator", "\uD83D\uDCD6", "A mysterious figure appears at the edge of the arena, watching the young fighters with keen interest."),
                new StoryDialogue("The King", "\uD83D\uDC66", "Interesting... these children have potential. Let us see how far they can go."),
                new StoryDialogue("Narrator", "\uD83D\uDCD6", "The second chapter closes. Enemies both visible and invisible begin to make their move.")
            );
            case 3 -> Arrays.asList(
                new StoryDialogue("Narrator", "\uD83D\uDCD6", "The tournament reaches its midpoint. Fighters begin to unlock hidden abilities."),
                new StoryDialogue("Satan", "\uD83D\uDC77", "You've grown stronger since we last met. But is that enough to face what's coming?"),
                new StoryDialogue("Jin Mori", "\uD83D\uDCAA", "I don't know what 'what's coming' is, but I'll break through it just like everything else!"),
                new StoryDialogue("Maitreya", "\uD83D\uDC66", "Your spirit is admirable. But strength alone won't protect you from the darkness ahead."),
                new StoryDialogue("Ogre", "\uD83D\uDC77", "The celestial powers are awakening. The old gods are watching this tournament closely."),
                new StoryDialogue("Ilpyo Park", "\uD83E\uDDD9", "Gods? You're telling me actual gods are interested in a fighting tournament?"),
                new StoryDialogue("Maitreya", "\uD83D\uDC66", "This is no ordinary tournament. It is a selection. The winners will inherit powers beyond imagination."),
                new StoryDialogue("Jin Mori", "\uD83D\uDCAA", "Then I guess I'll be inheriting all of it! I'm not losing to anyone \u2014 god or human!"),
                new StoryDialogue("Narrator", "\uD83D\uDCD6", "A massive earthquake shakes the arena. The sky tears open, revealing a celestial realm above."),
                new StoryDialogue("Narrator", "\uD83D\uDCD6", "Chapter 3 ends. The line between mortal and divine begins to blur.")
            );
            case 4 -> Arrays.asList(
                new StoryDialogue("Narrator", "\uD83D\uDCD6", "Celestial beings descend upon the tournament. The arena is no longer just a stage \u2014 it is a battlefield of gods."),
                new StoryDialogue("Dragon Knight", "\uD83D\uDC32", "I have crossed dimensions to witness this tournament. The power here is unlike anything I've sensed."),
                new StoryDialogue("Shadow Assassin", "\uD83D\uDDE1\uFE0F", "The dark one sends his regards. The underworld has placed bets on who will survive."),
                new StoryDialogue("Thunder God", "\uD83D\uDCAA", "Mortal, you stand before a god. Bow, and I may grant you a swift defeat."),
                new StoryDialogue("Jin Mori", "\uD83D\uDCAA", "Bow? Ha! I've never bowed to anyone. Come at me, 'god' \u2014 let's see what you've got!"),
                new StoryDialogue("Thunder God", "\uD83D\uDCAA", "...Bold words. Let us see if your fists can back them up!"),
                new StoryDialogue("Narrator", "\uD83D\uDCD6", "The battle shakes the heavens themselves. Jin Mori stands his ground against the divine."),
                new StoryDialogue("Ice Queen", "\uD83E\uDDD9", "Impressive... a mortal pushing back a god. This tournament truly is unprecedented."),
                new StoryDialogue("Flame Emperor", "\uD83D\uDD25", "The balance of power is shifting. If mortals can challenge gods, everything changes."),
                new StoryDialogue("Narrator", "\uD83D\uDCD6", "Chapter 4 concludes. The final battle awaits \u2014 only the strongest will claim the throne.")
            );
            case 5 -> Arrays.asList(
                new StoryDialogue("Narrator", "\uD83D\uDCD6", "The final chapter. Only the elite remain. The championship match is about to begin."),
                new StoryDialogue("Narrator", "\uD83D\uDCD6", "Five fighters stand at the peak. Five destinies intertwined. One champion will emerge."),
                new StoryDialogue("Jin Mori", "\uD83D\uDCAA", "This is it... everything we've fought for comes down to this moment."),
                new StoryDialogue("Han Daewi", "\uD83D\uDCAA", "No regrets. We came this far together. Let's end this properly."),
                new StoryDialogue("Yu Mira", "\u2694\uFE0F", "Win or lose, I'm proud to have fought alongside you both."),
                new StoryDialogue("Ilpyo Park", "\uD83E\uDDD9", "The tournament may end today, but our journey doesn't stop here."),
                new StoryDialogue("Narrator", "\uD83D\uDCD6", "The final battle commences. Blows trade faster than the eye can follow."),
                new StoryDialogue("Narrator", "\uD83D\uDCD6", "With a final, earth-shattering strike \u2014 the champion is decided."),
                new StoryDialogue("Jin Mori", "\uD83D\uDCAA", "We did it... WE DID IT! Legends Of Tournament champions, baby!"),
                new StoryDialogue("Narrator", "\uD83D\uDCD6", "The crowd roars. Confetti rains from above. A new legend is born.\n\nTHE END... or is it just the beginning?")
            );
            default -> Arrays.asList(
                new StoryDialogue("Narrator", "\uD83D\uDCD6", "No story available for this chapter yet.")
            );
        };
    }

    public static String getStoryChapterTitle(int chapter) {
        return switch (chapter) {
            case 1 -> "The Tournament Begins";
            case 2 -> "Rising Shadows";
            case 3 -> "Awakening Powers";
            case 4 -> "Clash of Titans";
            case 5 -> "Final Showdown";
            default -> "Unknown Chapter";
        };
    }

    public static String getStoryChapterIcon(int chapter) {
        return switch (chapter) {
            case 1 -> "\uD83C\uDFC6";
            case 2 -> "\uD83C\uDF19";
            case 3 -> "\u26A1";
            case 4 -> "\uD83D\uDD25";
            case 5 -> "\uD83D\uDC51";
            default -> "\u2753";
        };
    }

    public static boolean isChapterUnlocked(int chapter, int currentChapter) {
        return chapter <= currentChapter;
    }

    public static String getStoryBackground(int chapter) {
        return switch (chapter) {
            case 1 -> "linear-gradient(to bottom right, #1a1a2e, #16213e)";
            case 2 -> "linear-gradient(to bottom right, #0f0c29, #302b63)";
            case 3 -> "linear-gradient(to bottom right, #1a0a2e, #2d1b69)";
            case 4 -> "linear-gradient(to bottom right, #2d0a0a, #4a1a1a)";
            case 5 -> "linear-gradient(to bottom right, #0a0a2e, #1a1a4a)";
            default -> "linear-gradient(to bottom right, #0f1729, #1a2744)";
        };
    }

    // ── Event Mission System ──

    public record EventMission(String id, String category, String name, String description, String icon,
                                String requirement, int targetCount,
                                long rewardGold, long rewardGems, long rewardExp, int rewardTickets) {}

    public static List<EventMission> createEventMissions() {
        return Arrays.asList(
            // Beginner Event
            new EventMission("ev_beg_1", "Beginner Event", "First Stage", "Complete stage 1-1", "\u2694", "STAGE", 1, 500, 0, 100, 0),
            new EventMission("ev_beg_2", "Beginner Event", "Character Collector", "Obtain 3 characters", "\uD83D\uDC64", "CHARACTERS", 3, 1000, 50, 200, 0),
            new EventMission("ev_beg_3", "Beginner Event", "Player Level 5", "Reach account level 5", "\u2B50", "PLAYER_LEVEL", 5, 1500, 100, 500, 0),
            new EventMission("ev_beg_4", "Beginner Event", "Stage Clearer", "Clear 10 stages", "\u2694", "STAGE", 10, 2000, 100, 600, 0),
            new EventMission("ev_beg_5", "Beginner Event", "First Summons", "Summon 5 times", "\uD83C\uDF81", "SUMMON", 5, 2500, 150, 800, 5),
            new EventMission("ev_beg_6", "Beginner Event", "Player Level 10", "Reach account level 10", "\u2B50", "PLAYER_LEVEL", 10, 3000, 200, 1000, 0),
            new EventMission("ev_beg_7", "Beginner Event", "SSR Obtained", "Obtain 1 SSR character", "\uD83C\uDF1F", "SSR_COUNT", 1, 5000, 300, 2000, 10),
            new EventMission("ev_beg_8", "Beginner Event", "Chapter 1 Clear", "Complete all stages in Chapter 1", "\uD83C\uDFC6", "CHAPTER", 1, 5000, 500, 3000, 0),
            // Growth Event
            new EventMission("ev_gro_1", "Growth Event", "Player Level 20", "Reach account level 20", "\u2B50", "PLAYER_LEVEL", 20, 3000, 200, 1500, 0),
            new EventMission("ev_gro_2", "Growth Event", "Character Collector", "Obtain 10 characters", "\uD83D\uDC64", "CHARACTERS", 10, 4000, 300, 2000, 5),
            new EventMission("ev_gro_3", "Growth Event", "Stage Master", "Clear 50 stages", "\u2694", "STAGE", 50, 5000, 400, 3000, 0),
            new EventMission("ev_gro_4", "Growth Event", "Power 10K", "Reach 10,000 total power", "\uD83D\uDCAA", "POWER", 10000, 5000, 500, 2500, 10),
            new EventMission("ev_gro_5", "Growth Event", "Veteran Summoner", "Summon 30 times", "\uD83C\uDF81", "SUMMON", 30, 8000, 600, 4000, 15),
            // Carnival Event
            new EventMission("ev_car_1", "Carnival Event", "Loyal Player", "Login for 3 days", "\uD83D\uDCC5", "LOGIN", 3, 2000, 100, 500, 0),
            new EventMission("ev_car_2", "Carnival Event", "Battle Hardened", "Win 10 battles", "\u2694", "BATTLE_WIN", 10, 3000, 200, 1000, 3),
            new EventMission("ev_car_3", "Carnival Event", "Boss Slayer", "Defeat 5 bosses", "\uD83D\uDC7E", "BOSS_KILL", 5, 4000, 300, 1500, 5),
            new EventMission("ev_car_4", "Carnival Event", "Gold Hoarder", "Collect 50K gold", "\uD83D\uDCB0", "GOLD", 50000, 5000, 400, 2000, 0),
            // Ranking Event
            new EventMission("ev_rank_1", "Ranking Event", "PvP Fighter", "Win 3 PvP matches", "\u2694", "PVP_WIN", 3, 3000, 200, 1500, 0),
            new EventMission("ev_rank_2", "Ranking Event", "Arena Dominator", "Win 10 PvP matches", "\u2694", "PVP_WIN", 10, 5000, 500, 3000, 5),
            new EventMission("ev_rank_3", "Ranking Event", "Powerhouse", "Reach 50K total power", "\uD83D\uDCAA", "POWER", 50000, 10000, 1000, 5000, 10),
            new EventMission("ev_rank_4", "Ranking Event", "Raid Attacker", "Deal 100K boss damage", "\uD83D\uDC7E", "BOSS_DMG", 100000, 8000, 800, 4000, 5),
            // New Player Event
            new EventMission("ev_new_1", "New Player Event", "First Login", "Login for the first time", "\uD83C\uDF89", "LOGIN", 1, 1000, 100, 500, 3),
            new EventMission("ev_new_2", "New Player Event", "Team Building", "Build a team of 3 characters", "\uD83D\uDC64", "CHARACTERS", 3, 2000, 200, 1000, 0),
            new EventMission("ev_new_3", "New Player Event", "Stage Explorer", "Clear 5 stages", "\u2694", "STAGE", 5, 3000, 300, 1500, 5),
            new EventMission("ev_new_4", "New Player Event", "Player Level 8", "Reach account level 8", "\u2B50", "PLAYER_LEVEL", 8, 4000, 400, 2000, 0)
        );
    }

    public static String[] getEventCategories() {
        return new String[]{"Beginner Event", "Carnival Event", "New Player Event", "Growth Event", "Ranking Event"};
    }

    public static String getEventDescription(String category) {
        return switch (category) {
            case "Beginner Event" -> "Complete beginner missions to earn starter rewards!";
            case "Carnival Event" -> "Limited time carnival missions with special rewards!";
            case "New Player Event" -> "Exclusive missions for new players!";
            case "Growth Event" -> "Grow your power and earn growth rewards!";
            case "Ranking Event" -> "Compete in rankings for exclusive prizes!";
            default -> "Complete missions to earn rewards!";
        };
    }

    public static String getEventIcon(String category) {
        return switch (category) {
            case "Beginner Event" -> "\uD83C\uDF89";
            case "Carnival Event" -> "\uD83C\uDFAD";
            case "New Player Event" -> "\uD83C\uDF93";
            case "Growth Event" -> "\uD83D\uDCAA";
            case "Ranking Event" -> "\uD83C\uDFC6";
            default -> "\u2753";
        };
    }

    public static List<EventMission> getEventMissionsByCategory(String category) {
        return createEventMissions().stream()
            .filter(m -> m.category().equals(category))
            .toList();
    }
}
