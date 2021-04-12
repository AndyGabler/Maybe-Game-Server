package com.gabler.gameserver.auth;

import com.gabler.server.ChatThread;
import com.gabler.server.Server;
import com.gabler.server.ServerConfiguration;
import com.gabler.udpmanager.ResourceLock;
import lombok.SneakyThrows;

import javax.net.ssl.SSLServerSocketFactory;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Server for the creation of {@link Session}s via authentication.
 *
 * @author Andy Gabler
 */
public class AuthenticationServlet extends ServerConfiguration {

    private static final Logger LOGGER = Logger.getLogger("AuthenticationServlet");
    private static final int AUTH_SERVLET_PORT = 13352;

    private final ResourceLock<HashMap<String, Session>> sessionManager;
    private final IAuthenticationProvider authenticationProvider;

    /**
     * Instantiate a server for the creation of {@link Session}s via authentication.
     *
     * @param aSessionManager The session manager
     * @param anAuthenticationProvider Object responsible for authentication
     */
    public AuthenticationServlet(
        ResourceLock<HashMap<String, Session>> aSessionManager,
        IAuthenticationProvider anAuthenticationProvider
    ) {
        sessionManager = aSessionManager;
        authenticationProvider = anAuthenticationProvider;
    }

    /**
     * Start the authentication servlet.
     */
    public void start() {
        final Server server = new Server(AuthenticationServlet::createSslServerSocket, AUTH_SERVLET_PORT);
        server.setOperations(this);
        server.start();
    }

    /**
     * Create SSL server socket.
     *
     * @param portNumber The port number
     * @return The socket
     */
    @SneakyThrows
    private static ServerSocket createSslServerSocket(int portNumber) {
        return SSLServerSocketFactory.getDefault().createServerSocket(portNumber);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clientSentMessage(String principle, ChatThread clientAccess) {
        final ArrayList<String> usernamePasswordPair = getUsernamePasswordCombination(principle);

        if (usernamePasswordPair != null) {
            final String username = usernamePasswordPair.get(0);
            final String password = usernamePasswordPair.get(1);

            boolean authenticated = false;
            try {
                authenticated = authenticationProvider.authenticate(username, password);
            } catch (Exception exception) {
                LOGGER.log(Level.SEVERE, "Authentication failed due to technical error.", exception);
            }

            if (authenticated) {
                final String sessionSecret = generateSession();
                clientAccess.sendMessage("SESSION " + sessionSecret, null);
            } else {
                clientAccess.sendMessage("NOSESSION", null);
            }
        }
        clientAccess.terminate();
    }

    /**
     * Split principle in form of "username password" into separate values.
     *
     * @param principle The values
     * @return Values separated out
     */
    private static ArrayList<String> getUsernamePasswordCombination(String principle) {
        final int spaceIndex = principle.indexOf(" ");

        if (spaceIndex == -1 || spaceIndex == principle.length() - 1 || spaceIndex == 0) {
            return null;
        }

        final String username = principle.substring(0, spaceIndex);
        final String password = principle.substring(spaceIndex +  1);

        final ArrayList<String> pair = new ArrayList<>();
        pair.add(username);
        pair.add(password);
        return pair;
    }

    /**
     * Upon successful authentication, generate a new session and return session secret reference.
     *
     * @return Session secret reference
     */
    private String generateSession() {
        final Session newSession = new Session();

        boolean uniqueSessionSecretFound = false;
        String newSecret = null;
        while (!uniqueSessionSecretFound) {
            final String secret = new BigInteger(128, new SecureRandom()).toString();
            uniqueSessionSecretFound = sessionManager.performRunInLock(sessions -> sessions.get(secret) == null);

            if (uniqueSessionSecretFound) {
                sessionManager.performRunInLock(sessions -> {
                    newSession.setSecret(secret);
                    sessions.put(secret, newSession);
                });
                newSecret = secret;
            }
        }

        return newSecret;
    }

    /**
     * Terminate the servlet.
     */
    public void terminate() {
        LOGGER.info("Terminating Authentication Server.");
        server.terminate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void threadTerminationAction() {
        LOGGER.info("Authentication servlet listener terminated.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void serverTerminationAction() {
        LOGGER.info("Authentication servlet terminated.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void joinAction(PrintWriter clientPrintStream) {
        LOGGER.info("New client connected to Authentication server.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canTerminate() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String handleIncoming(String message, ChatThread chatThread, ChatThread chatThread1) {
        return message;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canCallMethod() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean sendsAck(ChatThread chatThread) {
        return false;
    }
}
