package com.pow.simulator;

import com.pow.simulator.consensus.InteractiveMinerCLI;
import com.pow.simulator.consensus.MiningResult;
import com.pow.simulator.consensus.MultiThreadedMiner;
import com.pow.simulator.consensus.TargetMatcher;
import com.pow.simulator.core.Blockchain;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit and integration tests for the Interactive Mining Suite.
 * Authored by: Nguyễn Đỗ Trung Lương (MSSV: 2380601305)
 */
class InteractiveMinerTest {

    @Test
    @DisplayName("Should successfully mine block via programmatic CLI simulation with Virtual Threads")
    void testProgrammaticMiningWithVirtualThreads() {
        Blockchain chain = Blockchain.withDefaultGenesis(8);
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outContent);

        InteractiveMinerCLI cli = new InteractiveMinerCLI(System.in, printStream);
        MiningResult result = cli.runMiningSimulation(
                chain,
                MultiThreadedMiner.ExecutionMode.VIRTUAL_THREADS,
                4,
                8
        );

        assertTrue(result.success(), "Mining simulation should succeed");
        assertNotNull(result.minedBlock(), "Mined block should not be null");
        assertEquals(1, result.minedBlock().index(), "New block index should be 1");
        assertTrue(TargetMatcher.matches(result.minedBlock().hash(), 8), "Hash must meet target difficulty");
        assertEquals(2, chain.size(), "Chain size should increase to 2");

        String output = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("STATUS        : SUCCESS"), "CLI output should report SUCCESS");
        assertTrue(output.contains("WINNING NONCE"), "CLI output should report winning nonce");
    }

    @Test
    @DisplayName("Should parse and execute interactive session from simulated user input")
    void testInteractiveSessionWithSimulatedInput() {
        Blockchain chain = Blockchain.withDefaultGenesis(8);
        // Simulate inputs: mode "1" (Virtual Threads), "2" workers, "8" bits
        String simulatedInput = "1\n2\n8\n";
        ByteArrayInputStream inContent = new ByteArrayInputStream(simulatedInput.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outContent);

        InteractiveMinerCLI cli = new InteractiveMinerCLI(inContent, printStream);
        cli.startInteractiveSession(chain);

        assertEquals(2, chain.size(), "Interactive session should append mined block to chain");
        String output = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("INTERACTIVE PROOF-OF-WORK MINING CONSOLE"), "Header should be printed");
        assertTrue(output.contains("Nguyen Do Trung Luong") || output.contains("Nguyễn Đỗ Trung Lương"), "Author attribution should be printed");
    }
}
