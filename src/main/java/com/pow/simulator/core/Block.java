package com.pow.simulator.core;

import com.pow.simulator.crypto.HashUtil;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
        return createGenesisBlock(1700000000000L);
    }

    public static Block createGenesisBlock(int initialDifficultyBits, long timestamp) {
        return createGenesisBlock(timestamp);
    }

    public static Block createGenesisBlock(long timestamp) {
        String preHash = "0".repeat(64);
        String content = "Khởi tạo hệ thống (0 phiếu)";
        long nonce = 0L;
        while (true) {
            String hash = calculateHash(preHash, timestamp, content, nonce);
            if (hash.startsWith("00000")) {
                return new Block(hash, timestamp, preHash, content, nonce);
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
        this(hash, timestamp, previousHash, (merkleRoot != null && !merkleRoot.isBlank() ? merkleRoot : (txs != null ? txs.toString() : "")), nonce);
    }

    public int index() {
        return 0;
    }

    public int difficultyBits() {
        return 8;
    }

    public String merkleRoot() {
        return HashUtil.sha256Hex(content);
    }

    public List<Transaction> transactions() {
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
        StringBuilder sb = new StringBuilder();
        if (txs != null) {
            for (Transaction tx : txs) {
                if (!sb.isEmpty()) sb.append(" | ");
                sb.append(tx.sender()).append(" bầu cho ").append(tx.recipient());
            }
        }
        return new Block("", timestamp, previousHash, sb.toString(), 0L);
    }

    public Block withNonceAndHash(long winningNonce, String validHash) {
        return new Block(validHash, timestamp, previousHash, content, winningNonce);
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
            return new Block(h, ts, prev, c, n);
        }
        return createGenesisBlock();
    }

    @Override
    public String toString() {
        return String.format("Block [Hash: %s, Time: %s, PreHash: %s, Content: %s, Nonce: %d]",
                getShortHash(), time(), (previousHash.length() > 8 ? previousHash.substring(0, 8) + "..." : previousHash), content, nonce);
    }
}
