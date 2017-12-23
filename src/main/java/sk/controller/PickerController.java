package sk.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import sk.Utils;
import sk.model.GameModel;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Stack;

public class PickerController {

    @FXML
    private Label playerSwitchLabel;
    @FXML
    private Label lmb;
    @FXML
    private Label rmb;
    @FXML
    private GridPane pickerClick;

    private GameModel gameModel;
    private boolean isShipHorizontal;

    private FieldController fieldController;
    private Stage mainStage;
    private Scene fieldScene;
    private Stack<Integer> shipSize;
    private int activePlayerNumber;

    private final Properties prop = new Properties();

    public PickerController() {
        try {
            InputStream in = getClass().getResourceAsStream("/values.properties");
            prop.load(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setMainStage(Stage mainStage, Scene fieldScene) {
        this.mainStage = mainStage;
        this.fieldScene = fieldScene;

        lmb.setText(prop.getProperty("instructionLMB"));
        rmb.setText(prop.getProperty("instructionRMB"));
    }

    public void setFieldController(FieldController fieldController) {
        this.fieldController = fieldController;
    }

    public void init() {
        playerSwitchLabel.setText(prop.getProperty("p2TurnToPlace"));
        isShipHorizontal = true;
        setShipSize();
        activePlayerNumber = 2;
        gameModel = new GameModel(new Utils.CellStates[10][10], new Utils.CellStates[10][10]);
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                addPickerPane(i, j);
            }
        }
        //Fills remaining cells with water
        Utils.CellStates[][] p1State = new Utils.CellStates[10][10];
        Utils.CellStates[][] p2State = new Utils.CellStates[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                p1State[i][j] = Utils.CellStates.EMPTY;
                p2State[i][j] = Utils.CellStates.EMPTY;
            }
        }
        gameModel.setP1State(p1State);
        gameModel.setP2State(p2State);
    }

    //Stack of ship sizes for placement
    private void setShipSize() {
        shipSize = new Stack<>();
        shipSize.push(1);
        shipSize.push(1);
        shipSize.push(1);
        shipSize.push(1);
        shipSize.push(2);
        shipSize.push(2);
        shipSize.push(2);
        shipSize.push(3);
        shipSize.push(3);
        shipSize.push(4);
    }

    private void addPickerPane(int colIndex, int rowIndex) {
        Pane pane = new Pane();
        pane.setId("pickerCell" + colIndex + "" + rowIndex);
        pane.setOnMouseEntered(event -> paneEntered(colIndex, rowIndex));
        pane.setOnMouseClicked(event -> paneClicked(colIndex, rowIndex, event.getButton()));
        pickerClick.add(pane, colIndex, rowIndex);
    }

    private void paneEntered(int colIndex, int rowIndex) {
        if (checkForNoCollision(colIndex, rowIndex)) {
            reDrawPicker(colIndex, rowIndex);
        } else {
            reDrawPicker(-1, -1);
        }
    }

    //Checking if the future ship position interferes with other objects or edge
    private boolean checkForNoCollision(int colIndex, int rowIndex) {
        int shipCount = shipSize.peek();
        Utils.CellStates[][] pState;
        if (activePlayerNumber == 1) {
            pState = gameModel.getP1State();
        } else {
            pState = gameModel.getP2State();
        }
        if (isShipHorizontal) {
            if (colIndex > 10 - shipCount) return false;
            for (int i = 0; i < shipCount; i++) {
                if (!pState[(colIndex + i)][rowIndex].equals(Utils.CellStates.EMPTY)) return false;
            }
        } else {
            if (rowIndex > 10 - shipSize.peek()) return false;
            for (int i = 0; i < shipCount; i++) {
                if (!pState[colIndex][rowIndex + i].equals(Utils.CellStates.EMPTY)) return false;
            }
        }

        return true;
    }

