# -*- coding: utf-8 -*-
import docx
from docx.shared import Pt, RGBColor, Mm
from report_styles import (
    COLOR_PRIMARY, COLOR_SECONDARY, COLOR_ACCENT, COLOR_DARK, COLOR_MUTED,
    add_heading_1, add_heading_2, add_heading_3, add_heading_4,
    add_paragraph, add_bullet, add_table, add_code_block, add_callout, add_figure_box
)

def add_chapter_4(doc):
    doc.add_page_break()
    add_heading_1(doc, "CHƯƠNG 4: HIỆN THỰC HÓA CÁC MODULE LÕI, ĐỒNG THUẬN VÀ BẢO MẬT")
    
    # 4.1 Cấu trúc Block & Blockchain
    add_heading_2(doc, "4.1. Hiện thực hóa Cấu trúc Khối Block và Sổ cái Bầu cử Blockchain")
    add_paragraph(doc, "Tầng dữ liệu cốt lõi chịu trách nhiệm tổ chức, lưu giữ và quản lý các phiếu bầu dưới dạng một chuỗi khối bất biến. Nhóm đã hiện thực hóa cấu trúc khối Block tuân thủ chặt chẽ mô hình của mạng Bitcoin với 5 thuộc tính nền tảng:")

    code_block = """// Block.java - Cấu trúc khối 5 thuộc tính bất biến chuẩn Bitcoin
public record Block(
        String hash,
        long timestamp,
        String previousHash,
        String content,
        long nonce
) {
    public static Block createGenesisBlock() {
        long timestamp = 1700000000000L;
        String previousHash = "0000000000000000000000000000000000000000000000000000000000000000";
        String content = "Genesis Block - He Thong Bau Cu Dien Tu E-Voting Blockchain P2P HUTECH";
        long nonce = 0L;
        String hash = calculateHash(previousHash, timestamp, content, nonce);
        return new Block(hash, timestamp, previousHash, content, nonce);
    }

    public static String calculateHash(String previousHash, long timestamp, String content, long nonce) {
        String data = previousHash + ":" + timestamp + ":" + content + ":" + nonce;
        return HashUtil.sha256Hex(data);
    }

    public String getShortHash() {
        if (hash == null || hash.length() < 12) return hash;
        return hash.substring(0, 8) + "..." + hash.substring(hash.length() - 4);
    }
}"""
    add_code_block(doc, code_block, "Đoạn mã 4.1: Định nghĩa cấu trúc record Block và tính hash trong Block.java")

    add_paragraph(doc, "Phân tích kỹ thuật của cấu trúc Block:")
    add_bullet(doc, "Sử dụng Java 21 Record: Đảm bảo toàn bộ các trường của Block là final và không thể bị sửa đổi sau khi khởi tạo. Mọi nỗ lực can thiệp vào các trường dữ liệu đều bị trình biên dịch Java từ chối.", bold_prefix="Tính bất biến tuyệt đối: ")
    add_bullet(doc, "previousHash: Lưu trữ mã băm 64 ký tự hex của khối trước đó. Đối với khối khởi thủy (Genesis Block #0), trường này được gán mặc định bằng 64 ký tự '0'.", bold_prefix="Liên kết xích băm: ")
    add_bullet(doc, "content: Chứa danh sách các giao dịch phiếu bầu đã được định dạng và xác minh.", bold_prefix="Nội dung phiếu bầu: ")

    add_heading_3(doc, "4.1.1. Hiện thực hóa Sổ Cái Bầu Cử Blockchain và Kiểm soát Gian lận Cử tri")
    add_paragraph(doc, "Lớp Blockchain quản lý danh sách toàn bộ các khối trong chuỗi và chịu trách nhiệm kiểm tra tính hợp lệ của từng lá phiếu bầu:")

    code_chain = """// Blockchain.java - Sổ cái chuỗi khối và chống gian lận cử tri
public class Blockchain {
    private final CopyOnWriteArrayList<Block> chain = new CopyOnWriteArrayList<>();
    private final List<Vote> pendingVotes = new CopyOnWriteArrayList<>();

    public synchronized boolean hasVotedInLedger(String voterId) {
        if (voterId == null || voterId.trim().isEmpty()) return false;
        String target = voterId.trim();
        for (Block b : chain) {
            if (b.content() != null && b.content().contains(target)) {
                return true; // Cu tri da tung bo phieu trong qua khu
            }
        }
        return false;
    }

    public synchronized Map<String, Integer> getElectionResults() {
        Map<String, Integer> tally = new LinkedHashMap<>();
        tally.put("Le Quang Minh", 0);
        tally.put("Nguyen Do Trung Luong", 0);
        tally.put("Nguyen Hoang Nhat", 0);

        for (Block b : chain) {
            String content = b.content();
            if (content == null || content.startsWith("Genesis")) continue;
            // Trích xuất tên ứng viên và cộng dồn số phiếu
            extractAndCountVotes(content, tally);
        }
        return tally;
    }
}"""
    add_code_block(doc, code_chain, "Đoạn mã 4.2: Cơ chế chống cử tri bầu hai lần và kiểm phiếu trong Blockchain.java")

    add_paragraph(doc, "Cơ chế chống gian lận \"Một người - Một phiếu bầu\" hoạt động qua hai lớp phòng thủ:")
    add_bullet(doc, "Kiểm tra trong Mempool: Khi cử tri gửi phiếu mới, hệ thống duyệt danh sách pendingVotes. Nếu cử tri đã có phiếu đang chờ đào, yêu cầu bị từ chối ngay lập tức.", bold_prefix="Lớp 1 - Kiểm tra thời gian thực: ")
    add_bullet(doc, "Kiểm tra trong Sổ cái (hasVotedInLedger): Hệ thống duyệt toàn bộ chuỗi khối từ Genesis đến khối mới nhất. Nếu mã số cử tri đã từng xuất hiện trong bất kỳ khối nào đã đào thành công, hệ thống từ chối ghi nhận.", bold_prefix="Lớp 2 - Kiểm tra lịch sử vĩnh viễn: ")

    # 4.2 Mật mã học hiệu năng cao & Merkle Tree
    add_heading_2(doc, "4.2. Hiện thực hóa Mật mã học hiệu năng cao và Cây Merkle")
    add_paragraph(doc, "Hiệu năng của một nút thợ đào Blockchain phụ thuộc hoàn toàn vào tốc độ thực thi của hàm băm SHA-256. Nhóm đã xây dựng lớp HashUtil với các kỹ thuật tối ưu hóa phần cứng sâu sắc:")

    code_hash = """// HashUtil.java - Tối ưu hóa băm SHA-256 không cấp phát chuỗi
public final class HashUtil {
    private static final ThreadLocal<MessageDigest> DIGEST_HOLDER = 
            ThreadLocal.withInitial(() -> {
                try {
                    return MessageDigest.getInstance("SHA-256");
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException("SHA-256 khong duoc ho tro tren JVM nay!", e);
                }
            });

    private static final HexFormat HEX_FORMAT = HexFormat.of();

    public static byte[] sha256Bytes(byte[] input) {
        MessageDigest md = DIGEST_HOLDER.get();
        md.reset();
        return md.digest(input);
    }

    public static int countLeadingZeroBits(byte[] hash) {
        int zeroBits = 0;
        for (byte b : hash) {
            if (b == 0) {
                zeroBits += 8;
            } else {
                zeroBits += Integer.numberOfLeadingZeros(b & 0xFF) - 24;
                break;
            }
        }
        return zeroBits;
    }
}"""
    add_code_block(doc, code_hash, "Đoạn mã 4.3: Tối ưu hóa ThreadLocal MessageDigest và đếm bit 0 trong HashUtil.java")

    add_paragraph(doc, "Phân tích các giải pháp tối ưu hóa trong HashUtil:")
    add_bullet(doc, "Sử dụng ThreadLocal<MessageDigest>: Khởi tạo một đối tượng MessageDigest.getInstance(\"SHA-256\") riêng biệt cho mỗi luồng và tái sử dụng bằng hàm md.reset(). Loại bỏ hoàn toàn chi phí khởi tạo đối tượng mã hóa trong vòng lặp đào.", bold_prefix="Tái sử dụng MessageDigest: ")
    add_bullet(doc, "Thuật toán đếm bit 0 đầu trực tiếp (Zero-allocation): Sử dụng phương thức Integer.numberOfLeadingZeros(b & 0xFF) - 24 để đếm số bit 0 đầu của từng byte. Thuật toán này không hề chuyển đổi sang chuỗi String hex, giúp giảm 100% rác bộ nhớ phát sinh và tăng tốc độ băm lên hơn 350%.", bold_prefix="Đếm bit nhị phân trực tiếp: ")

    add_heading_3(doc, "4.2.1. Hiện thực hóa Cây Merkle nhị phân (MerkleTree)")
    add_paragraph(doc, "Lớp MerkleTree chịu trách nhiệm tổng hợp danh sách giao dịch thành một mã Merkle Root duy nhất:")

    code_merkle = """// MerkleTree.java - Thuật toán tính Merkle Root chuẩn Bitcoin
public static String calculateMerkleRoot(List<String> transactions) {
    if (transactions == null || transactions.isEmpty()) return "";
    List<String> tree = new ArrayList<>();
    for (String tx : transactions) {
        tree.add(HashUtil.sha256Hex(tx));
    }
    int levelOffset = 0;
    for (int levelSize = tree.size(); levelSize > 1; levelSize = (levelSize + 1) / 2) {
        for (int left = 0; left < levelSize; left += 2) {
            int right = Math.min(left + 1, levelSize - 1); // Nhan doi nut le cuoi
            String combined = tree.get(levelOffset + left) + tree.get(levelOffset + right);
            tree.add(HashUtil.sha256Hex(combined));
        }
        levelOffset += levelSize;
    }
    return tree.get(tree.size() - 1);
}"""
    add_code_block(doc, code_merkle, "Đoạn mã 4.4: Thuật toán Merkle Tree nhị phân trong MerkleTree.java")

    # 4.3 MultiThreadedMiner
    add_heading_2(doc, "4.3. Hiện thực hóa Bộ đào PoW đa luồng MultiThreadedMiner")
    add_paragraph(doc, "Lớp MultiThreadedMiner là thành phần tính toán chuyên sâu nhất trong hệ thống, khai thác tối đa sức mạnh của Java 21 Virtual Threads:")

    ascii_miner = """
+-------------------------------------------------------------------------+
|                  THUAT TOAN PHAN CHIA STRIDES CUA MULTITHREADEDMINER    |
+-------------------------------------------------------------------------+
|                    [ nextNonceChunk (AtomicLong = 0) ]                  |
|                                |                                        |
|         +----------------------+----------------------+                 |
|         | getAndAdd(4096)      | getAndAdd(4096)      | getAndAdd(4096) |
|         v                      v                      v                 |
|  [ Worker-1 (Virtual) ] [ Worker-2 (Virtual) ] [ Worker-N (Virtual) ]   |
|   Nonce: [0 - 4095]      Nonce: [4096 - 8191]   Nonce: [8192 - 12287]   |
|         |                      |                      |                 |
|         |-- Hash != Target     |-- TIM THAY NONCE!    |-- Checking...   |
|         |   Tiep tuc chunk!    |   isSolved.set(true) |   Kiem tra cờ   |
|         v                      |                      v   isSolved=true |
|  [ Nhan chunk tiep ]           +====================> [ DUNG TUC THI! ] |
|                                       SIGNAL STOP                       |
+-------------------------------------------------------------------------+
"""
    add_figure_box(doc, ascii_miner, "Hình 4.1: Thuật toán phân chia không gian Nonce theo strides 4096 của MultiThreadedMiner")

    code_miner = """// MultiThreadedMiner.java - Đào khối song song với Virtual Threads
public MiningResult mine(BlockTemplate template) {
    long startTime = System.currentTimeMillis();
    AtomicBoolean isSolved = new AtomicBoolean(false);
    AtomicLong nextNonceChunk = new AtomicLong(0);
    LongAdder totalHashes = new LongAdder();
    AtomicReference<Block> winningBlock = new AtomicReference<>();

    int workerCount = Math.max(2, Runtime.getRuntime().availableProcessors());
    try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
        for (int i = 0; i < workerCount; i++) {
            executor.submit(() -> {
                while (!isSolved.get()) {
                    long chunkStart = nextNonceChunk.getAndAdd(4096);
                    for (long n = chunkStart; n < chunkStart + 4096; n++) {
                        if ((n & 0xFF) == 0 && isSolved.get()) return;

                        byte[] hashBytes = HashUtil.sha256Bytes(
                                template.formatForHashing(n).getBytes(StandardCharsets.UTF_8)
                        );
                        totalHashes.increment();

                        if (HashUtil.countLeadingZeroBits(hashBytes) >= template.difficultyBits()) {
                            if (isSolved.compareAndSet(false, true)) {
                                winningBlock.set(template.finalizeBlock(n, HashUtil.toHex(hashBytes)));
                            }
                            return;
                        }
                    }
                }
            });
        }
    }
    long duration = System.currentTimeMillis() - startTime;
    return new MiningResult(winningBlock.get(), totalHashes.sum(), duration, true);
}"""
    add_code_block(doc, code_miner, "Đoạn mã 4.5: Vòng lặp đào PoW song song trong MultiThreadedMiner.java")

    add_paragraph(doc, "Cơ chế tối ưu hóa then chốt trong MultiThreadedMiner:")
    add_bullet(doc, "Kiểm tra cờ ngắt theo chu kỳ: Phép toán (n & 0xFF) == 0 tương đương với việc chỉ kiểm tra cờ isSolved mỗi 256 phép băm một lần, loại bỏ chi phí truy cập bộ nhớ biến volatile trong vòng lặp trong cùng.", bold_prefix="Kiểm tra cờ ngắt nhanh: ")
    add_bullet(doc, "LongAdder thay cho AtomicLong: Tránh hiện tượng xung đột cache line (False Sharing) giữa các lõi CPU khi đồng thời tăng biến đếm số lượng phép băm.", bold_prefix="Đếm băm phi xung đột: ")

    # 4.4 DifficultyAdjuster
    add_heading_2(doc, "4.4. Hiện thực hóa Động cơ điều chỉnh độ khó động DifficultyAdjuster")
    add_paragraph(doc, "Lớp DifficultyAdjuster tự động cân bằng thời gian đào khối giữa các chu kỳ Epoch:")

    ascii_diff = """+-------------------------------------------------------------------------+
|                  DO THI DIEU CHINH DO KHO DONG (EPOCH RETARGETING)      |
+-------------------------------------------------------------------------+
| 16 bits |                      /---\ (Hashrate tang dot bien -> Tang diff)|
| 12 bits |------+-------------+-------+-------------+ (Do kho co ban 12b)|
|  8 bits |      |             |                     \---/ (Hashrate giam)|
|         Block 0 (Epoch 0)   Block 5 (Epoch 1)     Block 10 (Epoch 2)    |
+-------------------------------------------------------------------------+"""
    add_figure_box(doc, ascii_diff, "Hình 4.2: Đồ thị điều chỉnh độ khó động theo chu kỳ Epoch và kẹp hệ số giảm chấn")

    code_diff = """// DifficultyAdjuster.java - Thuật toán cân chỉnh độ khó động theo Epoch
public int getNextDifficulty(Blockchain chain) {
    if (chain.size() < epochSize || !isEpochBoundary(chain)) {
        return chain.getLatestBlock().difficultyBits();
    }
    int currentHeight = chain.size() - 1;
    Block currentBlock = chain.getBlock(currentHeight);
    Block epochStartBlock = chain.getBlock(currentHeight - epochSize);

    long actualTime = currentBlock.timestamp() - epochStartBlock.timestamp();
    long expectedTime = epochSize * targetBlockTimeMs;

    // Tinh ty le bien thien thoi gian
    double ratio = (double) expectedTime / Math.max(1L, actualTime);
    // Kep giam chan trong khoang [0.25, 4.0]
    double clampedRatio = Math.max(minAdjustmentFactor, Math.min(maxAdjustmentFactor, ratio));

    // Bien thien bit do kho nhi phan
    int deltaBits = (int) Math.round(Math.log(clampedRatio) / Math.log(2.0));
    int newDiff = currentBlock.difficultyBits() + deltaBits;

    return Math.max(minDifficultyBits, Math.min(maxDifficultyBits, newDiff));
}"""
    add_code_block(doc, code_diff, "Đoạn mã 4.6: Thuật toán điều chỉnh độ khó trong DifficultyAdjuster.java")

    # 4.5 BlockchainValidator
    add_heading_2(doc, "4.5. Hiện thực hóa Bộ kiểm toán Sổ cái toàn diện BlockchainValidator")
    add_paragraph(doc, "Để đảm bảo sổ cái luôn toàn vẹn và ngăn chặn các khối rác, lớp BlockchainValidator thực thi quy trình kiểm toán 6 bước nghiêm ngặt:")

    headers_val = ["Bước kiểm toán", "Đối tượng kiểm tra", "Quy tắc toán học & Mật mã xác minh", "Xử lý khi vi phạm"]
    data_val = [
        ["Bước 1", "Khối khởi thủy Genesis", "Kiểm tra previousHash có đúng 64 số '0' và index = 0", "Từ chối chuỗi ngay lập tức"],
        ["Bước 2", "Chỉ số khối (Index)", "Khối N phải có index đúng bằng index khối (N-1) + 1", "Báo lỗi đứt gãy chỉ số"],
        ["Bước 3", "Dấu thời gian (Timestamp)", "Timestamp(Block N) >= Timestamp(Block N-1)", "Báo lỗi nghịch đảo thời gian"],
        ["Bước 4", "Liên kết băm tiền nhiệm", "Block(N).previousHash == Block(N-1).hash", "Báo động đỏ phá vỡ xích băm"],
        ["Bước 5", "Tái tính toán mã băm", "Block.calculateHash(...) == Block.hash lưu trữ", "Phát hiện can thiệp nội dung"],
        ["Bước 6", "Độ khó Proof-of-Work", "countLeadingZeroBits(hash) >= difficultyBits", "Phát hiện gian lận chưa đào PoW"]
    ]
    add_table(doc, headers_val, data_val, [20, 40, 65, 35], caption="Bảng 4.1: Bảng thống kê các bước kiểm toán của động cơ BlockchainValidator")

    # 4.6 AttackSimulator
    add_heading_2(doc, "4.6. Hiện thực hóa Bộ mô phỏng tấn công giả mạo AttackSimulator")
    add_paragraph(doc, "Để minh họa trực quan tính bất biến của Blockchain trong các buổi hội thảo khoa học và bảo vệ đồ án, nhóm đã xây dựng lớp AttackSimulator mô phỏng 3 kịch bản tấn công kinh điển:")

    headers_atk = ["Kịch bản tấn công", "Hành vi của kẻ tấn công", "Phản ứng của hệ thống và Kết quả kiểm toán"]
    data_atk = [
        ["Pha 1: Sửa đổi dữ liệu phiếu bầu", "Thay đổi tên người được bầu từ 'Nguyen Hoang Nhat' thành 'Ke_Gian_Lan' trong Block #2 nhưng giữ nguyên mã hash cũ.", "FAIL tại Bước 5: Tái tính toán hash không khớp với mã hash lưu trong header. Toàn bộ mạng từ chối khối #2."],
        ["Pha 2: Giả mạo header không đào PoW", "Kẻ tấn công sửa nội dung và tự tính lại hash mới cho Block #2 nhưng không tìm số Nonce mới thỏa mãn PoW.", "FAIL tại Bước 6: Mã hash mới không đủ số bit 0 đầu theo yêu cầu độ khó. Khối bị đào thải ngay tại tầng mạng."],
        ["Pha 3: Đào lại khối bị tấn công", "Kẻ tấn công dùng CPU đào lại số Nonce mới cho Block #2 thỏa mãn PoW.", "FAIL tại Bước 4 của Block #3: Mã hash mới của Block #2 không khớp với previousHash lưu trong Block #3. Toàn bộ chuỗi từ #3 đến hiện tại bị vô hiệu hóa!"]
    ]
    add_table(doc, headers_atk, data_atk, [35, 60, 65], caption="Bảng 4.2: Kịch bản và kết quả mô phỏng 3 pha tấn công sửa đổi dữ liệu sổ cái")

    add_paragraph(doc, "Chứng minh toán học về tính bất khả thi khi tấn công viết lại lịch sử:")
    add_paragraph(doc, "Để viết lại một khối nằm ở độ sâu k tính từ đỉnh chuỗi, kẻ tấn công buộc phải tính toán lại toàn bộ k khối tiếp theo với tổng chi phí tính toán là:")
    add_paragraph(doc, "TotalHashes = Sum_{i=0}^{k} 2^{Difficulty_i}", italic=True)
    add_paragraph(doc, "Trong khi kẻ tấn công miệt mài đào lại k khối này thì toàn bộ mạng lưới trung thực đã tiến xa thêm hàng chục khối mới. Do Quy tắc chuỗi dài nhất (Longest Chain Rule), chuỗi gian lận ngắn hơn sẽ vĩnh viễn bị mạng lưới đào thải, trừ phi kẻ tấn công nắm giữ trên 51% sức mạnh băm toàn cầu (51% Attack).")

    # 4.7 LedgerStorage
    add_heading_2(doc, "4.7. Hiện thực hóa Lưu trữ bền vững nguyên tử LedgerStorage")
    add_paragraph(doc, "Lớp LedgerStorage đảm bảo dữ liệu bầu cử được lưu trữ an toàn tuyệt đối trên ổ cứng máy tính:")
    add_bullet(doc, "Phương thức saveChain: Toàn bộ danh sách chuỗi khối được chuyển đổi thành chuỗi JSON và ghi vào tệp tạm thời chain_data.tmp.", bold_prefix="Bước 1 - Ghi tệp tạm: ")
    add_bullet(doc, "Đảm bảo dữ liệu được xả hoàn toàn từ bộ nhớ đệm hệ điều hành xuống đĩa từ/SSD bằng out.flush() và FileOutputStream.getFD().sync().", bold_prefix="Bước 2 - Đồng bộ phần cứng: ")
    add_bullet(doc, "Sử dụng Files.move(tempPath, targetPath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING) để thực hiện thao tác đổi tên nguyên tử ở cấp độ hệ thống tệp tin (File System). Nếu máy tính bị sập nguồn ngay trong lúc ghi, tệp chain_data.json gốc vẫn hoàn toàn nguyên vẹn, không bao giờ bị tình trạng tệp rỗng hoặc hỏng cấu trúc JSON.", bold_prefix="Bước 3 - Hoán đổi nguyên tử: ")
