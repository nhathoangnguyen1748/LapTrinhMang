package com.pow.simulator.web;

/**
 * Giao diện Quản Trị Hệ Thống Bầu Cử Điện Tử Blockchain (E-Voting Dashboard).
 * Phong cách Minimalist White / Light Theme sang trọng, tinh tế, chuẩn UI/UX.
 * Hiển thị trực quan:
 * 1. Thống kê hệ thống & Bộ chọn độ khó PoW (1 - 6 số 0)
 * 2. Cổng Bỏ Phiếu Cử Tri & Hòm Phiếu Chờ Mempool trên cụm 3 Nodes P2P
 * 3. Nút Khai Thác PoW Đóng Khối
 * 4. Sổ Cái Chuỗi Khối với đúng 5 thuộc tính: Hash, Time, Pre Hash, Nội dung, Nonce
 * 5. Công cụ kiểm thử bảo mật (Can thiệp sửa đổi khối) & Nhật ký P2P Realtime
 */
public final class DashboardHtmlTemplate {

    private DashboardHtmlTemplate() {}

    public static String getHtml() {
        StringBuilder sb = new StringBuilder(50000);
        sb.append(getHeadHtml());
        sb.append(getHeaderHtml());
        sb.append(getVotingBoothHtml());
        sb.append(getMempoolAndResultsHtml());
        sb.append(getLedgerAndLogsHtml());
        sb.append(getScriptsHtml());
        return sb.toString();
    }

    private static String getHeadHtml() {
        return """
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Bảng Điều Khiển Sổ Cái Bầu Cử Blockchain - E-Voting</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&family=JetBrains+Mono:wght@400;500;600;700&display=swap');
        body { font-family: 'Plus Jakarta Sans', -apple-system, BlinkMacSystemFont, sans-serif; }
        .font-mono { font-family: 'JetBrains Mono', monospace; }
        ::-webkit-scrollbar { width: 6px; height: 6px; }
        ::-webkit-scrollbar-track { background: #f1f5f9; }
        ::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 4px; }
        ::-webkit-scrollbar-thumb:hover { background: #94a3b8; }
        .candidate-card { transition: all 0.2s ease; }
        .candidate-card:hover { transform: translateY(-2px); border-color: #6366f1; }
        .glow-danger { box-shadow: 0 0 20px rgba(239, 68, 68, 0.2); }
    </style>
</head>
<body class="bg-slate-50 text-slate-800 antialiased min-h-screen p-3 md:p-6 selection:bg-indigo-600 selection:text-white">
    <div class="max-w-7xl mx-auto space-y-6">
""";
    }

