package com.pow.simulator.web;

import com.pow.simulator.core.Block;
import com.pow.simulator.core.Blockchain;
import com.pow.simulator.core.Vote;
import com.pow.simulator.storage.LedgerStorage;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import com.pow.simulator.crypto.HashUtil;

/**
 * Máy chủ HTTP/REST API cho Hệ Thống Bầu Cử Điện Tử Blockchain (E-Voting).
 * Quản lý hòm phiếu Mempool, Sổ Cái 5 thuộc tính, và Lưu trữ ổ cứng.
 */
public class NodeApiServer implements AutoCloseable {

    private final int httpPort;
    private final HttpServer httpServer;
    private final List<String> eventLogs = new CopyOnWriteArrayList<>();
    private final LedgerStorage storage = LedgerStorage.defaultInstance();

    public static class PeerNode {
        private final String id;
        private final String name;
        private final String role;
        private final int port;
        private final Blockchain blockchain;
        private final String hashRate;
        private final java.util.concurrent.atomic.AtomicInteger blocksMined = new java.util.concurrent.atomic.AtomicInteger(0);
        private volatile String status = "ONLINE";

        public PeerNode(String id, String name, String role, int port, Blockchain blockchain, String hashRate) {
            this.id = id;
            this.name = name;
            this.role = role;
            this.port = port;
            this.blockchain = blockchain;
            this.hashRate = hashRate;
        }

        public String id() { return id; }
        public String name() { return name; }
        public String role() { return role; }
        public int port() { return port; }
        public Blockchain blockchain() { return blockchain; }
        public String hashRate() { return hashRate; }
        public int blocksMined() { return blocksMined.get(); }
        public void incrementBlocksMined() { blocksMined.incrementAndGet(); }
        public String status() { return status; }
        public void setStatus(String s) { this.status = s; }
    }
    private final List<PeerNode> clusterNodes = new CopyOnWriteArrayList<>();

    private Blockchain blockchain;
    private final List<String> candidates = List.of(
            "Le Quang Minh",
            "Nguyen Do Trung Luong",
            "Nguyen Hoang Nhat"
    );

    private boolean isTampered = false;
    private int tamperedBlockIndex = -1;
    private String tamperFailureReason = "";
    private final java.util.concurrent.atomic.AtomicBoolean isMining = new java.util.concurrent.atomic.AtomicBoolean(false);
    private final java.util.concurrent.atomic.AtomicBoolean continuousMiningActive = new java.util.concurrent.atomic.AtomicBoolean(false);
    private final java.util.concurrent.atomic.AtomicInteger continuousRoundsCount = new java.util.concurrent.atomic.AtomicInteger(0);
    private final java.util.concurrent.atomic.AtomicBoolean currentRaceCancelled = new java.util.concurrent.atomic.AtomicBoolean(false);
    private volatile Thread continuousMiningThread = null;
    private volatile boolean autoMineEnabled = false;
    private volatile String latestWinnerId = "Node-1";

    public NodeApiServer(int httpPort) throws IOException {
        this.httpPort = httpPort;
        this.httpServer = HttpServer.create(new InetSocketAddress(httpPort), 0);

        initBlockchain();
        setupEndpoints();
        httpServer.setExecutor(java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor());
    }

    public synchronized void initBlockchain() {
        eventLogs.clear();
        isTampered = false;
        tamperedBlockIndex = -1;
        tamperFailureReason = "";

        // Tải sổ cái từ ổ cứng nếu có, nếu chưa có thì khởi tạo Genesis Block
        Blockchain saved = storage.loadChain();
        if (saved != null && saved.size() > 0) {
            this.blockchain = saved;
            logEvent("📂 Đã phục hồi Sổ Cái Bầu Cử từ ổ cứng (" + blockchain.size() + " khối đã lưu).");
        } else {
            this.blockchain = Blockchain.withDefaultGenesis();
            try {
                storage.saveChain(this.blockchain);
                logEvent("🌟 Khởi tạo Sổ Cái mới với Genesis Block (Khối khởi nguyên #0).");
            } catch (Exception e) {
                logEvent("⚠️ Lỗi khởi tạo lưu trữ: " + e.getMessage());
            }
        }

        // Khởi tạo Cụm Mạng P2P gồm 10 Nodes độc lập
        clusterNodes.clear();
        PeerNode node1 = new PeerNode("Node-1", "Máy 1: Khoa CNTT", "Trạm Gateway & Điều Phối", 8001, this.blockchain, "280 KH/s");
        PeerNode node2 = new PeerNode("Node-2", "Máy 2: Ban Thanh Tra", "Nút Kiểm Soát & Đối Soát", 8002, this.blockchain.copy(), "240 KH/s");
        PeerNode node3 = new PeerNode("Node-3", "Máy 3: Hội Đồng SV", "Nút Lưu Trữ & Xác Thực", 8003, this.blockchain.copy(), "310 KH/s");
        PeerNode node4 = new PeerNode("Node-4", "Máy 4: TT Dữ Liệu", "Server Tính Toán Phân Tán", 8004, this.blockchain.copy(), "350 KH/s");
        PeerNode node5 = new PeerNode("Node-5", "Máy 5: Trạm Dự Phòng", "Chi Nhánh Ung Văn Khiêm", 8005, this.blockchain.copy(), "220 KH/s");
        PeerNode node6 = new PeerNode("Node-6", "Máy 6: Khoa Điện - ĐT", "Nút Xác Thực Phụ", 8006, this.blockchain.copy(), "260 KH/s");
        PeerNode node7 = new PeerNode("Node-7", "Máy 7: Viện Nghiên Cứu", "Nút Phân Tích Dữ Liệu", 8007, this.blockchain.copy(), "320 KH/s");
        PeerNode node8 = new PeerNode("Node-8", "Máy 8: Ban Khảo Thí SV", "Nút Tra Cứu Cử Tri", 8008, this.blockchain.copy(), "290 KH/s");
        PeerNode node9 = new PeerNode("Node-9", "Máy 9: Đoàn Trường", "Nút Giám Sát Bầu Cử", 8009, this.blockchain.copy(), "270 KH/s");
        PeerNode node10 = new PeerNode("Node-10", "Máy 10: TT Khảo Thí", "Nút Khóa Sổ Cái Dự Phòng", 8010, this.blockchain.copy(), "340 KH/s");
        clusterNodes.add(node1);
        clusterNodes.add(node2);
        clusterNodes.add(node3);
        clusterNodes.add(node4);
        clusterNodes.add(node5);
        clusterNodes.add(node6);
        clusterNodes.add(node7);
        clusterNodes.add(node8);
        clusterNodes.add(node9);
        clusterNodes.add(node10);
        this.blockchain.setDifficultyLeadingZeros(6);
        for (PeerNode p : clusterNodes) {
            p.blockchain().setDifficultyLeadingZeros(6);
        }
        logEvent("🌐 [MẠNG P2P MESH] Đã kích hoạt cụm 10 Máy (Port 8001 -> 8010) kết nối Full-Mesh 45 liên kết TCP Sockets.");
    }

