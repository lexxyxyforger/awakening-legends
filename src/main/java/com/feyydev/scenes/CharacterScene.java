package com.feyydev.scenes;

import com.feyydev.Main.SceneType;
import com.feyydev.components.CharacterCardComponent;
import com.feyydev.components.CharacterDetailPanel;
import com.feyydev.managers.CharacterManager;
import com.feyydev.models.*;
import com.feyydev.utils.Constants;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import java.util.List;
import java.util.function.Consumer;

public class CharacterScene {
    private final Scene scene;
    private final Player player;
    private final Consumer<SceneType> navigator;

    private final List<GameCharacter> characters;
    private int selectedIndex;
    private final HBox selectorRow;
    private final StackPane detailContainer;
    private final Label counterLabel;
    private final CharacterManager characterManager;

    public CharacterScene(Player player, Consumer<SceneType> navigator) {
        this.player = player;
        this.navigator = navigator;
        this.characterManager = CharacterManager.getInstance();
        this.characters = player.getCharacters();
        this.selectedIndex = 0;
        this.selectorRow = new HBox(8);
        this.detailContainer = new StackPane();
        this.counterLabel = new Label();
        this.scene = buildScene();
        selectCharacter(0);
    }

    public Scene getScene() { return scene; }

    public void refresh() {
        selectCharacter(selectedIndex);
    }

    public void refreshGrid() {
        if (characters.isEmpty()) {
            detailContainer.getChildren().clear();
            selectorRow.getChildren().clear();
            updateCounter();
            return;
        }
        if (selectedIndex >= characters.size()) {
            selectedIndex = characters.size() - 1;
        }
        selectCharacter(selectedIndex);
    }

    private Scene buildScene() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");

        HBox topBar = new HBox(12);
        topBar.setPadding(new Insets(12, 20, 8, 20));
        topBar.setAlignment(Pos.CENTER_LEFT);

        Button backBtn = new Button("\u2190 Back");
        backBtn.getStyleClass().add("back-button");
        backBtn.setOnAction(e -> {
            System.out.println("[DEBUG] CharacterScene Back clicked");
            navigator.accept(SceneType.HOME);
        });

        Label title = new Label("\uD83D\uDC64 Characters");
        title.getStyleClass().add("scene-title");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        counterLabel.getStyleClass().add("scene-subtitle");
        updateCounter();

        Button invBtn = new Button("\uD83C\uDF92 Inventory");
        invBtn.getStyleClass().add("action-button");
        invBtn.setOnAction(e -> navigator.accept(SceneType.INVENTORY));

        topBar.getChildren().addAll(backBtn, title, sp, counterLabel, invBtn);

        detailContainer.setAlignment(Pos.CENTER);
        VBox.setVgrow(detailContainer, Priority.ALWAYS);

        VBox bottom = new VBox(6);
        bottom.setPadding(new Insets(4, 20, 16, 20));

        Label selectHint = new Label("SELECT CHARACTER");
        selectHint.getStyleClass().add("char-selector-hint");

        HBox navRow = new HBox(8);
        navRow.setAlignment(Pos.CENTER);

        Button prevBtn = new Button("\u25C0");
        prevBtn.getStyleClass().add("char-nav-btn");
        prevBtn.setOnAction(e -> {
            if (selectedIndex > 0) selectCharacter(selectedIndex - 1);
        });

        selectorRow.setAlignment(Pos.CENTER);
        rebuildSelector();

        Button nextBtn = new Button("\u25B6");
        nextBtn.getStyleClass().add("char-nav-btn");
        nextBtn.setOnAction(e -> {
            if (selectedIndex < characters.size() - 1) selectCharacter(selectedIndex + 1);
        });

        navRow.getChildren().addAll(prevBtn, selectorRow, nextBtn);
        bottom.getChildren().addAll(selectHint, navRow);

        root.setTop(topBar);
        root.setCenter(detailContainer);
        root.setBottom(bottom);

        Scene s = new Scene(root, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        s.getStylesheets().add(getClass().getResource("/com/feyydev/global.css").toExternalForm());
        s.getStylesheets().add(getClass().getResource("/com/feyydev/character.css").toExternalForm());
        return s;
    }

    private void selectCharacter(int index) {
        if (index < 0 || index >= characters.size()) return;
        selectedIndex = index;
        GameCharacter c = characters.get(index);

        detailContainer.getChildren().clear();
        CharacterDetailPanel panel = new CharacterDetailPanel(c, () -> {
            player.calcTotalPower();
            selectCharacter(selectedIndex);
        });
        detailContainer.getChildren().add(panel);

        updateCounter();
        rebuildSelector();
    }

    private void rebuildSelector() {
        selectorRow.getChildren().clear();
        for (int i = 0; i < characters.size(); i++) {
            int idx = i;
            GameCharacter c = characters.get(i);
            CharacterCardComponent card = new CharacterCardComponent(c, () -> selectCharacter(idx));
            card.setActive(i == selectedIndex);
            selectorRow.getChildren().add(card);
        }
    }

    private void updateCounter() {
        if (characters.isEmpty()) {
            counterLabel.setText("0 heroes");
            return;
        }
        counterLabel.setText(characters.size() + " heroes  \u2022  Page " + (selectedIndex + 1) + "/" + characters.size());
    }
}
