package com.gabler.gameserver.dhke;

import lombok.SneakyThrows;

import java.io.File;
import java.net.URL;
import java.util.Scanner;

/**
 * Setup configuration for a DHKE socket.
 *
 * @author Andy Gabler
 */
public class DhkeServletCertificateUtil {

    /**
     * Add SSL system properties.
     */
    @SneakyThrows
    public static void addSslToSystemProperties() {
        final URL keyStoreUrl = DhkeServletCertificateUtil.class.getClassLoader().getResource("gamesslstore-server.store");
        final String sslStorePath = keyStoreUrl.getFile();

        final URL sslServerPasswordResource = DhkeServletCertificateUtil.class.getClassLoader().getResource("ssl-server-password.txt");
        final String sslServerPassword = new Scanner(new File(sslServerPasswordResource.getFile())).nextLine();

        System.setProperty("javax.net.ssl.keyStore", sslStorePath);
        System.setProperty("javax.net.ssl.keyStorePassword", sslServerPassword);

        final URL keyTrustUrl = DhkeServletCertificateUtil.class.getClassLoader().getResource("gamesslstore-client.store");
        final String sslTrustPath = keyTrustUrl.getFile();

        System.setProperty("javax.net.ssl.trustStore", sslTrustPath);

        /*
         * Without this, handshakes fail. A System.setProperty("javax.net.debug", "all"); reveals that the error
         * produced is (HANDSHAKE_FAILURE): No available authentication scheme. This is because a DSA certificate is
         * not supported in TLS 1.3. Certificate supported in TLS 1.2.
         *
         * https://stackoverflow.com/questions/55854904/javax-net-ssl-sslhandshakeexception-no-available-authentication-scheme
         * https://bugs.openjdk.java.net/browse/JDK-8211426?focusedCommentId=14218233&page=com.atlassian.jira.plugin.system.issuetabpanels%3Acomment-tabpanel#comment-14218233
         *
         * TODO Upgrade Certificate instead of downgrading TLS
         */
        System.setProperty("jdk.tls.server.protocols", "TLSv1.2");
    }
}
