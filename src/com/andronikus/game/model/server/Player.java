package com.andronikus.game.model.server;

import com.andronikus.gameserver.engine.ScalableBalanceConstants;
import lombok.Data;

/**
 * A player in the game.
 *
 * @author Andronikus
 */
@Data
public class Player implements IMoveable {

    /**
     * Tie-back to the session with the server.
     */
    private String sessionId;

    private long x;
    private long y;

    private long xVelocity = 0;
    private long yVelocity = 0;

    private long externalXAcceleration = 0;
    private long externalYAcceleration = 0;

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

    private int turretHeat = 0;
    private int turretCoolDown = 0;
    private long laserShotTime = -10000L; // TODO if cool down is ever this long that's bad
    private Double laserShotAngle = null;

    private PlayerColor color = null;

    private boolean thrusting = false;

    private int venom = 0;

    private Long collidedPortalId = null;
    private boolean performedWarp = false;

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

    @Override
    public void setMoveableId(long id) {
        // can't set player ID
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Deprecated
    public long getMoveableId() {
        return 0L;
    }

    @Override
    public void setXPosition(long x) {
        this.x = x;
    }

    @Override
    public void setYPosition(long y) {
        this.y = y;
    }

    @Override
    public void setXTickDelta(long xDelta) {
        this.xVelocity = xDelta;
    }

    @Override
    public void setYTickDelta(long yDelta) {
        this.yVelocity = yDelta;
    }

    @Override
    public void setDirection(double angle) {
        this.angle = angle;
    }

    @Override
    public void setDirectionTickDelta(double angle) {
        // Nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String moveableTag() {
        return "PLAYER";
    }
}
