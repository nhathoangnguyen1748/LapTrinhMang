package com.pow.simulator.web;

import java.awt.Desktop;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;

/**
 * Main launcher for the Interactive P2P Blockchain Web Simulator.
 * Starts the embedded HTTP & REST API server on port 8000 (fallback 8080) and opens the default browser.
 */
public class WebLauncher {

    public static void main(String[] args) {
        int port = 8000;
        String envPort = System.getenv("PORT");
        if (envPort != null && !envPort.isBlank()) {
            try {
                port = Integer.parseInt(envPort.trim());
            } catch (Exception ignored) {}
        } else if (args != null && args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (Exception ignored) {}
        }

        System.out.println("=========================================================================");
        System.out.println("   KHOI CHAY GIAO DIEN TRUC QUAN P2P BLOCKCHAIN NETWORK (JAVA 21)        ");
        System.out.println("=========================================================================");
        System.out.println(" [1/3] Dang khoi tao Cum mang P2P TCP Sockets (Ports 8001 - 8004)...");

        try {
            NodeApiServer server = new NodeApiServer(port);
            server.start();

            System.out.println(" [2/3] May chu HTTP REST API da san sang tai cong: " + port);
            System.out.println(" [3/3] Dang mo trinh duyet mac dinh...");

            String url = "http://localhost:" + port;
            System.out.println();
            printNetworkAccessGuide(port);


            // Open browser automatically
            tryOpenBrowser(url);

            // Register shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\nDang tat may chu Web va cac nut P2P TCP...");
                server.close();
                System.out.println("He thong da dong hoan tat an toan.");
            }));

            System.out.println("=========================================================================");
            System.out.println(" He thong dang chay ngam tren nen tang Java 21 Virtual Threads.");
            System.out.println(" Ban co the thao tac truc tiep tren trinh duyet Web!");
            System.out.println(" Nhan Ctrl+C de dung chuong trinh.");
            System.out.println("=========================================================================");

            // Keep server running indefinitely until explicitly stopped
            while (true) {
                try {
                    Thread.sleep(60_000);
                } catch (InterruptedException e) {
                    break;
                }
            }

        } catch (Exception e) {
            System.err.println("[ERROR] Loi khoi chay Web Server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static int determinePort(int... candidates) {
        for (int p : candidates) {
            try (ServerSocket ss = new ServerSocket(p)) {
                return p;
            } catch (IOException ignored) {
                // Port occupied, try next
            }
        }
        return 8000;
    }

    public static void tryOpenBrowser(String url) {
        if (System.getenv("PORT") != null || System.getenv("RENDER") != null) return;
        try {
            String os = System.getProperty("os.name", "").toLowerCase();
            if (os.contains("win")) {
                // Official native Windows command that works from any console/service
                new ProcessBuilder("cmd", "/c", "start", "", url).start();
                return;
            }
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                new ProcessBuilder("xdg-open", url).start();
            }
        } catch (Exception e) {
            System.out.println("Khong the tu dong mo trinh duyet. Vui long mo thu cong: " + url);
        }
    }
    public static void printNetworkAccessGuide(int port) {
        System.out.println("=========================================================================");
        System.out.println(" [*] DIA CHI TRUY CAP GIAO DIEN (HO TRO DA MAY TINH & DIEN THOAI):");
        System.out.println("    - May nay (Localhost):       http://localhost:" + port);
        try {
            java.util.Enumeration<java.net.NetworkInterface> interfaces = java.net.NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                java.net.NetworkInterface iface = interfaces.nextElement();
                if (iface.isLoopback() || !iface.isUp()) continue;
                java.util.Enumeration<java.net.InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    java.net.InetAddress addr = addresses.nextElement();
                    if (addr instanceof java.net.Inet4Address) {
                        String ip = addr.getHostAddress();
                        String name = iface.getDisplayName().toLowerCase();
                        String desc = "Mang Noi Bo (LAN/Wi-Fi)";
                        if (name.contains("tailscale") || ip.startsWith("100.")) {
                            desc = "Mang Ao P2P Tailscale (Internet)";
                        } else if (name.contains("zerotier")) {
                            desc = "Mang Ao P2P ZeroTier (Internet)";
                        }
                        System.out.printf("    - %-27s http://%s:%d\n", desc + ":", ip, port);
                    }
                }
            }
        } catch (Exception ignored) {}
        System.out.println("-------------------------------------------------------------------------");
        System.out.println(" [*] CAC BAN O MAY KHAC (KHAC NHA/KHAC MANG) CHI CAN TRUY CAP VAO:");
        System.out.println("    1. Bo phieu cu tri:  http://<IP_MAY_NAY>:" + port + "/vote");
        System.out.println("    2. So do 10 Nodes:   http://<IP_MAY_NAY>:" + port + "/nodes");
        System.out.println("    3. So Cai Explorer:  http://<IP_MAY_NAY>:" + port);
        System.out.println("=========================================================================");
    }
}
