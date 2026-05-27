package com.feyydev.managers;

import com.feyydev.models.*;
import java.util.*;
import java.util.stream.Collectors;

public class CharacterManager {
    private static CharacterManager instance;
    private Player player;

    private CharacterManager() {}

    public static CharacterManager getInstance() {
        if (instance == null) instance = new CharacterManager();
        return instance;
    }

    public void setPlayer(Player player) { this.player = player; }

    public List<GameCharacter> getCharacters() { return player.getCharacters(); }

    public List<GameCharacter> getCharactersByRarity(String rarity) {
        return player.getCharacters().stream()
            .filter(c -> c.getRarity().equals(rarity))
            .collect(Collectors.toList());
    }

    public List<GameCharacter> getCharactersByCategory(String category) {
        return player.getCharacters().stream()
            .filter(c -> c.getCategory().equals(category))
            .collect(Collectors.toList());
    }

    public List<GameCharacter> searchCharacters(String query) {
        return player.getCharacters().stream()
            .filter(c -> c.getName().toLowerCase().contains(query.toLowerCase()))
            .collect(Collectors.toList());
    }

    public GameCharacter getCharacter(String id) {
        return player.getCharacters().stream()
            .filter(c -> c.getId().equals(id))
            .findFirst().orElse(null);
    }

    public void addCharacter(GameCharacter character) {
        Optional<GameCharacter> existing = player.getCharacters().stream()
            .filter(c -> c.getId().equals(character.getId()))
            .findFirst();
        if (existing.isPresent()) {
            existing.get().addDuplicate();
        } else {
            player.getCharacters().add(character);
        }
    }

    public boolean addExp(String id, long amount) {
        GameCharacter c = getCharacter(id);
        if (c != null) { c.addExp(amount); return true; }
        return false;
    }

    public boolean awaken(String id) {
        GameCharacter c = getCharacter(id);
        return c != null && c.awaken();
    }

    public boolean evolve(String id) {
        GameCharacter c = getCharacter(id);
        return c != null && c.evolve();
    }

    public boolean upgradeSkill(String id) {
        GameCharacter c = getCharacter(id);
        return c != null && c.upgradeSkill();
    }

    public boolean equipWeapon(String characterId, String weaponId) {
        GameCharacter c = getCharacter(characterId);
        if (c == null) return false;
        Weapon w = null;
        for (Weapon weapon : player.getWeapons()) {
            if (weapon.getId().equals(weaponId)) { w = weapon; break; }
        }
        if (w == null) return false;
        if (c.getEquippedWeaponId() != null) unequipWeapon(characterId);
        c.setEquippedWeaponId(weaponId);
        c.setAttack(c.getAttack() + w.getEnhancedAttack());
        c.setCriticalChance(c.getCriticalChance() + w.getCriticalChanceBonus());
        return true;
    }

    public void unequipWeapon(String characterId) {
        GameCharacter c = getCharacter(characterId);
        if (c == null || c.getEquippedWeaponId() == null) return;
        String oldId = c.getEquippedWeaponId();
        for (Weapon w : player.getWeapons()) {
            if (w.getId().equals(oldId)) {
                c.setAttack(c.getAttack() - w.getEnhancedAttack());
                c.setCriticalChance(c.getCriticalChance() - w.getCriticalChanceBonus());
                break;
            }
        }
        c.setEquippedWeaponId(null);
    }

    public boolean equipArmor(String characterId, String armorId) {
        GameCharacter c = getCharacter(characterId);
        if (c == null) return false;
        Armor a = null;
        for (Armor armor : player.getArmors()) {
            if (armor.getId().equals(armorId)) { a = armor; break; }
        }
        if (a == null) return false;
        if (c.getEquippedArmorId() != null) unequipArmor(characterId);
        c.setEquippedArmorId(armorId);
        c.setDefense(c.getDefense() + a.getEnhancedDefense());
        c.setMaxHp(c.getMaxHp() + (long)a.getHpBonus());
        c.healFull();
        return true;
    }

    public void unequipArmor(String characterId) {
        GameCharacter c = getCharacter(characterId);
        if (c == null || c.getEquippedArmorId() == null) return;
        String oldId = c.getEquippedArmorId();
        for (Armor a : player.getArmors()) {
            if (a.getId().equals(oldId)) {
                c.setDefense(c.getDefense() - a.getEnhancedDefense());
                c.setMaxHp(c.getMaxHp() - (long)a.getHpBonus());
                break;
            }
        }
        c.setEquippedArmorId(null);
    }

    public int getSSRCount() { return (int) player.getCharacters().stream().filter(c -> c.getRarity().equals("SSR")).count(); }
    public int getTotalLevel() { return player.getCharacters().stream().mapToInt(GameCharacter::getLevel).sum(); }
}
