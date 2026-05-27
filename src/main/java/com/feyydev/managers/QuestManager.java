package com.feyydev.managers;

import com.feyydev.models.*;
import com.feyydev.utils.Constants;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class QuestManager {
    private static QuestManager instance;
    private Player player;
    private List<Quest> dailyQuests;
    private List<Quest> weeklyQuests;
    private List<Quest> achievements;

    private QuestManager() {}

    public static QuestManager getInstance() {
        if (instance == null) instance = new QuestManager();
        return instance;
    }

    public void setPlayer(Player player) {
        this.player = player;
        initQuests();
    }

    private void initQuests() {
        dailyQuests = new ArrayList<>(Constants.createDailyQuests());
        weeklyQuests = new ArrayList<>(Constants.createWeeklyQuests());
        achievements = new ArrayList<>(Constants.createAchievementQuests());
    }

    public List<Quest> getDailyQuests() { return dailyQuests; }
    public List<Quest> getWeeklyQuests() { return weeklyQuests; }
    public List<Quest> getAchievements() { return achievements; }

    public List<Quest> getActiveQuests() {
        List<Quest> all = new ArrayList<>();
        all.addAll(dailyQuests);
        all.addAll(weeklyQuests);
        all.addAll(achievements);
        return all;
    }

    public void updateProgress(String requirement, int amount) {
        for (Quest q : dailyQuests) {
            if (q.getRequirement().equals(requirement) && !q.isCompleted()) {
                q.addProgress(amount);
            }
        }
        for (Quest q : weeklyQuests) {
            if (q.getRequirement().equals(requirement) && !q.isCompleted()) {
                q.addProgress(amount);
            }
        }
        for (Quest q : achievements) {
            if (q.getRequirement().equals(requirement) && !q.isCompleted()) {
                q.addProgress(amount);
            }
        }
        checkAchievements();
    }

    private void checkAchievements() {
        if (player.getCurrentStage() >= 1 && !isAchievementClaimed("achieve_first_clear")) {
            findAchievement("achieve_first_clear").ifPresent(q -> q.setCompleted(true));
        }
        long kills = dailyQuests.stream().filter(q -> q.getRequirement().equals("KILL")).mapToInt(Quest::getProgress).sum();
        if (kills >= 50 && !isAchievementClaimed("achieve_50_kills")) {
            findAchievement("achieve_50_kills").ifPresent(q -> q.setCompleted(true));
        }
        if (kills >= 100 && !isAchievementClaimed("achieve_100_kills")) {
            findAchievement("achieve_100_kills").ifPresent(q -> q.setCompleted(true));
        }
        if (player.getLevel() >= 50 && !isAchievementClaimed("achieve_level_50")) {
            findAchievement("achieve_level_50").ifPresent(q -> q.setCompleted(true));
        }
    }

    private boolean isAchievementClaimed(String id) {
        return player.getCompletedAchievements().contains(id);
    }

    private java.util.Optional<Quest> findAchievement(String id) {
        return achievements.stream().filter(q -> q.getId().equals(id)).findFirst();
    }

    public boolean claimReward(String questId) {
        for (Quest q : dailyQuests) {
            if (q.getId().equals(questId) && q.isReadyToClaim()) {
                player.addGold(q.getRewardGold());
                player.addGems(q.getRewardGems());
                player.addExp(q.getRewardExp());
                q.setClaimed(true);
                return true;
            }
        }
        for (Quest q : weeklyQuests) {
            if (q.getId().equals(questId) && q.isReadyToClaim()) {
                player.addGold(q.getRewardGold());
                player.addGems(q.getRewardGems());
                player.addExp(q.getRewardExp());
                q.setClaimed(true);
                return true;
            }
        }
        for (Quest q : achievements) {
            if (q.getId().equals(questId) && q.isReadyToClaim()) {
                player.addGold(q.getRewardGold());
                player.addGems(q.getRewardGems());
                player.addExp(q.getRewardExp());
                q.setClaimed(true);
                player.getCompletedAchievements().add(questId);
                return true;
            }
        }
        return false;
    }

    public void resetDailyQuests() {
        dailyQuests = new ArrayList<>(Constants.createDailyQuests());
    }

    public boolean allDailyClaimed() {
        return dailyQuests.stream().allMatch(Quest::isClaimed);
    }

    public int getUnclaimedCount() {
        return (int) getActiveQuests().stream().filter(Quest::isReadyToClaim).count();
    }
}
