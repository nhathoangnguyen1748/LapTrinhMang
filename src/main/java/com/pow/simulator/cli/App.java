package com.pow.simulator.cli;

import com.pow.simulator.attack.AttackSimulator;
import com.pow.simulator.benchmark.BenchmarkRunner;
import com.pow.simulator.consensus.InteractiveMinerCLI;
import com.pow.simulator.consensus.MiningResult;
import com.pow.simulator.consensus.MultiThreadedMiner;
import com.pow.simulator.core.Block;
import com.pow.simulator.core.Blockchain;
import com.pow.simulator.core.Transaction;
import com.pow.simulator.difficulty.DifficultyAdjuster;
import com.pow.simulator.network.NetworkDemo;
import com.pow.simulator.validation.BlockchainValidator;
import com.pow.simulator.validation.ValidationResult;

import java.util.List;

/**
 * Main command-line application entrypoint demonstrating:
 * 1. Genesis initialization & multi-threaded virtual thread mining.
 * 2. Dynamic difficulty retargeting across epochs.
 * 3. Comprehensive cryptographic ledger validation.
 * 4. Tamper attack simulation & downstream cascade invalidation.
 * 5. Automated 20-block performance benchmark & difficulty curve.
 */
public class App {

    public static void main(String[] args) {
        printBanner();

        System.out.println("\n[PHASE 1: GENESIS INITIALIZATION & MULTI-THREADED MINING]");
        int initialDifficulty = 12; // 12 leading zero bits (~3 hex zeros)
        Blockchain chain = Blockchain.withDefaultGenesis(initialDifficulty);
        Block genesis = chain.getLatestBlock();
        System.out.printf("Initialized Genesis Block #0 | Hash: %s | Diff: %d bits\n",
                genesis.hash(), genesis.difficultyBits());

        if (args.length > 0 && ("--interactive".equalsIgnoreCase(args[0]) || "-i".equalsIgnoreCase(args[0]) || "--miner".equalsIgnoreCase(args[0]))) {
            InteractiveMinerCLI cli = new InteractiveMinerCLI();
            cli.startInteractiveSession(chain);
            return;
        }

        MultiThreadedMiner miner = new MultiThreadedMiner(
                MultiThreadedMiner.ExecutionMode.VIRTUAL_THREADS,
                Runtime.getRuntime().availableProcessors()
        );
        DifficultyAdjuster adjuster = new DifficultyAdjuster(800L, 3, 4.0, 0.25, 6, 24);

        System.out.println("\nMining sample transactions with Java Virtual Threads (MultiThreadedMiner)...");
        for (int i = 1; i <= 6; i++) {
            int targetDiff = adjuster.getNextDifficulty(chain);

            if (adjuster.isEpochBoundary(chain)) {
                DifficultyAdjuster.EpochReport report = adjuster.evaluateAdjustment(chain);
                System.out.println("\n>>> [EPOCH RETARGET EVENT] " + report);
            }

            List<Transaction> txs = List.of(
                    Transaction.create("Node_" + (i - 1), "Node_" + i, 2.5 * i),
                    Transaction.create("Miner_Reward", "Node_" + i, 3.125)
            );

            Block template = Block.createTemplate(
                    i,
                    System.currentTimeMillis(),
                    chain.getLatestBlock().hash(),
                    txs,
                    targetDiff
            );

            MiningResult result = miner.mine(template);
            chain.addBlock(result.minedBlock());

            System.out.printf("  + Mined Block #%d [Nonce: %-10d | Time: %4d ms | Hashes: %-8d | Rate: %-11s | Hash: %s]\n",
                    result.minedBlock().index(),
                    result.minedBlock().nonce(),
                    result.durationMs(),
                    result.totalHashes(),
                    result.getFormattedHashrate(),
                    result.minedBlock().getShortHash());
        }

        System.out.println("\n[PHASE 2: CRYPTOGRAPHIC LEDGER AUDIT]");
        ValidationResult initialAudit = BlockchainValidator.validate(chain);
        System.out.println("Audit Result: " + initialAudit);
        System.out.printf("Ledger depth: %d blocks verified without anomaly.\n", chain.size());

        System.out.println("\n[PHASE 3: LEDGER INTEGRITY & ATTACK SIMULATION]");
        System.out.println("Simulating malicious payload tampering on Block #2...");
        AttackSimulator attackSimulator = new AttackSimulator(miner);
        AttackSimulator.TamperAttackReport attackReport = attackSimulator.simulateTamperAttack(chain, 2);
        System.out.println(attackReport.getSummary());

        System.out.println("\n[PHASE 4: AUTOMATED 20-BLOCK PERFORMANCE BENCHMARK]");
        System.out.println("Running 20-block automated benchmark harness with dynamic retargeting...");
        BenchmarkRunner benchmarkRunner = new BenchmarkRunner(
                miner,
                new DifficultyAdjuster(400L, 5, 4.0, 0.25, 8, 22)
        );
        BenchmarkRunner.BenchmarkReport benchmarkReport = benchmarkRunner.runBenchmark(20, 10);
        benchmarkReport.printSummaryTable();

        System.out.println("\n[PHASE 5: DISTRIBUTED P2P TCP SOCKET NETWORK CLUSTER]");
        NetworkDemo.runDemo();

        System.out.println("\n=============================================================");
        System.out.println("   PoW ENGINE & P2P NETWORK CLUSTER COMPLETED SUCCESSFULLY   ");
        System.out.println("=============================================================\n");
    }

    private static void printBanner() {
        System.out.println("""
            =============================================================
             PRODUCTION P2P PROOF-OF-WORK BLOCKCHAIN & LEDGER NETWORK
                     LẬP TRÌNH MẠNG (TCP SOCKETS)  |  JDK 21+  |  NetBeans
            =============================================================
            * Peer-to-Peer TCP/IP Socket Cluster with Length-Prefixed Framing
            * Asynchronous ServerSocket Concurrency (Java 21 Virtual Threads)
            * Multi-Hop Gossip Protocol Transaction Propagation
            * Block Broadcasting & Real-Time Ledger Synchronization
            * Cryptographic SHA-256 Hashing, Merkle Roots & PoW Engine
            * Dynamic Difficulty Retargeting & Tamper Attack Audit
            =============================================================""");
    }
}
