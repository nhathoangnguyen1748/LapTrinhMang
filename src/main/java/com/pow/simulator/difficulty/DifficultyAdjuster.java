package com.pow.simulator.difficulty;

import com.pow.simulator.core.Block;
import com.pow.simulator.core.Blockchain;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Động cơ tự động điều chỉnh độ khó Proof-of-Work theo chu kỳ Epochs (Dynamic Difficulty Retargeting).
 * Mô phỏng thuật toán retargeting của mạng Bitcoin:
 * Cứ sau mỗi chu kỳ EPOCH_SIZE (ví dụ 5 blocks), hệ thống sẽ so sánh:
 *   - actualTimeMs: Thời gian thực tế các thợ đào hoàn thành epoch
 *   - expectedTimeMs: Thời gian mong muốn của mạng lưới (targetBlockTimeMs * epochSize)
 * Từ đó tính toán tỷ lệ rawRatio = expectedTime / actualTime:
 *   - Nếu đào quá nhanh (actual < expected) -> Tăng độ khó (+bits).
 *   - Nếu đào quá chậm (actual > expected) -> Giảm độ khó (-bits).
 * Áp dụng hệ số giảm chấn toán học (Dampening Factor [0.25x - 4.0x]) để chống sốc độ khó.
 *
 * Tác giả: Nguyễn Đỗ Trung Lương (MSSV: 2380601305)
 */
public class DifficultyAdjuster {

    // Thời gian mục tiêu cho mỗi khối (mặc định 1.5 giây = 1500 ms)
    public static final long DEFAULT_TARGET_BLOCK_TIME_MS = 1500L;
    // Chu kỳ số khối giữa các lần điều chỉnh độ khó
    public static final int DEFAULT_EPOCH_SIZE = 5;
    // Hệ số giảm chấn tăng tối đa: 4.0x (tương đương +2 bits)
    public static final double MAX_ADJUSTMENT_FACTOR = 4.0;
    // Hệ số giảm chấn giảm tối đa: 0.25x (tương đương -2 bits)
    public static final double MIN_ADJUSTMENT_FACTOR = 0.25;
    // Sàn độ khó tối thiểu (4 bits = ít nhất nửa byte số 0)
    public static final int MIN_DIFFICULTY_BITS = 4;
    // Trần độ khó tối đa trong mô phỏng (32 bits)
    public static final int MAX_DIFFICULTY_BITS = 32;

    private final long targetBlockTimeMs;
    private final int epochSize;
    private final double maxAdjustmentFactor;
    private final double minAdjustmentFactor;
    private final int minDifficultyBits;
    private final int maxDifficultyBits;

    /**
     * Khởi tạo bộ điều chỉnh độ khó với tham số mặc định.
     */
    public DifficultyAdjuster() {
        this(DEFAULT_TARGET_BLOCK_TIME_MS, DEFAULT_EPOCH_SIZE,
                MAX_ADJUSTMENT_FACTOR, MIN_ADJUSTMENT_FACTOR,
                MIN_DIFFICULTY_BITS, MAX_DIFFICULTY_BITS);
    }

    /**
     * Constructor đầy đủ tham số phục vụ cấu hình linh hoạt và kiểm thử tự động.
     */
    public DifficultyAdjuster(
            long targetBlockTimeMs,
            int epochSize,
            double maxAdjustmentFactor,
            double minAdjustmentFactor,
            int minDifficultyBits,
            int maxDifficultyBits
    ) {
        if (targetBlockTimeMs <= 0) throw new IllegalArgumentException("targetBlockTimeMs must be > 0");
        if (epochSize <= 1) throw new IllegalArgumentException("epochSize must be > 1");
        this.targetBlockTimeMs = targetBlockTimeMs;
        this.epochSize = epochSize;
        this.maxAdjustmentFactor = maxAdjustmentFactor;
        this.minAdjustmentFactor = minAdjustmentFactor;
        this.minDifficultyBits = minDifficultyBits;
        this.maxDifficultyBits = maxDifficultyBits;
    }

    /**
     * Kiểm tra xem độ cao hiện tại của chuỗi có nằm trên ranh giới chu kỳ điều chỉnh (Epoch Boundary) hay không.
     *
     * @param chain Đối tượng sổ cái chuỗi khối
     * @return true nếu vừa hoàn thành một chu kỳ Epoch
     */
    public boolean isEpochBoundary(Blockchain chain) {
        Objects.requireNonNull(chain, "chain must not be null");
        return chain.size() >= epochSize && (chain.size() % epochSize == 0);
    }

    /**
     * Tính toán độ khó (số bit 0) cho khối tiếp theo cần khai thác.
     *
     * @param chain Trạng thái chuỗi khối hiện tại
     * @return Số lượng bit 0 mục tiêu cho khối mới
     */
    public int getNextDifficulty(Blockchain chain) {
        Objects.requireNonNull(chain, "chain must not be null");
        int currentDiff = chain.getLatestBlock().difficultyBits();

        if (!isEpochBoundary(chain)) {
            return currentDiff;
        }

        EpochReport report = evaluateAdjustment(chain);
        return report.newDifficulty();
    }

