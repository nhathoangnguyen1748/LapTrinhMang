package com.pow.simulator.consensus;

import com.pow.simulator.core.Block;
import com.pow.simulator.core.Blockchain;
import com.pow.simulator.core.Transaction;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/**
 * Interactive Mining CLI Suite designed for real-time PoW experimentation.
 * Allows developers and evaluators to:
 * 1. Select execution models (Java 21 Virtual Threads vs Platform Thread Pool).
 * 2. Fine-tune mining worker concurrency and difficulty bits.
 * 3. Inspect target thresholds, bitmasks, and real-time mining telemetry.
 *
 * Authored by: Nguyễn Đỗ Trung Lương (MSSV: 2380601305)
 */
public class InteractiveMinerCLI {

    private final InputStream in;
    private final PrintStream out;

    public InteractiveMinerCLI() {
        this(System.in, System.out);
    }

    public InteractiveMinerCLI(InputStream in, PrintStream out) {
        this.in = Objects.requireNonNull(in, "InputStream must not be null");
        this.out = Objects.requireNonNull(out, "PrintStream must not be null");
    }

    /**
     * Executes an interactive mining session via console input.
     */
    public void startInteractiveSession(Blockchain chain) {
        Scanner scanner = new Scanner(in);
        out.println("\n=======================================================");
        out.println("   INTERACTIVE PROOF-OF-WORK MINING CONSOLE (CLI)     ");
        out.println("   Author: Nguyễn Đỗ Trung Lương (MSSV: 2380601305)    ");
        out.println("=======================================================");

        int defaultCores = Runtime.getRuntime().availableProcessors();
        out.printf("Hardware Architecture detected: %d CPU Core(s)\n", defaultCores);

        out.println("\n[1] Select Execution Thread Model:");
        out.println("    1. Java 21 Virtual Threads (Thread.ofVirtual() - High Concurrency)");
        out.println("    2. Classic Platform Thread Pool (Fixed Thread Pool)");
        out.print("Select option [1/2, default 1]: ");
        String modeChoice = scanner.hasNextLine() ? scanner.nextLine().trim() : "1";
        MultiThreadedMiner.ExecutionMode mode = "2".equals(modeChoice)
                ? MultiThreadedMiner.ExecutionMode.PLATFORM_THREADS
                : MultiThreadedMiner.ExecutionMode.VIRTUAL_THREADS;

        out.print("Enter number of worker threads [default " + defaultCores + "]: ");
        String threadInput = scanner.hasNextLine() ? scanner.nextLine().trim() : "";
        int threads = defaultCores;
        try {
            if (!threadInput.isEmpty()) {
                threads = Math.max(1, Integer.parseInt(threadInput));
            }
        } catch (NumberFormatException e) {
            threads = defaultCores;
        }

        out.print("Enter target difficulty in bits (e.g. 8, 12, 16) [default 12]: ");
        String diffInput = scanner.hasNextLine() ? scanner.nextLine().trim() : "";
        int difficultyBits = 12;
        try {
            if (!diffInput.isEmpty()) {
                difficultyBits = Math.clamp(Integer.parseInt(diffInput), 4, 32);
            }
        } catch (NumberFormatException e) {
            difficultyBits = 12;
        }

        runMiningSimulation(chain, mode, threads, difficultyBits);
    }

    /**
     * Programmatically runs a configured mining benchmark/simulation with full telemetry.
     */
    public MiningResult runMiningSimulation(Blockchain chain,
                                           MultiThreadedMiner.ExecutionMode mode,
                                           int threads,
                                           int difficultyBits) {
        out.println("\n----------------- MINING SPECIFICATION -----------------");
        out.printf("  • Thread Architecture : %s\n", mode);
        out.printf("  • Concurrency Workers : %d threads\n", threads);
        out.printf("  • Target Difficulty   : %s\n", TargetMatcher.getDifficultyDescription(difficultyBits));
        out.printf("  • Numerical Target    : %s\n", TargetMatcher.computeTarget(difficultyBits).toString(16));
        out.printf("  • Chain Tip Index     : #%d\n", chain.getLatestBlock().index());
        out.printf("  • Previous Hash       : %s\n", chain.getLatestBlock().hash());
        out.println("--------------------------------------------------------");

        List<Transaction> pendingTxs = List.of(
                Transaction.create("Miner_Reward", "2380601305_TrungLuong", 50.0),
                Transaction.create("Alice", "Bob", 12.75),
                Transaction.create("Charlie", "David", 3.50)
        );

        int newIndex = chain.getLatestBlock().index() + 1;
        Block template = Block.createTemplate(
                newIndex,
                System.currentTimeMillis(),
                chain.getLatestBlock().hash(),
                pendingTxs,
                difficultyBits
        );

        out.printf("[*] Launching %d mining workers across partitioned nonce spaces...\n", threads);
        MultiThreadedMiner miner = new MultiThreadedMiner(mode, threads);

        long startWallTime = System.currentTimeMillis();
        MiningResult result = miner.mine(template, threads);
        long endWallTime = System.currentTimeMillis();

        out.println("\n=================== MINING RESULT ===================");
        if (result.success()) {
            Block minedBlock = result.minedBlock();
            chain.addBlock(minedBlock);

            out.println("  STATUS        : SUCCESS (Block Solved & Verified!)");
            out.printf("  BLOCK INDEX   : #%d\n", minedBlock.index());
            out.printf("  WINNING NONCE : %d\n", minedBlock.nonce());
            out.printf("  BLOCK HASH    : %s\n", minedBlock.hash());
            out.printf("  MERKLE ROOT   : %s\n", minedBlock.merkleRoot());
            out.printf("  TRANSACTIONS  : %d transaction(s)\n", minedBlock.transactions().size());
            out.printf("  ELAPSED TIME  : %d ms (Wall-clock: %d ms)\n", result.durationMs(), (endWallTime - startWallTime));
            out.printf("  TOTAL HASHES  : %,d hashes\n", result.totalHashes());
            out.printf("  HASHRATE      : %s\n", result.getFormattedHashrate());
            out.printf("  WORKERS USED  : %d\n", result.threadsUsed());
            out.printf("  TARGET MATCH  : Verified leading zeros >= %d bits: %b\n",
                    difficultyBits, TargetMatcher.matches(minedBlock.hash(), difficultyBits));
        } else {
            out.println("  STATUS        : FAILED (Nonce space exhausted or cancelled)");
            out.printf("  TIME ELAPSED  : %d ms\n", result.durationMs());
            out.printf("  HASHES CHECKED: %,d hashes\n", result.totalHashes());
        }
        out.println("=====================================================\n");

        return result;
    }
}
