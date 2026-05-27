package com.feyydev.components;

import com.feyydev.managers.CharacterManager;
import com.feyydev.models.GameCharacter;
import com.feyydev.utils.Constants;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class CharacterDetailPanel extends VBox {
    private final GameCharacter character;
    private final Runnable onRefresh;

    private final StackPane portraitWrap;
    private final Label charEmoji;
    private final DropShadow glow;

    private final Label rarityLabel;
    private final Label nameLabel;
    private final Label roleLabel;
    private final Label powerLabel;

    private final Label hpVal;
    private final Label atkVal;
    private final Label defVal;
    private final Label critVal;
    private final Label cdmgVal;
    private final Label spdVal;

    public CharacterDetailPanel(GameCharacter character, Runnable onRefresh) {
        this.character = character;
        this.onRefresh = onRefresh;
        setAlignment(Pos.CENTER);
        setPadding(new Insets(10, 40, 10, 40));

        portraitWrap = new StackPane();
        portraitWrap.setAlignment(Pos.CENTER);
        portraitWrap.setPrefSize(280, 320);

        charEmoji = new Label();
        charEmoji.setFont(Font.font(200));
        charEmoji.setAlignment(Pos.CENTER);

        glow = new DropShadow();
        glow.setRadius(45);
        glow.setSpread(0.25);
        charEmoji.setEffect(glow);

        portraitWrap.getChildren().add(charEmoji);

        rarityLabel = new Label();
        nameLabel = new Label();
        roleLabel = new Label();
        powerLabel = new Label();

        hpVal = new Label();
        atkVal = new Label();
        defVal = new Label();
        critVal = new Label();
        cdmgVal = new Label();
        spdVal = new Label();

        VBox infoCard = buildInfoCard();
        HBox buttons = buildButtons();

        getChildren().addAll(portraitWrap, infoCard, buttons);
        updateDisplay();
        startAnimations();
    }

    private void startAnimations() {
        Timeline pulseGlow = new Timeline(
            new KeyFrame(Duration.ZERO, e -> {
                glow.setRadius(40);
                glow.setColor(Color.web("#3b82f6", 0.35));
            }),
            new KeyFrame(Duration.seconds(2), e -> {
                glow.setRadius(55);
                glow.setColor(Color.web("#a855f7", 0.3));
            }),
            new KeyFrame(Duration.seconds(4), e -> {
                glow.setRadius(40);
                glow.setColor(Color.web("#3b82f6", 0.35));
            })
        );
        pulseGlow.setCycleCount(Timeline.INDEFINITE);
        pulseGlow.play();

        TranslateTransition floatAnim = new TranslateTransition(Duration.seconds(3), portraitWrap);
        floatAnim.setToY(-8);
        floatAnim.setAutoReverse(true);
        floatAnim.setCycleCount(Timeline.INDEFINITE);
        floatAnim.setInterpolator(Interpolator.EASE_BOTH);
        floatAnim.play();
    }

    private void updateDisplay() {
        String rc = Constants.getRarityColor(character.getRarity());

        String iconStr = Constants.getCharIcon(character.getName());
        charEmoji.setText(iconStr);
        glow.setColor(Color.web(rcToHex(rc), 0.35));

        rarityLabel.setText(character.getRarity());
        rarityLabel.getStyleClass().removeAll("char-rarity-ssr", "char-rarity-sr", "char-rarity-r");
        rarityLabel.getStyleClass().add("char-rarity-" + character.getRarity().toLowerCase());

        nameLabel.setText(character.getName());
        nameLabel.getStyleClass().add("char-name");

        roleLabel.setText(character.getCategory());
        roleLabel.setStyle("-fx-text-fill: " + Constants.getCategoryColor(character.getCategory()) + ";");

        long power = calcPower();
        powerLabel.setText("Power: " + Constants.formatNumber(power));

        hpVal.setText(Constants.formatNumber(character.getMaxHp()));
        atkVal.setText(Constants.formatNumber(character.getAttack()));
        defVal.setText(Constants.formatNumber(character.getDefense()));
        critVal.setText((int)(character.getCriticalChance() * 100) + "%");
        cdmgVal.setText((int)(character.getCriticalDamage() * 100) + "%");
        spdVal.setText(String.valueOf(character.getSpeed()));
    }

    private long calcPower() {
        return character.getAttack() * 30 + character.getDefense() * 20
            + character.getMaxHp() * 2 + character.getLevel() * 100L;
    }

    private String rcToHex(String cssColor) {
        if (cssColor.contains("#")) return cssColor.substring(cssColor.indexOf("#"), cssColor.indexOf("#") + 7);
        return "#3b82f6";
    }

    private VBox buildInfoCard() {
        VBox card = new VBox(0);
        card.setMaxWidth(460);
        card.getStyleClass().add("char-info-card");

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(16, 20, 12, 20));
        header.getStyleClass().add("char-info-header");

        VBox leftInfo = new VBox(4);
        leftInfo.setAlignment(Pos.CENTER_LEFT);
        HBox rarityRole = new HBox(8);
        rarityRole.setAlignment(Pos.CENTER_LEFT);
        rarityRole.getChildren().addAll(rarityLabel, roleLabel);
        leftInfo.getChildren().addAll(nameLabel, rarityRole);

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        VBox rightPower = new VBox(2);
        rightPower.setAlignment(Pos.CENTER_RIGHT);
        Label pTitle = new Label("POWER SCORE");
        pTitle.getStyleClass().add("char-power-title");
        rightPower.getChildren().addAll(powerLabel, pTitle);
        powerLabel.getStyleClass().add("char-power-value");

        header.getChildren().addAll(leftInfo, sp, rightPower);

        GridPane stats = new GridPane();
        stats.setHgap(24);
        stats.setVgap(8);
        stats.setPadding(new Insets(14, 20, 16, 20));
        stats.getStyleClass().add("char-stats-grid");

        addStatRow(stats, 0, "\u2764", "HP", hpVal, "hp");
        addStatRow(stats, 1, "\u2694", "ATK", atkVal, "atk");
        addStatRow(stats, 2, "\uD83D\uDEE1", "DEF", defVal, "def");
        addStatRow(stats, 0, "\uD83C\uDFAF", "CRIT", critVal, "crit");
        addStatRow(stats, 1, "\uD83D\uDD25", "CDMG", cdmgVal, "cdmg");
        addStatRow(stats, 2, "\u26A1", "SPD", spdVal, "spd");

        HBox footer = new HBox(16);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(10, 20, 14, 20));
        footer.getStyleClass().add("char-footer");

        Label lvlLabel = new Label("Lv." + character.getLevel() + "/" + character.getMaxLevel());
        lvlLabel.getStyleClass().add("char-footer-label");

        String awStr = character.getAwakeningLevel() > 0 ? "\u2B50+" + character.getAwakeningLevel() : "";
        Label awLabel = new Label(awStr);
        awLabel.getStyleClass().add("char-footer-awaken");

        String evStr = character.getEvolutionLevel() > 0 ? "\uD83E\uDEE0+" + character.getEvolutionLevel() : "";
        Label evLabel = new Label(evStr);
        evLabel.getStyleClass().add("char-footer-evolve");

        String skStr = "Skill Lv." + character.getSkillLevel();
        Label skLabel = new Label(skStr);
        skLabel.getStyleClass().add("char-footer-skill");

        footer.getChildren().addAll(lvlLabel, awLabel, evLabel, skLabel);

        card.getChildren().addAll(header, stats, footer);
        return card;
    }

    private void addStatRow(GridPane grid, int col, String icon, String label, Label value, String statKey) {
        HBox row = new HBox(6);
        row.setAlignment(Pos.CENTER_LEFT);
        Label iconLbl = new Label(icon);
        iconLbl.getStyleClass().add("stat-icon");
        Label nameLbl = new Label(label);
        nameLbl.getStyleClass().add("stat-label");
        row.getChildren().addAll(iconLbl, nameLbl);

        value.getStyleClass().add("stat-value-" + statKey);

        HBox valRow = new HBox(6);
        valRow.setAlignment(Pos.CENTER_RIGHT);
        valRow.getChildren().add(value);

        VBox colBox = new VBox(2);
        colBox.getChildren().addAll(row, valRow);

        grid.add(colBox, col, grid.getRowCount());
    }

    private HBox buildButtons() {
        HBox btnBox = new HBox(10);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.setPadding(new Insets(4, 0, 0, 0));

        Button upgradeBtn = new Button("\u2B06 Upgrade");
        upgradeBtn.getStyleClass().addAll("char-action-btn", "char-btn-upgrade");
        upgradeBtn.setOnAction(e -> {
            CharacterManager cm = CharacterManager.getInstance();
            if (cm.addExp(character.getId(), 100)) {
                if (onRefresh != null) onRefresh.run();
            }
        });

        Button equipBtn = new Button("\uD83D\uDEE1 Equip");
        equipBtn.getStyleClass().addAll("char-action-btn", "char-btn-equip");

        Button skillsBtn = new Button("\u2728 Skills");
        skillsBtn.getStyleClass().addAll("char-action-btn", "char-btn-skills");
        skillsBtn.setOnAction(e -> {
            CharacterManager cm = CharacterManager.getInstance();
            if (cm.upgradeSkill(character.getId())) {
                if (onRefresh != null) onRefresh.run();
            }
        });

        Button awakenBtn = new Button("\u2B50 Awaken");
        awakenBtn.getStyleClass().addAll("char-action-btn", "char-btn-awaken");
        awakenBtn.setOnAction(e -> {
            CharacterManager cm = CharacterManager.getInstance();
            if (cm.awaken(character.getId())) {
                if (onRefresh != null) onRefresh.run();
            }
        });

        btnBox.getChildren().addAll(upgradeBtn, equipBtn, skillsBtn, awakenBtn);
        return btnBox;
    }
}
