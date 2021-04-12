package com.gabler.gameserver.dhke;

import com.gabler.gameserver.ServletCertificateUtil;

import java.math.BigInteger;

/**
 * Bootstrap a DHKE server.
 *
 * @author Andy Gabler
 */
public class DhkeServletDriverTest {

    public static void main(String[] args) {
        ServletCertificateUtil.addSslToSystemProperties();
        final DhkeServlet servlet = new DhkeServlet(DhkeServletDriverTest::printKeyBytesAndId);
        servlet.start();
    }

    private static void printKeyBytesAndId(byte[] bytes, String keyId) {
        System.out.println("DHKE Byte array: ");
        for (byte b : bytes) {
            System.out.print(String.format("0x%02X ", b));
        }

        System.out.println("\nDHKE Integer Form " + new BigInteger(bytes).toString());
        System.out.println("Key ID: " + keyId);
    }
}
