package com.pow.simulator.network;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("P2P Network Protocol & Framing Tests")
class NetworkProtocolTest {

    @Test
    @DisplayName("NetworkMessage serializes and deserializes correctly with arbitrary payloads and newlines")
    void testMessageSerializationRoundtrip() {
        String multilinePayload = "Header\nDataLine1\tDataLine2\nSpecial: !@#$%^&*()_+{}[]";
        NetworkMessage original = NetworkMessage.create(MessageType.BLOCK_BROADCAST, "Node-Test-1", multilinePayload);

        String serialized = original.serialize();
        assertNotNull(serialized);

        NetworkMessage deserialized = NetworkMessage.deserialize(serialized);
        assertEquals(original.messageId(), deserialized.messageId());
        assertEquals(original.type(), deserialized.type());
        assertEquals(original.senderNodeId(), deserialized.senderNodeId());
        assertEquals(original.timestamp(), deserialized.timestamp());
        assertEquals(original.payload(), deserialized.payload());
    }

    @Test
    @DisplayName("NetworkProtocol writes and reads length-prefixed TCP stream frames with byte-fidelity")
    void testLengthPrefixedStreamFraming() throws IOException {
        NetworkMessage msg1 = NetworkMessage.create(MessageType.HANDSHAKE, "Node-1", "1:genesis_hash_123");
        NetworkMessage msg2 = NetworkMessage.create(MessageType.TX_BROADCAST, "Node-2", "tx123\tAlice\tBob\t50.0\t1700000000");

        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        DataOutputStream dataOut = new DataOutputStream(byteOut);

        // Write two consecutive frames into the stream (simulating TCP stream packet concatenation)
        NetworkProtocol.writeMessage(dataOut, msg1);
        NetworkProtocol.writeMessage(dataOut, msg2);

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        DataInputStream dataIn = new DataInputStream(byteIn);

        // Read frames back sequentially
        NetworkMessage readMsg1 = NetworkProtocol.readMessage(dataIn);
        NetworkMessage readMsg2 = NetworkProtocol.readMessage(dataIn);

        assertNotNull(readMsg1);
        assertEquals(msg1.messageId(), readMsg1.messageId());
        assertEquals(MessageType.HANDSHAKE, readMsg1.type());
        assertEquals(msg1.payload(), readMsg1.payload());

        assertNotNull(readMsg2);
        assertEquals(msg2.messageId(), readMsg2.messageId());
        assertEquals(MessageType.TX_BROADCAST, readMsg2.type());
        assertEquals(msg2.payload(), readMsg2.payload());
    }
}
