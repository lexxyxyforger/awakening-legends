package com.feyydev.scenes;

import com.feyydev.Main.SceneType;
import com.feyydev.managers.CharacterManager;
import com.feyydev.managers.EventManager;
import com.feyydev.managers.GachaManager;
import com.feyydev.models.*;
import com.feyydev.utils.Constants;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.*;
import java.util.function.Consumer;

public class GachaScene {
    private final Scene scene;
    private final Player player;
    private final Consumer<SceneType> navigator;
    private final GachaManager gachaManager;
    private final CharacterManager characterManager;
    private final VBox bannerList;
    private final VBox resultArea;
    private final VBox historyArea;
    private final Label pityLabel;
    private SummonBanner selectedBanner;

    public GachaScene(Player player, Consumer<SceneType> navigator) {
        this.player = player;
        this.navigator = navigator;
        this.gachaManager = GachaManager.getInstance();
        this.characterManager = CharacterManager.getInstance();
        this.bannerList = new VBox(10);
        this.resultArea = new VBox(10);
        this.historyArea = new VBox(4);
        this.pityLabel = new Label();
        this.scene = buildScene();
    }

    public Scene getScene() { return scene; }
    public void refresh() { populateBanners(); updatePity(); }

    private Scene buildScene() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");
        root.setPadding(new Insets(16));

