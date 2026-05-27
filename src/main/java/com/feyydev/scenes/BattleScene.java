package com.feyydev.scenes;

import com.feyydev.Main.SceneType;
import com.feyydev.managers.*;
import com.feyydev.models.*;
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

public class BattleScene {
    private final Scene scene;
    private final Player player;
    private final Consumer<SceneType> navigator;
    private final BattleManager battleManager;
    private final QuestManager questManager;
    private final CharacterManager characterManager;
    private GameCharacter activeCharacter;

    private final Label enemyNameLabel;
    private final ProgressBar enemyHpBar;
    private final Label enemyHpLabel;
    private final Label charNameLabel;
    private final ProgressBar charHpBar;
    private final Label charHpLabel;
    private final TextArea battleLog;
    private final VBox actionButtons;
    private final VBox battleResultBox;
    private final Label damagePopLabel;
    private final Label turnIndicator;
    private boolean battleEnded;

    public void refresh() {
        battleEnded = false;
        actionButtons.setVisible(true);
        battleResultBox.setVisible(false);
        activeCharacter = player.getEquippedCharacter();
        if (activeCharacter != null && battleManager.getCurrentEnemy() != null) {
            updateUI();
        }
    }

    public BattleScene(Player player, Consumer<SceneType> navigator) {
        this.player = player;
        this.navigator = navigator;
        this.battleManager = BattleManager.getInstance();
        this.questManager = QuestManager.getInstance();
        this.characterManager = CharacterManager.getInstance();
        this.activeCharacter = player.getEquippedCharacter();

        this.enemyNameLabel = new Label();
        this.enemyHpBar = new ProgressBar(1.0);
        this.enemyHpLabel = new Label();
        this.charNameLabel = new Label();
        this.charHpBar = new ProgressBar(1.0);
        this.charHpLabel = new Label();
        this.battleLog = new TextArea();
        this.actionButtons = new VBox(10);
        this.battleResultBox = new VBox(20);
        this.damagePopLabel = new Label();

        this.turnIndicator = new Label("\u2694 Your Turn");
        this.turnIndicator.setAlignment(Pos.CENTER);

        this.scene = buildScene();
    }

    public Scene getScene() { return scene; }

    private Scene buildScene() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");
        root.setPadding(new Insets(16));

        HBox topBar = new HBox(12);
        topBar.setAlignment(Pos.CENTER_LEFT);
        Button backBtn = new Button("\u2190 Retreat");
        backBtn.getStyleClass().add("back-button");
        backBtn.setOnAction(e -> {
            System.out.println("[DEBUG] BattleScene Back clicked");
            navigator.accept(SceneType.HOME);
        });

