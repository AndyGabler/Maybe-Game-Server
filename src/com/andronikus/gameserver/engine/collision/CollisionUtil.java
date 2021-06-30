package com.andronikus.gameserver.engine.collision;

import java.awt.Point;
import java.util.ArrayList;

/**
 * Common utility for determining if two objects are colliding with each other.
 *
 * @author Andronikus
 */
public class CollisionUtil {

     /**
     * Between two rectangles, does one completely engulf the other?
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
    public static boolean rectangularHitboxesCollide(
        long x0, long y0, int width0, int height0, double tiltAngle0,
        long x1, long y1, int width1, int height1, double tiltAngle1
    ) {
        //First and foremost, find the points we are working with
        //Frankly, it is easier to do this when working with two arrays of points

        //Get the points
        ArrayList<Point> firstPoints = cornerPointsOnRectangle(x0, y0, width0, height0, tiltAngle0);
        ArrayList<Point> secondPoints = cornerPointsOnRectangle(x1, y1, width1, height1, tiltAngle1);

        //Made it past that? Check if the two tilted shapes are hitting
        ArrayList<LinearLine> firstsLines = new ArrayList<>();
        ArrayList<LinearLine> secondsLines = new ArrayList<>();

        for (int pos = 0; pos < firstPoints.size(); pos++) {
            if (pos == 3) {
                firstsLines.add(new LinearLine(firstPoints.get(pos), firstPoints.get(0)));
                secondsLines.add(new LinearLine(secondPoints.get(pos), secondPoints.get(0)));
            } else {
                firstsLines.add(new LinearLine(firstPoints.get(pos), firstPoints.get(pos + 1)));
                secondsLines.add(new LinearLine(secondPoints.get(pos), secondPoints.get(pos + 1)));
            }
        }

        return linearLinesIntersect(firstsLines, secondsLines) ||
            checkEngulfmentMethodForHitboxes(
                x0, y0, width0, height0,
                x1, y1, width1, height1,
                firstPoints, secondPoints
            );
    }


    /**
     * The corner points based on how tilted a rectangle is.
     *
     * @param x Centerpoint of the hitbox
     * @param y Centerpoint of the hitbox
     * @param width Width of the hitbox
     * @param height Height of the hitbox
     * @param tiltAngle Angle at which the hitbox is tilted
     * @return The points
     */
    private static ArrayList<Point> cornerPointsOnRectangle(long x, long y, int width, int height, double tiltAngle) {
        //Angle between center and corners
        double theta = Math.atan((double)height / (double)width);

        //Acting like a circle now
        double xRad = width / 2;
        double yRad = height / 2;

        Point centerPoint = new Point((int) x, (int)y);

        double[] angles = {
            theta + tiltAngle,
            Math.PI - theta + tiltAngle,
            Math.PI + theta + tiltAngle,
            Math.PI * 2 - theta + tiltAngle
        };

        ArrayList<Point> points = new ArrayList<>();

        //Distance between the points and the center
        double distance = Math.sqrt(Math.pow(xRad, 2) + Math.pow(yRad, 2));

        for (double angle : angles) {
            int xDisplace = (int) (Math.cos(angle) * distance);
            int yDisplace = (int) (Math.sin(angle) * distance);
            points.add(new Point(centerPoint.x + xDisplace, centerPoint.y + yDisplace));
        }

        return points;
    }

