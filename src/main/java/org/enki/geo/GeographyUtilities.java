package org.enki.geo;

import javax.measure.Quantity;
import javax.measure.quantity.Angle;

import java.util.Objects;

import static systems.uom.common.USCustomary.DEGREE_ANGLE;

public class GeographyUtilities {

    private GeographyUtilities() {
        throw new AssertionError("static utility class is not intended to be instantiated");
    }

    /**
     * Describe a heading using a 16-wind compass rose.
     *
     * @param d the heading in degrees
     * @return a String describing the point of a 16-wind compass rose for the heading (e.g., "E", "WNW")
     */
    private static String directionName(final double d) {
        if (d < 0 || d >= 360) {
            throw new IllegalArgumentException();
        }

        final String[] names =
                {"N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "WNW"};
        final int numberHeadings = names.length;
        final double headingSize = 360.0 / numberHeadings;
        return names[((int) ((d + (headingSize / 2.0)) / headingSize)) % numberHeadings];
    }

    /**
     * Describe a heading using a 16-wind compass rose.
     *
     * @param d the heading
     * @return a String describing the point of a 16-wind compass rose for the heading (e.g., "E", "WNW")
     */
    public static String directionName(final Quantity<Angle> d) {
        return directionName(d.to(DEGREE_ANGLE).getValue().doubleValue());
    }

    static {
        assert Objects.equals(directionName(0), "N");
        assert Objects.equals(directionName(11), "N");
        assert Objects.equals(directionName(12), "NNE");
        assert Objects.equals(directionName(33), "NNE");
        assert Objects.equals(directionName(34), "NE");
        assert Objects.equals(directionName(56), "NE");
        assert Objects.equals(directionName(57), "ENE");
        assert Objects.equals(directionName(78), "ENE");
        assert Objects.equals(directionName(79), "E");
        assert Objects.equals(directionName(348), "WNW");
        assert Objects.equals(directionName(350), "N");
    }

}
