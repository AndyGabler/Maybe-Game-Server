package com.andronikus.game.model.server;

import com.andronikus.gameserver.engine.ScalableBalanceConstants;
import lombok.Data;

/**
 * Asteroid, or rather, giant rock in space.
 *
 * @author Andronikus
 */
@Data
public class Asteroid implements IMoveable {

    private long id;
    private long x;
    private long y;
    private long xVelocity;
    private long yVelocity;
    private double angle = 0;
    private double angularVelocity;
    private int size = 1;
    private int durability = ScalableBalanceConstants.LARGE_ASTEROID_STARTING_DURABILITY;
    private int crackingTicks = 0;

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
        return size == 0 ? ScalableBalanceConstants.SMALL_ASTEROID_SIZE : ScalableBalanceConstants.LARGE_ASTEROID_WIDTH;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getBoxHeight() {
        return size == 0 ? ScalableBalanceConstants.SMALL_ASTEROID_SIZE : ScalableBalanceConstants.LARGE_ASTEROID_HEIGHT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getTilt() {
        return angle;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMoveableId(long id) {
        this.id = id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setXPosition(long x) {
        this.x = x;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setYPosition(long y) {
        this.y = y;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setXTickDelta(long xDelta) {
        this.xVelocity = xDelta;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setYTickDelta(long yDelta) {
        this.yVelocity = yDelta;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDirection(double angle) {
        this.angle = angle;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDirectionTickDelta(double angle) {
        this.angularVelocity = angle;
    }
}
