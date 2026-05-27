package com.feyydev.scenes;

import com.feyydev.Main.SceneType;
import com.feyydev.managers.CharacterManager;
import com.feyydev.managers.InventoryManager;
import com.feyydev.models.*;
import com.feyydev.utils.Constants;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CharacterScene {
    private final Scene scene;
    private final Player player;
    private final Consumer<SceneType> navigator;
    private final CharacterManager characterManager;
    private final InventoryManager inventoryManager;
    private final FlowPane charGrid;
    private final VBox detailPanel;
    private final TextField searchField;
    private final HBox filterBar;
    private GameCharacter selectedCharacter;
    private String activeFilter = "All";

    public CharacterScene(Player player, Consumer<SceneType> navigator) {
        this.player = player;
        this.navigator = navigator;
        this.characterManager = CharacterManager.getInstance();
        this.inventoryManager = InventoryManager.getInstance();
        this.charGrid = new FlowPane(10, 10);
        this.detailPanel = new VBox(10);
        this.searchField = new TextField();
        this.filterBar = new HBox(6);
        this.scene = buildScene();
        refreshGrid();
    }

    public Scene getScene() { return scene; }

    private Scene buildScene() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");
        root.setPadding(new Insets(16));

        HBox topBar = new HBox(12);
        topBar.setAlignment(Pos.CENTER_LEFT);
        Button backBtn = new Button("\u2190 Back");
        backBtn.getStyleClass().add("back-button");
        backBtn.setOnAction(e -> navigator.accept(SceneType.HOME));
        Label title = new Label("\uD83D\uDC64 Characters");
        title.getStyleClass().add("scene-title");
        topBar.getChildren().addAll(backBtn, title);

        searchField.setPromptText("Search characters...");
        searchField.setPrefWidth(200);
        searchField.textProperty().addListener((obs, o, n) -> filterGrid());

        String[] filters = {"All", "SSR", "SR", "R", "Martial Artist", "Sword User", "Mage", "Assassin", "Tank", "Support"};
        filterBar.setAlignment(Pos.CENTER_LEFT);
        filterBar.setPadding(new Insets(6, 0, 6, 0));
        for (String f : filters) {
            Button btn = new Button(f);
            btn.getStyleClass().add("small-button");
            if (f.equals(activeFilter)) btn.setStyle("-fx-background-color: rgba(59,130,246,0.2); -fx-text-fill: #60a5fa; -fx-border-color: #3b82f6;");
            btn.setOnAction(e -> {
                activeFilter = f;
                updateFilterStyles();
                filterGrid();
            });
            filterBar.getChildren().add(btn);
        }

        charGrid.setPrefWidth(560);
        charGrid.setPadding(new Insets(8));
        ScrollPane gridScroll = new ScrollPane(charGrid);
        gridScroll.getStyleClass().add("scroll-pane");
        gridScroll.setFitToWidth(true);

        detailPanel.setPadding(new Insets(16));
        detailPanel.setPrefWidth(340);
        detailPanel.getStyleClass().add("glass-card");
        detailPanel.setAlignment(Pos.TOP_CENTER);
        Label hint = new Label("Select a character to view details");
        hint.getStyleClass().add("hint-label");
        detailPanel.getChildren().add(hint);

        VBox leftSide = new VBox(6);
        leftSide.getChildren().addAll(searchField, filterBar, gridScroll);
        HBox.setHgrow(leftSide, Priority.ALWAYS);

        SplitPane split = new SplitPane();
        split.getItems().addAll(leftSide, detailPanel);
        split.setDividerPosition(0, 0.58);

        root.setTop(topBar);
        root.setCenter(split);

        Scene s = new Scene(root, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        s.getStylesheets().add(getClass().getResource("/com/feyydev/style.css").toExternalForm());
        return s;
    }

    private void updateFilterStyles() {
        for (var node : filterBar.getChildren()) {
            if (node instanceof Button btn) {
                btn.setStyle("");
                if (btn.getText().equals(activeFilter)) {
                    btn.setStyle("-fx-background-color: rgba(59,130,246,0.2); -fx-text-fill: #60a5fa; -fx-border-color: #3b82f6;");
                }
            }
        }
    }

    public void refreshGrid() { filterGrid(); }

    private void filterGrid() {
        charGrid.getChildren().clear();
        String query = searchField.getText().toLowerCase();
        List<GameCharacter> chars = player.getCharacters().stream()
            .filter(c -> {
                boolean matchFilter = activeFilter.equals("All") || c.getRarity().equals(activeFilter) || c.getCategory().equals(activeFilter);
                boolean matchSearch = query.isEmpty() || c.getName().toLowerCase().contains(query);
                return matchFilter && matchSearch;
            })
            .collect(Collectors.toList());

        for (GameCharacter c : chars) {
            VBox card = createCharacterCard(c);
            charGrid.getChildren().add(card);
        }
        if (chars.isEmpty()) {
            Label empty = new Label("No characters found");
            empty.getStyleClass().add("hint-label");
            charGrid.getChildren().add(empty);
        }
    }

    private VBox createCharacterCard(GameCharacter c) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(130, 160);
        card.getStyleClass().addAll("character-card", "card-" + c.getRarity().toLowerCase());

        String rc = Constants.getRarityColor(c.getRarity());

        VBox avatarBg = new VBox();
        avatarBg.setPrefSize(52, 52);
        avatarBg.setStyle("-fx-background-color: " + rc + "22; -fx-background-radius: 26; -fx-border-color: " + rc + "44; -fx-border-radius: 26;");
        avatarBg.setAlignment(Pos.CENTER);
        Label avatarIcon = new Label(Constants.getCharIcon(c.getName()));
        avatarIcon.setFont(Font.font(22));
        avatarBg.getChildren().add(avatarIcon);

        Label rarityBadge = new Label(Constants.getRarityBadge(c.getRarity()));
        rarityBadge.setStyle("-fx-background-color: " + rc + "; -fx-text-fill: #000; -fx-font-weight: bold; -fx-padding: 1 8; -fx-background-radius: 8; -fx-font-size: 10px;");

        Label nameLabel = new Label(c.getName());
        nameLabel.setStyle("-fx-text-fill: " + rc + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-text-alignment: center;");

        Label lvlLabel = new Label("Lv." + c.getLevel() + " \u2B50" + c.getAwakeningLevel());
        lvlLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 10px;");

        Label catLabel = new Label(c.getCategory());
        catLabel.setStyle("-fx-text-fill: " + Constants.getCategoryColor(c.getCategory()) + "; -fx-font-size: 9px; -fx-font-weight: bold;");

        card.getChildren().addAll(avatarBg, rarityBadge, nameLabel, lvlLabel, catLabel);
        card.setOnMouseClicked(e -> showCharacterDetail(c));
        return card;
    }

    private void showCharacterDetail(GameCharacter c) {
        selectedCharacter = c;
        detailPanel.getChildren().clear();

        String rc = Constants.getRarityColor(c.getRarity());

        VBox avatarBg = new VBox();
        avatarBg.setPrefSize(70, 70);
        avatarBg.setStyle("-fx-background-color: " + rc + "22; -fx-background-radius: 35; -fx-border-color: " + rc + "44; -fx-border-radius: 35; -fx-border-width: 3;");
        avatarBg.setAlignment(Pos.CENTER);
        Label avatarIcon = new Label(Constants.getCharIcon(c.getName()));
        avatarIcon.setFont(Font.font(32));
        avatarBg.getChildren().add(avatarIcon);

        Label nameLbl = new Label(c.getName());
        nameLbl.setStyle("-fx-text-fill: " + rc + "; -fx-font-size: 18px; -fx-font-weight: bold;");

        HBox infoRow = new HBox(10);
        infoRow.setAlignment(Pos.CENTER);
        Label rarityLbl = new Label(Constants.getRarityBadge(c.getRarity()));
        rarityLbl.setStyle("-fx-background-color: " + rc + "; -fx-text-fill: #000; -fx-font-weight: bold; -fx-padding: 2 10; -fx-background-radius: 8;");
        Label catLbl = new Label(c.getCategory());
        catLbl.setStyle("-fx-text-fill: " + Constants.getCategoryColor(c.getCategory()) + "; -fx-font-weight: bold; -fx-font-size: 12px;");
        infoRow.getChildren().addAll(rarityLbl, catLbl);

        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(12);
        statsGrid.setVgap(4);
        statsGrid.setPadding(new Insets(8));
        statsGrid.setStyle("-fx-background-color: rgba(255,255,255,0.03); -fx-background-radius: 10;");

        addStatRow(statsGrid, 0, "\u2764 HP", String.valueOf(c.getMaxHp()), "#4ade80");
        addStatRow(statsGrid, 1, "\u2694 ATK", String.valueOf(c.getAttack()), "#f87171");
        addStatRow(statsGrid, 2, "\uD83D\uDEE1 DEF", String.valueOf(c.getDefense()), "#60a5fa");
        addStatRow(statsGrid, 3, "\u26A1 SPD", String.valueOf(c.getSpeed()), "#fbbf24");
        addStatRow(statsGrid, 4, "\uD83D\uDCA5 Crit", (int)(c.getCriticalChance() * 100) + "%", "#f59e0b");
        addStatRow(statsGrid, 5, "\uD83D\uDD25 Crit DMG", (int)(c.getCriticalDamage() * 100) + "%", "#f97316");
        addStatRow(statsGrid, 6, "\u2728 Skill", (int)(c.getSkillDamage() * 100) + "%", "#a855f7");
        addStatRow(statsGrid, 7, "\uD83D\uDCA5 Ult", (int)(c.getUltimateDamage() * 100) + "%", "#ef4444");

        Label progressLbl = new Label("Lv." + c.getLevel() + "/" + c.getMaxLevel() + " \u2B50+" + c.getAwakeningLevel() + " \uD83E\uDEE0+" + c.getEvolutionLevel());
        progressLbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");

        VBox actionButtons = new VBox(6);
        actionButtons.setAlignment(Pos.CENTER);

        if (c.canAwaken()) {
            Button awakeBtn = new Button("\u2B50 Awaken (" + ((c.getAwakeningLevel() + 1) * 30) + " shards)");
            awakeBtn.getStyleClass().add("primary-button");
            awakeBtn.setOnAction(e -> {
                if (characterManager.awaken(c.getId())) {
                    showToast(c.getName() + " awakened to +" + c.getAwakeningLevel() + "!");
                    showCharacterDetail(c);
                }
            });
            actionButtons.getChildren().add(awakeBtn);
        }
        if (c.canEvolve()) {
            Button evolveBtn = new Button("\uD83E\uDEE0 Evolve (" + (c.getEvolutionLevel() + 1) + " dupes)");
            evolveBtn.getStyleClass().add("success-button");
            evolveBtn.setOnAction(e -> {
                if (characterManager.evolve(c.getId())) {
                    showToast(c.getName() + " evolved to +" + c.getEvolutionLevel() + "!");
                    showCharacterDetail(c);
                }
            });
            actionButtons.getChildren().add(evolveBtn);
        }
        if (c.canUpgradeSkill()) {
            Button skillBtn = new Button("\u2728 Skill Lv." + c.getSkillLevel() + " (" + (c.getSkillLevel() * 5) + " shards)");
            skillBtn.getStyleClass().add("action-button");
            skillBtn.setOnAction(e -> {
                if (characterManager.upgradeSkill(c.getId())) {
                    showToast("Skill upgraded to Lv." + c.getSkillLevel() + "!");
                    showCharacterDetail(c);
                }
            });
            actionButtons.getChildren().add(skillBtn);
        }

        Button selectBtn = new Button("\u2694 Select for Battle");
        selectBtn.getStyleClass().add("primary-button");
        selectBtn.setOnAction(e -> {
            player.setActiveTeamId(c.getId());
            showToast(c.getName() + " selected!");
        });
        actionButtons.getChildren().add(selectBtn);

        detailPanel.getChildren().addAll(avatarBg, nameLbl, infoRow, statsGrid, progressLbl, actionButtons);
    }

    private void addStatRow(GridPane grid, int row, String label, String value, String color) {
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px;");
        Label val = new Label(value);
        val.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 12px; -fx-font-weight: bold;");
        grid.add(lbl, 0, row);
        grid.add(val, 1, row);
    }

    private void showToast(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null); a.setContentText(msg);
        a.getDialogPane().setStyle("-fx-background-color: #1e293b; -fx-text-fill: #fff;");
        a.showAndWait();
    }
}
