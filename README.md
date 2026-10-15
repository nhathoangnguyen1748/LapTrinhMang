# Production-Ready Proof-of-Work (PoW) Engine & Ledger Simulator in Java

A modular, high-throughput, and cryptographically sound Proof-of-Work (PoW) blockchain simulation engine implemented in **Java 21+** with **Maven**. Built for standard CLI execution and first-class integration with **Apache NetBeans**.

---

## 👥 Bảng Ma Trận Phân Công & Đóng Góp Thành Viên

| MSSV | Họ và Tên | Vai trò | Công việc đã thực hiện | Issues & Pull Requests |
| :---: | :--- | :---: | :--- | :---: |
| **2380601549** | Nguyễn Hoàng Nhật | Nhóm trưởng | Thiết kế kiến trúc PoW Engine, Tầng dữ liệu `Block`, `Transaction`, `MerkleTree`, `HashUtil` (SHA-256 ThreadLocal) & Quản lý Repository | Issue #1, PR #4 |
| **2380601305** | Nguyễn Đỗ Trung Lương | Thành viên | Thuật toán đồng thuận PoW, công cụ đào đa luồng `MultiThreadedMiner` (Java 21 Virtual Threads) & `DifficultyAdjuster` (Epoch Retargeting) | Issue #2, PR #5 |
| **2380601342** | Lê Quang Minh | Thành viên | Kiểm toán sổ cái `BlockchainValidator`, mô phỏng tấn công giả mạo `AttackSimulator` & Bộ kiểm thử tự động JUnit 5 | Issue #3, PR #6 |

---

## 🌟 Architecture & Highlights

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

## 🧪 Test Suite Overview

Located in `src/test/java/com/pow/simulator`:
- `BlockMiningTest`: Verifies mined block hashes satisfy target difficulty bits and Merkle calculations.
- `DifficultyAdjustmentTest`: Verifies difficulty scaling (up/down), dampening caps ($0.25\times - 4.0\times$), and floor constraints.
- `TamperDetectionTest`: Confirms detection of payload modifications, forged hashes, broken linkage, and temporal paradoxes.
- `ConcurrencyTest`: Validates virtual thread cancellation, absence of deadlocks, and clean worker termination.
