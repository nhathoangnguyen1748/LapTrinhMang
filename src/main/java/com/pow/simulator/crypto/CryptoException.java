package com.pow.simulator.crypto;

/**
 * Custom runtime exception thrown when cryptographic operations fail.
 */
public class CryptoException extends RuntimeException {
    public CryptoException(String message) {
        super(message);
    }

    public CryptoException(String message, Throwable cause) {
        super(message, cause);
    }
}
