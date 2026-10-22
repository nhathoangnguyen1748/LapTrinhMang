# Hướng Dẫn Kiểm Toán Toàn Vẹn Blockchain & Mô Phỏng Tấn Công Tamper

> **Tài liệu kiểm toán kỹ thuật - Module Issue #3**  
> **Người thực hiện:** Lê Quang Minh (MSSV: 2380601342)  
> **Dự án:** Proof-of-Work Engine & Distributed Ledger Simulator (Lập Trình Mạng)

---

## 1. Giới Thiệu Tổng Quan

Module này chịu trách nhiệm bảo đảm tính toàn vẹn tuyệt đối của sổ cái Blockchain, phát hiện mọi hành vi giả mạo dữ liệu và mô phỏng các kịch bản tấn công mật mã nhằm chứng minh tính bất biến (immutability) của Proof-of-Work.

Hai thành phần nòng cốt của module:
1. `com.pow.simulator.validation.BlockchainValidator`: Bộ kiểm toán mật mã và quy tắc đồng thuận Nakamoto.
2. `com.pow.simulator.attack.AttackSimulator`: Công cụ mô phỏng 3 giai đoạn tấn công giả mạo và phân tích độ phức tạp tính toán để viết lại chuỗi.

---

## 2. Quy Trình Kiểm Toán Của `BlockchainValidator`

Mỗi khi kiểm tra một chuỗi khối `Blockchain`, hàm `validate(chain)` sẽ thực thi tuần tự 6 bước thẩm định mật mã nghiêm ngặt:

```
[Genesis Block #0] -> [Index Continuity] -> [Temporal Monotonicity] -> [Hash Linkage] -> [Merkle Root] -> [PoW Target]
```

### Bước 1: Thẩm định khối khởi nguyên (Genesis Block #0)
- `index == 0`
- `previousHash` bắt buộc phải là chuỗi 64 ký tự `0` (`0000000000000000...0000000000000000`).
- Merkle Root tính toán từ danh sách giao dịch khởi nguyên phải khớp tuyệt đối với `genesis.merkleRoot()`.
- Hash tính toán lại từ header phải khớp chính xác với `genesis.hash()`.

### Bước 2: Kiểm tra tính liên tục của chỉ số (Index Continuity)
- Với mọi khối $i \ge 1$: `block.index() == i`.
- Ngăn chặn kẻ tấn công bỏ bớt khối hoặc chèn khối không theo thứ tự vị trí.

### Bước 3: Kiểm tra nghịch lý thời gian (Temporal Monotonicity)
- `currBlock.timestamp() >= prevBlock.timestamp()`.
- Ngăn chặn việc làm giả timestamp về quá khứ nhằm thao túng thuật toán điều chỉnh độ khó (`DifficultyAdjuster`).

### Bước 4: Kiểm tra tính liên kết chuỗi mật mã (Cryptographic Hash Linkage)
- `currBlock.previousHash().equals(prevBlock.hash())`.
- Đảm bảo mỗi khối gắn chặt vào lịch sử khối liền trước. Bất kỳ sự thay đổi nào ở khối trước đều làm đứt gãy liên kết này.

### Bước 5: Kiểm tra tính toàn vẹn dữ liệu giao dịch (Merkle Tree Root Integrity)
- Tính toán lại Merkle Root từ danh sách giao dịch hiện có: `MerkleTree.calculateRoot(currBlock.transactions())`.
- So sánh với `currBlock.merkleRoot()` lưu trên Header.
- Nếu kẻ tấn công thay đổi dù chỉ 1 ký tự hoặc số tiền giao dịch, Merkle Root sẽ đổi khác ngay lập tức và bị chặn ở bước này.

### Bước 6: Thẩm định thỏa mãn điều kiện độ khó Proof-of-Work
- Header của khối được băm lại bằng SHA-256: `currBlock.calculateHash(currBlock.nonce()) == currBlock.hash()`.
- Thẩm định độ khó qua `TargetMatcher.matches(currBlock.hash(), currBlock.difficultyBits())`.
- Hash phải có đủ số bit 0 ở đầu theo yêu cầu độ khó tại thời điểm khai thác.

