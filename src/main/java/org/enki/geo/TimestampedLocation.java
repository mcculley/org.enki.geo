package org.enki.geo;

import java.time.Instant;
import java.util.Objects;

public class TimestampedLocation extends LatLong {

    public final Instant timestamp;

    public TimestampedLocation(final LatLong location, final Instant timestamp) {
        super(location);
        this.timestamp = timestamp;
    }

    public TimestampedLocation(final double latitude, final double longitude, final Instant timestamp) {
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
