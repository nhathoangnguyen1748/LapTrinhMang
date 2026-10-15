package com.pow.simulator.consensus;

import com.pow.simulator.core.Block;
import com.pow.simulator.crypto.HashUtil;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.LongAdder;

/**
 * High-performance multi-threaded Proof-of-Work miner.
 * Utilizes Java 21 Virtual Threads or Platform Thread Pools, partitioned nonce strides,
 * and immediate atomic cancellation across all workers when a solution is reached.
 */
public class MultiThreadedMiner implements MiningService {

    public enum ExecutionMode {
        VIRTUAL_THREADS,
        PLATFORM_THREADS
    }

    private static final int DEFAULT_CHUNK_SIZE = 4096;
    private final ExecutionMode executionMode;
    private final int defaultThreadCount;

    /**
     * Initializes the miner with Virtual Threads by default.
     */
    public MultiThreadedMiner() {
        this(ExecutionMode.VIRTUAL_THREADS, Math.max(2, Runtime.getRuntime().availableProcessors()));
    }

    /**
     * Initializes the miner with a specific execution mode and thread count.
     */
    public MultiThreadedMiner(ExecutionMode executionMode, int defaultThreadCount) {
        this.executionMode = Objects.requireNonNull(executionMode, "ExecutionMode must not be null");
        this.defaultThreadCount = Math.max(1, defaultThreadCount);
    }

    @Override
    public MiningResult mine(Block templateBlock) {
        return mine(templateBlock, defaultThreadCount);
    }

    @Override
    public MiningResult mine(Block templateBlock, int threadCount) {
        Objects.requireNonNull(templateBlock, "Template block must not be null");
        int workers = Math.max(1, threadCount);

        long startTime = System.currentTimeMillis();
        AtomicBoolean isSolved = new AtomicBoolean(false);
        AtomicLong nextNonceChunk = new AtomicLong(0L);
        LongAdder totalHashesAdder = new LongAdder();
        AtomicReference<WinningNonce> solution = new AtomicReference<>(null);

        // Precompute static header segments to minimize string allocations in tight mining loop
        String headerPrefix = templateBlock.index() + ":"
                + templateBlock.timestamp() + ":"
                + templateBlock.previousHash() + ":"
                + templateBlock.merkleRoot() + ":";
        String headerSuffix = ":" + templateBlock.difficultyBits();

        int targetDifficulty = templateBlock.difficultyBits();

        ExecutorService executor = (executionMode == ExecutionMode.VIRTUAL_THREADS)
                ? Executors.newVirtualThreadPerTaskExecutor()
                : Executors.newFixedThreadPool(workers);

        try {
            List<Future<?>> futures = new ArrayList<>(workers);

            for (int workerId = 0; workerId < workers; workerId++) {
                final int id = workerId;
                futures.add(executor.submit(() -> {
                    long localHashes = 0;

                    try {
                        while (!isSolved.get() && !Thread.currentThread().isInterrupted()) {
                            long chunkStart = nextNonceChunk.getAndAdd(DEFAULT_CHUNK_SIZE);
                            long chunkEnd = chunkStart + DEFAULT_CHUNK_SIZE;

                            for (long nonce = chunkStart; nonce < chunkEnd; nonce++) {
                                localHashes++;

                                // Periodic cancellation check every 256 nonces within a chunk
                                if ((nonce & 0xFF) == 0 && isSolved.get()) {
                                    break;
                                }

                                String header = headerPrefix + nonce + headerSuffix;
                                byte[] headerBytes = header.getBytes(StandardCharsets.UTF_8);
                                byte[] hashBytes = HashUtil.sha256(headerBytes);

                                if (TargetMatcher.matches(hashBytes, targetDifficulty)) {
                                    if (isSolved.compareAndSet(false, true)) {
                                        String winningHash = HashUtil.bytesToHex(hashBytes);
                                        solution.set(new WinningNonce(nonce, winningHash));
                                    }
                                    break;
                                }
                            }

                            if (isSolved.get()) {
                                break;
                            }
                        }
                    } finally {
                        totalHashesAdder.add(localHashes);
                    }
                }));
            }

            // Await completion of all worker tasks
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    throw new RuntimeException("Worker thread encountered an unexpected error", e.getCause());
                }
            }

        } finally {
            executor.shutdownNow();
            try {
                if (!executor.awaitTermination(2, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        long durationMs = System.currentTimeMillis() - startTime;
        WinningNonce result = solution.get();

        if (result != null) {
            Block minedBlock = templateBlock.withNonceAndHash(result.nonce, result.hash);
            return MiningResult.ofSuccess(minedBlock, durationMs, totalHashesAdder.sum(), workers);
        } else {
            return MiningResult.ofFailure(durationMs, totalHashesAdder.sum(), workers);
        }
    }

    private record WinningNonce(long nonce, String hash) {}
}
