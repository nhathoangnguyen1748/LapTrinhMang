# -*- coding: utf-8 -*-
import docx
from docx.shared import Pt, RGBColor, Mm
from report_styles import (
    COLOR_PRIMARY, COLOR_SECONDARY, COLOR_ACCENT, COLOR_DARK, COLOR_MUTED,
    add_heading_1, add_heading_2, add_heading_3, add_heading_4,
    add_paragraph, add_bullet, add_table, add_code_block, add_callout, add_figure_box
)

def add_chapter_6(doc):
    doc.add_page_break()
    add_heading_1(doc, "CHƯƠNG 6: KẾT LUẬN VÀ HƯỚNG PHÁT TRIỂN")
    
    # 6.1 Kết quả đạt được
    add_heading_2(doc, "6.1. Các kết quả nổi bật đạt được của đề tài")
    add_paragraph(doc, "Trải qua quá trình nghiên cứu lý thuyết chuyên sâu và triển khai kỹ thuật phần mềm nghiêm túc, đề tài \"Xây dựng hệ thống Bầu cử điện tử E-Voting phân tán dựa trên công nghệ Blockchain với giao thức đồng thuận Proof-of-Work và mạng ngang hàng P2P TCP/IP trên nền tảng Java 21\" đã hoàn thành trọn vẹn 100% các mục tiêu đề ra ban đầu:")
    
    add_bullet(doc, "Tự thiết kế và hiện thực hóa thành công tầng mạng ngang hàng bằng Socket TCP thuần của Java (ServerSocket, Socket, DataInputStream, DataOutputStream) mà không sử dụng bất kỳ thư viện mạng bên thứ ba nào, kiểm soát hoàn toàn ngăn xếp mạng từ mức byte.", bold_prefix="1. Làm chủ lập trình mạng TCP Socket thuần: ")
    add_bullet(doc, "Giải quyết triệt để hiện tượng kinh điển dính gói tin (TCP sticky packets) và phân mảnh luồng dữ liệu bằng chuẩn đóng khung 4-byte Big-Endian Length-Prefixed Stream Framing, đảm bảo tính toàn vẹn 100% cho mọi thông điệp truyền tải.", bold_prefix="2. Khắc phục triệt để hiện tượng dính gói TCP: ")
    add_bullet(doc, "Hiện thực hóa giao thức truyền tin đồn đa chặng (Gossip Protocol) với bộ đệm khử lặp seenMessageIds Sliding Cache, cho phép mạng P2P Mesh lan truyền giao dịch và khối tới hàng chục nút chỉ trong vài mili-giây mà không gây ra hiện tượng bão gói tin (Broadcast Storm).", bold_prefix="3. Giao thức truyền tin đồn hiệu quả cao: ")
    add_bullet(doc, "Ứng dụng thành công công nghệ đột phá Java 21 Virtual Threads (Project Loom) vào việc xử lý kết nối socket bất đồng bộ và bộ đào PoW đa luồng, mang lại tốc độ băm song song vượt trội (đạt trên 265 kH/s trên CPU thông thường) với mức tiêu tốn bộ nhớ RAM cực thấp.", bold_prefix="4. Tiên phong ứng dụng Java 21 Virtual Threads: ")
    add_bullet(doc, "Hiện thực hóa đầy đủ sổ cái Blockchain bất biến chuẩn Bitcoin với khối 5 trường thuộc tính, hàm băm SHA-256 tối ưu hóa không cấp phát chuỗi, chữ ký số bất đối xứng ECDSA, cây Merkle nhị phân, thuật toán điều chỉnh độ khó động (Epoch Retargeting) và luật chuỗi dài nhất Nakamoto Consensus.", bold_prefix="5. Sổ cái Blockchain chuẩn mực mật mã học: ")
    add_bullet(doc, "Bảo vệ nguyên tắc cốt lõi của bầu cử \"Một người - Một phiếu bầu\" qua hai lớp kiểm soát chặt chẽ (Mempool và Sổ cái lịch sử), ngăn chặn hoàn toàn các hành vi gian lận hoặc bỏ phiếu nhiều lần.", bold_prefix="6. Cơ chế chống gian lận cử tri tuyệt đối: ")
    add_bullet(doc, "Xây dựng hệ sinh thái giao diện trực quan hóa hoàn chỉnh: Cổng bỏ phiếu cử tri (/vote), Sơ đồ cụm 10 nút P2P Mesh với hoạt ảnh hạt photon truyền tin thời gian thực (/nodes), Sổ cái Explorer (/) và hệ thống cảnh báo đỏ phát hiện can thiệp dữ liệu tức thì.", bold_prefix="7. Hệ sinh thái Dashboard trực quan, sinh động: ")
    add_bullet(doc, "Hỗ trợ triển khai thực tế trên mạng đa máy tính (Multi-machine deployment) thông qua LAN, Wi-Fi và Tailscale VPN chỉ với một cú nhấp chuột (1-Click Run), đáp ứng tiêu chuẩn phần mềm sẵn sàng đưa vào ứng dụng thực tiễn.", bold_prefix="8. Khả năng vận hành phân tán thực tế: ")

    # 6.2 Hạn chế
    add_heading_2(doc, "6.2. Các hạn chế còn tồn tại và bài học kinh nghiệm")
    add_paragraph(doc, "Bên cạnh những thành tựu nổi bật, nhóm tác giả cũng thẳng thắn nhìn nhận những hạn chế kỹ thuật hiện tại của đề tài:")
    add_bullet(doc, "Cơ chế đồng thuận Proof-of-Work (PoW) mặc dù có tính bảo mật toán học và chống tấn công Sybil cao nhất, nhưng lại tiêu tốn tài nguyên tính toán CPU và điện năng trong quá trình giải số Nonce. Đối với các cuộc bầu cử có quy mô hàng triệu cử tri, thời gian sinh khối có thể trở thành rào cản thông lượng.", bold_prefix="Tiêu tốn năng lượng của PoW: ")
    add_bullet(doc, "Trong phiên bản hiện tại, mặc dù danh tính cử tri được bảo vệ dưới dạng mã số và địa chỉ ví ECDSA (pow1_...), nhưng dữ liệu phiếu bầu vẫn được lưu công khai trong chuỗi khối để phục vụ việc kiểm phiếu minh bạch. Điều này chưa đáp ứng được chuẩn mực tuyệt đối về \"Bỏ phiếu kín vô danh\" (Secret Ballot) ở cấp độ quốc gia.", bold_prefix="Tính riêng tư của lá phiếu: ")
    add_bullet(doc, "Hệ thống mạng P2P hiện tại dựa vào danh sách địa chỉ IP tĩnh hoặc tệp cấu hình hạt giống peer_seed.txt, chưa tích hợp giao thức khám phá nút tự động phân tán hoàn toàn như Kademlia DHT (Distributed Hash Table).", bold_prefix="Khám phá nút tự động: ")

    # 6.3 Hướng phát triển
    add_heading_2(doc, "6.3. Hướng phát triển trong tương lai")
    add_paragraph(doc, "Dựa trên những kinh nghiệm thực chiến đã tích lũy được từ đồ án, nhóm đề xuất các hướng nâng cấp và mở rộng chuyên sâu cho hệ thống trong tương lai:")
    add_bullet(doc, "Nghiên cứu chuyển đổi hoặc kết hợp cơ chế đồng thuận PoW với Proof-of-Stake (PoS) hoặc Byzantine Fault Tolerance (PBFT / Raft) nhằm giảm thiểu triệt để điện năng tiêu thụ, tăng tốc độ xác nhận giao dịch lên hàng ngàn phiếu bầu mỗi giây.", bold_prefix="Nâng cấp cơ chế đồng thuận lai (Hybrid Consensus): ")
    add_bullet(doc, "Tích hợp Bằng chứng không kiến thức (zk-SNARKs - Zero-Knowledge Succinct Non-Interactive Argument of Knowledge) hoặc Mã hóa đồng cấu (Homomorphic Encryption): Cho phép cử tri chứng minh lá phiếu của mình là hợp lệ và được tính vào kết quả chung mà không ai (kể cả thợ đào hay hội đồng bầu cử) biết được cử tri đã bầu cho ứng cử viên nào, đạt chuẩn mực bỏ phiếu kín tuyệt đối.", bold_prefix="Ứng dụng Mật mã học Zero-Knowledge Proofs: ")
    add_bullet(doc, "Hiện thực hóa máy ảo thực thi hợp đồng thông minh nhẹ (Lightweight Smart Contract Engine) để tự động hóa quy trình mở hòm phiếu, kiểm phiếu và công bố trúng cử khi hết thời gian quy định mà không cần bất kỳ sự can thiệp thủ công nào của con người.", bold_prefix="Hợp đồng thông minh tự động (Smart Contracts): ")
    add_bullet(doc, "Phát triển ứng dụng di động hoàn chỉnh (Mobile App trên iOS và Android) tích hợp quét sinh trắc học (vân tay / FaceID) để tăng cường trải nghiệm người dùng cho cử tri sinh viên.", bold_prefix="Ứng dụng di động sinh trắc học: ")

    # Tài liệu tham khảo
    doc.add_page_break()
    add_heading_1(doc, "TÀI LIỆU THAM KHẢO")
    
    references = [
        "[1] S. Nakamoto, \"Bitcoin: A Peer-to-Peer Electronic Cash System,\" Decentralized Business Review, 2008. [Online]. Available: https://bitcoin.org/bitcoin.pdf",
        "[2] Oracle Corporation, \"JEP 444: Virtual Threads,\" Java Enhancement Proposals, JDK 21 Documentation, Sep. 2023. [Online]. Available: https://openjdk.org/jeps/444",
        "[3] J. Postel, \"Transmission Control Protocol - DARPA Internet Program Protocol Specification,\" RFC 793, IETF, Sep. 1981. [Online]. Available: https://www.rfc-editor.org/rfc/rfc793",
        "[4] R. C. Merkle, \"A Digital Signature Based on a Conventional Encryption Function,\" in Advances in Cryptology — CRYPTO '87, Lecture Notes in Computer Science, vol. 293, Springer, Berlin, Heidelberg, 1988, pp. 369-378.",
        "[5] National Institute of Standards and Technology (NIST), \"Secure Hash Standard (SHS),\" Federal Information Processing Standards Publication (FIPS PUB 180-4), Aug. 2015.",
        "[6] D. Johnson, A. Menezes, and S. Vanstone, \"The Elliptic Curve Digital Signature Algorithm (ECDSA),\" International Journal of Information Security, vol. 1, no. 1, pp. 36-63, Aug. 2001.",
        "[7] A. S. Tanenbaum and D. J. Wetherall, Computer Networks, 5th ed. Boston, MA: Pearson Prentice Hall, 2011.",
        "[8] B. Goetz et al., Java Concurrency in Practice, Boston, MA: Addison-Wesley Professional, 2006.",
        "[9] A. M. Antonopoulos, Mastering Bitcoin: Programming the Open Blockchain, 2nd ed. Sebastopol, CA: O'Reilly Media, 2017.",
        "[10] L. Lamport, R. Shostak, and M. Pease, \"The Byzantine Generals Problem,\" ACM Transactions on Programming Languages and Systems (TOPLAS), vol. 4, no. 3, pp. 382-401, Jul. 1982.",
        "[11] K. Birman, M. Hayden, O. Ozkasap, Z. Xiao, K. Budiu, and Y. Minsky, \"Bimodal Multicast,\" ACM Transactions on Computer Systems (TOCS), vol. 17, no. 2, pp. 41-88, May 1999.",
        "[12] Trường Đại học Công nghệ TP.HCM (HUTECH), \"Đề cương chi tiết học phần Lập trình mạng,\" Khoa Công nghệ Thông tin, 2024."
    ]
    for ref in references:
        add_paragraph(doc, ref, indent=False)

    # Phụ lục
    doc.add_page_break()
    add_heading_1(doc, "PHỤ LỤC: HƯỚNG DẪN CÀI ĐẶT VÀ VẬN HÀNH 1-CLICK")
    
    add_heading_2(doc, "A.1. Yêu cầu môi trường hệ thống")
    add_bullet(doc, "Bộ phát triển Java Development Kit: JDK 21 trở lên (khuyến nghị OpenJDK 21 hoặc Oracle JDK 21).", bold_prefix="Java SDK: ")
    add_bullet(doc, "Công cụ quản lý dự án: Apache Maven 3.8+ (hoặc sử dụng NetBeans 19+, IntelliJ IDEA 2023+).", bold_prefix="Build Tool: ")
    add_bullet(doc, "Trình duyệt web hiện đại: Google Chrome, Microsoft Edge, Mozilla Firefox hoặc Safari hỗ trợ HTML5 Canvas.", bold_prefix="Trình duyệt: ")

    add_heading_2(doc, "A.2. Hướng dẫn biên dịch mã nguồn")
    add_paragraph(doc, "Mở cửa sổ dòng lệnh Terminal hoặc Command Prompt tại thư mục gốc của dự án và thực thi lệnh Maven:")
    
    code_build = """mvn clean test       # Biên dịch mã nguồn và chạy 38 ca kiểm thử
mvn clean package    # Đóng gói sản phẩm thành file JAR thực thi"""
    add_code_block(doc, code_build, "Lệnh biên dịch dự án với Apache Maven")
    add_heading_2(doc, "A.3. Hướng dẫn khởi chạy Hệ sinh thái Web Simulator (Khuyến nghị)")
    add_paragraph(doc, "Để khởi chạy toàn bộ cụm 10 nút P2P, máy chủ REST API cổng 8000 và tự động mở giao diện web trên trình duyệt, chỉ cần nhấp đúp chuột vào tệp run_web.bat hoặc gõ lệnh:")

    code_run_web = """java -cp target/classes com.pow.simulator.web.WebLauncher
# Hoặc nhấp đúp trực tiếp vào tệp run_web.bat tại thư mục gốc"""
    add_code_block(doc, code_run_web, "Lệnh khởi chạy Web Simulator")
    add_paragraph(doc, "Sau khi khởi chạy thành công, mở trình duyệt và truy cập các địa chỉ sau:")
    add_bullet(doc, "http://localhost:8000/vote  – Cổng bỏ phiếu cử tri trực tuyến", bold_prefix="1. Bỏ phiếu cử tri: ")
    add_bullet(doc, "http://localhost:8000/nodes – Sơ đồ 10 nút P2P Mesh trực quan hóa hạt photon", bold_prefix="2. Sơ đồ 10 Nodes: ")
    add_bullet(doc, "http://localhost:8000/      – Sổ cái chuỗi khối Explorer kiểm toán", bold_prefix="3. Sổ Cái Explorer: ")

    add_heading_2(doc, "A.4. Hướng dẫn triển khai mạng phân tán trên nhiều máy tính khác nhau")
    add_paragraph(doc, "Để kết nối 3 máy tính của các bạn sinh viên (Máy 1 - Hoàng Nhật, Máy 2 - Trung Lương, Máy 3 - Quang Minh) trong phòng lab hoặc qua Internet:")
    add_bullet(doc, "Máy 1 (Hoàng Nhật) chạy run_seed_node.bat (cổng 8001), lấy địa chỉ IP LAN hoặc Tailscale.", bold_prefix="Bước 1 (Seed Node): ")
    add_bullet(doc, "Máy 2 (Trung Lương) & Máy 3 (Quang Minh): Điền IP:8001 vào peer_seed.txt rồi chạy run_peer_node.bat.", bold_prefix="Bước 2 (Peer Nodes): ")
    add_bullet(doc, "Hệ thống tự động bắt tay TCP, đồng bộ chuỗi khối dài nhất và hòa mạng P2P hoàn tất!", bold_prefix="Bước 3 (Đồng bộ): ")
