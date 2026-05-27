package com.feyydev.scenes;

import com.feyydev.Main.SceneType;
import com.feyydev.managers.*;
import com.feyydev.models.*;
import com.feyydev.services.MailService;
import com.feyydev.utils.Constants;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.function.Consumer;

public class HomeScene {
    private final Scene scene;
    private final Player player;
    private final Consumer<SceneType> navigator;
    private final BorderPane root;
    private final StackPane modalLayer;

    // Header
    private final Label goldLabel;
    private final Label gemsLabel;
    private final Label energyLabel;
    private final Label levelLabel;
    private final Label userNameLabel;
    private final ProgressBar expBar;
    private final Label expLabel;
    private final VBox avatarBox;

    // Character
    private final StackPane charPortraitWrap;
    private final Label charEmoji;
    private final DropShadow charGlow;
    private final Label charRarityLabel;
    private final Label charNameLabel;
    private final Label charLevelLabel;
    private final Label charStatsLabel;

    // Modal
    private final StackPane profileModal;

    public HomeScene(Player player, Consumer<SceneType> navigator) {
        this.player = player;
        this.navigator = navigator;
        this.root = new BorderPane();
        this.modalLayer = new StackPane();
        this.modalLayer.setMouseTransparent(true);

        this.goldLabel = new Label();
        this.gemsLabel = new Label();
        this.energyLabel = new Label();
        this.levelLabel = new Label();
        this.userNameLabel = new Label();
        this.expBar = new ProgressBar();
        this.expLabel = new Label();
        this.avatarBox = new VBox();

        this.charPortraitWrap = new StackPane();
        this.charEmoji = new Label();
        this.charGlow = new DropShadow();
        this.charRarityLabel = new Label();
        this.charNameLabel = new Label();
        this.charLevelLabel = new Label();
        this.charStatsLabel = new Label();

        this.profileModal = new StackPane();

        this.scene = buildScene();
    }

    public Scene getScene() { return scene; }

    public void refresh() {
        EventManager em = EventManager.getInstance();
        em.updateProgress("LOGIN", 1);
        em.updateProgress("PLAYER_LEVEL", player.getLevel());
        em.updateProgress("POWER", (int) player.getTotalPower());
        updateLabels();
        updateCharacterDisplay();
        updateHeaderAvatar();
    }

    // ═══════════════════ SCENE ═══════════════════

