# 🌐 Next.js Frontend Dashboard (Mô Phỏng Mạng P2P Blockchain)

Dự án này là giao diện Web trực quan hóa mô hình mạng ngang hàng P2P Blockchain cho môn học **Lập Trình Mạng (Network Programming)**.

## 🏗️ Kiến Trúc Hệ Thống
- **Core Engine & Networking**: Viết bằng **Java 21**, vận hành các tiến trình P2P qua `ServerSocket` & `Socket` nguyên bản trên các cổng TCP `8001, 8002, 8003, 8004`, đa luồng bằng **Java 21 Virtual Threads**.
- **Embedded Web Server**: Lớp cầu nối HTTP REST API (`NodeApiServer.java`) mở tại cổng `http://localhost:8080`.
- **Frontend Dashboard**:
  1. **Tùy chọn 1 (Khuyên dùng - Tiện lợi nhất, 0 cần cài đặt)**: Chạy file `run_gui.bat` tại thư mục gốc. Hệ thống Java sẽ tự động khởi chạy và mở ngay Dashboard trực quan tại `http://localhost:8080`.
  2. **Tùy chọn 2 (Chạy dự án Next.js độc lập)**: Chạy ứng dụng Next.js này (port 3000) kết nối tới lõi Java (port 8080).

---

## 🚀 Hướng Dẫn Chạy Next.js (Tùy chọn 2)

### Bước 1: Khởi động Backend Java
Chạy `run_gui.bat` ở thư mục gốc để mở cổng REST API `8080`.

### Bước 2: Cài đặt và chạy Next.js
Mở terminal trong thư mục `frontend-nextjs/`:
```bash
npm install
npm run dev
```

Mở trình duyệt truy cập: **http://localhost:3000**

Mọi thao tác bấm nút trên giao diện Next.js (Gửi Tx, Khai thác PoW, Kết nối Node D, Thử tấn công giả mạo) sẽ được chuyển tiếp tức thì về hệ thống mạng TCP Sockets Java phía dưới.
