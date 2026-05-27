package com.feyydev.models;

public class Mail {
    private String id;
    private String senderName;
    private String senderAvatar;
    private String title;
    private String message;
    private long rewardGold;
    private long rewardGems;
    private long rewardExp;
    private int rewardSummonTickets;
    private int rewardShards;
    private String rewardItemId;
    private boolean claimed;
    private long expirationTime;
    private long sentTime;

    public Mail() {}

    public Mail(String id, String senderName, String senderAvatar, String title, String message,
                long rewardGold, long rewardGems, long rewardExp, int rewardSummonTickets,
                int rewardShards, String rewardItemId) {
        this.id = id;
        this.senderName = senderName;
        this.senderAvatar = senderAvatar;
        this.title = title;
        this.message = message;
        this.rewardGold = rewardGold;
        this.rewardGems = rewardGems;
        this.rewardExp = rewardExp;
        this.rewardSummonTickets = rewardSummonTickets;
        this.rewardShards = rewardShards;
        this.rewardItemId = rewardItemId;
        this.claimed = false;
        this.sentTime = System.currentTimeMillis();
        this.expirationTime = sentTime + 7L * 24 * 60 * 60 * 1000;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expirationTime;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getSenderName() { return senderName; }
    public void setSenderName(String n) { this.senderName = n; }
    public String getSenderAvatar() { return senderAvatar; }
    public void setSenderAvatar(String a) { this.senderAvatar = a; }
    public String getTitle() { return title; }
    public void setTitle(String t) { this.title = t; }
    public String getMessage() { return message; }
    public void setMessage(String m) { this.message = m; }
    public long getRewardGold() { return rewardGold; }
    public void setRewardGold(long g) { this.rewardGold = g; }
    public long getRewardGems() { return rewardGems; }
    public void setRewardGems(long g) { this.rewardGems = g; }
    public long getRewardExp() { return rewardExp; }
    public void setRewardExp(long e) { this.rewardExp = e; }
    public int getRewardSummonTickets() { return rewardSummonTickets; }
    public void setRewardSummonTickets(int t) { this.rewardSummonTickets = t; }
    public int getRewardShards() { return rewardShards; }
    public void setRewardShards(int s) { this.rewardShards = s; }
    public String getRewardItemId() { return rewardItemId; }
    public void setRewardItemId(String i) { this.rewardItemId = i; }
    public boolean isClaimed() { return claimed; }
    public void setClaimed(boolean c) { this.claimed = c; }
    public long getExpirationTime() { return expirationTime; }
    public void setExpirationTime(long e) { this.expirationTime = e; }
    public long getSentTime() { return sentTime; }
    public void setSentTime(long s) { this.sentTime = s; }
}
