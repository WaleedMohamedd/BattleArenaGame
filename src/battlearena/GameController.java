package battlearena;

import javafx.application.Platform;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.*;

public class GameController {
    // مقاسات الـ Arena والـ UI
    private static final double ARENA_WIDTH = 1000;
    private static final double ARENA_HEIGHT = 600;
    private static final double UI_HEIGHT = 80;

    // حالة الجيم
    private Fighter player1;
    private Fighter player2;
    private List<Projectile> projectiles;
    private Set<KeyCode> pressedKeys;
    private boolean gameRunning;
    private boolean fullMovementMode;
    private AnimationTimer gameLoop;
    private long lastFrameTime;

    // عناصر الـ UI
    private Pane gamePane;
    private BorderPane root;
    private ProgressBar health1Bar;
    private ProgressBar health2Bar;
    private Label health1Label;
    private Label health2Label;
    private Label weapon1Label;
    private Label weapon2Label;
    private Label winnerLabel;
    private VBox gameOverPanel;
    private Line middleLine;

    // علشان نبدّل بين الـ Scenes
    private Stage stage;
    private Scene selectionScene;

    public GameController(Stage stage, Scene selectionScene) {
        this.stage = stage;
        this.selectionScene = selectionScene;
        this.projectiles = new ArrayList<>();
        this.pressedKeys = new HashSet<>();
        this.gameRunning = false;
        this.fullMovementMode = true;
    }

    // بيجهّز Scene اللعبة بكل الـ UI
    public Scene createGameScene(String fighter1Type, String fighter2Type) {
        player1 = createFighter(fighter1Type, 1);
        player2 = createFighter(fighter2Type, 2);

        player1.setPosition(100, ARENA_HEIGHT / 2 - 20);
        player2.setPosition(ARENA_WIDTH - 150, ARENA_HEIGHT / 2 - 20);

        player1.rotateTo(0);
        player2.rotateTo(180);

        root = new BorderPane();
        root.setStyle("-fx-background-color: #1a1a2e;");

        HBox topPanel = createTopPanel();
        root.setTop(topPanel);

        gamePane = new Pane();
        gamePane.setPrefSize(ARENA_WIDTH, ARENA_HEIGHT);
        gamePane.setStyle("-fx-background-color: #16213e;");

        createArenaVisuals();

        gamePane.getChildren().addAll(player1.getVisual(), player2.getVisual());

        createGameOverPanel();

        StackPane gameStack = new StackPane(gamePane, gameOverPanel);
        root.setCenter(gameStack);

        HBox bottomPanel = createBottomPanel();
        root.setBottom(bottomPanel);

        Scene gameScene = new Scene(root, ARENA_WIDTH, ARENA_HEIGHT + UI_HEIGHT * 2);

        setupInputHandling(gameScene);
        initializeGameLoop();

        gamePane.setFocusTraversable(true);
        Platform.runLater(() -> gamePane.requestFocus());

        return gameScene;
    }

    private Fighter createFighter(String type, int playerNumber) {
        switch (type.toLowerCase()) {
            case "warrior":
                return new Warrior(playerNumber);
            case "mage":
                return new Mage(playerNumber);
            case "archer":
                return new Archer(playerNumber);
            default:
                return new Warrior(playerNumber);
        }
    }

    private HBox createTopPanel() {
        HBox topPanel = new HBox(20);
        topPanel.setPadding(new Insets(10));
        topPanel.setAlignment(Pos.CENTER);
        topPanel.setStyle("-fx-background-color: #0f0f23;");
        topPanel.setPrefHeight(UI_HEIGHT);

        VBox player1Box = createPlayerInfoBox(1);

        Label vsLabel = new Label("VS");
        vsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        vsLabel.setTextFill(Color.WHITE);

        VBox player2Box = createPlayerInfoBox(2);

        // زرار تغيير وضع الحركة (Full / Split)
        Button modeButton = new Button("Mode: Full Arena");
        modeButton.setStyle("-fx-background-color: #4a4a8a; -fx-text-fill: white;");
        modeButton.setOnAction(e -> {
            fullMovementMode = !fullMovementMode;
            modeButton.setText(fullMovementMode ? "Mode: Full Arena" : "Mode: Split Arena");
            middleLine.setVisible(!fullMovementMode);
        });

        HBox.setHgrow(player1Box, Priority.ALWAYS);
        HBox.setHgrow(player2Box, Priority.ALWAYS);

        topPanel.getChildren().addAll(player1Box, vsLabel, player2Box, modeButton);

        return topPanel;
    }

