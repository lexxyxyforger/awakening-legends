package com.feyydev.components;

import com.feyydev.Main.SceneType;
import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class NavigationBar extends HBox {
    private final List<VBox> items = new ArrayList<>();
    private final Consumer<SceneType> navigator;
    private SceneType activeType;
    private static final String[][] NAV_ITEMS = {
        {"\uD83C\uDFE0", "Home", "HOME"},
        {"\uD83D\uDC64", "Heroes", "CHARACTER"},
        {"\uD83C\uDF81", "Summon", "GACHA"},
        {"\uD83D\uDCD6", "Story", "STORY"},
        {"\uD83D\uDED2", "Shop", "SHOP"},
        {"\uD83C\uDF0D", "World", "WORLD_MAP"},
    };

    public NavigationBar(Consumer<SceneType> navigator, SceneType active) {
        this.navigator = navigator;
        this.activeType = active;
        getStyleClass().add("bottom-nav");
        setAlignment(Pos.CENTER);
        setPrefHeight(80);

        for (String[] item : NAV_ITEMS) {
            VBox box = new VBox(2);
            box.setAlignment(Pos.CENTER);
            box.setPrefWidth(80);
            box.setPrefHeight(70);
            box.setCursor(javafx.scene.Cursor.HAND);
            box.setPickOnBounds(true);
            box.getStyleClass().add("nav-item");

            Label icon = new Label(item[0]);
            icon.getStyleClass().add("nav-icon");
            Label label = new Label(item[1]);
            label.getStyleClass().add("nav-label");

            if (item[1].equalsIgnoreCase(active.name()) || item[2].equals(active.name())) {
                box.getStyleClass().add("active");
                icon.getStyleClass().add("active");
                label.getStyleClass().add("active");
            }

            box.getChildren().addAll(icon, label);
            SceneType type = SceneType.valueOf(item[2]);
            box.setOnMouseClicked(e -> {
                System.out.println("[DEBUG] NavigationBar clicked: " + item[1]);
                if (navigator != null) navigator.accept(type);
            });

            box.setOnMouseEntered(e -> {
                ScaleTransition st = new ScaleTransition(Duration.millis(150), box);
                st.setToX(1.08); st.setToY(1.08);
                st.play();
            });
            box.setOnMouseExited(e -> {
                ScaleTransition st = new ScaleTransition(Duration.millis(150), box);
                st.setToX(1.0); st.setToY(1.0);
                st.play();
            });

            items.add(box);
            getChildren().add(box);
        }
    }

    public void setActive(SceneType type) {
        this.activeType = type;
        for (int i = 0; i < items.size(); i++) {
            VBox box = items.get(i);
            Label icon = (Label) box.getChildren().get(0);
            Label label = (Label) box.getChildren().get(1);
            SceneType itemType = SceneType.valueOf(NAV_ITEMS[i][2]);
            boolean isActive = itemType == type;
            box.getStyleClass().remove("active");
            icon.getStyleClass().remove("active");
            label.getStyleClass().remove("active");
            if (isActive) {
                box.getStyleClass().add("active");
                icon.getStyleClass().add("active");
                label.getStyleClass().add("active");
            }
        }
    }
}
