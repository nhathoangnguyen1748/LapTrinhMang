package com.pow.simulator.core;

import com.pow.simulator.crypto.HashUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Computes Bitcoin-style binary Merkle Trees for immutable transaction sets.
 * Guarantees cryptographic integrity of transaction bundles stored in blocks.
 */
public final class MerkleTree {

    private MerkleTree() {
        // Prevent instantiation
    }

    /**
     * Calculates the Merkle Root hash from a list of transactions.
     *
     * @param transactions list of transactions
     * @return 64-character SHA-256 hex string representing the Merkle Root
     */
    public static String calculateRoot(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return HashUtil.sha256Hex("EMPTY_TRANSACTIONS_ROOT");
        }

        List<String> currentLevel = new ArrayList<>();
        for (Transaction tx : transactions) {
            Objects.requireNonNull(tx, "Transaction inside block must not be null");
            currentLevel.add(tx.txId());
        }

        while (currentLevel.size() > 1) {
            List<String> nextLevel = new ArrayList<>();

            for (int i = 0; i < currentLevel.size(); i += 2) {
                String left = currentLevel.get(i);
                // If odd number of elements, duplicate the last element (Bitcoin standard)
                String right = (i + 1 < currentLevel.size()) ? currentLevel.get(i + 1) : left;

                String combinedHash = HashUtil.sha256Hex(left + right);
                nextLevel.add(combinedHash);
            }

            currentLevel = nextLevel;
        }

        return currentLevel.getFirst();
    }
}