        Label title = new Label("\u2694 Battle");
        title.getStyleClass().add("scene-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label infoLabel = new Label("Ch." + player.getCurrentChapter() + " - Stage " + player.getCurrentStage());
        infoLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 13px;");
        topBar.getChildren().addAll(backBtn, title, spacer, infoLabel);

        VBox enemySection = new VBox(8);
        enemySection.setAlignment(Pos.CENTER);
        enemySection.setPadding(new Insets(8));
        enemySection.getStyleClass().add("enemy-section");

        Label enemyIcon = new Label("\uD83D\uDC7E");
        enemyIcon.setFont(Font.font(40));
        enemyNameLabel.getStyleClass().add("enemy-name");
        enemyHpBar.setPrefWidth(350);
        enemyHpBar.setPrefHeight(12);
        enemyHpBar.getStyleClass().add("hp-bar-enemy");
        enemyHpLabel.getStyleClass().add("hp-label");
        enemySection.getChildren().addAll(enemyIcon, enemyNameLabel, enemyHpBar, enemyHpLabel);

        damagePopLabel.getStyleClass().add("damage-popup");
        damagePopLabel.setVisible(false);
        StackPane centerStack = new StackPane(enemySection, damagePopLabel);
        StackPane.setAlignment(damagePopLabel, Pos.CENTER);

        VBox charSection = new VBox(6);
        charSection.setAlignment(Pos.CENTER);
        charSection.setPadding(new Insets(6, 0, 0, 0));
        charSection.getStyleClass().add("char-section");
        charNameLabel.getStyleClass().add("char-name");
        charHpBar.setPrefWidth(350);
        charHpBar.setPrefHeight(12);
        charHpBar.getStyleClass().add("hp-bar-friendly");
        charHpLabel.getStyleClass().add("hp-label");
        charSection.getChildren().addAll(charNameLabel, charHpBar, charHpLabel);

        battleLog.setPrefHeight(100);
        battleLog.setPrefWidth(600);
        battleLog.setEditable(false);
        battleLog.getStyleClass().add("battle-log");

        actionButtons.setAlignment(Pos.CENTER);
        buildActionButtons();

        battleResultBox.setAlignment(Pos.CENTER);
        battleResultBox.setVisible(false);

        VBox bottomSection = new VBox(8);
        bottomSection.setAlignment(Pos.CENTER);
        bottomSection.getChildren().addAll(battleLog, actionButtons, battleResultBox);

        VBox center = new VBox(10, centerStack, turnIndicator, charSection);
        center.setAlignment(Pos.CENTER);

        root.setTop(topBar);
        root.setCenter(center);
        root.setBottom(bottomSection);

        Scene s = new Scene(root, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        s.getStylesheets().add(getClass().getResource("/com/feyydev/global.css").toExternalForm());
        return s;
    }

    private void buildActionButtons() {
        Button attackBtn = styledButton("\u2694 Attack");
        Button skillBtn = styledButton("\uD83D\uDCA5 Skill");
        Button ultimateBtn = styledButton("\u2604 Ultimate");
        Button potionBtn = styledButton("\uD83E\uDDEA Potion");

        attackBtn.setOnAction(e -> executeAction(BattleManager.BattleAction.ATTACK));
        skillBtn.setOnAction(e -> executeAction(BattleManager.BattleAction.SKILL));
        ultimateBtn.setOnAction(e -> executeAction(BattleManager.BattleAction.ULTIMATE));
        potionBtn.setOnAction(e -> executeAction(BattleManager.BattleAction.POTION));

        HBox row1 = new HBox(12, attackBtn, skillBtn);
        HBox row2 = new HBox(12, ultimateBtn, potionBtn);
        row1.setAlignment(Pos.CENTER);
        row2.setAlignment(Pos.CENTER);
        actionButtons.getChildren().addAll(row1, row2);
    }

    private Button styledButton(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("action-button");
        btn.setPrefWidth(150);
        return btn;
    }

    private void executeAction(BattleManager.BattleAction action) {
        if (battleEnded) return;
        activeCharacter = player.getEquippedCharacter();
        if (activeCharacter == null) return;

        BattleManager.BattleResult result = battleManager.executeAction(activeCharacter, action);
        showDamagePopup(battleManager.getLastDamageDealt(), battleManager.isLastWasCritical());
        updateUI();

        if (result == BattleManager.BattleResult.PLAYER_WIN) {
            handleVictory();
            return;
        }

        PauseTransition delay = new PauseTransition(Duration.seconds(0.6));
        delay.setOnFinished(e -> {
            BattleManager.BattleResult enemyResult = battleManager.executeEnemyTurn();
            updateUI();
            if (enemyResult == BattleManager.BattleResult.ENEMY_WIN) {
                handleDefeat();
            }
        });
        delay.play();
    }

    private void handleVictory() {
        battleEnded = true;
        actionButtons.setVisible(false);

        long gold = battleManager.getTotalRewardGold();
        long exp = battleManager.getTotalRewardExp();

        player.addGold(gold);
        player.addExp(exp);
        if (activeCharacter != null) activeCharacter.addExp(exp);
        questManager.updateProgress("KILL", 1);
        questManager.updateProgress("STAGE", 1);
        questManager.updateProgress("GOLD", (int) gold);

        EventManager em = EventManager.getInstance();
        em.updateProgress("STAGE", 1);
        em.updateProgress("BATTLE_WIN", 1);
        em.updateProgress("GOLD", (int) gold);
        if (activeCharacter != null) em.updateProgress("POWER", (int) activeCharacter.getAttack());
        em.updateProgress("PLAYER_LEVEL", player.getLevel());

        int nextStage = player.getCurrentStage() + 1;
        if (nextStage > 10) {
            player.setCurrentChapter(player.getCurrentChapter() + 1);
            player.setCurrentStage(1);
        } else {
            player.setCurrentStage(nextStage);
        }

        battleResultBox.getChildren().clear();
        Label resultLabel = new Label("\uD83C\uDFC6 VICTORY! \uD83C\uDFC6");
        resultLabel.getStyleClass().add("victory-label");

        HBox rewardRow = new HBox(16);
        rewardRow.setAlignment(Pos.CENTER);
        Label goldR = new Label("\uD83D\uDCB0+" + Constants.formatNumber(gold));
        goldR.getStyleClass().add("reward-badge-gold");
        Label expR = new Label("\u2B50+" + Constants.formatNumber(exp) + " EXP");
        expR.getStyleClass().add("reward-badge-exp");
        rewardRow.getChildren().addAll(goldR, expR);

        Button continueBtn = new Button("\u2694 Next Stage");
        continueBtn.getStyleClass().add("primary-button");
        continueBtn.setOnAction(e -> {
            battleEnded = false;
            actionButtons.setVisible(true);
            battleResultBox.setVisible(false);
            startNewBattle(player.getCurrentChapter(), player.getCurrentStage());
        });

        Button homeBtn = new Button("\uD83C\uDFE0 Home");
        homeBtn.getStyleClass().add("action-button");
        homeBtn.setOnAction(e -> navigator.accept(SceneType.HOME));

        battleResultBox.getChildren().addAll(resultLabel, rewardRow, continueBtn, homeBtn);
        battleResultBox.setVisible(true);
    }

    private void handleDefeat() {
        battleEnded = true;
        actionButtons.setVisible(false);
        battleResultBox.getChildren().clear();

        Label resultLabel = new Label("\u2620 DEFEAT \u2620");
        resultLabel.getStyleClass().add("defeat-label");

        Button retryBtn = new Button("\uD83D\uDD04 Retry");
        retryBtn.getStyleClass().add("primary-button");
        retryBtn.setOnAction(e -> {
            battleEnded = false;
            actionButtons.setVisible(true);
            battleResultBox.setVisible(false);
            startNewBattle(player.getCurrentChapter(), player.getCurrentStage());
        });

        Button homeBtn = new Button("\uD83C\uDFE0 Home");
        homeBtn.getStyleClass().add("action-button");
        homeBtn.setOnAction(e -> navigator.accept(SceneType.HOME));

        battleResultBox.getChildren().addAll(resultLabel, retryBtn, homeBtn);
        battleResultBox.setVisible(true);
    }

    public void startNewBattle(int chapter, int stage) {
        activeCharacter = player.getEquippedCharacter();
        if (activeCharacter == null) return;
        var enemies = Constants.createStageEnemies(chapter, stage);
        battleManager.startBattle(Arrays.asList(activeCharacter), enemies);
        updateUI();
        battleLog.clear();
        battleLog.appendText("Chapter " + chapter + " - Stage " + stage + "\n");
        for (String log : battleManager.getBattleLog()) {
            battleLog.appendText(log + "\n");
        }
    }

    private void updateUI() {
        var enemy = battleManager.getCurrentEnemy();
        if (enemy != null) {
            enemyNameLabel.setText(enemy.getName() + " (Lv." + enemy.getLevel() + ")");
            enemyHpBar.setProgress((double) enemy.getHp() / enemy.getMaxHp());
            enemyHpLabel.setText(Constants.formatNumber(enemy.getHp()) + " / " + Constants.formatNumber(enemy.getMaxHp()));
        }
        if (activeCharacter != null) {
            charNameLabel.setText(activeCharacter.getName() + " (Lv." + activeCharacter.getLevel() + ")");
            charHpBar.setProgress((double) activeCharacter.getHp() / activeCharacter.getMaxHp());
            charHpLabel.setText(activeCharacter.getHp() + " / " + activeCharacter.getMaxHp());
        }

        turnIndicator.getStyleClass().removeAll("turn-indicator-player", "turn-indicator-enemy");
        if (battleManager.isPlayerTurn()) {
            turnIndicator.setText("\u2694 Your Turn");
            turnIndicator.getStyleClass().add("turn-indicator-player");
        } else {
            turnIndicator.setText("\uD83D\uDC7E Enemy's Turn...");
            turnIndicator.getStyleClass().add("turn-indicator-enemy");
        }
        actionButtons.setDisable(!battleManager.isPlayerTurn());

        var log = battleManager.getBattleLog();
        if (!log.isEmpty()) {
            String last = log.get(log.size() - 1);
            if (!battleLog.getText().contains(last)) {
                battleLog.appendText(last + "\n");
            }
        }
    }

    private void showDamagePopup(long damage, boolean critical) {
        if (damage <= 0) return;
        damagePopLabel.setText((critical ? "\u26A1 " : "") + damage);
        damagePopLabel.getStyleClass().removeAll("damage-popup", "critical-popup");
        damagePopLabel.getStyleClass().add(critical ? "critical-popup" : "damage-popup");
        damagePopLabel.setVisible(true);

        FadeTransition fade = new FadeTransition(Duration.seconds(1), damagePopLabel);
        fade.setFromValue(1.0); fade.setToValue(0.0);
        damagePopLabel.setTranslateY(0);
        fade.play();

        TranslateTransition move = new TranslateTransition(Duration.seconds(0.8), damagePopLabel);
        move.setByY(-50);
        move.play();
        move.setOnFinished(e -> { damagePopLabel.setVisible(false); });
    }
}
