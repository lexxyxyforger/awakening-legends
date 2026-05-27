package com.feyydev.models;

public class Item {
    private String id;
    private String name;
    private String type;
    private String description;
    private String rarity;
    private int value;
    private long healAmount;
    private int quantity;
    private String icon;

    public Item() {}

    public Item(String id, String name, String type, String description, String rarity, int value) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.rarity = rarity;
        this.value = value;
        this.quantity = 1;
        this.healAmount = 0;
        this.icon = getIconForType(type);
    }
    
    private static String getIconForType(String type) {
        if (type == null) return "\uD83D\uDCE6";
        return switch (type) {
            case "Consumable" -> "\uD83E\uDDEA";
            case "Material" -> "\uD83D\uDC8E";
            default -> "\uD83D\uDCE6";
        };
    }

    public Item(String id, String name, String type, String description, String rarity, int value, long healAmount) {
        this(id, name, type, description, rarity, value);
        this.healAmount = healAmount;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getDescription() { return description; }
    public String getRarity() { return rarity; }
    public int getValue() { return value; }
    public long getHealAmount() { return healAmount; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int q) { this.quantity = q; }
    public void addQuantity(int a) { this.quantity += a; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
}
