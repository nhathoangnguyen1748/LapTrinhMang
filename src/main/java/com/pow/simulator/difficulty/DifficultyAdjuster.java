package com.pow.simulator.difficulty;

import com.pow.simulator.core.Block;
import com.pow.simulator.core.Blockchain;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Bitcoin-style dynamic difficulty adjustment engine.
 * Dynamically retargets Proof-of-Work difficulty every EPOCH_SIZE blocks based on
 * actual elapsed time versus expected block generation time, applying mathematical
 * dampening factors to prevent erratic difficulty swings.
 */
public class DifficultyAdjuster {

    public static final long DEFAULT_TARGET_BLOCK_TIME_MS = 1500L;
    public static final int DEFAULT_EPOCH_SIZE = 5;
    public static final double MAX_ADJUSTMENT_FACTOR = 4.0;
    public static final double MIN_ADJUSTMENT_FACTOR = 0.25;
    public static final int MIN_DIFFICULTY_BITS = 4;
    public static final int MAX_DIFFICULTY_BITS = 32;

    private final long targetBlockTimeMs;
    private final int epochSize;
    private final double maxAdjustmentFactor;
    private final double minAdjustmentFactor;
    private final int minDifficultyBits;
    private final int maxDifficultyBits;

    /**
     * Default constructor using production-tuned simulation constants.
     */
    public DifficultyAdjuster() {
        this(DEFAULT_TARGET_BLOCK_TIME_MS, DEFAULT_EPOCH_SIZE,
                MAX_ADJUSTMENT_FACTOR, MIN_ADJUSTMENT_FACTOR,
                MIN_DIFFICULTY_BITS, MAX_DIFFICULTY_BITS);
    }

    /**
     * Fully parameterized constructor for custom test scenarios.
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
     * Determines whether the current chain tip constitutes an epoch adjustment boundary.
     *
     * @param chain blockchain instance
     * @return true if an epoch just completed
     */
    public boolean isEpochBoundary(Blockchain chain) {
        Objects.requireNonNull(chain, "chain must not be null");
        return chain.size() >= epochSize && (chain.size() % epochSize == 0);
    }

    /**
     * Calculates the difficulty for the next block to be mined.
     *
     * @param chain current blockchain state
     * @return difficulty in leading zero bits
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
     * Evaluates the epoch performance and calculates detailed adjustment metrics.
     *
     * @param chain current blockchain state
     * @return EpochReport containing audit details of the adjustment
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

        // Ratio > 1.0 means mined faster than expected -> increase difficulty
        // Ratio < 1.0 means mined slower than expected -> decrease difficulty
        double rawRatio = (double) expectedTimeMs / (double) actualTimeMs;

        // Apply Bitcoin-style dampening factor (e.g. capped between 0.25x and 4.0x)
        double clampedRatio = Math.clamp(rawRatio, minAdjustmentFactor, maxAdjustmentFactor);

        int currentDifficulty = latestBlock.difficultyBits();

        // In binary PoW, difficulty is in bits (work doubles with each bit: 2^delta)
        // deltaBits = round(log2(clampedRatio))
        int deltaBits = (int) Math.round(Math.log(clampedRatio) / Math.log(2.0));

        // Ensure sensitivity if actual time diverges noticeably
        if (deltaBits == 0) {
            if (rawRatio >= 1.20) {
                deltaBits = 1;
            } else if (rawRatio <= 0.80) {
                deltaBits = -1;
            }
        }

        // Clamp delta to dampening bounds [-2, +2] corresponding to [0.25, 4.0]
        deltaBits = Math.clamp(deltaBits, -2, 2);

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

    /**
     * Immutable report detailing difficulty epoch calculations.
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