    private VBox createPlayerInfoBox(int playerNum) {
        VBox box = new VBox(5);
        box.setAlignment(playerNum == 1 ? Pos.CENTER_LEFT : Pos.CENTER_RIGHT);

        Fighter player = playerNum == 1 ? player1 : player2;

        Label nameLabel = new Label("Player " + playerNum + " - " + player.getName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        nameLabel.setTextFill(player.getColor());

        ProgressBar healthBar = new ProgressBar(1.0);
        healthBar.setPrefWidth(200);
        healthBar.setStyle("-fx-accent: " + toHexString(player.getColor()) + ";");

        Label healthLabel = new Label("HP: " + (int)player.getHealth() + "/" + (int)player.getMaxHealth());
        healthLabel.setTextFill(Color.WHITE);

        Label weaponLabel = new Label("Weapon: " + player.getCurrentWeapon().getName());
        weaponLabel.setTextFill(Color.LIGHTGRAY);

        if (playerNum == 1) {
            health1Bar = healthBar;
            health1Label = healthLabel;
            weapon1Label = weaponLabel;
        } else {
            health2Bar = healthBar;
            health2Label = healthLabel;
            weapon2Label = weaponLabel;
        }

        box.getChildren().addAll(nameLabel, healthBar, healthLabel, weaponLabel);
        return box;
    }

    private String toHexString(Color color) {
        return String.format("#%02X%02X%02X",
                (int)(color.getRed() * 255),
                (int)(color.getGreen() * 255),
                (int)(color.getBlue() * 255));
    }

    private HBox createBottomPanel() {
        HBox bottomPanel = new HBox(40);
        bottomPanel.setPadding(new Insets(10));
        bottomPanel.setAlignment(Pos.CENTER);
        bottomPanel.setStyle("-fx-background-color: #0f0f23;");
        bottomPanel.setPrefHeight(UI_HEIGHT);

        Label p1Controls = new Label("P1: WASD=Move, F=Shoot, R/T=Switch Weapon");
        p1Controls.setTextFill(Color.LIGHTBLUE);

        Label p2Controls = new Label("P2: Arrows=Move, L=Shoot, ;/'=Switch Weapon");
        p2Controls.setTextFill(Color.LIGHTCORAL);

        Label escHint = new Label("ESC=Back to Menu");
        escHint.setTextFill(Color.GRAY);

        bottomPanel.getChildren().addAll(p1Controls, p2Controls, escHint);

        return bottomPanel;
    }

    private void createArenaVisuals() {
        Rectangle floor1 = new Rectangle(0, 0, ARENA_WIDTH / 2, ARENA_HEIGHT);
        floor1.setFill(Color.rgb(30, 40, 60));

        Rectangle floor2 = new Rectangle(ARENA_WIDTH / 2, 0, ARENA_WIDTH / 2, ARENA_HEIGHT);
        floor2.setFill(Color.rgb(40, 30, 60));

        // خط النص اللي بيقسم الـ Arena في وضع الـ Split
        middleLine = new Line(ARENA_WIDTH / 2, 0, ARENA_WIDTH / 2, ARENA_HEIGHT);
        middleLine.setStroke(Color.YELLOW);
        middleLine.setStrokeWidth(3);
        middleLine.getStrokeDashArray().addAll(15.0, 10.0);
        middleLine.setVisible(!fullMovementMode);

        Rectangle border = new Rectangle(0, 0, ARENA_WIDTH, ARENA_HEIGHT);
        border.setFill(Color.TRANSPARENT);
        border.setStroke(Color.WHITE);
        border.setStrokeWidth(3);

        gamePane.getChildren().addAll(floor1, floor2, middleLine, border);
    }

    private void createGameOverPanel() {
        gameOverPanel = new VBox(20);
        gameOverPanel.setAlignment(Pos.CENTER);
        gameOverPanel.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8);");
        gameOverPanel.setVisible(false);

        winnerLabel = new Label();
        winnerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        winnerLabel.setTextFill(Color.GOLD);

        Button playAgainBtn = new Button("Play Again");
        playAgainBtn.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        playAgainBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 10 30;");
        playAgainBtn.setOnAction(e -> restartGame());

        Button menuBtn = new Button("Back to Menu");
        menuBtn.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        menuBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-padding: 10 30;");
        menuBtn.setOnAction(e -> {
            stopGame();
            stage.setScene(selectionScene);
        });

        Button exitBtn = new Button("Exit Game");
        exitBtn.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        exitBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-padding: 10 30;");
        exitBtn.setOnAction(e -> System.exit(0));

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(playAgainBtn, menuBtn, exitBtn);

        playAgainBtn.setFocusTraversable(false);
        menuBtn.setFocusTraversable(false);
        exitBtn.setFocusTraversable(false);

        gameOverPanel.getChildren().addAll(winnerLabel, buttonBox);
    }

