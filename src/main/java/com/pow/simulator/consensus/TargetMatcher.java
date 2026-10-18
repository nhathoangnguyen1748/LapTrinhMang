package com.pow.simulator.consensus;

import com.pow.simulator.crypto.HashUtil;

import java.math.BigInteger;

/**
 * Dịch vụ kiểm tra và thẩm định điều kiện mục tiêu Proof-of-Work (Target Verification Service).
 * Hỗ trợ hai cơ chế thẩm định:
 * 1. Bitwise leading zeros trên mảng byte: Thẩm định siêu tốc O(1), không cấp phát vùng nhớ heap,
 *    dùng trực tiếp trong vòng lặp đào hàng triệu hash/giây của MultiThreadedMiner.
 * 2. BigInteger 256-bit numerical comparison: So sánh giá trị số học của hash với ngưỡng Target,
 *    tuân thủ đúng nguyên lý toán học của Satoshi Nakamoto trong mạng Bitcoin.
 *
 * Tác giả: Nguyễn Đỗ Trung Lương (MSSV: 2380601305)
 */
public final class TargetMatcher {

    private static final BigInteger MAX_TARGET = BigInteger.ONE.shiftLeft(256).subtract(BigInteger.ONE);

    private TargetMatcher() {
        // Chống khởi tạo instance (Utility class)
    }

    /**
     * Kiểm tra xem mảng byte hash (32 bytes SHA-256) có thỏa mãn số bit 0 dẫn đầu không.
     * Cơ chế: Kiểm tra từng byte từ trái qua phải, đếm liên tục số bit 0 đầu tiên.
     *
     * @param hashBytes      Mảng 32 bytes kết quả băm SHA-256
     * @param difficultyBits Số lượng bit 0 yêu cầu liên tiếp ở đầu
     * @return true nếu số bit 0 dẫn đầu >= difficultyBits
     */
    public static boolean matches(byte[] hashBytes, int difficultyBits) {
        if (difficultyBits <= 0) {
            return true;
        }
        return HashUtil.countLeadingZeroBits(hashBytes) >= difficultyBits;
    }

    /**
     * Kiểm tra chuỗi hash dạng Hex (64 ký tự) có thỏa mãn độ khó bit 0 hay không.
     *
     * @param hexHash        Chuỗi 64 ký tự hex SHA-256
     * @param difficultyBits Số lượng bit 0 yêu cầu liên tiếp ở đầu
     * @return true nếu số bit 0 dẫn đầu >= difficultyBits
     */
    public static boolean matches(String hexHash, int difficultyBits) {
        if (difficultyBits <= 0) {
            return true;
        }
        return HashUtil.countLeadingZeroBits(hexHash) >= difficultyBits;
    }

    /**
     * Tính toán giá trị ngưỡng số học Target (256-bit BigInteger) tương ứng với số bit độ khó.
     * Công thức toán học: Target = 2^(256 - difficultyBits) - 1.
     *
     * @param difficultyBits Số bit 0 yêu cầu
     * @return Ngưỡng trần số học BigInteger
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
     * Xác thực giá trị số học của Hash xem có nhỏ hơn hoặc bằng ngưỡng Target không (Hash <= Target).
     *
     * @param hashBytes Mảng 32 bytes của Hash
     * @param target    Ngưỡng số học 256-bit
     * @return true nếu giá trị số học của hash hợp lệ
     */
    public static boolean matchesTarget(byte[] hashBytes, BigInteger target) {
        BigInteger hashValue = new BigInteger(1, hashBytes);
        return hashValue.compareTo(target) <= 0;
    }

    /**
     * Trả về mô tả trực quan ngắn gọn về độ khó mục tiêu (ví dụ: "16 bits (~4 hex zeros)").
     *
     * @param difficultyBits Số bit độ khó
     * @return Chuỗi mô tả trực quan
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
     * Trả về chuỗi mặt nạ nhị phân (Binary Target Mask) thể hiện trực quan điều kiện PoW.
     * Ví dụ: 8 bits -> "00000000... (8 leading zero bits required)".
     *
     * @param difficultyBits Số bit 0 yêu cầu
     * @return Chuỗi biểu diễn mặt nạ nhị phân
     */
    public static String getBinaryTargetMask(int difficultyBits) {
        if (difficultyBits <= 0) {
            return "NO_RESTRICTION (0 bits)";
        }
        int clampedBits = Math.clamp(difficultyBits, 1, 256);
        return "0".repeat(clampedBits) + "... (" + clampedBits + " leading zero bits required)";
    }
}
