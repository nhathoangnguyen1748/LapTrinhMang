package com.pow.simulator.network;

import com.pow.simulator.core.Block;
import com.pow.simulator.core.Transaction;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Scanner;

/**
 * Khởi chạy 1 Node P2P độc lập trên máy tính cá nhân.
 * Hỗ trợ chạy phân tán trên nhiều máy tính khác nhau qua LAN, Wi-Fi hoặc Internet (Tailscale/VPN).
 */
public class StandaloneNode {

    public static void main(String[] args) {
        System.out.println("=========================================================================");
        System.out.println("   KHỞI CHẠY NÚT MẠNG P2P BLOCKCHAIN ĐỘC LẬP (STANDALONE P2P NODE)       ");
        System.out.println("   Nhóm: Nguyễn Hoàng Nhật - Nguyễn Đỗ Trung Lương - Lê Quang Minh       ");
        System.out.println("=========================================================================");

        Scanner scanner = new Scanner(System.in);
        String nodeId;
        int port;
        String peerHost = "";
        int peerPort = 0;

        if (args != null && args.length >= 1 && "seed".equalsIgnoreCase(args[0])) {
            nodeId = "Seed-Gateway (Hoang Nhat)";
            port = (args.length >= 2) ? Integer.parseInt(args[1]) : 8001;
            System.out.println("👉 [CHẾ ĐỘ SEED NODE] Khởi chạy làm máy chủ Gateway tại cổng " + port);
        } else if (args != null && args.length >= 1 && ("auto".equalsIgnoreCase(args[0]) || "peer".equalsIgnoreCase(args[0]))) {
            nodeId = "Peer-" + System.getProperty("user.name", "Worker") + "-" + (System.currentTimeMillis() % 1000);
            port = 0; // Tự động chọn cổng trống
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
                    System.out.println("⚠️ Không đọc được peer_seed.txt: " + e.getMessage());
                }
            }
            if (peerHost.isEmpty()) {
                peerHost = "127.0.0.1";
                peerPort = 8001;
            }
            System.out.println("👉 [CHẾ ĐỘ TỰ ĐỘNG THAM GIA NODE - 1 CLICK]");
            System.out.printf("   Tên Node: %s | Đang kết nối tới Seed: %s:%d\n", nodeId, peerHost, peerPort);
        } else if (args != null && args.length >= 2) {
            nodeId = args[0];
            port = Integer.parseInt(args[1]);
            if (args.length >= 4) {
                peerHost = args[2];
                peerPort = Integer.parseInt(args[3]);
            }
        } else {
            System.out.println("\nChọn vai trò máy của bạn trong nhóm 3 người:");
            System.out.println("  [1] Máy 1 - Nguyễn Hoàng Nhật (Trạm Gateway & Điều Phối - Port 8001)");
            System.out.println("  [2] Máy 2 - Nguyễn Đỗ Trung Lương (Nút Thợ Đào PoW - Port 8002)");
            System.out.println("  [3] Máy 3 - Lê Quang Minh (Nút Kiểm Toán & Sổ Cái - Port 8003)");
            System.out.println("  [4] Tùy chỉnh tên Node và Cổng TCP");
            System.out.println("  [5] Tự động tham gia từ file cấu hình peer_seed.txt");
            System.out.print("\n👉 Nhập lựa chọn của bạn (1-5) [Mặc định 1]: ");
            String choice = scanner.nextLine().trim();

            if ("2".equals(choice)) {
                nodeId = "Node-2 (Trung Luong)";
                port = 8002;
                System.out.print("👉 Nhập địa chỉ IP của Máy 1 (Nhấn Enter nếu là localhost): ");
                peerHost = scanner.nextLine().trim();
                if (peerHost.isEmpty()) peerHost = "127.0.0.1";
                peerPort = 8001;
            } else if ("3".equals(choice)) {
                nodeId = "Node-3 (Quang Minh)";
                port = 8003;
                System.out.print("👉 Nhập địa chỉ IP của Máy 1 (Nhấn Enter nếu là localhost): ");
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
                System.out.print("Tên Node: ");
                nodeId = scanner.nextLine().trim();
                if (nodeId.isEmpty()) nodeId = "Custom-Node";
                System.out.print("Cổng TCP lắng nghe: ");
                String pStr = scanner.nextLine().trim();
                port = pStr.isEmpty() ? 8001 : Integer.parseInt(pStr);
                System.out.print("IP của máy Peer cần kết nối (Bỏ trống nếu là máy chủ): ");
                peerHost = scanner.nextLine().trim();
                if (!peerHost.isEmpty()) {
                    System.out.print("Cổng TCP của Peer: ");
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
                System.out.printf("\n[CONNECT] Đang kết nối TCP tới Peer %s:%d...\n", peerHost, peerPort);
                boolean ok = node.connectToPeer(peerHost, peerPort);
                if (ok) {
                    System.out.println("✅ Kết nối TCP Socket thành công! Đang đồng bộ Sổ Cái...");
                } else {
                    System.out.println("⚠️ Chưa thể kết nối tới Peer. Node vẫn hoạt động và sẵn sàng nhận kết nối đến.");
                }
            }

            printHelp();

            while (node.isRunning()) {
                System.out.print("\n[" + nodeId + "] Nhập lệnh (1-5, 'help'): ");
                if (!scanner.hasNextLine()) {
                    Thread.sleep(500);
                    continue;
                }
                String cmd = scanner.nextLine().trim().toLowerCase();

                if ("5".equals(cmd) || "exit".equals(cmd) || "quit".equals(cmd)) {
                    System.out.println("Đang dừng Node P2P...");
                    node.close();
                    break;
                } else if ("1".equals(cmd) || "tx".equals(cmd) || "vote".equals(cmd)) {
                    System.out.print("  Nhập tên người gửi (hoặc MSSV cử tri): ");
                    String sender = scanner.nextLine().trim();
                    if (sender.isEmpty()) sender = "Voter_" + (System.currentTimeMillis() % 1000);
                    System.out.print("  Nhập người nhận (hoặc ứng cử viên): ");
                    String recipient = scanner.nextLine().trim();
                    if (recipient.isEmpty()) recipient = "Le Quang Minh";
                    Transaction tx = Transaction.create(sender, recipient, 10.0);
                    node.broadcastTransaction(tx);
                    System.out.println("🚀 [ĐÃ PHÁT TÁN] Giao dịch " + tx.txId().substring(0, 8) + " đã gửi qua mạng TCP tới các máy khác!");
                } else if ("2".equals(cmd) || "mine".equals(cmd)) {
                    System.out.println("⚡ [POW MINING] Đang kích hoạt Java 21 Virtual Threads giải bài toán băm...");
                    long t0 = System.currentTimeMillis();
                    com.pow.simulator.consensus.MiningResult result = node.mineAndBroadcast();
                    long dt = System.currentTimeMillis() - t0;
                    if (result != null && result.success()) {
                        Block tip = result.minedBlock();
                        System.out.printf("🎉 [ĐÃ ĐÀO KHỐI #%d THÀNH CÔNG] Nonce: %d | Hash: %s | Tốc độ: %.2f H/s | Thời gian: %d ms\n",
                                tip.index(), tip.nonce(), tip.getShortHash(), result.hashrate(), dt);
                        System.out.println("📡 Khối mới đã phát tán qua TCP Socket tới tất cả các máy trong mạng!");
                    } else {
                        System.out.println("⚠️ Không có giao dịch trong Mempool hoặc khối đào không thành công.");
                    }
                } else if ("3".equals(cmd) || "chain".equals(cmd)) {
                    System.out.println("\n========== SỔ CÁI CHUỖI KHỐI HIỆN TẠI ==========");
                    System.out.printf("Chiều cao: %d khối | Độ khó: %d bits\n",
                            node.getBlockchain().size(), node.getBlockchain().getLatestBlock().difficultyBits());
                    System.out.println("Tính toàn vẹn: " + (node.getBlockchain().isChainValid() ? "✅ 100% HỢP LỆ" : "❌ BỊ LỖI"));
                    for (int i = 0; i < node.getBlockchain().size(); i++) {
                        Block b = node.getBlockchain().getBlock(i);
                        System.out.printf("  #%d | Nonce: %-8d | Hash: %s | Pre: %s | Txs: %d\n",
                                b.index(), b.nonce(), b.getShortHash(), b.previousHash().substring(0, Math.min(8, b.previousHash().length())), b.transactions().size());
                    }
                    System.out.println("================================================");
                } else if ("4".equals(cmd) || "peers".equals(cmd)) {
                    System.out.println("\n========== DANH SÁCH PEERS KẾT NỐI TCP ==========");
                    System.out.println("Số lượng peer đang kết nối: " + node.getPeerCount());
                    for (String addr : node.getConnectedPeerAddresses()) {
                        System.out.println("  🔗 " + addr);
                    }
                    System.out.println("=================================================");
                } else if ("connect".equals(cmd)) {
                    System.out.print("  Nhập IP Peer: ");
                    String h = scanner.nextLine().trim();
                    System.out.print("  Nhập Cổng TCP Peer: ");
                    int p = Integer.parseInt(scanner.nextLine().trim());
                    node.connectToPeer(h, p);
                } else {
                    printHelp();
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Lỗi Node P2P: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void printHelp() {
        System.out.println("\n----------------- BẢNG LỆNH ĐIỀU KHIỂN -----------------");
        System.out.println("  [1] hoặc 'tx'    : Tạo và phát tán giao dịch / phiếu bầu qua TCP");
        System.out.println("  [2] hoặc 'mine'  : Tự đào khối PoW và phát tán SYNC_BLOCK");
        System.out.println("  [3] hoặc 'chain' : Xem sổ cái và kiểm toán tính toàn vẹn");
        System.out.println("  [4] hoặc 'peers' : Xem các máy tính đang kết nối TCP");
        System.out.println("  [5] hoặc 'exit'  : Dừng và ngắt kết nối Node");
        System.out.println("  'connect'        : Kết nối thêm tới 1 máy khác qua IP:Port");
        System.out.println("---------------------------------------------------------");
    }

    private static void printLocalIps(int port) {
        System.out.println("\n[TCP SERVER] Node đang lắng nghe kết nối tại cổng: " + port);
        System.out.println("Địa chỉ IP của máy này để máy khác kết nối đến:");
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
