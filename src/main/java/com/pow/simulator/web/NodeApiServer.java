package com.pow.simulator.web;

import com.pow.simulator.consensus.MiningResult;
import com.pow.simulator.core.Block;
import com.pow.simulator.core.Blockchain;
import com.pow.simulator.core.Transaction;
import com.pow.simulator.network.P2PNode;
import com.pow.simulator.validation.BlockchainValidator;
import com.pow.simulator.validation.ValidationResult;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Embedded HTTP/REST API Server bridging Java P2P TCP Sockets with the Visual Web Dashboard.
 * Utilizes standard JDK HttpServer with zero external dependencies.
 */
public class NodeApiServer implements AutoCloseable {

    private final int httpPort;
    private final HttpServer httpServer;
    private final List<String> eventLogs = new CopyOnWriteArrayList<>();

    private P2PNode nodeA; // Miner (port 8001)
    private P2PNode nodeB; // Relay (port 8002)
    private P2PNode nodeC; // Wallet (port 8003)
    private P2PNode nodeD; // Late joiner (port 8004)

    private boolean isTampered = false;
    private int tamperedBlockIndex = -1;
    private String tamperFailureReason = "";

    public NodeApiServer(int httpPort) throws IOException {
        this.httpPort = httpPort;
        this.httpServer = HttpServer.create(new InetSocketAddress(httpPort), 0);

        initP2PNetwork();
        setupEndpoints();
        httpServer.setExecutor(java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor());
    }

    public synchronized void initP2PNetwork() {
        // Shutdown any existing nodes
        closeNodes();

        eventLogs.clear();
        isTampered = false;
        tamperedBlockIndex = -1;
        tamperFailureReason = "";

        int initialDiff = 10;
        try {
            nodeA = new P2PNode("Node-A (Miner)", 8001, initialDiff);
            nodeB = new P2PNode("Node-B (Relay)", 8002, initialDiff);
            nodeC = new P2PNode("Node-C (Wallet)", 8003, initialDiff);

            nodeA.setEventLogger(this::logEvent);
            nodeB.setEventLogger(this::logEvent);
            nodeC.setEventLogger(this::logEvent);

            nodeA.start();
            nodeB.start();
            nodeC.start();

            Thread.sleep(100);

            // Establish TCP socket connections
            nodeB.connectToPeer("127.0.0.1", 8001); // B -> A
            nodeC.connectToPeer("127.0.0.1", 8002); // C -> B

            logEvent("P2P Network Cluster initialized: Node-A (8001), Node-B (8002), Node-C (8003) connected via TCP Sockets.");
        } catch (Exception e) {
            logEvent("Error initializing P2P Cluster: " + e.getMessage());
        }
    }

    private void logEvent(String msg) {
        String timestamp = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
        eventLogs.add("[" + timestamp + "] " + msg);
        if (eventLogs.size() > 150) {
            eventLogs.removeFirst();
        }
    }

    private void setupEndpoints() {
        httpServer.createContext("/api/status", this::handleStatus);
        httpServer.createContext("/api/chain", this::handleChain);
        httpServer.createContext("/api/send-tx", this::handleSendTx);
        httpServer.createContext("/api/mine", this::handleMine);
        httpServer.createContext("/api/connect-node-d", this::handleConnectNodeD);
        httpServer.createContext("/api/tamper", this::handleTamper);
        httpServer.createContext("/api/reset", this::handleReset);
        httpServer.createContext("/", this::handleStatic);
    }

    public void start() {
        httpServer.start();
        logEvent("Web Visual Dashboard API listening on http://localhost:" + httpPort);
    }

    private void handleStatus(HttpExchange exchange) throws IOException {
        if (handleCors(exchange)) return;

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"isTampered\":").append(isTampered).append(",");
        sb.append("\"tamperedBlockIndex\":").append(tamperedBlockIndex).append(",");
        sb.append("\"tamperReason\":\"").append(escapeJson(tamperFailureReason)).append("\",");

        // Nodes array
        sb.append("\"nodes\":[");
        List<P2PNode> activeNodes = new ArrayList<>();
        if (nodeA != null) activeNodes.add(nodeA);
        if (nodeB != null) activeNodes.add(nodeB);
        if (nodeC != null) activeNodes.add(nodeC);
        if (nodeD != null && nodeD.isRunning()) activeNodes.add(nodeD);