    private static String getHeaderHtml() {
        return """
        <!-- HEADER -->
        <header class="bg-white border border-slate-200/90 rounded-2xl p-4 md:p-6 flex flex-col lg:flex-row items-center justify-between gap-4 shadow-xs">
            <div class="flex items-center gap-4">
                <div class="w-13 h-13 rounded-2xl bg-indigo-600 text-white flex items-center justify-center text-2xl shadow-md shadow-indigo-600/20 shrink-0">
                    🗳️
                </div>
                <div>
                    <div class="flex items-center gap-2">
                        <h1 class="text-xl md:text-2xl font-extrabold text-slate-900 tracking-tight">
                            HỆ THỐNG BẦU CỬ ĐIỆN TỬ BLOCKCHAIN (E-VOTING)
                        </h1>
                        <span class="px-2.5 py-0.5 text-xs font-bold bg-emerald-50 text-emerald-700 border border-emerald-200 rounded-full flex items-center gap-1">
                            <span class="w-2 h-2 rounded-full bg-emerald-500 animate-pulse"></span> TRỰC TUYẾN
                        </span>
                    </div>
                    <p class="text-xs text-slate-500 mt-1 font-medium">
                        Đồ án Lập Trình Mạng &bull; Nhóm: <strong class="text-indigo-600">Nguyễn Hoàng Nhật</strong> (Lead) &bull; <strong class="text-indigo-600">Nguyễn Đỗ Trung Lương</strong> &bull; <strong class="text-indigo-600">Lê Quang Minh</strong>
                    </p>
                </div>
            </div>

            <!-- STATS BADGES -->
            <div class="flex flex-wrap items-center gap-2.5">
                <div class="px-3 py-1.5 bg-slate-50 border border-slate-200 rounded-xl text-center">
                    <div class="text-[10px] text-slate-500 uppercase font-mono font-medium tracking-wider">Số Khối (Blocks)</div>
                    <div id="statBlocks" class="text-base font-extrabold text-indigo-600">0</div>
                </div>
                <div class="px-3 py-1.5 bg-slate-50 border border-slate-200 rounded-xl text-center">
                    <div class="text-[10px] text-slate-500 uppercase font-mono font-medium tracking-wider">Phiếu Đã Đóng Khối</div>
                    <div id="statTotalVotes" class="text-base font-extrabold text-emerald-600">0</div>
                </div>
                <div class="px-3 py-1.5 bg-slate-50 border border-slate-200 rounded-xl text-center">
                    <div class="text-[10px] text-slate-500 uppercase font-mono font-medium tracking-wider">Phiếu Trong Mempool</div>
                    <div id="statMempool" class="text-base font-extrabold text-amber-600">0</div>
                </div>
                
                <!-- BỘ CHỌN ĐỘ KHÓ POW -->
                <div class="px-3 py-1.5 bg-amber-50/60 border border-amber-200 rounded-xl text-center flex flex-col justify-center">
                    <div class="text-[10px] text-amber-800 uppercase font-mono font-bold tracking-wider mb-0.5 flex items-center justify-center gap-1">
                        <span>🎯</span> Độ Khó PoW (Tuỳ Chỉnh)
                    </div>
                    <div class="flex items-center gap-1 justify-center">
                        <select id="difficultySelect" onchange="changeDifficulty(this.value)"
                                class="bg-white border border-amber-300 rounded-lg px-2 py-0.5 text-xs font-mono font-bold text-amber-900 focus:outline-none focus:border-amber-500 cursor-pointer shadow-2xs">
                            <option value="1">1 số 0 (0... ~1ms)</option>
                            <option value="2">2 số 0 (00... ~5ms)</option>
                            <option value="3">3 số 0 (000... ~30ms)</option>
                            <option value="4">4 số 0 (0000... ~100ms)</option>
                            <option value="5" selected>5 số 0 (00000... ~400ms)</option>
                            <option value="6">6 số 0 (000000... ~3s)</option>
                        </select>
                    </div>
                </div>

                <div id="chainStatusBadge" class="px-3.5 py-2 bg-emerald-50 border border-emerald-200 rounded-xl text-center">
                    <div class="text-[10px] text-emerald-700 uppercase font-mono font-medium tracking-wider">Trạng Thái Sổ Cái</div>
                    <div id="statIntegrity" class="text-xs font-extrabold text-emerald-800 flex items-center gap-1 justify-center">
                        <span>🛡️</span> AN TOÀN 100%
                    </div>
                </div>

                <a href="/vote" target="_blank" class="px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl text-xs font-extrabold shadow-sm shadow-indigo-600/20 flex items-center gap-1.5 transition-all transform hover:scale-[1.02]">
                    <span>🗳️</span> PHÒNG BỎ PHIẾU CỬ TRI (/vote)
                </a>
            </div>
        </header>

        <!-- CẢNH BÁO GIAN LẬN NẾU CÓ -->
        <div id="tamperAlertBanner" class="hidden bg-rose-50 border border-rose-300 text-rose-800 p-4 rounded-xl flex items-center justify-between shadow-sm glow-danger">
            <div class="flex items-center gap-3">
                <span class="text-2xl">🚨</span>
                <div>
                    <h3 class="font-extrabold text-sm text-rose-900">CẢNH BÁO: PHÁT HIỆN GIAN LẬN SỬA ĐỔI NỘI DUNG SỔ CÁI!</h3>
                    <p id="tamperReasonText" class="text-xs text-rose-700 mt-0.5 font-mono"></p>
                </div>
            </div>
            <button onclick="resetElection()" class="px-3.5 py-1.5 bg-rose-600 hover:bg-rose-700 text-white font-bold rounded-lg text-xs transition">
                Phục Hồi Sổ Cái
            </button>
        </div>
""";
    }

