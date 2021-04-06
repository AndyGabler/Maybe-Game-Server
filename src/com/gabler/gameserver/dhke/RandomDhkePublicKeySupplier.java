package com.gabler.gameserver.dhke;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.function.Supplier;

public class RandomDhkePublicKeySupplier implements Supplier<DhkePublicKey> {

    private final Supplier<BigInteger> bigPrimeSupplier;

    public RandomDhkePublicKeySupplier() {
        this(() -> BigInteger.probablePrime(128, new SecureRandom()) );
    }

    public RandomDhkePublicKeySupplier(Supplier<BigInteger> aBigPrimeSupplier) {
        this.bigPrimeSupplier = aBigPrimeSupplier;
    }

    @Override
    public DhkePublicKey get() {
        final BigInteger space = getBigPrime();
        // TODO actually compute primitive root. For now, this is good enough
        // TODO Chose number that should be big enough where it won't be any kind of early cycle.
        final BigInteger gValue = BigInteger.valueOf(1646526151);

        return new DhkePublicKey(space, gValue);
    }

    private BigInteger getBigPrime() {
        BigInteger bigPrime = bigPrimeSupplier.get();

        if (!bigPrime.isProbablePrime(100)) {
            return getBigPrime();
        }

        return bigPrime;
    }

    /*
    private BigInteger getPrimitiveRoot(BigInteger prime) {
        final BigInteger phi = prime.subtract(BigInteger.ONE);
        https://github.com/ajithkp560/Diffie-Hellman-Key-Exchange/blob/master/src/com/blogspot/terminalcoders/PrimitiveRootGen.java
        return null;
    }

    private ArrayList<BigInteger> factor(BigInteger toFactor) {
        final ArrayList<BigInteger> primeFactors = new ArrayList<>();
        while (toFactor.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            primeFactors.add(BigInteger.TWO);
            toFactor = toFactor.divide(BigInteger.valueOf(2));
        }
        for (BigInteger index = BigInteger.valueOf(3); index.compareTo(toFactor.sqrt()) <= 0; index = index.add(BigInteger.TWO)) {
            if (toFactor.mod(index).equals(BigInteger.ZERO)) {
                primeFactors.add(index);
                toFactor = toFactor.divide(index);
            }
        }
        return primeFactors;
    }*/
}
