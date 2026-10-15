package com.pow.simulator.core;

import com.pow.simulator.crypto.HashUtil;

import java.util.Objects;

/**
 * Immutable transaction record representing a transfer of value between two parties.
 *
 * @param txId      cryptographic transaction identifier (SHA-256)
 * @param sender    sender public address or identity
 * @param recipient recipient public address or identity
 * @param amount    quantity transferred
 * @param timestamp epoch millisecond when transaction was signed/created
 */
public record Transaction(
        String txId,
        String sender,
        String recipient,
        double amount,
        long timestamp
) {
    public Transaction {
        Objects.requireNonNull(sender, "Sender must not be null");
        Objects.requireNonNull(recipient, "Recipient must not be null");
        if (amount <= 0) {
            throw new IllegalArgumentException("Transaction amount must be strictly positive: " + amount);
        }
        if (txId == null || txId.isBlank()) {
            txId = HashUtil.sha256Hex(sender + ":" + recipient + ":" + amount + ":" + timestamp);
        }
    }

    /**
     * Factory constructor generating transaction with current timestamp.
     */
    public static Transaction create(String sender, String recipient, double amount) {
        long now = System.currentTimeMillis();
        String id = HashUtil.sha256Hex(sender + ":" + recipient + ":" + amount + ":" + now);
        return new Transaction(id, sender, recipient, amount, now);
    }

    @Override
    public String toString() {
        return String.format("Tx[%s: %s -> %s | %.4f BTC]",
                txId.substring(0, Math.min(8, txId.length())), sender, recipient, amount);
    }
}
