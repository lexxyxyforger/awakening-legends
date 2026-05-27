package com.feyydev.models;

import java.util.List;

public class SummonBanner {
    private String id;
    private String name;
    private String description;
    private String type;
    private boolean active;
    private List<String> rateUpCharacterIds;
    private double rateUpBonus;
    private int costGems;
    private int costTickets;
    private int multiPullCount;

    public SummonBanner() {}

    public SummonBanner(String id, String name, String description, String type,
                        List<String> rateUpCharacterIds, double rateUpBonus,
                        int costGems, int costTickets) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.active = true;
        this.rateUpCharacterIds = rateUpCharacterIds;
        this.rateUpBonus = rateUpBonus;
        this.costGems = costGems;
        this.costTickets = costTickets;
        this.multiPullCount = 10;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getType() { return type; }
    public boolean isActive() { return active; }
    public void setActive(boolean a) { this.active = a; }
    public List<String> getRateUpCharacterIds() { return rateUpCharacterIds; }
    public double getRateUpBonus() { return rateUpBonus; }
    public int getCostGems() { return costGems; }
    public int getCostTickets() { return costTickets; }
    public int getMultiPullCount() { return multiPullCount; }
}