    private void logEvent(String msg) {
        String timestamp = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
        eventLogs.add("[" + timestamp + "] " + msg);
        if (eventLogs.size() > 100) {
            eventLogs.removeFirst();
        }
    }

    private void setupEndpoints() {
        httpServer.createContext("/api/election/status", this::handleElectionStatus);
        httpServer.createContext("/api/election/vote", this::handleElectionVote);
        httpServer.createContext("/api/election/mine", this::handleElectionMine);
        httpServer.createContext("/api/election/difficulty", this::handleElectionDifficulty);
        httpServer.createContext("/api/election/tamper", this::handleElectionTamper);
        httpServer.createContext("/api/election/reset", this::handleElectionReset);

        // Hỗ trợ alias tương thích
        httpServer.createContext("/api/status", this::handleElectionStatus);
        httpServer.createContext("/api/chain", this::handleElectionStatus);

        // Giao diện bỏ phiếu cử tri riêng biệt (/vote và /ballot)
        httpServer.createContext("/vote", this::handleVoterPage);
        httpServer.createContext("/ballot", this::handleVoterPage);

        // Giao diện Giả lập 5 Máy PoW (/nodes, /pow, /cluster, /simulator)
        httpServer.createContext("/nodes", this::handleNodeClusterPage);
        httpServer.createContext("/pow", this::handleNodeClusterPage);
        httpServer.createContext("/cluster", this::handleNodeClusterPage);
        httpServer.createContext("/simulator", this::handleNodeClusterPage);

        // API cho Cụm 5 Máy PoW
        httpServer.createContext("/api/cluster/mine", this::handleClusterMine);
        httpServer.createContext("/api/cluster/seed-votes", this::handleClusterSeedVotes);
        httpServer.createContext("/api/cluster/continuous/start", this::handleContinuousStart);
        httpServer.createContext("/api/cluster/continuous/stop", this::handleContinuousStop);

        httpServer.createContext("/", this::handleStatic);
    }

    public void start() {
        httpServer.start();
        logEvent("Cổng Web Bầu Cử Blockchain sẵn sàng tại: http://localhost:" + httpPort);
        if (autoMineEnabled && !blockchain.getMempool().isEmpty()) {
            triggerAutoMining();
        }
    }

    public void triggerAutoMining() {
        if (!autoMineEnabled || blockchain.getMempool().isEmpty()) return;
        if (isMining.compareAndSet(false, true)) {
            Thread.ofVirtual().name("pow-auto-miner").start(() -> {
                try {
                    int pendingCount = blockchain.getMempool().size();
                    logEvent("⚡ [ĐỒNG THUẬN TỰ ĐỘNG] Tự động giải thuật toán băm để đóng gói " + pendingCount + " phiếu bầu trong Mempool...");
                    long start = System.currentTimeMillis();

                    Block minedBlock = blockchain.minePendingVotes();
                    long duration = System.currentTimeMillis() - start;

                    storage.saveChain(blockchain);

                    logEvent("🎉 [ĐÃ ĐÓNG BLOCK MỚI] Node 1 hoàn tất Block #" + (blockchain.size() - 1)
                            + " [Nonce: " + minedBlock.nonce() + " | Hash: " + minedBlock.getShortHash()
                            + " | Thời gian: " + duration + " ms] đã lưu vào Sổ Cái!");

                    for (PeerNode peer : clusterNodes) {
                        if (peer.blockchain() != this.blockchain) {
                            try {
                                peer.blockchain().syncMinedBlock(minedBlock);
                                logEvent("📡 [P2P BLOCK BROADCAST] Node 1 phát tán Block mới sang " + peer.name());
                            } catch (Exception ex) {
                                logEvent("⚠️ [P2P SYNC ERROR] " + peer.name() + ": " + ex.getMessage());
                            }
                        }
                    }
                    logEvent("🧹 Đã tự động dọn sạch các phiếu trong Mempool của CẢ " + clusterNodes.size() + " NODES!");
                } catch (Exception ex) {
                    logEvent("⚠️ [AUTO-MINER ERROR] " + ex.getMessage());
                } finally {
                    isMining.set(false);
                    if (!blockchain.getMempool().isEmpty()) {
                        triggerAutoMining();
                    }
                }
            });
        }
    }

    // =========================================================================
    //  API HANDLERS
    // =========================================================================

