package com.feyydev.scenes;

import com.feyydev.Main.SceneType;
import com.feyydev.managers.*;
import com.feyydev.models.Player;
import com.feyydev.services.MailService;
import com.feyydev.utils.Constants;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import java.util.function.Consumer;

public class HomeScene {
    private final Scene scene;
    private final Player player;
    private final Consumer<SceneType> navigator;
    private final Label goldLabel;
    private final Label gemsLabel;
    private final Label energyLabel;
    private final Label levelLabel;
    private final Label userNameLabel;
    private final Label playerIdLabel;
    private final ProgressBar expBar;
    private final Label expLabel;
    private final VBox centerContent;
    private final Label mailBadge;
    private final Label questBadge;

    public HomeScene(Player player, Consumer<SceneType> navigator) {
        this.player = player;
        this.navigator = navigator;
        this.goldLabel = new Label();
        this.gemsLabel = new Label();
        this.energyLabel = new Label();
        this.levelLabel = new Label();
        this.userNameLabel = new Label();
        this.playerIdLabel = new Label();
        this.expBar = new ProgressBar();
        this.expLabel = new Label();
        this.centerContent = new VBox(12);
        this.mailBadge = new Label();
        this.questBadge = new Label();
        this.scene = buildScene();
    }

    public Scene getScene() { return scene; }

    public void refresh() {
        EventManager em = EventManager.getInstance();
        em.updateProgress("LOGIN", 1);
        em.updateProgress("PLAYER_LEVEL", player.getLevel());
        em.updateProgress("POWER", (int) player.getTotalPower());
        updateLabels();
        updateCenterContent();
    }

    private Scene buildScene() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");
        root.setPadding(new Insets(12, 12, 0, 12));

        root.setTop(buildHeader());
        root.setLeft(buildLeftSidebar());
        root.setRight(buildRightSidebar());
        root.setCenter(buildCenterWrapper());
        root.setBottom(buildBottomNav());

        Scene s = new Scene(root, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        s.getStylesheets().add(getClass().getResource("/com/feyydev/style.css").toExternalForm());
        return s;
    }

    private VBox buildHeader() {
        updateLabels();

        HBox topRow = new HBox(16);
        topRow.setAlignment(Pos.CENTER_LEFT);
        topRow.setPadding(new Insets(4, 0, 8, 0));

        VBox avatarBox = new VBox();
        avatarBox.setPrefSize(50, 50);
        avatarBox.setStyle("-fx-background-color: linear-gradient(to bottom right, #3b82f6, #60a5fa); -fx-background-radius: 25; -fx-border-color: rgba(255,255,255,0.2); -fx-border-radius: 25; -fx-border-width: 2;");
        avatarBox.setAlignment(Pos.CENTER);
        Label avatarIcon = new Label("\uD83D\uDC64");
        avatarIcon.setFont(Font.font(24));
        avatarBox.getChildren().add(avatarIcon);

        VBox nameBox = new VBox(2);
        userNameLabel.setText(player.getName());
        userNameLabel.getStyleClass().add("player-name");
        String pid = player.getPlayerId() != null ? player.getPlayerId() : "----";
        playerIdLabel.setText("ID: " + pid);
        playerIdLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 10px;");
        HBox lvlRow = new HBox(6);
        lvlRow.setAlignment(Pos.CENTER_LEFT);
        levelLabel.setText("Lv." + player.getLevel());
        levelLabel.getStyleClass().add("level-label");
        expBar.setPrefWidth(160);
        expBar.setMaxWidth(160);
        if (player.getExpToNext() > 0) expBar.setProgress((double) player.getExp() / player.getExpToNext());
        else expBar.setProgress(1.0);
        expBar.setPrefHeight(6);
        expLabel.setText(Constants.formatNumber(player.getExp()) + " / " + Constants.formatNumber(player.getExpToNext()));
        expLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 10px;");
        lvlRow.getChildren().addAll(levelLabel, expBar, expLabel);
        nameBox.getChildren().addAll(userNameLabel, playerIdLabel, lvlRow);

        HBox currencyBox = new HBox(10);
        currencyBox.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(currencyBox, Priority.ALWAYS);

        VBox goldV = createCurrencyItem("\uD83D\uDCB0", goldLabel, "#fbbf24");
        VBox gemsV = createCurrencyItem("\uD83D\uDC8E", gemsLabel, "#c084fc");
        VBox energyV = createCurrencyItem("\u26A1", energyLabel, "#60a5fa");
        currencyBox.getChildren().addAll(goldV, gemsV, energyV);

        topRow.getChildren().addAll(avatarBox, nameBox, currencyBox);
        return new VBox(topRow);
    }

