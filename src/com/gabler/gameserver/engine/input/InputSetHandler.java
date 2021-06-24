package com.gabler.gameserver.engine.input;

import com.gabler.game.model.server.GameState;
import com.gabler.game.model.server.Player;
import com.gabler.gameserver.auth.Session;
import com.gabler.gameserver.engine.ClientInputSet;

import java.util.HashMap;
import java.util.Optional;

/**
 * Handler for a {@link ClientInputSet} that mutates a {@link GameState} based on the input.
 *
 * @author Andy Gabler
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
        handler.put("RTHRUST", new ReverseThrustInputCodeHandler());
        handler.put("LROTATE", new LeftRotateInputCodeHandler());
        handler.put("RROTATE", new RightRotateInputCodeHandler());
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

        inputSet.getInputCodes().forEach(input -> {
            if (input != null && input.length() > 0) {
                final String noParameterInput = inputCodeForFullInput(input);
                final IInputCodeHandler handler = handlerMap.get(noParameterInput);

                if (handler != null) {
                    if (!handler.requiresPlayer() || player != null) {
                        handler.handleInput(gameState, player, input, session);
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