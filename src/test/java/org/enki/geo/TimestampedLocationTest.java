package org.enki.geo;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TimestampedLocationTest {

    @Test
    public void testEquals() {
        final Instant now = Instant.now();
        final LatLong l1 = new LatLong(25, 80);
        final TimestampedLocation tl1 = new TimestampedLocation(l1, now);
        final TimestampedLocation tl2 = new TimestampedLocation(l1.latitude, l1.longitude, now);
        final TimestampedLocation tl3 = new TimestampedLocation(24, 80, now);
        final TimestampedLocation tl4 = new TimestampedLocation(25, 81, now);
        final TimestampedLocation tl5 = new TimestampedLocation(25, 80, now.minusSeconds(1));
        assertEquals(tl1, tl2);
        assertNotEquals(tl1, null);
        assertNotEquals(tl1, "not a location");
        assertEquals(tl1, tl1);
        assertNotEquals(tl1, tl3);
        assertNotEquals(tl1, tl4);
        assertNotEquals(tl1, tl5);
    }

    @Test
    public void testHashCode() {
        final Instant now = Instant.now();
        final LatLong l1 = new LatLong(25, 80);
        final TimestampedLocation tl1 = new TimestampedLocation(l1, now);
        assertEquals(8, Set.of(tl1, "2", "3", "4", "5", "6", "7", "8").size());
    }

}
