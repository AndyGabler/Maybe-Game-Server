package com.andronikus.game.model.server;

import com.andronikus.gameserver.engine.ScalableBalanceConstants;
import lombok.Data;

/**
 * Snake that floats around in space and bites those who are not careful.
 *
 * @author Andronikus
 */
@Data
public class Snake implements IMoveable {

    private long id;
    private long x;
    private long y;
    private long xVelocity;
    private long yVelocity;
    private double angle;
    private int health = ScalableBalanceConstants.SNAKE_HEALTH;
    private Player target;
    private boolean chasing = false;

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
        return ScalableBalanceConstants.SNAKE_WIDTH;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getBoxHeight() {
        return ScalableBalanceConstants.SNAKE_HEIGHT;
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
    public long getMoveableId() {
        return id;
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
    public void setDirectionTickDelta(double angle) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public String moveableTag() {
        return "PORTAL";
    }
}
