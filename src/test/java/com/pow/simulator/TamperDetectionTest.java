package com.pow.simulator;

import com.pow.simulator.consensus.MiningResult;
import com.pow.simulator.consensus.MultiThreadedMiner;
import com.pow.simulator.core.Block;
import com.pow.simulator.core.Blockchain;
import com.pow.simulator.core.Transaction;
import com.pow.simulator.validation.BlockchainValidator;
import com.pow.simulator.validation.ValidationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tamper Detection & Ledger Integrity Tests")
class TamperDetectionTest {

    private MultiThreadedMiner miner;
    private Blockchain validChain;

    @BeforeEach
    void setUp() {
        miner = new MultiThreadedMiner(MultiThreadedMiner.ExecutionMode.VIRTUAL_THREADS, 4);
        validChain = Blockchain.withDefaultGenesis(8);

        // Mine 3 valid blocks
        for (int i = 1; i <= 3; i++) {
            List<Transaction> txs = List.of(
                    Transaction.create("User_" + (i - 1), "User_" + i, 5.0 * i)
            );
            Block template = Block.createTemplate(
                    i,
                    System.currentTimeMillis() + i * 1000,
                    validChain.getLatestBlock().hash(),
                    txs,
                    8
            );
            MiningResult result = miner.mine(template);
            validChain.addBlock(result.minedBlock());
        }
    }

    @Test
    @DisplayName("Untampered blockchain passes all cryptographic validations")
    void testValidChainPassesAudit() {
        ValidationResult result = BlockchainValidator.validate(validChain);
        assertTrue(result.isValid(), "Clean blockchain must be valid");
        assertEquals(-1, result.failingBlockIndex());
        assertTrue(BlockchainValidator.isChainValid(validChain));
    }

    @Test
    @DisplayName("Altering transaction payload in a block immediately triggers validation failure")
    void testTamperedTransactionPayloadDetected() {
        Blockchain tampered = validChain.copy();
        Block victim = tampered.getBlock(2);

        // Tamper transaction list (e.g. inject forged transfer)
        List<Transaction> forgedTxs = List.of(
                Transaction.create("Hacker", "HackerWallet", 1_000_000.0)
        );
        Block forgedBlock = new Block(
                victim.index(),
                victim.timestamp(),
                victim.previousHash(),
                victim.merkleRoot(), // keep original root
                forgedTxs,
                victim.nonce(),
                victim.difficultyBits(),
                victim.hash()
        );
        tampered.setBlockForSimulation(2, forgedBlock);

        ValidationResult result = BlockchainValidator.validate(tampered);
        assertFalse(result.isValid(), "Tampered transactions must invalidate chain");
        assertEquals(2, result.failingBlockIndex());
        assertTrue(result.failureReason().contains("Transaction payload tampered"),
                "Failure reason should pinpoint transaction tampering: " + result.failureReason());
        assertFalse(BlockchainValidator.isChainValid(tampered));
    }

    @Test
    @DisplayName("Modifying a block's hash without re-mining triggers hash mismatch failure")
    void testTamperedBlockHashDetected() {
        Blockchain tampered = validChain.copy();
        Block victim = tampered.getBlock(1);

        Block forgedBlock = new Block(
                victim.index(),
                victim.timestamp(),
                victim.previousHash(),
                victim.merkleRoot(),
                victim.transactions(),
                victim.nonce(),
                victim.difficultyBits(),
                "f".repeat(64) // Forged hash
        );
        tampered.setBlockForSimulation(1, forgedBlock);

        ValidationResult result = BlockchainValidator.validate(tampered);
        assertFalse(result.isValid());
        assertEquals(1, result.failingBlockIndex());
    }

    @Test
    @DisplayName("Altering previousHash linkage breaks chain continuity")
    void testBrokenHashLinkageDetected() {
        Blockchain tampered = validChain.copy();
        Block victim = tampered.getBlock(2);

        Block forgedBlock = new Block(
                victim.index(),
                victim.timestamp(),
                "0".repeat(64), // Invalid predecessor hash
                victim.merkleRoot(),
                victim.transactions(),
                victim.nonce(),
                victim.difficultyBits(),
                victim.hash()
        );
        tampered.setBlockForSimulation(2, forgedBlock);

        ValidationResult result = BlockchainValidator.validate(tampered);
        assertFalse(result.isValid());
        assertEquals(2, result.failingBlockIndex());
        assertTrue(result.failureReason().contains("Broken hash linkage"));
    }

    @Test
    @DisplayName("Timestamp going backwards in time is rejected as temporal inconsistency")
    void testTimestampInconsistencyDetected() {
        Blockchain tampered = validChain.copy();
        Block victim = tampered.getBlock(2);

        Block forgedBlock = new Block(
                victim.index(),
                tampered.getBlock(1).timestamp() - 5000, // In the past
                victim.previousHash(),
                victim.merkleRoot(),
                victim.transactions(),
                victim.nonce(),
                victim.difficultyBits(),
                victim.hash()
        );
        tampered.setBlockForSimulation(2, forgedBlock);

        ValidationResult result = BlockchainValidator.validate(tampered);
        assertFalse(result.isValid());
        assertEquals(2, result.failingBlockIndex());
        assertTrue(result.failureReason().contains("Temporal paradox"));
    }

    @Test
    @DisplayName("Tampering with the Genesis block is detected at block #0")
    void testGenesisTamperingDetected() {
        Blockchain tampered = validChain.copy();
        Block genesis = tampered.getBlock(0);

        Block forgedGenesis = new Block(
                0,
                genesis.timestamp(),
                "1".repeat(64), // Non-zero previousHash
                genesis.merkleRoot(),
                genesis.transactions(),
                genesis.nonce(),
                genesis.difficultyBits(),
                genesis.hash()
        );
        tampered.setBlockForSimulation(0, forgedGenesis);

        ValidationResult result = BlockchainValidator.validate(tampered);
        assertFalse(result.isValid());
        assertEquals(0, result.failingBlockIndex());
    }
}
