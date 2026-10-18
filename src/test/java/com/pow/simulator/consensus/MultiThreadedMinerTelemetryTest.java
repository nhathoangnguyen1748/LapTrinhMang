package com.pow.simulator.consensus;

import com.pow.simulator.core.Block;
import com.pow.simulator.core.Blockchain;
import com.pow.simulator.core.Transaction;
import com.pow.simulator.core.Vote;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Kiểm thử Tích hợp Động cơ Khai thác PoW Đa Luồng & Đo lường Vi mô (Telemetry).
 * <p>
 * Module phụ trách: Nguyễn Đỗ Trung Lương (MSSV: 2380601305)
 * Kiểm chứng các cơ chế cốt lõi:
 * 1. Khai thác song song qua Java 21 Virtual Threads (Project Loom).
 * 2. So sánh hiệu năng với Platform Thread Pool truyền thống.
 * 3. Độ chính xác của bộ đối sánh mục tiêu băm (TargetMatcher).
 * 4. Thu thập chỉ số đo lường Telemetry (Hashrate, thời gian, số hash đã duyệt).
 * 5. Tích hợp trực tiếp với Sổ cái Bầu cử phân tán (voteChain).
 */
@DisplayName("Kiểm thử Động cơ Đồng thuận PoW & Telemetry (Nguyễn Đỗ Trung Lương - 2380601305)")
class MultiThreadedMinerTelemetryTest {

    @Test
    @DisplayName("Virtual Threads: Khai thác khối thành công và ghi nhận chỉ số Telemetry chính xác")
    void testVirtualThreadsMinerTelemetry() {
        // Khởi tạo miner với chế độ Virtual Threads (Java 21 Project Loom)
        MultiThreadedMiner virtualMiner = new MultiThreadedMiner(
                MultiThreadedMiner.ExecutionMode.VIRTUAL_THREADS, 4
        );
        assertEquals(MultiThreadedMiner.ExecutionMode.VIRTUAL_THREADS, virtualMiner.getExecutionMode());
        assertEquals(4, virtualMiner.getDefaultThreadCount());

        int difficultyBits = 10;
        List<Transaction> txs = List.of(
                Transaction.create("CuTri_A", "LeQuangMinh", 1.0),
                Transaction.create("CuTri_B", "NguyenDoTrungLuong", 1.0)
        );

        Block template = Block.createTemplate(1, System.currentTimeMillis(), "0".repeat(64), txs, difficultyBits);

        // Thực hiện đào khối
        MiningResult result = virtualMiner.mine(template);

        // Kiểm chứng tính đúng đắn của khối được đào
        assertTrue(result.success(), "Thuật toán PoW phải khai thác thành công khối hợp lệ");
        assertNotNull(result.minedBlock(), "Khối được khai thác không được rỗng");
        assertTrue(TargetMatcher.matches(result.minedBlock().hash(), difficultyBits),
                "Mã băm của khối đào phải khớp với TargetMatcher theo difficultyBits");

        // Kiểm chứng các chỉ số vi mô Telemetry
        assertTrue(result.durationMs() >= 0, "Thời gian khai thác không được âm");
        assertTrue(result.totalHashes() > 0, "Số lượng hash đã thử nghiệm phải lớn hơn 0");
        assertTrue(result.hashrate() >= 0.0, "Tốc độ băm (H/s) phải là số dương hợp lệ");
        assertEquals(4, result.threadsUsed(), "Số lượng thread sử dụng phải đúng với cấu hình worker");
    }

    @Test
    @DisplayName("Platform Threads: So sánh tính toàn vẹn giữa Virtual Threads và OS Threads truyền thống")
    void testPlatformThreadsMinerComparison() {
        MultiThreadedMiner platformMiner = new MultiThreadedMiner(
                MultiThreadedMiner.ExecutionMode.PLATFORM_THREADS, 2
        );
        assertEquals(MultiThreadedMiner.ExecutionMode.PLATFORM_THREADS, platformMiner.getExecutionMode());

        int difficultyBits = 8;
        Block template = Block.createTemplate(1, System.currentTimeMillis(), "0".repeat(64), List.of(), difficultyBits);

        MiningResult result = platformMiner.mine(template);

        assertTrue(result.success(), "Chế độ Platform Threads phải khai thác khối thành công");
        assertTrue(TargetMatcher.matches(result.minedBlock().hash(), difficultyBits));
        assertEquals(result.minedBlock().calculateHash(result.minedBlock().nonce()), result.minedBlock().hash(),
                "Mã băm lưu trữ phải trùng khớp tuyệt đối với mã băm tính toán lại từ Nonce");
        assertEquals(2, result.threadsUsed());
    }

    @Test
    @DisplayName("TargetMatcher: Kiểm tra bộ đối sánh mục tiêu theo cả Difficulty Bits và Leading Zeros")
    void testTargetMatcherValidation() {
        // Chuỗi hash có 8 bit 0 đầu (tương đương 2 ký tự '0' ở đầu hệ hex: 0x00...)
        String validHash8Bits = "00a1b2c3d4e5f67890abcdef1234567890abcdef1234567890abcdef12345678";
        assertTrue(TargetMatcher.matches(validHash8Bits, 8), "Hash có 2 số 0 đầu phải thỏa mãn 8 bit 0");
        assertFalse(TargetMatcher.matches(validHash8Bits, 16), "Hash chỉ có 8 bit 0 không thể thỏa mãn 16 bit 0");

        // Chuỗi hash có 16 bit 0 đầu (tương đương 4 ký tự '0' ở đầu hệ hex: 0x0000...)
        String validHash16Bits = "0000a1b2c3d4e5f67890abcdef1234567890abcdef1234567890abcdef12345678";
        assertTrue(TargetMatcher.matches(validHash16Bits, 16));
        assertTrue(TargetMatcher.matches(validHash16Bits, 8));

        // Kiểm tra đối sánh tiền tố chuỗi (Leading Zeros)
        assertTrue(validHash16Bits.startsWith("0000"), "Hash phải bắt đầu bằng 4 số 0");
        assertFalse(validHash8Bits.startsWith("0000"), "Hash 2 số 0 không được thỏa mãn 4 số 0");
    }

    @Test
    @DisplayName("Tích hợp Sổ Cái Bầu Cử: Khai thác phiếu từ Mempool thành công với độ khó tùy chỉnh")
    void testElectionBlockchainMiningIntegration() {
        Blockchain chain = Blockchain.withDefaultGenesis();
        chain.setDifficultyLeadingZeros(2); // Đặt độ khó 2 số 0 đầu cho bài test

        assertEquals(2, chain.getDifficultyLeadingZeros());

        // Đưa các phiếu bầu vào Mempool
        chain.addVoteToMempool(new Vote("SV_TEST_01", "Nguyễn Đỗ Trung Lương"));
        chain.addVoteToMempool(new Vote("SV_TEST_02", "Nguyễn Hoàng Nhật"));

        assertEquals(2, chain.getMempool().size(), "Mempool phải chứa 2 phiếu chờ");

        // Gọi động cơ đào PoW đóng gói khối
        Block minedBlock = chain.minePendingVotes();

        assertNotNull(minedBlock);
        assertTrue(minedBlock.hash().startsWith("00"), "Mã băm khối phải bắt đầu bằng ít nhất 2 số 0");
        assertEquals(2, chain.size(), "Chiều cao chuỗi phải tăng lên 2 khối");
        assertEquals(0, chain.getMempool().size(), "Mempool phải được dọn sạch sau khi đào thành công");
        assertTrue(chain.isChainValid(), "Chuỗi khối bầu cử phải hợp lệ 100%");
    }
}
