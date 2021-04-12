package com.gabler.gameserver.app;

import com.gabler.gameserver.ServletCertificateUtil;
import com.gabler.gameserver.auth.IAuthenticationProvider;
import com.gabler.gameserver.auth.MySqlAuthenticationProvider;
import com.gabler.gameserver.auth.Session;
import com.gabler.gameserver.auth.StaticAuthenticationProvider;
import com.gabler.gameserver.server.GameServer;
import com.gabler.udpmanager.ResourceLock;

import java.util.HashMap;

/**
 * Application entry point class.
 *
 * @author Andy Gabler
 */
public class AppStart {

    /**
     * Bootstrap the server with all essential components and run.
     *
     * @param args The arguments
     */
    public static void main(String[] args) {
        final ApplicationOptions applicationOptions = new ApplicationOptions(args);
        final String authScheme = applicationOptions.getOption("authMethod", true, 1).get(0);
        applicationOptions.checkUnusedOptions();

        IAuthenticationProvider authenticationProvider = null;
        if (authScheme.equalsIgnoreCase("static")) {
            authenticationProvider = new StaticAuthenticationProvider();
        } else if (authScheme.equalsIgnoreCase("mysql")) {
            authenticationProvider = new MySqlAuthenticationProvider();
        }

        if (authenticationProvider == null) {
            throw new IllegalArgumentException("No authentication provider given.");
        }

        ServletCertificateUtil.addSslToSystemProperties();
        final ResourceLock<HashMap<String, Session>> sessionManager = new ResourceLock<>(new HashMap<>());
        final GameServer server = new GameServer(sessionManager, authenticationProvider);
        server.start();
    }
}
