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
import java.util.function.Consumer;

public class ShopScene {
    private final Scene scene;
    private final Player player;
    private final Consumer<SceneType> navigator;
    private final InventoryManager inventoryManager;
    private final VBox shopList;
    private final HBox tabBar;
    private String activeTab = "Gold";

    public ShopScene(Player player, Consumer<SceneType> navigator) {
        this.player = player;
        this.navigator = navigator;
        this.inventoryManager = InventoryManager.getInstance();
        this.shopList = new VBox(10);
        this.tabBar = new HBox(6);
        this.scene = buildScene();
    }

    public Scene getScene() { return scene; }
    public void refresh() { populateShop(); }

    private Scene buildScene() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");
        root.setPadding(new Insets(16));

        HBox topBar = new HBox(12);
        topBar.setAlignment(Pos.CENTER_LEFT);
        Button backBtn = new Button("\u2190 Back");
        backBtn.getStyleClass().add("back-button");
        backBtn.setOnAction(e -> {
            System.out.println("[DEBUG] ShopScene Back clicked");
            navigator.accept(SceneType.HOME);
        });
        Label title = new Label("\uD83D\uDED2 Shop");
        title.getStyleClass().add("scene-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label goldDisp = new Label("\uD83D\uDCB0 " + Constants.formatNumber(player.getGold()));
        goldDisp.setStyle("-fx-text-fill: #fbbf24; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-color: rgba(251,191,36,0.08); -fx-padding: 4 10; -fx-background-radius: 8;");
        Label gemDisp = new Label("\uD83D\uDC8E " + Constants.formatNumber(player.getGems()));
        gemDisp.setStyle("-fx-text-fill: #c084fc; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-color: rgba(192,132,252,0.08); -fx-padding: 4 10; -fx-background-radius: 8;");

        topBar.getChildren().addAll(backBtn, title, spacer, goldDisp, gemDisp);

        String[] tabs = {"Gold", "Gem", "Event", "Premium"};
        tabBar.setAlignment(Pos.CENTER);
        tabBar.setPadding(new Insets(6, 0, 6, 0));
        for (String tab : tabs) {
            Button btn = new Button(tab);
            btn.getStyleClass().add("tab-button");
            if (tab.equals(activeTab)) btn.getStyleClass().add("selected");
            btn.setOnAction(e -> {
                activeTab = tab;
                updateTabStyles();
                populateShop();
            });
            tabBar.getChildren().add(btn);
        }

        shopList.setAlignment(Pos.TOP_CENTER);
        shopList.setPadding(new Insets(8, 0, 0, 0));

        ScrollPane scroll = new ScrollPane(shopList);
        scroll.getStyleClass().add("scroll-pane");
        scroll.setFitToWidth(true);

        root.setTop(topBar);
        root.setCenter(new VBox(6, tabBar, scroll));

        Scene s = new Scene(root, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        s.getStylesheets().add(getClass().getResource("/com/feyydev/global.css").toExternalForm());
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

    private void populateShop() {
        shopList.getChildren().clear();
        String[][] items = getShopItems(activeTab);
        for (String[] item : items) {
            VBox card = createShopCard(item[0], item[1], Integer.parseInt(item[2]), item[3], item[4], () -> buyItem(item[0], Integer.parseInt(item[2]), item[3], item[4]));
            shopList.getChildren().add(card);
        }
    }

    private String[][] getShopItems(String tab) {
        return switch (tab) {
            case "Gold" -> new String[][]{
                {"Small Potion", "Restores 500 HP", "50", "GOLD", "\uD83E\uDDEA"},
                {"Medium Potion", "Restores 1500 HP", "150", "GOLD", "\uD83E\uDDEA"},
                {"Large Potion", "Restores 5000 HP", "400", "GOLD", "\uD83E\uDDEA"},
                {"Energy Drink", "Restores 20 Energy", "200", "GOLD", "\u26A1"},
                {"Practice Sword", "Common weapon", "500", "GOLD", "\u2694"},
                {"Cloth Armor", "Common armor", "400", "GOLD", "\uD83D\uDEE1"}
            };
            case "Gem" -> new String[][]{
                {"Summon Ticket x1", "1 character summon", "100", "GEMS", "\uD83C\uDF81"},
                {"Summon Ticket x10", "10 character summons", "900", "GEMS", "\uD83C\uDF81"},
                {"Energy Refill", "Full energy restore", "50", "GEMS", "\u26A1"},
                {"Raid Token x3", "3 raid entries", "100", "GEMS", "\uD83D\uDC7E"},
                {"Arena Token x5", "5 arena entries", "50", "GEMS", "\u2694"},
                {"Premium Weapon Box", "Random rare+ weapon", "300", "GEMS", "\uD83D\uDCE6"}
            };
            case "Event" -> new String[][]{
                {"Event Currency x10", "Limited event currency", "1000", "GOLD", "\uD83C\uDF1F"},
                {"Event Weapon", "Limited event weapon", "5000", "GOLD", "\u2694"},
                {"Character Shard x5", "5 random character shards", "200", "GEMS", "\uD83D\uDC8E"},
                {"Special Skin", "Character cosmetic skin", "1000", "GEMS", "\uD83C\uDFC6"}
            };
            case "Premium" -> new String[][]{
                {"SSR Selection Box", "Choose any SSR character", "5000", "GEMS", "\uD83C\uDF1F"},
                {"Legendary Weapon", "Top tier weapon", "3000", "GEMS", "\u2694"},
                {"Mythic Armor", "Top tier armor", "3000", "GEMS", "\uD83D\uDEE1"},
                {"Growth Package", "EXP + Gold bundle", "2000", "GEMS", "\uD83D\uDCE6"}
            };
            default -> new String[][]{};
        };
    }

    private VBox createShopCard(String name, String desc, int cost, String currency, String icon, Runnable buyAction) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(14));
        card.getStyleClass().addAll("item-card", "shop-card");
        card.setPrefWidth(500);
        String borderColor = currency.equals("GEMS") ? "rgba(192,132,252,0.2)" : "rgba(251,191,36,0.2)";
        card.setStyle("-fx-border-color: " + borderColor + ";");

        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);
        Label iconLbl = new Label(icon);
        iconLbl.setFont(Font.font(24));
        VBox info = new VBox(2);
        Label nameLbl = new Label(name);
        nameLbl.setStyle("-fx-text-fill: #e2e8f0; -fx-font-size: 14px; -fx-font-weight: bold;");
        Label descLbl = new Label(desc);
        descLbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px;");
        info.getChildren().addAll(nameLbl, descLbl);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        String color = currency.equals("GEMS") ? "#c084fc" : "#fbbf24";
        String curIcon = currency.equals("GEMS") ? "\uD83D\uDC8E" : "\uD83D\uDCB0";
        Label priceLbl = new Label(curIcon + " " + cost);
        priceLbl.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: rgba(0,0,0,0.2); -fx-padding: 4 10; -fx-background-radius: 6;");

