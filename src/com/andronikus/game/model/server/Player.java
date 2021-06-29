package com.andronikus.game.model.server;

import com.andronikus.gameserver.engine.ScalableBalanceConstants;
import lombok.Data;

import java.io.Serializable;

/**
 * A player in the game.
 *
 * @author Andronikus
 */
@Data
public class Player implements Serializable {

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

    private double angle = 0;
    private double rotationalVelocity = 0;

    private int health = ScalableBalanceConstants.PLAYER_HEALTH;
    private int shieldCount = ScalableBalanceConstants.PLAYER_SHIELD_COUNT;
    private boolean dead = false;
}
