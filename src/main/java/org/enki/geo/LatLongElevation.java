package org.enki.geo;

import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

import java.util.Objects;

import static tech.units.indriya.unit.Units.METRE;

public class LatLongElevation extends LatLong {

    public final Quantity<Length> elevation;

    /**
     * Construct a LatLongElevation using a supplied latitude and longitude in degrees and an elevation.
     *
     * @param latitude  latitude in degrees
     * @param longitude longitude in degrees
     * @param elevation elevation
     */
    public LatLongElevation(final double latitude, final double longitude, final Quantity<Length> elevation) {
        super(latitude, longitude);
        this.elevation = elevation;
    }

    /**
     * Calculate distance squared between this coordinate and another.
     *
     * @param b the other LatLong coordinate
     * @return distance in meters
     */
    public Quantity<Length> distanceSquared(final LatLongElevation b) {
        final Quantity<Length> elevationDelta = b.elevation.subtract(elevation);
        return Quantities
                .getQuantity(super.distanceSquared(b).getValue().doubleValue() +
                        Math.pow(elevationDelta.getValue().doubleValue(), 2), METRE);
    }

    /**
     * Compute distance in meters between this coordinate and another.
     *
     * @param b the other LatLong coordinate
     * @return the distance in meters
     */
    public Quantity<Length> distance(final LatLongElevation b) {
        return Quantities.getQuantity(Math.sqrt(distanceSquared(b).getValue().doubleValue()), METRE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        LatLongElevation that = (LatLongElevation) o;
        return elevation.equals(that.elevation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), elevation);
    }

}