package com.pow.simulator.network;

import com.pow.simulator.core.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("P2P TCP Socket Cluster & Gossip Network Tests")
class P2PClusterNetworkTest {

    private P2PNode nodeA;
    private P2PNode nodeB;
    private P2PNode nodeC;

    @AfterEach
    void tearDown() {
        if (nodeA != null) nodeA.close();
        if (nodeB != null) nodeB.close();
        if (nodeC != null) nodeC.close();
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    @DisplayName("P2P nodes connect via TCP sockets, broadcast transactions, mine blocks and synchronize ledgers")
    void testEndToEndP2PNetworkWorkflow() throws IOException, InterruptedException {
        int initialDifficulty = 8; // Low difficulty for instantaneous unit test mining

        // 1. Start Node A and Node B on dynamic ephemeral ports (port 0)
        nodeA = new P2PNode("TestNode-A", 0, initialDifficulty);
        nodeB = new P2PNode("TestNode-B", 0, initialDifficulty);
        nodeA.start();
        nodeB.start();

        assertTrue(nodeA.getPort() > 0, "Node A must bind to a valid dynamic port");
        assertTrue(nodeB.getPort() > 0, "Node B must bind to a valid dynamic port");

        // 2. Connect Node B to Node A over TCP socket
        boolean connected = nodeB.connectToPeer("127.0.0.1", nodeA.getPort());
        assertTrue(connected, "Node B must successfully establish TCP socket connection to Node A");

        Thread.sleep(200);
        assertEquals(1, nodeA.getPeerCount(), "Node A should record 1 active peer connection");
        assertEquals(1, nodeB.getPeerCount(), "Node B should record 1 active peer connection");

        // 3. Broadcast Transaction from Node B -> verify Node A receives it into Mempool over TCP
        Transaction tx = Transaction.create("UserA", "UserB", 15.0);
        nodeB.broadcastTransaction(tx);

        Thread.sleep(200); // Allow TCP packet transmission
        assertTrue(nodeA.getMempool().contains(tx.txId()), "Node A must receive transaction via TCP gossip");

        // 4. Node A mines a block containing the transaction and broadcasts across the network
        var miningResult = nodeA.mineAndBroadcast();
        assertTrue(miningResult.success());
        assertEquals(2, nodeA.getBlockchain().size());

        Thread.sleep(300); // Allow TCP block transmission and remote validation

        // Verify Node B accepted and appended the block to its local ledger
        assertEquals(2, nodeB.getBlockchain().size(), "Node B must accept and append block via TCP");
        assertEquals(nodeA.getBlockchain().getLatestBlock().hash(),
                nodeB.getBlockchain().getLatestBlock().hash(),
                "Node B ledger tip hash must match Node A");

        // 5. Chain Synchronization: Node C starts with only Genesis, connects to Node A and syncs ledger
        nodeC = new P2PNode("TestNode-C", 0, initialDifficulty);
        nodeC.start();
        assertEquals(1, nodeC.getBlockchain().size());

        nodeC.connectToPeer("127.0.0.1", nodeA.getPort());
        Thread.sleep(300); // Allow HANDSHAKE, SYNC_REQUEST, and SYNC_RESPONSE over TCP

        assertEquals(2, nodeC.getBlockchain().size(), "Node C must synchronize full chain over TCP");
        assertEquals(nodeA.getBlockchain().getLatestBlock().hash(),
                nodeC.getBlockchain().getLatestBlock().hash(),
                "Node C synchronized tip hash must match Node A");
    }
}
