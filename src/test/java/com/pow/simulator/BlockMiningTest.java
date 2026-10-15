package com.pow.simulator;

import com.pow.simulator.consensus.MiningResult;
import com.pow.simulator.consensus.MultiThreadedMiner;
import com.pow.simulator.consensus.TargetMatcher;
import com.pow.simulator.core.Block;
import com.pow.simulator.core.MerkleTree;
import com.pow.simulator.core.Transaction;
import com.pow.simulator.crypto.HashUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Block Mining & Cryptography Tests")
class BlockMiningTest {

    private MultiThreadedMiner miner;

    @BeforeEach
    void setUp() {
        miner = new MultiThreadedMiner(MultiThreadedMiner.ExecutionMode.VIRTUAL_THREADS, 4);
    }

    @Test
    @DisplayName("Mined block hash must satisfy the configured difficulty bits")
    void testMinedBlockHashSatisfiesTargetDifficulty() {
        int difficultyBits = 12; // 12 leading zero bits
        List<Transaction> txs = List.of(
                Transaction.create("Alice", "Bob", 10.5),
                Transaction.create("Bob", "Charlie", 2.0)
        );

        Block template = Block.createTemplate(1, System.currentTimeMillis(), "0".repeat(64), txs, difficultyBits);
        MiningResult result = miner.mine(template);

        assertTrue(result.success(), "Mining should successfully find a valid block");
        Block mined = result.minedBlock();
        assertNotNull(mined);

        // Verify TargetMatcher accepts the hash
        assertTrue(TargetMatcher.matches(mined.hash(), difficultyBits),
                "Mined hash must satisfy target difficulty bits");

        // Verify the number of leading zero bits is >= difficultyBits
        int leadingZeros = HashUtil.countLeadingZeroBits(mined.hash());
        assertTrue(leadingZeros >= difficultyBits,
                String.format("Expected >= %d zero bits, but got %d", difficultyBits, leadingZeros));
    }

    @Test
    @DisplayName("Recalculated header hash with winning nonce must strictly equal stored hash")
    void testHeaderHashRecalculationMatches() {
        int difficultyBits = 10;
        List<Transaction> txs = List.of(Transaction.create("Coinbase", "Miner1", 50.0));
        Block template = Block.createTemplate(2, System.currentTimeMillis(), "abc12345".repeat(8), txs, difficultyBits);

        MiningResult result = miner.mine(template);
        Block mined = result.minedBlock();

        String recalculatedHash = mined.calculateHash(mined.nonce());
        assertEquals(mined.hash(), recalculatedHash, "Stored hash must match recalculated hash");
    }

    @Test
    @DisplayName("Altering the nonce produces an entirely different hash")
    void testNonceAlterationChangesHash() {
        int difficultyBits = 8;
        Block template = Block.createTemplate(1, System.currentTimeMillis(), "0".repeat(64), List.of(), difficultyBits);
        MiningResult result = miner.mine(template);
        Block mined = result.minedBlock();

        String alteredHash = mined.calculateHash(mined.nonce() + 1);
        assertNotEquals(mined.hash(), alteredHash, "Altering nonce must change cryptographic hash");
    }

    @Test
    @DisplayName("Merkle Tree calculation correctly handles 0, 1, 2, and odd transaction sets")
    void testMerkleTreeCalculations() {
        // 0 transactions
        String emptyRoot = MerkleTree.calculateRoot(List.of());
        assertNotNull(emptyRoot);
        assertEquals(64, emptyRoot.length());

        // 1 transaction
        Transaction tx1 = Transaction.create("A", "B", 1.0);
        String singleRoot = MerkleTree.calculateRoot(List.of(tx1));
        assertEquals(tx1.txId(), singleRoot);

        // 2 transactions
        Transaction tx2 = Transaction.create("B", "C", 2.0);
        String pairRoot = MerkleTree.calculateRoot(List.of(tx1, tx2));
        String expectedPair = HashUtil.sha256Hex(tx1.txId() + tx2.txId());
        assertEquals(expectedPair, pairRoot);

        // 3 transactions (odd count duplicates last transaction, Bitcoin style)
        Transaction tx3 = Transaction.create("C", "D", 3.0);
        String oddRoot = MerkleTree.calculateRoot(List.of(tx1, tx2, tx3));
        String level1Right = HashUtil.sha256Hex(tx3.txId() + tx3.txId());
        String expectedOdd = HashUtil.sha256Hex(expectedPair + level1Right);
        assertEquals(expectedOdd, oddRoot);
    }
}
