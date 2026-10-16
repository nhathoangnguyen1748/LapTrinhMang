# Production-Ready Proof-of-Work (PoW) Engine & Ledger Simulator in Java

A modular, high-throughput, and cryptographically sound Proof-of-Work (PoW) blockchain simulation engine implemented in **Java 21+** with **Maven**. Built for standard CLI execution and first-class integration with **Apache NetBeans**.

---

## 👥 Bảng Ma Trận Phân Công & Đóng Góp Thành Viên

| MSSV | Họ và Tên | Vai trò | Công việc đã thực hiện | Issues & Pull Requests |
| :---: | :--- | :---: | :--- | :---: |
| **2380601549** | Nguyễn Hoàng Nhật | Nhóm trưởng | Thiết kế kiến trúc Blockchain & Tầng mạng P2P TCP Socket Cluster (`NetworkProtocol`, `NodeServer`, `PeerConnection`, `P2PNode`), Giao diện trực quan Web Dashboard & Next.js UI (`NodeApiServer`, `WebLauncher`), Quản lý Repo | Issue #1, Issue #7<br>PR #4, PR #8, PR #9 |
| **2380601305** | Nguyễn Đỗ Trung Lương | Thành viên | Thuật toán đồng thuận PoW, công cụ đào đa luồng `MultiThreadedMiner` (Java 21 Virtual Threads) & `DifficultyAdjuster` (Epoch Retargeting) | Issue #2<br>PR #5 |
| **2380601342** | Lê Quang Minh | Thành viên | Kiểm toán sổ cái `BlockchainValidator`, mô phỏng tấn công giả mạo `AttackSimulator` & Toàn bộ bộ kiểm thử tự động JUnit 5 | Issue #3<br>PR #6 |

---

## 🌐 Trọng Tâm Kiến Trúc Mạng (LẬP TRÌNH MẠNG - TCP/IP SOCKETS)

Dự án hiện thực hóa trọn vẹn mô hình **Mạng phân tán ngang hàng (Peer-to-Peer - P2P)** trên nền tảng **Java 21 Sockets & Virtual Threads**:

1. **Giao thức đóng gói Stream Framing (`NetworkProtocol`)**:
   - Khắc phục triệt để hiện tượng phân mảnh gói tin TCP (TCP Stream Fragmentation / Sticky Packets) bằng kỹ thuật **4-byte Big-Endian Length-Prefixed Framing**.
   - Mỗi khung tin (frame) mang một `NetworkMessage` định danh bằng UUID chống bão lặp gói tin (broadcast storm protection).
2. **Xử lý đa luồng Socket với Java 21 Virtual Threads (`NodeServer`, `PeerConnection`)**:
   - `NodeServer` chạy trên `ServerSocket` bất đồng bộ. Mỗi kết nối client đến được phục vụ bởi một Virtual Thread nhẹ (`Thread.ofVirtual()`), không gây cạn kiệt tài nguyên luồng hệ điều hành.
   - Kết nối song công (Duplex TCP Socket) với cơ chế tự động phát hiện đóng kết nối và xử lý ngắt.
3. **Giao thức truyền bá Gossip Protocol đa chặng (`P2PNode`, `Mempool`)**:
   - Giao dịch được tạo tại một Client/Wallet Node được truyền qua socket TCP tới các Relay Node trung gian và phát tán tới Miner Node (`Node-C -> Node-B -> Node-A`).
   - Bộ đệm `seenMessageIds` dạng Concurrent Sliding Cache loại bỏ nguy cơ loop gói tin trong mạng hình lưới.
4. **Đồng thuận phân tán & Đồng bộ chuỗi khối qua mạng (Nakamoto Consensus)**:
   - Khi một Node đào thành công khối mới, khối được băm và phát tán tức thì (`BLOCK_BROADCAST`) qua mạng TCP tới toàn bộ các Node.
   - Các Node nhận khối, thẩm định tính hợp lệ mật mã (`BlockchainValidator`) và cập nhật sổ cái cục bộ.
   - Khi một Node mới tham gia mạng hoặc có chuỗi ngắn hơn, giao thức bắt tay `HANDSHAKE` tự động kích hoạt `SYNC_REQUEST` / `SYNC_RESPONSE` để truyền tải và thay thế bằng chuỗi dài nhất hợp lệ (Longest Chain Rule).

---

## 🌟 Architecture & Highlights (Các Module Bổ Trợ)

1. **Cryptographic Core (`com.pow.simulator.crypto` & `core`)**:
   - Cryptographically secure SHA-256 hashing using `MessageDigest`.
   - Optimized `HexFormat` encoding and direct bitwise byte-inspection for zero-allocation mining loops.
   - Bitcoin-compliant binary Merkle Trees (`MerkleTree`) for transaction payload integrity.
   - Immutable data models using Java 21 `record` (`Block`, `Transaction`, `MiningResult`).

2. **Dynamic Difficulty Retargeting (`com.pow.simulator.difficulty`)**:
   - Bitcoin-style epoch retargeting every `EPOCH_SIZE` blocks (e.g. 5 blocks).
   - Dynamically evaluates `actual_time_taken` vs `expected_time_taken`.
   - Mathematical dampening factors clamp retargeting swings to $[0.25\times, 4.0\times]$ ($\pm 2$ bits) to eliminate erratic difficulty spikes.

