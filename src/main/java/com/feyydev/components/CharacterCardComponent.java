package com.feyydev.components;

import com.feyydev.models.GameCharacter;
import com.feyydev.utils.Constants;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class CharacterCardComponent extends VBox {
    private final GameCharacter character;
    private boolean active;

    public CharacterCardComponent(GameCharacter character, Runnable onClick) {
        this.character = character;
        this.active = false;

        setAlignment(Pos.CENTER);
        setPrefSize(80, 90);
        setCursor(javafx.scene.Cursor.HAND);
        setPickOnBounds(true);
        getStyleClass().add("char-card");

        String rc = Constants.getRarityColor(character.getRarity());

        Label icon = new Label(Constants.getCharIcon(character.getName()));
        icon.setFont(Font.font(24));
        icon.getStyleClass().add("char-card-icon");

        Label name = new Label(character.getName());
        name.setStyle("-fx-text-fill: " + rc + ";");
        name.getStyleClass().add("char-card-name");

        Label lvl = new Label("Lv." + character.getLevel());
        lvl.getStyleClass().add("char-card-level");

        getChildren().addAll(icon, name, lvl);
        setOnMouseClicked(e -> {
            if (onClick != null) {
                System.out.println("[DEBUG] CharacterCard clicked: " + character.getName());
                onClick.run();
            }
        });
    }

    public void setActive(boolean active) {
        this.active = active;
        getStyleClass().removeAll("char-card", "char-card-active");
        if (active) {
            getStyleClass().add("char-card-active");
        } else {
            getStyleClass().add("char-card");
        }
    }

    public GameCharacter getCharacter() { return character; }
}
