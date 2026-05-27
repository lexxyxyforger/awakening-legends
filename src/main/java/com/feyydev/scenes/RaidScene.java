package com.feyydev.scenes;

import com.feyydev.Main.SceneType;
import com.feyydev.managers.BattleManager;
import com.feyydev.managers.RaidManager;
import com.feyydev.models.GameCharacter;
import com.feyydev.models.Player;
import com.feyydev.models.RaidBoss;
import com.feyydev.utils.Constants;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.util.Duration;
import java.util.*;
import java.util.function.Consumer;

public class RaidScene {
    private final Scene scene;
    private final Player player;
    private final Consumer<SceneType> navigator;
    private final RaidManager raidManager;
    private final Label bossNameLabel;
    private final ProgressBar bossHpBar;
    private final Label bossHpLabel;
    private final Label timerLabel;
    private final Label damageLabel;
    private final VBox contributionList;
    private final VBox actionPanel;
    private final VBox resultPanel;
    private Timeline raidTimer;
    private long lastDamage;

    public RaidScene(Player player, Consumer<SceneType> navigator) {
        this.player = player;
        this.navigator = navigator;
        this.raidManager = RaidManager.getInstance();
        this.bossNameLabel = new Label();
        this.bossHpBar = new ProgressBar(1.0);
        this.bossHpLabel = new Label();
        this.timerLabel = new Label();
        this.damageLabel = new Label();
        this.contributionList = new VBox(4);
        this.actionPanel = new VBox(12);
        this.resultPanel = new VBox(16);
        this.scene = buildScene();
    }

    public Scene getScene() { return scene; }

    private Scene buildScene() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");
        root.setPadding(new Insets(16));

        HBox topBar = new HBox(12);
        topBar.setAlignment(Pos.CENTER_LEFT);
        Button backBtn = new Button("\u2190 Back");
        backBtn.getStyleClass().add("back-button");
        backBtn.setOnAction(e -> {
            if (raidTimer != null) raidTimer.stop();
            navigator.accept(SceneType.HOME);
        });
        Label title = new Label("\uD83D\uDC7E Boss Raid");
        title.getStyleClass().add("scene-title");
        topBar.getChildren().addAll(backBtn, title);

        VBox bossSection = new VBox(8);
        bossSection.setAlignment(Pos.CENTER);
        bossSection.setPadding(new Insets(10));

        Label bossIcon = new Label("\uD83D\uDC7F");
        bossIcon.setFont(Font.font(48));

        bossNameLabel.getStyleClass().add("boss-name");
        bossHpBar.setPrefWidth(400);
        bossHpBar.setPrefHeight(16);
        bossHpBar.getStyleClass().add("raid-hp-bar");
        bossHpLabel.setStyle("-fx-text-fill: #fbbf24; -fx-font-size: 14px; -fx-font-weight: bold;");

        timerLabel.setStyle("-fx-text-fill: #f87171; -fx-font-size: 16px; -fx-font-weight: bold;");
        damageLabel.setStyle("-fx-text-fill: #fbbf24; -fx-font-size: 18px; -fx-font-weight: bold;");

        bossSection.getChildren().addAll(bossIcon, bossNameLabel, bossHpBar, bossHpLabel, timerLabel, damageLabel);

        VBox rightPanel = new VBox(8);
        rightPanel.setPrefWidth(250);
        rightPanel.setPadding(new Insets(8));
        rightPanel.getStyleClass().add("glass-card");

        Label contribTitle = new Label("\uD83C\uDFC6 Contribution");
        contribTitle.setStyle("-fx-text-fill: #93c5fd; -fx-font-size: 14px; -fx-font-weight: bold;");

        contributionList.setAlignment(Pos.TOP_CENTER);
        ScrollPane contribScroll = new ScrollPane(contributionList);
        contribScroll.getStyleClass().add("scroll-pane");
        contribScroll.setFitToWidth(true);
        contribScroll.setPrefHeight(300);

        rightPanel.getChildren().addAll(contribTitle, contribScroll);

        actionPanel.setAlignment(Pos.CENTER);
        actionPanel.setPadding(new Insets(10));

        Button startBtn = new Button("\u2694 Start Raid");
        startBtn.getStyleClass().add("primary-button");
        startBtn.setOnAction(e -> startRaid());

        Button attackBtn = new Button("\uD83D\uDCA5 Attack!");
        attackBtn.getStyleClass().addAll("action-button", "attack-button");
        attackBtn.setOnAction(e -> executeAttack());
        attackBtn.setDisable(true);

        Button retreatBtn = new Button("\uD83C\uDFC3 Retreat");
        retreatBtn.getStyleClass().add("danger-button");
        retreatBtn.setOnAction(e -> endRaid());
        retreatBtn.setDisable(true);

        HBox attackRow = new HBox(12, attackBtn, retreatBtn);
        attackRow.setAlignment(Pos.CENTER);
        actionPanel.getChildren().addAll(startBtn, attackRow);

        resultPanel.setAlignment(Pos.CENTER);
        resultPanel.setVisible(false);

        HBox content = new HBox(16, new VBox(10, bossSection, actionPanel, resultPanel), rightPanel);
        HBox.setHgrow(content, Priority.ALWAYS);

        root.setTop(topBar);
        root.setCenter(content);

        Scene s = new Scene(root, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        s.getStylesheets().add(getClass().getResource("/com/feyydev/style.css").toExternalForm());
        return s;
    }

