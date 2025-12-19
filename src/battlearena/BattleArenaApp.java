package battlearena;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class BattleArenaApp extends Application {

    private Stage primaryStage;
    private Scene selectionScene;
    private GameController gameController;

    // عناصر اختيار الشخصيات + عرض الـ stats
    private ComboBox<String> player1Selection;
    private ComboBox<String> player2Selection;
    private Label player1InfoLabel;
    private Label player2InfoLabel;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("2D Battle Arena");
        primaryStage.setResizable(false);

        // شاشة الاختيار أول ما البرنامج يفتح
        selectionScene = createSelectionScene();
        primaryStage.setScene(selectionScene);
        primaryStage.show();
    }

    private Scene createSelectionScene() {
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #1a1a2e, #16213e);");

        Text title = new Text("⚔ BATTLE ARENA ⚔");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        title.setFill(Color.GOLD);

        Text subtitle = new Text("Select Your Fighters");
        subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 24));
        subtitle.setFill(Color.LIGHTGRAY);

        String[] fighterTypes = {"Warrior", "Mage", "Archer"};

        HBox playersBox = new HBox(80);
        playersBox.setAlignment(Pos.CENTER);

        VBox player1Panel = createPlayerSelectionPanel(1, fighterTypes);

        VBox vsBox = new VBox();
        vsBox.setAlignment(Pos.CENTER);
        Label vsLabel = new Label("VS");
        vsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        vsLabel.setTextFill(Color.RED);
        vsBox.getChildren().add(vsLabel);

        VBox player2Panel = createPlayerSelectionPanel(2, fighterTypes);

        playersBox.getChildren().addAll(player1Panel, vsBox, player2Panel);

        HBox infoBox = new HBox(100);
        infoBox.setAlignment(Pos.CENTER);

        player1InfoLabel = createInfoLabel("Warrior");
        player2InfoLabel = createInfoLabel("Warrior");

        VBox info1Box = new VBox(5);
        info1Box.setAlignment(Pos.CENTER);
        Label info1Title = new Label("Player 1 Stats:");
        info1Title.setTextFill(Color.LIGHTBLUE);
        info1Title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        info1Box.getChildren().addAll(info1Title, player1InfoLabel);

        VBox info2Box = new VBox(5);
        info2Box.setAlignment(Pos.CENTER);
        Label info2Title = new Label("Player 2 Stats:");
        info2Title.setTextFill(Color.LIGHTCORAL);
        info2Title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        info2Box.getChildren().addAll(info2Title, player2InfoLabel);

        infoBox.getChildren().addAll(info1Box, info2Box);

        Button startButton = new Button("START BATTLE!");
        startButton.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        startButton.setStyle("-fx-background-color: linear-gradient(to bottom, #4CAF50, #2E7D32);" + "-fx-text-fill: white;" + "-fx-padding: 15 50;" + "-fx-background-radius: 10;" + "-fx-cursor: hand;");
        startButton.setOnMouseEntered(e -> startButton.setStyle("-fx-background-color: linear-gradient(to bottom, #66BB6A, #43A047);" + "-fx-text-fill: white;" + "-fx-padding: 15 50;" + "-fx-background-radius: 10;" + "-fx-cursor: hand;"));
        startButton.setOnMouseExited(e -> startButton.setStyle("-fx-background-color: linear-gradient(to bottom, #4CAF50, #2E7D32);" + "-fx-text-fill: white;" + "-fx-padding: 15 50;" + "-fx-background-radius: 10;" + "-fx-cursor: hand;"));
        startButton.setOnAction(e -> startGame());

        VBox controlsInfo = createControlsInfoPanel();

        Button exitButton = new Button("Exit");
        exitButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        exitButton.setStyle("-fx-background-color: #e74c3c;" + "-fx-text-fill: white;" + "-fx-padding: 8 25;");
        exitButton.setOnAction(e -> System.exit(0));

        root.getChildren().addAll(title, subtitle, playersBox, infoBox, startButton, controlsInfo, exitButton);

        return new Scene(root, 1000, 760);
    }

    private VBox createPlayerSelectionPanel(int playerNum, String[] fighterTypes) {
        VBox panel = new VBox(15);
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1);" + "-fx-background-radius: 15;");
        panel.setPrefWidth(250);

        Label playerLabel = new Label("PLAYER " + playerNum);
        playerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        playerLabel.setTextFill(playerNum == 1 ? Color.LIGHTBLUE : Color.LIGHTCORAL);

        Label selectLabel = new Label("Choose Fighter:");
        selectLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        selectLabel.setTextFill(Color.WHITE);

        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().addAll(fighterTypes);
        comboBox.setValue(fighterTypes[0]);
        comboBox.setPrefWidth(180);
        comboBox.setStyle("-fx-background-color: #34495e;" + "-fx-text-fill: white;" + "-fx-font-size: 14px;");

        if (playerNum == 1) {
            player1Selection = comboBox;
            comboBox.setOnAction(e -> updatePlayerInfo(1));
        } else {
            player2Selection = comboBox;
            comboBox.setOnAction(e -> updatePlayerInfo(2));
        }

        HBox fighterIcons = new HBox(10);
        fighterIcons.setAlignment(Pos.CENTER);

        for (String type : fighterTypes) {
            Button iconBtn = createFighterButton(type, playerNum, comboBox);
            fighterIcons.getChildren().add(iconBtn);
        }

        panel.getChildren().addAll(playerLabel, selectLabel, comboBox, fighterIcons);

        return panel;
    }

    // زرار سريع لاختيار الشخصية
    private Button createFighterButton(String type, int playerNum, ComboBox<String> comboBox) {
        Button btn = new Button(type.substring(0, 1));
        btn.setPrefSize(50, 50);
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        Color color;
        switch (type.toLowerCase()) {
            case "warrior":
                color = Color.DARKRED;
                break;
            case "mage":
                color = Color.DARKVIOLET;
                break;
            case "archer":
                color = Color.DARKGREEN;
                break;
            default:
                color = Color.GRAY;
        }

        String colorHex = String.format("#%02X%02X%02X", (int)(color.getRed() * 255), (int)(color.getGreen() * 255), (int)(color.getBlue() * 255));

        btn.setStyle("-fx-background-color: " + colorHex + ";" + "-fx-text-fill: white;" + "-fx-background-radius: 10;");

        btn.setOnAction(e -> {
            comboBox.setValue(type);
            updatePlayerInfo(playerNum);
        });

        Tooltip tooltip = new Tooltip(type);
        Tooltip.install(btn, tooltip);

        return btn;
    }

    private Label createInfoLabel(String type) {
        Label label = new Label(getFighterInfo(type));
        label.setTextFill(Color.LIGHTGRAY);
        label.setFont(Font.font("Monospace", 12));
        return label;
    }

    private String getFighterInfo(String type) {
        switch (type.toLowerCase()) {
            case "warrior":
                return "HP: 120 | Speed: 3.5\nWeapons: Cannon, Pistol\n\"High damage dealer\"";
            case "mage":
                return "HP: 90 | Speed: 4.0\nWeapons: Magic Staff, Cannon, Pistol\n\"Versatile fighter\"";
            case "archer":
                return "HP: 80 | Speed: 5.0\nWeapons: Bow, Pistol\n\"Fast and agile\"";
            default:
                return "";
        }
    }

    private void updatePlayerInfo(int playerNum) {
        if (playerNum == 1 && player1Selection != null) {
            player1InfoLabel.setText(getFighterInfo(player1Selection.getValue()));
        } else if (playerNum == 2 && player2Selection != null) {
            player2InfoLabel.setText(getFighterInfo(player2Selection.getValue()));
        }
    }

    private VBox createControlsInfoPanel() {
        VBox panel = new VBox(10);
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(15));
        panel.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3);" + "-fx-background-radius: 10;");

        Label title = new Label("CONTROLS");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        title.setTextFill(Color.WHITE);

        HBox controlsRow = new HBox(50);
        controlsRow.setAlignment(Pos.CENTER);

        VBox p1Controls = new VBox(3);
        p1Controls.setAlignment(Pos.CENTER_LEFT);
        Label p1Title = new Label("Player 1:");
        p1Title.setTextFill(Color.LIGHTBLUE);
        p1Title.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        Label p1Move = new Label("Move: W/A/S/D");
        p1Move.setTextFill(Color.WHITE);
        Label p1Shoot = new Label("Shoot: F");
        p1Shoot.setTextFill(Color.WHITE);
        Label p1Switch = new Label("Switch Weapon: R/T");
        p1Switch.setTextFill(Color.WHITE);
        p1Controls.getChildren().addAll(p1Title, p1Move, p1Shoot, p1Switch);

        VBox p2Controls = new VBox(3);
        p2Controls.setAlignment(Pos.CENTER_LEFT);
        Label p2Title = new Label("Player 2:");
        p2Title.setTextFill(Color.LIGHTCORAL);
        p2Title.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        Label p2Move = new Label("Move: Arrow Keys");
        p2Move.setTextFill(Color.WHITE);
        Label p2Shoot = new Label("Shoot: L");
        p2Shoot.setTextFill(Color.WHITE);
        Label p2Switch = new Label("Switch Weapon: ;/'");
        p2Switch.setTextFill(Color.WHITE);
        p2Controls.getChildren().addAll(p2Title, p2Move, p2Shoot, p2Switch);

        controlsRow.getChildren().addAll(p1Controls, p2Controls);

        panel.getChildren().addAll(title, controlsRow);

        return panel;
    }

    // بتبدأ اللعبة بعد ما الاتنين يختاروا
    private void startGame() {
        String fighter1Type = player1Selection.getValue();
        String fighter2Type = player2Selection.getValue();

        if (fighter1Type == null || fighter2Type == null) {
            showAlert("Please select a fighter for both players!");
            return;
        }

        gameController = new GameController(primaryStage, selectionScene);
        Scene gameScene = gameController.createGameScene(fighter1Type, fighter2Type);

        primaryStage.setScene(gameScene);

        gameScene.getRoot().requestFocus();
    }

    // Alert بسيط لو في حاجة ناقصة
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}