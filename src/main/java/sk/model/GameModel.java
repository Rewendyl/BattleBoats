package sk.model;

import sk.Utils;

import java.awt.*;
import java.util.ArrayList;

//Model for tracking game state, contains both players' fields
public class GameModel {

    //Players' game fields
    private Utils.CellStates[][] p1State;
    private Utils.CellStates[][] p2State;

    //Player 1 ships
    private ArrayList<Ship> p1ShipList;

    //Player 2 ships
    private ArrayList<Ship> p2ShipList;

    //Tracks what player is playing currently: number 1 or 2
    private int playerNumberActive;

    public ArrayList<Ship> getP2ShipList() {
        return p2ShipList;
    }

    public ArrayList<Ship> getP1ShipList() {
        return p1ShipList;
    }

    public int getPlayerNumberActive() {
        return playerNumberActive;
    }

    public Utils.CellStates[][] getP1State() {
        return p1State;
    }

    public void setP1State(Utils.CellStates[][] p1State) {
        this.p1State = p1State;
    }

    public Utils.CellStates[][] getP2State() {
        return p2State;
    }

    public void setP2State(Utils.CellStates[][] p2State) {
        this.p2State = p2State;
    }

    public void setP1SingleState(int col, int row, Utils.CellStates state) {
        p1State[col][row] = state;
    }

    public void setP2SingleState(int col, int row, Utils.CellStates state) {
        p2State[col][row] = state;
    }

    public void addP1ShipList(int size, int col, int row, boolean isHorizontal) {
        p1ShipList.add(new Ship(size, col, row, isHorizontal));
    }

    public void addP2ShipList(int size, int col, int row, boolean isHorizontal) {
        p2ShipList.add(new Ship(size, col, row, isHorizontal));
    }

    public GameModel(Utils.CellStates[][] p1State, Utils.CellStates[][] p2State) {
        this.p1State = p1State;
        this.p2State = p2State;
        playerNumberActive = 1;
        p1ShipList = new ArrayList<>();
        p2ShipList = new ArrayList<>();
    }

    //Switches between active player
    public void changePlayer() {
        if (playerNumberActive == 1) {
            playerNumberActive = 2;
        } else {
            playerNumberActive = 1;
        }
    }

    //Registering player's move and changes his opponent's field. Returns type of game impact
    public Utils.MoveStates changeFieldState(int colIndex, int rowIndex, Utils.CellStates[][] pState, ArrayList<Ship> pShipList) {
        if (pState[colIndex][rowIndex].equals(Utils.CellStates.HIT) ||
                pState[colIndex][rowIndex].equals(Utils.CellStates.MISS)) return Utils.MoveStates.EXIST;
        else if (pState[colIndex][rowIndex].equals(Utils.CellStates.WATER)) {
            pState[colIndex][rowIndex] = Utils.CellStates.MISS;
            return Utils.MoveStates.MISS;
        } else {
            //Model changing if the player hit the ship, covering cells around hit target
            pState[colIndex][rowIndex] = Utils.CellStates.HIT;
            if (colIndex > 0 && rowIndex > 0) {
                pState[colIndex - 1][rowIndex - 1] = Utils.CellStates.MISS;
            }
            if (colIndex < 9 && rowIndex > 0) {
                pState[colIndex + 1][rowIndex - 1] = Utils.CellStates.MISS;
            }
            if (colIndex > 0 && rowIndex < 9) {
                pState[colIndex - 1][rowIndex + 1] = Utils.CellStates.MISS;
            }
            if (colIndex < 9 && rowIndex < 9) {
                pState[colIndex + 1][rowIndex + 1] = Utils.CellStates.MISS;
            }
            for (Ship ship : pShipList) {
                if (ship.isThisShipHit(colIndex, rowIndex)) {
                    if (ship.hit()) {
                        ArrayList<Point> misses = ship.getSurroundings();
                        for (Point miss : misses) {
                            pState[miss.x][miss.y] = Utils.CellStates.MISS;
                        }
                        pShipList.remove(ship);
                    }
                    break;
                }
            }


        }
        //Changing player's opponent field
        if (playerNumberActive == 1) {
            p2State = pState;
            p2ShipList = pShipList;
        } else {
            p1State = pState;
            p1ShipList = pShipList;
        }

        if (checkWin()) return Utils.MoveStates.WIN;
        else return Utils.MoveStates.HIT;
    }

    private boolean checkWin() {
        return p1ShipList.isEmpty() || p2ShipList.isEmpty();
    }
}
