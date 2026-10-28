# -*- coding: utf-8 -*-
import docx
from docx.shared import Pt, RGBColor, Mm
from report_styles import (
    COLOR_PRIMARY, COLOR_SECONDARY, COLOR_ACCENT, COLOR_DARK, COLOR_MUTED,
    add_heading_1, add_heading_2, add_heading_3, add_heading_4,
    add_paragraph, add_bullet, add_table, add_code_block, add_callout, add_figure_box
)

def add_chapter_3(doc):
    doc.add_page_break()
    add_heading_1(doc, "CHƯƠNG 3: HIỆN THỰC HÓA CHI TIẾT TẦNG MẠNG P2P TCP SOCKETS")
    
    # 3.1 NetworkProtocol
    add_heading_2(doc, "3.1. Hiện thực hóa Giao thức đóng khung luồng tin NetworkProtocol")
    add_paragraph(doc, "Lớp NetworkProtocol là nền tảng kỹ thuật trọng yếu của tầng giao vận trong hệ thống. Lớp này trực tiếp tương tác với các luồng nhập/xuất của Socket (DataInputStream và DataOutputStream) để thực hiện quá trình đóng gói khung (Framing) và giải mã khung (Deframing) dữ liệu qua mạng TCP/IP.")
    
    add_heading_3(doc, "3.1.1. Mã nguồn hiện thực hóa và phân tích cơ chế đóng gói ghi khung tin (writeMessage)")
    add_paragraph(doc, "Phương thức writeMessage nhận luồng xuất DataOutputStream và đối tượng NetworkMessage, thực hiện chuyển đổi chuỗi tuần tự hóa thành mảng byte UTF-8 và ghi tiền tố độ dài 4 byte:")

    code_write = """// NetworkProtocol.java - Cơ chế đóng gói ghi khung tin qua TCP
public static synchronized void writeMessage(DataOutputStream out, NetworkMessage message) 
        throws IOException {
    if (out == null || message == null) {
        throw new IllegalArgumentException("OutputStream va NetworkMessage khong duoc phep null");
    }
    String serialized = message.serialize();
    byte[] payloadBytes = serialized.getBytes(StandardCharsets.UTF_8);
    
    // Ghi 4 byte Big-Endian chi dinh chinh xac do dai mang byte payload
    out.writeInt(payloadBytes.length);
    // Ghi toan bo mang byte payload vao bo dem luong TCP
    out.write(payloadBytes);
    // Xa tuc thi bo dem dam bao goi tin duoc gui ngay qua mang
    out.flush();
}"""
    add_code_block(doc, code_write, "Đoạn mã 3.1: Phương thức writeMessage đóng khung dữ liệu trong NetworkProtocol.java")

    add_paragraph(doc, "Phân tích kỹ thuật chi tiết của phương thức writeMessage:")
    add_bullet(doc, "Từ khóa synchronized được sử dụng nhằm đảm bảo tính an toàn đa luồng (Thread-safety). Trong một nút mạng P2P, nhiều luồng ảo có thể đồng thời yêu cầu gửi thông điệp qua cùng một kết nối Socket. Việc đồng bộ hóa ngăn chặn tình trạng hai luồng xen kẽ các byte ghi vào luồng xuất, làm xáo trộn cấu trúc khung tin.", bold_prefix="Đồng bộ hóa luồng ghi: ")
    add_bullet(doc, "Phương thức out.writeInt(payloadBytes.length) sử dụng định dạng chuẩn Big-Endian (Network Byte Order) để ghi 4 byte nguyên biểu diễn độ dài payload, đảm bảo tính tương thích tuyệt đối giữa các hệ điều hành khác nhau (x86 Little-Endian và ARM).", bold_prefix="Chuẩn hóa thứ tự byte Big-Endian: ")
    add_bullet(doc, "Lệnh out.flush() là bắt buộc để ép hệ điều hành đẩy dữ liệu ra ngăn xếp mạng ngay lập tức, tránh việc dữ liệu bị giữ lại trong bộ đệm nội bộ của Java gây trễ truyền thông.", bold_prefix="Xả bộ đệm tức thời (Flush): ")

    add_heading_3(doc, "3.1.2. Mã nguồn hiện thực hóa và phân tích cơ chế giải mã đọc khung tin (readMessage)")
    add_paragraph(doc, "Phương thức readMessage chịu trách nhiệm tiếp nhận luồng byte liên tục từ DataInputStream, giải mã chính xác ranh giới của thông điệp:")

    code_read = """// NetworkProtocol.java - Cơ chế đọc va giai ma khung tin tu luong TCP
public static NetworkMessage readMessage(DataInputStream in) throws IOException {
    if (in == null) {
        throw new IllegalArgumentException("InputStream khong duoc phep null");
    }
    // 1. Doc dung 4 byte Big-Endian de xac dinh do dai Payload
    int length;
    try {
        length = in.readInt();
    } catch (EOFException e) {
        // Peer chu dong ngat ket noi TCP sach se
        return null;
    }
    
    // 2. Kiem tra tinh hop le va han muc an toan chong tan cong OOM
    if (length < 0) {
        throw new IOException("Phat hien do dai khung tin am bat thuong: " + length);
    }
    if (length > MAX_FRAME_SIZE) { // MAX_FRAME_SIZE = 16 * 1024 * 1024 (16MB)
        throw new IOException("Kich thuoc khung tin vuot qua han muc an toan 16MB: " + length);
    }
    
    // 3. Doc chinh xac length byte tu luong TCP bang readNBytes
    byte[] buffer = in.readNBytes(length);
    if (buffer.length != length) {
        throw new EOFException("Ket noi bi dong bat ngo truoc khi doc du " + length + " bytes!");
    }
    
    // 4. Chuyen doi mang byte ve chuoi UTF-8 va phuc hoi doi tuong
    String serialized = new String(buffer, StandardCharsets.UTF_8);
    return NetworkMessage.deserialize(serialized);
}"""
    add_code_block(doc, code_read, "Đoạn mã 3.2: Phương thức readMessage giải mã khung tin trong NetworkProtocol.java")

    add_paragraph(doc, "Điểm nổi bật trong thiết kế của phương thức readMessage:")
    add_bullet(doc, "Sử dụng in.readNBytes(length) (tính năng mới từ Java 9+): Khác với phương thức in.read(buffer) truyền thống vốn có thể chỉ đọc được một phần dữ liệu khi mạng bị phân mảnh và trả về số byte thực tế ít hơn kích thước mảng, readNBytes thực hiện vòng lặp chặn cho đến khi đọc đủ 100% số byte được yêu cầu hoặc khi luồng bị đóng hoàn toàn.", bold_prefix="Cơ chế chặn đọc đủ byte (readNBytes): ")
    add_bullet(doc, "Thiết lập trần giới hạn an toàn 16MB: Bảo vệ hệ thống khỏi các cuộc tấn công gây cạn kiệt bộ nhớ (Denial of Service - DoS qua OOM). Nếu một nút độc hại cố tình gửi một số int độ dài giả mạo lên tới 2GB, máy chủ sẽ lập tức ngắt kết nối thay vì cấp phát mảng byte khổng lồ.", bold_prefix="Hạn mức an toàn chống DoS: ")
    add_bullet(doc, "Bắt ngoại lệ EOFException đúng chuẩn mạng: Phân biệt rõ ràng giữa việc kết nối đóng bình thường (Graceful Close) trả về null và lỗi đường truyền đột ngột.", bold_prefix="Xử lý đóng kết nối chuẩn mực: ")

    # 3.2 NetworkMessage
    add_heading_2(doc, "3.2. Hiện thực hóa Khung thông điệp NetworkMessage")
    add_paragraph(doc, "Thông điệp mạng được định nghĩa dưới dạng một Java 21 record bất biến, tự động tạo các hàm getter, equals(), hashCode() và toString():")

    code_msg = """// NetworkMessage.java - Cấu trúc thông điệp mạng bất biến
public record NetworkMessage(
        String messageId,
        MessageType type,
        String senderNodeId,
        long timestamp,
        String payload
) {
    public static NetworkMessage create(MessageType type, String senderNodeId, String payload) {
        return new NetworkMessage(
                UUID.randomUUID().toString(),
                type,
                senderNodeId,
                System.currentTimeMillis(),
                payload != null ? payload : ""
        );
    }

    public String serialize() {
        return messageId + "\\n" +
               type.name() + "\\n" +
               senderNodeId + "\\n" +
               timestamp + "\\n" +
               payload;
    }

    public static NetworkMessage deserialize(String data) throws IllegalArgumentException {
        String[] lines = data.split("\\n", 5);
        if (lines.length < 5) {
            throw new IllegalArgumentException("Du lieu khong dung dinh dang 5 truong NetworkMessage!");
        }
        return new NetworkMessage(
                lines[0],
                MessageType.valueOf(lines[1]),
                lines[2],
                Long.parseLong(lines[3]),
                lines[4]
        );
    }
}"""
    add_code_block(doc, code_msg, "Đoạn mã 3.3: Định nghĩa record NetworkMessage trong NetworkMessage.java")

    add_paragraph(doc, "Đặc điểm thiết kế của định dạng tuần tự hóa (Serialization):")
    add_paragraph(doc, "Hệ thống sử dụng định dạng phân tách bằng ký tự xuống dòng (\\n) với đúng 5 thành phần. Tham số thứ hai của hàm data.split(\"\\n\", 5) quy định giới hạn tách tối đa 5 phần tử. Điều này mang ý nghĩa kỹ thuật then chốt: Phần thân payload (phần tử thứ 5) hoàn toàn có thể chứa các ký tự xuống dòng nội bộ (chẳng hạn cấu trúc JSON nhiều dòng của danh sách khối hoặc phiếu bầu) mà không làm phá vỡ logic giải mã của 4 trường siêu dữ liệu ở phần đầu.")

    # 3.3 NodeServer
    add_heading_2(doc, "3.3. Hiện thực hóa Máy chủ TCP bất đồng bộ NodeServer trên Java 21")
    add_paragraph(doc, "Lớp NodeServer quản lý tiến trình máy chủ TCP ServerSocket, lắng nghe các yêu cầu kết nối từ các nút láng giềng và khởi tạo các kênh kết nối song công.")

    code_server = """// NodeServer.java - Lắng nghe kết nối TCP trên Java 21 Virtual Threads
public class NodeServer implements AutoCloseable {
    private final int port;
    private final Consumer<PeerConnection> onNewConnectionCallback;
    private final CopyOnWriteArrayList<PeerConnection> activeConnections = new CopyOnWriteArrayList<>();
    private ServerSocket serverSocket;
    private volatile boolean running = false;

    public void start() throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.running = true;

        // Khoi chay luong ao Java 21 lang nghe ket noi bat dong bo
        Thread.ofVirtual().name("p2p-server-acceptor-" + port).start(() -> {
            while (running && !serverSocket.isClosed()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    // Thiet lap TCP_NODELAY tat thuat toan Nagle giam do tre
                    clientSocket.setTcpNoDelay(true);

                    // Khoi tao PeerConnection cho ket noi moi den
                    PeerConnection connection = new PeerConnection(clientSocket, this::handleDisconnect);
                    activeConnections.add(connection);

                    if (onNewConnectionCallback != null) {
                        onNewConnectionCallback.accept(connection);
                    }
                    connection.startReading();
                } catch (IOException e) {
                    if (!running) break; // Dong may chu chu dong
                }
            }
        });
    }
}"""
    add_code_block(doc, code_server, "Đoạn mã 3.4: Hiện thực hóa máy chủ TCP lắng nghe trong NodeServer.java")

    add_paragraph(doc, "Những cải tiến kỹ thuật nổi bật trong NodeServer:")
    add_bullet(doc, "clientSocket.setTcpNoDelay(true): Vô hiệu hóa thuật toán Nagle trên tầng socket. Thuật toán Nagle mặc định trì hoãn việc gửi các gói tin nhỏ để chờ gom nhóm, gây ra độ trễ nhân tạo từ 40ms đến 200ms. Việc bật cờ TCP_NODELAY giúp các thông điệp giao dịch và khối được truyền đi tức thì ngay khi xuất xưởng.", bold_prefix="Tắt thuật toán Nagle (TCP_NODELAY): ")
    add_bullet(doc, "Quản lý luồng trên Thread.ofVirtual(): Toàn bộ vòng lặp serverSocket.accept() được chạy ngầm trên một Virtual Thread độc lập. Khi không có kết nối đến, luồng ảo rơi vào trạng thái chặn nhưng không hề chiếm dụng luồng hệ điều hành.", bold_prefix="Luồng chấp nhận kết nối ảo: ")
    add_bullet(doc, "activeConnections sử dụng CopyOnWriteArrayList: Cung cấp tính an toàn đa luồng tuyệt đối khi danh sách kết nối được duyệt đồng thời bởi nhiều luồng phát tán dữ liệu trong khi các luồng khác có thể thêm hoặc xóa kết nối.", bold_prefix="Cấu trúc dữ liệu luồng an toàn: ")

    # 3.4 PeerConnection
    add_heading_2(doc, "3.4. Hiện thực hóa Kênh kết nối song công PeerConnection")
    add_paragraph(doc, "Mỗi kết nối TCP hai chiều giữa hai nút mạng được đóng gói trọn vẹn trong lớp PeerConnection:")

    ascii_peer = """
+-------------------------------------------------------------------------+
|              KIEN TRUC SONG CONG (FULL-DUPLEX) CUA PEERCONNECTION       |
+-------------------------------------------------------------------------+
|                                                                         |
|  [ SENDING FLOW (Thread-safe) ]                                         |
|  Thread 1 \\                                                             |
|  Thread 2 -+-> synchronized(writeLock) -> NetworkProtocol.writeMessage  |
|  Thread 3 /                                        |                    |
|                                                    v                    |
|                                           DataOutputStream (TCP Out)    |
|                                                    |                    |
|  ==================== TCP SOCKET CONNECTION =======|=================   |
|                                                    v                    |
|  [ RECEIVING FLOW (Dedicated Virtual Thread) ]     |                    |
|  DataInputStream (TCP In)                          |                    |
|       |                                                                 |
|       v                                                                 |
|  NetworkProtocol.readMessage() (Blocking readNBytes)                    |
|       |                                                                 |
|       v                                                                 |
|  messageConsumer.accept(msg) -> P2PNode.handleMessage(msg)             |
+-------------------------------------------------------------------------+
"""
    add_figure_box(doc, ascii_peer, "Hình 3.1: Kiến trúc xử lý socket bất đồng bộ trên Java 21 Virtual Threads của NodeServer")

    code_conn = """// PeerConnection.java - Kênh kết nối TCP song công
public class PeerConnection implements AutoCloseable {
    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;
    private final Object writeLock = new Object();
    private Consumer<NetworkMessage> messageConsumer;
    private volatile boolean active = true;

    public PeerConnection(Socket socket) throws IOException {
        this.socket = socket;
        this.socket.setTcpNoDelay(true);
        this.in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        this.out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }

    public void startReading() {
        Thread.ofVirtual().name("p2p-reader-" + socket.getRemoteSocketAddress()).start(() -> {
            try {
                while (active && !socket.isClosed()) {
                    NetworkMessage message = NetworkProtocol.readMessage(in);
                    if (message == null) break; // Dong ket noi chu dong tu peer
                    if (messageConsumer != null) {
                        messageConsumer.accept(message);
                    }
                }
            } catch (IOException ignored) {
            } finally {
                close();
            }
        });
    }

    public boolean sendMessage(NetworkMessage message) {
        if (!active || socket.isClosed()) return false;
        synchronized (writeLock) {
            try {
                NetworkProtocol.writeMessage(out, message);
                return true;
            } catch (IOException e) {
                close();
                return false;
            }
        }
    }
}"""
    add_code_block(doc, code_conn, "Đoạn mã 3.5: Lớp kết nối song công PeerConnection.java")

    # 3.5 P2PNode & Gossip
    add_heading_2(doc, "3.5. Hiện thực hóa Nút mạng P2P trung tâm và Giao thức Gossip")
    add_paragraph(doc, "Lớp P2PNode là \"bộ não\" điều phối toàn bộ các hoạt động mạng, liên kết các thành phần socket, mempool, blockchain và thuật toán đồng thuận:")

    ascii_gossip = """
+-------------------------------------------------------------------------+
|                  CO CHE TRUYEN TIN DON GOSSIP VA KHO LOC TRUNG          |
+-------------------------------------------------------------------------+
|                       [ NGUOI GUI: Node-C ]                             |
|                                |                                        |
|                     TX_BROADCAST (UUID: a1b2)                           |
|                                |                                        |
|                                v                                        |
|                       [ NUT NHAN: Node-B ]                              |
|                                |                                        |
|              +-----------------+-----------------+                      |
|              | seenMessageIds.add("a1b2")        |                      |
|              +-----------------+-----------------+                      |
|                                |                                        |
|                +---------------+---------------+                        |
|                |                               |                        |
|        [ Tra ve FALSE ]                 [ Tra ve TRUE ]                 |
|       (Da tung nhan truoc)             (Goi tin hoan toan moi)          |
|                |                               |                        |
|                v                               v                        |
|         Huy bo goi tin!             1. Nhap Mempool                     |
|     (Ngat bao goi tin loop)         2. Gossip chuyen tiep toi peers     |
|                                        (Loai tru nguoi gui Node-C)      |
+-------------------------------------------------------------------------+
"""
    add_figure_box(doc, ascii_gossip, "Hình 3.2: Cơ chế truyền tin đồn Gossip Protocol và bộ đệm khử lặp seenMessageIds")

    code_node = """// P2PNode.java - Xử lý thông điệp trung tâm và giao thức Gossip
public void handleMessage(NetworkMessage msg, PeerConnection source) {
    // 1. Kiem tra va ghi nhan vao bo dem loc trung lap (Sliding Cache)
    if (!seenMessageIds.add(msg.messageId())) {
        return; // Da tung xu ly goi tin nay, huy bo de tranh lap vong vo han
    }

    switch (msg.type()) {
        case TX_BROADCAST -> {
            Transaction tx = Transaction.deserialize(msg.payload());
            // Kiem tra hop le va dua vao Mempool
            if (mempool.add(tx)) {
                // Gossip tiep tuc toi cac peers khac (loai tru nut vua gui den)
                relayMessage(msg, source);
            }
        }
        case BLOCK_BROADCAST -> {
            Block block = Block.deserialize(msg.payload());
            // Kiem toan tinh toan ven cua khoi nhan duoc
            if (blockchain.isValidNewBlock(block)) {
                blockchain.addBlock(block);
                mempool.removeAll(block.transactions());
                // Gossip khoi hop le toi toan mang
                relayMessage(msg, source);
            } else if (block.index() > blockchain.getLatestBlock().index() + 1) {
                // Phat hien chuoi cua peer dai hon -> Gui yeu cau dong bo
                source.sendMessage(NetworkMessage.create(
                        MessageType.SYNC_REQUEST, nodeId, ""
                ));
            }
        }
        case SYNC_REQUEST -> {
            // Dong goi toan bo so cai gui ve cho peer yeu cau
            String chainJson = blockchain.toJson();
            source.sendMessage(NetworkMessage.create(
                    MessageType.SYNC_RESPONSE, nodeId, chainJson
            ));
        }
        case SYNC_RESPONSE -> {
            // Nhan so cai va thuc hien quy tac chuoi dai nhat Nakamoto
            Blockchain receivedChain = Blockchain.fromJson(msg.payload());
            if (receivedChain.size() > blockchain.size() && BlockchainValidator.validate(receivedChain).valid()) {
                blockchain.replaceChain(receivedChain);
                System.out.printf("[%s] Dong bo thanh cong so cai moi! Chieu cao: %d\\n", 
                        nodeId, blockchain.size());
            }
        }
    }
}"""
    add_code_block(doc, code_node, "Đoạn mã 3.6: Xử lý thông điệp mạng P2P và Gossip trong P2PNode.java")

    # 3.6 Mempool
    add_heading_2(doc, "3.6. Hiện thực hóa Hòm phiếu đệm Mempool an toàn luồng")
    add_paragraph(doc, "Lớp Mempool hoạt động như một vùng đệm trung chuyển lưu trữ các giao dịch chưa được xác nhận đóng khối:")
    add_bullet(doc, "Sử dụng ConcurrentHashMap<String, Transaction> lưu trữ các giao dịch theo txId, mang lại độ phức tạp tra cứu và xóa O(1) với khả năng truy cập đồng thời cao.", bold_prefix="Cấu trúc lưu trữ O(1): ")
    add_bullet(doc, "Phương thức take(maxCount): Lấy ra một danh sách gồm tối đa maxCount giao dịch để thợ đào đưa vào khuôn mẫu khối mới.", bold_prefix="Lấy giao dịch đóng khối: ")
    add_bullet(doc, "Phương thức removeAll(List<Transaction> txs): Khi một khối mới được chấp nhận vào chuỗi (dù do chính nút này đào hay nhận từ nút khác qua mạng), toàn bộ các giao dịch đã được xác nhận sẽ bị dọn dẹp sạch sẽ khỏi Mempool.", bold_prefix="Dọn dẹp giao dịch đã xác nhận: ")

    # 3.7 StandaloneNode
    add_heading_2(doc, "3.7. Hiện thực hóa Nút chạy độc lập đa máy tính StandaloneNode")
    add_paragraph(doc, "Để hệ thống không chỉ dừng lại ở việc mô phỏng nội bộ trên một máy tính mà có thể triển khai thực tế trên mạng phân tán gồm nhiều máy tính vật lý của các thành viên trong nhóm (Nguyễn Hoàng Nhật, Nguyễn Đỗ Trung Lương, Lê Quang Minh), lớp StandaloneNode được phát triển:")
    add_bullet(doc, "Tự động phát hiện địa chỉ IP của tất cả các giao diện mạng (NetworkInterface) đang hoạt động trên máy: Ethernet LAN, Wi-Fi cục bộ, và mạng riêng ảo Tailscale/ZeroTier.", bold_prefix="Tự động nhận diện IP: ")
    add_bullet(doc, "Hỗ trợ tệp cấu hình hạt giống peer_seed.txt: Cho phép các máy khách chỉ cần chỉ định địa chỉ IP:Port của máy chủ Gateway (ví dụ 192.168.1.15:8001 hoặc 100.85.12.34:8001) là có thể tự động kết nối và đồng bộ vào mạng P2P chỉ với một cú nhấp chuột (1-Click Run).", bold_prefix="Cấu hình mạng hạt giống: ")
    add_bullet(doc, "Giao diện dòng lệnh CLI chuyên nghiệp: Cung cấp menu tương tác trực quan cho phép người dùng tự phát tán phiếu bầu (lệnh 'vote'), tự kích hoạt đào khối (lệnh 'mine'), xem sổ cái (lệnh 'chain') và kiểm tra các kết nối TCP hiện có (lệnh 'peers').", bold_prefix="Menu điều khiển CLI: ")
