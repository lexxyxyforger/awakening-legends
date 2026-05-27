package com.feyydev.models;

public class Weapon {
    private String id;
    private String name;
    private String type;
    private String rarity;
    private long attack;
    private double criticalChanceBonus;
    private double hpBonus;
    private int level;
    private int enhancementLevel;
    private int refinementLevel;
    private int quantity;

    public Weapon() {}

    public Weapon(String id, String name, String type, String rarity, long attack) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.rarity = rarity;
        this.attack = attack;
        this.level = 1;
        this.enhancementLevel = 0;
        this.refinementLevel = 0;
        this.quantity = 1;
        this.criticalChanceBonus = 0;
        this.hpBonus = 0;
    }

    public long getEnhancedAttack() {
        return attack + (enhancementLevel * attack / 10) + (refinementLevel * attack / 5);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getRarity() { return rarity; }
    public long getAttack() { return attack; }
    public void setAttack(long a) { this.attack = a; }
    public double getCriticalChanceBonus() { return criticalChanceBonus; }
    public void setCriticalChanceBonus(double c) { this.criticalChanceBonus = c; }
    public double getHpBonus() { return hpBonus; }
    public void setHpBonus(double h) { this.hpBonus = h; }
    public int getLevel() { return level; }
    public void setLevel(int l) { this.level = l; }
    public int getEnhancementLevel() { return enhancementLevel; }
    public void setEnhancementLevel(int e) { this.enhancementLevel = e; }
    public int getRefinementLevel() { return refinementLevel; }
    public void setRefinementLevel(int r) { this.refinementLevel = r; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int q) { this.quantity = q; }
}
