package com.feyydev.scenes;

import com.feyydev.Main.SceneType;
import com.feyydev.managers.QuestManager;
import com.feyydev.models.Player;
import com.feyydev.models.Quest;
import com.feyydev.utils.Constants;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import java.util.List;
import java.util.function.Consumer;

public class QuestScene {
    private final Scene scene;
    private final Player player;
    private final Consumer<SceneType> navigator;
    private final QuestManager questManager;
    private final VBox questList;
    private final HBox tabBar;
    private String activeTab = "Daily";

    public QuestScene(Player player, Consumer<SceneType> navigator) {
        this.player = player;
        this.navigator = navigator;
        this.questManager = QuestManager.getInstance();
        this.questList = new VBox(10);
        this.tabBar = new HBox(6);
        this.scene = buildScene();
    }

    public Scene getScene() { return scene; }
    public void refreshQuests() { populateQuests(); }

    private Scene buildScene() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");
        root.setPadding(new Insets(16));

        HBox topBar = new HBox(12);
        topBar.setAlignment(Pos.CENTER_LEFT);
        Button backBtn = new Button("\u2190 Back");
        backBtn.getStyleClass().add("back-button");
        backBtn.setOnAction(e -> navigator.accept(SceneType.HOME));
        Label title = new Label("\uD83D\uDCCB Quests");
        title.getStyleClass().add("scene-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        int unclaimed = questManager.getUnclaimedCount();
        Label unclaimedLbl = new Label(unclaimed > 0 ? unclaimed + " ready!" : "");
        unclaimedLbl.setStyle("-fx-text-fill: #fbbf24; -fx-font-weight: bold;");

        topBar.getChildren().addAll(backBtn, title, spacer, unclaimedLbl);

        String[] tabs = {"Daily", "Weekly", "Achievement"};
        tabBar.setAlignment(Pos.CENTER);
        tabBar.setPadding(new Insets(6, 0, 6, 0));
        for (String tab : tabs) {
            Button btn = new Button(tab);
            btn.getStyleClass().add("tab-button");
            if (tab.equals(activeTab)) btn.getStyleClass().add("selected");
            btn.setOnAction(e -> {
                activeTab = tab;
                updateTabStyles();
                populateQuests();
            });
            tabBar.getChildren().add(btn);
        }

        questList.setAlignment(Pos.TOP_CENTER);
        questList.setPadding(new Insets(8, 0, 0, 0));

        ScrollPane scroll = new ScrollPane(questList);
        scroll.getStyleClass().add("scroll-pane");
        scroll.setFitToWidth(true);

        root.setTop(topBar);
        root.setCenter(new VBox(6, tabBar, scroll));

        Scene s = new Scene(root, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        s.getStylesheets().add(getClass().getResource("/com/feyydev/style.css").toExternalForm());
        return s;
    }

    private void updateTabStyles() {
        for (var node : tabBar.getChildren()) {
            if (node instanceof Button btn) {
                btn.getStyleClass().remove("selected");
                if (btn.getText().equals(activeTab)) btn.getStyleClass().add("selected");
            }
        }
    }

    private void populateQuests() {
        questList.getChildren().clear();
        List<Quest> quests = switch (activeTab) {
            case "Daily" -> questManager.getDailyQuests();
            case "Weekly" -> questManager.getWeeklyQuests();
            case "Achievement" -> questManager.getAchievements();
            default -> questManager.getDailyQuests();
        };

        if (quests.isEmpty()) {
            Label empty = new Label("No quests available");
            empty.getStyleClass().add("hint-label");
            questList.getChildren().add(empty);
            return;
        }

        for (Quest q : quests) {
            VBox card = new VBox(10);
            card.setPadding(new Insets(14));
            card.setPrefWidth(550);
            card.getStyleClass().add("quest-card");

            HBox header = new HBox(10);
            header.setAlignment(Pos.CENTER_LEFT);

            String icon = switch (q.getRequirement()) {
                case "KILL" -> "\u2694";
                case "STAGE" -> "\uD83C\uDF96";
                case "LOGIN" -> "\uD83D\uDC4B";
                case "GOLD" -> "\uD83D\uDCB0";
                case "SUMMON" -> "\uD83C\uDF81";
                case "CHAPTER" -> "\uD83C\uDF0D";
                case "SSR_COUNT" -> "\u2B50";
                case "PLAYER_LEVEL" -> "\uD83D\uDCAA";
                default -> "\uD83D\uDCCB";
            };
            Label iconLbl = new Label(icon);
            iconLbl.setFont(Font.font(22));

            VBox titleBox = new VBox(2);
            Label nameLbl = new Label(q.getName());
            nameLbl.setStyle("-fx-text-fill: #1e293b; -fx-font-size: 14px; -fx-font-weight: bold;");
            Label descLbl = new Label(q.getDescription());
            descLbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px;");
            titleBox.getChildren().addAll(nameLbl, descLbl);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Label statusLbl = new Label();
            if (q.isClaimed()) {
                statusLbl.setText("\u2705");
                statusLbl.setStyle("-fx-text-fill: #10b981; -fx-font-size: 16px;");
            } else if (q.isCompleted()) {
                statusLbl.setText("\u2B50 Claim!");
                statusLbl.setStyle("-fx-text-fill: #fbbf24; -fx-font-weight: bold; -fx-background-color: rgba(251,191,36,0.12); -fx-padding: 3 10; -fx-background-radius: 8; -fx-font-size: 12px;");
            } else {
                statusLbl.setText(q.getProgress() + "/" + q.getTargetCount());
                statusLbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px; -fx-background-color: rgba(255,255,255,0.04); -fx-padding: 3 8; -fx-background-radius: 6;");
            }

            header.getChildren().addAll(iconLbl, titleBox, spacer, statusLbl);

            ProgressBar progressBar = new ProgressBar();
            progressBar.setPrefWidth(520);
            progressBar.setPrefHeight(8);
            double prog = q.getTargetCount() > 0 ? (double) q.getProgress() / q.getTargetCount() : 0;
            progressBar.setProgress(prog);
            progressBar.getStyleClass().add(q.isCompleted() ? "exp-bar" : "progress-bar");

            HBox rewardRow = new HBox(12);
            rewardRow.setAlignment(Pos.CENTER_LEFT);
            if (q.getRewardGold() > 0) {
                Label r = new Label("\uD83D\uDCB0+" + Constants.formatNumber(q.getRewardGold()));
                r.setStyle("-fx-text-fill: #fbbf24; -fx-font-size: 12px; -fx-font-weight: bold; -fx-background-color: rgba(251,191,36,0.08); -fx-padding: 3 8; -fx-background-radius: 6;");
                rewardRow.getChildren().add(r);
            }
            if (q.getRewardGems() > 0) {
                Label r = new Label("\uD83D\uDC8E+" + Constants.formatNumber(q.getRewardGems()));
                r.setStyle("-fx-text-fill: #c084fc; -fx-font-size: 12px; -fx-font-weight: bold; -fx-background-color: rgba(192,132,252,0.08); -fx-padding: 3 8; -fx-background-radius: 6;");
                rewardRow.getChildren().add(r);
            }
            if (q.getRewardExp() > 0) {
                Label r = new Label("\u2B50+" + Constants.formatNumber(q.getRewardExp()) + " EXP");
                r.setStyle("-fx-text-fill: #a855f7; -fx-font-size: 12px; -fx-font-weight: bold; -fx-background-color: rgba(168,85,247,0.08); -fx-padding: 3 8; -fx-background-radius: 6;");
                rewardRow.getChildren().add(r);
            }

            Button claimBtn = new Button("Claim");
            claimBtn.getStyleClass().add("success-button");
            claimBtn.setDisable(!q.isReadyToClaim());
            claimBtn.setOnAction(e -> {
                if (questManager.claimReward(q.getId())) {
                    showToast("Reward claimed! +" + q.getRewardGold() + " Gold");
                    populateQuests();
                }
            });

            card.getChildren().addAll(header, progressBar, rewardRow, claimBtn);
            questList.getChildren().add(card);
        }

        boolean allClaimed = activeTab.equals("Daily") && questManager.allDailyClaimed();
        if (allClaimed) {
            VBox doneBox = new VBox(8);
            doneBox.setAlignment(Pos.CENTER);
            doneBox.setPadding(new Insets(16));
            doneBox.setStyle("-fx-background-color: rgba(16,185,129,0.04); -fx-background-radius: 12; -fx-border-color: rgba(16,185,129,0.15); -fx-border-radius: 12;");
            Label doneLbl = new Label("\uD83C\uDFC6 All daily quests completed!");
            doneLbl.setStyle("-fx-text-fill: #10b981; -fx-font-size: 15px; -fx-font-weight: bold;");
            Label subLbl = new Label("Come back tomorrow for new quests");
            subLbl.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");
            doneBox.getChildren().addAll(doneLbl, subLbl);
            questList.getChildren().add(doneBox);
        }
    }

    private void showToast(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null); a.setContentText(msg);
        a.getDialogPane().setStyle("-fx-background-color: #1e293b; -fx-text-fill: #fff;");
        a.showAndWait();
    }
}
