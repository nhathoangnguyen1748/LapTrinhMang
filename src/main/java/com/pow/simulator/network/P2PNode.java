package com.pow.simulator.network;

import com.pow.simulator.consensus.MiningResult;
import com.pow.simulator.consensus.MultiThreadedMiner;
import com.pow.simulator.consensus.TargetMatcher;
import com.pow.simulator.core.Block;
import com.pow.simulator.core.Blockchain;
import com.pow.simulator.core.MerkleTree;
import com.pow.simulator.core.Transaction;
import com.pow.simulator.difficulty.DifficultyAdjuster;
import com.pow.simulator.validation.BlockchainValidator;
import com.pow.simulator.validation.ValidationResult;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Decentralized Peer-to-Peer (P2P) Node operating over TCP sockets.
 * Combines local blockchain storage, mempool, multi-threaded virtual-thread mining,
 * TCP server socket listening, and Gossip Protocol message routing.
 */
public class P2PNode implements AutoCloseable {

    private final String nodeId;
    private final int requestedPort;
    private final Blockchain blockchain;
    private final Mempool mempool;
    private final MultiThreadedMiner miner;
    private final DifficultyAdjuster difficultyAdjuster;
    private final Map<String, PeerConnection> connectedPeers = new ConcurrentHashMap<>();
    private final Set<String> seenMessageIds = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    private NodeServer server;
    private Consumer<String> eventLogger = System.out::println;

    public P2PNode(String nodeId, int port, int initialDifficultyBits) {
        this.nodeId = Objects.requireNonNull(nodeId, "nodeId must not be null");
        this.requestedPort = port;
        this.blockchain = Blockchain.withDefaultGenesis(initialDifficultyBits);
        this.mempool = new Mempool();
        this.miner = new MultiThreadedMiner(MultiThreadedMiner.ExecutionMode.VIRTUAL_THREADS, 4);
        this.difficultyAdjuster = new DifficultyAdjuster(1500L, 5, 4.0, 0.25, 4, 32);
    }

    public P2PNode(String nodeId, int port, Blockchain existingChain) {
        this.nodeId = Objects.requireNonNull(nodeId, "nodeId must not be null");
        this.requestedPort = port;
        this.blockchain = Objects.requireNonNull(existingChain, "existingChain must not be null");
        this.mempool = new Mempool();
        this.miner = new MultiThreadedMiner(MultiThreadedMiner.ExecutionMode.VIRTUAL_THREADS, 4);
        this.difficultyAdjuster = new DifficultyAdjuster(1500L, 5, 4.0, 0.25, 4, 32);
    }

    public void setEventLogger(Consumer<String> logger) {
        this.eventLogger = (logger != null) ? logger : s -> {};
    }

    /**
     * Starts the P2P Node's TCP ServerSocket.
     */
    public synchronized void start() throws IOException {
        if (isRunning.compareAndSet(false, true)) {
            this.server = new NodeServer(requestedPort, this::registerIncomingPeer);
            log(String.format("[%s] P2P TCP Server started on port %d | Ledger Height: %d",
                    nodeId, server.getPort(), blockchain.size()));
        }
    }

    /**
     * Initiates an outbound TCP connection to a remote peer node.
     */
    public boolean connectToPeer(String host, int peerPort) {
        try {
            Socket socket = new Socket();
            socket.setTcpNoDelay(true);
            socket.connect(new InetSocketAddress(host, peerPort), 3000);

            PeerConnection peer = new PeerConnection(socket);
            setupPeerConnection(peer);

            // Send initial HANDSHAKE packet
            String handshakePayload = blockchain.size() + ":" + blockchain.getLatestBlock().hash();
            peer.send(NetworkMessage.create(MessageType.HANDSHAKE, nodeId, handshakePayload));

            log(String.format("[%s] Outbound TCP socket connected to peer %s:%d", nodeId, host, peerPort));
            return true;
        } catch (IOException e) {
            log(String.format("[%s] Failed to connect to peer %s:%d: %s", nodeId, host, peerPort, e.getMessage()));
            return false;
        }
    }

    private void registerIncomingPeer(PeerConnection peer) {
        log(String.format("[%s] Inbound TCP socket accepted from %s", nodeId, peer.getRemoteAddress()));
        setupPeerConnection(peer);
    }

    private void setupPeerConnection(PeerConnection peer) {
        String peerKey = peer.getRemoteAddress();
        connectedPeers.put(peerKey, peer);

        peer.startListening(
                msg -> handleMessage(peer, msg),
                disconnected -> {
                    connectedPeers.remove(peerKey);
                    log(String.format("[%s] Peer disconnected: %s", nodeId, peerKey));
                }
        );
    }

