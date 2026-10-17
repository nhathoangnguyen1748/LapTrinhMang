package com.pow.simulator.core;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Sổ Cái Blockchain Bầu Cử (Election Blockchain Ledger).
 * Quản lý chuỗi các khối có 5 thuộc tính, hòm phiếu Mempool,
 * và thống kê kết quả bầu cử (danh sách cử tri, số phiếu ứng cử viên).
 */
public class Blockchain {

    private final List<Block> chain;
    private final List<Vote> mempool = new CopyOnWriteArrayList<>();
    private int difficultyLeadingZeros = 5; // Mặc định 5 số '0' ("00000...") theo yêu cầu!

    public Blockchain(Block genesisBlock) {
        Objects.requireNonNull(genesisBlock, "Genesis block must not be null");
        this.chain = new CopyOnWriteArrayList<>();
        this.chain.add(genesisBlock);
    }

    private Blockchain(List<Block> existingChain) {
        this.chain = new CopyOnWriteArrayList<>(existingChain);
    }

    public static Blockchain withDefaultGenesis() {
        return new Blockchain(Block.createGenesisBlock());
    }

    public static Blockchain withDefaultGenesis(int initialDifficultyBits) {
        return withDefaultGenesis();
    }

    public static Blockchain withDefaultGenesis(int initialDifficultyBits, long timestamp) {
        return new Blockchain(Block.createGenesisBlock(timestamp));
    }

    public synchronized void addBlock(Block block) {
        Objects.requireNonNull(block, "Block to add must not be null");
        Block latest = getLatestBlock();
        if (!block.previousHash().equals(latest.hash())) {
            throw new IllegalArgumentException(String.format(
                    "Hash linkage mismatch: block previousHash %s does not match latest hash %s",
                    block.previousHash(), latest.hash()));
        }
        chain.add(block);
    }

    public Block getLatestBlock() {
        if (chain.isEmpty()) {
            throw new IllegalStateException("Blockchain is empty");
        }
        return chain.get(chain.size() - 1);
    }

    public Block getGenesisBlock() {
        return chain.get(0);
    }

    public Block getBlock(int index) {
        if (index < 0 || index >= chain.size()) {
            throw new IndexOutOfBoundsException("Block index out of bounds: " + index);
        }
        return chain.get(index);
    }

    public int size() {
        return chain.size();
    }

    public int getHeight() {
        return chain.size();
    }

    public List<Block> getChain() {
        return Collections.unmodifiableList(new ArrayList<>(chain));
    }

    public Blockchain copy() {
        return new Blockchain(new ArrayList<>(this.chain));
    }

    public synchronized boolean replaceChain(List<Block> newChain) {
        if (newChain == null || newChain.size() <= this.chain.size()) {
            return false;
        }
        this.chain.clear();
        this.chain.addAll(newChain);
        return true;
    }

    // =========================================================================
    //  QUẢN LÝ MEMPOOL (HÒM PHIẾU TẠM) & BẦU CỬ
    // =========================================================================

    public List<Vote> getMempool() {
        return Collections.unmodifiableList(new ArrayList<>(mempool));
    }

    /**
     * Thêm phiếu bầu vào Mempool.
     * Kiểm tra chống gian lận: 1 người chỉ được bầu 1 lần duy nhất!
     */
    public synchronized void addVoteToMempool(Vote vote) {
        Objects.requireNonNull(vote, "Phiếu bầu không được null");
        String voter = vote.voterId();

        // Kiểm tra xem cử tri đã bỏ phiếu trong Sổ Cái chưa
        if (hasVotedInLedger(voter)) {
            throw new IllegalArgumentException("Gian lận: Cử tri '" + voter + "' đã bỏ phiếu trong sổ cái rồi! Mỗi cử tri chỉ được bầu 1 lần duy nhất.");
        }

        // Kiểm tra xem cử tri có đang chờ trong Mempool chưa
        for (Vote pending : mempool) {
            if (pending.voterId().equalsIgnoreCase(voter)) {
                throw new IllegalArgumentException("Cử tri '" + voter + "' đã có phiếu đang chờ trong Mempool!");
            }
        }

        mempool.add(vote);
    }

    /**
     * Khai thác PoW: Đóng gói các phiếu bầu trong Mempool thành Block mới (5 thuộc tính)
     */
    public synchronized Block minePendingVotes() {
        if (mempool.isEmpty()) {
            throw new IllegalStateException("Hòm phiếu Mempool đang trống! Hãy bỏ phiếu trước khi khai thác.");
        }

        // Gom nội dung các phiếu bầu
        List<Vote> votesToMine = new ArrayList<>(mempool);
        StringBuilder contentBuilder = new StringBuilder();
        for (int i = 0; i < votesToMine.size(); i++) {
            if (i > 0) contentBuilder.append(" | ");
            contentBuilder.append(votesToMine.get(i).toText());
        }
        String content = contentBuilder.toString();

        long timestamp = System.currentTimeMillis();
        String previousHash = getLatestBlock().hash();
        String targetPrefix = "0".repeat(difficultyLeadingZeros);

        AtomicBoolean solved = new AtomicBoolean(false);
        AtomicReference<Block> resultBlock = new AtomicReference<>(null);
        AtomicLong nonceGenerator = new AtomicLong(0L);

        int workers = Math.max(4, Runtime.getRuntime().availableProcessors());
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<?>> futures = new ArrayList<>();
            for (int w = 0; w < workers; w++) {
                futures.add(executor.submit(() -> {
                    while (!solved.get() && !Thread.currentThread().isInterrupted()) {
                        long chunkStart = nonceGenerator.getAndAdd(4096);
                        for (long n = chunkStart; n < chunkStart + 4096; n++) {
                            if ((n & 0xFF) == 0 && solved.get()) break;
                            String h = Block.calculateHash(previousHash, timestamp, content, n);
                            if (h.startsWith(targetPrefix)) {
                                if (solved.compareAndSet(false, true)) {
                                    resultBlock.set(new Block(h, timestamp, previousHash, content, n));
                                }
                                break;
                            }
                        }
                    }
                }));
            }
            for (var f : futures) {
                try {
                    f.get();
                } catch (Exception ignored) {}
            }
        }

