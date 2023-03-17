package org.enki.geo;

import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static systems.uom.common.USCustomary.DEGREE_ANGLE;
import static systems.uom.common.USCustomary.NAUTICAL_MILE;
import static org.enki.core.Collections.concat;

class LatLongTest {

    @Test
    void distanceAlongEquator() {
        // One minute of latitude at the equator is one nautical mile.
        final LatLong equator1 = new LatLong(0, 0);
        final LatLong equator2 = new LatLong(1.0 / 60.0, 0);
        final Quantity<Length> distanceMetersEquator = equator1.distance(equator2);
        assertEquals(1.00, distanceMetersEquator.to(NAUTICAL_MILE).getValue().doubleValue(), 0.005);
    }

    @Test
    void heading() {
        final LatLong equator1 = new LatLong(0, 0);
        final LatLong equator3 =
                equator1.plus(Quantities.getQuantity(90, DEGREE_ANGLE), Quantities.getQuantity(1, NAUTICAL_MILE));
        assertEquals(0, equator3.latitude, 0.001);
        assertEquals(Quantities.getQuantity(1, NAUTICAL_MILE).getValue().doubleValue(),
                equator1.distance(equator3).to(NAUTICAL_MILE).getValue().doubleValue());
    }

    @Test
    void simpleHeading() {
        // Why does this not work for latitudes outside of the equator?
        assertEquals(180, new LatLong(1, -80).heading(new LatLong(0, -80)).getValue().doubleValue());
        assertEquals(0, new LatLong(0, -80).heading(new LatLong(1, -80)).getValue().doubleValue());
        assertEquals(270, new LatLong(0, -80).heading(new LatLong(0, -81)).getValue().doubleValue());
        assertEquals(90, new LatLong(0, -81).heading(new LatLong(0, -80)).getValue().doubleValue());
    }

    @Test
    void conversion0() {
        final LatLong equator1 = new LatLong(0, 0);
        assertEquals("0º 0', 0º 0'", new LatLong.DegreesDecimalMinutes(equator1).toString());
    }

