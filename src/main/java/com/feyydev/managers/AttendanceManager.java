package com.feyydev.managers;

import com.feyydev.models.AttendanceReward;
import com.feyydev.models.GameCharacter;
import com.feyydev.models.Player;
import com.feyydev.utils.Constants;
import java.util.List;

public class AttendanceManager {
    private static AttendanceManager instance;
    private List<AttendanceReward> rewards;
    private Player player;

    private AttendanceManager() {
        rewards = Constants.createAttendanceRewards();
    }

    public static AttendanceManager getInstance() {
        if (instance == null) instance = new AttendanceManager();
        return instance;
    }

    public void setPlayer(Player player) { this.player = player; }

    public List<AttendanceReward> getRewards() { return rewards; }

    public AttendanceReward getCurrentReward() {
        int day = Math.min(player.getAttendanceDay(), rewards.size() - 1);
        return rewards.get(Math.max(0, day));
    }

    public boolean canClaim() {
        return player.getAttendanceDay() < rewards.size();
    }

    public boolean claimReward() {
        if (!canClaim()) return false;
        AttendanceReward reward = rewards.get(player.getAttendanceDay());
        switch (reward.getRewardType()) {
            case "GOLD" -> player.addGold(reward.getRewardAmount());
            case "GEMS" -> player.addGems(reward.getRewardAmount());
            case "ENERGY" -> player.setEnergy((int) Math.min(player.getMaxEnergy(), player.getEnergy() + reward.getRewardAmount()));
            case "ITEM" -> {
                for (var item : Constants.createDefaultPotions()) {
                    if (item.getName().equals(reward.getRewardName())) {
                        item.setQuantity((int) reward.getRewardAmount());
                        InventoryManager.getInstance().addItem(item);
                        break;
                    }
                }
            }
            case "TICKET" -> {
                var ticket = new com.feyydev.models.Item("summon_ticket", "Summon Ticket", "Material",
                    "Used for summoning", "Rare", 0);
                ticket.setQuantity((int) reward.getRewardAmount());
                InventoryManager.getInstance().addItem(ticket);
            }
            case "CHARACTER" -> {
                if (reward.getRarity().equals("SSR")) {
                    var ssrs = Constants.createDefaultCharacters().stream()
                        .filter(c -> c.getRarity().equals("SSR")).toList();
                    if (!ssrs.isEmpty()) {
                        GameCharacter freeChar = ssrs.get((int)(System.currentTimeMillis() % ssrs.size()));
                        GameCharacter newChar = new GameCharacter(freeChar.getId() + "_att", freeChar.getName(), "SSR", freeChar.getCategory());
                        CharacterManager.getInstance().addCharacter(newChar);
                    }
                } else {
                    var srs = Constants.createDefaultCharacters().stream()
                        .filter(c -> c.getRarity().equals("SR")).toList();
                    if (!srs.isEmpty()) {
                        GameCharacter freeChar = srs.get((int)(System.currentTimeMillis() % srs.size()));
                        GameCharacter newChar = new GameCharacter(freeChar.getId() + "_att", freeChar.getName(), "SR", freeChar.getCategory());
                        CharacterManager.getInstance().addCharacter(newChar);
                    }
                }
            }
        }
        player.setAttendanceDay(player.getAttendanceDay() + 1);
        return true;
    }

    public int getCurrentDay() { return Math.min(player.getAttendanceDay() + 1, rewards.size()); }
    public int getTotalDays() { return rewards.size(); }
}
