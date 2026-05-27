package com.feyydev.models;

import java.util.ArrayList;
import java.util.List;

public class GameCharacter {
    private String id;
    private String name;
    private String rarity;
    private String category;
    private int level;
    private int maxLevel;
    private long hp;
    private long maxHp;
    private long attack;
    private long defense;
    private double criticalChance;
    private double criticalDamage;
    private double skillDamage;
    private double ultimateDamage;
    private long speed;
    private long exp;
    private long expToNext;
    private String equippedWeaponId;
    private String equippedArmorId;
    private int duplicates;
    private int shards;
    private int awakeningLevel;
    private int evolutionLevel;
    private int skillLevel;

    public GameCharacter() {
        this.skillLevel = 1;
    }

    public GameCharacter(String id, String name, String rarity, String category) {
        this.id = id;
        this.name = name;
        this.rarity = rarity;
        this.category = category;
        this.level = 1;
        this.maxLevel = rarity.equals("SSR") ? 120 : rarity.equals("SR") ? 100 : 80;
        this.exp = 0;
        this.expToNext = 100;
        this.duplicates = 0;
        this.shards = 0;
        this.awakeningLevel = 0;
        this.evolutionLevel = 0;
        this.skillLevel = 1;
        initStats();
    }

    private void initStats() {
        switch (rarity) {
            case "SSR":
                maxHp = 5000 + (long)(level * 150) + (awakeningLevel * 500L) + (evolutionLevel * 1000L);
                hp = maxHp;
                attack = 300 + (long)(level * 20) + (awakeningLevel * 30L) + (evolutionLevel * 60L);
                defense = 200 + (long)(level * 15) + (awakeningLevel * 20L) + (evolutionLevel * 40L);
                criticalChance = 0.15 + (awakeningLevel * 0.02);
                criticalDamage = 1.8 + (awakeningLevel * 0.1);
                skillDamage = 2.5 + (skillLevel * 0.3);
                ultimateDamage = 4.0 + (skillLevel * 0.5);
                speed = 100 + (long)(level * 2);
                break;
            case "SR":
                maxHp = 3000 + (long)(level * 100) + (awakeningLevel * 300L);
                hp = maxHp;
                attack = 180 + (long)(level * 12) + (awakeningLevel * 18L);
                defense = 120 + (long)(level * 10) + (awakeningLevel * 12L);
                criticalChance = 0.10 + (awakeningLevel * 0.015);
                criticalDamage = 1.5 + (awakeningLevel * 0.08);
                skillDamage = 1.8 + (skillLevel * 0.2);
                ultimateDamage = 3.0 + (skillLevel * 0.35);
                speed = 90 + (long)(level * 1.5);
                break;
            default:
                maxHp = 1500 + (long)(level * 50) + (awakeningLevel * 150L);
                hp = maxHp;
                attack = 80 + (long)(level * 6) + (awakeningLevel * 10L);
                defense = 60 + (long)(level * 5) + (awakeningLevel * 6L);
                criticalChance = 0.05 + (awakeningLevel * 0.01);
                criticalDamage = 1.2 + (awakeningLevel * 0.05);
                skillDamage = 1.3 + (skillLevel * 0.15);
                ultimateDamage = 2.0 + (skillLevel * 0.25);
                speed = 80 + (long)(level * 1);
                break;
        }
    }

    public void recalcStats() { initStats(); }

    public void addExp(long amount) {
        exp += amount;
        while (exp >= expToNext && level < maxLevel) {
            exp -= expToNext;
            level++;
            expToNext = (long)(100 * Math.pow(1.15, level - 1));
            initStats();
        }
        if (level >= maxLevel) { exp = 0; expToNext = 0; }
    }

    public void healFull() { hp = maxHp; }

    public boolean canAwaken() {
        return awakeningLevel < 5 && shards >= (awakeningLevel + 1) * 30;
    }

    public boolean awaken() {
        if (!canAwaken()) return false;
        shards -= (awakeningLevel + 1) * 30;
        awakeningLevel++;
        initStats();
        return true;
    }

    public boolean canEvolve() {
        return evolutionLevel < 3 && duplicates >= (evolutionLevel + 1);
    }

    public boolean evolve() {
        if (!canEvolve()) return false;
        duplicates -= (evolutionLevel + 1);
        evolutionLevel++;
        initStats();
        return true;
    }

    public boolean canUpgradeSkill() {
        return skillLevel < 10 && shards >= skillLevel * 5;
    }

    public boolean upgradeSkill() {
        if (!canUpgradeSkill()) return false;
        shards -= skillLevel * 5;
        skillLevel++;
        initStats();
        return true;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRarity() { return rarity != null ? rarity : "R"; }
    public void setRarity(String rarity) { this.rarity = rarity; }
    public String getCategory() { return category != null ? category : "Martial Artist"; }
    public void setCategory(String category) { this.category = category; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; recalcStats(); }
    public int getMaxLevel() { return maxLevel; }
    public long getHp() { return hp; }
    public void setHp(long hp) { this.hp = Math.max(0, Math.min(hp, maxHp)); }
    public long getMaxHp() { return maxHp; }
    public void setMaxHp(long maxHp) { this.maxHp = maxHp; }
    public long getAttack() { return attack; }
    public void setAttack(long attack) { this.attack = attack; }
    public long getDefense() { return defense; }
    public void setDefense(long defense) { this.defense = defense; }
    public double getCriticalChance() { return criticalChance; }
    public void setCriticalChance(double cc) { this.criticalChance = cc; }
    public double getCriticalDamage() { return criticalDamage; }
    public void setCriticalDamage(double cd) { this.criticalDamage = cd; }
    public double getSkillDamage() { return skillDamage; }
    public void setSkillDamage(double sd) { this.skillDamage = sd; }
    public double getUltimateDamage() { return ultimateDamage; }
    public void setUltimateDamage(double ud) { this.ultimateDamage = ud; }
    public long getSpeed() { return speed; }
    public void setSpeed(long speed) { this.speed = speed; }
    public long getExp() { return exp; }
    public long getExpToNext() { return expToNext; }
    public String getEquippedWeaponId() { return equippedWeaponId; }
    public void setEquippedWeaponId(String wid) { this.equippedWeaponId = wid; }
    public String getEquippedArmorId() { return equippedArmorId; }
    public void setEquippedArmorId(String aid) { this.equippedArmorId = aid; }
    public int getDuplicates() { return duplicates; }
    public void setDuplicates(int d) { this.duplicates = d; }
    public int getShards() { return shards; }
    public void setShards(int s) { this.shards = s; }
    public int getAwakeningLevel() { return awakeningLevel; }
    public void setAwakeningLevel(int al) { this.awakeningLevel = al; }
    public int getEvolutionLevel() { return evolutionLevel; }
    public void setEvolutionLevel(int el) { this.evolutionLevel = el; }
    public int getSkillLevel() { return skillLevel; }
    public void setSkillLevel(int sl) { this.skillLevel = sl; }
    public void addDuplicate() {
        this.duplicates++;
        this.shards += rarity.equals("SSR") ? 50 : rarity.equals("SR") ? 25 : 10;
    }
}
