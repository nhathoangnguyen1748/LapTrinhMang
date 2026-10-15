package com.pow.simulator.network;

import com.pow.simulator.core.Transaction;

import java.util.concurrent.TimeUnit;

/**
 * Interactive demonstration of the distributed P2P TCP Socket network cluster.
 * Showcases:
 * 1. Multi-threaded TCP ServerSocket initialization on distinct ports.
 * 2. Asynchronous duplex TCP PeerConnection socket handshakes.
 * 3. Multi-hop Gossip Protocol transaction propagation.
 * 4. PoW Block mining and real-time TCP socket broadcasting.
 * 5. Nakamoto Consensus Longest-Chain synchronization across network nodes.
 */
public class NetworkDemo {

    public static void runDemo() {
        System.out.println("""
            =============================================================
             DISTRIBUTED P2P BLOCKCHAIN NETWORK (TCP SOCKETS CLUSTER)
                     LẬP TRÌNH MẠNG  |  JAVA 21 VIRTUAL THREADS
            =============================================================
            * Reliable Length-Prefixed Stream Framing over TCP Sockets
            * Asynchronous ServerSocket Concurrency using Virtual Threads
            * Multi-Hop Gossip Protocol Transaction Propagation
            * Block Broadcasting & Ledger Audit across Peer Nodes
            * Distributed Nakamoto Consensus (Chain Sync over Network)
            =============================================================""");

        int initialDiff = 10; // Fast mining for network responsiveness

        try {
            // 1. Initialize 3 P2P Nodes on separate TCP ports
            System.out.println("\n[STEP 1: INITIALIZING P2P NODES ON TCP PORTS]");
            P2PNode nodeA = new P2PNode("Node-A (Miner)", 8001, initialDiff);
            P2PNode nodeB = new P2PNode("Node-B (Relay)", 8002, initialDiff);
            P2PNode nodeC = new P2PNode("Node-C (Wallet)", 8003, initialDiff);

            nodeA.start();
            nodeB.start();
            nodeC.start();

            Thread.sleep(100);

            // 2. Connect peers over real TCP Sockets
            System.out.println("\n[STEP 2: ESTABLISHING TCP P2P PEER CONNECTIONS]");
            nodeB.connectToPeer("127.0.0.1", 8001); // Node B connects to Node A
            nodeC.connectToPeer("127.0.0.1", 8002); // Node C connects to Node B

            Thread.sleep(200);

            // 3. Multi-hop transaction propagation (Node-C -> Node-B -> Node-A)
            System.out.println("\n[STEP 3: MULTI-HOP GOSSIP TRANSACTION PROPAGATION]");
            Transaction tx1 = Transaction.create("Wallet_User_1", "Merchant_ABC", 12.50);
            System.out.println("Node-C creates new Transaction: " + tx1);
            nodeC.broadcastTransaction(tx1);

            Thread.sleep(300); // Allow TCP propagation across hops

            System.out.printf("  -> Mempool Node-C: %d txs | Node-B: %d txs | Node-A: %d txs\n",
                    nodeC.getMempool().size(), nodeB.getMempool().size(), nodeA.getMempool().size());

            // 4. Node-A mines block from mempool and broadcasts over TCP
            System.out.println("\n[STEP 4: POW MINING & NETWORK BLOCK BROADCASTING]");
            System.out.println("Node-A triggers mining of Block #1...");
            nodeA.mineAndBroadcast();

            Thread.sleep(400); // Allow TCP block transmission and peer verification

            System.out.println("\n[LEDGER STATE ACROSS NETWORK AFTER BLOCK BROADCAST]");
            System.out.printf("  * Node-A Ledger Height: %d | Tip Hash: %s\n",
                    nodeA.getBlockchain().size(), nodeA.getBlockchain().getLatestBlock().getShortHash());
            System.out.printf("  * Node-B Ledger Height: %d | Tip Hash: %s\n",
                    nodeB.getBlockchain().size(), nodeB.getBlockchain().getLatestBlock().getShortHash());
            System.out.printf("  * Node-C Ledger Height: %d | Tip Hash: %s\n",
                    nodeC.getBlockchain().size(), nodeC.getBlockchain().getLatestBlock().getShortHash());

            // 5. Late-joining Node-D synchronizing chain over TCP
            System.out.println("\n[STEP 5: DISTRIBUTED CHAIN SYNCHRONIZATION (NODE-D JOINS LATE)]");
            P2PNode nodeD = new P2PNode("Node-D (Newbie)", 8004, initialDiff);
            nodeD.start();
            System.out.printf("Node-D started with only Genesis Block (Height: %d)\n", nodeD.getBlockchain().size());

            System.out.println("Node-D connects to Node-A via TCP socket...");
            nodeD.connectToPeer("127.0.0.1", 8001);

            Thread.sleep(400); // Allow HANDSHAKE, SYNC_REQUEST and SYNC_RESPONSE over TCP

            System.out.printf("Node-D Ledger Height after TCP Sync: %d | Tip Hash: %s\n",
                    nodeD.getBlockchain().size(), nodeD.getBlockchain().getLatestBlock().getShortHash());

            // Clean shutdown of all nodes
            System.out.println("\n[STEP 6: CLEAN SHUTDOWN OF P2P NETWORK CLUSTER]");
            nodeA.close();
            nodeB.close();
            nodeC.close();
            nodeD.close();
            System.out.println("All TCP server sockets and peer connections closed successfully.\n");

        } catch (Exception e) {
            System.err.println("Error in P2P Network Demo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        runDemo();
    }
}
