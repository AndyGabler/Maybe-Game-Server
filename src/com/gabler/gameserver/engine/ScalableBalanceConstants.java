package com.gabler.gameserver.engine;

/**
 * Constants that depend on one another for balancing purposes. These are used to keep the size of the game and map in
 * check.
 *
 * @author Andy Gabler
 */
public class ScalableBalanceConstants {

    /*
     * When player is rotating, this represents the amount of complete rotations that may occur each second.
     */
    public static final double ROTATIONS_PER_SECOND = 0.75;

    /*
     * When player is thrusting, these are the accelerations.
     */
    public static final long REVERSE_THRUST_ACCELERATION = -1;
    public static final long THRUST_ACCELERATION = 1;

    /*
     * Acceleration for when breaking.
     */
    public static final long BREAKING_THRUSTER_ACCELERATION = -1;

    /*
     * Constants for modifiers when boosting.
     */
    public static final long BOOSTING_MAX_PLAYER_SPEED = 38;
    public static final long BOOSTING_PLAYER_ACCELERATION = 3;

    /*
     * Speed limits for players.
     */
    public static final long MAX_PLAYER_SPEED = 18;
    public static final long MIN_PLAYER_SPEED = -7;

    /*
     * Boundaries for the border on engine.
     */
    public static final long BORDER_X_COORDINATE = 13350;
    public static final long BORDER_Y_COORDINATE = 8035;

    /*
     * Engine TPS.
     */
    public static final int DEFAULT_TPS = 30;
}
