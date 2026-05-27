package com.feyydev.scenes;

import com.feyydev.Main.SceneType;
import com.feyydev.models.Player;
import com.feyydev.utils.Constants;
import com.feyydev.utils.Constants.StoryDialogue;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import java.util.List;
import java.util.function.Consumer;

public class StoryScene {
    private final Scene scene;
    private final Player player;
    private final Consumer<SceneType> navigator;
    private final StackPane root;
    private final VBox chapterPanel;
    private final VBox dialoguePanel;
    private final Label dialogueText;
    private final Label speakerLabel;
    private final Label speakerIcon;
    private final Label chapterTitleLabel;
    private final Label progressLabel;
    private final Button nextBtn;
    private int currentChapter;
    private int currentDialogueIndex;
    private List<StoryDialogue> currentDialogues;
    private boolean autoMode;

    public StoryScene(Player player, Consumer<SceneType> navigator) {
        this.player = player;
        this.navigator = navigator;
        this.chapterPanel = new VBox(12);
        this.dialoguePanel = new VBox(16);
        this.dialogueText = new Label();
        this.speakerLabel = new Label();
        this.speakerIcon = new Label();
        this.chapterTitleLabel = new Label();
        this.progressLabel = new Label();
        this.nextBtn = new Button("\u25B6 Next");
        this.root = new StackPane();
        this.scene = buildScene();
    }

    public Scene getScene() { return scene; }

    public void refresh() {
        showChapterSelect();
    }

    private Scene buildScene() {
        root.getStyleClass().add("root");

        chapterPanel.setAlignment(Pos.TOP_CENTER);
        chapterPanel.setPadding(new Insets(20));

        showChapterSelect();

        dialoguePanel.setAlignment(Pos.BOTTOM_CENTER);
        dialoguePanel.setPadding(new Insets(20));
        dialoguePanel.setVisible(false);

        root.getChildren().addAll(chapterPanel, dialoguePanel);

        Scene s = new Scene(root, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        s.getStylesheets().add(getClass().getResource("/com/feyydev/style.css").toExternalForm());
        return s;
    }

    private void showChapterSelect() {
        chapterPanel.setVisible(true);
        dialoguePanel.setVisible(false);

        chapterPanel.getChildren().clear();

        HBox topBar = new HBox(12);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(0, 0, 8, 0));
        Button backBtn = new Button("\u2190 Back");
        backBtn.getStyleClass().add("back-button");
        backBtn.setOnAction(e -> navigator.accept(SceneType.HOME));
        Label title = new Label("\uD83D\uDCD6 Story");
        title.getStyleClass().add("scene-title");
        topBar.getChildren().addAll(backBtn, title);

        Label subtitle = new Label("Relive the epic tale of the Legends Of Tournament");
        subtitle.getStyleClass().add("scene-subtitle");
        subtitle.setPadding(new Insets(0, 0, 12, 0));

        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(14);
        grid.setAlignment(Pos.CENTER);

        int cols = 3;
        for (int ch = 1; ch <= 5; ch++) {
            VBox card = new VBox(10);
            card.setAlignment(Pos.CENTER);
            card.setPrefSize(180, 160);
            card.getStyleClass().add("story-chapter-card");

            boolean unlocked = Constants.isChapterUnlocked(ch, player.getCurrentChapter());
            boolean completed = player.getLastStoryChapter() >= ch;
            String icon = Constants.getStoryChapterIcon(ch);

            Label iconLbl = new Label(icon);
            iconLbl.setFont(Font.font(36));

            Label chLbl = new Label("Chapter " + ch);
            chLbl.setStyle("-fx-text-fill: #93c5fd; -fx-font-size: 16px; -fx-font-weight: bold;");

            Label nameLbl = new Label(Constants.getStoryChapterTitle(ch));
            nameLbl.setStyle("-fx-text-fill: #e2e8f0; -fx-font-size: 13px;");

            if (!unlocked) {
                card.setStyle("-fx-background-color: rgba(255,255,255,0.02); -fx-background-radius: 14; -fx-border-color: rgba(59,130,246,0.08); -fx-border-radius: 14;");
                iconLbl.setStyle("-fx-opacity: 0.3;");
                chLbl.setStyle("-fx-text-fill: #64748b; -fx-font-size: 16px; -fx-font-weight: bold;");
                nameLbl.setStyle("-fx-text-fill: #475569; -fx-font-size: 13px;");
                Label locked = new Label("\uD83D\uDD12 Locked");
                locked.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");
                card.getChildren().addAll(iconLbl, chLbl, nameLbl, locked);
            } else {
                String bg = Constants.getStoryBackground(ch);
                card.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 14; -fx-border-color: rgba(59,130,246,0.2); -fx-border-radius: 14; -fx-cursor: hand;");
                if (completed) {
                    Label check = new Label("\u2705 Read");
                    check.setStyle("-fx-text-fill: #10b981; -fx-font-size: 11px; -fx-font-weight: bold;");
                    card.getChildren().addAll(iconLbl, chLbl, nameLbl, check);
                } else {
                    Label badge = new Label("\uD83D\uDD14 New");
                    badge.setStyle("-fx-text-fill: #fbbf24; -fx-font-size: 11px; -fx-font-weight: bold;");
                    card.getChildren().addAll(iconLbl, chLbl, nameLbl, badge);
                }
                int chNum = ch;
                card.setOnMouseClicked(e -> startChapter(chNum));
                card.setOnMouseEntered(e -> card.setScaleX(1.04));
                card.setOnMouseExited(e -> card.setScaleX(1.0));
            }

            int col = (ch - 1) % cols;
            int row = (ch - 1) / cols;
            grid.add(card, col, row);
        }

        chapterPanel.getChildren().addAll(topBar, subtitle, grid);
    }