    @Test
    void conversion1() {
        final LatLong l1 = new LatLong(27.50, -82.75);
        final LatLong.DegreesDecimalMinutes lddm1 = new LatLong.DegreesDecimalMinutes(l1);
        assertNotEquals(lddm1, null);
        assertNotEquals(lddm1, "not a location");
        assertEquals(lddm1, lddm1);

        // test hashCode()
        assertEquals(8, Set.of(lddm1, "2", "3", "4", "5", "6", "7", "8").size());

        assertEquals(27, lddm1.latitudeDegrees);
        assertEquals(-82, lddm1.longitudeDegrees);
        assertEquals(30, lddm1.latitudeDecimalMinutes);
        assertEquals(45, lddm1.longitudeDecimalMinutes);
        assertEquals("27º 30', -82º 45'", lddm1.toString());
        assertEquals("27º 30' N, 82º 45' W", lddm1.toStringCardinal());
        assertEquals(l1, lddm1.getLatLong());
        final LatLong.DegreesDecimalMinutes lddm2 = new LatLong.DegreesDecimalMinutes(-27, 30, -82, 45);
        assertNotEquals(lddm1, lddm2);
        final LatLong.DegreesDecimalMinutes lddm3 = new LatLong.DegreesDecimalMinutes(27, 30, 82, 45);
        assertNotEquals(lddm1, lddm3);
        final LatLong.DegreesDecimalMinutes lddm4 = new LatLong.DegreesDecimalMinutes(27, 12, -82, 45);
        assertNotEquals(lddm1, lddm4);
        final LatLong.DegreesDecimalMinutes lddm5 = new LatLong.DegreesDecimalMinutes(27, 30, -82, 12);
        assertNotEquals(lddm1, lddm5);
        final LatLong.DegreesDecimalMinutes lddm6 = new LatLong.DegreesDecimalMinutes(27, 30, -82, 13);
        assertNotEquals(lddm5, lddm6);
        final LatLong.DegreesMinutesSeconds ldms1 = new LatLong.DegreesMinutesSeconds(l1);
        assertEquals(27, ldms1.latitudeDegrees);
        assertEquals(-82, ldms1.longitudeDegrees);
        assertEquals(30, ldms1.latitudeMinutes);
        assertEquals(45, ldms1.longitudeMinutes);
        assertEquals(0, ldms1.latitudeSeconds);
        assertEquals(0, ldms1.longitudeSeconds);
        assertEquals("27º 30' 0\" N, 82º 45' 0\" W", ldms1.toString());
        assertEquals(l1, ldms1.getLatLong());
        final LatLong.DegreesMinutesSeconds ldms2 = new LatLong.DegreesMinutesSeconds(27, 30, 0, -82, 45, 0);
        assertEquals(ldms1, ldms2);
        assertEquals(ldms1, ldms1);
        assertNotEquals(ldms1, null);
        assertNotEquals(ldms1, "not a location");
        final LatLong.DegreesMinutesSeconds ldms3 = new LatLong.DegreesMinutesSeconds(27, 30, 30, -82, 45, 0);
        assertNotEquals(ldms2, ldms3);
        final LatLong.DegreesMinutesSeconds ldms4 = new LatLong.DegreesMinutesSeconds(27, 30, 0, -82, 45, 30);
        assertNotEquals(ldms2, ldms4);
        final LatLong.DegreesMinutesSeconds ldms5 = new LatLong.DegreesMinutesSeconds(27, 0, 30, -82, 45, 0);
        assertNotEquals(ldms2, ldms5);
        final LatLong.DegreesMinutesSeconds ldms6 = new LatLong.DegreesMinutesSeconds(27, 2, 30, -82, 45, 0);
        assertNotEquals(ldms2, ldms6);
        final LatLong.DegreesMinutesSeconds ldms7 = new LatLong.DegreesMinutesSeconds(27, 30, 30, -82, 15, 0);
        assertNotEquals(ldms2, ldms7);
        final LatLong.DegreesMinutesSeconds ldms8 = new LatLong.DegreesMinutesSeconds(15, 30, 0, -82, 45, 0);
        assertNotEquals(ldms2, ldms8);
        final LatLong.DegreesMinutesSeconds ldms9 = new LatLong.DegreesMinutesSeconds(27, 30, 30, -81, 45, 0);
        assertNotEquals(ldms2, ldms9);

        // test hashCode()
        assertEquals(8, Set.of(ldms1, ldms3, ldms4, ldms5, ldms6, ldms7, ldms8, ldms9).size());
    }

    @Test
    void conversion2() {
        final LatLong l2 = new LatLong(27.625, -82.875);
        final LatLong.DegreesDecimalMinutes lddm2 = new LatLong.DegreesDecimalMinutes(l2);
        assertEquals(27, lddm2.latitudeDegrees);
        assertEquals(-82, lddm2.longitudeDegrees);
        assertEquals(37.5, lddm2.latitudeDecimalMinutes);
        assertEquals(52.5, lddm2.longitudeDecimalMinutes);
        assertEquals("27º 37.5', -82º 52.5'", lddm2.toString());
        assertEquals(l2, lddm2.getLatLong());
        final LatLong.DegreesMinutesSeconds ldms2 = new LatLong.DegreesMinutesSeconds(l2);
        assertEquals(27, ldms2.latitudeDegrees);
        assertEquals(-82, ldms2.longitudeDegrees);
        assertEquals(37, ldms2.latitudeMinutes);
        assertEquals(52, ldms2.longitudeMinutes);
        assertEquals(30, ldms2.latitudeSeconds);
        assertEquals(30, ldms2.longitudeSeconds);
        assertEquals("27º 37' 30\" N, 82º 52' 30\" W", ldms2.toString());
        assertEquals(l2, ldms2.getLatLong());
    }

