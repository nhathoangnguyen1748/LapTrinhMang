package com.pow.simulator;

import com.pow.simulator.consensus.MiningResult;
import com.pow.simulator.consensus.MultiThreadedMiner;
import com.pow.simulator.consensus.TargetMatcher;
import com.pow.simulator.core.Block;
import com.pow.simulator.core.Transaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Concurrency & Worker Cancellation Tests")
class ConcurrencyTest {

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    @DisplayName("Multi-threaded virtual thread mining halts all workers promptly upon finding target")
    void testVirtualThreadMiningCancellation() {
        MultiThreadedMiner miner = new MultiThreadedMiner(
                MultiThreadedMiner.ExecutionMode.VIRTUAL_THREADS, 16);

        Block template = Block.createTemplate(
                1,
                System.currentTimeMillis(),
                "0".repeat(64),
                List.of(Transaction.create("Node1", "Node2", 10.0)),
                10 // Moderate difficulty for quick resolution
        );

        long start = System.currentTimeMillis();
        MiningResult result = miner.mine(template);
        long elapsed = System.currentTimeMillis() - start;

        assertTrue(result.success(), "Mining must succeed");
        assertNotNull(result.minedBlock());
        assertTrue(TargetMatcher.matches(result.minedBlock().hash(), 10));
        assertTrue(elapsed < 8000, "Mining should complete promptly without thread lingering");
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    @DisplayName("Platform threads mode functions correctly and produces valid PoW")
    void testPlatformThreadMining() {
        MultiThreadedMiner miner = new MultiThreadedMiner(
                MultiThreadedMiner.ExecutionMode.PLATFORM_THREADS, 4);

        Block template = Block.createTemplate(
                1,
                System.currentTimeMillis(),
                "0".repeat(64),
                List.of(Transaction.create("PoolA", "MinerB", 25.0)),
                10
        );

        MiningResult result = miner.mine(template);

        assertTrue(result.success());
        assertTrue(TargetMatcher.matches(result.minedBlock().hash(), 10));
        assertTrue(result.totalHashes() > 0);
    }

    @Test
    @Timeout(value = 15, unit = TimeUnit.SECONDS)
    @DisplayName("Concurrent independent mining requests execute without interference or race conditions")
    void testParallelIndependentMiningRuns() throws Exception {
        int tasks = 3;
        ExecutorService outerExecutor = Executors.newFixedThreadPool(tasks);
        List<Callable<MiningResult>> miningJobs = new java.util.ArrayList<>();

        for (int i = 0; i < tasks; i++) {
            final int id = i;
            miningJobs.add(() -> {
                MultiThreadedMiner miner = new MultiThreadedMiner(MultiThreadedMiner.ExecutionMode.VIRTUAL_THREADS, 2);
                Block template = Block.createTemplate(
                        id + 1,
                        System.currentTimeMillis() + id * 50,
                        "0".repeat(64),
                        List.of(Transaction.create("Sender" + id, "Receiver" + id, 10.0)),
                        8
                );
                return miner.mine(template);
            });
        }

        List<Future<MiningResult>> results = outerExecutor.invokeAll(miningJobs);
        outerExecutor.shutdown();
        assertTrue(outerExecutor.awaitTermination(10, TimeUnit.SECONDS));

        for (Future<MiningResult> f : results) {
            MiningResult res = f.get();
            assertTrue(res.success());
            assertTrue(TargetMatcher.matches(res.minedBlock().hash(), 8));
        }
    }
}
