package com.feyydev.scenes;

import com.feyydev.Main.SceneType;
import com.feyydev.managers.AttendanceManager;
import com.feyydev.models.AttendanceReward;
import com.feyydev.models.Player;
import com.feyydev.utils.Constants;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import java.util.function.Consumer;

public class AttendanceScene {
    private final Scene scene;
    private final Player player;
    private final Consumer<SceneType> navigator;
    private final AttendanceManager attendanceManager;
    private final GridPane rewardGrid;
    private final Label dayLabel;
    private final Button claimBtn;

    public AttendanceScene(Player player, Consumer<SceneType> navigator) {
        this.player = player;
        this.navigator = navigator;
        this.attendanceManager = AttendanceManager.getInstance();
        this.rewardGrid = new GridPane();
        this.dayLabel = new Label();
        this.claimBtn = new Button();
        this.scene = buildScene();
    }

    public Scene getScene() { return scene; }
    public void refresh() { populateGrid(); }

    private Scene buildScene() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");
        root.setPadding(new Insets(16));

        HBox topBar = new HBox(12);
        topBar.setAlignment(Pos.CENTER_LEFT);
        Button backBtn = new Button("\u2190 Back");
        backBtn.getStyleClass().add("back-button");
        backBtn.setOnAction(e -> navigator.accept(SceneType.HOME));
        Label title = new Label("\uD83D\uDCC5 28-Day Attendance");
        title.getStyleClass().add("scene-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        dayLabel.setStyle("-fx-text-fill: #93c5fd; -fx-font-size: 14px; -fx-font-weight: bold;");

        claimBtn.getStyleClass().add("primary-button");
        claimBtn.setOnAction(e -> {
            if (attendanceManager.claimReward()) {
                showToast("Attendance reward claimed! Day " + attendanceManager.getCurrentDay());
                populateGrid();
            } else {
                showToast("Already claimed today's reward or all rewards claimed");
            }
        });

        topBar.getChildren().addAll(backBtn, title, spacer, dayLabel, claimBtn);

        rewardGrid.setHgap(10);
        rewardGrid.setVgap(10);
        rewardGrid.setPadding(new Insets(12));
        rewardGrid.setAlignment(Pos.CENTER);

        ScrollPane scroll = new ScrollPane(rewardGrid);
        scroll.getStyleClass().add("scroll-pane");
        scroll.setFitToWidth(true);

        root.setTop(topBar);
        root.setCenter(scroll);

        Scene s = new Scene(root, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        s.getStylesheets().add(getClass().getResource("/com/feyydev/style.css").toExternalForm());
        return s;
    }

    private void populateGrid() {
        rewardGrid.getChildren().clear();
        int currentDay = player.getAttendanceDay();
        dayLabel.setText("Day " + attendanceManager.getCurrentDay() + "/28");

        claimBtn.setText(currentDay < 28 ? "\uD83C\uDFC6 Claim Day " + (currentDay + 1) : "\u2705 Complete");
        claimBtn.setDisable(currentDay >= 28);

        for (int i = 0; i < attendanceManager.getTotalDays(); i++) {
            AttendanceReward reward = attendanceManager.getRewards().get(i);
            VBox dayCard = new VBox(6);
            dayCard.setAlignment(Pos.CENTER);
            dayCard.getStyleClass().add("attendance-day");

            if (i < currentDay) dayCard.getStyleClass().add("claimed");
            else if (i == currentDay) dayCard.getStyleClass().add("current");
            else dayCard.getStyleClass().add("locked");

            if (reward.isSpecial()) dayCard.getStyleClass().add("special");

            Label dayNum = new Label("Day " + (i + 1));
            dayNum.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px; -fx-font-weight: bold;");

            String icon = switch (reward.getRewardType()) {
                case "GOLD" -> "\uD83D\uDCB0";
                case "GEMS" -> "\uD83D\uDC8E";
                case "ENERGY" -> "\u26A1";
                case "ITEM" -> "\uD83E\uDDEA";
                case "TICKET" -> "\uD83C\uDF81";
                case "CHARACTER" -> "\uD83D\uDC64";
                default -> "\uD83C\uDFC6";
            };
            Label iconLbl = new Label(icon);
            iconLbl.setFont(Font.font(22));

            Label rewardLabel;
            if (reward.getRewardType().equals("CHARACTER")) {
                rewardLabel = new Label(reward.getRewardName());
            } else {
                rewardLabel = new Label("+" + Constants.formatNumber(reward.getRewardAmount()));
            }
            rewardLabel.setStyle("-fx-text-fill: #334155; -fx-font-size: 12px; -fx-font-weight: bold;");

            Label nameLbl = new Label(reward.getRewardName());
            nameLbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 10px;");

            if (reward.isSpecial()) {
                String rc = reward.getRarity().equals("SSR") ? "#FFD700" : "#C084FC";
                dayCard.setStyle(dayCard.getStyle() + "-fx-border-color: " + rc + "66;");
            }

            if (i < currentDay) {
                Label check = new Label("\u2705");
                check.setStyle("-fx-text-fill: #10b981;");
                dayCard.getChildren().addAll(dayNum, iconLbl, rewardLabel, nameLbl, check);
            } else {
                dayCard.getChildren().addAll(dayNum, iconLbl, rewardLabel, nameLbl);
            }

            int col = i % 7;
            int row = i / 7;
            rewardGrid.add(dayCard, col, row);
        }
    }

    private void showToast(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null); a.setContentText(msg);
        a.getDialogPane().setStyle("-fx-background-color: #1e293b; -fx-text-fill: #fff;");
        a.showAndWait();
    }
}
