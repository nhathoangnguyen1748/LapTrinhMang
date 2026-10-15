package com.pow.simulator.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Length-prefixed framing protocol encoder/decoder over reliable TCP socket streams.
 * Prevents TCP stream fragmentation and packet boundary concatenation.
 */
public final class NetworkProtocol {

    public static final int MAX_FRAME_SIZE = 16 * 1024 * 1024; // 16 MB frame limit

    private NetworkProtocol() {
        // Prevent instantiation
    }

    /**
     * Encodes and writes a network message as a length-prefixed frame to the output stream.
     *
     * @param out     target DataOutputStream
     * @param message network message to transmit
     * @throws IOException on socket write failure
     */
    public static synchronized void writeMessage(DataOutputStream out, NetworkMessage message) throws IOException {
        byte[] payloadBytes = message.serialize().getBytes(StandardCharsets.UTF_8);
        if (payloadBytes.length > MAX_FRAME_SIZE) {
            throw new IOException("Frame size exceeds maximum allowed: " + payloadBytes.length);
        }
        out.writeInt(payloadBytes.length);
        out.write(payloadBytes);
        out.flush();
    }

    /**
     * Reads and decodes a length-prefixed frame from the input stream.
     *
     * @param in source DataInputStream
     * @return decoded NetworkMessage instance
     * @throws IOException on socket read failure or stream termination
     */
    public static NetworkMessage readMessage(DataInputStream in) throws IOException {
        int length = in.readInt();
        if (length <= 0 || length > MAX_FRAME_SIZE) {
            throw new IOException("Invalid frame length received: " + length);
        }

        byte[] buffer = in.readNBytes(length);
        if (buffer.length < length) {
            throw new IOException("Premature end of TCP stream while reading frame payload");
        }

        String raw = new String(buffer, StandardCharsets.UTF_8);
        return NetworkMessage.deserialize(raw);
    }
}
