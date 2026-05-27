package com.feyydev.models;

public class StatusEffect {
    private String id;
    private String name;
    private int duration;
    private int remainingTurns;
    private double value;
    private String type;

    public StatusEffect() {}

    public StatusEffect(String id, String name, int duration, double value, String type) {
        this.id = id;
        this.name = name;
        this.duration = duration;
        this.remainingTurns = duration;
        this.value = value;
        this.type = type;
    }

    public boolean tick() {
        remainingTurns--;
        return remainingTurns <= 0;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getDuration() { return duration; }
    public int getRemainingTurns() { return remainingTurns; }
    public double getValue() { return value; }
    public String getType() { return type; }
}
