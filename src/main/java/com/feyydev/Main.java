package com.feyydev;

import com.feyydev.managers.*;
import com.feyydev.models.Player;
import com.feyydev.scenes.*;
import com.feyydev.services.MailService;
import com.feyydev.services.AudioManager;
import com.feyydev.utils.Constants;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.time.LocalDate;

public class Main extends Application {

        public enum SceneType {
        HOME, BATTLE, CHARACTER, INVENTORY, SHOP, QUEST, GACHA, EVENT,
        MAILBOX, ATTENDANCE, WORLD_MAP, RAID, STORY
    }

    private Stage stage;
    private Player player;
    private HomeScene homeScene;
    private BattleScene battleScene;
    private CharacterScene characterScene;
    private InventoryScene inventoryScene;
    private ShopScene shopScene;
    private QuestScene questScene;
    private GachaScene gachaScene;
    private EventScene eventScene;
    private MailboxScene mailboxScene;
    private AttendanceScene attendanceScene;
    private WorldMapScene worldMapScene;
    private RaidScene raidScene;
    private StoryScene storyScene;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        stage.setTitle(Constants.GAME_TITLE);
        stage.setResizable(false);

        SaveManager saveManager = SaveManager.getInstance();
        player = saveManager.loadGame();

        CharacterManager.getInstance().setPlayer(player);
        InventoryManager.getInstance().setPlayer(player);
        QuestManager.getInstance().setPlayer(player);
        AttendanceManager.getInstance().setPlayer(player);
        RaidManager.getInstance().setPlayer(player);
        ArenaManager.getInstance().setPlayer(player);
        EventManager.getInstance().setPlayer(player);
        GachaManager.getInstance().init();

        checkLoginReward();
        MailService.getInstance().cleanExpiredMails(player);

        AudioManager.getInstance().playHomeBGM();

        homeScene = new HomeScene(player, this::navigateTo);
        battleScene = new BattleScene(player, this::navigateTo);
        characterScene = new CharacterScene(player, this::navigateTo);
        inventoryScene = new InventoryScene(player, this::navigateTo);
        shopScene = new ShopScene(player, this::navigateTo);
        questScene = new QuestScene(player, this::navigateTo);
        gachaScene = new GachaScene(player, this::navigateTo);
        eventScene = new EventScene(player, this::navigateTo);
        mailboxScene = new MailboxScene(player, this::navigateTo);
        attendanceScene = new AttendanceScene(player, this::navigateTo);
        worldMapScene = new WorldMapScene(player, this::navigateTo);
        raidScene = new RaidScene(player, this::navigateTo);
        storyScene = new StoryScene(player, this::navigateTo);

        navigateTo(SceneType.HOME);
        stage.setOnCloseRequest(e -> saveManager.saveGame(player));
        stage.show();
    }

    private void checkLoginReward() {
        String today = LocalDate.now().toString();
        if (!today.equals(player.getLastLoginDate())) {
            player.setDailyLoginStreak(player.getDailyLoginStreak() + 1);
            player.setLastLoginDate(today);
            long bonus = player.getDailyLoginStreak() * 100;
            player.addGold(bonus);
            player.addGems(player.getDailyLoginStreak() * 10);
            player.addExp(50);
        }
    }

    private void navigateTo(SceneType type) {
        saveCurrentState();
        switch (type) {
            case HOME -> { homeScene.refresh(); stage.setScene(homeScene.getScene()); AudioManager.getInstance().playHomeBGM(); }
            case BATTLE -> { stage.setScene(battleScene.getScene()); AudioManager.getInstance().playBattleBGM(); }
            case CHARACTER -> { characterScene.refreshGrid(); stage.setScene(characterScene.getScene()); }
            case INVENTORY -> stage.setScene(inventoryScene.getScene());
            case SHOP -> stage.setScene(shopScene.getScene());
            case QUEST -> { questScene.refreshQuests(); stage.setScene(questScene.getScene()); }
            case GACHA -> stage.setScene(gachaScene.getScene());
            case EVENT -> { eventScene.refresh(); stage.setScene(eventScene.getScene()); }
            case MAILBOX -> { mailboxScene.refresh(); stage.setScene(mailboxScene.getScene()); }
            case ATTENDANCE -> { attendanceScene.refresh(); stage.setScene(attendanceScene.getScene()); }
            case WORLD_MAP -> { worldMapScene.refresh(); stage.setScene(worldMapScene.getScene()); }
            case RAID -> stage.setScene(raidScene.getScene());
            case STORY -> { storyScene.refresh(); stage.setScene(storyScene.getScene()); }
        }
    }

    private void saveCurrentState() {
        SaveManager.getInstance().saveGame(player);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
