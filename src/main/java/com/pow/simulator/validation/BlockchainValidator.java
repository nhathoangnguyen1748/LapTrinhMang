package com.pow.simulator.validation;

import com.pow.simulator.consensus.TargetMatcher;
import com.pow.simulator.core.Block;
import com.pow.simulator.core.Blockchain;
import com.pow.simulator.core.MerkleTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Enterprise blockchain auditor performing comprehensive cryptographic and consensus verification.
 */
public final class BlockchainValidator {

    private BlockchainValidator() {
        // Prevent instantiation
    }

    /**
     * Convenience method returning boolean integrity status.
     *
     * @param chain blockchain ledger
     * @return true if valid
     */
    public static boolean isChainValid(Blockchain chain) {
        return validate(chain).isValid();
    }

    /**
     * Performs full cryptographically rigorous audit of the blockchain ledger.
     *
     * @param chain blockchain ledger
     * @return ValidationResult with complete diagnostic logs
     */
    public static ValidationResult validate(Blockchain chain) {
        if (chain == null || chain.size() == 0) {
            return ValidationResult.invalid(-1, "Blockchain is null or contains zero blocks", List.of());
        }

        List<String> logs = new ArrayList<>();
        List<Block> blocks = chain.getChain();

        // 1. Validate Genesis Block
        Block genesis = blocks.getFirst();
        logs.add("Auditing Genesis Block #0...");

        if (genesis.index() != 0) {
            return ValidationResult.invalid(0, "Genesis block index is not 0: " + genesis.index(), logs);
        }

        String expectedGenesisPrev = "0".repeat(64);
        if (!expectedGenesisPrev.equals(genesis.previousHash())) {
            return ValidationResult.invalid(0,
                    "Genesis previousHash must be 64 zeros, found: " + genesis.previousHash(), logs);
        }

        if (!genesis.transactions().isEmpty()) {
            String computedGenesisMerkle = MerkleTree.calculateRoot(genesis.transactions());
            if (!computedGenesisMerkle.equals(genesis.merkleRoot())) {
                return ValidationResult.invalid(0,
                        "Genesis Merkle root tampered: expected " + computedGenesisMerkle + ", got " + genesis.merkleRoot(), logs);
            }
        }

        String computedGenesisHash = genesis.calculateHash(genesis.nonce());
        if (!computedGenesisHash.equals(genesis.hash())) {
            return ValidationResult.invalid(0,
                    "Genesis block hash mismatch: stored " + genesis.hash() + " != calculated " + computedGenesisHash, logs);
        }

        logs.add("Genesis Block #0 verified successfully.");

        // 2. Validate Subsequent Blocks
        for (int i = 1; i < blocks.size(); i++) {
            Block prev = blocks.get(i - 1);
            Block curr = blocks.get(i);
            logs.add(String.format("Auditing Block #%d [Hash: %s]...", curr.index(), curr.getShortHash()));

            // A. Index continuity
            if (curr.index() != i) {
                return ValidationResult.invalid(i,
                        String.format("Index discontinuity: block position is %d but index field is %d", i, curr.index()), logs);
            }

            // B. Temporal monotonicity
            if (curr.timestamp() < prev.timestamp()) {
                return ValidationResult.invalid(i,
                        String.format("Temporal paradox: block timestamp (%d) is earlier than previous block (%d)",
                                curr.timestamp(), prev.timestamp()), logs);
            }

            // C. Cryptographic hash linkage
            if (!curr.previousHash().equals(prev.hash())) {
                return ValidationResult.invalid(i,
                        String.format("Broken hash linkage: block previousHash (%s) does not match predecessor hash (%s)",
                                curr.previousHash(), prev.hash()), logs);
            }

            // D. Merkle Root integrity (detects altered transactions/payload)
            if (!curr.transactions().isEmpty()) {
                String computedMerkleRoot = MerkleTree.calculateRoot(curr.transactions());
                if (!computedMerkleRoot.equals(curr.merkleRoot())) {
                    return ValidationResult.invalid(i,
                            String.format("Transaction payload tampered! Stored MerkleRoot (%s) does not match actual transactions root (%s)",
                                    curr.merkleRoot(), computedMerkleRoot), logs);
                }
            }

            // E. Cryptographic Header Hash recalculation (detects altered headers/nonces)
            String recalculatedHash = curr.calculateHash(curr.nonce());
            if (!recalculatedHash.equals(curr.hash())) {
                return ValidationResult.invalid(i,
                        String.format("Block hash tampered! Stored hash (%s) does not match recalculated hash (%s)",
                                curr.hash(), recalculatedHash), logs);
            }

            // F. Proof-of-Work Target Satisfaction
            if (!TargetMatcher.matches(curr.hash(), curr.difficultyBits())) {
                return ValidationResult.invalid(i,
                        String.format("Invalid Proof of Work! Hash %s does not satisfy difficulty requirement of %d bits",
                                curr.hash(), curr.difficultyBits()), logs);
            }

            logs.add(String.format("Block #%d passed all cryptographic verifications.", curr.index()));
        }

        logs.add(String.format("Ledger verification complete: all %d blocks are cryptographically intact.", blocks.size()));
        return ValidationResult.valid(logs);
    }
}
