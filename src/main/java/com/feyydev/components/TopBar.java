package com.feyydev.components;

import com.feyydev.Main.SceneType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import java.util.List;
import java.util.function.Consumer;

public class TopBar extends HBox {
    private final Consumer<SceneType> navigator;
    private final Button backBtn;
    private final Label titleLabel;

    public TopBar(String title, Consumer<SceneType> navigator, Node... rightNodes) {
        this(title, navigator, SceneType.HOME, rightNodes);
    }

    public TopBar(String title, Consumer<SceneType> navigator, SceneType backTarget, Node... rightNodes) {
        this.navigator = navigator;
        setAlignment(Pos.CENTER_LEFT);
        setPadding(new Insets(8, 16, 8, 16));

        backBtn = new Button("\u2190 Back");
        backBtn.getStyleClass().add("back-button");
        backBtn.setOnAction(e -> {
            if (this.navigator != null) this.navigator.accept(backTarget);
        });

        titleLabel = new Label(title);
        titleLabel.getStyleClass().add("scene-title");

        getChildren().addAll(backBtn, titleLabel);

        if (rightNodes != null && rightNodes.length > 0) {
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            getChildren().add(spacer);
            getChildren().addAll(rightNodes);
        }
    }

    public TopBar(String title, Consumer<SceneType> navigator, List<Node> rightNodes) {
        this(title, navigator, rightNodes.toArray(new Node[0]));
    }

    public void setBackTarget(SceneType target) {
        backBtn.setOnAction(e -> {
            if (navigator != null) navigator.accept(target);
        });
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }
}