    private VBox createCurrencyItem(String icon, Label value, String color) {
        VBox box = new VBox(2);
        box.setAlignment(Pos.CENTER);
        box.getStyleClass().add("currency-box");
        Label iconLbl = new Label(icon);
        iconLbl.setFont(Font.font(16));
        value.getStyleClass().add("currency-label");
        value.setStyle("-fx-text-fill: " + color + ";");
        box.getChildren().addAll(iconLbl, value);
        return box;
    }

    private VBox buildLeftSidebar() {
        VBox sidebar = new VBox(6);
        sidebar.setPrefWidth(140);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPadding(new Insets(8));
        sidebar.setAlignment(Pos.TOP_CENTER);

        Label leftTitle = new Label("Quick Menu");
        leftTitle.setStyle("-fx-text-fill: #93c5fd; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 4 0;");

        String[][] items = {
            {"\uD83C\uDF89", "Beginner Event"},
            {"\uD83D\uDED2", "Beginner Shop"},
            {"\uD83C\uDFC6", "Quest Pass"},
            {"\uD83E\uDE99", "Coin Shop"},
            {"\uD83C\uDF1F", "Limited Event"},
            {"\uD83D\uDCC5", "Daily Event"},
            {"\uD83D\uDCEB", "Mailbox"},
            {"\uD83D\uDCCB", "Attendance"}
        };
        for (String[] item : items) {
            Button btn = new Button(item[0] + "  " + item[1]);
            btn.getStyleClass().add("sidebar-item");
            btn.setPrefWidth(130);
            switch (item[1]) {
                case "Mailbox" -> {
                    int mailCount = MailService.getInstance().getUnclaimedCount(player);
                    if (mailCount > 0) {
                        mailBadge.setText(String.valueOf(mailCount));
                        mailBadge.getStyleClass().add("badge");
                        btn.setGraphic(mailBadge);
                        btn.setContentDisplay(javafx.scene.control.ContentDisplay.RIGHT);
                    }
                    btn.setOnAction(e -> navigator.accept(SceneType.MAILBOX));
                }
                case "Attendance" -> btn.setOnAction(e -> navigator.accept(SceneType.ATTENDANCE));
                case "Quest Pass" -> btn.setOnAction(e -> navigator.accept(SceneType.QUEST));
                default -> btn.setOnAction(e -> navigator.accept(SceneType.SHOP));
            }
            sidebar.getChildren().add(btn);
        }
        return new VBox(sidebar);
    }

    private VBox buildRightSidebar() {
        VBox sidebar = new VBox(6);
        sidebar.setPrefWidth(140);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPadding(new Insets(8));
        sidebar.setAlignment(Pos.TOP_CENTER);

        Label rightTitle = new Label("Content");
        rightTitle.setStyle("-fx-text-fill: #93c5fd; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 4 0;");

        String[][] items = {
            {"\uD83C\uDF89", "Event"},
            {"\uD83D\uDCC5", "Daily"},
            {"\u2694", "Ranked PvP"},
            {"\uD83C\uDFC6", "Arena"},
            {"\uD83C\uDF1F", "Guild"},
            {"\uD83D\uDC7E", "Raid"},
            {"\uD83C\uDFAE", "World Map"}
        };
        for (String[] item : items) {
            Button btn = new Button(item[0] + "  " + item[1]);
            btn.getStyleClass().add("sidebar-item");
            btn.setPrefWidth(130);
            switch (item[1]) {
                case "Arena" -> btn.setOnAction(e -> navigator.accept(SceneType.BATTLE));
                case "Raid" -> btn.setOnAction(e -> navigator.accept(SceneType.RAID));
                case "World Map" -> btn.setOnAction(e -> navigator.accept(SceneType.WORLD_MAP));
                case "Event" -> btn.setOnAction(e -> navigator.accept(SceneType.EVENT));
                case "Daily" -> btn.setOnAction(e -> navigator.accept(SceneType.QUEST));
                default -> btn.setOnAction(e -> navigator.accept(SceneType.BATTLE));
            }
            sidebar.getChildren().add(btn);
        }
        return new VBox(sidebar);
    }

