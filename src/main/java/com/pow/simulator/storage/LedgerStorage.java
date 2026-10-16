package com.pow.simulator.storage;

import com.pow.simulator.core.Block;
import com.pow.simulator.core.Blockchain;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Enterprise Disk Persistence Manager for the Blockchain Ledger.
 * Ensures blocks, transactions, and balances survive application restarts.
 */
public class LedgerStorage {

    private final Path storageDir;
    private final Path chainFile;

    public LedgerStorage(Path storageDir) {
        this.storageDir = storageDir;
        this.chainFile = storageDir.resolve("chain_data.json");
        try {
            Files.createDirectories(storageDir);
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize storage directory: " + storageDir, e);
        }
    }

    public static LedgerStorage defaultInstance() {
        return new LedgerStorage(Paths.get("data", "ledger"));
    }

    /**
     * Checks if a persistent chain already exists on disk.
     */
    public boolean hasSavedChain() {
        return Files.exists(chainFile) && (getChainFileSize() > 0);
    }

    /**
     * Atomically writes the entire blockchain ledger to disk.
     */
    public synchronized void saveChain(Blockchain blockchain) throws IOException {
        String serialized = blockchain.serializeChain();
        Path tempFile = storageDir.resolve("chain_data.tmp");
        Files.writeString(tempFile, serialized, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        Files.move(tempFile, chainFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
    }

    /**
     * Loads and reconstructs the blockchain ledger from disk.
     *
     * @return restored Blockchain or null if no persistent data exists
     */
    public synchronized Blockchain loadChain() {
        if (!hasSavedChain()) {
            return null;
        }
        try {
            String raw = Files.readString(chainFile, StandardCharsets.UTF_8);
            List<Block> blocks = Blockchain.deserializeBlocks(raw);
            if (blocks.isEmpty()) {
                return null;
            }
            // Initialize blockchain with first block, then append rest
            Blockchain chain = new Blockchain(blocks.get(0));
            for (int i = 1; i < blocks.size(); i++) {
                chain.addBlock(blocks.get(i));
            }
            return chain;
        } catch (Exception e) {
            System.err.println("⚠️ Warning: Failed to read persistent chain from disk: " + e.getMessage());
            return null;
        }
    }

    public long getChainFileSize() {
        try {
            return Files.exists(chainFile) ? Files.size(chainFile) : 0L;
        } catch (IOException e) {
            return 0L;
        }
    }

    public String getLastModifiedFormatted() {
        try {
            if (Files.exists(chainFile)) {
                Instant instant = Files.getLastModifiedTime(chainFile).toInstant();
                return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        .withZone(ZoneId.systemDefault())
                        .format(instant);
            }
        } catch (IOException ignored) {}
        return "Chưa lưu";
    }

    public Path getChainFile() {
        return chainFile;
    }
}
