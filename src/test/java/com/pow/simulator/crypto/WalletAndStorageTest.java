package com.pow.simulator.crypto;

import com.pow.simulator.core.Block;
import com.pow.simulator.core.Blockchain;
import com.pow.simulator.core.Transaction;
import com.pow.simulator.storage.LedgerStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ECDSA Wallet, Cryptographic Signature & Ledger Storage Tests")
class WalletAndStorageTest {

    @Test
    @DisplayName("Test ECDSA KeyPair generation and address formatting")
    void testWalletGeneration() {
        Wallet wallet = Wallet.createNew();
        assertNotNull(wallet);
        assertNotNull(wallet.getAddress());
        assertTrue(wallet.getAddress().startsWith("pow1_"), "Address must start with prefix pow1_");
        assertNotNull(wallet.getPrivateKeyHex());
        assertFalse(wallet.getPrivateKeyHex().isBlank());
        assertNotNull(wallet.getPublicKeyHex());
        assertFalse(wallet.getPublicKeyHex().isBlank());
    }

    @Test
    @DisplayName("Test digital signature signing and verification")
    void testDigitalSignatureVerification() {
        Wallet sender = Wallet.createNew();

        String payload = "pow1_testSender:pow1_testRecipient:50.0:1700000000000";
        String signature = sender.sign(payload);

        assertNotNull(signature);
        assertFalse(signature.isBlank());

        // Verify valid signature
        boolean isValid = Wallet.verify(sender.getPublicKeyHex(), payload, signature);
        assertTrue(isValid, "Digital signature must verify correctly with matching public key");

        // Verify tampering is detected
        String tamperedPayload = "pow1_testSender:pow1_testRecipient:500.0:1700000000000";
        boolean isTamperedValid = Wallet.verify(sender.getPublicKeyHex(), tamperedPayload, signature);
        assertFalse(isTamperedValid, "Tampered payload must fail signature verification");

        // Verify wrong public key fails
        Wallet attacker = Wallet.createNew();
        boolean isAttackerValid = Wallet.verify(attacker.getPublicKeyHex(), payload, signature);
        assertFalse(isAttackerValid, "Wrong public key must fail signature verification");
    }

    @Test
    @DisplayName("Test Transaction object signature integration")
    void testTransactionSignature() {
        Wallet sender = Wallet.createNew();
        Wallet receiver = Wallet.createNew();

        Transaction signedTx = Transaction.createSigned(sender, receiver.getAddress(), 15.5);
        assertNotNull(signedTx);
        assertEquals(sender.getAddress(), signedTx.sender());
        assertEquals(receiver.getAddress(), signedTx.recipient());
        assertEquals(15.5, signedTx.amount());
        assertNotNull(signedTx.signature());
        assertFalse(signedTx.signature().isBlank());
        assertNotNull(signedTx.publicKey());
        assertFalse(signedTx.publicKey().isBlank());

        assertTrue(signedTx.isSignatureValid(), "Transaction with valid signature must pass isSignatureValid");

        // Tamper transaction amount
        Transaction tamperedTx = new Transaction(
                signedTx.txId(),
                signedTx.sender(),
                signedTx.recipient(),
                999.0, // tampered amount
                signedTx.timestamp(),
                signedTx.signature(),
                signedTx.publicKey()
        );
        assertFalse(tamperedTx.isSignatureValid(), "Transaction with altered amount must fail isSignatureValid");
    }

    @Test
    @DisplayName("Test Blockchain balance calculation")
    void testBlockchainBalance() {
        Blockchain chain = Blockchain.withDefaultGenesis(8);
        Wallet alice = Wallet.createNew();
        Wallet bob = Wallet.createNew();

        // Initially balance is 0
        assertEquals(0.0, chain.getBalance(alice.getAddress()));
        assertEquals(0.0, chain.getBalance(bob.getAddress()));

        // Block 1: Coinbase gives Alice 50 COIN
        Transaction coinbaseTx = Transaction.create("COINBASE", alice.getAddress(), 50.0);
        Block block1 = Block.createTemplate(1, System.currentTimeMillis(), chain.getLatestBlock().hash(), List.of(coinbaseTx), 8);
        Block mined1 = new Block(1, block1.timestamp(), block1.previousHash(), block1.merkleRoot(), block1.transactions(), 8, 100, "00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff");
        chain.addBlock(mined1);

        assertEquals(50.0, chain.getBalance(alice.getAddress()));

        // Block 2: Alice sends 20 COIN to Bob
        Transaction sendTx = Transaction.createSigned(alice, bob.getAddress(), 20.0);
        Block block2 = Block.createTemplate(2, System.currentTimeMillis(), mined1.hash(), List.of(sendTx), 8);
        Block mined2 = new Block(2, block2.timestamp(), block2.previousHash(), block2.merkleRoot(), block2.transactions(), 8, 200, "00aabbccddeeff00112233445566778899aabbccddeeff001122334455667788");
        chain.addBlock(mined2);

        assertEquals(30.0, chain.getBalance(alice.getAddress()), 0.001);
        assertEquals(20.0, chain.getBalance(bob.getAddress()), 0.001);
    }

    @Test
    @DisplayName("Test LedgerStorage save and load")
    void testLedgerStorage(@TempDir Path tempDir) throws IOException {
        LedgerStorage storage = new LedgerStorage(tempDir);

        Blockchain originalChain = Blockchain.withDefaultGenesis(8);
        Wallet wallet = Wallet.createNew();
        Transaction tx = Transaction.create("SYSTEM", wallet.getAddress(), 100.0);
        Block block1 = Block.createTemplate(1, System.currentTimeMillis(), originalChain.getLatestBlock().hash(), List.of(tx), 8);
        Block mined1 = new Block(1, block1.timestamp(), block1.previousHash(), block1.merkleRoot(), block1.transactions(), 8, 42, "00testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttest");
        originalChain.addBlock(mined1);

        // Save
        storage.saveChain(originalChain);
        assertTrue(storage.hasSavedChain(), "Saved file must exist on disk");
        assertTrue(storage.getChainFileSize() > 0, "File size must be greater than 0");

        // Load
        Blockchain loadedChain = storage.loadChain();
        assertNotNull(loadedChain, "Loaded chain must not be null");
        assertEquals(2, loadedChain.size(), "Loaded chain size must match original chain");
        assertEquals(originalChain.getBlock(0).hash(), loadedChain.getBlock(0).hash());
        assertEquals(originalChain.getLatestBlock().hash(), loadedChain.getLatestBlock().hash());
        assertEquals(100.0, loadedChain.getBalance(wallet.getAddress()));
    }
}
