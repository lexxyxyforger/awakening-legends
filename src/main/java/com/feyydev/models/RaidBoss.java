package com.feyydev.models;

import java.util.ArrayList;
import java.util.List;

public class RaidBoss {
    private String id;
    private String name;
    private String type;
    private int level;
    private long maxHp;
    private long hp;
    private long attack;
    private long defense;
    private double criticalChance;
    private double criticalDamage;
    private int timeLimit;
    private List<RaidContribution> contributions;

    public RaidBoss() {
        contributions = new ArrayList<>();
    }

    public RaidBoss(String id, String name, String type, int level) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.level = level;
        this.maxHp = 500000 + (long)(level * 50000);
        this.hp = maxHp;
        this.attack = 500 + (long)(level * 50);
        this.defense = 200 + (long)(level * 20);
        this.criticalChance = 0.1;
        this.criticalDamage = 1.8;
        this.timeLimit = 180;
        this.contributions = new ArrayList<>();
    }

    public void takeDamage(long damage) { hp = Math.max(0, hp - damage); }
    public boolean isAlive() { return hp > 0; }
    public double getHpPercent() { return (double) hp / maxHp; }

    public void addContribution(String playerName, long damage) {
        contributions.add(new RaidContribution(playerName, damage));
        contributions.sort((a, b) -> Long.compare(b.getDamage(), a.getDamage()));
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String n) { this.name = n; }
    public String getType() { return type; }
    public int getLevel() { return level; }
    public long getMaxHp() { return maxHp; }
    public long getHp() { return hp; }
    public void setHp(long hp) { this.hp = hp; }
    public long getAttack() { return attack; }
    public long getDefense() { return defense; }
    public double getCriticalChance() { return criticalChance; }
    public double getCriticalDamage() { return criticalDamage; }
    public int getTimeLimit() { return timeLimit; }
    public List<RaidContribution> getContributions() { return contributions; }

    public static class RaidContribution {
        private String playerName;
        private long damage;

        public RaidContribution() {}
        public RaidContribution(String playerName, long damage) {
            this.playerName = playerName;
            this.damage = damage;
        }
        public String getPlayerName() { return playerName; }
        public long getDamage() { return damage; }
    }
}
