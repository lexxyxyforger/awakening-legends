package com.feyydev.scenes;

import com.feyydev.Main.SceneType;
import com.feyydev.models.Mail;
import com.feyydev.models.Player;
import com.feyydev.services.MailService;
import com.feyydev.utils.Constants;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import java.util.function.Consumer;

public class MailboxScene {
    private final Scene scene;
    private final Player player;
    private final Consumer<SceneType> navigator;
    private final VBox mailList;
    private final Label unclaimedLabel;

    public MailboxScene(Player player, Consumer<SceneType> navigator) {
        this.player = player;
        this.navigator = navigator;
        this.mailList = new VBox(8);
        this.unclaimedLabel = new Label();
        this.scene = buildScene();
    }

    public Scene getScene() { return scene; }
    public void refresh() { populateMail(); }

    private Scene buildScene() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");
        root.setPadding(new Insets(16));

        HBox topBar = new HBox(12);
        topBar.setAlignment(Pos.CENTER_LEFT);
        Button backBtn = new Button("\u2190 Back");
        backBtn.getStyleClass().add("back-button");
        backBtn.setOnAction(e -> {
            System.out.println("[DEBUG] MailboxScene Back clicked");
            navigator.accept(SceneType.HOME);
        });
        Label title = new Label("\uD83D\uDCEB Mailbox");
        title.getStyleClass().add("scene-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        unclaimedLabel.setStyle("-fx-text-fill: #60a5fa; -fx-font-size: 13px; -fx-font-weight: bold;");

        Button claimAllBtn = new Button("\uD83D\uDCB0 Claim All");
        claimAllBtn.getStyleClass().add("primary-button");
        claimAllBtn.setOnAction(e -> {
            int count = MailService.getInstance().claimAllMail(player);
            if (count > 0) showToast("Claimed " + count + " rewards!"); else showToast("No mails to claim");
            populateMail();
        });

        topBar.getChildren().addAll(backBtn, title, spacer, unclaimedLabel, claimAllBtn);

        mailList.setAlignment(Pos.TOP_CENTER);
        mailList.setPadding(new Insets(8, 0, 0, 0));

        ScrollPane scroll = new ScrollPane(mailList);
        scroll.getStyleClass().add("scroll-pane");
        scroll.setFitToWidth(true);

        root.setTop(topBar);
        root.setCenter(scroll);

        Scene s = new Scene(root, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        s.getStylesheets().add(getClass().getResource("/com/feyydev/global.css").toExternalForm());
        return s;
    }

    private void populateMail() {
        mailList.getChildren().clear();
        if (player.getMailbox().isEmpty()) {
            Label empty = new Label("No mail yet");
            empty.getStyleClass().add("hint-label");
            mailList.getChildren().add(empty);
            return;
        }

        int unclaimed = 0;
        for (Mail mail : player.getMailbox()) {
            if (mail.isExpired()) continue;
            if (!mail.isClaimed()) unclaimed++;
            VBox card = createMailCard(mail);
            mailList.getChildren().add(card);
        }
        unclaimedLabel.setText(unclaimed > 0 ? unclaimed + " unclaimed" : "");
    }

    private VBox createMailCard(Mail mail) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(14));
        card.setPrefWidth(700);
        card.getStyleClass().add("mail-card");
        if (!mail.isClaimed()) card.getStyleClass().add("unread");

        HBox top = new HBox(12);
        top.setAlignment(Pos.CENTER_LEFT);

        Label avatar = new Label(mail.getSenderAvatar());
        avatar.setFont(Font.font(28));

        VBox info = new VBox(2);
        Label title = new Label(mail.getTitle());
        title.setStyle("-fx-text-fill: #e2e8f0; -fx-font-size: 14px; -fx-font-weight: bold;");
        Label sender = new Label("From: " + mail.getSenderName());
        sender.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");
        Label msg = new Label(mail.getMessage());
        msg.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");
        msg.setWrapText(true);
        info.getChildren().addAll(title, sender);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        long remaining = mail.getExpirationTime() - System.currentTimeMillis();
        long days = remaining / (24 * 60 * 60 * 1000);
        Label expiry = new Label(days > 0 ? days + "d left" : "Expiring");
        expiry.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");

        top.getChildren().addAll(avatar, info, spacer, expiry);

        HBox rewardRow = new HBox(10);
        rewardRow.setAlignment(Pos.CENTER_LEFT);
        rewardRow.setPadding(new Insets(4, 0, 0, 0));

        if (mail.getRewardGold() > 0) {
            Label r = new Label("\uD83D\uDCB0+" + Constants.formatNumber(mail.getRewardGold()));
            r.getStyleClass().add("reward-badge-gold");
            rewardRow.getChildren().add(r);
        }
        if (mail.getRewardGems() > 0) {
            Label r = new Label("\uD83D\uDC8E+" + Constants.formatNumber(mail.getRewardGems()));
            r.getStyleClass().add("reward-badge-gems");
            rewardRow.getChildren().add(r);
        }
        if (mail.getRewardExp() > 0) {
            Label r = new Label("\u2B50+" + Constants.formatNumber(mail.getRewardExp()) + " EXP");
            r.getStyleClass().add("reward-badge-exp");
            rewardRow.getChildren().add(r);
        }
        if (mail.getRewardSummonTickets() > 0) {
            Label r = new Label("\uD83C\uDF81+" + mail.getRewardSummonTickets() + " Ticket");
            r.getStyleClass().add("reward-badge-ticket");
            rewardRow.getChildren().add(r);
        }

        if (!mail.isClaimed()) {
            Button claimBtn = new Button("Claim");
            claimBtn.getStyleClass().add("success-button");
            claimBtn.setOnAction(e -> {
                MailService.getInstance().claimMail(player, mail.getId());
                mail.setClaimed(true);
                showToast("Reward claimed!");
                populateMail();
            });
            card.getChildren().addAll(top, msg, rewardRow, claimBtn);
        } else {
            Label claimedLbl = new Label("\u2705 Claimed");
            claimedLbl.setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold; -fx-font-size: 12px;");
            card.getChildren().addAll(top, msg, rewardRow, claimedLbl);
        }
        return card;
    }

    private void showToast(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null); a.setContentText(msg);
        a.getDialogPane().setStyle("-fx-background-color: #1e293b; -fx-text-fill: #fff;");
        a.showAndWait();
    }
}
