package com.feyydev.services;

import java.util.Random;

public class AudioManager {
    private static AudioManager instance;
    private boolean muted;
    private double volume;
    private final Random random;

    private AudioManager() {
        this.muted = false;
        this.volume = 0.7;
        this.random = new Random();
    }

    public static AudioManager getInstance() {
        if (instance == null) instance = new AudioManager();
        return instance;
    }

    public void playBGM(String type) {
        if (muted) return;
        System.out.println("[Audio] BGM: " + type);
    }

    public void playHomeBGM() { playBGM("home"); }
    public void playBattleBGM() { playBGM("battle"); }
    public void playBossBGM() { playBGM("boss"); }
    public void playRaidBGM() { playBGM("raid"); }
    public void playGachaBGM() { playBGM("gacha"); }
    public void playEventBGM() { playBGM("event"); }

    public void playSFX(String type) {
        if (muted) return;
        System.out.println("[Audio] SFX: " + type);
    }

    public void playClick() { playSFX("click"); }
    public void playLevelUp() { playSFX("level_up"); }
    public void playGachaPull() { playSFX("gacha_pull"); }
    public void playGachaSSR() { playSFX("gacha_ssr"); }
    public void playDamage() { playSFX("damage"); }
    public void playVictory() { playSFX("victory"); }
    public void playDefeat() { playSFX("defeat"); }
    public void playClaim() { playSFX("claim"); }
    public void playEquip() { playSFX("equip"); }
    public void playUpgrade() { playSFX("upgrade"); }

    public boolean isMuted() { return muted; }
    public void setMuted(boolean muted) { this.muted = muted; }
    public double getVolume() { return volume; }
    public void setVolume(double volume) { this.volume = Math.max(0, Math.min(1, volume)); }
}
