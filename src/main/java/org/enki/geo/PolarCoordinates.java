package org.enki.geo;

import org.jetbrains.annotations.NotNull;

import javax.measure.Quantity;
import javax.measure.quantity.Angle;
import java.awt.geom.Point2D;
import java.util.function.Function;

import static org.enki.core.Utilities.formatWithoutTrailingZeros;
import static tech.units.indriya.unit.Units.RADIAN;

/**
 * A location using polar coordinates.
 */
public class PolarCoordinates {

    /**
     * The distance from the center.
     */
    public final double r;

    /**
     * The angle in radians.
     */
    public final double theta;

    /**
     * Create new PolarCoordinate.
     *
     * @param r     the radius of the point
     * @param theta the angle of the point
     */
    public PolarCoordinates(final double r, final @NotNull Quantity<Angle> theta) {
        this.r = r;
        this.theta = theta.to(RADIAN).getValue().doubleValue();
    }

    /**
     * Convert a PolarCoordinate to a Cartesian coordinate in Point2D.Double.
     *
     * @return a Point2D.Double
     */
    public final @NotNull Point2D.Double toCartesian() {
        return toCartesian(Function.identity(), Function.identity());
    }

    /**
     * Convert a PolarCoordinate to a Cartesian coordinate in Point2D.Double using transformations for the angle and
     * radius.
     *
     * @param radiusTransformer the Function to apply to the radius
     * @param thetaTransformer  the Function to apply to the angle
     * @return a Point2D.Double
     */
    public final @NotNull Point2D.Double toCartesian(final @NotNull Function<Double, Double> radiusTransformer,
                                                     final @NotNull Function<Double, Double> thetaTransformer) {
        final double transformedRadius = radiusTransformer.apply(r);
        final double rotatedTheta = thetaTransformer.apply(theta);
        final double x = transformedRadius * Math.cos(rotatedTheta);
        final double y = transformedRadius * Math.sin(rotatedTheta);
        return new Point2D.Double(x, y);
    }

    @Override
    public @NotNull String toString() {
        return "(" + formatWithoutTrailingZeros(r) + ", " + formatWithoutTrailingZeros(theta) + "º)";
    }

}