    private void startChapter(int chapter) {
        currentChapter = chapter;
        currentDialogueIndex = 0;
        currentDialogues = Constants.getStoryDialogues(chapter);
        autoMode = false;

        chapterPanel.setVisible(false);
        dialoguePanel.setVisible(true);

        buildDialogueUI();
        showDialogue();
    }

    private void buildDialogueUI() {
        dialoguePanel.getChildren().clear();

        VBox content = new VBox(12);
        content.setAlignment(Pos.CENTER);
        VBox.setVgrow(content, Priority.ALWAYS);

        chapterTitleLabel.getStyleClass().add("scene-title");
        chapterTitleLabel.setText("\uD83D\uDCD6 Chapter " + currentChapter + ": " + Constants.getStoryChapterTitle(currentChapter));

        progressLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");

        VBox dialogueBox = new VBox(8);
        dialogueBox.setAlignment(Pos.CENTER);
        dialogueBox.setPrefHeight(300);
        dialogueBox.setMaxWidth(650);
        dialogueBox.setPadding(new Insets(30));

        HBox speakerRow = new HBox(10);
        speakerRow.setAlignment(Pos.CENTER);

        speakerIcon.setFont(Font.font(42));

        speakerLabel.setStyle("-fx-text-fill: #93c5fd; -fx-font-size: 18px; -fx-font-weight: bold;");
        speakerLabel.setTextAlignment(TextAlignment.CENTER);

        speakerRow.getChildren().addAll(speakerIcon, speakerLabel);

        dialogueText.setStyle("-fx-text-fill: #e2e8f0; -fx-font-size: 15px;");
        dialogueText.setWrapText(true);
        dialogueText.setTextAlignment(TextAlignment.CENTER);
        dialogueText.setMaxWidth(600);
        dialogueText.setAlignment(Pos.CENTER);

        dialogueBox.getChildren().addAll(speakerRow, dialogueText);

        HBox controls = new HBox(12);
        controls.setAlignment(Pos.CENTER);
        controls.setPadding(new Insets(8));

        Button skipBtn = new Button("\u23E9 Skip");
        skipBtn.getStyleClass().add("action-button");
        skipBtn.setOnAction(e -> skipChapter());

        Button autoBtn = new Button("\u25B6\uFE0F Auto");
        autoBtn.getStyleClass().add("action-button");
        autoBtn.setOnAction(e -> toggleAuto());

        nextBtn.getStyleClass().add("primary-button");
        nextBtn.setOnAction(e -> nextDialogue());

        controls.getChildren().addAll(skipBtn, autoBtn, nextBtn);

        Button backBtn = new Button("\u2190 Chapter Select");
        backBtn.getStyleClass().add("back-button");
        backBtn.setOnAction(e -> {
            autoMode = false;
            showChapterSelect();
        });

        content.getChildren().addAll(chapterTitleLabel, progressLabel, dialogueBox, controls, backBtn);

        dialoguePanel.getChildren().add(content);
    }