    @Test
    void conversion3() {
        final LatLong l1 = new LatLong(-27.50, 82.75);
        final LatLong.DegreesDecimalMinutes lddm1 = new LatLong.DegreesDecimalMinutes(l1);
        assertEquals(-27, lddm1.latitudeDegrees);
        assertEquals(82, lddm1.longitudeDegrees);
        assertEquals(30, lddm1.latitudeDecimalMinutes);
        assertEquals(45, lddm1.longitudeDecimalMinutes);
        assertEquals("-27º 30', 82º 45'", lddm1.toString());
        assertEquals("27º 30' S, 82º 45' E", lddm1.toStringCardinal());
        assertEquals(l1, lddm1.getLatLong());
        final LatLong.DegreesMinutesSeconds ldms1 = new LatLong.DegreesMinutesSeconds(l1);
        assertEquals(-27, ldms1.latitudeDegrees);
        assertEquals(82, ldms1.longitudeDegrees);
        assertEquals(30, ldms1.latitudeMinutes);
        assertEquals(45, ldms1.longitudeMinutes);
        assertEquals(0, ldms1.latitudeSeconds);
        assertEquals(0, ldms1.longitudeSeconds);
        assertEquals("27º 30' 0\" S, 82º 45' 0\" E", ldms1.toString());
        assertEquals(l1, ldms1.getLatLong());
    }

    @Test
    public void testBeforeRoute() {
        final LatLong location = new LatLong(5, -80);
        final List<LatLong> route = List.of(
                new LatLong(5, -81),
                new LatLong(5, -82),
                new LatLong(5, -83)
        );
        final List<LatLong> remainingRoute = location.remainingRoute(route);
        assertEquals(concat(location, route), remainingRoute);
    }

    @Test
    public void testInsideRoute() {
        final LatLong location = new LatLong(5, -81);
        final List<LatLong> route = List.of(
                new LatLong(5, -80),
                new LatLong(5, -82),
                new LatLong(5, -83)
        );
        final List<LatLong> remainingRoute = location.remainingRoute(route);
        assertEquals(List.of(
                new LatLong(5, -81),
                new LatLong(5, -82),
                new LatLong(5, -83)
        ), remainingRoute);
    }

    @Test
    public void testInsideAboveRoute() {
        final LatLong location = new LatLong(6, -81);
        final List<LatLong> route = List.of(
                new LatLong(5, -80),
                new LatLong(5, -82),
                new LatLong(5, -83)
        );
        final List<LatLong> remainingRoute = location.remainingRoute(route);
        assertEquals(List.of(
                new LatLong(6, -81),
                new LatLong(5, -82),
                new LatLong(5, -83)
        ), remainingRoute);
    }

    @Test
    public void testFortPierceToFortMyers() {
        final List<LatLong> route = List.of(
                new LatLong(27.464336, -80.316056),
                new LatLong(27.474215, -80.276067),
                new LatLong(27.255667, -80.169947),
                new LatLong(27.116472, -80.111733),
                new LatLong(27.045271, -80.074074),
                new LatLong(26.965859, -80.048419),
                new LatLong(26.818930, -80.000386),
                new LatLong(26.496805, -80.021040),
                new LatLong(26.220099, -80.060551),
                new LatLong(25.931726, -80.067937),
                new LatLong(25.834770, -80.075650),
                new LatLong(25.751492, -80.078567),
                new LatLong(25.247685, -80.160723),
                new LatLong(25.055714, -80.302710),
                new LatLong(24.835746, -80.572094),
                new LatLong(24.592965, -81.297224),
                new LatLong(24.509402, -81.734804),
                new LatLong(24.513069, -81.817047),
                new LatLong(24.752602, -81.882376),
                new LatLong(26.522385, -81.993888)
        );
        final Quantity<Length> distance = LatLong.distance(route);
        assertEquals(676860.54, distance.getValue().doubleValue(), 0.1);
        final LatLong location = new LatLong(25.183, -80.3203);
        final List<LatLong> remainingRoute = location.remainingRoute(route);
        assertEquals(remainingRoute, List.of(
                new LatLong(25.183, -80.3203),
                new LatLong(25.055714, -80.302710),
                new LatLong(24.835746, -80.572094),
                new LatLong(24.592965, -81.297224),
                new LatLong(24.509402, -81.734804),
                new LatLong(24.513069, -81.817047),
                new LatLong(24.752602, -81.882376),
                new LatLong(26.522385, -81.993888)
        ));

        // Exercise the code path that uses only the final point in the supplied route.
        final LatLong shortRemainingLocation = new LatLong(26.5, -81.98);
        final List<LatLong> shortRemainingRoute = shortRemainingLocation.remainingRoute(route);
        assertEquals(2, shortRemainingRoute.size());

        assertThrows(IllegalArgumentException.class,
                () -> shortRemainingLocation.remainingRoute(Collections.emptyList()));

        assertEquals(2, shortRemainingLocation.remainingRoute(List.of(new LatLong(26.522385, -81.993888))).size());
        assertEquals(3, new LatLong(24.74, -81.87).remainingRoute(
                List.of(new LatLong(24.752602, -81.882376), new LatLong(26.522385, -81.993888))).size());
    }

