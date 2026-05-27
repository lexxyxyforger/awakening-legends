package com.feyydev.managers;

import com.feyydev.models.*;
import java.util.*;

public class BattleManager {
    public enum BattleAction { ATTACK, SKILL, ULTIMATE, POTION, ENEMY_ATTACK }
    public enum BattleResult { PLAYER_WIN, ENEMY_WIN, ONGOING }

    private static BattleManager instance;
    private final List<GameCharacter> team;
    private List<Enemy> enemies;
    private boolean playerTurn;
    private int turnCount;
    private final Random random;
    private final List<String> battleLog;
    private long lastDamageDealt;
    private boolean lastWasCritical;
    private int currentTargetIndex;
    private boolean isRaidBattle;
    private boolean isPvPBattle;

    private BattleManager() {
        random = new Random();
        battleLog = new ArrayList<>();
        team = new ArrayList<>();
        enemies = new ArrayList<>();
    }

    public static BattleManager getInstance() {
        if (instance == null) instance = new BattleManager();
        return instance;
    }

    public void startBattle(List<GameCharacter> team, List<Enemy> enemies) {
        this.team.clear();
        this.team.addAll(new ArrayList<>(team));
        this.enemies = new ArrayList<>(enemies);
        this.playerTurn = true;
        this.turnCount = 0;
        this.battleLog.clear();
        this.lastDamageDealt = 0;
        this.lastWasCritical = false;
        this.currentTargetIndex = 0;
        this.isRaidBattle = false;
        this.isPvPBattle = false;
        for (GameCharacter c : team) c.healFull();
        for (Enemy e : enemies) e.healFull();
        battleLog.add("Battle started! " + team.size() + " vs " + enemies.size());
    }

    public void startRaidBattle(List<GameCharacter> team, Enemy boss) {
        startBattle(team, Arrays.asList(boss));
        this.isRaidBattle = true;
    }

    public BattleResult executeAction(GameCharacter character, BattleAction action) {
        if (!playerTurn || enemies.isEmpty() || currentTargetIndex >= enemies.size()) return BattleResult.ONGOING;
        Enemy target = enemies.get(currentTargetIndex);
        if (!target.isAlive()) {
            nextTarget();
            return BattleResult.ONGOING;
        }

        switch (action) {
            case ATTACK -> characterAttack(character, target);
            case SKILL -> characterSkill(character, target);
            case ULTIMATE -> characterUltimate(character, target);
            case POTION -> characterPotion(character);
        }

        if (!target.isAlive()) {
            battleLog.add(target.getName() + " defeated!");
            nextTarget();
            if (enemies.stream().noneMatch(Enemy::isAlive)) {
                battleLog.add("Victory! All enemies defeated!");
                return BattleResult.PLAYER_WIN;
            }
        }

        playerTurn = false;
        return BattleResult.ONGOING;
    }

    private void nextTarget() {
        currentTargetIndex++;
        while (currentTargetIndex < enemies.size() && !enemies.get(currentTargetIndex).isAlive()) {
            currentTargetIndex++;
        }
    }

    public BattleResult executeEnemyTurn() {
        if (playerTurn || team.isEmpty()) return BattleResult.ONGOING;
        for (Enemy enemy : enemies) {
            if (!enemy.isAlive()) continue;
            List<GameCharacter> alive = new ArrayList<>();
            for (GameCharacter c : team) {
                if (c.getHp() > 0) alive.add(c);
            }
            if (alive.isEmpty()) continue;
            GameCharacter target = alive.get(random.nextInt(alive.size()));
            enemyAttack(enemy, target);
        }
        turnCount++;
        playerTurn = true;
        if (team.stream().allMatch(c -> c.getHp() <= 0)) {
            battleLog.add("All characters defeated!");
            return BattleResult.ENEMY_WIN;
        }
        return BattleResult.ONGOING;
    }

    private void characterAttack(GameCharacter c, Enemy target) {
        long baseDamage = c.getAttack() - target.getDefense() / 2;
        if (baseDamage <= 0) baseDamage = 1;
        lastWasCritical = random.nextDouble() < c.getCriticalChance();
        long damage = lastWasCritical ? (long)(baseDamage * c.getCriticalDamage()) : baseDamage;
        damage += random.nextInt((int)(damage * 0.2) + 1) - (long)(damage * 0.1);
        lastDamageDealt = Math.max(1, damage);
        target.takeDamage(lastDamageDealt);
        String log = c.getName() + " attacks " + target.getName() + " for " + lastDamageDealt;
        if (lastWasCritical) log += " (CRITICAL!)";
        battleLog.add(log);
    }

