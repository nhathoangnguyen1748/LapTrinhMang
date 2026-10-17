package com.pow.simulator.core;

import com.pow.simulator.crypto.HashUtil;
import com.pow.simulator.crypto.Wallet;

import java.util.Objects;

/**
 * Immutable transaction record representing a transfer of value between two parties.
 * Supports ECDSA digital cryptographic signatures for real decentralized authentication.
 *
 * @param txId      cryptographic transaction identifier (SHA-256)
 * @param sender    sender public address or identity
 * @param recipient recipient public address or identity
 * @param amount    quantity transferred
 * @param timestamp epoch millisecond when transaction was created
 * @param signature hex-encoded cryptographic signature (optional for genesis/simulations)
 * @param publicKey hex-encoded public key used to verify the signature
 */
public record Transaction(
        String txId,
        String sender,
        String recipient,
        double amount,
        long timestamp,
        String signature,
        String publicKey
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
        if (signature == null) signature = "";
        if (publicKey == null) publicKey = "";
    }

    /**
     * Backward-compatible 5-argument constructor for existing code and tests.
     */
    public Transaction(String txId, String sender, String recipient, double amount, long timestamp) {
        this(txId, sender, recipient, amount, timestamp, "", "");
    }

    /**
     * Factory constructor generating transaction with current timestamp.
     */
    public static Transaction create(String sender, String recipient, double amount) {
        long now = System.currentTimeMillis();
        String id = HashUtil.sha256Hex(sender + ":" + recipient + ":" + amount + ":" + now);
        return new Transaction(id, sender, recipient, amount, now, "", "");
    }

    /**
     * Factory constructor creating a cryptographically signed transaction using an ECDSA Wallet.
     */
    public static Transaction createSigned(Wallet senderWallet, String recipient, double amount) {
        Objects.requireNonNull(senderWallet, "Sender wallet must not be null");
        long now = System.currentTimeMillis();
        String senderAddress = senderWallet.getAddress();
        String payload = senderAddress + ":" + recipient + ":" + amount + ":" + now;
        String id = HashUtil.sha256Hex(payload);
        String signature = senderWallet.sign(payload);
        String pubKeyHex = senderWallet.getPublicKeyHex();
        return new Transaction(id, senderAddress, recipient, amount, now, signature, pubKeyHex);
    }

    /**
     * Returns the payload string used for cryptographic signing and verification.
     */
    public String getSigningPayload() {
        return sender + ":" + recipient + ":" + amount + ":" + timestamp;
    }

    /**
     * Verifies the cryptographic signature of this transaction.
     * Special system accounts (COINBASE, GENESIS) bypass signatures.
     */
    public boolean isSignatureValid() {
        if ("COINBASE".equalsIgnoreCase(sender) || "GENESIS".equalsIgnoreCase(sender)) {
            return true;
        }
        if (signature.isBlank() || publicKey.isBlank()) {
            // Unsigned legacy simulation transaction
            return true;
        }
        return Wallet.verify(publicKey, getSigningPayload(), signature);
    }

    /**
     * Serializes this transaction into a clean tab-delimited string for network transport.
     */
    public String toNetworkString() {
        return txId + "\t" + sender + "\t" + recipient + "\t" + amount + "\t" + timestamp
                + "\t" + signature + "\t" + publicKey;
    }

    /**
     * Deserializes a transaction from a network string with backward compatibility.
     */
    public static Transaction fromNetworkString(String raw) {
        String[] parts = raw.split("\t", -1);
        if (parts.length >= 7) {
            return new Transaction(
                    parts[0], parts[1], parts[2],
                    Double.parseDouble(parts[3]), Long.parseLong(parts[4]),
                    parts[5], parts[6]
            );
        }
        if (parts.length == 5) {
            return new Transaction(
                    parts[0], parts[1], parts[2],
                    Double.parseDouble(parts[3]), Long.parseLong(parts[4]),
                    "", ""
            );
        }
        throw new IllegalArgumentException("Invalid transaction network string: " + raw);
    }

    @Override
    public String toString() {
        String sigIndicator = signature.isBlank() ? "" : " [ECDSA Signed]";
        return String.format("Tx[%s: %s -> %s | %.4f BTC%s]",
                txId.substring(0, Math.min(8, txId.length())), sender, recipient, amount, sigIndicator);
    }
}