    private static String getVotingBoothHtml() {
        return """
        <!-- SECTION 1: CỔNG BỎ PHIẾU NHANH TRÊN ADMIN -->
        <section class="bg-white border border-slate-200/90 rounded-2xl p-5 md:p-6 shadow-xs space-y-4">
            <div class="flex items-center justify-between border-b border-slate-100 pb-3">
                <div class="flex items-center gap-2">
                    <span class="text-xl">🗳️</span>
                    <div>
                        <h2 class="text-base font-extrabold text-slate-900">1. CỔNG BỎ PHIẾU NHANH CỦA CỬ TRI</h2>
                        <p class="text-xs text-slate-500 font-medium">Bỏ phiếu thử nghiệm nhanh hoặc dùng phòng cử tri riêng biệt tại <a href="/vote" target="_blank" class="text-indigo-600 font-bold underline">/vote</a></p>
                    </div>
                </div>
                <span class="text-xs px-2.5 py-1 bg-indigo-50 text-indigo-700 border border-indigo-200 rounded-lg font-mono font-semibold">
                    1 cử tri &bull; 1 phiếu bầu duy nhất
                </span>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-12 gap-5">
                <!-- NHẬP TÊN CỬ TRI -->
                <div class="md:col-span-5 space-y-2.5">
                    <label class="block text-xs font-bold text-slate-700">
                        Họ Tên / Mã Số Cử Tri (Voter ID):
                    </label>
                    <input type="text" id="voterInput" placeholder="Ví dụ: SV001 - Nguyễn Hoàng Nhật"
                           class="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-sm font-semibold text-slate-900 placeholder-slate-400 focus:bg-white focus:outline-none focus:border-indigo-600 focus:ring-4 focus:ring-indigo-100 transition font-mono">
                    
                    <div>
                        <div class="text-[11px] text-slate-500 mb-1 font-medium">Bấm chọn nhanh cử tri mẫu:</div>
                        <div class="flex flex-wrap gap-1.5">
                            <button onclick="selectVoter('2380601549')" class="px-2 py-0.5 text-xs bg-slate-100 hover:bg-slate-200 text-slate-700 rounded-md border border-slate-200 transition font-mono font-medium">2380601549</button>
                            <button onclick="selectVoter('2380601305')" class="px-2 py-0.5 text-xs bg-slate-100 hover:bg-slate-200 text-slate-700 rounded-md border border-slate-200 transition font-mono font-medium">2380601305</button>
                            <button onclick="selectVoter('2380601342')" class="px-2 py-0.5 text-xs bg-slate-100 hover:bg-slate-200 text-slate-700 rounded-md border border-slate-200 transition font-mono font-medium">2380601342</button>
                        </div>
                    </div>
                </div>

                <!-- CHỌN ỨNG CỬ VIÊN -->
                <div class="md:col-span-7 space-y-2.5">
                    <label class="block text-xs font-bold text-slate-700">
                        Chọn Ứng Cử Viên Đại Biểu:
                    </label>
                    <div class="grid grid-cols-1 sm:grid-cols-3 gap-3">
                        <!-- CANDIDATE 1 -->
                        <div onclick="selectCandidate(0)" id="cardCand0"
                             class="candidate-card cursor-pointer bg-white border-2 border-slate-200 rounded-xl p-3 text-center transition space-y-1.5 shadow-2xs">
                            <div class="w-10 h-10 mx-auto rounded-full bg-sky-50 text-sky-600 border border-sky-100 flex items-center justify-center text-lg shadow-2xs">
                                👨‍💼
                            </div>
                            <div class="font-bold text-xs text-slate-900">Lê Quang Minh</div>
                            <div class="text-[10px] text-sky-600 font-medium">Đại biểu Khoa CNTT</div>
                        </div>

                        <!-- CANDIDATE 2 -->
                        <div onclick="selectCandidate(1)" id="cardCand1"
                             class="candidate-card cursor-pointer bg-white border-2 border-slate-200 rounded-xl p-3 text-center transition space-y-1.5 shadow-2xs">
                            <div class="w-10 h-10 mx-auto rounded-full bg-emerald-50 text-emerald-600 border border-emerald-100 flex items-center justify-center text-lg shadow-2xs">
                                👨‍🎓
                            </div>
                            <div class="font-bold text-xs text-slate-900">Nguyễn Đỗ Trung Lương</div>
                            <div class="text-[10px] text-emerald-600 font-medium">Ban Học Tập</div>
                        </div>

                        <!-- CANDIDATE 3 -->
                        <div onclick="selectCandidate(2)" id="cardCand2"
                             class="candidate-card cursor-pointer bg-white border-2 border-slate-200 rounded-xl p-3 text-center transition space-y-1.5 shadow-2xs">
                            <div class="w-10 h-10 mx-auto rounded-full bg-amber-50 text-amber-600 border border-amber-100 flex items-center justify-center text-lg shadow-2xs">
                                👨‍💻
                            </div>
                            <div class="font-bold text-xs text-slate-900">Nguyễn Hoàng Nhật</div>
                            <div class="text-[10px] text-amber-600 font-medium">Ban Phong Trào</div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- NÚT GỬI PHIẾU BẦU -->
            <div class="pt-2 border-t border-slate-100 flex flex-col sm:flex-row items-center justify-between gap-3">
                <div class="text-xs text-slate-500 font-medium flex items-center gap-1.5">
                    <span>💡</span> Phiếu sẽ lập tức phát tán qua P2P vào <strong>Mempool của cả 3 Nodes</strong>.
                </div>
                <button onclick="submitVote()"
                        class="w-full sm:w-auto px-6 py-2.5 bg-indigo-600 hover:bg-indigo-700 text-white font-extrabold text-xs rounded-xl shadow-xs transition flex items-center justify-center gap-2">
                    <span>🗳️</span> BỎ PHIẾU BẦU CỬ (PHÁT TÁN VÀO MEMPOOL)
                </button>
            </div>
        </section>
""";
    }

