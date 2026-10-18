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
 * Công cụ khai thác đồng thuận Proof-of-Work (PoW) đa luồng hiệu năng cao.
 * Tận dụng Java 21 Virtual Threads, phân chia không gian Nonce theo Atomic Chunks
 * và cơ chế hủy tức thì tất cả worker khi tìm thấy hash hợp lệ.
 *
 * Tác giả: Nguyễn Đỗ Trung Lương (MSSV: 2380601305)
 */
public class MultiThreadedMiner implements MiningService {

    public enum ExecutionMode {
        VIRTUAL_THREADS,
        PLATFORM_THREADS
    }

    // Kích thước mỗi chunk phân bổ nonce cho worker (4,096 nonces)
    private static final int DEFAULT_CHUNK_SIZE = 4096;
    private final ExecutionMode executionMode;
    private final int defaultThreadCount;

    /**
     * Khởi tạo miner mặc định với Java 21 Virtual Threads và số luồng bằng số nhân CPU.
     */
    public MultiThreadedMiner() {
        this(ExecutionMode.VIRTUAL_THREADS, Math.max(2, Runtime.getRuntime().availableProcessors()));
    }

    /**
     * Khởi tạo miner với chế độ thực thi và số luồng tùy chọn.
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

        // 1. Cờ hiệu nguyên tử báo hiệu đã tìm ra lời giải (Atomic Cancellation)
        AtomicBoolean isSolved = new AtomicBoolean(false);

        // 2. Con trỏ nguyên tử cấp phát dải Nonce cho từng luồng (Stride Chunking)
        AtomicLong nextNonceChunk = new AtomicLong(0L);

        // 3. Bộ đếm tổng số phép băm SHA-256 đã thực hiện trên tất cả các luồng
        LongAdder totalHashesAdder = new LongAdder();

        // 4. Lưu trữ kết quả Nonce và Hash chiến thắng
        AtomicReference<WinningNonce> solution = new AtomicReference<>(null);

        // 5. Tối ưu hóa chuỗi Header: Tính trước Prefix và Suffix để giảm thiểu cấp phát bộ nhớ trong vòng lặp đào
        String headerPrefix;
        String headerSuffix;
        if (!templateBlock.transactions().isEmpty()) {
            headerPrefix = templateBlock.index() + ":"
                    + templateBlock.timestamp() + ":"
                    + templateBlock.previousHash() + ":"
                    + templateBlock.merkleRoot() + ":";
            headerSuffix = ":" + templateBlock.difficultyBits();
        } else {
            headerPrefix = templateBlock.previousHash() + ":"
                    + templateBlock.timestamp() + ":"
                    + templateBlock.content() + ":";
            headerSuffix = "";
        }

        int targetDifficulty = templateBlock.difficultyBits();

        // 6. Khởi tạo ExecutorService: Java 21 Virtual Threads hoặc Fixed Platform Thread Pool
        ExecutorService executor = (executionMode == ExecutionMode.VIRTUAL_THREADS)
                ? Executors.newVirtualThreadPerTaskExecutor()
                : Executors.newFixedThreadPool(workers);

        try {
            List<Future<?>> futures = new ArrayList<>(workers);

            // 7. Khởi chạy song song các mining worker
            for (int workerId = 0; workerId < workers; workerId++) {
                futures.add(executor.submit(() -> {
                    long localHashes = 0;

                    try {
                        while (!isSolved.get() && !Thread.currentThread().isInterrupted()) {
                            // Lấy một khối Nonce độc quyền cho worker hiện tại
                            long chunkStart = nextNonceChunk.getAndAdd(DEFAULT_CHUNK_SIZE);
                            long chunkEnd = chunkStart + DEFAULT_CHUNK_SIZE;

                            for (long nonce = chunkStart; nonce < chunkEnd; nonce++) {
                                localHashes++;

                                // Kiểm tra cờ hủy định kỳ mỗi 256 lần lặp để phản ứng nhanh khi có luồng khác thắng
                                if ((nonce & 0xFF) == 0 && isSolved.get()) {
                                    break;
                                }

                                String header = headerPrefix + nonce + headerSuffix;
                                byte[] headerBytes = header.getBytes(StandardCharsets.UTF_8);
                                byte[] hashBytes = HashUtil.sha256(headerBytes);

                                // Kiểm tra độ khó PoW (Leading zero bits)
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

            // 8. Chờ tất cả các worker dừng lại sau khi tìm thấy kết quả
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    throw new RuntimeException("Lỗi worker đào PoW: " + e.getMessage(), e.getCause());
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

    /**
     * Trả về mô hình thực thi luồng hiện tại (Virtual Threads hoặc Platform Threads).
     */
    public ExecutionMode getExecutionMode() {
        return executionMode;
    }

    /**
     * Trả về số lượng luồng worker mặc định được phân bổ cho miner.
     */
    public int getDefaultThreadCount() {
        return defaultThreadCount;
    }

    /**
     * Trả về kích thước chunk phân bổ nonce atomic giữa các worker.
     */
    public static int getDefaultChunkSize() {
        return DEFAULT_CHUNK_SIZE;
    }

    private record WinningNonce(long nonce, String hash) {}
}
