package com.pow.simulator.benchmark;

import java.util.concurrent.TimeUnit;

/**
 * Performance profiler tracking mining durations, hash execution counts, and hashrate metrics.
 */
public class PerformanceMeter {

    private long startTimeNanos;
    private long endTimeNanos;
    private long hashesEvaluated;

    public void start() {
        this.startTimeNanos = System.nanoTime();
        this.endTimeNanos = 0;
        this.hashesEvaluated = 0;
    }

    public void stop(long totalHashes) {
        this.endTimeNanos = System.nanoTime();
        this.hashesEvaluated = totalHashes;
    }

    public long getElapsedMillis() {
        long end = (endTimeNanos > 0) ? endTimeNanos : System.nanoTime();
        return TimeUnit.NANOSECONDS.toMillis(end - startTimeNanos);
    }

    public double getHashrate() {
        long elapsedMs = Math.max(1, getElapsedMillis());
        return (hashesEvaluated / (elapsedMs / 1000.0));
    }

    public String formatHashrate() {
        double rate = getHashrate();
        if (rate >= 1_000_000.0) {
            return String.format("%.2f MH/s", rate / 1_000_000.0);
        } else if (rate >= 1_000.0) {
            return String.format("%.2f kH/s", rate / 1_000.0);
        } else {
            return String.format("%.2f H/s", rate);
        }
    }
}
