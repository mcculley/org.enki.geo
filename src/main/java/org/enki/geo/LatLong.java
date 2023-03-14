package org.enki.geo;

import com.google.common.collect.ImmutableList;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Length;

import java.net.URI;
import java.util.List;
import java.util.Objects;

import static java.lang.Math.abs;
import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.copySign;
import static java.lang.Math.cos;
import static java.lang.Math.floor;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;
import static systems.uom.common.USCustomary.DEGREE_ANGLE;
import static tech.units.indriya.unit.Units.METRE;
import static tech.units.indriya.unit.Units.RADIAN;

// FIXME: This needs verification at 180/-180 boundary (and other boundaries).

public class LatLong {

    public final double latitude; // The latitude in degrees.
    public final double longitude; // The longitude in degrees.

    /**
     * Construct a LatLong using a supplied latitude and longitude in degrees.
     *
     * @param latitude  latitude in degrees
     * @param longitude longitude in degrees
     */
    public LatLong(final double latitude, final double longitude) {
        if (abs(longitude) > 180) {
            throw new IllegalArgumentException("invalid longitude " + longitude);
        }

        if (abs(latitude) > 90) {
            throw new IllegalArgumentException("invalid latitude " + latitude);
        }

        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Construct a LatLong using a supplied geo URI (https://datatracker.ietf.org/doc/html/rfc5870).
     *
     * @param geoURI the GeoURI
     */
    public LatLong(final URI geoURI) {
        if (!geoURI.getScheme().equals("geo")) {
            throw new IllegalArgumentException(
                    String.format("unexpected scheme '%s' in '%s'", geoURI.getScheme(), geoURI));
        }

        final String location = geoURI.getSchemeSpecificPart().split(";")[0];
        final String[] coordinates = location.split(",");
        latitude = Double.parseDouble(coordinates[0]);
        longitude = Double.parseDouble(coordinates[1]);

        if (abs(longitude) > 180) {
            throw new IllegalArgumentException("invalid longitude " + longitude);
        }

        if (abs(latitude) > 90) {
            throw new IllegalArgumentException("invalid latitude " + latitude);
        }
    }

    /**
     * Construct a LatLong using a supplied LatLong.
     *
     * @param other the existing LatLong object
     */
    public LatLong(final LatLong other) {
        this(other.latitude, other.longitude);
    }

    // Radius of the Earth, according to WGS-84 (https://apps.dtic.mil/sti/pdfs/ADA280358.pdf).
    private static final Quantity<Length> radiusOfEarth = Quantities.getQuantity(6378137, METRE);

    /**
     * Calculate distance squared between this coordinate and another.
     *
     * @param b the other LatLong coordinate
     * @return distance in meters
     */
    public Quantity<Length> distanceSquared(final LatLong b) {
        // FIXME: See org.geotools.geometry.jts.JTS.orthodromicDistance for a more accurate way to calculate distance.
        final double latDistance = toRadians(b.latitude - latitude);
        final double lonDistance = toRadians(b.longitude - longitude);
        final double a = sin(latDistance / 2) * sin(latDistance / 2) +
                cos(toRadians(latitude)) * cos(toRadians(b.latitude)) * sin(lonDistance / 2) * sin(lonDistance / 2);
        final double c = 2 * atan2(sqrt(a), sqrt(1 - a));
        return Quantities.getQuantity(pow(radiusOfEarth.getValue().doubleValue() * c, 2), METRE);
    }

    /**
     * Compute distance in meters between this coordinate and another.
     *
     * @param b the other LatLong coordinate
     * @return the distance in meters
     */
    public Quantity<Length> distance(final LatLong b) {
        return Quantities.getQuantity(sqrt(distanceSquared(b).getValue().doubleValue()), METRE);
    }

    public static Quantity<Length> distance(final List<LatLong> route) {
        Quantity<Length> sum = Quantities.getQuantity(0, METRE);
        for (int i = 1; i < route.size(); i++) {
            sum = sum.add(route.get(i).distance(route.get(i - 1)));
        }

        return sum;
    }

    /**
     * Given a heading and a distance from this location, compute the new location.
     *
     * @param heading  the heading
     * @param distance the distance
     * @return the projected location
     */
    public LatLong plus(final Quantity<Angle> heading, final Quantity<Length> distance) {
        final double bearingR = heading.to(RADIAN).getValue().doubleValue();
        final double latR = toRadians(latitude);
        final double lonR = toRadians(longitude);

        final double distanceToRadius =
                distance.to(METRE).getValue().doubleValue() / radiusOfEarth.to(METRE).getValue().doubleValue();

        final double newLatR =
                asin(sin(latR) * cos(distanceToRadius) + cos(latR) * sin(distanceToRadius) * cos(bearingR));
        final double newLonR = lonR + atan2(sin(bearingR) * sin(distanceToRadius) * cos(latR),
                cos(distanceToRadius) - sin(latR) * sin(newLatR));

        final double latNew = toDegrees(newLatR);
        final double lonNew = toDegrees(newLonR);

        return new LatLong(latNew, lonNew);
    }

    /**
     * Return a heading in degrees from this location to a specified coordinate.
     *
     * @param b the coordinate
     * @return the heading in degrees
     */
    public Quantity<Angle> heading(final LatLong b) {
        final double Δlong = toRadians(b.longitude - longitude);
        final double lat1 = toRadians(latitude);
        final double lat2 = toRadians(b.latitude);
        final double θ = atan2(sin(Δlong) * cos(lat2), cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(Δlong));
        final double headingInDegrees = toDegrees(θ);
        return Quantities.getQuantity(headingInDegrees < 0 ? 360 + headingInDegrees : headingInDegrees, DEGREE_ANGLE);
    }

    /**
     * Given two vectors ab and ac described by location objects a, b, and c, compute the dot product.
     *
     * @param a the start point of the vector ab
     * @param b the end point of the vector ab
     * @param c the end point of the vector ac
     * @return the dot product
     */
    private static double dotProduct(final LatLong a, final LatLong b, final LatLong c) {
        // FIXME: This probably breaks around 0º and 180º longitude. Add test cases for that and fix.
        final double abx = b.longitude - a.longitude;
        final double aby = b.latitude - a.latitude;
        final double acx = c.longitude - a.longitude;
        final double acy = c.latitude - a.latitude;
        return abx * acx + aby * acy;
    }

    /**
     * Given a route, return the remaining route, assuming this location is somewhere before or along the route, but not
     * necessarily collinear with the route.
     *
     * @param route A route
     * @return The remaining route, which should either be location and the initial route, or location and the remaining
     * points on the route.
     */
    public List<LatLong> remainingRoute(final List<LatLong> route) {
        if (route.size() < 3) {
            // FIXME: Handle small routes correctly. This assumes that we are somewhere along a route of only two points.
            return List.of(this, route.get(route.size() - 1));
        }

        final int n = route.size();
        final double[] distance = new double[n];
        double closestDistance = Double.MAX_VALUE;
        int closestIndex = -1;
        for (int i = 0; i < n; i++) {
            distance[i] = distance(route.get(i)).getValue().doubleValue();
            if (distance[i] < closestDistance) {
                closestDistance = distance[i];
                closestIndex = i;
            }
        }

        if (closestIndex == n - 1) {
            return List.of(this, route.get(route.size() - 1));
        }

        // Find the dot product of the vector from the closest point to this location and the closest point to the
        // next point. A positive value means this location is ahead of the closest point along the route and that we
        // should use the next point.
        final double dotProduct = dotProduct(route.get(closestIndex), route.get(closestIndex + 1), this);
        final int bestIndex = dotProduct < 0 ? closestIndex : closestIndex + 1;

        final ImmutableList.Builder<LatLong> newList = new ImmutableList.Builder<>();
        newList.add(this);
        newList.addAll(route.subList(bestIndex, n));
        return newList.build();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final LatLong latLong = (LatLong) o;
        return Double.compare(latLong.latitude, latitude) == 0 && Double.compare(latLong.longitude, longitude) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }

    @Override
    public String toString() {
        return String.format("%fº, %fº", latitude, longitude);
    }

    // FIXME: Handle altitude in geo URI.

    /**
     * Convert this location to a geo URI (https://datatracker.ietf.org/doc/html/rfc5870).
     *
     * @return this location encoded as a geo URI.
     */
    public URI toGeoURI() {
        return URI.create(String.format("geo:%f,%f", latitude, longitude));
    }

    public static class DegreesDecimalMinutes {

        public final int latitudeDegrees, longitudeDegrees;
        public final double latitudeDecimalMinutes, longitudeDecimalMinutes;

        public DegreesDecimalMinutes(final int latitudeDegrees, final double latitudeDecimalMinutes,
                                     final int longitudeDegrees, final double longitudeDecimalMinutes) {
            this.latitudeDegrees = latitudeDegrees;
            this.latitudeDecimalMinutes = latitudeDecimalMinutes;
            this.longitudeDegrees = longitudeDegrees;
            this.longitudeDecimalMinutes = longitudeDecimalMinutes;
        }

        public DegreesDecimalMinutes(final LatLong c) {
            this((int) c.latitude, (abs(c.latitude) - floor(abs(c.latitude))) * 60.0, (int) c.longitude,
                    (abs(c.longitude) - floor(abs(c.longitude))) * 60.0);
        }

        public String toString() {
            return String.format("%dº %.8f', %dº %.8f'", latitudeDegrees, latitudeDecimalMinutes, longitudeDegrees,
                    longitudeDecimalMinutes);
        }

        public String toStringCardinal() {
            return String.format("%dº %.8f' %c, %dº %.8f' %c", abs(latitudeDegrees), latitudeDecimalMinutes,
                    (latitudeDegrees > 0 ? 'N' : 'S'), abs(longitudeDegrees),
                    longitudeDecimalMinutes, (longitudeDegrees > 0 ? 'E' : 'W'));
        }

        public LatLong getLatLong() {
            final double latitude = copySign(abs(latitudeDegrees) + latitudeDecimalMinutes / 60, latitudeDegrees);
            final double longitude = copySign(abs(longitudeDegrees) + longitudeDecimalMinutes / 60, longitudeDegrees);
            return new LatLong(latitude, longitude);
        }

    }

    public static class DegreesMinutesSeconds {

        public final int latitudeDegrees, longitudeDegrees;
        public final int latitudeMinutes, longitudeMinutes;
        public final double latitudeSeconds, longitudeSeconds;

        public DegreesMinutesSeconds(final int latitudeDegrees, final int latitudeMinutes, final double latitudeSeconds,
                                     final int longitudeDegrees, final int longitudeMinutes,
                                     final double longitudeSeconds) {
            this.latitudeDegrees = latitudeDegrees;
            this.latitudeMinutes = latitudeMinutes;
            this.longitudeDegrees = longitudeDegrees;
            this.longitudeMinutes = longitudeMinutes;
            this.latitudeSeconds = latitudeSeconds;
            this.longitudeSeconds = longitudeSeconds;
        }

        public DegreesMinutesSeconds(final LatLong c) {
            final double absoluteLatitude = abs(c.latitude);
            this.latitudeDegrees = (int) c.latitude;
            final double minutesNotTruncatedLatitude = (absoluteLatitude - abs(latitudeDegrees)) * 60;
            this.latitudeMinutes = (int) minutesNotTruncatedLatitude;
            this.latitudeSeconds = (minutesNotTruncatedLatitude - latitudeMinutes) * 60;
            final double absoluteLongitude = abs(c.longitude);
            this.longitudeDegrees = (int) c.longitude;
            final double minutesNotTruncatedLongitude = (absoluteLongitude - abs(longitudeDegrees)) * 60;
            this.longitudeMinutes = (int) minutesNotTruncatedLongitude;
            this.longitudeSeconds = (minutesNotTruncatedLongitude - longitudeMinutes) * 60;
        }

        public String toString() {
            return String.format("%dº %d' %.8f\" %c, %dº %d' %.8f\" %c",
                    abs(latitudeDegrees), latitudeMinutes, latitudeSeconds, (latitudeDegrees > 0 ? 'N' : 'S'),
                    abs(longitudeDegrees), longitudeMinutes, longitudeSeconds, (longitudeDegrees > 0 ? 'E' : 'W'));
        }

        public LatLong getLatLong() {
            final double latitude =
                    copySign(abs(latitudeDegrees) + latitudeMinutes / 60.0 + latitudeSeconds / 3600.0, latitudeDegrees);
            final double longitude =
                    copySign(abs(longitudeDegrees) + longitudeMinutes / 60.0 + longitudeSeconds / 3600.0,
                            longitudeDegrees);
            return new LatLong(latitude, longitude);
        }

    }

}
