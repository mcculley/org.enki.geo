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

    /**
     * Given a floating point number, render it into a String with no trailing zeros. (e.g. 25.0 renders as "25" and
     * 25.050 renders as "25.05").
     *
     * @param x the number to render
     * @return a String with no trailing zeros or decimal point if there are no fractional digits
     */
    public static String formatWithoutTrailingZeros(final double x) {
        return Double.toString(x).replaceAll("\\.?0*$", "");
    }

}
