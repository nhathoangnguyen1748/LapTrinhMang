package com.pow.simulator.consensus;

import com.pow.simulator.crypto.HashUtil;

import java.math.BigInteger;

/**
 * Proof-of-Work target verification service.
 * Supports both bitwise leading-zero checks and Bitcoin-style BigInteger numerical target comparisons.
 */
public final class TargetMatcher {

    private static final BigInteger MAX_TARGET = BigInteger.ONE.shiftLeft(256).subtract(BigInteger.ONE);

    private TargetMatcher() {
        // Prevent instantiation
    }

    /**
     * Checks if a 32-byte hash satisfies the required difficulty in leading zero bits.
     * High-speed path directly inspecting byte array.
     *
     * @param hashBytes      32-byte SHA-256 hash
     * @param difficultyBits number of required consecutive leading zero bits
     * @return true if hash has at least difficultyBits leading zero bits
     */
    public static boolean matches(byte[] hashBytes, int difficultyBits) {
        if (difficultyBits <= 0) {
            return true;
        }
        return HashUtil.countLeadingZeroBits(hashBytes) >= difficultyBits;
    }

    /**
     * Checks if a 64-character hex hash satisfies the required difficulty in leading zero bits.
     *
     * @param hexHash        64-character SHA-256 hex string
     * @param difficultyBits number of required consecutive leading zero bits
     * @return true if hash has at least difficultyBits leading zero bits
     */
    public static boolean matches(String hexHash, int difficultyBits) {
        if (difficultyBits <= 0) {
            return true;
        }
        return HashUtil.countLeadingZeroBits(hexHash) >= difficultyBits;
    }

    /**
     * Computes the numerical 256-bit target value corresponding to a difficulty bit count.
     * Target = 2^(256 - difficultyBits) - 1.
     *
     * @param difficultyBits number of leading zero bits
     * @return BigInteger target threshold
     */
    public static BigInteger computeTarget(int difficultyBits) {
        if (difficultyBits <= 0) {
            return MAX_TARGET;
        }
        if (difficultyBits >= 256) {
            return BigInteger.ZERO;
        }
        return BigInteger.ONE.shiftLeft(256 - difficultyBits).subtract(BigInteger.ONE);
    }

    /**
     * Verifies if hash numerical value is strictly below the target threshold.
     *
     * @param hashBytes 32-byte hash
     * @param target    256-bit target threshold
     * @return true if numerical value < target
     */
    public static boolean matchesTarget(byte[] hashBytes, BigInteger target) {
        BigInteger hashValue = new BigInteger(1, hashBytes);
        return hashValue.compareTo(target) <= 0;
    }

    /**
     * Generates a human-readable representation of the required target prefix.
     *
     * @param difficultyBits target bits
     * @return description such as "16 bits (~4 hex zeros)"
     */
    public static String getDifficultyDescription(int difficultyBits) {
        int hexZeros = difficultyBits / 4;
        int remBits = difficultyBits % 4;
        if (remBits == 0) {
            return String.format("%d bits ('%s' prefix)", difficultyBits, "0".repeat(hexZeros));
        } else {
            return String.format("%d bits (~%d hex zeros + %d bit mask)", difficultyBits, hexZeros, remBits);
        }
    }

    /**
     * Trả về chuỗi biểu diễn nhị phân (binary mask) của độ khó yêu cầu.
     * Ví dụ: difficultyBits = 8 -> "00000000... (8 leading zero bits required)".
     * Hữu ích cho việc kiểm tra trực quan và bảo vệ đồ án trước hội đồng.
     *
     * @param difficultyBits số bit 0 yêu cầu
     * @return chuỗi mask nhị phân mô tả điều kiện PoW
     */
    public static String getBinaryTargetMask(int difficultyBits) {
        if (difficultyBits <= 0) {
            return "NO_RESTRICTION (0 bits)";
        }
        int clampedBits = Math.clamp(difficultyBits, 1, 256);
        return "0".repeat(clampedBits) + "... (" + clampedBits + " leading zero bits required)";
    }
}
