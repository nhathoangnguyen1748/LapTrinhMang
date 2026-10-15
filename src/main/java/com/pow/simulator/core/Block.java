package com.pow.simulator.core;

import com.pow.simulator.crypto.HashUtil;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Immutable representation of a Block in the PoW blockchain ledger.
 *
 * @param index          zero-based position in the blockchain
 * @param timestamp      Unix epoch millisecond of block creation
 * @param previousHash   64-character SHA-256 hex hash of preceding block
 * @param merkleRoot     64-character SHA-256 Merkle root of transaction set
 * @param transactions   immutable list of transactions included in block
 * @param nonce          arbitrary 64-bit integer adjusted during Proof-of-Work
 * @param difficultyBits target difficulty represented in leading zero bits
 * @param hash           64-character SHA-256 hex hash of this block's header
 */
public record Block(
        int index,
        long timestamp,
        String previousHash,
        String merkleRoot,
        List<Transaction> transactions,
        long nonce,
        int difficultyBits,
        String hash
) {
    public Block {
        Objects.requireNonNull(previousHash, "previousHash must not be null");
        Objects.requireNonNull(merkleRoot, "merkleRoot must not be null");
        transactions = (transactions == null) ? List.of() : List.copyOf(transactions);
        if (difficultyBits < 0) {
            throw new IllegalArgumentException("difficultyBits must be non-negative: " + difficultyBits);
        }
    }

    /**
     * Factory constructor for creating a template block prior to mining.
     */
    public static Block createTemplate(
            int index,
            long timestamp,
            String previousHash,
            List<Transaction> transactions,
            int difficultyBits
    ) {
        String merkleRoot = MerkleTree.calculateRoot(transactions);
        return new Block(
                index,
                timestamp,
                previousHash,
                merkleRoot,
                transactions,
                0L,
                difficultyBits,
                ""
        );
    }

    public static final long DEFAULT_GENESIS_TIMESTAMP = 1700000000000L;

    /**
     * Factory constructor for creating the Genesis block with reproducible fixed timestamp.
     * Guarantees identical genesis block hashes across distributed network nodes.
     */
    public static Block createGenesisBlock(int initialDifficultyBits) {
        return createGenesisBlock(initialDifficultyBits, DEFAULT_GENESIS_TIMESTAMP);
    }

    /**
     * Factory constructor for creating the Genesis block with custom timestamp.
     */
    public static Block createGenesisBlock(int initialDifficultyBits, long timestamp) {
        List<Transaction> genesisTxs = List.of(
                new Transaction("genesis-tx-0", "COINBASE", "Satoshi", 50.0, timestamp)
        );
        String merkleRoot = MerkleTree.calculateRoot(genesisTxs);
        Block template = new Block(0, timestamp, "0".repeat(64), merkleRoot, genesisTxs, 0L, initialDifficultyBits, "");
        String initialHash = template.calculateHash(0L);
        return template.withNonceAndHash(0L, initialHash);
    }

    /**
     * Formats the block header representation for cryptographic hashing.
     */
    public String buildHeader(long candidateNonce) {
        return index + ":" + timestamp + ":" + previousHash + ":" + merkleRoot + ":" + candidateNonce + ":" + difficultyBits;
    }

    /**
     * Computes raw UTF-8 bytes of header for high-speed mining loops.
     */
    public byte[] buildHeaderBytes(long candidateNonce) {
        return buildHeader(candidateNonce).getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Computes the SHA-256 hex hash for a candidate nonce.
     */
    public String calculateHash(long candidateNonce) {
        return HashUtil.sha256Hex(buildHeader(candidateNonce));
    }

    /**
     * Computes the raw 32-byte SHA-256 hash for a candidate nonce.
     */
    public byte[] calculateHashBytes(long candidateNonce) {
        return HashUtil.sha256(buildHeaderBytes(candidateNonce));
    }

    /**
     * Produces an immutable copy of this block with the winning nonce and valid hash.
     */
    public Block withNonceAndHash(long winningNonce, String validHash) {
        return new Block(
                index,
                timestamp,
                previousHash,
                merkleRoot,
                transactions,
                winningNonce,
                difficultyBits,
                validHash
        );
    }

    /**
     * Returns a truncated representation of the block hash for clean logging.
     */
    public String getShortHash() {
        if (hash == null || hash.length() < 12) {
            return hash;
        }
        return hash.substring(0, 8) + "..." + hash.substring(hash.length() - 6);
    }

    /**
     * Serializes this block and its transactions into a network-safe string format.
     */
    public String toNetworkString() {
        StringBuilder sb = new StringBuilder();
        sb.append(index).append("\t")
          .append(timestamp).append("\t")
          .append(previousHash).append("\t")
          .append(merkleRoot).append("\t")
          .append(nonce).append("\t")
          .append(difficultyBits).append("\t")
          .append(hash).append("\n");
        for (Transaction tx : transactions) {
            sb.append(tx.toNetworkString()).append("\n");
        }
        return sb.toString().trim();
    }

    /**
     * Deserializes a block and its transactions from a network string format.
     */
    public static Block fromNetworkString(String raw) {
        String[] lines = raw.split("\n");
        String[] header = lines[0].trim().split("\t", -1);
        int index = Integer.parseInt(header[0]);
        long timestamp = Long.parseLong(header[1]);
        String previousHash = header[2];
        String merkleRoot = header[3];
        long nonce = Long.parseLong(header[4]);
        int difficultyBits = Integer.parseInt(header[5]);
        String hash = header[6];

        List<Transaction> txs = new ArrayList<>();
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].trim();
            if (!line.isEmpty()) {
                txs.add(Transaction.fromNetworkString(line));
            }
        }
        return new Block(index, timestamp, previousHash, merkleRoot, txs, nonce, difficultyBits, hash);
    }

    @Override
    public String toString() {
        return String.format("Block #%d [Nonce: %d, Diff: %d bits, Hash: %s, Txs: %d]",
                index, nonce, difficultyBits, getShortHash(), transactions.size());
    }
}
