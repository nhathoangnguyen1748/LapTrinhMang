package com.pow.simulator.network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Manages an active duplex TCP Socket connection with a remote peer node.
 * Uses a dedicated Java 21 Virtual Thread for incoming frame processing.
 */
public class PeerConnection implements AutoCloseable {

    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;
    private final String remoteAddress;
    private final AtomicBoolean isClosed = new AtomicBoolean(false);

    private volatile String remoteNodeId = "UNKNOWN";
    private Consumer<NetworkMessage> messageConsumer;
    private Consumer<PeerConnection> closeHandler;

    public PeerConnection(Socket socket) throws IOException {
        this.socket = Objects.requireNonNull(socket, "Socket must not be null");
        this.remoteAddress = socket.getRemoteSocketAddress().toString();
        this.in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        this.out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }

    /**
     * Starts the asynchronous read loop on a Java 21 Virtual Thread.
     *
     * @param onMessage callback invoked when a valid message frame arrives
     * @param onClose   callback invoked when socket closes or disconnects
     */
    public void startListening(Consumer<NetworkMessage> onMessage, Consumer<PeerConnection> onClose) {
        this.messageConsumer = onMessage;
        this.closeHandler = onClose;

        Thread.ofVirtual()
                .name("peer-reader-" + remoteAddress)
                .start(this::readLoop);
    }

    private void readLoop() {
        try {
            while (!isClosed.get() && !socket.isClosed()) {
                NetworkMessage message = NetworkProtocol.readMessage(in);
                if (message != null && messageConsumer != null) {
                    messageConsumer.accept(message);
                }
            }
        } catch (IOException ignored) {
            // Socket disconnected or EOF
        } finally {
            close();
        }
    }

    /**
     * Sends a network message frame over the TCP stream.
     * Thread-safe against concurrent senders.
     *
     * @param message frame to transmit
     * @throws IOException on socket write failure
     */
    public synchronized void send(NetworkMessage message) throws IOException {
        if (isClosed.get() || socket.isClosed()) {
            throw new IOException("Cannot send message, socket is closed: " + remoteAddress);
        }
        NetworkProtocol.writeMessage(out, message);
    }

    public String getRemoteNodeId() {
        return remoteNodeId;
    }

    public void setRemoteNodeId(String remoteNodeId) {
        this.remoteNodeId = remoteNodeId;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public boolean isOpen() {
        return !isClosed.get() && !socket.isClosed();
    }

    @Override
    public void close() {
        if (isClosed.compareAndSet(false, true)) {
            try {
                socket.close();
            } catch (IOException ignored) {}

            if (closeHandler != null) {
                closeHandler.accept(this);
            }
        }
    }

    @Override
    public String toString() {
        return String.format("Peer[%s @ %s]", remoteNodeId, remoteAddress);
    }
}