    private void startRaid() {
        if (player.getRaidTokens() <= 0) {
            showToast("No raid tokens!");
            return;
        }
        player.spendRaidTokens(1);
        raidManager.startRaid(player.getCurrentChapter());
        updateRaidUI();
        actionPanel.getChildren().clear();

        Button attackBtn = new Button("\uD83D\uDCA5 Attack!");
        attackBtn.getStyleClass().addAll("action-button", "attack-button");
        attackBtn.setOnAction(e -> executeAttack());

        Button retreatBtn = new Button("\uD83C\uDFC3 Retreat");
        retreatBtn.getStyleClass().add("danger-button");
        retreatBtn.setOnAction(e -> endRaid());

        actionPanel.getChildren().addAll(new HBox(12, attackBtn, retreatBtn));
        resultPanel.setVisible(false);
        startTimer();
    }

    private void executeAttack() {
        if (!raidManager.isRaidActive()) {
            showToast("Raid ended!");
            return;
        }
        GameCharacter chara = player.getEquippedCharacter();
        if (chara == null) return;

        long damage = raidManager.executeAttack(Arrays.asList(chara));
        lastDamage = damage;
        updateRaidUI();

        if (!raidManager.getCurrentBoss().isAlive()) {
            handleRaidVictory();
        }
    }

    private void updateRaidUI() {
        RaidBoss boss = raidManager.getCurrentBoss();
        if (boss == null) return;
        bossNameLabel.setText(boss.getName() + " (Lv." + boss.getLevel() + ")");
        bossHpBar.setProgress(boss.getHpPercent());
        bossHpLabel.setText(Constants.formatNumber(boss.getHp()) + " / " + Constants.formatNumber(boss.getMaxHp()));
        damageLabel.setText("Last: " + Constants.formatNumber(lastDamage) + " damage");

        contributionList.getChildren().clear();
        int rank = 1;
        for (var c : raidManager.getContributions()) {
            HBox entry = new HBox(8);
            entry.setAlignment(Pos.CENTER_LEFT);
            Label rankLbl = new Label("#" + rank);
            rankLbl.getStyleClass().add("contribution-rank");
            Label nameLbl = new Label(c.getPlayerName());
            nameLbl.setStyle("-fx-text-fill: #334155; -fx-font-size: 12px;");
            Label dmgLbl = new Label(Constants.formatNumber(c.getDamage()));
            dmgLbl.setStyle("-fx-text-fill: #fbbf24; -fx-font-size: 12px; -fx-font-weight: bold;");
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            entry.getChildren().addAll(rankLbl, nameLbl, spacer, dmgLbl);
            contributionList.getChildren().add(entry);
            rank++;
        }
    }

    private void startTimer() {
        raidTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            long elapsed = System.currentTimeMillis() - raidManager.raidStartTime;
            long remaining = 180 - elapsed / 1000;
            if (remaining <= 0) {
                timerLabel.setText("Time's up!");
                endRaid();
            } else {
                timerLabel.setText("\u23F1 " + remaining + "s");
            }
        }));
        raidTimer.setCycleCount(Timeline.INDEFINITE);
        raidTimer.play();
    }

    private void handleRaidVictory() {
        if (raidTimer != null) raidTimer.stop();
        resultPanel.setVisible(true);
        resultPanel.getChildren().clear();

        Label victLbl = new Label("\uD83C\uDFC6 VICTORY!");
        victLbl.getStyleClass().add("victory-label");

        VBox rewards = new VBox(6);
        rewards.setAlignment(Pos.CENTER);
        rewards.setPadding(new Insets(8));
        rewards.getStyleClass().add("glass-card");
        for (var r : raidManager.getRaidRewards()) {
            Label rl = new Label(r.getName() + ": +" + Constants.formatNumber(r.getAmount()));
            rl.setStyle("-fx-text-fill: #fbbf24; -fx-font-size: 13px;");
            rewards.getChildren().add(rl);
            player.addGold(r.getAmount());
            player.addExp(r.getAmount() / 2);
        }

        Button homeBtn = new Button("Back to Home");
        homeBtn.getStyleClass().add("action-button");
        homeBtn.setOnAction(e -> {
            raidManager.endRaid();
            navigator.accept(SceneType.HOME);
        });

        resultPanel.getChildren().addAll(victLbl, rewards, homeBtn);
        actionPanel.getChildren().clear();
    }

    private void endRaid() {
        if (raidTimer != null) raidTimer.stop();
        raidManager.endRaid();
        resultPanel.setVisible(true);
        resultPanel.getChildren().clear();

        Label endLbl = new Label("\uD83C\uDFC3 Raid Ended");
        endLbl.setStyle("-fx-text-fill: #fbbf24; -fx-font-size: 22px; -fx-font-weight: bold;");

        Label summary = new Label("Total Damage: " + Constants.formatNumber(player.getTotalBossDamage()));
        summary.setStyle("-fx-text-fill: #334155; -fx-font-size: 14px;");

        Button homeBtn = new Button("Back to Home");
        homeBtn.getStyleClass().add("action-button");
        homeBtn.setOnAction(e -> navigator.accept(SceneType.HOME));

        resultPanel.getChildren().addAll(endLbl, summary, homeBtn);
        actionPanel.getChildren().clear();
    }

    private void showToast(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null); a.setContentText(msg);
        a.getDialogPane().setStyle("-fx-background-color: #1e293b; -fx-text-fill: #fff;");
        a.showAndWait();
    }
}
