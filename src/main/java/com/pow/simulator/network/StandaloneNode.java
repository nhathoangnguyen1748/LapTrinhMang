package com.pow.simulator.network;

import com.pow.simulator.core.Block;
import com.pow.simulator.core.Transaction;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Scanner;

/**
 * Khoi chay 1 Node P2P doc lap tren may tinh ca nhan.
 * Ho tro chay phan tan tren nhieu may tinh khac nhau qua LAN, Wi-Fi hoac Internet (Tailscale/Ngrok).
 */
public class StandaloneNode {

    public static void main(String[] args) {
        System.out.println("=========================================================================");
        System.out.println("   KHOI CHAY NUT MANG P2P BLOCKCHAIN DOC LAP (STANDALONE P2P NODE)       ");
        System.out.println("   Nhom: Nguyen Hoang Nhat - Nguyen Do Trung Luong - Le Quang Minh       ");
        System.out.println("=========================================================================");

        Scanner scanner = new Scanner(System.in);
        String nodeId;
        int port;
        String peerHost = "";
        int peerPort = 0;

        if (args != null && args.length >= 1 && "seed".equalsIgnoreCase(args[0])) {
            nodeId = "Seed-Gateway (Hoang Nhat)";
            port = (args.length >= 2) ? Integer.parseInt(args[1]) : 8001;
            System.out.println("-> [CHE DO SEED NODE] Khoi chay lam may chu Gateway tai cong " + port);
        } else if (args != null && args.length >= 1 && ("auto".equalsIgnoreCase(args[0]) || "peer".equalsIgnoreCase(args[0]))) {
            nodeId = "Peer-" + System.getProperty("user.name", "Worker") + "-" + (System.currentTimeMillis() % 1000);
            port = 0; // Tu dong chon cong trong
            java.io.File seedFile = new java.io.File("peer_seed.txt");
            if (seedFile.exists()) {
                try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(seedFile, java.nio.charset.StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (line.isEmpty() || line.startsWith("#")) continue;
                        String[] parts = line.split(":");
                        if (parts.length >= 2) {
                            peerHost = parts[0].trim();
                            peerPort = Integer.parseInt(parts[1].trim());
                            break;
                        }
                    }
                } catch (Exception e) {
                    System.out.println("[!] Khong doc duoc peer_seed.txt: " + e.getMessage());
                }
            }
            if (peerHost.isEmpty()) {
                peerHost = "127.0.0.1";
                peerPort = 8001;
            }
            System.out.println("-> [CHE DO TU DONG THAM GIA NODE - 1 CLICK]");
            System.out.printf("   Ten Node: %s | Dang ket noi toi Seed: %s:%d\n", nodeId, peerHost, peerPort);
        } else if (args != null && args.length >= 2) {
            nodeId = args[0];
            port = Integer.parseInt(args[1]);
            if (args.length >= 4) {
                peerHost = args[2];
                peerPort = Integer.parseInt(args[3]);
            }
        } else {
            System.out.println("\nChon vai tro may cua ban trong nhom 3 nguoi:");
            System.out.println("  [1] May 1 - Nguyen Hoang Nhat (Tram Gateway & Dieu Phoi - Port 8001)");
            System.out.println("  [2] May 2 - Nguyen Do Trung Luong (Nut Tho Dao PoW - Port 8002)");
            System.out.println("  [3] May 3 - Le Quang Minh (Nut Kiem Toan & So Cai - Port 8003)");
            System.out.println("  [4] Tuy chinh ten Node va Cong TCP");
            System.out.println("  [5] Tu dong tham gia tu file cau hinh peer_seed.txt");
            System.out.print("\n-> Nhap lua chon cua ban (1-5) [Mac dinh 1]: ");
            String choice = scanner.nextLine().trim();

            if ("2".equals(choice)) {
                nodeId = "Node-2 (Trung Luong)";
                port = 8002;
                System.out.print("-> Nhap dia chi IP cua May 1 (Nhan Enter neu la localhost): ");
                peerHost = scanner.nextLine().trim();
                if (peerHost.isEmpty()) peerHost = "127.0.0.1";
                peerPort = 8001;
            } else if ("3".equals(choice)) {
                nodeId = "Node-3 (Quang Minh)";
                port = 8003;
                System.out.print("-> Nhap dia chi IP cua May 1 (Nhan Enter neu la localhost): ");
                peerHost = scanner.nextLine().trim();
                if (peerHost.isEmpty()) peerHost = "127.0.0.1";
                peerPort = 8001;
            } else if ("5".equals(choice)) {
                nodeId = "Peer-" + System.getProperty("user.name", "Worker") + "-" + (System.currentTimeMillis() % 1000);
                port = 0;
                java.io.File seedFile = new java.io.File("peer_seed.txt");
                if (seedFile.exists()) {
                    try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(seedFile, java.nio.charset.StandardCharsets.UTF_8))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            line = line.trim();
                            if (line.isEmpty() || line.startsWith("#")) continue;
                            String[] parts = line.split(":");
                            if (parts.length >= 2) {
                                peerHost = parts[0].trim();
                                peerPort = Integer.parseInt(parts[1].trim());
                                break;
                            }
                        }
                    } catch (Exception ignored) {}
                }
                if (peerHost.isEmpty()) {
                    peerHost = "127.0.0.1";
                    peerPort = 8001;
                }
            } else if ("4".equals(choice)) {
                System.out.print("Ten Node: ");
                nodeId = scanner.nextLine().trim();
                if (nodeId.isEmpty()) nodeId = "Custom-Node";
                System.out.print("Cong TCP lang nghe: ");
                String pStr = scanner.nextLine().trim();
                port = pStr.isEmpty() ? 8001 : Integer.parseInt(pStr);
                System.out.print("IP cua may Peer can ket noi (Bo trong neu la may chu): ");
                peerHost = scanner.nextLine().trim();
                if (!peerHost.isEmpty()) {
                    System.out.print("Cong TCP cua Peer: ");
                    peerPort = Integer.parseInt(scanner.nextLine().trim());
                }
            } else {
                nodeId = "Node-1 (Hoang Nhat - Gateway)";
                port = 8001;
            }
        }

        try {
            int diffBits = 10;
            P2PNode node = new P2PNode(nodeId, port, diffBits);
            node.start();

            printLocalIps(node.getPort());

            if (!peerHost.isEmpty() && peerPort > 0) {
                System.out.printf("\n[CONNECT] Dang ket noi TCP toi Peer %s:%d...\n", peerHost, peerPort);
                boolean ok = node.connectToPeer(peerHost, peerPort);
                if (ok) {
                    System.out.println("[OK] Ket noi TCP Socket thanh cong! Dang dong bo So Cai...");
                } else {
                    System.out.println("[!] Chua the ket noi toi Peer. Node van hoat dong va san sang nhan ket noi den.");
                }
            }

            printHelp();

            while (node.isRunning()) {
                System.out.print("\n[" + nodeId + "] Nhap lenh (1-5, 'help'): ");
                if (!scanner.hasNextLine()) {
                    Thread.sleep(500);
                    continue;
                }
                String cmd = scanner.nextLine().trim().toLowerCase();

                if ("5".equals(cmd) || "exit".equals(cmd) || "quit".equals(cmd)) {
                    System.out.println("Dang dung Node P2P...");
                    node.close();
                    break;
                } else if ("1".equals(cmd) || "tx".equals(cmd) || "vote".equals(cmd)) {
                    System.out.print("  Nhap ten nguoi gui (hoac MSSV cu tri): ");
                    String sender = scanner.nextLine().trim();
                    if (sender.isEmpty()) sender = "Voter_" + (System.currentTimeMillis() % 1000);
                    System.out.print("  Nhap nguoi nhan (hoac ung cu vien): ");
                    String recipient = scanner.nextLine().trim();
                    if (recipient.isEmpty()) recipient = "Le Quang Minh";
                    Transaction tx = Transaction.create(sender, recipient, 10.0);
                    node.broadcastTransaction(tx);
                    System.out.printf("[DA PHAT TAN] Giao dich %s da gui qua mang TCP toi cac may khac!\n", tx.txId().substring(0, 8));
                } else if ("2".equals(cmd) || "mine".equals(cmd)) {
                    System.out.println("[POW MINING] Dang kich hoat Java 21 Virtual Threads giai bai toan bam...");
                    long t0 = System.currentTimeMillis();
                    com.pow.simulator.consensus.MiningResult result = node.mineAndBroadcast();
                    long dt = System.currentTimeMillis() - t0;
                    if (result != null && result.success()) {
                        Block tip = result.minedBlock();
                        System.out.printf("[DAO KHOI #%d THANH CONG] Nonce: %d | Hash: %s | Toc do: %.2f H/s | Thoi gian: %d ms\n",
                                tip.index(), tip.nonce(), tip.getShortHash(), result.hashrate(), dt);
                        System.out.println("[BROADCAST] Khoi moi da phat tan qua TCP Socket toi tat ca cac may trong mang!");
                    } else {
                        System.out.println("[!] Khong co giao dich trong Mempool hoac khoi dao khong thanh cong.");
                    }
                } else if ("3".equals(cmd) || "chain".equals(cmd)) {
                    System.out.println("\n========== SO CAI CHUOI KHOI HIEN TAI ==========");
                    System.out.printf("Chieu cao: %d khoi | Do kho: %d bits\n",
                            node.getBlockchain().size(), node.getBlockchain().getLatestBlock().difficultyBits());
                    System.out.println("Tinh toan ven: " + (node.getBlockchain().isChainValid() ? "[OK] 100% HOP LE" : "[FAIL] BI LOI"));
                    for (int i = 0; i < node.getBlockchain().size(); i++) {
                        Block b = node.getBlockchain().getBlock(i);
                        System.out.printf("  #%d | Nonce: %-8d | Hash: %s | Pre: %s | Txs: %d\n",
                                b.index(), b.nonce(), b.getShortHash(), b.previousHash().substring(0, Math.min(8, b.previousHash().length())), b.transactions().size());
                    }
                    System.out.println("================================================");
                } else if ("4".equals(cmd) || "peers".equals(cmd)) {
                    System.out.println("\n========== DANH SACH PEERS KET NOI TCP ==========");
                    System.out.println("So luong peer dang ket noi: " + node.getPeerCount());
                    for (String addr : node.getConnectedPeerAddresses()) {
                        System.out.println("  [PEER] " + addr);
                    }
                    System.out.println("=================================================");
                } else if ("connect".equals(cmd)) {
                    System.out.print("  Nhap IP Peer: ");
                    String h = scanner.nextLine().trim();
                    System.out.print("  Nhap Cong TCP Peer: ");
                    int p = Integer.parseInt(scanner.nextLine().trim());
                    node.connectToPeer(h, p);
                } else {
                    printHelp();
                }
            }

        } catch (Exception e) {
            System.err.println("[ERROR] Loi Node P2P: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void printHelp() {
        System.out.println("\n----------------- BANG LENH DIEU KHIEN -----------------");
        System.out.println("  [1] hoac 'tx'    : Tao va phat tan giao dich / phieu bau qua TCP");
        System.out.println("  [2] hoac 'mine'  : Tu dao khoi PoW va phat tan SYNC_BLOCK");
        System.out.println("  [3] hoac 'chain' : Xem so cai va kiem toan tinh toan ven");
        System.out.println("  [4] hoac 'peers' : Xem cac may tinh dang ket noi TCP");
        System.out.println("  [5] hoac 'exit'  : Dung va ngat ket noi Node");
        System.out.println("  'connect'        : Ket noi them toi 1 may khac qua IP:Port");
        System.out.println("---------------------------------------------------------");
    }

    private static void printLocalIps(int port) {
        System.out.println("\n[TCP SERVER] Node dang lang nghe ket noi tai cong: " + port);
        System.out.println("Dia chi IP cua may nay de may khac ket noi den:");
        System.out.println("  - Localhost: 127.0.0.1:" + port);
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                if (iface.isLoopback() || !iface.isUp()) continue;
                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr instanceof Inet4Address) {
                        String ip = addr.getHostAddress();
                        System.out.printf("  - %s: %s:%d\n", iface.getDisplayName(), ip, port);
                    }
                }
            }
        } catch (Exception ignored) {}
    }
}
