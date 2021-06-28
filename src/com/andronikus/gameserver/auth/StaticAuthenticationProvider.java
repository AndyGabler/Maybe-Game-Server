package com.andronikus.gameserver.auth;

import java.util.HashMap;

/**
 * Authentication provider that maintains an internal list of users and passwords.
 *
 * In theory, in a production server, a stronger and more dynamic implementation of {@link IAuthenticationProvider}
 * would be used but in a development setting, this is a viable stub for providing quick-and-dirty authentication.
 *
 * @author Andronikus
 */
public class StaticAuthenticationProvider implements IAuthenticationProvider {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean authenticate(String username, String password) {
        final HashMap<String, String> users = new HashMap<>();
        users.put("player1", "player1password");
        users.put("player2", "player2password");
        users.put("player3", "player2password");
        users.put("player4", "player2password");

        final String correctPassword = users.get(username.toLowerCase());
        return correctPassword != null && correctPassword.equals(password);
    }
}