    /**
     * Do two sets of linear lines intersect?
     *
     * @param firstsLines First set of lines
     * @param secondsLines Second set of lines
     * @return Are they intersecting
     */
    private static boolean linearLinesIntersect(ArrayList<LinearLine> firstsLines, ArrayList<LinearLine> secondsLines) {
        //Check by checking if linear lines are colliding
        for (LinearLine lineOne : firstsLines) {

            //First, check if the slope of the line is infinite
            if (lineOne.slopeIsInfinite) {
                //Line one is infinite... now let's find out about line two
                for (LinearLine lineTwo : secondsLines) {
                    if (lineTwo.slopeIsInfinite) {
                        //This means that both lines are infinite
                        if (twoInfiniteSlopeLinesColliding(lineOne, lineTwo)) {
                            return true;
                        }
                    } else {
                        //Line two is slanted, line one is infinite
                        if (infiniteAndSlantedLinesColliding(lineTwo, lineOne)) {
                            return true;
                        }
                    }
                } //End of line two iteration when line one is infinite
            } else {
                //Line one is not infinite
                for (LinearLine lineTwo : secondsLines) {

                    //Find out if line two is infinite too
                    if (lineTwo.slopeIsInfinite) {
                        //Line one is slanted. Line two is not
                        if (infiniteAndSlantedLinesColliding(lineOne, lineTwo)) {
                            return true;
                        }
                    } else {
                        //BOTH lines are slanted and not infinite
                        if (nonInifiniteLinesColliding(lineOne, lineTwo)) {
                            return true;
                        }
                    } //End of figuring out line two and acting
                } //End of iteration through seconds lines
            } //End of if block determining collision at all
        } //End of the iteration through first sets of lines
        return false;
    }

    /**
     * Is a slanted line and a line with infinite line colliding?
     *
     * @param slanted The slanted line
     * @param infinite The infinite line
     * @return Whether or not they are colliding
     */
    private static boolean infiniteAndSlantedLinesColliding(LinearLine slanted, LinearLine infinite) {
        //X of infinite must be on slanted's exact range
        //Y point of slanted at that x has to be in range

        //Not in range? Stop
        if (slanted.begin.getX() > infinite.end.getX() || slanted.end.getX() < infinite.end.getX()) {
            return false;
        }

        //Slanted's position at this point
        double yOfSlanted = (slanted.slope * infinite.begin.getX()) + slanted.yIntercept;

        //High and low of the infinite line
        double lowYInfinite;
        double highYInfinite;

        if (infinite.begin.getY() > infinite.end.getY()) {
            lowYInfinite = infinite.end.getY();
            highYInfinite = infinite.begin.getY();
        } else {
            lowYInfinite = infinite.begin.getY();
            highYInfinite = infinite.end.getY();
        }

        return yOfSlanted >= lowYInfinite && yOfSlanted <= highYInfinite;
    }

    /**
     * Are two lines who do not have infinite slopes colliding?
     *
     * @param lineOne First line
     * @param lineTwo Second line
     * @return Are they colliding?
     */
    private static boolean nonInifiniteLinesColliding(LinearLine lineOne, LinearLine lineTwo) {
        int xIntercept = (int)((lineTwo.yIntercept - lineOne.yIntercept) / (lineOne.slope - lineTwo.slope));

        return lineOne.begin.x <= xIntercept && lineOne.end.x >= xIntercept && lineTwo.begin.x <= xIntercept && lineTwo.end.x >= xIntercept;
    }

    /**
     * Are two infinite lines colliding?
     *
     * @param lineOne First line
     * @param lineTwo Second line
     * @return Are they colliding?
     */
    private static boolean twoInfiniteSlopeLinesColliding(LinearLine lineOne, LinearLine lineTwo) {
        //Don't even bother finding smallest y's if the X's are not the same
        if (lineOne.begin.x != lineTwo.begin.x) {
            return false;
        }

        //Get line one's high and end y
        double oneLow;
        double oneHigh;

        if (lineOne.begin.getY() > lineOne.end.getY()) {
            oneLow = lineOne.end.getY();
            oneHigh = lineOne.begin.getY();
        } else {
            oneHigh = lineOne.end.getY();
            oneLow = lineOne.begin.getY();
        }

        //Get line two's high and end u
        double twoLow;
        double twoHigh;

        if (lineTwo.begin.getY() > lineTwo.end.getY()) {
            twoLow = lineTwo.end.getY();
            twoHigh = lineTwo.begin.getY();
        } else {
            twoHigh = lineTwo.end.getY();
            twoLow = lineTwo.begin.getY();
        }
        //Are lines ones points within range of line two or line two's between one's?
        return (oneLow >= twoLow && oneLow <= twoHigh) || (oneHigh >= twoLow && oneHigh <= twoHigh) ||
                (twoLow >= oneLow && twoLow <= oneHigh) || (twoHigh >= oneLow && twoHigh <= oneHigh);
    }

