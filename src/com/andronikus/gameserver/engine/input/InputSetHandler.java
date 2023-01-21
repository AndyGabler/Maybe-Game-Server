package com.andronikus.gameserver.engine.input;

import com.andronikus.game.model.server.GameState;
import com.andronikus.game.model.server.Player;
import com.andronikus.gameserver.auth.Session;
import com.andronikus.gameserver.engine.ClientInput;
import com.andronikus.gameserver.engine.ClientInputSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Handler for a {@link ClientInputSet} that mutates a {@link GameState} based on the input.
 *
 * @author Andronikus
 */
public class InputSetHandler {

    // Note, that a hash map can be read concurrently, so long as it won't be written to.
    private final HashMap<String, IInputCodeHandler> handlerMap;

    /**
     * Instantiate handler for a {@link ClientInputSet}.
     */
    public InputSetHandler() {
        this(defaultHandlerMap());
    }

    /**
     * Instantiate handler for a {@link ClientInputSet}.
     *
     * @param aHandlerMap Map of input codes to the {@link IInputCodeHandler} implementation that will handle it.
     */
    public InputSetHandler(HashMap<String, IInputCodeHandler> aHandlerMap) {
        handlerMap = aHandlerMap;
    }

    /**
     * Default map of input codes to their handler.
     *
     * @return The map of code to handler
     */
    private static HashMap<String, IInputCodeHandler> defaultHandlerMap() {
        final HashMap<String, IInputCodeHandler> handler = new HashMap<>();
        handler.put("JOINGAME", new JoinGameInputCodeHandler());
        handler.put("THRUST", new ThrustInputCodeHandler());
        handler.put("THRUSTEND", new ThrustEndInputCodeHandler());
        handler.put("RTHRUST", new ReverseThrustInputCodeHandler());
        handler.put("LROTATE", new LeftRotateInputCodeHandler());
        handler.put("RROTATE", new RightRotateInputCodeHandler());
        handler.put("BOOST", new BoostInputCodeHandler());
        handler.put("BOOSTEND", new BoostInputCodeHandler());
        handler.put("BREAK", new BreakInputCodeHandler());
        handler.put("SHOOT", new ShootInputCodeHandler());
        return handler;
    }

    /**
     * Mutate a given {@link GameState} based upon a given {@link ClientInputSet}.
     *
     * @param inputSet The set of inputs to handle
     * @param gameState The game state to mutate based on the inputs
     */
    public void putInputSetOnGameState(ClientInputSet inputSet, GameState gameState) {
        final Session session = inputSet.getSession();
        final Player player = playerForSession(gameState, session.getId());

        final ArrayList<String> allInputCodes = inputSet.getInputs().stream().map(ClientInput::getCode).collect(Collectors.toCollection(ArrayList::new));
        inputSet.getInputs().forEach(input -> {
            if (input != null && input.getCode().length() > 0) {
                final String noParameterInput = inputCodeForFullInput(input.getCode());
                final IInputCodeHandler handler = handlerMap.get(noParameterInput);

                if (handler != null) {
                    if (!handler.requiresPlayer() ||
                        (player != null && (!handler.playerMustBeAlive() || !player.isDead()))) {
                        handler.handleInput(gameState, player, input, allInputCodes, session);
                    }
                }
            }
        });
    }

    /**
     * Get a {@link Player} from session information.
     *
     * @param gameState The {@link GameState} to find players on
     * @param sessionId The session identifier that a player will be tagged with
     * @return The player, null if it does not exist
     */
    private Player playerForSession(GameState gameState, String sessionId) {
        final Optional<Player> optionalPlayer = gameState.getPlayers().stream().filter(
            player -> player.getSessionId().equalsIgnoreCase(sessionId)
        ).findFirst();

        Player player = null;
        if (optionalPlayer.isPresent()) {
            player = optionalPlayer.get();
        }
        return player;
    }

    /**
     * Get the input code for a full input. That is, the input keyword, without manual parameters denoted by a space.
     *
     * @param fullInputCode The entire input code, parameters and all
     * @return The input code without parameters
     */
    private static String inputCodeForFullInput(String fullInputCode) {
        final int indexOfSpace = fullInputCode.indexOf(" ");
        if (indexOfSpace == -1) {
            return fullInputCode;
        }

        return fullInputCode.substring(0, indexOfSpace);
    }
}