    private static String getMempoolAndResultsHtml() {
        return """
        <!-- SECTION 2: HÒM PHIẾU TẠM (MEMPOOL) & KẾT QUẢ BẦU CỬ -->
        <div class="grid grid-cols-1 lg:grid-cols-12 gap-6">
            <!-- HÒM PHIẾU MEMPOOL -->
            <div class="lg:col-span-5 bg-white border border-slate-200/90 rounded-2xl p-5 shadow-xs flex flex-col justify-between">
                <div>
                    <div class="flex items-center justify-between border-b border-slate-100 pb-3 mb-3">
                        <div class="flex items-center gap-2">
                            <span class="text-lg">⏳</span>
                            <h3 class="text-sm font-extrabold text-slate-900">2. HÒM PHIẾU TẠM (MEMPOOL)</h3>
                        </div>
                        <span id="mempoolBadgeCount" class="px-2.5 py-0.5 bg-amber-50 text-amber-800 border border-amber-200 rounded-full text-xs font-mono font-bold">
                            0 phiếu chờ
                        </span>
                    </div>

                    <!-- ĐỒNG BỘ MEMPOOL TRÊN CỤM 3 NODES P2P -->
                    <div class="mb-3 p-3 bg-slate-50 border border-slate-200 rounded-xl space-y-1.5">
                        <div class="text-[11px] font-bold text-slate-700 flex items-center justify-between">
                            <span>🌐 ĐỒNG BỘ MEMPOOL 3 NODES P2P:</span>
                            <span class="text-[9px] font-mono text-emerald-700 bg-emerald-50 px-2 py-0.5 rounded-full border border-emerald-200 font-bold">● 3/3 NODES IN SYNC</span>
                        </div>
                        <div class="grid grid-cols-3 gap-2 text-center text-[10px] font-mono">
                            <div class="bg-white p-2 rounded-lg border border-slate-200 shadow-2xs">
                                <div class="text-slate-500 font-medium truncate">Node 1 (CNTT)</div>
                                <div id="dashNode1Mem" class="text-amber-600 font-extrabold text-xs mt-0.5">0 phiếu</div>
                            </div>
                            <div class="bg-white p-2 rounded-lg border border-slate-200 shadow-2xs">
                                <div class="text-slate-500 font-medium truncate">Node 2 (Giám Sát)</div>
                                <div id="dashNode2Mem" class="text-amber-600 font-extrabold text-xs mt-0.5">0 phiếu</div>
                            </div>
                            <div class="bg-white p-2 rounded-lg border border-slate-200 shadow-2xs">
                                <div class="text-slate-500 font-medium truncate">Node 3 (Hội Đồng)</div>
                                <div id="dashNode3Mem" class="text-amber-600 font-extrabold text-xs mt-0.5">0 phiếu</div>
                            </div>
                        </div>
                    </div>

                    <div id="mempoolList" class="space-y-2 max-h-56 overflow-y-auto pr-1">
                        <!-- Danh sách phiếu trong mempool render bằng JS -->
                        <div class="text-center py-8 text-xs text-slate-400 italic font-medium">
                            Chưa có phiếu bầu nào trong Mempool. Hãy bỏ phiếu ở trên!
                        </div>
                    </div>
                </div>

                <!-- NÚT KHAI THÁC POW -->
                <div class="pt-4 border-t border-slate-100 mt-4">
                    <button onclick="mineBlock()" id="btnMine"
                            class="w-full py-3 bg-emerald-600 hover:bg-emerald-700 text-white font-extrabold text-sm rounded-xl shadow-md shadow-emerald-600/20 transition-all flex items-center justify-center gap-2 cursor-pointer">
                        <span>⛏️</span> KHAI THÁC BLOCK MỚI (PoW ĐÓNG KHỐI)
                    </button>
                    <p class="text-[11px] text-slate-500 text-center mt-2 font-medium">
                        Thợ đào sẽ gom phiếu từ Mempool, giải mã Nonce đạt độ khó và khóa sổ cái vĩnh viễn.
                    </p>
                </div>
            </div>

            <!-- KẾT QUẢ BẦU CỬ TRỰC TIẾP TỪ SỔ CÁI -->
            <div class="lg:col-span-7 bg-white border border-slate-200/90 rounded-2xl p-5 shadow-xs space-y-4">
                <div class="flex items-center justify-between border-b border-slate-100 pb-3">
                    <div class="flex items-center gap-2">
                        <span class="text-lg">📊</span>
                        <h3 class="text-sm font-extrabold text-slate-900">3. KẾT QUẢ BẦU CỬ TRỰC TIẾP (TỪ SỔ CÁI)</h3>
                    </div>
                    <span class="text-xs text-slate-500 font-medium">Tự động cập nhật khi có Block mới</span>
                </div>

                <!-- THANH TỶ LỆ PHIẾU BẦU CHO CÁC ỨNG CỬ VIÊN -->
                <div id="candidatesResultsContainer" class="space-y-3">
                    <!-- Sẽ render bằng JS -->
                </div>

                <!-- DANH SÁCH CỬ TRI ĐÃ BẦU MINH BẠCH -->
                <div class="pt-3 border-t border-slate-100">
                    <div class="flex items-center justify-between text-xs text-slate-700 font-bold mb-2">
                        <span>Danh Sách Cử Tri Đã Bỏ Phiếu (Minh Bạch):</span>
                        <span id="voterCountBadge" class="text-indigo-600 font-mono font-bold">0 người</span>
                    </div>
                    <div id="votedVotersTags" class="flex flex-wrap gap-1.5 max-h-24 overflow-y-auto">
                        <span class="text-xs text-slate-400 italic font-medium">Chưa có cử tri nào được ghi nhận vào Sổ Cái.</span>
                    </div>
                </div>
            </div>
        </div>
""";
    }

