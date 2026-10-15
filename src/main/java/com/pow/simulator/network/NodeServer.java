package com.pow.simulator.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Multi-threaded TCP ServerSocket listener.
 * Accepts incoming peer connections and spawns Java 21 Virtual Threads for non-blocking concurrency.
 */
public class NodeServer implements AutoCloseable {

    private final int port;
    private final ServerSocket serverSocket;
    private final Consumer<PeerConnection> connectionConsumer;
    private final AtomicBoolean isRunning = new AtomicBoolean(true);

    public NodeServer(int port, Consumer<PeerConnection> connectionConsumer) throws IOException {
        this.port = port;
        this.connectionConsumer = Objects.requireNonNull(connectionConsumer, "Connection consumer must not be null");
        this.serverSocket = new ServerSocket(port);

        Thread.ofVirtual()
                .name("p2p-server-acceptor-" + serverSocket.getLocalPort())
                .start(this::acceptLoop);
    }

    private void acceptLoop() {
        while (isRunning.get() && !serverSocket.isClosed()) {
            try {
                Socket clientSocket = serverSocket.accept();
                clientSocket.setTcpNoDelay(true); // Disable Nagle's algorithm for instant blockchain packet dispatch
                PeerConnection peer = new PeerConnection(clientSocket);
                connectionConsumer.accept(peer);
            } catch (IOException e) {
                if (!isRunning.get()) {
                    break;
                }
            }
        }
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    public boolean isRunning() {
        return isRunning.get() && !serverSocket.isClosed();
    }

    @Override
    public void close() {
        if (isRunning.compareAndSet(true, false)) {
            try {
                serverSocket.close();
            } catch (IOException ignored) {}
        }
    }
}
