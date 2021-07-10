package com.andronikus.game.model.server;

import com.andronikus.gameserver.engine.ScalableBalanceConstants;
import lombok.Data;

/**
 * A laser projectile.
 *
 * @author Andronikus
 */
@Data
public class Laser implements ICollideable {

    private long x;
    private long y;
    private long xVelocity;
    private long yVelocity;
    private String loyalty;
    private long id;
    private double angle;

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
        return ScalableBalanceConstants.LASER_WIDTH;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getBoxHeight() {
        return ScalableBalanceConstants.LASER_HEIGHT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getTilt() {
        return angle;
    }
}
