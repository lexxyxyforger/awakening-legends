package com.feyydev.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class RewardCard extends HBox {
    public RewardCard(long gold, long gems, long exp, int tickets) {
        this(gold, gems, exp, tickets, true);
    }

    public RewardCard(long gold, long gems, long exp, int tickets, boolean compact) {
        setAlignment(Pos.CENTER_LEFT);
        setPadding(new Insets(compact ? 2 : 6, 0, compact ? 2 : 6, 0));
        setSpacing(8);

        if (gold > 0) {
            getChildren().add(createReward("\uD83D\uDCB0+" + gold, "reward-badge-gold"));
        }
        if (gems > 0) {
            getChildren().add(createReward("\uD83D\uDC8E+" + gems, "reward-badge-gems"));
        }
        if (exp > 0) {
            getChildren().add(createReward("\u2B50+" + exp + " EXP", "reward-badge-exp"));
        }
        if (tickets > 0) {
            getChildren().add(createReward("\uD83C\uDF81+" + tickets + " Ticket", "reward-badge-ticket"));
        }
    }

    private Label createReward(String text, String styleClass) {
        Label label = new Label(text);
        label.getStyleClass().add(styleClass);
        return label;
    }
}