    /**
     * Core message routing and Gossip Protocol logic.
     */
    public void handleMessage(PeerConnection peer, NetworkMessage msg) {
        // Prevent broadcast loops / duplicate processing
        if (!seenMessageIds.add(msg.messageId())) {
            return;
        }

        switch (msg.type()) {
            case HANDSHAKE -> {
                peer.setRemoteNodeId(msg.senderNodeId());
                int remoteHeight = parseChainHeight(msg.payload());
                log(String.format("[%s] Received HANDSHAKE from %s (Chain height: %d)",
                        nodeId, msg.senderNodeId(), remoteHeight));

                // Reply with HANDSHAKE_ACK
                try {
                    String ackPayload = blockchain.size() + ":" + blockchain.getLatestBlock().hash();
                    peer.send(NetworkMessage.create(MessageType.HANDSHAKE_ACK, nodeId, ackPayload));
                } catch (IOException ignored) {}

                // If remote node has a longer chain, request chain sync
                if (remoteHeight > blockchain.size()) {
                    requestChainSync(peer);
                }
            }

            case HANDSHAKE_ACK -> {
                peer.setRemoteNodeId(msg.senderNodeId());
                int remoteHeight = parseChainHeight(msg.payload());
                log(String.format("[%s] Received HANDSHAKE_ACK from %s (Chain height: %d)",
                        nodeId, msg.senderNodeId(), remoteHeight));

                if (remoteHeight > blockchain.size()) {
                    requestChainSync(peer);
                }
            }

            case TX_BROADCAST -> {
                try {
                    Transaction tx = Transaction.fromNetworkString(msg.payload());
                    if (mempool.add(tx)) {
                        log(String.format("[%s] TCP received valid Transaction %s (Sender: %s, Amount: %.2f)",
                                nodeId, tx.txId().substring(0, 8), tx.sender(), tx.amount()));
                        // Gossip transaction to other connected peers
                        gossip(msg, peer.getRemoteAddress());
                    }
                } catch (Exception e) {
                    log(String.format("[%s] Rejected malformed transaction from %s: %s", nodeId, peer, e.getMessage()));
                }
            }

            case BLOCK_BROADCAST -> {
                try {
                    Block block = Block.fromNetworkString(msg.payload());
                    handleIncomingBlock(peer, msg, block);
                } catch (Exception e) {
                    log(String.format("[%s] Rejected malformed block from %s: %s", nodeId, peer, e.getMessage()));
                }
            }

            case SYNC_REQUEST -> {
                log(String.format("[%s] Handling SYNC_REQUEST from %s", nodeId, msg.senderNodeId()));
                try {
                    String chainData = blockchain.serializeChain();
                    peer.send(NetworkMessage.create(MessageType.SYNC_RESPONSE, nodeId, chainData));
                } catch (IOException e) {
                    log(String.format("[%s] Failed to send SYNC_RESPONSE to %s: %s", nodeId, peer, e.getMessage()));
                }
            }

            case SYNC_RESPONSE -> {
                log(String.format("[%s] Received SYNC_RESPONSE from %s", nodeId, msg.senderNodeId()));
                handleChainSyncResponse(msg.payload());
            }

            case PING -> {
                try {
                    peer.send(NetworkMessage.create(MessageType.PONG, nodeId, ""));
                } catch (IOException ignored) {}
            }

            case PONG -> {
                // Heartbeat confirmed
            }
        }
    }

    private void handleIncomingBlock(PeerConnection peer, NetworkMessage msg, Block block) {
        Block currentTip = blockchain.getLatestBlock();

        // Check if block extends our current chain
        if (block.previousHash().equals(currentTip.hash())) {
            // Validate Proof of Work target
            if (!TargetMatcher.matches(block.hash(), block.difficultyBits())) {
                log(String.format("[%s] Block rejected: PoW does not satisfy target difficulty", nodeId));
                return;
            }

            // Validate Merkle Root if block has transactions
            if (!block.transactions().isEmpty()) {
                String calculatedMerkle = MerkleTree.calculateRoot(block.transactions());
                if (!calculatedMerkle.equals(block.merkleRoot())) {
                    log(String.format("[%s] Block rejected: Merkle Root mismatch", nodeId));
                    return;
                }
            }

            // Validate header hash recalculation
            String recalculatedHash = Block.calculateHash(block.previousHash(), block.timestamp(), block.content(), block.nonce());
            if (!recalculatedHash.equals(block.hash())) {
                log(String.format("[%s] Block rejected: Hash recalculation mismatch", nodeId));
                return;
            }

            // Valid block: append to local chain
            blockchain.addBlock(block);
            mempool.removeAll(block.transactions());
            log(String.format("[%s] ACCEPTED Block #%d via TCP broadcast! [Hash: %s, Ledger Height: %d]",
                    nodeId, blockchain.size() - 1, block.getShortHash(), blockchain.size()));

            // Gossip block to all other peers
            gossip(msg, peer.getRemoteAddress());

        } else if (!blockchain.hasBlock(block.hash())) {
            log(String.format("[%s] Received block ahead of current tip or on fork. Requesting chain synchronization...", nodeId));
            requestChainSync(peer);
        }
    }

