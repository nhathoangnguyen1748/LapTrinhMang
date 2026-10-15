package com.pow.simulator.consensus;

import com.pow.simulator.core.Block;

/**
 * Immutable metrics and outcome record of a Proof-of-Work mining run.
 *
 * @param minedBlock  the successfully mined block with target hash and winning nonce
 * @param durationMs  elapsed mining time in milliseconds
 * @param totalHashes total number of SHA-256 operations executed across all threads
 * @param hashrate    effective hash rate in hashes per second (H/s)
 * @param threadsUsed number of concurrent worker threads employed
 * @param success     whether a valid block was successfully found
 */
public record MiningResult(
        Block minedBlock,
        long durationMs,
        long totalHashes,
        double hashrate,
        int threadsUsed,
        boolean success
) {
    public MiningResult {
        if (durationMs < 0) {
            throw new IllegalArgumentException("durationMs cannot be negative: " + durationMs);
        }
        if (totalHashes < 0) {
            throw new IllegalArgumentException("totalHashes cannot be negative: " + totalHashes);
        }
    }

    /**
     * Factory method creating a successful mining result and computing hashrate.
     */
    public static MiningResult ofSuccess(
            Block minedBlock,
            long durationMs,
            long totalHashes,
            int threadsUsed
    ) {
        double seconds = Math.max(0.001, durationMs / 1000.0);
        double rate = totalHashes / seconds;
        return new MiningResult(minedBlock, durationMs, totalHashes, rate, threadsUsed, true);
    }

    /**
     * Factory method for aborted or timed-out mining.
     */
    public static MiningResult ofFailure(long durationMs, long totalHashes, int threadsUsed) {
        double seconds = Math.max(0.001, durationMs / 1000.0);
        double rate = totalHashes / seconds;
        return new MiningResult(null, durationMs, totalHashes, rate, threadsUsed, false);
    }

    /**
     * Formats hashrate into human-friendly H/s, kH/s, or MH/s.
     */
    public String getFormattedHashrate() {
        if (hashrate >= 1_000_000.0) {
            return String.format("%.2f MH/s", hashrate / 1_000_000.0);
        } else if (hashrate >= 1_000.0) {
            return String.format("%.2f kH/s", hashrate / 1_000.0);
        } else {
            return String.format("%.2f H/s", hashrate);
        }
    }
}
