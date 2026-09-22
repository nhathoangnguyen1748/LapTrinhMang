package com.pow.simulator;

import com.pow.simulator.core.Block;
import com.pow.simulator.core.Blockchain;
import com.pow.simulator.difficulty.DifficultyAdjuster;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Dynamic Difficulty Adjustment Tests")
class DifficultyAdjustmentTest {

    private DifficultyAdjuster adjuster;

    @BeforeEach
    void setUp() {
        // Target: 2000 ms per block, epoch = 5 blocks, clamp [0.25, 4.0], min 4, max 32
        adjuster = new DifficultyAdjuster(2000L, 5, 4.0, 0.25, 4, 32);
    }

    @Test
    @DisplayName("Difficulty scales UP when blocks are mined much faster than target time")
    void testDifficultyScalesUpWhenFast() {
        int initialDiff = 10;
        long baseTimestamp = 1700000000000L;
        Blockchain chain = Blockchain.withDefaultGenesis(initialDiff, baseTimestamp);

        // Simulate 4 more blocks (total 5 blocks in chain) mined in only 400 ms (expected 10,000 ms)
        for (int i = 1; i <= 4; i++) {
            Block prev = chain.getLatestBlock();
            Block block = new Block(
                    i,
                    baseTimestamp + (i * 100L), // Only 100ms per block
                    prev.hash(),
                    "merkleRoot",
                    List.of(),
                    12345L,
                    initialDiff,
                    "0".repeat(64)
            );
            chain.addBlock(block);
        }

        assertEquals(5, chain.size());
        assertTrue(adjuster.isEpochBoundary(chain), "5 blocks should trigger epoch boundary");

        int nextDiff = adjuster.getNextDifficulty(chain);
        assertTrue(nextDiff > initialDiff,
                String.format("Difficulty should scale up: expected > %d, got %d", initialDiff, nextDiff));

        DifficultyAdjuster.EpochReport report = adjuster.evaluateAdjustment(chain);
        assertTrue(report.action().startsWith("INCREASED"));
    }

    @Test
    @DisplayName("Difficulty scales DOWN when blocks are mined much slower than target time")
    void testDifficultyScalesDownWhenSlow() {
        int initialDiff = 16;
        long baseTimestamp = 1700000000000L;
        Blockchain chain = Blockchain.withDefaultGenesis(initialDiff, baseTimestamp);

        // Simulate 4 blocks taking 30,000 ms (expected is 10,000 ms)
        for (int i = 1; i <= 4; i++) {
            Block prev = chain.getLatestBlock();
            Block block = new Block(
                    i,
                    baseTimestamp + (i * 7500L), // 7.5s per block
                    prev.hash(),
                    "merkleRoot",
                    List.of(),
                    12345L,
                    initialDiff,
                    "0".repeat(64)
            );
            chain.addBlock(block);
        }

        int nextDiff = adjuster.getNextDifficulty(chain);
        assertTrue(nextDiff < initialDiff,
                String.format("Difficulty should scale down: expected < %d, got %d", initialDiff, nextDiff));

        DifficultyAdjuster.EpochReport report = adjuster.evaluateAdjustment(chain);
        assertTrue(report.action().startsWith("DECREASED"));
    }

    @Test
    @DisplayName("Dampening factor strictly limits maximum single-epoch adjustment to 4x / 0.25x (at most 2 bits)")
    void testDampeningFactorCapsExtremeSwings() {
        int initialDiff = 14;
        long baseTimestamp = 1700000000000L;
        Blockchain chain = Blockchain.withDefaultGenesis(initialDiff, baseTimestamp);

        // Virtually instant blocks (1 ms total for epoch, raw ratio = 10,000x)
        for (int i = 1; i <= 4; i++) {
            Block prev = chain.getLatestBlock();
            Block block = new Block(
                    i,
                    baseTimestamp + 1L,
                    prev.hash(),
                    "merkleRoot",
                    List.of(),
                    1L,
                    initialDiff,
                    "0".repeat(64)
            );
            chain.addBlock(block);
        }

        DifficultyAdjuster.EpochReport report = adjuster.evaluateAdjustment(chain);
        assertEquals(4.0, report.clampedRatio(), 0.001, "Clamped ratio must not exceed 4.0");

        int nextDiff = adjuster.getNextDifficulty(chain);
        // At 4.0x, log2(4.0) = 2 bits maximum increase
        assertEquals(initialDiff + 2, nextDiff, "Difficulty increase must be capped by dampening factor to +2 bits");
    }

    @Test
    @DisplayName("Difficulty never drops below configured MIN_DIFFICULTY_BITS threshold")
    void testMinimumDifficultyFloorEnforced() {
        int initialDiff = 4; // Already at minimum
        long baseTimestamp = 1700000000000L;
        Blockchain chain = Blockchain.withDefaultGenesis(initialDiff, baseTimestamp);

        // Extremely slow mining
        for (int i = 1; i <= 4; i++) {
            Block prev = chain.getLatestBlock();
            Block block = new Block(
                    i,
                    baseTimestamp + (i * 100000L),
                    prev.hash(),
                    "merkleRoot",
                    List.of(),
                    1L,
                    initialDiff,
                    "0".repeat(64)
            );
            chain.addBlock(block);
        }

        int nextDiff = adjuster.getNextDifficulty(chain);
        assertEquals(4, nextDiff, "Difficulty must not fall below minDifficultyBits (4)");
    }

    @Test
    @DisplayName("Difficulty remains unchanged between epoch boundaries")
    void testDifficultyUnchangedWithinEpoch() {
        int initialDiff = 10;
        Blockchain chain = Blockchain.withDefaultGenesis(initialDiff);

        // Add 2 blocks (size 3, epoch is 5)
        for (int i = 1; i <= 2; i++) {
            Block prev = chain.getLatestBlock();
            Block block = new Block(
                    i,
                    System.currentTimeMillis() + i * 100,
                    prev.hash(),
                    "merkleRoot",
                    List.of(),
                    1L,
                    initialDiff,
                    "0".repeat(64)
            );
            chain.addBlock(block);
        }

        assertFalse(adjuster.isEpochBoundary(chain));
        assertEquals(initialDiff, adjuster.getNextDifficulty(chain));
    }
}
