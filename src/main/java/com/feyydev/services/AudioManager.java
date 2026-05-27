package com.feyydev.services;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.InputStream;
import java.net.URL;

public class AudioManager {
    private static AudioManager instance;
    private boolean muted;
    private double volume;
    private MediaPlayer bgmPlayer;
    private boolean audioAvailable;

    private AudioManager() {
        this.muted = false;
        this.volume = 0.7;
        this.audioAvailable = checkAudioFile();
    }

    public static AudioManager getInstance() {
        if (instance == null) instance = new AudioManager();
        return instance;
    }

    private boolean checkAudioFile() {
        try (InputStream is = getClass().getResourceAsStream("/com/feyydev/assets/music/soundtrack.mp3")) {
            if (is == null) {
                System.out.println("[Audio] soundtrack.mp3 not found on classpath");
                return false;
            }
            byte[] header = new byte[12];
            if (is.read(header) < 12) return false;
            String brand = new String(header, 4, 4);
            if ("dash".equals(brand)) {
                System.out.println("[Audio] soundtrack.mp3 is not playable audio (DASH manifest) — replace with a real MP3");
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public void playBGM(String type) {
        stopBGM();
        if (muted || !audioAvailable) return;

        URL resource = getClass().getResource("/com/feyydev/assets/music/soundtrack.mp3");
        if (resource == null) return;
        try {
            Media media = new Media(resource.toExternalForm());
            bgmPlayer = new MediaPlayer(media);
            bgmPlayer.setVolume(volume);
            bgmPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            bgmPlayer.play();
        } catch (Exception e) {
            System.out.println("[Audio] BGM failed (" + type + "): " + e.getMessage());
        }
    }

    public void stopBGM() {
        if (bgmPlayer != null) {
            bgmPlayer.stop();
            bgmPlayer.dispose();
            bgmPlayer = null;
        }
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
    public void setMuted(boolean muted) {
        this.muted = muted;
        if (muted) stopBGM();
    }
    public double getVolume() { return volume; }
    public void setVolume(double volume) {
        this.volume = Math.max(0, Math.min(1, volume));
        if (bgmPlayer != null) bgmPlayer.setVolume(this.volume);
    }
}
