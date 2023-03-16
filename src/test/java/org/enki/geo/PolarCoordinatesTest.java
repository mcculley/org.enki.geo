package org.enki.geo;

import org.junit.jupiter.api.Test;
import tech.units.indriya.quantity.Quantities;

import java.awt.geom.Point2D;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static systems.uom.common.USCustomary.DEGREE_ANGLE;

public class PolarCoordinatesTest {

    @Test
    public void testSimple() {
        final PolarCoordinates c = new PolarCoordinates(1.0, Quantities.getQuantity(0, DEGREE_ANGLE));
        final Point2D.Double p = c.toCartesian();
        assertEquals(1, p.x);
        assertEquals(0, p.y);
        assertEquals("(1, 0º)", c.toString());
    }

}
