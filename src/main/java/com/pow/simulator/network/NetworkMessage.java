package com.pow.simulator.network;

import java.util.Objects;
import java.util.UUID;

/**
 * Immutable network frame transmitted across P2P TCP socket connections.
 *
 * @param messageId    unique UUID preventing duplicate processing in the gossip network
 * @param type         protocol operation type
 * @param senderNodeId identifier of sending node (e.g. Node-8001)
 * @param payload      serialized content (transaction, block, chain list, status)
 * @param timestamp    epoch millisecond when packet was dispatched
 */
public record NetworkMessage(
        String messageId,
        MessageType type,
        String senderNodeId,
        String payload,
        long timestamp
) {
    public NetworkMessage {
        Objects.requireNonNull(type, "MessageType must not be null");
        Objects.requireNonNull(senderNodeId, "senderNodeId must not be null");
        if (messageId == null || messageId.isBlank()) {
            messageId = UUID.randomUUID().toString();
        }
        if (payload == null) {
            payload = "";
        }
    }

    /**
     * Factory constructor for creating a new outbound message.
     */
    public static NetworkMessage create(MessageType type, String senderNodeId, String payload) {
        return new NetworkMessage(UUID.randomUUID().toString(), type, senderNodeId, payload, System.currentTimeMillis());
    }

    /**
     * Serializes this message into a transport string representation.
     */
    public String serialize() {
        return messageId + "\n" + type.name() + "\n" + senderNodeId + "\n" + timestamp + "\n" + payload;
    }

    /**
     * Deserializes a transport string back into a NetworkMessage.
     */
    public static NetworkMessage deserialize(String raw) {
        int firstNl = raw.indexOf('\n');
        int secondNl = raw.indexOf('\n', firstNl + 1);
        int thirdNl = raw.indexOf('\n', secondNl + 1);
        int fourthNl = raw.indexOf('\n', thirdNl + 1);

        if (firstNl == -1 || secondNl == -1 || thirdNl == -1 || fourthNl == -1) {
            throw new IllegalArgumentException("Malformed network message frame: " + raw);
        }

        String msgId = raw.substring(0, firstNl);
        MessageType type = MessageType.valueOf(raw.substring(firstNl + 1, secondNl));
        String senderId = raw.substring(secondNl + 1, thirdNl);
        long ts = Long.parseLong(raw.substring(thirdNl + 1, fourthNl));
        String payload = raw.substring(fourthNl + 1);

        return new NetworkMessage(msgId, type, senderId, payload, ts);
    }

    @Override
    public String toString() {
        return String.format("[%s from %s | ID: %s | Len: %d]",
                type, senderNodeId, messageId.substring(0, Math.min(8, messageId.length())), payload.length());
    }
}