    private static String getLedgerAndLogsHtml() {
        return """
        <!-- SECTION 3: SỔ CÁI CHUỖI KHỐI (BLOCKCHAIN LEDGER) - 5 THUỘC TÍNH CHUẨN -->
        <section class="bg-white border border-slate-200/90 rounded-2xl p-5 md:p-6 shadow-xs space-y-4">
            <div class="flex flex-col md:flex-row items-start md:items-center justify-between gap-3 border-b border-slate-100 pb-3">
                <div class="flex items-center gap-2">
                    <span class="text-xl">🔗</span>
                    <div>
                        <h3 class="text-base font-extrabold text-slate-900">4. SỔ CÁI CHUỖI KHỐI (BLOCKCHAIN LEDGER)</h3>
                        <p class="text-xs text-slate-500 font-medium">Mỗi khối gồm đúng 5 thuộc tính: <strong>Hash &bull; Time &bull; Pre Hash &bull; Nội dung &bull; Nonce</strong></p>
                    </div>
                </div>

                <!-- CÁC NÚT THAO TÁC SỔ CÁI -->
                <div class="flex items-center gap-2">
                    <button onclick="tamperTest()" class="px-3 py-1.5 bg-rose-50 hover:bg-rose-100 text-rose-700 border border-rose-200 rounded-lg text-xs font-bold transition flex items-center gap-1">
                        <span>⚠️</span> Thử Nghiệm Gian Lận
                    </button>
                    <button onclick="resetElection()" class="px-3 py-1.5 bg-slate-100 hover:bg-slate-200 text-slate-700 border border-slate-200 rounded-lg text-xs font-bold transition flex items-center gap-1">
                        <span>🔄</span> Làm Mới Cuộc Bầu Cử
                    </button>
                </div>
            </div>

            <!-- CHUỖI KHỐI RENDER DẠNG CARD KẾT NỐI -->
            <div id="blockchainContainer" class="flex gap-4 overflow-x-auto pb-4 pt-2">
                <!-- Render các khối bằng Javascript -->
            </div>
        </section>

        <!-- SECTION 4: NHẬT KÝ HOẠT ĐỘNG THỜI GIAN THỰC (LIVE LOGS) -->
        <section class="bg-slate-900 text-slate-200 rounded-2xl p-4 shadow-sm border border-slate-800">
            <div class="flex items-center justify-between border-b border-slate-800 pb-2 mb-3">
                <div class="flex items-center gap-2 text-xs font-mono text-slate-300 font-bold">
                    <span class="w-2 h-2 rounded-full bg-emerald-400 animate-ping"></span>
                    <span>NHẬT KÝ HOẠT ĐỘNG MẠNG LƯỚI & SỔ CÁI (REALTIME AUDIT LOGS)</span>
                </div>
                <button onclick="fetchStatus()" class="text-xs text-sky-400 hover:underline font-mono">Làm mới</button>
            </div>
            <div id="terminalLogs" class="font-mono text-xs text-slate-300 space-y-1 max-h-36 overflow-y-auto leading-relaxed">
                <!-- Render logs -->
            </div>
        </section>
""";
    }

