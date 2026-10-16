package com.pow.simulator.web;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

/**
 * Main launcher for the Interactive P2P Blockchain Web Simulator.
 * Starts the embedded HTTP & REST API server on port 8080 and opens the default browser.
 */
public class WebLauncher {

    private static final int PORT = 8080;

    public static void main(String[] args) {
        System.out.println("=========================================================================");
        System.out.println("   KHOI CHAY GIAO DIEN TRUC QUAN P2P BLOCKCHAIN NETWORK (JAVA 21)        ");
        System.out.println("=========================================================================");
        System.out.println(" [1/3] Dang khoi tao Cum mang P2P TCP Sockets (Ports 8001 - 8004)...");

        try {
            NodeApiServer server = new NodeApiServer(PORT);
            server.start();

            System.out.println(" [2/3] May chu HTTP REST API da san sang tai cong " + PORT);
            System.out.println(" [3/3] Dang mo trinh duyet mac dinh...");

            String url = "http://localhost:" + PORT;
            System.out.println();
            System.out.println(" >>> TRUY CAP GIAO DIEN TAI: " + url + " <<<");
            System.out.println();

            // Try opening default browser
            tryOpenBrowser(url);

            // Register shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\nDang tat may chu Web va cac nut P2P TCP...");
                server.close();
                System.out.println("He thong da dong hoan tat an toan.");
            }));

            System.out.println("He thong dang chay ngam tren nen tang Java 21 Virtual Threads.");
            System.out.println("Nhan Enter hoac Ctrl+C de dung chuong trinh.");
            System.out.println("=========================================================================");

            // Keep main thread alive
            try {
                System.in.read();
            } catch (Exception ignored) {
                // Keep alive with sleep if stdin is not available
                while (true) {
                    Thread.sleep(10000);
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Loi khoi chay Web Server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void tryOpenBrowser(String url) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                // Fallback for Windows
                Runtime.getRuntime().exec(new String[]{"rundll32", "url.dll,FileProtocolHandler", url});
            }
        } catch (Exception e) {
            System.out.println("Khong the tu dong mo trinh duyet. Vui long mo thu cong: " + url);
        }
    }
}