    private void characterSkill(GameCharacter c, Enemy target) {
        long baseDamage = (long)((c.getAttack() - target.getDefense() / 3) * c.getSkillDamage());
        if (baseDamage <= 0) baseDamage = 10;
        lastWasCritical = random.nextDouble() < c.getCriticalChance();
        long damage = lastWasCritical ? (long)(baseDamage * c.getCriticalDamage()) : baseDamage;
        damage += random.nextInt((int)(damage * 0.15) + 1);
        lastDamageDealt = Math.max(1, damage);
        target.takeDamage(lastDamageDealt);
        if (random.nextDouble() < 0.2) {
            target.addStatusEffect(new StatusEffect("stun_" + System.currentTimeMillis(), "Stun", 1, 0, "STUN"));
            battleLog.add(c.getName() + " stuns " + target.getName() + "!");
        }
        String log = c.getName() + " uses SKILL on " + target.getName() + " for " + lastDamageDealt;
        if (lastWasCritical) log += " (CRITICAL!)";
        battleLog.add(log);
    }

    private void characterUltimate(GameCharacter c, Enemy target) {
        long baseDamage = (long)((c.getAttack() - target.getDefense() / 4) * c.getUltimateDamage());
        if (baseDamage <= 0) baseDamage = 50;
        lastWasCritical = random.nextDouble() < c.getCriticalChance();
        long damage = lastWasCritical ? (long)(baseDamage * c.getCriticalDamage()) : baseDamage;
        damage += random.nextInt((int)(damage * 0.1) + 1);
        lastDamageDealt = Math.max(1, damage);
        target.takeDamage(lastDamageDealt);
        String log = c.getName() + " uses ULTIMATE on " + target.getName() + " for " + lastDamageDealt;
        if (lastWasCritical) log += " (CRITICAL!)";
        battleLog.add(log);
    }

    private void characterPotion(GameCharacter c) {
        InventoryManager inv = InventoryManager.getInstance();
        Item potion = null;
        for (Item item : inv.getItems()) {
            if (item.getType().equals("Consumable") && item.getQuantity() > 0 && item.getHealAmount() > 0) {
                potion = item;
                break;
            }
        }
        if (potion != null) {
            long heal = potion.getHealAmount();
            c.setHp(c.getHp() + heal);
            potion.addQuantity(-1);
            battleLog.add(c.getName() + " uses " + potion.getName() + " recovers " + heal + " HP");
        } else {
            battleLog.add("No potions available!");
        }
    }

    private void enemyAttack(Enemy enemy, GameCharacter target) {
        long baseDamage = enemy.getAttack() - target.getDefense() / 2;
        if (baseDamage <= 0) baseDamage = 1;
        boolean enemyCrit = random.nextDouble() < enemy.getCriticalChance();
        long damage = enemyCrit ? (long)(baseDamage * enemy.getCriticalDamage()) : baseDamage;
        damage += random.nextInt((int)(damage * 0.2) + 1) - (long)(damage * 0.1);
        damage = Math.max(1, damage);
        target.setHp(target.getHp() - damage);
        String log = enemy.getName() + " attacks " + target.getName() + " for " + damage;
        if (enemyCrit) log += " (CRITICAL!)";
        battleLog.add(log);
    }

    public long getTotalRewardGold() {
        return enemies.stream().mapToLong(Enemy::getGoldReward).sum();
    }

    public long getTotalRewardExp() {
        return enemies.stream().mapToLong(Enemy::getExpReward).sum();
    }

    public List<GameCharacter> getTeam() { return Collections.unmodifiableList(team); }
    public List<Enemy> getEnemies() { return enemies != null ? Collections.unmodifiableList(enemies) : List.of(); }
    public Enemy getCurrentEnemy() {
        if (currentTargetIndex < enemies.size()) return enemies.get(currentTargetIndex);
        return null;
    }
    public boolean isPlayerTurn() { return playerTurn; }
    public List<String> getBattleLog() { return Collections.unmodifiableList(battleLog); }
    public long getLastDamageDealt() { return lastDamageDealt; }
    public boolean isLastWasCritical() { return lastWasCritical; }
    public int getTurnCount() { return turnCount; }
    public boolean isRaidBattle() { return isRaidBattle; }
}
