package com.feyydev.scenes;

import com.feyydev.Main.SceneType;
import com.feyydev.services.AudioManager;
import com.feyydev.utils.Constants;
import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class SplashScene {
    private final Scene scene;
    private final Consumer<SceneType> navigator;
    private final StackPane root;
    private final Pane particleLayer;
    private final Random random;
    private Timeline particleTimeline;

    public SplashScene(Consumer<SceneType> navigator) {
        this.navigator = navigator;
        this.random = new Random();
        this.root = new StackPane();
        this.particleLayer = new Pane();
        this.particleLayer.setMouseTransparent(true);
        this.scene = new Scene(root, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        buildScene();
    }

    public Scene getScene() { return scene; }

    private void buildScene() {
        // ── Background image (full screen, cover mode) ──
        try {
            Image img = new Image(getClass().getResource("/com/feyydev/assets/backgrounds/loading-homepage.png").toExternalForm());
            ImageView bg = new ImageView(img);
            bg.setPreserveRatio(false);
            bg.setFitWidth(Constants.WINDOW_WIDTH);
            bg.setFitHeight(Constants.WINDOW_HEIGHT);
            root.getChildren().add(bg);
        } catch (Exception e) {
            Region fallback = new Region();
            fallback.setStyle("-fx-background-color: linear-gradient(to bottom, #0a0e1a, #0f1729, #1a2744);");
            root.getChildren().add(fallback);
        }

        // ── Dark vignette overlay (visual only — don't block clicks) ──
        Region overlay = new Region();
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.4);");
        overlay.setMouseTransparent(true);
        root.getChildren().add(overlay);

        // ── Particle layer ──
        root.getChildren().add(particleLayer);

        // ── Logo ──
        Text logo = new Text(Constants.GAME_TITLE + "\nFan Game");
        logo.setFill(Color.WHITE);
        logo.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 44));
        logo.setTextAlignment(TextAlignment.CENTER);

        Glow glow = new Glow(0.4);
        logo.setEffect(glow);

        Timeline glowAnim = new Timeline(
            new KeyFrame(Duration.ZERO, e -> glow.setLevel(0.3)),
            new KeyFrame(Duration.seconds(1.5), e -> glow.setLevel(0.9)),
            new KeyFrame(Duration.seconds(3), e -> glow.setLevel(0.3))
        );
        glowAnim.setCycleCount(Timeline.INDEFINITE);
        glowAnim.play();

        // ── Subtitle ──
        Text subtitle = new Text("Fanmade JavaFX RPG");
        subtitle.setFill(Color.web("#94a3b8"));
        subtitle.setFont(Font.font("Segoe UI", FontWeight.LIGHT, 16));

        VBox centerBox = new VBox(10, logo, subtitle);
        centerBox.setAlignment(Pos.CENTER);
        root.getChildren().add(centerBox);

        // ── Bottom text ──
        Text bottomText = new Text("Fanmade Project");
        bottomText.setFill(Color.web("#64748b", 0.7));
        bottomText.setFont(Font.font("Segoe UI", 12));
        StackPane.setAlignment(bottomText, Pos.BOTTOM_CENTER);
        bottomText.setTranslateY(-24);
        root.getChildren().add(bottomText);

        // ── Particles ──
        spawnParticles();

        // ── Audio ──
        AudioManager.getInstance().playHomeBGM();

        // ── Entrance animation ──
        root.setOpacity(0);
        root.setScaleX(1.0);
        root.setScaleY(1.0);

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        ScaleTransition zoom = new ScaleTransition(Duration.seconds(3), root);
        zoom.setFromX(1.0);
        zoom.setFromY(1.0);
        zoom.setToX(1.05);
        zoom.setToY(1.05);
        zoom.setInterpolator(Interpolator.EASE_BOTH);

        ParallelTransition entrance = new ParallelTransition(fadeIn, zoom);
        entrance.play();

        scene.getStylesheets().add(getClass().getResource("/com/feyydev/global.css").toExternalForm());
        root.getStyleClass().add("root");

        // ── Exit after 3 seconds ──
        Timeline timer = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(800), root);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(ev -> {
                stopParticles();
                navigator.accept(SceneType.HOME);
            });
            fadeOut.play();
        }));
        timer.play();
    }

    private void spawnParticles() {
        List<Circle> particles = new ArrayList<>();
        for (int i = 0; i < 35; i++) {
            Circle c = new Circle(2 + random.nextDouble() * 3);
            Color color = random.nextBoolean() ? Color.web("#3b82f6", 0.55) : Color.web("#a855f7", 0.55);
            c.setFill(color);
            c.setCenterX(random.nextDouble() * Constants.WINDOW_WIDTH);
            c.setCenterY(random.nextDouble() * Constants.WINDOW_HEIGHT);
            particleLayer.getChildren().add(c);
            particles.add(c);
        }

        particleTimeline = new Timeline();
        for (Circle c : particles) {
            double speed = 8 + random.nextDouble() * 18;
            double driftX = (random.nextDouble() - 0.5) * 8;
            particleTimeline.getKeyFrames().add(
                new KeyFrame(Duration.millis(50), e -> {
                    c.setOpacity(0.2 + random.nextDouble() * 0.3);
                    c.setCenterX(c.getCenterX() + driftX * 0.1);
                    c.setCenterY(c.getCenterY() - speed * 0.1);
                    if (c.getCenterY() < -10) {
                        c.setCenterY(Constants.WINDOW_HEIGHT + 10);
                        c.setCenterX(random.nextDouble() * Constants.WINDOW_WIDTH);
                    }
                    if (c.getCenterX() < -10) c.setCenterX(Constants.WINDOW_WIDTH + 10);
                    if (c.getCenterX() > Constants.WINDOW_WIDTH + 10) c.setCenterX(-10);
                })
            );
        }
        particleTimeline.setCycleCount(Timeline.INDEFINITE);
        particleTimeline.play();
    }

    private void stopParticles() {
        if (particleTimeline != null) {
            particleTimeline.stop();
        }
    }
}
