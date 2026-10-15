package com.pow.simulator.attack;

import com.pow.simulator.consensus.MiningResult;
import com.pow.simulator.consensus.MiningService;
import com.pow.simulator.consensus.MultiThreadedMiner;
import com.pow.simulator.consensus.TargetMatcher;
import com.pow.simulator.core.Block;
import com.pow.simulator.core.Blockchain;
import com.pow.simulator.core.MerkleTree;
import com.pow.simulator.core.Transaction;
import com.pow.simulator.validation.BlockchainValidator;
import com.pow.simulator.validation.ValidationResult;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Simulator demonstrating ledger tampering attacks, downstream cascading invalidation,
 * and the astronomical computational cost required to rewrite Proof-of-Work history.
 */
public class AttackSimulator {

    private final MiningService miner;

    public AttackSimulator() {
        this(new MultiThreadedMiner());
    }

    public AttackSimulator(MiningService miner) {
        this.miner = Objects.requireNonNull(miner, "miner must not be null");
    }

    /**
     * Simulates a multi-phase tampering attack on a target block.
     *
     * @param originalChain    valid blockchain
     * @param targetBlockIndex index of block the attacker attempts to alter
     * @return comprehensive TamperAttackReport
     */
    public TamperAttackReport simulateTamperAttack(Blockchain originalChain, int targetBlockIndex) {
        Objects.requireNonNull(originalChain, "originalChain must not be null");
        if (targetBlockIndex <= 0 || targetBlockIndex >= originalChain.size()) {
            throw new IllegalArgumentException(
                    "targetBlockIndex must be between 1 and " + (originalChain.size() - 1));
        }

        Blockchain attackChain = originalChain.copy();
        Block victimBlock = attackChain.getBlock(targetBlockIndex);

        // Phase 1: Direct payload tampering (modify transaction payload)
        List<Transaction> forgedTxs = new ArrayList<>(victimBlock.transactions());
        Transaction maliciousTx = new Transaction(
                "hacked-tx-" + System.currentTimeMillis(),
                "VictimAddress",
                "AttackerVaultAddress",
                999999.0,
                System.currentTimeMillis()
        );
        if (!forgedTxs.isEmpty()) {
            forgedTxs.set(0, maliciousTx);
        } else {
            forgedTxs.add(maliciousTx);
        }

        // Block with tampered transactions but original stored Merkle root & hash
        Block payloadTamperedBlock = new Block(
                victimBlock.index(),
                victimBlock.timestamp(),
                victimBlock.previousHash(),
                victimBlock.merkleRoot(), // unchanged stored root
                forgedTxs,
                victimBlock.nonce(),
                victimBlock.difficultyBits(),
                victimBlock.hash()
        );
        attackChain.setBlockForSimulation(targetBlockIndex, payloadTamperedBlock);
        ValidationResult phase1Result = BlockchainValidator.validate(attackChain);

        // Phase 2: Attacker recalculates Merkle root and updates header hash naively (without mining)
        String newMerkleRoot = MerkleTree.calculateRoot(forgedTxs);
        Block headerTamperedBlock = new Block(
                victimBlock.index(),
                victimBlock.timestamp(),
                victimBlock.previousHash(),
                newMerkleRoot,
                forgedTxs,
                victimBlock.nonce(),
                victimBlock.difficultyBits(),
                ""
        );
        String naiveNewHash = headerTamperedBlock.calculateHash(victimBlock.nonce());
        headerTamperedBlock = headerTamperedBlock.withNonceAndHash(victimBlock.nonce(), naiveNewHash);
        attackChain.setBlockForSimulation(targetBlockIndex, headerTamperedBlock);
        ValidationResult phase2Result = BlockchainValidator.validate(attackChain);

        // Phase 3: Attacker successfully re-mines ONLY the victim block to satisfy PoW
        Block reMinedTemplate = Block.createTemplate(
                victimBlock.index(),
                victimBlock.timestamp(),
                victimBlock.previousHash(),
                forgedTxs,
                victimBlock.difficultyBits()
        );
        MiningResult reMineResult = miner.mine(reMinedTemplate);
        attackChain.setBlockForSimulation(targetBlockIndex, reMineResult.minedBlock());
        ValidationResult phase3Result = BlockchainValidator.validate(attackChain);

        // Calculate downstream cascade metrics
        int downstreamBlocksCount = attackChain.size() - targetBlockIndex - 1;

        // Calculate theoretical recalculation cost (estimated hash attempts for all subsequent blocks)
        BigInteger totalEstimatedHashes = BigInteger.ZERO;
        for (int i = targetBlockIndex; i < attackChain.size(); i++) {
            int diffBits = attackChain.getBlock(i).difficultyBits();
            BigInteger expectedHashesForBlock = BigInteger.ONE.shiftLeft(diffBits);
            totalEstimatedHashes = totalEstimatedHashes.add(expectedHashesForBlock);
        }

        return new TamperAttackReport(
                targetBlockIndex,
                victimBlock.hash(),
                reMineResult.minedBlock().hash(),
                downstreamBlocksCount,
                phase1Result,
                phase2Result,
                phase3Result,
                totalEstimatedHashes
        );
    }