    private synchronized void handleChainSyncResponse(String chainPayload) {
        List<Block> remoteBlocks = Blockchain.deserializeBlocks(chainPayload);
        if (remoteBlocks.isEmpty()) return;

        // Build a temporary chain to validate remote chain
        Blockchain candidateChain = new Blockchain(remoteBlocks.getFirst());
        for (int i = 1; i < remoteBlocks.size(); i++) {
            candidateChain.addBlock(remoteBlocks.get(i));
        }

        ValidationResult audit = BlockchainValidator.validate(candidateChain);
        if (audit.isValid() && candidateChain.size() > blockchain.size()) {
            blockchain.replaceChain(remoteBlocks);
            log(String.format("[%s] NAKAMOTO CONSENSUS: Local chain replaced with longer valid chain (Height: %d)!",
                    nodeId, blockchain.size()));
        } else {
            log(String.format("[%s] Chain synchronization rejected: %s", nodeId, audit.failureReason()));
        }
    }

    public void requestChainSync(PeerConnection peer) {
        try {
            peer.send(NetworkMessage.create(MessageType.SYNC_REQUEST, nodeId, ""));
            log(String.format("[%s] Dispatched SYNC_REQUEST to peer %s", nodeId, peer.getRemoteAddress()));
        } catch (IOException e) {
            log(String.format("[%s] Failed to send SYNC_REQUEST: %s", nodeId, e.getMessage()));
        }
    }

    /**
     * Broadcasts a new transaction created by this node to all connected peers.
     */
    public void broadcastTransaction(Transaction tx) {
        mempool.add(tx);
        NetworkMessage msg = NetworkMessage.create(MessageType.TX_BROADCAST, nodeId, tx.toNetworkString());
        seenMessageIds.add(msg.messageId());
        log(String.format("[%s] Broadcasting new Transaction %s across TCP gossip network...", nodeId, tx.txId().substring(0, 8)));
        gossip(msg, null);
    }

    /**
     * Mines the next block from mempool transactions and broadcasts the block across the network.
     */
    public MiningResult mineAndBroadcast() {
        List<Transaction> txsToMine = mempool.take(10);
        int targetDiff = difficultyAdjuster.getNextDifficulty(blockchain);
        Block prev = blockchain.getLatestBlock();

        Block template = Block.createTemplate(
                blockchain.size(),
                System.currentTimeMillis(),
                prev.hash(),
                txsToMine,
                targetDiff
        );

        log(String.format("[%s] Mining Block #%d with Java Virtual Threads (Diff: %d bits, Txs: %d)...",
                nodeId, template.index(), targetDiff, txsToMine.size()));

        MiningResult result = miner.mine(template);
        if (result.success()) {
            blockchain.addBlock(result.minedBlock());
            mempool.removeAll(txsToMine);

            log(String.format("[%s] MINED Block #%d! Nonce: %d | Time: %d ms | Hash: %s",
                    nodeId, result.minedBlock().index(), result.minedBlock().nonce(),
                    result.durationMs(), result.minedBlock().getShortHash()));

            // Broadcast block to all peers
            NetworkMessage msg = NetworkMessage.create(
                    MessageType.BLOCK_BROADCAST,
                    nodeId,
                    result.minedBlock().toNetworkString()
            );
            seenMessageIds.add(msg.messageId());
            gossip(msg, null);
        }
        return result;
    }

    private void gossip(NetworkMessage msg, String excludeAddress) {
        for (Map.Entry<String, PeerConnection> entry : connectedPeers.entrySet()) {
            if (excludeAddress != null && entry.getKey().equals(excludeAddress)) {
                continue;
            }
            try {
                entry.getValue().send(msg);
            } catch (IOException e) {
                log(String.format("[%s] Gossip error sending to %s: %s", nodeId, entry.getKey(), e.getMessage()));
            }
        }
    }

    private int parseChainHeight(String payload) {
        try {
            int colon = payload.indexOf(':');
            return (colon != -1) ? Integer.parseInt(payload.substring(0, colon)) : Integer.parseInt(payload);
        } catch (Exception e) {
            return 0;
        }
    }

    private void log(String message) {
        eventLogger.accept(message);
    }

    public String getNodeId() {
        return nodeId;
    }

    public int getPort() {
        return (server != null) ? server.getPort() : requestedPort;
    }

    public Blockchain getBlockchain() {
        return blockchain;
    }

    public Mempool getMempool() {
        return mempool;
    }

    public int getPeerCount() {
        return connectedPeers.size();
    }

    public boolean isRunning() {
        return isRunning.get();
    }

    @Override
    public void close() {
        if (isRunning.compareAndSet(true, false)) {
            for (PeerConnection peer : connectedPeers.values()) {
                peer.close();
            }
            connectedPeers.clear();
            if (server != null) {
                server.close();
            }
            log(String.format("[%s] P2P Node stopped.", nodeId));
        }
    }
}
