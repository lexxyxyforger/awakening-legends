package com.feyydev.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class MissionCard extends VBox {
    private final HBox header;
    private final Label iconLbl;
    private final Label nameLbl;
    private final Label descLbl;
    private final ProgressBar progressBar;
    private final Label progressText;
    private final HBox rewardRow;
    private final Button claimBtn;

    public MissionCard(String icon, String name, String description) {
        this(icon, name, description, 0, 0);
    }

    public MissionCard(String icon, String name, String description, int progress, int target) {
        setPadding(new Insets(14));
        setMaxWidth(550);
        getStyleClass().add("quest-card");

        header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        iconLbl = new Label(icon);
        iconLbl.setFont(Font.font(20));

        VBox info = new VBox(2);
        nameLbl = new Label(name);
        nameLbl.setStyle("-fx-text-fill: #e2e8f0; -fx-font-size: 13px; -fx-font-weight: bold;");
        descLbl = new Label(description);
        descLbl.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");
        info.getChildren().addAll(nameLbl, descLbl);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        progressText = new Label(progress + "/" + target);
        progressText.setStyle("-fx-text-fill: #64748b; -fx-font-size: 10px;");

        header.getChildren().addAll(iconLbl, info, spacer, progressText);

        progressBar = new ProgressBar(target > 0 ? Math.min(1.0, (double) progress / target) : 0);
        progressBar.setPrefWidth(520);
        progressBar.setPrefHeight(8);
        progressBar.getStyleClass().add("progress-bar");

        rewardRow = new HBox(10);
        rewardRow.setAlignment(Pos.CENTER_LEFT);

        claimBtn = new Button("Claim");
        claimBtn.getStyleClass().add("success-button");

        getChildren().addAll(header, progressBar, rewardRow, claimBtn);
    }

    public void setProgress(int current, int target) {
        progressBar.setProgress(target > 0 ? Math.min(1.0, (double) current / target) : 0);
        progressText.setText(current + "/" + target);
    }

    public void addReward(String text, String styleClass) {
        Label r = new Label(text);
        r.getStyleClass().add(styleClass);
        rewardRow.getChildren().add(r);
    }

    public void setClaimable(boolean claimable) {
        claimBtn.setDisable(!claimable);
    }

    public void setClaimed(boolean claimed) {
        if (claimed) {
            claimBtn.setText("\u2713 Claimed");
            claimBtn.setDisable(true);
            claimBtn.setStyle("-fx-background-color: rgba(148,163,184,0.15); -fx-text-fill: #64748b; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 6 14; -fx-background-radius: 8; -fx-cursor: default;");
        } else {
            claimBtn.setText("Claim");
            claimBtn.getStyleClass().add("success-button");
        }
    }

    public void setOnClaim(Runnable action) {
        claimBtn.setOnAction(e -> {
            if (action != null) action.run();
        });
    }

    public Button getClaimButton() {
        return claimBtn;
    }
}
