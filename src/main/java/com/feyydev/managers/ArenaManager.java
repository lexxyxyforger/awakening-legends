package com.feyydev.managers;

import com.feyydev.models.*;
import com.feyydev.utils.Constants;
import java.util.*;

public class ArenaManager {
    private static ArenaManager instance;
    private Random random;
    private Player player;

    private ArenaManager() {
        random = new Random();
    }

    public static ArenaManager getInstance() {
        if (instance == null) instance = new ArenaManager();
        return instance;
    }

    public void setPlayer(Player player) { this.player = player; }

    public List<ArenaOpponent> generateOpponents() {
        List<ArenaOpponent> opponents = new ArrayList<>();
        String[] names = {"ShadowFist", "DragonSlayer99", "AnimeKing", "TournamentPro", "DarkMage"};
        int basePower = (int) player.getTotalPower();
        for (int i = 0; i < 3; i++) {
            int power = basePower + random.nextInt(200) - 100;
            opponents.add(new ArenaOpponent(
                names[i],
                power,
                1000 + random.nextInt(500),
                "\uD83D\uDC64"
            ));
        }
        return opponents;
    }

    public BattleManager.BattleResult fight(ArenaOpponent opponent) {
        if (!player.spendArenaTokens(1)) return null;
        BattleManager bm = BattleManager.getInstance();
        GameCharacter chara = player.getEquippedCharacter();
        if (chara == null) return null;
        Enemy dummy = new Enemy("arena_opp", opponent.getName(), "Arena", opponent.getPower() / 10, false);
        bm.startBattle(Arrays.asList(chara), Arrays.asList(dummy));
        BattleManager.BattleResult result = BattleManager.BattleResult.ONGOING;
        while (result == BattleManager.BattleResult.ONGOING) {
            result = bm.executeAction(chara, BattleManager.BattleAction.ATTACK);
            if (result == BattleManager.BattleResult.ONGOING) {
                result = bm.executeEnemyTurn();
            }
        }
        if (result == BattleManager.BattleResult.PLAYER_WIN) {
            player.setPvpWins(player.getPvpWins() + 1);
            player.setPvpScore(player.getPvpScore() + 30);
            player.addGold(500);
        } else {
            player.setPvpLosses(player.getPvpLosses() + 1);
            player.setPvpScore(Math.max(0, player.getPvpScore() - 15));
        }
        return result;
    }

    public static class ArenaOpponent {
        private String name;
        private int power;
        private int score;
        private String avatar;
        public ArenaOpponent(String name, int power, int score, String avatar) {
            this.name = name; this.power = power; this.score = score; this.avatar = avatar;
        }
        public String getName() { return name; }
        public int getPower() { return power; }
        public int getScore() { return score; }
        public String getAvatar() { return avatar; }
    }
}
