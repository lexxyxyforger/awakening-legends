package com.feyydev.managers;

import com.feyydev.models.*;
import java.util.List;
import java.util.stream.Collectors;

public class InventoryManager {
    private static InventoryManager instance;
    private Player player;

    private InventoryManager() {}

    public static InventoryManager getInstance() {
        if (instance == null) instance = new InventoryManager();
        return instance;
    }

    public void setPlayer(Player player) { this.player = player; }

    public List<Item> getItems() { return player.getInventory(); }

    public List<Item> getItemsByType(String type) {
        return player.getInventory().stream()
            .filter(i -> i.getType().equals(type))
            .collect(Collectors.toList());
    }

    public List<Item> getItemsByRarity(String rarity) {
        return player.getInventory().stream()
            .filter(i -> i.getRarity().equals(rarity))
            .collect(Collectors.toList());
    }

    public List<Weapon> getWeapons() { return player.getWeapons(); }

    public List<Weapon> getWeaponsByRarity(String rarity) {
        return player.getWeapons().stream()
            .filter(w -> w.getRarity().equals(rarity))
            .collect(Collectors.toList());
    }

    public List<Armor> getArmors() { return player.getArmors(); }

    public List<Armor> getArmorsByRarity(String rarity) {
        return player.getArmors().stream()
            .filter(a -> a.getRarity().equals(rarity))
            .collect(Collectors.toList());
    }

    public void addItem(Item item) {
        for (Item existing : player.getInventory()) {
            if (existing.getId().equals(item.getId())) {
                existing.addQuantity(item.getQuantity());
                return;
            }
        }
        player.getInventory().add(item);
    }

    public boolean useItem(String itemId, GameCharacter character) {
        for (Item item : player.getInventory()) {
            if (item.getId().equals(itemId) && item.getQuantity() > 0) {
                if (item.getType().equals("Consumable") && item.getHealAmount() > 0) {
                    character.setHp(character.getHp() + item.getHealAmount());
                    item.addQuantity(-1);
                    return true;
                }
                if (item.getId().equals("energy_drink")) {
                    player.setEnergy(Math.min(player.getMaxEnergy(), player.getEnergy() + 20));
                    item.addQuantity(-1);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasItem(String itemId, int amount) {
        return player.getInventory().stream()
            .anyMatch(i -> i.getId().equals(itemId) && i.getQuantity() >= amount);
    }

    public void addWeapon(Weapon weapon) { player.getWeapons().add(weapon); }
    public void addArmor(Armor armor) { player.getArmors().add(armor); }

    public boolean enhanceWeapon(String weaponId) {
        for (Weapon w : player.getWeapons()) {
            if (w.getId().equals(weaponId) && w.getEnhancementLevel() < 10) {
                w.setEnhancementLevel(w.getEnhancementLevel() + 1);
                return true;
            }
        }
        return false;
    }

    public boolean refineWeapon(String weaponId) {
        for (Weapon w : player.getWeapons()) {
            if (w.getId().equals(weaponId) && w.getRefinementLevel() < 5) {
                w.setRefinementLevel(w.getRefinementLevel() + 1);
                return true;
            }
        }
        return false;
    }

    public boolean buyItem(String itemId, long cost, String currency) {
        if (currency.equals("GOLD")) return player.spendGold(cost);
        if (currency.equals("GEMS")) return player.spendGems(cost);
        return false;
    }

    public int getItemCount(String itemId) {
        return player.getInventory().stream()
            .filter(i -> i.getId().equals(itemId))
            .mapToInt(Item::getQuantity)
            .sum();
    }
}