        HBox topBar = new HBox(12);
        topBar.setAlignment(Pos.CENTER_LEFT);
        Button backBtn = new Button("\u2190 Back");
        backBtn.getStyleClass().add("back-button");
        backBtn.setOnAction(e -> {
            System.out.println("[DEBUG] GachaScene Back clicked");
            navigator.accept(SceneType.HOME);
        });
        Label title = new Label("\u2728 Summon");
        title.getStyleClass().add("scene-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        pityLabel.setStyle("-fx-text-fill: #93c5fd; -fx-font-size: 13px; -fx-font-weight: bold;");
        topBar.getChildren().addAll(backBtn, title, spacer, pityLabel);

        populateBanners();

        VBox leftPanel = new VBox(8);
        leftPanel.setPrefWidth(300);
        Label bannerTitle = new Label("Select Banner");
        bannerTitle.setStyle("-fx-text-fill: #93c5fd; -fx-font-size: 13px; -fx-font-weight: bold;");
        leftPanel.getChildren().addAll(bannerTitle, bannerList);

        resultArea.setAlignment(Pos.CENTER);
        resultArea.setPadding(new Insets(12));
        resultArea.getStyleClass().add("glass-card");

        Label ratesInfo = new Label(
            "Rates | SSR: " + (Constants.SSR_RATE * 100) + "% | " +
            "SR: " + (Constants.SR_RATE * 100) + "% | " +
            "R: " + Constants.R_RATE * 100 + "%\n" +
            "Pity: SSR @ " + Constants.PITY_SSR + " pulls | SR @ " + Constants.PITY_SR + " pulls"
        );
        ratesInfo.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");
        ratesInfo.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        VBox centerPanel = new VBox(8, resultArea, ratesInfo);
        HBox.setHgrow(centerPanel, Priority.ALWAYS);

        HBox content = new HBox(16, leftPanel, centerPanel);
        HBox.setHgrow(content, Priority.ALWAYS);

        VBox bottomPanel = new VBox(8);
        bottomPanel.setPadding(new Insets(8, 0, 0, 0));
        Label historyTitle = new Label("\uD83D\uDCCB Pull History");
        historyTitle.setStyle("-fx-text-fill: #93c5fd; -fx-font-size: 14px; -fx-font-weight: bold;");
        historyArea.setAlignment(Pos.TOP_CENTER);
        historyArea.setPadding(new Insets(6));
        historyArea.getStyleClass().add("glass-card");
        ScrollPane historyScroll = new ScrollPane(historyArea);
        historyScroll.getStyleClass().add("scroll-pane");
        historyScroll.setFitToWidth(true);
        historyScroll.setPrefHeight(120);
        bottomPanel.getChildren().addAll(historyTitle, historyScroll);

        root.setTop(topBar);
        root.setCenter(content);
        root.setBottom(bottomPanel);

        Scene s = new Scene(root, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        s.getStylesheets().add(getClass().getResource("/com/feyydev/global.css").toExternalForm());
        return s;
    }

    private void populateBanners() {
        bannerList.getChildren().clear();
        for (SummonBanner banner : gachaManager.getBanners()) {
            VBox bannerCard = new VBox(8);
            bannerCard.setPadding(new Insets(14));
            bannerCard.getStyleClass().add("banner-active");
            bannerCard.setPrefWidth(280);

            Label nameLbl = new Label(banner.getName());
            nameLbl.setStyle("-fx-text-fill: #fbbf24; -fx-font-size: 16px; -fx-font-weight: bold;");
            Label descLbl = new Label(banner.getDescription());
            descLbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");
            Label costLbl = new Label("\uD83D\uDC8E " + banner.getCostGems() + " per pull | " + banner.getMultiPullCount() + "x multi");
            costLbl.setStyle("-fx-text-fill: #c084fc; -fx-font-size: 12px; -fx-font-weight: bold;");

            HBox btnRow = new HBox(8);
            btnRow.setAlignment(Pos.CENTER);
            Button singleBtn = new Button("\uD83C\uDF81 x1");
            singleBtn.getStyleClass().add("action-button");
            singleBtn.setOnAction(e -> {
                selectedBanner = banner;
                doPull(1);
            });
            Button multiBtn = new Button("\uD83C\uDF81 x10");
            multiBtn.getStyleClass().add("primary-button");
            multiBtn.setOnAction(e -> {
                selectedBanner = banner;
                doPull(10);
            });
            btnRow.getChildren().addAll(singleBtn, multiBtn);
            bannerCard.getChildren().addAll(nameLbl, descLbl, costLbl, btnRow);
            bannerList.getChildren().add(bannerCard);
        }
    }

    private void doPull(int count) {
        if (selectedBanner == null) return;
        int cost = selectedBanner.getCostGems() * count;
        if (!player.spendGems(cost)) {
            showToast("Not enough gems! Need " + cost);
            return;
        }

        List<GameCharacter> results = gachaManager.multiPull(count, selectedBanner);
        resultArea.getChildren().clear();

        Label pullTitle = new Label("\u2728 Pull Results \u2728");
        pullTitle.setStyle("-fx-text-fill: #fbbf24; -fx-font-size: 18px; -fx-font-weight: bold;");

        FlowPane resultGrid = new FlowPane(8, 8);
        resultGrid.setAlignment(Pos.CENTER);

        for (GameCharacter c : results) {
            String rc = Constants.getRarityColor(c.getRarity());
            VBox card = new VBox(6);
            card.setAlignment(Pos.CENTER);
            card.setPrefSize(100, 110);
            card.getStyleClass().add("result-card");
            card.setStyle("-fx-border-color: " + rc + "55;");

            VBox avatarBg = new VBox();
            avatarBg.setPrefSize(40, 40);
            avatarBg.setStyle("-fx-background-color: " + rc + "22; -fx-background-radius: 20; -fx-border-color: " + rc + "44; -fx-border-radius: 20;");
            avatarBg.setAlignment(Pos.CENTER);
            Label avi = new Label(Constants.getCharIcon(c.getName()));
            avi.setFont(Font.font(18));
            avatarBg.getChildren().add(avi);

            Label badge = new Label(Constants.getRarityBadge(c.getRarity()));
            badge.setStyle("-fx-background-color: " + rc + "; -fx-text-fill: #000; -fx-font-weight: bold; -fx-padding: 1 6; -fx-background-radius: 6; -fx-font-size: 9px;");

            Label nameLbl = new Label(c.getName());
            nameLbl.setStyle("-fx-text-fill: " + rc + "; -fx-font-size: 10px; -fx-font-weight: bold; -fx-text-alignment: center;");

            card.getChildren().addAll(avatarBg, badge, nameLbl);
            resultGrid.getChildren().add(card);
            characterManager.addCharacter(c);

            if ("SSR".equals(c.getRarity())) {
                System.out.println("[Gacha] SSR pulled: " + c.getName());
            }
        }

        EventManager em = EventManager.getInstance();
        em.updateProgress("SUMMON", count);
        em.updateProgress("CHARACTERS", count);
        long ssrCount = results.stream().filter(c -> "SSR".equals(c.getRarity())).count();
        if (ssrCount > 0) em.updateProgress("SSR_COUNT", (int) ssrCount);

        ScrollPane resultScroll = new ScrollPane(resultGrid);
        resultScroll.getStyleClass().add("scroll-pane");
        resultScroll.setFitToWidth(true);
        resultScroll.setPrefHeight(200);

        resultArea.getChildren().addAll(pullTitle, resultScroll);
        updateHistory();
        updatePity();
    }

    private void updateHistory() {
        historyArea.getChildren().clear();
        var history = gachaManager.getPullHistory();
        for (int i = Math.max(0, history.size() - 20); i < history.size(); i++) {
            GameCharacter c = history.get(i);
            String rc = Constants.getRarityColor(c.getRarity());
            HBox entry = new HBox(6);
            entry.setAlignment(Pos.CENTER_LEFT);
            entry.setPadding(new Insets(2, 0, 2, 4));
            Label badge = new Label(Constants.getRarityBadge(c.getRarity()));
            badge.setStyle("-fx-background-color: " + rc + "; -fx-text-fill: #000; -fx-font-weight: bold; -fx-padding: 1 5; -fx-background-radius: 5; -fx-font-size: 9px;");
            Label nameLbl = new Label(c.getName());
            nameLbl.setStyle("-fx-text-fill: " + rc + "; -fx-font-size: 11px;");
            entry.getChildren().addAll(badge, nameLbl);
            historyArea.getChildren().add(entry);
        }
    }

    private void updatePity() {
        String bannerId = selectedBanner != null ? selectedBanner.getId() : "default";
        pityLabel.setText("\uD83D\uDC8E " + player.getGems() + " | Pity: SSR " + gachaManager.getPitySSR(bannerId) + "/" + Constants.PITY_SSR);
    }

    private void showToast(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null); a.setContentText(msg);
        a.getDialogPane().setStyle("-fx-background-color: #1e293b; -fx-text-fill: #fff;");
        a.showAndWait();
    }
}
