package com.pow.simulator.web;

/**
 * Giao diện Quản Trị Hệ Thống Bầu Cử Điện Tử Blockchain (E-Voting Dashboard).
 * Thiết kế tối giản, tinh tế, chuyên nghiệp (Clean Minimalist White Theme).
 * Tập trung tối đa vào 3 thành phần cốt lõi:
 * 1. Hòm phiếu Mempool (các phiếu chờ đóng khối)
 * 2. Khai thác PoW (nút bấm giải Nonce tìm độ khó)
 * 3. Sổ Cái Chuỗi Khối (các Block nối xích với nhau qua PreHash và Nonce 5 thuộc tính)
 */
public final class DashboardHtmlTemplate {

    private DashboardHtmlTemplate() {}

    public static String getHtml() {
        return """
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sổ Cái Blockchain & Khai Thác PoW | E-Voting Ledger</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&family=JetBrains+Mono:wght@400;500;600;700&display=swap');
        body { font-family: 'Plus Jakarta Sans', -apple-system, BlinkMacSystemFont, sans-serif; }
        .font-mono { font-family: 'JetBrains Mono', monospace; }
        ::-webkit-scrollbar { width: 6px; height: 6px; }
        ::-webkit-scrollbar-track { background: #f8fafc; }
        ::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 9999px; }
        ::-webkit-scrollbar-thumb:hover { background: #94a3b8; }
        .block-card { transition: all 0.25s cubic-bezier(0.16, 1, 0.3, 1); }
        .block-card:hover { transform: translateY(-3px); box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.08); }
        .chain-connector {
            display: flex;
            align-items: center;
            justify-content: center;
            color: #6366f1;
            font-size: 1.25rem;
            flex-shrink: 0;
            padding: 0 8px;
        }
        .glow-danger { box-shadow: 0 0 20px rgba(239, 68, 68, 0.2); }
    </style>
</head>
<body class="bg-[#f8fafc] text-slate-800 antialiased min-h-screen p-4 md:p-8 flex flex-col justify-between selection:bg-indigo-600 selection:text-white space-y-6">

    <div class="max-w-7xl mx-auto w-full space-y-6">

        <!-- 1. HEADER TỐI GIẢN & CHUYÊN NGHIỆP -->
        <header class="bg-white border border-slate-200/90 rounded-2xl p-4 md:p-6 flex flex-col lg:flex-row items-center justify-between gap-4 shadow-xs">
            <div class="flex items-center gap-3.5">
                <div class="w-12 h-12 rounded-xl bg-indigo-600 text-white flex items-center justify-center text-2xl shadow-md shadow-indigo-600/20 shrink-0">
                    🗳️
                </div>
                <div>
                    <div class="flex items-center gap-2">
                        <h1 class="text-xl md:text-2xl font-extrabold text-slate-900 tracking-tight">
                            SỔ CÁI BLOCKCHAIN & KHAI THÁC POW
                        </h1>
                        <span class="px-2.5 py-0.5 text-xs font-bold bg-emerald-50 text-emerald-700 border border-emerald-200 rounded-full flex items-center gap-1 font-mono">
                            <span class="w-2 h-2 rounded-full bg-emerald-500 animate-pulse"></span> P2P ONLINE
                        </span>
                    </div>
                    <p class="text-xs text-slate-500 font-medium mt-0.5">
                        Đồ án Lập Trình Mạng &bull; HUTECH 2026 &bull; Nhóm: <strong class="text-indigo-600">Nguyễn Hoàng Nhật</strong>, <strong class="text-indigo-600">Nguyễn Đỗ Trung Lương</strong>, <strong class="text-indigo-600">Lê Quang Minh</strong>
                    </p>
                </div>
            </div>

            <!-- THANH THỐNG KÊ GỌN GÀNG -->
            <div class="flex flex-wrap items-center gap-2.5">
                <div class="px-3.5 py-1.5 bg-slate-50 border border-slate-200 rounded-xl text-center">
                    <div class="text-[10px] text-slate-500 uppercase font-mono font-medium">Chiều Cao Sổ Cái</div>
                    <div class="text-base font-extrabold text-indigo-600 font-mono"><span id="statBlocks">0</span> khối</div>
                </div>

                <div class="px-3.5 py-1.5 bg-slate-50 border border-slate-200 rounded-xl text-center">
                    <div class="text-[10px] text-slate-500 uppercase font-mono font-medium">Mempool Chờ</div>
                    <div class="text-base font-extrabold text-amber-600 font-mono"><span id="statMempool">0</span> phiếu</div>
                </div>

                <!-- BỘ CHỌN ĐỘ KHÓ -->
                <div class="px-3 py-1.5 bg-amber-50/70 border border-amber-200 rounded-xl text-center flex flex-col justify-center">
                    <div class="text-[10px] text-amber-800 uppercase font-mono font-bold tracking-wider mb-0.5 flex items-center justify-center gap-1">
                        <span>🎯</span> Độ Khó PoW
                    </div>
                    <select id="difficultySelect" onchange="changeDifficulty(this.value)"
                            class="bg-white border border-amber-300 rounded-lg px-2 py-0.5 text-xs font-mono font-bold text-amber-900 focus:outline-none cursor-pointer shadow-2xs">
                        <option value="1">1 số 0 (0...)</option>
                        <option value="2">2 số 0 (00...)</option>
                        <option value="3">3 số 0 (000...)</option>
                        <option value="4">4 số 0 (0000...)</option>
                        <option value="5" selected>5 số 0 (00000...)</option>
                        <option value="6">6 số 0 (000000...)</option>
                    </select>
                </div>

                <div id="chainStatusBadge" class="px-3.5 py-2 bg-emerald-50 border border-emerald-200 rounded-xl text-center">
                    <div class="text-[10px] text-emerald-700 uppercase font-mono font-medium">Trạng Thái Sổ Cái</div>
                    <div id="statIntegrity" class="text-xs font-extrabold text-emerald-800 flex items-center gap-1 justify-center">
                        <span>🛡️</span> AN TOÀN 100%
                    </div>
                </div>

                <a href="/vote" target="_blank" class="px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl text-xs font-extrabold shadow-sm shadow-indigo-600/20 flex items-center gap-1.5 transition-all transform hover:scale-[1.02]">
                    <span>🗳️</span> PHÒNG BỎ PHIẾU CỬ TRI (/vote)
                </a>
            </div>
        </header>

        <!-- BANNER CẢNH BÁO GIAN LẬN NẾU BỊ CAN THIỆP -->
        <div id="tamperAlertBanner" class="hidden bg-rose-50 border border-rose-300 text-rose-800 p-4 rounded-xl flex items-center justify-between shadow-sm glow-danger">
            <div class="flex items-center gap-3">
                <span class="text-2xl">🚨</span>
                <div>
                    <h3 class="font-extrabold text-sm text-rose-900">PHÁT HIỆN GIAN LẬN: SỔ CÁI BỊ SAI MÃ BĂM SHA-256!</h3>
                    <p id="tamperReasonText" class="text-xs text-rose-700 mt-0.5 font-mono"></p>
                </div>
            </div>
            <button onclick="resetElection()" class="px-3.5 py-1.5 bg-rose-600 hover:bg-rose-700 text-white font-bold rounded-lg text-xs transition">
                Phục Hồi Sổ Cái
            </button>
        </div>

        <!-- 2. TRUNG TÂM HOẠT ĐỘNG: HÒM PHIẾU MEMPOOL & KHAI THÁC POW -->
        <div class="grid grid-cols-1 lg:grid-cols-12 gap-6">

            <!-- CỘT 1: HÒM PHIẾU TẠM (MEMPOOL) -->
            <div class="lg:col-span-7 bg-white border border-slate-200/90 rounded-2xl p-5 shadow-xs space-y-4 flex flex-col justify-between">
                <div>
                    <div class="flex items-center justify-between border-b border-slate-100 pb-3 mb-3">
                        <div class="flex items-center gap-2">
                            <span class="text-xl">⏳</span>
                            <div>
                                <h2 class="text-sm font-extrabold text-slate-900">HÒM PHIẾU CHỜ XÁC NHẬN (MEMPOOL)</h2>
                                <p class="text-[11px] text-slate-500 font-medium">Phiếu cử tri gửi từ phòng bỏ phiếu đang nằm trên RAM chờ thợ đào đóng khối</p>
                            </div>
                        </div>
                        <span id="mempoolBadgeCount" class="px-2.5 py-0.5 bg-amber-50 text-amber-800 border border-amber-200 rounded-full text-xs font-mono font-bold">
                            0 phiếu chờ
                        </span>
                    </div>

                    <!-- WIDGET ĐỒNG BỘ 3 NODES P2P -->
                    <div class="mb-3 p-3 bg-slate-50 border border-slate-200 rounded-xl space-y-1.5">
                        <div class="text-[11px] font-bold text-slate-700 flex items-center justify-between">
                            <span>🌐 ĐỒNG BỘ MEMPOOL TRÊN 3 NODES P2P:</span>
                            <span class="text-[9px] font-mono text-emerald-700 bg-emerald-50 px-2 py-0.5 rounded-full border border-emerald-200 font-bold">● 3/3 NODES IN SYNC</span>
                        </div>
                        <div class="grid grid-cols-3 gap-2 text-center text-[10px] font-mono">
                            <div class="bg-white p-2 rounded-lg border border-slate-200 shadow-2xs">
                                <div class="text-slate-500 font-medium truncate">Node 1 (Khoa CNTT)</div>
                                <div id="dashNode1Mem" class="text-amber-600 font-extrabold text-xs mt-0.5">0 phiếu</div>
                            </div>
                            <div class="bg-white p-2 rounded-lg border border-slate-200 shadow-2xs">
                                <div class="text-slate-500 font-medium truncate">Node 2 (Ban Giám Sát)</div>
                                <div id="dashNode2Mem" class="text-amber-600 font-extrabold text-xs mt-0.5">0 phiếu</div>
                            </div>
                            <div class="bg-white p-2 rounded-lg border border-slate-200 shadow-2xs">
                                <div class="text-slate-500 font-medium truncate">Node 3 (Hội Đồng SV)</div>
                                <div id="dashNode3Mem" class="text-amber-600 font-extrabold text-xs mt-0.5">0 phiếu</div>
                            </div>
                        </div>
                    </div>

                    <!-- DANH SÁCH CÁC PHIẾU ĐANG CHỜ TRONG MEMPOOL -->
                    <div id="mempoolList" class="space-y-2 max-h-48 overflow-y-auto pr-1">
                        <div class="text-center py-8 text-xs text-slate-400 italic font-medium">
                            Chưa có phiếu bầu nào trong Mempool. Hãy mở <a href="/vote" target="_blank" class="text-indigo-600 font-bold underline">Phòng Bỏ Phiếu Cử Tri</a> để gửi phiếu!
                        </div>
                    </div>
                </div>

                <div class="pt-3 border-t border-slate-100 text-xs text-slate-500 flex items-center justify-between font-medium">
                    <span>💡 Mỗi khi cử tri bỏ phiếu, phiếu sẽ tức thì phát tán vào Mempool của cả 3 Nodes.</span>
                    <a href="/vote" target="_blank" class="text-indigo-600 hover:text-indigo-800 font-bold underline">Mở phòng bỏ phiếu &rarr;</a>
                </div>
            </div>

            <!-- CỘT 2: TRUNG TÂM KHAI THÁC POW (MINING ENGINE) -->
            <div class="lg:col-span-5 bg-white border border-slate-200/90 rounded-2xl p-5 shadow-xs flex flex-col justify-between space-y-4">
                <div class="space-y-3">
                    <div class="flex items-center justify-between border-b border-slate-100 pb-3">
                        <div class="flex items-center gap-2">
                            <span class="text-xl">⛏️</span>
                            <h2 class="text-sm font-extrabold text-slate-900">ĐỘNG CƠ KHAI THÁC POW</h2>
                        </div>
                        <span class="text-[10px] font-mono text-indigo-700 bg-indigo-50 border border-indigo-200 px-2 py-0.5 rounded-full font-bold">
                            JAVA 21 VIRTUAL THREADS
                        </span>
                    </div>

                    <div class="space-y-2 text-xs">
                        <div class="p-3 bg-slate-50 rounded-xl border border-slate-200 space-y-1.5">
                            <div class="flex items-center justify-between">
                                <span class="text-slate-500 font-medium">Mục Tiêu Độ Khó:</span>
                                <span id="targetPrefixDisplay" class="font-mono font-bold text-amber-700 bg-amber-50 px-2 py-0.5 rounded border border-amber-200">00000...</span>
                            </div>
                            <div class="flex items-center justify-between">
                                <span class="text-slate-500 font-medium">Cơ Chế Thuật Toán:</span>
                                <span class="font-mono text-slate-700 font-semibold">SHA-256 Nonce Brute-Force</span>
                            </div>
                            <div class="flex items-center justify-between">
                                <span class="text-slate-500 font-medium">Số Phiếu Sẽ Đóng Gói:</span>
                                <span id="miningPendingVotesText" class="font-mono font-bold text-indigo-600">0 phiếu</span>
                            </div>
                        </div>

                        <p class="text-[11px] text-slate-500 font-medium leading-relaxed">
                            Bấm nút bên dưới để giải bài toán Proof-of-Work: gom toàn bộ phiếu từ Mempool, tìm số <strong>Nonce</strong> thỏa mãn độ khó và khóa vĩnh viễn vào Sổ Cái.
                        </p>
                    </div>
                </div>

                <!-- NÚT KHAI THÁC POW TO, NỔI BẬT -->
                <div>
                    <button onclick="mineBlock()" id="btnMine"
                            class="w-full py-3.5 px-4 bg-emerald-600 hover:bg-emerald-700 text-white font-extrabold text-sm rounded-xl shadow-md shadow-emerald-600/20 transition-all flex items-center justify-center gap-2 cursor-pointer transform active:scale-[0.99]">
                        <span>⛏️</span> KHAI THÁC BLOCK MỚI (PoW ĐÓNG KHỐI)
                    </button>
                </div>
            </div>

        </div>

        <!-- 3. TÂM ĐIỂM: SỔ CÁI CHUỖI KHỐI (CÁC BLOCK NỐI XÍCH VỚI NHAU) -->
        <section class="bg-white border border-slate-200/90 rounded-2xl p-5 md:p-6 shadow-xs space-y-4">
            <div class="flex flex-col md:flex-row items-start md:items-center justify-between gap-3 border-b border-slate-100 pb-3">
                <div class="flex items-center gap-2.5">
                    <span class="text-2xl">🔗</span>
                    <div>
                        <h2 class="text-base font-extrabold text-slate-900">SỔ CÁI CHUỖI KHỐI (BLOCKCHAIN LEDGER)</h2>
                        <p class="text-xs text-slate-500 font-medium">Các khối được liên kết xích với nhau qua mã băm <strong>Pre Hash</strong> và khóa bằng <strong>Số Nonce (PoW)</strong></p>
                    </div>
                </div>

                <!-- CÁC NÚT THAO TÁC SỔ CÁI -->
                <div class="flex items-center gap-2">
                    <button onclick="tamperTest()" class="px-3 py-1.5 bg-rose-50 hover:bg-rose-100 text-rose-700 border border-rose-200 rounded-lg text-xs font-bold transition flex items-center gap-1 cursor-pointer">
                        <span>⚠️</span> Thử Nghiệm Gian Lận
                    </button>
                    <button onclick="resetElection()" class="px-3 py-1.5 bg-slate-100 hover:bg-slate-200 text-slate-700 border border-slate-200 rounded-lg text-xs font-bold transition flex items-center gap-1 cursor-pointer">
                        <span>🔄</span> Làm Mới Sổ Cái
                    </button>
                </div>
            </div>

            <!-- CHUỖI KHỐI RENDER DẠNG CARD KẾT NỐI LIÊN HOÀN (HORIZONTAL CHAIN STREAM) -->
            <div id="blockchainContainer" class="flex items-center gap-2 overflow-x-auto pb-4 pt-2">
                <!-- Render các khối bằng Javascript -->
            </div>
        </section>

    </div>

    <!-- FOOTER TỐI GIẢN -->
    <footer class="max-w-7xl mx-auto w-full pt-4 border-t border-slate-200 text-center text-xs text-slate-400 font-medium flex flex-col sm:flex-row items-center justify-between gap-2">
        <div>Hệ Thống Bầu Cử Điện Tử Blockchain &bull; Lập Trình Mạng HUTECH 2026</div>
        <div class="flex items-center gap-3">
            <a href="/vote" target="_blank" class="text-indigo-600 hover:underline font-bold">Phòng Bỏ Phiếu Cử Tri (/vote)</a>
            <span>&bull;</span>
            <span class="text-slate-400 font-mono">Port 8000 &bull; P2P Sockets</span>
        </div>
    </footer>

    <!-- LOGIC JAVASCRIPT ĐIỀU KHIỂN -->
    <script>
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
            document.getElementById('statMempool').textContent = data.mempool.length;
            document.getElementById('mempoolBadgeCount').textContent = data.mempool.length + ' phiếu chờ';
            document.getElementById('miningPendingVotesText').textContent = data.mempool.length + ' phiếu';
            document.getElementById('targetPrefixDisplay').textContent = (data.targetPrefix || '00000') + '...';

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
                mempoolEl.innerHTML = '<div class="text-center py-8 text-xs text-slate-400 italic font-medium">Chưa có phiếu bầu nào trong Mempool. Hãy mở <a href="/vote" target="_blank" class="text-indigo-600 font-bold underline">Phòng Bỏ Phiếu Cử Tri</a> để gửi phiếu!</div>';
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

            // Render Chuỗi Khối (Block Ledger) với 5 THUỘC TÍNH CHUẨN ĐƯỢC NỐI VỚI NHAU
            const chainCont = document.getElementById('blockchainContainer');
            chainCont.innerHTML = data.blocks.map((b, i) => {
                const isGenesis = (i === 0);
                const isTamperedBlock = data.isTampered && data.tamperedBlockIndex === i;
                const zeroMatches = b.hash.match(/^0+/) || [''];
                const zeroCount = zeroMatches[0].length;
                const leading = b.hash.substring(0, zeroCount);
                const rest = b.hash.substring(zeroCount);

                return `
                    <div class="min-w-[320px] max-w-[340px] flex-shrink-0 bg-white border-2 ${isTamperedBlock ? 'border-rose-400 bg-rose-50/20' : (isGenesis ? 'border-indigo-400' : 'border-slate-200/90')} rounded-2xl p-4.5 shadow-xs block-card space-y-2.5 transition">
                        <div class="flex items-center justify-between border-b border-slate-100 pb-2">
                            <span class="text-xs font-extrabold ${isGenesis ? 'text-indigo-600' : 'text-slate-800'} font-mono uppercase flex items-center gap-1">
                                ${isGenesis ? '🌟 GENESIS BLOCK #0' : '📦 BLOCK #' + i}
                            </span>
                            <span class="text-[10px] px-2 py-0.5 bg-slate-100 text-slate-600 rounded font-mono font-bold">
                                Nonce: ${b.nonce}
                            </span>
                        </div>

                        <!-- 5 THUỘC TÍNH CHUẨN CỦA MỖI BLOCK -->
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
                                <span class="text-[10px] text-slate-500 font-medium block">3. Mã Băm Khối Trước (Pre Hash):</span>
                                <div class="p-1.5 bg-slate-50 rounded border border-slate-200 text-[10px] text-slate-500 break-all">
                                    ${escapeHtml(b.preHash)}
                                </div>
                            </div>

                            <!-- 4. NỘI DUNG -->
                            <div>
                                <span class="text-[10px] text-slate-500 font-medium block">4. Nội Dung Phiếu Bầu (Content):</span>
                                <div class="p-2 bg-indigo-50/60 rounded-lg border border-indigo-100 text-xs font-sans text-indigo-950 break-words leading-relaxed max-h-24 overflow-y-auto font-medium">
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

                    ${i < data.blocks.length - 1 ? `
                    <div class="chain-connector">
                        <div class="flex flex-col items-center gap-0.5">
                            <span class="text-base text-indigo-500">🔗</span>
                            <span class="text-xs text-slate-400 font-bold">➔</span>
                        </div>
                    </div>` : ''}
                `;
            }).join('');
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
                    alert(`🎉 ĐÃ ĐÀO KHỐI THÀNH CÔNG!\\n\\nBlock mới thỏa mãn độ khó (${diffVal} số 0 ở đầu):\\nHash: ${data.hash}\\nNonce chiến thắng: ${data.nonce}\\nNội dung các phiếu bầu đã được khóa cứng vĩnh viễn vào Sổ Cái!`);
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
                } else {
                    alert('Lỗi làm mới: ' + err.message);
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
