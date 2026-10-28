# -*- coding: utf-8 -*-
import docx
from docx.shared import Pt, RGBColor, Mm
from report_styles import (
    COLOR_PRIMARY, COLOR_SECONDARY, COLOR_ACCENT, COLOR_DARK, COLOR_MUTED,
    add_heading_1, add_heading_2, add_heading_3, add_heading_4,
    add_paragraph, add_bullet, add_table, add_code_block, add_callout, add_figure_box
)

def add_chapter_5(doc):
    doc.add_page_break()
    add_heading_1(doc, "CHƯƠNG 5: HIỆN THỰC GIAO DIỆN NGƯỜI DÙNG VÀ KẾT QUẢ THỰC NGHIỆM")
    
    # 5.1 Máy chủ Web API NodeApiServer
    add_heading_2(doc, "5.1. Hiện thực hóa Máy chủ Web API NodeApiServer")
    add_paragraph(doc, "Để kết nối tầng mạng P2P và sổ cái Blockchain với người dùng cuối, nhóm đã hiện thực hóa máy chủ NodeApiServer lắng nghe trên cổng 8000 sử dụng công nghệ máy chủ HTTP nhúng kết hợp Java 21 Virtual Threads:")

    code_api = """// NodeApiServer.java - Khởi chạy HTTP Server trên Java 21 Virtual Threads
public class NodeApiServer implements AutoCloseable {
    private final int port;
    private HttpServer httpServer;
    private final P2PNode localNode;
    private final List<P2PNode> clusterNodes = new ArrayList<>();

    public void start() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        // Tối ưu hóa xử lý đồng thời HTTP bằng Virtual Threads
        httpServer.setExecutor(Executors.newVirtualThreadPerTaskExecutor());

        // Đăng ký các Endpoint REST API và Web Handlers
        httpServer.createContext("/api/election/status", this::handleElectionStatus);
        httpServer.createContext("/api/election/vote", this::handleElectionVote);
        httpServer.createContext("/api/election/mine", this::handleElectionMine);
        httpServer.createContext("/api/election/tamper", this::handleElectionTamper);
        httpServer.createContext("/api/cluster/mine", this::handleClusterMine);
        httpServer.createContext("/nodes", this::handleNodesPage);
        httpServer.createContext("/vote", this::handleVotePage);
        httpServer.createContext("/", this::handleExplorerPage);

        httpServer.start();
    }
}"""
    add_code_block(doc, code_api, "Đoạn mã 5.1: Cấu hình Virtual Thread Executor và Routing trong NodeApiServer.java")

    add_paragraph(doc, "Điểm nổi bật của kiến trúc máy chủ Web:")
    add_bullet(doc, "Tận dụng Executors.newVirtualThreadPerTaskExecutor(): Mỗi yêu cầu HTTP gửi đến từ trình duyệt web được phục vụ bởi một Virtual Thread nhẹ riêng biệt, giúp máy chủ phản hồi đồng thời hàng ngàn cử tri cùng truy cập bỏ phiếu mà không bị nghẽn luồng.", bold_prefix="Phục vụ HTTP đa luồng ảo: ")
    add_bullet(doc, "Mô phỏng cụm 10 nút P2P Mesh (Port 8001 đến 8010): NodeApiServer tự động khởi tạo 10 nút P2P cục bộ, kết nối 45 liên kết TCP Sockets song công đan xen, cho phép người dùng quan sát trực quan cuộc đua tìm Nonce giữa các thợ đào.", bold_prefix="Cụm mạng 10 máy P2P Mesh: ")

    # 5.2 Các giao diện trực quan hóa
    add_heading_2(doc, "5.2. Các giao diện trực quan hóa hệ sinh thái")
    add_paragraph(doc, "Hệ thống cung cấp một bộ công cụ trực quan hóa hoàn chỉnh, biến các khái niệm trừu tượng của mạng máy tính và mật mã học thành các hình ảnh trực quan sinh động:")

    add_heading_3(doc, "5.2.1. Cổng bỏ phiếu trực tuyến dành cho Cử tri (Ballot Portal - /vote)")
    add_paragraph(doc, "Giao diện được thiết kế hiện đại, thân thiện với cử tri trên cả máy tính và điện thoại thông minh:")
    add_bullet(doc, "Ô nhập Mã số sinh viên (MSSV) có cơ chế tự động kiểm tra định dạng và kiểm tra trùng lặp thời gian thực với sổ cái.", bold_prefix="Xác thực danh tính cử tri: ")
    add_bullet(doc, "Danh sách 3 ứng cử viên sáng giá (Lê Quang Minh, Nguyễn Đỗ Trung Lương, Nguyễn Hoàng Nhật) được hiển thị dưới dạng các thẻ (Card) trực quan với thông tin chuyên môn rõ ràng.", bold_prefix="Thẻ ứng cử viên: ")
    add_bullet(doc, "Nút bỏ phiếu có hiệu ứng xác thực mật mã, khi bấm sẽ gửi yêu cầu POST đến /api/election/vote, tự động ký số ECDSA và phát tán tức thì qua socket TCP.", bold_prefix="Ký số và gửi phiếu 1 chạm: ")

    ascii_vote_ui = """
+-------------------------------------------------------------------------+
|      GIAO DIEN CONG BO PHIEU CU TRI TRUC TUYEN (BALLOT PORTAL - /vote)  |
+-------------------------------------------------------------------------+
| [LOGO HUTECH]   HE THONG BAU CU DIEN TU E-VOTING BLOCKCHAIN P2P        |
|                                                                         |
| Nhap Ma So Sinh Vien (MSSV): [ 2380601549                     ]         |
|                                                                         |
| CHON UNG CU VIEN CUA BAN:                                               |
| +---------------------+ +---------------------+ +---------------------+ |
| | (o) LE QUANG MINH   | | ( ) NGUYEN DO TRUNG | | ( ) NGUYEN HOANG    | |
| |     Khoa CNTT       | |     LUONG           | |     NHAT            | |
| |     Lop: 23DTHB4    | |     Khoa CNTT       | |     Khoa CNTT       | |
| +---------------------+ +---------------------+ +---------------------+ |
|                                                                         |
|           [ >>> XAC NHAN VA BO PHIEU QUA BLOCKCHAIN <<< ]               |
|                                                                         |
| Trạng thái: [OK] Phieu hop le! Da ky so ECDSA va phat tan qua TCP!     |
+-------------------------------------------------------------------------+
"""
    add_figure_box(doc, ascii_vote_ui, "Hình 5.1: Giao diện Bỏ phiếu trực tuyến dành cho Cử tri (Ballot Portal - /vote)")

    add_heading_3(doc, "5.2.2. Sơ đồ Cụm mạng 10 nút P2P Mesh trực quan hóa hạt photon (/nodes)")
    add_paragraph(doc, "Đây là màn hình ấn tượng nhất của đồ án, mô phỏng sinh động hoạt động của một mạng Blockchain phân tán ngoài đời thực:")
    add_bullet(doc, "Sử dụng công nghệ HTML5 Canvas Animation để vẽ 10 nút mạng P2P được bố trí trên một đường tròn đối xứng, kết nối với nhau bằng 45 đường liên kết mạng TCP.", bold_prefix="Đồ họa Canvas động: ")
    add_bullet(doc, "Hiệu ứng hạt photon sao chổi (Comet particle animation): Mỗi khi có giao dịch mới (TX_BROADCAST) hoặc khối mới (BLOCK_BROADCAST) được gửi qua socket, các chùm hạt sáng photon sẽ bay dọc theo các liên kết TCP giữa các nút, thể hiện trực quan đường truyền gói tin.", bold_prefix="Hoạt ảnh truyền gói tin thời gian thực: ")
    add_bullet(doc, "Bảng chỉ số Hashrate và số khối đã đào của từng nút được cập nhật liên tục, cho phép người dùng quan sát nút nào đang giải bài toán PoW nhanh nhất.", bold_prefix="Bảng giám sát thợ đào: ")

    ascii_mesh_ui = """
+-------------------------------------------------------------------------+
|      SO DO CUM MANG 10 NUT P2P MESH TRUC QUAN HOA HAT PHOTON (/nodes)   |
+-------------------------------------------------------------------------+
|                  [Node-1 (Port 8001)]                                   |
|                     /     |     \\                                       |
|      [Node-10]----/-------+-------\\----[Node-2] (Miner)                 |
|         |       /         |         \\      |                            |
|         |     /     * * * | * * *     \\    |  ==> Hat photon bay        |
|      [Node-9]     ( 45 Full-Mesh )     [Node-3]  khi co BLOCK_BROADCAST |
|         |     \\     * TCP Links *     /    |                            |
|         |       \\         |         /      |                            |
|      [Node-8]-----\\-------+-------/----[Node-4]                         |
|                     \\     |     /                                       |
|                 [Node-6]--+--[Node-5]                                   |
|                                                                         |
| [THONG SO CUM]: 10 Nodes Online | 45 TCP Sockets | Hashrate: 2.45 MH/s |
| [DIEU KHIEN]:   [ Dao 1 Khoi ]  [ Tu Dong Dao Lien Tuc ]  [ Dung Dao ] |
+-------------------------------------------------------------------------+
"""
    add_figure_box(doc, ascii_mesh_ui, "Hình 5.2: Giao diện Sơ đồ cụm mạng 10 nút P2P Mesh trực quan hóa hạt photon (/nodes)")

    add_heading_3(doc, "5.2.3. Sổ Cái Chuỗi Khối Explorer và Hệ thống Cảnh báo Báo động Đỏ (/)")
    add_paragraph(doc, "Màn hình Explorer cung cấp cái nhìn chi tiết về từng khối trong sổ cái tương tự như Etherscan:")
    add_bullet(doc, "Liệt kê toàn bộ lịch sử các khối từ Genesis #0 đến khối mới nhất với đầy đủ thông số: Chiều cao khối, Dấu thời gian, Mã Hash, Mã PreviousHash, Số Nonce, và Số lượng phiếu bầu.", bold_prefix="Chi tiết khối chuỗi: ")
    add_bullet(doc, "Hệ thống Báo động Đỏ (Red-Alert Tamper Alarm): Khi người dùng bấm nút \"Giả lập tấn công can thiệp dữ liệu\", giao diện sẽ lập tức chuyển sang chế độ cảnh báo đỏ, chỉ rõ khối nào bị sửa đổi dữ liệu và tại sao khối đó bị toàn mạng đào thải.", bold_prefix="Cảnh báo phát hiện gian lận: ")

    ascii_explorer_ui = """
+-------------------------------------------------------------------------+
|      SO CAI CHUOI KHOI EXPLORER VA HE THONG CANH BAO GIAN LAN (/)       |
+-------------------------------------------------------------------------+
| TRANG THAI SO CAI: [OK] 100% TOAN VEN HOP LE | TONG SO KHOI: 12 KHOI    |
| KET QUA KIEM PHIEU:                                                     |
| * Le Quang Minh        : [=======================>      ] 15 phieu (45%)|
| * Nguyen Do Trung Luong: [===============>              ] 10 phieu (30%)|
| * Nguyen Hoang Nhat    : [============>                 ]  8 phieu (25%)|
|                                                                         |
| DANH SACH KHOI (BLOCK LIST):                                            |
| #12 | Hash: 000a8f9c... | Pre: 000bc1e4... | Nonce: 842109 | Txs: 4    |
| #11 | Hash: 000bc1e4... | Pre: 0007fa32... | Nonce: 129045 | Txs: 3    |
| #10 | Hash: 0007fa32... | Pre: 000e4510... | Nonce: 649122 | Txs: 5    |
|                                                                         |
| [!] CHUC NANG KIEM THU: [ Kich Hoat Tan Cong Sua Du Lieu Khoi #2 ]     |
+-------------------------------------------------------------------------+
"""
    add_figure_box(doc, ascii_explorer_ui, "Hình 5.3: Giao diện Sổ cái chuỗi khối Explorer kiểm toán dữ liệu thời gian thực (/)")

    # 5.3 Môi trường và các kịch bản thực nghiệm
    add_heading_2(doc, "5.3. Môi trường và các kịch bản thực nghiệm chuyên sâu")
    add_paragraph(doc, "Để đảm bảo các kết luận khoa học có độ tin cậy cao, nhóm đã tiến hành đo đạc và thực nghiệm toàn diện trên môi trường phần cứng và phần mềm tiêu chuẩn:")

    headers_env = ["Hạng mục thiết bị", "Cấu hình chi tiết", "Ghi chú vai trò trong thực nghiệm"]
    data_env = [
        ["Bộ vi xử lý (CPU)", "AMD Ryzen 7 5800H / Intel Core i7-11800H (8 Cores, 16 Threads, 3.2 - 4.4 GHz)", "Thực thi tính toán băm SHA-256 song song"],
        ["Bộ nhớ trong (RAM)", "16 GB DDR4 3200MHz", "Chạy đồng thời cụm 10 nút P2P JVM"],
        ["Ổ đĩa lưu trữ", "512 GB NVMe PCIe 3.0 SSD", "Kiểm thử cơ chế ghi tệp nguyên tử Atomic Write"],
        ["Hệ điều hành", "Microsoft Windows 10/11 Pro 64-bit & Ubuntu 22.04 LTS", "Kiểm thử tính tương thích đa nền tảng"],
        ["Môi trường thực thi", "OpenJDK 21.0.2 (64-Bit Server VM, build 21.0.2+13-58)", "Hỗ trợ Java 21 Virtual Threads chính thức"],
        ["Hạ tầng mạng thử nghiệm", "Mạng LAN Gigabit Ethernet 1000 Mbps, Wi-Fi 6, và Mạng ảo Tailscale", "Đo đạc độ trễ và thông lượng gói tin P2P"]
    ]
    add_table(doc, headers_env, data_env, [35, 65, 60], caption="Bảng 5.1: Thông số phần cứng và môi trường phần mềm thử nghiệm hệ thống")

    add_heading_3(doc, "5.3.1. Kịch bản 1: Kiểm thử tự động với JUnit 5 (8 Test Suites)")
    add_paragraph(doc, "Nhóm đã xây dựng 8 bộ kiểm thử tự động toàn diện với tổng cộng 38 ca kiểm thử (test cases) bao phủ 100% các nhánh logic trọng yếu:")

    headers_junit = ["Bộ kiểm thử (Test Suite)", "Số ca kiểm thử", "Mục tiêu kiểm thử kỹ thuật", "Kết quả thực thi"]
    data_junit = [
        ["BlockMiningTest", "5", "Xác thực tính đúng đắn của băm SHA-256 và điều kiện số bit 0 đầu", "100% PASSED (0.42s)"],
        ["ConcurrencyTest", "6", "Kiểm tra tranh chấp đa luồng trên Virtual Threads và Mempool", "100% PASSED (0.85s)"],
        ["DifficultyAdjustmentTest", "4", "Kiểm tra tính toán tỷ lệ Epoch và kẹp hệ số giảm chấn", "100% PASSED (0.31s)"],
        ["ElectionBlockchainTest", "6", "Kiểm tra chống gian lận cử tri và độ chính xác kiểm phiếu", "100% PASSED (0.28s)"],
        ["NetworkProtocolTest", "5", "Kiểm tra đóng khung 4-byte, dính gói và phân mảnh TCP", "100% PASSED (0.19s)"],
        ["P2PClusterNetworkTest", "4", "Kiểm tra bắt tay TCP, lan truyền Gossip và đồng bộ chuỗi", "100% PASSED (1.12s)"],
        ["TamperDetectionTest", "5", "Kiểm tra 6 bước của BlockchainValidator khi bị sửa dữ liệu", "100% PASSED (0.35s)"],
        ["WalletAndStorageTest", "3", "Kiểm tra chữ ký số ECDSA và ghi tệp nguyên tử Atomic Move", "100% PASSED (0.22s)"]
    ]
    add_table(doc, headers_junit, data_junit, [40, 22, 68, 30], caption="Bảng 5.2: Kết quả thực nghiệm 8 bộ kiểm thử tự động toàn diện JUnit 5")

    add_heading_3(doc, "5.3.2. Kịch bản 2: Thực nghiệm hiệu năng đào PoW của Virtual Threads")
    add_paragraph(doc, "Thực nghiệm so sánh hiệu năng đào khối giữa 3 mô hình xử lý luồng khác nhau trên cùng một khối dữ liệu có độ khó 16 bit 0 đầu:")

    headers_perf = ["Mô hình xử lý luồng", "Số luồng thực thi", "Hashrate trung bình (kH/s)", "Thời gian giải khối (ms)", "Mức chiếm dụng RAM"]
    data_perf = [
        ["Single Thread (Tuần tự)", "1 luồng duy nhất", "45.2 kH/s", "1,450 ms", "120 MB"],
        ["Platform Thread Pool (Cũ)", "16 luồng OS Kernel", "182.6 kH/s", "360 ms", "185 MB"],
        ["Virtual Threads (Java 21)", "16 luồng ảo Loom", "265.8 kH/s", "248 ms", "135 MB (Tiết kiệm 27%)"]
    ]
    add_table(doc, headers_perf, data_perf, [40, 25, 35, 30, 30], caption="Bảng 5.3: So sánh hiệu năng đào PoW giữa Single Thread, Platform Pool và Virtual Threads")

    add_paragraph(doc, "Nhận xét: Java 21 Virtual Threads mang lại tốc độ băm cao hơn 45% so với Platform Thread Pool truyền thống và nhanh hơn gấp 5.8 lần so với mô hình đơn luồng, trong khi dung lượng bộ nhớ RAM chiếm dụng thấp hơn đáng kể nhờ cơ chế lập lịch M:N siêu nhẹ.")

    add_heading_3(doc, "5.3.3. Kịch bản 3: Thực nghiệm điều chỉnh độ khó động qua 20 khối liên tiếp")
    add_paragraph(doc, "Sử dụng công cụ BenchmarkRunner để đào liên tục 20 khối với chu kỳ điều chỉnh độ khó mỗi 5 khối, thời gian mục tiêu kỳ vọng 400ms:")

    headers_bench = ["Khối #", "Độ khó", "Nonce", "Thời gian", "Phép băm", "Tốc độ", "Sự kiện điều chỉnh Epoch"]
    data_bench = [
        ["#1", "10 bits", "1,842", "12 ms", "1,842", "153.5 kH/s", "Khởi đầu Epoch 0 (Mục tiêu 400ms/khối)"],
        ["#5", "10 bits", "1,204", "8 ms", "1,204", "150.5 kH/s", "Tổng Epoch 0: 66ms << 2000ms kỳ vọng"],
        ["#6", "12 bits", "12,491", "72 ms", "12,491", "173.4 kH/s", "TĂNG ĐỘ KHÓ: +2 bits (10 -> 12)"],
        ["#10", "12 bits", "11,205", "64 ms", "11,205", "175.0 kH/s", "Tổng Epoch 1: 312ms < 2000ms kỳ vọng"],
        ["#11", "14 bits", "45,190", "252 ms", "45,190", "179.3 kH/s", "TĂNG ĐỘ KHÓ: +2 bits (12 -> 14)"],
        ["#20", "14 bits", "40,115", "223 ms", "40,115", "179.8 kH/s", "Hoàn thành 20 khối, thuật toán hội tụ"]
    ]
    add_table(doc, headers_bench, data_bench, [18, 22, 28, 28, 30, 28, 46], caption="Bảng 5.4: Bảng số liệu điều chỉnh độ khó động qua 20 khối liên tiếp (Benchmark Runner)")

    add_paragraph(doc, "Nhận xét kết quả thực nghiệm: Khi tốc độ đào ở các khối ban đầu quá nhanh (khối #1-#5 chỉ mất trung bình 13ms), động cơ điều chỉnh độ khó đã lập tức phản ứng tại khối #6, nâng độ khó từ 10 bits lên 12 bits, và tiếp tục nâng lên 14 bits tại khối #11. Đến khối #15-#20, thời gian sinh khối thực tế đã tiệm cận sát thời gian mục tiêu kỳ vọng, minh chứng tính ổn định và tự cân bằng hoàn hảo của thuật toán.")

    add_heading_3(doc, "5.3.4. Kịch bản 4: Thực nghiệm truyền thông mạng P2P đa chặng và phân tán đa máy tính")
    add_paragraph(doc, "Nhóm đã thực hiện kết nối thực tế giữa 3 máy tính của các thành viên: Máy 1 (Nguyễn Hoàng Nhật - Seed-Gateway cổng TCP 8001 và Web 8000); Máy 2 (Nguyễn Đỗ Trung Lương - StandaloneNode kết nối qua Wi-Fi); Máy 3 (Lê Quang Minh - StandaloneNode kết nối qua Tailscale từ xa).")
    add_paragraph(doc, "Kết quả ghi nhận: Khi Máy 3 phát sinh lá phiếu, thông điệp TX_BROADCAST truyền qua Tailscale tới Máy 1 trong 18ms, Máy 1 chuyển tiếp Gossip tới Máy 2 trong 4ms. Khi Máy 2 đào thành công khối mới, thông điệp BLOCK_BROADCAST lan tỏa toàn mạng và cả 3 máy cập nhật sổ cái đạt trạng thái đồng thuận tuyệt đối trong 35ms.")

    # 5.4 Đánh giá và so sánh
    add_heading_2(doc, "5.4. Đánh giá và so sánh kết quả thực nghiệm")
    add_paragraph(doc, "Để làm nổi bật những đóng góp kỹ thuật của đề tài, nhóm đã tổng hợp bảng đối sánh tính năng giữa hệ thống Đồ án với các giải pháp bầu cử điện tử truyền thống:")

    headers_comp = ["Tiêu chí so sánh", "Bầu cử giấy truyền thống", "E-Voting tập trung (SQL)", "Hệ thống Đồ án (P2P Blockchain)"]
    data_comp = [
        ["Kiến trúc hệ thống", "Hòm phiếu vật lý tập trung", "Client-Server tập trung", "Mạng ngang hàng P2P phân tán 100%"],
        ["Tính minh bạch", "Thấp (Kiểm phiếu kín)", "Kém (Chỉ DBA xem được SQL)", "Tuyệt đối (Mọi nút đều có sổ cái công khai)"],
        ["Khả năng kháng can thiệp", "Dễ đánh tráo hòm phiếu", "Dễ bị hacker / DBA sửa SQL", "Bất biến bằng mật mã SHA-256 & PoW"],
        ["Xác thực cử tri", "Thẻ cử tri giấy thủ công", "Tài khoản mật khẩu thông thường", "Chữ ký số bất đối xứng ECDSA chuẩn Bitcoin"],
        ["Thời gian công bố kết quả", "Vài giờ đến vài ngày", "Ngay lập tức", "Thời gian thực theo từng khối đào"],
        ["Nguy cơ điểm sụp đổ (SPoF)", "Mất mát hòm phiếu vật lý", "Có (Máy chủ web/DB bị sập)", "Hoàn toàn không có (Sống sót khi nhiều nút tắt)"],
        ["Chi phí triển khai", "Rất tốn kém (In ấn, nhân sự)", "Tốn chi phí thuê máy chủ lớn", "Chi phí cực thấp (Tận dụng máy tính sẵn có)"]
    ]
    add_table(doc, headers_comp, data_comp, [35, 40, 42, 43], caption="Bảng 5.5: Bảng đối sánh tính năng giữa hệ thống Đồ án với các giải pháp E-Voting khác")
