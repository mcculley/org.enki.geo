package org.enki.geo;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Objects;

/**
 * A location on Earth associated with a timestamp.
 */
public class TimestampedLocation extends LatLong {

    public final Instant timestamp;

    /**
     * Create a new TimestampedLocation.
     *
     * @param location  the Location
     * @param timestamp the timestamp, as an <code>Instant</code>
     */
    public TimestampedLocation(final @NotNull LatLong location, final @NotNull Instant timestamp) {
        super(location);
        this.timestamp = timestamp;
    }

    /**
     * Create a new TimestampedLocation given a latitude, longitude, and timestamp.
     *
     * @param latitude  the latitude in degrees
     * @param longitude the longitude in degrees
     * @param timestamp the timestamp, as an <code>Instant</code>
     */
    public TimestampedLocation(final double latitude, final double longitude, final @NotNull Instant timestamp) {
        super(latitude, longitude);
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        final TimestampedLocation that = (TimestampedLocation) o;
        return Double.compare(that.latitude, latitude) == 0 &&
                Double.compare(that.longitude, longitude) == 0 && timestamp.equals(that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), timestamp);
    }

}
