package com.gabler.game.model.server;

import lombok.Data;

import java.io.Serializable;

/**
 * A player in the game.
 *
 * @author Andy Gabler
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

    private double angle;
    private double rotationalVelocity = 0;
}
