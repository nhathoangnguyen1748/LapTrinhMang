package com.pow.simulator.web;

/**
 * Giao diện Bỏ Phiếu Cử Tri Điện Tử Độc Lập (Dedicated Desktop-First Voter Portal).
 * Thiết kế tối ưu UI/UX cao cấp cho màn hình Desktop:
 * - Tập trung 100% vào trải nghiệm bỏ phiếu, không gây xao nhãng.
 * - Hoàn toàn không chứa nút bấm hay đường dẫn sang trang /nodes hay /explorer.
 * - Loại bỏ hoàn toàn các icon / emoji rườm rà.
 * - Phân cấp thông tin rõ ràng, phông chữ chuyên nghiệp, tương tác mượt mà.
 */
public final class VoterHtmlTemplate {

    private VoterHtmlTemplate() {}

    public static String getHtml() {
        return """
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cổng Bỏ Phiếu Cử Tri Điện Tử</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&family=JetBrains+Mono:wght@400;500;600&display=swap');
        
        body {
            font-family: 'Plus Jakarta Sans', -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
            background: linear-gradient(180deg, #f8fafc 0%, #f1f5f9 100%);
        }
        
        .font-mono {
            font-family: 'JetBrains Mono', monospace;
        }

        .candidate-card {
            transition: all 0.2s cubic-bezier(0.16, 1, 0.3, 1);
        }

        .candidate-card:hover {
            border-color: #94a3b8;
            transform: translateY(-1px);
        }

        .candidate-card.is-selected {
            border-color: #0f172a !important;
            background-color: #f8fafc !important;
            box-shadow: 0 4px 20px -2px rgba(15, 23, 42, 0.08);
        }

        .radio-dot {
            transition: all 0.2s cubic-bezier(0.16, 1, 0.3, 1);
        }

        .candidate-card.is-selected .radio-circle {
            border-color: #0f172a;
        }

        .candidate-card.is-selected .radio-dot {
            transform: scale(1);
            background-color: #0f172a;
        }
    </style>
</head>
<body class="min-h-screen flex flex-col justify-between text-slate-800 antialiased selection:bg-slate-900 selection:text-white">

    <!-- THANH TIÊU ĐỀ HỆ THỐNG (CHUYÊN BIỆT CHO CỬ TRI, KHÔNG ĐIỀU HƯỚNG RA NGOÀI) -->
    <header class="border-b border-slate-200/80 bg-white/80 backdrop-blur-md sticky top-0 z-30">
        <div class="max-w-4xl mx-auto px-6 py-4 flex items-center justify-between">
            <div class="flex items-center gap-3">
                <div class="w-8 h-8 rounded-lg bg-slate-900 text-white flex items-center justify-center font-bold text-xs tracking-wider">
                    BC
                </div>
                <div>
                    <span class="text-[10px] font-bold tracking-widest text-slate-400 uppercase block">Cổng Bỏ Phiếu Trực Tuyến</span>
                    <span class="text-sm font-extrabold text-slate-900 tracking-tight">Hệ Thống Bầu Cử Điện Tử</span>
                </div>
            </div>

            <!-- CHỈ BÁO BẢO MẬT (KHÔNG CÓ ĐƯỜNG DẪN RA NGOÀI) -->
            <div class="flex items-center gap-2 text-xs font-medium text-slate-600 bg-slate-100 border border-slate-200/80 px-3 py-1.5 rounded-full">
                <span class="w-2 h-2 rounded-full bg-emerald-500 animate-pulse"></span>
                <span>Hòm phiếu trực tuyến đang mở</span>
            </div>
        </div>
    </header>

    <!-- NỘI DUNG CHÍNH (ĐƯỢC CĂN GIỮA ĐẸP MẮT TRÊN MÀN HÌNH DESKTOP) -->
    <main class="max-w-xl mx-auto px-4 py-8 md:py-12 flex-1 w-full flex flex-col justify-center">

        <!-- THẺ FORM BỎ PHIẾU CHÍNH THỨC -->
        <div id="votingFormCard" class="bg-white border border-slate-200/90 rounded-3xl p-7 sm:p-10 shadow-[0_20px_50px_-15px_rgba(15,23,42,0.06)] space-y-7">
            
            <!-- HEADER BÀI BẦU CỬ -->
            <div class="border-b border-slate-100 pb-5">
                <span class="text-[11px] font-bold tracking-widest text-slate-400 uppercase">Phiếu Biểu Quyết Đại Biểu</span>
                <h1 class="text-xl sm:text-2xl font-extrabold text-slate-900 tracking-tight mt-1">Đại Hội Đại Biểu Sinh Viên</h1>
                <p class="text-xs text-slate-500 mt-1.5 leading-relaxed">
                    Mỗi cử tri có quyền biểu quyết cho 01 đại biểu tín nhiệm. Dữ liệu phiếu bầu được mã hóa bảo mật và xác thực tự động trên hệ thống.
                </p>
            </div>

            <!-- MỤC 1: NHẬP MÃ CỬ TRI -->
            <div class="space-y-2">
                <label for="voterIdInput" class="block text-xs font-bold text-slate-700 uppercase tracking-wider">
                    Mã định danh cử tri (MSSV / CCCD) <span class="text-red-500">*</span>
                </label>
                <input type="text" id="voterIdInput" placeholder="Ví dụ: 2380601549..." autocomplete="off" spellcheck="false"
                       class="w-full bg-white border border-slate-300 rounded-xl px-4 py-3.5 text-sm text-slate-900 placeholder-slate-400 focus:outline-none focus:border-slate-900 focus:ring-2 focus:ring-slate-900/10 transition font-mono tracking-wide">
                <p class="text-[11px] text-slate-400">
                    Nhập chính xác mã sinh viên hoặc số định danh cá nhân đã được ghi danh.
                </p>
            </div>

            <!-- MỤC 2: CHỌN ĐẠI BIỂU TÍN NHIỆM -->
            <div class="space-y-2.5">
                <div class="flex items-center justify-between">
                    <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider">
                        Chọn người bỏ phiếu <span class="text-red-500">*</span>
                    </label>
                    <span class="text-[11px] text-slate-400 font-medium">Bỏ phiếu cho 01 ứng viên</span>
                </div>

                <div class="space-y-3" id="candidatesGroup">
                    
                    <!-- Ứng cử viên 1 -->
                    <div class="candidate-card border border-slate-200 rounded-2xl p-4 sm:p-4.5 cursor-pointer flex items-center justify-between select-none"
                         onclick="selectCandidate('Le Quang Minh', this)">
                        <div class="flex items-center gap-4">
                            <span class="w-8 h-8 rounded-lg bg-slate-100 border border-slate-200 flex items-center justify-center text-xs font-mono font-bold text-slate-600">
                                01
                            </span>
                            <div>
                                <div class="text-sm sm:text-base font-bold text-slate-900">Lê Quang Minh</div>
                                <div class="text-xs text-slate-500 mt-0.5">Khoa Công Nghệ Thông Tin</div>
                            </div>
                        </div>
                        <div class="radio-circle w-5 h-5 rounded-full border-2 border-slate-300 flex items-center justify-center">
                            <span class="radio-dot w-2.5 h-2.5 rounded-full scale-0"></span>
                        </div>
                    </div>

                    <!-- Ứng cử viên 2 -->
                    <div class="candidate-card border border-slate-200 rounded-2xl p-4 sm:p-4.5 cursor-pointer flex items-center justify-between select-none"
                         onclick="selectCandidate('Nguyen Do Trung Luong', this)">
                        <div class="flex items-center gap-4">
                            <span class="w-8 h-8 rounded-lg bg-slate-100 border border-slate-200 flex items-center justify-center text-xs font-mono font-bold text-slate-600">
                                02
                            </span>
                            <div>
                                <div class="text-sm sm:text-base font-bold text-slate-900">Nguyễn Đỗ Trung Lương</div>
                                <div class="text-xs text-slate-500 mt-0.5">Ban Học Tập & Nghiên Cứu</div>
                            </div>
                        </div>
                        <div class="radio-circle w-5 h-5 rounded-full border-2 border-slate-300 flex items-center justify-center">
                            <span class="radio-dot w-2.5 h-2.5 rounded-full scale-0"></span>
                        </div>
                    </div>

                    <!-- Ứng cử viên 3 -->
                    <div class="candidate-card border border-slate-200 rounded-2xl p-4 sm:p-4.5 cursor-pointer flex items-center justify-between select-none"
                         onclick="selectCandidate('Nguyen Hoang Nhat', this)">
                        <div class="flex items-center gap-4">
                            <span class="w-8 h-8 rounded-lg bg-slate-100 border border-slate-200 flex items-center justify-center text-xs font-mono font-bold text-slate-600">
                                03
                            </span>
                            <div>
                                <div class="text-sm sm:text-base font-bold text-slate-900">Nguyễn Hoàng Nhật</div>
                                <div class="text-xs text-slate-500 mt-0.5">Ban Phong Trào & Thể Thao</div>
                            </div>
                        </div>
                        <div class="radio-circle w-5 h-5 rounded-full border-2 border-slate-300 flex items-center justify-center">
                            <span class="radio-dot w-2.5 h-2.5 rounded-full scale-0"></span>
                        </div>
                    </div>

                </div>
                <input type="hidden" id="selectedCandidate" value="">
            </div>

            <!-- THÔNG BÁO LỖI NẾU CÓ -->
            <div id="errorMessage" class="hidden text-xs text-rose-700 bg-rose-50 border border-rose-200 rounded-xl p-3.5 font-medium leading-relaxed"></div>

            <!-- NÚT GỬI PHIẾU BẦU -->
            <div class="space-y-3 pt-2">
                <button type="button" id="btnSubmitVote" onclick="submitVote()"
                        class="w-full py-4 px-6 bg-slate-900 hover:bg-slate-800 text-white font-bold text-sm rounded-2xl shadow-sm hover:shadow-md transition-all active:scale-[0.99] cursor-pointer">
                    Xác nhận bỏ phiếu
                </button>
                <p class="text-center text-[11px] text-slate-400">
                    Phiếu bầu được tiếp nhận tự động vào mạng lưới và bảo đảm tính ẩn danh minh bạch.
                </p>
            </div>

        </div>

        <!-- THẺ KẾT QUẢ / BIÊN NHẬN ĐIỆN TỬ (CHỈ HIỂN THỊ KHI BỎ PHIẾU THÀNH CÔNG) -->
        <div id="voteReceiptCard" class="hidden bg-white border border-slate-200/90 rounded-3xl p-7 sm:p-10 shadow-[0_20px_50px_-15px_rgba(15,23,42,0.06)] space-y-6">
            
            <div class="border-b border-slate-100 pb-4">
                <div class="inline-flex items-center gap-1.5 text-[11px] font-bold text-emerald-700 bg-emerald-50 border border-emerald-200 px-3 py-1 rounded-full uppercase tracking-wider mb-2">
                    <span class="w-1.5 h-1.5 rounded-full bg-emerald-600"></span>
                    Tiếp nhận thành công
                </div>
                <h2 class="text-xl font-extrabold text-slate-900">Biên Nhận Bỏ Phiếu Điện Tử</h2>
                <p class="text-xs text-slate-500 mt-1">
                    Phiếu bầu của bạn đã được ký số và ghi nhận vào hàng đợi của hệ thống.
                </p>
            </div>

            <!-- BẢNG CHI TIẾT PHIẾU -->
            <div class="bg-slate-50 border border-slate-200/90 rounded-2xl p-5 space-y-3.5 text-xs font-mono">
                <div class="flex items-center justify-between border-b border-slate-200/70 pb-2.5">
                    <span class="text-slate-500 font-sans">Mã cử tri:</span>
                    <span id="receiptVoter" class="text-slate-900 font-bold text-sm"></span>
                </div>
                <div class="flex items-center justify-between border-b border-slate-200/70 pb-2.5">
                    <span class="text-slate-500 font-sans">Đại biểu được chọn:</span>
                    <span id="receiptCandidate" class="text-slate-900 font-bold text-sm"></span>
                </div>
                <div class="flex items-center justify-between border-b border-slate-200/70 pb-2.5">
                    <span class="text-slate-500 font-sans">Mã giao dịch (TxID):</span>
                    <span id="receiptTxId" class="text-slate-700 font-bold"></span>
                </div>
                <div class="flex items-center justify-between pt-1">
                    <span class="text-slate-500 font-sans">Trạng thái phiếu:</span>
                    <span class="text-emerald-700 font-bold">Hợp lệ &bull; Đã vào hàng đợi</span>
                </div>
            </div>

            <!-- CHỈ DUY NHẤT 1 NÚT QUAY LẠI BỎ PHIẾU LƯỢT TIẾP THEO (KHÔNG LIÊN KẾT RA NGOÀI) -->
            <div class="pt-2">
                <button type="button" onclick="voteAnother()"
                        class="w-full py-3.5 px-6 bg-slate-900 hover:bg-slate-800 text-white text-xs font-bold rounded-2xl transition shadow-xs text-center cursor-pointer">
                    Bỏ phiếu cho cử tri tiếp theo
                </button>
            </div>
        </div>

    </main>

    <!-- FOOTER TRANG BẦU CỬ ĐỘC LẬP -->
    <footer class="border-t border-slate-200/80 bg-white/60 py-4 text-center text-xs text-slate-400">
        Hệ Thống Bầu Cử Điện Tử Trực Tuyến &bull; Đại Học HUTECH
    </footer>

    <!-- SCRIPT ĐIỀU KHIỂN LOGIC -->
    <script>
        let selectedCandidateName = '';

        function selectCandidate(name, element) {
            selectedCandidateName = name;
            document.getElementById('selectedCandidate').value = name;

            document.querySelectorAll('.candidate-card').forEach(card => {
                card.classList.remove('is-selected');
            });

            element.classList.add('is-selected');
            hideError();
        }

        function showError(msg) {
            const errEl = document.getElementById('errorMessage');
            errEl.textContent = msg;
            errEl.classList.remove('hidden');
        }

        function hideError() {
            const errEl = document.getElementById('errorMessage');
            errEl.classList.add('hidden');
        }

        async function submitVote() {
            const voterId = document.getElementById('voterIdInput').value.trim();
            const candidate = selectedCandidateName;

            if (!voterId) {
                showError('Vui lòng nhập mã định danh cử tri của bạn.');
                document.getElementById('voterIdInput').focus();
                return;
            }

            if (!candidate) {
                showError('Vui lòng chọn 01 đại biểu bạn tín nhiệm.');
                return;
            }

            hideError();
            const btn = document.getElementById('btnSubmitVote');
            btn.disabled = true;
            btn.textContent = 'Đang xử lý và gửi phiếu bầu...';

            try {
                const res = await fetch('/api/election/vote', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ voterId: voterId, candidate: candidate })
                });
                const data = await res.json();

                if (data.success) {
                    document.getElementById('votingFormCard').classList.add('hidden');
                    const receipt = document.getElementById('voteReceiptCard');
                    receipt.classList.remove('hidden');

                    document.getElementById('receiptVoter').textContent = voterId;
                    document.getElementById('receiptCandidate').textContent = candidate;
                    document.getElementById('receiptTxId').textContent = '0x' + Math.random().toString(16).substring(2, 10).toUpperCase();
                } else {
                    showError(data.error || 'Yêu cầu bỏ phiếu không hợp lệ.');
                }
            } catch (err) {
                showError('Lỗi kết nối tới máy chủ: ' + err.message);
            } finally {
                btn.disabled = false;
                btn.textContent = 'Xác nhận bỏ phiếu';
            }
        }

        function voteAnother() {
            document.getElementById('votingFormCard').classList.remove('hidden');
            document.getElementById('voteReceiptCard').classList.add('hidden');
            document.getElementById('voterIdInput').value = '';
            selectedCandidateName = '';
            document.getElementById('selectedCandidate').value = '';
            document.querySelectorAll('.candidate-card').forEach(card => {
                card.classList.remove('is-selected');
            });
            hideError();
            document.getElementById('voterIdInput').focus();
        }
    </script>
</body>
</html>
""";
    }
}
