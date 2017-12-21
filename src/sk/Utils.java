package sk;

import javafx.scene.Node;

public class Utils {

    //States of the cell in the game field
    public enum CellStates {
        WATER, SHIP, HIT, MISS, EMPTY
    }

    //Shows what impact player caused with his move
    public enum MoveStates {
        HIT, MISS, EXIST, WIN
    }

    //Parses node cell index by its Name, e.g.: gameCell42 -> x = 4, y = 2
    public static int[] getNodeIndex(Node node) {
        int[] result = new int[2];
        String id = node.getId();
        result[1] = Character.getNumericValue(id.charAt(id.length() - 1));
        result[0] = Character.getNumericValue(id.charAt(id.length() - 2));
        return result;
    }
}