        Button buyBtn = new Button("Buy");
        buyBtn.getStyleClass().add("primary-button");
        buyBtn.setStyle("-fx-background-color: " + (currency.equals("GEMS") ? "linear-gradient(to right, #7c3aed, #a855f7)" : "linear-gradient(to right, #d97706, #f59e0b)") + ";");
        buyBtn.setOnAction(e -> buyAction.run());

        topRow.getChildren().addAll(iconLbl, info, spacer, priceLbl, buyBtn);
        card.getChildren().add(topRow);
        return card;
    }

    private void buyItem(String name, int cost, String currency, String icon) {
        boolean success = false;
        if (currency.equals("GOLD")) {
            if (player.spendGold(cost)) success = true;
        } else if (currency.equals("GEMS")) {
            if (player.spendGems(cost)) success = true;
        }
        if (success) {
            String itemId = name.toLowerCase().replace(" ", "_").replace("x", "");
            Item newItem = new Item(itemId, name, "Consumable", "Purchased from " + activeTab + " shop", "Common", cost);
            if (name.contains("Ticket")) {
                newItem = new Item(itemId, name, "Material", "Used for summoning", "Rare", cost);
                newItem.setQuantity(name.contains("x10") ? 10 : 1);
            }
            newItem.setIcon(icon);
            inventoryManager.addItem(newItem);
            showToast("Purchased " + name + "!");
        } else {
            showToast("Not enough " + currency + "!");
        }
    }

    private void showToast(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null); a.setContentText(msg);
        a.getDialogPane().setStyle("-fx-background-color: #1e293b; -fx-text-fill: #fff;");
        a.showAndWait();
    }
}
