package com.feyydev.scenes;

import com.feyydev.Main.SceneType;
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

public class InventoryScene {
    private final Scene scene;
    private final Player player;
    private final Consumer<SceneType> navigator;
    private final InventoryManager inventoryManager;
    private final VBox itemList;
    private final HBox tabBar;
    private String activeTab = "Consumable";

    public InventoryScene(Player player, Consumer<SceneType> navigator) {
        this.player = player;
        this.navigator = navigator;
        this.inventoryManager = InventoryManager.getInstance();
        this.itemList = new VBox(8);
        this.tabBar = new HBox(6);
        this.scene = buildScene();
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
        Label title = new Label("\uD83C\uDF92 Inventory");
        title.getStyleClass().add("scene-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label countLbl = new Label(player.getInventory().size() + " items");
        countLbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 13px;");
        topBar.getChildren().addAll(backBtn, title, spacer, countLbl);

        String[] tabs = {"Consumable", "Material", "Weapon", "Armor"};
        tabBar.setAlignment(Pos.CENTER);
        tabBar.setPadding(new Insets(6, 0, 6, 0));
        for (String tab : tabs) {
            Button btn = new Button(tab);
            btn.getStyleClass().add("tab-button");
            if (tab.equals(activeTab)) btn.getStyleClass().add("selected");
            btn.setOnAction(e -> {
                activeTab = tab;
                updateTabStyles();
                populateItems();
            });
            tabBar.getChildren().add(btn);
        }

        itemList.setAlignment(Pos.TOP_CENTER);
        itemList.setPadding(new Insets(8, 0, 0, 0));

        ScrollPane scroll = new ScrollPane(itemList);
        scroll.getStyleClass().add("scroll-pane");
        scroll.setFitToWidth(true);

        root.setTop(topBar);
        root.setCenter(new VBox(6, tabBar, scroll));

        Scene s = new Scene(root, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        s.getStylesheets().add(getClass().getResource("/com/feyydev/style.css").toExternalForm());
        return s;
    }

    private void updateTabStyles() {
        for (var node : tabBar.getChildren()) {
            if (node instanceof Button btn) {
                btn.getStyleClass().remove("selected");
                if (btn.getText().equals(activeTab)) btn.getStyleClass().add("selected");
            }
        }
    }

    private void populateItems() {
        itemList.getChildren().clear();
        List<?> items = switch (activeTab) {
            case "Consumable" -> inventoryManager.getItemsByType("Consumable");
            case "Material" -> inventoryManager.getItemsByType("Material");
            case "Weapon" -> inventoryManager.getWeapons();
            case "Armor" -> inventoryManager.getArmors();
            default -> inventoryManager.getItems();
        };

        if (items.isEmpty()) {
            Label empty = new Label("No " + activeTab.toLowerCase() + " items");
            empty.getStyleClass().add("hint-label");
            itemList.getChildren().add(empty);
            return;
        }

        for (Object obj : items) {
            if (obj instanceof Item item) {
                VBox card = createItemCard(item);
                itemList.getChildren().add(card);
            } else if (obj instanceof Weapon weapon) {
                VBox card = createWeaponCard(weapon);
                itemList.getChildren().add(card);
            } else if (obj instanceof Armor armor) {
                VBox card = createArmorCard(armor);
                itemList.getChildren().add(card);
            }
        }
    }

    private VBox createItemCard(Item item) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(12));
        card.setPrefWidth(550);
        card.getStyleClass().add("item-card");

        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);

        Label icon = new Label(item.getIcon() != null ? item.getIcon() : "\uD83D\uDCE6");
        icon.setFont(Font.font(22));

        VBox info = new VBox(2);
        Label nameLbl = new Label(item.getName());
        nameLbl.setStyle("-fx-text-fill: #1e293b; -fx-font-size: 14px; -fx-font-weight: bold;");
        Label descLbl = new Label(item.getDescription() + " (x" + item.getQuantity() + ")");
        descLbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px;");
        info.getChildren().addAll(nameLbl, descLbl);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label rarityLbl = new Label(item.getRarity());
        String rc = Constants.getRarityHex(item.getRarity());
        rarityLbl.setStyle("-fx-text-fill: " + rc + "; -fx-font-size: 11px; -fx-font-weight: bold; -fx-background-color: rgba(0,0,0,0.2); -fx-padding: 2 8; -fx-background-radius: 6;");

        row.getChildren().addAll(icon, info, spacer, rarityLbl);
        card.getChildren().add(row);
        return card;
    }

    private VBox createWeaponCard(Weapon w) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(12));
        card.setPrefWidth(550);
        card.getStyleClass().add("item-card");
        String rc = Constants.getRarityHex(w.getRarity());

        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);

        Label icon = new Label(Constants.getItemIcon(w.getType()));
        icon.setFont(Font.font(22));

        VBox info = new VBox(2);
        Label nameLbl = new Label(w.getName());
        nameLbl.setStyle("-fx-text-fill: " + rc + "; -fx-font-size: 14px; -fx-font-weight: bold;");
        Label descLbl = new Label(w.getType() + " | ATK: " + w.getAttack() + " (+" + w.getEnhancementLevel() + ")");
        descLbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px;");
        info.getChildren().addAll(nameLbl, descLbl);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label rarityLbl = new Label(w.getRarity());
        rarityLbl.setStyle("-fx-text-fill: " + rc + "; -fx-font-size: 11px; -fx-font-weight: bold; -fx-background-color: rgba(0,0,0,0.2); -fx-padding: 2 8; -fx-background-radius: 6;");

        Label enhanceLbl = new Label("+" + w.getEnhancementLevel());
        enhanceLbl.setStyle("-fx-text-fill: #60a5fa; -fx-font-weight: bold;");

        row.getChildren().addAll(icon, info, spacer, enhanceLbl, rarityLbl);
        card.getChildren().add(row);
        return card;
    }

    private VBox createArmorCard(Armor a) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(12));
        card.setPrefWidth(550);
        card.getStyleClass().add("item-card");
        String rc = Constants.getRarityHex(a.getRarity());

        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);

        Label icon = new Label(Constants.getItemIcon(a.getType()));
        icon.setFont(Font.font(22));

        VBox info = new VBox(2);
        Label nameLbl = new Label(a.getName());
        nameLbl.setStyle("-fx-text-fill: " + rc + "; -fx-font-size: 14px; -fx-font-weight: bold;");
        Label descLbl = new Label(a.getType() + " | DEF: " + a.getDefense() + " (+" + a.getEnhancementLevel() + ")");
        descLbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px;");
        info.getChildren().addAll(nameLbl, descLbl);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label rarityLbl = new Label(a.getRarity());
        rarityLbl.setStyle("-fx-text-fill: " + rc + "; -fx-font-size: 11px; -fx-font-weight: bold; -fx-background-color: rgba(0,0,0,0.2); -fx-padding: 2 8; -fx-background-radius: 6;");

        Label enhanceLbl = new Label("+" + a.getEnhancementLevel());
        enhanceLbl.setStyle("-fx-text-fill: #60a5fa; -fx-font-weight: bold;");

        row.getChildren().addAll(icon, info, spacer, enhanceLbl, rarityLbl);
        card.getChildren().add(row);
        return card;
    }
}
