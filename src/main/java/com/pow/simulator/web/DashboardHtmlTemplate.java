package com.pow.simulator.web;

/**
 * Giao diện Trực Quan Hệ Thống Bầu Cử Điện Tử Blockchain (E-Voting).
 * Hiển thị trực quan:
 * 1. Cổng bỏ phiếu của cử tri (Voter Booth)
 * 2. Hòm phiếu tạm (Mempool)
 * 3. Bảng kết quả bầu cử trực tiếp từ Sổ Cái (Election Results)
 * 4. Sổ Cái Chuỗi Khối với đúng 5 thuộc tính: Hash, Time, Pre Hash, Nội dung, Nonce
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
<html lang="vi" class="dark">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hệ Thống Bầu Cử Điện Tử Blockchain - E-Voting</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script>
        tailwind.config = {
            darkMode: 'class',
            theme: {
                extend: {
                    colors: {
                        cyber: {
                            bg: '#090d16',
                            card: '#0f172a',
                            border: '#1e293b',
                            accent: '#38bdf8',
                            gold: '#f59e0b',
                            success: '#10b981',
                            danger: '#ef4444'
                        }
                    },
                    fontFamily: {
                        mono: ['"JetBrains Mono"', 'Consolas', 'monospace'],
                        sans: ['Inter', 'system-ui', 'sans-serif']
                    }
                }
            }
        }
    </script>
    <style>
        ::-webkit-scrollbar { width: 6px; height: 6px; }
        ::-webkit-scrollbar-track { background: #090d16; }
        ::-webkit-scrollbar-thumb { background: #334155; border-radius: 4px; }
        ::-webkit-scrollbar-thumb:hover { background: #475569; }
        .glow-accent { box-shadow: 0 0 15px rgba(56, 189, 248, 0.2); }
        .glow-green { box-shadow: 0 0 15px rgba(16, 185, 129, 0.2); }
        .glow-danger { box-shadow: 0 0 20px rgba(239, 68, 68, 0.3); }
    </style>
</head>
<body class="bg-cyber-bg text-slate-100 font-sans min-h-screen p-3 md:p-6">
    <div class="max-w-7xl mx-auto space-y-6">
""";
    }

    private static String getHeaderHtml() {
        return """
        <!-- HEADER -->
        <header class="bg-cyber-card border border-cyber-border rounded-2xl p-4 md:p-6 flex flex-col md:flex-row items-center justify-between gap-4 shadow-xl">
            <div class="flex items-center gap-4">
                <div class="w-14 h-14 rounded-2xl bg-gradient-to-tr from-sky-500 via-indigo-500 to-purple-600 flex items-center justify-center text-3xl shadow-lg shadow-sky-500/20">
                    🗳️
                </div>
                <div>
                    <div class="flex items-center gap-2">
                        <h1 class="text-xl md:text-2xl font-black text-transparent bg-clip-text bg-gradient-to-r from-sky-400 to-purple-400">
                            HỆ THỐNG BẦU CỬ ĐIỆN TỬ BLOCKCHAIN (E-VOTING)
                        </h1>
                        <span class="px-2.5 py-0.5 text-xs font-bold bg-emerald-500/10 text-emerald-400 border border-emerald-500/30 rounded-full flex items-center gap-1">
                            <span class="w-2 h-2 rounded-full bg-emerald-400 animate-pulse"></span> TRỰC TUYẾN
                        </span>
                    </div>
                    <p class="text-xs text-slate-400 mt-1">
                        Đồ án Lập Trình Mạng &bull; Nhóm: <strong class="text-sky-300">Nguyễn Hoàng Nhật</strong> (Lead) &bull; <strong class="text-sky-300">Nguyễn Đỗ Trung Lương</strong> &bull; <strong class="text-sky-300">Lê Quang Minh</strong>
                    </p>
                </div>
            </div>

            <!-- STATS BADGES -->
            <div class="flex flex-wrap items-center gap-3">
                <div class="px-3.5 py-2 bg-slate-900 border border-slate-800 rounded-xl text-center">
                    <div class="text-[10px] text-slate-400 uppercase font-mono tracking-wider">Số Khối (Blocks)</div>
                    <div id="statBlocks" class="text-lg font-black text-sky-400">0</div>
                </div>
                <div class="px-3.5 py-2 bg-slate-900 border border-slate-800 rounded-xl text-center">
                    <div class="text-[10px] text-slate-400 uppercase font-mono tracking-wider">Phiếu Đã Đóng Khối</div>
                    <div id="statTotalVotes" class="text-lg font-black text-emerald-400">0</div>
                </div>
                <div class="px-3.5 py-2 bg-slate-900 border border-slate-800 rounded-xl text-center">
                    <div class="text-[10px] text-slate-400 uppercase font-mono tracking-wider">Phiếu Trong Mempool</div>
                    <div id="statMempool" class="text-lg font-black text-amber-400">0</div>
                </div>
                <div class="px-3.5 py-1.5 bg-slate-900 border border-amber-800/60 rounded-xl text-center flex flex-col justify-center">
                    <div class="text-[10px] text-amber-400 uppercase font-mono tracking-wider mb-1 flex items-center justify-center gap-1">
                        <span>🎯</span> Độ Khó PoW (Tuỳ Chỉnh)
                    </div>
                    <div class="flex items-center gap-1 justify-center">
                        <select id="difficultySelect" onchange="changeDifficulty(this.value)"
                                class="bg-slate-950 border border-amber-600/50 rounded-lg px-2 py-0.5 text-xs font-mono font-bold text-amber-300 focus:outline-none focus:border-amber-400 cursor-pointer">
                            <option value="1">1 số 0 (0... ~1ms)</option>
                            <option value="2">2 số 0 (00... ~5ms)</option>
                            <option value="3">3 số 0 (000... ~30ms)</option>
                            <option value="4">4 số 0 (0000... ~100ms)</option>
                            <option value="5" selected>5 số 0 (00000... ~400ms)</option>
                            <option value="6">6 số 0 (000000... ~3s)</option>
                        </select>
                    </div>
                </div>
                <div id="chainStatusBadge" class="px-4 py-2 bg-emerald-950 border border-emerald-500/50 rounded-xl text-center">
                    <div class="text-[10px] text-emerald-300 uppercase font-mono tracking-wider">Trạng Thái Sổ Cái</div>
                    <div id="statIntegrity" class="text-xs font-black text-emerald-400 flex items-center gap-1 justify-center">
                        <span>🛡️</span> AN TOÀN 100%
                    </div>
                </div>
            </div>
        </header>

        <!-- CẢNH BÁO GIAN LẬN NẾU CÓ -->
        <div id="tamperAlertBanner" class="hidden bg-rose-950/80 border-2 border-rose-500 text-rose-200 p-4 rounded-xl flex items-center justify-between shadow-lg glow-danger animate-bounce">
            <div class="flex items-center gap-3">
                <span class="text-2xl">🚨</span>
                <div>
                    <h4 class="font-bold text-rose-300 text-sm">CẢNH BÁO GIAN LẬN: SỔ CÁI BỊ CAN THIỆP TRÁI PHÉP!</h4>
                    <p id="tamperReasonText" class="text-xs text-rose-300/80 mt-0.5"></p>
                </div>
            </div>
            <button onclick="resetElection()" class="px-3 py-1.5 bg-rose-600 hover:bg-rose-500 text-white text-xs font-bold rounded-lg transition">
                Khôi Phục Sổ Cái
            </button>
        </div>
""";
    }

    private static String getVotingBoothHtml() {
        return """
        <!-- SECTION 1: CỔNG BỎ PHIẾU CỦA CỬ TRI (VOTING BOOTH) -->
        <section class="bg-cyber-card border border-cyber-border rounded-2xl p-5 md:p-6 shadow-xl space-y-4">
            <div class="flex items-center justify-between border-b border-cyber-border pb-3">
                <div class="flex items-center gap-2">
                    <span class="text-2xl">🗳️</span>
                    <div>
                        <h2 class="text-lg font-bold text-slate-100">1. CỔNG BỎ PHIẾU CỦA CỬ TRI</h2>
                        <p class="text-xs text-slate-400">Chọn cử tri, chọn ứng cử viên và gửi phiếu bầu vào Mempool</p>
                    </div>
                </div>
                <span class="text-xs px-2.5 py-1 bg-sky-950 text-sky-400 border border-sky-800 rounded-lg font-mono">
                    Mỗi cử tri chỉ được bầu 1 lần duy nhất
                </span>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-12 gap-5">
                <!-- NHẬP TÊN CỬ TRI -->
                <div class="md:col-span-5 space-y-3">
                    <label class="block text-xs font-semibold text-slate-300">
                        Họ Tên / Mã Số Cử Tri (Voter ID):
                    </label>
                    <input type="text" id="voterInput" placeholder="Ví dụ: SV001 - Nguyễn Hoàng Nhật"
                           class="w-full bg-slate-900 border border-slate-700 rounded-xl px-4 py-2.5 text-sm text-sky-300 placeholder-slate-500 focus:outline-none focus:border-sky-500 focus:ring-1 focus:ring-sky-500 transition font-mono">
                    
                    <div>
                        <div class="text-[11px] text-slate-400 mb-1.5">Hoặc bấm chọn nhanh cử tri mẫu:</div>
                        <div class="flex flex-wrap gap-1.5">
                            <button onclick="selectVoter('SV01 - Hoàng Nhật')" class="px-2 py-1 text-xs bg-slate-800 hover:bg-slate-700 text-slate-300 rounded border border-slate-700 transition">SV01 - Hoàng Nhật</button>
                            <button onclick="selectVoter('SV02 - Trung Lương')" class="px-2 py-1 text-xs bg-slate-800 hover:bg-slate-700 text-slate-300 rounded border border-slate-700 transition">SV02 - Trung Lương</button>
                            <button onclick="selectVoter('SV03 - Quang Minh')" class="px-2 py-1 text-xs bg-slate-800 hover:bg-slate-700 text-slate-300 rounded border border-slate-700 transition">SV03 - Quang Minh</button>
                            <button onclick="selectVoter('SV04 - Thùy Linh')" class="px-2 py-1 text-xs bg-slate-800 hover:bg-slate-700 text-slate-300 rounded border border-slate-700 transition">SV04 - Thùy Linh</button>
                            <button onclick="selectVoter('SV05 - Văn Nam')" class="px-2 py-1 text-xs bg-slate-800 hover:bg-slate-700 text-slate-300 rounded border border-slate-700 transition">SV05 - Văn Nam</button>
                        </div>
                    </div>
                </div>

                <!-- CHỌN ỨNG CỬ VIÊN -->
                <div class="md:col-span-7 space-y-3">
                    <label class="block text-xs font-semibold text-slate-300">
                        Chọn Ứng Cử Viên Đại Biểu:
                    </label>
                    <div class="grid grid-cols-1 sm:grid-cols-3 gap-3">
                        <!-- CANDIDATE 1 -->
                        <div onclick="selectCandidate(0)" id="cardCand0"
                             class="candidate-card cursor-pointer bg-slate-900 border-2 border-slate-800 hover:border-sky-500/50 rounded-xl p-3 text-center transition space-y-2">
                            <div class="w-12 h-12 mx-auto rounded-full bg-gradient-to-tr from-sky-500 to-blue-600 flex items-center justify-center text-xl shadow">
                                👨‍💻
                            </div>
                            <div class="font-bold text-xs text-slate-200">Lê Quang Minh</div>
                            <div class="text-[10px] text-sky-400">Đại biểu Khoa CNTT</div>
                        </div>

                        <!-- CANDIDATE 2 -->
                        <div onclick="selectCandidate(1)" id="cardCand1"
                             class="candidate-card cursor-pointer bg-slate-900 border-2 border-slate-800 hover:border-sky-500/50 rounded-xl p-3 text-center transition space-y-2">
                            <div class="w-12 h-12 mx-auto rounded-full bg-gradient-to-tr from-emerald-500 to-teal-600 flex items-center justify-center text-xl shadow">
                                🎓
                            </div>
                            <div class="font-bold text-xs text-slate-200">Nguyễn Đỗ Trung Lương</div>
                            <div class="text-[10px] text-emerald-400">Ban Học Tập</div>
                        </div>

                        <!-- CANDIDATE 3 -->
                        <div onclick="selectCandidate(2)" id="cardCand2"
                             class="candidate-card cursor-pointer bg-slate-900 border-2 border-slate-800 hover:border-sky-500/50 rounded-xl p-3 text-center transition space-y-2">
                            <div class="w-12 h-12 mx-auto rounded-full bg-gradient-to-tr from-amber-500 to-orange-600 flex items-center justify-center text-xl shadow">
                                ⚡
                            </div>
                            <div class="font-bold text-xs text-slate-200">Nguyễn Hoàng Nhật</div>
                            <div class="text-[10px] text-amber-400">Ban Phong Trào</div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- NÚT GỬI PHIẾU BẦU -->
            <div class="pt-3 border-t border-cyber-border flex flex-col sm:flex-row items-center justify-between gap-3">
                <div class="text-xs text-slate-400 flex items-center gap-1.5">
                    <span>💡</span> Phiếu sau khi bấm gửi sẽ vào <strong>Hòm Phiếu Mempool</strong> chờ thợ đào đóng khối PoW.
                </div>
                <button onclick="submitVote()"
                        class="w-full sm:w-auto px-6 py-2.5 bg-gradient-to-r from-sky-500 to-blue-600 hover:from-sky-400 hover:to-blue-500 text-white font-bold text-sm rounded-xl shadow-lg shadow-sky-500/25 transition flex items-center justify-center gap-2">
                    <span>🗳️</span> BỎ PHIẾU BẦU CỬ (GỬI VÀO MEMPOOL)
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
            <div class="lg:col-span-5 bg-cyber-card border border-cyber-border rounded-2xl p-5 shadow-xl flex flex-col justify-between">
                <div>
                    <div class="flex items-center justify-between border-b border-cyber-border pb-3 mb-3">
                        <div class="flex items-center gap-2">
                            <span class="text-xl">⏳</span>
                            <h3 class="text-sm font-bold text-slate-200">2. HÒM PHIẾU TẠM (MEMPOOL)</h3>
                        </div>
                        <span id="mempoolBadgeCount" class="px-2 py-0.5 bg-amber-500/10 text-amber-400 border border-amber-500/30 rounded-full text-xs font-mono font-bold">
                            0 phiếu chờ
                        </span>
                    </div>

                    <div id="mempoolList" class="space-y-2 max-h-56 overflow-y-auto pr-1">
                        <!-- Danh sách phiếu trong mempool render bằng JS -->
                        <div class="text-center py-8 text-xs text-slate-500 italic">
                            Chưa có phiếu bầu nào trong Mempool. Hãy bỏ phiếu ở trên!
                        </div>
                    </div>
                </div>

                <div class="pt-4 border-t border-cyber-border mt-4">
                    <button onclick="mineBlock()" id="btnMine"
                            class="w-full py-3 bg-gradient-to-r from-emerald-500 to-teal-600 hover:from-emerald-400 hover:to-teal-500 text-white font-black text-sm rounded-xl shadow-lg shadow-emerald-500/25 transition flex items-center justify-center gap-2">
                        <span>⛏️</span> KHAI THÁC BLOCK MỚI (PoW ĐÓNG KHỐI)
                    </button>
                    <p class="text-[11px] text-slate-400 text-center mt-2">
                        Thợ đào sẽ gom các phiếu trong Mempool, giải mã Nonce và ghi vào Sổ Cái vĩnh viễn.
                    </p>
                </div>
            </div>

            <!-- KẾT QUẢ BẦU CỬ TRỰC TIẾP TỪ SỔ CÁI -->
            <div class="lg:col-span-7 bg-cyber-card border border-cyber-border rounded-2xl p-5 shadow-xl space-y-4">
                <div class="flex items-center justify-between border-b border-cyber-border pb-3">
                    <div class="flex items-center gap-2">
                        <span class="text-xl">📊</span>
                        <h3 class="text-sm font-bold text-slate-200">3. KẾT QUẢ BẦU CỬ TRỰC TIẾP (TỪ SỔ CÁI)</h3>
                    </div>
                    <span class="text-xs text-slate-400">Tự động cập nhật khi có Block mới</span>
                </div>

                <!-- THANH TỶ LỆ PHIẾU BẦU CHO CÁC ỨNG CỬ VIÊN -->
                <div id="candidatesResultsContainer" class="space-y-3">
                    <!-- Sẽ render bằng JS -->
                </div>

                <!-- DANH SÁCH CỬ TRI ĐÃ BẦU MINH BẠCH -->
                <div class="pt-3 border-t border-cyber-border">
                    <div class="flex items-center justify-between text-xs text-slate-300 font-semibold mb-2">
                        <span>Danh Sách Cử Tri Đã Bỏ Phiếu (Minh Bạch):</span>
                        <span id="voterCountBadge" class="text-sky-400 font-mono">0 người</span>
                    </div>
                    <div id="votedVotersTags" class="flex flex-wrap gap-1.5 max-h-24 overflow-y-auto">
                        <span class="text-xs text-slate-500 italic">Chưa có cử tri nào được ghi nhận vào Sổ Cái.</span>
                    </div>
                </div>
            </div>
        </div>
""";
    }

    private static String getLedgerAndLogsHtml() {
        return """
        <!-- SECTION 3: SỔ CÁI CHUỖI KHỐI (BLOCKCHAIN LEDGER) - 5 THUỘC TÍNH CHUẨN -->
        <section class="bg-cyber-card border border-cyber-border rounded-2xl p-5 md:p-6 shadow-xl space-y-4">
            <div class="flex flex-col md:flex-row items-start md:items-center justify-between gap-3 border-b border-cyber-border pb-3">
                <div class="flex items-center gap-2">
                    <span class="text-2xl">🔗</span>
                    <div>
                        <h3 class="text-lg font-bold text-slate-100">4. SỔ CÁI CHUỖI KHỐI (BLOCKCHAIN LEDGER)</h3>
                        <p class="text-xs text-slate-400">Mỗi khối gồm đúng 5 thuộc tính: <strong>Hash &bull; Time &bull; Pre Hash &bull; Nội dung &bull; Nonce</strong></p>
                    </div>
                </div>

                <!-- CÁC NÚT THAO TÁC SỔ CÁI -->
                <div class="flex items-center gap-2">
                    <button onclick="tamperTest()" class="px-3 py-1.5 bg-rose-950 text-rose-300 hover:bg-rose-900 border border-rose-800 rounded-lg text-xs font-bold transition flex items-center gap-1">
                        <span>⚠️</span> Thử Nghiệm Gian Lận
                    </button>
                    <button onclick="resetElection()" class="px-3 py-1.5 bg-slate-800 hover:bg-slate-700 text-slate-300 rounded-lg text-xs font-bold transition flex items-center gap-1">
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
        <section class="bg-slate-950 border border-slate-900 rounded-2xl p-4 shadow-xl">
            <div class="flex items-center justify-between border-b border-slate-800 pb-2 mb-3">
                <div class="flex items-center gap-2 text-xs font-mono text-slate-400">
                    <span class="w-2 h-2 rounded-full bg-sky-400 animate-ping"></span>
                    <span>NHẬT KÝ HOẠT ĐỘNG MẠNG LƯỚI & SỔ CÁI (REALTIME AUDIT LOGS)</span>
                </div>
                <button onclick="fetchStatus()" class="text-xs text-sky-400 hover:underline">Làm mới</button>
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
                    card.classList.add('border-sky-400', 'bg-sky-950/30', 'glow-accent');
                    card.classList.remove('border-slate-800');
                } else {
                    card.classList.remove('border-sky-400', 'bg-sky-950/30', 'glow-accent');
                    card.classList.add('border-slate-800');
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
                badge.className = "px-4 py-2 bg-rose-950 border border-rose-500 rounded-xl text-center glow-danger";
                badge.innerHTML = '<div class="text-[10px] text-rose-300 uppercase font-mono tracking-wider">Trạng Thái Sổ Cái</div><div class="text-xs font-black text-rose-400 flex items-center gap-1 justify-center"><span>🚨</span> BỊ GIAN LẬN!</div>';
                alertBanner.classList.remove('hidden');
                document.getElementById('tamperReasonText').textContent = data.tamperReason || 'Mã hash khối không khớp với nội dung đã lưu!';
            } else {
                badge.className = "px-4 py-2 bg-emerald-950 border border-emerald-500/50 rounded-xl text-center glow-green";
                badge.innerHTML = '<div class="text-[10px] text-emerald-300 uppercase font-mono tracking-wider">Trạng Thái Sổ Cái</div><div class="text-xs font-black text-emerald-400 flex items-center gap-1 justify-center"><span>🛡️</span> AN TOÀN 100%</div>';
                alertBanner.classList.add('hidden');
            }

            // Render Mempool
            const mempoolEl = document.getElementById('mempoolList');
            if (data.mempool.length === 0) {
                mempoolEl.innerHTML = '<div class="text-center py-8 text-xs text-slate-500 italic">Chưa có phiếu bầu nào trong Mempool. Hãy bỏ phiếu ở trên!</div>';
            } else {
                mempoolEl.innerHTML = data.mempool.map(v => `
                    <div class="p-2.5 bg-slate-900 border border-amber-900/50 rounded-xl flex items-center justify-between text-xs">
                        <div>
                            <div class="font-bold text-slate-200">Cử tri: <span class="text-amber-400 font-mono">${escapeHtml(v.voterId)}</span></div>
                            <div class="text-slate-400 text-[11px]">Bầu cho: <strong class="text-sky-300">${escapeHtml(v.candidate)}</strong></div>
                        </div>
                        <div class="text-right font-mono text-[10px] text-slate-500">
                            <div>${escapeHtml(v.time)}</div>
                            <span class="px-1.5 py-0.5 bg-amber-950 text-amber-300 border border-amber-800 rounded text-[9px]">Chờ PoW</span>
                        </div>
                    </div>
                `).join('');
            }

            // Render Kết quả bầu cử
            const candCont = document.getElementById('candidatesResultsContainer');
            const total = data.totalVotes;
            candCont.innerHTML = data.candidates.map((c, i) => {
                const colors = [
                    { bar: 'from-sky-500 to-blue-600', text: 'text-sky-400' },
                    { bar: 'from-emerald-500 to-teal-600', text: 'text-emerald-400' },
                    { bar: 'from-amber-500 to-orange-600', text: 'text-amber-400' }
                ][i % 3];
                const isLeader = total > 0 && c.votes === Math.max(...data.candidates.map(x => x.votes));

                return `
                    <div class="p-3 bg-slate-900 border border-slate-800 rounded-xl space-y-1.5">
                        <div class="flex items-center justify-between text-xs">
                            <div class="font-bold text-slate-200 flex items-center gap-1.5">
                                <span>${i === 0 ? '👨‍💻' : (i === 1 ? '🎓' : '⚡')}</span>
                                <span>${escapeHtml(c.name)}</span>
                                ${isLeader ? '<span class="px-2 py-0.2 text-[9px] bg-amber-500/20 text-amber-300 border border-amber-500/40 rounded-full font-bold">👑 DẪN ĐẦU</span>' : ''}
                            </div>
                            <div class="font-mono font-bold ${colors.text}">
                                ${c.votes} phiếu (${c.percent}%)
                            </div>
                        </div>
                        <div class="w-full h-2.5 bg-slate-950 rounded-full overflow-hidden border border-slate-800">
                            <div class="h-full bg-gradient-to-r ${colors.bar} rounded-full transition-all duration-500" style="width: ${c.percent}%"></div>
                        </div>
                    </div>
                `;
            }).join('');

            // Render Danh sách cử tri đã bầu
            const votersEl = document.getElementById('votedVotersTags');
            if (data.votedVoters.length === 0) {
                votersEl.innerHTML = '<span class="text-xs text-slate-500 italic">Chưa có cử tri nào được ghi nhận vào Sổ Cái.</span>';
            } else {
                votersEl.innerHTML = data.votedVoters.map(v => `
                    <span class="px-2.5 py-1 bg-slate-800 text-slate-300 border border-slate-700 rounded-lg text-xs font-mono flex items-center gap-1">
                        <span class="text-emerald-400">✓</span> ${escapeHtml(v)}
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
                    <div class="min-w-[320px] max-w-[340px] flex-shrink-0 bg-slate-900 border-2 ${isTamperedBlock ? 'border-rose-500 glow-danger' : (isGenesis ? 'border-purple-500/50' : 'border-sky-500/50')} rounded-2xl p-4 shadow-xl space-y-2.5">
                        <div class="flex items-center justify-between border-b border-slate-800 pb-2">
                            <span class="text-xs font-black ${isGenesis ? 'text-purple-400' : 'text-sky-400'} font-mono uppercase">
                                ${isGenesis ? '🌟 KHỐI KHỞI NGUYÊN (GENESIS #0)' : '📦 KHỐI BẦU CỬ #' + i}
                            </span>
                            <span class="text-[10px] px-2 py-0.5 bg-slate-800 text-slate-400 rounded font-mono">
                                Nonce: ${b.nonce}
                            </span>
                        </div>

                        <!-- 5 THUỘC TÍNH CHUẨN -->
                        <div class="space-y-1.5 text-xs font-mono">
                            <!-- 1. HASH -->
                            <div>
                                <div class="flex items-center justify-between">
                                    <span class="text-[10px] text-slate-400">1. Mã Băm (Hash):</span>
                                    <span class="text-[9px] text-amber-400 font-bold">🎯 ${zeroCount} số 0 đầu</span>
                                </div>
                                <div class="p-1.5 bg-slate-950 rounded border border-slate-800 text-[11px] font-bold ${isTamperedBlock ? 'text-rose-400' : 'text-emerald-400'} break-all">
                                    <span class="text-amber-400 bg-amber-500/20 px-1 py-0.5 rounded font-black">${escapeHtml(leading)}</span>${escapeHtml(rest)}
                                </div>
                            </div>

                            <!-- 2. TIME -->
                            <div>
                                <span class="text-[10px] text-slate-400 block">2. Thời Gian (Time):</span>
                                <div class="text-slate-300 font-sans text-xs">
                                    ⏱️ ${escapeHtml(b.time)}
                                </div>
                            </div>

                            <!-- 3. PRE HASH -->
                            <div>
                                <span class="text-[10px] text-slate-400 block">3. Mã Băm Trước (Pre Hash):</span>
                                <div class="p-1.5 bg-slate-950 rounded border border-slate-800 text-[10px] text-slate-400 break-all">
                                    ${escapeHtml(b.preHash)}
                                </div>
                            </div>

                            <!-- 4. NỘI DUNG -->
                            <div>
                                <span class="text-[10px] text-slate-400 block">4. Nội Dung (Phiếu Bầu Cử):</span>
                                <div class="p-2 bg-slate-950 rounded border border-slate-800 text-xs font-sans text-sky-200 break-words leading-relaxed max-h-24 overflow-y-auto">
                                    📜 ${escapeHtml(b.noiDung)}
                                </div>
                            </div>

                            <!-- 5. NONCE -->
                            <div class="flex items-center justify-between pt-1 border-t border-slate-800/80">
                                <span class="text-[10px] text-slate-400">5. Số Nonce (PoW):</span>
                                <span class="px-2 py-0.5 bg-amber-950/60 text-amber-300 border border-amber-800/60 rounded font-bold text-xs">
                                    🎲 ${b.nonce}
                                </span>
                            </div>
                        </div>
                    </div>
                    ${i < data.blocks.length - 1 ? '<div class="flex items-center text-slate-600 text-2xl font-bold flex-shrink-0">➔</div>' : ''}
                `;
            }).join('');

            // Render Logs
            const logEl = document.getElementById('terminalLogs');
            logEl.innerHTML = data.logs.map(l => `<div>${escapeHtml(l)}</div>`).join('');
            logEl.scrollTop = logEl.scrollHeight;
        }

        async function submitVote() {
            const voterId = document.getElementById('voterInput').value.trim();
            const candidate = candidateNames[selectedCandidateIdx];

            if (!voterId) {
                alert('Vui lòng nhập họ tên hoặc mã số cử tri!');
                return;
            }

            try {
                const res = await fetch('/api/election/vote', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ voterId, candidate })
                });
                const data = await res.json();
                if (data.success) {
                    alert('🎉 Đã nhận phiếu bầu của cử tri [' + voterId + '] vào Mempool thành công!\\n\\nHãy bấm nút "⛏️ KHAI THÁC BLOCK MỚI" bên dưới để đóng khối vào Sổ Cái!');
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
                alert('Lỗi kết nối: ' + err.message);
            } finally {
                btn.disabled = false;
                btn.innerHTML = '<span>⛏️</span> KHAI THÁC BLOCK MỚI (PoW ĐÓNG KHỐI)';
            }
        }

        async function tamperTest() {
            if (!confirm('Bạn có muốn thử nghiệm sửa đổi nội dung phiếu bầu trong Khối #1 để kiểm tra tính bảo mật của Blockchain không?')) return;
            try {
                const res = await fetch('/api/election/tamper', { method: 'POST' });
                const data = await res.json();
                alert('⚠️ ĐÃ SỬA ĐỔI NỘI DUNG!\\n\\nSổ Cái sẽ lập tức phát hiện mã băm không khớp và chuyển sang trạng thái BÁO ĐỘNG ĐỎ!');
                fetchStatus();
            } catch (err) {
                alert('Lỗi: ' + err.message);
            }
        }

        async function resetElection() {
            if (!confirm('Bạn có chắc muốn làm mới lại toàn bộ Sổ Cái Bầu Cử không?')) return;
            try {
                const res = await fetch('/api/election/reset', { method: 'POST' });
                const data = await res.json();
                fetchStatus();
            } catch (err) {
                alert('Lỗi: ' + err.message);
            }
        }

        function escapeHtml(text) {
            if (!text) return '';
            return String(text)
                .replace(/&/g, '&amp;')
                .replace(/</g, '&lt;')
                .replace(/>/g, '&gt;')
                .replace(/"/g, '&quot;')
                .replace(/'/g, '&#039;');
        }

        // Tự động làm mới mỗi 2 giây
        fetchStatus();
        setInterval(fetchStatus, 2000);
    </script>
</body>
</html>
""";
    }
}
