package com.andronikus.gameserver.dhke;

import lombok.Getter;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.function.Supplier;

/**
 * State of a DHKE transaction.
 *
 * @author Andronikus
 */
public class DhkeState {

    private final Supplier<BigInteger> privateKeySupplier;
    private DhkePublicKey publicKey;

    /**
     * The key resulting from the DHKE exchange.
     */
    @Getter
    private byte[] key;

    /**
     * Initialize new state of a DHKE transaction.
     *
     * @param publicKey The public key
     */
    public DhkeState(DhkePublicKey publicKey) {
        this(() -> new BigInteger(128, new SecureRandom()), publicKey);
    }

    /**
     *Initialize new state of a DHKE transaction.
     *
     * @param aPrivateKeySupplier Supplier of the private key
     * @param publicKey The public key
     */
    public DhkeState(Supplier<BigInteger> aPrivateKeySupplier, DhkePublicKey publicKey) {
        privateKeySupplier = aPrivateKeySupplier;
        this.publicKey = publicKey;
    }

    /**
     * Take and accept the next integer in the DHKE transaction.
     *
     * @param nextInteger The next integer to take
     * @return The next integer to go across the wire
     */
    public BigInteger takeNextInteger(BigInteger nextInteger) {
        final BigInteger privateKey = privateKeySupplier.get();

        final BigInteger sharedSecret = nextInteger.modPow(privateKey, publicKey.getPrimeModularSpace());
        final byte[] secretBytes = sharedSecret.toByteArray();

        key = new byte[16];
        for (int index = 0; index < key.length; index++) {
            // Ensure if key is longer than 16 bytes, we actually record 16, but 16 if it's unsigned
            key[index] = secretBytes[index + (secretBytes.length - 16)];
        }

        // Make sure the client can have all they need to make the same key
        return publicKey.getPrimitiveRoot().modPow(privateKey, publicKey.getPrimeModularSpace());
    }

    /**
     * Is the DHKE complete with a key ready to go?
     *
     * @return If the key is ready to be used
     */
    public boolean isComplete() {
        return key != null;
    }
}
