package com.pow.simulator.core;

import com.pow.simulator.consensus.TargetMatcher;
import com.pow.simulator.crypto.HashUtil;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Khối Blockchain Bầu Cử với đúng 5 thuộc tính chuẩn:
 * 1. hash: Mã băm SHA-256 của khối
 * 2. timestamp (time): Thời gian tạo khối
 * 3. previousHash (pre hash): Mã băm khối trước đó
 * 4. content (nội dung): Nội dung thông tin các phiếu bầu cử
 * 5. nonce: Số ngẫu nhiên tìm được qua Proof-of-Work
 */
public record Block(
        String hash,
        long timestamp,
        String previousHash,
        String content,
        long nonce
) {
    record BlockMeta(int index, int difficultyBits, List<Transaction> transactions, String customMerkleRoot) {}
    private static final Map<String, BlockMeta> META_REGISTRY = new ConcurrentHashMap<>();

    private static String getMetaKey(String h, String prev, long ts, String c) {
        return prev + ":" + ts + ":" + c;
    }

    public static void registerBlockIndex(Block block, int index) {
        if (block == null) return;
        String key = getMetaKey(block.hash(), block.previousHash(), block.timestamp(), block.content());
        BlockMeta old = META_REGISTRY.get(key);
        if (old == null && block.hash() != null && !block.hash().isBlank()) {
            old = META_REGISTRY.get(block.hash());
        }
        int diff = (old != null) ? old.difficultyBits() : block.difficultyBits();
        List<Transaction> txs = (old != null) ? old.transactions() : block.transactions();
        String root = (old != null) ? old.customMerkleRoot() : block.merkleRoot();
        BlockMeta updated = new BlockMeta(index, diff, txs, root);
        META_REGISTRY.put(key, updated);
        if (block.hash() != null && !block.hash().isBlank()) {
            META_REGISTRY.put(block.hash(), updated);
        }
    }

    public Block {
        Objects.requireNonNull(previousHash, "previousHash không được để trống");
        content = (content == null) ? "" : content;
        if (hash == null || hash.isBlank()) {
            hash = calculateHash(previousHash, timestamp, content, nonce);
        }
    }

    /**
     * Tính toán mã băm SHA-256 từ 4 thuộc tính: preHash + time + nội dung + nonce.
     */
    public static String calculateHash(String previousHash, long timestamp, String content, long nonce) {
        String data = previousHash + ":" + timestamp + ":" + content + ":" + nonce;
        return HashUtil.sha256Hex(data);
    }

    public static Block createGenesisBlock() {
        return createGenesisBlock(1700000000000L);
    }

    public static Block createGenesisBlock(int initialDifficultyBits) {
        return createGenesisBlock(initialDifficultyBits, 1700000000000L);
    }

    public static Block createGenesisBlock(long timestamp) {
        return createGenesisBlock(5, timestamp);
    }

    public static Block createGenesisBlock(int initialDifficultyBits, long timestamp) {
        String preHash = "0".repeat(64);
        String content = "Khởi tạo hệ thống (0 phiếu)";
        long nonce = 0L;
        int targetBits = Math.max(4, initialDifficultyBits);
        while (true) {
            String hash = calculateHash(preHash, timestamp, content, nonce);
            if (TargetMatcher.matches(hash, targetBits) || (targetBits <= 20 && hash.startsWith("00000"))) {
                Block genesis = new Block(hash, timestamp, preHash, content, nonce);
                BlockMeta meta = new BlockMeta(0, targetBits, List.of(), MerkleTree.calculateRoot(List.of()));
                META_REGISTRY.put(hash, meta);
                META_REGISTRY.put(getMetaKey(hash, preHash, timestamp, content), meta);
                return genesis;
            }
            nonce++;
        }
    }

    // Các hàm alias tiện dụng theo yêu cầu đề bài: hash, time, pre hash, nội dung, nonce
    public String preHash() {
        return previousHash;
    }

    public String noiDung() {
        return content;
    }

    public String time() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault())
                .format(Instant.ofEpochMilli(timestamp));
    }

    public String getShortHash() {
        if (hash == null || hash.length() < 12) return hash;
        return hash.substring(0, 8) + "..." + hash.substring(hash.length() - 6);
    }

    public boolean isValid(int leadingZeros) {
        String target = "0".repeat(Math.max(0, leadingZeros));
        if (!hash.startsWith(target)) return false;
        String expected = calculateHash(previousHash, timestamp, content, nonce);
        return hash.equals(expected);
    }

    // Backward-compatibility constructor
    public Block(int index, long timestamp, String previousHash, String merkleRoot, List<Transaction> txs, long nonce, int difficultyBits, String hash) {
        this(hash, timestamp, previousHash, buildContentFromTxs(txs, merkleRoot), nonce);
        List<Transaction> safeTxs = (txs != null) ? List.copyOf(txs) : List.of();
        String root = (merkleRoot != null && !merkleRoot.isBlank()) ? merkleRoot : MerkleTree.calculateRoot(safeTxs);
        BlockMeta meta = new BlockMeta(index, difficultyBits, safeTxs, root);
        String key = getMetaKey(this.hash(), previousHash, timestamp, this.content());
        META_REGISTRY.put(key, meta);
        if (this.hash() != null && !this.hash().isBlank()) {
            META_REGISTRY.put(this.hash(), meta);
        }
    }

    private static String buildContentFromTxs(List<Transaction> txs, String fallback) {
        if (txs != null && !txs.isEmpty()) {
            StringBuilder sb = new StringBuilder("TXS:");
            for (int i = 0; i < txs.size(); i++) {
                if (i > 0) sb.append(";;;");
                Transaction tx = txs.get(i);
                sb.append(tx.txId()).append("#")
                  .append(tx.sender()).append("#")
                  .append(tx.recipient()).append("#")
                  .append(tx.amount()).append("#")
                  .append(tx.timestamp()).append("#")
                  .append(tx.signature()).append("#")
                  .append(tx.publicKey());
            }
            return sb.toString();
        }
        return (fallback != null) ? fallback : "";
    }

    public int index() {
        BlockMeta meta = META_REGISTRY.get(getMetaKey(hash, previousHash, timestamp, content));
        if (meta == null && hash != null && !hash.isBlank()) {
            meta = META_REGISTRY.get(hash);
        }
        return (meta != null) ? meta.index() : 0;
    }

    public int difficultyBits() {
        BlockMeta meta = META_REGISTRY.get(getMetaKey(hash, previousHash, timestamp, content));
        if (meta == null && hash != null && !hash.isBlank()) {
            meta = META_REGISTRY.get(hash);
        }
        if (meta != null && meta.difficultyBits() > 0) {
            return meta.difficultyBits();
        }
        if (hash != null && !hash.isBlank()) {
            int zeros = HashUtil.countLeadingZeroBits(hash);
            if (zeros > 0) return zeros;
        }
        return 8;
    }

    public String merkleRoot() {
        BlockMeta meta = META_REGISTRY.get(getMetaKey(hash, previousHash, timestamp, content));
        if (meta == null && hash != null && !hash.isBlank()) {
            meta = META_REGISTRY.get(hash);
        }
        if (meta != null && meta.customMerkleRoot() != null && !meta.customMerkleRoot().isBlank()) {
            return meta.customMerkleRoot();
        }
        List<Transaction> txs = transactions();
        if (!txs.isEmpty()) {
            return MerkleTree.calculateRoot(txs);
        }
        return HashUtil.sha256Hex(content);
    }

    public List<Transaction> transactions() {
        BlockMeta meta = META_REGISTRY.get(getMetaKey(hash, previousHash, timestamp, content));
        if (meta == null && hash != null && !hash.isBlank()) {
            meta = META_REGISTRY.get(hash);
        }
        if (meta != null && meta.transactions() != null && !meta.transactions().isEmpty()) {
            return meta.transactions();
        }
        if (content != null && content.startsWith("TXS:")) {
            List<Transaction> list = new ArrayList<>();
            String rawTxs = content.substring(4);
            for (String part : rawTxs.split(";;;")) {
                if (!part.isBlank()) {
                    String[] p = part.split("#", -1);
                    if (p.length >= 7) {
                        list.add(new Transaction(p[0], p[1], p[2], Double.parseDouble(p[3]), Long.parseLong(p[4]), p[5], p[6]));
                    } else if (p.length >= 5) {
                        list.add(new Transaction(p[0], p[1], p[2], Double.parseDouble(p[3]), Long.parseLong(p[4]), "", ""));
                    }
                }
            }
            return Collections.unmodifiableList(list);
        }
        return List.of();
    }

    public String buildHeader(long candidateNonce) {
        return previousHash + ":" + timestamp + ":" + content + ":" + candidateNonce;
    }

    public byte[] buildHeaderBytes(long candidateNonce) {
        return buildHeader(candidateNonce).getBytes(StandardCharsets.UTF_8);
    }

    public String calculateHash(long candidateNonce) {
        return calculateHash(previousHash, timestamp, content, candidateNonce);
    }

    public static Block createTemplate(int index, long timestamp, String previousHash, List<Transaction> txs, int diff) {
        List<Transaction> safeTxs = (txs != null) ? List.copyOf(txs) : List.of();
        String content = buildContentFromTxs(safeTxs, "");
        Block b = new Block("", timestamp, previousHash, content, 0L);
        BlockMeta meta = new BlockMeta(index, diff, safeTxs, MerkleTree.calculateRoot(safeTxs));
        String key = getMetaKey(b.hash(), previousHash, timestamp, content);
        META_REGISTRY.put(key, meta);
        META_REGISTRY.put(b.hash(), meta);
        return b;
    }

    public Block withNonceAndHash(long winningNonce, String validHash) {
        String key = getMetaKey(hash, previousHash, timestamp, content);
        BlockMeta old = META_REGISTRY.get(key);
        if (old == null && hash != null && !hash.isBlank()) {
            old = META_REGISTRY.get(hash);
        }
        Block b = new Block(validHash, timestamp, previousHash, content, winningNonce);
        if (old != null) {
            META_REGISTRY.put(validHash, old);
            META_REGISTRY.put(key, old);
        } else {
            int diff = Math.max(1, HashUtil.countLeadingZeroBits(validHash));
            BlockMeta fallback = new BlockMeta(0, diff, List.of(), MerkleTree.calculateRoot(List.of()));
            META_REGISTRY.put(validHash, fallback);
            META_REGISTRY.put(key, fallback);
        }
        return b;
    }

    public String toNetworkString() {
        return timestamp + "\t" + previousHash + "\t" + nonce + "\t" + hash + "\t" + content.replace("\n", " ");
    }

    public static Block fromNetworkString(String raw) {
        String[] parts = raw.split("\t", 5);
        if (parts.length >= 5) {
            long ts = Long.parseLong(parts[0]);
            String prev = parts[1];
            long n = Long.parseLong(parts[2]);
            String h = parts[3];
            String c = parts[4];
            Block block = new Block(h, ts, prev, c, n);
            List<Transaction> txs = block.transactions();
            int diff = Math.max(1, HashUtil.countLeadingZeroBits(h));
            BlockMeta meta = new BlockMeta(0, diff, txs, MerkleTree.calculateRoot(txs));
            META_REGISTRY.put(h, meta);
            META_REGISTRY.put(getMetaKey(h, prev, ts, c), meta);
            return block;
        }
        return createGenesisBlock();
    }

    @Override
    public String toString() {
        return String.format("Block [Hash: %s, Time: %s, PreHash: %s, Content: %s, Nonce: %d]",
                getShortHash(), time(), (previousHash.length() > 8 ? previousHash.substring(0, 8) + "..." : previousHash), content, nonce);
    }
}
