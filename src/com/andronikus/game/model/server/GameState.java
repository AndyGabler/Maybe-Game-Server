package com.andronikus.game.model.server;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * State of the game.
 *
 * @author Andronikus
 */
@Data
public class GameState implements Serializable {
    private long version = 0;
    private ArrayList<ICollideable> collideables = new ArrayList<>();
    private ArrayList<Player> players = new ArrayList<>();
    private ArrayList<Laser> lasers = new ArrayList<>();
    private ArrayList<Asteroid> asteroids = new ArrayList<>();
    private ArrayList<Snake> snakes = new ArrayList<>();
    private ArrayList<MicroBlackHole> blackHoles = new ArrayList<>();
    private ArrayList<Portal> portals = new ArrayList<>();
    private long nextLaserId = 0;
    private long nextSpawnId = 0;
    private IBorder border;
    private boolean serverDebugMode = false;
    private ArrayList<CommandAcknowledgement> commandAcknowledgements = new ArrayList<>();

    // Control flags
    private boolean tickEnabled = true;
    private boolean collisionsEnabled = true;
    private boolean movementEnabled = true;
    private boolean spawningEnabled = true;
}
