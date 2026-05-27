package com.feyydev.managers;

import com.feyydev.models.GameCharacter;
import com.feyydev.models.SummonBanner;
import com.feyydev.utils.Constants;
import java.util.*;

public class GachaManager {
    private static GachaManager instance;
    private final Random random;
    private List<GameCharacter> allCharacters;
    private List<SummonBanner> banners;
    private List<GameCharacter> pullHistory;
    private Map<String, Integer> bannerPitySSR;
    private Map<String, Integer> bannerPitySR;
    private int totalPulls;
    private PlayerProvider playerProvider;

    @FunctionalInterface
    public interface PlayerProvider {
        com.feyydev.models.Player get();
    }

    private GachaManager() {
        random = new Random();
        pullHistory = new ArrayList<>();
        banners = new ArrayList<>();
        bannerPitySSR = new HashMap<>();
        bannerPitySR = new HashMap<>();
        totalPulls = 0;
    }

    public static GachaManager getInstance() {
        if (instance == null) instance = new GachaManager();
        return instance;
    }

    public void setPlayerProvider(PlayerProvider provider) { this.playerProvider = provider; }
    public void init() {
        allCharacters = Constants.createDefaultCharacters();
        banners = new ArrayList<>(Constants.createSummonBanners());
        if (pullHistory == null) pullHistory = new ArrayList<>();
    }

    public GameCharacter pull(SummonBanner banner) {
        String bannerId = banner != null ? banner.getId() : "default";
        int pitySSR = bannerPitySSR.getOrDefault(bannerId, 0) + 1;
        int pitySR = bannerPitySR.getOrDefault(bannerId, 0) + 1;
        bannerPitySSR.put(bannerId, pitySSR);
        bannerPitySR.put(bannerId, pitySR);
        totalPulls++;

        double ssrRate = Constants.SSR_RATE + (banner != null ? banner.getRateUpBonus() : 0);
        double srRate = Constants.SR_RATE;

        if (pitySSR >= Constants.PITY_SSR) {
            ssrRate = 1.0;
        } else if (pitySR >= Constants.PITY_SR) {
            srRate = Math.min(1.0, ssrRate + 0.5);
        }

        double roll = random.nextDouble();
        String rarity;
        if (roll < ssrRate) {
            rarity = "SSR";
            bannerPitySSR.put(bannerId, 0);
            bannerPitySR.put(bannerId, 0);
        } else if (roll < ssrRate + srRate) {
            rarity = "SR";
            bannerPitySR.put(bannerId, 0);
        } else {
            rarity = "R";
        }

        List<GameCharacter> pool;
        if (banner != null && !banner.getRateUpCharacterIds().isEmpty() && rarity.equals("SSR") && random.nextDouble() < 0.5) {
            pool = allCharacters.stream()
                .filter(c -> banner.getRateUpCharacterIds().contains(c.getId()) && c.getRarity().equals(rarity))
                .toList();
            if (pool.isEmpty()) {
                pool = allCharacters.stream().filter(c -> c.getRarity().equals(rarity)).toList();
            }
        } else {
            pool = allCharacters.stream().filter(c -> c.getRarity().equals(rarity)).toList();
        }

        if (pool.isEmpty()) {
            pool = allCharacters.stream().filter(c -> c.getRarity().equals("R")).toList();
        }

        GameCharacter pulled = pool.get(random.nextInt(pool.size()));
        GameCharacter result = new GameCharacter(
            pulled.getId() + "_" + System.currentTimeMillis(),
            pulled.getName(),
            pulled.getRarity(),
            pulled.getCategory()
        );
        pullHistory.add(result);
        if (playerProvider != null && playerProvider.get() != null) {
            playerProvider.get().getCharacters().add(result);
        }
        return result;
    }

    public List<GameCharacter> multiPull(int count, SummonBanner banner) {
        List<GameCharacter> results = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            results.add(pull(banner));
        }
        return results;
    }

    public List<SummonBanner> getBanners() { return banners; }
    public SummonBanner getBanner(String id) {
        return banners.stream().filter(b -> b.getId().equals(id)).findFirst().orElse(null);
    }
    public List<GameCharacter> getPullHistory() { return pullHistory; }
    public void clearHistory() { pullHistory.clear(); }

    public int getPitySSR(String bannerId) { return bannerPitySSR.getOrDefault(bannerId, 0); }
    public int getPitySR(String bannerId) { return bannerPitySR.getOrDefault(bannerId, 0); }
    public int getTotalPulls() { return totalPulls; }
}
