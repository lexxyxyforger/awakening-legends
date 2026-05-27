package com.feyydev.models;

public class Armor {
    private String id;
    private String name;
    private String type;
    private String rarity;
    private long defense;
    private double hpBonus;
    private int level;
    private int enhancementLevel;
    private int refinementLevel;
    private int quantity;

    public Armor() {}

    public Armor(String id, String name, String type, String rarity, long defense) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.rarity = rarity;
        this.defense = defense;
        this.level = 1;
        this.enhancementLevel = 0;
        this.refinementLevel = 0;
        this.quantity = 1;
        this.hpBonus = 0;
    }

    public long getEnhancedDefense() {
        return defense + (enhancementLevel * defense / 10) + (refinementLevel * defense / 5);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getRarity() { return rarity; }
    public long getDefense() { return defense; }
    public void setDefense(long d) { this.defense = d; }
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