    private void handleElectionStatus(HttpExchange exchange) throws IOException {
        if (handleCors(exchange)) return;

        Map<String, Integer> voteResults = blockchain.getElectionResults(candidates);
        int totalVotes = blockchain.getTotalVotes();
        List<String> votedVoters = blockchain.getAllVotedVoters();
        List<Vote> mempoolVotes = blockchain.getMempool();
        List<Block> chainBlocks = blockchain.getChain();

        boolean chainValid = !isTampered && blockchain.isChainValid();

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"isChainValid\":").append(chainValid).append(",");
        sb.append("\"isTampered\":").append(isTampered).append(",");
        sb.append("\"tamperedBlockIndex\":").append(tamperedBlockIndex).append(",");
        sb.append("\"tamperReason\":\"").append(escapeJson(tamperFailureReason)).append("\",");
        sb.append("\"isContinuousMining\":").append(continuousMiningActive.get()).append(",");
        sb.append("\"continuousRounds\":").append(continuousRoundsCount.get()).append(",");
        sb.append("\"latestWinnerId\":\"").append(escapeJson(latestWinnerId)).append("\",");
        sb.append("\"totalVotes\":").append(totalVotes).append(",");
        sb.append("\"blockCount\":").append(chainBlocks.size()).append(",");
        sb.append("\"difficulty\":").append(blockchain.getDifficultyLeadingZeros()).append(",");
        sb.append("\"targetPrefix\":\"").append("0".repeat(blockchain.getDifficultyLeadingZeros())).append("\",");
        sb.append("\"storagePath\":\"").append(escapeJson(storage.getChainFile().toAbsolutePath().toString())).append("\",");
        sb.append("\"storageSize\":").append(storage.getChainFileSize()).append(",");

        // Ứng cử viên & kết quả bầu cử
        sb.append("\"candidates\":[");
        int cIdx = 0;
        for (String c : candidates) {
            if (cIdx++ > 0) sb.append(",");
            int votes = voteResults.getOrDefault(c, 0);
            double percent = (totalVotes > 0) ? (votes * 100.0 / totalVotes) : 0.0;
            sb.append("{")
              .append("\"name\":\"").append(escapeJson(c)).append("\",")
              .append("\"votes\":").append(votes).append(",")
              .append("\"percent\":").append(String.format(java.util.Locale.US, "%.1f", percent))
              .append("}");
        }
        sb.append("],");

        // Danh sách cử tri đã bầu
        sb.append("\"votedVoters\":[");
        for (int i = 0; i < votedVoters.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append("\"").append(escapeJson(votedVoters.get(i))).append("\"");
        }
        sb.append("],");

        // Hòm phiếu Mempool
        sb.append("\"mempool\":[");
        for (int i = 0; i < mempoolVotes.size(); i++) {
            if (i > 0) sb.append(",");
            Vote v = mempoolVotes.get(i);
            sb.append("{")
              .append("\"voteId\":\"").append(escapeJson(v.voteId())).append("\",")
              .append("\"voterId\":\"").append(escapeJson(v.voterId())).append("\",")
              .append("\"candidate\":\"").append(escapeJson(v.candidate())).append("\",")
              .append("\"time\":\"").append(escapeJson(v.timeFormatted())).append("\"")
              .append("}");
        }
        sb.append("],");

        // Danh sách Block với ĐÚNG 5 THUỘC TÍNH: hash, time, pre hash, nội dung, nonce
        sb.append("\"blocks\":[");
        for (int i = 0; i < chainBlocks.size(); i++) {
            if (i > 0) sb.append(",");
            Block b = chainBlocks.get(i);
            sb.append("{")
              .append("\"index\":").append(i).append(",")
              .append("\"hash\":\"").append(escapeJson(b.hash())).append("\",")
              .append("\"time\":\"").append(escapeJson(b.time())).append("\",")
              .append("\"timestamp\":").append(b.timestamp()).append(",")
              .append("\"preHash\":\"").append(escapeJson(b.previousHash())).append("\",")
              .append("\"noiDung\":\"").append(escapeJson(b.content())).append("\",")
              .append("\"nonce\":").append(b.nonce())
              .append("}");
        }
        sb.append("],");

        // Danh sách Giao Dịch / Phiếu Bầu (Latest Transactions - Etherscan Style)
        sb.append("\"transactions\":[");
        int txCount = 0;
        // 1. Giao dịch Mempool (Pending)
        for (Vote v : mempoolVotes) {
            if (txCount++ > 0) sb.append(",");
            String cleanVoter = Blockchain.removeAccents(v.voterId()).trim();
            String cleanCand = Blockchain.normalizeCandidateName(v.candidate());
            sb.append("{")
              .append("\"txHash\":\"0x").append(escapeJson(v.voteId())).append("\",")
              .append("\"blockIndex\":-1,")
              .append("\"status\":\"Pending\",")
              .append("\"voterId\":\"").append(escapeJson(cleanVoter)).append("\",")
              .append("\"candidate\":\"").append(escapeJson(cleanCand)).append("\",")
              .append("\"time\":\"").append(escapeJson(v.timeFormatted())).append("\",")
              .append("\"timestamp\":").append(v.timestamp())
              .append("}");
        }
        // 2. Giao dịch đã đóng trong các Khối (Confirmed, duyệt từ khối mới nhất)
        for (int bIdx = chainBlocks.size() - 1; bIdx >= 1; bIdx--) {
            Block b = chainBlocks.get(bIdx);
            String content = b.content();
            String[] items = content.contains(" | ") ? content.split(" \\| ") : new String[]{content};
            for (int subIdx = 0; subIdx < items.length; subIdx++) {
                String raw = items[subIdx];
                String voter = "";
                String cand = "";
                int vStart = raw.indexOf("Cử tri [");
                if (vStart == -1) vStart = raw.indexOf("Cu tri [");
                if (vStart != -1) {
                    int vEnd = raw.indexOf("]", vStart);
                    if (vEnd != -1) voter = raw.substring(raw.indexOf("[", vStart) + 1, vEnd);
                }
                int cStart = raw.indexOf("bầu cho [");
                if (cStart == -1) cStart = raw.indexOf("bau cho [");
                if (cStart != -1) {
                    int cEnd = raw.indexOf("]", cStart);
                    if (cEnd != -1) cand = raw.substring(raw.indexOf("[", cStart) + 1, cEnd);
                }
                if (!voter.isBlank() || !cand.isBlank()) {
                    if (txCount++ > 0) sb.append(",");
                    String cleanVoter = Blockchain.removeAccents(voter).trim();
                    String cleanCand = Blockchain.normalizeCandidateName(cand);
                    String txHash = "0x" + com.pow.simulator.crypto.HashUtil.sha256Hex(b.hash() + ":" + voter + ":" + subIdx).substring(0, 8);
                    sb.append("{")
                      .append("\"txHash\":\"").append(txHash).append("\",")
                      .append("\"blockIndex\":").append(bIdx).append(",")
                      .append("\"status\":\"Confirmed\",")
                      .append("\"voterId\":\"").append(escapeJson(cleanVoter)).append("\",")
                      .append("\"candidate\":\"").append(escapeJson(cleanCand)).append("\",")
                      .append("\"time\":\"").append(escapeJson(b.time())).append("\",")
                      .append("\"timestamp\":").append(b.timestamp())
                      .append("}");
                }
            }
        }
        sb.append("],");

