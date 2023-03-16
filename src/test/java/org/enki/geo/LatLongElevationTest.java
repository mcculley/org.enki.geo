package org.enki.geo;

import org.junit.jupiter.api.Test;
import tech.units.indriya.quantity.Quantities;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static tech.units.indriya.unit.Units.METRE;

public class LatLongElevationTest {

    @Test
    public void testDistance() {
        final LatLongElevation l1 = new LatLongElevation(50, 20, Quantities.getQuantity(0, METRE));
        final LatLongElevation l2 = new LatLongElevation(new LatLong(50, 20), Quantities.getQuantity(100, METRE));
        assertEquals(100, l1.distance(l2).getValue().doubleValue());
    }

    @Test
    public void testEquality() {
        final LatLongElevation l1 = new LatLongElevation(50, 20, Quantities.getQuantity(0, METRE));
        final LatLongElevation l2 = new LatLongElevation(new LatLong(50, 20), Quantities.getQuantity(0, METRE));
        final LatLongElevation l3 = new LatLongElevation(new LatLong(50, 30), Quantities.getQuantity(0, METRE));
        assertEquals(l1, l2);
        assertNotEquals(l1, null);
        assertNotEquals(l1, "not a location");
        assertEquals(l1, l1);
        assertNotEquals(l1,l3);
    }

    @Test
    public void testHashCode() {
        final LatLongElevation l1 = new LatLongElevation(50, 20, Quantities.getQuantity(0, METRE));
        assertEquals(9, Set.of(l1, "2", "3", "4", "5", "6", "7", "8", "9").size());
    }

}
