package com.feyydev.services;

import com.feyydev.models.*;
import com.google.gson.*;
import java.io.*;

public class JsonService {
    private static JsonService instance;
    private final Gson gson;

    private JsonService() {
        gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    }

    public static JsonService getInstance() {
        if (instance == null) instance = new JsonService();
        return instance;
    }

    public String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public Player fromJson(String json) {
        return gson.fromJson(json, Player.class);
    }

    public boolean saveToFile(Player player, String filePath) {
        try {
            File file = new File(filePath);
            file.getParentFile().mkdirs();
            try (Writer writer = new FileWriter(file)) {
                gson.toJson(player, writer);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Player loadFromFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) return null;
        try (Reader reader = new FileReader(file)) {
            return gson.fromJson(reader, Player.class);
        } catch (IOException | JsonParseException e) {
            System.err.println("Failed to load save file: " + e.getMessage());
            return null;
        }
    }

    public Gson getGson() { return gson; }
}
