package com.feyydev.scenes;

import com.feyydev.Main.SceneType;
import com.feyydev.managers.EventManager;
import com.feyydev.managers.InventoryManager;
import com.feyydev.models.Player;
import com.feyydev.utils.Constants;
import com.feyydev.utils.Constants.EventMission;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import java.util.List;
import java.util.function.Consumer;

public class EventScene {
    private final Scene scene;
    private final Player player;
    private final Consumer<SceneType> navigator;
    private final VBox missionPanel;
    private final VBox categoryNav;
    private final Label progressLabel;
    private final ProgressBar categoryProgressBar;
    private String activeCategory = "Beginner Event";

    public EventScene(Player player, Consumer<SceneType> navigator) {
        this.player = player;
        this.navigator = navigator;
        this.missionPanel = new VBox(10);
        this.categoryNav = new VBox(6);
        this.progressLabel = new Label();
        this.categoryProgressBar = new ProgressBar(0);
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
            System.out.println("[DEBUG] EventScene Back clicked");
            navigator.accept(SceneType.HOME);
        });
        Label title = new Label("\uD83C\uDF89 Events");
        title.getStyleClass().add("scene-title");
        topBar.getChildren().addAll(backBtn, title);

        categoryNav.setPadding(new Insets(8));
        categoryNav.setPrefWidth(160);
        categoryNav.getStyleClass().add("sidebar");
        for (String cat : Constants.getEventCategories()) {
            Button btn = new Button(cat);
            btn.getStyleClass().add("sidebar-item");
            btn.setPrefWidth(150);
            btn.setOnAction(e -> {
                activeCategory = cat;
                updateCategoryStyles();
                populateMissions();
            });
            if (cat.equals(activeCategory))
                btn.setStyle("-fx-text-fill: #60a5fa; -fx-background-color: rgba(59,130,246,0.08);");
            categoryNav.getChildren().add(btn);
        }

        categoryProgressBar.setPrefWidth(400);
        categoryProgressBar.setPrefHeight(8);
        categoryProgressBar.getStyleClass().add("exp-bar");
        progressLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");

        missionPanel.setAlignment(Pos.TOP_CENTER);
        missionPanel.setPadding(new Insets(8));
        populateMissions();

        ScrollPane scroll = new ScrollPane(missionPanel);
        scroll.getStyleClass().add("scroll-pane");
        scroll.setFitToWidth(true);

        HBox content = new HBox(16, categoryNav, scroll);
        HBox.setHgrow(scroll, Priority.ALWAYS);

        root.setTop(topBar);
        root.setCenter(content);

        Scene s = new Scene(root, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        s.getStylesheets().add(getClass().getResource("/com/feyydev/global.css").toExternalForm());
        return s;
    }

    public void refresh() {
        updateCategoryStyles();
        populateMissions();
    }

    private void updateCategoryStyles() {
        for (var node : categoryNav.getChildren()) {
            if (node instanceof Button btn) {
                btn.setStyle("");
                if (btn.getText().equals(activeCategory))
                    btn.setStyle("-fx-text-fill: #60a5fa; -fx-background-color: rgba(59,130,246,0.08);");
            }
        }
    }

    private void populateMissions() {
        missionPanel.getChildren().clear();

        EventManager em = EventManager.getInstance();

        VBox header = new VBox(4);
        header.setAlignment(Pos.CENTER);
        Label catTitle = new Label(Constants.getEventIcon(activeCategory) + " " + activeCategory);
        catTitle.setStyle("-fx-text-fill: #f1f5f9; -fx-font-size: 18px; -fx-font-weight: bold;");
        Label catDesc = new Label(Constants.getEventDescription(activeCategory));
        catDesc.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");
        header.getChildren().addAll(catTitle, catDesc);

        int completed = em.getCompletedCount(activeCategory);
        int total = em.getTotalCount(activeCategory);
        categoryProgressBar.setProgress(total > 0 ? (double) completed / total : 0);
        progressLabel.setText("Progress: " + completed + "/" + total + " missions completed");

        missionPanel.getChildren().addAll(header, categoryProgressBar, progressLabel);

        List<EventMission> missions = em.getMissionsByCategory(activeCategory);
        for (EventMission m : missions) {
            VBox mCard = new VBox(8);
            mCard.setPadding(new Insets(14));
            mCard.setMaxWidth(520);
            mCard.getStyleClass().add("quest-card");

            int curProg = em.getProgress(m.requirement());
            int target = m.targetCount();
            boolean claimed = em.isMissionClaimed(m.id());
            boolean completable = em.isMissionCompletable(m);

            HBox mRow = new HBox(10);
            mRow.setAlignment(Pos.CENTER_LEFT);

            Label icon = new Label(m.icon());
            icon.setFont(Font.font(20));

            VBox mInfo = new VBox(2);
        Label mName = new Label(m.name());
        mName.setStyle("-fx-text-fill: #e2e8f0; -fx-font-size: 13px; -fx-font-weight: bold;");
            Label mDesc = new Label(m.description());
            mDesc.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");

            ProgressBar progBar = new ProgressBar(target > 0 ? Math.min(1.0, (double) curProg / target) : 0);
            progBar.setPrefWidth(180);
            progBar.setPrefHeight(6);
            progBar.getStyleClass().add("exp-bar");

            Label progText = new Label(curProg + "/" + target);
            progText.setStyle("-fx-text-fill: #64748b; -fx-font-size: 10px;");

            HBox progRow = new HBox(6, progBar, progText);
            progRow.setAlignment(Pos.CENTER_LEFT);
            mInfo.getChildren().addAll(mName, mDesc, progRow);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            VBox rewardBox = new VBox(2);
            rewardBox.setAlignment(Pos.CENTER_RIGHT);
            StringBuilder rewardText = new StringBuilder();
            if (m.rewardGold() > 0) rewardText.append("\uD83D\uDCB0").append(m.rewardGold()).append(" ");
            if (m.rewardGems() > 0) rewardText.append("\uD83D\uDC8E").append(m.rewardGems()).append(" ");
            if (m.rewardExp() > 0) rewardText.append("\u2B50").append(m.rewardExp()).append(" ");
            if (m.rewardTickets() > 0) rewardText.append("\uD83C\uDFABx").append(m.rewardTickets());
            Label reward = new Label(rewardText.toString().trim());
            reward.setStyle("-fx-text-fill: #fbbf24; -fx-font-weight: bold; -fx-font-size: 11px; -fx-background-color: rgba(251,191,36,0.08); -fx-padding: 3 8; -fx-background-radius: 6;");

            Button claimBtn = new Button(claimed ? "\u2713 Claimed" : "Claim");
            if (claimed) {
                claimBtn.setDisable(true);
                claimBtn.setStyle("-fx-background-color: #e2e8f0; -fx-text-fill: #94a3b8;");
            } else if (completable) {
                claimBtn.getStyleClass().add("success-button");
            } else {
                claimBtn.setDisable(true);
                claimBtn.setStyle("-fx-background-color: #e2e8f0; -fx-text-fill: #94a3b8;");
            }

            String missionId = m.id();
            claimBtn.setOnAction(e -> {
                if (EventManager.getInstance().claimMission(missionId)) {
                    populateMissions();
                }
            });

            rewardBox.getChildren().add(reward);
            mRow.getChildren().addAll(icon, mInfo, spacer, rewardBox, claimBtn);
            mCard.getChildren().add(mRow);
            missionPanel.getChildren().add(mCard);
        }
    }
}
