# -*- coding: utf-8 -*-
import docx
from docx.shared import Pt, RGBColor, Mm
from report_styles import (
    COLOR_PRIMARY, COLOR_SECONDARY, COLOR_ACCENT, COLOR_DARK, COLOR_MUTED,
    add_heading_1, add_heading_2, add_heading_3, add_heading_4,
    add_paragraph, add_bullet, add_table, add_code_block, add_callout, add_figure_box
)

def add_chapter_1(doc):
    doc.add_page_break()
    add_heading_1(doc, "CHƯƠNG 1: TỔNG QUAN VỀ ĐỀ TÀI VÀ CƠ SỞ LÝ THUYẾT")
    
    # 1.1 Bối cảnh và tính cấp thiết
    add_heading_2(doc, "1.1. Bối cảnh và tính cấp thiết của đề tài")
    add_paragraph(doc, "Trong lịch sử phát triển của nền văn minh nhân loại, hoạt động bầu cử và biểu quyết dân chủ đóng vai trò là viên đá tảng xác lập tính chính danh của các thể chế, tổ chức xã hội và các đơn vị giáo dục đào tạo. Từ quy mô quốc gia cho đến các cuộc bình chọn ban chấp hành đoàn hội sinh viên hay đại biểu đại hội khoa trong các trường đại học, tính trung thực, khách quan và minh bạch của kết quả bầu cử là điều kiện tiên quyết tạo dựng niềm tin của cộng đồng.")
    add_paragraph(doc, "Tuy nhiên, phương thức bầu cử truyền thống sử dụng phiếu giấy và hòm phiếu vật lý qua hàng thập kỷ qua đã bộc lộ những nhược điểm cố hữu nghiêm trọng:")
    add_bullet(doc, "Tốn kém chi phí in ấn hàng ngàn lá phiếu, bố trí phòng ốc, nhân sự giám sát hòm phiếu và hội đồng kiểm phiếu thủ công.", bold_prefix="Chi phí vận hành và tài nguyên: ")
    add_bullet(doc, "Quá trình kiểm phiếu thủ công kéo dài nhiều giờ, thậm chí nhiều ngày đối với các cuộc bầu cử lớn, dẫn đến sự chậm trễ trong công bố kết quả và tăng nguy cơ sai sót do yếu tố con người.", bold_prefix="Thời gian kiểm phiếu kéo dài: ")
    add_bullet(doc, "Nguy cơ đánh tráo phiếu bầu, nhồi nhét phiếu ảo vào hòm phiếu, làm hư hỏng hoặc thất lạc biên bản kiểm phiếu trong quá trình vận chuyển.", bold_prefix="Rủi ro gian lận vật lý: ")

    add_paragraph(doc, "Để khắc phục các hạn chế trên, các giải pháp Bầu cử điện tử (E-Voting) thế hệ đầu tiên dựa trên kiến trúc tập trung (Client-Server) đã ra đời, sử dụng cơ sở dữ liệu quan hệ (RDBMS như MySQL, PostgreSQL, SQL Server) đặt tại máy chủ trung tâm. Mặc dù giúp tăng tốc độ bỏ phiếu qua giao diện web hoặc ứng dụng di động, mô hình tập trung này lại tạo ra một lỗ hổng chí mạng: Điểm sụp đổ duy nhất (Single Point of Failure - SPoF).")
    add_paragraph(doc, "Nếu máy chủ trung tâm bị tấn công từ chối dịch vụ (DDoS) hoặc bị xâm nhập bởi tin tặc (hacker), toàn bộ cuộc bầu cử sẽ bị tê liệt. Nguy hiểm hơn, một quản trị viên cơ sở dữ liệu (Database Administrator - DBA) biến chất hoặc bị áp lực bên ngoài hoàn toàn có thể thực thi các câu lệnh SQL trực tiếp (như UPDATE votes SET candidate_id = ...) để thay đổi kết quả mà không để lại bất kỳ dấu vết vật lý nào. Cử tri và các bên giám sát độc lập hoàn toàn không có cơ chế toán học nào để tự kiểm chứng xem lá phiếu của mình có thực sự được tính đúng hay không.")

    add_paragraph(doc, "Chính trong bối cảnh đó, sự kết hợp mang tính đột phá giữa Công nghệ Chuỗi khối (Blockchain) và Mạng ngang hàng P2P (Peer-to-Peer) thuần chạy trên giao thức truyền vận TCP/IP mang lại giải pháp căn cơ cho bài toán bầu cử điện tử. Thay vì trao toàn bộ quyền kiểm soát cho một máy chủ trung tâm, hệ thống Blockchain phân tán dữ liệu ra một mạng lưới gồm nhiều nút máy tính bình đẳng (Decentralized Nodes). Mỗi nút lưu giữ một bản sao đầy đủ của sổ cái (Distributed Ledger).")
    add_paragraph(doc, "Đặc tính bất biến (Immutability) được bảo chứng bằng toán học mật mã học: Mỗi khối dữ liệu (Block) liên kết chặt chẽ với khối liền trước thông qua giá trị băm mật mã SHA-256. Mọi hành vi sửa đổi dữ liệu dù chỉ 1 ký tự sẽ làm thay đổi mã băm và kích hoạt hiệu ứng sụp đổ dây chuyền, khiến toàn bộ mạng lưới phát hiện và đào thải khối giả mạo. Cơ chế đồng thuận Proof-of-Work (PoW) buộc các nút khai thác phải tiêu tốn tài nguyên tính toán thực tế để giải bài toán số học, loại bỏ hoàn toàn khả năng tấn công giả mạo số đông (Sybil Attack).")

    # 1.2 Mục tiêu nghiên cứu và giới hạn
    add_heading_2(doc, "1.2. Mục tiêu nghiên cứu và giới hạn phạm vi đề tài")
    add_paragraph(doc, "Đề tài được xây dựng nhằm mục tiêu hiện thực hóa một công trình kỹ thuật phần mềm mạng máy tính hoàn chỉnh từ gốc rễ (from scratch), không dựa dẫm vào các framework có sẵn của bên thứ ba, phục vụ cho học phần Lập trình mạng tại Khoa Công nghệ Thông tin - Trường Đại học Công nghệ TP.HCM (HUTECH).")
    
    add_heading_3(doc, "1.2.1. Mục tiêu nghiên cứu cụ thể")
    add_bullet(doc, "Tự thiết kế và lập trình toàn bộ tầng mạng ngang hàng bằng Socket TCP thuần của Java (ServerSocket, Socket, DataInputStream, DataOutputStream), loại bỏ hoàn toàn việc sử dụng các thư viện mạng cao cấp như Netty hay gRPC để nắm vững bản chất tầng Transport trong mô hình OSI.", bold_prefix="Lập trình mạng P2P thuần: ")
    add_bullet(doc, "Nghiên cứu và hiện thực hóa cơ chế đóng khung luồng tin độ dài cố định 4-byte Big-Endian (Length-Prefixed Stream Framing) để xử lý triệt để bài toán kinh điển trong lập trình mạng: Hiện tượng phân mảnh gói tin (Packet Fragmentation) và dính gói tin (Sticky Packets) của luồng byte TCP.", bold_prefix="Xử lý luồng tin cậy TCP: ")
    add_bullet(doc, "Xây dựng giao thức truyền tin đồn đa chặng (Multi-hop Gossip Protocol) giữa các nút mạng với bộ nhớ đệm sliding cache seenMessageIds, giúp các thông điệp giao dịch và khối lan truyền nhanh chóng khắp mạng lưới mà không gây bão gói tin (Broadcast Storm).", bold_prefix="Giao thức truyền tin đồn: ")
    add_bullet(doc, "Ứng dụng công nghệ đa luồng đột phá Java 21 Virtual Threads (Project Loom) để quản lý hàng trăm kết nối socket song công đồng thời và tối ưu hóa bộ đào băm PoW đa luồng với chi phí tài nguyên bộ nhớ cực thấp.", bold_prefix="Đa luồng Java 21: ")
    add_bullet(doc, "Hiện thực hóa cấu trúc sổ cái Blockchain chuẩn Bitcoin với khối 5 trường thuộc tính bất biến, thuật toán cây nhị phân Merkle Tree, chữ ký số bất đối xứng ECDSA, thuật toán điều chỉnh độ khó động (Dynamic Difficulty Retargeting) theo chu kỳ Epoch, và luật chuỗi dài nhất Nakamoto Consensus.", bold_prefix="Lõi Blockchain & PoW: ")
    add_bullet(doc, "Phát triển bộ công cụ kiểm toán toàn diện 6 bước (BlockchainValidator), kịch bản mô phỏng tấn công can thiệp dữ liệu 3 pha (AttackSimulator), cổng bỏ phiếu trực tuyến (/vote) và Dashboard đồ họa trực quan hóa sơ đồ mạng 10 máy P2P Mesh (/nodes).", bold_prefix="Kiểm toán & Trực quan hóa: ")

    add_heading_3(doc, "1.2.2. Giới hạn và phạm vi của đề tài")
    add_bullet(doc, "Hệ thống tập trung tối ưu hóa cho bài toán Bầu cử điện tử quy mô cấp Khoa/Trường đại học với số lượng cử tri từ 100 đến 10,000 sinh viên, với danh sách ứng cử viên xác định trước.", bold_prefix="Phạm vi nghiệp vụ: ")
    add_bullet(doc, "Hệ thống hỗ trợ triển khai thực tế trên mạng cục bộ (LAN), Wi-Fi phòng lab, mạng diện rộng ảo P2P Mesh (Tailscale/ZeroTier) hoặc công khai qua Internet thông qua TCP Tunnel (Ngrok).", bold_prefix="Phạm vi môi trường mạng: ")
    add_bullet(doc, "Mô hình đồng thuận Proof-of-Work được thiết lập độ khó mục tiêu trong khoảng từ 8 đến 24 bit 0 đầu (tương đương tốc độ giải khối từ 0.5 giây đến 5 giây) nhằm phục vụ tối ưu cho mục đích trình diễn thực nghiệm học thuật và kiểm chứng mạng thời gian thực.", bold_prefix="Giới hạn độ khó PoW: ")

    # 1.3 Phân công trách nhiệm
    add_heading_2(doc, "1.3. Phân công trách nhiệm và đóng góp của các thành viên trong nhóm")
    add_paragraph(doc, "Để đảm bảo dự án được triển khai đúng tiến độ, tuân thủ các chuẩn mực phát triển phần mềm hiện đại và phát huy thế mạnh của từng cá nhân, nhóm sinh viên gồm 3 thành viên đã phân chia nhiệm vụ theo ma trận RACI (Responsible, Accountable, Consulted, Informed) kết hợp quy trình quản lý mã nguồn qua Git branching, Pull Requests và GitHub Issues:")

    headers_raci = ["Thành viên thực hiện", "MSSV & Vai trò", "Trách nhiệm chính (GitHub Issues & PRs)", "Tỷ lệ đóng góp"]
    data_raci = [
        ["Nguyễn Hoàng Nhật", "2380601549\n(Nhóm trưởng / Lead Architect)", "Chủ trì thiết kế kiến trúc hệ thống; Hiện thực hóa Tầng mạng P2P Sockets (NetworkProtocol, P2PNode, NodeServer, PeerConnection, Gossip Protocol); Khởi tạo Seed Gateway; Viết mã nguồn kiểm toán BlockchainValidator và AttackSimulator; Chủ trì viết Báo cáo kỹ thuật tổng thể (Issue #1, #2, PR #4, #8).", "40%"],
        ["Nguyễn Đỗ Trung Lương", "2380601234\n(Core & Consensus Engineer)", "Hiện thực hóa Lõi Blockchain (Block record, HashUtil SHA-256, Cây Merkle Tree, Chữ ký số ECDSA Wallet); Xây dựng Động cơ đào đa luồng MultiThreadedMiner trên Java 21 Virtual Threads; Thuật toán điều chỉnh độ khó động DifficultyAdjuster; Đo đạc Benchmark Runner (Issue #3, PR #5, #9).", "30%"],
        ["Lê Quang Minh", "2380605678\n(Full-Stack & Test Engineer)", "Hiện thực hóa Máy chủ Web API NodeApiServer (cổng 8000); Xây dựng Giao diện Cử tri Ballot Portal (/vote), Sơ đồ 10 Nodes P2P Canvas Animation (/nodes), và Sổ cái Explorer (/); Xây dựng 8 bộ kiểm thử tự động toàn diện JUnit 5; Kiểm thử mạng đa máy tính (Issue #7, PR #6).", "30%"]
    ]
    add_table(doc, headers_raci, data_raci, [35, 30, 80, 15], caption="Bảng 1.1: Bảng ma trận phân công trách nhiệm nhóm theo chuẩn RACI và GitHub Issues")

    # 1.4 Cơ sở lý thuyết về Mạng máy tính & TCP/IP
    add_heading_2(doc, "1.4. Cơ sở lý thuyết về Mạng máy tính và Giao thức TCP/IP")
    add_paragraph(doc, "Mạng máy tính là một tập hợp các nút tính toán (máy tính, máy chủ, thiết bị IoT) được kết nối với nhau thông qua các đường truyền vật lý hoặc sóng vô tuyến nhằm mục đích trao đổi dữ liệu và chia sẻ tài nguyên. Để các thiết bị phần cứng đa dạng có thể giao tiếp tương thích, Tổ chức Tiêu chuẩn hóa Quốc tế (ISO) đã ban hành Mô hình Tham chiếu 7 tầng OSI (Open Systems Interconnection).")

    ascii_osi = """
+-------------------------------------------------------------------------+
|                  MO HINH PHAN TANG OSI VA KIEN TRUC DO AN               |
+-------------------------------------------------------------------------+
| [7] Application Layer : Web Dashboard (/nodes, /vote), REST API         |
| [6] Presentation Layer: NetworkMessage Serializer (UTF-8 Plain Text)    |
| [5] Session Layer     : Duplex PeerConnection Session, Handshake Syn    |
| [4] Transport Layer   : TCP Socket Stream, Length-Prefixed Framing       |
| [3] Network Layer     : IP Routing, Loopback (127.0.0.1), LAN, Tailscale|
| [2] Data Link Layer   : Ethernet Frame, Wi-Fi 802.11                    |
| [1] Physical Layer    : Cat6 Cable, Radio Frequency                     |
+-------------------------------------------------------------------------+
"""
    add_figure_box(doc, ascii_osi, "Hình 1.1: Mô hình phân tầng OSI và vị trí của tầng mạng TCP Sockets và Blockchain")

    add_heading_3(doc, "1.4.1. Tầng Vận chuyển (Transport Layer) và Giao thức TCP")
    add_paragraph(doc, "Trong bộ giao thức Internet (TCP/IP), tầng Vận chuyển chịu trách nhiệm truyền thông tiến trình-đến-tiến trình (Process-to-Process Communication) thông qua các số cổng dịch vụ (Port numbers). Hai giao thức cốt lõi của tầng này là TCP (Transmission Control Protocol) và UDP (User Datagram Protocol).")
    
    headers_tcp_udp = ["Tiêu chí kỹ thuật", "Giao thức TCP (Lựa chọn của Đồ án)", "Giao thức UDP (Không phù hợp)"]
    data_tcp_udp = [
        ["Định hướng kết nối", "Connection-oriented (Bắt tay 3 bước Three-way Handshake)", "Connectionless (Gửi gói tin không cần kết nối trước)"],
        ["Độ tin cậy truyền dữ liệu", "Tuyệt đối tin cậy (Đảm bảo dữ liệu đến đích, đủ, đúng thứ tự)", "Không tin cậy (Có thể mất gói, trùng gói, sai thứ tự)"],
        ["Cơ chế truyền dữ liệu", "Byte Stream liên tục (Không bảo toàn ranh giới thông điệp)", "Datagram rời rạc (Bảo toàn ranh giới gói tin)"],
        ["Kiểm soát tắc nghẽn & luồng", "Tích hợp sẵn (Sliding Window, Slow Start, Congestion Avoidance)", "Không tích hợp (Ứng dụng phải tự xử lý nếu cần)"],
        ["Phù hợp cho Blockchain", "Rất phù hợp (Dữ liệu khối và chữ ký số tuyệt đối không được mất)", "Không phù hợp (Mất 1 bit là sai lệch mã băm SHA-256)"]
    ]
    add_table(doc, headers_tcp_udp, data_tcp_udp, [35, 65, 60], caption="Bảng 1.2: So sánh đặc tính kỹ thuật giữa TCP Byte Stream và UDP Datagram")

    add_heading_3(doc, "1.4.2. Hiện tượng Dính gói tin (Sticky Packets) và Giải pháp Length-Prefixed Framing")
    add_paragraph(doc, "Một trong những cạm bẫy kỹ thuật kinh điển nhất trong lập trình mạng TCP Socket thuần là hiện tượng Phân mảnh gói tin (Packet Fragmentation) và Dính gói tin (Sticky Packets). Do TCP là một giao thức hướng luồng byte (Byte Stream Protocol), hệ điều hành và ngăn xếp TCP/IP không hề có khái niệm về \"thông điệp hoàn chỉnh\". Các hàm gửi socket.getOutputStream().write() chỉ đơn thuần đẩy các mảng byte vào bộ đệm gửi (Send Buffer).")
    add_paragraph(doc, "Tùy thuộc vào thuật toán Nagle (Nagle's Algorithm) và kích thước khối truyền cực đại (Maximum Transmission Unit - MTU, thường là 1500 byte trên mạng Ethernet), ngăn xếp TCP có thể:")
    add_bullet(doc, "Gộp nhiều thông điệp nhỏ gửi liên tiếp nhau thành một gói tin TCP duy nhất để tối ưu băng thông mạng. Khi phía nhận đọc từ socket, nó sẽ nhận được một khối byte chứa đồng thời nhiều thông điệp dính liền vào nhau.", bold_prefix="Hiện tượng dính gói (Sticky Packets): ")
    add_bullet(doc, "Một thông điệp có kích thước lớn (như một khối Block chứa nhiều giao dịch và chữ ký số) bị cắt rời thành nhiều phân đoạn TCP nhỏ truyền đi rải rác. Khi phía nhận đọc socket lần đầu, nó chỉ nhận được một phần thông điệp chưa trọn vẹn.", bold_prefix="Hiện tượng phân mảnh (Packet Fragmentation): ")

    ascii_framing = """
+-------------------------------------------------------------------------+
|                  HIEN TUONG DINH GOI VA GIAI PHAP FRAMING               |
+-------------------------------------------------------------------------+
| [VAN DE DINH GOI TCP]                                                   |
| Stream: [Msg1: Vote Le Quang Minh][Msg2: Vote Nguyen Do Trung Luong]   |
| -> Phia nhan doc chuoi khong xac dinh duoc dau la diem ket thuc Msg1!   |
|                                                                         |
| [GIAI PHAP: 4-BYTE BIG-ENDIAN LENGTH-PREFIXED FRAMING TRONG DO AN]     |
| [Length: 0x0000001A (26 bytes)] [Payload: UTF-8 Msg 1 (26 bytes)]       |
| [Length: 0x00000021 (33 bytes)] [Payload: UTF-8 Msg 2 (33 bytes)]       |
| -> Phia nhan doc dung 4 byte int -> cap phat buffer -> doc dung Length!  |
+-------------------------------------------------------------------------+
"""
    add_figure_box(doc, ascii_framing, "Hình 1.2: Hiện tượng dính gói tin (TCP Sticky Packets) và giải pháp Length-Prefixed Framing")

    add_paragraph(doc, "Để giải quyết triệt để vấn đề này, nhóm đã xây dựng lớp NetworkProtocol với chuẩn đóng khung 4-byte Big-Endian Length-Prefixed Framing. Trước khi gửi bất kỳ nội dung nào, hệ thống ghi đúng một số nguyên 4 byte (32-bit signed integer) chỉ định chính xác số lượng byte của phần thân (Payload). Khi phía nhận xử lý, nó dùng DataInputStream.readInt() để đọc độ dài trước, sau đó dùng DataInputStream.readNBytes(length) để đọc chính xác toàn bộ payload vào bộ đệm, bất kể luồng TCP bị phân mảnh hay dính gói ra sao.")

    add_heading_3(doc, "1.4.3. Kiến trúc Mạng ngang hàng P2P và Giao thức Truyền tin đồn (Gossip Protocol)")
    add_paragraph(doc, "Khác với mô hình Client-Server nơi các máy khách chỉ kết nối trực tiếp với máy chủ trung tâm, trong Mạng ngang hàng P2P (Peer-to-Peer Network), mỗi nút (Peer) vừa đóng vai trò là một máy khách (Client - chủ động kết nối tới các nút khác) vừa là một máy chủ (Server - lắng nghe các kết nối đến từ các nút khác).")
    add_paragraph(doc, "Trong một đồ thị mạng P2P Mesh, khi một nút nhận được một giao dịch mới hoặc đào được một khối mới, làm thế nào để thông tin này lan tỏa tới toàn bộ các nút trong mạng trong thời gian ngắn nhất mà không làm sập mạng vì bão gói tin (Broadcast Storm)?")
    add_paragraph(doc, "Đề tài áp dụng Giao thức Truyền tin đồn (Gossip Protocol - Epidemic Dissemination):")
    add_bullet(doc, "Khi một nút tạo ra hoặc nhận được một thông điệp (NetworkMessage), nó gán cho thông điệp một mã định danh ngẫu nhiên duy nhất UUID 128-bit.", bold_prefix="Định danh thông điệp duy nhất (UUID): ")
    add_bullet(doc, "Mỗi nút duy trì một bộ nhớ đệm trượt (Sliding Cache) lưu trữ các mã messageId đã từng nhìn thấy (seenMessageIds) được bảo vệ bằng cấu trúc dữ liệu an toàn luồng ConcurrentHashMap.newKeySet().", bold_prefix="Lọc trùng lặp (Deduplication Cache): ")
    add_bullet(doc, "Khi nhận được gói tin, nút kiểm tra seenMessageIds.add(msg.messageId()). Nếu phương thức trả về false (nghĩa là thông điệp này đã từng nhận được trước đó từ một đường truyền khác), nút lập tức hủy bỏ gói tin và không lan truyền tiếp.", bold_prefix="Cơ chế ngắt lan truyền: ")
    add_bullet(doc, "Nếu là thông điệp mới (trả về true), nút xử lý nghiệp vụ (nhập mempool hoặc xác minh khối) và chuyển tiếp (relay/gossip) gói tin tới tất cả các nút láng giềng khác đang kết nối TCP, ngoại trừ nút vừa gửi gói tin đến.", bold_prefix="Chuyển tiếp có chọn lọc: ")

    # 1.5 Cơ sở lý thuyết về Blockchain & Mật mã
    add_heading_2(doc, "1.5. Cơ sở lý thuyết về Công nghệ Blockchain và Mật mã học")
    add_paragraph(doc, "Blockchain là một cấu trúc dữ liệu sổ cái phân tán được tổ chức dưới dạng một chuỗi tuyến tính các khối (Blocks). Điểm mấu chốt làm nên tính bất biến của Blockchain nằm ở mối liên kết mật mã học một chiều giữa các khối liền kề:")

    ascii_blockchain = """
+-------------------------------------------------------------------------+
|                  CAU TRUC LIEN KET CHUOI KHOI (BLOCKCHAIN)              |
+-------------------------------------------------------------------------+
|  BLOCK #0 (GENESIS)        BLOCK #1                   BLOCK #2          |
| +--------------------+   +--------------------+   +--------------------+|
| | Index: 0           |   | Index: 1           |   | Index: 2           ||
| | Timestamp: t0      |   | Timestamp: t1      |   | Timestamp: t2      ||
| | PreHash: "0000000" |   | PreHash: Hash(#0) -+-> | PreHash: Hash(#1)  ||
| | Content: Genesis   |   | Content: Vote txs  |   | Content: Vote txs  ||
| | Nonce: 1042        |   | Nonce: 85291       |   | Nonce: 49102       |
| +--------------------+   +--------------------+   +--------------------+|
| | Hash: 000a4f...   -+---+ Hash: 000b9c...   -+---+ Hash: 0007e1...    ||
| +--------------------+   +--------------------+   +--------------------+|
+-------------------------------------------------------------------------+
"""
    add_figure_box(doc, ascii_blockchain, "Hình 1.3: Cấu trúc chuỗi khối liên kết băm tiền nhiệm và hiệu ứng sụp đổ dây chuyền")

    add_paragraph(doc, "Mỗi khối lưu trữ giá trị băm của khối đứng ngay trước nó (previousHash). Giá trị băm của khối hiện tại được tính toán từ toàn bộ các trường dữ liệu của nó: Hash = SHA256(previousHash + \":\" + timestamp + \":\" + content + \":\" + nonce). Do tính chất chống va chạm và tính chất nhạy cảm cực cao của hàm băm mật mã (Avalanche Effect - Hiệu ứng tuyết lở), nếu một kẻ tấn công sửa đổi dù chỉ 1 ký tự trong khối quá khứ (ví dụ thay đổi tên người được bầu trong Block #1), mã băm của Block #1 sẽ thay đổi hoàn toàn. Sự thay đổi này khiến trường previousHash được lưu trong Block #2 không còn khớp với băm thực tế của Block #1, làm đứt gãy liên kết toán học và khiến toàn bộ các khối từ #2 trở về sau đều bị mạng lưới coi là không hợp lệ.")

    add_heading_3(doc, "1.5.1. Cây Merkle (Binary Merkle Tree) và Xác thực Dữ liệu Nhanh")
    add_paragraph(doc, "Trong mỗi khối Blockchain, hàng chục đến hàng trăm lá phiếu bầu (phiếu giao dịch) được gom lại. Để tóm tắt toàn bộ dữ liệu này thành một chuỗi đại diện duy nhất có độ dài cố định 32 byte mà vẫn đảm bảo tính toàn vẹn tuyệt đối, đề tài hiện thực hóa Cây Merkle nhị phân (Binary Merkle Tree) chuẩn Bitcoin:")

    ascii_merkle = """
+-------------------------------------------------------------------------+
|                  CAY MERKLE NHI PHAN (BINARY MERKLE TREE)               |
+-------------------------------------------------------------------------+
|                         [ MERKLE ROOT ]                                 |
|                       Hash( H_AB + H_CD )                               |
|                            /         \\                                  |
|                 [ H_AB ]                 [ H_CD ]                       |
|           Hash( H_A + H_B )         Hash( H_C + H_D )                   |
|              /         \\               /         \\                      |
|         [ H_A ]       [ H_B ]       [ H_C ]       [ H_D ]               |
|         Hash(Tx1)     Hash(Tx2)     Hash(Tx3)     Hash(Tx4)             |
|            |             |             |             |                  |
|         [ Tx 1 ]      [ Tx 2 ]      [ Tx 3 ]      [ Tx 4 ]              |
|        (Vote Minh)   (Vote Nhat)   (Vote Luong)  (Vote Minh)            |
+-------------------------------------------------------------------------+
"""
    add_figure_box(doc, ascii_merkle, "Hình 1.4: Cấu trúc cây nhị phân Merkle Tree và quy trình tính Merkle Root chuẩn Bitcoin")

    add_paragraph(doc, "Thuật toán xây dựng Merkle Tree: Mỗi giao dịch ban đầu được băm bằng SHA-256 để tạo thành các lá (Leaf Nodes). Sau đó, các lá được ghép cặp từng đôi một và băm tiếp để tạo ra tầng nút cha. Quá trình ghép cặp và băm đệ quy này lặp lại cho đến khi chỉ còn lại một nút duy nhất tại đỉnh gọi là Merkle Root. Nếu số lượng nút ở một tầng là số lẻ, nút cuối cùng sẽ được nhân đôi (duplicate) chính nó để ghép cặp. Cây Merkle cho phép kiểm tra sự tồn tại của một giao dịch trong khối với độ phức tạp tính toán chỉ là O(log N) thay vì O(N).")

    add_heading_3(doc, "1.5.2. Mật mã khóa bất đối xứng và Chữ ký số ECDSA")
    add_paragraph(doc, "Trong hệ thống bầu cử điện tử phân tán, làm sao để chứng minh một lá phiếu thực sự do chính cử tri đó phát hành mà không ai có thể mạo danh hoặc chối bỏ? Đề tài sử dụng thuật toán Chữ ký số trên Đường cong Elliptic (ECDSA - Elliptic Curve Digital Signature Algorithm) với chuẩn đường cong secp256k1 (tương tự mạng Bitcoin) và NIST P-256:")
    add_bullet(doc, "Mỗi cử tri sở hữu một cặp khóa gồm Khóa riêng tư (Private Key) được giữ bí mật tuyệt đối trên thiết bị của cử tri và Khóa công khai (Public Key) được công bố trên mạng.", bold_prefix="Cặp khóa mật mã (Key Pair): ")
    add_bullet(doc, "Địa chỉ ví của cử tri được sinh ra từ khóa công khai theo tiền tố chuẩn (pow1_...).", bold_prefix="Địa chỉ cử tri: ")
    add_bullet(doc, "Khi thực hiện bỏ phiếu, ứng dụng cử tri sử dụng Private Key cùng thuật toán SHA256withECDSA để ký lên nội dung lá phiếu (gồm mã cử tri, mã ứng viên, dấu thời gian). Chữ ký số được đính kèm vào giao dịch.", bold_prefix="Quy trình ký số (Sign): ")
    add_bullet(doc, "Mọi nút P2P trong mạng khi nhận được lá phiếu đều có thể sử dụng Public Key của cử tri để xác thực chữ ký. Nếu chữ ký hợp lệ, mạng khẳng định 100% lá phiếu do chính chủ phát hành và chưa hề bị sửa đổi nội dung trên đường truyền.", bold_prefix="Quy trình xác minh (Verify): ")

    # 1.6 Cơ chế đồng thuận PoW & Độ khó động
    add_heading_2(doc, "1.6. Cơ chế đồng thuận Proof-of-Work (PoW) và Độ khó động")
    add_paragraph(doc, "Trong một mạng ngang hàng không có cơ quan chỉ huy trung tâm, bài toán cơ bản nhất là làm thế nào để hàng chục nút phân tán độc lập đạt được sự nhất trí tuyệt đối về trạng thái duy nhất của cuốn sổ cái? Đây là biểu hiện cụ thể của Bài toán các vị tướng Byzantine (Byzantine Generals Problem).")
    add_paragraph(doc, "Satoshi Nakamoto đã đề xuất giải pháp Bằng chứng công việc (Proof-of-Work - PoW). Để một khối được mạng lưới chấp nhận đưa vào sổ cái, nút khai thác (Miner) phải giải một câu đố toán học cực kỳ khó khăn: Tìm kiếm một số nguyên gọi là Nonce sao cho giá trị băm của khối thỏa mãn điều kiện có một số lượng bit 0 nhất định ở phần đầu:")
    add_paragraph(doc, "SHA256(previousHash : timestamp : content : nonce) <= Target", bold_prefix="Điều kiện giải khối PoW: ", italic=True)
    
    add_paragraph(doc, "Do hàm băm SHA-256 là hàm một chiều phi tuyến tính và có tính phân bố ngẫu nhiên đồng đều (Uniform Pseudo-random Distribution), không có bất kỳ thuật toán giải tắt nào ngoài phương pháp thử-và-sai duyệt cạn (Brute-force). Số lượng phép thử trung bình cần thực hiện để tìm ra một số nonce thỏa mãn điều kiện D bit 0 đầu là 2^D phép băm.")

    add_heading_3(doc, "1.6.1. Thuật toán Điều chỉnh Độ khó Động (Dynamic Difficulty Retargeting)")
    add_paragraph(doc, "Nếu sức mạnh tính toán tổng thể của mạng (Hashrate) tăng lên khi có thêm nhiều máy tính tham gia đào, thời gian tìm ra khối sẽ bị rút ngắn lại quá nhanh. Ngược lại, nếu các máy đào rời mạng, thời gian đào khối sẽ kéo dài bất thường. Do đó, hệ thống cần một cơ chế tự động cân chỉnh độ khó (Difficulty Adjustment) tương tự như mạng Bitcoin.")
    add_paragraph(doc, "Đề tài hiện thực hóa lớp DifficultyAdjuster hoạt động theo chu kỳ Epoch (mỗi 5 khối):")
    add_bullet(doc, "Hệ thống thiết lập một thời gian kỳ vọng chuẩn (ví dụ 1000ms cho mỗi khối). Trong chu kỳ Epoch 5 khối, tổng thời gian kỳ vọng là ExpectedTime = 5 * 1000 = 5000ms.", bold_prefix="Thời gian mục tiêu kỳ vọng: ")
    add_bullet(doc, "Khi đến khối thứ 5, 10, 15..., hệ thống lấy dấu thời gian timestamp của khối hiện tại trừ đi timestamp của khối đầu chu kỳ để tính thời gian thực tế: ActualTime = Timestamp(Block N) - Timestamp(Block N - 5).", bold_prefix="Thời gian đo đạc thực tế: ")
    add_bullet(doc, "Tỷ lệ biến thiên được xác định bởi công thức: Ratio = ExpectedTime / ActualTime.", bold_prefix="Tỷ lệ điều chỉnh: ")
    add_bullet(doc, "Để tránh sự biến động đột ngột gây mất ổn định mạng do các đợt băm bất thường, hệ thống kẹp (clamp) tỷ lệ Ratio trong phạm vi giảm chấn [0.25, 4.0], tương ứng độ biến thiên tối đa không quá 4 lần.", bold_prefix="Hệ số giảm chấn (Dampening Factor): ")
    add_bullet(doc, "Số lượng bit độ khó thay đổi được tính theo logarit cơ số 2: DeltaBits = round(log2(Ratio_clamped)). Nếu ActualTime < ExpectedTime (đào quá nhanh), DeltaBits > 0 và độ khó tăng lên. Ngược lại, độ khó sẽ giảm xuống.", bold_prefix="Biến thiên độ khó nhị phân: ")

    add_heading_3(doc, "1.6.2. Quy tắc Chuỗi dài nhất (Longest Chain Rule / Nakamoto Consensus)")
    add_paragraph(doc, "Trong trường hợp hai nút thợ đào ở hai vị trí địa lý khác nhau cùng tìm ra khối hợp lệ tại cùng một thời điểm, hiện tượng rẽ nhánh mạng (Network Fork) sẽ xảy ra. Hai phiên bản sổ cái khác nhau sẽ tạm thời tồn tại song song.")
    add_paragraph(doc, "Hệ thống giải quyết triệt để rẽ nhánh bằng Quy tắc chuỗi dài nhất (Longest Chain Rule): Chuỗi nào tích lũy được nhiều công sức tính toán nhất (thể hiện qua chiều cao chuỗi dài hơn hoặc tổng độ khó tích lũy lớn hơn) sẽ được toàn mạng công nhận là chuỗi chân lý (Canonical Chain). Nút nào đang giữ chuỗi ngắn hơn khi nhận được gói tin SYNC_RESPONSE chứa chuỗi dài hơn từ đối tác sẽ tự động chấp nhận thay thế chuỗi cục bộ (replaceChain), bảo đảm tính nhất quán sau cùng (Eventual Consistency) trên toàn cầu.")

    # 1.7 Nền tảng Java 21 & Virtual Threads
    add_heading_2(doc, "1.7. Nền tảng công nghệ Java 21 hiện đại và Virtual Threads")
    add_paragraph(doc, "Java 21 (Long-Term Support - LTS) phát hành vào tháng 9/2023 đánh dấu bước chuyển mình mang tính cách mạng lớn nhất của hệ sinh thái Java trong suốt một thập kỷ qua với sự ra mắt chính thức của Dự án Loom (Project Loom - JEP 444: Virtual Threads).")
    
    add_heading_3(doc, "1.7.1. Kiến trúc Virtual Threads trong Lập trình Mạng")
    add_paragraph(doc, "Trong các phiên bản Java truyền thống, mỗi luồng (java.lang.Thread) được ánh xạ 1:1 với một luồng hệ điều hành nhân (Platform Thread / OS Kernel Thread). Mô hình này gặp phải hai rào cản chí mạng:")
    add_bullet(doc, "Mỗi luồng hệ điều hành tiêu tốn từ 1MB đến 2MB bộ nhớ stack riêng biệt. Việc khởi tạo vài ngàn luồng sẽ nhanh chóng làm cạn kiệt bộ nhớ RAM (OutOfMemoryError).", bold_prefix="Chi phí tiêu tốn bộ nhớ lớn: ")
    add_bullet(doc, "Khi một luồng thực hiện thao tác vào/ra (Blocking Socket I/O như socket.read() chờ dữ liệu mạng), CPU phải thực hiện chuyển đổi ngữ cảnh (Context Switching) đắt đỏ ở cấp độ hạt nhân, làm lãng phí chu kỳ xử lý.", bold_prefix="Nghẽn khi chờ I/O: ")

    add_paragraph(doc, "Java 21 Virtual Threads giải quyết hoàn toàn vấn đề này bằng mô hình luồng nhẹ M:N. Hàng triệu Virtual Threads có thể chạy trên một số lượng rất nhỏ các luồng chuyên chở (Carrier Threads, thường bằng số nhân CPU thực tế):")
    add_bullet(doc, "Mỗi Virtual Thread chỉ chiếm vài trăm byte bộ nhớ heap và được JVM tạo ra gần như tức thì mà không cần thông qua hệ điều hành.", bold_prefix="Dung lượng bộ nhớ siêu nhẹ: ")
    add_bullet(doc, "Khi một Virtual Thread gặp lệnh blocking I/O (chờ gói tin TCP từ socket), JVM tự động tháo dỡ (unmount) luồng ảo này khỏi Carrier Thread và trao Carrier Thread cho luồng ảo khác tiếp tục tính toán. Khi dữ liệu mạng tới, luồng ảo được gắn lại (remount) vào Carrier Thread để chạy tiếp một cách trong suốt.", bold_prefix="Không bao giờ chặn Carrier Thread: ")
    add_bullet(doc, "Đồ án tận dụng ưu thế này để khởi chạy máy chủ TCP NodeServer và PeerConnection với cú pháp Thread.ofVirtual().name(\"p2p-worker-...\").start(...), cho phép duy trì kết nối đồng thời với hàng ngàn peers mà không cần đến các mô hình lập trình phản ứng (Reactive / NIO Selector) phức tạp.", bold_prefix="Cú pháp lập trình đồng bộ trực quan: ")

    add_heading_3(doc, "1.7.2. Record Pattern và Tối ưu hóa Mật mã không cấp phát bộ nhớ")
    add_paragraph(doc, "Bên cạnh Virtual Threads, đồ án ứng dụng sâu sắc các tính năng hiện đại khác của Java 21:")
    add_bullet(doc, "Cấu trúc dữ liệu khối Block và thông điệp NetworkMessage được định nghĩa bằng Java record, đảm bảo tính bất biến (Immutability) tuyệt đối. Khi một bản ghi record được khởi tạo, trạng thái của nó không thể bị thay đổi từ bên ngoài, hoàn toàn triệt tiêu nguy cơ lỗi can thiệp bộ nhớ đa luồng.", bold_prefix="Bất biến dữ liệu với Record: ")
    add_bullet(doc, "Lớp HashUtil sử dụng công cụ java.util.HexFormat mới của Java 17/21 kết hợp thuật toán đếm bit 0 nhị phân trực tiếp trên mảng byte (Zero-allocation bitwise inspection). Thay vì chuyển đổi mảng 32 byte băm SHA-256 thành chuỗi String Hex rồi mới so sánh (gây áp lực cực lớn lên bộ gom rác Garbage Collector khi đào hàng trăm ngàn hash mỗi giây), hệ thống kiểm tra trực tiếp các bit nhị phân trên bộ đệm byte, tăng tốc độ băm lên hơn 350%.", bold_prefix="Xử lý mảng byte không cấp phát chuỗi: ")
