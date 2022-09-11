package com.andronikus.gameserver.engine.collision;

/**
 * Common utility for determining if two objects are colliding with each other.
 *
 * @author Andronikus
 */
public class CollisionUtil {

     /**
     * Between two rectangles, is there a collision based on the eliptoid in the rectangles?
     *
     * @param x0 First X
     * @param y0 First Y
     * @param width0 First width
     * @param height0 First height
     * @param tiltAngle0 Angle at which the first hitbox is tilted
     * @param x1 Second X
     * @param y1 Second Y
     * @param width1 Second width
     * @param height1 Second height
     * @param tiltAngle1 Angle at which the second hitbox is tilted
     * @return Are two rectangular hitboxes colliding with one another?
     */
    public static boolean boundedEliptoidCollisionTest(
        long x0, long y0, int width0, int height0, double tiltAngle0,
        long x1, long y1, int width1, int height1, double tiltAngle1
    ) {
        // The given X and Y coordinates are the corner points, let's move to the center.
        final long centerX0 = x0 /*+ (width0 / 2)*/;
        final long centerY0 = y0 /*+ (height0 / 2)*/;
        final long centerX1 = x1 /*+ (width1 / 2)*/;
        final long centerY1 = y1 /*+ (height1 / 2)*/;

        // Next up, calculate the distance between the two
        final long xDelta0 = centerX1 - centerX0;
        final long xDelta1 = centerX0 - centerX1;
        final long yDelta0 = centerY1 - centerY0;
        final long yDelta1 = centerY0 - centerY1;

        final double distanceSquared = xDelta0 * xDelta0 + yDelta0 * yDelta0;
        final double distance = Math.sqrt(distanceSquared);

        // Early return if Deltas are 0, that would break things
        if (distance == 0) {
            return true;
        }

        // Calculate angle from one hitbox to the next
        final double theta0 = extendedRangeArcCosine(xDelta0, yDelta0, distance);
        final double theta1 = extendedRangeArcCosine(xDelta1, yDelta1, distance);

        // Calculate radial angle
        final double radialAngle0 = theta0 - tiltAngle0;
        final double radialAngle1 = theta1 - tiltAngle1;

        // Calculate Max distance for a collision to occur
        final double radialX0 = Math.cos(radialAngle0) * (double) width0 / 2;
        final double radialY0 = Math.sin(radialAngle0) * (double) height0 / 2;
        final double radialX1 = Math.cos(radialAngle1) * (double) width1 / 2;
        final double radialY1 = Math.sin(radialAngle1) * (double) height1 / 2;

        // TODO optimize to skip for squares
        final double radialDistance0 = Math.sqrt(radialX0 * radialX0 + radialY0 * radialY0);
        final double radialDistance1 = Math.sqrt(radialX1 * radialX1 + radialY1 * radialY1);

        // If distance between objects is less than combined radius', objects are colliding
        return distance <= radialDistance0 + radialDistance1;
    }

    private static double extendedRangeArcCosine(double adjacent, double opposite, double hypotenuse) {
        double postFix = 1;
        if (opposite < 0) {
            postFix = -1;
        }
        return (Math.acos(adjacent / hypotenuse) * postFix + Math.PI * 2) % (Math.PI * 2);
    }
}
