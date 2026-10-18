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
import java.util.Objects;

/**
 * Khối Blockchain chuẩn hóa tích hợp đa hình:
 * 1. hash: Mã băm SHA-256 của khối
 * 2. timestamp (time): Thời gian tạo khối
 * 3. previousHash (pre hash): Mã băm khối trước đó
 * 4. content (nội dung): Nội dung thông tin phiếu bầu cử / payload
 * 5. nonce: Số ngẫu nhiên tìm được qua Proof-of-Work
 *
 * Đồng thời duy trì index, difficultyBits, merkleRoot và transactions phục vụ
 * kiểm toán toàn vẹn mật mã, mạng P2P socket và bộ kiểm thử tự động của dự án.
 */
public record Block(
        String hash,
        long timestamp,
        String previousHash,
        String content,
        long nonce,
        int index,
        int difficultyBits,
        String merkleRoot,
        List<Transaction> transactions
) {

    /**
     * Constructor chuẩn 5 tham số theo yêu cầu bài toán Bầu cử điện tử (E-Voting).
     */
    public Block(String hash, long timestamp, String previousHash, String content, long nonce) {
        this(hash, timestamp, previousHash, content, nonce, 0, 8,
                HashUtil.sha256Hex(content != null ? content : ""), List.of());
    }

    /**
     * Constructor 7 tham số hỗ trợ định danh index và difficultyBits cho PoW Node.
     */
    public Block(String hash, long timestamp, String previousHash, String content, long nonce, int index, int difficultyBits) {
        this(hash, timestamp, previousHash, content, nonce, index, difficultyBits,
                HashUtil.sha256Hex(content != null ? content : ""), List.of());
    }

    /**
     * Constructor tương thích ngược cho bộ mô phỏng Bitcoin PoW với MerkleTree và Transaction List.
     */
    public Block(int index, long timestamp, String previousHash, String merkleRoot,
                 List<Transaction> txs, long nonce, int difficultyBits, String hash) {
        this(
                hash,
                timestamp,
                previousHash,
                (merkleRoot != null && !merkleRoot.isBlank() ? merkleRoot : (txs != null ? txs.toString() : "")),
                nonce,
                index,
                difficultyBits,
                (merkleRoot != null ? merkleRoot : (txs != null ? MerkleTree.calculateRoot(txs) : "")),
                (txs != null ? Collections.unmodifiableList(new ArrayList<>(txs)) : List.of())
        );
    }

    /**
     * Compact constructor: Đảm bảo tính bất biến và tự động băm hash nếu chưa có.
     */
    public Block {
        Objects.requireNonNull(previousHash, "previousHash không được để trống");
        content = (content == null) ? "" : content;
        merkleRoot = (merkleRoot == null) ? "" : merkleRoot;
        transactions = (transactions == null) ? List.of() : transactions;
        if (hash == null || hash.isBlank()) {
            if (!transactions.isEmpty()) {
                String header = index + ":" + timestamp + ":" + previousHash + ":" + merkleRoot + ":" + nonce + ":" + difficultyBits;
                hash = HashUtil.sha256Hex(header);
            } else {
                hash = calculateHash(previousHash, timestamp, content, nonce);
            }
        }
    }

    /**
     * Tính toán mã băm SHA-256 từ 4 thuộc tính: preHash + time + nội dung + nonce.
     */
    public static String calculateHash(String previousHash, long timestamp, String content, long nonce) {
        String data = previousHash + ":" + timestamp + ":" + content + ":" + nonce;
        return HashUtil.sha256Hex(data);
    }

    /**
     * Khởi tạo khối nguyên thủy (Genesis Block) với độ khó mặc định 8 bits.
     */
    public static Block createGenesisBlock() {
        return createGenesisBlock(8, 1700000000000L);
    }

    public static Block createGenesisBlock(int initialDifficultyBits) {
        return createGenesisBlock(initialDifficultyBits, 1700000000000L);
    }

    public static Block createGenesisBlock(long timestamp) {
        return createGenesisBlock(8, timestamp);
    }

    public static Block createGenesisBlock(int initialDifficultyBits, long timestamp) {
        String preHash = "0".repeat(64);
        String content = "Khởi tạo hệ thống (0 phiếu)";
        List<Transaction> genesisTxs = List.of(
                new Transaction("genesis-tx-0", "COINBASE", "Satoshi", 50.0, timestamp)
        );
        String root = MerkleTree.calculateRoot(genesisTxs);
        long nonce = 0L;
        while (true) {
            String header = 0 + ":" + timestamp + ":" + preHash + ":" + root + ":" + nonce + ":" + initialDifficultyBits;
            byte[] hashBytes = HashUtil.sha256(header.getBytes(StandardCharsets.UTF_8));
            if (TargetMatcher.matches(hashBytes, initialDifficultyBits)) {
                String h = HashUtil.bytesToHex(hashBytes);
                return new Block(h, timestamp, preHash, content, nonce, 0, initialDifficultyBits, root, genesisTxs);
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

    public String buildHeader(long candidateNonce) {
        if (!transactions.isEmpty()) {
            return index + ":" + timestamp + ":" + previousHash + ":" + merkleRoot + ":" + candidateNonce + ":" + difficultyBits;
        }
        return previousHash + ":" + timestamp + ":" + content + ":" + candidateNonce;
    }

    public byte[] buildHeaderBytes(long candidateNonce) {
        return buildHeader(candidateNonce).getBytes(StandardCharsets.UTF_8);
    }

    public String calculateHash(long candidateNonce) {
        return HashUtil.sha256Hex(buildHeader(candidateNonce));
    }

    public static Block createTemplate(int index, long timestamp, String previousHash, List<Transaction> txs, int diff) {
        List<Transaction> safeTxs = (txs != null) ? txs : List.of();
        String root = MerkleTree.calculateRoot(safeTxs);
        StringBuilder sb = new StringBuilder();
        for (Transaction tx : safeTxs) {
            if (!sb.isEmpty()) sb.append(" | ");
            sb.append(tx.sender()).append(" bầu cho ").append(tx.recipient());
        }
        return new Block("", timestamp, previousHash, sb.toString(), 0L, index, diff, root, safeTxs);
    }

    public Block withNonceAndHash(long winningNonce, String validHash) {
        return new Block(validHash, timestamp, previousHash, content, winningNonce, index, difficultyBits, merkleRoot, transactions);
    }

    public String toNetworkString() {
        StringBuilder sb = new StringBuilder();
        sb.append(index).append("\t")
          .append(timestamp).append("\t")
          .append(previousHash).append("\t")
          .append(merkleRoot).append("\t")
          .append(nonce).append("\t")
          .append(difficultyBits).append("\t")
          .append(hash).append("\t")
          .append(content.replace("\n", " ").replace("\t", " ")).append("\n");
        for (Transaction tx : transactions) {
            sb.append(tx.toNetworkString()).append("\n");
        }
        return sb.toString().trim();
    }

    public static Block fromNetworkString(String raw) {
        String[] lines = raw.split("\n");
        String[] header = lines[0].trim().split("\t", -1);
        if (header.length >= 7) {
            int index = Integer.parseInt(header[0]);
            long timestamp = Long.parseLong(header[1]);
            String previousHash = header[2];
            String merkleRoot = header[3];
            long nonce = Long.parseLong(header[4]);
            int difficultyBits = Integer.parseInt(header[5]);
            String hash = header[6];
            String content = header.length > 7 ? header[7] : "";

            List<Transaction> txs = new ArrayList<>();
            for (int i = 1; i < lines.length; i++) {
                String line = lines[i].trim();
                if (!line.isEmpty()) {
                    txs.add(Transaction.fromNetworkString(line));
                }
            }
            return new Block(hash, timestamp, previousHash, content, nonce, index, difficultyBits, merkleRoot, txs);
        } else if (header.length >= 5) {
            long ts = Long.parseLong(header[0]);
            String prev = header[1];
            long n = Long.parseLong(header[2]);
            String h = header[3];
            String c = header[4];
            return new Block(h, ts, prev, c, n);
        }
        return createGenesisBlock();
    }

    @Override
    public String toString() {
        return String.format("Block #%d [Hash: %s, Time: %s, PreHash: %s, Content: %s, Nonce: %d]",
                index, getShortHash(), time(), (previousHash.length() > 8 ? previousHash.substring(0, 8) + "..." : previousHash), content, nonce);
    }
}
