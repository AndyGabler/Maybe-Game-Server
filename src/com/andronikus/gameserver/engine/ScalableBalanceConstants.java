package com.andronikus.gameserver.engine;

/**
 * Constants that depend on one another for balancing purposes. These are used to keep the size of the game and map in
 * check.
 *
 * @author Andronikus
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
    public static final int BOOSTING_CHARGE = 200;
    public static final int BOOSTING_BURN_RATE = 5;
    public static final int BOOSTING_RECHARGE_RATE = 1;


    /*
     * Speed limits for players.
     */
    public static final long MAX_PLAYER_SPEED = 18;
    public static final long MIN_PLAYER_SPEED = -7;

    /*
     * Size of the player.
     */
    public static final int PLAYER_SIZE = 64;

    /*
     * Boundaries for the border on engine.
     */
    public static final long BORDER_X_COORDINATE = 13350;
    public static final long BORDER_Y_COORDINATE = 8035;

    /*
     * Engine TPS.
     */
    public static final int DEFAULT_TPS = 30;

    /*
     * Player health numbers.
     */
    public static final int PLAYER_HEALTH = 100;
    public static final int PLAYER_SHIELD_COUNT = 4;
    public static final int SHIELD_RECHARGE_CAP = 1000;
    public static final int SHIELD_RECHARGE_RATE = 3;

    /*
     * Throttles on how many lasers a player can fire at a time.
     */
    public static final int PLAYER_LASER_CHARGES = 8;
    public static final int PLAYER_LASER_RECHARGE_THRESHOLD = 750;
    public static final int PLAYER_LASER_RECHARGE_RATE = 26;

    /*
     * Speed of a laser as it leaves a ship.
     */
    public static final long LASER_SPEED = 59;

    /*
     * Laser sizes.
     */
    public static final int LASER_WIDTH = 48;
    public static final int LASER_HEIGHT = 32;

    /*
     * Laser damage.
     */
    public static final int LASER_DAMAGE = 23;
}
