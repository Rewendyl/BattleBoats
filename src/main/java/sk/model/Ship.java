package sk.model;

import java.awt.*;
import java.util.ArrayList;

public class Ship {
    private int size;
    private int hp;
    private int[][] coords;
    private boolean isHorizontal;

    public Ship(int size, int col, int row, boolean isHorizontal) {
        this.size = size;
        this.hp = size;
        this.coords = new int[2][size];
        this.isHorizontal = isHorizontal;
        for (int i = 0; i < size; i++) {
            if (isHorizontal) {
                this.coords[0][i] = col + i;
                this.coords[1][i] = row;
            } else {
                this.coords[0][i] = col;
                this.coords[1][i] = row + i;
            }
        }
    }

    //Ship getting damaged. Returns true if ship is dead
    public boolean hit() {
        hp--;
        return hp == 0;
    }

    public boolean isThisShipHit(int col, int row) {
        for (int i = 0; i < size; i++) {
            if (coords[0][i] == col && coords[1][i] == row) return true;
        }
        return false;
    }

    //Returns surrounding cells of the ship
    public ArrayList<Point> getSurroundings() {
        ArrayList<Point> result = new ArrayList<>();
        int width;
        int length;
        if (isHorizontal) {
            width = size;
            length = 1;
        } else {
            width = 1;
            length = size;
        }
        for (int i = coords[0][0] - 1; i < coords[0][0] + width + 1; i++) {
            for (int j = coords[1][0] - 1; j < coords[1][0] + length + 1; j++) {
                if (!isThisShipHit(i, j)) {
                    if (i >= 0 && i < 10 && j >= 0 && j < 10) {
                        result.add(new Point(i, j));
                    }
                }
            }
        }
        return result;
    }
}
