package com.feyydev.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Player {
    private String name;
    private int level;
    private long exp;
    private long expToNext;
    private long gold;
    private long gems;
    private int energy;
    private int maxEnergy;
    private int currentChapter;
    private int currentStage;
    private int dailyLoginStreak;
    private String lastLoginDate;
    private int attendanceDay;
    private long arenaTokens;
    private long raidTokens;
    private long totalPower;
    private long totalBossDamage;
    private int pvpWins;
    private int pvpLosses;
    private int pvpScore;
    private String guildName;
    private List<GameCharacter> characters;
    private List<Item> inventory;
    private List<Weapon> weapons;
    private List<Armor> armors;
    private List<Mail> mailbox;
    private List<String> teamIds;
    private String activeTeamId;
    private boolean beginnerEventClaimed;
    private List<String> completedAchievements;
    private int lastStoryChapter;
    private Map<String, Integer> eventProgress;
    private List<String> claimedEventMissions;

    public Player() {
        this.name = "Player";
        this.level = 1;
        this.exp = 0;
        this.expToNext = 100;
        this.gold = 1000;
        this.gems = 500;
        this.energy = 50;
        this.maxEnergy = 50;
        this.currentChapter = 1;
        this.currentStage = 1;
        this.dailyLoginStreak = 0;
        this.lastLoginDate = "";
        this.attendanceDay = 0;
        this.arenaTokens = 10;
        this.raidTokens = 3;
        this.totalPower = 0;
        this.totalBossDamage = 0;
        this.pvpWins = 0;
        this.pvpLosses = 0;
        this.pvpScore = 1000;
        this.guildName = "";
        this.characters = new ArrayList<>();
        this.inventory = new ArrayList<>();
        this.weapons = new ArrayList<>();
        this.armors = new ArrayList<>();
        this.mailbox = new ArrayList<>();
        this.teamIds = new ArrayList<>();
        this.activeTeamId = null;
        this.beginnerEventClaimed = false;
        this.completedAchievements = new ArrayList<>();
        this.lastStoryChapter = 0;
        this.eventProgress = new HashMap<>();
        this.claimedEventMissions = new ArrayList<>();
    }

    public void addExp(long amount) {
        exp += amount;
        while (exp >= expToNext && level < 100) {
            exp -= expToNext;
            level++;
            expToNext = (long)(100 * Math.pow(1.2, level - 1));
        }
        if (level >= 100) { exp = 0; expToNext = 0; }
    }

    public boolean useEnergy(int amount) {
        if (energy >= amount) { energy -= amount; return true; }
        return false;
    }

    public void addGold(long amount) { gold += amount; }
    public boolean spendGold(long amount) {
        if (gold >= amount) { gold -= amount; return true; }
        return false;
    }
    public void addGems(long amount) { gems += amount; }
    public boolean spendGems(long amount) {
        if (gems >= amount) { gems -= amount; return true; }
        return false;
    }
    public void addArenaTokens(long amount) { arenaTokens += amount; }
    public boolean spendArenaTokens(long amount) {
        if (arenaTokens >= amount) { arenaTokens -= amount; return true; }
        return false;
    }
    public void addRaidTokens(long amount) { raidTokens += amount; }
    public boolean spendRaidTokens(long amount) {
        if (raidTokens >= amount) { raidTokens -= amount; return true; }
        return false;
    }

    public long calcTotalPower() {
        long power = 0;
        for (GameCharacter c : characters) {
            power += c.getAttack() + c.getDefense() + c.getMaxHp() / 10;
        }
        totalPower = power;
        return power;
    }

    public String getName() { return name; }
    public void setName(String n) { this.name = n; }
    public int getLevel() { return level; }
    public void setLevel(int l) { this.level = l; }
    public long getExp() { return exp; }
    public void setExp(long e) { this.exp = e; }
    public long getExpToNext() { return expToNext; }
    public void setExpToNext(long e) { this.expToNext = e; }
    public long getGold() { return gold; }
    public void setGold(long g) { this.gold = g; }
    public long getGems() { return gems; }
    public void setGems(long g) { this.gems = g; }
    public int getEnergy() { return energy; }
    public void setEnergy(int e) { this.energy = e; }
    public int getMaxEnergy() { return maxEnergy; }
    public void setMaxEnergy(int m) { this.maxEnergy = m; }
    public int getCurrentChapter() { return currentChapter; }
    public void setCurrentChapter(int c) { this.currentChapter = c; }
    public int getCurrentStage() { return currentStage; }
    public void setCurrentStage(int s) { this.currentStage = s; }
    public int getDailyLoginStreak() { return dailyLoginStreak; }
    public void setDailyLoginStreak(int d) { this.dailyLoginStreak = d; }
    public String getLastLoginDate() { return lastLoginDate; }
    public void setLastLoginDate(String d) { this.lastLoginDate = d; }
    public int getAttendanceDay() { return attendanceDay; }
    public void setAttendanceDay(int a) { this.attendanceDay = a; }
    public long getArenaTokens() { return arenaTokens; }
    public void setArenaTokens(long a) { this.arenaTokens = a; }
    public long getRaidTokens() { return raidTokens; }
    public void setRaidTokens(long r) { this.raidTokens = r; }
    public long getTotalPower() { return totalPower; }
    public void setTotalPower(long t) { this.totalPower = t; }
    public long getTotalBossDamage() { return totalBossDamage; }
    public void setTotalBossDamage(long t) { this.totalBossDamage = t; }
    public int getPvpWins() { return pvpWins; }
    public void setPvpWins(int p) { this.pvpWins = p; }
    public int getPvpLosses() { return pvpLosses; }
    public void setPvpLosses(int p) { this.pvpLosses = p; }
    public int getPvpScore() { return pvpScore; }
    public void setPvpScore(int p) { this.pvpScore = p; }
    public String getGuildName() { return guildName; }
    public void setGuildName(String g) { this.guildName = g; }

    public int getLastStoryChapter() { return lastStoryChapter; }
    public void setLastStoryChapter(int c) { this.lastStoryChapter = c; }
    public List<GameCharacter> getCharacters() { if (characters == null) characters = new ArrayList<>(); return characters; }
    public void setCharacters(List<GameCharacter> c) { this.characters = c; }
    public List<Item> getInventory() { if (inventory == null) inventory = new ArrayList<>(); return inventory; }
    public void setInventory(List<Item> i) { this.inventory = i; }
    public List<Weapon> getWeapons() { if (weapons == null) weapons = new ArrayList<>(); return weapons; }
    public void setWeapons(List<Weapon> w) { this.weapons = w; }
    public List<Armor> getArmors() { if (armors == null) armors = new ArrayList<>(); return armors; }
    public void setArmors(List<Armor> a) { this.armors = a; }
    public List<Mail> getMailbox() { if (mailbox == null) mailbox = new ArrayList<>(); return mailbox; }
    public void setMailbox(List<Mail> m) { this.mailbox = m; }
    public List<String> getTeamIds() { if (teamIds == null) teamIds = new ArrayList<>(); return teamIds; }
    public void setTeamIds(List<String> t) { this.teamIds = t; }
    public String getActiveTeamId() { return activeTeamId; }
    public void setActiveTeamId(String a) { this.activeTeamId = a; }
    public boolean isBeginnerEventClaimed() { return beginnerEventClaimed; }
    public void setBeginnerEventClaimed(boolean b) { this.beginnerEventClaimed = b; }
    public List<String> getCompletedAchievements() { if (completedAchievements == null) completedAchievements = new ArrayList<>(); return completedAchievements; }
    public void setCompletedAchievements(List<String> c) { this.completedAchievements = c; }

    public Map<String, Integer> getEventProgress() { if (eventProgress == null) eventProgress = new HashMap<>(); return eventProgress; }
    public void setEventProgress(Map<String, Integer> m) { this.eventProgress = m; }
    public List<String> getClaimedEventMissions() { if (claimedEventMissions == null) claimedEventMissions = new ArrayList<>(); return claimedEventMissions; }
    public void setClaimedEventMissions(List<String> l) { this.claimedEventMissions = l; }

    public GameCharacter getEquippedCharacter() {
        if (characters == null || characters.isEmpty()) return null;
        if (activeTeamId == null) {
            activeTeamId = characters.get(0).getId();
            return characters.get(0);
        }
        return characters.stream().filter(c -> c.getId().equals(activeTeamId)).findFirst().orElse(null);
    }
}
