package org.enki.geo;

import org.junit.jupiter.api.Test;
import tech.units.indriya.quantity.Quantities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static systems.uom.common.USCustomary.DEGREE_ANGLE;

public class GeographyUtilitiesTest {

    @Test
    public void testDirectionName() {
        assertThrows(IllegalArgumentException.class, () -> GeographyUtilities.directionName(-1));
        assertThrows(IllegalArgumentException.class, () -> GeographyUtilities.directionName(361));
        assertEquals("N", GeographyUtilities.directionName(0));
        assertEquals("N", GeographyUtilities.directionName(11));
        assertEquals("NNE", GeographyUtilities.directionName(12));
        assertEquals("NNE", GeographyUtilities.directionName(33));
        assertEquals("NE", GeographyUtilities.directionName(34));
        assertEquals("NE", GeographyUtilities.directionName(56));
        assertEquals("ENE", GeographyUtilities.directionName(57));
        assertEquals("ENE", GeographyUtilities.directionName(Quantities.getQuantity(57, DEGREE_ANGLE)));
        assertEquals("ENE", GeographyUtilities.directionName(78));
        assertEquals("E", GeographyUtilities.directionName(79));
        assertEquals("WNW", GeographyUtilities.directionName(348));
        assertEquals("N", GeographyUtilities.directionName(350));
    }

}
