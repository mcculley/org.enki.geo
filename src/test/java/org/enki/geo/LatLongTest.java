package org.enki.geo;

import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static systems.uom.common.USCustomary.DEGREE_ANGLE;
import static systems.uom.common.USCustomary.NAUTICAL_MILE;

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
        assertEquals("0º 0.00000000', 0º 0.00000000'", new LatLong.DegreesDecimalMinutes(equator1).toString());
    }

    @Test
    void conversion1() {
        final LatLong l1 = new LatLong(27.50, -82.75);
        final LatLong.DegreesDecimalMinutes lddm1 = new LatLong.DegreesDecimalMinutes(l1);
        assertEquals(27, lddm1.latitudeDegrees);
        assertEquals(-82, lddm1.longitudeDegrees);
        assertEquals(30, lddm1.latitudeDecimalMinutes);
        assertEquals(45, lddm1.longitudeDecimalMinutes);
        assertEquals("27º 30.00000000', -82º 45.00000000'", lddm1.toString());
        assertEquals(l1, lddm1.getLatLong());
        final LatLong.DegreesMinutesSeconds ldms1 = new LatLong.DegreesMinutesSeconds(l1);
        assertEquals(27, ldms1.latitudeDegrees);
        assertEquals(-82, ldms1.longitudeDegrees);
        assertEquals(30, ldms1.latitudeMinutes);
        assertEquals(45, ldms1.longitudeMinutes);
        assertEquals(0, ldms1.latitudeSeconds);
        assertEquals(0, ldms1.longitudeSeconds);
        assertEquals("27º 30' 0.00000000\" N, 82º 45' 0.00000000\" W", ldms1.toString());
        assertEquals(l1, ldms1.getLatLong());
    }

    @Test
    void conversion2() {
        final LatLong l2 = new LatLong(27.625, -82.875);
        final LatLong.DegreesDecimalMinutes lddm2 = new LatLong.DegreesDecimalMinutes(l2);
        assertEquals(27, lddm2.latitudeDegrees);
        assertEquals(-82, lddm2.longitudeDegrees);
        assertEquals(37.5, lddm2.latitudeDecimalMinutes);
        assertEquals(52.5, lddm2.longitudeDecimalMinutes);
        assertEquals("27º 37.50000000', -82º 52.50000000'", lddm2.toString());
        assertEquals(l2, lddm2.getLatLong());
        final LatLong.DegreesMinutesSeconds ldms2 = new LatLong.DegreesMinutesSeconds(l2);
        assertEquals(27, ldms2.latitudeDegrees);
        assertEquals(-82, ldms2.longitudeDegrees);
        assertEquals(37, ldms2.latitudeMinutes);
        assertEquals(52, ldms2.longitudeMinutes);
        assertEquals(30, ldms2.latitudeSeconds);
        assertEquals(30, ldms2.longitudeSeconds);
        assertEquals("27º 37' 30.00000000\" N, 82º 52' 30.00000000\" W", ldms2.toString());
        assertEquals(l2, ldms2.getLatLong());
    }

}