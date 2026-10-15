package com.pow.simulator.network;

import com.pow.simulator.core.Transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe memory pool (mempool) caching unconfirmed transactions received over the P2P network.
 */
public class Mempool {

    private final Map<String, Transaction> transactions = new ConcurrentHashMap<>();

    /**
     * Adds an unconfirmed transaction to the pool.
     *
     * @param tx transaction received over the network
     * @return true if newly added, false if already present
     */
    public boolean add(Transaction tx) {
        if (tx == null) return false;
        return transactions.putIfAbsent(tx.txId(), tx) == null;
    }

    /**
     * Extracts a batch of transactions to be bundled into the next mined block.
     *
     * @param maxCount maximum number of transactions
     * @return list of transactions
     */
    public synchronized List<Transaction> take(int maxCount) {
        List<Transaction> batch = new ArrayList<>();
        for (Transaction tx : transactions.values()) {
            batch.add(tx);
            if (batch.size() >= maxCount) {
                break;
            }
        }
        return Collections.unmodifiableList(batch);
    }

    /**
     * Removes confirmed transactions once a block is added to the blockchain.
     *
     * @param confirmedTxs transactions included in a mined/received block
     */
    public void removeAll(List<Transaction> confirmedTxs) {
        if (confirmedTxs == null) return;
        for (Transaction tx : confirmedTxs) {
            transactions.remove(tx.txId());
        }
    }

    public boolean contains(String txId) {
        return transactions.containsKey(txId);
    }

    public int size() {
        return transactions.size();
    }

    public void clear() {
        transactions.clear();
    }
}
