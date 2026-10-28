# -*- coding: utf-8 -*-
import docx
from docx.shared import Pt, RGBColor, Mm
from report_styles import (
    COLOR_PRIMARY, COLOR_SECONDARY, COLOR_ACCENT, COLOR_DARK, COLOR_MUTED,
    add_heading_1, add_heading_2, add_heading_3, add_heading_4,
    add_paragraph, add_bullet, add_table, add_code_block, add_callout, add_figure_box
)

def add_chapter_2(doc):
    doc.add_page_break()
    add_heading_1(doc, "CHƯƠNG 2: PHÂN TÍCH YÊU CẦU VÀ THIẾT KẾ KIẾN TRÚC HỆ THỐNG")
    
    # 2.1 Yêu cầu chức năng
    add_heading_2(doc, "2.1. Phân tích yêu cầu chức năng (Functional Requirements)")
    add_paragraph(doc, "Xuất phát từ bài toán thực tế về việc tổ chức các cuộc bầu cử đại biểu sinh viên hoặc bình chọn ban cán sự tại các trường đại học, hệ thống cần đáp ứng đầy đủ các yêu cầu chức năng nghiệp vụ và kỹ thuật mạng phân tán sau đây:")
    
    add_bullet(doc, "Hệ thống phải cho phép cử tri nhập mã số sinh viên (MSSV) và chọn ứng cử viên từ danh sách chỉ định sẵn. Mỗi cử tri chỉ được bỏ phiếu duy nhất một lần. Hệ thống phải kiểm tra tính hợp lệ tức thì: nếu MSSV đã từng xuất hiện trong bất kỳ khối nào thuộc sổ cái (Ledger) hoặc đang nằm chờ trong bộ nhớ đệm (Mempool), yêu cầu bỏ phiếu mới sẽ bị từ chối ngay lập tức với thông báo lỗi cụ thể.", bold_prefix="FR1 - Quản lý cử tri và quy tắc \"Một người - Một phiếu\": ")
    add_bullet(doc, "Khi một lá phiếu hợp lệ được tạo ra, nút mạng cục bộ phải lập tức đóng gói thành giao dịch (Transaction/Vote), gán chữ ký số ECDSA và phát tán (broadcast) thông điệp TX_BROADCAST qua socket TCP tới tất cả các nút ngang hàng đang kết nối theo giao thức Gossip.", bold_prefix="FR2 - Khởi tạo và phát tán phiếu bầu qua P2P: ")
    add_bullet(doc, "Mỗi nút P2P duy trì một hòm phiếu đệm (Mempool) lưu trữ các giao dịch chưa được xác nhận. Hòm phiếu đệm phải đảm bảo tính an toàn đồng thời (Thread-safe) để nhiều luồng mạng có thể thêm, lấy và xóa giao dịch mà không xảy ra tranh chấp (Race Condition).", bold_prefix="FR3 - Hòm phiếu đệm giao dịch (Mempool): ")
    add_bullet(doc, "Các nút đóng vai trò thợ đào (Miner) phải có khả năng gom các lá phiếu trong Mempool, tạo khuôn mẫu khối (Block Template) và kích hoạt công cụ đào đa luồng trên nền tảng Java 21 Virtual Threads để giải câu đố Proof-of-Work với độ khó chỉ định.", bold_prefix="FR4 - Khai thác khối đồng thuận Proof-of-Work: ")
    add_bullet(doc, "Khi một thợ đào tìm ra số Nonce hợp lệ, khối mới phải lập tức được đóng gói vào thông điệp BLOCK_BROADCAST và phát tán qua mạng P2P. Các nút nhận khối phải thực hiện kiểm toán tính toàn vẹn (liên kết previousHash, cấu trúc Merkle Tree, độ khó băm). Nếu khối hợp lệ, nút cập nhật vào sổ cái cục bộ và xóa các giao dịch tương ứng khỏi Mempool.", bold_prefix="FR5 - Lan truyền khối và đồng thuận toàn mạng: ")
    add_bullet(doc, "Khi một nút mới tham gia mạng hoặc bị mất kết nối một thời gian và kết nối lại, nút phải tự động gửi thông điệp SYNC_REQUEST tới các nút láng giềng để yêu cầu đồng bộ sổ cái. Áp dụng Quy tắc chuỗi dài nhất (Longest Chain Rule) để giải quyết các trường hợp rẽ nhánh.", bold_prefix="FR6 - Bắt tay và đồng bộ chuỗi dài nhất (Nakamoto Sync): ")
    add_bullet(doc, "Cung cấp động cơ kiểm toán độc lập (BlockchainValidator) cho phép quét toàn bộ chuỗi khối để phát hiện bất kỳ sự can thiệp trái phép nào; cung cấp công cụ giả lập tấn công (AttackSimulator) để mô phỏng hành vi sửa đổi dữ liệu khối và chứng minh tính bất biến.", bold_prefix="FR7 - Kiểm toán sổ cái và giả lập tấn công: ")
    add_bullet(doc, "Cung cấp hệ thống Dashboard web trực quan: Cổng bỏ phiếu cử tri (/vote), Sơ đồ cụm 10 nút P2P Mesh với hoạt ảnh hạt photon truyền tin thời gian thực (/nodes), và Sổ cái Explorer kiểm tra chi tiết các khối (/), kết nối qua máy chủ REST API cổng 8000.", bold_prefix="FR8 - Giao diện trực quan hóa hệ sinh thái: ")

    # 2.2 Yêu cầu phi chức năng
    add_heading_2(doc, "2.2. Phân tích yêu cầu phi chức năng (Non-Functional Requirements)")
    add_paragraph(doc, "Bên cạnh các tính năng nghiệp vụ, tính khả thi của một hệ thống mạng phân tán phụ thuộc chặt chẽ vào các chỉ số phi chức năng:")
    
    add_bullet(doc, "Tốc độ băm khối song song trên CPU sử dụng Java 21 Virtual Threads phải đạt trên 200,000 hashes/giây (200 kH/s) trên máy tính thông thường; độ trễ truyền gói tin qua TCP Socket giữa các nút trong mạng cục bộ LAN phải dưới 10ms; thời gian phản hồi API REST dưới 50ms.", bold_prefix="NFR1 - Hiệu năng cao (High Performance): ")
    add_bullet(doc, "Hệ thống mạng P2P phải có khả năng tự phục hồi khi có nút bị tắt đột ngột (Crash Fault Tolerance). Các kết nối TCP bị đứt gãy phải được dọn dẹp sạch sẽ tài nguyên, không gây tràn bộ nhớ (Memory Leak) hoặc khóa chết luồng (Deadlock).", bold_prefix="NFR2 - Độ tin cậy và khả năng chịu lỗi (Fault Tolerance): ")
    add_bullet(doc, "Dữ liệu phiếu bầu được mã hóa bằng hàm băm SHA-256 tiêu chuẩn mật mã học của NIST; chữ ký số ECDSA đảm bảo tính toàn vẹn và chống chối bỏ; hệ thống có năng lực kháng cự tấn công Sybil và tấn công phát tán lặp lại (Replay Attack).", bold_prefix="NFR3 - An toàn thông tin và bảo mật (Security): ")
    add_bullet(doc, "Hệ thống hỗ trợ chạy phân tán trên nhiều máy tính vật lý khác nhau thông qua mạng LAN, Wi-Fi hoặc mạng ảo diện rộng Tailscale, cấu hình linh hoạt thông qua file hạt giống peer_seed.txt.", bold_prefix="NFR4 - Khả năng mở rộng phân tán (Scalability): ")
    add_bullet(doc, "Dữ liệu sổ cái chuỗi khối được lưu trữ bền vững xuống đĩa cứng (Persistence) thông qua cơ chế ghi tệp nguyên tử (Atomic Write), đảm bảo không bao giờ bị hỏng tệp khi mất điện đột ngột.", bold_prefix="NFR5 - Tính toàn vẹn lưu trữ (Data Persistence): ")

    # 2.3 Thiết kế kiến trúc tổng thể
    add_heading_2(doc, "2.3. Thiết kế kiến trúc tổng thể 5 tầng của hệ thống")
    add_paragraph(doc, "Để đảm bảo tính module hóa cao độ, dễ bảo trì, mở rộng và kiểm thử độc lập, hệ thống được thiết kế theo Mô hình Kiến trúc Phân tầng (Layered Architecture) gồm 5 tầng chức năng rõ rệt:")

    ascii_arch = """+-------------------------------------------------------------------------+
|                  KIEN TRUC TONG THE 5 TANG CUA HE THONG                 |
+-------------------------------------------------------------------------+
| TANG 5: PRESENTATION (Ballot Portal, Visualizer, Explorer, REST API)   |
| TANG 4: AUDITING & SECURITY (BlockchainValidator, AttackSimulator)     |
| TANG 3: CONSENSUS & MINING (MultiThreadedMiner Loom, DifficultyAdjuster)|
| TANG 2: CORE CRYPTO & DATA (Block, MerkleTree, HashUtil, Wallet, Vote)  |
| TANG 1: DISTRIBUTED P2P NETWORKING (Framing, Virtual Threads, Gossip)  |
+-------------------------------------------------------------------------+"""
    add_figure_box(doc, ascii_arch, "Hình 2.1: Sơ đồ kiến trúc tổng thể 5 tầng của hệ thống E-Voting Blockchain P2P")

    add_paragraph(doc, "Nguyên tắc hoạt động tương tác giữa các tầng:")
    add_bullet(doc, "Cung cấp toàn bộ nền tảng hạ tầng kết nối vật lý, thiết lập kênh truyền socket TCP song công, giải quyết triệt để phân mảnh/dính gói tin, và lan truyền thông điệp theo mô hình tin đồn Gossip.", bold_prefix="Tầng 1 - Mạng P2P (Networking): ")
    add_bullet(doc, "Định nghĩa cấu trúc dữ liệu bất biến của các thực thể (Block, Vote, Transaction), hiện thực hóa các thuật toán mật mã học cốt lõi (SHA-256, Merkle Tree, ECDSA) và quản lý lưu trữ bền vững sổ cái.", bold_prefix="Tầng 2 - Lõi Dữ liệu & Mật mã (Core & Crypto): ")
    add_bullet(doc, "Thực thi thuật toán đồng thuận Bằng chứng công việc (PoW) trên nền tảng Java 21 Virtual Threads, chia dải Nonce theo các bước sải 4096, và tự động điều chỉnh độ khó động sau mỗi 5 khối.", bold_prefix="Tầng 3 - Khai thác & Đồng thuận (Consensus): ")
    add_bullet(doc, "Cung cấp động cơ kiểm toán độc lập 6 bước và công cụ giả lập tấn công can thiệp dữ liệu, đảm bảo phát hiện ngay lập tức bất kỳ sự gian lận nào trong chuỗi khối.", bold_prefix="Tầng 4 - Kiểm toán & Bảo mật (Security): ")
    add_bullet(doc, "Cung cấp các giao diện trực quan hóa thời gian thực (Cổng bỏ phiếu cử tri, Sơ đồ mạng 10 máy, Sổ cái Explorer) và hệ thống REST API tương tác hai chiều.", bold_prefix="Tầng 5 - Ứng dụng & Trực quan (Presentation): ")

    # Sơ đồ tuần tự quy trình bỏ phiếu
    ascii_seq_vote = """
+-------------------------------------------------------------------------+
|      BIEU DO TUAN TU: CỬ TRI BỎ PHIẾU ĐẾN KHI KHỐI ĐƯỢC ĐÓNG VÀ ĐỒNG BỘ |
+-------------------------------------------------------------------------+
| Voter Web        Node P2P (Miner)     P2P Network (Peers)     Ledger    |
|    |-- 1. POST /vote -->|                      |                 |      |
|    |   (MSSV, CandId)   |                      |                 |      |
|    |                    |-- 2. Check Voter --->| (Ledger+Mempool)|      |
|    |                    |-- 3. Broadcast TX -->| (Gossip Msg)    |      |
|    |                    |-- 4. Add Mempool --->|                 |      |
|    |<-- 200 OK ---------|                      |                 |      |
|    |                    |-- 5. Mine Block ---->| (PoW Virtual Th)|      |
|    |                    |-- 6. Block Mined! -->|                 |      |
|    |                    |-- 7. Broadcast Block>| (BLOCK_BROADCAST|      |
|    |                    |                      |-- 8. Verify --->|      |
|    |                    |-- 9. Append Block -->|--------+------->|      |
|    |                    |-- 10. Clear Mempool->|        v Update Ledger |
+-------------------------------------------------------------------------+
"""
    add_figure_box(doc, ascii_seq_vote, "Hình 2.2: Biểu đồ tuần tự (Sequence Diagram) quy trình cử tri bỏ phiếu đến khi đóng khối")

    # 2.4 Thiết kế chi tiết Tầng mạng
    add_heading_2(doc, "2.4. Thiết kế chi tiết Tầng mạng P2P TCP Sockets")
    add_paragraph(doc, "Tầng mạng P2P được xem là \"xương sống\" của toàn bộ đồ án, chịu trách nhiệm kết nối vật lý, điều khiển luồng và đồng bộ dữ liệu giữa các máy tính. Nhóm đã thiết kế các thành phần kiến trúc cốt lõi như sau:")

    add_heading_3(doc, "2.4.1. Thiết kế Khung thông điệp NetworkMessage")
    add_paragraph(doc, "Mọi thông tin trao đổi giữa các nút P2P trong mạng đều được đóng gói dưới dạng đối tượng NetworkMessage với các trường thông tin chuẩn hóa sau:")

    headers_msg = ["Trường dữ liệu", "Kiểu dữ liệu", "Mục đích sử dụng và ý nghĩa kỹ thuật"]
    data_msg = [
        ["messageId", "String (UUID)", "Mã định danh ngẫu nhiên duy nhất 128-bit dùng để lọc trùng lặp gói tin"],
        ["type", "MessageType (Enum)", "Loại thông điệp quy định hành vi xử lý của nút nhận"],
        ["senderNodeId", "String", "Tên định danh của nút gửi gói tin (ví dụ: 'Node-1', 'Seed-Gateway')"],
        ["timestamp", "long", "Dấu thời gian phát hành gói tin tính theo mili-giây (System.currentTimeMillis())"],
        ["payload", "String", "Phần thân nội dung thông điệp (chứa JSON giao dịch, khối, hoặc chuỗi khối)"]
    ]
    add_table(doc, headers_msg, data_msg, [30, 35, 95], caption="Bảng 2.1: Đặc tả các trường thông tin trong gói tin NetworkMessage")

    add_paragraph(doc, "Hệ thống hỗ trợ 8 loại thông điệp MessageType chuyên biệt:")
    add_bullet(doc, "Gói tin bắt tay ban đầu khi một nút kết nối tới peer láng giềng, gửi kèm thông tin về chiều cao chuỗi hiện tại của mình.", bold_prefix="HANDSHAKE: ")
    add_bullet(doc, "Gói tin phản hồi bắt tay thành công, xác nhận quan hệ kết nối song công.", bold_prefix="HANDSHAKE_ACK: ")
    add_bullet(doc, "Phát tán một giao dịch phiếu bầu mới từ cử tri qua mạng P2P Gossip.", bold_prefix="TX_BROADCAST: ")
    add_bullet(doc, "Phát tán một khối mới vừa được đào thành công bởi một thợ đào.", bold_prefix="BLOCK_BROADCAST: ")
    add_bullet(doc, "Yêu cầu nút đối tác gửi toàn bộ sổ cái chuỗi khối để tiến hành đồng bộ.", bold_prefix="SYNC_REQUEST: ")
    add_bullet(doc, "Chứa toàn bộ danh sách các khối trong sổ cái gửi về nút yêu cầu đồng bộ.", bold_prefix="SYNC_RESPONSE: ")
    add_bullet(doc, "Gói tin nhịp tim kiểm tra trạng thái hoạt động của kênh kết nối TCP.", bold_prefix="PING: ")
    add_bullet(doc, "Gói tin phản hồi nhịp tim xác nhận kết nối còn sống.", bold_prefix="PONG: ")

    add_heading_3(doc, "2.4.2. Thiết kế Giao thức Đóng khung Luồng tin NetworkProtocol")
    add_paragraph(doc, "Để khắc phục triệt để hiện tượng dính gói tin (Sticky Packets), lớp NetworkProtocol được thiết kế với chuẩn đóng khung phân tách rõ ràng:")
    add_bullet(doc, "4 byte đầu tiên: Ghi số nguyên int (32-bit Big-Endian) biểu thị chiều dài chính xác tính bằng byte của chuỗi UTF-8 Payload.", bold_prefix="Tiền tố độ dài (Length Header): ")
    add_bullet(doc, "N byte tiếp theo: Toàn bộ mảng byte nội dung của thông điệp được mã hóa UTF-8.", bold_prefix="Thân thông điệp (Payload): ")
    add_bullet(doc, "Hạn mức kích thước tối đa: Giới hạn chiều dài payload không vượt quá 16MB (16 * 1024 * 1024 bytes) để chống tấn công cạn kiệt bộ nhớ (OOM Attack).", bold_prefix="Cơ chế phòng vệ: ")

    # Biểu đồ tuần tự bắt tay và đồng bộ
    ascii_seq_sync = """
+-------------------------------------------------------------------------+
|      BIEU DO TUAN TU: BẮT TAY VÀ ĐỒNG BỘ CHUỖI DÀI NHẤT (NAKAMOTO)      |
+-------------------------------------------------------------------------+
| Node-Newbie (Height 1)                 Node-Gateway (Height 5)          |
|      |-------- 1. TCP Connect (Port 8001) ------->|                     |
|      |<------- 2. Socket Accepted ----------------|                     |
|      |-------- 3. HANDSHAKE (My Height: 1) ------>|                     |
|      |<------- 4. HANDSHAKE_ACK (My Height: 5) ---|                     |
|      |-- Height 1 < 5 -> Must Sync!               |                     |
|      |-------- 5. SYNC_REQUEST ------------------>|                     |
|      |<------- 6. SYNC_RESPONSE (5 Blocks JSON)---|                     |
|      |-- 7. Audit & Validate Received Chain       |                     |
|      |-- 8. Replace Local Chain (Longest Chain)   |                     |
|      |-- Height updated to 5!                     |                     |
+-------------------------------------------------------------------------+
"""
    add_figure_box(doc, ascii_seq_sync, "Hình 2.3: Biểu đồ tuần tự quá trình bắt tay và đồng bộ chuỗi dài nhất giữa các nút P2P")

    # 2.5 Thiết kế Tầng lõi Dữ liệu & Mật mã
    add_heading_2(doc, "2.5. Thiết kế chi tiết Tầng lõi Dữ liệu và Mật mã học")
    add_paragraph(doc, "Để đảm bảo tính tương thích và mô phỏng chuẩn mực mạng Bitcoin nguyên bản của Satoshi Nakamoto, cấu trúc của một khối Block trong hệ thống được thiết kế tinh gọn gồm đúng 5 trường thuộc tính cốt lõi:")

    headers_block = ["Tên trường", "Kiểu dữ liệu", "Mô tả ý nghĩa và vai trò trong khối"]
    data_block = [
        ["hash", "String (64 hex)", "Mã băm SHA-256 duy nhất đại diện cho toàn bộ khối dữ liệu"],
        ["timestamp", "long", "Thời điểm khối được khai thác thành công (tính bằng mili-giây Unix)"],
        ["previousHash", "String (64 hex)", "Mã băm SHA-256 của khối liền trước, tạo nên chuỗi liên kết bất biến"],
        ["content", "String", "Nội dung dữ liệu của khối (danh sách các lá phiếu bầu hợp lệ)"],
        ["nonce", "long", "Số nguyên ngẫu nhiên tìm được thỏa mãn điều kiện độ khó Proof-of-Work"]
    ]
    add_table(doc, headers_block, data_block, [30, 35, 95], caption="Bảng 2.2: Đặc tả các trường thông tin trong cấu trúc khối Block 5 thuộc tính chuẩn Bitcoin")

    add_paragraph(doc, "Công thức tính mã băm đại diện của khối được chuẩn hóa theo quy tắc nối chuỗi ký tự phân tách bằng dấu hai chấm:")
    add_paragraph(doc, "Block.calculateHash(previousHash, timestamp, content, nonce) = SHA256(previousHash + \":\" + timestamp + \":\" + content + \":\" + nonce)", bold_prefix="Quy tắc tính toán băm khối: ", italic=True)

    # 2.6 Thiết kế Tầng Đồng thuận PoW
    add_heading_2(doc, "2.6. Thiết kế chi tiết Tầng Đồng thuận PoW và Điều chỉnh độ khó")
    add_paragraph(doc, "Tầng đồng thuận đảm bảo sự công bằng và an toàn dữ liệu trên toàn mạng lưới thông qua hai thành phần cốt lõi:")
    
    add_heading_3(doc, "2.6.1. Thiết kế Bộ đào PoW đa luồng MultiThreadedMiner")
    add_paragraph(doc, "Trong các hệ thống Blockchain cổ điển, thợ đào thường duyệt số nonce tuần tự từ 0 tăng dần đến Long.MAX_VALUE trên một luồng duy nhất, khiến hiệu suất CPU bị hạn chế tối đa. Để tận dụng tối đa năng lực xử lý đa nhân của vi xử lý hiện đại, MultiThreadedMiner được thiết kế với cơ chế phân chia dải Nonce theo các bước sải atomic (Stride Partitioning):")
    add_bullet(doc, "Sử dụng biến AtomicLong nextNonceChunk để cấp phát các khối nonce có kích thước 4,096 đơn vị cho các luồng đào.", bold_prefix="Cấp phát sải nonce nguyên tử (Stride 4096): ")
    add_bullet(doc, "Mỗi luồng ảo (Virtual Thread) nhận một dải nonce [chunkStart, chunkStart + 4096) và thực thi vòng lặp tính băm. Khi hết dải, luồng tiếp tục gọi nextNonceChunk.getAndAdd(4096) để nhận dải tiếp theo mà không bị xung đột với các luồng khác.", bold_prefix="Không gian tìm kiếm không giao thoa: ")
    add_bullet(doc, "Khi bất kỳ một luồng nào tìm thấy Nonce thỏa mãn điều kiện độ khó mục tiêu, nó lập tức kích hoạt cờ AtomicBoolean isSolved.compareAndSet(false, true). Toàn bộ các luồng đào khác trong hệ thống khi kiểm tra cờ này sẽ lập tức dừng vòng lặp ngay tại chu kỳ tiếp theo, tiết kiệm tối đa điện năng và chu kỳ CPU.", bold_prefix="Cơ chế ngắt tức thì toàn mạng: ")
    add_bullet(doc, "Sử dụng java.util.concurrent.atomic.LongAdder để ghi nhận tổng số lượng phép băm đã thực hiện. So với AtomicLong truyền thống, LongAdder phân tán tranh chấp ghi sang nhiều biến cục bộ nội bộ, loại bỏ hoàn toàn hiện tượng thắt cổ chai khi hàng chục luồng đồng thời ghi nhận số phép băm.", bold_prefix="Đếm phép băm phi tranh chấp (LongAdder): ")

    add_heading_3(doc, "2.6.2. Thiết kế Động cơ Điều chỉnh Độ khó Động DifficultyAdjuster")
    add_paragraph(doc, "Động cơ DifficultyAdjuster được thiết kế để duy trì nhịp độ sinh khối ổn định, tương tự như cơ chế tự cân bằng của Bitcoin sau mỗi 2016 khối. Trong hệ thống mô phỏng, chu kỳ Epoch được rút ngắn xuống mỗi 5 khối để phục vụ quan sát trực quan:")
    add_paragraph(doc, "Tỷ lệ biến thiên: Ratio = (5 * TargetTimePerBlock) / (Timestamp_current - Timestamp_epoch_start)", italic=True)
    add_paragraph(doc, "Hệ số kẹp giảm chấn: ClampedRatio = Math.max(0.25, Math.min(4.0, Ratio))", italic=True)
    add_paragraph(doc, "Độ biến thiên bit: DeltaBits = Math.round(log2(ClampedRatio))", italic=True)
    add_paragraph(doc, "Độ khó mới: NewDifficulty = Math.max(MinDiff, Math.min(MaxDiff, CurrentDiff + DeltaBits))", italic=True)

    # 2.7 Thiết kế Tầng Kiểm toán và Giả lập Tấn công
    add_heading_2(doc, "2.7. Thiết kế Tầng Kiểm toán và Giả lập Tấn công")
    add_paragraph(doc, "Một hệ thống Blockchain chuẩn doanh nghiệp không thể thiếu các công cụ kiểm toán độc lập và cơ chế cảnh báo gian lận:")
    add_bullet(doc, "Lớp BlockchainValidator thực hiện quy trình kiểm toán 6 bước nghiêm ngặt: (1) Kiểm tra khối Genesis; (2) Kiểm tra tính liên tục của chỉ số khối; (3) Kiểm tra tính tăng đơn điệu của dấu thời gian; (4) Kiểm tra tính toàn vẹn của liên kết previousHash; (5) Tính toán lại mã băm thực tế và so sánh với giá trị băm lưu trữ; (6) Kiểm tra xem mã băm có thực sự thỏa mãn số lượng bit 0 đầu theo độ khó chỉ định hay không.", bold_prefix="Quy trình kiểm toán 6 bước: ")
    add_bullet(doc, "Lớp AttackSimulator xây dựng kịch bản tấn công 3 pha nhằm chứng minh năng lực tự phòng vệ của chuỗi khối: Pha 1 - Sửa đổi nội dung phiếu bầu trong khối quá khứ; Pha 2 - Cập nhật lại băm header nhưng không giải bài toán PoW; Pha 3 - Dùng CPU đào lại khối bị tấn công và chứng minh hiệu ứng sụp đổ dây chuyền làm hỏng các khối phía sau.", bold_prefix="Kịch bản giả lập tấn công 3 pha: ")

    # 2.8 Thiết kế Tầng Lưu trữ & Web API
    add_heading_2(doc, "2.8. Thiết kế Tầng Lưu trữ bền vững và Web REST API")
    add_paragraph(doc, "Hệ thống cung cấp cơ chế lưu trữ bền vững (Persistence) và giao diện lập trình ứng dụng REST API đa nền tảng:")
    add_bullet(doc, "Lớp LedgerStorage thực hiện tuần tự hóa toàn bộ sổ cái chuỗi khối ra tệp định dạng JSON (chain_data.json). Để ngăn ngừa hiện tượng hỏng tệp khi mất điện đột ngột hoặc dừng tiến trình giữa chừng, hệ thống áp dụng cơ chế Ghi tệp nguyên tử (Atomic Write): Dữ liệu được ghi ra tệp tạm chain_data.tmp trước, sau khi ghi hoàn tất và xả bộ đệm thành công, hệ thống mới thực hiện thao tác đổi tên tệp nguyên tử (Atomic File Move) đè lên chain_data.json.", bold_prefix="Ghi tệp nguyên tử (Atomic Write): ")
    add_bullet(doc, "Lớp NodeApiServer khởi chạy máy chủ HTTP nhúng trên cổng 8000 sử dụng com.sun.net.httpserver.HttpServer được tối ưu hóa với ExecutorService của Java 21 Virtual Threads, phục vụ các giao diện web trực quan và cung cấp các endpoint REST API trao đổi dữ liệu JSON.", bold_prefix="Máy chủ HTTP REST API: ")

    headers_api = ["Phương thức HTTP", "Đường dẫn Endpoint", "Mô tả chức năng nghiệp vụ"]
    data_api = [
        ["GET", "/api/election/status", "Lấy toàn bộ trạng thái cuộc bầu cử, chiều cao chuỗi, kết quả kiểm toán, và kết quả kiểm phiếu"],
        ["POST", "/api/election/vote", "Cử tri gửi phiếu bầu mới (JSON body: voterId, candidateId)"],
        ["POST", "/api/election/mine", "Kích hoạt đào khối thủ công từ các phiếu bầu đang chờ trong Mempool"],
        ["POST", "/api/election/tamper", "Kích hoạt mô phỏng can thiệp dữ liệu khối để kiểm tra hệ thống báo động đỏ"],
        ["POST", "/api/cluster/mine", "Điều phối một nút bất kỳ trong cụm 10 máy P2P đào khối và phát tán"],
        ["POST", "/api/cluster/continuous/start", "Kích hoạt chế độ tự động đào khối liên tục mô phỏng mạng Bitcoin"],
        ["POST", "/api/cluster/continuous/stop", "Dừng chế độ đào khối liên tục"],
        ["GET", "/nodes", "Truy cập giao diện trực quan hóa sơ đồ mạng 10 nút P2P Mesh (Canvas Animation)"],
        ["GET", "/vote", "Truy cập Cổng bỏ phiếu trực tuyến dành cho cử tri"],
        ["GET", "/", "Truy cập Sổ cái chuỗi khối Explorer kiểm tra chi tiết các khối"]
    ]
    add_table(doc, headers_api, data_api, [30, 45, 85], caption="Bảng 2.3: Danh mục các Endpoint REST API của máy chủ NodeApiServer trên cổng 8000")