        // Trạng thái Cụm Mạng P2P Nodes
        sb.append("\"cluster\":[");
        for (int i = 0; i < clusterNodes.size(); i++) {
            if (i > 0) sb.append(",");
            PeerNode node = clusterNodes.get(i);
            sb.append("{")
              .append("\"id\":\"").append(escapeJson(node.id())).append("\",")
              .append("\"name\":\"").append(escapeJson(node.name())).append("\",")
              .append("\"role\":\"").append(escapeJson(node.role())).append("\",")
              .append("\"port\":").append(node.port()).append(",")
              .append("\"status\":\"").append(escapeJson(node.status())).append("\",")
              .append("\"hashRate\":\"").append(escapeJson(node.hashRate())).append("\",")
              .append("\"blocksMined\":").append(node.blocksMined()).append(",")
              .append("\"blockCount\":").append(node.blockchain().size()).append(",")
              .append("\"mempoolCount\":").append(node.blockchain().getMempool().size()).append(",")
              .append("\"mempool\":[");
            List<Vote> pMem = node.blockchain().getMempool();
            for (int j = 0; j < pMem.size(); j++) {
                if (j > 0) sb.append(",");
                Vote v = pMem.get(j);
                sb.append("{")
                  .append("\"voteId\":\"").append(escapeJson(v.voteId())).append("\",")
                  .append("\"voterId\":\"").append(escapeJson(v.voterId())).append("\",")
                  .append("\"candidate\":\"").append(escapeJson(v.candidate())).append("\",")
                  .append("\"time\":\"").append(escapeJson(v.timeFormatted())).append("\"")
                  .append("}");
            }
            sb.append("]");
            sb.append("}");
        }
        sb.append("],");

        // Logs
        sb.append("\"logs\":[");
        for (int i = 0; i < eventLogs.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append("\"").append(escapeJson(eventLogs.get(i))).append("\"");
        }
        sb.append("]");

        sb.append("}");

