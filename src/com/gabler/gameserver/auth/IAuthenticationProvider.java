package com.gabler.gameserver.auth;

/**
 * Responsible for determining if a username-password principle is valid.
 *
 * @author Andy Gabler
 */
public interface IAuthenticationProvider {

    /**
     * Perform authentication.
     *
     * @param username The username
     * @param password The password
     * @return If principle is valid
     * @throws Exception Errors in creation of authentication
     */
    boolean authenticate(String username, String password) throws Exception;
}
