package sk.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import sk.Utils;
import sk.model.GameModel;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class FieldController {

    @FXML
    private GridPane battleView;
    @FXML
    private GridPane battleClick;
    @FXML
    private Pane infoPane;
    @FXML
    private Pane infoImage;
    @FXML
    private Label clickLabel;
    @FXML
    private Label viewLabel;

    private GameModel gameModel;
    //Flag to lock user inputs
    private boolean isInterfaceActive;

    private final Properties prop = new Properties();

    private PickerController pickerController;
    private Stage mainStage;
    private Scene pickerScene;

    public void setMainStage(Stage mainStage, Scene pickerScene) {
        this.mainStage = mainStage;
        this.pickerScene = pickerScene;
    }

    public void setPickerController(PickerController pickerController) {
        this.pickerController = pickerController;
    }

    public void setGameModel(GameModel gameModel) {
        this.gameModel = gameModel;
        isInterfaceActive = true;
        initiateNewGame();
        infoPane.setOnMouseClicked(event -> infoPaneClicked());
        infoImage.setOnMouseClicked(event -> infoPaneClicked());
        infoImage.prefHeightProperty().bind(infoPane.heightProperty());
        infoImage.prefWidthProperty().bind(infoPane.widthProperty());
        viewLabel.setText(prop.getProperty("yourFieldPlayer1"));
        clickLabel.setText(prop.getProperty("opponentP2Field"));

    }

    private void initiateNewGame() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                addViewPane(i, j);
                addClickPane(i, j);
            }
        }
        reDrawGame();
    }

    public FieldController() {
        try {
            InputStream in = getClass().getResourceAsStream("/values.properties");
            prop.load(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Filling player's grid with cells
    private void addViewPane(int colIndex, int rowIndex) {
        Pane pane = new Pane();
        pane.setId("gameViewCell" + colIndex + "" + rowIndex);
        battleView.add(pane, colIndex, rowIndex);
    }

    //Filling opponent's grid with cells
    private void addClickPane(int colIndex, int rowIndex) {
        Pane pane = new Pane();
        pane.setId("gameCell" + colIndex + "" + rowIndex);
        pane.setOnMouseClicked(event -> paneClicked(colIndex, rowIndex));
        battleClick.add(pane, colIndex, rowIndex);
    }

    //Parsing clicks on opponent's field
    private void paneClicked(int colIndex, int rowIndex) {
        if (isInterfaceActive) {
            Utils.MoveStates result;
            if (gameModel.getPlayerNumberActive() == 1) {
                result = gameModel.changeFieldState(colIndex, rowIndex, gameModel.getP2State(), gameModel.getP2ShipList());
            } else {
                result = gameModel.changeFieldState(colIndex, rowIndex, gameModel.getP1State(), gameModel.getP1ShipList());
            }
            if (!result.equals(Utils.MoveStates.EXIST)) {
                reDrawGame();
                if (result.equals(Utils.MoveStates.MISS)) {
                    isInterfaceActive = false;
                    Timeline timeline = new Timeline(new KeyFrame(
                            Duration.millis(Integer.parseInt(prop.getProperty("turnSwitchDelayMS"))),
                            ae -> delayedPlayerChange()));
                    timeline.play();
                } else if (result.equals(Utils.MoveStates.WIN)) {
                    playerWon();
                }
            }
        }
    }

    private void playerWon() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(prop.getProperty("winTitle"));
        alert.setHeaderText(null);
        if (gameModel.getPlayerNumberActive() == 1) {
            alert.setContentText(prop.getProperty("p1Won"));
        } else {
            alert.setContentText(prop.getProperty("p2Won"));
        }
        alert.showAndWait();
        pickerController.init();
        mainStage.setScene(pickerScene);
    }

    private void infoPaneClicked() {
        infoPane.setDisable(true);
        infoPane.setVisible(false);
        infoImage.setDisable(true);
        infoImage.setVisible(false);
        isInterfaceActive = true;
    }

    //Pauses the game to allow players to switch without cheating
    private void delayedPlayerChange() {
        gameModel.changePlayer();
        if (gameModel.getPlayerNumberActive() == 1) {
            viewLabel.setText(prop.getProperty("yourFieldPlayer1"));
            clickLabel.setText(prop.getProperty("opponentP2Field"));
        } else {
            viewLabel.setText(prop.getProperty("yourFieldPlayer2"));
            clickLabel.setText(prop.getProperty("opponentP1Field"));
        }
        reDrawGame();
        infoPane.setDisable(false);
        infoPane.setVisible(true);
        infoImage.setDisable(false);
        infoImage.setVisible(true);
        infoPane.setStyle("-fx-background-color: #cccccc;" +
                "-fx-background-repeat: stretch");
        infoImage.setStyle("-fx-background-image: url('" + prop.getProperty("swapPlayerPic") + "');" +
                "-fx-background-position: center;" +
                "-fx-background-repeat: no-repeat;" +
                "-fx-background-size: contain;" +
                "-fx-padding: 10;");
        infoImage.toFront();
    }

    //Redraws the game fields
    private void reDrawGame() {
        Utils.CellStates[][] leftPanel;
        Utils.CellStates[][] rightPanel;
        if (gameModel.getPlayerNumberActive() == 1) {
            leftPanel = gameModel.getP1State();
            rightPanel = gameModel.getP2State();
        } else {
            leftPanel = gameModel.getP2State();
            rightPanel = gameModel.getP1State();
        }
        for (Node n : battleView.getChildren()) {
            if (n.getId() != null && n.getId().startsWith("gameViewCell")) {
                int[] index = Utils.getNodeIndex(n);
                switch (leftPanel[index[0]][index[1]]) {
                    case HIT:
                        setNodeImage(n, prop.getProperty("hitPic"));
                        break;
                    case MISS:
                        setNodeImage(n, prop.getProperty("missPic"));
                        break;
                    case SHIP:
                        setNodeImage(n, prop.getProperty("shipPic"));
                        break;
                    case WATER:
                        setNodeImage(n, prop.getProperty("waterPic"));
                        break;
                }
            }
        }
        for (Node n : battleClick.getChildren()) {
            if (n.getId() != null && n.getId().startsWith("gameCell")) {
                int[] index = Utils.getNodeIndex(n);
                switch (rightPanel[index[0]][index[1]]) {
                    case HIT:
                        setNodeImage(n, prop.getProperty("hitPic"));
                        break;
                    case MISS:
                        setNodeImage(n, prop.getProperty("missPic"));
                        break;
                    default:
                        n.setStyle("");
                        break;
                }
            }
        }
    }

    private void setNodeImage(Node n, String name) {
        n.setStyle("-fx-background-image: url('" + name + "');" +
                "-fx-background-size: cover;" +
                "-fx-background-repeat: stretch");
    }
}