        Block minedBlock = resultBlock.get();
        if (minedBlock == null) {
            // Fallback single-thread
            long n = 0;
            while (true) {
                String h = Block.calculateHash(previousHash, timestamp, content, n);
                if (h.startsWith(targetPrefix)) {
                    minedBlock = new Block(h, timestamp, previousHash, content, n);
                    break;
                }
                n++;
            }
        }

        chain.add(minedBlock);
        mempool.removeAll(votesToMine);

        return minedBlock;
    }

    // =========================================================================
    //  THỐNG KÊ SỔ CÁI BẦU CỬ
    // =========================================================================

    /**
     * Đếm tổng số phiếu của từng ứng cử viên được lưu trong toàn bộ Sổ Cái.
     */
    public Map<String, Integer> getElectionResults(List<String> registeredCandidates) {
        Map<String, Integer> results = new LinkedHashMap<>();
        for (String c : registeredCandidates) {
            results.put(c, 0);
        }

        for (int i = 1; i < chain.size(); i++) { // Bỏ qua Genesis Block
            String content = chain.get(i).content();
            int start = content.indexOf("bầu cho [");
            while (start != -1) {
                int end = content.indexOf("]", start);
                if (end != -1) {
                    String candInVote = content.substring(start + "bầu cho [".length(), end).trim();
                    for (String c : registeredCandidates) {
                        String coreName = c.contains("(") ? c.substring(0, c.indexOf("(")).trim() : c;
                        if (candInVote.equalsIgnoreCase(c) || candInVote.toLowerCase().contains(coreName.toLowerCase()) || c.toLowerCase().contains(candInVote.toLowerCase())) {
                            results.put(c, results.getOrDefault(c, 0) + 1);
                            break;
                        }
                    }
                    start = content.indexOf("bầu cho [", end);
                } else {
                    break;
                }
            }
        }
        return results;
    }

    /**
     * Danh sách tất cả cử tri đã bỏ phiếu trong Sổ Cái.
     */
    public List<String> getAllVotedVoters() {
        List<String> voters = new ArrayList<>();
        for (int i = 1; i < chain.size(); i++) {
            String content = chain.get(i).content();
            int startIdx = 0;
            while ((startIdx = content.indexOf("Cử tri [", startIdx)) != -1) {
                int endIdx = content.indexOf("]", startIdx);
                if (endIdx != -1) {
                    String voter = content.substring(startIdx + "Cử tri [".length(), endIdx);
                    if (!voters.contains(voter)) {
                        voters.add(voter);
                    }
                    startIdx = endIdx;
                } else {
                    break;
                }
            }
        }
        return voters;
    }

    public boolean hasVotedInLedger(String voterId) {
        if (voterId == null || voterId.isBlank()) return false;
        for (String v : getAllVotedVoters()) {
            if (v.equalsIgnoreCase(voterId.trim())) return true;
        }
        return false;
    }

    public int getTotalVotes() {
        return getAllVotedVoters().size();
    }

    /**
     * Kiểm tra tính toàn vẹn của Sổ Cái.
     * Bất kỳ sự can thiệp / sửa đổi nội dung khối cũ nào đều bị phát hiện!
     */
    public boolean isChainValid() {
        for (int i = 1; i < chain.size(); i++) {
            Block current = chain.get(i);
            Block previous = chain.get(i - 1);

            // Kiểm tra hash liên kết
            if (!current.previousHash().equals(previous.hash())) {
                return false;
            }

            // Kiểm tra tính toán lại hash
            String calculated = Block.calculateHash(current.previousHash(), current.timestamp(), current.content(), current.nonce());
            if (!current.hash().equals(calculated)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Thử nghiệm gian lận: Thay đổi nội dung của khối mà không tính lại hash
     */
    public synchronized void tamperBlockContent(int blockIndex, String fakeContent) {
        if (blockIndex <= 0 || blockIndex >= chain.size()) return;
        Block target = chain.get(blockIndex);
        Block tampered = new Block(target.hash(), target.timestamp(), target.previousHash(), fakeContent, target.nonce());
        chain.set(blockIndex, tampered);
    }

    public synchronized void setBlockForSimulation(int index, Block modifiedBlock) {
        chain.set(index, modifiedBlock);
    }

    // =========================================================================
    //  SERIALIZATION & STORAGE
    // =========================================================================

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

    public synchronized double getBalance(String address) {
        return 0.0;
    }

    public int getDifficultyLeadingZeros() {
        return difficultyLeadingZeros;
    }

    public void setDifficultyLeadingZeros(int difficultyLeadingZeros) {
        this.difficultyLeadingZeros = difficultyLeadingZeros;
    }
}