    /**
     * Đánh giá hiệu năng khai thác của Epoch vừa kết thúc và xuất báo cáo kiểm toán chi tiết.
     *
     * @param chain Trạng thái sổ cái
     * @return EpochReport chứa toàn bộ thông số đánh giá độ khó
     */
    public EpochReport evaluateAdjustment(Blockchain chain) {
        Objects.requireNonNull(chain, "chain must not be null");
        List<Block> blocks = chain.getChain();
        int size = blocks.size();

        if (size < epochSize) {
            int currentDiff = blocks.getLast().difficultyBits();
            return new EpochReport(0, size, 0, 0, 1.0, 1.0, currentDiff, currentDiff, "INITIAL_EPOCH");
        }

        Block latestBlock = blocks.get(size - 1);
        Block epochStartBlock = blocks.get(size - epochSize);

        long actualTimeMs;
        if (size == epochSize && epochStartBlock.index() == 0 && size > 1) {
            long elapsedMined = latestBlock.timestamp() - blocks.get(1).timestamp();
            actualTimeMs = Math.max(1L, (elapsedMined * epochSize) / Math.max(1, epochSize - 1));
        } else {
            actualTimeMs = Math.max(1L, latestBlock.timestamp() - epochStartBlock.timestamp());
        }
        long expectedTimeMs = targetBlockTimeMs * epochSize;

        // 1. Tỷ lệ tốc độ thô: rawRatio > 1 nghĩa là đào nhanh hơn dự kiến; rawRatio < 1 là đào chậm
        double rawRatio = (double) expectedTimeMs / (double) actualTimeMs;

        // 2. Kẹp tỷ lệ trong khoảng giảm chấn [minAdjustmentFactor, maxAdjustmentFactor] (ví dụ: [0.25x, 4.0x])
        double clampedRatio = Math.clamp(rawRatio, minAdjustmentFactor, maxAdjustmentFactor);

        int currentDifficulty = latestBlock.difficultyBits();

        // 3. Quy đổi tỷ lệ sang số lượng bit thay đổi: deltaBits = round(log2(clampedRatio))
        int deltaBits = (int) Math.round(Math.log(clampedRatio) / Math.log(2.0));

        // 4. Đảm bảo độ nhạy nếu thời gian thực tế lệch đáng kể khỏi kỳ vọng (+-20%)
        if (deltaBits == 0) {
            if (rawRatio >= 1.20) {
                deltaBits = 1;
            } else if (rawRatio <= 0.80) {
                deltaBits = -1;
            }
        }

        // 5. Giới hạn biến động tối đa mỗi epoch là [-2 bits, +2 bits]
        deltaBits = Math.clamp(deltaBits, -2, 2);

        // 6. Kẹp trong giới hạn an toàn minDifficultyBits và maxDifficultyBits
        int newDifficulty = Math.clamp(currentDifficulty + deltaBits, minDifficultyBits, maxDifficultyBits);

        String direction;
        if (newDifficulty > currentDifficulty) {
            direction = "INCREASED (+" + (newDifficulty - currentDifficulty) + " bits)";
        } else if (newDifficulty < currentDifficulty) {
            direction = "DECREASED (-" + (currentDifficulty - newDifficulty) + " bits)";
        } else {
            direction = "UNCHANGED";
        }

        int epochNumber = size / epochSize;
        return new EpochReport(
                epochNumber,
                size,
                actualTimeMs,
                expectedTimeMs,
                rawRatio,
                clampedRatio,
                currentDifficulty,
                newDifficulty,
                direction
        );
    }

    public long getTargetBlockTimeMs() {
        return targetBlockTimeMs;
    }

    public int getEpochSize() {
        return epochSize;
    }

    public double getMaxAdjustmentFactor() {
        return maxAdjustmentFactor;
    }

    public double getMinAdjustmentFactor() {
        return minAdjustmentFactor;
    }

    public int getMinDifficultyBits() {
        return minDifficultyBits;
    }

    public int getMaxDifficultyBits() {
        return maxDifficultyBits;
    }

    /**
     * Bản ghi chứa đầy đủ thông số kiểm toán sự kiện điều chỉnh độ khó.
     */
    public record EpochReport(
            int epochNumber,
            int totalBlocks,
            long actualTimeMs,
            long expectedTimeMs,
            double rawRatio,
            double clampedRatio,
            int oldDifficulty,
            int newDifficulty,
            String action
    ) {
        @Override
        public String toString() {
            return String.format(
                    "[Epoch #%d | Blocks: %d] Actual: %d ms vs Expected: %d ms | Ratio: %.2fx (Clamped: %.2fx) | Diff: %d -> %d (%s)",
                    epochNumber, totalBlocks, actualTimeMs, expectedTimeMs, rawRatio, clampedRatio, oldDifficulty, newDifficulty, action);
        }
    }
}
