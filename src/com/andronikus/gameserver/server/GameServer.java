package com.andronikus.gameserver.server;

import com.andronikus.gameserver.auth.AuthenticationServlet;
import com.andronikus.gameserver.auth.IAuthenticationProvider;
import com.andronikus.gameserver.auth.Session;
import com.andronikus.gameserver.dhke.DhkeServlet;
import com.andronikus.gameserver.engine.GameStateBytesWrapper;
import com.andronikus.gameserver.engine.ServerEngine;
import com.andronikus.game.model.client.ClientRequest;
import com.andronikus.game.model.client.InputRequest;
import com.andronikus.gameserver.engine.command.CommandEngineTransferQueue;
import com.gabler.udpmanager.ResourceLock;
import com.gabler.udpmanager.server.IUdpServerConfiguration;
import com.gabler.udpmanager.server.ServerClientCallback;
import com.gabler.udpmanager.server.UdpServer;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Game server. Manages security and framework for transmitting messages between a central game server and game client.
 *
 * @author Andronikus
 */
public class GameServer implements IUdpServerConfiguration {

    private static final Logger LOGGER = Logger.getLogger("GameServer");

    private static final int GAME_SERVER_PORT = 13350;
    private static final int GAME_SERVER_THREAD_POOL_SIZE = 1;

    private final ResourceLock<HashMap<String, Session>> sessionManager;
    private final Function<byte[], ClientRequest> byteToClientRequestTransformer;
    private final DhkeServlet keyServlet;
    private final UdpServer server;
    private final ServerEngine engine;
    private final AuthenticationServlet authenticationServlet;

    /**
     * Initialize a game server.
     *
     * @param aSessionManager Lock and a map of session secrets to sessions
     * @param authenticationProvider Object used for authentication
     */
    public GameServer(ResourceLock<HashMap<String, Session>> aSessionManager, IAuthenticationProvider authenticationProvider) {
        this(new BytesToObjectTransformer<>(), aSessionManager, authenticationProvider);
    }

    /**
     * Initialize a game server.
     *
     * @param aByteToClientRequestTransformer Transformer for turning a bytes message to a {@link ClientRequest}
     * @param aSessionManager Lock and a map of session secrets to sessions
     * @param authenticationProvider Object used for authentication
     */
    @SneakyThrows
    public GameServer(
        Function<byte[], ClientRequest> aByteToClientRequestTransformer,
        ResourceLock<HashMap<String, Session>> aSessionManager,
        IAuthenticationProvider authenticationProvider
    ) {
        server = new UdpServer(GAME_SERVER_PORT, GAME_SERVER_THREAD_POOL_SIZE);
        server.setConfiguration(this);
        keyServlet = new DhkeServlet(this::addClientKeyToServer);
        engine = new ServerEngine(this::broadcastGameStateBytes);
        byteToClientRequestTransformer = aByteToClientRequestTransformer;
        sessionManager = aSessionManager;
        authenticationServlet = new AuthenticationServlet(aSessionManager, authenticationProvider);
        engine.calculateInitialGameState();
    }

    /**
     * Add client key to the server.
     *
     * @param key The 128-bit key
     * @param keyId The key ID
     */
    private synchronized void addClientKeyToServer(byte[] key, String keyId) {
        server.addClientKey(keyId, key);
    }

    /**
     * Start the game server.
     */
    public void start() {
        server.start();
        keyServlet.start();
        authenticationServlet.start();
        engine.start();
    }

