package com.gabler.gameserver.dhke;

import lombok.Getter;

import java.math.BigInteger;

/**
 * DHKE public key.
 *
 * @author Andy Gabler
 */
@Getter
public class DhkePublicKey {

    /**
     * In the equation k = g^(ab) mod p, this represents p.
     */
    private BigInteger primeModularSpace;

    /**
     * In the equation k = g^(ab) mod p, this represents g.
     */
    private BigInteger primitiveRoot;

    /**
     * Instantiate a DHKE public key.
     *
     * @param primeModularSpace Prime modular space
     * @param primitiveRoot Primitive root of the modular space
     */
    public DhkePublicKey(BigInteger primeModularSpace, BigInteger primitiveRoot) {
        this.primeModularSpace = primeModularSpace;
        this.primitiveRoot = primitiveRoot;
    }
}
