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
    private long nextLaserId = 0;
    private IBorder border;
}
