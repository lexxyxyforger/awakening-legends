package com.feyydev.models;

import java.util.ArrayList;
import java.util.List;

public class Enemy {
    private String id;
    private String name;
    private String type;
    private int level;
    private long hp;
    private long maxHp;
    private long attack;
    private long defense;
    private double criticalChance;
    private double criticalDamage;
    private long goldReward;
    private long expReward;
    private boolean isBoss;
    private boolean isElite;
    private List<StatusEffect> statusEffects;

    public Enemy() {
        statusEffects = new ArrayList<>();
    }

    public Enemy(String id, String name, String type, int level, boolean isBoss) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.level = level;
        this.isBoss = isBoss;
        this.isElite = false;
        this.statusEffects = new ArrayList<>();
        double multiplier = isBoss ? 3.0 : 1.0;
        maxHp = (long)((500 + level * 80) * multiplier);
        hp = maxHp;
        attack = (long)((30 + level * 8) * multiplier);
        defense = (long)((20 + level * 5) * multiplier);
        criticalChance = 0.05;
        criticalDamage = 1.5;
        goldReward = isBoss ? (50 + level * 20) : (10 + level * 5);
        expReward = isBoss ? (80 + level * 30) : (20 + level * 8);
    }

    public void takeDamage(long damage) { hp = Math.max(0, hp - damage); }
    public boolean isAlive() { return hp > 0; }
    public void healFull() { hp = maxHp; }

    public void addStatusEffect(StatusEffect effect) {
        statusEffects.add(effect);
    }

    public void tickStatusEffects() {
        List<StatusEffect> expired = new ArrayList<>();
        for (StatusEffect se : statusEffects) {
            if (se.tick()) expired.add(se);
        }
        statusEffects.removeAll(expired);
    }

    public boolean hasStatus(String type) {
        return statusEffects.stream().anyMatch(s -> s.getType().equals(type) && s.getRemainingTurns() > 0);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String n) { this.name = n; }
    public String getType() { return type; }
    public int getLevel() { return level; }
    public long getHp() { return hp; }
    public void setHp(long hp) { this.hp = hp; }
    public long getMaxHp() { return maxHp; }
    public long getAttack() { return attack; }
    public long getDefense() { return defense; }
    public double getCriticalChance() { return criticalChance; }
    public double getCriticalDamage() { return criticalDamage; }
    public long getGoldReward() { return goldReward; }
    public long getExpReward() { return expReward; }
    public boolean isBoss() { return isBoss; }
    public boolean isElite() { return isElite; }
    public void setElite(boolean e) { this.isElite = e; }
    public List<StatusEffect> getStatusEffects() { return statusEffects; }
}
