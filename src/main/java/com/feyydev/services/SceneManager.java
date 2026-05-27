package com.feyydev.services;

import com.feyydev.Main.SceneType;

import java.util.function.Consumer;

public class SceneManager {
    private static SceneManager instance;
    private Consumer<SceneType> navigator;

    private SceneManager() {}

    public static SceneManager getInstance() {
        if (instance == null) instance = new SceneManager();
        return instance;
    }

    public void init(Consumer<SceneType> navigator) {
        this.navigator = navigator;
    }

    public void navigate(SceneType type) {
        if (navigator != null) {
            System.out.println("[SceneManager] Navigate to " + type);
            navigator.accept(type);
        }
    }

    public void back() {
        navigate(SceneType.HOME);
    }
}
