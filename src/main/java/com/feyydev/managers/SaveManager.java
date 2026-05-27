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
        String filePath = Constants.SAVE_DIR + "/" + Constants.SAVE_FILE;
        player.calcTotalPower();
        return jsonService.saveToFile(player, filePath);
    }

    public Player loadGame() {
        String filePath = Constants.SAVE_DIR + "/" + Constants.SAVE_FILE;
        try {
            Player player = jsonService.loadFromFile(filePath);
            if (player != null) return player;
        } catch (Exception e) {
            System.err.println("Save file corrupt, creating new game: " + e.getMessage());
        }
        return createNewGame();
    }

    public Player createNewGame() {
        Player player = new Player();
        player.getCharacters().addAll(Constants.createDefaultCharacters());
        player.getWeapons().addAll(Constants.createDefaultWeapons().subList(0, 3));
        player.getArmors().addAll(Constants.createDefaultArmors().subList(0, 2));
        player.getInventory().addAll(Constants.createDefaultPotions());
        player.getInventory().forEach(i -> {
            if (i.getType().equals("Consumable")) i.setQuantity(10);
        });
        return player;
    }

    public boolean hasSaveData() {
        return new java.io.File(Constants.SAVE_DIR + "/" + Constants.SAVE_FILE).exists();
    }

    public void deleteSave() {
        java.io.File file = new java.io.File(Constants.SAVE_DIR + "/" + Constants.SAVE_FILE);
        if (file.exists()) file.delete();
    }
}