    private void setupInputHandling(Scene scene) {
        scene.setOnKeyPressed(e -> {
            pressedKeys.add(e.getCode());

            // سويتش السلاح بيحصل مرة واحدة مع ضغطة الزرار
            if (gameRunning) {
                switch (e.getCode()) {
                    case R:
                        player1.switchWeaponPrevious();
                        updateWeaponLabel(1);
                        break;
                    case T:
                        player1.switchWeaponNext();
                        updateWeaponLabel(1);
                        break;
                    case SEMICOLON:
                        player2.switchWeaponPrevious();
                        updateWeaponLabel(2);
                        break;
                    case QUOTE:
                        player2.switchWeaponNext();
                        updateWeaponLabel(2);
                        break;
                    case ESCAPE:
                        stopGame();
                        stage.setScene(selectionScene);
                        break;
                    default:
                        break;
                }
            }
        });

        scene.setOnKeyReleased(e -> {
            pressedKeys.remove(e.getCode());
        });
    }

    private void updateWeaponLabel(int playerNum) {
        if (playerNum == 1) {
            weapon1Label.setText("Weapon: " + player1.getCurrentWeapon().getName());
        } else {
            weapon2Label.setText("Weapon: " + player2.getCurrentWeapon().getName());
        }
    }

    private void initializeGameLoop() {
        lastFrameTime = System.nanoTime();
        gameRunning = true;

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!gameRunning) return;

                double deltaTime = (now - lastFrameTime) / 1_000_000_000.0;
                lastFrameTime = now;

                deltaTime = Math.min(deltaTime, 0.05);

