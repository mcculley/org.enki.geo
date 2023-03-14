package org.enki.geo;

import org.jetbrains.annotations.NotNull;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

import java.util.Objects;

import static tech.units.indriya.unit.Units.METRE;

/**
 * A location on Earth that includes an altitude above sea level.
 */
public class LatLongElevation extends LatLong {

    public final Quantity<Length> elevation;

    /**
     * Construct a LatLongElevation using a supplied latitude and longitude in degrees and an elevation.
     *
     * @param latitude  latitude in degrees
     * @param longitude longitude in degrees
     * @param elevation elevation
     */
    public LatLongElevation(final double latitude, final double longitude, final @NotNull Quantity<Length> elevation) {
        super(latitude, longitude);
        this.elevation = elevation;
    }

    /**
     * Calculate distance squared between this coordinate and another.
     *
     * @param b the other LatLong coordinate
     * @return distance in meters
     */
    public @NotNull Quantity<Length> distanceSquared(final @NotNull LatLongElevation b) {
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
    public @NotNull Quantity<Length> distance(final @NotNull LatLongElevation b) {
        return Quantities.getQuantity(Math.sqrt(distanceSquared(b).getValue().doubleValue()), METRE);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        final LatLongElevation that = (LatLongElevation) o;
        return elevation.equals(that.elevation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), elevation);
    }

}