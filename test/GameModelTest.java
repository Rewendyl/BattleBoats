package test;

import org.junit.*;
import sk.Utils;
import sk.model.GameModel;

public class GameModelTest {

    static GameModel gm;

    @Before
    public void initGameModel() {
        Utils.CellStates[][] p1State = new Utils.CellStates[10][10];
        Utils.CellStates[][] p2State = new Utils.CellStates[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                p1State[i][j] = Utils.CellStates.WATER;
                p2State[i][j] = Utils.CellStates.WATER;
            }
        }
        gm = new GameModel(p1State, p2State);
    }

    @Test
    public void testChangeToMiss() {
        Assert.assertEquals(Utils.MoveStates.MISS, gm.changeFieldState(0, 0, gm.getP1State(), gm.getP1ShipList()));
    }

    @Test
    public void testHittingAMiss() {
        gm.setP1SingleState(0, 0, Utils.CellStates.MISS);
        Assert.assertEquals(Utils.MoveStates.EXIST, gm.changeFieldState(0, 0, gm.getP1State(), gm.getP1ShipList()));
    }

    @Test
    public void testWinningEmptyGame() {
        gm.setP1SingleState(0, 0, Utils.CellStates.SHIP);
        Assert.assertEquals(Utils.MoveStates.WIN, gm.changeFieldState(0, 0, gm.getP1State(), gm.getP1ShipList()));
    }

    @Test
    public void testHittingShip() {
        gm.setP1SingleState(0, 0, Utils.CellStates.SHIP);
        gm.addP1ShipList(3, 2, 2, true);
        gm.addP1ShipList(3, 5, 5, false);
        Assert.assertEquals(Utils.MoveStates.HIT, gm.changeFieldState(0, 0, gm.getP1State(), gm.getP1ShipList()));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testWrongFieldSize() {
        gm.changeFieldState(10, 10, gm.getP1State(), gm.getP1ShipList());
    }

}
