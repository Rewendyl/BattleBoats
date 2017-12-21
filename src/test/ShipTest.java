package test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sk.model.Ship;

/**
 * Created by Rewend on 21.12.2017.
 */
public class ShipTest {

    Ship sh;

    @Before
    public void initGameModel() {
        sh = new Ship(4, 0,3, true);
    }

    @Test
    public void testDead() {
        Ship ship = new Ship(3, 0,3, true);
        ship.hit();
        ship.hit();
        Assert.assertTrue(ship.hit());
    }

    @Test
    public void testIsThisShipHit() {
        Assert.assertTrue(sh.isThisShipHit(2,3));
    }
}