    @Test
    public void testEquals() {
        final LatLong a = new LatLong(25.250, -80.125);
        final LatLong b = new LatLong(25.250, -80.125);
        final LatLong c = new LatLong(25.750, -80.125);
        final LatLong d = new LatLong(25.250, -80.500);
        assertNotEquals(a, null);
        assertNotEquals(a, "not a LatLong");
        assertEquals(a, a);
        assertEquals(a, b);
        assertNotEquals(a, c);
        assertNotEquals(a, d);
    }

    @Test
    public void testHashCode() {
        final LatLong a = new LatLong(25.250, -80.125);
        final LatLong c = new LatLong(25.750, -80.125);
        final LatLong d = new LatLong(25.250, -80.500);
        final Set<LatLong> s = Set.of(a, c, d);
        assertEquals(3, s.size());
    }

    @Test
    public void testConstructor() {
        final LatLong a = new LatLong(25.250, -80.125);
        assertEquals(a, new LatLong(a));
        assertThrows(IllegalArgumentException.class, () -> new LatLong(91, 0));
        assertThrows(IllegalArgumentException.class, () -> new LatLong(-91, 0));
        assertThrows(IllegalArgumentException.class, () -> new LatLong(0, 181));
        assertThrows(IllegalArgumentException.class, () -> new LatLong(0, -181));
    }

    @Test
    public void testGeoURI() {
        assertThrows(IllegalArgumentException.class,
                () -> GeographyUtilities.parseGeoURI(URI.create("geography:25.250,-80.125")));

        assertThrows(IllegalArgumentException.class, () -> GeographyUtilities.parseGeoURI(URI.create("geo:91,0")));
        assertThrows(IllegalArgumentException.class, () -> GeographyUtilities.parseGeoURI(URI.create("geo:-91,0")));
        assertThrows(IllegalArgumentException.class, () -> GeographyUtilities.parseGeoURI(URI.create("geo:0,181")));
        assertThrows(IllegalArgumentException.class, () -> GeographyUtilities.parseGeoURI(URI.create("geo:0,-181")));

        final LatLong a = new LatLong(25.25, -80.125);
        final URI ag = URI.create("geo:25.25,-80.125");
        assertEquals(a, GeographyUtilities.parseGeoURI(ag));
        assertEquals(ag, GeographyUtilities.parseGeoURI(ag).toGeoURI());

        final LatLong b = new LatLong(25, -80);
        final URI bg = URI.create("geo:25,-80");
        assertEquals(b, GeographyUtilities.parseGeoURI(bg));
        assertEquals(bg, GeographyUtilities.parseGeoURI(bg).toGeoURI());
    }

    @Test
    public void testToString() {
        assertEquals("25.25º, 80.125º", new LatLong(25.25, 80.125).toString());
    }

}