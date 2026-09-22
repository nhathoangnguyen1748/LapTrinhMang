package com.pow.simulator.crypto;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * High-performance cryptographic utility for SHA-256 hashing and encoding.
 * Utilizes ThreadLocal MessageDigest instances to ensure thread-safety and avoid
 * allocation overhead during high-throughput mining loops.
 */
public final class HashUtil {

    private static final String SHA_256_ALGORITHM = "SHA-256";
    private static final HexFormat HEX_FORMAT = HexFormat.of();

    private static final ThreadLocal<MessageDigest> DIGEST_THREAD_LOCAL =
            ThreadLocal.withInitial(() -> {
                try {
                    return MessageDigest.getInstance(SHA_256_ALGORITHM);
                } catch (NoSuchAlgorithmException e) {
                    throw new CryptoException("Failed to initialize SHA-256 MessageDigest", e);
                }
            });

    private HashUtil() {
        // Prevent instantiation
    }

    /**
     * Obtains the thread-local SHA-256 MessageDigest, reset and ready for use.
     *
     * @return clean MessageDigest instance
     */
    public static MessageDigest getDigest() {
        MessageDigest digest = DIGEST_THREAD_LOCAL.get();
        digest.reset();
        return digest;
    }

    /**
     * Computes the raw SHA-256 hash of a byte array.
     *
     * @param input raw data bytes
     * @return 32-byte hash
     */
    public static byte[] sha256(byte[] input) {
        MessageDigest digest = getDigest();
        return digest.digest(input);
    }

    /**
     * Computes the raw SHA-256 hash of a UTF-8 string.
     *
     * @param text input string
     * @return 32-byte hash
     */
    public static byte[] sha256(String text) {
        return sha256(text.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Computes the SHA-256 hash of raw bytes and returns it as a lowercase hex string.
     *
     * @param input raw data bytes
     * @return 64-character hex string
     */
    public static String sha256Hex(byte[] input) {
        return HEX_FORMAT.formatHex(sha256(input));
    }

    /**
     * Computes the SHA-256 hash of a UTF-8 string and returns it as a lowercase hex string.
     *
     * @param text input string
     * @return 64-character hex string
     */
    public static String sha256Hex(String text) {
        return HEX_FORMAT.formatHex(sha256(text));
    }

    /**
     * Formats a raw 32-byte hash into a 64-character hexadecimal string.
     *
     * @param bytes hash bytes
     * @return lowercase hex string
     */
    public static String bytesToHex(byte[] bytes) {
        return HEX_FORMAT.formatHex(bytes);
    }

    /**
     * Parses a hex string into a raw byte array.
     *
     * @param hex string
     * @return byte array
     */
    public static byte[] hexToBytes(String hex) {
        return HEX_FORMAT.parseHex(hex);
    }

    /**
     * Counts the number of leading zero bits in a 32-byte SHA-256 hash.
     * Executed directly on bytes without string allocation for maximum mining throughput.
     *
     * @param hashBytes 32-byte hash
     * @return count of consecutive leading zero bits
     */
    public static int countLeadingZeroBits(byte[] hashBytes) {
        int zeroBits = 0;
        for (byte b : hashBytes) {
            int unsignedByte = b & 0xFF;
            if (unsignedByte == 0) {
                zeroBits += 8;
            } else {
                zeroBits += Integer.numberOfLeadingZeros(unsignedByte << 24);
                break;
            }
        }
        return zeroBits;
    }

    /**
     * Counts the number of leading zero bits in a 64-character hexadecimal hash string.
     *
     * @param hexHash 64-character hex string
     * @return count of consecutive leading zero bits
     */
    public static int countLeadingZeroBits(String hexHash) {
        byte[] bytes = hexToBytes(hexHash);
        return countLeadingZeroBits(bytes);
    }

    /**
     * Counts the number of leading '0' characters in a hex string.
     *
     * @param hexString hex string
     * @return count of consecutive leading '0' characters
     */
    public static int countLeadingZeroHex(String hexString) {
        int count = 0;
        for (int i = 0; i < hexString.length(); i++) {
            if (hexString.charAt(i) == '0') {
                count++;
            } else {
                break;
            }
        }
        return count;
    }
}