        sendResponse(exchange, 200, "application/json", sb.toString());
    }

    private void handleElectionVote(HttpExchange exchange) throws IOException {
        if (handleCors(exchange)) return;
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        String voterId = extractJsonField(body, "voterId", "");
        String candidate = extractJsonField(body, "candidate", "");

        if (voterId.isBlank() || candidate.isBlank()) {
            sendResponse(exchange, 400, "application/json", "{\"error\":\"Vui lòng nhập tên cử tri và chọn ứng cử viên!\"}");
            return;
        }

        try {
            Vote vote = new Vote(voterId, candidate);

            // 1. Ghi nhận tại Node 1 (Cổng tiếp nhận)
            blockchain.addVoteToMempool(vote);
            logEvent("🗳️ [CỔNG TIẾP NHẬN] Cử tri '" + voterId + "' đã gửi phiếu bầu cho: " + candidate);

            // 2. Phát tán P2P tới các Node còn lại trong cụm mạng
            int broadcastSuccessCount = 1;
            for (PeerNode peer : clusterNodes) {
                if (peer.blockchain() != this.blockchain) {
                    try {
                        peer.blockchain().addVoteToMempool(vote);
                        broadcastSuccessCount++;
                        logEvent("🌐 [P2P GOSSIP BROADCAST] Đã phát tán phiếu của [" + voterId + "] qua TCP tới " + peer.name() + " -> Đã vào Mempool!");
                    } catch (Exception ex) {
                        logEvent("⚠️ [P2P SYNC ERROR] " + peer.name() + ": " + ex.getMessage());
                    }
                }
            }
            logEvent("✅ [HOÀN TẤT ĐỒNG BỘ] Phiếu bầu [" + voterId + "] đã có mặt trong Mempool của TẤT CẢ " + broadcastSuccessCount + " NODES!");

            // 3. Tự động kích hoạt luồng tính toán PoW nếu bật auto-mine
            if (autoMineEnabled) {
                triggerAutoMining();
            }

            sendResponse(exchange, 200, "application/json", "{\"success\":true,\"message\":\"Đã nhận phiếu bầu và phát tán vào Mempool của tất cả " + broadcastSuccessCount + " Nodes thành công! Hãy chuyển sang trang 'Mạng Lưới 10 Máy PoW' (/nodes) để quan sát cuộc đua đóng khối.\"}");
        } catch (IllegalArgumentException e) {
            logEvent("⚠️ [TỪ CHỐI BỎ PHIẾU] " + e.getMessage());
            sendResponse(exchange, 400, "application/json", "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
        } catch (Exception e) {
            sendResponse(exchange, 500, "application/json", "{\"error\":\"Lỗi hệ thống: " + escapeJson(e.getMessage()) + "\"}");
        }
    }

    private synchronized void handleElectionMine(HttpExchange exchange) throws IOException {
        if (handleCors(exchange)) return;

        if (!isMining.compareAndSet(false, true)) {
            sendResponse(exchange, 409, "application/json", "{\"error\":\"Hệ thống đang tính toán PoW. Vui lòng đợi trong giây lát...\"}");
            return;
        }

        try {
            int pendingCount = blockchain.getMempool().size();
            if (pendingCount == 0) {
                logEvent("⚡ [XÁC THỰC POW] Node 1 bắt đầu tính toán Block mới (Khối rỗng - 0 phiếu bầu)...");
            } else {
                logEvent("⚡ [XÁC THỰC POW] Node 1 bắt đầu tính toán Block mới để đóng gói " + pendingCount + " phiếu bầu...");
            }
            long start = System.currentTimeMillis();

            Block minedBlock = blockchain.minePendingVotes();
            long duration = System.currentTimeMillis() - start;

            // Tự động lưu chuỗi khối vào ổ cứng
            storage.saveChain(blockchain);
            this.latestWinnerId = "Node-1";

            logEvent("🎉 [ĐÃ ĐÓNG KHỐI] Node 1 hoàn tất Block #" + (blockchain.size() - 1)
                    + " [Nonce: " + minedBlock.nonce() + " | Hash: " + minedBlock.getShortHash()
                    + " | Thời gian: " + duration + " ms] đã lưu vào Sổ Cái!");

            // 3. Phát tán Block mới tới các Node khác trong cụm P2P
            for (PeerNode peer : clusterNodes) {
                if (peer.blockchain() != this.blockchain) {
                    try {
                        peer.blockchain().syncMinedBlock(minedBlock);
                        logEvent("📡 [P2P BLOCK BROADCAST] Node 1 phát tán Block mới sang " + peer.name() + " -> Đã thêm vào Sổ cái & làm sạch Mempool!");
                    } catch (Exception ex) {
                        peer.blockchain().forceSyncChain(this.blockchain.getChain());
                        logEvent("🔄 [P2P NAKAMOTO SYNC] " + peer.name() + " đã đồng bộ lại chuỗi khối hợp lệ.");
                    }
                }
            }
            logEvent("🧹 Đã tự động dọn sạch các phiếu trong Mempool của CẢ " + clusterNodes.size() + " NODES!");

            String json = "{\"success\":true,\"hash\":\"" + escapeJson(minedBlock.hash())
                    + "\",\"time\":\"" + escapeJson(minedBlock.time())
                    + "\",\"preHash\":\"" + escapeJson(minedBlock.previousHash())
                    + "\",\"noiDung\":\"" + escapeJson(minedBlock.content())
                    + "\",\"nonce\":" + minedBlock.nonce() + "}";
            sendResponse(exchange, 200, "application/json", json);
        } catch (Exception e) {
            logEvent("❌ Lỗi tính toán block: " + e.getMessage());
            sendResponse(exchange, 500, "application/json", "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
        } finally {
            isMining.set(false);
        }
    }

    private void handleElectionTamper(HttpExchange exchange) throws IOException {
        if (handleCors(exchange)) return;
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        if (blockchain.size() <= 1) {
            sendResponse(exchange, 400, "application/json", "{\"error\":\"Cần có ít nhất 1 Block bầu cử (ngoài Genesis) để thử nghiệm sửa đổi!\"}");
            return;
        }

        int targetIndex = 1;
        String fakeContent = "Cử tri [Hacker] đã sửa đổi phiếu bầu gian lận thành công!";

        blockchain.tamperBlockContent(targetIndex, fakeContent);
        isTampered = true;
        tamperedBlockIndex = targetIndex;
        tamperFailureReason = "Phát hiện gian lận tại Khối #" + targetIndex + ": Nội dung phiếu bầu bị sửa đổi khiến mã băm SHA-256 không còn khớp! Toàn bộ sổ cái bị vô hiệu hóa.";

        logEvent("🚨 [CẢNH BÁO GIAN LẬN] Nội dung Khối #" + targetIndex + " đã bị cố ý can thiệp! Sổ cái lập tức báo động đỏ.");
        sendResponse(exchange, 200, "application/json", "{\"success\":true,\"message\":\"Đã can thiệp sửa đổi nội dung khối để kiểm tra tính bảo mật!\"}");
    }

    private void handleElectionDifficulty(HttpExchange exchange) throws IOException {
        if (handleCors(exchange)) return;
        int diff = 6; // Cố định 6 zeros theo yêu cầu
        blockchain.setDifficultyLeadingZeros(diff);
        for (PeerNode peer : clusterNodes) {
            peer.blockchain().setDifficultyLeadingZeros(diff);
        }
        sendResponse(exchange, 200, "application/json", "{\"success\":true,\"difficulty\":6,\"targetPrefix\":\"000000\"}");
    }

    private void handleElectionReset(HttpExchange exchange) throws IOException {
        if (handleCors(exchange)) return;
        initBlockchain();
        logEvent("🔄 [RESET] Sổ Cái Bầu Cử và Mempool các Node đã được khởi động lại sạch sẽ.");
        sendResponse(exchange, 200, "application/json", "{\"success\":true,\"message\":\"Đã làm mới sổ cái thành công!\"}");
    }

    private void handleVoterPage(HttpExchange exchange) throws IOException {
        String html = VoterHtmlTemplate.getHtml();
        sendResponse(exchange, 200, "text/html; charset=utf-8", html);
    }

    private void handleStatic(HttpExchange exchange) throws IOException {
        String html = DashboardHtmlTemplate.getHtml();
        sendResponse(exchange, 200, "text/html; charset=utf-8", html);
    }

    private void handleNodeClusterPage(HttpExchange exchange) throws IOException {
        String html = NodeClusterHtmlTemplate.getHtml();
        sendResponse(exchange, 200, "text/html; charset=utf-8", html);
    }

    private void handleClusterSeedVotes(HttpExchange exchange) throws IOException {
        if (handleCors(exchange)) return;
        try {
            long salt = System.currentTimeMillis() % 10000;
            Vote v1 = new Vote("2380601549_Nhat_" + salt, candidates.get(0));
            Vote v2 = new Vote("2380601305_Luong_" + salt, candidates.get(1));
            Vote v3 = new Vote("2380601342_Minh_" + salt, candidates.get(2));

            List<Vote> newVotes = List.of(v1, v2, v3);
            for (Vote v : newVotes) {
                blockchain.addVoteToMempool(v);
                for (PeerNode node : clusterNodes) {
                    if (node.blockchain() != this.blockchain) {
                        try {
                            node.blockchain().addVoteToMempool(v);
                        } catch (Exception ignored) {}
                    }
                }
            }

            logEvent("🗳️ [NẠP PHIẾU MẪU] Đã thêm 3 phiếu bầu mẫu vào Mempool của CẢ 10 MÁY!");
            sendResponse(exchange, 200, "application/json", "{\"success\":true,\"count\":3,\"message\":\"Đã nạp 3 phiếu mẫu vào Mempool của 10 máy!\"}");
        } catch (Exception e) {
            sendResponse(exchange, 500, "application/json", "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
        }
    }

    private void handleContinuousStart(HttpExchange exchange) throws IOException {
        if (handleCors(exchange)) return;
        if (continuousMiningActive.compareAndSet(false, true)) {
            currentRaceCancelled.set(false);
            continuousRoundsCount.set(0);
            logEvent("🚀 [CHẾ ĐỘ CHẠY LIÊN TỤC] Đã kích hoạt chế độ PoW liên tục giữa 10 máy.");

            continuousMiningThread = Thread.ofVirtual().name("continuous-pow-runner").start(() -> {
                while (continuousMiningActive.get()) {
                    continuousRoundsCount.incrementAndGet();
                    logEvent("🏁 [CHẠY LIÊN TỤC - VÒNG #" + continuousRoundsCount.get() + "] 10 Máy chuẩn bị tranh tài đóng khối tiếp theo...");

                    try {
                        executeClusterRaceInternal(null);
                    } catch (Exception e) {
                        logEvent("⚠️ [LỖI CHẠY LIÊN TỤC] " + e.getMessage());
                    }

                    // Tạm nghỉ 2.5 giây giữa các vòng để người dùng quan sát hiệu ứng lan truyền mạng
                    for (int i = 0; i < 25 && continuousMiningActive.get(); i++) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                }
                logEvent("⏹️ [CHẾ ĐỘ CHẠY LIÊN TỤC] Đã dừng chu trình tính toán PoW.");
            });

            sendResponse(exchange, 200, "application/json", "{\"success\":true,\"message\":\"Đã kích hoạt chế độ chạy PoW liên tục!\",\"isContinuousMining\":true}");
        } else {
            sendResponse(exchange, 200, "application/json", "{\"success\":true,\"message\":\"Chế độ chạy liên tục đã đang hoạt động.\",\"isContinuousMining\":true}");
        }
    }

    private void handleContinuousStop(HttpExchange exchange) throws IOException {
        if (handleCors(exchange)) return;
        continuousMiningActive.set(false);
        currentRaceCancelled.set(true);
        if (continuousMiningThread != null) {
            continuousMiningThread.interrupt();
        }
        isMining.set(false);
        for (PeerNode p : clusterNodes) {
            if ("MINING".equals(p.status())) {
                p.setStatus("ONLINE");
            }
        }
        logEvent("⏹️ [DỪNG CHẠY LIÊN TỤC] Đã nhận lệnh dừng từ người dùng. Tạm dừng tính toán khối.");
        sendResponse(exchange, 200, "application/json", "{\"success\":true,\"message\":\"Đã dừng chế độ chạy PoW liên tục!\",\"isContinuousMining\":false}");
    }

    private synchronized Map<String, Object> executeClusterRaceInternal(String singleNodeId) throws Exception {
        currentRaceCancelled.set(false);
        if (!isMining.compareAndSet(false, true)) {
            throw new IllegalStateException("Hệ thống đang thực hiện một lượt tính toán PoW khác.");
        }

        try {
            // 1. Đồng bộ ngọn chuỗi cả 10 máy trước khi tính toán
            List<Block> masterChain = this.blockchain.getChain();
            for (PeerNode peer : clusterNodes) {
                if (peer.blockchain() != this.blockchain) {
                    peer.blockchain().forceSyncChain(masterChain);
                }
            }

            // Thanh lọc các phiếu bầu đã có trong Sổ Cái ra khỏi Mempool (ngăn tuyệt đối việc đóng trùng phiếu)
            this.blockchain.purgeVotesAlreadyInLedger();
            for (PeerNode peer : clusterNodes) {
                peer.blockchain().purgeVotesAlreadyInLedger();
            }

            List<Vote> votesToMine = new ArrayList<>(blockchain.getMempool());
            String content;
            if (votesToMine.isEmpty()) {
                content = "Khối rỗng (0 phiếu bầu)";
                logEvent("🔒 [HỆ THỐNG ĐỒNG THUẬN] Hòm phiếu Mempool đang trống -> Bắt đầu tính toán Khối Rỗng (0 phiếu bầu)...");
            } else {
                StringBuilder contentBuilder = new StringBuilder();
                for (int i = 0; i < votesToMine.size(); i++) {
                    if (i > 0) contentBuilder.append(" | ");
                    contentBuilder.append(votesToMine.get(i).toText());
                }
                content = contentBuilder.toString();
            }

            int pendingCount = votesToMine.size();
            long start = System.currentTimeMillis();

            PeerNode winnerNode;
            Block minedBlock;
            long duration;

            boolean isRace = (singleNodeId == null || singleNodeId.isBlank());

            if (isRace) {
                // CUỘC TRANH TÀI POW GIỮA 10 MÁY (TỐC ĐỘ CHẬM KIỂM SOÁT)
                logEvent("🏁 [CUỘC TRANH TÀI POW] Khởi tạo 10 luồng ảo cùng giải thuật toán đóng gói " + pendingCount + " phiếu (Tốc độ kiểm soát)...");

                java.util.concurrent.atomic.AtomicBoolean cancelled = new java.util.concurrent.atomic.AtomicBoolean(false);
                java.util.concurrent.atomic.AtomicReference<PeerNode> winnerRef = new java.util.concurrent.atomic.AtomicReference<>(null);
                java.util.concurrent.atomic.AtomicReference<Block> blockRef = new java.util.concurrent.atomic.AtomicReference<>(null);

                long timestamp = System.currentTimeMillis();
                String previousHash = blockchain.getLatestBlock().hash();
                int targetZeros = 6;
                int totalMachines = clusterNodes.size();

                List<Thread> vThreads = new ArrayList<>();
                for (int i = 0; i < totalMachines; i++) {
                    final int machineIndex = i;
                    final PeerNode machine = clusterNodes.get(i);
                    machine.setStatus("MINING");

                    Thread vt = Thread.ofVirtual().name("pow-racer-" + machine.id()).start(() -> {
                        MessageDigest md = HashUtil.getDigest();
                        byte[] basePrefix = (previousHash + ":" + timestamp + ":" + content + ":").getBytes(StandardCharsets.UTF_8);
                        int fullBytes = targetZeros / 2;
                        boolean oddNibble = (targetZeros % 2 != 0);

                        long n = machineIndex;
                        long checkCount = 0;

                        while (!cancelled.get() && !currentRaceCancelled.get() && !Thread.currentThread().isInterrupted()) {
                            md.reset();
                            md.update(basePrefix);
                            md.update(Long.toString(n).getBytes(StandardCharsets.US_ASCII));
                            byte[] digest = md.digest();

                            boolean match = true;
                            for (int b = 0; b < fullBytes; b++) {
                                if (digest[b] != 0) {
                                    match = false;
                                    break;
                                }
                            }
                            if (match && oddNibble) {
                                if ((digest[fullBytes] & 0xF0) != 0) {
                                    match = false;
                                }
                            }

                            if (match) {
                                if (cancelled.compareAndSet(false, true)) {
                                    String h = HashUtil.bytesToHex(digest);
                                    Block b = new Block(h, timestamp, previousHash, content, n);
                                    Block.registerBlockIndex(b, blockchain.size());
                                    winnerRef.set(machine);
                                    blockRef.set(b);
                                }
                                break;
                            }

                            n += totalMachines;
                            checkCount++;

                            // TỐC ĐỘ TÍNH TOÁN CHẬM, ĐỀU & DỄ QUAN SÁT:
                            // Cứ mỗi 512 hashes, thread tạm nghỉ 2ms (khoảng ~250 KH/s mỗi máy)
                            if ((checkCount & 0x1FF) == 0) {
                                try {
                                    Thread.sleep(2);
                                } catch (InterruptedException e) {
                                    break;
                                }
                            }
                        }
                    });
                    vThreads.add(vt);
                }

                for (Thread t : vThreads) {
                    try {
                        t.join();
                    } catch (InterruptedException ignored) {}
                }

                if (currentRaceCancelled.get()) {
                    for (PeerNode p : clusterNodes) {
                        p.setStatus("ONLINE");
                    }
                    Map<String, Object> cancelledMap = new HashMap<>();
                    cancelledMap.put("cancelled", true);
                    return cancelledMap;
                }

                duration = System.currentTimeMillis() - start;
                winnerNode = winnerRef.get();
                minedBlock = blockRef.get();

                if (winnerNode == null || minedBlock == null) {
                    winnerNode = clusterNodes.get(0);
                    minedBlock = blockchain.minePendingVotes();
                } else {
                    try {
                        winnerNode.blockchain().syncMinedBlock(minedBlock);
                    } catch (Exception ignored) {
                        winnerNode.blockchain().forceSyncChain(this.blockchain.getChain());
                    }
                }
            } else {
                // TÍNH TOÁN TRÊN 1 MÁY CHỈ ĐỊNH (CŨNG CHẠY TỐC ĐỘ CHẬM KIỂM SOÁT)
                final String targetId = singleNodeId;
                winnerNode = clusterNodes.stream()
                        .filter(m -> m.id().equalsIgnoreCase(targetId))
                        .findFirst()
                        .orElse(clusterNodes.get(0));

                winnerNode.setStatus("COMPUTING");
                logEvent("⚡ [XÁC THỰC ĐỘC LẬP] Máy " + winnerNode.name() + " bắt đầu giải thuật toán băm (Tốc độ chậm kiểm soát)...");

                java.util.concurrent.atomic.AtomicReference<Block> blockRef = new java.util.concurrent.atomic.AtomicReference<>(null);
                long timestamp = System.currentTimeMillis();
                String previousHash = blockchain.getLatestBlock().hash();
                int targetZeros = 6;

                Thread vt = Thread.ofVirtual().name("pow-single-" + winnerNode.id()).start(() -> {
                    MessageDigest md = HashUtil.getDigest();
                    byte[] basePrefix = (previousHash + ":" + timestamp + ":" + content + ":").getBytes(StandardCharsets.UTF_8);
                    int fullBytes = targetZeros / 2;
                    boolean oddNibble = (targetZeros % 2 != 0);

                    long n = 0;
                    long checkCount = 0;
                    while (!currentRaceCancelled.get() && !Thread.currentThread().isInterrupted()) {
                        md.reset();
                        md.update(basePrefix);
                        md.update(Long.toString(n).getBytes(StandardCharsets.US_ASCII));
                        byte[] digest = md.digest();

                        boolean match = true;
                        for (int b = 0; b < fullBytes; b++) {
                            if (digest[b] != 0) { match = false; break; }
                        }
                        if (match && oddNibble) {
                            if ((digest[fullBytes] & 0xF0) != 0) match = false;
                        }

                        if (match) {
                            String h = HashUtil.bytesToHex(digest);
                            Block b = new Block(h, timestamp, previousHash, content, n);
                            Block.registerBlockIndex(b, blockchain.size());
                            blockRef.set(b);
                            break;
                        }
                        n++;
                        checkCount++;
                        if ((checkCount & 0x1FF) == 0) {
                            try {
                                Thread.sleep(1);
                            } catch (InterruptedException e) {
                                break;
                            }
                        }
                    }
                });

                try {
                    vt.join();
                } catch (InterruptedException ignored) {}

                if (currentRaceCancelled.get()) {
                    winnerNode.setStatus("ONLINE");
                    Map<String, Object> cancelledMap = new HashMap<>();
                    cancelledMap.put("cancelled", true);
                    return cancelledMap;
                }

                duration = System.currentTimeMillis() - start;
                minedBlock = blockRef.get();
                if (minedBlock == null) {
                    minedBlock = winnerNode.blockchain().minePendingVotes();
                } else {
                    try {
                        winnerNode.blockchain().syncMinedBlock(minedBlock);
                    } catch (Exception ignored) {
                        winnerNode.blockchain().forceSyncChain(this.blockchain.getChain());
                    }
                }
            }

            // Ghi nhận thành tích của máy thắng
            winnerNode.incrementBlocksMined();
            winnerNode.setStatus("WINNER");
            this.latestWinnerId = winnerNode.id();

            // Cập nhật Sổ Cái chính từ winnerNode
            if (this.blockchain != winnerNode.blockchain()) {
                this.blockchain.forceSyncChain(winnerNode.blockchain().getChain());
            }

            // DỌN SẠCH CÁC PHIẾU BẦU ĐÃ ĐÓNG KHỐI TRÊN CẢ 10 MÁY VÀ SỔ CÁI CHÍNH
            this.blockchain.removeConfirmedVotesFromMempool(minedBlock.content());
            this.blockchain.purgeVotesAlreadyInLedger();
            for (PeerNode peer : clusterNodes) {
                peer.blockchain().removeConfirmedVotesFromMempool(minedBlock.content());
                peer.blockchain().purgeVotesAlreadyInLedger();
            }

            // Lưu sổ cái vào ổ cứng
            storage.saveChain(this.blockchain);

            // Phát tán tới 9 máy còn lại trong cụm P2P qua TCP Sockets simulation
            for (PeerNode peer : clusterNodes) {
                if (peer != winnerNode) {
                    peer.setStatus("SYNCED");
                    try {
                        peer.blockchain().syncMinedBlock(minedBlock);
                        logEvent("📡 [TCP GOSSIP] " + winnerNode.id() + " -> " + peer.id() + " (:" + peer.port() + "): SYNC_BLOCK #" + (this.blockchain.size() - 1));
                    } catch (Exception ex) {
                        peer.blockchain().forceSyncChain(this.blockchain.getChain());
                        logEvent("🔄 [P2P NAKAMOTO SYNC] " + peer.id() + " đã tự phục hồi và đồng bộ chuỗi dài nhất (#" + (this.blockchain.size() - 1) + ").");
                    }
                    peer.blockchain().removeConfirmedVotesFromMempool(minedBlock.content());
                    peer.blockchain().purgeVotesAlreadyInLedger();
                }
            }

            logEvent("🏆 [KẾT QUẢ] " + winnerNode.name() + " đã tìm được Hash hợp lệ đóng Block #" + (this.blockchain.size() - 1)
                    + " [Nonce: " + minedBlock.nonce() + " | Time: " + duration + " ms]!");

            Map<String, Object> res = new HashMap<>();
            res.put("success", true);
            res.put("winnerId", winnerNode.id());
            res.put("winnerName", winnerNode.name());
            res.put("winnerPort", winnerNode.port());
            res.put("nonce", minedBlock.nonce());
            res.put("hash", minedBlock.hash());
            res.put("blockIndex", this.blockchain.size() - 1);
            res.put("durationMs", duration);
            return res;
        } finally {
            isMining.set(false);
        }
    }

    private void handleClusterMine(HttpExchange exchange) throws IOException {
        if (handleCors(exchange)) return;

        String uri = exchange.getRequestURI().toString();
        boolean isRace = uri.contains("race=true");
        String nodeId = "";
        int nIdx = uri.indexOf("nodeId=");
        if (nIdx != -1) {
            int end = uri.indexOf("&", nIdx);
            nodeId = (end == -1) ? uri.substring(nIdx + 7) : uri.substring(nIdx + 7, end);
        }

        try {
            Map<String, Object> result = executeClusterRaceInternal(isRace ? null : nodeId);
            if (Boolean.TRUE.equals(result.get("cancelled"))) {
                sendResponse(exchange, 200, "application/json", "{\"success\":false,\"cancelled\":true,\"message\":\"Lượt tính toán đã bị hủy!\"}");
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("{");
            sb.append("\"success\":true,");
            sb.append("\"winnerId\":\"").append(escapeJson((String) result.get("winnerId"))).append("\",");
            sb.append("\"winnerName\":\"").append(escapeJson((String) result.get("winnerName"))).append("\",");
            sb.append("\"winnerPort\":").append(result.get("winnerPort")).append(",");
            sb.append("\"nonce\":").append(result.get("nonce")).append(",");
            sb.append("\"hash\":\"").append(escapeJson((String) result.get("hash"))).append("\",");
            sb.append("\"blockIndex\":").append(result.get("blockIndex")).append(",");
            sb.append("\"durationMs\":").append(result.get("durationMs"));
            sb.append("}");
            sendResponse(exchange, 200, "application/json", sb.toString());
        } catch (IllegalStateException e) {
            sendResponse(exchange, 409, "application/json", "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
        } catch (Exception e) {
            logEvent("❌ Lỗi cuộc đua tìm hash PoW: " + e.getMessage());
            sendResponse(exchange, 500, "application/json", "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
        }
    }

    private boolean handleCors(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return true;
        }
        return false;
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String contentType, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private String extractJsonField(String body, String fieldName, String defaultVal) {
        String key = "\"" + fieldName + "\"";
        int idx = body.indexOf(key);
        if (idx == -1) return defaultVal;
        int colon = body.indexOf(":", idx);
        if (colon == -1) return defaultVal;
        int start = colon + 1;
        while (start < body.length() && (body.charAt(start) == ' ' || body.charAt(start) == '\"')) {
            start++;
        }
        int end = start;
        while (end < body.length() && body.charAt(end) != '\"' && body.charAt(end) != ',' && body.charAt(end) != '}') {
            end++;
        }
        return body.substring(start, end).trim();
    }

    private String escapeJson(String raw) {
        if (raw == null) return "";
        return raw.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\b", "\\b")
                  .replace("\f", "\\f")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }

    @Override
    public void close() {
        httpServer.stop(0);
    }
}