    private VBox buildCenterWrapper() {
        VBox wrapper = new VBox(12);
        wrapper.setAlignment(Pos.TOP_CENTER);
        wrapper.setPadding(new Insets(0, 8, 0, 8));
        HBox.setHgrow(wrapper, Priority.ALWAYS);

        Label logo = new Label(Constants.GAME_TITLE);
        logo.getStyleClass().add("game-logo");

        VBox banner = new VBox(8);
        banner.setAlignment(Pos.CENTER);
        banner.setPrefHeight(90);
        banner.setMaxWidth(480);
        banner.getStyleClass().add("glass-card");
        Label phaseLbl = new Label("\uD83C\uDFC6 Season 1 - Tournament Arc");
        phaseLbl.setStyle("-fx-text-fill: #fbbf24; -fx-font-size: 16px; -fx-font-weight: bold;");
        Label subLbl = new Label("Fight your way to the top and become the champion!");
        subLbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px;");
        banner.getChildren().addAll(phaseLbl, subLbl);

        centerContent.setAlignment(Pos.CENTER);
        updateCenterContent();

        wrapper.getChildren().addAll(logo, banner, centerContent);
        return wrapper;
    }

    private void updateCenterContent() {
        centerContent.getChildren().clear();

        VBox teamShowcase = new VBox(8);
        teamShowcase.setAlignment(Pos.CENTER);
        teamShowcase.setPrefHeight(140);
        teamShowcase.setMaxWidth(460);
        teamShowcase.getStyleClass().add("glass-card");

        Label teamTitle = new Label("\u2694 Current Team");
        teamTitle.setStyle("-fx-text-fill: #93c5fd; -fx-font-size: 13px; -fx-font-weight: bold;");

        HBox teamRow = new HBox(10);
        teamRow.setAlignment(Pos.CENTER);
        var chars = player.getCharacters();
        for (int i = 0; i < Math.min(6, chars.size()); i++) {
            var c = chars.get(i);
            VBox memCard = new VBox(4);
            memCard.setAlignment(Pos.CENTER);
            memCard.setPrefSize(65, 80);
            String rc = Constants.getRarityColor(c.getRarity());
            memCard.setStyle("-fx-background-color: rgba(255,255,255,0.04); -fx-background-radius: 10; -fx-border-color: " + rc + "44; -fx-border-radius: 10;");
            Label icon = new Label(Constants.getCharIcon(c.getName()));
            icon.setFont(Font.font(18));
            Label cName = new Label(c.getName().length() > 6 ? c.getName().substring(0, 6) + ".." : c.getName());
            cName.setStyle("-fx-text-fill: " + rc + "; -fx-font-size: 9px; -fx-font-weight: bold;");
            Label lvl = new Label("Lv." + c.getLevel());
            lvl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 9px;");
            memCard.getChildren().addAll(icon, cName, lvl);
            teamRow.getChildren().add(memCard);
        }
        teamShowcase.getChildren().addAll(teamTitle, teamRow);

        VBox progressInfo = new VBox(6);
        progressInfo.setAlignment(Pos.CENTER);
        progressInfo.setPadding(new Insets(12));
        progressInfo.setMaxWidth(460);
        progressInfo.getStyleClass().add("glass-card");

        Label chapLbl = new Label("\uD83D\uDCCD Chapter " + player.getCurrentChapter() + " - Stage " + player.getCurrentStage());
        chapLbl.setStyle("-fx-text-fill: #334155; -fx-font-size: 14px; -fx-font-weight: bold;");
        Label powerLbl = new Label("\u26A1 Power: " + Constants.formatNumber(player.calcTotalPower()));
        powerLbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px;");
        Label streakLbl = new Label("\uD83D\uDD25 Login Streak: " + player.getDailyLoginStreak() + " days");
        streakLbl.setStyle("-fx-text-fill: #fbbf24; -fx-font-size: 12px;");

        HBox topMenu = new HBox(8);
        topMenu.setAlignment(Pos.CENTER);
        String[][] topItems = {
            {"\uD83D\uDCCB", "Attendance", "ATTENDANCE"},
            {"\uD83D\uDEE0", "Quest", "QUEST"},
            {"\uD83D\uDC64", "Upgrade", "CHARACTER"},
            {"\uD83D\uDC65", "Friends", "HOME"},
            {"\u2694", "Team", "CHARACTER"},
            {"\uD83D\uDCEB", "Mailbox", "MAILBOX"}
        };
        for (String[] item : topItems) {
            VBox btn = new VBox(3);
            btn.setAlignment(Pos.CENTER);
            btn.getStyleClass().add("menu-button");
            btn.setPrefSize(60, 56);
            Label icon = new Label(item[0]);
            icon.getStyleClass().add("menu-icon");
            Label txt = new Label(item[1]);
            txt.getStyleClass().add("menu-label");
            btn.getChildren().addAll(icon, txt);
            switch (item[2]) {
                case "ATTENDANCE" -> btn.setOnMouseClicked(e -> navigator.accept(SceneType.ATTENDANCE));
                case "QUEST" -> btn.setOnMouseClicked(e -> navigator.accept(SceneType.QUEST));
                case "CHARACTER" -> btn.setOnMouseClicked(e -> navigator.accept(SceneType.CHARACTER));
                case "MAILBOX" -> btn.setOnMouseClicked(e -> navigator.accept(SceneType.MAILBOX));
            }
            topMenu.getChildren().add(btn);
        }

        progressInfo.getChildren().addAll(chapLbl, powerLbl, streakLbl, topMenu);
        centerContent.getChildren().addAll(teamShowcase, progressInfo);
    }

