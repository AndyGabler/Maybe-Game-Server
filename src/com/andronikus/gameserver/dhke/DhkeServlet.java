package com.andronikus.gameserver.dhke;

import com.gabler.server.ChatThread;
import com.gabler.server.Server;
import com.gabler.server.ServerConfiguration;
import com.gabler.udpmanager.ResourceLock;
import lombok.SneakyThrows;

import javax.net.ssl.SSLServerSocketFactory;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * Server for the creation of AES keys through Diffie-Helman Key Exchange.
 *
 * @author Andronikus
 */
public class DhkeServlet extends ServerConfiguration {

    private static final Logger LOGGER = Logger.getLogger("DhkeServlet");
    private static final int DHKE_SERVLET_PORT = 13351;

    private final BiConsumer<byte[], String> keyAndIdConsumer;
    private final Supplier<DhkePublicKey> publicKeySupplier;
    private final ResourceLock<HashMap<ChatThread, DhkeState>> clientDhkeStates;
    private volatile int keyIdCounter = 0; // Only to be used within lock of DHKE states

    /**
     * Instantiate a server for the creation of AES keys through Diffie-Helman Key Exchange.
     *
     * @param aKeyAndIdConsumer Handler for the keys, once created
     */
    public DhkeServlet(BiConsumer<byte[], String> aKeyAndIdConsumer) {
        this(
            aKeyAndIdConsumer,
            new RandomDhkePublicKeySupplier()
        );
    }

    /**
     * Instantiate a server for the creation of AES keys through Diffie-Helman Key Exchange.
     *
     * @param aKeyAndIdConsumer Handler for the keys, once created
     * @param aPublicKeySupplier Supplier for the public key
     */
    public DhkeServlet(BiConsumer<byte[], String> aKeyAndIdConsumer, Supplier<DhkePublicKey> aPublicKeySupplier) {
        publicKeySupplier = aPublicKeySupplier;
        keyAndIdConsumer = aKeyAndIdConsumer;

        clientDhkeStates = new ResourceLock<>(new HashMap<>());
    }

    /**
     * Start the DHKE servlet.
     */
    public void start() {
        final Server server = new Server(DhkeServlet::createSslServerSocket, DHKE_SERVLET_PORT);
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
    public void clientSentMessage(String message, ChatThread clientAccess) {
        final DhkePublicKey publicKey = publicKeySupplier.get();

        clientDhkeStates.performRunInLock(states -> {
            DhkeState clientDhkeState = states.get(clientAccess);

            if (clientDhkeState == null) {
                clientDhkeState = new DhkeState(publicKey);
                states.put(clientAccess, clientDhkeState);
                clientAccess.sendMessage(publicKey.getPrimeModularSpace().toString() + " " + publicKey.getPrimitiveRoot().toString(), null);
            } else {
                try {
                    clientAccess.sendMessage(clientDhkeState.takeNextInteger(new BigInteger(message)).toString(), null);
                } catch (Exception exception) {
                    clientAccess.sendMessage("E", null);
                    states.remove(clientAccess);
                }
            }

            // If we are complete, the final order of business is to assign a new ID to the key
            if (clientDhkeState.isComplete()) {
                final String keyId = "dhkeKey" + keyIdCounter;
                keyIdCounter = keyIdCounter + 1; // Only used within this lock, we are okay

                /*
                 * We can send key ID unencrypted. So long as the shared secret key is signed, we need not fear ID spoofing and
                 * a MITM with a spoofed key.
                 */
                clientAccess.sendMessage(keyId, null);
                states.remove(clientAccess);
                keyAndIdConsumer.accept(clientDhkeState.getKey(), keyId);
            }
        });
    }

    /**
     * Terminate the servlet.
     */
    public void terminate() {
        LOGGER.info("Terminating DHKE Server.");
        server.terminate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void threadTerminationAction() {
        LOGGER.info("DHKE servlet listener terminated.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void serverTerminationAction() {
        LOGGER.info("DHKE servlet terminated.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void joinAction(PrintWriter clientPrintStream) {
        LOGGER.info("New client connected to DHKE server.");
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
