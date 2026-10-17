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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Máy chủ HTTP/REST API cho Hệ Thống Bầu Cử Điện Tử Blockchain (E-Voting).
 * Quản lý hòm phiếu Mempool, Sổ Cái 5 thuộc tính, và Lưu trữ ổ cứng.
 */
public class NodeApiServer implements AutoCloseable {

    private final int httpPort;
    private final HttpServer httpServer;
    private final List<String> eventLogs = new CopyOnWriteArrayList<>();
    private final LedgerStorage storage = LedgerStorage.defaultInstance();

    private Blockchain blockchain;
    private final List<String> candidates = List.of(
            "Lê Quang Minh (Đại biểu Khoa CNTT)",
            "Nguyễn Đỗ Trung Lương (Đại biểu Ban Học Tập)",
            "Nguyễn Hoàng Nhật (Đại biểu Ban Phong Trào)"
    );

    private boolean isTampered = false;
    private int tamperedBlockIndex = -1;
    private String tamperFailureReason = "";

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

        httpServer.createContext("/", this::handleStatic);
    }

    public void start() {
        httpServer.start();
        logEvent("Cổng Web Bầu Cử Blockchain sẵn sàng tại: http://localhost:" + httpPort);
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
            blockchain.addVoteToMempool(vote);
            logEvent("🗳️ [MEMPOOL] Cử tri '" + voterId + "' vừa gửi phiếu bầu cho: " + candidate);

            sendResponse(exchange, 200, "application/json", "{\"success\":true,\"message\":\"Đã nhận phiếu bầu vào Mempool thành công!\"}");
        } catch (IllegalArgumentException e) {
            logEvent("⚠️ [TỪ CHỐI BỎ PHIẾU] " + e.getMessage());
            sendResponse(exchange, 400, "application/json", "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
        } catch (Exception e) {
            sendResponse(exchange, 500, "application/json", "{\"error\":\"Lỗi hệ thống: " + escapeJson(e.getMessage()) + "\"}");
        }
    }

    private void handleElectionMine(HttpExchange exchange) throws IOException {
        if (handleCors(exchange)) return;

        try {
            int pendingCount = blockchain.getMempool().size();
            if (pendingCount == 0) {
                sendResponse(exchange, 400, "application/json", "{\"error\":\"Hòm phiếu Mempool đang trống! Hãy bỏ phiếu trước khi khai thác block mới.\"}");
                return;
            }

            logEvent("⛏️ [POW MINING] Bắt đầu đào Block mới để đóng gói " + pendingCount + " phiếu bầu...");
            long start = System.currentTimeMillis();

            Block minedBlock = blockchain.minePendingVotes();
            long duration = System.currentTimeMillis() - start;

            // Tự động lưu chuỗi khối vào ổ cứng
            storage.saveChain(blockchain);

            logEvent("🎉 [ĐÃ ĐÀO KHỐI] Block mới # " + (blockchain.size() - 1)
                    + " [Nonce: " + minedBlock.nonce() + " | Hash: " + minedBlock.getShortHash()
                    + " | Thời gian: " + duration + " ms] đã lưu vào Sổ Cái vĩnh viễn!");

            String json = "{\"success\":true,\"hash\":\"" + escapeJson(minedBlock.hash())
                    + "\",\"time\":\"" + escapeJson(minedBlock.time())
                    + "\",\"preHash\":\"" + escapeJson(minedBlock.previousHash())
                    + "\",\"noiDung\":\"" + escapeJson(minedBlock.content())
                    + "\",\"nonce\":" + minedBlock.nonce() + "}";
            sendResponse(exchange, 200, "application/json", json);
        } catch (Exception e) {
            logEvent("❌ Lỗi khai thác block: " + e.getMessage());
            sendResponse(exchange, 500, "application/json", "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
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
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        int diff = 3;
        try {
            diff = Integer.parseInt(extractJsonField(body, "difficulty", "3"));
        } catch (Exception ignored) {}
        if (diff < 1) diff = 1;
        if (diff > 6) diff = 6;

        blockchain.setDifficultyLeadingZeros(diff);
        logEvent("⚙️ [CẤU HÌNH ĐỘ KHÓ] Đã đổi độ khó PoW thành: " + diff + " số 0 (" + "0".repeat(diff) + "...)");
        sendResponse(exchange, 200, "application/json", "{\"success\":true,\"difficulty\":" + diff + ",\"targetPrefix\":\"" + "0".repeat(diff) + "\"}");
    }

    private void handleElectionReset(HttpExchange exchange) throws IOException {
        if (handleCors(exchange)) return;
        initBlockchain();
        logEvent("🔄 [RESET] Sổ Cái Bầu Cử đã được khởi động lại sạch sẽ.");
        sendResponse(exchange, 200, "application/json", "{\"success\":true,\"message\":\"Đã làm mới sổ cái thành công!\"}");
    }

    private void handleStatic(HttpExchange exchange) throws IOException {
        String html = DashboardHtmlTemplate.getHtml();
        sendResponse(exchange, 200, "text/html; charset=utf-8", html);
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
