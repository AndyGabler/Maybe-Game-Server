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
    public static final int BOOSTING_BURN_RATE = 3;
    public static final int BOOSTING_RECHARGE_RATE = 5;


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
     * Engine rates.
     */
    public static final int DEFAULT_TPS = 30;
    public static final int BROADCAST_RATE = 30;

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
    public static final int PLAYER_LASER_CHARGES = 100;
    public static final int PLAYER_LASER_SHOT_COOL_DOWN_TICKS = 7;
    public static final int PLAYER_LASER_RECHARGE_THRESHOLD = 750;
    public static final int PLAYER_LASER_RECHARGE_RATE = 26;

    /*
     * Speed of a laser as it leaves a ship.
     */
    public static final long LASER_SPEED = 44;

    /*
     * Laser sizes.
     */
    public static final int LASER_WIDTH = 16;
    public static final int LASER_HEIGHT = 50;

    /*
     * Laser damage.
     */
    public static final int LASER_DAMAGE = 23;

    /*
     * Asteroid sizes.
     */
    public static final int SMALL_ASTEROID_SIZE = 64;
    public static final int LARGE_ASTEROID_WIDTH = 96;
    public static final int LARGE_ASTEROID_HEIGHT = 192;

    /*
     * Speed of asteroids.
     */
    public static final long ASTEROID_STARTING_SPEED_MINIMUM = 2;
    public static final long ASTEROID_STARTING_SPEED_MAXIMUM = 25;

    /*
     * Chance an asteroid will spawn on each tick.
     */
    public static final double ASTEROID_SPAWN_CHANCE = 0.005;

    /*
     * Asteroid rotational constants.
     */
    public static final double ASTEROID_ROTATIONAL_VELOCITY_MINIMUM = 0;
    public static final double ASTEROID_ROTATIONAL_VELOCITY_MAXIMUM = Math.PI / 32;

    /*
     * Asteroid durability trackers.
     */
    public static final int LARGE_ASTEROID_STARTING_DURABILITY = 30;
    public static final int SMALL_ASTEROID_STARTING_DURABILITY = 12;
    public static final int ASTEROID_LASER_DURABILITY_DAMAGE = 11;
    public static final int ASTEROID_CRACKING_TICKS = 12;

    /*
     * Asteroid split distances.
     */
    public static final int ASTEROID_SPLIT_DISTANCE_MAX = 175;
    public static final int ASTEROID_SPLIT_DISTANCE_MIN = 32;

    /*
     * Asteroid damages.
     */
    public static final int ASTEROID_DAMAGE_SMALL = 10;
    public static final int ASTEROID_DAMAGE_LARGE = 42;

    /*
     * Health of a snake.
     */
    public static final int SNAKE_HEALTH = 50;

    public static final int SNAKE_CHASING_SPEED = 19;

    /*
     * Snake chase distance
     */
    public static final int SNAKE_CHASE_DISTANCE = 500;

    /*
     * Snake sizes.
     */
    public static final int SNAKE_WIDTH = 16;
    public static final int SNAKE_HEIGHT = 64;

    /*
     * Snake venom stats.
     */
    public static final int SNAKE_VENOM_TICKS_BETWEEN_DAMAGE = 7;
    public static final int SNAKE_VENOM_TICKS = 49;
    public static final int SNAKE_VENOM_DAMAGE = 1;

    /*
     * Size of black holes.
     */
    public static final int BLACK_HOLE_SIZE = 128;

    /*
     * Values defining the effectiveness of black holes.
     */
    public static final double BLACK_HOLE_ACTIVE_RANGE = 450.0;
    public static final double BLACK_HOLE_GRAVITY = 7.3549;

    /*
     * Size of portals.
     */
    public static final int PORTAL_SIZE = 64;

    /*
     * Probability of a portal spawning.
     */
    public static final double PORTAL_SPAWN_CHANCE = 1.0 / 275.0;

    public static final int PORTAL_COLLISION_TICKS_BEFORE_TRANSPORT = 18;
    public static final int PORTAL_TRANSPORT_TICKS_BEFORE_RELEASE = 18;

    /*
     * Chance that a portal/black hole despawn on each tick. Average of 7s of sticking around.
     */
    public static final double PORTAL_BLACK_HOLE_DESPAWN_CHANCE = 1.0 / 210.0;

    /*
     * Life span of a collision flag.
     */
    public static final long COLLISION_FLAG_LIFE_SPAN_TICKS = 5;

    /*
     * Input Acknowledgement properties
     */
    public static final long INPUT_ACKNOWLEDGEMENT_LIFE_SPAN_TICKS = ScalableBalanceConstants.DEFAULT_TPS * 2;
    // With 6 players at 5 inputs per tick, this would cover one whole tick of all inputting.
    public static final int INPUT_ACKNOWLEDGEMENT_GAMESTATE_LIMIT = 30;
}
