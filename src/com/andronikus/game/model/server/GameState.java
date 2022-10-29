package com.andronikus.game.model.server;

import com.andronikus.game.model.server.debug.ServerDebugSettings;
import com.andronikus.game.model.server.input.InputAcknowledgement;
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
    private ArrayList<IMoveable> collideables = new ArrayList<>();
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
    private ServerDebugSettings debugSettings = null;

    // Control flags
    private boolean tickEnabled = true;
    private boolean collisionsEnabled = true;
    private boolean movementEnabled = true;
    private boolean spawningEnabled = true;

    // Input related
    private ArrayList<InputAcknowledgement> inputAcknowledgements = new ArrayList<>();
}