---

## 3. Mô Phỏng Tấn Công Giả Mạo (`AttackSimulator`)

`AttackSimulator.simulateTamperAttack(Blockchain chain, int targetBlockIndex)` thực hiện mô phỏng kịch bản kẻ tấn công muốn sửa đổi giao dịch tại khối `#k` và phân tích kết quả qua 3 giai đoạn:

| Giai Đoạn | Hành Vi Kẻ Tấn Công | Kết Quả Thẩm Định | Cơ Chế Phát Hiện |
| :--- | :--- | :---: | :--- |
| **Pha 1: Sửa Payload** | Sửa danh sách giao dịch (chuyển tiền về ví hacker) nhưng giữ nguyên Merkle Root cũ trên header. | **BLOCKED** | Bị bắt tại Bước 5: Merkle Root tính toán từ payload không khớp Merkle Root trên header. |
| **Pha 2: Sửa Header Không Đào** | Tính lại Merkle Root mới và gắn vào header, tính hash khối mới nhưng không tìm Nonce PoW hợp lệ. | **BLOCKED** | Bị bắt tại Bước 6: Hash khối không thỏa mãn yêu cầu số bit 0 mục tiêu (`TargetMatcher`). |
| **Pha 3: Tự Đào Lại Khối Nạn Nhân** | Hacker dùng phần cứng đào lại thành công PoW hợp lệ cho Khối `#k`. | **BLOCKED** | Bị bắt tại Bước 4 ở Khối `#k+1`: Do hash Khối `#k` đã thay đổi, Khối `#k+1` kế tiếp có `previousHash` trỏ vào hash cũ -> **Hiệu ứng sụp đổ dây chuyền (Cascading Downstream Invalidation)** làm vô hiệu toàn bộ chuỗi từ `#k+1` đến ngọn. |

---

## 4. Phân Tích Chi Phí Tính Toán Viết Lại Sổ Cái (51% Attack Cost)

Để cuộc tấn công thành công và được mạng ngang hàng chấp nhận theo luật chuỗi dài nhất (**Nakamoto Longest Chain Rule**), kẻ tấn công buộc phải đào lại Khối `#k` **và toàn bộ tất cả các khối từ `#k+1` cho đến đỉnh của chuỗi**.

Tổng số phép thử băm SHA-256 ước lượng mà kẻ tấn công phải thực hiện:
$$\text{Total Hashes} \approx \sum_{i = k}^{N} 2^{D_i}$$

*Trong đó:*
- $k$: Vị trí khối bị sửa đổi.
- $N$: Chiều cao khối hiện tại của blockchain.
- $D_i$: Độ khó (số bit 0 yêu cầu) tại khối $i$.

Do đó, một khối càng lùi sâu vào lịch sử (càng có nhiều khối con xác nhận phía sau), chi phí năng lượng và năng lực tính toán để viết lại lịch sử càng tiến tới mức bất khả thi về mặt vật lý nếu không nắm giữ quá 51% hashrate của toàn mạng.

---

## 5. Danh Mục Kiểm Thử Tự Động (JUnit 5 Suite)

| Tên Test Class | Số Lượng Tests | Mục Tiêu Kiểm Thử |
| :--- | :---: | :--- |
| `TamperDetectionTest` | 6 tests | Kiểm thử phát hiện sửa payload, sửa hash, bẻ gãy linkage, vi phạm timestamp, và sửa khối Genesis. |
| `BlockMiningTest` | 4 tests | Kiểm thử đào khối thỏa mãn difficulty bits và tính Merkle Root chuẩn Bitcoin. |
| `DifficultyAdjustmentTest` | 5 tests | Kiểm thử điều chỉnh độ khó Epoch Retargeting, hệ số giới hạn clamping $[0.25\times, 4.0\times]$. |
| `ConcurrencyTest` | 3 tests | Kiểm thử khai thác đồng thời đa luồng (Java 21 Virtual Threads) và cơ chế ngắt an toàn khi tìm thấy nonce. |
