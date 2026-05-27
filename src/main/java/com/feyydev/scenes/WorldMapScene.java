package com.feyydev.scenes;

import com.feyydev.Main.SceneType;
import com.feyydev.managers.BattleManager;
import com.feyydev.managers.QuestManager;
import com.feyydev.models.Enemy;
import com.feyydev.models.GameCharacter;
import com.feyydev.models.Player;
import com.feyydev.utils.Constants;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import java.util.*;
import java.util.function.Consumer;

public class WorldMapScene {
    private final Scene scene;
    private final Player player;
    private final Consumer<SceneType> navigator;
    private final VBox stageList;
    private final Label chapterLabel;
    private int selectedChapter;

    public WorldMapScene(Player player, Consumer<SceneType> navigator) {
        this.player = player;
        this.navigator = navigator;
        this.stageList = new VBox(8);
        this.chapterLabel = new Label();
        this.selectedChapter = Math.max(1, player.getCurrentChapter());
        this.scene = buildScene();
    }

    public Scene getScene() { return scene; }
    public void refresh() { populateStages(); }

    private Scene buildScene() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");
        root.setPadding(new Insets(16));

        HBox topBar = new HBox(12);
        topBar.setAlignment(Pos.CENTER_LEFT);
        Button backBtn = new Button("\u2190 Back");
        backBtn.getStyleClass().add("back-button");
        backBtn.setOnAction(e -> navigator.accept(SceneType.HOME));
        Label title = new Label("\uD83C\uDF0D World Map");
        title.getStyleClass().add("scene-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox chapterNav = new HBox(6);
        chapterNav.setAlignment(Pos.CENTER);
        for (int i = 1; i <= 5; i++) {
            int ch = i;
            Button btn = new Button("Ch." + i);
            btn.getStyleClass().add("small-button");
            if (i == selectedChapter) btn.setStyle("-fx-background-color: rgba(59,130,246,0.2); -fx-text-fill: #60a5fa; -fx-border-color: #3b82f6;");
            btn.setOnAction(e -> {
                selectedChapter = ch;
                updateChapterStyles(chapterNav);
                populateStages();
            });
            chapterNav.getChildren().add(btn);
        }

        topBar.getChildren().addAll(backBtn, title, spacer, chapterNav);

        chapterLabel.setStyle("-fx-text-fill: #fbbf24; -fx-font-size: 18px; -fx-font-weight: bold;");

        stageList.setAlignment(Pos.TOP_CENTER);
        stageList.setPadding(new Insets(12, 0, 0, 0));

        ScrollPane scroll = new ScrollPane(stageList);
        scroll.getStyleClass().add("scroll-pane");
        scroll.setFitToWidth(true);

        root.setTop(topBar);
        root.setCenter(new VBox(10, chapterLabel, scroll));

        Scene s = new Scene(root, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        s.getStylesheets().add(getClass().getResource("/com/feyydev/style.css").toExternalForm());
        return s;
    }

    private void updateChapterStyles(HBox chapterNav) {
        for (var node : chapterNav.getChildren()) {
            if (node instanceof Button btn) {
                btn.setStyle("");
                int ch = Integer.parseInt(btn.getText().substring(3));
                if (ch == selectedChapter) {
                    btn.setStyle("-fx-background-color: rgba(59,130,246,0.2); -fx-text-fill: #60a5fa; -fx-border-color: #3b82f6;");
                }
            }
        }
    }

    private void populateStages() {
        stageList.getChildren().clear();
        String[] chapterNames = {
            "The Awakening", "Dark Alliance", "Tournament Arc", "Celestial War", "Final Stand"
        };
        chapterLabel.setText("Chapter " + selectedChapter + ": " + chapterNames[selectedChapter - 1]);

        for (int stage = 1; stage <= 10; stage++) {
            int s = stage;
            boolean isBoss = stage == 10;
            boolean isElite = stage == 5;
            boolean isCompleted = selectedChapter < player.getCurrentChapter() ||
                (selectedChapter == player.getCurrentChapter() && stage < player.getCurrentStage());
            boolean isCurrent = selectedChapter == player.getCurrentChapter() && stage == player.getCurrentStage();
            boolean isLocked = selectedChapter > player.getCurrentChapter() ||
                (selectedChapter == player.getCurrentChapter() && stage > player.getCurrentStage());

            VBox stageCard = new VBox(6);
            stageCard.setPadding(new Insets(10, 16, 10, 16));
            stageCard.setMaxWidth(600);
            stageCard.getStyleClass().add("map-stage");

            if (isCompleted) stageCard.getStyleClass().add("completed");
            if (isCurrent) stageCard.getStyleClass().add("current");
            if (isLocked) stageCard.getStyleClass().add("locked");
            if (isBoss) stageCard.getStyleClass().add("boss");
            if (isElite) stageCard.getStyleClass().add("elite");

            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);

            String icon = isBoss ? "\uD83D\uDC7E" : isElite ? "\u2B50" : "\u2694";
            Label iconLbl = new Label(icon);
            iconLbl.setFont(Font.font(20));

            VBox info = new VBox(2);
            String stageName = isBoss ? "Boss Stage" : isElite ? "Elite Stage" : "Stage " + stage;
            Label nameLbl = new Label(stageName);
            nameLbl.setStyle("-fx-text-fill: " + (isLocked ? "#64748b" : "#1e293b") + "; -fx-font-size: 14px; -fx-font-weight: bold;");

            String[] stageTypes = {"Training Grounds", "Dark Forest", "Mountain Pass", "Ancient Ruins", "Crystal Cave",
                "Volcanic Ridge", "Temple of Trials", "Abyss Gate", "Heavenly Court", "Throne Room"};
            Label locLbl = new Label(stageTypes[stage - 1]);
            locLbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");
            info.getChildren().addAll(nameLbl, locLbl);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            if (isCompleted) {
                Label done = new Label("\u2705");
                done.setStyle("-fx-text-fill: #10b981; -fx-font-size: 16px;");
                row.getChildren().addAll(iconLbl, info, spacer, done);
            } else if (isLocked) {
                Label lock = new Label("\uD83D\uDD12");
                lock.setFont(Font.font(16));
                row.getChildren().addAll(iconLbl, info, spacer, lock);
            } else {
                Label reward = new Label("\uD83D\uDCB0+" + (10 + stage * 5) + " \u2B50+" + (20 + stage * 8));
                reward.setStyle("-fx-text-fill: #fbbf24; -fx-font-size: 11px;");
                Button fightBtn = new Button("Fight");
                fightBtn.getStyleClass().add("primary-button");
                fightBtn.setOnAction(e -> startBattle(selectedChapter, s));
                row.getChildren().addAll(iconLbl, info, spacer, reward, fightBtn);
            }

            stageCard.getChildren().add(row);
            stageList.getChildren().add(stageCard);
        }
    }

    private void startBattle(int chapter, int stage) {
        GameCharacter chara = player.getEquippedCharacter();
        if (chara == null) {
            showToast("Select a character first!");
            return;
        }
        if (!player.useEnergy(Constants.ENERGY_PER_STAGE)) {
            showToast("Not enough energy!");
            return;
        }
        BattleManager.getInstance().startBattle(
            Arrays.asList(chara),
            Constants.createStageEnemies(chapter, stage)
        );
        player.setCurrentChapter(chapter);
        player.setCurrentStage(stage);
        navigator.accept(SceneType.BATTLE);
    }

    private void showToast(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null); a.setContentText(msg);
        a.getDialogPane().setStyle("-fx-background-color: #1e293b; -fx-text-fill: #fff;");
        a.showAndWait();
    }
}