3. **Multi-Threaded Mining Engine (`com.pow.simulator.consensus`)**:
   - Utilizes **Java 21 Virtual Threads** (`Executors.newVirtualThreadPerTaskExecutor()`) or fixed platform thread pools.
   - Nonce partitioning with chunked atomic strides (`AtomicLong`) eliminating duplicate hash evaluations and thread contention.
   - Immediate cross-worker cancellation via `AtomicBoolean` when any worker discovers a winning target hash.

4. **Ledger Integrity & Attack Simulation (`com.pow.simulator.validation` & `attack`)**:
   - Enterprise auditor `BlockchainValidator` verifying genesis block, hash linkage, payload Merkle roots, header recalculations, and PoW target satisfaction.
   - `AttackSimulator` demonstrating:
     - **Phase 1**: Payload tampering (Merkle root mismatch detection).
     - **Phase 2**: Header tampering without PoW (target difficulty violation).
     - **Phase 3**: Re-mining victim block only (cascading downstream invalidation of all subsequent blocks).
     - Calculates theoretical re-mining cost ($O(\sum 2^{D_i})$), illustrating why PoW renders history immutable against deep reorganization.

5. **Performance Benchmarking & Metrics (`com.pow.simulator.benchmark`)**:
   - Hashrate meter calculating real-time $H/s$ and $kH/s$.
   - Automated 20-block benchmark printing an ASCII results table and difficulty progression curve.

---

## 🚀 Opening and Running in Apache NetBeans

Because this project is built as a standard Maven project, Apache NetBeans natively supports it without any custom plugins:

1. Launch **Apache NetBeans**.
2. Click **File -> Open Project...**
3. Browse to `c:\Users\Admin\Desktop\LapTrinhMang` and select it (NetBeans will display the Maven project icon).
4. **Run Application**: Right-click the project -> **Run** (or press `F6`).
5. **Run Tests**: Right-click the project -> **Test** (or press `Alt + F6`).

---

## 💻 CLI Execution (Maven)

Run from PowerShell or Command Prompt:

### 1. Execute All JUnit 5 Tests
```powershell
$env:JAVA_HOME = "C:\Program Files\Apache NetBeans\jdk"
& "C:\Program Files\Apache NetBeans\java\maven\bin\mvn.cmd" clean test
```

### 2. Run the Interactive Simulator & Benchmark
```powershell
$env:JAVA_HOME = "C:\Program Files\Apache NetBeans\jdk"
& "C:\Program Files\Apache NetBeans\java\maven\bin\mvn.cmd" compile exec:java
```

---

## 🖥️ Giao Diện Trực Quan Web Dashboard & Next.js

Hệ thống cung cấp giao diện trực quan hóa tương tác theo thời gian thực (Real-time Visual Dashboard) để quan sát hoạt động của mạng P2P TCP Sockets, các luồng gói tin chuyển động, chuỗi khối Ledger, và các thao tác đồng thuận:

### 1. Khởi chạy Giao diện Trực quan Nhanh (1-Click - Khuyên dùng)
- **Cách 1**: Nhấp đúp chuột vào file **`run_gui.bat`** tại thư mục gốc.
- **Cách 2**: Chạy lệnh Maven:
  ```powershell
  $env:JAVA_HOME = "C:\Program Files\Apache NetBeans\jdk"
  & "C:\Program Files\Apache NetBeans\java\maven\bin\mvn.cmd" compile exec:java -Dexec.mainClass="com.pow.simulator.web.WebLauncher"
  ```
- Trình duyệt sẽ tự động mở tại **`http://localhost:8080`**.
- Giao diện tích hợp đầy đủ tính năng:
  - **Sơ đồ mạng P2P (Topology Canvas)** với các hạt chuyển động mô phỏng gói tin TCP giữa các cổng 8001, 8002, 8003, 8004.
  - **Nút điều khiển**: Gửi giao dịch (Node C -> B -> A), Khai thác PoW (Node A), Kết nối Node D (TCP Sync), Giả lập tấn công sửa dữ liệu (Audit Check).
  - **Trình duyệt Sổ cái (Ledger Explorer)**: Xem từng block, nonce, difficulty, merkle root, danh sách giao dịch.
  - **Terminal Console**: Xem trực tiếp log bắt tay TCP, broadcast và đồng thuận.

### 2. Giao diện Next.js 14 Độc Lập (`frontend-nextjs`)
- Dành cho người muốn kiểm tra mã nguồn React / Next.js 14 kết nối tới backend Java:
  - Nhấp đúp vào **`run_nextjs.bat`** hoặc vào thư mục `frontend-nextjs` chạy:
    ```bash
    npm install
    npm run dev
    ```
  - Truy cập: **`http://localhost:3000`**. Ứng dụng Next.js kết nối ngược về backend Java cổng 8080 qua REST API.

---

## 🧪 Test Suite Overview

Located in `src/test/java/com/pow/simulator`:
- `BlockMiningTest`: Verifies mined block hashes satisfy target difficulty bits and Merkle calculations.
- `DifficultyAdjustmentTest`: Verifies difficulty scaling (up/down), dampening caps ($0.25\times - 4.0\times$), and floor constraints.
- `TamperDetectionTest`: Confirms detection of payload modifications, forged hashes, broken linkage, and temporal paradoxes.
- `ConcurrencyTest`: Validates virtual thread cancellation, absence of deadlocks, and clean worker termination.
- `NetworkProtocolTest`: Verifies 4-byte length-prefix stream framing and payload serialization.
- `P2PClusterNetworkTest`: End-to-end integration test of 3-node P2P TCP network, gossip transaction broadcast, PoW block propagation, and late-joining node Nakamoto consensus sync.

