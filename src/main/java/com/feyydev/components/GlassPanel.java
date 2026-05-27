package com.feyydev.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class GlassPanel extends VBox {
    public GlassPanel(Node... children) {
        setAlignment(Pos.TOP_CENTER);
        setPadding(new Insets(16));
        setMaxWidth(600);
        getStyleClass().add("glass-card");
        getChildren().addAll(children);
    }

    public GlassPanel withHeader(String headerText) {
        Label header = new Label(headerText);
        header.setStyle("-fx-text-fill: #f1f5f9; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 0 0 8 0;");
        getChildren().add(0, header);
        return this;
    }

    public GlassPanel withPadding(int top, int right, int bottom, int left) {
        setPadding(new Insets(top, right, bottom, left));
        return this;
    }

    public GlassPanel withMaxWidth(double width) {
        setMaxWidth(width);
        return this;
    }
}