    private HBox buildBottomNav() {
        HBox bottom = new HBox(6);
        bottom.setAlignment(Pos.CENTER);
        bottom.getStyleClass().add("bottom-nav");

        String[][] navItems = {
            {"\uD83C\uDFE0", "Home", "HOME"},
            {"\uD83D\uDC64", "Character", "CHARACTER"},
            {"\uD83C\uDF81", "Summon", "GACHA"},
            {"\uD83C\uDF0D", "World Map", "WORLD_MAP"},
            {"\uD83D\uDED2", "Shop", "SHOP"},
            {"\uD83D\uDCD6", "Story", "STORY"}
        };
        for (String[] item : navItems) {
            VBox nav = new VBox(1);
            nav.setAlignment(Pos.CENTER);
            nav.getStyleClass().add("nav-item");
            if (item[1].equals("Home")) nav.getStyleClass().add("active");
            Label icon = new Label(item[0]);
            icon.getStyleClass().add("nav-icon");
            Label txt = new Label(item[1]);
            txt.getStyleClass().add(item[1].equals("Home") ? "nav-label active" : "nav-label");
            nav.getChildren().addAll(icon, txt);
            switch (item[2]) {
                case "HOME" -> nav.setOnMouseClicked(e -> navigator.accept(SceneType.HOME));
                case "CHARACTER" -> nav.setOnMouseClicked(e -> navigator.accept(SceneType.CHARACTER));
                case "GACHA" -> nav.setOnMouseClicked(e -> navigator.accept(SceneType.GACHA));
                case "WORLD_MAP" -> nav.setOnMouseClicked(e -> navigator.accept(SceneType.WORLD_MAP));
                case "SHOP" -> nav.setOnMouseClicked(e -> navigator.accept(SceneType.SHOP));
                case "QUEST" -> nav.setOnMouseClicked(e -> navigator.accept(SceneType.QUEST));
                case "STORY" -> nav.setOnMouseClicked(e -> navigator.accept(SceneType.STORY));
            }
            bottom.getChildren().add(nav);
        }
        return bottom;
    }

    public void updateLabels() {
        goldLabel.setText(Constants.formatNumber(player.getGold()));
        gemsLabel.setText(Constants.formatNumber(player.getGems()));
        energyLabel.setText(player.getEnergy() + "/" + player.getMaxEnergy());
        levelLabel.setText("Lv." + player.getLevel());
        userNameLabel.setText(player.getName());
        if (player.getExpToNext() > 0) {
            expBar.setProgress((double) player.getExp() / player.getExpToNext());
        }
        expLabel.setText(Constants.formatNumber(player.getExp()) + " / " + Constants.formatNumber(player.getExpToNext()));
    }
}