    /**
     * Between two rectangles, does one completely engulf the other?
     *
     * @param x0 First X
     * @param y0 First Y
     * @param width0 First width
     * @param height0 First height
     * @param x1 Second X
     * @param y1 Second Y
     * @param width1 Second width
     * @param height1 Second height
     * @param onePoints First set of points
     * @param twoPoints Second set of points
     * @return Is one engulfing the other?
     */
    private static boolean checkEngulfmentMethodForHitboxes(
            long x0, long y0, int width0, int height0,
            long x1, long y1, int width1, int height1,
            ArrayList<Point> onePoints, ArrayList<Point> twoPoints
    ) {
        //lineSetEngulfsPoint(Point in, Point[] pointSet)
        if (width0 >= width1 && height0 >= height1) {
            //One might be engulfing two
            Point center = new Point((int) x1, (int) y1);

            return lineSetEngulfsPoint(center, onePoints);

        } else if (width0 <= width1 && height0 <= height1) {
            //Two might be enfulfing one
            Point center = new Point((int) x0, (int) y0);

            return lineSetEngulfsPoint(center, twoPoints);

        }
        return false;
    }

    /**
     * Calls method that detects if point set is engulfing a point.
     *
     * @param in Point that could be engulfed
     * @param pointSet Point set that could be engulfing
     * @return Is the point engulfed?
     */
    private static boolean lineSetEngulfsPoint(Point in, ArrayList<Point> pointSet) {
        Point[] points = new Point[pointSet.size()];

        for (int pos = 0; pos < points.length; pos++) {
            points[pos] = pointSet.get(0);
        }

        return lineSetEngulfsPoint(in, points);
    }

    /**
     * Does a point set's imaginary line set engulf a point?
     *
     * @param in The point
     * @param pointSet The point set
     * @return Does the imaginary point set's line set engulf a point?
     */
    private static boolean lineSetEngulfsPoint(Point in, Point[] pointSet) {

        /*
         * This is a three step algorithm:
         *    1) Get the angle of every point
         *    2) Figure out the rotation each angle is on
         *    3) Check if it has made a full rotation outside of the point
         */

        double[] angles = new double[pointSet.length + 1];

        //Step #1
        for (int pos = 0; pos < angles.length - 1; pos++) {

            Point pointInSet = pointSet[pos];

            double theta = Math.atan( (pointInSet.getY() - in.getY()) / (pointInSet.getX() - in.getX()) );

            if (pointInSet.getY() < in.getY()) {
                if (pointInSet.getX() > in.getX()) {
                    theta = theta + Math.PI * 2;
                } else {
                    theta = theta + Math.PI;
                }
            } else {
                if (pointInSet.getX() <= in.getX()) {
                    theta = theta + Math.PI;
                }
            }

            angles[pos] = theta;

        }
        angles[angles.length - 1] = angles[0];

        //Step #2 (if you get a bug, check here)
        final double fullRotation = Math.PI * 2;

        for (int pos = 0; pos < angles.length; pos++) {
            //Multiple of 360 this rotation
            int multipleOf360 = 0;

            //Index of the current angle
            int indexOfCurrent;

            //Value of the angles
            double currentAngle;
            double previousAngle;

            currentAngle = angles[pos];
            indexOfCurrent = pos;

            if (pos == 0) {
                previousAngle = angles[angles.length - 1];
            } else {
                previousAngle = angles[pos -1];
            }

            //Okay, we found our angles. Now set them
            while ( Math.abs( (currentAngle + fullRotation * multipleOf360) - previousAngle ) > Math.abs( (currentAngle + fullRotation * (multipleOf360 + 1) ) - previousAngle)) {
                multipleOf360++;
            }

            while ( Math.abs( (currentAngle + fullRotation * multipleOf360) - previousAngle ) > Math.abs( (currentAngle + fullRotation * (multipleOf360 - 1) ) - previousAngle)) {
                multipleOf360--;
            }

            currentAngle += (fullRotation * multipleOf360);
            //Current angle has been calculated, now put it back in the array
            angles[indexOfCurrent] = currentAngle;
        }

        //Step 3
        return angles[0] != angles[angles.length - 1];
    }
}
