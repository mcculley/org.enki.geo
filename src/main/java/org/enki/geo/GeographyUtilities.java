package org.enki.geo;

import org.jetbrains.annotations.NotNull;

import javax.measure.Quantity;
import javax.measure.quantity.Angle;

import static systems.uom.common.USCustomary.DEGREE_ANGLE;

/**
 * Miscellaneous utilities for dealing with geography.
 */
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
    public static @NotNull String directionName(final double d) {
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
    public static @NotNull String directionName(final @NotNull Quantity<Angle> d) {
        return directionName(d.to(DEGREE_ANGLE).getValue().doubleValue());
    }

}
