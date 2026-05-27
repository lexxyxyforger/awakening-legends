package com.feyydev.managers;

import com.feyydev.models.Player;
import com.feyydev.utils.Constants;
import com.feyydev.utils.Constants.EventMission;
import java.util.List;

public class EventManager {
    private static EventManager instance;
    private Player player;
    private List<EventMission> missions;

    private EventManager() {
        missions = Constants.createEventMissions();
    }

    public static EventManager getInstance() {
        if (instance == null) instance = new EventManager();
        return instance;
    }

    public void setPlayer(Player player) { this.player = player; }

    public List<EventMission> getMissionsByCategory(String category) {
        return Constants.getEventMissionsByCategory(category);
    }

    public int getProgress(String requirement) {
        return player.getEventProgress().getOrDefault(requirement, 0);
    }

    public void updateProgress(String requirement, int amount) {
        var prog = player.getEventProgress();
        prog.put(requirement, prog.getOrDefault(requirement, 0) + amount);
    }

    public boolean isMissionClaimed(String missionId) {
        return player.getClaimedEventMissions().contains(missionId);
    }

    public boolean isMissionCompletable(EventMission mission) {
        if (isMissionClaimed(mission.id())) return false;
        return getProgress(mission.requirement()) >= mission.targetCount();
    }

    public boolean claimMission(String missionId) {
        EventMission mission = missions.stream()
            .filter(m -> m.id().equals(missionId))
            .findFirst().orElse(null);
        if (mission == null) return false;
        if (!isMissionCompletable(mission)) return false;

        player.addGold(mission.rewardGold());
        player.addGems(mission.rewardGems());
        player.addExp(mission.rewardExp());

        if (mission.rewardTickets() > 0) {
            var ticket = new com.feyydev.models.Item("summon_ticket", "Summon Ticket", "Material",
                "Used for summoning", "Rare", 0);
            ticket.setQuantity(mission.rewardTickets());
            InventoryManager.getInstance().addItem(ticket);
        }

        player.getClaimedEventMissions().add(missionId);
        return true;
    }

    public int getCompletedCount(String category) {
        return (int) getMissionsByCategory(category).stream()
            .filter(m -> isMissionClaimed(m.id()))
            .count();
    }

    public int getTotalCount(String category) {
        return getMissionsByCategory(category).size();
    }

    public double getCategoryProgress(String category) {
        int total = getTotalCount(category);
        if (total == 0) return 0;
        return (double) getCompletedCount(category) / total;
    }
}
