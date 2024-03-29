package org.enki.geo;

import org.jetbrains.annotations.NotNull;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

import java.net.URI;
import java.util.Objects;

import static org.enki.core.Utilities.formatWithoutTrailingZeros;
import static tech.units.indriya.unit.Units.METRE;

/**
 * A location on Earth that includes an altitude above sea level.
 */
public class LatLongElevation extends LatLong {

    /**
     * The elevation above sea level.
     */
    public final Quantity<Length> elevation;

    /**
     * Construct a LatLongElevation using a supplied LatLong and an elevation.
     *
     * @param location  the 2D location
     * @param elevation elevation
     */
    public LatLongElevation(final @NotNull LatLong location, final @NotNull Quantity<Length> elevation) {
        super(location);
        this.elevation = elevation;
    }

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
    @Override
    public @NotNull Quantity<Length> distanceSquared(final @NotNull LatLong b) {
        if (!(b instanceof LatLongElevation)) {
            throw new IllegalArgumentException("cannot compute distance between 2D and 3D location");
        }

        final LatLongElevation other = (LatLongElevation) b;
        final Quantity<Length> elevationDelta = other.elevation.subtract(elevation);
        return Quantities
                .getQuantity(super.distanceSquared(b).getValue().doubleValue() +
                        Math.pow(elevationDelta.getValue().doubleValue(), 2), METRE);
    }

    /**
     * Compute distance in meters between this coordinate and another.
     *
     * @param b the other LatLongElevation coordinate
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

    @Override
    public @NotNull String toString() {
        return super.toString() + ", " + formatWithoutTrailingZeros(elevation.getValue().doubleValue()) + "m";
    }

    @Override
    public @NotNull URI toGeoURI() {
        return URI.create(super.toGeoURI() + "," + formatWithoutTrailingZeros(elevation.getValue().doubleValue()));
    }

}