                handleInput(deltaTime);
                updateProjectiles(deltaTime);
                checkCollisions();
                updateUI();
                checkWinCondition();
            }
        };

        gameLoop.start();
    }

    private void handleInput(double deltaTime) {
        double dx1 = 0, dy1 = 0;
        if (pressedKeys.contains(KeyCode.W)) dy1 -= 1;
        if (pressedKeys.contains(KeyCode.S)) dy1 += 1;
        if (pressedKeys.contains(KeyCode.A)) dx1 -= 1;
        if (pressedKeys.contains(KeyCode.D)) dx1 += 1;

        // علشان الحركة القطرية متبقاش أسرع
        if (dx1 != 0 && dy1 != 0) {
            dx1 *= 0.7071;
            dy1 *= 0.7071;
        }

        double minX1 = 0;
        double maxX1 = fullMovementMode ? ARENA_WIDTH : ARENA_WIDTH / 2 - 5;
        player1.move(dx1, dy1, minX1, maxX1, 0, ARENA_HEIGHT);

        if (pressedKeys.contains(KeyCode.F)) {
            // توجيه تلقائي ناحية الخصم وقت الضرب
            player1.rotateTowards(player2.getX() + player2.getWidth() / 2,
                    player2.getY() + player2.getHeight() / 2);
            Projectile p = player1.shoot();
            if (p != null) {
                projectiles.add(p);
                gamePane.getChildren().add(p.getVisual());
            }
        }

        double dx2 = 0, dy2 = 0;
        if (pressedKeys.contains(KeyCode.NUMPAD8)) dy2 -= 1;
        if (pressedKeys.contains(KeyCode.NUMPAD5)) dy2 += 1;
        if (pressedKeys.contains(KeyCode.NUMPAD4)) dx2 -= 1;
        if (pressedKeys.contains(KeyCode.NUMPAD6)) dx2 += 1;

        // علشان الحركة القطرية متبقاش أسرع
        if (dx2 != 0 && dy2 != 0) {
            dx2 *= 0.7071;
            dy2 *= 0.7071;
        }

        double minX2 = fullMovementMode ? 0 : ARENA_WIDTH / 2 + 5;
        double maxX2 = ARENA_WIDTH;
        player2.move(dx2, dy2, minX2, maxX2, 0, ARENA_HEIGHT);

        if (pressedKeys.contains(KeyCode.L)) {
            // توجيه تلقائي ناحية الخصم وقت الضرب
            player2.rotateTowards(player1.getX() + player1.getWidth() / 2,
                    player1.getY() + player1.getHeight() / 2);
            Projectile p = player2.shoot();
            if (p != null) {
                projectiles.add(p);
                gamePane.getChildren().add(p.getVisual());
            }
        }
    }

    private void updateProjectiles(double deltaTime) {
        Iterator<Projectile> iterator = projectiles.iterator();
        while (iterator.hasNext()) {
            Projectile p = iterator.next();
            p.update(deltaTime);

            // امسح الطلقات اللي خرجت بره حدود الـ Arena
            if (p.isOutOfBounds(ARENA_WIDTH, ARENA_HEIGHT)) {
                gamePane.getChildren().remove(p.getVisual());
                iterator.remove();
            }
        }
    }

    private void checkCollisions() {
        Iterator<Projectile> iterator = projectiles.iterator();
        while (iterator.hasNext()) {
            Projectile p = iterator.next();

            if (p.getOwner() != player1 && p.checkCollision(player1)) {
                player1.takeDamage(p.getDamage());
                p.setActive(false);
                gamePane.getChildren().remove(p.getVisual());
                iterator.remove();
                continue;
            }

            if (p.getOwner() != player2 && p.checkCollision(player2)) {
                player2.takeDamage(p.getDamage());
                p.setActive(false);
                gamePane.getChildren().remove(p.getVisual());
                iterator.remove();
            }
        }
    }

    private void updateUI() {
        health1Bar.setProgress(player1.getHealthPercentage());
        health2Bar.setProgress(player2.getHealthPercentage());

        health1Label.setText("HP: " + (int)player1.getHealth() + "/" + (int)player1.getMaxHealth());
        health2Label.setText("HP: " + (int)player2.getHealth() + "/" + (int)player2.getMaxHealth());

        updateHealthBarColor(health1Bar, player1.getHealthPercentage());
        updateHealthBarColor(health2Bar, player2.getHealthPercentage());
    }

    private void updateHealthBarColor(ProgressBar bar, double percentage) {
        if (percentage < 0.25) {
            bar.setStyle("-fx-accent: #e74c3c;"); // Red
        } else if (percentage < 0.5) {
            bar.setStyle("-fx-accent: #f39c12;"); // Orange
        } else {
            bar.setStyle("-fx-accent: #27ae60;"); // Green
        }
    }

    private void checkWinCondition() {
        if (player1.isDead() || player2.isDead()) {
            gameRunning = false;

            String winner;
            if (player1.isDead() && player2.isDead()) {
                winner = "It's a Draw!";
            } else if (player1.isDead()) {
                winner = "Player 2 Wins!";
            } else {
                winner = "Player 1 Wins!";
            }

            winnerLabel.setText(winner);
            gameOverPanel.setVisible(true);
        }
    }

    private void restartGame() {
        for (Projectile p : projectiles) {
            gamePane.getChildren().remove(p.getVisual());
        }
        projectiles.clear();

        player1.reset();
        player2.reset();
        player1.setPosition(100, ARENA_HEIGHT / 2 - 20);
        player2.setPosition(ARENA_WIDTH - 150, ARENA_HEIGHT / 2 - 20);
        player1.rotateTo(0);
        player2.rotateTo(180);

        updateWeaponLabel(1);
        updateWeaponLabel(2);

        gameOverPanel.setVisible(false);

        pressedKeys.clear();

        gameRunning = true;
        lastFrameTime = System.nanoTime();

        Platform.runLater(() -> gamePane.requestFocus());
    }

    public void stopGame() {
        gameRunning = false;
        if (gameLoop != null) {
            gameLoop.stop();
        }
        pressedKeys.clear();
    }

    public void setFullMovementMode(boolean enabled) {
        this.fullMovementMode = enabled;
        if (middleLine != null) {
            middleLine.setVisible(!enabled);
        }
    }
}