    private static String getScriptsHtml() {
        return """
    </div>

    <script>
        let selectedCandidateIdx = 0;
        const candidateNames = [
            "Lê Quang Minh (Đại biểu Khoa CNTT)",
            "Nguyễn Đỗ Trung Lương (Đại biểu Ban Học Tập)",
            "Nguyễn Hoàng Nhật (Đại biểu Ban Phong Trào)"
        ];

        function selectCandidate(idx) {
            selectedCandidateIdx = idx;
            document.querySelectorAll('.candidate-card').forEach((card, i) => {
                if (i === idx) {
                    card.classList.add('border-indigo-600', 'bg-indigo-50/50');
                    card.classList.remove('border-slate-200');
                } else {
                    card.classList.remove('border-indigo-600', 'bg-indigo-50/50');
                    card.classList.add('border-slate-200');
                }
            });
        }

        function selectVoter(name) {
            document.getElementById('voterInput').value = name;
        }

        // Khởi tạo chọn ứng cử viên đầu tiên
        selectCandidate(0);

        async function fetchStatus() {
            try {
                const res = await fetch('/api/election/status');
                const data = await res.json();
                renderDashboard(data);
            } catch (e) {
                console.error("Lỗi cập nhật:", e);
            }
        }

        async function changeDifficulty(val) {
            try {
                const res = await fetch('/api/election/difficulty', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ difficulty: parseInt(val) })
                });
                const data = await res.json();
                if (data.success) {
                    fetchStatus();
                }
            } catch (err) {
                console.error("Lỗi đổi độ khó:", err);
            }
        }

        function renderDashboard(data) {
            // Sync difficulty select if not focused
            const diffSel = document.getElementById('difficultySelect');
            if (diffSel && data.difficulty && document.activeElement !== diffSel) {
                diffSel.value = data.difficulty;
            }

            // Stats
            document.getElementById('statBlocks').textContent = data.blockCount;
            document.getElementById('statTotalVotes').textContent = data.totalVotes;
            document.getElementById('statMempool').textContent = data.mempool.length;
            document.getElementById('mempoolBadgeCount').textContent = data.mempool.length + ' phiếu chờ';
            document.getElementById('voterCountBadge').textContent = data.votedVoters.length + ' người';

            // Trạng thái sổ cái
            const badge = document.getElementById('chainStatusBadge');
            const alertBanner = document.getElementById('tamperAlertBanner');
            if (data.isTampered || !data.isChainValid) {
                badge.className = "px-3.5 py-2 bg-rose-50 border border-rose-300 rounded-xl text-center glow-danger";
                badge.innerHTML = '<div class="text-[10px] text-rose-700 uppercase font-mono tracking-wider font-bold">Trạng Thái Sổ Cái</div><div class="text-xs font-black text-rose-700 flex items-center gap-1 justify-center"><span>🚨</span> BỊ GIAN LẬN!</div>';
                alertBanner.classList.remove('hidden');
                document.getElementById('tamperReasonText').textContent = data.tamperReason || 'Mã hash khối không khớp với nội dung đã lưu!';
            } else {
                badge.className = "px-3.5 py-2 bg-emerald-50 border border-emerald-200 rounded-xl text-center";
                badge.innerHTML = '<div class="text-[10px] text-emerald-700 uppercase font-mono tracking-wider font-bold">Trạng Thái Sổ Cái</div><div class="text-xs font-black text-emerald-800 flex items-center gap-1 justify-center"><span>🛡️</span> AN TOÀN 100%</div>';
                alertBanner.classList.add('hidden');
            }

            // Render P2P Cluster Mempools
            if (data.cluster && data.cluster.length >= 3) {
                const n1 = document.getElementById('dashNode1Mem');
                const n2 = document.getElementById('dashNode2Mem');
                const n3 = document.getElementById('dashNode3Mem');
                if (n1) n1.textContent = data.cluster[0].mempoolCount + ' phiếu';
                if (n2) n2.textContent = data.cluster[1].mempoolCount + ' phiếu';
                if (n3) n3.textContent = data.cluster[2].mempoolCount + ' phiếu';
            }

            // Render Mempool
            const mempoolEl = document.getElementById('mempoolList');
            if (data.mempool.length === 0) {
                mempoolEl.innerHTML = '<div class="text-center py-8 text-xs text-slate-400 italic font-medium">Chưa có phiếu bầu nào trong Mempool. Hãy bỏ phiếu ở trên!</div>';
            } else {
                mempoolEl.innerHTML = data.mempool.map(v => `
                    <div class="p-2.5 bg-slate-50 border border-slate-200 rounded-xl flex items-center justify-between text-xs">
                        <div>
                            <div class="font-bold text-slate-800">Cử tri: <span class="text-indigo-600 font-mono">${escapeHtml(v.voterId)}</span></div>
                            <div class="text-slate-500 text-[11px] font-medium">Bầu cho: <strong class="text-slate-700">${escapeHtml(v.candidate)}</strong></div>
                        </div>
                        <div class="text-right font-mono text-[10px] text-slate-500">
                            <div>${escapeHtml(v.time)}</div>
                            <span class="px-2 py-0.5 bg-amber-50 text-amber-800 border border-amber-200 rounded text-[9px] font-bold">Chờ PoW</span>
                        </div>
                    </div>
                `).join('');
            }

            // Render Kết quả bầu cử
            const candCont = document.getElementById('candidatesResultsContainer');
            const total = data.totalVotes;
            candCont.innerHTML = data.candidates.map((c, i) => {
                const colors = [
                    { bar: 'from-sky-500 to-indigo-600', text: 'text-indigo-700' },
                    { bar: 'from-emerald-500 to-teal-600', text: 'text-emerald-700' },
                    { bar: 'from-amber-500 to-orange-500', text: 'text-amber-700' }
                ][i % 3];
                const isLeader = total > 0 && c.votes === Math.max(...data.candidates.map(x => x.votes));

                return `
                    <div class="p-3 bg-slate-50 border border-slate-200 rounded-xl space-y-1.5">
                        <div class="flex items-center justify-between text-xs">
                            <div class="font-bold text-slate-800 flex items-center gap-1.5">
                                <span>${i === 0 ? '👨‍💼' : (i === 1 ? '🎓' : '👨‍💻')}</span>
                                <span>${escapeHtml(c.name)}</span>
                                ${isLeader ? '<span class="px-2 py-0.5 text-[9px] bg-amber-100 text-amber-800 border border-amber-300 rounded-full font-bold">👑 DẪN ĐẦU</span>' : ''}
                            </div>
                            <div class="font-mono font-bold ${colors.text}">
                                ${c.votes} phiếu (${c.percent}%)
                            </div>
                        </div>
                        <div class="w-full h-2.5 bg-slate-200 rounded-full overflow-hidden border border-slate-200">
                            <div class="h-full bg-gradient-to-r ${colors.bar} rounded-full transition-all duration-500" style="width: ${c.percent}%"></div>
                        </div>
                    </div>
                `;
            }).join('');

            // Render Danh sách cử tri đã bầu
            const votersEl = document.getElementById('votedVotersTags');
            if (data.votedVoters.length === 0) {
                votersEl.innerHTML = '<span class="text-xs text-slate-400 italic font-medium">Chưa có cử tri nào được ghi nhận vào Sổ Cái.</span>';
            } else {
                votersEl.innerHTML = data.votedVoters.map(v => `
                    <span class="px-2.5 py-1 bg-slate-100 text-slate-700 border border-slate-200 rounded-lg text-xs font-mono font-medium flex items-center gap-1">
                        <span class="text-emerald-600 font-bold">✓</span> ${escapeHtml(v)}
                    </span>
                `).join('');
            }

            // Render Chuỗi Khối (Block Ledger) với 5 THUỘC TÍNH CHUẨN
            const chainCont = document.getElementById('blockchainContainer');
            chainCont.innerHTML = data.blocks.map((b, i) => {
                const isGenesis = (i === 0);
                const isTamperedBlock = data.isTampered && data.tamperedBlockIndex === i;
                const zeroMatches = b.hash.match(/^0+/) || [''];
                const zeroCount = zeroMatches[0].length;
                const leading = b.hash.substring(0, zeroCount);
                const rest = b.hash.substring(zeroCount);

                return `
                    <div class="min-w-[320px] max-w-[340px] flex-shrink-0 bg-white border-2 ${isTamperedBlock ? 'border-rose-400 bg-rose-50/20' : (isGenesis ? 'border-indigo-400' : 'border-slate-200 hover:border-indigo-400')} rounded-2xl p-4 shadow-sm space-y-2.5 transition">
                        <div class="flex items-center justify-between border-b border-slate-100 pb-2">
                            <span class="text-xs font-extrabold ${isGenesis ? 'text-indigo-600' : 'text-slate-800'} font-mono uppercase">
                                ${isGenesis ? '🌟 KHỐI KHỞI NGUYÊN (GENESIS #0)' : '📦 KHỐI BẦU CỬ #' + i}
                            </span>
                            <span class="text-[10px] px-2 py-0.5 bg-slate-100 text-slate-600 rounded font-mono font-bold">
                                Nonce: ${b.nonce}
                            </span>
                        </div>

                        <!-- 5 THUỘC TÍNH CHUẨN -->
                        <div class="space-y-1.5 text-xs font-mono">
                            <!-- 1. HASH -->
                            <div>
                                <div class="flex items-center justify-between">
                                    <span class="text-[10px] text-slate-500 font-medium">1. Mã Băm (Hash):</span>
                                    <span class="text-[9px] text-amber-700 bg-amber-50 px-1.5 py-0.2 rounded font-bold border border-amber-200">🎯 ${zeroCount} số 0 đầu</span>
                                </div>
                                <div class="p-2 bg-slate-50 rounded-lg border border-slate-200 text-[11px] font-bold ${isTamperedBlock ? 'text-rose-600' : 'text-slate-800'} break-all">
                                    <span class="text-amber-800 bg-amber-100 px-1 py-0.5 rounded font-black">${escapeHtml(leading)}</span>${escapeHtml(rest)}
                                </div>
                            </div>

                            <!-- 2. TIME -->
                            <div>
                                <span class="text-[10px] text-slate-500 font-medium block">2. Thời Gian (Time):</span>
                                <div class="text-slate-700 font-sans text-xs font-semibold">
                                    ⏱️ ${escapeHtml(b.time)}
                                </div>
                            </div>

                            <!-- 3. PRE HASH -->
                            <div>
                                <span class="text-[10px] text-slate-500 font-medium block">3. Mã Băm Trước (Pre Hash):</span>
                                <div class="p-1.5 bg-slate-50 rounded border border-slate-200 text-[10px] text-slate-500 break-all">
                                    ${escapeHtml(b.preHash)}
                                </div>
                            </div>

                            <!-- 4. NỘI DUNG -->
                            <div>
                                <span class="text-[10px] text-slate-500 font-medium block">4. Nội Dung (Phiếu Bầu Cử):</span>
                                <div class="p-2 bg-indigo-50/60 rounded-lg border border-indigo-100 text-xs font-sans text-indigo-900 break-words leading-relaxed max-h-24 overflow-y-auto font-medium">
                                    📜 ${escapeHtml(b.noiDung)}
                                </div>
                            </div>

                            <!-- 5. NONCE -->
                            <div class="flex items-center justify-between pt-1 border-t border-slate-100">
                                <span class="text-[10px] text-slate-500 font-medium">5. Số Nonce (PoW):</span>
                                <span class="px-2 py-0.5 bg-amber-50 text-amber-800 border border-amber-200 rounded font-bold text-xs">
                                    🎲 ${b.nonce}
                                </span>
                            </div>
                        </div>
                    </div>
                    ${i < data.blocks.length - 1 ? '<div class="flex items-center text-slate-300 text-2xl font-bold flex-shrink-0">➔</div>' : ''}
                `;
            }).join('');

            // Logs
            const logsEl = document.getElementById('terminalLogs');
            logsEl.innerHTML = data.logs.slice().reverse().map(l => `<div>${escapeHtml(l)}</div>`).join('');
        }

        async function submitVote() {
            const voterId = document.getElementById('voterInput').value.trim();
            const candidate = candidateNames[selectedCandidateIdx];

            if (!voterId) {
                alert('Vui lòng nhập tên cử tri hoặc bấm chọn mẫu!');
                return;
            }

            try {
                const res = await fetch('/api/election/vote', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ voterId: voterId, candidate: candidate })
                });
                const data = await res.json();
                if (data.success) {
                    alert('🎉 THÀNH CÔNG!\\n\\n' + data.message);
                    document.getElementById('voterInput').value = '';
                    fetchStatus();
                } else {
                    alert('❌ LỖI: ' + (data.error || 'Không thể bỏ phiếu!'));
                }
            } catch (err) {
                alert('Lỗi kết nối: ' + err.message);
            }
        }

        async function mineBlock() {
            const btn = document.getElementById('btnMine');
            const diffSel = document.getElementById('difficultySelect');
            const diffVal = diffSel ? diffSel.value : '5';
            btn.disabled = true;
            btn.innerHTML = `<span>⏳</span> ĐANG ĐÀO BLOCK (PoW Tìm ${diffVal} Số 0 Đầu)...`;

            try {
                const res = await fetch('/api/election/mine', { method: 'POST' });
                const data = await res.json();
                if (data.success) {
                    alert(`🎉 ĐÃ ĐÀO KHỐI THÀNH CÔNG!\\n\\nBlock mới thỏa mãn độ khó (${diffVal} số 0 ở đầu):\\nHash: ${data.hash}\\nNonce chiến thắng: ${data.nonce}\\nNội dung các phiếu bầu đã được khóa cứng vĩnh viễn!`);
                    fetchStatus();
                } else {
                    alert('❌ LỖI KHAI THÁC: ' + (data.error || 'Lỗi đóng khối!'));
                }
            } catch (err) {
                alert('Lỗi khai thác: ' + err.message);
            } finally {
                btn.disabled = false;
                btn.innerHTML = '<span>⛏️</span> KHAI THÁC BLOCK MỚI (PoW ĐÓNG KHỐI)';
            }
        }

        async function tamperTest() {
            if (!confirm('Bạn có chắc chắn muốn thử nghiệm SỬA ĐỔI NỘI DUNG của Khối #1 trong Sổ Cái?\\n\\nMã băm SHA-256 sẽ không còn khớp, hệ thống sẽ phát hiện gian lận và báo động đỏ ngay lập tức.')) {
                return;
            }

            try {
                const res = await fetch('/api/election/tamper', { method: 'POST' });
                const data = await res.json();
                if (data.success) {
                    fetchStatus();
                } else {
                    alert('Lỗi: ' + (data.error || 'Không thể thực hiện can thiệp!'));
                }
            } catch (err) {
                alert('Lỗi can thiệp: ' + err.message);
            }
        }

        async function resetElection() {
            if (!confirm('Bạn có muốn LÀM MỚI lại toàn bộ Sổ Cái và hòm phiếu Mempool?\\n\\nTất cả các khối đã đào sẽ được khởi động lại sạch sẽ về Genesis Block #0.')) {
                return;
            }

            try {
                const res = await fetch('/api/election/reset', { method: 'POST' });
                const data = await res.json();
                if (data.success) {
                    alert('Đã làm mới sổ cái thành công!');
                    fetchStatus();
                }
            } catch (err) {
                alert('Lỗi làm mới: ' + err.message);
            }
        }

        function escapeHtml(text) {
            if (!text) return '';
            return String(text)
                .replace(/&/g, "&amp;")
                .replace(/</g, "&lt;")
                .replace(/>/g, "&gt;")
                .replace(/"/g, "&quot;")
                .replace(/'/g, "&#039;");
        }

        // Tự động tải dữ liệu và làm mới mỗi 1.5 giây
        fetchStatus();
        setInterval(fetchStatus, 1500);
    </script>
</body>
</html>
""";
    }
}
