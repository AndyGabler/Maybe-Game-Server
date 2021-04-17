package com.gabler.gameserver.server;

import com.gabler.gameserver.auth.AuthenticationServlet;
import com.gabler.gameserver.auth.IAuthenticationProvider;
import com.gabler.gameserver.auth.Session;
import com.gabler.gameserver.dhke.DhkeServlet;
import com.gabler.gameserver.engine.ServerEngine;
import com.gabler.game.model.client.ClientRequest;
import com.gabler.game.model.server.GameState;
import com.gabler.udpmanager.ResourceLock;
import com.gabler.udpmanager.server.IUdpServerConfiguration;
import com.gabler.udpmanager.server.ServerClientCallback;
import com.gabler.udpmanager.server.UdpServer;
import lombok.SneakyThrows;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Game server. Manages security and framework for transmitting messages between a central game server and game client.
 *
 * @author Andy Gabler
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
        engine = new ServerEngine(this::broadcastGameState);
        byteToClientRequestTransformer = aByteToClientRequestTransformer;
        sessionManager = aSessionManager;
        authenticationServlet = new AuthenticationServlet(aSessionManager, authenticationProvider);
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
        engine.calculateInitialGameState();
        engine.start();
    }

    /**
     * Broadcast a game state to all clients.
     *
     * @param gameState The game state
     */
    public void broadcastGameState(GameState gameState) {

        try {
            final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            final ObjectOutputStream outputStream = new ObjectOutputStream(byteStream);
            outputStream.writeObject(gameState);
            byte[] payload = byteStream.toByteArray();

            server.clientBroadcast(payload);
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

        if (request.getSequenceNumber() > session.getLastRecordedSequenceNumber()) {
            engine.addInputs(request.getInputCodes(), session);
            session.setLastRecordedSequenceNumber(request.getSequenceNumber());
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
