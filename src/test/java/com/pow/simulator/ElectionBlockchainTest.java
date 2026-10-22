package com.pow.simulator;

import com.pow.simulator.core.Block;
import com.pow.simulator.core.Blockchain;
import com.pow.simulator.core.Vote;
import com.pow.simulator.storage.LedgerStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Hệ Thống Bầu Cử Blockchain (E-Voting) Tests")
class ElectionBlockchainTest {

    @Test
    @DisplayName("Block phải có đúng 5 thuộc tính: hash, time, pre hash, nội dung, nonce")
    void testBlockHasFiveAttributes() {
        long now = System.currentTimeMillis();
        String preHash = "0".repeat(64);
        String noiDung = "Cử tri [SV01] bầu cho [Lê Quang Minh]";
        long nonce = 12345L;
        String expectedHash = Block.calculateHash(preHash, now, noiDung, nonce);

        Block block = new Block(expectedHash, now, preHash, noiDung, nonce);

        // 1. hash
        assertEquals(expectedHash, block.hash());
        // 2. time / timestamp
        assertEquals(now, block.timestamp());
        assertNotNull(block.time());
        // 3. pre hash / previousHash
        assertEquals(preHash, block.previousHash());
        assertEquals(preHash, block.preHash());
        // 4. nội dung / content
        assertEquals(noiDung, block.content());
        assertEquals(noiDung, block.noiDung());
        // 5. nonce
        assertEquals(nonce, block.nonce());
    }

    @Test
    @DisplayName("Mempool lưu trữ thông tin bầu cử và ngăn chặn cử tri bầu 2 lần (Chống gian lận)")
    void testMempoolAndDoubleVoting() {
        Blockchain chain = Blockchain.withDefaultGenesis();
        assertEquals(0, chain.getMempool().size());

        Vote vote1 = new Vote("SV001 - Nguyễn Hoàng Nhật", "Lê Quang Minh");
        chain.addVoteToMempool(vote1);

        assertEquals(1, chain.getMempool().size());
        assertEquals("SV001 - Nguyễn Hoàng Nhật", chain.getMempool().get(0).voterId());
        assertEquals("Lê Quang Minh", chain.getMempool().get(0).candidate());

        // Cố tình bầu lần 2 với cùng mã cử tri -> phải ném lỗi IllegalArgumentException
        Vote duplicateVote = new Vote("SV001 - Nguyễn Hoàng Nhật", "Nguyễn Đỗ Trung Lương");
        assertThrows(IllegalArgumentException.class, () -> chain.addVoteToMempool(duplicateVote),
                "Hệ thống phải từ chối khi cùng một cử tri bỏ phiếu lần 2");
    }

    @Test
    @DisplayName("Khai thác PoW: Đóng gói phiếu từ Mempool thành Block mới có 5 thuộc tính và 5 số 0 đầu")
    void testPoWMiningAndLedgerUpdate() {
        Blockchain chain = Blockchain.withDefaultGenesis();
        chain.setDifficultyLeadingZeros(5); // Mục tiêu: hash bắt đầu bằng "00000"

        Vote vote1 = new Vote("SV01 - Hoàng Nhật", "Lê Quang Minh");
        Vote vote2 = new Vote("SV02 - Trung Lương", "Nguyễn Hoàng Nhật");
        chain.addVoteToMempool(vote1);
        chain.addVoteToMempool(vote2);

        assertEquals(2, chain.getMempool().size());

        // Khai thác PoW đóng khối
        Block minedBlock = chain.minePendingVotes();

        assertNotNull(minedBlock);
        assertEquals(2, chain.size(), "Chiều cao chuỗi phải tăng lên 2");
        assertTrue(minedBlock.hash().startsWith("00000"), "Mã hash khối mới phải bắt đầu bằng 5 số 0 ('00000')");
        assertEquals(chain.getBlock(0).hash(), minedBlock.previousHash(), "PreHash phải trỏ vào Genesis block");
        assertTrue(minedBlock.content().contains("SV01 - Hoàng Nhật"));
        assertTrue(minedBlock.content().contains("SV02 - Trung Lương"));
        assertTrue(minedBlock.nonce() >= 0);

        // Mempool phải được làm trống sau khi đóng khối
        assertEquals(0, chain.getMempool().size());

        // Cử tri đã nằm trong sổ cái không được bầu lại
        Vote voteRepeat = new Vote("SV01 - Hoàng Nhật", "Lê Quang Minh");
        assertThrows(IllegalArgumentException.class, () -> chain.addVoteToMempool(voteRepeat));
    }

    @Test
    @DisplayName("Sổ Cái thống kê kết quả bầu cử chính xác và lưu danh sách cử tri đã bầu")
    void testElectionResultsAndVoterList() {
        Blockchain chain = Blockchain.withDefaultGenesis();
        chain.setDifficultyLeadingZeros(1); // Đào siêu nhanh cho test

        List<String> candidates = List.of("Lê Quang Minh", "Nguyễn Đỗ Trung Lương", "Nguyễn Hoàng Nhật");

        // Đợt 1: 2 người bầu
        chain.addVoteToMempool(new Vote("SV01", "Lê Quang Minh"));
        chain.addVoteToMempool(new Vote("SV02", "Lê Quang Minh"));
        chain.minePendingVotes();

        // Đợt 2: 1 người bầu
        chain.addVoteToMempool(new Vote("SV03", "Nguyễn Hoàng Nhật"));
        chain.minePendingVotes();

        // Kiểm tra kết quả Sổ Cái
        Map<String, Integer> results = chain.getElectionResults(candidates);
        assertEquals(2, results.get("Lê Quang Minh"));
        assertEquals(0, results.get("Nguyễn Đỗ Trung Lương"));
        assertEquals(1, results.get("Nguyễn Hoàng Nhật"));

        assertEquals(3, chain.getTotalVotes());
        List<String> voters = chain.getAllVotedVoters();
        assertTrue(voters.contains("SV01"));
        assertTrue(voters.contains("SV02"));
        assertTrue(voters.contains("SV03"));
    }

