package com.pow.simulator.network;

/**
 * Enumeration of P2P network protocol message types transmitted across TCP sockets.
 */
public enum MessageType {
    HANDSHAKE,
    HANDSHAKE_ACK,
    TX_BROADCAST,
    BLOCK_BROADCAST,
    SYNC_REQUEST,
    SYNC_RESPONSE,
    PING,
    PONG
}
