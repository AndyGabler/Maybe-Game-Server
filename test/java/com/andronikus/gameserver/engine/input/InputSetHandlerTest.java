package com.andronikus.gameserver.engine.input;

import com.andronikus.gameserver.auth.Session;
import com.andronikus.gameserver.engine.ClientInputSet;
import com.andronikus.game.model.server.GameState;
import com.andronikus.game.model.server.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Test of {@link InputSetHandler}.
 *
 * @author Andronikus
 */
public class InputSetHandlerTest {

    private InputSetHandler objectUnderTest;
    private GameState gameState;
    private Player player;

    private Session sessionWithPlayer;
    private Session sessionWithoutPlayer;

    private IInputCodeHandler handler1;
    private IInputCodeHandler handler2;

    @BeforeEach
    public void setup() {
        player = new Player();
        player.setSessionId("SESSION-ID-1");

        gameState = new GameState();
        gameState.getPlayers().add(player);

        sessionWithPlayer = new Session();
        sessionWithPlayer.setId("SESSION-ID-1");
        sessionWithoutPlayer = new Session();
        sessionWithoutPlayer.setId("SESSION-ID-2");

        handler1 = Mockito.mock(IInputCodeHandler.class);
        handler2 = Mockito.mock(IInputCodeHandler.class);

        final HashMap<String, IInputCodeHandler> handlerMap = new HashMap<>();
        handlerMap.put("INPUTCODE1", handler1);
        handlerMap.put("ANOTHERINPUTCODE", handler2);
        objectUnderTest = new InputSetHandler(handlerMap);
    }

    @Test
    public void testNoPlayerSession() {
        final ClientInputSet inputSet = createSingleInputInputSet("INPUTCODE1", sessionWithoutPlayer);
        objectUnderTest.putInputSetOnGameState(inputSet, gameState);

        final ArrayList<String> inputCodesCopy = new ArrayList<>();
        inputCodesCopy.add("INPUTCODE1");

        Mockito.verify(handler1, Mockito.times(1)).handleInput(gameState, null, "INPUTCODE1", inputCodesCopy, sessionWithoutPlayer);
        Mockito.verify(handler2, Mockito.times(0)).handleInput(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    public void testSessionWithPlayer() {
        final ClientInputSet inputSet = createSingleInputInputSet("INPUTCODE1", sessionWithPlayer);
        objectUnderTest.putInputSetOnGameState(inputSet, gameState);

        final ArrayList<String> inputCodesCopy = new ArrayList<>();
        inputCodesCopy.add("INPUTCODE1");

        Mockito.verify(handler1, Mockito.times(1)).handleInput(gameState, player, "INPUTCODE1", inputCodesCopy, sessionWithPlayer);
        Mockito.verify(handler2, Mockito.times(0)).handleInput(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    public void testSingleWordInput() {
        final ClientInputSet inputSet = createSingleInputInputSet("ANOTHERINPUTCODE", sessionWithPlayer);
        objectUnderTest.putInputSetOnGameState(inputSet, gameState);

        final ArrayList<String> inputCodesCopy = new ArrayList<>();
        inputCodesCopy.add("ANOTHERINPUTCODE");

        Mockito.verify(handler2, Mockito.times(1)).handleInput(gameState, player, "ANOTHERINPUTCODE", inputCodesCopy, sessionWithPlayer);
        Mockito.verify(handler1, Mockito.times(0)).handleInput(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    public void testMultipleWordInput() {
        final ClientInputSet inputSet = createSingleInputInputSet("ANOTHERINPUTCODE 43", sessionWithPlayer);
        objectUnderTest.putInputSetOnGameState(inputSet, gameState);

        final ArrayList<String> inputCodesCopy = new ArrayList<>();
        inputCodesCopy.add("ANOTHERINPUTCODE 43");

        Mockito.verify(handler2, Mockito.times(1)).handleInput(gameState, player, "ANOTHERINPUTCODE 43", inputCodesCopy, sessionWithPlayer);
        Mockito.verify(handler1, Mockito.times(0)).handleInput(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    public void testInvalidCommand() {
        final ClientInputSet inputSet = createSingleInputInputSet("INVALID-COMMAND", sessionWithPlayer);
        objectUnderTest.putInputSetOnGameState(inputSet, gameState);

        Mockito.verify(handler1, Mockito.times(0)).handleInput(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(handler2, Mockito.times(0)).handleInput(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
    }

    /**
     * Test creation of an input set with a single input code.
     *
     * @param input Input code
     * @param session Session the input set came from
     * @return The input set to use in testing
     */
    private ClientInputSet createSingleInputInputSet(String input, Session session) {
        final ClientInputSet inputSet = new ClientInputSet();
        inputSet.setInputCodes(new ArrayList<>());
        inputSet.setSession(session);
        inputSet.getInputCodes().add(input);
        return inputSet;
    }
}