    private Scene buildScene() {
        root.setPrefSize(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        root.getStyleClass().add("root");

        // Background layer
        StackPane bgLayer = new StackPane();
        ImageView bg = new ImageView();
        try {
            Image img = new Image(getClass().getResource("/com/feyydev/assets/backgrounds/loading-homepage.png").toExternalForm());
            bg.setImage(img);
            bg.setPreserveRatio(false);
            bg.setFitWidth(Constants.WINDOW_WIDTH);
            bg.setFitHeight(Constants.WINDOW_HEIGHT);
        } catch (Exception ignored) {}
        bg.setEffect(new GaussianBlur(6));

        Region overlay = new Region();
        overlay.setStyle("-fx-background-color: rgba(8, 12, 24, 0.50);");
        overlay.setMouseTransparent(true);
        bgLayer.getChildren().addAll(bg, overlay);

        // BorderPane content
        root.setTop(buildHeader());
        root.setLeft(buildLeftRail());
        root.setCenter(buildCenter());
        root.setRight(buildRightRail());
        root.setBottom(buildBottomNav());

        // Wrap everything: bgLayer behind, content + modal on top
        StackPane wrapper = new StackPane();
        wrapper.getChildren().addAll(bgLayer, root, modalLayer);
        StackPane.setAlignment(root, Pos.TOP_LEFT);
        root.setPrefSize(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);

        // Profile modal (initially hidden)
        profileModal.setVisible(false);
        modalLayer.getChildren().add(profileModal);

        Scene s = new Scene(wrapper, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        s.getStylesheets().add(getClass().getResource("/com/feyydev/global.css").toExternalForm());
        s.getStylesheets().add(getClass().getResource("/com/feyydev/home.css").toExternalForm());
        return s;
    }

    // ═══════════════════ HEADER ═══════════════════

    private HBox buildHeader() {
        HBox header = new HBox(12);
        header.setPadding(new Insets(8, 20, 8, 20));
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("home-header");

        avatarBox.setPrefSize(36, 36);
        avatarBox.setAlignment(Pos.CENTER);
        avatarBox.setCursor(javafx.scene.Cursor.HAND);
        avatarBox.setPickOnBounds(true);
        avatarBox.getStyleClass().add("home-avatar");
        avatarBox.setOnMouseClicked(e -> showProfileDialog());
        updateHeaderAvatar();

        VBox nameBox = new VBox(1);
        userNameLabel.setText(player.getName());
        userNameLabel.getStyleClass().add("player-name");

        HBox levelRow = new HBox(8);
        levelRow.setAlignment(Pos.CENTER_LEFT);
        levelLabel.setText("Lv." + player.getLevel());
        levelLabel.getStyleClass().add("level-label");

        expBar.setPrefWidth(100);
        expBar.setPrefHeight(3);
        expBar.getStyleClass().add("exp-bar");
        expLabel.getStyleClass().add("hint-label");
        levelRow.getChildren().addAll(levelLabel, expBar, expLabel);
        nameBox.getChildren().addAll(userNameLabel, levelRow);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox currencyBox = new HBox(6);
        currencyBox.setAlignment(Pos.CENTER_RIGHT);
        currencyBox.getChildren().addAll(
            makeCurrency("\uD83D\uDCB0", goldLabel, "#fbbf24"),
            makeCurrency("\uD83D\uDC8E", gemsLabel, "#c084fc"),
            makeCurrency("\u26A1", energyLabel, "#60a5fa")
        );

        header.getChildren().addAll(avatarBox, nameBox, spacer, currencyBox);
        return header;
    }

    private VBox makeCurrency(String icon, Label value, String color) {
        VBox box = new VBox(0);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(2, 10, 2, 10));
        box.getStyleClass().add("home-currency");
        Label iconLbl = new Label(icon);
        iconLbl.setFont(Font.font(12));
        value.setText("0");
        value.setStyle("-fx-text-fill: " + color + ";");
        box.getChildren().addAll(iconLbl, value);
        return box;
    }

    // ═══════════════════ CENTER (CHARACTER) ═══════════════════

    private StackPane buildCenter() {
        StackPane center = new StackPane();
        center.setPadding(new Insets(0, 0, 0, 0));
        center.setAlignment(Pos.CENTER);

        VBox display = new VBox(10);
        display.setAlignment(Pos.CENTER);

        // Character portrait — large emoji with glow
        charPortraitWrap.setPrefSize(280, 340);
        charPortraitWrap.setAlignment(Pos.CENTER);
        charPortraitWrap.setCursor(javafx.scene.Cursor.HAND);
        charPortraitWrap.setPickOnBounds(true);
        charPortraitWrap.setOnMouseClicked(e -> navigator.accept(SceneType.CHARACTER));

        charGlow.setColor(Color.web("#3b82f6", 0.4));
        charGlow.setRadius(45);
        charGlow.setSpread(0.25);
        charGlow.setWidth(200);
        charGlow.setHeight(200);

        charEmoji.setFont(Font.font(200));
        charEmoji.setEffect(charGlow);
        charEmoji.setAlignment(Pos.CENTER);

        // Pulsing glow animation
        Timeline pulseGlow = new Timeline(
            new KeyFrame(Duration.ZERO, e -> {
                charGlow.setRadius(40);
                charGlow.setColor(Color.web("#3b82f6", 0.35));
            }),
            new KeyFrame(Duration.seconds(2), e -> {
                charGlow.setRadius(55);
                charGlow.setColor(Color.web("#a855f7", 0.3));
            }),
            new KeyFrame(Duration.seconds(4), e -> {
                charGlow.setRadius(40);
                charGlow.setColor(Color.web("#3b82f6", 0.35));
            })
        );
        pulseGlow.setCycleCount(Timeline.INDEFINITE);
        pulseGlow.play();

        TranslateTransition floatAnim = new TranslateTransition(Duration.seconds(3), charPortraitWrap);
        floatAnim.setToY(-8);
        floatAnim.setAutoReverse(true);
        floatAnim.setCycleCount(Timeline.INDEFINITE);
        floatAnim.setInterpolator(Interpolator.EASE_BOTH);
        floatAnim.play();

        charPortraitWrap.getChildren().add(charEmoji);

        charNameLabel.getStyleClass().add("char-name");
        charLevelLabel.getStyleClass().add("char-level");
        charStatsLabel.getStyleClass().add("char-stats");

        Label hint = new Label("\u25BC Tap for details");
        hint.getStyleClass().add("hint-label");
        hint.setOnMouseClicked(e -> navigator.accept(SceneType.CHARACTER));

        display.getChildren().addAll(charPortraitWrap, charRarityLabel, charNameLabel, charLevelLabel, charStatsLabel, hint);
        updateCharacterDisplay();

        center.getChildren().add(display);
        return center;
    }

    // ═══════════════════ LEFT RAIL ═══════════════════

    private VBox buildLeftRail() {
        VBox rail = new VBox(10);
        rail.setAlignment(Pos.CENTER);
        rail.setPadding(new Insets(0, 0, 0, 14));
        rail.setMaxWidth(60);
        rail.setMinWidth(60);
        rail.setPrefWidth(60);

        String[][] items = {
            {"\uD83C\uDF89", "EVENT"},
            {"\uD83D\uDCCB", "ATTENDANCE"},
            {"\uD83D\uDCEB", "MAILBOX"},
            {"\uD83D\uDEE0\uFE0F", "QUEST"},
        };

        for (String[] item : items) {
            StackPane btn = new StackPane();
            btn.setPrefSize(46, 46);
            btn.setPickOnBounds(true);
            btn.getStyleClass().add("side-rail-btn");

            Label icon = new Label(item[0]);
            icon.setFont(Font.font(18));
            btn.getChildren().add(icon);

            SceneType target = switch (item[1]) {
                case "EVENT" -> SceneType.EVENT;
                case "ATTENDANCE" -> SceneType.ATTENDANCE;
                case "MAILBOX" -> SceneType.MAILBOX;
                case "QUEST" -> SceneType.QUEST;
                default -> SceneType.HOME;
            };
            btn.setOnMouseClicked(e -> {
                System.out.println("[DEBUG] LeftRail clicked: " + item[1]);
                navigator.accept(target);
            });
            rail.getChildren().add(btn);
        }
        return rail;
    }

    // ═══════════════════ RIGHT RAIL ═══════════════════

    private VBox buildRightRail() {
        VBox rail = new VBox(10);
        rail.setAlignment(Pos.CENTER);
        rail.setPadding(new Insets(0, 14, 0, 0));
        rail.setMaxWidth(60);
        rail.setMinWidth(60);
        rail.setPrefWidth(60);

        String[][] items = {
            {"\u2694\uFE0F", "BATTLE"},
            {"\uD83C\uDFC6", "WORLD_MAP"},
            {"\uD83D\uDC7E", "RAID"},
            {"\uD83C\uDFAE", "HOME"},
        };

        for (String[] item : items) {
            StackPane btn = new StackPane();
            btn.setPrefSize(46, 46);
            btn.setPickOnBounds(true);
            btn.getStyleClass().add("side-rail-btn-purple");

            Label icon = new Label(item[0]);
            icon.setFont(Font.font(18));
            btn.getChildren().add(icon);

            SceneType target = switch (item[1]) {
                case "BATTLE" -> SceneType.BATTLE;
                case "WORLD_MAP" -> SceneType.WORLD_MAP;
                case "RAID" -> SceneType.RAID;
                default -> SceneType.HOME;
            };
            btn.setOnMouseClicked(e -> {
                System.out.println("[DEBUG] RightRail clicked: " + item[1]);
                navigator.accept(target);
            });
            rail.getChildren().add(btn);
        }
        return rail;
    }

    // ═══════════════════ BOTTOM NAV ═══════════════════

    private HBox buildBottomNav() {
        HBox nav = new HBox(0);
        nav.setAlignment(Pos.CENTER);
        nav.setPrefHeight(80);
        nav.setMinHeight(80);
        nav.setMaxHeight(80);
        nav.getStyleClass().add("home-bottom-nav");

        String[][] items = {
            {"\uD83C\uDFE0", "Home", "HOME"},
            {"\uD83D\uDC64", "Heroes", "CHARACTER"},
            {"\uD83C\uDF81", "Summon", "GACHA"},
            {"\uD83D\uDCD6", "Story", "STORY"},
            {"\uD83D\uDED2", "Shop", "SHOP"},
            {"\uD83C\uDF0D", "World", "WORLD_MAP"},
        };

        for (String[] item : items) {
            VBox btn = new VBox(2);
            btn.setAlignment(Pos.CENTER);
            btn.setPrefWidth(90);
            btn.setPrefHeight(70);
            btn.setPadding(new Insets(6, 8, 4, 8));
            btn.setCursor(javafx.scene.Cursor.HAND);
            btn.setPickOnBounds(true);

            Label icon = new Label(item[0]);
            icon.setFont(Font.font(32));

            Label label = new Label(item[1]);
            label.setFont(Font.font(14));
            label.setStyle("-fx-font-weight: bold;");

            boolean isHome = item[1].equals("Home");
            if (isHome) {
                btn.getStyleClass().add("nav-item-home");
                icon.getStyleClass().add("nav-icon-home");
                label.getStyleClass().add("nav-label-home");
            } else {
                icon.getStyleClass().add("nav-icon-inactive");
                label.getStyleClass().add("nav-label-inactive");
            }

            btn.getChildren().addAll(icon, label);
            SceneType t = SceneType.valueOf(item[2]);
            btn.setOnMouseClicked(e -> {
                System.out.println("[DEBUG] BottomNav clicked: " + item[1]);
                navigator.accept(t);
            });
            nav.getChildren().add(btn);
        }
        return nav;
    }

    // ═══════════════════ UPDATES ═══════════════════

    public void updateLabels() {
        goldLabel.setText(Constants.formatNumber(player.getGold()));
        gemsLabel.setText(Constants.formatNumber(player.getGems()));
        energyLabel.setText(player.getEnergy() + "/" + player.getMaxEnergy());
        levelLabel.setText("Lv." + player.getLevel());
        userNameLabel.setText(player.getName());
        if (player.getExpToNext() > 0) {
            expBar.setProgress(player.getExpProgress());
        }
        expLabel.setText(Constants.formatNumber(player.getExp()) + " / " + Constants.formatNumber(player.getExpToNext()));
    }

    private void updateCharacterDisplay() {
        GameCharacter c = player.getEquippedCharacter();
        if (c == null) {
            charEmoji.setText("\uD83D\uDC64");
            charRarityLabel.setText("");
            charNameLabel.setText("No Hero");
            charLevelLabel.setText("");
            charStatsLabel.setText("Tap to summon your first hero!");
            return;
        }

        String iconStr = player.getProfileImagePath() != null && new File(player.getProfileImagePath()).exists()
            ? "\uD83D\uDC64"
            : Constants.getCharIcon(c.getName());
        charEmoji.setText(iconStr);

        charRarityLabel.setText(c.getRarity());
        charRarityLabel.getStyleClass().removeAll("char-rarity-ssr", "char-rarity-sr", "char-rarity-r");
        charRarityLabel.getStyleClass().add("char-rarity-" + c.getRarity().toLowerCase());
        charNameLabel.setText(c.getName());
        charLevelLabel.setText("Lv." + c.getLevel());
        String hpStr = c.getMaxHp() > 0 ? "HP " + Constants.formatNumber(c.getHp()) + "/" + Constants.formatNumber(c.getMaxHp()) : "";
        charStatsLabel.setText("ATK " + Constants.formatNumber(c.getAttack()) + "  DEF " + Constants.formatNumber(c.getDefense()) + "  " + hpStr);
    }

    private void updateHeaderAvatar() {
        avatarBox.getChildren().clear();
        String path = player.getProfileImagePath();
        if (path != null) {
            File f = new File(path);
            if (f.exists()) {
                ImageView iv = new ImageView(new Image(f.toURI().toString(), 32, 32, true, true));
                iv.setClip(new Circle(16, 16, 16));
                avatarBox.getChildren().add(iv);
                return;
            }
        }
        Label emoji = new Label("\uD83D\uDC64");
        emoji.setFont(Font.font(18));
        avatarBox.getChildren().add(emoji);
    }

    // ═══════════════════ PROFILE MODAL ═══════════════════

    private void showProfileDialog() {
        modalLayer.setMouseTransparent(false);

        VBox card = new VBox(14);
        card.setPadding(new Insets(24));
        card.setMaxWidth(360);
        card.getStyleClass().add("glass-card");
        card.setAlignment(Pos.TOP_CENTER);

        StackPane avatarWrap = buildProfileAvatar(100);
        Button changeBtn = new Button("Change Photo");
        changeBtn.getStyleClass().addAll("action-button", "small-button");
        changeBtn.setOnAction(e -> uploadProfileImage(avatarWrap));

        Label idLabel = new Label("ID: " + (player.getPlayerId() != null ? player.getPlayerId() : "----"));
        idLabel.getStyleClass().add("scene-subtitle");

        HBox nameRow = new HBox(8);
        nameRow.setAlignment(Pos.CENTER);
        Label nameLbl = new Label("Name:");
        nameLbl.getStyleClass().add("scene-subtitle");
        TextField nameField = new TextField(player.getName());
        nameField.setPrefWidth(180);
        nameField.getStyleClass().add("text-field");
        Button saveNameBtn = new Button("Save");
        saveNameBtn.getStyleClass().add("primary-button");
        saveNameBtn.setOnAction(ev -> {
            String n = nameField.getText().trim();
            if (!n.isEmpty()) {
                player.setName(n);
                userNameLabel.setText(n);
                SaveManager.getInstance().saveGame(player);
                saveNameBtn.setText("Saved!");
            }
        });
        nameRow.getChildren().addAll(nameLbl, nameField, saveNameBtn);

        Label statsLabel = new Label("Lv." + player.getLevel() + "  |  "
            + Constants.formatNumber(player.getGold()) + " Gold  |  "
            + Constants.formatNumber(player.getGems()) + " Gems");
        statsLabel.getStyleClass().add("scene-subtitle");

        Label progressLabel = new Label("Chapter " + player.getCurrentChapter()
            + "-" + player.getCurrentStage() + "  |  Power: " + Constants.formatNumber(player.calcTotalPower()));
        progressLabel.getStyleClass().add("hint-label");

        Button closeBtn = new Button("Close");
        closeBtn.getStyleClass().add("action-button");
        closeBtn.setOnAction(e -> hideProfileModal());

        card.setPickOnBounds(true);
        card.getChildren().addAll(avatarWrap, changeBtn, idLabel, nameRow, statsLabel, progressLabel, closeBtn);

        Region backdrop = new Region();
        backdrop.setStyle("-fx-background-color: rgba(0,0,0,0.6);");
        backdrop.setOnMouseClicked(e -> hideProfileModal());

        profileModal.getChildren().setAll(backdrop, card);
        StackPane.setAlignment(card, Pos.CENTER);
        profileModal.setVisible(true);

        FadeTransition ft = new FadeTransition(Duration.millis(200), profileModal);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    private void hideProfileModal() {
        modalLayer.setMouseTransparent(true);
        profileModal.setVisible(false);
        profileModal.getChildren().clear();
    }

    private StackPane buildProfileAvatar(double size) {
        StackPane wrap = new StackPane();
        wrap.setPrefSize(size, size);
        wrap.setAlignment(Pos.CENTER);
        wrap.getStyleClass().add("home-avatar");
        String path = player.getProfileImagePath();
        if (path != null) {
            File f = new File(path);
            if (f.exists()) {
                ImageView iv = new ImageView(new Image(f.toURI().toString()));
                iv.setFitWidth(size);
                iv.setFitHeight(size);
                iv.setClip(new Circle(size / 2, size / 2, size / 2));
                wrap.getChildren().add(iv);
                return wrap;
            }
        }
        Label emoji = new Label("\uD83D\uDC64");
        emoji.setFont(Font.font(size * 0.4));
        wrap.getChildren().add(emoji);
        return wrap;
    }

    private void uploadProfileImage(StackPane avatarWrap) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Choose Profile Picture");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        File file = fc.showOpenDialog(scene.getWindow());
        if (file == null) return;
        try {
            File dir = new File(Constants.PROFILES_DIR);
            dir.mkdirs();
            String name = file.getName();
            String ext = name.contains(".") ? name.substring(name.lastIndexOf('.')) : ".png";
            String pid = player.getPlayerId() != null ? player.getPlayerId() : "default";
            File dest = new File(dir, pid + ext);
            Files.copy(file.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
            player.setProfileImagePath(dest.getAbsolutePath());
            SaveManager.getInstance().saveGame(player);
            updateHeaderAvatar();
            updateCharacterDisplay();

            avatarWrap.getChildren().clear();
            ImageView iv = new ImageView(new Image(dest.toURI().toString()));
            iv.setFitWidth(100);
            iv.setFitHeight(100);
            iv.setClip(new Circle(50, 50, 50));
            avatarWrap.getChildren().add(iv);
        } catch (IOException e) {
            System.err.println("Upload failed: " + e.getMessage());
        }
    }
}
