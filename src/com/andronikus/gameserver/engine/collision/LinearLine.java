package com.andronikus.gameserver.engine.collision;

import java.awt.Point;

/**
 * Linear line in a collision.
 *
 * @author Andronikus
 */
public class LinearLine {

    //Slope of the line
    double slope;

    //Slope is infinite
    boolean slopeIsInfinite = false;

    //y intercept
    int yIntercept;

    //The first end point
    Point begin;

    //The last end point
    Point end;

    /**
     * Linear line between an end and starting point
     *
     * @param start Start point
     * @param end   End point
     */
    public LinearLine(Point start, Point end) {
        constructLinearLine(start, end);
    }

    /**
     * Linear line whos values will need to be filled in manually
     */
    public LinearLine() {
    }

    /**
     * Delegate method to handle early returns
     *
     * @param start Start point
     * @param end   End point
     */
    private void constructLinearLine(Point start, Point end) {
        //Determine if slope is infinite or if it needs to be calculated
        if (start.x != end.x) {
            //Make sure that our X's are in order, assuming we know how CollisionUtil works
            if (start.x > end.x) {
                constructLinearLine(end, start);
                return;
            }
            slope = (double) (end.y - start.y) / (double) (end.x - start.x);
        } else {
            slopeIsInfinite = true;
        }

        //Sets beginning and start points
        this.begin = start;
        this.end = end;

        //If we have an infinite slope, set the yIntercept to the smallest y value, if not, math it out
        if (slopeIsInfinite) {

            if (start.y > end.y) {
                yIntercept = end.y;
            } else {
                yIntercept = start.y;
            }

        } else {
            yIntercept = (int) (start.y - (double) (slope * (double) start.x));
        }
    }
}
