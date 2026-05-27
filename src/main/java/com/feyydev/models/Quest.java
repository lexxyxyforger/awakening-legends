package com.feyydev.models;

public class Quest {
    private String id;
    private String name;
    private String description;
    private String type;
    private String requirement;
    private int targetCount;
    private int progress;
    private boolean completed;
    private boolean claimed;
    private long rewardGold;
    private long rewardGems;
    private long rewardExp;
    private int rewardTickets;
    private String rewardItemId;
    private String category;

    public Quest() {}

    public Quest(String id, String name, String description, String type,
                 String requirement, int targetCount,
                 long rewardGold, long rewardGems, long rewardExp) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.requirement = requirement;
        this.targetCount = targetCount;
        this.progress = 0;
        this.completed = false;
        this.claimed = false;
        this.rewardGold = rewardGold;
        this.rewardGems = rewardGems;
        this.rewardExp = rewardExp;
        this.rewardTickets = 0;
        this.rewardItemId = null;
        this.category = "Daily";
    }

    public void addProgress(int amount) {
        progress = Math.min(targetCount, progress + amount);
        if (progress >= targetCount) completed = true;
    }

    public boolean isReadyToClaim() { return completed && !claimed; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getType() { return type; }
    public String getRequirement() { return requirement; }
    public int getTargetCount() { return targetCount; }
    public int getProgress() { return progress; }
    public void setProgress(int p) { this.progress = p; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean c) { this.completed = c; }
    public boolean isClaimed() { return claimed; }
    public void setClaimed(boolean c) { this.claimed = c; }
    public long getRewardGold() { return rewardGold; }
    public long getRewardGems() { return rewardGems; }
    public long getRewardExp() { return rewardExp; }
    public int getRewardTickets() { return rewardTickets; }
    public void setRewardTickets(int t) { this.rewardTickets = t; }
    public String getRewardItemId() { return rewardItemId; }
    public void setRewardItemId(String i) { this.rewardItemId = i; }
    public String getCategory() { return category; }
    public void setCategory(String c) { this.category = c; }
}