    /**
     * Broadcast a game state to all clients.
     *
     * @param bytesWrapper The wrapper for the game state in bytes
     */
    public void broadcastGameStateBytes(GameStateBytesWrapper bytesWrapper) {

        try {
            server.clientBroadcast(bytesWrapper.getPayload());
        } catch (Exception exception) {
            LOGGER.log(Level.SEVERE, "Server gamestate broadcast failed.", exception);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleBytesMessage(byte[] bytes, ServerClientCallback serverClientCallback) {
        final String logHeader = "[" + serverClientCallback.getAddress().toString() + "(" + serverClientCallback.getPortNumber() + ")] ";

        ClientRequest request;
        try {
            request = byteToClientRequestTransformer.apply(bytes);
        } catch (Exception exception) {
            /*
             * This probably did not come from a client designed for this server.
             */
            LOGGER.log(Level.SEVERE, logHeader + "Could not serialize client bytes message to a ClientRequest.", exception);
            return;
        }

        final Session session = sessionManager.performRunInLock((Function<HashMap<String, Session>, Session>) sessions -> sessions.get(request.getSessionToken()));
        if (session == null) {
            LOGGER.severe(logHeader + "Invalid session secret received.");
            return;
        } else if (session.getConnectionInfo() == null) {
            LOGGER.log(Level.INFO, logHeader + "Binding session to client.");
            session.setConnectionInfo(serverClientCallback);
        } else if (!session.getConnectionInfo().getAddress().equals(serverClientCallback.getAddress())) {
            // If a change of address occurs, that's probably sus. If this ever happens, need to know so we can improve security.
            LOGGER.severe(logHeader + "Potential session jacking. Session ID belongs to " + session.getConnectionInfo().getAddress().toString());

            // Don't give feedback if this occurs
            return;
        }

        if (
            request.getSequenceNumber() > session.getLastRecordedSequenceNumber() &&
            (request.getInputCode0() != null ||
             request.getInputCode1() != null ||
             request.getInputCode2() != null ||
             request.getInputCode3() != null ||
             request.getInputCode4() != null ||
             (request.getClientCommands() != null && !request.getClientCommands().isEmpty()))
        ) {
            final ArrayList<InputRequest> inputs = new ArrayList<>();
            inputs.add(request.getInputCode0());
            inputs.add(request.getInputCode1());
            inputs.add(request.getInputCode2());
            inputs.add(request.getInputCode3());
            inputs.add(request.getInputCode4());
            engine.addInputs(
                inputs
                    .stream()
                    .filter(input -> input != null && input.getInputCode() != null)
                    .collect(Collectors.toList()),
                session
            );
            session.setLastRecordedSequenceNumber(request.getSequenceNumber());

            if (engine.isDebugMode()) {
                // TODO throttle for DDoS
                final CommandEngineTransferQueue transferQueue = engine.getCommandTransferQueue();
                request.getClientCommands().forEach(clientCommand -> transferQueue.takeNewClientCommand(clientCommand, session));
                request.getCommandsToRemove().forEach(clientCommand -> transferQueue.retireClientCommand(clientCommand, session));
            } else if (!request.getClientCommands().isEmpty()) {
                LOGGER.severe("Server is not in debug mode but received a command from session " + session.getId() + " for user " + session.getUsername() + ".");
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleStringMessage(String message, ServerClientCallback serverClientCallback) {
        final String logHeader = "[" + serverClientCallback.getAddress().toString() + "(" + serverClientCallback.getPortNumber() + ")] ";
        if (message.equals("CONN")) {
            LOGGER.info(logHeader + "New client connected.");
        } else {
            LOGGER.warning(logHeader + "String message received. This is abnormal.");
        }
    }

    /**
     * Set whether the server is in debug mode.
     *
     * @param debugMode If server is in debug mode
     */
    public void setDebugMode(boolean debugMode) {
        engine.setDebugMode(debugMode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startAction() {
        LOGGER.info("Game Server started.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void terminationAction() {
        LOGGER.info("Game Server terminated. Killing the engine and servlets.");
        engine.kill();
        authenticationServlet.terminate();
        keyServlet.terminate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void pauseAction() {
        LOGGER.info("Game Server paused. Paused engine.");
        engine.pauseEngine();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resumeAction() {
        LOGGER.info("Game Server resumed. Resuming engine.");
        engine.resumeEngine();
    }
}
