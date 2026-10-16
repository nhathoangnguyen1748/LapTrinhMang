package com.pow.simulator.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Thread-safe container and manager for the sequence of blocks in the blockchain ledger.
 */
public class Blockchain {

    private final List<Block> chain;

    /**
     * Initializes an empty blockchain with the provided genesis block.
     *
     * @param genesisBlock first block in the chain
     */
    public Blockchain(Block genesisBlock) {
        Objects.requireNonNull(genesisBlock, "Genesis block must not be null");
        this.chain = new CopyOnWriteArrayList<>();
        this.chain.add(genesisBlock);
    }

    /**
     * Private constructor for cloning blockchain instances.
     */
    private Blockchain(List<Block> existingChain) {
        this.chain = new CopyOnWriteArrayList<>(existingChain);
    }

    /**
     * Factory constructor creating a standard blockchain with default genesis block.
     *
     * @param initialDifficultyBits difficulty bits for genesis
     * @return freshly initialized Blockchain
     */
    public static Blockchain withDefaultGenesis(int initialDifficultyBits) {
        Block genesis = Block.createGenesisBlock(initialDifficultyBits);
        return new Blockchain(genesis);
    }

    /**
     * Factory constructor creating a blockchain with default genesis block and specific timestamp.
     *
     * @param initialDifficultyBits difficulty bits for genesis
     * @param timestamp             epoch milliseconds for genesis block
     * @return freshly initialized Blockchain
     */
    public static Blockchain withDefaultGenesis(int initialDifficultyBits, long timestamp) {
        Block genesis = Block.createGenesisBlock(initialDifficultyBits, timestamp);
        return new Blockchain(genesis);
    }

    /**
     * Adds a newly mined block to the head of the chain.
     *
     * @param block mined block
     * @throws IllegalArgumentException if block does not link to the current latest block
     */
    public synchronized void addBlock(Block block) {
        Objects.requireNonNull(block, "Block to add must not be null");
        Block latest = getLatestBlock();
        if (block.index() != latest.index() + 1) {
            throw new IllegalArgumentException(String.format(
                    "Invalid block index: expected %d, got %d", latest.index() + 1, block.index()));
        }
        if (!block.previousHash().equals(latest.hash())) {
            throw new IllegalArgumentException(String.format(
                    "Hash linkage mismatch: block previousHash %s does not match latest hash %s",
                    block.previousHash(), latest.hash()));
        }
        chain.add(block);
    }

    /**
     * Retrieves the most recently added block at the tip of the chain.
     */
    public Block getLatestBlock() {
        if (chain.isEmpty()) {
            throw new IllegalStateException("Blockchain is empty");
        }
        return chain.get(chain.size() - 1);
    }

    /**
     * Retrieves a block at a specific zero-based index.
     */
    public Block getBlock(int index) {
        if (index < 0 || index >= chain.size()) {
            throw new IndexOutOfBoundsException("Block index out of bounds: " + index);
        }
        return chain.get(index);
    }

    /**
     * Returns total number of blocks in the chain.
     */
    public int size() {
        return chain.size();
    }

    /**
     * Returns an unmodifiable snapshot view of the block sequence.
     */
    public List<Block> getChain() {
        return Collections.unmodifiableList(new ArrayList<>(chain));
    }

    /**
     * Creates an independent deep copy of this blockchain.
     * Crucial for branch exploration and attack simulation without corrupting valid state.
     */
    public Blockchain copy() {
        return new Blockchain(new ArrayList<>(this.chain));
    }

    /**
     * Replaces the local chain with a valid, longer chain (Nakamoto consensus rule).
     */
    public synchronized boolean replaceChain(List<Block> newChain) {
        if (newChain == null || newChain.size() <= this.chain.size()) {
            return false;
        }
        this.chain.clear();
        this.chain.addAll(newChain);
        return true;
    }

    /**
     * Serializes the entire sequence of blocks for network transmission.
     */
    public synchronized String serializeChain() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chain.size(); i++) {
            sb.append(chain.get(i).toNetworkString());
            if (i < chain.size() - 1) {
                sb.append("\n---BLOCK_DELIMITER---\n");
            }
        }
        return sb.toString();
    }

    /**
     * Deserializes a full blockchain from network string representation.
     */
    public static List<Block> deserializeBlocks(String raw) {
        if (raw == null || raw.isBlank()) {
            return List.of();
        }
        String[] blockChunks = raw.split("\n---BLOCK_DELIMITER---\n");
        List<Block> parsedBlocks = new ArrayList<>();
        for (String chunk : blockChunks) {
            String trimmed = chunk.trim();
            if (!trimmed.isEmpty()) {
                parsedBlocks.add(Block.fromNetworkString(trimmed));
            }
        }
        return parsedBlocks;
    }

    /**
     * Replaces a block at an index with a modified block (used specifically by attack simulators).
     */
    public synchronized void setBlockForSimulation(int index, Block modifiedBlock) {
        chain.set(index, modifiedBlock);
    }

    /**
     * Calculates the confirmed balance for any address across all blocks in the chain.
     */
    public synchronized double getBalance(String address) {
        if (address == null || address.isBlank()) return 0.0;
        double balance = 0.0;
        for (Block b : chain) {
            for (Transaction tx : b.transactions()) {
                if (address.equalsIgnoreCase(tx.recipient())) {
                    balance += tx.amount();
                }
                if (address.equalsIgnoreCase(tx.sender())) {
                    balance -= tx.amount();
                }
            }
        }
        return Math.max(0.0, balance);
    }
}