    private void paneClicked(int colIndex, int rowIndex, MouseButton mb) {
        if (mb.equals(MouseButton.SECONDARY)) {
            isShipHorizontal = !isShipHorizontal;
            if (checkForNoCollision(colIndex, rowIndex)) reDrawPicker(colIndex, rowIndex);
            else reDrawPicker(-1, -1);
        } else {
            //Putting new ship into GameModel
            if (checkForNoCollision(colIndex, rowIndex)) {
                int shipCount = shipSize.peek();
                if (isShipHorizontal) {
                    for (int i = 0; i < shipCount; i++) {
                        if (activePlayerNumber == 1)
                            gameModel.setP1SingleState(colIndex + i, rowIndex, Utils.CellStates.SHIP);
                        else
                            gameModel.setP2SingleState(colIndex + i, rowIndex, Utils.CellStates.SHIP);
                    }
                    putWaterAround(colIndex, rowIndex, colIndex + shipCount - 1, rowIndex);
                } else {
                    for (int i = 0; i < shipCount; i++) {
                        if (activePlayerNumber == 1)
                            gameModel.setP1SingleState(colIndex, rowIndex + i, Utils.CellStates.SHIP);
                        else
                            gameModel.setP2SingleState(colIndex, rowIndex + i, Utils.CellStates.SHIP);
                    }
                    putWaterAround(colIndex, rowIndex, colIndex, rowIndex + shipCount - 1);
                }
                if (activePlayerNumber == 1)
                    gameModel.addP1ShipList(shipCount, colIndex, rowIndex, isShipHorizontal);
                else
                    gameModel.addP2ShipList(shipCount, colIndex, rowIndex, isShipHorizontal);
                shipSize.pop();
                if (shipSize.empty()) changePickerState();
                reDrawPicker(-1, -1);
            }
        }
    }

    //Changes Player 2 pick to Player 1, then launches the game
    private void changePickerState() {
        if (activePlayerNumber == 2) {
            setShipSize();
            activePlayerNumber = 1;
            playerSwitchLabel.setText(prop.getProperty("p1TurnToPlace"));
            isShipHorizontal = true;
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    if (gameModel.getP2State()[i][j].equals(Utils.CellStates.EMPTY)) {
                        gameModel.setP2SingleState(i, j, Utils.CellStates.WATER);
                    }
                }
            }
            reDrawPicker(-1, -1);
        } else {
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    if (gameModel.getP1State()[i][j].equals(Utils.CellStates.EMPTY)) {
                        gameModel.setP1SingleState(i, j, Utils.CellStates.WATER);
                    }
                }
            }
            fieldController.setGameModel(gameModel);
            mainStage.setScene(fieldScene);
        }
    }

    //Fills GameModel field with water around the ship
    private void putWaterAround(int colIndex, int rowIndex, int colIndexEnd, int rowIndexEnd) {
        if (activePlayerNumber == 1) {
            for (int i = colIndex - 1; i <= colIndexEnd + 1; i++) {
                for (int j = rowIndex - 1; j <= rowIndexEnd + 1; j++) {
                    if (i >= 0 && i <= 9 && j >= 0 && j <= 9 && gameModel.getP1State()[i][j].equals(Utils.CellStates.EMPTY)) {
                        gameModel.setP1SingleState(i, j, Utils.CellStates.WATER);
                    }
                }
            }
        } else {
            for (int i = colIndex - 1; i <= colIndexEnd + 1; i++) {
                for (int j = rowIndex - 1; j <= rowIndexEnd + 1; j++) {
                    if (i >= 0 && i <= 9 && j >= 0 && j <= 9 && gameModel.getP2State()[i][j].equals(Utils.CellStates.EMPTY)) {
                        gameModel.setP2SingleState(i, j, Utils.CellStates.WATER);
                    }
                }
            }
        }
    }

    //Redraws field with new added ship
    private void reDrawPicker(int col, int row) {
        Utils.CellStates[][] panel;
        if (activePlayerNumber == 1) {
            panel = gameModel.getP1State();
        } else {
            panel = gameModel.getP2State();
        }
        for (Node n : pickerClick.getChildren()) {
            if (n.getId() != null && n.getId().startsWith("pickerCell")) {
                int[] index = Utils.getNodeIndex(n);
                switch (panel[index[0]][index[1]]) {
                    case SHIP:
                        setNodeImage(n, prop.getProperty("shipPic"));
                        break;
                    case WATER:
                        setNodeImage(n, prop.getProperty("waterPic"));
                        break;
                    default:
                        n.setStyle("");
                        break;
                }
            }

        }
        if (col >= 0 && col <= 9 && row >= 0 && row <= 9) {
            reDrawTemporal(col, row);
        }
    }

    //Redraws ship ghost while mouse moving
    private void reDrawTemporal(int col, int row) {
        int shipCount = shipSize.peek();
        if (isShipHorizontal) {
            for (int i = 0; i < shipCount; i++) {
                setNodeImage(mainStage.getScene().lookup("#pickerCell" + (col + i) + "" + row), prop.getProperty("shipPic"));
            }
        } else {
            for (int i = 0; i < shipCount; i++) {
                setNodeImage(mainStage.getScene().lookup("#pickerCell" + col + "" + (row + i)), prop.getProperty("shipPic"));
            }
        }
    }

    private void setNodeImage(Node n, String name) {
        n.setStyle("-fx-background-image: url('" + name + "');" +
                "-fx-background-size: cover;" +
                "-fx-background-repeat: stretch");
    }
}
