package com.pow.simulator.benchmark;

import com.pow.simulator.consensus.MiningResult;
import com.pow.simulator.consensus.MiningService;
import com.pow.simulator.consensus.MultiThreadedMiner;
import com.pow.simulator.core.Block;
import com.pow.simulator.core.Blockchain;
import com.pow.simulator.core.Transaction;
import com.pow.simulator.difficulty.DifficultyAdjuster;
import com.pow.simulator.validation.BlockchainValidator;
import com.pow.simulator.validation.ValidationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Automated benchmarking harness for evaluating mining performance, hashrates,
 * and dynamic difficulty retargeting across block epochs.
 */
public class BenchmarkRunner {

    private final MiningService miner;
    private final DifficultyAdjuster difficultyAdjuster;

    public BenchmarkRunner() {
        this(new MultiThreadedMiner(), new DifficultyAdjuster(400L, 5, 4.0, 0.25, 6, 24));
    }

    public BenchmarkRunner(MiningService miner, DifficultyAdjuster difficultyAdjuster) {
        this.miner = Objects.requireNonNull(miner, "miner must not be null");
        this.difficultyAdjuster = Objects.requireNonNull(difficultyAdjuster, "difficultyAdjuster must not be null");
    }

    /**
     * Executes an automated benchmark over a specified number of blocks (e.g. 20 blocks).
     *
     * @param totalBlocksToMine number of blocks to simulate
     * @param initialDifficulty starting difficulty in leading zero bits
     * @return BenchmarkReport with detailed per-block metrics and summary
     */
    public BenchmarkReport runBenchmark(int totalBlocksToMine, int initialDifficulty) {
        Blockchain chain = Blockchain.withDefaultGenesis(initialDifficulty);
        List<BlockBenchmarkEntry> entries = new ArrayList<>();
        List<String> eventLogs = new ArrayList<>();

        long benchmarkStartTime = System.currentTimeMillis();
        long cumulativeHashes = 0;

        for (int i = 1; i <= totalBlocksToMine; i++) {
            int currentDiff = difficultyAdjuster.getNextDifficulty(chain);

            if (difficultyAdjuster.isEpochBoundary(chain)) {
                DifficultyAdjuster.EpochReport report = difficultyAdjuster.evaluateAdjustment(chain);
                eventLogs.add(report.toString());
            }

            Block previous = chain.getLatestBlock();
            long now = System.currentTimeMillis();

            // Simulate realistic transaction batch
            List<Transaction> txs = List.of(
                    Transaction.create("MinerPool", "User_" + i, 6.25),
                    Transaction.create("User_" + (i - 1), "User_" + i, 1.50)
            );

            Block template = Block.createTemplate(i, now, previous.hash(), txs, currentDiff);
            MiningResult result = miner.mine(template);

            chain.addBlock(result.minedBlock());
            cumulativeHashes += result.totalHashes();

            entries.add(new BlockBenchmarkEntry(
                    i,
                    currentDiff,
                    result.durationMs(),
                    result.minedBlock().nonce(),
                    result.totalHashes(),
                    result.hashrate(),
                    result.minedBlock().hash()
            ));
        }

        long benchmarkDurationMs = System.currentTimeMillis() - benchmarkStartTime;
        ValidationResult audit = BlockchainValidator.validate(chain);

        return new BenchmarkReport(chain, entries, eventLogs, benchmarkDurationMs, cumulativeHashes, audit);
    }

    /**
     * Individual block benchmark statistics.
     */
    public record BlockBenchmarkEntry(
            int blockIndex,
            int difficultyBits,
            long durationMs,
            long nonce,
            long hashes,
            double hashrate,
            String hash
    ) {
        public String formatHashrate() {
            if (hashrate >= 1_000_000.0) {
                return String.format("%.2f MH/s", hashrate / 1_000_000.0);
            } else if (hashrate >= 1_000.0) {
                return String.format("%.2f kH/s", hashrate / 1_000.0);
            } else {
                return String.format("%.2f H/s", hashrate);
            }
        }
    }

    /**
     * Comprehensive benchmark outcome report.
     */
    public record BenchmarkReport(
            Blockchain chain,
            List<BlockBenchmarkEntry> entries,
            List<String> epochLogs,
            long totalDurationMs,
            long totalHashes,
            ValidationResult chainValidation
    ) {
        public double getAverageHashrate() {
            double seconds = Math.max(0.001, totalDurationMs / 1000.0);
            return totalHashes / seconds;
        }

        public double getAverageBlockTimeMs() {
            if (entries.isEmpty()) return 0;
            return entries.stream().mapToLong(BlockBenchmarkEntry::durationMs).average().orElse(0);
        }

        public void printSummaryTable() {
            System.out.println("\n+-------+------------+-----------+--------------+------------+---------------+----------------------------------+");
            System.out.println("| Block | Difficulty | Time (ms) | WinningNonce | Hashes     | Hashrate      | Target Hash (First 20 Chars)     |");
            System.out.println("+-------+------------+-----------+--------------+------------+---------------+----------------------------------+");

            for (BlockBenchmarkEntry e : entries) {
                System.out.printf("| %-5d | %-10s | %-9d | %-12d | %-10d | %-13s | %-32s |\n",
                        e.blockIndex(),
                        e.difficultyBits() + " bits",
                        e.durationMs(),
                        e.nonce(),
                        e.hashes(),
                        e.formatHashrate(),
                        e.hash().substring(0, Math.min(32, e.hash().length())));
            }

            System.out.println("+-------+------------+-----------+--------------+------------+---------------+----------------------------------+");

            if (!epochLogs.isEmpty()) {
                System.out.println("\n[EPOCH RETARGETING EVENTS]");
                for (String log : epochLogs) {
                    System.out.println("  -> " + log);
                }
            }

            printDifficultyCurve();

            System.out.println("\n[PERFORMANCE & INTEGRITY SUMMARY]");
            System.out.printf("  * Total Blocks Mined:    %d blocks\n", entries.size());
            System.out.printf("  * Total Elapsed Time:    %d ms (%.2f s)\n", totalDurationMs, totalDurationMs / 1000.0);
            System.out.printf("  * Total SHA-256 Hashes:  %,d operations\n", totalHashes);
            System.out.printf("  * Average Block Latency: %.2f ms\n", getAverageBlockTimeMs());
            System.out.printf("  * Global Mean Hashrate:  %.2f kH/s (%.2f H/s)\n", getAverageHashrate() / 1000.0, getAverageHashrate());
            System.out.printf("  * Ledger Cryptographic Audit: %s\n", chainValidation.isValid() ? "ALL BLOCKS VALID (PASS)" : "CORRUPTED (FAIL)");
        }

        private void printDifficultyCurve() {
            System.out.println("\n[DIFFICULTY PROGRESSION CURVE OVER 20 BLOCKS]");
            int minDiff = entries.stream().mapToInt(BlockBenchmarkEntry::difficultyBits).min().orElse(0);
            int maxDiff = entries.stream().mapToInt(BlockBenchmarkEntry::difficultyBits).max().orElse(0);

            for (BlockBenchmarkEntry e : entries) {
                int barLength = Math.max(1, (e.difficultyBits() - minDiff + 1) * 3);
                String bar = "#".repeat(barLength);
                System.out.printf("  Block #%02d [%2d bits] %-20s (time: %4d ms)\n",
                        e.blockIndex(), e.difficultyBits(), bar, e.durationMs());
            }
        }
    }
}