    /**
     * Record detailing the simulation phases and computational cost analysis of a tamper attack.
     */
    public record TamperAttackReport(
            int targetBlockIndex,
            String originalHash,
            String forgedHash,
            int brokenDownstreamBlocks,
            ValidationResult phase1Result,
            ValidationResult phase2Result,
            ValidationResult phase3Result,
            BigInteger totalEstimatedHashesToRewriteChain
    ) {
        public String getSummary() {
            StringBuilder sb = new StringBuilder();
            sb.append("\n=======================================================\n");
            sb.append("         ATTACK SIMULATION & INTEGRITY AUDIT           \n");
            sb.append("=======================================================\n");
            sb.append(String.format("Target Block Index: #%d\n", targetBlockIndex));
            sb.append(String.format("Original Hash:      %s\n", originalHash));
            sb.append(String.format("Attacker New Hash:  %s\n", forgedHash));
            sb.append(String.format("Downstream Blocks:  %d blocks orphaned / broken\n\n", brokenDownstreamBlocks));

            sb.append("--- PHASE 1: Direct Payload Alteration (Merkle Root not updated) ---\n");
            sb.append(String.format("Result: %s\n", phase1Result.isValid() ? "PASS" : "BLOCKED"));
            sb.append(String.format("Detection: %s\n\n", phase1Result.failureReason()));

            sb.append("--- PHASE 2: Updating Header Without PoW Nonce Search ---\n");
            sb.append(String.format("Result: %s\n", phase2Result.isValid() ? "PASS" : "BLOCKED"));
            sb.append(String.format("Detection: %s\n\n", phase2Result.failureReason()));

            sb.append("--- PHASE 3: Attacker Re-mines Victim Block with Valid PoW ---\n");
            sb.append(String.format("Result: %s\n", phase3Result.isValid() ? "PASS" : "BLOCKED"));
            sb.append(String.format("Detection: %s\n\n", phase3Result.failureReason()));

            sb.append("--- COMPUTATIONAL RECALCULATION COST ANALYSIS ---\n");
            sb.append(String.format("Total PoW work required to rewrite from Block #%d to tip: ~%s SHA-256 hashes\n",
                    targetBlockIndex, totalEstimatedHashesToRewriteChain));
            sb.append("Conclusion: Altering an earlier block invalidates all subsequent block linkages.\n");
            sb.append("To force network acceptance, an attacker must outpace the honest collective hashrate\n");
            sb.append("(51% attack threshold), making deep ledger reorganization computationally intractable.\n");
            sb.append("=======================================================\n");
            return sb.toString();
        }
    }
}
