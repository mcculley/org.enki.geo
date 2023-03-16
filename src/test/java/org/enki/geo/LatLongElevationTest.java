package org.enki.geo;

import org.junit.jupiter.api.Test;
import tech.units.indriya.quantity.Quantities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tech.units.indriya.unit.Units.METRE;

public class LatLongElevationTest {

    @Test
    public void testToString() {
        final LatLongElevation l1 = new LatLongElevation(50, 20, Quantities.getQuantity(0, METRE));
        final LatLongElevation l2 = new LatLongElevation(new LatLong(50,20), Quantities.getQuantity(100, METRE));
        assertEquals(100, l1.distance(l2).getValue().doubleValue());
    }

}