        for (int i = 0; i < activeNodes.size(); i++) {
            P2PNode n = activeNodes.get(i);
            sb.append("{")
              .append("\"id\":\"").append(n.getNodeId()).append("\",")
              .append("\"port\":").append(n.getPort()).append(",")
              .append("\"chainHeight\":").append(n.getBlockchain().size()).append(",")
              .append("\"mempoolCount\":").append(n.getMempool().size()).append(",")
              .append("\"peerCount\":").append(n.getPeerCount()).append(",")
              .append("\"tipHash\":\"").append(n.getBlockchain().getLatestBlock().getShortHash()).append("\"")
              .append("}");
            if (i < activeNodes.size() - 1) sb.append(",");
        }
        sb.append("],");

        // Event logs
        sb.append("\"logs\":[");
        for (int i = 0; i < eventLogs.size(); i++) {
            sb.append("\"").append(escapeJson(eventLogs.get(i))).append("\"");
            if (i < eventLogs.size() - 1) sb.append(",");
        }
        sb.append("]");
        sb.append("}");

        sendResponse(exchange, 200, "application/json", sb.toString());
    }

    private void handleChain(HttpExchange exchange) throws IOException {
        if (handleCors(exchange)) return;

        List<Block> blocks = (nodeA != null) ? nodeA.getBlockchain().getChain() : List.of();
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < blocks.size(); i++) {
            Block b = blocks.get(i);
            sb.append("{")
              .append("\"index\":").append(b.index()).append(",")
              .append("\"timestamp\":").append(b.timestamp()).append(",")
              .append("\"previousHash\":\"").append(b.previousHash()).append("\",")
              .append("\"merkleRoot\":\"").append(b.merkleRoot()).append("\",")
              .append("\"nonce\":").append(b.nonce()).append(",")
              .append("\"difficultyBits\":").append(b.difficultyBits()).append(",")
              .append("\"hash\":\"").append(b.hash()).append("\",")
              .append("\"shortHash\":\"").append(b.getShortHash()).append("\",")
              .append("\"isCompromised\":").append(isTampered && b.index() >= tamperedBlockIndex).append(",")
              .append("\"transactions\":[");

            List<Transaction> txs = b.transactions();
            for (int j = 0; j < txs.size(); j++) {
                Transaction t = txs.get(j);
                sb.append("{")
                  .append("\"txId\":\"").append(t.txId()).append("\",")
                  .append("\"sender\":\"").append(escapeJson(t.sender())).append("\",")
                  .append("\"recipient\":\"").append(escapeJson(t.recipient())).append("\",")
                  .append("\"amount\":").append(t.amount()).append(",")
                  .append("\"timestamp\":").append(t.timestamp())
                  .append("}");
                if (j < txs.size() - 1) sb.append(",");
            }
            sb.append("]}");
            if (i < blocks.size() - 1) sb.append(",");
        }
        sb.append("]");

        sendResponse(exchange, 200, "application/json", sb.toString());
    }

    private void handleSendTx(HttpExchange exchange) throws IOException {
        if (handleCors(exchange)) return;
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "application/json", "{\"error\":\"POST required\"}");
            return;
        }

        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String sender = extractJsonField(body, "sender", "Alice_" + System.currentTimeMillis() % 1000);
        String recipient = extractJsonField(body, "recipient", "Merchant_XYZ");
        double amount = 10.0;
        try {
            amount = Double.parseDouble(extractJsonField(body, "amount", "10.0"));
        } catch (Exception ignored) {}

        Transaction tx = Transaction.create(sender, recipient, amount);
        if (nodeC != null) {
            nodeC.broadcastTransaction(tx);
        }

        String json = "{\"success\":true,\"txId\":\"" + tx.txId() + "\",\"sender\":\"" + escapeJson(sender)
                + "\",\"recipient\":\"" + escapeJson(recipient) + "\",\"amount\":" + amount + "}";
        sendResponse(exchange, 200, "application/json", json);
    }

    private void handleMine(HttpExchange exchange) throws IOException {
        if (handleCors(exchange)) return;
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "application/json", "{\"error\":\"POST required\"}");
            return;
        }

        if (nodeA == null) {
            sendResponse(exchange, 500, "application/json", "{\"error\":\"Node A is not running\"}");
            return;
        }

        MiningResult result = nodeA.mineAndBroadcast();
        String json = String.format("{\"success\":%b,\"blockIndex\":%d,\"nonce\":%d,\"hash\":\"%s\",\"durationMs\":%d,\"hashrate\":%.2f}",
                result.success(),
                result.minedBlock() != null ? result.minedBlock().index() : -1,
                result.minedBlock() != null ? result.minedBlock().nonce() : 0,
                result.minedBlock() != null ? result.minedBlock().getShortHash() : "",
                result.durationMs(),
                result.hashrate());

        sendResponse(exchange, 200, "application/json", json);
    }

    private void handleConnectNodeD(HttpExchange exchange) throws IOException {
        if (handleCors(exchange)) return;
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "application/json", "{\"error\":\"POST required\"}");
            return;
        }

        try {
            if (nodeD == null || !nodeD.isRunning()) {
                nodeD = new P2PNode("Node-D (Sync)", 8004, 10);
                nodeD.setEventLogger(this::logEvent);
                nodeD.start();
                Thread.sleep(100);
            }

            nodeD.connectToPeer("127.0.0.1", 8001); // Connect to Node A
            Thread.sleep(300); // Allow TCP Handshake & Sync

            String json = "{\"success\":true,\"nodeDHeight\":" + nodeD.getBlockchain().size()
                    + ",\"tipHash\":\"" + nodeD.getBlockchain().getLatestBlock().getShortHash() + "\"}";
            sendResponse(exchange, 200, "application/json", json);
        } catch (Exception e) {
            sendResponse(exchange, 500, "application/json", "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
        }
    }

    private void handleTamper(HttpExchange exchange) throws IOException {
        if (handleCors(exchange)) return;
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "application/json", "{\"error\":\"POST required\"}");
            return;
        }

        if (nodeA == null || nodeA.getBlockchain().size() < 2) {
            sendResponse(exchange, 400, "application/json", "{\"error\":\"Need at least 2 blocks to simulate attack\"}");
            return;
        }

        int targetIndex = 1;
        Block victim = nodeA.getBlockchain().getBlock(targetIndex);

        // Forged transaction replacing original
        Transaction hackedTx = new Transaction(
                "hacked-tx-" + System.currentTimeMillis(),
                "VictimVault",
                "AttackerWallet",
                999999.0,
                System.currentTimeMillis()
        );

        Block tamperedBlock = new Block(
                victim.index(),
                victim.timestamp(),
                victim.previousHash(),
                victim.merkleRoot(), // keep stale root
                List.of(hackedTx),
                victim.nonce(),
                victim.difficultyBits(),
                victim.hash()
        );

        nodeA.getBlockchain().setBlockForSimulation(targetIndex, tamperedBlock);
        ValidationResult audit = BlockchainValidator.validate(nodeA.getBlockchain());

        isTampered = true;
        tamperedBlockIndex = targetIndex;
        tamperFailureReason = audit.failureReason();

        logEvent("⚠️ [ATTACK SIMULATION] Block #" + targetIndex + " was modified maliciously!");
        logEvent("❌ [AUDIT DETECTED] " + audit.failureReason());

        String json = "{\"success\":true,\"targetIndex\":" + targetIndex
                + ",\"isValid\":" + audit.isValid()
                + ",\"failureReason\":\"" + escapeJson(audit.failureReason()) + "\"}";

        sendResponse(exchange, 200, "application/json", json);
    }

    private void handleReset(HttpExchange exchange) throws IOException {
        if (handleCors(exchange)) return;
        initP2PNetwork();
        sendResponse(exchange, 200, "application/json", "{\"success\":true,\"message\":\"Network cluster reset successfully\"}");
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

    private void sendResponse(HttpExchange exchange, int statusCode, String contentType, String content) throws IOException {
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.getResponseHeaders().set("Cache-Control", "no-cache");
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

    private void closeNodes() {
        if (nodeA != null) nodeA.close();
        if (nodeB != null) nodeB.close();
        if (nodeC != null) nodeC.close();
        if (nodeD != null) nodeD.close();
    }

    @Override
    public void close() {
        closeNodes();
        httpServer.stop(0);
    }
}
