package com.feyydev.managers;

import com.feyydev.models.Player;
import com.feyydev.services.JsonService;
import com.feyydev.utils.Constants;

public class SaveManager {
    private static SaveManager instance;
    private final JsonService jsonService;

    private SaveManager() {
        jsonService = JsonService.getInstance();
    }

    public static SaveManager getInstance() {
        if (instance == null) instance = new SaveManager();
        return instance;
    }

    public boolean saveGame(Player player) {
        try {
            String filePath = Constants.SAVE_DIR + "/" + Constants.SAVE_FILE;
            player.calcTotalPower();
            return jsonService.saveToFile(player, filePath);
        } catch (Exception e) {
            System.err.println("Save failed: " + e.getMessage());
            return false;
        }
    }

    public Player loadGame() {
        String filePath = Constants.SAVE_DIR + "/" + Constants.SAVE_FILE;
        try {
            Player player = jsonService.loadFromFile(filePath);
            if (player != null) {
                if (player.getPlayerId() == null || player.getPlayerId().isEmpty()) {
                    int count = player.getNextPlayerId();
                    if (count == 0) count = 101;
                    player.setPlayerId(formatPlayerId(count));
                    if (player.getNextPlayerId() == 0) player.setNextPlayerId(101);
                }
                // Fix up all characters after Gson deserialization
                player.getCharacters().forEach(c -> c.postLoadFixup());
                return player;
            }
        } catch (Exception e) {
            System.err.println("Save file corrupt, creating new game: " + e.getMessage());
        }
        return createNewGame();
    }

    public Player createNewGame() {
        Player player = new Player();
        int id = player.getNextPlayerId();
        player.setPlayerId(formatPlayerId(id));
        player.setNextPlayerId(id + 1);
        player.getCharacters().addAll(Constants.createDefaultCharacters());
        player.getWeapons().addAll(Constants.createDefaultWeapons().subList(0, 3));
        player.getArmors().addAll(Constants.createDefaultArmors().subList(0, 2));
        player.getInventory().addAll(Constants.createDefaultPotions());
        player.getInventory().forEach(i -> {
            if (i.getType().equals("Consumable")) i.setQuantity(10);
        });
        return player;
    }

    private String formatPlayerId(int id) {
        if (id <= 100) {
            return String.format("%04d", id);
        } else {
            return String.format("%05d", id);
        }
    }

    public boolean hasSaveData() {
        return new java.io.File(Constants.SAVE_DIR + "/" + Constants.SAVE_FILE).exists();
    }

    public void deleteSave() {
        try {
            java.io.File file = new java.io.File(Constants.SAVE_DIR + "/" + Constants.SAVE_FILE);
            if (file.exists() && !file.delete()) {
                System.err.println("Failed to delete save file: " + file.getPath());
            }
        } catch (Exception e) {
            System.err.println("Error deleting save: " + e.getMessage());
        }
    }
}
