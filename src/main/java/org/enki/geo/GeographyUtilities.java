package org.enki.geo;

import org.enki.core.ExcludeFromJacocoGeneratedReport;
import org.jetbrains.annotations.NotNull;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Angle;

import java.net.URI;

import static java.lang.Math.abs;
import static systems.uom.common.USCustomary.DEGREE_ANGLE;
import static tech.units.indriya.unit.Units.METRE;

/**
 * Miscellaneous utilities for dealing with geography.
 */
public class GeographyUtilities {

    @ExcludeFromJacocoGeneratedReport
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
     * Construct a LatLong using a supplied geo URI (https://datatracker.ietf.org/doc/html/rfc5870).
     *
     * @param geoURI the geo URI
     * @return a <code>LatLong</code> or <code>LatLongElevation</code>, depending on the kind of geo URI
     */
    public static @NotNull LatLong parseGeoURI(final @NotNull URI geoURI) {
        if (!geoURI.getScheme().equals("geo"))
            throw new IllegalArgumentException(
                    String.format("unexpected scheme '%s' in '%s'", geoURI.getScheme(), geoURI));

        final String location = geoURI.getSchemeSpecificPart().split(";")[0];
        final String[] coordinates = location.split(",");
        final double latitude = Double.parseDouble(coordinates[0]);
        final double longitude = Double.parseDouble(coordinates[1]);

        if (abs(longitude) > 180)
            throw new IllegalArgumentException("invalid longitude " + longitude);

        if (abs(latitude) > 90)
            throw new IllegalArgumentException("invalid latitude " + latitude);

        if (coordinates.length == 2)
            return new LatLong(latitude, longitude);
        else if (coordinates.length == 3) {
            final double elevationInMeters = Double.parseDouble(coordinates[2]);
            return new LatLongElevation(latitude, longitude, Quantities.getQuantity(elevationInMeters, METRE));
        } else
            throw new IllegalArgumentException(String.format("unexpected number of coordinate parts in '%s'", geoURI));
    }

}