    private void showDialogue() {
        if (currentDialogues == null || currentDialogueIndex >= currentDialogues.size()) {
            chapterComplete();
            return;
        }

        StoryDialogue d = currentDialogues.get(currentDialogueIndex);
        speakerIcon.setText(d.icon());
        speakerLabel.setText(d.speaker());
        dialogueText.setText(d.text());
        progressLabel.setText("Dialogue " + (currentDialogueIndex + 1) + " / " + currentDialogues.size());

        String bg = Constants.getStoryBackground(currentChapter);
        dialoguePanel.setStyle("-fx-background-color: " + bg + ";");

        speakerIcon.setOpacity(0);
        speakerLabel.setOpacity(0);
        dialogueText.setOpacity(0);

        FadeTransition fi = new FadeTransition(Duration.millis(400), speakerIcon);
        fi.setToValue(1.0);
        fi.play();

        FadeTransition fn = new FadeTransition(Duration.millis(500), speakerLabel);
        fn.setToValue(1.0);
        fn.play();

        FadeTransition ft = new FadeTransition(Duration.millis(600), dialogueText);
        ft.setToValue(1.0);
        ft.play();

        if (autoMode) {
            PauseTransition pause = new PauseTransition(Duration.seconds(3));
            pause.setOnFinished(e -> nextDialogue());
            pause.play();
        }

        nextBtn.setText(currentDialogueIndex < currentDialogues.size() - 1 ? "\u25B6 Next" : "\u2705 Finish");
    }

    private void nextDialogue() {
        currentDialogueIndex++;
        showDialogue();
    }

    private void skipChapter() {
        currentDialogueIndex = currentDialogues.size();
        chapterComplete();
    }

    private void toggleAuto() {
        autoMode = !autoMode;
        if (autoMode) {
            PauseTransition pause = new PauseTransition(Duration.seconds(3));
            pause.setOnFinished(e -> { if (autoMode) nextDialogue(); });
            pause.play();
        }
    }

    private void chapterComplete() {
        dialoguePanel.getChildren().clear();

        boolean firstTime = player.getLastStoryChapter() < currentChapter;
        if (firstTime) {
            player.setLastStoryChapter(currentChapter);
            long reward = currentChapter * 1000L;
            player.addGold(reward);
            player.addGems(currentChapter * 100L);
            player.addExp(currentChapter * 500L);
        }

        VBox completeBox = new VBox(16);
        completeBox.setAlignment(Pos.CENTER);
        completeBox.setPadding(new Insets(40));
        completeBox.setPrefHeight(350);

        Label doneIcon = new Label("\u2728");
        doneIcon.setFont(Font.font(56));

        Label title = new Label("Chapter " + currentChapter + " Complete!");
        title.setStyle("-fx-text-fill: #fbbf24; -fx-font-size: 24px; -fx-font-weight: bold;");

        Label sub = new Label("You've witnessed the story of " + Constants.getStoryChapterTitle(currentChapter));
        sub.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 14px;");

        VBox rewardBox = new VBox(6);
        rewardBox.setAlignment(Pos.CENTER);
        rewardBox.setPadding(new Insets(12, 24, 12, 24));
        rewardBox.getStyleClass().add("glass-card");
        if (firstTime) {
            Label r1 = new Label("\uD83D\uDCB0 +" + Constants.formatNumber(currentChapter * 1000L) + " Gold");
            r1.setStyle("-fx-text-fill: #fbbf24; -fx-font-size: 14px;");
            Label r2 = new Label("\uD83D\uDC8E +" + (currentChapter * 100L) + " Gems");
            r2.setStyle("-fx-text-fill: #a855f7; -fx-font-size: 14px;");
            Label r3 = new Label("\u2B50 +" + Constants.formatNumber(currentChapter * 500L) + " Exp");
            r3.setStyle("-fx-text-fill: #93c5fd; -fx-font-size: 14px;");
            rewardBox.getChildren().addAll(r1, r2, r3);
        } else {
            Label r0 = new Label("You've already read this chapter.");
            r0.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px;");
            rewardBox.getChildren().add(r0);
        }

        Button nextChapterBtn = new Button("\u25B6 Next Chapter");
        nextChapterBtn.getStyleClass().add("primary-button");
        nextChapterBtn.setDisable(currentChapter >= 5 || !Constants.isChapterUnlocked(currentChapter + 1, player.getCurrentChapter()));
        int nextCh = currentChapter + 1;
        nextChapterBtn.setOnAction(e -> startChapter(nextCh));

        Button backBtn = new Button("\u2190 Chapter Select");
        backBtn.getStyleClass().add("action-button");
        backBtn.setOnAction(e -> showChapterSelect());

        HBox btnRow = new HBox(12, nextChapterBtn, backBtn);
        btnRow.setAlignment(Pos.CENTER);

        completeBox.getChildren().addAll(doneIcon, title, sub, rewardBox, btnRow);
        dialoguePanel.getChildren().add(completeBox);
    }
}
