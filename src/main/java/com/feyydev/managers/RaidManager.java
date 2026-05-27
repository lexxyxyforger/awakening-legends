package com.feyydev.managers;

import com.feyydev.models.*;
import com.feyydev.utils.Constants;
import java.util.*;

public class RaidManager {
    private static RaidManager instance;
    private RaidBoss currentBoss;
    public long raidStartTime;
    private boolean raidActive;
    private List<RaidBoss.RaidContribution> contributions;
    private Random random;
    private Player player;

    private RaidManager() {
        random = new Random();
        contributions = new ArrayList<>();
    }

    public static RaidManager getInstance() {
        if (instance == null) instance = new RaidManager();
        return instance;
    }

    public void setPlayer(Player player) { this.player = player; }

    public void startRaid(int chapter) {
        String[] bossNames = {"Titan Guardian", "Shadow Lord", "Demon King", "Celestial Dragon", "Supreme Overlord"};
        String bossName = bossNames[Math.min(chapter - 1, bossNames.length - 1)];
        int level = chapter * 20;
        currentBoss = new RaidBoss("raid_" + chapter, bossName, "Raid Boss", level);
        raidStartTime = System.currentTimeMillis();
        raidActive = true;
        contributions.clear();
    }

    public long executeAttack(List<GameCharacter> team) {
        if (!raidActive || currentBoss == null) return 0;
        long totalDamage = 0;
        for (GameCharacter c : team) {
            if (c.getHp() <= 0) continue;
            long damage = c.getAttack() - currentBoss.getDefense() / 3;
            if (damage <= 0) damage = 10;
            if (random.nextDouble() < c.getCriticalChance()) {
                damage = (long)(damage * c.getCriticalDamage());
            }
            damage += random.nextInt((int)(damage * 0.2) + 1);
            totalDamage += Math.max(1, damage);
        }
        currentBoss.takeDamage(totalDamage);
        currentBoss.addContribution(player.getName(), totalDamage);
        player.setTotalBossDamage(player.getTotalBossDamage() + totalDamage);
        if (!currentBoss.isAlive()) {
            raidActive = false;
        }
        return totalDamage;
    }

    public boolean isTimeUp() {
        return System.currentTimeMillis() - raidStartTime > Constants.RAID_TIME_LIMIT * 1000L;
    }

    public boolean isRaidActive() { return raidActive && currentBoss != null && currentBoss.isAlive() && !isTimeUp(); }
    public RaidBoss getCurrentBoss() { return currentBoss; }
    public void endRaid() { raidActive = false; }

    public List<RewardSummary> getRaidRewards() {
        List<RewardSummary> rewards = new ArrayList<>();
        long totalDamage = player.getTotalBossDamage();
        rewards.add(new RewardSummary("\uD83D\uDCB0 Gold", (long)(totalDamage * 0.1)));
        rewards.add(new RewardSummary("\u2B50 EXP", (long)(totalDamage * 0.05)));
        rewards.add(new RewardSummary("\uD83D\uDC8E Gems", (long)(Math.min(totalDamage * 0.01, 500))));
        return rewards;
    }

    public List<RaidBoss.RaidContribution> getContributions() {
        if (currentBoss != null) return currentBoss.getContributions();
        return contributions;
    }

    public static class RewardSummary {
        private String name;
        private long amount;
        public RewardSummary(String name, long amount) { this.name = name; this.amount = amount; }
        public String getName() { return name; }
        public long getAmount() { return amount; }
    }
}