    @Test
    @DisplayName("Phát hiện gian lận: Sửa đổi nội dung phiếu bầu trong khối cũ làm đứt chuỗi ngay lập tức")
    void testTamperDetection() {
        Blockchain chain = Blockchain.withDefaultGenesis();
        chain.setDifficultyLeadingZeros(1);

        chain.addVoteToMempool(new Vote("SV01 - Hoàng Nhật", "Lê Quang Minh"));
        chain.minePendingVotes();

        assertTrue(chain.isChainValid(), "Chuỗi nguyên bản phải hợp lệ");

        // Hacker can thiệp sửa nội dung Block #1
        chain.tamperBlockContent(1, "Cử tri [Hacker] đã tráo phiếu bầu thành công!");

        assertFalse(chain.isChainValid(), "Sổ cái phải phát hiện gian lận khi nội dung bị sửa đổi");
    }

    @Test
    @DisplayName("Lưu trữ ổ cứng (Persistence): Sổ cái bầu cử được lưu và phục hồi vĩnh viễn")
    void testLedgerStoragePersistence(@TempDir Path tempDir) throws IOException {
        LedgerStorage storage = new LedgerStorage(tempDir);

        Blockchain original = Blockchain.withDefaultGenesis();
        original.setDifficultyLeadingZeros(1);
        original.addVoteToMempool(new Vote("SV01 - Hoàng Nhật", "Lê Quang Minh"));
        original.minePendingVotes();

        // Lưu vào đĩa
        storage.saveChain(original);
        assertTrue(storage.hasSavedChain());

        // Tải lại từ đĩa
        Blockchain restored = storage.loadChain();
        assertNotNull(restored);
        assertEquals(2, restored.size());
        assertEquals(original.getBlock(1).hash(), restored.getBlock(1).hash());
        assertEquals(original.getBlock(1).content(), restored.getBlock(1).content());
        assertEquals(original.getBlock(1).nonce(), restored.getBlock(1).nonce());
    }

    @Test
    @DisplayName("Độ khó tùy chỉnh linh hoạt: Có thể thay đổi từ 1 đến 5 số 0 theo ý muốn")
    void testDynamicDifficultyCustomization() {
        Blockchain chain = Blockchain.withDefaultGenesis();

        // Độ khó = 2
        chain.setDifficultyLeadingZeros(2);
        chain.addVoteToMempool(new Vote("VoterA", "Candidate1"));
        Block b1 = chain.minePendingVotes();
        assertTrue(b1.hash().startsWith("00"));

        // Thay đổi sang Độ khó = 3
        chain.setDifficultyLeadingZeros(3);
        chain.addVoteToMempool(new Vote("VoterB", "Candidate2"));
        Block b2 = chain.minePendingVotes();
        assertTrue(b2.hash().startsWith("000"));

        // Thay đổi sang Độ khó = 4
        chain.setDifficultyLeadingZeros(4);
        chain.addVoteToMempool(new Vote("VoterC", "Candidate3"));
        Block b3 = chain.minePendingVotes();
        assertTrue(b3.hash().startsWith("0000"));

        assertEquals(4, chain.size());
        assertTrue(chain.isChainValid());
    }

    @Test
    @DisplayName("Cụm mạng P2P đa Node: Phiếu bầu phát tán vào Mempool của tất cả các Node, khối đào đồng bộ toàn mạng")
    void testMultiNodeClusterMempoolAndBlockSync() {
        Blockchain node1 = Blockchain.withDefaultGenesis();
        Blockchain node2 = node1.copy();
        Blockchain node3 = node1.copy();

        assertEquals(1, node1.size());
        assertEquals(1, node2.size());
        assertEquals(1, node3.size());

        // Cử tri gửi phiếu bầu -> Phát tán P2P vào Mempool của cả 3 node
        Vote vote = new Vote("2380601549", "Nguyễn Hoàng Nhật");
        node1.addVoteToMempool(vote);
        node2.addVoteToMempool(vote);
        node3.addVoteToMempool(vote);

        assertEquals(1, node1.getMempool().size());
        assertEquals(1, node2.getMempool().size());
        assertEquals(1, node3.getMempool().size());
        assertEquals("2380601549", node2.getMempool().get(0).voterId());
        assertEquals("2380601549", node3.getMempool().get(0).voterId());

        // Node 1 đào khối PoW
        node1.setDifficultyLeadingZeros(2);
        node2.setDifficultyLeadingZeros(2);
        node3.setDifficultyLeadingZeros(2);
        Block minedBlock = node1.minePendingVotes();

        // Node 1 đã đóng khối và dọn sạch mempool
        assertEquals(2, node1.size());
        assertEquals(0, node1.getMempool().size());

        // Phát tán Block mới sang Node 2 và Node 3
        node2.syncMinedBlock(minedBlock);
        node3.syncMinedBlock(minedBlock);

        // Cả 3 node đều có cùng chiều cao sổ cái và mempool đều sạch
        assertEquals(2, node2.size());
        assertEquals(2, node3.size());
        assertEquals(0, node2.getMempool().size());
        assertEquals(0, node3.getMempool().size());

        // Cả 3 sổ cái đều hợp lệ tuyệt đối
        assertTrue(node1.isChainValid());
        assertTrue(node2.isChainValid());
        assertTrue(node3.isChainValid());
    }
}
