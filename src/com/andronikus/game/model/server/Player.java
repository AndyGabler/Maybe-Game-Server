package com.andronikus.game.model.server;

import com.andronikus.gameserver.engine.ScalableBalanceConstants;
import lombok.Data;

/**
 * A player in the game.
 *
 * @author Andronikus
 */
@Data
public class Player implements ICollideable {

    /**
     * Tie-back to the session with the server.
     */
    private String sessionId;

    private long x;
    private long y;

    private long XVelocity = 0;
    private long yVelocity = 0;

    private long speed = 0;
    private long acceleration = 0;

    private boolean boosting = false;
    private int boostingCharge = ScalableBalanceConstants.BOOSTING_CHARGE;
    private int boostingRecharge = ScalableBalanceConstants.BOOSTING_CHARGE;

    private double angle = 0;
    private double rotationalVelocity = 0;

    private int health = ScalableBalanceConstants.PLAYER_HEALTH;
    private boolean dead = false;

    private int shieldCount = ScalableBalanceConstants.PLAYER_SHIELD_COUNT;
    private int shieldRecharge = 0;
    private boolean shieldLostThisTick = false;

    private int laserCharges = ScalableBalanceConstants.PLAYER_LASER_CHARGES;
    private int laserRecharge = 0;

    private PlayerColor color = null;

    private boolean thrusting = false;

    private int venom = 0;

    /**
     * {@inheritDoc}
     */
    @Override
    public long getBoxX() {
        return x;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getBoxY() {
        return y;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getBoxWidth() {
        return ScalableBalanceConstants.PLAYER_SIZE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getBoxHeight() {
        return ScalableBalanceConstants.PLAYER_SIZE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getTilt() {
        return angle;
    }
}
