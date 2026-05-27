package com.feyydev.models;

public class AttendanceReward {
    private int day;
    private String rewardType;
    private long rewardAmount;
    private String rewardName;
    private String rarity;
    private boolean special;

    public AttendanceReward() {}

    public AttendanceReward(int day, String rewardType, long rewardAmount, String rewardName, String rarity, boolean special) {
        this.day = day;
        this.rewardType = rewardType;
        this.rewardAmount = rewardAmount;
        this.rewardName = rewardName;
        this.rarity = rarity;
        this.special = special;
    }

    public int getDay() { return day; }
    public String getRewardType() { return rewardType; }
    public long getRewardAmount() { return rewardAmount; }
    public String getRewardName() { return rewardName; }
    public String getRarity() { return rarity; }
    public boolean isSpecial() { return special; }
}
