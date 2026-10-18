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
        if (args != null && args.length > 0) {
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
            System.out.println(" >>> TRUY CAP GIAO DIEN TAI: " + url + " <<<");
            System.out.println();

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
            System.err.println("❌ Loi khoi chay Web Server: " + e.getMessage());
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
}
